package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.PopconfirmAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Popconfirm 展示页（M19.9）—— 气泡确认框，比 Modal 轻量。
 */
public class PopconfirmPage implements ShowcasePage {

    @Override public String   key()      { return "popconfirm"; }
    @Override public String   title()    { return "Popconfirm 气泡确认"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Popconfirm 气泡确认");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("点击触发节点弹出气泡，比 Modal 轻量——admin「删除/批量操作」的快确认。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithDescription(),
                        sectionDelete(),
                        sectionCustomText()
                )
                .build();
    }

    private Node sectionBasic() {
        Button trigger = ButtonAnt.create("点我").build();
        PopconfirmAnt.create()
                .title("确认操作？")
                .target(trigger)
                .onConfirm(v -> MessageAnt.success("已确认"))
                .onCancel(v -> MessageAnt.info("已取消"))
                .build();
        trigger.setOnAction(e ->
                PopconfirmAnt.create()
                        .title("确认操作？")
                        .target(trigger)
                        .onConfirm(v -> MessageAnt.success("已确认"))
                        .onCancel(v -> MessageAnt.info("已取消"))
                        .build().show());

        return ShowcaseSection.create()
                .title("场景 1：基础用法")
                .description("点击按钮弹出确认气泡 —— 比 Modal 占用空间小，操作更轻")
                .demo(trigger)
                .code("""
                        Button trigger = ButtonAnt.create("点我").build();
                        trigger.setOnAction(e ->
                            PopconfirmAnt.create()
                                .title("确认操作？")
                                .target(trigger)
                                .onConfirm(v -> handleConfirm())
                                .onCancel(v -> handleCancel())
                                .build().show());
                        """)
                .build();
    }

    private Node sectionWithDescription() {
        Button trigger = ButtonAnt.create("提交订单").type(ButtonAnt.Type.PRIMARY).build();
        trigger.setOnAction(e ->
                PopconfirmAnt.create()
                        .title("确认提交订单？")
                        .description("提交后无法撤销，请确认订单信息无误")
                        .target(trigger)
                        .onConfirm(v -> MessageAnt.success("订单已提交"))
                        .build().show());

        return ShowcaseSection.create()
                .title("场景 2：带描述")
                .description(".description(...) 在标题下方加详细说明，告知用户后果")
                .demo(trigger)
                .code("""
                        PopconfirmAnt.create()
                            .title("确认提交订单？")
                            .description("提交后无法撤销")
                            .target(trigger)
                            .onConfirm(v -> submit())
                            .build().show();
                        """)
                .build();
    }

    private Node sectionDelete() {
        Button trigger = ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build();
        trigger.setOnAction(e ->
                PopconfirmAnt.create()
                        .title("确认删除？")
                        .description("此操作不可恢复")
                        .okText("删除")
                        .cancelText("再想想")
                        .target(trigger)
                        .onConfirm(v -> MessageAnt.show("已删除", MessageAnt.Type.SUCCESS, 3))
                        .build().show());

        return ShowcaseSection.create()
                .title("场景 3：删除场景（admin 高频）")
                .description("配 DANGER 按钮 + 自定义按钮文字「删除/再想想」—— 表格行删除的标配")
                .demo(trigger)
                .code("""
                        Button del = ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build();
                        del.setOnAction(e ->
                            PopconfirmAnt.create()
                                .title("确认删除？")
                                .description("此操作不可恢复")
                                .okText("删除").cancelText("再想想")
                                .target(del)
                                .onConfirm(v -> deleteRow())
                                .build().show());
                        """)
                .build();
    }

    private Node sectionCustomText() {
        Button retry = ButtonAnt.create("重试").build();
        retry.setOnAction(e ->
                PopconfirmAnt.create()
                        .title("重新发送请求？")
                        .okText("继续")
                        .cancelText("放弃")
                        .target(retry)
                        .onConfirm(v -> MessageAnt.info("正在重试..."))
                        .build().show());

        Button logout = ButtonAnt.create("退出").build();
        logout.setOnAction(e ->
                PopconfirmAnt.create()
                        .title("确认退出登录？")
                        .okText("退出")
                        .cancelText("留下")
                        .target(logout)
                        .onConfirm(v -> MessageAnt.success("已退出"))
                        .build().show());

        HBox row = HBoxBuilder.create().spacing(8).children(retry, logout).build();

        return ShowcaseSection.create()
                .title("场景 4：自定义按钮文字")
                .description(".okText / .cancelText —— 用动作动词替代「确定/取消」更精确")
                .demo(row)
                .code("""
                        PopconfirmAnt.create()
                            .title("...")
                            .okText("继续")
                            .cancelText("放弃")
                            .target(node)
                            .build().show();
                        """)
                .build();
    }
}
