package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 提及组件 - 对标 Ant Design Mentions（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：输入时 @ 提及用户/对象的文本城，包装 JavaFX {@link TextArea} + {@link Popup}，
 * 输入触发符（默认 {@code @}）后弹出候选列表，选中后自动插入。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>自定义触发符（默认 {@code @}，可改为 {@code #} / {@code $} 等）</li>
 *   <li>候选项配置（value + label）</li>
 *   <li>选中回调（{@code onSelect}）+ 文本变化回调（{@code onChange}）</li>
 *   <li>自定义占位文本 + 行数</li>
 *   <li>所有视觉样式走 LESS（{@link JfxStyles#MENTIONS} + {@link JfxStyles#POPUP_MENU}）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>评论框 @ 用户</li>
 *   <li>任务分配 @ 负责人</li>
 *   <li>聊天输入 @ 群成员</li>
 *   <li>代码注释 # 标签引用</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * TextArea mentions = MentionsAnt.create()
 *     .placeholder("输入 @ 提及用户...")
 *     .prefix("@")
 *     .option("zhangsan", "张三")
 *     .option("lisi", "李四")
 *     .option("wangwu", "王五")
 *     .onSelect(value -> System.out.println("选中了：" + value))
 *     .rows(4)
 *     .build();
 * }</pre>
 */
public class MentionsAnt {

    public static class Option {
        private final String value;
        private final String label;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String placeholder = "";
        private List<Option> options = new ArrayList<>();
        private String prefix = "@";
        private Consumer<String> onSelect = null;
        private Consumer<String> onChange = null;
        private int rows = 4;
        private StringProperty bindProperty = null;

        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder option(String value, String label) { this.options.add(new Option(value, label)); return this; }
        public Builder options(List<Option> options) { this.options = options; return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder onSelect(Consumer<String> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onChange(Consumer<String> onChange) { this.onChange = onChange; return this; }
        public Builder rows(int rows) { this.rows = rows; return this; }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(StringProperty property) {
            this.bindProperty = property;
            return this;
        }

        public TextArea build() {
            TextArea textArea = new TextArea();
            textArea.getStyleClass().addAll(JfxStyles.MENTIONS, JfxStyles.MENTIONS_AREA);
            textArea.setPromptText(placeholder);
            textArea.setPrefRowCount(rows);

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                textArea.textProperty().bindBidirectional(bindProperty);
            }

            Popup popup = new Popup();
            popup.setAutoHide(true);

            VBox optionsPanel = new VBox(0);
            optionsPanel.getStyleClass().add(JfxStyles.POPUP_MENU);
            optionsPanel.setPrefWidth(200);
            popup.getContent().add(optionsPanel);

            textArea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (onChange != null) onChange.accept(newVal);

                if (newVal.endsWith(prefix)) {
                    optionsPanel.getChildren().clear();
                    for (Option option : options) {
                        HBox row = new HBox(8);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.getStyleClass().add(JfxStyles.POPUP_MENU_ITEM);
                        Label label = new Label(option.getLabel());
                        row.getChildren().add(label);
                        // hover 由 LESS 控制
                        row.setOnMouseClicked(e -> {
                            String currentText = textArea.getText();
                            String newText = currentText.substring(0, currentText.length() - prefix.length())
                                    + prefix + option.getValue() + " ";
                            textArea.setText(newText);
                            textArea.positionCaret(newText.length());
                            popup.hide();
                            if (onSelect != null) onSelect.accept(option.getValue());
                        });
                        optionsPanel.getChildren().add(row);
                    }

                    if (!options.isEmpty()) {
                        Bounds bounds = textArea.localToScreen(textArea.getBoundsInLocal());
                        popup.show(textArea, bounds.getMinX(), bounds.getMaxY() + 4);
                    }
                } else if (!newVal.contains(prefix)) {
                    popup.hide();
                }
            });

            return textArea;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
