package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.builder.Radius;

import java.util.function.Consumer;

/**
 * JFXium TextArea 组件 - 对标 Ant Design Input.TextArea（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：多行文本输入控件，继承自 {@link TextArea}，
 * 跟 {@link InputAnt} / {@link org.openkawu.jfxium.component.layout.VBoxAnt VBoxAnt}
 * 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * TextArea ta = TextAreaAnt.create()
 *     .placeholder("请输入描述")
 *     .rows(4)
 *     .onChange(text -> System.out.println(text))
 *     .build();
 *
 * // build 后再改状态（继承式核心优势）
 * ta.disabled(true).rows(6);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class DescriptionArea extends TextAreaAnt {
 *     public DescriptionArea() {
 *         placeholder("请输入描述...");
 *         rows(4);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link TextArea} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class TextAreaAnt extends TextArea {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空文本域）。 */
    public static TextAreaAnt create() {
        return new TextAreaAnt();
    }

    /** 工厂入口（带初始文本）。 */
    public static TextAreaAnt create(String text) {
        return new TextAreaAnt(text);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public TextAreaAnt() {
        super();
        init();
    }

    public TextAreaAnt(String text) {
        super(text);
        init();
    }

    private void init() {
        setWrapText(true);
        getStyleClass().add("jfx-text-area");
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置占位提示文本。 */
    public TextAreaAnt placeholder(String placeholder) {
        setPromptText(placeholder != null ? placeholder : "");
        return this;
    }

    /** 设置文本（链式包装 setText）。 */
    public TextAreaAnt text(String text) {
        setText(text != null ? text : "");
        return this;
    }

    /** 设置默认可见行数。 */
    public TextAreaAnt rows(int rows) {
        setPrefRowCount(rows);
        return this;
    }

    /** 设置是否自动换行。 */
    public TextAreaAnt wrapText(boolean wrap) {
        setWrapText(wrap);
        return this;
    }

    /** 设置禁用状态。 */
    public TextAreaAnt disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /** 设置可编辑状态。 */
    public TextAreaAnt editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    /** 监听文本变化。 */
    public TextAreaAnt onChange(Consumer<String> handler) {
        if (handler != null) {
            textProperty().addListener((obs, oldVal, newVal) -> handler.accept(newVal));
        }
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public TextAreaAnt bindValue(StringProperty property) {
        if (property != null) {
            textProperty().bindBidirectional(property);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    /** 追加一个 styleClass（幂等）。 */
    public TextAreaAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass。 */
    public TextAreaAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    /** inline style（应急用，优先用 styleClass + LESS）。 */
    public TextAreaAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /**
     * 设置圆角（精确档位），默认 {@link Radius#SM}。
     */
    public TextAreaAnt borderRadius(Radius radius) {
        getStyleClass().removeAll(JfxStyles.RADIUS_NONE, JfxStyles.RADIUS_SM, JfxStyles.RADIUS_LG);
        if (radius == Radius.NONE) {
            getStyleClass().add(JfxStyles.RADIUS_NONE);
        } else if (radius == Radius.SM) {
            getStyleClass().add(JfxStyles.RADIUS_SM);
        } else if (radius == Radius.LG) {
            getStyleClass().add(JfxStyles.RADIUS_LG);
        }
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public TextAreaAnt build() {
        return this;
    }

    // ============================================================
    // 静态便捷方法
    // ============================================================

    /**
     * 创建只读文本域（用于显示错误信息等）。
     * 保留原静态方法以向后兼容。
     */
    public static TextArea readOnly(String message) {
        TextAreaAnt ta = new TextAreaAnt(message);
        ta.setEditable(false);
        ta.getStyleClass().add("jfx-text-area-read-only");
        return ta;
    }
}
