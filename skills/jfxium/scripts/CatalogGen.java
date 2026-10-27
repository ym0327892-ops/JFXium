import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * JFXium 组件目录生成器（JDK 单文件，无第三方依赖）。
 *
 * <p>扫描核心库源码，抽取下游 AI / IDE 最需要的元信息：
 * 类名、包、是否导出、继承的原生控件、工厂方法、{@code build()} 返回类型、关键实例方法、一句话定位。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * java skills/jfxium/scripts/CatalogGen.java
 * java skills/jfxium/scripts/CatalogGen.java --src jfxium/src/main/java \
 *         --out skills/jfxium/assets/catalog
 * }</pre>
 *
 * <p>产物 {@code components.json} / {@code components.md} / {@code components.index.md}
 * 需要随代码一起提交；改动组件公开 API 后重跑本工具，禁止手改产物。</p>
 */
public final class CatalogGen {

    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\\s+([\\w.]+);", Pattern.MULTILINE);
    private static final Pattern EXPORT = Pattern.compile("^\\s*exports\\s+([\\w.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern FACTORY = Pattern.compile(
            "public\\s+static\\s+([\\w.<>\\[\\], ?]+?)\\s+(create\\w*)\\s*\\(([^)]*)\\)");
    private static final Pattern BUILD = Pattern.compile(
            "public\\s+([\\w.<>\\[\\], ?]+?)\\s+build\\s*\\(\\s*\\)");
    private static final Pattern METHOD = Pattern.compile(
            "public\\s+(?:static\\s+)?(?:final\\s+)?([\\w.<>\\[\\], ?]+?)\\s+(\\w+)\\s*\\(");
    private static final Pattern LOCATION = Pattern.compile("定位</b>\\s*[：:]\\s*([^<\\n]+)");
    private static final Pattern VERSION = Pattern.compile("<version>([^<]+)</version>");

    private static final Set<String> API_NOISE = Set.of(
            "build", "create", "getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait",
            "fireEvent", "addEventHandler", "removeEventHandler", "addEventFilter", "removeEventFilter",
            "requestFocus", "setUserData", "getUserData", "getProperties", "getStyleClass", "getId", "setId",
            "getStyle", "setStyle", "getParent", "getScene", "isFocused", "getWidth", "getHeight",
            "getLayoutBounds", "getBoundsInLocal", "getBoundsInParent", "snapshot", "setVisible", "isVisible",
            "setManaged", "isManaged", "setDisable", "isDisable", "getChildren", "lookup", "lookupAll",
            "applyCss", "layout", "resize", "autosize", "relocate", "toFront", "toBack", "getInsets",
            "getPadding", "setPadding", "getBackground", "getBorder");

    public static void main(String[] args) throws Exception {
        Path src = Path.of(arg(args, "--src", "jfxium/src/main/java"));
        Path out = Path.of(arg(args, "--out", "skills/jfxium/assets/catalog"));
        Path moduleInfo = src.resolve("org/openkawu/jfxium/module-info.java");
        Set<String> exports = parseExports(moduleInfo);

        List<Entry> entries = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(src)) {
            stream.filter(p -> p.toString().endsWith(".java"))
                    .sorted()
                    .forEach(p -> {
                        Entry entry = parse(p, exports);
                        if (entry != null) {
                            entries.add(entry);
                        }
                    });
        }
        entries.sort(Comparator.comparing(Entry::pkg).thenComparing(Entry::cls));

        String version = readVersion(Path.of("pom.xml"));
        Files.createDirectories(out);
        Files.writeString(out.resolve("components.json"), toJson(version, entries));
        Files.writeString(out.resolve("components.md"), toMarkdown(version, entries));
        Files.writeString(out.resolve("components.index.md"), toIndex(version, entries));
        System.out.printf("catalog: %d components, version=%s -> %s%n",
                entries.size(), version, out.toAbsolutePath());
    }

    private record Entry(String cls, String pkg, String extendsType, String path,
                         List<String> factories, String buildType, List<String> api, String summary,
                         boolean exported) {
    }

    private static Entry parse(Path file, Set<String> exports) {
        String text;
        try {
            text = Files.readString(file);
        } catch (IOException e) {
            return null;
        }
        String pkg = first(PACKAGE, text);
        if (pkg == null
                || pkg.equals("org.openkawu.jfxium.component.base")
                || !(pkg.startsWith("org.openkawu.jfxium.component.")
                || pkg.equals("org.openkawu.jfxium.layout")
                || pkg.equals("org.openkawu.jfxium.template"))) {
            return null;
        }
        String base = file.getFileName().toString().replace(".java", "");
        if (base.startsWith("Abstract")
                || base.endsWith("Helper") || base.endsWith("Builder")
                || base.endsWith("Themes") || base.endsWith("Mask")) {
            return null;
        }
        Pattern decl = Pattern.compile(
                "public\\s+(?:abstract\\s+|final\\s+|sealed\\s+|non-sealed\\s+)*class\\s+" + Pattern.quote(base)
                        + "\\b(?:<[^>]*>)?(?:\\s+extends\\s+([\\w.]+)(?:<[^>]*>)?)?");
        Matcher dm = decl.matcher(text);
        if (!dm.find() || text.contains("abstract class " + base)) {
            return null;
        }
        String extendsType = dm.group(1);
        List<String> factories = findAll(FACTORY, text).stream()
                .map(m -> m.name + "(" + compact(m.params) + ") -> " + m.type)
                .limit(4)
                .toList();

        Matcher bm = BUILD.matcher(text);
        String buildType = bm.find() ? bm.group(1).trim() : null;

        LinkedHashSet<String> api = new LinkedHashSet<>();
        Matcher mm = METHOD.matcher(text);
        while (mm.find() && api.size() < 18) {
            String name = mm.group(2);
            String type = mm.group(1).trim();
            if (type.equals("static") || API_NOISE.contains(name)
                    || name.startsWith("get") || name.startsWith("set") || name.startsWith("is")
                    || name.startsWith("fire") || type.equals("void") && name.equals("dispose")) {
                continue;
            }
            api.add(name);
        }

        String summary = summarize(text);
        if (summary.startsWith("内部")) {
            return null;
        }
        boolean exported = exports.contains(pkg);
        return new Entry(base, pkg, extendsType, file.toString().replace('\\', '/'),
                factories, buildType, List.copyOf(api), summary, exported);
    }

    private record Found(String type, String name, String params) {
    }

    private static List<Found> findAll(Pattern pattern, String text) {
        List<Found> found = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            found.add(new Found(m.group(1).trim(), m.group(2), m.group(3)));
        }
        return found;
    }

    private static String compact(String params) {
        return params.replaceAll("\\s+", " ").trim();
    }

    private static String summarize(String text) {
        Matcher m = LOCATION.matcher(text);
        if (m.find()) {
            return cleanup(m.group(1));
        }
        // 退回 Javadoc 第一行自然语言
        Matcher jd = Pattern.compile("/\\*\\*(.*?)\\*/", Pattern.DOTALL).matcher(text);
        if (jd.find()) {
            for (String line : jd.group(1).split("\\R")) {
                String s = line.replaceFirst("^\\s*\\*\\s?", "").trim();
                if (!s.isEmpty() && !s.startsWith("@") && !s.startsWith("<") && !s.startsWith("*")) {
                    return cleanup(s);
                }
            }
        }
        return "";
    }

    private static String cleanup(String s) {
        return s
                .replaceAll("\\{@code\\s+([^}]+)}", "$1")
                .replaceAll("\\{@link\\s+[^\\s}]+\\s+([^}]+)}", "$1")
                .replaceAll("\\{@link\\s+([^}]+)}", "$1")
                .replaceAll("<[^>]+>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static Set<String> parseExports(Path moduleInfo) throws IOException {
        Set<String> exports = new LinkedHashSet<>();
        if (!Files.exists(moduleInfo)) {
            return exports;
        }
        Matcher m = EXPORT.matcher(Files.readString(moduleInfo));
        while (m.find()) {
            exports.add(m.group(1));
        }
        return exports;
    }

    private static String readVersion(Path pom) {
        try {
            Matcher m = VERSION.matcher(Files.readString(pom));
            return m.find() ? m.group(1) : "unknown";
        } catch (IOException e) {
            return "unknown";
        }
    }

    private static String first(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private static String arg(String[] args, String name, String fallback) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return fallback;
    }

    private static String json(String s) {
        if (s == null) {
            return "null";
        }
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> {
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
                }
            }
        }
        return b.append('"').toString();
    }

    private static String array(List<String> values) {
        return values.stream().map(CatalogGen::json).reduce((a, b) -> a + ", " + b)
                .map(s -> "[" + s + "]").orElse("[]");
    }

    private static String toJson(String version, List<Entry> entries) {
        StringBuilder b = new StringBuilder();
        b.append("{\n  \"schema\": 1,\n  \"library\": \"JFXium\",\n  \"version\": ")
                .append(json(version)).append(",\n  \"generatedBy\": \"skills/jfxium/scripts/CatalogGen.java\",\n")
                .append("  \"count\": ").append(entries.size()).append(",\n  \"entries\": [\n");
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            b.append("    {\"class\": ").append(json(e.cls))
                    .append(", \"package\": ").append(json(e.pkg))
                    .append(", \"exports\": ").append(e.exported)
                    .append(", \"extends\": ").append(json(e.extendsType))
                    .append(", \"factories\": ").append(array(e.factories))
                    .append(", \"build\": ").append(json(e.buildType))
                    .append(", \"api\": ").append(array(e.api))
                    .append(", \"summary\": ").append(json(e.summary))
                    .append(", \"source\": ").append(json(e.path))
                    .append('}');
            b.append(i + 1 < entries.size() ? ",\n" : "\n");
        }
        return b.append("  ]\n}\n").toString();
    }

    private static String toIndex(String version, List<Entry> entries) {
        StringBuilder b = new StringBuilder();
        b.append("# JFXium 组件速查（v").append(version).append("）\n\n")
                .append("> 生成物，禁止手改。完整信息（extends / 定位 / source）见 `components.md` / `components.json`。\n\n")
                .append("| 组件 | 包 | build() | 关键 API |\n")
                .append("|---|---|---|---|\n");
        for (Entry e : entries) {
            b.append("| **").append(e.cls).append("** | ")
                    .append(e.pkg.replace("org.openkawu.jfxium.", "")).append(" | ")
                    .append(e.buildType == null ? "—" : "`" + e.buildType + "`").append(" | ")
                    .append(e.api.isEmpty() ? "—" : String.join(", ", e.api)).append(" |\n");
        }
        return b.toString();
    }

    private static String toMarkdown(String version, List<Entry> entries) {
        StringBuilder b = new StringBuilder();
        b.append("# JFXium 组件目录（v").append(version).append("）\n\n")
                .append("> 由 `skills/jfxium/scripts/CatalogGen.java` 生成，**禁止手改**。\n")
                .append("> 改动组件公开 API 后重跑：`java skills/jfxium/scripts/CatalogGen.java`\n\n")
                .append("> AI 使用顺序：本目录定位类名 → 打开 `source` 指向的类 Javadoc → ")
                .append("`jfxium-demo` 找对应示例。\n\n");
        String currentPkg = null;
        for (Entry e : entries) {
            if (!e.pkg.equals(currentPkg)) {
                currentPkg = e.pkg;
                b.append("\n## ").append(currentPkg).append("\n\n")
                        .append("| 组件 | extends | 工厂 | build() | 关键 API | 定位 |\n")
                        .append("|---|---|---|---|---|---|\n");
            }
            b.append("| **").append(e.cls).append("** | ")
                    .append(e.extendsType == null ? "—" : "`" + e.extendsType + "`").append(" | ")
                    .append(e.factories.isEmpty() ? "—" : "`" + String.join("`<br>`", e.factories) + "`").append(" | ")
                    .append(e.buildType == null ? "—" : "`" + e.buildType + "`").append(" | ")
                    .append(e.api.isEmpty() ? "—" : String.join(", ", e.api)).append(" | ")
                    .append(e.summary.replace("|", "\\|")).append(" |\n");
        }
        return b.toString();
    }
}
