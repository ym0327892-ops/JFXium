package org.openkawu.jfxium.component.control;

import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 排版组件 - 对标 Ant Design Typography。
 *
 * 重构：所有视觉样式（颜色/字号/装饰）走 LESS（{@code typography-*} 系列），
 * Java 端不再 setStyle 拼字符串。Font 由 Java 设置（结构性属性，level → fontSize 动态计算）。
 */
public class TypographyAnt {

    public enum Type {
        PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER, DISABLED
    }

    public static class TitleBuilder {
        private final String text;
        private final int level;

        public TitleBuilder(String text, int level) {
            this.text = text;
            this.level = level;
        }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.TYPOGRAPHY_TITLE);
            // Title 字号是 level 的函数，结构性属性留 Java；颜色由 LESS 控制
            double fontSize = switch (level) {
                case 1 -> 38;
                case 2 -> 30;
                case 3 -> 24;
                case 4 -> 20;
                case 5 -> 16;
                default -> 38;
            };
            label.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            return label;
        }
    }

    public static class ParagraphBuilder {
        private final String text;
        private boolean ellipsis = false;
        private int rows = 0;

        public ParagraphBuilder(String text) { this.text = text; }
        public ParagraphBuilder ellipsis(boolean ellipsis) { this.ellipsis = ellipsis; return this; }
        public ParagraphBuilder rows(int rows) { this.rows = rows; return this; }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.TYPOGRAPHY_PARAGRAPH);
            label.setWrapText(true);
            label.setFont(Font.font("System", 14));
            if (ellipsis && rows > 0) {
                // 高度限制是结构性属性，留 Java
                label.setMaxHeight(rows * 20);
            }
            return label;
        }
    }

    public static class TextBuilder {
        private final String text;
        private Type type = Type.PRIMARY;
        private boolean copyable = false;
        private boolean strong = false;
        private boolean italic = false;
        private boolean underline = false;
        private boolean delete = false;
        private boolean code = false;
        private boolean mark = false;

        public TextBuilder(String text) { this.text = text; }
        public TextBuilder type(Type type) { this.type = type; return this; }
        public TextBuilder copyable(boolean copyable) { this.copyable = copyable; return this; }
        public TextBuilder copyable() { return copyable(true); }
        public TextBuilder strong(boolean strong) { this.strong = strong; return this; }
        public TextBuilder strong() { return strong(true); }
        public TextBuilder italic(boolean italic) { this.italic = italic; return this; }
        public TextBuilder italic() { return italic(true); }
        public TextBuilder underline(boolean underline) { this.underline = underline; return this; }
        public TextBuilder underline() { return underline(true); }
        public TextBuilder delete(boolean delete) { this.delete = delete; return this; }
        public TextBuilder delete() { return delete(true); }
        public TextBuilder code(boolean code) { this.code = code; return this; }
        public TextBuilder code() { return code(true); }
        public TextBuilder mark(boolean mark) { this.mark = mark; return this; }
        public TextBuilder mark() { return mark(true); }

        public Label build() {
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.TYPOGRAPHY_TEXT);

            // type 走修饰类（PRIMARY 是默认无需追加）
            switch (type) {
                case SECONDARY -> label.getStyleClass().add(CssClasses.TYPOGRAPHY_SECONDARY);
                case SUCCESS -> label.getStyleClass().add(CssClasses.TYPOGRAPHY_SUCCESS);
                case WARNING -> label.getStyleClass().add(CssClasses.TYPOGRAPHY_WARNING);
                case DANGER -> label.getStyleClass().add(CssClasses.TYPOGRAPHY_DANGER);
                case DISABLED -> label.getStyleClass().add(CssClasses.TYPOGRAPHY_DISABLED);
                default -> { /* PRIMARY 无修饰 */ }
            }

            // strong 是 fontWeight 调整，走 Font；其余装饰走 styleClass
            if (strong) {
                label.setFont(Font.font("System", FontWeight.BOLD, 14));
            } else {
                label.setFont(Font.font("System", 14));
            }
            if (italic)    label.getStyleClass().add(CssClasses.TYPOGRAPHY_ITALIC);
            if (underline) label.getStyleClass().add(CssClasses.TYPOGRAPHY_UNDERLINE);
            if (delete)    label.getStyleClass().add(CssClasses.TYPOGRAPHY_DELETE);
            if (code)      label.getStyleClass().add(CssClasses.TYPOGRAPHY_CODE);
            if (mark)      label.getStyleClass().add(CssClasses.TYPOGRAPHY_MARK);

            if (copyable) {
                label.getStyleClass().add(CssClasses.TYPOGRAPHY_COPYABLE);
                label.setOnMouseClicked(e -> {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(text);
                    clipboard.setContent(content);
                });
            }
            return label;
        }
    }

    public static TitleBuilder title(String text, int level) { return new TitleBuilder(text, level); }
    public static ParagraphBuilder paragraph(String text) { return new ParagraphBuilder(text); }
    public static TextBuilder text(String text) { return new TextBuilder(text); }
}
