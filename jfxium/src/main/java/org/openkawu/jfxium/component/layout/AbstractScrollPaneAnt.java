package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * ScrollPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractScrollPaneAnt<SELF extends AbstractScrollPaneAnt<SELF>>
        extends ScrollPane implements LayoutCommon<SELF> {

    private StackPane viewport;
    private Insets pendingPadding;

    protected AbstractScrollPaneAnt() {
        super();
    }

    protected AbstractScrollPaneAnt(Node content) {
        super();
        content(content);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 设置滚动内容（自动包一层 {@link StackPane} 作为 viewport 容器）。 */
    public SELF content(Node content) {
        if (content == null) {
            clearViewportChildren();
            setContent(null);
            viewport = null;
            return self();
        }
        clearViewportChildren();
        viewport = new StackPane(content);
        viewport.getStyleClass().add(JfxStyles.SCROLL_PANE_VIEWPORT);
        viewport.setAlignment(Pos.TOP_LEFT);
        if (pendingPadding != null) {
            viewport.setPadding(pendingPadding);
        }
        setContent(viewport);
        return self();
    }

    public SELF fitToWidth(boolean fit) {
        setFitToWidth(fit);
        return self();
    }

    public SELF fitToHeight(boolean fit) {
        setFitToHeight(fit);
        return self();
    }

    public SELF pannable(boolean pannable) {
        setPannable(pannable);
        return self();
    }

    public SELF hbarPolicy(ScrollBarPolicy policy) {
        if (policy != null) setHbarPolicy(policy);
        return self();
    }

    public SELF vbarPolicy(ScrollBarPolicy policy) {
        if (policy != null) setVbarPolicy(policy);
        return self();
    }

    @Override
    public SELF padding(double padding) {
        pendingPadding = new Insets(padding);
        applyPendingPadding();
        return self();
    }

    @Override
    public SELF padding(double top, double right, double bottom, double left) {
        pendingPadding = new Insets(top, right, bottom, left);
        applyPendingPadding();
        return self();
    }

    @Override
    public SELF padding(Insets padding) {
        pendingPadding = padding;
        applyPendingPadding();
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
    }

    private void applyPendingPadding() {
        if (viewport != null && pendingPadding != null) {
            viewport.setPadding(pendingPadding);
        }
    }

    private void clearViewportChildren() {
        if (viewport != null) {
            viewport.getChildren().clear();
        }
    }
}
