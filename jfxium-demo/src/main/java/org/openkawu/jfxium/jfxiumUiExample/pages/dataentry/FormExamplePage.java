package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.form.Rule;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.FormAnt;
import org.openkawu.jfxium.component.composite.AlertAnt;

/**
 * Form 表单 —— 布局 / 校验 / 联动 / header / footer / section 分段。
 *
 * <p>FormAnt 是 JFXium 最复杂的组件之一，本页覆盖 M19.23（校验/联动）+
 * M19.39（header/footer/section）全部增强能力。</p>
 */
public class FormExamplePage extends VBoxAnt {

    public FormExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Form 表单")
                .description("高性能表单组件，支持 3 种布局 / 校验规则 / 字段联动 / 分段标题。")
                .sections(
                        basicSection(),
                        validateSection(),
                        linkedSection(),
                        headerFooterSection(),
                        sectionDemoSection(),
                        layoutCompareSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 基础表单（HORIZONTAL layout，3 字段，单按钮 footer）。 */
    private Node basicSection() {
        Node form = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .item("用户名", InputAnt.create().placeholder("请输入用户名").build())
                .item("邮箱", InputAnt.create().placeholder("请输入邮箱").build())
                .item("备注", InputAnt.create().placeholder("可选").build())
                .footer(ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> MessageAnt.success("提交成功")).build())
                .build();
        String code = """
                FormAnt.create()
                        .layout(FormAnt.Layout.HORIZONTAL)
                        .item("用户名", InputAnt.create().placeholder("请输入用户名").build())
                        .item("邮箱", InputAnt.create().placeholder("请输入邮箱").build())
                        .item("备注", InputAnt.create().placeholder("可选").build())
                        .footer(submitBtn)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础表单", "HORIZONTAL 布局 + 3 字段 + 单按钮 footer。", code, form);
    }

    /** 2. 校验示例（required + minLength + custom rule）。 */
    private Node validateSection() {
        // holder 模式：lambda 内引用 result 需要 effectively final 变量
        final FormAnt.Result[] holder = new FormAnt.Result[1];
        holder[0] = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .item("用户名", InputAnt.create().placeholder("至少 3 个字符").build(), "username")
                    .required()
                    .rule(Rule.minLength(3, "用户名至少 3 个字符"))
                    .end()
                .item("邮箱", InputAnt.create().placeholder("必须包含 @").build(), "email")
                    .required()
                    .rule(Rule.pattern(".*@.*", "请输入有效邮箱"))
                    .end()
                .footer(ButtonAnt.create("校验").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> {
                            boolean ok = holder[0].validate();
                            if (ok) MessageAnt.success("校验通过");
                        }).build())
                .buildResult();

        String code = """
                FormAnt.Result result = FormAnt.create()
                        .item("用户名", usernameField, "username")
                            .required()
                            .rule(Rule.minLength(3, "用户名至少 3 个字符"))
                            .end()
                        .item("邮箱", emailField, "email")
                            .required()
                            .rule(Rule.pattern(".*@.*", "请输入有效邮箱"))
                            .end()
                        .footer(validateBtn)
                        .buildResult();

                // 点击校验按钮时
                boolean ok = result.validate();
                """;
        return Demos.sectionWithCode("2. 校验", "required + minLength + pattern 规则，点击按钮触发校验。",
                code, holder[0].getRoot());
    }

    /** 3. 字段联动（password / confirm）。 */
    private Node linkedSection() {
        var pwdField = InputAnt.create().placeholder("密码").build();
        var confirmField = InputAnt.create().placeholder("确认密码").build();

        final FormAnt.Result[] holder = new FormAnt.Result[1];
        holder[0] = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .item("密码", pwdField, "password")
                    .required()
                    .end()
                .item("确认密码", confirmField, "confirm")
                    .required()
                    .rule(Rule.custom(v -> {
                        String pwd = pwdField.getText();
                        return pwd != null && pwd.equals(String.valueOf(v));
                    }, "两次密码不一致"))
                    .end()
                .footer(ButtonAnt.create("校验").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> holder[0].validate()).build())
                .buildResult();

        // 联动：password 变化时重新校验 confirm
        holder[0].onChange("password", (val, ctx) -> ctx.validateField("confirm"));

        String code = """
                result.onChange("password", (val, ctx) -> ctx.validateField("confirm"));
                """;
        return Demos.sectionWithCode("3. 字段联动",
                "password 变化时自动重新校验 confirm 字段。",
                code, holder[0].getRoot());
    }

    /** 4. header + 多按钮 footer。 */
    private Node headerFooterSection() {
        Node form = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .header(AlertAnt.info("请仔细填写以下信息，带 * 为必填项。").build())
                .item("姓名", InputAnt.create().placeholder("真实姓名").build(), true)
                .item("手机", InputAnt.create().placeholder("11 位手机号").build(), true)
                .footer(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("重置").build(),
                        ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> MessageAnt.success("已提交")).build()
                )
                .build();
        String code = """
                FormAnt.create()
                        .header(AlertAnt.info("请仔细填写以下信息").build())
                        .item("姓名", nameField, true)
                        .item("手机", phoneField, true)
                        .footer(cancelBtn, resetBtn, submitBtn)   // 变长重载
                        .build();
                """;
        return Demos.sectionWithCode("4. header + 多按钮 footer",
                "header(Node) 顶部 banner；footer(Node...) 变长重载支持多按钮。",
                code, form);
    }

    /** 5. section 分段标题。 */
    private Node sectionDemoSection() {
        Node form = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .section("基本信息")
                .item("姓名", InputAnt.create().placeholder("姓名").build())
                .item("年龄", InputAnt.create().placeholder("年龄").build())
                .section("联系方式")
                .item("邮箱", InputAnt.create().placeholder("邮箱").build())
                .item("电话", InputAnt.create().placeholder("电话").build())
                .section("权限设置")
                .item("角色", InputAnt.create().placeholder("admin / user").build())
                .footer(ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        String code = """
                FormAnt.create()
                        .section("基本信息")
                        .item("姓名", nameField)
                        .item("年龄", ageField)
                        .section("联系方式")
                        .item("邮箱", emailField)
                        .item("电话", phoneField)
                        .section("权限设置")
                        .item("角色", roleField)
                        .footer(saveBtn)
                        .build();
                """;
        return Demos.sectionWithCode("5. section 分段",
                "section(String) 在长表单中按业务语义分组字段。",
                code, form);
    }

    /** 6. 三种 layout 对比。 */
    private Node layoutCompareSection() {
        Node horizontal = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .item("用户名", InputAnt.create().placeholder("HORIZONTAL").build())
                .item("密码", InputAnt.create().placeholder("水平布局").build())
                .build();
        Node vertical = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("用户名", InputAnt.create().placeholder("VERTICAL").build())
                .item("密码", InputAnt.create().placeholder("垂直布局").build())
                .build();
        Node inline = FormAnt.create()
                .layout(FormAnt.Layout.INLINE)
                .item("用户名", InputAnt.create().placeholder("INLINE").build())
                .item("密码", InputAnt.create().placeholder("内联").build())
                .footer(ButtonAnt.create("查询").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        String code = """
                // 三种 layout
                FormAnt.create().layout(FormAnt.Layout.HORIZONTAL)...build();
                FormAnt.create().layout(FormAnt.Layout.VERTICAL)...build();
                FormAnt.create().layout(FormAnt.Layout.INLINE)...build();
                """;
        return Demos.sectionWithCode("6. 三种 layout 对比",
                "HORIZONTAL（标签左 + 控件右）/ VERTICAL（标签上 + 控件下）/ INLINE（一行内联）。",
                code, Demos.column(
                        new Label("HORIZONTAL:"), horizontal,
                        new Label("VERTICAL:"), vertical,
                        new Label("INLINE:"), inline));
    }
}
