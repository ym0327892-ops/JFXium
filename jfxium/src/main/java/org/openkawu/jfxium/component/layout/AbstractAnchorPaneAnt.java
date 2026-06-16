package org.openkawu.jfxium.component.layout;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

/**
 * AnchorPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractAnchorPaneAnt<SELF extends AbstractAnchorPaneAnt<SELF>>
        extends AnchorPane implements LayoutCommon<SELF> {

    private static final String CENTER_BINDING_KEY = AbstractAnchorPaneAnt.class.getName() + ".centerBinding";

    protected AbstractAnchorPaneAnt() {
        super();
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    public SELF anchor(Node node, Double top, Double right, Double bottom, Double left) {
        if (node != null) {
            clearCenterBinding(node);
            if (top != null) setTopAnchor(node, top);
            if (right != null) setRightAnchor(node, right);
            if (bottom != null) setBottomAnchor(node, bottom);
            if (left != null) setLeftAnchor(node, left);
        }
        return self();
    }

    public SELF topAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setTopAnchor(node, value);
        }
        return self();
    }

    public SELF bottomAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setBottomAnchor(node, value);
        }
        return self();
    }

    public SELF leftAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setLeftAnchor(node, value);
        }
        return self();
    }

    public SELF rightAnchor(Node node, double value) {
        if (node != null) {
            clearCenterBinding(node);
            setRightAnchor(node, value);
        }
        return self();
    }

    public SELF center(Node node) {
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
        return self();
    }

    public SELF fill(Node node) {
        if (node != null) {
            clearCenterBinding(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
        return self();
    }

    public SELF children(Node... nodes) {
        addChildren(nodes);
        return self();
    }

    public SELF add(Node node) {
        if (node != null) {
            getChildren().add(node);
        }
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
    }

    private void addChildren(Node... nodes) {
        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null) {
                    getChildren().add(node);
                }
            }
        }
    }

    private void clearAnchors(Node node) {
        AnchorPane.setTopAnchor(node, null);
        AnchorPane.setBottomAnchor(node, null);
        AnchorPane.setLeftAnchor(node, null);
        AnchorPane.setRightAnchor(node, null);
    }

    private void clearCenterBinding(Node node) {
        Object bindingObj = node.getProperties().remove(CENTER_BINDING_KEY);
        if (bindingObj instanceof AbstractAnchorPaneAnt<?>.CenterBinding binding) {
            binding.dispose();
        }
    }

    private final class CenterBinding {
        private final Node node;
        private final ChangeListener<Number> containerWidthListener;
        private final ChangeListener<Number> containerHeightListener;
        private final ChangeListener<Bounds> nodeBoundsListener;
        private final ChangeListener<Parent> parentListener;
        private final AbstractAnchorPaneAnt<SELF> owner;

        private CenterBinding(Node node) {
            this.node = node;
            this.owner = AbstractAnchorPaneAnt.this;
            this.containerWidthListener = (obs, oldValue, newValue) -> update();
            this.containerHeightListener = (obs, oldValue, newValue) -> update();
            this.nodeBoundsListener = (obs, oldValue, newValue) -> update();
            this.parentListener = (obs, oldValue, newValue) -> {
                if (newValue == owner) {
                    Platform.runLater(this::update);
                } else {
                    clearCenterBinding(this.node);
                }
            };
        }

        private void dispose() {
            owner.widthProperty().removeListener(containerWidthListener);
            owner.heightProperty().removeListener(containerHeightListener);
            node.layoutBoundsProperty().removeListener(nodeBoundsListener);
            node.parentProperty().removeListener(parentListener);
        }

        private void update() {
            if (node.getParent() != owner) {
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

            double x = Math.max(0, (owner.getWidth() - nodeWidth) / 2.0);
            double y = Math.max(0, (owner.getHeight() - nodeHeight) / 2.0);
            node.relocate(x, y);
        }
    }
}
