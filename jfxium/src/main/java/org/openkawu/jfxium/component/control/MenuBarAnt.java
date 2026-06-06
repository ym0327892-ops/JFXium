package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 系统菜单栏组件 - 对标 Ant Design 无直接对应，补齐 PC 桌面软件刚需。
 *
 * <p><b>定位</b>：窗口顶部系统菜单栏（File / Edit / View / Help...），
 * 支持图标、快捷键提示、分组分隔线。与 {@link org.openkawu.jfxium.component.composite.MenuAnt MenuAnt}
 *（侧边导航）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>菜单组</b>：menu(title) 创建顶级菜单，支持嵌套子菜单</li>
 *   <li><b>菜单项</b>：item(label, action) 普通项 / item(label, icon, action) 带图标</li>
 *   <li><b>快捷键</b>：accelerator(KeyCombination) 绑定键盘快捷键（如 Ctrl+S）</li>
 *   <li><b>分隔线</b>：divider() 插入分组分隔线</li>
 *   <li><b>禁用</b>：disabled(true) 禁用整个菜单栏 / 单项禁用</li>
 *   <li><b>视觉</b>：走 {@link CssClasses#MENU_BAR} 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础菜单栏
 * MenuBarAnt menuBar = MenuBarAnt.create()
 *     .menu("File")
 *         .item("New", () -> newFile())
 *         .item("Open", () -> openFile())
 *         .divider()
 *         .item("Save", saveIcon, () -> saveFile())
 *         .accelerator(KeyCombination.keyCombination("Ctrl+S"))
 *         .divider()
 *         .item("Exit", () -> exitApp())
 *         .endMenu()
 *     .menu("Edit")
 *         .item("Undo", () -> undo())
 *         .item("Redo", () -> redo())
 *         .endMenu()
 *     .menu("Help")
 *         .item("About", () -> showAbout())
 *         .endMenu()
 *     .build();
 *
 * // 业务继承用法
 * public class AppMenuBar extends MenuBarAnt {
 *     public AppMenuBar() {
 *         menu("File")
 *             .item("New", this::onNew)
 *             .item("Open", this::onOpen)
 *             .endMenu();
 *     }
 * }
 * }</pre>
 *
 * @see org.openkawu.jfxium.component.composite.MenuAnt 侧边导航菜单（非系统菜单栏）
 */
public class MenuBarAnt extends MenuBar {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static MenuBarAnt create() {
        return new MenuBarAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public MenuBarAnt() {
        super();
        getStyleClass().add(CssClasses.MENU_BAR);
    }

    // ============================================================
    // 菜单构建（返回 MenuBuilder 用于链式添加项）
    // ============================================================

    public MenuBuilder menu(String title) {
        MenuBuilder builder = new MenuBuilder(this, title);
        return builder;
    }

    public MenuBarAnt menu(Menu menu) {
        getMenus().add(menu);
        return this;
    }

    // ============================================================
    // 快捷方法
    // ============================================================

    /** 清空所有菜单。 */
    public MenuBarAnt clearMenus() {
        getMenus().clear();
        return this;
    }

    /** 禁用整个菜单栏。 */
    public MenuBarAnt disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    // ============================================================
    // MenuBuilder：用于构建单个 Menu 及其子项
    // ============================================================

    public static class MenuBuilder {
        private final MenuBarAnt menuBar;
        private final Menu menu;
        private MenuItem currentItem;

        MenuBuilder(MenuBarAnt menuBar, String title) {
            this.menuBar = menuBar;
            this.menu = new Menu(title);
            this.menu.getStyleClass().add(CssClasses.MENU_BAR_MENU);
        }

        // --- 菜单项 ---

        public MenuBuilder item(String label, Runnable action) {
            MenuItem item = new MenuItem(label);
            item.getStyleClass().add(CssClasses.MENU_BAR_ITEM);
            if (action != null) {
                item.setOnAction(e -> action.run());
            }
            menu.getItems().add(item);
            this.currentItem = item;
            return this;
        }

        public MenuBuilder item(String label, Node icon, Runnable action) {
            MenuItem item = new MenuItem(label, icon);
            item.getStyleClass().add(CssClasses.MENU_BAR_ITEM);
            if (action != null) {
                item.setOnAction(e -> action.run());
            }
            menu.getItems().add(item);
            this.currentItem = item;
            return this;
        }

        public MenuBuilder item(String label, Consumer<MenuItem> action) {
            MenuItem item = new MenuItem(label);
            item.getStyleClass().add(CssClasses.MENU_BAR_ITEM);
            if (action != null) {
                item.setOnAction(e -> action.accept(item));
            }
            menu.getItems().add(item);
            this.currentItem = item;
            return this;
        }

        // --- 禁用当前项 ---

        public MenuBuilder disabled(boolean disabled) {
            if (currentItem != null) {
                currentItem.setDisable(disabled);
            }
            return this;
        }

        // --- 快捷键 ---

        public MenuBuilder accelerator(KeyCombination combination) {
            if (currentItem != null) {
                currentItem.setAccelerator(combination);
            }
            return this;
        }

        public MenuBuilder accelerator(String shortcut) {
            if (currentItem != null) {
                currentItem.setAccelerator(KeyCombination.keyCombination(shortcut));
            }
            return this;
        }

        // --- 分隔线 ---

        public MenuBuilder divider() {
            SeparatorMenuItem sep = new SeparatorMenuItem();
            sep.getStyleClass().add(CssClasses.MENU_BAR_DIVIDER);
            menu.getItems().add(sep);
            return this;
        }

        // --- 子菜单 ---

        public SubMenuBuilder subMenu(String label) {
            return new SubMenuBuilder(this, label);
        }

        // --- 结束当前菜单 ---

        public MenuBarAnt endMenu() {
            menuBar.getMenus().add(menu);
            return menuBar;
        }

        // 内部访问
        Menu getMenu() {
            return menu;
        }
    }

    // ============================================================
    // SubMenuBuilder：用于构建嵌套子菜单
    // ============================================================

    public static class SubMenuBuilder {
        private final MenuBuilder parent;
        private final Menu subMenu;
        private MenuItem currentItem;

        SubMenuBuilder(MenuBuilder parent, String label) {
            this.parent = parent;
            this.subMenu = new Menu(label);
            this.subMenu.getStyleClass().add(CssClasses.MENU_BAR_SUBMENU);
        }

        public SubMenuBuilder item(String label, Runnable action) {
            MenuItem item = new MenuItem(label);
            item.getStyleClass().add(CssClasses.MENU_BAR_ITEM);
            if (action != null) {
                item.setOnAction(e -> action.run());
            }
            subMenu.getItems().add(item);
            this.currentItem = item;
            return this;
        }

        public SubMenuBuilder item(String label, Node icon, Runnable action) {
            MenuItem item = new MenuItem(label, icon);
            item.getStyleClass().add(CssClasses.MENU_BAR_ITEM);
            if (action != null) {
                item.setOnAction(e -> action.run());
            }
            subMenu.getItems().add(item);
            this.currentItem = item;
            return this;
        }

        public SubMenuBuilder disabled(boolean disabled) {
            if (currentItem != null) {
                currentItem.setDisable(disabled);
            }
            return this;
        }

        public SubMenuBuilder accelerator(KeyCombination combination) {
            if (currentItem != null) {
                currentItem.setAccelerator(combination);
            }
            return this;
        }

        public SubMenuBuilder accelerator(String shortcut) {
            if (currentItem != null) {
                currentItem.setAccelerator(KeyCombination.keyCombination(shortcut));
            }
            return this;
        }

        public SubMenuBuilder divider() {
            SeparatorMenuItem sep = new SeparatorMenuItem();
            sep.getStyleClass().add(CssClasses.MENU_BAR_DIVIDER);
            subMenu.getItems().add(sep);
            return this;
        }

        public MenuBuilder endSubMenu() {
            parent.getMenu().getItems().add(subMenu);
            return parent;
        }
    }
}
