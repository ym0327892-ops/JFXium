package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.MenuItemFactory;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 分割按钮组件（M19.50 重构）— 包装 JavaFX {@link SplitMenuButton}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：组合按钮——左侧主体是普通按钮（触发默认动作），右侧带小箭头点击弹出下拉菜单
 * （提供备选动作）。与 {@link MenuButtonAnt}（整体都是 trigger）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>admin "保存 / 保存并新建 / 保存并退出"——主操作 + 备选</li>
 *   <li>编辑器 "运行 / 调试 / 性能分析"——常用主操作 + 同类备选</li>
 *   <li>下载 "立即下载 / 选择路径下载"</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * SplitMenuButton save = SplitButtonAnt.create("保存")
 *     .onClick(e -> save())                            // 主按钮点击
 *     .item("保存并新建", e -> saveAndNew())             // 下拉项 1
 *     .item("保存并退出", e -> saveAndExit())            // 下拉项 2
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class SaveSplitButton extends SplitButtonAnt {
 *     public SaveSplitButton() {
 *         super("保存");
 *         onClick(e -> save());
 *         item("保存并新建", e -> saveAndNew());
 *         item("保存并退出", e -> saveAndExit());
 *     }
 * }
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>API 与 MenuButtonAnt 镜像，多了一个 {@link #onClick(EventHandler)}（主按钮点击事件）</li>
 *   <li>样式走 LESS {@code .split-menu-button} 系列（已有完整规则）</li>
 *   <li>SplitButton 必须有箭头，不支持 ArrowStyle.NONE</li>
 * </ul>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link SplitMenuButton} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size/shape/arrowStyle} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class SplitButtonAnt extends SplitMenuButton
        implements LayoutCommon<SplitButtonAnt>, DisabledSupport<SplitButtonAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    /**
     * 箭头样式（M19.6.1）。
     * <ul>
     *   <li>{@link #CHEVRON} —— Ant Design 风格细 V 形（默认）</li>
     *   <li>{@link #TRIANGLE} —— 实心三角形（AtlantaFX 风格）</li>
     * </ul>
     * <p>注意：SplitButton 必须有箭头，<b>不支持 NONE</b>。</p>
     */
    public enum ArrowStyle {
        CHEVRON,
        TRIANGLE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空文本）。 */
    public static SplitButtonAnt create() {
        return new SplitButtonAnt("");
    }

    /** 工厂入口（带文本）。 */
    public static SplitButtonAnt create(String text) {
        return new SplitButtonAnt(TextUtils.safeText(text));
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SplitButtonAnt() {
        super();
        init();
    }

    public SplitButtonAnt(String text) {
        super();
        setText(TextUtils.safeText(text));
        init();
    }

    private void init() {
        setFocusTraversable(true);
        // 主题样式类（jfx-split-menu-button）—— 与 _splitmenubutton.less 选择器对齐
        // 不挂这行则 _splitmenubutton.less 全部规则不命中（修复 #99）
        getStyleClass().add(JfxStyles.JFX_SPLIT_MENU_BUTTON);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /**
     * 设置文本（链式包装 setText）。null 安全：null 视为空串。
     */
    public SplitButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂。委托 {@link ApplySizeUtil#apply(javafx.scene.Node, Size)} 实现。
     */
    public SplitButtonAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /**
     * 箭头样式（默认 CHEVRON）。SplitButton 必须有箭头，不支持 NONE。
     */
    public SplitButtonAnt arrowStyle(ArrowStyle style) {
        getStyleClass().removeAll(JfxStyles.JFX_ARROW_TRIANGLE);
        if (style == ArrowStyle.TRIANGLE) {
            if (!getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE)) {
                getStyleClass().add(JfxStyles.JFX_ARROW_TRIANGLE);
            }
        }
        return this;
    }

    /** 设置圆角（pill 形状）。 */
    public SplitButtonAnt rounded() {
        return ApplySizeUtil.applyShapeRounded(this);
    }

    /** 设置直角方形。 */
    public SplitButtonAnt square() {
        return ApplySizeUtil.applyShapeSquare(this);
    }

    /** 设置图标。 */
    public SplitButtonAnt icon(Node icon) {
        if (icon != null) {
            setGraphic(icon);
        }
        return this;
    }

    /** 设置图标位置（图标相对文本）。 */
    public SplitButtonAnt contentDisplay(ContentDisplay display) {
        if (display != null) {
            setContentDisplay(display);
        }
        return this;
    }

    /** 主按钮点击事件（左半部分点击触发，等价 SplitMenuButton.onAction）。 */
    public SplitButtonAnt onClick(EventHandler<ActionEvent> onClick) {
        if (onClick != null) {
            setOnAction(onClick);
        }
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 菜单项 API
    // ============================================================

    /** 添加菜单项。onClick 为 null 时不绑定回调。 */
    public SplitButtonAnt item(String label, EventHandler<ActionEvent> onClick) {
        getItems().add(MenuItemFactory.item(label, onClick));
        return this;
    }

    /** 添加带图标的菜单项。 */
    public SplitButtonAnt item(String label, Node icon, EventHandler<ActionEvent> onClick) {
        getItems().add(MenuItemFactory.item(label, icon, onClick));
        return this;
    }

    /** 添加禁用菜单项。 */
    public SplitButtonAnt itemDisabled(String label) {
        getItems().add(MenuItemFactory.itemDisabled(label));
        return this;
    }

    /** 添加分隔线。 */
    public SplitButtonAnt separator() {
        getItems().add(MenuItemFactory.separator());
        return this;
    }

    /** 直接添加原生 MenuItem（高级用法，自定义图标/快捷键等）。 */
    public SplitButtonAnt add(MenuItem item) {
        if (item != null) {
            getItems().add(item);
        }
        return this;
    }

    // ============================================================
    // 焦点 + 构建
    // ============================================================

    /**
     * 分割按钮总是焦点可遍历（聚焦后可用键盘 Enter/Space 触发主操作）。
     */
    public SplitButtonAnt focusTraversable() {
        setFocusTraversable(true);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>SplitButtonAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public SplitButtonAnt build() {
        return this;
    }
}