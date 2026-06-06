package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * FilterBarAnt - admin 列表页"上栏工具条"标准化组件。
 *
 * <p>典型布局：</p>
 * <pre>
 * ┌────────────────────────────────────────────────────────────┐
 * │ [搜索框]  [角色▾] [状态▾] [日期]  ⟶ spacer ⟵  [刷新] [新增+] │
 * └────────────────────────────────────────────────────────────┘
 *   ←──── 左侧筛选区 ────→         ←─── 右侧操作区 ───→
 * </pre>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>左筛选 + 右操作</b>：用 Region spacer 弹性分隔，符合组件组合规范 3.1</li>
 *   <li><b>最小职责</b>：只做布局编排，不绑业务逻辑（搜索/筛选回调由调用方传入）</li>
 *   <li><b>无 inline color</b>：所有颜色走主题变量，符合 SKILL #1</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * HBox bar = FilterBarAnt.create()
 *     .search("搜索用户名/邮箱", 240, kw -> reload(kw))
 *     .filter("角色", roleCombo)        // 任意 Node，常用 ComboBoxAnt
 *     .filter("状态", statusCombo)
 *     .action("刷新", () -> reload())
 *     .actionPrimary("新增", "+", () -> openAddModal())
 *     .build();
 * }</pre>
 */
public class FilterBarAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {

        /** 左侧筛选区子节点（按添加顺序）。 */
        private final List<Node> filters = new ArrayList<>();

        /** 右侧操作区按钮（按添加顺序）。 */
        private final List<Button> actions = new ArrayList<>();

        /** 整栏间距（默认 12）。 */
        private double spacing = 12;

        /** 内边距（默认 0）；通常调用方在外层 padding，FilterBar 自身不留白。 */
        private double padding = 0;

        private Builder() {}

        /** 整栏 spacing。 */
        public Builder spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        /** 整栏 padding。 */
        public Builder padding(double padding) {
            this.padding = padding;
            return this;
        }

        /** 添加搜索框（最常见，单独 API 简化调用）。 */
        public Builder search(String placeholder, double width, Consumer<String> onSearch) {
            TextField input = InputAnt.create()
                    .placeholder(placeholder)
                    .build();
            input.setPrefWidth(width);
            input.setMinWidth(width);
            // 回车触发
            input.setOnAction(e -> {
                if (onSearch != null) onSearch.accept(input.getText());
            });
            filters.add(input);
            return this;
        }

        /** 添加任意筛选控件（ComboBox、DatePicker 等）。带 label 前缀文字。 */
        public Builder filter(String label, Node control) {
            HBox group = new HBox(8);
            group.setAlignment(Pos.CENTER_LEFT);
            if (label != null && !label.isEmpty()) {
                Label l = new Label(label + ":");
                l.getStyleClass().add(JfxStyles.FILTER_BAR_LABEL);
                group.getChildren().add(l);
            }
            group.getChildren().add(control);
            filters.add(group);
            return this;
        }

        /** 添加任意筛选控件（无 label 前缀，直接放）。 */
        public Builder filter(Node control) {
            filters.add(control);
            return this;
        }

        /** 添加默认操作按钮（DEFAULT 类型）。 */
        public Builder action(String text, Runnable onClick) {
            Button btn = ButtonAnt.create(text)
                    .type(ButtonAnt.Type.DEFAULT)
                    .onClick(e -> { if (onClick != null) onClick.run(); })
                    .build();
            actions.add(btn);
            return this;
        }

        /** 添加主操作按钮（PRIMARY 类型，带可选前置文字图标）。 */
        public Builder actionPrimary(String text, String iconPrefix, Runnable onClick) {
            String label = (iconPrefix == null || iconPrefix.isEmpty()) ? text : (iconPrefix + " " + text);
            Button btn = ButtonAnt.create(label)
                    .type(ButtonAnt.Type.PRIMARY)
                    .onClick(e -> { if (onClick != null) onClick.run(); })
                    .build();
            actions.add(btn);
            return this;
        }

        /** 添加主操作按钮（无图标）。 */
        public Builder actionPrimary(String text, Runnable onClick) {
            return actionPrimary(text, null, onClick);
        }

        /** 添加任意自定义按钮节点（特殊场景，比如 IconButton）。 */
        public Builder actionNode(Button btn) {
            actions.add(btn);
            return this;
        }

        public HBox build() {
            HBox bar = new HBox(spacing);
            bar.getStyleClass().add(JfxStyles.FILTER_BAR);
            bar.setAlignment(Pos.CENTER_LEFT);
            if (padding > 0) {
                bar.setPadding(new javafx.geometry.Insets(padding));
            }

            // 1. 左侧筛选区（顺序添加）
            bar.getChildren().addAll(filters);

            // 2. 弹性 spacer 把右侧操作推到最右
            //    用独立 Region + Hgrow（符合组件组合规范 4.1）
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            bar.getChildren().add(spacer);

            // 3. 右侧操作区
            bar.getChildren().addAll(actions);

            // 用户 style/styleClass 在内置类后应用
            applyStyles(bar);
            return bar;
        }
    }
}
