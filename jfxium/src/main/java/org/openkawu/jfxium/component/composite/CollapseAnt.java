package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * CollapseAnt - 对标 Ant Design Collapse。
 *
 * 重构：collapse 容器 / panel header / arrow / content / divider 全部走 LESS 选择器，
 * disabled 通过 {@link CssClasses#COLLAPSE_DISABLED} 切换 cursor 与 label 颜色。
 */
public class CollapseAnt {

    public static class Panel {
        private final String key;
        private final String header;
        private final Node content;
        private boolean disabled;

        public Panel(String key, String header, Node content) {
            this.key = key;
            this.header = header;
            this.content = content;
        }

        public Panel disabled(boolean disabled) { this.disabled = disabled; return this; }

        public String getKey() { return key; }
        public String getHeader() { return header; }
        public Node getContent() { return content; }
        public boolean isDisabled() { return disabled; }
    }

    public static class Builder {
        private List<Panel> panels = new ArrayList<>();
        private boolean accordion = false;
        private List<String> activeKeys = new ArrayList<>();

        public Builder panel(String key, String header, Node content) {
            this.panels.add(new Panel(key, header, content));
            return this;
        }

        public Builder panel(String key, String header, Node content, boolean disabled) {
            Panel panel = new Panel(key, header, content);
            panel.disabled(disabled);
            this.panels.add(panel);
            return this;
        }

        public Builder panels(List<Panel> panels) { this.panels = panels; return this; }
        public Builder accordion(boolean accordion) { this.accordion = accordion; return this; }
        public Builder accordion() { return accordion(true); }
        public Builder activeKey(String key) { this.activeKeys.add(key); return this; }
        public Builder activeKeys(List<String> keys) { this.activeKeys = keys; return this; }

        public VBox build() {
            VBox collapse = new VBox(0);
            collapse.getStyleClass().add(CssClasses.COLLAPSE);

            for (int i = 0; i < panels.size(); i++) {
                Panel panel = panels.get(i);
                boolean isActive = activeKeys.contains(panel.getKey());

                VBox panelBox = new VBox(0);
                panelBox.getStyleClass().add(CssClasses.COLLAPSE_PANEL);

                HBox header = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                header.getStyleClass().add(CssClasses.COLLAPSE_HEADER);
                if (panel.isDisabled()) {
                    header.getStyleClass().add(CssClasses.COLLAPSE_DISABLED);
                }

                SVGPath arrow = new SVGPath();
                arrow.setContent("M4 6L8 10L12 6");
                arrow.getStyleClass().add(CssClasses.COLLAPSE_ARROW);
                if (isActive) arrow.setRotate(180);

                Label headerLabel = new Label(panel.getHeader());
                headerLabel.getStyleClass().add(CssClasses.COLLAPSE_HEADER_LABEL);
                header.getChildren().addAll(arrow, headerLabel);

                VBox contentBox = new VBox(0);
                contentBox.getStyleClass().add(CssClasses.COLLAPSE_CONTENT);
                if (panel.getContent() != null) {
                    contentBox.getChildren().add(panel.getContent());
                }
                contentBox.setVisible(isActive);
                contentBox.setManaged(isActive);
                contentBox.setOpacity(isActive ? 1 : 0);

                if (!panel.isDisabled()) {
                    header.setOnMouseClicked(e -> {
                        boolean expanding = !contentBox.isVisible();
                        if (accordion && expanding) {
                            // 关闭其他 panel
                            for (Node node : collapse.getChildren()) {
                                if (node instanceof VBox && node != panelBox) {
                                    VBox otherPanel = (VBox) node;
                                    for (Node child : otherPanel.getChildren()) {
                                        if (child instanceof VBox && child != contentBox) {
                                            VBox otherContent = (VBox) child;
                                            if (otherContent.isVisible()) {
                                                animatePanel(otherContent, false);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        animatePanel(contentBox, expanding);
                        // 箭头旋转动画
                        Timeline arrowAnim = new Timeline(
                                new KeyFrame(Duration.millis(200),
                                        new KeyValue(arrow.rotateProperty(), expanding ? 180 : 0))
                        );
                        arrowAnim.play();
                    });
                }

                panelBox.getChildren().addAll(header, contentBox);

                if (i < panels.size() - 1) {
                    Region divider = new Region();
                    divider.getStyleClass().add(CssClasses.COLLAPSE_DIVIDER);
                    panelBox.getChildren().add(divider);
                }

                collapse.getChildren().add(panelBox);
            }
            return collapse;
        }

        private void animatePanel(VBox contentBox, boolean show) {
            contentBox.setVisible(true);
            contentBox.setManaged(true);
            Timeline timeline = new Timeline();
            if (show) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 0)),
                        new KeyFrame(Duration.millis(200), new KeyValue(contentBox.opacityProperty(), 1))
                );
            } else {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 1)),
                        new KeyFrame(Duration.millis(200), new KeyValue(contentBox.opacityProperty(), 0))
                );
            }
            timeline.setOnFinished(e -> {
                if (!show) {
                    contentBox.setVisible(false);
                    contentBox.setManaged(false);
                }
            });
            timeline.play();
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
