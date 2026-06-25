package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.MenuBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * MenuBar 系统菜单栏 —— 基础菜单 / 快捷键 / 禁用 / 子菜单。
 */
public class MenuBarExamplePage extends VBoxAnt {

    public MenuBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("MenuBar 系统菜单栏")
                .description("窗口顶部系统菜单栏（File / Edit / View / Help），支持图标、快捷键、子菜单、分隔线。")
                .sections(
                        basicSection(),
                        acceleratorSection(),
                        subMenuSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = MenuBarAnt.create()
                .menu("File")
                    .item("New", () -> MessageAnt.info("已点击菜单项: New"))
                    .item("Open", () -> MessageAnt.info("已点击菜单项: Open"))
                    .divider()
                    .item("Save", () -> MessageAnt.info("已点击菜单项: Save"))
                    .item("Save As...", () -> MessageAnt.info("已点击菜单项: Save As..."))
                    .divider()
                    .item("Exit", () -> MessageAnt.info("已点击菜单项: Exit"))
                    .endMenu()
                .menu("Edit")
                    .item("Undo", () -> MessageAnt.info("已点击菜单项: Undo"))
                    .item("Redo", () -> MessageAnt.info("已点击菜单项: Redo"))
                    .divider()
                    .item("Cut", () -> MessageAnt.info("已点击菜单项: Cut"))
                    .item("Copy", () -> MessageAnt.info("已点击菜单项: Copy"))
                    .item("Paste", () -> MessageAnt.info("已点击菜单项: Paste"))
                    .endMenu()
                .menu("Help")
                    .item("About", () -> MessageAnt.info("已点击菜单项: About"))
                    .endMenu();
        String code = """
                MenuBarAnt.create()
                    .menu("File")
                        .item("New", () -> ...)
                        .item("Open", () -> ...)
                        .divider()
                        .item("Save", () -> ...)
                        .item("Exit", () -> ...)
                        .endMenu()
                    .menu("Edit")
                        .item("Undo", () -> ...)
                        .item("Redo", () -> ...)
                        .divider()
                        .item("Cut", () -> ...)
                        .item("Copy", () -> ...)
                        .item("Paste", () -> ...)
                        .endMenu()
                    .menu("Help")
                        .item("About", () -> ...)
                        .endMenu()
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "menu(title)...endMenu() 定义顶级菜单；item(label, action) 定义菜单项；divider() 插入分隔线。",
                code, demo);
    }

    private Node acceleratorSection() {
        Node demo = MenuBarAnt.create()
                .menu("File")
                    .item("New File", TypographyAnt.text("📄").build(), () -> MessageAnt.info("已点击菜单项: New File"))
                    .accelerator("Ctrl+N")
                    .item("Open File", TypographyAnt.text("📂").build(), () -> MessageAnt.info("已点击菜单项: Open File"))
                    .accelerator("Ctrl+O")
                    .divider()
                    .item("Save", () -> MessageAnt.info("已点击菜单项: Save"))
                    .accelerator("Ctrl+S")
                    .item("Save All", () -> MessageAnt.info("已点击菜单项: Save All"))
                    .accelerator("Ctrl+Shift+S")
                    .divider()
                    .item("Close", () -> MessageAnt.info("已点击菜单项: Close"))
                    .accelerator("Ctrl+W")
                    .endMenu()
                .menu("Edit")
                    .item("Undo", () -> MessageAnt.info("已点击菜单项: Undo"))
                    .accelerator("Ctrl+Z")
                    .item("Redo", () -> MessageAnt.info("已点击菜单项: Redo"))
                    .accelerator("Ctrl+Shift+Z")
                    .endMenu();
        String code = """
                MenuBarAnt.create()
                    .menu("File")
                        .item("New File", icon, () -> ...)
                        .accelerator("Ctrl+N")
                        .item("Save", () -> ...)
                        .accelerator("Ctrl+S")
                        .endMenu()
                    .menu("Edit")
                        .item("Undo", () -> ...)
                        .accelerator("Ctrl+Z")
                        .endMenu()
                    .build();
                """;
        return Demos.sectionWithCode("2. 带图标与快捷键",
                "item(label, icon, action) 带图标；accelerator() 设置快捷键（菜单项右侧显示快捷键文本）。",
                code, demo);
    }

    private Node subMenuSection() {
        Node demo = MenuBarAnt.create()
                .menu("View")
                    .item("Toolbar", () -> MessageAnt.info("已点击菜单项: Toolbar"))
                    .item("Status Bar", () -> MessageAnt.info("已点击菜单项: Status Bar"))
                    .divider()
                    .subMenu("Zoom")
                        .item("Zoom In", () -> MessageAnt.info("已点击菜单项: Zoom In"))
                        .item("Zoom Out", () -> MessageAnt.info("已点击菜单项: Zoom Out"))
                        .item("Reset Zoom", () -> MessageAnt.info("已点击菜单项: Reset Zoom"))
                        .endSubMenu()
                    .divider()
                    .item("Full Screen", () -> MessageAnt.info("已点击菜单项: Full Screen"))
                    .endMenu()
                .menu("Window")
                    .subMenu("Layout")
                        .item("Default", () -> MessageAnt.info("已点击菜单项: Default Layout"))
                        .item("Compact", () -> MessageAnt.info("已点击菜单项: Compact Layout"))
                        .divider()
                        .item("Reset", () -> MessageAnt.info("已点击菜单项: Reset Layout"))
                        .endSubMenu()
                    .item("Next Tab", () -> MessageAnt.info("已点击菜单项: Next Tab"))
                    .item("Previous Tab", () -> MessageAnt.info("已点击菜单项: Previous Tab"))
                    .endMenu();
        String code = """
                MenuBarAnt.create()
                    .menu("View")
                        .subMenu("Zoom")
                            .item("Zoom In", () -> ...)
                            .item("Zoom Out", () -> ...)
                            .item("Reset Zoom", () -> ...)
                            .endSubMenu()
                        .endMenu()
                    .build();
                """;
        return Demos.sectionWithCode("3. 子菜单",
                "subMenu(label)...endSubMenu() 创建嵌套子菜单；支持在子菜单中使用 item()、divider()、disabled() 等。",
                code, demo);
    }
}
