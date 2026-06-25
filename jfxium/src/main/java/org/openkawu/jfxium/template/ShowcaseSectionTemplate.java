package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ShowcaseSectionTemplate - 示例页/文档页的通用 section 壳。
 *
 * <p>JFXium 的许多示例页都在重复同一层结构：
 * 「小标题 + 说明 + 演示区」。这个模板把这层公共骨架收口，方便 demo /
 * 文档页复用，同时保持外层是否包 {@link org.openkawu.jfxium.component.composite.GroupBoxAnt}
 * 的自由度。</p>
 *
 * <p>它只负责 section 的内容编排，不处理代码展开、折叠状态或样式细节；
 * 上层可以继续在此基础上叠加 code toggle、copy block 等示例交互。</p>
 */
public final class ShowcaseSectionTemplate {

    private ShowcaseSectionTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private static final double HEADER_GAP = 4;
        private static final double BODY_GAP = 12;
        private static final double SECTION_GAP = 12;

        private String title = "";
        private String description = null;
        private final List<Node> bodyNodes = new ArrayList<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title);
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder body(Node... nodes) {
            if (nodes != null) {
                for (Node node : nodes) {
                    if (node != null) {
                        bodyNodes.add(node);
                    }
                }
            }
            return this;
        }

        public VBox build() {
            VBox header = buildHeader();
            VBox body = VBarAnt.create()
                    .compact()
                    .gap(BODY_GAP)
                    .top(bodyNodes.toArray(Node[]::new))
                    .build();

            VBox section = VBarAnt.create()
                    .compact()
                    .gap(SECTION_GAP)
                    .top(header)
                    .bottom(body)
                    .build();

            applyStyles(section);
            return section;
        }

        private VBox buildHeader() {
            if (!hasTitle() && !hasDescription()) {
                return null;
            }

            VBox header = VBarAnt.create()
                    .compact()
                    .gap(HEADER_GAP)
                    .top(buildTitle(), buildDescription())
                    .build();
            return header;
        }

        private Node buildTitle() {
            if (hasTitle()) {
                return TypographyAnt.title(title, 5).build();
            }
            return null;
        }

        private Node buildDescription() {
            if (hasDescription()) {
                Label descLabel = TypographyAnt.text(description)
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build();
                descLabel.setWrapText(true);
                return descLabel;
            }
            return null;
        }

        private boolean hasTitle() {
            return title != null && !title.isBlank();
        }

        private boolean hasDescription() {
            return description != null && !description.isBlank();
        }
    }
}
