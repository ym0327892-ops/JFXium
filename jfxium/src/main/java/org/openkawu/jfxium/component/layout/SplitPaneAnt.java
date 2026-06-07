package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.openkawu.jfxium.core.css.JfxStyles;

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
public class SplitPaneAnt extends SplitPane implements LayoutCommon<SplitPaneAnt> {

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
        super();
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    public SplitPaneAnt(Node... items) {
        super(items);
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    // ============================================================
    // 流式 API（SplitPane 特有业务方法）
    // ============================================================

    public SplitPaneAnt direction(Direction direction) {
        setOrientation(direction == Direction.VERTICAL
                ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        return this;
    }

    /** 添加单个窗格。 */
    public SplitPaneAnt item(Node item) {
        if (item != null) getItems().add(item);
        return this;
    }

    /** 批量添加窗格。 */
    public SplitPaneAnt items(Node... items) {
        if (items != null) {
            for (Node n : items) {
                if (n != null) getItems().add(n);
            }
        }
        return this;
    }

    /** 设置分隔条位置（0.0 ~ 1.0 比例，可设多个；items 数 - 1 个分隔条）。 */
    public SplitPaneAnt dividerPositions(double... positions) {
        if (positions != null && positions.length > 0) {
            setDividerPositions(positions);
        }
        return this;
    }

    /** 设置某个子节点是否随父容器调整大小。 */
    public SplitPaneAnt resizableWithParent(Node node, boolean resizable) {
        if (node != null) {
            SplitPane.setResizableWithParent(node, resizable);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<SplitPaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 SplitPaneAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public SplitPaneAnt build() {
        return this;
    }
}
