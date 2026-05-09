package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Menu Component
 * Inspired by Ant Design Menu
 *
 * Usage:
 * <pre>{@code
 * MenuBar menuBar = JFXMenu.create()
 *     .menu("File",
 *         JFXMenu.item("New", e -> System.out.println("New")),
 *         JFXMenu.item("Open", e -> System.out.println("Open")),
 *         JFXMenu.separator(),
 *         JFXMenu.item("Exit", e -> System.exit(0))
 *     )
 *     .menu("Edit",
 *         JFXMenu.item("Cut", e -> System.out.println("Cut")),
 *         JFXMenu.item("Copy", e -> System.out.println("Copy")),
 *         JFXMenu.item("Paste", e -> System.out.println("Paste"))
 *     )
 *     .build();
 * }</pre>
 */
public class JFXMenu {

    public static Builder create() {
        return new Builder();
    }

    /**
     * 创建菜单项
     */
    public static MenuItem item(String text, Consumer<javafx.event.ActionEvent> action) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(e -> action.accept(e));
        return item;
    }

    /**
     * 创建带图标的菜单项
     */
    public static MenuItem item(String text, Node graphic, Consumer<javafx.event.ActionEvent> action) {
        MenuItem item = new MenuItem(text, graphic);
        item.setOnAction(e -> action.accept(e));
        return item;
    }

    /**
     * 创建带快捷键的菜单项
     */
    public static MenuItem item(String text, KeyCombination accelerator, Consumer<javafx.event.ActionEvent> action) {
        MenuItem item = new MenuItem(text);
        item.setAccelerator(accelerator);
        item.setOnAction(e -> action.accept(e));
        return item;
    }

    /**
     * 创建分隔线
     */
    public static SeparatorMenuItem separator() {
        return new SeparatorMenuItem();
    }

    /**
     * 创建子菜单
     */
    public static Menu subMenu(String text, MenuItem... items) {
        Menu menu = new Menu(text);
        menu.getItems().addAll(items);
        return menu;
    }

    public static class Builder {
        private final List<Menu> menus = new ArrayList<>();

        private Builder() {}

        /**
         * 添加菜单
         */
        public Builder menu(String text, MenuItem... items) {
            Menu menu = new Menu(text);
            menu.getItems().addAll(items);
            menus.add(menu);
            return this;
        }

        /**
         * 添加子菜单
         */
        public Builder menu(Menu menu) {
            menus.add(menu);
            return this;
        }

        public MenuBar build() {
            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().addAll(menus);
            return menuBar;
        }
    }
}