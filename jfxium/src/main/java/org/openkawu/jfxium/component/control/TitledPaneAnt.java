package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 标题面板组件 - 对标 Ant Design Collapse Panel（继承式 + 双工厂模式，M19.x 重构）。
 *
 * <p><b>定位</b>：可折叠的标题 + 内容面板，继承自 {@link TitledPane}，
 * 可单独使用或组合到 {@link org.openkawu.jfxium.component.composite.CollapseAnt} 中。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置标题文本 + 内容节点</li>
 *   <li>控制展开/折叠状态（{@code expanded}）</li>
 *   <li>是否允许折叠（{@code collapsible}）</li>
 *   <li>动画开关（{@code animated}）</li>
 *   <li>继承 {@link LayoutCommon}：自动获得 styleClass / style / background / padding /
 *       borderXxx / borderRadius / 尺寸 / visible / disable 等通用能力</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>单个可折叠详情区域</li>
 *   <li>嵌入 CollapseAnt 作为子面板</li>
 *   <li>表单分组（点击展开高级选项）</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式（build 可选）</h2>
 * <pre>{@code
 * TitledPane pane = TitledPaneAnt.create("高级设置")
 *     .content(settingsForm)
 *     .expanded(false)        // 默认折叠
 *     .collapsible(true)
 *     .animated(true)
 *     .build();
 *
 * // build 之后再改（继承式核心优势）
 * pane.expanded(true);
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class AdvancedSettingsPane extends TitledPaneAnt {
 *     public AdvancedSettingsPane(Node content) {
 *         title("高级设置");
 *         content(content);
 *         expanded(false);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link TitledPane} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class TitledPaneAnt extends TitledPane
        implements LayoutCommon<TitledPaneAnt>, DisabledSupport<TitledPaneAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空标题 + 空内容）。 */
    public static TitledPaneAnt create() {
        return new TitledPaneAnt();
    }

    /** 工厂入口（带标题）。 */
    public static TitledPaneAnt create(String title) {
        return new TitledPaneAnt(TextUtils.safeText(title), null);
    }

    /** 工厂入口（带标题 + 内容）。 */
    public static TitledPaneAnt create(String title, Node content) {
        return new TitledPaneAnt(TextUtils.safeText(title), content);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public TitledPaneAnt() {
        super();
        init();
    }

    public TitledPaneAnt(String title) {
        super(TextUtils.safeText(title), null);
        init();
    }

    public TitledPaneAnt(String title, Node content) {
        super(TextUtils.safeText(title), content);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.JFX_TITLED_PANE);
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 流式 API —— 标题 / 内容
    // ============================================================

    /** 设置标题文本。 */
    public TitledPaneAnt title(String title) {
        setText(TextUtils.safeText(title));
        return this;
    }

    /** 设置内容节点。 */
    public TitledPaneAnt content(Node content) {
        setContent(content);
        return this;
    }

    // ============================================================
    // 流式 API —— 行为（expanded / animated / collapsible）
    // ============================================================

    /**
     * 是否展开。{@code true} = 展开，{@code false} = 折叠。
     * <p>与 JavaFX 原生 {@link TitledPane#setExpanded(boolean)} 等价，链式包装。</p>
     */
    public TitledPaneAnt expanded(boolean expanded) {
        setExpanded(expanded);
        return this;
    }

    /**
     * 是否启用展开/折叠动画。
     * <p>关闭时高度瞬间变化（无过渡），开启时高度平滑过渡。</p>
     */
    public TitledPaneAnt animated(boolean animated) {
        setAnimated(animated);
        return this;
    }

    /**
     * 是否允许用户折叠（点击标题切换展开状态）。
     * <p>{@code false} 时标题不可点击，强制保持展开。</p>
     */
    public TitledPaneAnt collapsible(boolean collapsible) {
        setCollapsible(collapsible);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身（向后兼容）。
     *
     * <p>TitledPaneAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public TitledPaneAnt build() {
        return this;
    }
}
