package org.openkawu.jfxium.core.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * MenuItem 工厂方法集中管理（P1-S4 抽取）。
 *
 * <p>消除 {@link org.openkawu.jfxium.component.control.MenuButtonAnt}、
 * {@link org.openkawu.jfxium.component.control.SplitButtonAnt}、
 * {@link org.openkawu.jfxium.component.control.AbstractMenuButtonAntBuilder}
 * 三处 100% 重复的 {@code newItem/newItemWithIcon/newDisabledItem} 方法。</p>
 *
 * <h2>使用模式</h2>
 * <pre>{@code
 * getItems().add(MenuItemFactory.item("保存", e -> save()));
 * getItems().add(MenuItemFactory.item("导出", icon, e -> export()));
 * getItems().add(MenuItemFactory.itemDisabled("已禁用"));
 * getItems().add(MenuItemFactory.separator());
 * }</pre>
 */
public final class MenuItemFactory {

    private MenuItemFactory() {
        // 工具类不允许实例化
    }

    /** 创建普通菜单项。onClick 为 null 时不绑定回调。 */
    public static MenuItem item(String label, EventHandler<ActionEvent> onClick) {
        MenuItem mi = new MenuItem(TextUtils.safeText(label));
        if (onClick != null) {
            mi.setOnAction(onClick);
        }
        return mi;
    }

    /** 创建带图标的菜单项。 */
    public static MenuItem item(String label, Node icon, EventHandler<ActionEvent> onClick) {
        MenuItem mi = new MenuItem(TextUtils.safeText(label), icon);
        if (onClick != null) {
            mi.setOnAction(onClick);
        }
        return mi;
    }

    /** 创建禁用菜单项。 */
    public static MenuItem itemDisabled(String label) {
        MenuItem mi = new MenuItem(TextUtils.safeText(label));
        mi.setDisable(true);
        return mi;
    }

    /** 创建分隔线。 */
    public static SeparatorMenuItem separator() {
        return new SeparatorMenuItem();
    }
}
