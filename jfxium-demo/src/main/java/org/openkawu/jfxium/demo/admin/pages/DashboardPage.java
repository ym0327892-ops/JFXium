package org.openkawu.jfxium.demo.admin.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.StatisticAnt;
import org.openkawu.jfxium.component.TimelineAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.ScrollPaneBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;

/**
 * 首页（Dashboard）。
 *
 * <pre>
 * ┌─ 欢迎语 ────────────────────────────────────┐
 * ├─ 4 列统计卡（GridPane 等分）───────────────┤
 * │  [总用户] [今日订单] [月销售额] [转化率]    │
 * ├─ 左 60% 最近活动（Timeline）─┬─ 右 40% 待办─┤
 * │                              │              │
 * └──────────────────────────────┴──────────────┘
 * </pre>
 *
 * <p>设计取舍：
 * <ul>
 *   <li>用 GridPane + percentWidth 实现等分（4 列各 25%、左右 60/40）</li>
 *   <li>整体外层 ScrollPane，窗口高度不够时可滚动</li>
 *   <li>每个卡片都是 CardAnt，shadow=SMALL，统一视觉</li>
 *   <li>所有颜色走 CSS 变量，主题切换自动跟随</li>
 * </ul>
 */
public class DashboardPage implements AdminPage {

    private final String username;

    public DashboardPage(String username) {
        this.username = username;
    }

    @Override public String key()   { return "dashboard"; }
    @Override public String title() { return "首页 / 仪表盘"; }

    @Override
    public Node getView() {
        // 1. 顶部欢迎语
        Node welcome = buildWelcome();

        // 2. 统计卡矩阵（4 列等分）
        Node stats = buildStatsRow();

        // 3. 下半部分：左 Timeline + 右 待办（6:4）
        Node bottom = buildBottomRow();

        VBox content = VBoxBuilder.create()
                .spacing(20)
                .padding(24)
                .children(welcome, stats, bottom)
                .build();
        content.setStyle("-fx-background-color: -color-bg-layout;");

        // 整页可滚（窗口高度紧时自动出滚动条）
        ScrollPane scroll = ScrollPaneBuilder.create()
                .content(content)
                .fitToWidth(true)
                .hbarPolicy(ScrollPane.ScrollBarPolicy.NEVER)
                .vbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)
                .build();
        scroll.setStyle("-fx-background: -color-bg-layout; -fx-background-color: -color-bg-layout;");
        return scroll;
    }

    // ============================================================
    // 顶部欢迎语
    // ============================================================
    private Node buildWelcome() {
        Label hello = new Label("你好，" + username + " 👋");
        hello.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");

        Label sub = new Label("欢迎回到 JFXium Admin，今天又是充满活力的一天");
        sub.setStyle("-fx-text-fill: -color-fg-muted;");

        return VBoxBuilder.create()
                .spacing(4)
                .children(hello, sub)
                .build();
    }

    // ============================================================
    // 4 列统计卡矩阵
    // ============================================================
    private Node buildStatsRow() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        // 4 列各占 25%
        for (int i = 0; i < 4; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        VBox card1 = statCard(IconAnt.Path.USERS,    "总用户",   "1,234",     "↑ 12.5%", true,  "icon-accent");
        VBox card2 = statCard(IconAnt.Path.FILE,     "今日订单", "89",        "↓ 3.2%",  false, "icon-warning");
        VBox card3 = statCard(IconAnt.Path.CHART,    "月销售额", "¥125,890",  "↑ 8.4%",  true,  "icon-success");
        VBox card4 = statCard(IconAnt.Path.DASHBOARD,"转化率",   "23.4%",     "↑ 1.2%",  true,  "icon-danger");

        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 2, 0);
        grid.add(card4, 3, 0);
        return grid;
    }

    /** 单个统计卡：[图标盒] 标题 / 大数值 / 增减趋势文字。 */
    private VBox statCard(IconAnt.Path iconPath, String title, String value, String trend, boolean up, String iconColorClass) {
        // 图标盒：彩色背景 + 白色 svg
        Region icon = IconAnt.path(iconPath, 20);
        icon.getStyleClass().add(iconColorClass);
        StackPane iconBox = new StackPane(icon);
        iconBox.setMinSize(40, 40);
        iconBox.setMaxSize(40, 40);
        iconBox.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-background-color: -color-bg-subtle;"
        );

        // 标题
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-muted;");

        // 大数值
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");

        // 趋势
        Label trendLabel = new Label(trend);
        trendLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + (up ? "-color-success-emphasis" : "-color-danger-emphasis") + ";"
        );
        Label trendHint = new Label(" 较上周");
        trendHint.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-muted;");
        HBox trendRow = HBoxBuilder.create()
                .spacing(0)
                .align(Pos.CENTER_LEFT)
                .children(trendLabel, trendHint)
                .build();

        // 顶部行：图标 + 弹性 + 标题（标题在右上小字角落，让卡片更专业）
        // 简化版：图标在最上、标题在图标下、值在标题下、趋势在最底
        VBox content = VBoxBuilder.create()
                .spacing(8)
                .children(iconBox, titleLabel, valueLabel, trendRow)
                .build();

        VBox card = CardAnt.create()
                .content(content)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        return card;
    }

    // ============================================================
    // 下半部分：最近活动 + 待办
    // ============================================================
    private Node buildBottomRow() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        // 左 60%、右 40%
        javafx.scene.layout.ColumnConstraints left = new javafx.scene.layout.ColumnConstraints();
        left.setPercentWidth(60);
        left.setHgrow(Priority.ALWAYS);
        javafx.scene.layout.ColumnConstraints right = new javafx.scene.layout.ColumnConstraints();
        right.setPercentWidth(40);
        right.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(left, right);

        grid.add(buildRecentActivity(), 0, 0);
        grid.add(buildTodoList(), 1, 0);
        return grid;
    }

    /** 最近活动：Timeline 形态。 */
    private VBox buildRecentActivity() {
        VBox timeline = TimelineAnt.create()
                .item("张三 创建订单 #1024",      "10:24",  TimelineAnt.DotColor.BLUE)
                .item("李四 完成支付 ¥1,899",    "09:58",  TimelineAnt.DotColor.GREEN)
                .item("系统 自动备份完成",         "09:30",  TimelineAnt.DotColor.GRAY)
                .item("王五 注册新账号",          "08:42",  TimelineAnt.DotColor.BLUE)
                .item("订单 #1019 退款成功",      "昨天",   TimelineAnt.DotColor.RED)
                .build();

        // 留出 Timeline 内边距
        VBox.setMargin(timeline, new Insets(8, 0, 0, 8));
        VBox content = new VBox(timeline);
        content.setPadding(new Insets(0));

        return CardAnt.create()
                .title("最近活动")
                .content(content)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
    }

    /** 待办任务：CheckBox 列表（不引新组件，直接 VBox + CheckBox）。 */
    private VBox buildTodoList() {
        VBox list = VBoxBuilder.create()
                .spacing(12)
                .padding(8, 0, 0, 0)
                .children(
                        todo("处理用户反馈 (3 条)", false),
                        todo("审核新注册用户 (12 个)", false),
                        todo("月度报表生成", true),
                        todo("数据库备份验证", false),
                        todo("更新发布说明", true)
                )
                .build();

        return CardAnt.create()
                .title("待办任务")
                .content(list)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
    }

    private HBox todo(String text, boolean done) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(done);
        if (done) {
            cb.setStyle(
                    "-fx-text-fill: -color-fg-muted;" +
                    "-fx-strikethrough: true;"   // JavaFX CheckBox 不支持 strikethrough，但留着是好意图
            );
        }
        return HBoxBuilder.create()
                .spacing(8)
                .align(Pos.CENTER_LEFT)
                .children(cb)
                .build();
    }
}
