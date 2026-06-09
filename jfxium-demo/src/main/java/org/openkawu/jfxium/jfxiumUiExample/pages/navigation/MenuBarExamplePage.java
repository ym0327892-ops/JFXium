package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.MenuBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;

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
                    .item("New", () -> System.out.println("New"))
                    .item("Open", () -> System.out.println("Open"))
                    .divider()
                    .item("Save", () -> System.out.println("Save"))
                    .item("Save As...", () -> System.out.println("Save As"))
                    .divider()
                    .item("Exit", () -> System.out.println("Exit"))
                    .endMenu()
                .menu("Edit")
                    .item("Undo", () -> System.out.println("Undo"))
                    .item("Redo", () -> System.out.println("Redo"))
                    .divider()
                    .item("Cut", () -> System.out.println("Cut"))
                    .item("Copy", () -> System.out.println("Copy"))
                    .item("Paste", () -> System.out.println("Paste"))
                    .endMenu()
                .menu("Help")
                    .item("About", () -> System.out.println("About"))
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
                    .item("New File", TypographyAnt.text("📄").build(), () -> System.out.println("New File"))
                    .accelerator("Ctrl+N")
                    .item("Open File", TypographyAnt.text("📂").build(), () -> System.out.println("Open"))
                    .accelerator("Ctrl+O")
                    .divider()
                    .item("Save", () -> System.out.println("Save"))
                    .accelerator("Ctrl+S")
                    .item("Save All", () -> System.out.println("Save All"))
                    .accelerator("Ctrl+Shift+S")
                    .divider()
                    .item("Close", () -> System.out.println("Close"))
                    .accelerator("Ctrl+W")
                    .endMenu()
                .menu("Edit")
                    .item("Undo", () -> System.out.println("Undo"))
                    .accelerator("Ctrl+Z")
                    .item("Redo", () -> System.out.println("Redo"))
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
                    .item("Toolbar", () -> System.out.println("Toggle Toolbar"))
                    .item("Status Bar", () -> System.out.println("Toggle Status Bar"))
                    .divider()
                    .subMenu("Zoom")
                        .item("Zoom In", () -> System.out.println("Zoom In"))
                        .item("Zoom Out", () -> System.out.println("Zoom Out"))
                        .item("Reset Zoom", () -> System.out.println("Reset"))
                        .endSubMenu()
                    .divider()
                    .item("Full Screen", () -> System.out.println("Full Screen"))
                    .endMenu()
                .menu("Window")
                    .subMenu("Layout")
                        .item("Default", () -> System.out.println("Default Layout"))
                        .item("Compact", () -> System.out.println("Compact Layout"))
                        .divider()
                        .item("Reset", () -> System.out.println("Reset Layout"))
                        .endSubMenu()
                    .item("Next Tab", () -> System.out.println("Next Tab"))
                    .item("Previous Tab", () -> System.out.println("Previous Tab"))
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
