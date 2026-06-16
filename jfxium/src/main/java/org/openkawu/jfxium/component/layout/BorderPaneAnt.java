package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;

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
public class BorderPaneAnt extends AbstractBorderPaneAnt<BorderPaneAnt> {

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
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<BorderPaneAnt> 默认实现
    // ============================================================

}
