package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * 遮罩层基础组件
 * 微型化设计：只负责遮罩功能，可被 Modal、Drawer 等组合使用
 * 
 * 使用示例：
 * <pre>{@code
 * StackPane overlay = Overlay.create()
 *     .opacity(0.45)
 *     .closable(true)
 *     .onClick(() -> System.out.println("Overlay clicked"))
 *     .build();
 * }</pre>
 */
public class Overlay {
    
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private double opacity = 0.45;
        private boolean closable = true;
        private Runnable onClick = null;

        public Builder opacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder onClick(Runnable onClick) {
            this.onClick = onClick;
            return this;
        }

        public StackPane build() {
            StackPane pane = new StackPane();
            pane.getStyleClass().add(JfxStyles.OVERLAY);
            // 用 JavaFX BackgroundFill 承载 alpha，避免 -fx-opacity 连子节点一起变透明。
            pane.setBackground(new Background(new BackgroundFill(
                    Color.color(0, 0, 0, opacity),
                    CornerRadii.EMPTY,
                    Insets.EMPTY)));
            pane.setAlignment(Pos.CENTER);
            
            if (closable && onClick != null) {
                pane.setOnMouseClicked(e -> {
                    if (e.getTarget() == pane) {
                        onClick.run();
                    }
                });
            }
            
            return pane;
        }
    }
}
