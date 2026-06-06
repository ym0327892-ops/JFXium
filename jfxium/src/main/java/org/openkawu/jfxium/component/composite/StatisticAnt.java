package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

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
            statistic.getStyleClass().add(CssClasses.STATISTIC);
            // 尺寸通过修饰类切换字号
            if (size == Size.SMALL) statistic.getStyleClass().add(CssClasses.STATISTIC_SMALL);
            else if (size == Size.LARGE) statistic.getStyleClass().add(CssClasses.STATISTIC_LARGE);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(CssClasses.STATISTIC_TITLE);
                statistic.getChildren().add(titleLabel);
            }

            HBox valueRow = new HBox(4);
            valueRow.setAlignment(Pos.CENTER_LEFT);

            if (prefixNode != null) {
                valueRow.getChildren().add(prefixNode);
            } else if (prefix != null && !prefix.isEmpty()) {
                Label prefixLabel = new Label(prefix);
                prefixLabel.getStyleClass().add(CssClasses.STATISTIC_PREFIX);
                valueRow.getChildren().add(prefixLabel);
            }

            Label valueLabel = new Label(value);
            valueLabel.getStyleClass().add(CssClasses.STATISTIC_VALUE);
            valueRow.getChildren().add(valueLabel);

            if (suffixNode != null) {
                valueRow.getChildren().add(suffixNode);
            } else if (suffix != null && !suffix.isEmpty()) {
                Label suffixLabel = new Label(suffix);
                suffixLabel.getStyleClass().add(CssClasses.STATISTIC_SUFFIX);
                valueRow.getChildren().add(suffixLabel);
            }

            statistic.getChildren().add(valueRow);
            return statistic;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
