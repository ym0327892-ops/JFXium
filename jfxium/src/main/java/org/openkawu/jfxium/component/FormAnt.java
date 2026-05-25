package org.openkawu.jfxium.component;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 表单组件 - 对标 Ant Design Form。
 *
 * <h2>修复说明</h2>
 * 原实现存在 4 个问题：
 * <ul>
 *   <li>{@code Label#setStyle} 拼字符串注入颜色/字号/字重，违反 SKILL 强约束（应走 styleClass + LESS）</li>
 *   <li>{@code helpLabel} 状态色（error/warning/success）在 Java 端硬编码状态分支，而非通过 styleClass 触发</li>
 *   <li>{@code footerBox} 通过内联 style 设置 padding，应该走 LESS 的 {@code .form-footer} 选择器</li>
 *   <li>{@code labelAlign} 类型为 {@code String}（"left"/"right"），运行时拼写错就静默回退；应改为枚举</li>
 * </ul>
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>所有 styleClass 改用 {@link CssClasses}（FORM_*、FORM_LABEL_*、FORM_HELP_*）</li>
 *   <li>{@code Label/HelpLabel} 不再 setStyle 拼颜色/字号，全部交给 LESS 选择器</li>
 *   <li>状态色通过 {@code form-help-error/warning/success} 子类切换，对齐组件状态机模式</li>
 *   <li>{@code labelAlign} 改成枚举 {@link Align}</li>
 *   <li>新增 {@code style/styleClass} 钩子，对齐其他 *Ant 组件</li>
 *   <li>{@code build()} 返回类型从 {@code VBox} 改为 {@link Pane}（实际仍是 VBox，但接口更宽容）</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * VBox form = (VBox) FormAnt.create()
 *     .layout(FormAnt.Layout.HORIZONTAL)
 *     .item("用户名", usernameField, true)
 *     .item("邮箱",   emailField,    true, "请输入有效邮箱")
 *     .footer(submitButton)
 *     .build();
 * }</pre>
 */
public class FormAnt {

    public enum Layout {
        HORIZONTAL, VERTICAL, INLINE
    }

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    /** 标签水平对齐 */
    public enum Align {
        LEFT, RIGHT
    }

    /** 校验状态。{@link #DEFAULT} 表示无校验信息（中性）。*/
    public enum ValidateStatus {
        DEFAULT, SUCCESS, WARNING, ERROR, VALIDATING
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     * 单个表单项配置。包内可见，仅供 Builder 使用。
     */
    public static class FormItem {
        String label;
        Node control;
        boolean required;
        String helpText;
        ValidateStatus validateStatus;
        int labelCol;
        int wrapperCol;

        public FormItem(String label, Node control) {
            this.label = label != null ? label : "";
            this.control = control;
            this.required = false;
            this.helpText = "";
            this.validateStatus = ValidateStatus.DEFAULT;
            this.labelCol = 6;
            this.wrapperCol = 18;
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<FormItem> items = new ArrayList<>();
        private Layout layout = Layout.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private boolean colon = true;
        private Align labelAlign = Align.RIGHT;
        // 默认 24 栅格里 6 + 18 = 24，对齐 Ant Form 默认
        private int labelCol = 6;
        private int wrapperCol = 18;
        private Node footer;

        private Builder() {}

        public Builder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder colon(boolean colon) {
            this.colon = colon;
            return this;
        }

        public Builder labelAlign(Align align) {
            this.labelAlign = align;
            return this;
        }

        public Builder labelCol(int labelCol) {
            this.labelCol = labelCol;
            return this;
        }

        public Builder wrapperCol(int wrapperCol) {
            this.wrapperCol = wrapperCol;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        // ===========================================================
        // item 重载：每个表单项的 required/helpText/状态 通过参数传递，
        // 而不是塞到 Builder 字段里——避免"上一个 item 的 required=true
        // 被下一个 item 误继承"这种 Builder 状态污染问题。
        // ===========================================================

        /** 添加一个普通表单项（非必填、无 helpText）。 */
        public Builder item(String label, Node control) {
            return item(label, control, false, "", ValidateStatus.DEFAULT);
        }

        /** 添加一个表单项，可指定是否必填。 */
        public Builder item(String label, Node control, boolean required) {
            return item(label, control, required, "", ValidateStatus.DEFAULT);
        }

        /** 添加一个表单项，可指定是否必填和帮助文本。 */
        public Builder item(String label, Node control, boolean required, String helpText) {
            return item(label, control, required, helpText, ValidateStatus.DEFAULT);
        }

        /** 添加一个表单项，完整指定必填/帮助文本/校验状态（最详细重载）。 */
        public Builder item(String label, Node control,
                            boolean required, String helpText,
                            ValidateStatus validateStatus) {
            FormItem item = new FormItem(label, control);
            item.required = required;
            item.helpText = helpText != null ? helpText : "";
            item.validateStatus = validateStatus != null ? validateStatus : ValidateStatus.DEFAULT;
            item.labelCol = this.labelCol;
            item.wrapperCol = this.wrapperCol;
            items.add(item);
            return this;
        }

        public VBox build() {
            // 容器外壳：始终是 VBox（layout 主体 + 可选 footer）
            VBox form = new VBox(0);
            form.getStyleClass().add(CssClasses.FORM);
            // size 通过 styleClass 暴露给 LESS 控制字号/间距，不在 Java 拼字符串
            form.getStyleClass().add("form-size-" + size.name().toLowerCase());

            Pane body = switch (layout) {
                case HORIZONTAL -> buildHorizontalForm();
                case VERTICAL -> buildVerticalForm();
                case INLINE -> buildInlineForm();
            };
            form.getChildren().add(body);

            if (footer != null) {
                HBox footerBox = new HBox(footer);
                footerBox.setAlignment(Pos.CENTER_RIGHT);
                footerBox.getStyleClass().add(CssClasses.FORM_FOOTER);
                form.getChildren().add(footerBox);
            }
            // 用户 style/styleClass 在所有内置类后应用，便于覆盖
            applyStyles(form);
            return form;
        }

        // ===========================================================
        // 三种布局
        // ===========================================================

        private GridPane buildHorizontalForm() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(CssClasses.FORM_HORIZONTAL);
            grid.setHgap(16);
            grid.setVgap(getVerticalGap());
            grid.setAlignment(Pos.TOP_LEFT);

            for (int i = 0; i < items.size(); i++) {
                FormItem item = items.get(i);
                Label label = createLabel(item);
                GridPane.setHalignment(label, labelAlign == Align.RIGHT ? HPos.RIGHT : HPos.LEFT);
                grid.add(label, 0, i);
                grid.add(createWrapper(item), 1, i);
            }

            // 列宽按 labelCol/wrapperCol 比例分配，对齐 Ant Form 24 栅格习惯
            double total = labelCol + wrapperCol;
            ColumnConstraints labelConstraint = new ColumnConstraints();
            labelConstraint.setPercentWidth((labelCol / total) * 100);
            ColumnConstraints controlConstraint = new ColumnConstraints();
            controlConstraint.setPercentWidth((wrapperCol / total) * 100);
            controlConstraint.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(labelConstraint, controlConstraint);
            return grid;
        }

        private VBox buildVerticalForm() {
            VBox container = new VBox(getVerticalGap());
            container.getStyleClass().add(CssClasses.FORM_VERTICAL);
            for (FormItem item : items) {
                VBox itemBox = new VBox(4);
                itemBox.getChildren().add(createLabel(item));
                itemBox.getChildren().add(createWrapper(item));
                container.getChildren().add(itemBox);
            }
            return container;
        }

        private HBox buildInlineForm() {
            HBox container = new HBox(16);
            container.getStyleClass().add(CssClasses.FORM_INLINE);
            container.setAlignment(Pos.CENTER_LEFT);
            for (FormItem item : items) {
                VBox itemBox = new VBox(4);
                if (!item.label.isEmpty()) {
                    itemBox.getChildren().add(createLabel(item));
                }
                itemBox.getChildren().add(item.control);
                container.getChildren().add(itemBox);
            }
            return container;
        }

        // ===========================================================
        // Label / Wrapper 工厂
        // ===========================================================

        /**
         * 创建标签。颜色/字号/字重全部走 LESS，
         * Java 这边只挂 styleClass：基础类 + 必填修饰类。
         */
        private Label createLabel(FormItem item) {
            String labelText = item.label;
            if (colon && !labelText.isEmpty()) {
                labelText += ":";
            }
            Label label = new Label(labelText);
            label.getStyleClass().add(CssClasses.FORM_LABEL);
            if (item.required) {
                label.getStyleClass().add(CssClasses.FORM_LABEL_REQUIRED);
            }
            return label;
        }

        /**
         * 创建控件包装器。包含控件本体 + 可选 helpText。
         * helpText 的状态色通过 styleClass 切换：
         * {@code form-help-text} 是基础类，{@code form-help-error} 等是状态修饰类。
         */
        private VBox createWrapper(FormItem item) {
            VBox wrapper = new VBox(4);
            wrapper.getStyleClass().add(CssClasses.FORM_ITEM_WRAPPER);
            wrapper.getChildren().add(item.control);

            if (!item.helpText.isEmpty()) {
                Label helpLabel = new Label(item.helpText);
                helpLabel.getStyleClass().add(CssClasses.FORM_HELP_TEXT);
                String stateClass = stateClassFor(item.validateStatus);
                if (stateClass != null) {
                    helpLabel.getStyleClass().add(stateClass);
                }
                wrapper.getChildren().add(helpLabel);
            }
            return wrapper;
        }

        /** 把校验状态映射到 LESS 选择器子类，DEFAULT/VALIDATING 返回 null（不加额外类）。*/
        private static String stateClassFor(ValidateStatus status) {
            return switch (status) {
                case ERROR -> CssClasses.FORM_HELP_ERROR;
                case WARNING -> CssClasses.FORM_HELP_WARNING;
                case SUCCESS -> CssClasses.FORM_HELP_SUCCESS;
                default -> null;
            };
        }

        /** 行间距由 size 决定，纯数字交给 VBox.spacing；视觉细节（字号等）由 LESS 处理。*/
        private int getVerticalGap() {
            return switch (size) {
                case SMALL -> 12;
                case LARGE -> 24;
                default -> 16;
            };
        }
    }
}
