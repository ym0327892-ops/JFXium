package org.openkawu.jfxium.core.layout;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * Scene 根布局管理器。
 * 提供标准的应用骨架：AppBar + Content + OverlayLayer。
 *
 * <h2>设计理念</h2>
 * SceneLayout <b>只提供骨架</b>（BorderPane 三段式：top/center/bottom），
 * 具体每个区域里用什么布局（VBox/HBox/BorderPane/GridPane）由用户自己决定。
 *
 * <h2>架构</h2>
 * <pre>
 * Scene
 *   └─ StackPane (root)
 *       ├─ BorderPane (mainLayout) - 骨架，固定为 BorderPane
 *       │   ├─ top: appBar      - 用户自定义（可以是 HBox/VBox/BorderPane 等）
 *       │   ├─ center: content  - 用户自定义（可以是任何布局）
 *       │   └─ bottom: footer   - 用户自定义（可以是 HBox/VBox 等）
 *       └─ StackPane (overlayLayer) - 全局浮层容器
 * </pre>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 1. 用户自己决定每个区域的布局
 * HBox appBar = HBoxBuilder.create()
 *     .spacing(16)
 *     .padding(16)
 *     .children(title, Spacers.grow(), buttons)
 *     .build();
 *
 * VBox content = VBoxBuilder.create()
 *     .spacing(24)
 *     .padding(24)
 *     .children(form, table)
 *     .build();
 *
 * HBox footer = HBoxBuilder.create()
 *     .padding(8, 16, 8, 16)
 *     .children(statusLabel)
 *     .build();
 *
 * // 2. SceneLayout 只提供骨架，不限制内部布局
 * SceneLayout sceneLayout = SceneLayout.create()
 *     .appBar(appBar)      // 可选，用户自己决定用什么布局
 *     .content(content)    // 必选，用户自己决定用什么布局
 *     .footer(footer)      // 可选，用户自己决定用什么布局
 *     .buildLayout();
 *
 * Scene scene = sceneLayout.createScene(1200, 800);
 *
 * // 3. 注册浮层管理器
 * OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());
 * }</pre>
 */
public class SceneLayout {
    private final StackPane root;
    private final BorderPane mainLayout;
    private final StackPane overlayLayer;

    private SceneLayout(StackPane root, BorderPane mainLayout, StackPane overlayLayer) {
        this.root = root;
        this.mainLayout = mainLayout;
        this.overlayLayer = overlayLayer;
    }

    /**
     * 创建 Builder。
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * 获取根容器。
     */
    public StackPane getRoot() {
        return root;
    }

    /**
     * 获取主布局容器。
     */
    public BorderPane getMainLayout() {
        return mainLayout;
    }

    /**
     * 获取浮层容器（供 OverlayManager 使用）。
     */
    public StackPane getOverlayLayer() {
        return overlayLayer;
    }

    /**
     * 创建 Scene。
     *
     * @param width  场景宽度
     * @param height 场景高度
     * @return Scene 对象
     */
    public Scene createScene(double width, double height) {
        return new Scene(root, width, height);
    }

    /**
     * SceneLayout Builder。
     */
    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node appBar;
        private Node content;
        private Node footer;

        /**
         * 设置顶部应用栏。
         */
        public Builder appBar(Node appBar) {
            this.appBar = appBar;
            return this;
        }

        /**
         * 设置主内容区域。
         */
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /**
         * 设置底部区域。
         */
        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        /**
         * 构建 SceneLayout。
         */
        public SceneLayout buildLayout() {
            // 主布局（BorderPane）
            BorderPane mainLayout = new BorderPane();
            if (appBar != null) {
                mainLayout.setTop(appBar);
            }
            if (content != null) {
                mainLayout.setCenter(content);
            }
            if (footer != null) {
                mainLayout.setBottom(footer);
            }

            // 浮层容器（StackPane）
            StackPane overlayLayer = new StackPane();
            overlayLayer.setMouseTransparent(true);  // 默认鼠标穿透
            overlayLayer.setPickOnBounds(false);     // 不拦截鼠标事件

            // 根容器（StackPane）：主布局 + 浮层
            StackPane root = new StackPane();
            root.getChildren().addAll(mainLayout, overlayLayer);

            return new SceneLayout(root, mainLayout, overlayLayer);
        }
    }
}
