package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectShowcaseTemplate - 工程项目展示首页模板。
 *
 * <p>用于把一个项目的“首页门面”统一收口：顶部说明、工程概览、发布节奏、
 * 常用入口等都可以顺序塞进来，避免 demo 里反复手写相同的页面骨架。
 * {@link Builder#hero(Node)} 可额外放置首屏门面卡，统一承载软件图标和状态摘要。</p>
 *
 * <p>它本质上还是基于 {@link PageTemplate} 的组合层，但把内容命名为
 * “工程展示”后，调用方更容易一眼看出页面意图。</p>
 */
public final class ProjectShowcaseTemplate {

    // i18n keys: project.showcase_title, project.showcase_description

    private ProjectShowcaseTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private String title = Messages.get("project.showcase_title");
        private String description = Messages.get("project.showcase_description");
        private Node hero;
        private final List<Node> sections = new ArrayList<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.showcase_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.showcase_description"));
            return this;
        }

        /**
         * 首屏门面区，通常放 ProjectHeroTemplate 的构建结果。
         */
        public Builder hero(Node hero) {
            this.hero = hero;
            return this;
        }

        public Builder section(Node section) {
            if (section != null) {
                sections.add(section);
            }
            return this;
        }

        public Builder sections(Node... sectionNodes) {
            if (sectionNodes != null) {
                for (Node node : sectionNodes) {
                    if (node != null) {
                        sections.add(node);
                    }
                }
            }
            return this;
        }

        public VBox build() {
            List<Node> bodySections = new ArrayList<>();
            if (hero != null) {
                bodySections.add(hero);
            }
            bodySections.addAll(sections);

            VBox root = PageTemplate.create()
                    .title(title)
                    .description(description)
                    .sections(bodySections.toArray(Node[]::new))
                    .build();
            applyStyles(root);
            return root;
        }
    }
}
