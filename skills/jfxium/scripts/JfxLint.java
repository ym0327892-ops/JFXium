import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * JFXium 红线检查器（JDK 单文件，无第三方依赖）。
 *
 * <p>给 AI 生成结果做确定性验收：原生控件绕过、内联样式、CSS 伪类/属性误用、缺失模块导出。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * java skills/jfxium/scripts/JfxLint.java path/to/src
 * java skills/jfxium/scripts/JfxLint.java jfxium-demo/src jfxium/src/main/java
 * }</pre>
 *
 * <p>输出 {@code path:line: severity RULE message}；存在 ERROR 时退出码 1。
 * 单行豁免：行内加 {@code jfx-lint:ignore}（仅限确实无法替换的场景，并在 PR 说明原因）。</p>
 */
public final class JfxLint {

    private static final String NATIVE_CONTROLS =
            "Label|Button|CheckBox|Hyperlink|TextField|PasswordField|ComboBox|RadioButton|TextArea|Slider"
                    + "|ProgressBar|ProgressIndicator|TableView|TreeView|TreeTableView|ListView|ToggleButton"
                    + "|MenuButton|SplitMenuButton|ChoiceBox|DatePicker|ColorPicker|Spinner|Accordion"
                    + "|TitledPane|Pagination|ToolBar|MenuBar|Separator|Canvas|Mentions";

    private static final Pattern NATIVE_NEW = Pattern.compile(
            "\\bnew\\s+(?:[\\w.]+\\.)?(" + NATIVE_CONTROLS + ")\\s*(?:<[^>]*>)?\\s*\\(");
    private static final Pattern INLINE_STYLE = Pattern.compile(
            "\\.setStyle\\s*\\(\\s*\"[^\"]*(?:#[0-9a-fA-F]{3,8}|rgba?\\s*\\(|-fx-[a-z-]+\\s*:|\\d+\\s*px)");
    private static final Pattern RAW_STYLE_CLASS = Pattern.compile(
            "getStyleClass\\s*\\(\\s*\\)\\s*\\.add(?:All)?\\s*\\(\\s*\"(?!jfx-)([a-z][\\w-]*)\"");
    private static final Pattern EXPORT = Pattern.compile("^\\s*exports\\s+([\\w.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern PKG = Pattern.compile("^\\s*package\\s+([\\w.]+);", Pattern.MULTILINE);
    private static final Pattern PUBLIC_CLASS = Pattern.compile(
            "^\\s*public\\s+(?:abstract\\s+|final\\s+|sealed\\s+|non-sealed\\s+)*class\\s+(\\w+)",
            Pattern.MULTILINE);

    private record Finding(Path file, int line, String severity, String rule, String message) {
    }

    private static final List<Finding> FINDINGS = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        List<Path> roots = new ArrayList<>();
        for (String a : args) {
            Path p = Path.of(a);
            if (Files.exists(p)) {
                roots.add(p);
            } else {
                System.err.println("skip missing path: " + a);
            }
        }
        if (roots.isEmpty()) {
            System.err.println("usage: java skills/jfxium/scripts/JfxLint.java <file-or-dir>...");
            System.exit(2);
        }

        List<Path> moduleInfos = new ArrayList<>();
        for (Path root : roots) {
            try (Stream<Path> stream = Files.walk(root)) {
                stream.filter(Files::isRegularFile).forEach(p -> {
                    String name = p.getFileName().toString();
                    if (name.equals("module-info.java")) {
                        synchronized (moduleInfos) {
                            moduleInfos.add(p);
                        }
                    } else if (name.endsWith(".java")) {
                        lintJava(p);
                    } else if (name.endsWith(".css") || name.endsWith(".less")) {
                        lintCss(p);
                    }
                });
            }
        }
        for (Path moduleInfo : moduleInfos) {
            lintModuleExports(moduleInfo);
        }

        FINDINGS.sort(Comparator.comparing((Finding f) -> f.file.toString()).thenComparingInt(f -> f.line));
        long errors = 0;
        long warnings = 0;
        for (Finding f : FINDINGS) {
            System.out.printf("%s:%d: %s %s %s%n", f.file, f.line, f.severity, f.rule, f.message);
            if ("ERROR".equals(f.severity)) {
                errors++;
            } else {
                warnings++;
            }
        }
        System.out.printf("%nJFXium lint: %d errors, %d warnings%n", errors, warnings);
        if (errors > 0) {
            System.exit(1);
        }
    }

    private static boolean isFrameworkSource(Path p) {
        String s = p.toString().replace('\\', '/');
        return s.contains("/src/main/java/org/openkawu/jfxium/");
    }

    private static void lintJava(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            return;
        }
        boolean framework = isFrameworkSource(file);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("jfx-lint:ignore")) {
                continue;
            }
            // 框架内部实现允许 new 原生控件（红线 #11 的豁免范围）
            if (!framework) {
                if (NATIVE_NEW.matcher(line).find()) {
                    add(file, i + 1, "ERROR", "NATIVE_NEW",
                            "禁止 new 原生 JavaFX 控件，改用对应 *Ant（见 skills/jfxium/references/native-map.md）");
                }
                if (INLINE_STYLE.matcher(line).find()) {
                    add(file, i + 1, "ERROR", "INLINE_STYLE",
                            "禁止 setStyle() 写颜色/尺寸，改用 styleClass + LESS");
                }
            }
            if (line.contains("-fx-transition")) {
                add(file, i + 1, "ERROR", "FX_TRANSITION",
                        "JavaFX CSS 不支持 -fx-transition，直接定义状态色");
            }
            if (line.toLowerCase().contains("box-shadow")) {
                add(file, i + 1, "ERROR", "BOX_SHADOW",
                        "禁止 CSS box-shadow，改用 -fx-effect: dropshadow(...)");
            }
            Matcher raw = RAW_STYLE_CLASS.matcher(line);
            if (!framework && raw.find() && !raw.group(1).startsWith("data:")) {
                add(file, i + 1, "WARN", "RAW_STYLE_CLASS",
                        "自定义 styleClass 应以 jfx- 开头，避免与 modena 冲突：" + raw.group(1));
            }
        }
    }

    private static void lintCss(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            return;
        }
        Pattern focus = Pattern.compile(":focus(?!ed|-within|-visible)");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String trimmed = line.trim();
            if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                continue;
            }
            if (line.contains("-fx-transition")) {
                add(file, i + 1, "ERROR", "FX_TRANSITION", "JavaFX CSS 不支持 -fx-transition");
            }
            if (line.contains("box-shadow")) {
                add(file, i + 1, "ERROR", "BOX_SHADOW", "改用 -fx-effect: dropshadow(...)");
            }
            if (line.contains(":active")) {
                add(file, i + 1, "ERROR", "CSS_ACTIVE", "JavaFX 用 :pressed / :armed，不是 :active");
            }
            if (focus.matcher(line).find()) {
                add(file, i + 1, "ERROR", "CSS_FOCUS", "JavaFX 用 :focused（或 :focus-within），不是 :focus");
            }
        }
    }

    private static void lintModuleExports(Path moduleInfo) {
        Path javaRoot = moduleInfo.getParent();
        Set<String> exports = new LinkedHashSet<>();
        Map<String, Set<String>> classes = new LinkedHashMap<>();
        try {
            Matcher m = EXPORT.matcher(Files.readString(moduleInfo));
            while (m.find()) {
                exports.add(m.group(1));
            }
            try (Stream<Path> stream = Files.walk(javaRoot)) {
                stream.filter(p -> p.toString().endsWith(".java") && !p.equals(moduleInfo)).forEach(p -> {
                    try {
                        String text = Files.readString(p);
                        Matcher pm = PKG.matcher(text);
                        if (!pm.find()) {
                            return;
                        }
                        String pkg = pm.group(1);
                        boolean api = pkg.startsWith("org.openkawu.jfxium.component.")
                                || pkg.equals("org.openkawu.jfxium.layout")
                                || pkg.equals("org.openkawu.jfxium.template");
                        if (!api) {
                            return;
                        }
                        Matcher cm = PUBLIC_CLASS.matcher(text);
                        if (cm.find()) {
                            synchronized (classes) {
                                classes.computeIfAbsent(pkg, k -> new LinkedHashSet<>()).add(cm.group(1));
                            }
                        }
                    } catch (IOException ignored) {
                        // 跳过不可读文件
                    }
                });
            }
        } catch (IOException e) {
            return;
        }
        for (Map.Entry<String, Set<String>> entry : classes.entrySet()) {
            if (!exports.contains(entry.getKey())) {
                add(moduleInfo, 1, "ERROR", "MODULE_EXPORT",
                        "public 包未 exports：" + entry.getKey() + " -> " + String.join(", ", entry.getValue()));
            }
        }
    }

    private static void add(Path file, int line, String severity, String rule, String message) {
        FINDINGS.add(new Finding(file, line, severity, rule, message));
    }
}
