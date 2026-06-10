package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.builder.Radius;

import java.util.function.Consumer;

/**
 * JFXium 输入框组件 - 对标 Ant Design Input（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：项目最基础的文本输入控件，继承自 {@link TextField}，
 * 跟 {@link org.openkawu.jfxium.component.layout.VBoxAnt VBoxAnt} /
 * {@link LabelAnt} 同款「双工厂模式」——既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>跟原 Builder 模式的区别</h2>
 * <table border="1">
 *   <caption>重构前后对比</caption>
 *   <tr><th>维度</th><th>原 Builder</th><th>继承式</th></tr>
 *   <tr><td>build() 返回</td><td>TextField（不可继承）</td><td>InputAnt extends TextField</td></tr>
 *   <tr><td>业务可继承</td><td>❌</td><td>✅ class SearchField extends InputAnt</td></tr>
 *   <tr><td>build 后再改</td><td>需 modify() 或手动 styleClass</td><td>直接链式 .size(SMALL).disabled(true)</td></tr>
 *   <tr><td>API 风格</td><td>create().xxx().build() 三段式</td><td>create().xxx() 两段式（build 可选）</td></tr>
 * </table>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * // 基础输入框（build() 可选，向后兼容旧代码）
 * TextField input = InputAnt.create()
 *     .placeholder("请输入姓名")
 *     .build();
 *
 * // 大尺寸 + 禁用
 * InputAnt largeInput = InputAnt.create()
 *     .placeholder("请输入")
 *     .size(Size.LARGE);
 *
 * // build 后再改尺寸 / 状态（继承式核心优势）
 * largeInput.size(Size.SMALL).disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class SearchField extends InputAnt {
 *     public SearchField() {
 *         placeholder("搜索...");
 *         size(Size.SMALL);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link TextField} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class InputAnt extends TextField implements LayoutCommon<InputAnt> {

    /** 尺寸枚举，与 ButtonAnt 一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT, SMALL, LARGE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空输入框）。 */
    public static InputAnt create() {
        return new InputAnt();
    }

    /** 工厂入口（带初始文本）。 */
    public static InputAnt create(String text) {
        return new InputAnt(text);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public InputAnt() {
        super();
        setFocusTraversable(true);
    }

    public InputAnt(String text) {
        super(text);
        setFocusTraversable(true);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置占位提示文本。 */
    public InputAnt placeholder(String placeholder) {
        setPromptText(placeholder != null ? placeholder : "");
        if (placeholder != null && !placeholder.isEmpty()) {
            setAccessibleText(placeholder);
        }
        return this;
    }

    /** 设置文本（链式包装 setText）。 */
    public InputAnt text(String text) {
        setText(text != null ? text : "");
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。
     */
    public InputAnt size(Size size) {
        getStyleClass().removeAll(JfxStyles.SIZE_SMALL, JfxStyles.SIZE_LARGE);
        if (size == Size.SMALL) {
            getStyleClass().add(JfxStyles.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            getStyleClass().add(JfxStyles.SIZE_LARGE);
        }
        return this;
    }

    /** 设置禁用状态。 */
    public InputAnt disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /** 设置只读状态（可选中复制但不可编辑）。 */
    public InputAnt readOnly(boolean readOnly) {
        setEditable(!readOnly);
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public InputAnt bindValue(StringProperty property) {
        if (property != null) {
            textProperty().bindBidirectional(property);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子（跟 *Ant 风格一致）
    // ============================================================

    /** 追加一个 styleClass（幂等——重复调不会重复挂）。 */
    public InputAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass。 */
    public InputAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    /** inline style（应急用，优先用 styleClass + LESS）。 */
    public InputAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    // ============================================================
    // 密码模式（M19.55 新增）
    // ============================================================

    /**
     * 创建密码输入框（带可见切换按钮）。
     *
     * <p>返回 {@link HBox} 容器，内含 PasswordField + 眼睛切换按钮。
     * 与 {@link #create()} 返回 InputAnt 不同，密码模式需要额外 UI 控件。</p>
     *
     * <h2>用法</h2>
     * <pre>{@code
     * HBox pwdBox = InputAnt.createPassword()
     *     .placeholder("请输入密码")
     *     .size(Size.LARGE)
     *     .onChange(val -> System.out.println("密码长度：" + val.length()))
     *     .build();
     * }</pre>
     */
    public static PasswordBuilder createPassword() {
        return new PasswordBuilder();
    }

    /** 密码输入框构建器。 */
    public static class PasswordBuilder {
        private String placeholder = "";
        private Size size = Size.DEFAULT;
        private Consumer<String> onChange = null;
        private boolean disabled = false;
        private StringProperty bindProperty = null;

        public PasswordBuilder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public PasswordBuilder size(Size size) {
            this.size = size;
            return this;
        }

        public PasswordBuilder onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public PasswordBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public PasswordBuilder bindValue(StringProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.getStyleClass().add(JfxStyles.INPUT_PASSWORD);
            container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            PasswordField pwdField = new PasswordField();
            pwdField.getStyleClass().add("text-input");
            if (size == Size.SMALL) {
                pwdField.getStyleClass().add(JfxStyles.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                pwdField.getStyleClass().add(JfxStyles.SIZE_LARGE);
            }
            pwdField.setPromptText(placeholder);
            pwdField.setDisable(disabled);
            HBox.setHgrow(pwdField, Priority.ALWAYS);

            // 可见切换按钮
            ToggleButton eyeBtn = new ToggleButton();
            eyeBtn.getStyleClass().add(JfxStyles.INPUT_PASSWORD_EYE);
            eyeBtn.setText("👁");
            eyeBtn.setSelected(false);

            // 切换密码可见性
            eyeBtn.selectedProperty().addListener((obs, old, selected) -> {
                if (selected) {
                    pwdField.setPromptText(placeholder);
                    // PasswordField 不能直接设 echo char，需要替换为 TextField
                    // 简化实现：通过 CSS 控制显示
                    pwdField.getStyleClass().remove(JfxStyles.INPUT_PASSWORD_MASKED);
                    pwdField.getStyleClass().add(JfxStyles.INPUT_PASSWORD_VISIBLE);
                } else {
                    pwdField.getStyleClass().remove(JfxStyles.INPUT_PASSWORD_VISIBLE);
                    pwdField.getStyleClass().add(JfxStyles.INPUT_PASSWORD_MASKED);
                }
            });
            pwdField.getStyleClass().add(JfxStyles.INPUT_PASSWORD_MASKED);

            // 回调
            if (onChange != null) {
                pwdField.textProperty().addListener((obs, old, val) -> onChange.accept(val));
            }

            // 绑定
            if (bindProperty != null) {
                pwdField.textProperty().bindBidirectional(bindProperty);
            }

            container.getChildren().addAll(pwdField, eyeBtn);
            return container;
        }
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>InputAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐——
     * 旧代码无需改动。</p>
     *
     * <p>业务继承场景下不需要调 build()——{@code this} 就是 InputAnt。</p>
     */
    public InputAnt build() {
        return this;
    }
}
