package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.DesktopNotificationAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * DesktopNotification 桌面通知 —— 锚定操作系统屏幕（非应用窗口）的通知。
 *
 * <p>跟 {@code Notification} 的边界：Notification 贴应用窗口角落、跟随窗口移动/缩放；
 * DesktopNotification 贴屏幕角落、不跟随窗口、{@code alwaysOnTop} 盖住其它程序，
 * 主窗口最小化后仍能弹出（类似系统通知）。</p>
 *
 * <p>本页覆盖：4 种类型 / 5 个屏幕位置（含顶部居中）/ 三种关闭方式 / 脱离窗口与堆叠。</p>
 */
public class DesktopNotificationExamplePage extends VBoxAnt {

    public DesktopNotificationExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("DesktopNotification 桌面通知")
                .description("锚定操作系统屏幕角落的通知，不跟随应用窗口，主窗口最小化后仍能弹出。")
                .sections(
                        typesSection(),
                        placementsSection(),
                        closeWaysSection(),
                        persistSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 4 种类型：success / error / warning / info（默认右下角、5 秒自动消失）。 */
    private Node typesSection() {
        Node row = Demos.row(
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> DesktopNotificationAnt.success("保存成功", "数据已写入磁盘")).build(),
                ButtonAnt.create("错误").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> DesktopNotificationAnt.error("导出失败", "磁盘空间不足，请清理后重试")).build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> DesktopNotificationAnt.warning("电量偏低", "剩余 15%，请及时充电")).build(),
                ButtonAnt.create("提示")
                        .onClick(e -> DesktopNotificationAnt.info("后台任务", "索引重建已完成")).build()
        );
        String code = """
                // 静态便捷方法：title + description，默认右下角、5 秒后自动消失
                DesktopNotificationAnt.success("保存成功", "数据已写入磁盘");
                DesktopNotificationAnt.error("导出失败", "磁盘空间不足");
                DesktopNotificationAnt.warning("电量偏低", "剩余 15%");
                DesktopNotificationAnt.info("后台任务", "索引重建已完成");
                """;
        return Demos.sectionWithCode("1. 4 种类型",
                "success / error / warning / info —— 静态便捷方法，默认锚定屏幕右下角、5 秒自动消失。",
                code, row);
    }

    /** 2. 5 个屏幕位置：四角 + 顶部居中（TOP_CENTER 从上方滑入）。 */
    private Node placementsSection() {
        Node row = Demos.row(
                ButtonAnt.create("顶部居中 TOP_CENTER").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> DesktopNotificationAnt.info("顶部居中", "placement(TOP_CENTER)，从上方滑入",
                                DesktopNotificationAnt.Placement.TOP_CENTER)).build(),
                ButtonAnt.create("左上 TOP_LEFT")
                        .onClick(e -> DesktopNotificationAnt.info("屏幕左上角", "placement(TOP_LEFT)",
                                DesktopNotificationAnt.Placement.TOP_LEFT)).build(),
                ButtonAnt.create("右上 TOP_RIGHT")
                        .onClick(e -> DesktopNotificationAnt.info("屏幕右上角", "placement(TOP_RIGHT)",
                                DesktopNotificationAnt.Placement.TOP_RIGHT)).build(),
                ButtonAnt.create("左下 BOTTOM_LEFT")
                        .onClick(e -> DesktopNotificationAnt.info("屏幕左下角", "placement(BOTTOM_LEFT)",
                                DesktopNotificationAnt.Placement.BOTTOM_LEFT)).build(),
                ButtonAnt.create("右下 BOTTOM_RIGHT（默认）")
                        .onClick(e -> DesktopNotificationAnt.info("屏幕右下角", "placement(BOTTOM_RIGHT)，默认位置",
                                DesktopNotificationAnt.Placement.BOTTOM_RIGHT)).build()
        );
        String code = """
                // 5 个屏幕位置：TOP_LEFT / TOP_CENTER / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT（默认）
                DesktopNotificationAnt.info("顶部居中", "描述", DesktopNotificationAnt.Placement.TOP_CENTER);

                // Builder 形式指定位置
                DesktopNotificationAnt.create()
                        .title("屏幕左上角")
                        .description("placement(TOP_LEFT)")
                        .placement(DesktopNotificationAnt.Placement.TOP_LEFT)
                        .build()
                        .show();
                """;
        return Demos.sectionWithCode("2. 5 个屏幕位置",
                "四角 + 顶部居中（TOP_CENTER）——顶部居中从上方垂直滑入，四角横向滑入。",
                code, row);
    }

    /** 3. 三种关闭方式：X 按钮 / duration 到点自动 / closeOnClick 点击关闭。 */
    private Node closeWaysSection() {
        Node row = Demos.row(
                ButtonAnt.create("X 按钮关闭").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> DesktopNotificationAnt.create()
                                .title("点 X 关闭")
                                .description("closable(true)（默认）—— 右上角 X 按钮，点它关闭并触发 onClose")
                                .duration(0)              // 常驻，凸显 X 关闭
                                .closable(true)
                                .onClose(v -> DesktopNotificationAnt.info("已关闭", "onClose 回调触发"))
                                .build().show()).build(),
                ButtonAnt.create("时间到自动关闭")
                        .onClick(e -> DesktopNotificationAnt.create()
                                .title("3 秒后自动关闭")
                                .description("duration(3) —— 到点自动 fade-out")
                                .duration(3)
                                .build().show()).build(),
                ButtonAnt.create("点击关闭 + 常驻").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> DesktopNotificationAnt.create()
                                .title("点一下就关")
                                .description("closeOnClick(true) + duration(0)：不自动消失，点卡片任意位置即关闭")
                                .placement(DesktopNotificationAnt.Placement.TOP_CENTER)
                                .duration(0)
                                .closeOnClick(true)
                                .build().show()).build(),
                ButtonAnt.create("常驻不消失").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> DesktopNotificationAnt.create()
                                .title("时间不限")
                                .description("duration(0) —— 不自动消失，只能点 X 或点击关闭")
                                .duration(0)
                                .build().show()).build()
        );
        String code = """
                // 方式一：X 按钮关闭（closable 默认 true），可选 onClose 回调
                DesktopNotificationAnt.create()
                        .title("点 X 关闭")
                        .duration(0)                       // 常驻，凸显手动关闭
                        .closable(true)
                        .onClose(v -> log("closed"))
                        .build().show();

                // 方式二：时间到了自动关闭
                DesktopNotificationAnt.create().title("3 秒后关闭").duration(3).build().show();

                // 方式三：点击卡片本体关闭（closeOnClick），可与 duration(0) 组合成常驻
                DesktopNotificationAnt.create()
                        .title("点一下就关")
                        .duration(0)                       // 时间不限：不自动消失
                        .closeOnClick(true)                // 点卡片即关闭
                        .build().show();
                """;
        return Demos.sectionWithCode("3. 三种关闭方式",
                "X 按钮（closable）/ duration 到点自动 / closeOnClick 点击关闭；duration(0) 表示时间不限、不自动消失。",
                code, row);
    }

    /** 4. 脱离窗口与堆叠：最小化主窗口仍弹出；连点多次同角落堆叠。 */
    private Node persistSection() {
        Node row = Demos.row(
                ButtonAnt.create("最小化窗口后仍弹出").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> DesktopNotificationAnt.create()
                                .title("脱离窗口")
                                .description("先点这个按钮，再最小化主窗口 —— 通知仍锚定屏幕角落显示")
                                .duration(8)
                                .build().show()).build(),
                ButtonAnt.create("连发 3 条（堆叠演示）")
                        .onClick(e -> {
                            DesktopNotificationAnt.info("第 1 条", "同一角落的通知会自上而下堆叠");
                            DesktopNotificationAnt.success("第 2 条", "关闭其中一条，其余自动重排");
                            DesktopNotificationAnt.warning("第 3 条", "堆叠上限由布局与屏幕高度决定");
                        }).build()
        );
        String code = """
                // 桌面通知锚定屏幕、alwaysOnTop —— 主窗口最小化后仍能弹出
                DesktopNotificationAnt.create()
                        .title("脱离窗口")
                        .description("锚定屏幕角落，不随主窗口最小化而消失")
                        .duration(8)
                        .build().show();

                // 同一位置连发多条 —— 自动堆叠、关闭后自动重排
                DesktopNotificationAnt.info("第 1 条", "...");
                DesktopNotificationAnt.success("第 2 条", "...");
                """;
        return Demos.sectionWithCode("4. 脱离窗口与堆叠",
                "锚定屏幕、alwaysOnTop：主窗口最小化后仍弹出；同角落多条自动堆叠、关闭后重排。",
                code, row);
    }
}
