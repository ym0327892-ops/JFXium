package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.NotificationAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Notification 通知提醒 —— 4 种类型 + 4 个角落位置。
 *
 * <p>跟 Message 的边界：Message 短而轻、自动消失；Notification 重而显眼、带标题描述、可手动关闭。</p>
 */
public class NotificationExamplePage extends VBoxAnt {

    public NotificationExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Notification 通知提醒")
                .description("带标题 + 描述的通知卡片，固定在屏幕角落，可手动关闭。")
                .sections(
                        typesSection(),
                        placementsSection()
                )
                .padding(24)
                .build());
    }

    private Node typesSection() {
        Node row = Demos.row(
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> NotificationAnt.success("提交成功", "数据已保存到服务器")).build(),
                ButtonAnt.create("错误").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> NotificationAnt.error("操作失败", "网络异常，请稍后重试")).build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> NotificationAnt.warning("权限不足", "请联系管理员开通相应权限")).build(),
                ButtonAnt.create("提示")
                        .onClick(e -> NotificationAnt.info("新消息", "你有 3 条未读邮件")).build()
        );
        String code = """
                // 静态便捷方法：title + description
                NotificationAnt.success("提交成功", "数据已保存到服务器");
                NotificationAnt.error("操作失败", "网络异常，请稍后重试");
                NotificationAnt.warning("权限不足", "请联系管理员开通相应权限");
                NotificationAnt.info("新消息", "你有 3 条未读邮件");
                """;
        return Demos.sectionWithCode("1. 4 种类型",
                "success / error / warning / info —— 静态便捷方法，title + description 两段式。",
                code, row);
    }

    private Node placementsSection() {
        Node row = Demos.row(
                ButtonAnt.create("左上 TOP_LEFT")
                        .onClick(e -> NotificationAnt.create()
                                .title("左上角").description("placement(TOP_LEFT)")
                                .placement(NotificationAnt.Placement.TOP_LEFT)
                                .build().show()).build(),
                ButtonAnt.create("右上 TOP_RIGHT（默认）")
                        .onClick(e -> NotificationAnt.create()
                                .title("右上角").description("placement(TOP_RIGHT)")
                                .placement(NotificationAnt.Placement.TOP_RIGHT)
                                .build().show()).build(),
                ButtonAnt.create("左下 BOTTOM_LEFT")
                        .onClick(e -> NotificationAnt.create()
                                .title("左下角").description("placement(BOTTOM_LEFT)")
                                .placement(NotificationAnt.Placement.BOTTOM_LEFT)
                                .build().show()).build(),
                ButtonAnt.create("右下 BOTTOM_RIGHT")
                        .onClick(e -> NotificationAnt.create()
                                .title("右下角").description("placement(BOTTOM_RIGHT)")
                                .placement(NotificationAnt.Placement.BOTTOM_RIGHT)
                                .build().show()).build()
        );
        String code = """
                NotificationAnt.create()
                        .title("通知标题")
                        .description("通知描述内容")
                        .placement(NotificationAnt.Placement.TOP_RIGHT)  // 默认右上角
                        .build()
                        .show();

                // 可选位置：TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT
                """;
        return Demos.sectionWithCode("2. 4 个角落位置",
                "TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT —— 默认右上角。",
                code, row);
    }
}
