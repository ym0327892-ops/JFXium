package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.MenuButtonAnt;

/**
 * MenuButton 菜单按钮 —— 基础菜单 / 箭头样式。
 */
public class MenuButtonExamplePage extends VBoxAnt {

    public MenuButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("MenuButton 菜单按钮")
                .description("外观像普通按钮，点击后弹出下拉菜单，用于在一组动作中选择。")
                .sections(basicSection(), arrowSection(), clickedSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = MenuButtonAnt.create("批量操作")
                .item("导出", e -> System.out.println("导出"))
                .item("删除", e -> System.out.println("删除"))
                .separator()
                .item("移动到...", e -> System.out.println("移动"))
                .build();
        String code = """
                MenuButtonAnt.create("批量操作")
                        .item("导出", e -> exportSelected())
                        .item("删除", e -> deleteSelected())
                        .separator()
                        .item("移动到...", e -> moveSelected())
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础菜单",
                "item 链式添加菜单项，separator 添加分隔线。", code, demo);
    }

    private Node arrowSection() {
        Node demo = Demos.row(
                MenuButtonAnt.create("Chevron")
                        .item("选项一", e -> {}).item("选项二", e -> {}).build(),
                MenuButtonAnt.create("Triangle")
                        .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                        .item("选项一", e -> {}).item("选项二", e -> {}).build(),
                MenuButtonAnt.create("无箭头")
                        .noArrow()
                        .item("选项一", e -> {}).item("选项二", e -> {}).build()
        );
        String code = """
                MenuButtonAnt.create("Chevron").item("选项一", e -> {}).build();
                MenuButtonAnt.create("Triangle")
                        .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                        .item("选项一", e -> {}).build();
                MenuButtonAnt.create("无箭头").noArrow()
                        .item("选项一", e -> {}).build();
                """;
        return Demos.sectionWithCode("2. 箭头样式",
                "arrowStyle 切换 CHEVRON / TRIANGLE，noArrow 隐藏箭头。", code, demo);
    }

    /**
     * 3. 显示点击的菜单项 —— 每个菜单项的 EventHandler 里更新结果 Label。
     *
     * <p>MenuButtonAnt 走的是 per-item EventHandler 模式（每项一个回调），
     * 而非统一的 onSelect(key)。在各项 handler 里把对应文案写进结果 Label，
     * 即可显示「用户刚点了哪一项」。</p>
     */
    private Node clickedSection() {
        Label result = new Label("点击的菜单项：(未点击)");
        Node menu = MenuButtonAnt.create("批量操作")
                .item("导出", e -> result.setText("点击的菜单项：导出"))
                .item("删除", e -> result.setText("点击的菜单项：删除"))
                .separator()
                .item("移动到...", e -> result.setText("点击的菜单项：移动到..."))
                .build();
        Node demo = Demos.column(menu, result);
        String code = """
                Label result = new Label("点击的菜单项：(未点击)");
                MenuButtonAnt.create("批量操作")
                        .item("导出", e -> result.setText("点击的菜单项：导出"))
                        .item("删除", e -> result.setText("点击的菜单项：删除"))
                        .separator()
                        .item("移动到...", e -> result.setText("点击的菜单项：移动到..."))
                        .build();
                """;
        return Demos.sectionWithCode("3. 显示点击的菜单项",
                "每个 item(label, handler) 的 handler 里更新结果 Label，即可显示当前点击项。",
                code, demo);
    }
}
