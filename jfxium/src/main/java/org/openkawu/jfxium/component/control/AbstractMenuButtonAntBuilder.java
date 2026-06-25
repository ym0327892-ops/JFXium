package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.MenuItemFactory;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单按钮 Builder 抽象基类（P1-S3 抽取）——
 * 集中 {@link MenuButtonAnt.Builder} 等菜单按钮 Builder 的共有字段与 API。
 *
 * <h2>抽取动机</h2>
 * <p>此前 {@link MenuButtonAnt.Builder} 自包含 size / disabled / icon / contentDisplay /
 * rounded / square / arrowStyle 等通用属性，每个字段 + setter + build() 末尾的应用逻辑
 * 都重复定义。新增菜单按钮 Builder 时需要拷贝全部代码。</p>
 *
 * <p>基类统一管理这些通用属性，子类只需关注自身特有字段（如 MenuButtonAnt 的 items 列表）
 * 与 build() 中的菜单项组装逻辑。</p>
 *
 * <h2>基类职责</h2>
 * <ul>
 *   <li><b>通用字段</b>：size / disabled / icon / contentDisplay / rounded / square / arrowStyle</li>
 *   <li><b>通用 setter</b>：size / disabled / icon / contentDisplay / rounded / square /
 *       arrowStyle / noArrow —— 全部返回 {@code SELF} 便于链式</li>
 *   <li><b>通用应用原语</b>：{@link #applyCommonProperties(javafx.scene.control.MenuButton)}
 *       —— 在子类 build() 末尾调用，把通用属性应用到 build 出的目标 MenuButton</li>
 *   <li><b>菜单项工厂</b>：{@link #newItem} / {@link #newItemWithIcon} / {@link #newDisabledItem}
 *       —— 集中 MenuItem 创建逻辑，子类的 {@code item / itemDisabled / separator / add} 直接复用</li>
 * </ul>
 *
 * <h2>使用模式</h2>
 * <pre>{@code
 * public static class MyMenuButtonBuilder extends AbstractMenuButtonAntBuilder<MyMenuButtonBuilder> {
 *     private final List<MenuItem> items = new ArrayList<>();
 *
 *     public MyMenuButtonBuilder item(String label, EventHandler<ActionEvent> onClick) {
 *         items.add(newItem(label, onClick));
 *         return this;
 *     }
 *
 *     public MenuButton build() {
 *         MenuButton btn = new MenuButton(text);
 *         btn.getItems().addAll(items);
 *         applyCommonProperties(btn);  // 一行委托基类应用 size/icon/disabled/...
 *         return btn;
 *     }
 * }
 * }</pre>
 *
 * @param <SELF> 子类自身类型（协变返回）
 * @see MenuButtonAnt.Builder
 */
public abstract class AbstractMenuButtonAntBuilder<SELF extends AbstractMenuButtonAntBuilder<SELF>>
        extends AbstractStyleBuilder<SELF> {

    // ============================================================
    // 通用字段
    // ============================================================

    /** 文本（构造时设置）。子类可继承。 */
    protected final String text;

    private Size size = Size.DEFAULT;
    // disabled 复用父类 AbstractStyleBuilder.disable 字段（P2-S7.4 抽取），
    // 子类用 disabled(true) / disabled() 调用父类默认实现即可，
    // applyCommonProperties() 会把 disable 字段应用到 MenuButton。
    private boolean rounded = false;
    private boolean square = false;
    private Node icon;
    private ContentDisplay contentDisplay = ContentDisplay.LEFT;

    /** 箭头样式（默认 CHEVRON）。子类若不需要箭头可忽略。 */
    private MenuButtonAnt.ArrowStyle arrowStyle = MenuButtonAnt.ArrowStyle.CHEVRON;

    // ============================================================
    // 构造函数
    // ============================================================

    /**
     * @param text 菜单按钮显示文本（允许 null，内部归一为空字符串）
     */
    protected AbstractMenuButtonAntBuilder(String text) {
        this.text = TextUtils.safeText(text);
    }

    // ============================================================
    // 通用 setter（全部返回 SELF 协变）
    // ============================================================

    /** 设置尺寸。 */
    public SELF size(Size size) {
        this.size = ApplySizeUtil.normalize(size);
        return self();
    }

    /** 设置圆角（pill 形状）。 */
    public SELF rounded() {
        this.rounded = true;
        this.square = false;
        return self();
    }

    /** 设置直角方形。 */
    public SELF square() {
        this.square = true;
        this.rounded = false;
        return self();
    }

    /** 设置图标。 */
    public SELF icon(Node icon) {
        this.icon = icon;
        return self();
    }

    /** 设置图标位置（图标相对文本）。 */
    public SELF contentDisplay(ContentDisplay display) {
        this.contentDisplay = display != null ? display : ContentDisplay.LEFT;
        return self();
    }

    /** 设置箭头样式（默认 CHEVRON）。 */
    public SELF arrowStyle(MenuButtonAnt.ArrowStyle style) {
        this.arrowStyle = style != null ? style : MenuButtonAnt.ArrowStyle.CHEVRON;
        return self();
    }

    /** 不显示下拉箭头（语法糖，等价 {@code arrowStyle(ArrowStyle.NONE)}）。 */
    public SELF noArrow() {
        return arrowStyle(MenuButtonAnt.ArrowStyle.NONE);
    }

    // ============================================================
    // 菜单项工厂方法（供子类复用，消除 item 重载重复）
    // ============================================================

    /** 创建普通菜单项。委托 {@link MenuItemFactory#item}。 */
    protected MenuItem newItem(String label, EventHandler<ActionEvent> onClick) {
        return MenuItemFactory.item(label, onClick);
    }

    /** 创建带图标的菜单项。委托 {@link MenuItemFactory#item}。 */
    protected MenuItem newItemWithIcon(String label, Node icon, EventHandler<ActionEvent> onClick) {
        return MenuItemFactory.item(label, icon, onClick);
    }

    /** 创建禁用菜单项。委托 {@link MenuItemFactory#itemDisabled}。 */
    protected MenuItem newDisabledItem(String label) {
        return MenuItemFactory.itemDisabled(label);
    }

    /** 创建分隔线。委托 {@link MenuItemFactory#separator}。 */
    protected SeparatorMenuItem newSeparator() {
        return MenuItemFactory.separator();
    }

    // ============================================================
    // 通用应用原语 —— 子类 build() 末尾调用
    // ============================================================

    /**
     * 把通用属性（size / disabled / icon / contentDisplay / shape / arrowStyle）应用到
     * 已构建的 MenuButton。
     *
     * <p>使用顺序：{@code new MenuButton -> setItems -> applyCommonProperties(btn)}
     * 这样所有用户通过 {@code style/styleClass} 注入的覆盖都最后生效。</p>
     *
     * @param btn 已构建的 MenuButton；null 时静默返回
     */
    protected void applyCommonProperties(javafx.scene.control.MenuButton btn) {
        if (btn == null) {
            return;
        }
        // 父类 AbstractStyleBuilder.disable 字段（P2-S7.4 统一）：
        // null = 不干预节点 disable 状态；非 null = 应用到目标节点
        if (disable != null) {
            btn.setDisable(disable);
        }

        // Size（委托 ApplySizeUtil，互斥清理 size-xs/small/middle/large）
        ApplySizeUtil.apply(btn, size);

        // Shape（互斥 rounded/square）
        if (rounded) {
            ApplySizeUtil.applyShapeRounded(btn);
        } else if (square) {
            ApplySizeUtil.applyShapeSquare(btn);
        }

        // Arrow style
        switch (arrowStyle) {
            case TRIANGLE -> btn.getStyleClass().add(JfxStyles.JFX_ARROW_TRIANGLE);
            case NONE -> btn.getStyleClass().add(JfxStyles.JFX_NO_ARROW);
            case CHEVRON -> { /* 默认，无需额外 styleClass */ }
        }

        // Icon
        if (icon != null) {
            btn.setGraphic(icon);
            btn.setContentDisplay(contentDisplay);
        }

        btn.setFocusTraversable(true);
    }

    // ============================================================
    // SELF 协变辅助
    // ============================================================

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }
}
