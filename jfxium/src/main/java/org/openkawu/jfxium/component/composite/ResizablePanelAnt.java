package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.NumericUtils;

/**
 * JFXium 可调整尺寸面板组件（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：可拖拽边缘调整大小的容器，包装 StackPane，
 * 支持水平 / 垂直 / 双向调整。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>调整模式</b>：HORIZONTAL / VERTICAL / BOTH / NONE</li>
 *   <li><b>尺寸约束</b>：prefWidth/prefHeight + minWidth/minHeight + maxWidth/maxHeight</li>
 *   <li>拖拽边缘调整大小（带视觉反馈）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>侧边详情面板（左右拖拽调整宽度）</li>
 *   <li>底部控制台面板（上下拖拽调整高度）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * StackPane panel = ResizablePanelAnt.create()
 *     .content(detailView)
 *     .mode(ResizablePanelAnt.Mode.HORIZONTAL)
 *     .prefWidth(320)
 *     .minWidth(220)
 *     .maxWidth(520)
 *     .build();
 * }</pre>
 */
public class ResizablePanelAnt {

    public enum Mode {
        HORIZONTAL,
        VERTICAL,
        BOTH,
        NONE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node content;
        private Mode mode = Mode.HORIZONTAL;
        private double minWidth = 120;
        private double minHeight = 80;
        private double prefWidth = -1;
        private double prefHeight = -1;
        private double maxWidth = Double.MAX_VALUE;
        private double maxHeight = Double.MAX_VALUE;
        private boolean growX = false;
        private boolean growY = false;

        private Builder() {}

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode != null ? mode : Mode.HORIZONTAL;
            return this;
        }

        public Builder minWidth(double minWidth) {
            this.minWidth = NumericUtils.ensureNonNegative(minWidth);
            return this;
        }

        public Builder minHeight(double minHeight) {
            this.minHeight = NumericUtils.ensureNonNegative(minHeight);
            return this;
        }

        public Builder prefWidth(double prefWidth) {
            this.prefWidth = prefWidth;
            return this;
        }

        public Builder prefHeight(double prefHeight) {
            this.prefHeight = prefHeight;
            return this;
        }

        public Builder maxWidth(double maxWidth) {
            this.maxWidth = NumericUtils.ensureNonNegative(maxWidth);
            return this;
        }

        public Builder maxHeight(double maxHeight) {
            this.maxHeight = NumericUtils.ensureNonNegative(maxHeight);
            return this;
        }

        public Builder grow(boolean grow) {
            this.growX = grow;
            this.growY = grow;
            return this;
        }

        public Builder growX(boolean growX) {
            this.growX = growX;
            return this;
        }

        public Builder growY(boolean growY) {
            this.growY = growY;
            return this;
        }

        public StackPane build() {
            StackPane panel = new StackPane();
            panel.getStyleClass().add(JfxStyles.RESIZABLE_PANEL);
            double safeMaxWidth = Math.max(minWidth, maxWidth);
            double safeMaxHeight = Math.max(minHeight, maxHeight);
            panel.setMinSize(minWidth, minHeight);
            panel.setMaxSize(safeMaxWidth, safeMaxHeight);
            if (prefWidth >= 0) {
                panel.setPrefWidth(prefWidth);
            }
            if (prefHeight >= 0) {
                panel.setPrefHeight(prefHeight);
            }
            if (growX) {
                HBox.setHgrow(panel, Priority.ALWAYS);
            }
            if (growY) {
                VBox.setVgrow(panel, Priority.ALWAYS);
            }

            if (content != null) {
                content.getStyleClass().add(JfxStyles.RESIZABLE_PANEL_CONTENT);
                panel.getChildren().add(content);
            }

            if (mode != Mode.NONE) {
                panel.getChildren().add(createHandle(panel, safeMaxWidth, safeMaxHeight));
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(panel);
            return panel;
        }

        private Node createHandle(StackPane panel, double safeMaxWidth, double safeMaxHeight) {
            StackPane handle = new StackPane();
            handle.getStyleClass().add(JfxStyles.RESIZABLE_PANEL_HANDLE);

            switch (mode) {
                case HORIZONTAL -> {
                    handle.getStyleClass().add(JfxStyles.RESIZABLE_PANEL_HANDLE_HORIZONTAL);
                    handle.setMinWidth(6);
                    handle.setPrefWidth(6);
                    handle.setMaxWidth(6);
                    handle.setMaxHeight(Double.MAX_VALUE);
                    handle.setCursor(Cursor.H_RESIZE);
                    StackPane.setAlignment(handle, Pos.CENTER_RIGHT);
                }
                case VERTICAL -> {
                    handle.getStyleClass().add(JfxStyles.RESIZABLE_PANEL_HANDLE_VERTICAL);
                    handle.setMinHeight(6);
                    handle.setPrefHeight(6);
                    handle.setMaxHeight(6);
                    handle.setMaxWidth(Double.MAX_VALUE);
                    handle.setCursor(Cursor.V_RESIZE);
                    StackPane.setAlignment(handle, Pos.BOTTOM_CENTER);
                }
                case BOTH -> {
                    handle.getStyleClass().add(JfxStyles.RESIZABLE_PANEL_HANDLE_BOTH);
                    handle.setMinSize(12, 12);
                    handle.setPrefSize(12, 12);
                    handle.setMaxSize(12, 12);
                    handle.setCursor(Cursor.SE_RESIZE);
                    StackPane.setAlignment(handle, Pos.BOTTOM_RIGHT);
                }
                case NONE -> {
                }
            }

            final double[] start = new double[4];
            handle.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                start[0] = event.getSceneX();
                start[1] = event.getSceneY();
                start[2] = panel.getWidth();
                start[3] = panel.getHeight();
                event.consume();
            });
            handle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                if (mode == Mode.HORIZONTAL || mode == Mode.BOTH) {
                    double width = NumericUtils.clamp(start[2] + event.getSceneX() - start[0], minWidth, safeMaxWidth, minWidth);
                    panel.setPrefWidth(width);
                }
                if (mode == Mode.VERTICAL || mode == Mode.BOTH) {
                    double height = NumericUtils.clamp(start[3] + event.getSceneY() - start[1], minHeight, safeMaxHeight, minHeight);
                    panel.setPrefHeight(height);
                }
                event.consume();
            });
            return handle;
        }
    }
}
