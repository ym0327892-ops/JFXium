package org.openkawu.jfxium.jfxiumUiExample.pages;

import javafx.scene.Node;
import javafx.scene.layout.VBox;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.DashboardTemplate;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.control.IconAnt;

/**
 * 首页 —— 欢迎 + 4 张统计卡概览。
 *
 * <p>用 {@link DashboardTemplate} 演示"统计卡片 + 多区域"模板的标准用法，
 * 并提示用户从左侧菜单进入各组件示例。</p>
 */
public class HomePage extends VBoxAnt {

    public HomePage() {
        Node intro = TypographyAnt.paragraph(
                "欢迎使用 JFXium UI Example 示例项目。\n" +
                "左侧菜单按【容器示例】和【控件示例】两大类组织，每个示例页都是可直接拷贝改用的最小用例。"
        ).build();

        Node hint = Demos.section("快速使用提示",
                "顶栏右上角可以切换 亮 / 暗 主题、紧凑 / 默认密度。所有示例页面都会跟随主题变化。",
                TypographyAnt.text("➜ 推荐先看 容器示例 → Card 卡片 / Tabs 标签页，再看 控件示例 → Button / Input。")
                        .type(TypographyAnt.Type.SECONDARY).build()
        );

        VBox dashboard = DashboardTemplate.create()
                .stat(IconAnt.Path.USERS,    "用户总数",   "1,234", "↑ 12.5%", true)
                .stat(IconAnt.Path.FILE,     "今日订单",   "89",    "↓ 3.2%",  false)
                .stat(IconAnt.Path.CHART,    "月销售额",   "¥125k", "↑ 8.4%",  true)
                .stat(IconAnt.Path.DASHBOARD,"转化率",     "23.4%", "↑ 1.2%",  true)
                .build();

        VBox page = PageTemplate.create()
                .title("欢迎回来")
                .description("JFXium UI 库示例集 —— 容器、控件，一项项体验")
                .body(VBoxAnt.create().spacing(20).children(intro, hint, dashboard))
                .padding(24)
                .build();

        spacing(0).children(page);
    }
}
