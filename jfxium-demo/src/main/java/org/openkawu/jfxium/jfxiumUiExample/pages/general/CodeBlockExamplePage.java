package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CodeBlockAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * CodeBlockAnt 示例页面 - 展示代码高亮显示组件。
 *
 * <p>功能特性：</p>
 * <ul>
 *   <li>语法高亮：Java 关键字、字符串、注释、数字不同颜色</li>
 *   <li>可复制：Ctrl+C 或右键菜单复制全部内容</li>
 *   <li>不可编辑：只读显示，无光标/输入</li>
 *   <li>行号：可选显示行号</li>
 *   <li>主题：亮色/暗色主题</li>
 * </ul>
 */
public class CodeBlockExamplePage extends VBoxAnt {

    public CodeBlockExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("CodeBlock 代码块")
                .description("带语法高亮的代码显示组件，不可编辑但可复制。")
                .sections(
                        basicSection(),
                        lineNumbersSection(),
                        themeSection(),
                        complexExampleSection()
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
                .language(org.openkawu.jfxium.component.CodeBlockAnt.Language.JAVA)
                .code(simpleCode)
                .showLineNumbers(false)
                .theme(org.openkawu.jfxium.component.CodeBlockAnt.Theme.LIGHT)
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
                .language(org.openkawu.jfxium.component.CodeBlockAnt.Language.JAVA)
                .code(codeWithNumbers)
                .showLineNumbers(true)
                .theme(org.openkawu.jfxium.component.CodeBlockAnt.Theme.LIGHT)
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

    /** 3. 暗色主题。 */
    private Node themeSection() {
        String darkThemeCode = """
            /*
             * 暗色主题代码示例
             * 适合夜间模式或深色背景
             */
            public class DarkThemeDemo {
                private final String name;
                private final int value;
                
                public DarkThemeDemo(String name, int value) {
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
                .language(org.openkawu.jfxium.component.CodeBlockAnt.Language.JAVA)
                .code(darkThemeCode)
                .showLineNumbers(true)
                .theme(org.openkawu.jfxium.component.CodeBlockAnt.Theme.DARK)
                .maxHeight(250)
                .build();

        String code = """
                CodeBlockAnt.create()
                        .language(CodeBlockAnt.Language.JAVA)
                        .code(sourceCode)
                        .showLineNumbers(true)
                        .theme(CodeBlockAnt.Theme.DARK)   // 暗色主题
                        .maxHeight(250)
                        .build();
                """;
        return Demos.sectionWithCode("3. 暗色主题",
                "适合夜间模式或深色界面。",
                code, codeBlock);
    }

    /** 4. 复杂示例。 */
    private Node complexExampleSection() {
        String complexCode = """
            package org.example;
            
            import java.util.*;
            import java.util.stream.Collectors;
            
            /**
             * 用户服务类
             * 演示更复杂的Java语法高亮
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
                
                // 使用Stream处理用户列表
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

        Node codeBlock = CodeBlockAnt.create()
                .language(org.openkawu.jfxium.component.CodeBlockAnt.Language.JAVA)
                .code(complexCode)
                .showLineNumbers(true)
                .theme(org.openkawu.jfxium.component.CodeBlockAnt.Theme.LIGHT)
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
}