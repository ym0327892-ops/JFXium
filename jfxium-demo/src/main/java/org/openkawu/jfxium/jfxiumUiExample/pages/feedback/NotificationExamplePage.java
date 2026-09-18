package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import org.openkawu.jfxium.component.control.HyperlinkAnt;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.NotificationAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Notification 通知提醒 —— 4 种类型 + 4 个角落位置 + 自定义时长 + X 按钮 + 回调事件。
 *
 * <p>跟 Message 的边界：Message 短而轻、自动消失；Notification 重而显眼、带标题描述、可手动关闭。
 * M19.51 修复后 NotificationAnt 默认渲染 X 按钮，可强制要求手动关闭。</p>
 */
public class NotificationExamplePage extends VBoxAnt {

    public NotificationExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Notification 通知提醒")
                .description("带标题 + 描述的通知卡片,固定在屏幕角落,可手动关闭。")
                .sections(
                        typesSection(),
                        placementsSection(),
                        durationSection(),
                        closableSection(),
                        eventsSection()
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

    /**
     * 3. 自定义持续时长 —— duration(秒),0 表示不自动消失,需配合 closable(true) 用 X 关闭。
     */
    private Node durationSection() {
        Node row = Demos.row(
                ButtonAnt.create("1 秒")
                        .onClick(e -> NotificationAnt.create()
                                .title("短促提示").description("duration(1)")
                                .duration(1).build().show()).build(),
                ButtonAnt.create("默认 4 秒")
                        .onClick(e -> NotificationAnt.info("普通提示", "duration 默认 4 秒")).build(),
                ButtonAnt.create("8 秒")
                        .onClick(e -> NotificationAnt.create()
                                .title("稍长提示").description("duration(8)")
                                .duration(8).build().show()).build(),
                ButtonAnt.create("duration(0) + closable").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> NotificationAnt.create()
                                .title("常驻通知").description("duration(0) 不自动消失,需点 X")
                                .duration(0)
                                .closable(true)        // M19.51 修复后默认就是 true,显式写出来做回归说明
                                .build().show()).build()
        );
        String code = """
                // 自定义持续时长(单位:秒);0 表示不自动消失,需手动点 X
                NotificationAnt.create()
                        .title("短促提示")
                        .description("duration(1)")
                        .duration(1)
                        .build()
                        .show();

                // 静态便捷重载 —— success / error / warning / info 都接受 durationSeconds
                NotificationAnt.success("提交成功", "数据已保存", 8);
                """;
        return Demos.sectionWithCode("3. 自定义时长",
                "duration(秒) —— 0 表示常驻不消失,需配合 closable 手动关闭。",
                code, row);
    }

    /**
     * 4. X 按钮 (closable) —— M19.51 修复点。
     * 修复前 X 按钮在 onClose=null 时永不渲染,修复后仅由 closable 单条件控制。
     */
    private Node closableSection() {
        Node row = Demos.row(
                ButtonAnt.create("可关闭(X 按钮)").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> NotificationAnt.create()
                                .title("可关闭")
                                .description("default closable=true,显示 X 按钮,可点 X 或等 4 秒自动消失")
                                .build().show()).build(),
                ButtonAnt.create("不可关闭").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> NotificationAnt.create()
                                .title("不可关闭")
                                .description("closable(false),X 按钮不渲染,4 秒后自动消失")
                                .closable(false)
                                .build().show()).build(),
                ButtonAnt.create("强制手动关闭(M19.51)").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> NotificationAnt.create()
                                .title("强制手动关闭")
                                .description("duration(0)+closable(true)= 必点 X 关闭,X 必定渲染")
                                .duration(0)
                                .closable(true)
                                .build().show()).build()
        );
        String code = """
                // 默认 closable=true —— X 按钮一定会渲染(M19.51 修复保证)
                NotificationAnt.create()
                        .title("可关闭")
                        .description("默认值 closable=true,显示 X 按钮")
                        .build()
                        .show();

                // closable(false) —— X 按钮不渲染,只能等自动消失
                NotificationAnt.create()
                        .title("不可关闭")
                        .description("closable(false)")
                        .closable(false)
                        .build()
                        .show();

                // duration(0) + closable(true) —— 通知永驻,只能点 X 关闭
                NotificationAnt.create()
                        .title("强制手动关闭")
                        .description("duration(0) + closable(true) 必须点 X")
                        .duration(0)
                        .build()
                        .show();
                """;
        return Demos.sectionWithCode("4. X 按钮与必点关闭",
                "closable(true) 渲染 X(M19.51);duration(0)+closable=true 实现『必须手动关闭』语义。",
                code, row);
    }

    /**
     * 5. 回调事件 —— onClick(点 body 触发,不关闭) + onClose(点 X 触发,关闭)。
     * 修复 Bug 3 后:点 body 仅触发 onClick 不再关闭;X 按钮与 duration 独立处理关闭。
     */
    private Node eventsSection() {
        Node row = Demos.row(
                ButtonAnt.create("onClick(点 body,不关闭)").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> NotificationAnt.create()
                                .title("onClick 演示")
                                .description("点击通知 body 触发 onClick —— 通知保持不消失")
                                .duration(0)              // 配合 duration(0) 看得更清楚
                                .closable(true)
                                .onClick(v -> NotificationAnt.info("被点击", "onClick 回调触发"))
                                .build().show()).build(),
                ButtonAnt.create("onClose(点 X,关闭)").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> NotificationAnt.create()
                                .title("onClose 演示")
                                .description("点击 X 触发 onClose + 关闭(NotificationCard 内部处理)")
                                .duration(0)
                                .closable(true)
                                .onClose(v -> NotificationAnt.success("已关闭", "onClose 回调触发"))
                                .build().show()).build(),
                ButtonAnt.create("content(Hyperlink)").type(ButtonAnt.Type.DEFAULT)
                        .onClick(e -> NotificationAnt.create()
                                .title("内容自定义")
                                .description("content 接受任意 Node —— 点 Hyperlink 不再被 box click 截胡")
                                .content(HyperlinkAnt.create("查看更新内容 →"))
                                .duration(0)
                                .closable(true)
                                .build().show()).build()
        );
        String code = """
                // onClick: 点击通知 body 触发(但不关闭 —— 修复 Bug 3 后的新约定)
                NotificationAnt.create()
                        .title("onClick 演示")
                        .description("点击 body 仅触发 onClick,不关闭")
                        .duration(0)                       // 配合 duration(0) 让行为可观察
                        .closable(true)
                        .onClick(v -> doSomething())
                        .build()
                        .show();

                // onClose: 点击 X 触发(NotificationCard 内部处理关闭 + 回调)
                NotificationAnt.create()
                        .title("onClose 演示")
                        .duration(0)
                        .closable(true)
                        .onClose(v -> System.out.println("通知被关闭"))
                        .build()
                        .show();

                // content 接受任意 Node —— Hyperlink / Button / 自定义组件均可正常点击
                // (修复 Bug 3 前:点 Hyperlink 会被 box click 截胡关掉通知)
                NotificationAnt.create()
                        .title("内容自定义")
                        .content(HyperlinkAnt.create("查看更新内容 →"))
                        .duration(0)
                        .closable(true)
                        .build()
                        .show();
                """;
        return Demos.sectionWithCode("5. 回调与自定义内容",
                "onClick 触发于 body 点击但不关闭;onClose 触发于 X 点击且关闭;content 接受任意 Node。",
                code, row);
    }
}
