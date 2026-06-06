package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CodeBlockAnt - 代码块显示组件（不可编辑，可复制）。
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>语法高亮：Java 关键字、字符串、注释、数字等</li>
 *   <li>可复制：Ctrl+C 或右键菜单复制全部内容</li>
 *   <li>不可编辑：只读显示，无光标/输入</li>
 *   <li>行号：可选显示行号（左侧）</li>
 *   <li>主题跟随：颜色使用 LESS 变量，随主题切换</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 基础用法
 * Node codeBlock = CodeBlockAnt.create()
 *     .language(CodeBlockAnt.Language.JAVA)
 *     .code("public class Hello {\n  public static void main(String[] args) {\n    System.out.println(\"Hello\");\n  }\n}")
 *     .showLineNumbers(true)
 *     .build();
 *
 * // 暗色主题代码
 * Node darkCode = CodeBlockAnt.create()
 *     .language(CodeBlockAnt.Language.JAVA)
 *     .code("// 注释\nint x = 42;")
 *     .theme(CodeBlockAnt.Theme.DARK)
 *     .build();
 * }</pre>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>不可编辑：用 TextFlow 而非 TextArea</li>
 *   <li>可复制：挂快捷键 Ctrl+C + 右键菜单</li>
 *   <li>视觉走 LESS：所有颜色用 CSS 变量</li>
 *   <li>轻量：不依赖外部语法高亮库</li>
 * </ul>
 */
public class CodeBlockAnt {

    /** 支持的语言（目前只实现 JAVA，可扩展）。 */
    public enum Language {
        JAVA,
        // 预留：JAVASCRIPT, PYTHON, XML, SQL, etc.
    }

    /** 代码主题（影响高亮颜色）。 */
    public enum Theme {
        LIGHT, DARK
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Language language = Language.JAVA;
        private String code = "";
        private boolean showLineNumbers = false;
        private Theme theme = Theme.LIGHT;
        private double maxHeight = 400; // 超出时滚动
        private boolean showCopyButton = true; // 顶部复制按钮（默认显示，对齐 GitHub/Ant Design 代码块）
        private String title = null;           // 顶部标题（可选；null 时用语言名）
        private boolean selectable = false;    // 是否可自由选区复制（true=TextArea 单色可选 / false=TextFlow 高亮不可选）

        private Builder() {}

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder code(String code) {
            this.code = code != null ? code : "";
            return this;
        }

        public Builder showLineNumbers(boolean show) {
            this.showLineNumbers = show;
            return this;
        }

        public Builder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public Builder maxHeight(double height) {
            this.maxHeight = height;
            return this;
        }

        /**
         * 是否显示顶部复制按钮 header（默认 true）。
         *
         * <p>header 结构：左侧语言/标题，右侧「复制」按钮。点击一键复制全部代码，
         * 并给出「已复制」短暂反馈——对齐 GitHub / Ant Design 代码块的复制交互。</p>
         */
        public Builder showCopyButton(boolean show) {
            this.showCopyButton = show;
            return this;
        }

        /** 顶部 header 的标题文字（可选）。不设时显示语言名（如 JAVA）。 */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * 是否允许自由拖拽选中并复制部分代码（默认 false）。
         *
         * <p><b>这是 JavaFX 的一个硬取舍（二选一）</b>：</p>
         * <ul>
         *   <li>{@code false}（默认）：用 {@link TextFlow} 渲染——<b>有多色语法高亮，但不能拖选</b>
         *       （JavaFX 的 TextFlow 不支持文本选区）。只能用顶部「复制」按钮整体复制。</li>
         *   <li>{@code true}：用只读 {@link javafx.scene.control.TextArea} 渲染——<b>能自由拖选 + Ctrl+C
         *       复制任意片段，但代码是单色</b>（TextArea 不支持富文本多色）。</li>
         * </ul>
         *
         * <p>选择依据：偏「让用户抄走片段」用 true（如文档示例代码）；偏「展示带高亮的成品代码」用 false。
         * 想同时要高亮 + 选区，需要引入 RichTextFX 等第三方库——与本组件「零依赖」原则冲突，故不内置。</p>
         */
        public Builder selectable(boolean selectable) {
            this.selectable = selectable;
            return this;
        }

        /**
         * 构建代码块显示组件。
         *
         * <p><b>布局策略</b>：行号 VBox 与代码 TextFlow 用 HBox 包成同一个内容节点，
         * 一起塞进 ScrollPane —— 两者共享滚动状态，不会出现"行号撑高但代码被居中
         * 留出大片空白"的视觉错位（修复历史 bug：BorderPane.left/center 高度不一时
         * 默认 CENTER 对齐导致 ScrollPane 被垂直居中）。</p>
         *
         * @return 一个 BorderPane：top 是可选复制 header，center 是 ScrollPane(HBox(行号, 代码))
         */
        public BorderPane build() {
            BorderPane root = new BorderPane();
            root.getStyleClass().add(JfxStyles.CODEBLOCK);
            root.getStyleClass().add("code-theme-" + theme.name().toLowerCase());

            // 内容区：selectable 决定用「可选区单色 TextArea」还是「高亮 TextFlow」
            Node center = selectable ? buildSelectableCenter() : buildHighlightedCenter();
            root.setCenter(center);

            // 顶部复制 header（可选）—— 提供「一键复制」的可见入口
            if (showCopyButton) {
                root.setTop(createHeader());
            }

            applyStyles(root);
            return root;
        }

        /**
         * 高亮模式（默认）：TextFlow 多色高亮，但不支持选区。
         * 行号 VBox 与代码 TextFlow 用 HBox 包成同一节点共享滚动，避免居中错位。
         */
        private Node buildHighlightedCenter() {
            List<CodeFragment> fragments = parseCode(code, language);
            TextFlow codeDisplay = createHighlightedDisplay(fragments);
            codeDisplay.getStyleClass().add(JfxStyles.CODEBLOCK_CONTENT);
            // Ctrl+C + 右键菜单，作为复制按钮之外的补充（TextFlow 无选区，复制整体）
            setupCopySupport(codeDisplay, code);

            Node scrollContent;
            if (showLineNumbers) {
                VBox lineNumbers = createLineNumbers(code);
                HBox combined = new HBox(lineNumbers, codeDisplay);
                HBox.setHgrow(codeDisplay, Priority.ALWAYS);
                scrollContent = combined;
            } else {
                scrollContent = codeDisplay;
            }

            ScrollPane scrollPane = new ScrollPane(scrollContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefViewportHeight(maxHeight);
            scrollPane.setMaxHeight(maxHeight);
            scrollPane.getStyleClass().add(JfxStyles.CODEBLOCK_SCROLL);
            return scrollPane;
        }

        /**
         * 可选区模式：只读 {@link javafx.scene.control.TextArea} 单色渲染，
         * 支持鼠标拖选 + Ctrl+C 复制任意片段（原生能力，参考 SelectableTextAnt）。
         *
         * <p><b>布局策略</b>：TextArea 自适应高度（按行数撑开、不出现内部滚动条），
         * 与行号 gutter 一起放进外层 ScrollPane 共享滚动——和高亮模式保持一致的滚动行为，
         * 行号与代码天然对齐（同一 ScrollPane 滚动，不需要手动 bind scrollTop）。</p>
         */
        private Node buildSelectableCenter() {
            javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(code);
            textArea.setEditable(false);
            textArea.setWrapText(false); // 代码不折行，超宽横向滚动
            textArea.getStyleClass().addAll(JfxStyles.CODEBLOCK_CONTENT, "jfx-codeblock-textarea");
            // 自适应行数：让 TextArea 撑到全部内容高度，避免内部滚动条与外层 ScrollPane 打架
            int rows = countLines(code);
            textArea.setPrefRowCount(rows);
            textArea.setMinHeight(Region.USE_PREF_SIZE);

            Node scrollContent;
            if (showLineNumbers) {
                VBox lineNumbers = createLineNumbers(code);
                HBox combined = new HBox(lineNumbers, textArea);
                HBox.setHgrow(textArea, Priority.ALWAYS);
                scrollContent = combined;
            } else {
                scrollContent = textArea;
            }

            ScrollPane scrollPane = new ScrollPane(scrollContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefViewportHeight(maxHeight);
            scrollPane.setMaxHeight(maxHeight);
            scrollPane.getStyleClass().add(JfxStyles.CODEBLOCK_SCROLL);
            return scrollPane;
        }

        /**
         * 创建顶部 header：左侧语言/标题，右侧「复制」按钮。
         *
         * <p>遵循组件组合规范 3.1 Header 三段式——独立 Region spacer 把复制按钮推到最右。
         * 复制按钮点击后短暂显示「已复制」文案再恢复（PauseTransition），给用户明确反馈。</p>
         */
        private HBox createHeader() {
            HBox header = new HBox(8);
            header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            header.getStyleClass().add(JfxStyles.CODEBLOCK_HEADER);

            // 左侧：语言/标题标签
            Label langLabel = new Label(title != null ? title : language.name());
            langLabel.getStyleClass().add(JfxStyles.CODEBLOCK_LANG);
            header.getChildren().add(langLabel);

            // 弹性 spacer：把复制按钮推到最右（独立 Region，遵循 SKILL §4.1）
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            header.getChildren().add(spacer);

            // 右侧：复制按钮
            javafx.scene.control.Button copyBtn = new javafx.scene.control.Button("复制");
            copyBtn.getStyleClass().add(JfxStyles.CODEBLOCK_COPY_BTN);
            copyBtn.setOnAction(e -> {
                copyToClipboard(code);
                // 「已复制」短暂反馈，再恢复成「复制」
                copyBtn.setText("已复制");
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.millis(1200));
                pause.setOnFinished(ev -> copyBtn.setText("复制"));
                pause.play();
            });
            header.getChildren().add(copyBtn);

            return header;
        }

        // ============================================================
        // 内部辅助方法
        // ============================================================

        /** 解析代码为带样式的片段列表。 */
        private List<CodeFragment> parseCode(String code, Language lang) {
            List<CodeFragment> fragments = new ArrayList<>();
            if (lang != Language.JAVA) {
                // 目前只实现 JAVA，其他语言返回纯文本
                fragments.add(new CodeFragment(code, "text"));
                return fragments;
            }

            // Java 语法高亮正则（简化版）
            String[] lines = code.split("\n", -1); // -1 保留空行
            for (String line : lines) {
                fragments.addAll(parseJavaLine(line));
                fragments.add(new CodeFragment("\n", "newline"));
            }
            // 移除最后一个多余的换行
            if (!fragments.isEmpty() && fragments.get(fragments.size() - 1).type.equals("newline")) {
                fragments.remove(fragments.size() - 1);
            }

            return fragments;
        }

        /** 解析单行 Java 代码。 */
        private List<CodeFragment> parseJavaLine(String line) {
            List<CodeFragment> lineFragments = new ArrayList<>();
            if (line.trim().isEmpty()) {
                lineFragments.add(new CodeFragment(line, "text"));
                return lineFragments;
            }

            // 匹配顺序：注释 → 字符串 → 关键字 → 数字 → 其他
            String remaining = line;
            
            // 1. 行注释
            if (remaining.startsWith("//")) {
                lineFragments.add(new CodeFragment(remaining, "comment"));
                return lineFragments;
            }

            // 2. 块注释（简化处理）
            if (remaining.contains("/*") || remaining.contains("*/")) {
                lineFragments.add(new CodeFragment(remaining, "comment"));
                return lineFragments;
            }

            // 3. 字符串（双引号）
            Pattern stringPattern = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");
            Matcher stringMatcher = stringPattern.matcher(remaining);
            int lastEnd = 0;
            while (stringMatcher.find()) {
                // 字符串前的文本
                if (stringMatcher.start() > lastEnd) {
                    String before = remaining.substring(lastEnd, stringMatcher.start());
                    lineFragments.addAll(parseNonStringJava(before));
                }
                // 字符串本身
                lineFragments.add(new CodeFragment(stringMatcher.group(), "string"));
                lastEnd = stringMatcher.end();
            }
            // 剩余部分
            if (lastEnd < remaining.length()) {
                String after = remaining.substring(lastEnd);
                lineFragments.addAll(parseNonStringJava(after));
            } else if (lastEnd == 0) {
                // 没有字符串匹配
                lineFragments.addAll(parseNonStringJava(remaining));
            }

            return lineFragments;
        }

        /** 解析非字符串部分的 Java 代码。 */
        private List<CodeFragment> parseNonStringJava(String text) {
            List<CodeFragment> fragments = new ArrayList<>();
            if (text.isEmpty()) return fragments;

            // Java 关键字列表
            String[] keywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for",
                "goto", "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
            };

            // 数字（整数、浮点数）
            Pattern numberPattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
            // 关键字正则（单词边界）
            StringBuilder keywordRegex = new StringBuilder("\\b(");
            for (int i = 0; i < keywords.length; i++) {
                if (i > 0) keywordRegex.append("|");
                keywordRegex.append(keywords[i]);
            }
            keywordRegex.append(")\\b");

            Pattern keywordPattern = Pattern.compile(keywordRegex.toString());

            // 简单分词：按空格分割，但保留分割符
            String[] tokens = text.split("(?<=\\s)|(?=\\s)");
            for (String token : tokens) {
                if (token.trim().isEmpty()) {
                    fragments.add(new CodeFragment(token, "text")); // 空格
                    continue;
                }

                // 检查关键字
                if (keywordPattern.matcher(token).matches()) {
                    fragments.add(new CodeFragment(token, "keyword"));
                }
                // 检查数字
                else if (numberPattern.matcher(token).matches()) {
                    fragments.add(new CodeFragment(token, "number"));
                }
                else {
                    fragments.add(new CodeFragment(token, "text"));
                }
            }

            return fragments;
        }

        /** 创建带高亮的显示区域。 */
        private TextFlow createHighlightedDisplay(List<CodeFragment> fragments) {
            TextFlow flow = new TextFlow();
            flow.setPadding(new Insets(8));

            for (CodeFragment frag : fragments) {
                Text text = new Text(frag.content);
                text.getStyleClass().add("code-" + frag.type);
                flow.getChildren().add(text);
            }

            return flow;
        }

        /** 设置复制支持：Ctrl+C 和右键菜单。 */
        private void setupCopySupport(Node node, String fullCode) {
            // Ctrl+C 快捷键
            KeyCombination copyShortcut = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
            node.setOnKeyPressed(e -> {
                if (copyShortcut.match(e)) {
                    copyToClipboard(fullCode);
                    e.consume();
                }
            });

            // 右键菜单
            node.setOnContextMenuRequested(e -> {
                javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.MenuItem copyItem = new javafx.scene.control.MenuItem("复制");
                copyItem.setOnAction(ev -> copyToClipboard(fullCode));
                menu.getItems().add(copyItem);
                menu.show(node, e.getScreenX(), e.getScreenY());
            });

            // 双击/三击全选（视觉反馈）
            node.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() >= 2) {
                    // TextFlow 没有选中概念，这里给视觉反馈
                    node.getStyleClass().add(JfxStyles.CODEBLOCK_HIGHLIGHT);
                    javafx.animation.PauseTransition pause = 
                        new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                    pause.setOnFinished(ev -> node.getStyleClass().remove(JfxStyles.CODEBLOCK_HIGHLIGHT));
                    pause.play();
                }
            });
        }

        /** 复制到剪贴板。 */
        private void copyToClipboard(String text) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
        }

        /** 创建行号栏。 */
        private VBox createLineNumbers(String code) {
            VBox lineBox = new VBox(0);
            lineBox.getStyleClass().add("code-line-numbers");
            lineBox.setPadding(new Insets(8, 12, 8, 12));

            String[] lines = code.split("\n", -1);
            for (int i = 1; i <= lines.length; i++) {
                Label lineNum = new Label(String.valueOf(i));
                lineNum.getStyleClass().add("code-line-number");
                lineBox.getChildren().add(lineNum);
            }

            return lineBox;
        }

        /** 统计代码行数（用于 selectable 模式 TextArea 自适应高度）。 */
        private int countLines(String code) {
            if (code == null || code.isEmpty()) return 1;
            int lines = 1;
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '\n') lines++;
            }
            return lines;
        }
    }

    // ============================================================
    // 内部数据结构
    // ============================================================

    /** 代码片段（内容 + 类型）。 */
    private static class CodeFragment {
        final String content;
        final String type; // "keyword", "string", "comment", "number", "text", "newline"

        CodeFragment(String content, String type) {
            this.content = content;
            this.type = type;
        }
    }
}