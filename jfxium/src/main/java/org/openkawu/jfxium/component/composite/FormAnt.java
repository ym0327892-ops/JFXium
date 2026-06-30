package org.openkawu.jfxium.component.composite;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.form.FormContext;
import org.openkawu.jfxium.core.form.Rule;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * JFXium 表单组件 - 对标 Ant Design Form。
 *
 * <h2>M19.23 增强：校验规则 + 字段联动 + 嵌套表单</h2>
 * <ul>
 *   <li>命名 item（{@code .item(label, control, name)}）+ 链式 {@link ItemBuilder} 配置规则 / helpText / 校验状态</li>
 *   <li>{@link Rule} 校验规则（required / pattern / minLength / email / custom 等）</li>
 *   <li>{@link FormContext} 统一管理字段值 / 错误，支持 {@link FormContext#onChange} 字段联动</li>
 *   <li>嵌套表单：item 控件本身可以是另一个 FormAnt 的 build 产物</li>
 *   <li>{@link FormResult} 包装：{@code buildResult()} 返回包含 root + context 的结果对象</li>
 * </ul>
 *
 * <h2>老 API 完全兼容</h2>
 * 不调 {@code .item(label, control, name)} 形式时，字段不进入 FormContext（与原行为一致）。
 *
 * <h2>使用示例</h2>
 *
 * <h3>基础（向下兼容）</h3>
 * <pre>{@code
 * VBox form = FormAnt.create()
 *     .layout(FormAnt.Layout.HORIZONTAL)
 *     .item("用户名", usernameField, true)
 *     .footer(submitBtn)
 *     .build();
 * }</pre>
 *
 * <h3>固定标签宽度 + onChange 实时校验 + reset（M19.47）</h3>
 * <pre>{@code
 * FormAnt.Result result = FormAnt.create()
 *     .labelWidth(120)                      // 固定像素标签宽度
 *     .labelAlign(FormAnt.Align.RIGHT)       // 标签右对齐
 *     .validateOnChange()                    // 输入时实时校验
 *     .requiredMark("(必填)")                // 自定义必填标记
 *     .rowGap(12)                            // 行间距
 *     .item("用户名", usernameField, "username")
 *         .required()
 *         .rule(Rule.minLength(3, "至少 3 个字符"))
 *         .end()
 *     .item("邮箱", emailField, "email")
 *         .required()
 *         .labelAlign(FormAnt.Align.LEFT)    // 单项覆盖：左对齐
 *         .end()
 *     .item("备用邮箱", backupEmailField, "backup")
 *         .hidden(true)                      // 隐藏（仍在 context 中）
 *         .end()
 *     .footer(submitBtn, resetBtn)
 *     .buildResult();
 *
 * // 提交
 * submitBtn.setOnAction(e -> {
 *     if (result.validate()) doSubmit(result.getValues());
 * });
 * // 重置
 * resetBtn.setOnAction(e -> result.reset());
 * }</pre>
 *
 * <h3>检验 + 联动（M19.23）</h3>
 * <pre>{@code
 * FormAnt.Result result = FormAnt.create()
 *     .item("用户名", usernameField, "username")
 *         .required()
 *         .rule(Rule.minLength(3, "至少 3 个字符"))
 *         .end()
 *     .item("密码", passwordField, "password")
 *         .required()
 *         .end()
 *     .item("确认密码", confirmField, "confirm")
 *         .required()
 *         .rule(Rule.custom(v -> {
 *             // 这里 v 是 confirm 字段的值；password 通过下面 onChange 同步
 *             return java.util.Objects.equals(v, passwordField.getText());
 *         }, "两次密码不一致"))
 *         .end()
 *     .footer(submitBtn)
 *     .buildResult();
 *
 * // 字段联动：password 变化时重新校验 confirm
 * result.context().onChange("password", (val, ctx) -> ctx.validateField("confirm"));
 *
 * submitBtn.setOnAction(e -> {
 *     if (result.validate()) {
 *         doSubmit(result.getValues());
 *     }
 * });
 * }</pre>
 */
public class FormAnt {

    public enum Layout {
        HORIZONTAL, VERTICAL, INLINE
    }

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    public enum Align {
        LEFT, RIGHT
    }

    /**
     * 校验触发时机。
     * <ul>
     *   <li>{@link #MANUAL}：仅调用 {@link Result#validate()} 时触发校验（默认）</li>
     *   <li>{@link #ON_CHANGE}：字段值变化时立即校验该字段，实时显示错误</li>
     * </ul>
     */
    public enum ValidateTrigger {
        MANUAL, ON_CHANGE
    }

    public enum ValidateStatus {
        DEFAULT, SUCCESS, WARNING, ERROR, VALIDATING
    }

    public static Builder create() {
        return new Builder();
    }

    /** 单个表单项配置（包内可见）。 */
    static class FormItem {
        String label;
        Node control;
        String name;        // M19.23：null 表示未命名，不进入 FormContext
        boolean required;
        String helpText;
        ValidateStatus validateStatus;
        List<Rule> rules = new ArrayList<>();
        // 单项级覆盖（M19.47）：null = 使用 form 级配置
        Align labelAlign;
        double labelWidth = -1;
        boolean hidden;

        FormItem(String label, Node control, String name) {
            this.label = TextUtils.safeText(label);
            this.control = control;
            this.name = name;
            this.required = false;
            this.helpText = "";
            this.validateStatus = ValidateStatus.DEFAULT;
        }
    }

    /**
     * 表单项链式配置器（M19.23）。
     * 通过 {@code .item(label, control, name)} 进入；通过 {@link #end()} 回链 Builder。
     */
    public static class ItemBuilder {
        private final FormItem item;
        private final Builder parent;

        ItemBuilder(FormItem item, Builder parent) {
            this.item = item;
            this.parent = parent;
        }

        /** 标记为必填（同时挂红 * 标签 + 自动加 required Rule）。 */
        public ItemBuilder required() {
            item.required = true;
            // 自动加一条 required 规则；用户也可以通过 rule(Rule.required("自定义消息")) 覆盖
            if (item.rules.stream().noneMatch(r -> r.getMessage().equals("此项必填"))) {
                item.rules.add(0, Rule.required());
            }
            return this;
        }

        public ItemBuilder rule(Rule rule) {
            if (rule != null) item.rules.add(rule);
            return this;
        }

        public ItemBuilder rules(Rule... rules) {
            if (rules != null) item.rules.addAll(Arrays.asList(rules));
            return this;
        }

        public ItemBuilder rules(Collection<Rule> rules) {
            if (rules != null) item.rules.addAll(rules);
            return this;
        }

        public ItemBuilder helpText(String text) {
            item.helpText = TextUtils.safeText(text);
            return this;
        }

        public ItemBuilder validateStatus(ValidateStatus status) {
            item.validateStatus = status != null ? status : ValidateStatus.DEFAULT;
            return this;
        }

        /**
         * 单项级标签对齐覆盖（覆盖 form 级 labelAlign）。
         * <p>仅 HORIZONTAL 布局生效。null 走 form 级默认。</p>
         */
        public ItemBuilder labelAlign(Align align) {
            item.labelAlign = align;
            return this;
        }

        /**
         * 单项级标签宽度覆盖（覆盖 form 级 labelWidth）。
         * <p>仅 HORIZONTAL 布局生效,且仅当 form 级也使用了 labelWidth 固定像素模式时有效。
         * &lt;=0 走 form 级默认。</p>
         */
        public ItemBuilder labelWidth(double px) {
            item.labelWidth = px;
            return this;
        }

        /** 隐藏当前表单项（仍在 FormContext 中,仅视觉不可见）。 */
        public ItemBuilder hidden(boolean hidden) {
            item.hidden = hidden;
            return this;
        }

        /** 隐藏当前表单项（语法糖）。 */
        public ItemBuilder hide() { return hidden(true); }

        /** 回链到父 Builder 继续链式配置。 */
        public Builder end() {
            return parent;
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Object> entries = new ArrayList<>(); // FormItem 或 SectionMarker（混合序列）
        private Layout layout = Layout.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private boolean colon = true;
        private Align labelAlign = Align.RIGHT;
        private int labelCol = 6;
        private int wrapperCol = 18;
        // 固定像素标签宽度（>0 时覆盖 labelCol/wrapperCol 比率体系）
        private double labelWidth = -1;
        // 行间距 / 列间距（>=0 时覆盖 CSS 默认值；-1 = 走 CSS token）
        private double rowGap = -1;
        private double columnGap = -1;
        // 校验触发时机
        private ValidateTrigger validateTrigger = ValidateTrigger.MANUAL;
        // required 标记文本（null = 不显示标记, "" = 使用 CSS 默认 " *"）
        private String requiredMark = null;
        // 是否允许标签文字换行
        private boolean labelWrap = false;
        // header / footer 增强（M19.39 spec）
        private Node header;
        private final List<Node> footerNodes = new ArrayList<>();
        private Pos footerAlign = Pos.CENTER_RIGHT;

        private Builder() {}

        public Builder layout(Layout layout) { this.layout = layout != null ? layout : Layout.HORIZONTAL; return this; }
        public Builder size(Size size) { this.size = size != null ? size : Size.DEFAULT; return this; }
        public Builder colon(boolean colon) { this.colon = colon; return this; }
        public Builder labelAlign(Align align) { this.labelAlign = align != null ? align : Align.RIGHT; return this; }
        public Builder labelCol(int labelCol) { this.labelCol = labelCol; return this; }
        public Builder wrapperCol(int wrapperCol) { this.wrapperCol = wrapperCol; return this; }

        /**
         * 固定像素标签宽度（替代 labelCol/wrapperCol 比率体系）。
         * <p>设置后 labelCol/wrapperCol 失效，标签列使用此固定宽度。
         * 典型值：80、100、120、160。</p>
         */
        public Builder labelWidth(double px) {
            this.labelWidth = TextUtils.safeNonNegative(px, -1);
            return this;
        }

        /**
         * 行间距（垂直方向 item 间距）。
         * <p>仅 HORIZONTAL / VERTICAL 布局生效。默认走 CSS {@code @spacing-lg} (16px)。</p>
         */
        public Builder rowGap(double px) {
            this.rowGap = TextUtils.safeNonNegative(px, -1);
            return this;
        }

        /**
         * 列间距（HORIZONTAL 布局下 label 与 control 之间的水平间距）。
         * <p>默认走 CSS {@code @spacing-lg} (16px)。</p>
         */
        public Builder columnGap(double px) {
            this.columnGap = TextUtils.safeNonNegative(px, -1);
            return this;
        }

        /**
         * 校验触发时机。
         * <ul>
         *   <li>{@link ValidateTrigger#MANUAL}（默认）：仅手动调用 {@code result.validate()} 时触发</li>
         *   <li>{@link ValidateTrigger#ON_CHANGE}：每个字段值变化时立即单独校验，实时显示错误提示</li>
         * </ul>
         */
        public Builder validateTrigger(ValidateTrigger trigger) {
            this.validateTrigger = trigger != null ? trigger : ValidateTrigger.MANUAL;
            return this;
        }

        /** onchange 校验（语法糖 = validateTrigger(ON_CHANGE)）。 */
        public Builder validateOnChange() { return validateTrigger(ValidateTrigger.ON_CHANGE); }

        /**
         * 自定义 required 标记文本。
         * <ul>
         *   <li>{@code null}（默认）：使用 CSS 默认 " *"（红色星号）</li>
         *   <li>{@code ""}：不显示 required 标记</li>
         *   <li>{@code "(必填)"}：自定义文案</li>
         * </ul>
         */
        public Builder requiredMark(String mark) { this.requiredMark = mark; return this; }

        /** 允许标签文字换行（默认单行截断）。 */
        public Builder labelWrap(boolean wrap) { this.labelWrap = wrap; return this; }

        /**
         * 设置 footer 区（单节点，向下兼容 M19.23 之前的 API）。
         * <p>覆盖语义：多次调用以最后一次为准。</p>
         */
        public Builder footer(Node footer) {
            this.footerNodes.clear();
            if (footer != null) this.footerNodes.add(footer);
            return this;
        }

        /**
         * 设置 footer 区（变长重载，M19.39 新增）—— 支持「取消 / 重置 / 提交」多按钮。
         * <p>覆盖语义：多次调用以最后一次为准。null 元素会被跳过。</p>
         */
        public Builder footer(Node... nodes) {
            this.footerNodes.clear();
            if (nodes != null) {
                for (Node n : nodes) {
                    if (n != null) this.footerNodes.add(n);
                }
            }
            return this;
        }

        /**
         * footer 按钮组对齐方式（M19.39 新增）。
         * <p>默认 {@code Pos.CENTER_RIGHT}（Ant Design 标准）。
         * 常见取值：{@code CENTER_LEFT}（向导步骤）/ {@code CENTER}（登录确认）。</p>
         */
        public Builder footerAlign(Pos align) {
            if (align != null) this.footerAlign = align;
            return this;
        }

        /** footer 按钮组左对齐。 */
        public Builder footerAlignLeft() {
            return footerAlign(Pos.CENTER_LEFT);
        }

        /** footer 按钮组居中对齐。 */
        public Builder footerAlignCenter() {
            return footerAlign(Pos.CENTER);
        }

        /** footer 按钮组右对齐（默认）。 */
        public Builder footerAlignRight() {
            return footerAlign(Pos.CENTER_RIGHT);
        }

        /**
         * 顶部 banner 区（M19.39 新增）—— 放置重要提示 / 标题图 / 用户信息等。
         * <p>覆盖语义：多次调用以最后一次为准。null 表示不渲染 header。</p>
         */
        public Builder header(Node header) {
            this.header = header;
            return this;
        }

        /**
         * 长表单分段标题（M19.39 新增）—— 在后续 item 之前插入一个视觉分组标题。
         * <p>INLINE layout 下忽略（单行表单分段无意义）。</p>
         */
        public Builder section(String title) {
            if (title != null && !title.isEmpty()) {
                entries.add(new SectionMarker(title));
            }
            return this;
        }

        // ===========================================================
        // 老 API（向下兼容）
        // ===========================================================

        public Builder item(String label, Node control) {
            return itemInternal(label, control, null, false, "", ValidateStatus.DEFAULT);
        }

        public Builder item(String label, Node control, boolean required) {
            return itemInternal(label, control, null, required, "", ValidateStatus.DEFAULT);
        }

        public Builder item(String label, Node control, boolean required, String helpText) {
            return itemInternal(label, control, null, required, helpText, ValidateStatus.DEFAULT);
        }

        public Builder item(String label, Node control,
                            boolean required, String helpText,
                            ValidateStatus validateStatus) {
            return itemInternal(label, control, null, required, helpText, validateStatus);
        }

        // ===========================================================
        // 新 API（M19.23）：命名 item + 链式配置
        // ===========================================================

        /**
         * 添加一个命名表单项，返回 {@link ItemBuilder} 链式配置。
         * <p>name 用于 FormContext 索引值 / 错误 / 联动。</p>
         */
        public ItemBuilder item(String label, Node control, String name) {
            FormItem fi = new FormItem(label, control, name);
            entries.add(fi);
            return new ItemBuilder(fi, this);
        }

        private Builder itemInternal(String label, Node control, String name,
                                     boolean required, String helpText,
                                     ValidateStatus status) {
            FormItem fi = new FormItem(label, control, name);
            fi.required = required;
            fi.helpText = TextUtils.safeText(helpText);
            fi.validateStatus = status != null ? status : ValidateStatus.DEFAULT;
            entries.add(fi);
            return this;
        }

        // ===========================================================
        // 构建
        // ===========================================================

        /** 直接构建（不需要 FormContext 时用此 API）。 */
        public VBox build() {
            return buildResult().getRoot();
        }

        /** 构建并返回 Result（含 FormContext 用于校验/联动）。 */
        public Result buildResult() {
            FormContext ctx = new FormContext();
            // 传递 validateTrigger 配置
            ctx.setValidateTrigger(validateTrigger == ValidateTrigger.ON_CHANGE);

            VBox form = new VBox();
            form.getStyleClass().add(JfxStyles.FORM);
            switch (size) {
                case SMALL -> form.getStyleClass().add(JfxStyles.FORM_SIZE_SMALL);
                case LARGE -> form.getStyleClass().add(JfxStyles.FORM_SIZE_LARGE);
                default -> {
                    // 默认尺寸由基础 Form 视觉承载，不额外挂无效的 size class。
                }
            }

            // header 区（M19.39）：位于全部内容之上
            if (header != null) {
                VBox headerBox = new VBox(header);
                headerBox.getStyleClass().add(JfxStyles.FORM_HEADER);
                form.getChildren().add(headerBox);
            }

            // rowGap / columnGap 覆盖 CSS 默认值（>=0 时生效，-1 走 CSS）
            // 走 JavaFX 原生 API（VBox.setSpacing / GridPane.setHgap），不拼接 setStyle 字符串
            if (rowGap >= 0) form.setSpacing(rowGap);

            // body 区：根据 layout 渲染 items + section markers
            Pane body = switch (layout) {
                case HORIZONTAL -> buildHorizontalForm(ctx);
                case VERTICAL -> buildVerticalForm(ctx);
                case INLINE -> buildInlineForm(ctx);
            };
            if (columnGap >= 0 && body instanceof GridPane grid) {
                grid.setHgap(columnGap);
            }
            form.getChildren().add(body);

            // footer 区（M19.39 增强：支持多节点 + 对齐配置）
            if (!footerNodes.isEmpty()) {
                HBox footerBox = new HBox();
                footerBox.setAlignment(footerAlign);
                footerBox.getStyleClass().add(JfxStyles.FORM_FOOTER);
                footerBox.getChildren().addAll(footerNodes);
                form.getChildren().add(footerBox);
            }
            applyStyles(form);
            return new Result(form, ctx);
        }

        // ===========================================================
        // 三种布局
        // ===========================================================

        private GridPane buildHorizontalForm(FormContext ctx) {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(JfxStyles.FORM_HORIZONTAL);
            grid.setAlignment(Pos.TOP_LEFT);

            int row = 0;
            for (Object entry : entries) {
                if (entry instanceof SectionMarker sm) {
                    // section 标题占满两列（M19.39）
                    Label sectionLabel = new Label(sm.title());
                    sectionLabel.getStyleClass().add(JfxStyles.FORM_SECTION_TITLE);
                    grid.add(sectionLabel, 0, row, 2, 1); // colspan=2
                    row++;
                } else if (entry instanceof FormItem item) {
                    if (item.hidden) continue; // 隐藏项跳过渲染
                    Label label = createLabel(item);
                    // 单项级 labelAlign 覆盖 form 级
                    Align effectiveAlign = item.labelAlign != null ? item.labelAlign : labelAlign;
                    GridPane.setHalignment(label, effectiveAlign == Align.RIGHT ? HPos.RIGHT : HPos.LEFT);
                    grid.add(label, 0, row);
                    grid.add(createWrapper(item, ctx), 1, row);
                    row++;
                }
            }

            // 列约束：固定像素模式 vs 比率模式
            if (labelWidth > 0) {
                // 固定像素模式
                ColumnConstraints labelConstraint = new ColumnConstraints();
                labelConstraint.setPrefWidth(labelWidth);
                labelConstraint.setMinWidth(labelWidth);
                labelConstraint.setMaxWidth(labelWidth);
                ColumnConstraints controlConstraint = new ColumnConstraints();
                controlConstraint.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().addAll(labelConstraint, controlConstraint);
            } else {
                // 比率模式（默认）
                double total = labelCol + wrapperCol;
                ColumnConstraints labelConstraint = new ColumnConstraints();
                labelConstraint.setPercentWidth((labelCol / total) * 100);
                ColumnConstraints controlConstraint = new ColumnConstraints();
                controlConstraint.setPercentWidth((wrapperCol / total) * 100);
                controlConstraint.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().addAll(labelConstraint, controlConstraint);
            }
            return grid;
        }

        private VBox buildVerticalForm(FormContext ctx) {
            VBox container = new VBox();
            container.getStyleClass().add(JfxStyles.FORM_VERTICAL);
            for (Object entry : entries) {
                if (entry instanceof SectionMarker sm) {
                    Label sectionLabel = new Label(sm.title());
                    sectionLabel.getStyleClass().add(JfxStyles.FORM_SECTION_TITLE);
                    container.getChildren().add(sectionLabel);
                } else if (entry instanceof FormItem item) {
                    if (item.hidden) continue;
                    VBox itemBox = new VBox();
                    itemBox.getStyleClass().add(JfxStyles.FORM_ITEM_BOX);
                    itemBox.getChildren().add(createLabel(item));
                    itemBox.getChildren().add(createWrapper(item, ctx));
                    container.getChildren().add(itemBox);
                }
            }
            return container;
        }

        private HBox buildInlineForm(FormContext ctx) {
            HBox container = new HBox();
            container.getStyleClass().add(JfxStyles.FORM_INLINE);
            container.setAlignment(Pos.CENTER_LEFT);
            // INLINE 模式忽略 section markers（spec Req 4 AC 6）
            for (Object entry : entries) {
                if (entry instanceof FormItem item) {
                    if (item.hidden) continue;
                    VBox itemBox = new VBox();
                    itemBox.getStyleClass().add(JfxStyles.FORM_ITEM_BOX);
                    if (!item.label.isEmpty()) {
                        itemBox.getChildren().add(createLabel(item));
                    }
                    itemBox.getChildren().add(item.control);
                    if (item.name != null) {
                        ctx.registerField(item.name, item.control, item.rules);
                    }
                    container.getChildren().add(itemBox);
                }
                // SectionMarker 在 INLINE 模式下被忽略
            }
            return container;
        }

        // ===========================================================
        // Label / Wrapper 工厂
        // ===========================================================

        private Label createLabel(FormItem item) {
            String labelText = item.label;
            if (colon && !labelText.isEmpty()) {
                labelText += ":";
            }
            // requiredMark 自定义：非 null 时用 Java 标签替代 CSS ::after 伪元素
            String effectiveRequiredMark = null;
            if (item.required) {
                effectiveRequiredMark = requiredMark != null ? requiredMark : " *";
            }
            if (effectiveRequiredMark != null && !effectiveRequiredMark.isEmpty()) {
                labelText += effectiveRequiredMark;
            }
            Label label = new Label(labelText);
            label.getStyleClass().add(JfxStyles.FORM_LABEL);
            if (item.required) {
                label.getStyleClass().add(JfxStyles.FORM_LABEL_REQUIRED);
                // 自定义 mark 时挂专用修饰类（跳过 CSS ::after，避免重复显示默认 *）
                if (requiredMark != null) {
                    label.getStyleClass().add(JfxStyles.FORM_LABEL_REQUIRED_CUSTOM);
                }
            }
            if (labelWrap) {
                label.setWrapText(true);
            }
            return label;
        }

        /**
         * Wrapper：控件本体 + 可选 helpText + 动态 errorLabel（M19.23）。
         */
        private VBox createWrapper(FormItem item, FormContext ctx) {
            VBox wrapper = new VBox();
            wrapper.getStyleClass().add(JfxStyles.FORM_ITEM_WRAPPER);
            wrapper.getChildren().add(item.control);

            // 静态 helpText（不变）
            if (!item.helpText.isEmpty()) {
                Label helpLabel = new Label(item.helpText);
                helpLabel.getStyleClass().add(JfxStyles.FORM_HELP_TEXT);
                String stateClass = stateClassFor(item.validateStatus);
                if (stateClass != null) helpLabel.getStyleClass().add(stateClass);
                wrapper.getChildren().add(helpLabel);
            }

            // 命名 item：注册到 ctx + 动态 errorLabel 监听 errorProperty
            if (item.name != null) {
                ctx.registerField(item.name, item.control, item.rules);
                Label errorLabel = new Label();
                errorLabel.getStyleClass().addAll(
                        JfxStyles.FORM_HELP_TEXT, JfxStyles.FORM_HELP_ERROR);
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                ctx.errorProperty(item.name).addListener((obs, ov, nv) -> {
                    boolean hasErr = nv != null && !nv.isEmpty();
                    errorLabel.setText(hasErr ? nv : "");
                    errorLabel.setVisible(hasErr);
                    errorLabel.setManaged(hasErr);
                });
                wrapper.getChildren().add(errorLabel);
            }
            return wrapper;
        }

        private static String stateClassFor(ValidateStatus status) {
            return switch (status) {
                case ERROR -> JfxStyles.FORM_HELP_ERROR;
                case WARNING -> JfxStyles.FORM_HELP_WARNING;
                case SUCCESS -> JfxStyles.FORM_HELP_SUCCESS;
                default -> null;
            };
        }

    }

    /**
     * 构建结果（M19.23）：root VBox + FormContext 句柄。
     *
     * <p>常用 API：</p>
     * <ul>
     *   <li>{@link #validate()} 跑全部规则；返回是否全部通过</li>
     *   <li>{@link #getValues()} 一次性取全部字段值（提交表单时用）</li>
     *   <li>{@link #context()} 直接拿 FormContext 做 onChange 字段联动 / setValue / clearErrors 等</li>
     * </ul>
     */
    public static class Result {
        private final VBox root;
        private final FormContext ctx;

        Result(VBox root, FormContext ctx) {
            this.root = root;
            this.ctx = ctx;
        }

        public VBox getRoot() { return root; }
        public FormContext context() { return ctx; }

        public boolean validate() { return ctx.validate(); }
        public java.util.Map<String, Object> getValues() { return ctx.getValues(); }

        /** 重置所有字段值 + 清除所有错误。 */
        public void reset() { ctx.reset(); }

        /** 重置单个字段值 + 清除其错误。 */
        public void resetField(String name) { ctx.resetField(name); }

        /** 字段联动语法糖（直接转发到 context.onChange）。 */
        public void onChange(String dependency, BiConsumer<Object, FormContext> handler) {
            ctx.onChange(dependency, handler);
        }
    }

    // ============================================================
    // 内部数据结构
    // ============================================================

    /** 分段标题标记（M19.39）—— 与 FormItem 混合存储在 entries 列表中。 */
    record SectionMarker(String title) {}
}
