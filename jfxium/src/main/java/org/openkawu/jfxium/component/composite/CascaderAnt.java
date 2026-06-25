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
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.IconPath;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
            this.value = TextUtils.safeText(value);
            this.label = TextUtils.safeText(label);
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
        // placeholder 复用父类 AbstractStyleBuilder.placeholder 字段（P2-S8 抽取）
        // 默认走 i18n cascader.placeholder
        // disabled 复用父类 AbstractStyleBuilder.disable 字段（P2-S7.4 抽取），
        // 无需自建字段与 setter，直接继承父类 disabled(boolean) / disabled() 即可。
        private boolean allowClear = true;
        private boolean showSearch = false;
        private Consumer<List<String>> onChange = null;
        private List<String> selectedPath = new ArrayList<>();
        private ObjectProperty<List<String>> bindProperty = null;
        private String searchQuery = "";
        private boolean suppressFieldListener = false;

        public Builder options(List<Option> options) { this.options = options != null ? options : new ArrayList<>(); return this; }
        // placeholder(String) 继承自父类 AbstractStyleBuilder（P2-S8 抽取）
        public Builder allowClear(boolean allowClear) { this.allowClear = allowClear; return this; }
        public Builder showSearch(boolean showSearch) { this.showSearch = showSearch; return this; }
        public Builder onChange(Consumer<List<String>> onChange) { this.onChange = onChange; return this; }
        public Builder value(List<String> path) { this.selectedPath = normalizePath(path); return this; }

        /** 双向绑定：控件值（选中的 values 路径）↔ Property 值实时同步。 */
        public Builder bindValue(ObjectProperty<List<String>> property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            if ((selectedPath == null || selectedPath.isEmpty()) && bindProperty != null) {
                selectedPath = resolveLabels(bindProperty.get());
            }

            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(JfxStyles.CASCADER);

            TextField field = new TextField();
            field.setPromptText(hasPlaceholder() ? placeholder : Messages.get("cascader.placeholder"));
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

            // BUG #132 修复：原来 new Label("×") 是裸 Unicode 字符当图标，现走 IconAnt.symbol 统一收口
            Label clearLabel = new Label();
            clearLabel.getStyleClass().add(JfxStyles.CASCADER_CLEAR);
            clearLabel.setGraphic(IconAnt.symbol(IconAnt.Symbol.CLOSE, 12));
            updateClearLabel(clearLabel);
            clearLabel.setOnMouseClicked(e -> {
                if (Boolean.TRUE.equals(disable) || selectedPath.isEmpty()) {
                    return;
                }
                selectedPath = new ArrayList<>();
                searchQuery = "";
                updateFieldText(field);
                rebuildColumns(cascaderPanel, field, popup, clearLabel);
                syncBoundValue(new ArrayList<>());
                if (onChange != null) {
                    onChange.accept(new ArrayList<>());
                }
                updateClearLabel(clearLabel);
                popup.hide();
                e.consume();
            });

            rebuildColumns(cascaderPanel, field, popup, clearLabel);

            field.setOnMouseClicked(e -> {
                if (Boolean.TRUE.equals(disable)) return;
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
            if (showSearch) {
                field.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (suppressFieldListener) {
                        return;
                    }
                    searchQuery = newValue != null ? newValue.trim() : "";
                    rebuildColumns(cascaderPanel, field, popup, clearLabel);
                    if (!searchQuery.isEmpty() && !popup.isShowing()) {
                        Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                        if (bounds != null) {
                            popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                        }
                    }
                });
            }

            container.getChildren().add(field);
            if (allowClear) {
                container.getChildren().add(clearLabel);
            }

            if (Boolean.TRUE.equals(disable)) {
                field.setDisable(true);
                // applyStyles 会同步设置 container 的 disable
            }

            if (bindProperty != null) {
                bindProperty.addListener((obs, oldValue, newValue) -> {
                    selectedPath = resolveLabels(newValue);
                    searchQuery = "";
                    updateFieldText(field);
                    updateClearLabel(clearLabel);
                    rebuildColumns(cascaderPanel, field, popup, clearLabel);
                });
                syncBoundValue(collectSelectedValues());
            }
            applyStyles(container);
            return container;
        }

        private void rebuildColumns(HBox panel, TextField field, Popup popup, Label clearLabel) {
            panel.getChildren().clear();
            appendColumns(panel, displayOptions(), 0, field, popup, clearLabel);
        }

        private void appendColumns(HBox panel, List<Option> currentOptions, int depth, TextField field, Popup popup,
                                   Label clearLabel) {
            if (currentOptions == null || currentOptions.isEmpty()) return;

            VBox column = new VBox(0);
            column.getStyleClass().add(JfxStyles.CASCADER_COLUMN);
            column.setPrefHeight(200);

            for (Option option : currentOptions) {
                HBox item = new HBox();
                item.setAlignment(Pos.CENTER_LEFT);
                item.getStyleClass().add(JfxStyles.CASCADER_ITEM);
                if (option.isDisabled()) {
                    item.getStyleClass().add(JfxStyles.CASCADER_ITEM_DISABLED);
                }

                Label label = new Label(option.getLabel());
                label.getStyleClass().add(JfxStyles.CASCADER_ITEM_LABEL);
                item.getChildren().add(label);

                if (option.hasChildren() && !option.isDisabled()) {
                    SVGPath arrow = IconPath.chevronRightCascader();
                    arrow.getStyleClass().add(JfxStyles.CASCADER_ARROW);
                    Region spacer = new Region();
                    spacer.setMaxWidth(Double.MAX_VALUE);
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
                            searchQuery = "";
                            rebuildColumns(panel, field, popup, clearLabel);
                        } else {
                            List<String> newPath = new ArrayList<>();
                            for (int i = 0; i < depth && i < selectedPath.size(); i++) {
                                newPath.add(selectedPath.get(i));
                            }
                            newPath.add(option.getLabel());
                            selectedPath = newPath;
                            searchQuery = "";
                            updateFieldText(field);
                            updateClearLabel(clearLabel);
                            popup.hide();
                            List<String> values = collectSelectedValues();
                            syncBoundValue(values);
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

                String selectedLabel = TextUtils.safeText(selectedPath.get(depth));
                for (Option option : currentOptions) {
                    if (TextUtils.safeText(option.getLabel()).equals(selectedLabel) && option.hasChildren()) {
                        appendColumns(panel, option.getChildren(), depth + 1, field, popup, clearLabel);
                        break;
                    }
                }
            }
        }

        private boolean collectValues(List<Option> options, List<String> path, int depth, List<String> values) {
            if (depth >= path.size()) return true;
            String targetLabel = TextUtils.safeText(path.get(depth));
            for (Option option : options) {
                if (TextUtils.safeText(option.getLabel()).equals(targetLabel)) {
                    values.add(TextUtils.safeText(option.getValue()));
                    if (option.hasChildren() && depth + 1 < path.size()) {
                        return collectValues(option.getChildren(), path, depth + 1, values);
                    }
                    return true;
                }
            }
            return false;
        }

        private List<String> collectSelectedValues() {
            List<String> values = new ArrayList<>();
            collectValues(options, selectedPath, 0, values);
            return values;
        }

        private List<Option> displayOptions() {
            if (!showSearch || searchQuery == null || searchQuery.isBlank()) {
                return options;
            }
            return filterOptions(options, searchQuery.toLowerCase(Locale.ROOT));
        }

        private List<Option> filterOptions(List<Option> source, String query) {
            List<Option> filtered = new ArrayList<>();
            for (Option option : source) {
                List<Option> childMatches = filterOptions(option.getChildren(), query);
                boolean selfMatches = TextUtils.safeText(option.getLabel()).toLowerCase(Locale.ROOT).contains(query);
                if (selfMatches || !childMatches.isEmpty()) {
                    filtered.add(new Option(option.getValue(), option.getLabel(), childMatches, option.isDisabled()));
                }
            }
            return filtered;
        }

        private List<String> resolveLabels(List<String> values) {
            if (values == null || values.isEmpty()) {
                return new ArrayList<>();
            }
            List<String> labels = new ArrayList<>();
            if (collectLabels(options, values, 0, labels)) {
                return labels;
            }
            return new ArrayList<>();
        }

        private boolean collectLabels(List<Option> options, List<String> values, int depth, List<String> labels) {
            if (depth >= values.size()) {
                return true;
            }
            String targetValue = TextUtils.safeText(values.get(depth));
            for (Option option : options) {
                if (TextUtils.safeText(option.getValue()).equals(targetValue)) {
                    labels.add(TextUtils.safeText(option.getLabel()));
                    if (option.hasChildren() && depth + 1 < values.size()) {
                        return collectLabels(option.getChildren(), values, depth + 1, labels);
                    }
                    return true;
                }
            }
            return false;
        }

        private void updateFieldText(TextField field) {
            suppressFieldListener = true;
            if (selectedPath == null || selectedPath.isEmpty()) {
                field.clear();
            } else {
                field.setText(String.join(" / ", selectedPath));
            }
            suppressFieldListener = false;
        }

        private void updateClearLabel(Label clearLabel) {
            boolean show = allowClear && !selectedPath.isEmpty();
            clearLabel.setVisible(show);
            clearLabel.setManaged(show);
        }

        private void syncBoundValue(List<String> values) {
            if (bindProperty != null) {
                bindProperty.set(values);
            }
        }

        private static List<String> normalizePath(List<String> path) {
            if (path == null || path.isEmpty()) {
                return new ArrayList<>();
            }
            List<String> normalized = new ArrayList<>(path.size());
            for (String item : path) {
                normalized.add(TextUtils.safeText(item));
            }
            return normalized;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
