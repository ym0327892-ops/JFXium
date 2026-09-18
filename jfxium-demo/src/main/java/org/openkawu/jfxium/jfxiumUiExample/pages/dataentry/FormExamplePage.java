package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import org.openkawu.jfxium.component.layout.BorderPaneAnt;
import javafx.stage.Window;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.form.Rule;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.AlertAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.component.composite.FormAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

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
                        footerAlignSection(),
                        sectionDemoSection(),
                        layoutCompareSection(),
                        playgroundSection(),
                        alertAntSection()
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
                .header(infoBanner("请仔细填写以下信息，带 * 为必填项。"))
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
                        .header(infoBanner("请仔细填写以下信息"))
                        .item("姓名", nameField, true)
                        .item("手机", phoneField, true)
                        .footer(cancelBtn, resetBtn, submitBtn)   // 变长重载
                        .build();
                """;
        return Demos.sectionWithCode("4. header + 多按钮 footer",
                "header(Node) 顶部 banner；footer(Node...) 变长重载支持多按钮。",
                code, form);
    }

    /** 5. footer 对齐演示。 */
    private Node footerAlignSection() {
        BorderPane preview = BorderPaneAnt.create();
        preview.setCenter(buildAlignedFooterForm(Pos.CENTER_RIGHT));

        Node alignSwitch = SegmentedAnt.create()
                .option("left", "左对齐")
                .option("center", "居中")
                .option("right", "右对齐")
                .selected("right")
                .onChange(value -> preview.setCenter(buildAlignedFooterForm(resolveFooterAlign(value))))
                .build();

        String code = """
                FormAnt.create()
                        .layout(FormAnt.Layout.HORIZONTAL)
                        .item("姓名", nameField, true)
                        .item("手机", phoneField, true)
                        .footerAlign(Pos.CENTER_RIGHT)
                        .footer(cancelBtn, resetBtn, submitBtn)
                        .build();
                """;

        return Demos.sectionWithCode("5. footer 对齐",
                "footerAlign(Pos.*) 控制按钮组在 footer 区内的对齐方式；默认是右对齐。",
                code,
                Demos.column(
                        TypographyAnt.text("切换下面的选项，观察按钮组在 footer 中的对齐变化。")
                                .type(TypographyAnt.TextColor.SECONDARY).build(),
                        alignSwitch,
                        preview));
    }

    /** 6. section 分段标题。 */
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
        return Demos.sectionWithCode("6. section 分段",
                "section(String) 在长表单中按业务语义分组字段。",
                code, form);
    }

    /** 7. 三种 layout 对比。 */
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
        return Demos.sectionWithCode("7. 三种 layout 对比",
                "HORIZONTAL（标签左 + 控件右）/ VERTICAL（标签上 + 控件下）/ INLINE（一行内联）。",
                code, Demos.column(
                        TypographyAnt.text("HORIZONTAL:").build(), horizontal,
                        TypographyAnt.text("VERTICAL:").build(), vertical,
                        TypographyAnt.text("INLINE:").build(), inline));
    }

    private Node buildAlignedFooterForm(Pos footerAlign) {
        return FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .item("姓名", InputAnt.create().placeholder("请输入姓名").build(), true)
                .item("手机", InputAnt.create().placeholder("请输入手机号").build(), true)
                .footerAlign(footerAlign)
                .footer(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("重置").build(),
                        ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> MessageAnt.success("已提交")).build()
                )
                .build();
    }

    private static Pos resolveFooterAlign(String value) {
        if ("left".equals(value)) {
            return Pos.CENTER_LEFT;
        }
        if ("center".equals(value)) {
            return Pos.CENTER;
        }
        return Pos.CENTER_RIGHT;
    }

    /** 8. 交互演示：实时切换 layout / size / labelAlign / colon。 */
    private Node playgroundSection() {
        Binder<String> layoutBinder = PlayGround.binder("horizontal");
        Binder<String> sizeBinder = PlayGround.binder("default");
        Binder<String> labelAlignBinder = PlayGround.binder("right");
        Binder<String> colonBinder = PlayGround.binder("on");
        Supplier<Node> factory = () -> FormAnt.create()
                .layout(parseLayout(layoutBinder.get()))
                .size(parseSize(sizeBinder.get()))
                .labelAlign(parseAlign(labelAlignBinder.get()))
                .colon("on".equals(colonBinder.get()))
                .item("用户名", InputAnt.create().placeholder("请输入用户名").build(), true)
                .item("邮箱", InputAnt.create().placeholder("请输入邮箱").build(), true)
                .item("备注", InputAnt.create().placeholder("可选").build())
                .footer(ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> MessageAnt.success("已提交")).build())
                .build();
        return Demos.section("8. 交互演示",
                "通过左侧控件实时改变 Form 的 layout / size / labelAlign / colon，右侧表单实时重建反映配置。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("布局", PlayGround.segmented(layoutBinder,
                                PlayGround.entry("horizontal", "水平"),
                                PlayGround.entry("vertical", "垂直"),
                                PlayGround.entry("inline", "内联"))),
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small", "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large", "大"))),
                        PlayGround.row("标签对齐", PlayGround.segmented(labelAlignBinder,
                                PlayGround.entry("right", "右对齐"),
                                PlayGround.entry("left", "左对齐"))),
                        PlayGround.row("冒号", PlayGround.segmented(colonBinder,
                                PlayGround.entry("on", "显示"),
                                PlayGround.entry("off", "隐藏")))));
    }

    private static FormAnt.Layout parseLayout(String v) {
        if ("vertical".equals(v)) return FormAnt.Layout.VERTICAL;
        if ("inline".equals(v)) return FormAnt.Layout.INLINE;
        return FormAnt.Layout.HORIZONTAL;
    }

    private static Size parseSize(String v) {
        if ("small".equals(v)) return Size.SMALL;
        if ("large".equals(v)) return Size.LARGE;
        return Size.DEFAULT;
    }

    private static FormAnt.Align parseAlign(String v) {
        if ("left".equals(v)) return FormAnt.Align.LEFT;
        return FormAnt.Align.RIGHT;
    }

    /** 9. AlertAnt 独立 Stage 模态弹窗（继承 JavaFX 原生 Alert）。 */
    private Node alertAntSection() {
        // 当前 Scene 的 owner 窗口：弹窗会 initOwner 该窗口并 APPLICATION_MODAL 阻塞
        Window owner = getScene() != null ? getScene().getWindow() : null;

        // info：信息提示（accent 蓝色）
        Node infoBtn = ButtonAnt.create("info")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> AlertAnt.info(
                        "操作成功",
                        "数据已保存到服务器，下次刷新即可生效。",
                        owner).showAndWait())
                .build();

        // warning：警告（warning 黄色）
        Node warningBtn = ButtonAnt.create("warning")
                .type(ButtonAnt.Type.WARNING)
                .onClick(e -> AlertAnt.warning(
                        "注意",
                        "当前网络不稳定，部分功能可能受影响。",
                        owner).showAndWait())
                .build();

        // error：错误（danger 红色）
        Node errorBtn = ButtonAnt.create("error")
                .type(ButtonAnt.Type.DANGER)
                .onClick(e -> AlertAnt.error(
                        "保存失败",
                        "网络连接中断，请检查网络后重试。",
                        owner).showAndWait())
                .build();

        // confirm：确认对话框（success 绿色）+ 返回 boolean + 自定义按钮文案
        Node confirmBtn = ButtonAnt.create("confirm")
                .onClick(e -> {
                    AlertAnt a = AlertAnt.confirm(
                            "删除确认",
                            "此操作不可撤销，确认要删除这条记录吗？",
                            owner);
                    a.okText("删除")
                     .cancelText("再想想");
                    if (a.showAndWaitForOk()) {
                        MessageAnt.success("已删除");
                    } else {
                        MessageAnt.info("已取消");
                    }
                })
                .build();

        Node preview = Demos.row(infoBtn, warningBtn, errorBtn, confirmBtn);

        String code = """
                // 4 个静态工厂（owner 传 Scene Window 即可）
                AlertAnt.info("操作成功", "数据已保存到服务器", owner).showAndWait();
                AlertAnt.warning("注意", "网络不稳定", owner).showAndWait();
                AlertAnt.error("保存失败", "网络连接中断", owner).showAndWait();

                // confirm：返回 boolean + 自定义按钮文案
                AlertAnt a = AlertAnt.confirm("删除确认", "此操作不可撤销", owner)
                        .okText("删除")
                        .cancelText("再想想");
                if (a.showAndWaitForOk()) { ... }

                // 继承自 Alert：showAndWait() / getResult() / setOnHidden() 等 API 完全兼容
                """;

        return Demos.sectionWithCode("9. AlertAnt 模态弹窗",
                "独立 Stage 模态对话框（继承 javafx.scene.control.Alert），4 种 type 切换 header 配色与 icon。",
                code, preview);
    }

    /** 蓝色信息提示横幅（作为 FormAnt.header 的占位 Node）。 */
    private static Node infoBanner(String text) {
        return org.openkawu.jfxium.component.control.LabelAnt.create(text)
                .styleClass("jfx-form-info-banner")
                .build();
    }
}
