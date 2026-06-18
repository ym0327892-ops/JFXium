package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.ModalAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Modal 模态对话框 —— 基础信息 / 确认 / 自定义内容 / 自定义宽度。
 *
 * <p><b>关键点</b>：ModalAnt.create()...build() 返回的是 ModalResult，<b>不是 Node</b>，
 * 必须再调 .open(owner) 才会弹出。owner 传当前任意 Node 即可（用于定位 Stage）。</p>
 */
public class ModalExamplePage extends VBoxAnt {

    public ModalExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Modal 模态对话框")
                .description("覆盖整个窗口的居中弹窗，必须用户处理完才能继续。")
                .sections(
                        basicSection(),
                        confirmSection(),
                        customContentSection(),
                        widthSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 最基础：title + content + 默认按钮。 */
    private Node basicSection() {
        Node btn = ButtonAnt.create("打开 Modal").type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> ModalAnt.create()
                        .title("基础对话框")
                        .content("这是一段说明文字。Modal 默认包含「确定 / 取消」两个按钮。")
                        .build()
                        .open((Node) e.getSource()))
                .build();
        String code = """
                ModalAnt.create()
                        .title("基础对话框")
                        .content("这是一段说明文字。")
                        .build()
                        .open(ownerNode);   // owner 传当前任意 Node
                """;
        return Demos.sectionWithCode("1. 基础",
                "title(...) + content(String) + build().open(owner) —— 最简用法。",
                code, btn);
    }

    /** 2. 确认对话框 + onOk 回调（删除场景）。 */
    private Node confirmSection() {
        Node btn = ButtonAnt.create("删除（带二次确认）").type(ButtonAnt.Type.DANGER)
                .onClick(e -> ModalAnt.create()
                        .title("确认删除")
                        .content("此操作不可撤销，确定要删除这条记录吗？")
                        .okText("删除")
                        .cancelText("取消")
                        .onOk(() -> MessageAnt.success("记录已删除"))
                        .build()
                        .open((Node) e.getSource()))
                .build();
        String code = """
                ModalAnt.create()
                        .title("确认删除")
                        .content("此操作不可撤销，确定要删除这条记录吗？")
                        .okText("删除")
                        .cancelText("取消")
                        .onOk(() -> MessageAnt.success("记录已删除"))
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("2. 危险操作二次确认",
                "onOk(Runnable) 只有点「确定」才触发 —— 配合 type=DANGER 触发器是删除场景标准用法。",
                code, btn);
    }

    /** 3. 自定义 content 节点（可放任意复杂表单 / 滚动内容）。 */
    private Node customContentSection() {
        Node btn = ButtonAnt.create("打开登录表单 Modal")
                .onClick(e -> {
                    InputAnt username = InputAnt.create().placeholder("用户名");
                    Node password = InputAnt.createPassword()
                            .placeholder("密码")
                            .build();
                    VBarAnt form = VBarAnt.create()
                            .compact()
                            .gap(12)
                            .top(
                                    TypographyAnt.text("请输入登录信息：").build(),
                                    username,
                                    password
                            )
                            .build();

                    ModalAnt.create()
                            .title("登录")
                            .content(form)              // ← Node 重载，可以放任意复杂内容
                            .okText("登录")
                            .onOk(() -> MessageAnt.info("尝试登录：" + username.getText()))
                            .build()
                            .open((Node) e.getSource());
                })
                .build();
        String code = """
                // content(Node) 重载 —— 可以塞任意复杂控件
                VBarAnt form = VBarAnt.create()
                        .compact()
                        .gap(12)
                        .top(
                                TypographyAnt.text("请输入登录信息：").build(),
                                InputAnt.create().placeholder("用户名").build(),
                                InputAnt.createPassword().placeholder("密码").build()
                        )
                        .build();

                ModalAnt.create()
                        .title("登录")
                        .content(form)
                        .okText("登录")
                        .onOk(() -> doLogin())
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("3. 自定义 content 节点",
                "content(Node) 重载 —— 可以塞任意复杂控件（表单、列表、滚动区...）。",
                code, btn);
    }

    /** 4. 自定义宽度 + 关闭按钮位置。 */
    private Node widthSection() {
        Node btn = ButtonAnt.create("宽 720px + 居中 + 左侧关闭按钮")
                .onClick(e -> ModalAnt.create()
                        .title("用户协议")
                        .content("这是一段较长的内容，演示 width(720) 让 Modal 更宽。\n\n" +
                                 "centered() 让 Modal 垂直居中（默认是顶部偏上）。\n" +
                                 "closePlacement(LEFT) 把关闭 X 按钮放到左侧。")
                        .width(720)
                        .centered()
                        .closePlacement(ModalAnt.ClosePlacement.LEFT)
                        .build()
                        .open((Node) e.getSource()))
                .build();
        String code = """
                ModalAnt.create()
                        .title("用户协议")
                        .content("较长的内容...")
                        .width(720)                                  // 自定义宽度
                        .centered()                                  // 垂直居中
                        .closePlacement(ModalAnt.ClosePlacement.LEFT) // 关闭按钮放左侧
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("4. 宽度 / 居中 / 关闭按钮位置",
                "width(int) 自定义宽度；centered() 垂直居中；closePlacement 控制关闭按钮位置。",
                code, btn);
    }
}
