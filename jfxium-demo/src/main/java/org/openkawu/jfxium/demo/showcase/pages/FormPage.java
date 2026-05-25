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
        Label pageDesc = new Label("3 种布局 / 3 档尺寸 / 必填标记 / 帮助文本 / 5 种校验状态。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionLayouts(),
                        sectionRequired(),
                        sectionHelpText(),
                        sectionValidateStatus(),
                        sectionSizes()
                )
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
