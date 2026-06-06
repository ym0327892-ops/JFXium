package org.openkawu.jfxium.component.composite;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * JFXium 开关组件 - 对标 Ant Design Switch（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：二态切换开关（开/关），用于切换某个配置项的启用状态，
 * 与 CheckBoxAnt（多选语义）和 ToggleButtonAnt（按钮语义）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / LARGE</li>
 *   <li><b>状态文本</b>：checkedText / uncheckedText（开/关文字）</li>
 *   <li><b>禁用</b>：disabled 模式</li>
 *   <li><b>加载</b>：loading 状态</li>
 *   <li><b>变化回调</b>：onChange 监听开关状态</li>
 *   <li>所有视觉样式走 LESS（{@code .jfx-switch} 系列）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node sw = SwitchAnt.create()
 *     .checked(true)
 *     .size(SwitchAnt.Size.SMALL)
 *     .checkedText("开")
 *     .uncheckedText("关")
 *     .onChange(checked -> System.out.println("开关：" + checked))
 *     .build();
 * }</pre>
 */
public class SwitchAnt {

    public static Builder create() {
        return new Builder();
    }

    /**
     * 形状枚举（M19.20）。
     * <ul>
     *   <li>{@link #PILL}：胶囊形（Ant Design 默认）</li>
     *   <li>{@link #ROUNDED}：圆角矩形（介于胶囊和直角之间）</li>
     *   <li>{@link #SQUARE}：直角矩形（极简风格）</li>
     * </ul>
     */
    public enum Shape {
        PILL,
        ROUNDED,
        SQUARE
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private boolean selected = false;
        private boolean disabled = false;
        private String checkedText = "";
        private String uncheckedText = "";
        private Shape shape = Shape.PILL;
        private Consumer<Boolean> onChange;
        private BooleanProperty bindProperty = null;

        private Builder() {}

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder checkedText(String text) {
            this.checkedText = text;
            return this;
        }

        public Builder uncheckedText(String text) {
            this.uncheckedText = text;
            return this;
        }

        /**
         * 设置形状（M19.20）。默认 {@link Shape#PILL}（胶囊形，对齐 Ant Design）。
         */
        public Builder shape(Shape shape) {
            this.shape = shape != null ? shape : Shape.PILL;
            return this;
        }

        public Builder onChange(Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(BooleanProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(CssClasses.SWITCH_CONTAINER);

            // 轨道、滑块容器、滑块尺寸固定，视觉细节（圆角、颜色、cursor）走 LESS
            Region track = new Region();
            track.setMinSize(44, 22);
            track.setMaxSize(44, 22);
            track.setPrefSize(44, 22);
            track.getStyleClass().add(CssClasses.SWITCH_TRACK);

            StackPane thumbContainer = new StackPane();
            thumbContainer.setMinSize(44, 22);
            thumbContainer.setMaxSize(44, 22);
            thumbContainer.setPrefSize(44, 22);
            thumbContainer.setAlignment(Pos.CENTER_LEFT);

            Region thumb = new Region();
            thumb.setMinSize(18, 18);
            thumb.setMaxSize(18, 18);
            thumb.setPrefSize(18, 18);
            thumb.getStyleClass().add(CssClasses.SWITCH_THUMB);
            thumb.setLayoutX(2);

            thumbContainer.getChildren().add(thumb);

            StackPane switchPane = new StackPane(track, thumbContainer);
            switchPane.setMinSize(44, 22);
            switchPane.setMaxSize(44, 22);
            switchPane.setPrefSize(44, 22);
            switchPane.getStyleClass().add(CssClasses.SWITCH);

            // 用 BooleanProperty 持有可变的当前选中态（支持双向绑定）
            final SimpleBooleanProperty valueProperty = new SimpleBooleanProperty(selected);
            if (bindProperty != null) {
                valueProperty.bindBidirectional(bindProperty);
            }

            if (selected) {
                switchPane.getStyleClass().add(CssClasses.SWITCH_SELECTED);
                // 关键：初始就选中时，thumb 要直接放到右侧（translateX=24），否则蓝轨道配左侧 thumb 视觉错乱
                thumb.setTranslateX(24);
            }
            // 禁用态用 styleClass 切换，避免 inline setStyle 在动态场景下残留 cursor
            if (disabled) {
                switchPane.getStyleClass().add(CssClasses.SWITCH_DISABLED);
            }
            // Shape 修饰类（M19.20）—— PILL 默认不挂；ROUNDED/SQUARE 挂修饰类切换圆角
            switch (shape) {
                case ROUNDED -> switchPane.getStyleClass().add("shape-rounded");
                case SQUARE  -> switchPane.getStyleClass().add("shape-square");
                default      -> { /* PILL 不挂额外类 */ }
            }

            switchPane.setOnMouseClicked(e -> {
                if (!disabled) {
                    boolean next = !valueProperty.get();
                    valueProperty.set(next);
                    toggle(switchPane, thumb, next);
                    if (onChange != null) {
                        onChange.accept(next);
                    }
                }
            });

            HBox.setHgrow(switchPane, Priority.NEVER);
            container.getChildren().add(switchPane);

            // 状态文本：仅当用户配置了 checkedText/uncheckedText 才创建
            if (!checkedText.isEmpty() || !uncheckedText.isEmpty()) {
                Label statusLabel = new Label(selected ? checkedText : uncheckedText);
                statusLabel.getStyleClass().add(CssClasses.SWITCH_STATUS_LABEL);
                // 用 properties 标记，方便 toggle 时找到这个 Label 来更新文本
                statusLabel.getProperties().put("switchLabel", true);
                container.getChildren().add(statusLabel);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(container);
            return container;
        }

        private void toggle(StackPane switchPane, Region thumb, boolean isSelected) {
            if (isSelected) {
                switchPane.getStyleClass().add(CssClasses.SWITCH_SELECTED);
            } else {
                switchPane.getStyleClass().remove(CssClasses.SWITCH_SELECTED);
            }

            // 从当前实际位置滑到目标位置（不写死 from，避免与初始 translateX 冲突）
            TranslateTransition slide = new TranslateTransition(Duration.millis(200), thumb);
            slide.setToX(isSelected ? 24 : 0);
            slide.play();

            // 通过 properties 标记定位状态文本，更新成新状态对应的文字
            Label statusLabel = null;
            for (javafx.scene.Node child : switchPane.getParent().getChildrenUnmodifiable()) {
                if (child instanceof Label && child.getProperties().containsKey("switchLabel")) {
                    statusLabel = (Label) child;
                    break;
                }
            }
            if (statusLabel != null) {
                statusLabel.setText(isSelected ? checkedText : uncheckedText);
            }
        }
    }
}
