package org.openkawu.jfxium.component.control;

import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 排版组件 - 对标 Ant Design Typography（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：富文本排版工厂，提供三种 Builder 入口（{@link TitleBuilder} / {@link ParagraphBuilder} / {@link TextBuilder}），
 * 用于构建标题、段落、内联文本等富排版节点。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>标题</b>：5 级标题（level 1–5），字号自动计算（38/30/24/20/16）</li>
 *   <li><b>段落</b>：自动换行 + 省略号（ellipsis）+ 行数限制</li>
 *   <li><b>内联文本</b>：type 色彩（SECONDARY/SUCCESS/WARNING/DANGER/DISABLED）
 *       + 装饰（strong/italic/underline/delete/code/mark）+ 可复制（copyable）</li>
 *   <li>所有视觉样式走 LESS（{@code typography-*} 系列），Java 端不再 setStyle</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>页面主标题 / 副标题</li>
 *   <li>帮助文本 / 说明段落</li>
 *   <li>带装饰的内联文本（代码片段、删除线、标记高亮）</li>
 *   <li>可复制文本（点击复制 API Key / Token）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 1 级标题
 * Label h1 = TypographyAnt.title("系统概览", 1).build();
 *
 * // 段落（2 行省略）
 * Label p = TypographyAnt.paragraph("这是一段很长的描述文本...")
 *     .ellipsis(true)
 *     .rows(2)
 *     .build();
 *
 * // 带装饰的内联文本
 * Label code = TypographyAnt.text("git clone https://...")
 *     .code()
 *     .copyable()
 *     .build();
 *
 * Label danger = TypographyAnt.text("危险操作")
 *     .type(Type.DANGER)
 *     .strong()
 *     .build();
 * }</pre>
 *
 * <h2>与 LabelAnt 的区别</h2>
 * <ul>
 *   <li>{@code TypographyAnt} —— 富排版（多 Builder、多装饰、copyable），{@code build()} 返回原生 Label</li>
 *   <li>{@code LabelAnt} —— 轻量链式 Label，{@code extends Label}，支持业务继承</li>
 * </ul>
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
            label.getStyleClass().add(JfxStyles.TYPOGRAPHY_TITLE);
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
            label.getStyleClass().add(JfxStyles.TYPOGRAPHY_PARAGRAPH);
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
            label.getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);

            // type 走修饰类（PRIMARY 是默认无需追加）
            switch (type) {
                case SECONDARY -> label.getStyleClass().add(JfxStyles.TYPOGRAPHY_SECONDARY);
                case SUCCESS -> label.getStyleClass().add(JfxStyles.TYPOGRAPHY_SUCCESS);
                case WARNING -> label.getStyleClass().add(JfxStyles.TYPOGRAPHY_WARNING);
                case DANGER -> label.getStyleClass().add(JfxStyles.TYPOGRAPHY_DANGER);
                case DISABLED -> label.getStyleClass().add(JfxStyles.TYPOGRAPHY_DISABLED);
                default -> { /* PRIMARY 无修饰 */ }
            }

            // strong 是 fontWeight 调整，走 Font；其余装饰走 styleClass
            if (strong) {
                label.setFont(Font.font("System", FontWeight.BOLD, 14));
            } else {
                label.setFont(Font.font("System", 14));
            }
            if (italic)    label.getStyleClass().add(JfxStyles.TYPOGRAPHY_ITALIC);
            if (underline) label.getStyleClass().add(JfxStyles.TYPOGRAPHY_UNDERLINE);
            if (delete)    label.getStyleClass().add(JfxStyles.TYPOGRAPHY_DELETE);
            if (code)      label.getStyleClass().add(JfxStyles.TYPOGRAPHY_CODE);
            if (mark)      label.getStyleClass().add(JfxStyles.TYPOGRAPHY_MARK);

            if (copyable) {
                label.getStyleClass().add(JfxStyles.TYPOGRAPHY_COPYABLE);
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
