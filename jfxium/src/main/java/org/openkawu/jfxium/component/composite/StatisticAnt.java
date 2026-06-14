package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 统计数值组件 - 对标 Ant Design Statistic（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：展示统计数字（带标题、前缀、后缀、精度控制），
 * 常用于 Dashboard 概览卡片。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>标题 + 数值 + 前缀/后缀（文本或节点）</li>
 *   <li>数值精度控制（precision）</li>
 *   <li>三种尺寸：SMALL / DEFAULT / LARGE</li>
 *   <li>所有视觉样式走 LESS（{@code .jfx-statistic} 系列）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node stat = StatisticAnt.create()
 *     .title("总用户数")
 *     .value("12,456")
 *     .suffix("人")
 *     .size(StatisticAnt.Size.LARGE)
 *     .build();
 * }</pre>
 */
public class StatisticAnt {
    private static final String CONTROLLER_KEY = StatisticAnt.class.getName() + ".controller";

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private String value = "";
        private String prefix = null;
        private String suffix = null;
        private String precision = null;
        private Size size = Size.DEFAULT;
        private Node prefixNode = null;
        private Node suffixNode = null;

        public Builder title(String title) { this.title = title; return this; }
        public Builder value(String value) { this.value = value; return this; }
        public Builder value(double value) { this.value = String.valueOf(value); return this; }
        public Builder value(int value) { this.value = String.valueOf(value); return this; }
        public Builder value(long value) { this.value = String.valueOf(value); return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder prefix(Node prefixNode) { this.prefixNode = prefixNode; return this; }
        public Builder suffix(String suffix) { this.suffix = suffix; return this; }
        public Builder suffix(Node suffixNode) { this.suffixNode = suffixNode; return this; }
        public Builder precision(int precision) { this.precision = String.valueOf(precision); return this; }
        public Builder size(Size size) { this.size = size; return this; }

        public VBox build() {
            VBox statistic = new VBox(4);
            statistic.setAlignment(Pos.CENTER_LEFT);
            statistic.getStyleClass().add(JfxStyles.STATISTIC);
            // 尺寸通过修饰类切换字号
            if (size == Size.SMALL) statistic.getStyleClass().add(JfxStyles.STATISTIC_SMALL);
            else if (size == Size.LARGE) statistic.getStyleClass().add(JfxStyles.STATISTIC_LARGE);

            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add(JfxStyles.STATISTIC_TITLE);
            titleLabel.setVisible(!title.isEmpty());
            titleLabel.setManaged(!title.isEmpty());
            statistic.getChildren().add(titleLabel);

            HBox valueRow = new HBox(4);
            valueRow.setAlignment(Pos.CENTER_LEFT);

            Label prefixLabel = new Label(prefix != null ? prefix : "");
            prefixLabel.getStyleClass().add(JfxStyles.STATISTIC_PREFIX);
            if (prefixNode != null) {
                valueRow.getChildren().add(prefixNode);
            } else {
                prefixLabel.setVisible(prefix != null && !prefix.isEmpty());
                prefixLabel.setManaged(prefix != null && !prefix.isEmpty());
                valueRow.getChildren().add(prefixLabel);
            }

            Label valueLabel = new Label(value);
            valueLabel.getStyleClass().add(JfxStyles.STATISTIC_VALUE);
            valueRow.getChildren().add(valueLabel);

            Label suffixLabel = new Label(suffix != null ? suffix : "");
            suffixLabel.getStyleClass().add(JfxStyles.STATISTIC_SUFFIX);
            if (suffixNode != null) {
                valueRow.getChildren().add(suffixNode);
            } else {
                suffixLabel.setVisible(suffix != null && !suffix.isEmpty());
                suffixLabel.setManaged(suffix != null && !suffix.isEmpty());
                valueRow.getChildren().add(suffixLabel);
            }

            statistic.getChildren().add(valueRow);
            statistic.getProperties().put(CONTROLLER_KEY,
                    new Controller(titleLabel, valueLabel,
                            prefixNode == null ? prefixLabel : null,
                            suffixNode == null ? suffixLabel : null));
            applyStyles(statistic);
            return statistic;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Controller controllerOf(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("StatisticAnt.controllerOf(node) 的 node 不能为 null");
        }
        Object controller = node.getProperties().get(CONTROLLER_KEY);
        if (controller instanceof Controller c) {
            return c;
        }
        throw new IllegalArgumentException("node 不是 StatisticAnt.build() 返回的统计组件");
    }

    public static class Controller {
        private final Label titleLabel;
        private final Label valueLabel;
        private final Label prefixLabel;
        private final Label suffixLabel;

        private Controller(Label titleLabel, Label valueLabel, Label prefixLabel, Label suffixLabel) {
            this.titleLabel = titleLabel;
            this.valueLabel = valueLabel;
            this.prefixLabel = prefixLabel;
            this.suffixLabel = suffixLabel;
        }

        public void setTitle(String title) {
            String text = title != null ? title : "";
            titleLabel.setText(text);
            titleLabel.setVisible(!text.isEmpty());
            titleLabel.setManaged(!text.isEmpty());
        }

        public String getTitle() {
            return titleLabel.getText();
        }

        public void setValue(String value) {
            valueLabel.setText(value != null ? value : "");
        }

        public void setValue(double value) {
            setValue(String.valueOf(value));
        }

        public void setValue(int value) {
            setValue(String.valueOf(value));
        }

        public void setValue(long value) {
            setValue(String.valueOf(value));
        }

        public String getValue() {
            return valueLabel.getText();
        }

        public void setPrefix(String prefix) {
            setOptionalText(prefixLabel, prefix);
        }

        public String getPrefix() {
            return prefixLabel != null ? prefixLabel.getText() : "";
        }

        public void setSuffix(String suffix) {
            setOptionalText(suffixLabel, suffix);
        }

        public String getSuffix() {
            return suffixLabel != null ? suffixLabel.getText() : "";
        }

        private static void setOptionalText(Label label, String text) {
            if (label == null) {
                return;
            }
            String value = text != null ? text : "";
            label.setText(value);
            label.setVisible(!value.isEmpty());
            label.setManaged(!value.isEmpty());
        }
    }
}
