package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.NumericUtils;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DashboardTemplate - admin Dashboard 概览页骨架（M19.16）。
 *
 * <p><b>定位</b>：标准 dashboard 布局——欢迎语 + N 列统计卡矩阵 + 底部多区域内容。
 * 抽自 admin demo 的 DashboardPage，所有 admin 后台首页用得上。</p>
 *
 * <h2>整体结构</h2>
 * <pre>
 * ┌────────────────────────────────────────┐
 * │ 欢迎语（可选）                           │
 * ├────────────────────────────────────────┤
 * │ ┌──┐ ┌──┐ ┌──┐ ┌──┐                    │
 * │ │卡│ │卡│ │卡│ │卡│  统计卡矩阵（4 列）  │
 * │ └──┘ └──┘ └──┘ └──┘                    │
 * ├────────────────────────────────────────┤
 * │ ┌──────────────┐ ┌──────────┐          │
 * │ │ 主区（60%）   │ │ 副区（40%）│  bottom │
 * │ └──────────────┘ └──────────┘          │
 * └────────────────────────────────────────┘
 * </pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>基础（4 个统计卡）</h3>
 * <pre>{@code
 * VBox dashboard = DashboardTemplate.create()
 *     .welcome("欢迎回来，张三 👋")
 *     .stat(IconAnt.Path.USERS,  "总用户",   "1,234",   "↑ 12.5%", true)
 *     .stat(IconAnt.Path.FILE,   "今日订单", "89",      "↓ 3.2%",  false)
 *     .stat(IconAnt.Path.CHART,  "月销售额", "¥125k",   "↑ 8.4%",  true)
 *     .stat(IconAnt.Path.DASHBOARD, "转化率","23.4%",   "↑ 1.2%",  true)
 *     .build();
 * }</pre>
 *
 * <h3>含底部双栏区</h3>
 * <pre>{@code
 * VBox dashboard = DashboardTemplate.create()
 *     .welcome("欢迎回来")
 *     .stat(...).stat(...).stat(...)
 *     .bottomLeft(timelineNode)      // 主区（60%）
 *     .bottomRight(todoListNode)     // 副区（40%）
 *     .build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>底层 VBox —— 欢迎/统计/底部区按顺序竖排</li>
 *   <li>统计卡用 GridPane 等分宽度，自动适配 N 列（默认按数量决定）</li>
 *   <li>每个 StatCard 是 GroupBoxAnt + 图标盒 + 标题 + 大值 + 趋势文字</li>
 *   <li>底部双栏比例 60:40（admin 行业惯例：主区给数据/活动，副区给操作/Todo）</li>
 *   <li>所有可选 —— 不调用即不渲染该区</li>
 * </ul>
 */
public class DashboardTemplate {

    public static Builder create() {
        return new Builder();
    }

    // ============================================================
    // 统计卡数据结构
    // ============================================================
    private record StatCard(IconAnt.Path icon, String title, String value, String trend, boolean up) {}

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String welcome;
        private final List<StatCard> stats = new ArrayList<>();
        private int statColumns = 0;   // 0 表示自动按 stats.size() 决定
        private Node bottomLeft;
        private Node bottomRight;
        private double leftRatio = 60;
        private double sectionGap = 20;
        private double padding = 24;

        private Builder() {}

        // ============================================================
        // 欢迎语
        // ============================================================

        public Builder welcome(String text) {
            this.welcome = text;
            return this;
        }

        // ============================================================
        // 统计卡
        // ============================================================

        /** 添加一个统计卡。trend 例如 "↑ 12.5%"，up=true 显示绿色，false 红色。 */
        public Builder stat(IconAnt.Path icon, String title, String value, String trend, boolean up) {
            stats.add(new StatCard(icon != null ? icon : IconAnt.Path.DASHBOARD,
                    TextUtils.safeText(title),
                    TextUtils.safeText(value),
                    TextUtils.safeText(trend),
                    up));
            return this;
        }

        /** 强制指定列数（默认根据 stats 数量自动适配）。 */
        public Builder statColumns(int columns) {
            this.statColumns = columns;
            return this;
        }

        // ============================================================
        // 底部双栏
        // ============================================================

        /** 底部主区（默认占 60%）。 */
        public Builder bottomLeft(Node node) {
            this.bottomLeft = node;
            return this;
        }

        /** 底部副区（默认占 40%）。 */
        public Builder bottomRight(Node node) {
            this.bottomRight = node;
            return this;
        }

        /** 底部左侧占比（默认 60，对应右侧 40）。 */
        public Builder leftRatio(double percent) {
            this.leftRatio = clampPercent(percent, 60);
            return this;
        }

        // ============================================================
        // 装饰
        // ============================================================

        public Builder sectionGap(double gap) { this.sectionGap = TextUtils.safeNonNegative(gap, 0); return this; }
        public Builder padding(double padding) { this.padding = TextUtils.safeNonNegative(padding, 0); return this; }

        // ============================================================
        // 构建
        // ============================================================

        public VBox build() {
            double resolvedSectionGap = TextUtils.safeNonNegative(sectionGap, 0);
            double resolvedPadding = TextUtils.safeNonNegative(padding, 0);
            VBox root = new VBox(resolvedSectionGap);
            root.setPadding(new Insets(resolvedPadding));
            root.getStyleClass().add(JfxStyles.DASHBOARD_ROOT);

            // 1. 欢迎语
            if (welcome != null && !welcome.isEmpty()) {
                LabelAnt w = LabelAnt.create(welcome);
                w.getStyleClass().add(JfxStyles.DASHBOARD_WELCOME);
                root.getChildren().add(w);
            }

            // 2. 统计卡
            if (!stats.isEmpty()) {
                root.getChildren().add(buildStatsGrid());
            }

            // 3. 底部双栏
            if (bottomLeft != null || bottomRight != null) {
                root.getChildren().add(buildBottomRow());
            }

            applyStyles(root);
            return root;
        }

        // ----------------- 统计卡矩阵 -----------------

        private GridPane buildStatsGrid() {
            int cols = statColumns > 0 ? statColumns : stats.size();
            GridPane grid = new GridPane();
            grid.setHgap(16);
            grid.setVgap(16);
            for (int i = 0; i < cols; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(100.0 / cols);
                cc.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(cc);
            }
            for (int i = 0; i < stats.size(); i++) {
                grid.add(buildStatCard(stats.get(i)), i % cols, i / cols);
            }
            return grid;
        }

        private VBox buildStatCard(StatCard s) {
            // 图标盒
            Region iconNode = IconAnt.path(s.icon(), 20);
            StackPane iconBox = new StackPane(iconNode);
            iconBox.setMinSize(40, 40);
            iconBox.setMaxSize(40, 40);
            iconBox.getStyleClass().add(JfxStyles.DASHBOARD_STAT_ICON_BOX);

            // 标题
            LabelAnt title = LabelAnt.create(s.title());
            title.getStyleClass().add(JfxStyles.DASHBOARD_STAT_TITLE);

            // 大数值
            LabelAnt value = LabelAnt.create(s.value());
            value.getStyleClass().add(JfxStyles.DASHBOARD_STAT_VALUE);

            // 趋势
            LabelAnt trend = LabelAnt.create(s.trend());
            String trendClass = s.up() ? JfxStyles.DASHBOARD_STAT_TREND_UP : JfxStyles.DASHBOARD_STAT_TREND_DOWN;
            trend.getStyleClass().add(trendClass);
            LabelAnt trendHint = LabelAnt.create(" " + Messages.get("dashboard.compared_to_last_week"));
            trendHint.getStyleClass().add(JfxStyles.DASHBOARD_STAT_TREND_HINT);
            HBox trendRow = new HBox(0, trend, trendHint);
            trendRow.setAlignment(Pos.CENTER_LEFT);

            VBox content = new VBox(8, iconBox, title, value, trendRow);

            return GroupBoxAnt.create()
                    .content(content)
                    .bordered(true)
                    .build();
        }

        // ----------------- 底部双栏 -----------------

        private GridPane buildBottomRow() {
            GridPane grid = new GridPane();
            grid.setHgap(16);
            ColumnConstraints left = new ColumnConstraints();
            left.setPercentWidth(clampPercent(leftRatio, 60));
            left.setHgrow(Priority.ALWAYS);
            ColumnConstraints right = new ColumnConstraints();
            right.setPercentWidth(100 - clampPercent(leftRatio, 60));
            right.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(left, right);

            if (bottomLeft != null) {
                grid.add(bottomLeft, 0, 0);
            }
            if (bottomRight != null) {
                grid.add(bottomRight, 1, 0);
            }
            return grid;
        }

        // safeSpacing 统一改用 TextUtils.safeNonNegative,见 P0-23。
        // safePercent 钳制到 [10, 90] 的语义为 Dashboard 专属,直接走 NumericUtils.clamp 收口。
        private static double clampPercent(double value, double fallback) {
            return NumericUtils.clamp(value, 10, 90, fallback);
        }
    }
}
