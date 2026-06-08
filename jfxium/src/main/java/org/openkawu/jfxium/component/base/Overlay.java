package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import org.openkawu.jfxium.core.css.JfxStyles;

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
            // ── 红线#1 必要例外 ────────────────────────────────────────
            // alpha 通道必须在 background-color 内（-fx-opacity 会同时影响
            // 子节点透明度——参见 _component-aux.less:475 注释）。CSS 变量
            // 体系无法支持 "rgba(var(--color), N)" 动态 alpha 替换，且 opacity
            // 是 Builder 入参的连续值（非有限离散值，不能映射为 styleClass）。
            // 此处的 setStyle("-fx-background-color: rgba(...)") 是 JavaFX 技术
            // 限制下的硬豁免，与 AtlantaFX 的 overlay 处理策略一致。
            // ─────────────────────────────────────────────────────────
            pane.getStyleClass().add(JfxStyles.OVERLAY);
            pane.setStyle("-fx-background-color: rgba(0, 0, 0, " + opacity + ");");
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
