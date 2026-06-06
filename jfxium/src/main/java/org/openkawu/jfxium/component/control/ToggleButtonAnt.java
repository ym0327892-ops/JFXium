package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.component.composite.SwitchAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

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

    /**
     * 创建一个「必选」互斥组（M19.42 #9）—— 至少有一个按钮保持选中，用户无法把当前选中项点成「全不选」。
     *
     * <p>JavaFX 原生 {@link ToggleGroup} 允许点击已选中项使其取消，导致「全不选」状态。
     * admin 的视图切换器（列表/卡片/表格）等场景要求「永远选中一个」，本方法挂一个监听器：
     * 当用户试图取消最后一个选中项时，自动把它选回去。</p>
     *
     * <pre>{@code
     * ToggleGroup viewGroup = ToggleButtonAnt.mandatoryGroup();
     * ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build();
     * ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build();
     * // 点击当前选中项不会取消，必须切到另一个
     * }</pre>
     */
    public static ToggleGroup mandatoryGroup() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            // newToggle == null 说明用户把唯一选中项点掉了 → 选回旧的，保证永远有一个选中
            if (newToggle == null && oldToggle != null) {
                javafx.application.Platform.runLater(() -> group.selectToggle(oldToggle));
            }
        });
        return group;
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
        private BooleanProperty bindProperty = null;

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

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(BooleanProperty property) {
            this.bindProperty = property;
            return this;
        }

        public ToggleButton build() {
            ToggleButton btn = new ToggleButton(text);

            // Size
            if (size == Size.SMALL) {
                btn.getStyleClass().add(JfxStyles.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                btn.getStyleClass().add(JfxStyles.SIZE_LARGE);
            }

            // Shape
            if (rounded) {
                btn.getStyleClass().add(JfxStyles.SHAPE_ROUNDED);
            } else if (square) {
                btn.getStyleClass().add(JfxStyles.SHAPE_SQUARE);
            }

            // Icon
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(contentDisplay);
            }

            btn.setSelected(selected);
            btn.setDisable(disabled);

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                btn.selectedProperty().bindBidirectional(bindProperty);
            }

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
