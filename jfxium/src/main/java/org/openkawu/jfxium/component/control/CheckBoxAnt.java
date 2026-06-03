package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * JFXium CheckBox Component
 * Inspired by Ant Design Checkbox
 *
 * Usage:
 * <pre>{@code
 * CheckBox checkBox = CheckBoxAnt.create("Remember me")
 *     .selected(true)
 *     .size(CheckBoxAnt.Size.SMALL)              // M19.6：与 InputAnt/ButtonAnt 一致
 *     .allowIndeterminate(true)                   // M19.6：让用户能在三态间循环
 *     .onChange(checked -> System.out.println("Checked: " + checked))
 *     .build();
 * }</pre>
 */
public class CheckBoxAnt {

    /** 尺寸枚举，与 ButtonAnt/InputAnt 完全一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    /**
     * 形状枚举（M19.20）。
     * <ul>
     *   <li>{@link #DEFAULT}：方形小圆角（4px，Ant Design 默认）</li>
     *   <li>{@link #CIRCLE}：圆形（与 RadioButton 同款外形，但仍是 CheckBox 的多选语义）</li>
     *   <li>{@link #SQUARE}：直角方形（0 圆角）</li>
     *   <li>{@link #ROUNDED}：大圆角（适合卡片式选择）</li>
     * </ul>
     */
    public enum Shape {
        DEFAULT,
        CIRCLE,
        SQUARE,
        ROUNDED
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private boolean selected = false;
        private boolean disabled = false;
        private boolean indeterminate = false;
        private boolean allowIndeterminate = false;
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.DEFAULT;
        private Consumer<Boolean> onChange;
        private BooleanProperty bindProperty = null;

        private Builder(String text) {
            this.text = text;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /** 初始勾选不确定状态（"半选"）。 */
        public Builder indeterminate(boolean indeterminate) {
            this.indeterminate = indeterminate;
            return this;
        }

        /**
         * 是否允许用户点击在 selected ↔ indeterminate ↔ unselected 三态间循环。
         * <p>对应 Ant Design Checkbox 的 indeterminate 三态切换语义。</p>
         */
        public Builder allowIndeterminate(boolean allow) {
            this.allowIndeterminate = allow;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        /**
         * 设置形状（M19.20）。默认 {@link Shape#DEFAULT}（方形小圆角，对齐 Ant Design）。
         */
        public Builder shape(Shape shape) {
            this.shape = shape != null ? shape : Shape.DEFAULT;
            return this;
        }

        public Builder onChange(Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(BooleanProperty property) {
            this.bindProperty = property;
            return this;
        }

        public CheckBox build() {
            CheckBox checkBox = new CheckBox(text);
            checkBox.setSelected(selected);
            checkBox.setDisable(disabled);
            checkBox.setIndeterminate(indeterminate);
            checkBox.setAllowIndeterminate(allowIndeterminate);

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                checkBox.selectedProperty().bindBidirectional(bindProperty);
            }

            if (onChange != null) {
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            // Size styleClass（复用通用常量，与 ButtonAnt/InputAnt 一致）
            if (size == Size.SMALL) {
                checkBox.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                checkBox.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Shape styleClass（M19.20）
            switch (shape) {
                case CIRCLE  -> checkBox.getStyleClass().add("shape-circle");
                case SQUARE  -> checkBox.getStyleClass().add("shape-square");
                case ROUNDED -> checkBox.getStyleClass().add("shape-rounded");
                default      -> { /* DEFAULT 不挂额外类 */ }
            }

            checkBox.getStyleClass().add("jfx-check-box");
            applyStyles(checkBox);
            return checkBox;
        }
    }
}
