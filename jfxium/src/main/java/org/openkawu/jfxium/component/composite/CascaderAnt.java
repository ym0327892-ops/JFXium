package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 级联选择器组件 - 对标 Ant Design Cascader。
 *
 * <p><b>定位</b>：多级下拉选择器，常用于省市区三级联动、分类目录选择等场景。
 * 支持多级嵌套 Option。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>多级选项</b>：Option 支持 children 嵌套（无限层级）</li>
 *   <li><b>搜索过滤</b>：输入关键字过滤选项</li>
 *   <li><b>回调</b>：onChange 回传选中的值路径</li>
 *   <li><b>占位符</b>：placeholder(text)</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox cascader = CascaderAnt.create()
 *     .placeholder("选择地区")
 *     .option(new Option("zj", "浙江", List.of(
 *         new Option("hz", "杭州", null),
 *         new Option("nb", "宁波", null))))
 *     .option(new Option("js", "江苏", List.of(
 *         new Option("nj", "南京", null))))
 *     .onChange(path -> System.out.println("选中：" + path))
 *     .build();
 * }</pre>
 */
public class CascaderAnt {

    public static class Option {
        private final String value;
        private final String label;
        private final List<Option> children;
        private final boolean disabled;

        public Option(String value, String label) { this(value, label, null, false); }
        public Option(String value, String label, List<Option> children) { this(value, label, children, false); }
        public Option(String value, String label, List<Option> children, boolean disabled) {
            this.value = value;
            this.label = label;
            this.children = children != null ? children : new ArrayList<>();
            this.disabled = disabled;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public List<Option> getChildren() { return children; }
        public boolean isDisabled() { return disabled; }
        public boolean hasChildren() { return children != null && !children.isEmpty(); }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Option> options = new ArrayList<>();
        private String placeholder = "Please select";
        private boolean disabled = false;
        private boolean allowClear = true;
        private boolean showSearch = false;
        private Consumer<List<String>> onChange = null;
        private List<String> selectedPath = new ArrayList<>();
        private ObjectProperty<List<String>> bindProperty = null;

        public Builder options(List<Option> options) { this.options = options; return this; }
        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder disabled(boolean disabled) { this.disabled = disabled; return this; }
        public Builder disabled() { return disabled(true); }
        public Builder allowClear(boolean allowClear) { this.allowClear = allowClear; return this; }
        public Builder showSearch(boolean showSearch) { this.showSearch = showSearch; return this; }
        public Builder onChange(Consumer<List<String>> onChange) { this.onChange = onChange; return this; }
        public Builder value(List<String> path) { this.selectedPath = path != null ? path : new ArrayList<>(); return this; }

        /** 双向绑定：控件值（选中的 values 路径）↔ Property 值实时同步。 */
        public Builder bindValue(ObjectProperty<List<String>> property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(JfxStyles.CASCADER);

            TextField field = new TextField();
            field.setPromptText(placeholder);
            field.setEditable(showSearch);
            field.getStyleClass().add(JfxStyles.CASCADER_FIELD);
            HBox.setHgrow(field, Priority.ALWAYS);

            if (!selectedPath.isEmpty()) {
                field.setText(String.join(" / ", selectedPath));
            }

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            HBox cascaderPanel = new HBox(0);
            cascaderPanel.getStyleClass().add(JfxStyles.POPUP_MENU);
            popup.getContent().add(cascaderPanel);

            buildColumns(cascaderPanel, options, 0, field, popup);

            field.setOnMouseClicked(e -> {
                if (disabled) return;
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                    popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                }
            });
            field.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) popup.hide();
            });

            container.getChildren().add(field);

            if (disabled) {
                field.setDisable(true);
                container.setDisable(true);
            }
            return container;
        }

        private void buildColumns(HBox panel, List<Option> currentOptions, int depth, TextField field, Popup popup) {
            panel.getChildren().clear();
            if (currentOptions == null || currentOptions.isEmpty()) return;

            VBox column = new VBox(0);
            column.getStyleClass().add(JfxStyles.CASCADER_COLUMN);
            column.setPrefHeight(200);

            for (Option option : currentOptions) {
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                item.getStyleClass().add(JfxStyles.CASCADER_ITEM);
                if (option.isDisabled()) {
                    item.getStyleClass().add(JfxStyles.CASCADER_ITEM_DISABLED);
                }

                Label label = new Label(option.getLabel());
                label.getStyleClass().add(JfxStyles.CASCADER_ITEM_LABEL);
                item.getChildren().add(label);

                if (option.hasChildren() && !option.isDisabled()) {
                    SVGPath arrow = new SVGPath();
                    arrow.setContent("M6 4L10 8L6 12");
                    arrow.getStyleClass().add(JfxStyles.CASCADER_ARROW);
                    HBox spacer = new HBox();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    item.getChildren().addAll(spacer, arrow);
                }

                if (!option.isDisabled()) {
                    item.setOnMouseClicked(e -> {
                        if (option.hasChildren()) {
                            List<String> newPath = new ArrayList<>(selectedPath);
                            if (depth < newPath.size()) {
                                newPath = newPath.subList(0, depth);
                            }
                            newPath.add(option.getLabel());
                            selectedPath = newPath;
                            buildColumns(panel, options, 0, field, popup);
                        } else {
                            List<String> newPath = new ArrayList<>();
                            for (int i = 0; i < depth && i < selectedPath.size(); i++) {
                                newPath.add(selectedPath.get(i));
                            }
                            newPath.add(option.getLabel());
                            selectedPath = newPath;
                            field.setText(String.join(" / ", selectedPath));
                            popup.hide();
                            List<String> values = new ArrayList<>();
                            collectValues(options, selectedPath, 0, values);
                            if (bindProperty != null) {
                                bindProperty.set(values);
                            }
                            if (onChange != null) {
                                onChange.accept(values);
                            }
                        }
                    });
                }
                column.getChildren().add(item);
            }
            panel.getChildren().add(column);

            // 根据 selectedPath 递归展开下一级
            if (!selectedPath.isEmpty() && depth < selectedPath.size()) {
                Region divider = new Region();
                divider.getStyleClass().add(JfxStyles.CASCADER_DIVIDER);
                panel.getChildren().add(divider);

                String selectedLabel = selectedPath.get(depth);
                for (Option option : currentOptions) {
                    if (option.getLabel().equals(selectedLabel) && option.hasChildren()) {
                        buildColumns(panel, option.getChildren(), depth + 1, field, popup);
                        break;
                    }
                }
            }
        }

        private boolean collectValues(List<Option> options, List<String> path, int depth, List<String> values) {
            if (depth >= path.size()) return true;
            String targetLabel = path.get(depth);
            for (Option option : options) {
                if (option.getLabel().equals(targetLabel)) {
                    values.add(option.getValue());
                    if (option.hasChildren() && depth + 1 < path.size()) {
                        return collectValues(option.getChildren(), path, depth + 1, values);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
