package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * SplitPaneAnt - 继承式 SplitPane 容器（M19.36 升级为双工厂模式）。
 *
 * <p>可拖拽分隔的多窗格的双工厂模式——既能当工厂用，也能被业务继承做"分屏页基类"。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * SplitPaneAnt split = SplitPaneAnt.create()
 *     .direction(SplitPaneAnt.Direction.HORIZONTAL)
 *     .items(leftPanel, rightPanel)
 *     .dividerPositions(0.3);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 SplitPaneAnt）</li>
 *   <li><b>双重身份</b>：是 SplitPane 也是工厂——继承自 {@link SplitPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class SplitPaneAnt extends AbstractSplitPaneAnt<SplitPaneAnt> {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    /** 工厂入口。 */
    public static SplitPaneAnt create() {
        return new SplitPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SplitPaneAnt() {
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    public SplitPaneAnt(Node... items) {
        super(items);
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<SplitPaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 SplitPaneAnt）
    // ============================================================

}
