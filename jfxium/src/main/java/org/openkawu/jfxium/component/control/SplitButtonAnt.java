package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.builder.Radius;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 分割按钮组件（M19.6）— 包装 JavaFX {@link SplitMenuButton}。
 *
 * <p><b>定位</b>：组合按钮——左侧主体是普通按钮（触发默认动作），右侧带小箭头点击弹出下拉菜单
 * （提供备选动作）。与 {@link MenuButtonAnt}（整体都是 trigger）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>admin "保存 / 保存并新建 / 保存并退出"——主操作 + 备选</li>
 *   <li>编辑器 "运行 / 调试 / 性能分析"——常用主操作 + 同类备选</li>
 *   <li>下载 "立即下载 / 选择路径下载"</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 * <pre>{@code
 * SplitMenuButton save = SplitButtonAnt.create("保存")
 *     .onClick(e -> save())                            // 主按钮点击
 *     .item("保存并新建", e -> saveAndNew())             // 下拉项 1
 *     .item("保存并退出", e -> saveAndExit())            // 下拉项 2
 *     .build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>API 与 MenuButtonAnt 镜像，多了一个 {@code onClick}（主按钮点击事件）</li>
 *   <li>样式走 LESS {@code .split-menu-button} 系列（已有完整规则）</li>
 * </ul>
 */
public class SplitButtonAnt {

    /** 与 ButtonAnt 一致的尺寸枚举。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    /** 箭头样式（M19.6.1）—— 与 MenuButtonAnt 一致。 */
    public enum ArrowStyle {
        CHEVRON,
        TRIANGLE
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
        private ArrowStyle arrowStyle = ArrowStyle.CHEVRON;
        private boolean disabled = false;
        private boolean rounded = false;
        private boolean square = false;
        private Node icon;
        private ContentDisplay contentDisplay = ContentDisplay.LEFT;
        private EventHandler<ActionEvent> onClick;
        private final List<MenuItem> items = new ArrayList<>();

        private Builder(String text) {
            this.text = text;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        /** 箭头样式（M19.6.1，默认 CHEVRON）。SplitButton 必须有箭头，不支持 NONE。 */
        public Builder arrowStyle(ArrowStyle style) {
            this.arrowStyle = style;
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

        /** 主按钮点击事件（左半部分点击触发）。 */
        public Builder onClick(EventHandler<ActionEvent> onClick) {
            this.onClick = onClick;
            return this;
        }

        /** 添加下拉菜单项。 */
        public Builder item(String label, EventHandler<ActionEvent> onClick) {
            MenuItem mi = new MenuItem(label);
            if (onClick != null) {
                mi.setOnAction(onClick);
            }
            items.add(mi);
            return this;
        }

        /** 添加带图标的菜单项。 */
        public Builder item(String label, Node icon, EventHandler<ActionEvent> onClick) {
            MenuItem mi = new MenuItem(label, icon);
            if (onClick != null) {
                mi.setOnAction(onClick);
            }
            items.add(mi);
            return this;
        }

        /** 添加禁用菜单项。 */
        public Builder itemDisabled(String label) {
            MenuItem mi = new MenuItem(label);
            mi.setDisable(true);
            items.add(mi);
            return this;
        }

        /** 添加分隔线。 */
        public Builder separator() {
            items.add(new SeparatorMenuItem());
            return this;
        }

        /** 直接添加原生 MenuItem（高级用法）。 */
        public Builder add(MenuItem item) {
            if (item != null) {
                items.add(item);
            }
            return this;
        }

        public SplitMenuButton build() {
            SplitMenuButton btn = new SplitMenuButton();
            btn.setText(text);
            btn.getItems().addAll(items);
            btn.setDisable(disabled);

            // 保留原默认 SM 圆角行为（100% 等价原实现）
            if (this.radius == null) this.radius = Radius.SM;

            if (onClick != null) {
                btn.setOnAction(onClick);
            }

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

            // Arrow style（M19.6.1）
            if (arrowStyle == ArrowStyle.TRIANGLE) {
                btn.getStyleClass().add("arrow-triangle");
            }

            // Icon
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(contentDisplay);
            }

            btn.setFocusTraversable(true);

            applyStyles(btn);
            return btn;
        }
    }
}
