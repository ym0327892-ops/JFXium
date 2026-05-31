package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import org.openkawu.jfxium.component.MenuAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Menu 菜单 —— 内联 / 水平 / 暗色主题。
 */
public class MenuExamplePage extends VBoxAnt {

    public MenuExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Menu 菜单")
                .description("导航菜单组件，支持内联展开、水平排列、暗色主题。")
                .sections(
                        inlineSection(),
                        horizontalSection(),
                        darkSection()
                )
                .padding(24)
                .build());
    }

    private Node inlineSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .item("home", "首页", () -> {})
                .subMenu("sub1", "导航一")
                    .item("item1", "选项 1", () -> {})
                    .item("item2", "选项 2", () -> {})
                .endSubMenu()
                .subMenu("sub2", "导航二")
                    .item("item3", "选项 3", () -> {})
                    .item("item4", "选项 4", () -> {})
                .endSubMenu()
                .selectedKey("home")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.INLINE)
                        .item("home", "首页", () -> {})
                        .subMenu("sub1", "导航一")
                            .item("item1", "选项 1", () -> {})
                            .item("item2", "选项 2", () -> {})
                        .endSubMenu()
                        .selectedKey("home")
                        .build();
                """;
        return Demos.sectionWithCode("1. 内联模式（INLINE）",
                "子菜单在菜单内展开，适合侧边栏导航。",
                code, demo);
    }

    private Node horizontalSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.HORIZONTAL)
                .item("mail", "邮件", () -> {})
                .item("app", "应用", () -> {})
                .item("setting", "设置", () -> {})
                .selectedKey("mail")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.HORIZONTAL)
                        .item("mail", "邮件", () -> {})
                        .item("app", "应用", () -> {})
                        .item("setting", "设置", () -> {})
                        .selectedKey("mail")
                        .build();
                """;
        return Demos.sectionWithCode("2. 水平模式（HORIZONTAL）",
                "菜单项水平排列，适合顶部导航栏。",
                code, demo);
    }

    private Node darkSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .theme(MenuAnt.Theme.DARK)
                .item("dashboard", "仪表盘", () -> {})
                .item("users", "用户管理", () -> {})
                .item("settings", "系统设置", () -> {})
                .selectedKey("dashboard")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.INLINE)
                        .theme(MenuAnt.Theme.DARK)
                        .item("dashboard", "仪表盘", () -> {})
                        .item("users", "用户管理", () -> {})
                        .selectedKey("dashboard")
                        .build();
                """;
        return Demos.sectionWithCode("3. 暗色主题",
                "theme(DARK) 暗色背景菜单，适合深色侧边栏。",
                code, demo);
    }
}
