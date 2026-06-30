package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.DescriptionsAnt;
import org.openkawu.jfxium.component.composite.ListAnt;
import org.openkawu.jfxium.component.composite.ProgressAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.composite.StatisticAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.Callbacks;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectOverviewTemplate - 工程项目展示模板。
 *
 * <p>把工程首页最常见的几块内容收口到一个模板里：
 * 运行态摘要、技术栈、构建进度、里程碑、近期动作。
 * 适合项目首页、控制台首页、README 中的应用概览页。</p>
 */
public final class ProjectOverviewTemplate {

    // i18n keys: project.overview_title, project.overview_description, project.overview_action

    private ProjectOverviewTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Metric(String title, String value, String note, IconAnt.Path icon) {}
        private record Meta(String label, Node content, int span) {}
        private record StatusTag(String text, TagAnt.Type type) {}
        private record Milestone(String content, String label, TimelineAnt.DotColor color) {}
        private record Activity(String key, String title, String description, String actionText) {}
        private record ProgressSpec(String title, double value, ProgressAnt.Status status, String note) {}

        private String title = null;
        private String description = null;
        private final List<StatusTag> statusTags = new ArrayList<>();
        private final List<Meta> metadata = new ArrayList<>();
        private final List<Metric> metrics = new ArrayList<>();
        private final List<StatusTag> techTags = new ArrayList<>();
        private final List<Milestone> milestones = new ArrayList<>();
        private final List<Activity> activities = new ArrayList<>();
        private ProgressSpec progress = null;
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.overview_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.overview_description"));
            return this;
        }

        public Builder status(String text) {
            return status(text, TagAnt.Type.PRIMARY);
        }

        public Builder status(String text, TagAnt.Type type) {
            statusTags.add(new StatusTag(TextUtils.safeText(text), type != null ? type : TagAnt.Type.DEFAULT));
            return this;
        }

        public Builder tech(String text) {
            return tech(text, TagAnt.Type.DEFAULT);
        }

        public Builder tech(String text, TagAnt.Type type) {
            techTags.add(new StatusTag(TextUtils.safeText(text), type != null ? type : TagAnt.Type.DEFAULT));
            return this;
        }

        public Builder metric(String title, String value, String note, IconAnt.Path icon) {
            metrics.add(new Metric(
                    TextUtils.safeText(title),
                    TextUtils.safeText(value),
                    TextUtils.safeText(note),
                    icon != null ? icon : IconAnt.Path.DASHBOARD
            ));
            return this;
        }

        public Builder meta(String label, String value) {
            return meta(label, TypographyAnt.text(TextUtils.safeText(value))
                    .type(TypographyAnt.TextColor.SECONDARY)
                    .build(), 1);
        }

        public Builder meta(String label, Node content) {
            return meta(label, content, 1);
        }

        public Builder meta(String label, Node content, int span) {
            metadata.add(new Meta(
                    TextUtils.safeText(label),
                    content != null ? content : TypographyAnt.text("").build(),
                    TextUtils.ensureAtLeastOne(span)
            ));
            return this;
        }

        public Builder milestone(String content, String label, TimelineAnt.DotColor color) {
            milestones.add(new Milestone(
                    TextUtils.safeText(content),
                    TextUtils.safeText(label),
                    color != null ? color : TimelineAnt.DotColor.BLUE
            ));
            return this;
        }

        public Builder activity(String key, String title, String description) {
            return activity(key, title, description, Messages.get("project.overview_action"));
        }

        public Builder activity(String key, String title, String description, String actionText) {
            activities.add(new Activity(
                    TextUtils.safeText(key),
                    TextUtils.safeText(title),
                    TextUtils.safeText(description),
                    TextUtils.safeText(actionText, Messages.get("project.overview_action"))
            ));
            return this;
        }

        public Builder progress(String title, double value, ProgressAnt.Status status, String note) {
            this.progress = new ProgressSpec(
                    TextUtils.safeText(title),
                    value,
                    status != null ? status : ProgressAnt.Status.NORMAL,
                    TextUtils.safeText(note)
            );
            return this;
        }

        public Builder onAction(Consumer<String> onAction) {
            this.onAction = onAction;
            return this;
        }

        public VBox build() {
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .children(
                            buildHeader(),
                            buildMetaGrid(),
                            buildMetricGrid(),
                            buildDetailGrid(),
                            buildActivitySurface()
                    )
                    .build();
            applyStyles(root);
            return root;
        }

        private Node buildHeader() {
            String resolvedTitle = TextUtils.safeText(title, Messages.get("project.overview_title"));
            String resolvedDescription = TextUtils.safeText(description, Messages.get("project.overview_description"));
            HBox statusBox = buildTagRow(statusTags);
            VBox headerBody = VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.text(resolvedDescription)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();

            SurfaceAnt.Builder builder = SurfaceAnt.create()
                    .title(resolvedTitle)
                    .extra(statusBox)
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(headerBody);
            return builder.build();
        }

        private Node buildMetricGrid() {
            if (metrics.isEmpty()) {
                return VBoxAnt.create().build();
            }

            GridAnt.Row row = GridAnt.row().align(Pos.TOP_LEFT);
            for (Metric metric : metrics) {
                row.col(GridAnt.col(buildMetricCard(metric))
                        .xs(24)
                        .sm(12)
                        .md(12)
                        .lg(6)
                        .xl(6)
                        .xxl(6));
            }

            return GridAnt.create()
                    .gutter(16)
                    .responsive()
                    .row(row)
                    .build();
        }

        private Node buildMetaGrid() {
            if (metadata.isEmpty()) {
                return VBoxAnt.create().build();
            }

            DescriptionsAnt.Builder descriptions = DescriptionsAnt.create()
                    .column(3)
                    .size(Size.SMALL);
            for (Meta meta : metadata) {
                descriptions.item(meta.label(), meta.content(), meta.span());
            }

            return SurfaceAnt.create()
                    .title("项目信息")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(descriptions.build())
                    .build();
        }

        private Node buildDetailGrid() {
            Node stackSurface = buildStackSurface();
            Node milestoneSurface = buildMilestoneSurface();

            GridAnt.Row row = GridAnt.row().align(Pos.TOP_LEFT);
            row.col(GridAnt.col(stackSurface).xs(24).sm(24).md(12).lg(12).xl(12).xxl(12));
            row.col(GridAnt.col(milestoneSurface).xs(24).sm(24).md(12).lg(12).xl(12).xxl(12));

            return GridAnt.create()
                    .gutter(16)
                    .responsive()
                    .row(row)
                    .build();
        }

        private Node buildActivitySurface() {
            ListAnt.Builder list = ListAnt.create()
                    .bordered(true)
                    .split(true);

            if (activities.isEmpty()) {
                list.item("暂无近期动作", "可以从右上角头像入口继续扩展工程设置。");
            } else {
                for (Activity activity : activities) {
                    Button actionButton = ButtonAnt.link(activity.actionText(), Size.SMALL)
                            .onClick(e -> Callbacks.fire(onAction, activity.key()))
                            .build();

                    list.item(null, activity.title(), activity.description(), actionButton);
                }
            }

            return SurfaceAnt.create()
                    .title("近期动作")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(list.build())
                    .build();
        }

        private Node buildMetricCard(Metric metric) {
            StatisticAnt.Builder statistic = StatisticAnt.create()
                    .title(metric.title())
                    .value(metric.value())
                    .prefix(IconAnt.path(metric.icon(), 18));

            VBoxAnt body = VBoxAnt.create()
                    .spacing(8)
                    .children(statistic.build());
            if (!metric.note().isEmpty()) {
                body.children(TypographyAnt.text(metric.note())
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build());
            }

            return SurfaceAnt.create()
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(body)
                    .build();
        }

        private Node buildStackSurface() {
            VBox body = VBoxAnt.create()
                    .spacing(12)
                    .children(
                            buildTagRow(techTags),
                            buildProgressBlock()
                    )
                    .build();

            return SurfaceAnt.create()
                    .title("技术栈 / 构建")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(body)
                    .build();
        }

        private Node buildProgressBlock() {
            if (progress == null) {
                return TypographyAnt.text("暂未配置工程进度。")
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build();
            }

            VBoxAnt body = VBoxAnt.create()
                    .spacing(8)
                    .children(
                            TypographyAnt.text(progress.title()).build(),
                            ProgressAnt.bar()
                                    .progress(progress.value())
                                    .status(progress.status())
                                    .showInfo(true)
                                    .build()
                    );
            if (!progress.note().isEmpty()) {
                body.children(TypographyAnt.text(progress.note())
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build());
            }

            return body;
        }

        private Node buildMilestoneSurface() {
            TimelineAnt.Builder timeline = TimelineAnt.create();
            if (milestones.isEmpty()) {
                timeline.item("暂无里程碑");
            } else {
                for (Milestone milestone : milestones) {
                    timeline.item(milestone.content(), milestone.label(), milestone.color());
                }
            }

            return SurfaceAnt.create()
                    .title("里程碑")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(timeline.build())
                    .build();
        }

        private HBox buildTagRow(List<StatusTag> tags) {
            HBox row = HBoxAnt.create()
                    .spacing(8)
                    .align(Pos.CENTER_LEFT)
                    .build();
            if (tags == null || tags.isEmpty()) {
                return row;
            }

            for (StatusTag tag : tags) {
                row.getChildren().add(TagAnt.create(tag.text())
                        .type(tag.type())
                        .size(Size.SMALL)
                        .build());
            }
            return row;
        }
    }
}
