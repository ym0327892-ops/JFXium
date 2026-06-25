package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.util.NumericUtils;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.CodeBlockAnt;

/**
 * CodeBlockAnt 示例页面 - 展示代码高亮显示组件。
 *
 * <p>功能特性：</p>
 * <ul>
 *   <li>语法高亮：Java 关键字、字符串、注释、数字不同颜色</li>
 *   <li>可复制：Ctrl+C 或右键菜单复制全部内容</li>
 *   <li>不可编辑：只读显示，无光标/输入</li>
 *   <li>行号：可选显示行号</li>
 *   <li>主题：亮色/暗色/自动跟随全局主题</li>
 * </ul>
 */
public class CodeBlockExamplePage extends VBoxAnt {

    /** 完整示例代码 —— 长度足够覆盖各高度档位 + 多种语法元素（注释/字符串/关键字/数字）。 */
    private static final String DEMO_CODE = """
            package org.example;

            import java.util.*;
            import java.util.stream.Collectors;

            /**
             * 用户服务类
             * 演示 Java 语法高亮与 CodeBlockAnt 配置
             */
            public class UserService {
                private final Map<Long, User> userCache = new HashMap<>();
                private static final int MAX_RETRY = 3;

                // 查找用户
                public Optional<User> findUserById(Long id) {
                    if (id == null || id <= 0) {
                        throw new IllegalArgumentException("无效的用户ID");
                    }

                    // 先从缓存查找
                    User cached = userCache.get(id);
                    if (cached != null) {
                        return Optional.of(cached);
                    }

                    // 数据库查询
                    for (int i = 0; i < MAX_RETRY; i++) {
                        try {
                            User user = database.queryUser(id);
                            if (user != null) {
                                userCache.put(id, user);
                                return Optional.of(user);
                            }
                        } catch (SQLException e) {
                            System.err.println("查询失败: " + e.getMessage());
                            if (i == MAX_RETRY - 1) {
                                throw new RuntimeException("重试次数超限", e);
                            }
                        }
                    }

                    return Optional.empty();
                }

                // 使用 Stream 处理用户列表
                public List<String> getActiveUserNames(List<User> users) {
                    return users.stream()
                        .filter(User::isActive)
                        .map(User::getName)
                        .filter(name -> name != null && !name.trim().isEmpty())
                        .sorted()
                        .collect(Collectors.toList());
                }
            }
            """;

    public CodeBlockExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("CodeBlock 代码块")
                .description("带语法高亮的代码显示组件，不可编辑但可复制。")
                .sections(
                        basicSection(),
                        lineNumbersSection(),
                        themeSection(),
                        complexExampleSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 基础用法。 */
    private Node basicSection() {
        String simpleCode = """
            // 简单的Java代码示例
            public class Hello {
                public static void main(String[] args) {
                    System.out.println("Hello, World!");
                    int count = 42;
                    double pi = 3.14159;
                }
            }
            """;

        Node codeBlock = CodeBlockAnt.create()
                .language(CodeBlockAnt.Language.JAVA)
                .code(simpleCode)
                .showLineNumbers(false)
                .theme(CodeBlockAnt.Theme.LIGHT)
                .maxHeight(200)
                .build();

        String code = """
                CodeBlockAnt.create()
                        .language(CodeBlockAnt.Language.JAVA)
                        .code(sourceCode)
                        .showLineNumbers(false)
                        .theme(CodeBlockAnt.Theme.LIGHT)
                        .maxHeight(200)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "最简单的代码块显示，带语法高亮。",
                code, codeBlock);
    }

    /** 2. 带行号。 */
    private Node lineNumbersSection() {
        String codeWithNumbers = """
            import java.util.List;
            import java.util.ArrayList;

            public class Calculator {
                // 计算阶乘
                public static int factorial(int n) {
                    if (n <= 1) {
                        return 1;
                    }
                    return n * factorial(n - 1);
                }

                // 主方法
                public static void main(String[] args) {
                    int result = factorial(5);
                    System.out.println("5! = " + result);
                }
            }
            """;

        Node codeBlock = CodeBlockAnt.create()
                .language(CodeBlockAnt.Language.JAVA)
                .code(codeWithNumbers)
                .showLineNumbers(true)
                .theme(CodeBlockAnt.Theme.LIGHT)
                .maxHeight(300)
                .build();

        String code = """
                CodeBlockAnt.create()
                        .language(CodeBlockAnt.Language.JAVA)
                        .code(sourceCode)
                        .showLineNumbers(true)   // 开启行号
                        .theme(CodeBlockAnt.Theme.LIGHT)
                        .maxHeight(300)
                        .build();
                """;
        return Demos.sectionWithCode("2. 带行号",
                "显示行号，便于代码引用和讨论。",
                code, codeBlock);
    }

    /** 3. 自动跟随全局主题。 */
    private Node themeSection() {
        String autoThemeCode = """
            /*
             * AUTO 主题代码示例
             * 会跟随全局 ThemeManager 的明暗切换
             */
            public class AutoThemeDemo {
                private final String name;
                private final int value;

                public AutoThemeDemo(String name, int value) {
                    this.name = name;
                    this.value = value;
                }

                public void printInfo() {
                    String message = "Name: " + name + ", Value: " + value;
                    System.out.println(message);
                }
            }
            """;

        Node codeBlock = CodeBlockAnt.create()
                .language(CodeBlockAnt.Language.JAVA)
                .code(autoThemeCode)
                .showLineNumbers(true)
                .theme(CodeBlockAnt.Theme.AUTO)
                .maxHeight(250)
                .build();

        String code = """
                CodeBlockAnt.create()
                        .language(CodeBlockAnt.Language.JAVA)
                        .code(sourceCode)
                        .showLineNumbers(true)
                        .theme(CodeBlockAnt.Theme.AUTO)   // 跟随全局主题
                        .maxHeight(250)
                        .build();
                """;
        return Demos.sectionWithCode("3. 自动主题",
                "跟随全局 ThemeManager 的明暗切换。",
                code, codeBlock);
    }

    /** 4. 复杂示例。 */
    private Node complexExampleSection() {
        Node codeBlock = CodeBlockAnt.create()
                .language(CodeBlockAnt.Language.JAVA)
                .code(DEMO_CODE)
                .showLineNumbers(true)
                .theme(CodeBlockAnt.Theme.LIGHT)
                .maxHeight(400)
                .build();

        String code = """
                CodeBlockAnt.create()
                        .language(CodeBlockAnt.Language.JAVA)
                        .code(complexCode)
                        .showLineNumbers(true)
                        .theme(CodeBlockAnt.Theme.LIGHT)
                        .maxHeight(400)
                        .build();
                """;
        return Demos.sectionWithCode("4. 复杂示例",
                "包含包声明、导入、注释、异常处理、Stream API等复杂语法。",
                code, codeBlock);
    }

    /**
     * 5. 交互演示（PlayGround —— rebuildRebuild 模式 6 维度）。
     *
     * <p>CodeBlockAnt 没有 modify 入口（内部委托 {@link javafx.scene.layout.BorderPane}，
     * 行号 / 主题 / TextArea vs TextFlow 在 build 期决定），故用 {@link PlayGround#rebindRebuild}。
     * 暴露 6 个维度：</p>
     * <ul>
     *   <li><b>语言</b> —— 7 档 Language 枚举（JAVA/XML/SHELL/JS/PYTHON/SQL/PLAIN，仅 JAVA 有高亮）</li>
     *   <li><b>行号</b> —— off / on</li>
     *   <li><b>主题</b> —— LIGHT / DARK / AUTO（AUTO 跟随全局明暗）</li>
     *   <li><b>高度</b> —— 200 / 300 / 400 / 500 四档</li>
     *   <li><b>复制按钮</b> —— off / on（顶部一键复制入口）</li>
     *   <li><b>可选区</b> —— off / on（true=TextArea 单色可拖选，false=TextFlow 多色不可选）</li>
     * </ul>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> lang       = PlayGround.binder("java");
        Binder<String> lineNum    = PlayGround.binder("on");
        Binder<String> theme      = PlayGround.binder("light");
        Binder<String> height     = PlayGround.binder("400");
        Binder<String> copyBtn    = PlayGround.binder("on");
        Binder<String> selectable = PlayGround.binder("off");

        // 2. display 工厂：读 binder → 重 build
        Supplier<Node> factory = () -> {
            CodeBlockAnt.Language l  = parseLang(lang.get());
            boolean showLines       = "on".equals(lineNum.get());
            CodeBlockAnt.Theme t     = parseTheme(theme.get());
            double h                = parseHeight(height.get());
            boolean showCopy        = "on".equals(copyBtn.get());
            boolean sel             = "on".equals(selectable.get());
            return CodeBlockAnt.create()
                    .language(l)
                    .code(DEMO_CODE)
                    .showLineNumbers(showLines)
                    .theme(t)
                    .maxHeight(h)
                    .showCopyButton(showCopy)
                    .selectable(sel)
                    .build();
        };

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 CodeBlock 的语言 / 行号 / 主题 / 高度 / 复制按钮 / 可选区 —— 6 维度 rebuildRebuild 模式。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("语言", PlayGround.segmented(lang,
                                PlayGround.entry("java",        "JAVA"),
                                PlayGround.entry("xml",         "XML"),
                                PlayGround.entry("shell",       "SHELL"),
                                PlayGround.entry("javascript",  "JS"),
                                PlayGround.entry("python",      "PY"),
                                PlayGround.entry("sql",         "SQL"),
                                PlayGround.entry("plain",       "PLAIN"))),
                        PlayGround.row("行号", PlayGround.segmented(lineNum,
                                PlayGround.entry("off", "隐藏"),
                                PlayGround.entry("on",  "显示"))),
                        PlayGround.row("主题", PlayGround.segmented(theme,
                                PlayGround.entry("light", "LIGHT"),
                                PlayGround.entry("dark",  "DARK"),
                                PlayGround.entry("auto",  "AUTO"))),
                        PlayGround.row("高度", PlayGround.segmented(height,
                                PlayGround.entry("200", "200px"),
                                PlayGround.entry("300", "300px"),
                                PlayGround.entry("400", "400px"),
                                PlayGround.entry("500", "500px"))),
                        PlayGround.row("复制按钮", PlayGround.segmented(copyBtn,
                                PlayGround.entry("off", "隐藏"),
                                PlayGround.entry("on",  "显示"))),
                        PlayGround.row("可选区", PlayGround.segmented(selectable,
                                PlayGround.entry("off", "高亮（不可选）"),
                                PlayGround.entry("on",  "单色（可选）")))));
    }

    // ============================================================
    // helpers
    // ============================================================

    private static CodeBlockAnt.Language parseLang(String v) {
        if (v == null) return CodeBlockAnt.Language.JAVA;
        return switch (v) {
            case "xml"         -> CodeBlockAnt.Language.XML;
            case "shell"       -> CodeBlockAnt.Language.SHELL;
            case "javascript"  -> CodeBlockAnt.Language.JAVASCRIPT;
            case "python"      -> CodeBlockAnt.Language.PYTHON;
            case "sql"         -> CodeBlockAnt.Language.SQL;
            case "plain"       -> CodeBlockAnt.Language.PLAIN;
            default            -> CodeBlockAnt.Language.JAVA;
        };
    }

    private static CodeBlockAnt.Theme parseTheme(String v) {
        if (v == null) return CodeBlockAnt.Theme.LIGHT;
        return switch (v) {
            case "dark"  -> CodeBlockAnt.Theme.DARK;
            case "auto"  -> CodeBlockAnt.Theme.AUTO;
            default      -> CodeBlockAnt.Theme.LIGHT;
        };
    }

    private static double parseHeight(String v) {
        if (v == null) return 400;
        try {
            double d = Double.parseDouble(v);
            // 与 Builder.maxHeight 钳制策略对齐：有限正值钳到 [1, 2000],非法回退到 400
            return NumericUtils.clamp(d, 1, 2000, 400);
        } catch (NumberFormatException e) {
            return 400;
        }
    }
}