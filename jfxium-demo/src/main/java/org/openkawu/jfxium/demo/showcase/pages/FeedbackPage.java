package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.NotificationAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Message + Notification 浮层反馈展示页（M19.8）。
 *
 * <p>两者都是浮层反馈，区别：</p>
 * <ul>
 *   <li><b>Message</b>：轻量、单行、自动消失（操作即时反馈）</li>
 *   <li><b>Notification</b>：重量、含标题+描述、可关闭、停留时间长（重要提醒）</li>
 * </ul>
 */
public class FeedbackPage implements ShowcasePage {

    @Override public String   key()      { return "feedback"; }
    @Override public String   title()    { return "Message / Notification"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Message / Notification 浮层反馈");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("两者都是浮层提示——Message 轻量自动消失，Notification 信息更丰富可关闭。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionMessageBasic(),
                        sectionMessagePosition(),
                        sectionNotificationBasic(),
                        sectionNotificationPlacement()
                )
                .build();
    }

    // ============================================================
    // Message
    // ============================================================
    private Node sectionMessageBasic() {
        Node ok = ButtonAnt.create("Success").onClick(e -> MessageAnt.success("操作成功")).build();
        Node info = ButtonAnt.create("Info").onClick(e -> MessageAnt.info("有新消息")).build();
        Node warn = ButtonAnt.create("Warning").onClick(e ->
                MessageAnt.show("请检查输入", MessageAnt.Type.WARNING, 3)).build();
        Node err = ButtonAnt.create("Error").onClick(e ->
                MessageAnt.show("操作失败", MessageAnt.Type.ERROR, 3)).build();

        HBox row = HBoxBuilder.create().spacing(8).children(ok, info, warn, err).build();

        return ShowcaseSection.create()
                .title("场景 1：Message 4 种类型")
                .description("Message 是轻量浮层——单行内容，3 秒自动消失。点按钮触发")
                .demo(row)
                .code("""
                        MessageAnt.success("操作成功");
                        MessageAnt.info("有新消息");
                        MessageAnt.show("请检查输入", MessageAnt.Type.WARNING, 3);
                        MessageAnt.show("操作失败", MessageAnt.Type.ERROR, 3);
                        """)
                .build();
    }

    private Node sectionMessagePosition() {
        Node top = ButtonAnt.create("顶部").onClick(e ->
                MessageAnt.show("从顶部弹出", MessageAnt.Type.INFO, 2, MessageAnt.Position.TOP)).build();
        Node bottom = ButtonAnt.create("底部").onClick(e ->
                MessageAnt.show("从底部弹出", MessageAnt.Type.INFO, 2, MessageAnt.Position.BOTTOM)).build();
        Node center = ButtonAnt.create("中间").onClick(e ->
                MessageAnt.show("中间淡入", MessageAnt.Type.INFO, 2, MessageAnt.Position.CENTER)).build();

        HBox row = HBoxBuilder.create().spacing(8).children(top, bottom, center).build();

        return ShowcaseSection.create()
                .title("场景 2：Message 3 个位置")
                .description("TOP / BOTTOM / CENTER —— 中间位置只显示一个，新消息替换旧的（适合状态切换提示）")
                .demo(row)
                .code("""
                        MessageAnt.show("...", Type.INFO, 2, Position.TOP);
                        MessageAnt.show("...", Type.INFO, 2, Position.BOTTOM);
                        MessageAnt.show("...", Type.INFO, 2, Position.CENTER);
                        """)
                .build();
    }

    // ============================================================
    // Notification
    // ============================================================
    private Node sectionNotificationBasic() {
        Node ok = ButtonAnt.create("Success").onClick(e ->
                NotificationAnt.success("保存成功", "用户配置已生效，会话期间持续可用。")).build();
        Node info = ButtonAnt.create("Info").onClick(e ->
                NotificationAnt.info("提示", "请阅读最新的服务条款。")).build();
        Node warn = ButtonAnt.create("Warning").onClick(e ->
                NotificationAnt.warning("警告", "API 调用次数已达本月配额的 80%。")).build();
        Node err = ButtonAnt.create("Error").onClick(e ->
                NotificationAnt.error("错误", "无法保存：远程服务返回 503。\n请稍后重试或联系客服。")).build();

        HBox row = HBoxBuilder.create().spacing(8).children(ok, info, warn, err).build();

        return ShowcaseSection.create()
                .title("场景 3：Notification 4 种类型（标题 + 描述）")
                .description("Notification 信息更丰富——标题 + 多行描述 + 自动关闭按钮。重要提醒首选")
                .demo(row)
                .code("""
                        NotificationAnt.success("保存成功", "用户配置已生效。");
                        NotificationAnt.info("提示", "请阅读最新条款。");
                        NotificationAnt.warning("警告", "API 配额接近上限。");
                        NotificationAnt.error("错误", "服务返回 503。");
                        """)
                .build();
    }

    private Node sectionNotificationPlacement() {
        Node tl = ButtonAnt.create("左上").onClick(e ->
                NotificationAnt.create().title("左上").description("Placement.TOP_LEFT")
                        .placement(NotificationAnt.Placement.TOP_LEFT).build().show()).build();
        Node tr = ButtonAnt.create("右上（默认）").onClick(e ->
                NotificationAnt.create().title("右上").description("Placement.TOP_RIGHT")
                        .placement(NotificationAnt.Placement.TOP_RIGHT).build().show()).build();
        Node bl = ButtonAnt.create("左下").onClick(e ->
                NotificationAnt.create().title("左下").description("Placement.BOTTOM_LEFT")
                        .placement(NotificationAnt.Placement.BOTTOM_LEFT).build().show()).build();
        Node br = ButtonAnt.create("右下").onClick(e ->
                NotificationAnt.create().title("右下").description("Placement.BOTTOM_RIGHT")
                        .placement(NotificationAnt.Placement.BOTTOM_RIGHT).build().show()).build();

        HBox row = HBoxBuilder.create().spacing(8).children(tl, tr, bl, br).build();

        return ShowcaseSection.create()
                .title("场景 4：Notification 4 个角落")
                .description("TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT —— 默认右上角")
                .demo(row)
                .code("""
                        NotificationAnt.create()
                            .title("...").description("...")
                            .placement(NotificationAnt.Placement.BOTTOM_RIGHT)
                            .build().show();
                        """)
                .build();
    }
}
