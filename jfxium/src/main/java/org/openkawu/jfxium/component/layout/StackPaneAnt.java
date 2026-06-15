package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * StackPaneAnt - 继承式 StackPane 容器（M19.36 引入）。
 *
 * <p>子节点叠层（z-axis）的双工厂模式——典型场景：徽标覆盖头像、loading 遮罩盖内容、
 * 浮层覆盖主面板。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * StackPaneAnt avatar = StackPaneAnt.create()
 *     .align(Pos.TOP_RIGHT)
 *     .children(avatarImage, badge);   // badge 浮在右上角
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class LoadingOverlay extends StackPaneAnt {
 *     public LoadingOverlay(Node content) {
 *         children(content, buildLoadingMask());
 *     }
 *     private Node buildLoadingMask() { ... }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~150 行重复模板代码，行为 100% 等价原 StackPaneAnt）</li>
 *   <li><b>双重身份</b>：是 StackPane 也是工厂——继承自 {@link StackPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class StackPaneAnt extends StackPane implements LayoutCommon<StackPaneAnt> {

    /** 工厂入口。 */
    public static StackPaneAnt create() {
        return new StackPaneAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static StackPaneAnt create(Node... children) {
        return new StackPaneAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public StackPaneAnt() {
        super();
    }

    public StackPaneAnt(Node... children) {
        super();
        this.children(children);
    }

    // ============================================================
    // 流式 API（StackPane 特有业务方法）
    // ============================================================

    /** 设置子节点对齐方式（StackPane 默认 CENTER）。 */
    public StackPaneAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    /** 批量添加子节点（null 节点会被过滤）。 */
    public StackPaneAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** 设置指定子节点在 StackPane 内的对齐方式（覆盖容器级 align）。 */
    public StackPaneAnt childAlign(Node child, Pos alignment) {
        StackPane.setAlignment(child, alignment);
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public StackPaneAnt margin(Node child, Insets margin) {
        StackPane.setMargin(child, margin);
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<StackPaneAnt> 默认实现
    // （节省 ~150 行重复模板代码，行为 100% 等价原 StackPaneAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public StackPaneAnt build() {
        return this;
    }
}
