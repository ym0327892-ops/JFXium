package org.openkawu.jfxium.component;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * JFXium 排版组件 - 对标 Ant Design Typography
 *
 * 提供标题、段落、文本等排版样式
 *
 * 使用示例：
 * <pre>{@code
 * // 标题
 * Label title = TypographyAnt.title("页面标题", 1).build();  // H1
 * Label subtitle = TypographyAnt.title("副标题", 2).build(); // H2
 *
 * // 段落
 * Label paragraph = TypographyAnt.paragraph("这是一段正文内容").build();
 *
 * // 文本变体
 * Label primary = TypographyAnt.text("主文本").type(TypographyAnt.Type.PRIMARY).build();
 * Label secondary = TypographyAnt.text("次要文本").type(TypographyAnt.Type.SECONDARY).build();
 * Label success = TypographyAnt.text("成功").type(TypographyAnt.Type.SUCCESS).build();
 * Label warning = TypographyAnt.text("警告").type(TypographyAnt.Type.WARNING).build();
 * Label danger = TypographyAnt.text("危险").type(TypographyAnt.Type.DANGER).build();
 *
 * // 可拷贝文本
 * Label copyable = TypographyAnt.text("点击复制我").copyable(true).build();
 * }</pre>
 */
public class TypographyAnt {

    public enum Type {
        PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER, DISABLED
    }

    public static class TitleBuilder {
        private String text;
        private int level = 1;

        public TitleBuilder(String text, int level) {
            this.text = text;
            this.level = level;
        }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add("typography-title");

            double fontSize;
            FontWeight weight = FontWeight.BOLD;

            switch (level) {
                case 1: fontSize = 38; break;
                case 2: fontSize = 30; break;
                case 3: fontSize = 24; break;
                case 4: fontSize = 20; break;
                case 5: fontSize = 16; break;
                default: fontSize = 38; break;
            }

            label.setFont(Font.font("System", weight, fontSize));
            label.setStyle("-fx-text-fill: -color-fg-default;");

            return label;
        }
    }

    public static class ParagraphBuilder {
        private String text;
        private boolean ellipsis = false;
        private int rows = 0;

        public ParagraphBuilder(String text) {
            this.text = text;
        }

        public ParagraphBuilder ellipsis(boolean ellipsis) {
            this.ellipsis = ellipsis;
            return this;
        }

        public ParagraphBuilder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add("typography-paragraph");
            label.setWrapText(true);
            label.setFont(Font.font("System", 14));
            label.setStyle("-fx-text-fill: -color-fg-default; -fx-line-spacing: 4px;");

            if (ellipsis && rows > 0) {
                label.setMaxHeight(rows * 20);
            }

            return label;
        }
    }

    public static class TextBuilder {
        private String text;
        private Type type = Type.PRIMARY;
        private boolean copyable = false;
        private boolean strong = false;
        private boolean italic = false;
        private boolean underline = false;
        private boolean delete = false;
        private boolean code = false;
        private boolean mark = false;

        public TextBuilder(String text) {
            this.text = text;
        }

        public TextBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public TextBuilder copyable(boolean copyable) {
            this.copyable = copyable;
            return this;
        }

        public TextBuilder copyable() {
            return copyable(true);
        }

        public TextBuilder strong(boolean strong) {
            this.strong = strong;
            return this;
        }

        public TextBuilder strong() {
            return strong(true);
        }

        public TextBuilder italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public TextBuilder italic() {
            return italic(true);
        }

        public TextBuilder underline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public TextBuilder underline() {
            return underline(true);
        }

        public TextBuilder delete(boolean delete) {
            this.delete = delete;
            return this;
        }

        public TextBuilder delete() {
            return delete(true);
        }

        public TextBuilder code(boolean code) {
            this.code = code;
            return this;
        }

        public TextBuilder code() {
            return code(true);
        }

        public TextBuilder mark(boolean mark) {
            this.mark = mark;
            return this;
        }

        public TextBuilder mark() {
            return mark(true);
        }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add("typography-text");

            String color;
            switch (type) {
                case SECONDARY: color = "-color-fg-muted"; break;
                case SUCCESS: color = "-color-success-emphasis"; break;
                case WARNING: color = "-color-warning-emphasis"; break;
                case DANGER: color = "-color-danger-emphasis"; break;
                case DISABLED: color = "-color-fg-subtle"; break;
                case PRIMARY:
                default: color = "-color-fg-default"; break;
            }

            String style = "-fx-text-fill: " + color + ";";

            if (strong) {
                label.setFont(Font.font("System", FontWeight.BOLD, 14));
            } else {
                label.setFont(Font.font("System", 14));
            }

            if (italic) {
                style += "-fx-font-style: italic;";
            }

            if (underline) {
                style += "-fx-underline: true;";
            }

            if (delete) {
                style += "-fx-strikethrough: true;";
            }

            if (code) {
                style += "-fx-font-family: 'SFMono-Regular', Consolas, monospace;" +
                        "-fx-background-color: -color-bg-subtle;" +
                        "-fx-padding: 2px 6px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-color: -color-border-default;" +
                        "-fx-border-radius: 4px;";
            }

            if (mark) {
                style += "-fx-background-color: #ffe58f;" +
                        "-fx-padding: 0 4px;";
            }

            label.setStyle(style);

            if (copyable) {
                label.setStyle(label.getStyle() + "-fx-cursor: hand;");
                label.setOnMouseClicked(e -> {
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(text);
                    clipboard.setContent(content);
                });
            }

            return label;
        }
    }

    public static TitleBuilder title(String text, int level) {
        return new TitleBuilder(text, level);
    }

    public static ParagraphBuilder paragraph(String text) {
        return new ParagraphBuilder(text);
    }

    public static TextBuilder text(String text) {
        return new TextBuilder(text);
    }
}
