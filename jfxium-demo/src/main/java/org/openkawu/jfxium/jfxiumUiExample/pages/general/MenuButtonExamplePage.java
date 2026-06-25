package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.MenuButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

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
                .item("导出", e -> MessageAnt.info("已点击菜单项: 导出"))
                .item("删除", e -> MessageAnt.info("已点击菜单项: 删除"))
                .separator()
                .item("移动到...", e -> MessageAnt.info("已点击菜单项: 移动到..."))
                .build();
        String code = """
                MenuButtonAnt.create("批量操作")
                        .item("导出", e -> MessageAnt.info("已点击菜单项: 导出"))
                        .item("删除", e -> MessageAnt.info("已点击菜单项: 删除"))
                        .separator()
                        .item("移动到...", e -> MessageAnt.info("已点击菜单项: 移动到..."))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础菜单",
                "item 链式添加菜单项，separator 添加分隔线。", code, demo);
    }

    private Node arrowSection() {
        Node demo = Demos.row(
                MenuButtonAnt.create("Chevron")
                        .item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).item("选项二", e -> MessageAnt.info("已点击菜单项: 选项二")).build(),
                MenuButtonAnt.create("Triangle")
                        .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                        .item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).item("选项二", e -> MessageAnt.info("已点击菜单项: 选项二")).build(),
                MenuButtonAnt.create("无箭头")
                        .noArrow()
                        .item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).item("选项二", e -> MessageAnt.info("已点击菜单项: 选项二")).build()
        );
        String code = """
                MenuButtonAnt.create("Chevron").item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).build();
                MenuButtonAnt.create("Triangle")
                        .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                        .item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).build();
                MenuButtonAnt.create("无箭头").noArrow()
                        .item("选项一", e -> MessageAnt.info("已点击菜单项: 选项一")).build();
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
        Label result = TypographyAnt.text("点击的菜单项：(未点击)").build();
        Node menu = MenuButtonAnt.create("批量操作")
                .item("导出", e -> result.setText("点击的菜单项：导出"))
                .item("删除", e -> result.setText("点击的菜单项：删除"))
                .separator()
                .item("移动到...", e -> result.setText("点击的菜单项：移动到..."))
                .build();
        Node demo = Demos.column(menu, result);
        String code = """
                Label result = TypographyAnt.text("点击的菜单项：(未点击)").build();
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
