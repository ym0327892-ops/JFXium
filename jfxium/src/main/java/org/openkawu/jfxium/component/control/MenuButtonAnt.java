package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.MenuItemFactory;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 菜单按钮组件（M19.50 重构）— 包装 JavaFX {@link MenuButton}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：外观像普通按钮，点击后弹出下拉菜单。在一组动作中做出选择。
 * 与 {@link org.openkawu.jfxium.component.overlay.DropdownAnt DropdownAnt}
 * （trigger + Popup 弹层）和 {@link ButtonAnt}（一次性触发）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>admin 列表页"批量操作 ▾"按钮（导出 / 删除 / 移动）</li>
 *   <li>编辑器"插入 ▾"按钮（图片 / 链接 / 表格）</li>
 *   <li>标题栏"用户菜单 ▾"</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * MenuButton actions = MenuButtonAnt.create("批量操作")
 *     .item("导出", e -> exportSelected())
 *     .item("删除", e -> deleteSelected())
 *     .separator()
 *     .item("移动到...", e -> moveSelected())
 *     .build();
 *
 * MenuButton withIcon = MenuButtonAnt.create("更多")
 *     .icon(IconAnt.path(IconAnt.Path.MORE, 16))
 *     .size(Size.SMALL)
 *     .item("设置", e -> openSettings())
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class ExportMenuButton extends MenuButtonAnt {
 *     public ExportMenuButton() {
 *         text("导出");
 *         item("导出 Excel", e -> exportExcel());
 *         item("导出 PDF", e -> exportPdf());
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link MenuButton} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size/shape} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class MenuButtonAnt extends MenuButton
        implements LayoutCommon<MenuButtonAnt>, DisabledSupport<MenuButtonAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    /**
     * 箭头样式（M19.6.1）。
     * <ul>
     *   <li>{@link #CHEVRON} —— Ant Design 风格细 V 形（默认）</li>
     *   <li>{@link #TRIANGLE} —— 实心三角形（AtlantaFX 风格）</li>
     *   <li>{@link #NONE} —— 完全不显示箭头</li>
     * </ul>
     */
    public enum ArrowStyle {
        CHEVRON,
        TRIANGLE,
        NONE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空文本）。 */
    public static MenuButtonAnt create() {
        return new MenuButtonAnt("");
    }

    /** 工厂入口（带文本）。 */
    public static MenuButtonAnt create(String text) {
        return new MenuButtonAnt(TextUtils.safeText(text));
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public MenuButtonAnt() {
        super("");
        init();
    }

    public MenuButtonAnt(String text) {
        super(TextUtils.safeText(text));
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.JFX_MENU_BUTTON);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /**
     * 设置文本（链式包装 setText）。null 安全：null 视为空串。
     */
    public MenuButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。委托 {@link ApplySizeUtil#apply(javafx.scene.Node, Size)} 实现。
     */
    public MenuButtonAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /** 设置圆角（pill 形状）。 */
    public MenuButtonAnt rounded() {
        return ApplySizeUtil.applyShapeRounded(this);
    }

    /** 设置直角方形。 */
    public MenuButtonAnt square() {
        return ApplySizeUtil.applyShapeSquare(this);
    }

    /** 设置图标。 */
    public MenuButtonAnt icon(Node icon) {
        if (icon != null) {
            setGraphic(icon);
        }
        return this;
    }

    /** 设置图标位置（图标相对文本）。 */
    public MenuButtonAnt contentDisplay(ContentDisplay display) {
        if (display != null) {
            setContentDisplay(display);
        }
        return this;
    }

    /** 设置箭头样式（默认 CHEVRON）。 */
    public MenuButtonAnt arrowStyle(ArrowStyle style) {
        getStyleClass().removeAll(JfxStyles.JFX_ARROW_TRIANGLE, JfxStyles.JFX_NO_ARROW);
        if (style == ArrowStyle.TRIANGLE) {
            getStyleClass().add(JfxStyles.JFX_ARROW_TRIANGLE);
        } else if (style == ArrowStyle.NONE) {
            getStyleClass().add(JfxStyles.JFX_NO_ARROW);
        }
        return this;
    }

    /** 不显示下拉箭头（语法糖，等价 {@code arrowStyle(ArrowStyle.NONE)}）。 */
    public MenuButtonAnt noArrow() {
        return arrowStyle(ArrowStyle.NONE);
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 菜单项 API
    // ============================================================

    /** 添加菜单项。onClick 为 null 时不绑定回调。 */
    public MenuButtonAnt item(String label, EventHandler<ActionEvent> onClick) {
        getItems().add(MenuItemFactory.item(label, onClick));
        return this;
    }

    /** 添加带图标的菜单项。 */
    public MenuButtonAnt item(String label, Node icon, EventHandler<ActionEvent> onClick) {
        getItems().add(MenuItemFactory.item(label, icon, onClick));
        return this;
    }

    /** 添加禁用菜单项。 */
    public MenuButtonAnt itemDisabled(String label) {
        getItems().add(MenuItemFactory.itemDisabled(label));
        return this;
    }

    /** 添加分隔线。 */
    public MenuButtonAnt separator() {
        getItems().add(MenuItemFactory.separator());
        return this;
    }

    /** 直接添加原生 MenuItem（高级用法，自定义图标/快捷键等）。 */
    public MenuButtonAnt add(MenuItem item) {
        if (item != null) {
            getItems().add(item);
        }
        return this;
    }

    // ============================================================
    // 焦点 + 构建
    // ============================================================

    /**
     * 菜单按钮总是焦点可遍历（聚焦后可用键盘 Enter/Space 触发下拉）。
     */
    public MenuButtonAnt focusTraversable() {
        setFocusTraversable(true);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>MenuButtonAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public MenuButtonAnt build() {
        return this;
    }
}