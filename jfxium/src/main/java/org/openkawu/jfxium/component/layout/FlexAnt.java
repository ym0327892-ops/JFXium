package org.openkawu.jfxium.component.layout;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXium 弹性布局组件 - 对标 Ant Design Flex / CSS Flexbox
 *
 * <h2>设计说明</h2>
 * 本组件原实现存在 3 个核心问题：
 * <ul>
 *   <li>{@code build()} 声明返回 {@code HBox}，但 COLUMN 方向时实际返回包装的 HBox(VBox)，
 *       API 类型与实际行为不一致</li>
 *   <li>{@code wrap}、{@code rowGap}、{@code columnGap} 字段定义了 setter 但 build() 中从未使用</li>
 *   <li>{@code BETWEEN/AROUND/EVENLY} 实现等同于"等分"，与 CSS Flexbox 语义不一致</li>
 * </ul>
 *
 * <h2>本次重写约定</h2>
 * <ul>
 *   <li>{@code build()} 返回 {@link Pane}，根据配置可能是 {@link HBox}/{@link VBox}/{@link FlowPane}，
 *       不再撒谎</li>
 *   <li>{@code wrap=true} 时使用 {@link FlowPane}，自动支持 {@code rowGap}/{@code columnGap}</li>
 *   <li>{@code justify=BETWEEN/AROUND/EVENLY} 通过插入弹性 {@link Region} 实现，
 *       对齐 CSS Flexbox 标准语义</li>
 *   <li>所有样式通过 {@link JfxStyles} 常量挂载，不再使用 {@code setStyle} 注入</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 水平排列，主轴居中、交叉轴居中
 * Pane flex = FlexAnt.create()
 *     .gap(16)
 *     .justify(Justify.CENTER)
 *     .align(Align.CENTER)
 *     .children(node1, node2, node3)
 *     .build();
 *
 * // 垂直排列
 * Pane flexV = FlexAnt.createVertical()
 *     .gap(8)
 *     .children(node1, node2)
 *     .build();
 *
 * // 自动换行（返回 FlowPane）
 * Pane flexWrap = FlexAnt.create()
 *     .wrap(true)
 *     .columnGap(12)
 *     .rowGap(8)
 *     .children(tags)
 *     .build();
 *
 * // 两端对齐（导航栏左右分布）
 * Pane navbar = FlexAnt.create()
 *     .justify(Justify.BETWEEN)
 *     .align(Align.CENTER)
 *     .children(logo, menu, userInfo)
 *     .build();
 * }</pre>
 */
public class FlexAnt {

    /** 主轴对齐（对齐 CSS justify-content）*/
    public enum Justify {
        START, END, CENTER,
        /** 两端贴边、中间均分 */
        BETWEEN,
        /** 每个元素两侧都有等量留白（首尾留白为间隔的一半）*/
        AROUND,
        /** 每个元素两侧留白完全相等 */
        EVENLY
    }

    /** 交叉轴对齐（对齐 CSS align-items）。BASELINE 在 JavaFX 仅对 ROW 方向有效。*/
    public enum Align {
        START, END, CENTER, STRETCH, BASELINE
    }

    /** 主轴方向 */
    public enum Direction {
        ROW, ROW_REVERSE, COLUMN, COLUMN_REVERSE
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder createVertical() {
        return new Builder().direction(Direction.COLUMN);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> children = new ArrayList<>();
        private Direction direction = Direction.ROW;
        private Justify justify = Justify.START;
        private Align align = Align.CENTER;
        private boolean wrap = false;
        private boolean growRequested = false;
        private double gap = 0;
        private double rowGap = -1;     // -1 表示未单独设置，回退到 gap
        private double columnGap = -1;  // 同上

        private Builder() {}

        public Builder direction(Direction direction) {
            this.direction = direction == null ? Direction.ROW : direction;
            return this;
        }

        public Builder justify(Justify justify) {
            this.justify = justify == null ? Justify.START : justify;
            return this;
        }

        public Builder align(Align align) {
            this.align = align == null ? Align.CENTER : align;
            return this;
        }

        /** 启用自动换行。启用后 build() 返回 {@link FlowPane}。*/
        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        public Builder wrap() {
            return wrap(true);
        }

        /**
         * 主轴/交叉轴统一间距。等价同时设置 rowGap 和 columnGap。
         * 单独 rowGap/columnGap 设置后会覆盖此值（仅在 wrap 模式下区分）。
         */
        public Builder gap(double gap) {
            this.gap = Double.isFinite(gap) ? Math.max(0, gap) : 0;
            return this;
        }

        /** 行间距，仅在 wrap=true 时生效（FlowPane 才区分行列间距）。*/
        public Builder rowGap(double rowGap) {
            this.rowGap = Double.isFinite(rowGap) ? Math.max(0, rowGap) : 0;
            return this;
        }

        /** 列间距，仅在 wrap=true 时生效。*/
        public Builder columnGap(double columnGap) {
            this.columnGap = Double.isFinite(columnGap) ? Math.max(0, columnGap) : 0;
            return this;
        }

        public Builder children(Node... nodes) {
            if (nodes != null) {
                for (Node node : nodes) {
                    if (node != null) {
                        this.children.add(node);
                    }
                }
            }
            return this;
        }

        /** 添加单个子节点，可声明是否在主轴方向上"伸展占据剩余空间"（grow）。*/
        public Builder child(Node node, boolean grow) {
            if (node == null) return this;
            this.children.add(node);
            if (grow) {
                growRequested = true;
                // 同时设置 H/V grow 是为了让该子节点在任何方向上都生效，
                // 不需要等到知道 direction 才能决定。无副作用。
                HBox.setHgrow(node, Priority.ALWAYS);
                VBox.setVgrow(node, Priority.ALWAYS);
            }
            return this;
        }

        /**
         * 构建容器。返回类型为 {@link Pane}，实际可能是：
         * <ul>
         *   <li>{@link FlowPane} —— wrap=true</li>
         *   <li>{@link VBox} —— direction 为 COLUMN/COLUMN_REVERSE 且 wrap=false</li>
         *   <li>{@link HBox} —— direction 为 ROW/ROW_REVERSE 且 wrap=false</li>
         * </ul>
         */
        public Pane build() {
            if (wrap && growRequested) {
                throw new IllegalStateException(
                        "FlexAnt.child(node, true) 与 wrap(true) 不能同时使用；" +
                                "FlowPane 不支持主轴 grow，请关闭 wrap 或移除 grow。");
            }

            // 反转方向通过反转 children 列表实现，简单且不依赖 JavaFX 内部
            List<Node> orderedChildren = isReversed() ? reverse(children) : children;

            Pane container;
            if (wrap) {
                container = buildFlowPane(orderedChildren);
            } else if (isVertical()) {
                container = buildVBox(orderedChildren);
            } else {
                container = buildHBox(orderedChildren);
            }

            applyStyles(container);
            return container;
        }

        // ===========================================================
        // 三种容器的具体构建逻辑
        // ===========================================================

        private FlowPane buildFlowPane(List<Node> ordered) {
            // FlowPane 在 wrap 模式下天然支持 rowGap/columnGap，是最贴合 Ant Flex wrap 行为的容器
            FlowPane flow = new FlowPane();
            flow.setOrientation(isVertical()
                    ? javafx.geometry.Orientation.VERTICAL
                    : javafx.geometry.Orientation.HORIZONTAL);
            flow.setHgap(effectiveColumnGap());
            flow.setVgap(effectiveRowGap());

            // FlowPane 的对齐用 alignment + columnHalignment/rowValignment 组合
            flow.setAlignment(toFlowPos());
            if (isVertical()) {
                flow.setColumnHalignment(toHPos(align));
            } else {
                flow.setRowValignment(toVPos(align));
            }

            flow.getChildren().addAll(ordered);
            flow.getStyleClass().addAll(JfxStyles.FLEX, JfxStyles.FLEX_WRAP);
            return flow;
        }

        private HBox buildHBox(List<Node> ordered) {
            HBox box = new HBox(isMainAxisDistributed() ? 0 : gap);
            // BETWEEN/AROUND/EVENLY 通过插入 spacer 实现，剩余对齐通过 setAlignment 处理
            List<Node> withSpacers = injectSpacersIfNeeded(ordered, true);
            box.getChildren().addAll(withSpacers);
            box.setAlignment(resolveAlignmentForBox(true));
            box.setFillHeight(align == Align.STRETCH);
            box.getStyleClass().addAll(JfxStyles.FLEX, JfxStyles.FLEX_HORIZONTAL);
            return box;
        }

        private VBox buildVBox(List<Node> ordered) {
            VBox box = new VBox(isMainAxisDistributed() ? 0 : gap);
            List<Node> withSpacers = injectSpacersIfNeeded(ordered, false);
            box.getChildren().addAll(withSpacers);
            box.setAlignment(resolveAlignmentForBox(false));
            box.setFillWidth(align == Align.STRETCH);
            box.getStyleClass().addAll(JfxStyles.FLEX, JfxStyles.FLEX_VERTICAL);
            return box;
        }

        // ===========================================================
        // BETWEEN/AROUND/EVENLY 的 spacer 注入
        // ===========================================================

        /**
         * 当 justify 是 BETWEEN/AROUND/EVENLY 时，往子节点列表中插入弹性 spacer，
         * 模拟 CSS Flexbox 的标准语义。这里 spacer 是 {@link Region}，
         * 通过 HBox/VBox.setHgrow/Vgrow=ALWAYS 在主轴上"吸收剩余空间"。
         *
         * @param ordered    已按方向反转后的子节点列表
         * @param horizontal true 表示水平容器（HBox），false 表示垂直容器（VBox）
         */
        private List<Node> injectSpacersIfNeeded(List<Node> ordered, boolean horizontal) {
            if (justify != Justify.BETWEEN && justify != Justify.AROUND && justify != Justify.EVENLY) {
                return ordered;
            }
            if (ordered.size() <= 1) {
                // 单个子节点用 alignment 处理就够了，无需 spacer
                return ordered;
            }

            List<Node> result = new ArrayList<>();
            boolean leadingSpacer = justify == Justify.AROUND || justify == Justify.EVENLY;
            boolean trailingSpacer = leadingSpacer;
            double edgeBasis = justify == Justify.AROUND ? gap / 2.0 : gap;
            double innerBasis = gap;

            if (leadingSpacer) {
                result.add(makeSpacer(horizontal, edgeBasis));
            }
            for (int i = 0; i < ordered.size(); i++) {
                result.add(ordered.get(i));
                if (i < ordered.size() - 1) {
                    result.add(makeSpacer(horizontal, innerBasis));
                }
            }
            if (trailingSpacer) {
                result.add(makeSpacer(horizontal, edgeBasis));
            }
            return result;
        }

        private Region makeSpacer(boolean horizontal, double basisSize) {
            Region spacer = new Region();
            double effectiveBasis = Math.max(0, basisSize);
            // 在主轴方向上 ALWAYS 吸收空间，交叉轴方向不需要管
            if (horizontal) {
                spacer.setMinWidth(effectiveBasis);
                spacer.setPrefWidth(effectiveBasis);
                HBox.setHgrow(spacer, Priority.ALWAYS);
            } else {
                spacer.setMinHeight(effectiveBasis);
                spacer.setPrefHeight(effectiveBasis);
                VBox.setVgrow(spacer, Priority.ALWAYS);
            }
            return spacer;
        }

        // ===========================================================
        // 对齐转换：枚举 → JavaFX Pos / HPos / VPos
        // ===========================================================

        /**
         * 解析 HBox/VBox 的 setAlignment 值。
         * 当 justify 是 BETWEEN/AROUND/EVENLY 时主轴对齐由 spacer 控制，
         * 这里只设置交叉轴对齐（horizontal=true 时即垂直方向）。
         */
        private Pos resolveAlignmentForBox(boolean horizontal) {
            // 对 BETWEEN/AROUND/EVENLY，主轴用 START 不影响（spacer 已经吸光剩余空间）
            // 关键是设置交叉轴对齐
            if (horizontal) {
                // HBox：主轴=水平、交叉轴=垂直
                HPos h = isMainAxisDistributed() ? HPos.LEFT : toHPos(justify);
                VPos v = align == Align.STRETCH ? VPos.TOP : toVPos(align);
                return combine(h, v);
            } else {
                // VBox：主轴=垂直、交叉轴=水平
                VPos v = isMainAxisDistributed() ? VPos.TOP : toVPos(justify);
                HPos h = align == Align.STRETCH ? HPos.LEFT : toHPos(align);
                return combine(h, v);
            }
        }

        private Pos toFlowPos() {
            // FlowPane 的主轴由 orientation 决定：
            // - 水平方向：justify 映射到水平，align 映射到垂直
            // - 垂直方向：justify 映射到垂直，align 映射到水平
            if (isVertical()) {
                return combine(toHPos(align), toVPos(justify));
            }
            return combine(toHPos(justify), toVPos(align));
        }

        private static Pos combine(HPos h, VPos v) {
            // 把 HPos + VPos 组合成 JavaFX Pos 枚举
            if (v == VPos.TOP) {
                return switch (h) {
                    case LEFT -> Pos.TOP_LEFT;
                    case CENTER -> Pos.TOP_CENTER;
                    case RIGHT -> Pos.TOP_RIGHT;
                };
            } else if (v == VPos.BOTTOM) {
                return switch (h) {
                    case LEFT -> Pos.BOTTOM_LEFT;
                    case CENTER -> Pos.BOTTOM_CENTER;
                    case RIGHT -> Pos.BOTTOM_RIGHT;
                };
            } else if (v == VPos.BASELINE) {
                return switch (h) {
                    case LEFT -> Pos.BASELINE_LEFT;
                    case CENTER -> Pos.BASELINE_CENTER;
                    case RIGHT -> Pos.BASELINE_RIGHT;
                };
            } else { // CENTER
                return switch (h) {
                    case LEFT -> Pos.CENTER_LEFT;
                    case CENTER -> Pos.CENTER;
                    case RIGHT -> Pos.CENTER_RIGHT;
                };
            }
        }

        private static HPos toHPos(Justify j) {
            return switch (j) {
                case END -> HPos.RIGHT;
                case CENTER -> HPos.CENTER;
                // START / BETWEEN / AROUND / EVENLY 主轴起点对齐（实际由 spacer 控制分布）
                default -> HPos.LEFT;
            };
        }

        private static HPos toHPos(Align a) {
            return switch (a) {
                case END -> HPos.RIGHT;
                case CENTER -> HPos.CENTER;
                // START / BASELINE
                default -> HPos.LEFT;
            };
        }

        private static VPos toVPos(Justify j) {
            return switch (j) {
                case END -> VPos.BOTTOM;
                case CENTER -> VPos.CENTER;
                default -> VPos.TOP;
            };
        }

        private static VPos toVPos(Align a) {
            return switch (a) {
                case END -> VPos.BOTTOM;
                case CENTER -> VPos.CENTER;
                case BASELINE -> VPos.BASELINE;
                default -> VPos.TOP;
            };
        }

        // ===========================================================
        // 工具方法
        // ===========================================================

        private boolean isVertical() {
            return direction == Direction.COLUMN || direction == Direction.COLUMN_REVERSE;
        }

        private boolean isReversed() {
            return direction == Direction.ROW_REVERSE || direction == Direction.COLUMN_REVERSE;
        }

        private boolean isMainAxisDistributed() {
            return justify == Justify.BETWEEN || justify == Justify.AROUND || justify == Justify.EVENLY;
        }

        private double effectiveRowGap() {
            return rowGap >= 0 ? rowGap : gap;
        }

        private double effectiveColumnGap() {
            return columnGap >= 0 ? columnGap : gap;
        }

        private static List<Node> reverse(List<Node> source) {
            List<Node> reversed = new ArrayList<>(source);
            Collections.reverse(reversed);
            return reversed;
        }
    }
}
