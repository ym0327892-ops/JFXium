package org.openkawu.jfxium.demo.showcase;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.CodeBlockAnt;
import org.openkawu.jfxium.component.CollapseAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;

/**
 * Showcase 单个示例区块工具：标题 + 描述 + 演示节点 + 折叠代码块。
 *
 * <p>所有组件展示页都用此工具类构建变体分块，保证视觉风格统一。</p>
 *
 * <p>用法：</p>
 * <pre>{@code
 * Node section = ShowcaseSection.create()
 *     .title("基础用法")
 *     .description("最简单的卡片，只包含标题和内容")
 *     .demo(myCard)                      // 实际渲染的演示节点
 *     .code("VBox card = CardAnt.create()...")    // 对应的源码
 *     .build();
 * }</pre>
 */
public final class ShowcaseSection {

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder {
        private String title = "";
        private String description = "";
        private Node demo;
        private String code;
        private String language = "java";

        private Builder() {}

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder demo(Node demo) { this.demo = demo; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder language(String language) { this.language = language; return this; }

        public Node build() {
            // 标题
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");

            // 描述（可选）
            Label descLabel = description.isEmpty() ? null : new Label(description);
            if (descLabel != null) {
                descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
                descLabel.setWrapText(true);
            }

            // 演示节点容器
            VBox demoBox = VBoxBuilder.create()
                    .spacing(0)
                    .padding(8, 0, 8, 0)
                    .align(Pos.TOP_LEFT)
                    .children(demo)
                    .build();

            // 头部信息（标题 + 描述）
            VBox header = new VBox(4);
            header.getChildren().add(titleLabel);
            if (descLabel != null) header.getChildren().add(descLabel);

            // 折叠代码块
            Node codeNode = null;
            if (code != null && !code.isBlank()) {
                Node codeBlock = CodeBlockAnt.create()
                        .language(language)
                        .content(code.stripIndent())
                        .copyable(true)
                        .build();
                codeNode = CollapseAnt.create()
                        .panel("code", "查看代码", codeBlock)
                        .build();
            }

            // 卡片整体
            VBox content = new VBox(12);
            content.getChildren().addAll(header, demoBox);
            if (codeNode != null) content.getChildren().add(codeNode);

            return CardAnt.create()
                    .content(content)
                    .bordered(true)
                    .shadow(CardAnt.Shadow.SMALL)
                    .build();
        }
    }
}
