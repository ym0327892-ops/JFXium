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
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
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
 * ProjectReleaseTemplate - 工程发布节奏模板。
 *
 * <p>面向项目主页 / 产品主页中的“版本节奏”区域，展示发布元信息、版本演进、
 * 近期变更和发布准备度。</p>
 */
public final class ProjectReleaseTemplate {

    // i18n keys: project.release_title, project.release_description, project.release_action

    private ProjectReleaseTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Meta(String label, Node content, int span) {}
        private record Release(String version, String label, TimelineAnt.DotColor color) {}
        private record Change(String key, String title, String description, String actionText) {}
        private record Readiness(String title, double value, ProgressAnt.Status status, String note) {}

        private String title = null;
        private String description = null;
        private final List<Meta> metadata = new ArrayList<>();
        private final List<Release> releases = new ArrayList<>();
        private final List<Change> changes = new ArrayList<>();
        private final List<String> statusTexts = new ArrayList<>();
        private final List<TagAnt.Type> statusTypes = new ArrayList<>();
        private Readiness readiness = null;
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.release_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.release_description"));
            return this;
        }

        public Builder status(String text) {
            return status(text, TagAnt.Type.PRIMARY);
        }

        public Builder status(String text, TagAnt.Type type) {
            statusTexts.add(TextUtils.safeText(text));
            statusTypes.add(type != null ? type : TagAnt.Type.DEFAULT);
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

        public Builder release(String version, String label, TimelineAnt.DotColor color) {
            releases.add(new Release(
                    TextUtils.safeText(version),
                    TextUtils.safeText(label),
                    color != null ? color : TimelineAnt.DotColor.BLUE
            ));
            return this;
        }

        public Builder change(String key, String title, String description) {
            return change(key, title, description, Messages.get("project.release_action"));
        }

        public Builder change(String key, String title, String description, String actionText) {
            changes.add(new Change(
                    TextUtils.safeText(key),
                    TextUtils.safeText(title),
                    TextUtils.safeText(description),
                    TextUtils.safeText(actionText, Messages.get("project.release_action"))
            ));
            return this;
        }

        public Builder readiness(String title, double value, ProgressAnt.Status status, String note) {
            this.readiness = new Readiness(
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
                            buildDetailGrid(),
                            buildReadinessSurface()
                    )
                    .build();
            applyStyles(root);
            return root;
        }

        private Node buildHeader() {
            String resolvedTitle = TextUtils.safeText(title, Messages.get("project.release_title"));
            String resolvedDescription = TextUtils.safeText(description, Messages.get("project.release_description"));
            HBox statusBox = buildTagRow();
            VBox headerBody = VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.text(resolvedDescription)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();

            return SurfaceAnt.create()
                    .title(resolvedTitle)
                    .extra(statusBox)
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(headerBody)
                    .build();
        }

        private Node buildMetaGrid() {
            if (metadata.isEmpty()) {
                return VBoxAnt.create().build();
            }

            DescriptionsAnt.Builder descriptions = DescriptionsAnt.create()
                    .column(4)
                    .size(Size.SMALL);
            for (Meta meta : metadata) {
                descriptions.item(meta.label(), meta.content(), meta.span());
            }

            return SurfaceAnt.create()
                    .title("发布元信息")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(descriptions.build())
                    .build();
        }

        private Node buildDetailGrid() {
            GridAnt.Row row = GridAnt.row().align(Pos.TOP_LEFT);
            row.col(GridAnt.col(buildReleaseTimeline()).xs(24).sm(24).md(12).lg(12).xl(12).xxl(12));
            row.col(GridAnt.col(buildChangeList()).xs(24).sm(24).md(12).lg(12).xl(12).xxl(12));

            return GridAnt.create()
                    .gutter(16)
                    .responsive()
                    .row(row)
                    .build();
        }

        private Node buildReleaseTimeline() {
            TimelineAnt.Builder timeline = TimelineAnt.create();
            if (releases.isEmpty()) {
                timeline.item("暂无发布记录");
            } else {
                for (Release release : releases) {
                    timeline.item(release.version(), release.label(), release.color());
                }
            }

            return SurfaceAnt.create()
                    .title("版本演进")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(timeline.build())
                    .build();
        }

        private Node buildChangeList() {
            ListAnt.Builder list = ListAnt.create()
                    .bordered(true)
                    .split(true);

            if (changes.isEmpty()) {
                list.item("暂无近期变更", "可以把下一次发布的重点放进这里。");
            } else {
                for (Change change : changes) {
                    Button action = ButtonAnt.link(change.actionText(), Size.SMALL)
                            .onClick(e -> Callbacks.fire(onAction, change.key()))
                            .build();
                    list.item(null, change.title(), change.description(), action);
                }
            }

            return SurfaceAnt.create()
                    .title("近期变更")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(list.build())
                    .build();
        }

        private Node buildReadinessSurface() {
            if (readiness == null) {
                return VBoxAnt.create().build();
            }

            VBox body = VBoxAnt.create()
                    .spacing(8)
                    .children(
                            TypographyAnt.text(readiness.title()).build(),
                            ProgressAnt.bar()
                                    .progress(readiness.value())
                                    .status(readiness.status())
                                    .showInfo(true)
                                    .build()
                    )
                    .build();
            if (!readiness.note().isEmpty()) {
                body.getChildren().add(TypographyAnt.text(readiness.note())
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build());
            }

            return SurfaceAnt.create()
                    .title("发布准备度")
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(body)
                    .build();
        }

        private HBox buildTagRow() {
            HBox row = HBoxAnt.create()
                    .spacing(8)
                    .align(Pos.CENTER_LEFT)
                    .build();
            for (int i = 0; i < statusTexts.size(); i++) {
                row.getChildren().add(TagAnt.create(statusTexts.get(i))
                        .type(statusTypes.get(i))
                        .size(Size.SMALL)
                        .build());
            }
            return row;
        }
    }
}
