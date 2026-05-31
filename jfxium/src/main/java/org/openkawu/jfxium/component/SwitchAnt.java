package org.openkawu.jfxium.component;

import javafx.animation.TranslateTransition;
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
 * JFXium Switch 开关组件 - 对标 Ant Design Switch。
 *
 * <h2>修复说明</h2>
 * 原实现存在 3 处 inline style 注入：
 * <ul>
 *   <li>{@code switchPane.setStyle("-fx-cursor: hand;")} 默认 cursor</li>
 *   <li>{@code switchPane.setStyle("-fx-opacity: 0.5; -fx-cursor: default;")}
 *       第二个 setStyle 整体覆盖第一个，cursor: hand 实际不会失效是因为 build 时
 *       两个分支不会同时进入；但**如果未来支持动态切换 disabled，cursor 会残留为 default**，
 *       是个潜在 bug</li>
 *   <li>{@code statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: ...; -fx-padding: ...")}
 *       字号/颜色/padding 拼字符串</li>
 * </ul>
 * 还有一处死代码：{@code Label textLabel = new Label(); textLabel.setStyle(...)} 创建后从未加入容器。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>所有 styleClass 改用 {@link CssClasses}</li>
 *   <li>新增 {@code .jfx-switch.switch-disabled} 选择器替代 inline 的 opacity+cursor</li>
 *   <li>新增 {@code .jfx-switch-status-label} 选择器替代 inline 的字号/颜色/padding</li>
 *   <li>删除死代码 {@code textLabel}</li>
 *   <li>接入 {@link AbstractStyleBuilder}，统一 style/styleClass 钩子</li>
 *   <li>消除 disabled cursor 残留隐患（用 styleClass 切换，不再 inline 设 setStyle）</li>
 * </ul>
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

            // 用单元素数组持有可变的当前选中态——lambda 内可改（effectively final 限制）
            final boolean[] currentSelected = {selected};

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
                    boolean next = !currentSelected[0];
                    currentSelected[0] = next;          // 关键：更新当前态，下次点击才会正确反转
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
