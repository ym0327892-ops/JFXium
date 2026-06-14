package org.openkawu.jfxium.demo.admin.pages;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.openkawu.jfxium.component.composite.*;
import org.openkawu.jfxium.core.css.Background;

/**
 * DashboardPage —— 数据概览页，集成：
 * StatisticAnt / SkeletonAnt / TimelineAnt / ProgressAnt / CarouselAnt /
 * FloatButtonAnt / SpinAnt overlay / SegmentedAnt。
 */
public class DashboardPage extends StackPane {

    private final VBox loadedContent;
    private final StackPane skeletonPlaceholder;
    private SpinAnt.Overlay spinOverlay; // lazy init — overlay needs node already in scene graph

    public DashboardPage() {
        setPadding(new Insets(24));
        skeletonPlaceholder = buildSkeleton();
        skeletonPlaceholder.getStyleClass().add(Background.DEFAULT.styleClass());
        loadedContent = buildContent();
        loadedContent.setVisible(false);
        getChildren().addAll(loadedContent, skeletonPlaceholder);

        PauseTransition pt = new PauseTransition(Duration.seconds(1.2));
        pt.setOnFinished(e -> {
            skeletonPlaceholder.setVisible(false);
            loadedContent.setVisible(true);
        });
        pt.play();
    }

    private StackPane buildSkeleton() {
        VBox sk = new VBox(20);
        sk.setPadding(new Insets(12, 0, 0, 0));
        HBox statsRow = new HBox(16);
        for (int i = 0; i < 4; i++) {
            VBox card = new VBox(8);
            card.setPrefWidth(200);
            card.getChildren().addAll(
                    SkeletonAnt.create().width(80).height(14).build(),
                    SkeletonAnt.create().width(120).height(28).build()
            );
            statsRow.getChildren().add(card);
        }
        VBox tl = new VBox(8);
        for (int i = 0; i < 5; i++) {
            tl.getChildren().add(SkeletonAnt.create().width(350).height(16).build());
        }
        sk.getChildren().addAll(statsRow, tl);
        return new StackPane(sk);
    }

    private VBox buildContent() {
        VBox root = new VBox(16);

        // Segmented time range
        HBox timeRange = SegmentedAnt.create()
                .option("today", "今日")
                .option("week", "本周")
                .option("month", "本月")
                .build();
        timeRange.getStyleClass().add(Background.DEFAULT.styleClass());

        // Stat cards
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                buildStatCard("总用户数", "12,856", "↑ 12%"),
                buildStatCard("活跃用户", "3,421", "↑ 8%"),
                buildStatCard("订单量", "2,340", "↓ 3%"),
                buildStatCard("营业额", "¥ 56.8万", "↑ 23%")
        );

        // Carousel + metrics/timeline
        HBox lowerRow = new HBox(16);
        StackPane carousel = CarouselAnt.create()
                .item(new Label("🚀 欢迎使用 JFXium Admin\n基于 JavaFX 21 构建"))
                .item(new Label("📊 数据概览\n实时监控系统运行状态"))
                .item(new Label("🔧 快速配置\n品牌色 / 密度 / 主题随心切换"))
                .build();
        carousel.setPrefWidth(400);
        carousel.setPrefHeight(200);

        VBox metrics = new VBox(12);
        metrics.getChildren().addAll(
                new Label("系统指标"),
                buildProgress("CPU", 0.45),
                buildProgress("内存", 0.72),
                buildProgress("磁盘", 0.38)
        );

        VBox timeline = buildTimeline();
        VBox rightCol = new VBox(16, metrics, timeline);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        lowerRow.getChildren().addAll(carousel, rightCol);

        root.getChildren().addAll(timeRange, statsRow, lowerRow);

        // FloatButton
        StackPane wrapper = new StackPane(root);
        StackPane fb = FloatButtonAnt.create()
                .onClick(() -> {
                    if (spinOverlay == null) {
                        spinOverlay = SpinAnt.overlay(loadedContent);
                    }
                    spinOverlay.show("刷新数据中...");
                    PauseTransition p = new PauseTransition(Duration.seconds(1.5));
                    p.setOnFinished(e2 -> spinOverlay.hide());
                    p.play();
                })
                .build();
        StackPane.setAlignment(fb, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fb, new Insets(0, 24, 24, 0));
        wrapper.getChildren().add(fb);

        VBox container = new VBox(wrapper);
        return container;
    }

    private Node buildStatCard(String title, String value, String trend) {
        Node stat = StatisticAnt.create().title(title).value(value).build();
        Label trendLabel = new Label(trend);
        trendLabel.getStyleClass().add(trend.contains("↑")
                ? "jfx-demo-trend-up"
                : "jfx-demo-trend-down");
        VBox card = new VBox(8, stat, trendLabel);
        card.setPrefWidth(220);
        return GroupBoxAnt.create().content(card).build();
    }

    private Node buildProgress(String label, double value) {
        Node bar = ProgressAnt.bar().progress(value).showInfo(true).build();
        return new VBox(4, new Label(label), bar);
    }

    private VBox buildTimeline() {
        Node timeline = TimelineAnt.create()
                .item("系统上线", "2026-06-01 09:00", TimelineAnt.DotColor.GREEN)
                .item("用户注册量突破 10000", "2026-06-05 14:30", TimelineAnt.DotColor.BLUE)
                .item("发布 v1.0-RC1", "2026-06-08 16:00", TimelineAnt.DotColor.BLUE)
                .item("数据库迁移完成", "2026-06-10 02:00", TimelineAnt.DotColor.GREEN)
                .item("CDN 部署中", "", TimelineAnt.DotColor.GRAY)
                .build();
        return new VBox(8, new Label("最近动态"), timeline);
    }
}
