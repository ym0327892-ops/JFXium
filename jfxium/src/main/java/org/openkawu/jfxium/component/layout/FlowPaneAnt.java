package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * FlowPaneAnt - 继承式 FlowPane 容器（M19.36 引入）。
 *
 * <p>流式布局（自动换行）的双工厂模式——典型场景：标签云、自适应按钮组、
 * 不定数量子节点 wrap 排列。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * FlowPaneAnt tagCloud = FlowPaneAnt.create()
 *     .hgap(8).vgap(8)
 *     .children(tag1, tag2, tag3, tag4, ...);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~120 行重复模板代码，行为 100% 等价原 FlowPaneAnt）</li>
 *   <li><b>双重身份</b>：是 FlowPane 也是工厂——继承自 {@link FlowPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class FlowPaneAnt extends AbstractFlowPaneAnt<FlowPaneAnt> {

    /** 工厂入口。 */
    public static FlowPaneAnt create() {
        return new FlowPaneAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static FlowPaneAnt create(Node... children) {
        return new FlowPaneAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public FlowPaneAnt() {
        super();
    }

    public FlowPaneAnt(Node... children) { super(children); }

    public FlowPaneAnt(Orientation orientation) { super(orientation); }

    public FlowPaneAnt(double hgap, double vgap) { super(hgap, vgap); }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<FlowPaneAnt> 默认实现
    // （节省 ~120 行重复模板代码，行为 100% 等价原 FlowPaneAnt）
    // ============================================================

}
