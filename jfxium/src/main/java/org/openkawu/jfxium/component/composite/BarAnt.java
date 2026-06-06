package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * BarAnt - 横向栏布局原子（左 / 中 / 右 三段式）。
 *
 * <p><b>定位</b>：项目最底层的「横向栏」布局原子。header / toolbar / footer / appbar
 * 全部基于它。把「左信息 + 弹性 spacer + 右操作」这一 PC admin 高频模式从「用户手写」
 * 提升为「组件复用」。</p>
 *
 * <p><b>命名说明（M19.52 从 SplitBarAnt 改名）</b>：原名 SplitBarAnt 的「Split」与
 * SplitPane（拖拽分屏）语义冲突——本组件跟「分隔/拖拽」无关，它是「横向栏」。
 * 改名 BarAnt：越底层越该短，三段式是它的用法不是名字。</p>
 *
 * <h2>三段 vs 二段（自动退化）</h2>
 * <pre>
 *  center 不为空（三段，center 真正居中）：
 *    [ left... ]  spacer  [ center... ]  spacer  [ right... ]
 *
 *  center 为空（二段）：
 *    [ left... ]              spacer              [ right... ]
 * </pre>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li><b>Card header</b>：[标题] [] [更多按钮]（配 {@code .padding(8,12,8,12).borderBottom()}）</li>
 *   <li><b>Modal/Drawer header</b>：[标题] [] [关闭按钮]（配 {@code .padding(16,24,16,24).borderBottom()}）</li>
 *   <li><b>App header</b>：[Logo] [搜索框] [用户菜单]</li>
 *   <li><b>Dialog footer</b>：[] [] [取消/确认]（配 {@code .borderTop()}）</li>
 *   <li><b>FilterBar</b>：[筛选条件...] [] [新增/导出]（不传 center 即退化为二段）</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 *
 * <h3>三段：左 + 中 + 右</h3>
 * <pre>{@code
 * HBox header = BarAnt.create()
 *     .left(closeBtn)
 *     .center(titleLabel)
 *     .right(saveBtn, cancelBtn)
 *     .gap(8)
 *     .build();
 * }</pre>
 *
 * <h3>二段：左 + 右（不调 center 即可）</h3>
 * <pre>{@code
 * HBox toolbar = BarAnt.create()
 *     .left(searchField, roleCombo)
 *     .right(refreshBtn, addBtn)
 *     .build();
 * }</pre>
 *
 * <h3>Card header（PC admin 高频）—— 自控高度 + 底部分隔线</h3>
 * <pre>{@code
 * HBox cardHeader = BarAnt.create()
 *     .left(LabelAnt.create("时间范围").build())
 *     .right(settingsBtn)
 *     .padding(8, 12, 8, 12)   // 高度由 padding 自控（PC 思维，见 SKILL PC UI 标准）
 *     .borderBottom()           // 底部 1px 分隔线
 *     .build();
 * }</pre>
 *
 * <h2>设计取舍</h2>
 * <ul>
 *   <li>build() 诚实返回 HBox（不撒谎）</li>
 *   <li>三段时 center <b>真正居中</b>：左/右两侧用独立 Region 做弹性 spacer，宽度对称分配</li>
 *   <li>受左/右宽度挤压时，center 会向较窄的一侧偏移——这是 flex 标准行为</li>
 *   <li>每段都允许多节点（按添加顺序水平排列），不传节点则该段为空但仅占一个 spacer 槽</li>
 *   <li>高度/边距由 {@code .padding(...)} 自控，不由外壳 CSS 钳死（PC UI 标准 §B.3）</li>
 *   <li>所有 styleClass / style 由 {@link AbstractStyleBuilder} 统一处理</li>
 * </ul>
 *
 * <p><b>替代关系</b>：取代了原 {@code ActionBarAnt}（顺序+spacer 模型）和原
 * {@code core.util.Headers} 工厂（仅 title+extra 二段），两者已于 M19 删除。
 * M19.52 从 {@code SplitBarAnt} 改名而来。</p>
 */
public class BarAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> left = new ArrayList<>();
        private final List<Node> center = new ArrayList<>();
        private final List<Node> right = new ArrayList<>();

        private double gap = 8;
        private Pos alignment = Pos.CENTER_LEFT;
        private javafx.geometry.Insets padding = null;
        private double minHeight = -1;
        private double prefHeight = -1;
        private double maxWidth = -1;

        private Builder() {}

        // ============================================================
        // 三段节点
        // ============================================================

        /** 左侧节点（按添加顺序水平排列）。多次调用累加。 */
        public Builder left(Node... nodes) {
            if (nodes != null) {
                for (Node n : nodes) {
                    if (n != null) left.add(n);
                }
            }
            return this;
        }

        /**
         * 中间节点（可选）。
         * <p>不调用或传空数组 → 退化为二段（左 + spacer + 右）。<br>
         * 调用且非空 → 三段（左 + spacer + 中 + spacer + 右），center 真正居中。</p>
         */
        public Builder center(Node... nodes) {
            if (nodes != null) {
                for (Node n : nodes) {
                    if (n != null) center.add(n);
                }
            }
            return this;
        }

        /** 右侧节点（按添加顺序水平排列）。多次调用累加。 */
        public Builder right(Node... nodes) {
            if (nodes != null) {
                for (Node n : nodes) {
                    if (n != null) right.add(n);
                }
            }
            return this;
        }

        // ============================================================
        // 装饰
        // ============================================================

        /** 子节点间距（默认 8）。 */
        public Builder gap(double gap) {
            this.gap = gap;
            return this;
        }

        /** 整体对齐方式（默认 CENTER_LEFT；通常无需修改）。 */
        public Builder alignment(Pos alignment) {
            this.alignment = alignment;
            return this;
        }

        /** 设置统一 padding（四边相同）—— 控制横向栏整体高度/边距。 */
        public Builder padding(double padding) {
            this.padding = new javafx.geometry.Insets(padding);
            return this;
        }

        /** 设置 4 边各自 padding —— 精确控制栏高度（top/bottom 撑高，left/right 缩进）。 */
        public Builder padding(double top, double right, double bottom, double left) {
            this.padding = new javafx.geometry.Insets(top, right, bottom, left);
            return this;
        }

        /** 最小高度（固定高度 toolbar 场景）。 */
        public Builder minHeight(double height) {
            this.minHeight = height;
            return this;
        }

        /** 首选高度。 */
        public Builder prefHeight(double height) {
            this.prefHeight = height;
            return this;
        }

        /** 最大宽度。 */
        public Builder maxWidth(double width) {
            this.maxWidth = width;
            return this;
        }

        // ============================================================
        // 构建
        // ============================================================

        public HBox build() {
            HBox bar = new HBox(gap);
            bar.getStyleClass().add(JfxStyles.SPLIT_BAR);
            bar.setAlignment(alignment);

            // 1. 左段
            bar.getChildren().addAll(left);

            // 2. 第一个 spacer（左 → 中/右）
            bar.getChildren().add(makeSpacer());

            // 3. 中段（可选）+ 第二个 spacer
            if (!center.isEmpty()) {
                bar.getChildren().addAll(center);
                // 加第二个 spacer，让 center 真正居中（左右弹性对称）
                bar.getChildren().add(makeSpacer());
            }

            // 4. 右段
            bar.getChildren().addAll(right);

            // 5. 应用 padding / sizing
            if (padding != null) {
                bar.setPadding(padding);
            }
            if (minHeight >= 0) {
                bar.setMinHeight(minHeight);
            }
            if (prefHeight >= 0) {
                bar.setPrefHeight(prefHeight);
            }
            if (maxWidth >= 0) {
                bar.setMaxWidth(maxWidth);
            }

            applyStyles(bar);
            return bar;
        }

        /**
         * 创建一个弹性 spacer：HBox 内宽度自动撑满剩余空间。
         * 双重保险（Hgrow + maxWidth）—— 符合组件组合规范 3.1 标准模式。
         */
        private static Region makeSpacer() {
            Region spacer = new Region();
            spacer.getStyleClass().add(JfxStyles.SPLIT_BAR_SPACER);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            return spacer;
        }
    }
}
