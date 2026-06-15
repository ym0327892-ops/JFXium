package org.openkawu.jfxium.component.layout;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 绝对定位布局组件 - 对标 CSS position: absolute。
 *
 * <p><b>定位</b>：绝对定位容器，继承自 JavaFX {@link AnchorPane}。
 * 子节点通过上下左右锚定值精确定位，常用于复杂仪表盘、拖拽设计器等需要
 * 像素级控制的场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>锚定</b>：anchor(node, top, right, bottom, left) 四边锚定</li>
 *   <li><b>快捷锚定</b>：topAnchor / bottomAnchor / leftAnchor / rightAnchor 单边设置</li>
 *   <li><b>居中</b>：center(node) 子节点居中</li>
 *   <li><b>全填充</b>：fill(node) 子节点填满容器</li>
 *   <li><b>子节点</b>：children(Node...) 批量添加</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#ANCHOR_PANE} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础绝对定位
 * AnchorPaneAnt pane = AnchorPaneAnt.create()
 *     .children(header, sidebar, content)
 *     .topAnchor(header, 0.0)
 *     .leftAnchor(sidebar, 0.0)
 *     .bottomAnchor(sidebar, 0.0)
 *     .anchor(content, 60.0, 0.0, 0.0, 200.0) // top, right, bottom, left
 *     .build();
 *
 * // 居中弹窗
 * AnchorPaneAnt dialog = AnchorPaneAnt.create()
 *     .children(overlay, popup)
 *     .fill(overlay)
 *     .center(popup)
 *     .build();
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 AnchorPaneAnt）</li>
 *   <li><b>双重身份</b>：是 AnchorPane 也是工厂——继承自 {@link AnchorPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class AnchorPaneAnt extends AnchorPane implements LayoutCommon<AnchorPaneAnt> {

    private static final String CENTER_BINDING_KEY = AnchorPaneAnt.class.getName() + ".centerBinding";

    // ============================================================
    // 工厂入口
    // ============================================================

    public static AnchorPaneAnt create() {
        return new AnchorPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public AnchorPaneAnt() {
        super();
        getStyleClass().add(JfxStyles.ANCHOR_PANE);
    }

    // ============================================================
    // 流式配置（AnchorPane 特有业务方法）
    // ============================================================

    public AnchorPaneAnt anchor(Node node, Double top, Double right, Double bottom, Double left) {
        if (node != null) {
            clearCenterBinding(node);
            if (top != null) setTopAnchor(node, top);
            if (right != null) setRightAnchor(node, right);
            if (bottom != null) setBottomAnchor(node, bottom);
            if (left != null) setLeftAnchor(node, left);
        }
        return this;
    }

    public AnchorPaneAnt topAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setTopAnchor(node, value);
        }
        return this;
    }

    public AnchorPaneAnt bottomAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setBottomAnchor(node, value);
        }
        return this;
    }

    public AnchorPaneAnt leftAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setLeftAnchor(node, value);
        }
        return this;
    }

    public AnchorPaneAnt rightAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setRightAnchor(node, value);
        }
        return this;
    }

    public AnchorPaneAnt center(Node node) {
        if (node != null) {
            clearCenterBinding(node);
            clearAnchors(node);

            CenterBinding binding = new CenterBinding(node);
            widthProperty().addListener(binding.containerWidthListener);
            heightProperty().addListener(binding.containerHeightListener);
            node.layoutBoundsProperty().addListener(binding.nodeBoundsListener);
            node.parentProperty().addListener(binding.parentListener);
            node.getProperties().put(CENTER_BINDING_KEY, binding);

            Platform.runLater(binding::update);
        }
        return this;
    }

    public AnchorPaneAnt fill(Node node) {
        if (node != null) {
            clearCenterBinding(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
        return this;
    }

    public AnchorPaneAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null) {
                    getChildren().add(node);
                }
            }
        }
        return this;
    }

    public AnchorPaneAnt add(Node node) {
        if (node != null) {
            getChildren().add(node);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<AnchorPaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 AnchorPaneAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public AnchorPaneAnt build() {
        return this;
    }

    private void clearAnchors(Node node) {
        AnchorPane.setTopAnchor(node, null);
        AnchorPane.setBottomAnchor(node, null);
        AnchorPane.setLeftAnchor(node, null);
        AnchorPane.setRightAnchor(node, null);
    }

    private void clearCenterBinding(Node node) {
        Object bindingObj = node.getProperties().remove(CENTER_BINDING_KEY);
        if (bindingObj instanceof CenterBinding binding) {
            widthProperty().removeListener(binding.containerWidthListener);
            heightProperty().removeListener(binding.containerHeightListener);
            node.layoutBoundsProperty().removeListener(binding.nodeBoundsListener);
            node.parentProperty().removeListener(binding.parentListener);
        }
    }

    private final class CenterBinding {
        private final Node node;
        private final ChangeListener<Number> containerWidthListener;
        private final ChangeListener<Number> containerHeightListener;
        private final ChangeListener<Bounds> nodeBoundsListener;
        private final ChangeListener<Parent> parentListener;

        private CenterBinding(Node node) {
            this.node = node;
            this.containerWidthListener = (obs, oldValue, newValue) -> update();
            this.containerHeightListener = (obs, oldValue, newValue) -> update();
            this.nodeBoundsListener = (obs, oldValue, newValue) -> update();
            this.parentListener = (obs, oldValue, newValue) -> {
                if (newValue == AnchorPaneAnt.this) {
                    Platform.runLater(this::update);
                } else {
                    clearCenterBinding(this.node);
                }
            };
        }

        private void update() {
            if (node.getParent() != AnchorPaneAnt.this) {
                return;
            }
            if (node instanceof Region region) {
                region.autosize();
            }

            double nodeWidth = node.prefWidth(-1);
            if (!Double.isFinite(nodeWidth) || nodeWidth <= 0) {
                nodeWidth = node.getLayoutBounds().getWidth();
            }

            double nodeHeight = node.prefHeight(-1);
            if (!Double.isFinite(nodeHeight) || nodeHeight <= 0) {
                nodeHeight = node.getLayoutBounds().getHeight();
            }

            double x = Math.max(0, (getWidth() - nodeWidth) / 2.0);
            double y = Math.max(0, (getHeight() - nodeHeight) / 2.0);
            node.relocate(x, y);
        }
    }
}
