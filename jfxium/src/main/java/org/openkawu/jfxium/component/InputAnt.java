package org.openkawu.jfxium.component;

import javafx.scene.control.TextField;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 输入框组件 - 对标 Ant Design Input
 *
 * 功能特性：
 * - 三种尺寸：小(Small)、中(Default)、大(Large)
 * - 提示文本：placeholder 支持
 * - 禁用/只读状态
 * - 内联样式自定义
 * - 无障碍支持：自动设置 AccessibleText
 *
 * 使用示例：
 * <pre>{@code
 * // 基础输入框
 * TextField input = InputAnt.create()
 *     .placeholder("请输入姓名")
 *     .build();
 *
 * // 大尺寸输入框
 * TextField largeInput = InputAnt.create()
 *     .placeholder("请输入")
 *     .size(InputAnt.Size.LARGE)
 *     .build();
 *
 * // 禁用状态
 * TextField disabledInput = InputAnt.create()
 *     .placeholder("禁用状态")
 *     .disabled(true)
 *     .build();
 *
 * // 只读状态
 * TextField readOnlyInput = InputAnt.create()
 *     .text("只读内容")
 *     .readOnly(true)
 *     .build();
 * }</pre>
 */
public class InputAnt {

    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String placeholder = "";
        private String text = "";
        private Size size = Size.DEFAULT;
        private boolean disabled = false;
        private boolean readOnly = false;

        private Builder() {}

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public TextField build() {
            TextField textField = new TextField(text);

            // Size
            if (size == Size.SMALL) {
                textField.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                textField.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Properties
            textField.setPromptText(placeholder);
            textField.setDisable(disabled);
            textField.setEditable(!readOnly);

            // Accessibility
            textField.setFocusTraversable(true);
            if (!placeholder.isEmpty()) {
                textField.setAccessibleText(placeholder);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(textField);
            return textField;
        }
    }
}
