package org.openkawu.jfxium.component;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 分割面板组件。
 *
 * <pre>{@code
 * SplitPane split = SplitPaneAnt.create()
 *     .direction(SplitPaneAnt.Direction.HORIZONTAL)
 *     .items(leftPanel, rightPanel)
 *     .dividerPositions(0.3)
 *     .build();
 * }</pre>
 */
public class SplitPaneAnt {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> items = new ArrayList<>();
        private Direction direction = Direction.HORIZONTAL;
        private double[] dividerPositions = new double[0];

        private Builder() {}

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder item(Node item) {
            if (item != null) {
                this.items.add(item);
            }
            return this;
        }

        public Builder items(Node... items) {
            if (items != null) {
                for (Node item : items) {
                    item(item);
                }
            }
            return this;
        }

        public Builder dividerPositions(double... positions) {
            this.dividerPositions = positions != null ? positions : new double[0];
            return this;
        }

        public Builder resizableWithParent(Node node, boolean resizable) {
            if (node != null) {
                SplitPane.setResizableWithParent(node, resizable);
            }
            return this;
        }

        public SplitPane build() {
            SplitPane pane = new SplitPane();
            pane.getStyleClass().add(CssClasses.SPLIT_PANE);
            pane.setOrientation(direction == Direction.VERTICAL ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            pane.getItems().addAll(items);
            if (dividerPositions.length > 0) {
                pane.setDividerPositions(dividerPositions);
            }
            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(pane);
            return pane;
        }
    }
}
