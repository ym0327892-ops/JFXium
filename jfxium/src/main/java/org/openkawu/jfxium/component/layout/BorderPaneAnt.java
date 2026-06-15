package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * BorderPaneAnt - 继承式 BorderPane 容器。
 *
 * <p>五区域（top / center / bottom / left / right）布局的双工厂模式——
 * 典型场景：应用主框架（顶栏 + 内容 + 底栏 + 侧栏）、
 * CRUD 页面骨架、设置面板。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * BorderPaneAnt root = BorderPaneAnt.create()
 *     .top(appBar)
 *     .center(content)
 *     .bottom(statusBar)
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class AppFrame extends BorderPaneAnt {
 *     public AppFrame() {
 *         top(buildAppBar());
 *         center(buildMainContent());
 *         bottom(buildStatusBar());
 *     }
 *     private Node buildAppBar() { ... }
 *     private Node buildMainContent() { ... }
 *     private Node buildStatusBar() { ... }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现</li>
 *   <li><b>双重身份</b>：是 BorderPane 也是工厂——继承自 {@link BorderPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class BorderPaneAnt extends BorderPane implements LayoutCommon<BorderPaneAnt> {

    /** 工厂入口。 */
    public static BorderPaneAnt create() {
        return new BorderPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public BorderPaneAnt() {
        super();
    }

    public BorderPaneAnt(Node center) {
        super(center);
    }

    public BorderPaneAnt(Node center, Node top, Node right, Node bottom, Node left) {
        super(center, top, right, bottom, left);
    }

    // ============================================================
    // 流式 API（BorderPane 特有业务方法）
    // ============================================================

    /** 设置顶部节点。 */
    public BorderPaneAnt top(Node node) {
        setTop(node);
        return this;
    }

    /** 设置中心节点。 */
    public BorderPaneAnt center(Node node) {
        setCenter(node);
        return this;
    }

    /** 设置底部节点。 */
    public BorderPaneAnt bottom(Node node) {
        setBottom(node);
        return this;
    }

    /** 设置左侧节点。 */
    public BorderPaneAnt left(Node node) {
        setLeft(node);
        return this;
    }

    /** 设置右侧节点。 */
    public BorderPaneAnt right(Node node) {
        setRight(node);
        return this;
    }

    /**
     * BorderPane 只能稳定承载五个命名区域，不能像普通 Pane 一样直接向 children 里塞任意节点。
     * 请改用 {@link #top(Node)} / {@link #left(Node)} / {@link #center(Node)} /
     * {@link #right(Node)} / {@link #bottom(Node)} 显式指定区域。
     *
     * <p>为兼容历史上把 BorderPaneAnt 当作普通容器使用的代码：
     * <ul>
     *   <li>0 个非 null 节点：忽略</li>
     *   <li>1 个非 null 节点：放到 center</li>
     *   <li>2+ 个非 null 节点：按传入顺序包进一个零间距 {@link VBox}，整体放到 center</li>
     * </ul>
     * 这样至少不会在运行时抛异常；若需要精确五区域布局，仍应改用命名区域 API。</p>
     */
    @Deprecated(forRemoval = false)
    public BorderPaneAnt children(Node... nodes) {
        if (nodes == null) {
            return this;
        }

        Node onlyNode = null;
        int nonNullCount = 0;
        for (Node node : nodes) {
            if (node != null) {
                onlyNode = node;
                nonNullCount++;
            }
        }

        if (nonNullCount == 0) {
            return this;
        }
        if (nonNullCount == 1) {
            setCenter(onlyNode);
            return this;
        }

        // 多节点兼容模式：退化为把节点顺序打包进 center 区，避免运行时 footgun。
        VBox fallbackCenter = new VBox();
        fallbackCenter.setSpacing(0);
        for (Node node : nodes) {
            if (node != null) {
                fallbackCenter.getChildren().add(node);
            }
        }
        setCenter(fallbackCenter);
        return this;
    }

    /** 设置指定子节点在 BorderPane 内的对齐方式。 */
    public BorderPaneAnt align(Node child, Pos alignment) {
        if (child != null && alignment != null) {
            BorderPane.setAlignment(child, alignment);
        }
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public BorderPaneAnt margin(Node child, Insets margin) {
        if (child != null) {
            BorderPane.setMargin(child, margin);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<BorderPaneAnt> 默认实现
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public BorderPaneAnt build() {
        return this;
    }
}
