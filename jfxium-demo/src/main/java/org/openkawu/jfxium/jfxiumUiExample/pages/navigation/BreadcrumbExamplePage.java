package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.BreadcrumbAnt;

/**
 * Breadcrumb 面包屑 —— 基础 / 分隔符 / 可点击。
 */
public class BreadcrumbExamplePage extends VBoxAnt {

    public BreadcrumbExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Breadcrumb 面包屑")
                .description("显示当前页面在层级结构中的位置，支持自定义分隔符和点击导航。")
                .sections(
                        basicSection(),
                        separatorSection(),
                        clickableSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = BreadcrumbAnt.create()
                .item("首页")
                .item("列表页")
                .item("详情页")
                .build();
        String code = """
                BreadcrumbAnt.create()
                        .item("首页")
                        .item("列表页")
                        .item("详情页")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "最简单的面包屑，最后一项为当前页。",
                code, demo);
    }

    private Node separatorSection() {
        Node demo = Demos.column(
                BreadcrumbAnt.create()
                        .separator("/")
                        .item("首页")
                        .item("应用中心")
                        .item("应用详情")
                        .build(),
                BreadcrumbAnt.create()
                        .separator(">")
                        .item("Home")
                        .item("Category")
                        .item("Detail")
                        .build()
        );
        String code = """
                BreadcrumbAnt.create()
                        .separator("/")
                        .item("首页").item("应用中心").item("应用详情")
                        .build();
                BreadcrumbAnt.create()
                        .separator(">")
                        .item("Home").item("Category").item("Detail")
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义分隔符",
                "separator(str) 自定义层级之间的分隔符号。",
                code, demo);
    }

    private Node clickableSection() {
        Node demo = BreadcrumbAnt.create()
                .item("首页", item -> {})
                .item("用户管理", item -> {})
                .item("用户详情")
                .build();
        String code = """
                BreadcrumbAnt.create()
                        .item("首页", item -> navigate("home"))
                        .item("用户管理", item -> navigate("users"))
                        .item("用户详情")   // 当前页不可点击
                        .build();
                """;
        return Demos.sectionWithCode("3. 可点击导航",
                "带回调的 item 可点击跳转；最后一项通常不设回调表示当前页。",
                code, demo);
    }
}
