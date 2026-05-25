package org.openkawu.jfxium.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 切换按钮组件（M19.6）— 包装 JavaFX {@link ToggleButton}。
 *
 * <p><b>定位</b>：具有"按下/弹起"两态切换的按钮（独立或加入 {@link ToggleGroup} 形成互斥组）。
 * 与 {@link ButtonAnt}（一次性触发）和 {@link SwitchAnt}（开关语义）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>编辑器工具条："加粗 / 斜体 / 下划线"独立切换</li>
 *   <li>视图切换：列表 / 网格视图（同一 ToggleGroup 互斥）</li>
 *   <li>过滤开关：勾上"仅显示活跃用户"</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 * <pre>{@code
 * // 独立切换按钮
 * ToggleButton bold = ToggleButtonAnt.create("加粗")
 *     .selected(true)
 *     .onChange(sel -> applyBold(sel))
 *     .build();
 *
 * // 互斥组（与 RadioButton 同模式）
 * ToggleGroup viewGroup = new ToggleGroup();
 * ToggleButton listView = ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build();
 * ToggleButton cardView = ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>API 与 ButtonAnt 镜像（type / size / shape / icon / disabled / onChange）</li>
 *   <li>样式走 LESS {@code .toggle-button} 系列（与 ButtonAnt 共享 button-base 视觉）</li>
 *   <li>选中态 {@code :selected} 由 LESS 控制，无需 Java 拼字符串</li>
 * </ul>
 */
public class ToggleButtonAnt {

    /** 与 ButtonAnt 一致的尺寸枚举。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private Size size = Size.DEFAULT;
        private boolean selected = false;
        private boolean disabled = false;
        private boolean rounded = false;
        private boolean square = false;
        private Node icon;
        private ContentDisplay contentDisplay = ContentDisplay.LEFT;
        private ToggleGroup toggleGroup;
        private EventHandler<ActionEvent> onAction;
        private java.util.function.Consumer<Boolean> onChange;

        private Builder(String text) {
            this.text = text;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder rounded() {
            this.rounded = true;
            this.square = false;
            return this;
        }

        public Builder square() {
            this.square = true;
            this.rounded = false;
            return this;
        }

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder contentDisplay(ContentDisplay display) {
            this.contentDisplay = display;
            return this;
        }

        /** 加入互斥 ToggleGroup（同组内只能选中一个）。 */
        public Builder toggleGroup(ToggleGroup group) {
            this.toggleGroup = group;
            return this;
        }

        public Builder onAction(EventHandler<ActionEvent> handler) {
            this.onAction = handler;
            return this;
        }

        /** 选中状态变化回调（推荐使用）。 */
        public Builder onChange(java.util.function.Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        public ToggleButton build() {
            ToggleButton btn = new ToggleButton(text);

            // Size
            if (size == Size.SMALL) {
                btn.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                btn.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Shape
            if (rounded) {
                btn.getStyleClass().add(CssClasses.SHAPE_ROUNDED);
            } else if (square) {
                btn.getStyleClass().add(CssClasses.SHAPE_SQUARE);
            }

            // Icon
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(contentDisplay);
            }

            btn.setSelected(selected);
            btn.setDisable(disabled);

            if (toggleGroup != null) {
                btn.setToggleGroup(toggleGroup);
            }

            if (onAction != null) {
                btn.setOnAction(onAction);
            }

            if (onChange != null) {
                btn.selectedProperty().addListener((obs, oldVal, newVal) -> onChange.accept(newVal));
            }

            btn.setFocusTraversable(true);
            btn.getStyleClass().add("jfx-toggle-button");
            applyStyles(btn);
            return btn;
        }
    }
}
