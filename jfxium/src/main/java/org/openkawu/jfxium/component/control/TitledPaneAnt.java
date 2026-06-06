package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 标题面板组件 - 对标 Ant Design Collapse Panel（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：可折叠的标题 + 内容面板，包装 JavaFX {@link TitledPane}，
 * 可单独使用或组合到 {@link AccordionAnt} 中。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置标题文本 + 内容节点</li>
 *   <li>控制展开/折叠状态（{@code expanded}）</li>
 *   <li>是否允许折叠（{@code collapsible}）</li>
 *   <li>动画开关（{@code animated}）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>单个可折叠详情区域</li>
 *   <li>嵌入 AccordionAnt 作为子面板</li>
 *   <li>表单分组（点击展开高级选项）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * TitledPane pane = TitledPaneAnt.create()
 *     .title("高级设置")
 *     .content(settingsForm)
 *     .expanded(false)       // 默认折叠
 *     .collapsible(true)     // 允许折叠
 *     .animated(true)        // 动画展开
 *     .build();
 * }</pre>
 */
public class TitledPaneAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node content;
        private boolean expanded = true;
        private boolean animated = true;
        private boolean collapsible = true;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder expanded(boolean expanded) {
            this.expanded = expanded;
            return this;
        }

        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public Builder collapsible(boolean collapsible) {
            this.collapsible = collapsible;
            return this;
        }

        public TitledPane build() {
            TitledPane titledPane = new TitledPane(title, content);
            titledPane.setExpanded(expanded);
            titledPane.setAnimated(animated);
            titledPane.setCollapsible(collapsible);
            titledPane.getStyleClass().add("jfx-titled-pane");
            applyStyles(titledPane);
            return titledPane;
        }
    }
}
