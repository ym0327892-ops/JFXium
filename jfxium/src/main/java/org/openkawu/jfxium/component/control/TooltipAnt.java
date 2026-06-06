package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 工具提示组件 - 对标 Ant Design Tooltip（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：悬浮提示气泡，包装 JavaFX {@link Tooltip}，
 * 鼠标悬浮在目标节点上时显示提示文本。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置提示文本</li>
 *   <li>控制显示延迟（{@code delay}，默认 200ms）</li>
 *   <li>控制显示时长（{@code duration}，默认 10s）</li>
 *   <li>控制消失延迟（{@code hideDelay}，默认 200ms）</li>
 *   <li>{@code install(node)} 一键安装到目标节点</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>图标按钮说明（图标无文本时提示功能）</li>
 *   <li>表格列标题解释（悬停显示列含义）</li>
 *   <li>禁用按钮原因说明（为什么不能点击）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 一键安装到按钮
 * Button btn = ButtonAnt.create("悬停我").build();
 * TooltipAnt.create("这是提示文本")
 *     .delay(Duration.millis(300))
 *     .install(btn);
 *
 * // 先 build 再手动安装
 * Tooltip tip = TooltipAnt.create("快捷键：Ctrl+S")
 *     .build();
 * Tooltip.install(saveButton, tip);
 * }</pre>
 */
public class TooltipAnt {

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private Duration showDelay = Duration.millis(200);
        private Duration showDuration = Duration.seconds(10);
        private Duration hideDelay = Duration.millis(200);

        private Builder(String text) {
            this.text = text;
        }

        public Builder delay(Duration delay) {
            this.showDelay = delay;
            return this;
        }

        public Builder duration(Duration duration) {
            this.showDuration = duration;
            return this;
        }

        public Builder hideDelay(Duration delay) {
            this.hideDelay = delay;
            return this;
        }

        public Tooltip build() {
            Tooltip tooltip = new Tooltip(text);
            tooltip.setShowDelay(showDelay);
            tooltip.setShowDuration(showDuration);
            tooltip.setHideDelay(hideDelay);
            tooltip.getStyleClass().add("jfx-tooltip");
            // Tooltip 是 Styleable 但不是 Node。applyStyles(Styleable) 只挂 styleClass，
            // inline style 由这里读取 protected 字段 style 后自己 setStyle。
            applyStyles((javafx.css.Styleable) tooltip);
            String inline = getStyle();
            if (!inline.isEmpty()) {
                tooltip.setStyle(inline);
            }
            return tooltip;
        }

        public void install(Node node) {
            Tooltip.install(node, build());
        }
    }
}
