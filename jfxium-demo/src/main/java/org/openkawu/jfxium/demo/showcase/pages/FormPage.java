package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.CheckBoxAnt;
import org.openkawu.jfxium.component.ComboBoxAnt;
import org.openkawu.jfxium.component.FormAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.component.SwitchAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Form 表单容器展示页（M19.8）。
 */
public class FormPage implements ShowcasePage {

    @Override public String   key()      { return "form"; }
    @Override public String   title()    { return "Form 表单"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Form 表单");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label(
                "3 种布局 / 3 档尺寸 / 必填标记 / 帮助文本 / 5 种校验状态。" +
                "M19.23 增强：校验规则 + 字段联动 + Result 句柄。"
        );
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");
        pageDesc.setWrapText(true);

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionLayouts(),
                        sectionRequired(),
                        sectionHelpText(),
                        sectionValidateStatus(),
                        sectionSizes(),
                        sectionValidationRules(),
                        sectionFieldDependency()
                )
                .build();
    }

    /** 场景 6：校验规则（M19.23）—— 提交时跑全部规则。 */
    private Node sectionValidationRules() {
        TextField username = input("3-16 字符，字母数字下划线");
        TextField email = input("user@example.com");
        TextField age = input("18-120");

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-text-fill: -color-fg-muted;");

        FormAnt.Result result = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("用户名", username, "username")
                    .required()
                    .rule(org.openkawu.jfxium.core.form.Rule.minLength(3, "至少 3 个字符"))
                    .rule(org.openkawu.jfxium.core.form.Rule.maxLength(16, "至多 16 个字符"))
                    .rule(org.openkawu.jfxium.core.form.Rule.pattern("^[a-zA-Z0-9_]+$",
                            "仅支持字母、数字、下划线"))
                    .end()
                .item("邮箱", email, "email")
                    .required()
                    .rule(org.openkawu.jfxium.core.form.Rule.email())
                    .end()
                .item("年龄", age, "age")
                    .required()
                    .rule(org.openkawu.jfxium.core.form.Rule.range(18, 120, "请输入 18-120 之间"))
                    .end()
                .buildResult();

        VBox formNode = result.getRoot();
        // 把提交按钮 + 重置按钮 + 结果标签拼到表单底部
        HBox actionBar = HBoxBuilder.create().spacing(8).children(
                ButtonAnt.create("提交校验")
                        .type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> {
                            if (result.validate()) {
                                resultLabel.setText("✓ 全部通过：" + result.getValues());
                                resultLabel.setStyle("-fx-text-fill: -color-success-emphasis;");
                            } else {
                                resultLabel.setText("✗ 校验失败，看每个字段下方红色提示");
                                resultLabel.setStyle("-fx-text-fill: -color-danger-emphasis;");
                            }
                        })
                        .build(),
                ButtonAnt.create("重置错误")
                        .onClick(e -> {
                            result.context().clearErrors();
                            resultLabel.setText("");
                        })
                        .build(),
                resultLabel
        ).build();
        formNode.getChildren().add(actionBar);

        return ShowcaseSection.create()
                .title("场景 6：校验规则（M19.23 新增）")
                .description("链式 .item(label, control, name).required().rule(Rule.email()) ... .end()。点提交按钮跑全部规则。")
                .demo(formNode)
                .code("""
                        FormAnt.Result result = FormAnt.create()
                            .item("用户名", usernameField, "username")
                                .required()
                                .rule(Rule.minLength(3, "至少 3 个字符"))
                                .rule(Rule.pattern("^[a-zA-Z0-9_]+$", "仅字母数字下划线"))
                                .end()
                            .item("邮箱", emailField, "email")
                                .required()
                                .rule(Rule.email())
                                .end()
                            .buildResult();

                        submitBtn.setOnAction(e -> {
                            if (result.validate()) {
                                doSubmit(result.getValues());
                            }
                        });
                        """)
                .build();
    }

    /** 场景 7：字段联动 —— 密码 / 确认密码（M19.23）。 */
    private Node sectionFieldDependency() {
        TextField password = input("密码");
        TextField confirm = input("再输一次");

        FormAnt.Result result = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("密码", password, "password")
                    .required()
                    .rule(org.openkawu.jfxium.core.form.Rule.minLength(6, "至少 6 位"))
                    .end()
                .item("确认密码", confirm, "confirm")
                    .required()
                    .rule(org.openkawu.jfxium.core.form.Rule.custom(
                            v -> java.util.Objects.equals(
                                    v == null ? "" : v.toString(),
                                    password.getText()),
                            "两次输入不一致"))
                    .end()
                .buildResult();

        // 字段联动：password 变化时重新校验 confirm
        result.onChange("password", (val, ctx) -> ctx.validateField("confirm"));
        // 同样 confirm 变化时重新校验自己（即时反馈）
        result.onChange("confirm", (val, ctx) -> ctx.validateField("confirm"));

        VBox formNode = result.getRoot();
        Label status = new Label("");
        status.setStyle("-fx-text-fill: -color-fg-muted;");
        formNode.getChildren().add(
                HBoxBuilder.create().spacing(8).children(
                        ButtonAnt.create("校验全部")
                                .type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> {
                                    boolean ok = result.validate();
                                    status.setText(ok ? "✓ 通过" : "✗ 失败");
                                    status.setStyle(ok
                                            ? "-fx-text-fill: -color-success-emphasis;"
                                            : "-fx-text-fill: -color-danger-emphasis;");
                                })
                                .build(),
                        status
                ).build()
        );

        return ShowcaseSection.create()
                .title("场景 7：字段联动（M19.23 新增）")
                .description("修改\"密码\"会自动重新校验\"确认密码\" —— 通过 result.onChange 实现")
                .demo(formNode)
                .code("""
                        FormAnt.Result result = FormAnt.create()
                            .item("密码", passwordField, "password")
                                .required().rule(Rule.minLength(6, "至少 6 位")).end()
                            .item("确认密码", confirmField, "confirm")
                                .required()
                                .rule(Rule.custom(
                                    v -> Objects.equals(v, passwordField.getText()),
                                    "两次输入不一致"))
                                .end()
                            .buildResult();

                        // 联动：password 变化时重新校验 confirm
                        result.onChange("password", (val, ctx) -> ctx.validateField("confirm"));
                        result.onChange("confirm", (val, ctx) -> ctx.validateField("confirm"));
                        """)
                .build();
    }

    private static TextField input(String placeholder) {
        TextField t = InputAnt.create().placeholder(placeholder).build();
        t.setPrefWidth(220);
        return t;
    }

    private Node sectionLayouts() {
        VBox horizontal = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .labelCol(6).wrapperCol(18)
                .item("用户名", input("请输入用户名"))
                .item("邮箱", input("请输入邮箱"))
                .build();

        VBox vertical = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("用户名", input("请输入用户名"))
                .item("邮箱", input("请输入邮箱"))
                .build();

        HBox row = HBoxBuilder.create().spacing(40)
                .children(
                        VBoxBuilder.create().spacing(6).children(grayLabel("HORIZONTAL（默认）"), horizontal).build(),
                        VBoxBuilder.create().spacing(6).children(grayLabel("VERTICAL"), vertical).build()
                ).build();

        return ShowcaseSection.create()
                .title("场景 1：3 种布局（HORIZONTAL / VERTICAL / INLINE）")
                .description("HORIZONTAL：标签在左 / VERTICAL：标签在上 / INLINE：行内排列（适合搜索栏）")
                .demo(row)
                .code("""
                        FormAnt.create()
                            .layout(FormAnt.Layout.HORIZONTAL)
                            .labelCol(6).wrapperCol(18)
                            .item("用户名", input)
                            .build();

                        FormAnt.create()
                            .layout(FormAnt.Layout.VERTICAL)
                            .item("用户名", input)
                            .build();
                        """)
                .build();
    }

    private Node sectionRequired() {
        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("用户名", input("必填项"), true)
                .item("邮箱", input("必填项"), true)
                .item("备注", input("可选"))
                .build();

        return ShowcaseSection.create()
                .title("场景 2：必填标记")
                .description(".item(label, control, true)：标签前自动显示红色 *")
                .demo(form)
                .code("""
                        FormAnt.create()
                            .item("用户名", input, true)        // 必填
                            .item("邮箱", input, true)
                            .item("备注", input)                 // 可选
                            .build();
                        """)
                .build();
    }

    private Node sectionHelpText() {
        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("密码", input("至少 8 位"), true, "需包含大小写字母与数字")
                .item("API Key", input(""), false, "可在「设置 → 安全」中生成")
                .build();

        return ShowcaseSection.create()
                .title("场景 3：帮助文本")
                .description(".item(label, control, required, helpText)：在输入框下方显示灰色提示")
                .demo(form)
                .code("""
                        FormAnt.create()
                            .item("密码", input, true, "需包含大小写字母与数字")
                            .build();
                        """)
                .build();
    }

    private Node sectionValidateStatus() {
        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("DEFAULT", input("默认"), false, "", FormAnt.ValidateStatus.DEFAULT)
                .item("SUCCESS", input("校验通过"), false, "格式正确", FormAnt.ValidateStatus.SUCCESS)
                .item("WARNING", input("有警告"), false, "建议补充更多信息", FormAnt.ValidateStatus.WARNING)
                .item("ERROR", input("校验失败"), true, "用户名已被占用", FormAnt.ValidateStatus.ERROR)
                .item("VALIDATING", input("正在校验"), false, "校验中...", FormAnt.ValidateStatus.VALIDATING)
                .build();

        return ShowcaseSection.create()
                .title("场景 4：5 种校验状态")
                .description("DEFAULT / SUCCESS / WARNING / ERROR / VALIDATING")
                .demo(form)
                .code("""
                        FormAnt.create()
                            .item("DEFAULT", input, false, "", ValidateStatus.DEFAULT)
                            .item("SUCCESS", input, false, "格式正确", ValidateStatus.SUCCESS)
                            .item("ERROR", input, true, "用户名已被占用", ValidateStatus.ERROR)
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        ComboBox<String> role = ComboBoxAnt.<String>create().items("管理员", "编辑", "访客").value("管理员").build();
        role.setPrefWidth(220);

        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .size(FormAnt.Size.DEFAULT)
                .item("用户名", input("请输入用户名"), true)
                .item("邮箱", input("请输入邮箱"), true)
                .item("角色", role, true)
                .item("启用", SwitchAnt.create().selected(true).build())
                .item("订阅邮件", CheckBoxAnt.create().selected(true).build())
                .footer(footerActions())
                .build();

        return ShowcaseSection.create()
                .title("场景 5：完整表单（含混合控件 + Footer）")
                .description("Input + Combo + Switch + CheckBox + footer 按钮组——admin 表单页的完整样板")
                .demo(form)
                .code("""
                        VBox form = FormAnt.create()
                            .layout(FormAnt.Layout.VERTICAL)
                            .item("用户名", input, true)
                            .item("邮箱", input, true)
                            .item("角色", roleCombo, true)
                            .item("启用", SwitchAnt.create().selected(true).build())
                            .footer(actionButtons)
                            .build();
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }

    private static HBox footerActions() {
        return HBoxBuilder.create()
                .spacing(8)
                .children(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY).build()
                )
                .build();
    }
}
