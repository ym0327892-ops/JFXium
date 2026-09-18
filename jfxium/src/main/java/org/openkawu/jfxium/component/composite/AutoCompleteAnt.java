package org.openkawu.jfxium.component.composite;

import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium 自动完成组件 - 对标 Ant Design AutoComplete。
 *
 * <p><b>定位</b>：输入框 + 智能建议下拉，常用于搜索框、城市选择、
 * 代码补全等场景。支持泛型选项 + 自定义过滤逻辑。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>泛型选项</b>：Builder&lt;T&gt; 支持任意类型选项</li>
 *   <li><b>过滤</b>：输入时自动过滤建议列表</li>
 *   <li><b>自定义渲染</b>：labelFunction 控制选项显示文案</li>
 *   <li><b>回调</b>：onSelect / onChange</li>
 *   <li><b>视觉</b>：弹层走通用 POPUP_MENU 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox autoComplete = AutoCompleteAnt.<String>create()
 *     .placeholder("搜索城市...")
 *     .options(List.of("北京", "上海", "广州", "深圳"))
 *     .labelFunction(s -> s)
 *     .onSelect(city -> System.out.println("选中：" + city))
 *     .build();
 * }</pre>
 */
public class AutoCompleteAnt {

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        // placeholder 复用父类 AbstractStyleBuilder.placeholder 字段（P2-S8 抽取）
        private String value = "";
        private List<T> options = new ArrayList<>();
        private Function<T, String> optionToString = item -> item == null ? "" : String.valueOf(item);
        private Function<String, List<T>> filter = null;
        // disabled 复用父类 AbstractStyleBuilder.disable 字段（P2-S7.4 抽取），
        // 无需自建字段与 setter，直接继承父类 disabled(boolean) / disabled() 即可。
        private Consumer<String> onChange = null;
        private Consumer<T> onSelect = null;
        private int maxSuggestions = 10;
        private StringProperty bindProperty = null;

        public Builder<T> value(String value) { this.value = TextUtils.safeText(value); return this; }
        public Builder<T> options(List<T> options) { this.options = options != null ? options : new ArrayList<>(); return this; }
        public Builder<T> optionToString(Function<T, String> converter) {
            this.optionToString = converter != null ? converter : (item -> item == null ? "" : String.valueOf(item));
            return this;
        }
        public Builder<T> filter(Function<String, List<T>> filter) { this.filter = filter; return this; }
        public Builder<T> onChange(Consumer<String> onChange) { this.onChange = onChange; return this; }
        public Builder<T> onSelect(Consumer<T> onSelect) { this.onSelect = onSelect; return this; }
        public Builder<T> maxSuggestions(int max) { this.maxSuggestions = Math.max(0, max); return this; }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder<T> bindValue(StringProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(JfxStyles.AUTO_COMPLETE);

            TextField field = new TextField(value);
            field.setPromptText(TextUtils.safeText(placeholder));
            field.getStyleClass().add(JfxStyles.AUTO_COMPLETE_FIELD);
            HBox.setHgrow(field, Priority.ALWAYS);

            // 双向绑定（在初始值设置之后）
            Bindings.bindBidirectional(field.textProperty(), bindProperty);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            // Popup 有独立 Scene，不继承宿主节点的主题样式表。
            // 显示时把宿主 Scene 的 stylesheets 注入 Popup Scene，确保暗色等主题下文字/背景颜色正确。
            popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (isShowing && popup.getScene() != null && field.getScene() != null) {
                    popup.getScene().getStylesheets().setAll(field.getScene().getStylesheets());
                }
            });

            VBox suggestionsBox = new VBox(0);
            suggestionsBox.getStyleClass().add(JfxStyles.POPUP_MENU);
            suggestionsBox.setPrefWidth(200);
            popup.getContent().add(suggestionsBox);

            field.textProperty().addListener((obs, oldVal, newVal) -> {
                if (onChange != null) onChange.accept(newVal);
                List<T> filtered = filter != null ? filter.apply(newVal) : filterOptions(newVal);
                updateSuggestions(filtered, suggestionsBox, popup, field);
            });

            field.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) popup.hide();
            });

            field.setOnMouseClicked(e -> {
                if (!field.getText().isEmpty()) {
                    List<T> filtered = filter != null ? filter.apply(field.getText()) : filterOptions(field.getText());
                    updateSuggestions(filtered, suggestionsBox, popup, field);
                }
            });

            container.getChildren().add(field);

            if (Boolean.TRUE.equals(disable)) {
                field.setDisable(true);
                // disabled opacity 走 LESS .jfx-auto-complete:disabled
                // （applyStyles 会同步设置 container 的 disable）
            }
            applyStyles(container);
            return container;
        }

        private List<T> filterOptions(String query) {
            if (query == null || query.isEmpty()) {
                return options.subList(0, Math.min(maxSuggestions, options.size()));
            }
            List<T> filtered = new ArrayList<>();
            String lowerQuery = query.toLowerCase(Locale.ROOT);
            for (T option : options) {
                if (safeOptionText(option).toLowerCase(Locale.ROOT).contains(lowerQuery)) {
                    filtered.add(option);
                    if (filtered.size() >= maxSuggestions) break;
                }
            }
            return filtered;
        }

        private void updateSuggestions(List<T> filtered, VBox suggestionsBox, Popup popup, TextField field) {
            if (filtered == null) {
                filtered = List.of();
            }
            suggestionsBox.getChildren().clear();
            if (filtered.isEmpty()) {
                popup.hide();
                return;
            }

            for (T option : filtered) {
                String text = safeOptionText(option);
                Label label = new Label(text);
                label.getStyleClass().add(JfxStyles.POPUP_MENU_ITEM);
                label.setMaxWidth(Double.MAX_VALUE);
                // hover 由 LESS .jfx-popup-menu-item:hover 控制
                label.setOnMouseClicked(e -> {
                    field.setText(text);
                    popup.hide();
                    if (onSelect != null) onSelect.accept(option);
                });
                suggestionsBox.getChildren().add(label);
            }

            if (!popup.isShowing()) {
                Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                if (bounds != null) {
                    popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                }
            }
        }

        private String safeOptionText(T option) {
            String text = optionToString != null ? optionToString.apply(option) : null;
            return TextUtils.safeText(text);
        }
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }
}
