package org.openkawu.jfxium.jfxiumUiExample.util;

import javafx.scene.control.MenuItem;

/**
 * MenuItem 静态工厂 —— demo 临时 util,等 JFXium 主体补 MenuItemAnt 后整合。
 *
 * <p><b>孵化原因</b>:JFXium 当前没有 {@code MenuItemAnt} 封装,而
 * {@code SplitMenuButtonAnt.items(MenuItem...)} / {@code MenuButtonAnt.items(MenuItem...)} /
 * {@code ContextMenuAnt.items(MenuItem...)} 等 API 直接接收原生 {@link MenuItem}。
 * 此 util 提供静态工厂,避免 demo 中直接 {@code new MenuItem(...)} 散落各处,
 * 为未来统一替换为 {@code MenuItemAnt.create(...)} 留单一切入点。</p>
 *
 * <p><b>整合评估</b>:</p>
 * <ul>
 *   <li><b>通用性</b>: 5 个 Menu 组件(SplitMenuButton / MenuButton / MenuBar / ContextMenu / MenuAnt)都涉及 MenuItem → 高</li>
 *   <li><b>使用频率</b>: 仅 {@code SplitMenuButtonExamplePage} 一处使用,8 处业务代码 → 中</li>
 *   <li><b>功能完整性</b>: 当前只覆盖最朴素的 {@code text} / {@code onAction} → 后续可补 styleClass 主题化</li>
 *   <li><b>建议</b>: 在 jfxium 主体新建 {@code MenuItemAnt}(继承 MenuItem,双工厂模式),
 *       5 个 Menu 组件的 {@code items()} 签名同步切到 {@code MenuItemAnt},本 util 即可删除</li>
 * </ul>
 */
public final class MenuItemUtil {

    private MenuItemUtil() {}

    /**
     * 创建纯文本菜单项。
     *
     * @param text 菜单项文本(null-safe,自动转空串)
     */
    public static MenuItem item(String text) {
        return new MenuItem(text == null ? "" : text);
    }

    /**
     * 创建带点击回调的菜单项。
     *
     * @param text     菜单项文本(null-safe)
     * @param onAction 点击回调(null-safe —— 传 null 不绑定事件)
     */
    public static MenuItem item(String text, Runnable onAction) {
        MenuItem mi = item(text);
        if (onAction != null) {
            mi.setOnAction(e -> onAction.run());
        }
        return mi;
    }
}
