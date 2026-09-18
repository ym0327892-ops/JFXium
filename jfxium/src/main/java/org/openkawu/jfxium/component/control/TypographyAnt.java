package org.openkawu.jfxium.component.control;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.FontTokens;

/**
 * JFXium 排版组件 - 对标 Ant Design Typography(组合式,Builder 模式)。
 *
 * <p><b>定位</b>:富文本排版工厂,提供三种 Builder 入口({@link TitleBuilder} / {@link ParagraphBuilder} / {@link TextBuilder}),
 * 用于构建标题、段落、内联文本等富排版节点。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>标题</b>:5 级标题(level 1–5),字号自动计算(38/30/24/20/16)</li>
 *   <li><b>段落</b>:自动换行 + 省略号(ellipsis)+ 行数限制</li>
 *   <li><b>内联文本</b>:type 色彩(SECONDARY/SUCCESS/WARNING/DANGER/DISABLED)
 *       + 装饰(strong/italic/underline/delete/code/mark)+ 可复制(copyable)</li>
 *   <li>所有视觉样式走 LESS({@code typography-*} 系列),Java 端不再 setStyle</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>页面主标题 / 副标题</li>
 *   <li>帮助文本 / 说明段落</li>
 *   <li>带装饰的内联文本(代码片段、删除线、标记高亮)</li>
 *   <li>可复制文本(点击复制 API Key / Token)</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 1 级标题
 * Label h1 = TypographyAnt.title("系统概览", 1).build();
 *
 * // 段落(2 行省略)
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
 *     .type(TextColor.DANGER)
 *     .strong()
 *     .build();
 * }</pre>
 *
 * <h2>实现策略</h2>
 * <p>三个 Builder 内部统一委托给 {@link LabelAnt}(继承式 LabelAnt IS-A 原生 Label),
 * 业务侧的 {@code build()} 返回类型仍是原生 {@link Label}——API 完全兼容。
 * 委托的好处:</p>
 * <ul>
 *   <li><b>复用</b>:type 色彩、strong/italic/underline/delete/code/mark/copyable 的实现只维护一处</li>
 *   <li><b>幂等性</b>:LabelAnt.type() 内置 {@code removeAll} 先清后挂,避免多次调 type 叠加 bug</li>
 *   <li><b>行数限制</b>:TypographyAnt 独有的 title 字号 / paragraph 高度保留在 Java(Font API + maxHeight 是结构性属性)</li>
 * </ul>
 */
public class TypographyAnt {

    /**
     * 文字语义色枚举（P0-F5 修复：原名 {@code Type} 与 {@link ButtonAnt.Type} 冲突，改名 {@code TextColor}）。
     *
     * <p>{@code Type} 是 {@link TypographyAnt} 文字色枚举与 {@link ButtonAnt} 按钮变体枚举同名，
     * 在 {@link LabelAnt#type(TypographyAnt.TextColor)} 的方法签名里两个枚举都叫 {@code Type}，
     * IDE 自动补全会优先匹配 {@code ButtonAnt.Type}，导致 {@code .type(...)} 调用了错误的重载
     * （或编译期 {@code switch} 报 missing case）。改名 {@code TextColor} 后语义直观、不再冲突。</p>
     */
    public enum TextColor {
        PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER, DISABLED
    }

    public static class TitleBuilder extends AbstractStyleBuilder<TitleBuilder> {
        private final String text;
        private final int level;

        public TitleBuilder(String text, int level) {
            this.text = text;
            this.level = level;
        }

        public Label build() {
            // 委托给 LabelAnt,它默认挂 TYPOGRAPHY_TEXT——Title 不是 Text,先清掉
            LabelAnt label = LabelAnt.create(text);
            label.getStyleClass().remove(JfxStyles.TYPOGRAPHY_TEXT);
            label.getStyleClass().add(JfxStyles.TYPOGRAPHY_TITLE);
            // Title 字号是 level 的函数,结构性属性留 Java;颜色由 LESS 控制
            double fontSize = switch (level) {
                case 1 -> FontTokens.TITLE_H1;
                case 2 -> FontTokens.TITLE_H2;
                case 3 -> FontTokens.TITLE_H3;
                case 4 -> FontTokens.TITLE_H4;
                case 5 -> FontTokens.TITLE_H5;
                default -> FontTokens.TITLE_H1;
            };
            label.setFont(Font.font(FontTokens.TEXT_FAMILY, FontWeight.BOLD, fontSize));
            // 用户通过 styleClass/style/padding/radius 注入的样式在最后追加,覆盖优先级最高
            applyStyles(label);
            return label;
        }
    }

    public static class ParagraphBuilder extends AbstractStyleBuilder<ParagraphBuilder> {
        private final String text;
        private boolean ellipsis = false;
        private int rows = 0;

        public ParagraphBuilder(String text) { this.text = text; }
        public ParagraphBuilder ellipsis(boolean ellipsis) { this.ellipsis = ellipsis; return this; }
        public ParagraphBuilder rows(int rows) { this.rows = rows; return this; }

        public Label build() {
            // 委托给 LabelAnt,Paragraph 也不是 Text
            LabelAnt label = LabelAnt.create(text);
            label.getStyleClass().remove(JfxStyles.TYPOGRAPHY_TEXT);
            label.getStyleClass().add(JfxStyles.TYPOGRAPHY_PARAGRAPH);
            label.wrap(true);
            label.setFont(Font.font(FontTokens.TEXT_FAMILY, FontTokens.FONT_SIZE_MD));
            if (ellipsis && rows > 0) {
                // 高度限制是结构性属性,留 Java
                label.setMaxHeight(rows * FontTokens.LINE_HEIGHT_NORMAL);
            }
            // 用户通过 styleClass/style/padding/radius 注入的样式在最后追加,覆盖优先级最高
            applyStyles(label);
            return label;
        }
    }

    public static class TextBuilder extends AbstractStyleBuilder<TextBuilder> {
        private final String text;
        private TextColor type = TextColor.PRIMARY;
        private boolean copyable = false;
        private boolean strong = false;
        private boolean italic = false;
        private boolean underline = false;
        private boolean delete = false;
        private boolean code = false;
        private boolean mark = false;

        public TextBuilder(String text) { this.text = text; }
        public TextBuilder type(TextColor type) { this.type = type; return this; }
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
            // 全部委托给 LabelAnt 链式 API:
            // - LabelAnt 默认构造挂 TYPOGRAPHY_TEXT
            // - .type() 内置 removeAll 幂等(已修 type switch 叠加 bug)
            // - .strong() 走 Font API 设 FontWeight.BOLD
            // - 其余装饰挂对应 jfx-typography-* styleClass
            // - .copyable() 挂 TYPOGRAPHY_COPYABLE + 接管 onMouseClicked
            LabelAnt label = LabelAnt.create(text)
                    .type(type)
                    .strong(strong)
                    .italic(italic)
                    .underline(underline)
                    .delete(delete)
                    .code(code)
                    .mark(mark)
                    .copyable(copyable);
            // 用户通过 styleClass/style/padding/radius 注入的样式在最后追加,覆盖优先级最高
            applyStyles(label);
            return label;
        }
    }

    public static TitleBuilder title(String text, int level) { return new TitleBuilder(text, level); }
    public static ParagraphBuilder paragraph(String text) { return new ParagraphBuilder(text); }
    public static TextBuilder text(String text) { return new TextBuilder(text); }
}
