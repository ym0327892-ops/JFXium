package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * SplitBarAnt - 横向三段式（左 / 中 / 右）布局组件。
 *
 * <p><b>定位</b>：admin / dialog / toolbar 高频"hbox 三段式"模式的标准实现。
 * 把 SKILL "组件组合规范" 3.1 节的 Header 三段式从"用户手写"提升为"组件复用"。</p>
 *
 * <h2>三段 vs 二段（自动退化）</h2>
 * <pre>
 *  center 不为空（三段，center 真正居中）：
 *    [ left... ]  spacer  [ center... ]  spacer  [ right... ]
 *
 *  center 为空（二段，等价 ActionBar 的 spacer 模式）：
 *    [ left... ]              spacer              [ right... ]
 * </pre>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li><b>Modal/Drawer header</b>：[关闭按钮] [标题] [extra 操作]</li>
 *   <li><b>Card top</b>：[图标+标题] [tabs] [更多按钮]</li>
 *   <li><b>App header</b>：[Logo] [搜索框] [用户菜单]</li>
 *   <li><b>Dialog footer</b>：[左下次要操作] [] [取消/确认]</li>
 *   <li><b>FilterBar</b>：[筛选条件...] [] [新增/导出]（不传 center 即退化为二段）</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 *
 * <h3>三段：左 + 中 + 右</h3>
 * <pre>{@code
 * HBox header = SplitBarAnt.create()
 *     .left(closeBtn)
 *     .center(titleLabel)
 *     .right(saveBtn, cancelBtn)
 *     .gap(8)
 *     .build();
 * }</pre>
 *
 * <h3>二段：左 + 右（不调 center 即可）</h3>
 * <pre>{@code
 * HBox toolbar = SplitBarAnt.create()
 *     .left(searchField, roleCombo)
 *     .right(refreshBtn, addBtn)
 *     .build();
 * }</pre>
 *
 * <h3>每段多节点（与 CrudTemplate 的 topLeft/topRight 一致）</h3>
 * <pre>{@code
 * HBox bar = SplitBarAnt.create()
 *     .left(icon, titleLabel)        // 图标 + 文字
 *     .right(tag1, tag2, btn)         // 多个尾部节点
 *     .build();
 * }</pre>
 *
 * <h2>设计取舍</h2>
 * <ul>
 *   <li>build() 诚实返回 HBox（不撒谎）</li>
 *   <li>三段时 center <b>真正居中</b>：左/右两侧用独立 Region 做弹性 spacer，宽度对称分配</li>
 *   <li>受左/右宽度挤压时，center 会向较窄的一侧偏移——这是 flex 标准行为</li>
 *   <li>每段都允许多节点（按添加顺序水平排列），不传节点则该段为空但仅占一个 spacer 槽</li>
 *   <li>所有 styleClass / style 由 {@link AbstractStyleBuilder} 统一处理</li>
 * </ul>
 *
 * <p><b>替代关系（M19）</b>：本组件取代了原 {@code ActionBarAnt}（顺序+spacer 模型）和
 * 原 {@code core.util.Headers} 工厂（仅 title+extra 二段）。两者的能力都被 SplitBarAnt
 * 完全覆盖，已于 M19 删除。</p>
 */
public class SplitBarAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> left = new ArrayList<>();
        private final List<Node> center = new ArrayList<>();
        private final List<Node> right = new ArrayList<>();

        private double gap = 8;
        private Pos alignment = Pos.CENTER_LEFT;

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

        // ============================================================
        // 构建
        // ============================================================

        public HBox build() {
            HBox bar = new HBox(gap);
            bar.getStyleClass().add(CssClasses.SPLIT_BAR);
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

            applyStyles(bar);
            return bar;
        }

        /**
         * 创建一个弹性 spacer：HBox 内宽度自动撑满剩余空间。
         * 双重保险（Hgrow + maxWidth）—— 符合组件组合规范 3.1 标准模式。
         */
        private static Region makeSpacer() {
            Region spacer = new Region();
            spacer.getStyleClass().add(CssClasses.SPLIT_BAR_SPACER);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            return spacer;
        }
    }
}
