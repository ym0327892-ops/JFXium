package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * SelectableTextAnt（M19.7）— 只读、可选、可复制的文本组件。
 *
 * <p><b>定位</b>：看起来像一段普通 Label 文本，但用户可以拖动选中、Ctrl+C 复制。
 * 与 InputAnt / TextAreaAnt 严格区分：
 * <ul>
 *   <li>InputAnt / TextAreaAnt —— 用于<b>用户输入</b>，有边框/背景/焦点环</li>
 *   <li>SelectableTextAnt —— 用于<b>展示数据</b>，无边框/无背景，看起来像 Label</li>
 * </ul></p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>错误详情面板 —— 简短描述 + 详细信息可复制</li>
 *   <li>API 响应、stack trace、日志片段</li>
 *   <li>用户名 / 邮箱 / token / 订单号 —— 任何用户可能想复制的字段</li>
 *   <li>关于面板的版权、版本号等</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 *
 * <h3>单行（默认）</h3>
 * <pre>{@code
 * Node email = SelectableTextAnt.create("zhangsan@example.com").build();
 * }</pre>
 *
 * <h3>多行 + 自动换行 + 自动高度</h3>
 * <pre>{@code
 * Node detail = SelectableTextAnt.create(longErrorMessage)
 *     .multiline(true)
 *     .wrap(true)              // 超出宽度自动折行
 *     .maxWidth(420)           // 给定最大宽度
 *     .build();
 * }</pre>
 *
 * <h3>语义类型（错误/警告/次要文本）</h3>
 * <pre>{@code
 * Node hint = SelectableTextAnt.create("仅前端可见，不会上报")
 *     .type(SelectableTextAnt.Type.SECONDARY)
 *     .build();
 *
 * Node err = SelectableTextAnt.create(stackTrace)
 *     .multiline(true).wrap(true)
 *     .type(SelectableTextAnt.Type.ERROR)
 *     .build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>底层：单行用 {@link TextField}，多行用 {@link TextArea}（两者都原生支持 selection + Ctrl+C）</li>
 *   <li>视觉：通过 styleClass {@code .jfx-selectable-text} 去掉所有 input chrome（背景/边框/focus 阴影/padding）</li>
 *   <li>多行自动高度：TextArea 通过 {@code wrapText=true} + 监听文本变化动态算 row count，避免出现内部滚动条</li>
 *   <li>不支持 placeholder / onChange / disabled —— 它不是输入控件，是只读展示</li>
 * </ul>
 */
public class SelectableTextAnt {

    /** 语义类型（M19.7）—— 影响文字颜色。 */
    public enum Type {
        DEFAULT,    // 普通正文
        SECONDARY,  // 次要文字（灰色）
        SUCCESS,    // 成功（绿色）
        WARNING,    // 警告（橙色）
        ERROR       // 错误（红色）
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private boolean multiline = false;
        private boolean wrap = true;
        private double maxWidth = -1;
        private double prefWidth = -1;
        private Type type = Type.DEFAULT;
        private boolean bordered = false;     // 默认关闭灰色边框（看起来像 Label）
        private boolean focusHalo = true;     // 默认开启：用户拖选文字时显示蓝边+光晕，明确"已聚焦"

        private Builder(String text) {
            this.text = text != null ? text : "";
        }

        /** 是否多行（默认 false 单行）。多行用 TextArea，否则 TextField。 */
        public Builder multiline(boolean multiline) {
            this.multiline = multiline;
            return this;
        }

        /** 多行模式下是否按宽度自动折行（默认 true，仅 multiline=true 时生效）。 */
        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        /** 最大宽度（影响折行点）。 */
        public Builder maxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /** 推荐宽度。 */
        public Builder prefWidth(double prefWidth) {
            this.prefWidth = prefWidth;
            return this;
        }

        /** 语义类型（影响文字颜色）。 */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * 是否显示静态灰色边框 + 内边距（默认 false 完全无装饰）。
         * <p>开启后视觉变成"卡片式只读文本块"——适合长 stack trace、JSON、配置等需要明显边界的展示。</p>
         */
        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        /**
         * 是否在拖选/聚焦时显示蓝色边框 + 外阴影光晕（默认 true）。
         * <p>开启时给用户"已聚焦、可以 Ctrl+C"的明确视觉反馈；关闭时聚焦无任何视觉变化（更安静）。</p>
         */
        public Builder focusHalo(boolean focusHalo) {
            this.focusHalo = focusHalo;
            return this;
        }

        public Node build() {
            if (multiline) {
                return buildTextArea();
            } else {
                return buildTextField();
            }
        }

        private TextField buildTextField() {
            TextField tf = new TextField(text);
            tf.setEditable(false);
            tf.getStyleClass().add(CssClasses.SELECTABLE_TEXT);
            if (bordered) tf.getStyleClass().add(CssClasses.SELECTABLE_TEXT_BORDERED);
            if (focusHalo) tf.getStyleClass().add(CssClasses.SELECTABLE_TEXT_FOCUS_HALO);
            applyTypeClass(tf.getStyleClass());
            if (prefWidth > 0) tf.setPrefWidth(prefWidth);
            if (maxWidth > 0) tf.setMaxWidth(maxWidth);
            applyStyles(tf);
            return tf;
        }

        private TextArea buildTextArea() {
            TextArea ta = new TextArea(text);
            ta.setEditable(false);
            ta.setWrapText(wrap);
            ta.getStyleClass().add(CssClasses.SELECTABLE_TEXT);
            if (bordered) ta.getStyleClass().add(CssClasses.SELECTABLE_TEXT_BORDERED);
            if (focusHalo) ta.getStyleClass().add(CssClasses.SELECTABLE_TEXT_FOCUS_HALO);
            applyTypeClass(ta.getStyleClass());

            if (prefWidth > 0) ta.setPrefWidth(prefWidth);
            if (maxWidth > 0) ta.setMaxWidth(maxWidth);

            // 自动行数：根据文本中的换行符 + 折行估算，避免出现内部滚动条
            // 规则：手动换行计 1 行，wrap 模式下文本长 > 估算列数时再加行
            ta.setPrefRowCount(estimateRows(text, wrap, prefWidth > 0 ? prefWidth : maxWidth));

            // 文本变化时重算行数（用户用 textProperty().set(...) 替换内容时仍然自适应）
            ta.textProperty().addListener((obs, oldVal, newVal) -> {
                ta.setPrefRowCount(estimateRows(newVal, wrap, prefWidth > 0 ? prefWidth : maxWidth));
            });

            applyStyles(ta);
            return ta;
        }

        /**
         * 估算文本占用的行数。
         * <p>逻辑：每个 '\n' 算一行；若启用 wrap 且有目标宽度，按 ~14px/字符估算每行字符数加 wrap 行。</p>
         * <p>这是粗略估算，目的是让 TextArea 显示出全部内容不出现内部滚动条；
         * 不追求像素级精确，宁愿多算一两行。</p>
         */
        private static int estimateRows(String s, boolean wrap, double widthHint) {
            if (s == null || s.isEmpty()) return 1;
            int hardLines = 1;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '\n') hardLines++;
            }
            if (!wrap || widthHint <= 0) return hardLines;
            // 每行约 widthHint / 8 个字符（保守估）；wrap 加的行数 = 长行 / 该值
            int charsPerRow = Math.max(8, (int) (widthHint / 8.0));
            int wrapLines = 0;
            int curLineLen = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '\n') {
                    curLineLen = 0;
                } else {
                    curLineLen++;
                    if (curLineLen > charsPerRow) {
                        wrapLines++;
                        curLineLen = 0;
                    }
                }
            }
            return hardLines + wrapLines;
        }

        private void applyTypeClass(javafx.collections.ObservableList<String> styleClass) {
            switch (type) {
                case SECONDARY -> styleClass.add(CssClasses.SELECTABLE_TEXT_SECONDARY);
                case SUCCESS   -> styleClass.add(CssClasses.SELECTABLE_TEXT_SUCCESS);
                case WARNING   -> styleClass.add(CssClasses.SELECTABLE_TEXT_WARNING);
                case ERROR     -> styleClass.add(CssClasses.SELECTABLE_TEXT_ERROR);
                case DEFAULT   -> { /* 无修饰类 */ }
            }
        }
    }
}
