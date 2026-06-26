package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.AvatarAnt;
import org.openkawu.jfxium.component.composite.DescriptionsAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt.DropdownResult;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectHeroTemplate - 工程项目展示首屏门面模板。
 *
 * <p>用于项目首页、模板示例页、README 预览页的首屏区，统一承载：
 * 软件图标、标题说明、状态标签、关键摘要和右侧操作区。</p>
 *
 * <p>它的目标不是替代 {@link ProjectOverviewTemplate} 或 {@link LaunchPadTemplate}，
 * 而是把所有工程展示页重复出现的“首屏门面”先收口，避免 demo 里一再手写相同结构。</p>
 */
public final class ProjectHeroTemplate {

    // i18n keys: project.hero_title, project.hero_subtitle, project.hero_description

    private ProjectHeroTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    /**
     * 复用工程展示页常见的“头像 + 用户名 + 下拉菜单”入口。
     *
     * <p>它语义上属于工程门面的一部分，但底层直接委托给 {@link WorkspaceTemplate}，
     * 这样首页、展示页和工作台都可以共用同一个入口实现。</p>
     */
    public static DropdownResult userMenu(String currentUser, Consumer<String> onAction) {
        return WorkspaceTemplate.userMenu(currentUser, onAction);
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record StatusTag(String text, TagAnt.Type type) {}
        private record Meta(String label, Node content, int span) {}

        private String title = Messages.get("project.hero_title");
        private String subtitle = Messages.get("project.hero_subtitle");
        private String description = Messages.get("project.hero_description");
        private IconAnt.Path icon = IconAnt.Path.DASHBOARD;
        private final List<StatusTag> statusTags = new ArrayList<>();
        private final List<Meta> metadata = new ArrayList<>();
        private final List<Node> actions = new ArrayList<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.hero_title"));
            return this;
        }

        public Builder subtitle(String subtitle) {
            this.subtitle = TextUtils.safeText(subtitle, Messages.get("project.hero_subtitle"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.hero_description"));
            return this;
        }

        public Builder icon(IconAnt.Path icon) {
            this.icon = icon != null ? icon : IconAnt.Path.DASHBOARD;
            return this;
        }

        public Builder status(String text) {
            return status(text, TagAnt.Type.PRIMARY);
        }

        public Builder status(String text, TagAnt.Type type) {
            statusTags.add(new StatusTag(TextUtils.safeText(text), type != null ? type : TagAnt.Type.DEFAULT));
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

        public Builder action(Node action) {
            if (action != null) {
                actions.add(action);
            }
            return this;
        }

        public Builder actions(Node... actionNodes) {
            if (actionNodes != null) {
                for (Node node : actionNodes) {
                    action(node);
                }
            }
            return this;
        }

        public VBox build() {
            VBox heroBody = VBoxAnt.create()
                    .spacing(12)
                    .children(
                            buildLeadRow(),
                            buildStatusRow(),
                            buildMetaGrid()
                    )
                    .build();

            VBox root = SurfaceAnt.create()
                    .title(title)
                    .extra(buildActionBar())
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(heroBody)
                    .build();

            applyStyles(root);
            return root;
        }

        private Node buildLeadRow() {
            return HBoxAnt.create()
                    .spacing(12)
                    .align(Pos.CENTER_LEFT)
                    .children(
                            AvatarAnt.create()
                                    .icon(IconAnt.path(icon, 20))
                                    .shape(AvatarAnt.Shape.SQUARE)
                                    .size(48)
                                    .build(),
                            VBoxAnt.create()
                                    .spacing(4)
                                    .children(
                                            TypographyAnt.text(subtitle)
                                                    .type(TypographyAnt.TextColor.SECONDARY)
                                                    .build(),
                                            TypographyAnt.paragraph(description).build()
                                    )
                                    .build()
                    )
                    .build();
        }

        private Node buildStatusRow() {
            if (statusTags.isEmpty()) {
                return null;
            }

            HBoxAnt row = HBoxAnt.create()
                    .spacing(8)
                    .align(Pos.CENTER_LEFT);
            for (StatusTag tag : statusTags) {
                row.children(TagAnt.create(tag.text()).type(tag.type()).build());
            }
            return row.build();
        }

        private Node buildMetaGrid() {
            if (metadata.isEmpty()) {
                return null;
            }

            DescriptionsAnt.Builder descriptions = DescriptionsAnt.create()
                    .column(3)
                    .size(Size.SMALL);
            for (Meta meta : metadata) {
                descriptions.item(meta.label(), meta.content(), meta.span());
            }
            return descriptions.build();
        }

        private Node buildActionBar() {
            if (actions.isEmpty()) {
                return null;
            }

            HBoxAnt bar = HBoxAnt.create()
                    .spacing(8)
                    .align(Pos.CENTER_RIGHT);
            bar.children(actions.toArray(Node[]::new));
            return bar.build();
        }
    }
}
