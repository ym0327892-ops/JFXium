package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.AvatarAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectFeatureTemplate - 项目特性/亮点网格展示模板。
 *
 * <p>用于项目首页、README 展示页、产品介绍页中「核心特性」区域，
 * 把一组特性做成「图标 + 标题 + 说明」的卡片网格，一眼扫完项目卖点。</p>
 *
 * <h2>适用场景</h2>
 * <ul>
 *   <li>项目首页「为什么选择 XXX」区域</li>
 *   <li>产品 Landing Page 的特性矩阵</li>
 *   <li>README / 文档站首页的功能亮点</li>
 *   <li>工程展示页的技术优势总结</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * ProjectFeatureTemplate.create()
 *     .title("核心特性")
 *     .description("JFXium 为 JavaFX 开发者提供的核心能力")
 *     .feature("Builder 模式", "所有组件统一 Builder API，链式调用一目了然", IconAnt.Path.SETTINGS)
 *     .feature("11 套主题", "Ant Design / Material / Shadcn 等风格一键切换", IconAnt.Path.DASHBOARD)
 *     .feature("97+ 组件", "控件、组合、弹层、布局、模板全覆盖", IconAnt.Path.HOME)
 *     .feature("零 FXML", "纯 Java 代码构建 UI，不需要 XML 配置", IconAnt.Path.FILE)
 *     .onAction(key -> System.out.println("clicked: " + key))
 *     .build();
 * }</pre>
 */
public final class ProjectFeatureTemplate {

    // i18n keys: project.feature_title, project.feature_description

    private ProjectFeatureTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Feature(String key, String title, String description, IconAnt.Path icon) {}

        private String title = null;
        private String description = null;
        private int columns = 2;
        private double gap = 16;
        private final List<Feature> features = new ArrayList<>();
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.feature_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.feature_description"));
            return this;
        }

        public Builder columns(int columns) {
            this.columns = (int) TextUtils.ensureAtLeastOne(columns);
            return this;
        }

        public Builder gap(double gap) {
            this.gap = TextUtils.safeNonNegative(gap, 16);
            return this;
        }

        public Builder onAction(Consumer<String> handler) {
            this.onAction = handler;
            return this;
        }

        public Builder feature(String title, String description, IconAnt.Path icon) {
            return feature(null, title, description, icon);
        }

        public Builder feature(String key, String title, String description, IconAnt.Path icon) {
            features.add(new Feature(
                    TextUtils.safeText(key),
                    TextUtils.safeText(title),
                    TextUtils.safeText(description),
                    icon != null ? icon : IconAnt.Path.DASHBOARD
            ));
            return this;
        }

        public VBox build() {
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .children(buildHeader(), buildGrid())
                    .build();
            applyStyles(root);
            return root;
        }

        private VBox buildHeader() {
            String resolvedTitle = TextUtils.safeText(title, Messages.get("project.feature_title"));
            String resolvedDescription = TextUtils.safeText(description, Messages.get("project.feature_description"));
            return VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(resolvedTitle, 4).build(),
                            TypographyAnt.text(resolvedDescription)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();
        }

        private Node buildGrid() {
            if (features.isEmpty()) {
                return VBoxAnt.create().build();
            }

            GridAnt.Row row = GridAnt.row();
            row.align(Pos.TOP_LEFT);
            int span = (int) TextUtils.ensureAtLeastOne(24 / columns);
            for (Feature feat : features) {
                row.col(GridAnt.col(buildCard(feat))
                        .xs(24)
                        .sm(24)
                        .md(span)
                        .lg(span)
                        .xl(span)
                        .xxl(span));
            }

            return GridAnt.create()
                    .gutter(gap)
                    .responsive()
                    .row(row)
                    .build();
        }

        private Node buildCard(Feature feat) {
            Node iconNode = AvatarAnt.create()
                    .icon(IconAnt.path(feat.icon(), 18))
                    .shape(AvatarAnt.Shape.SQUARE)
                    .size(40)
                    .build();

            Node titleNode = TypographyAnt.text(feat.title()).build();
            Node descNode = TypographyAnt.text(feat.description())
                    .type(TypographyAnt.TextColor.SECONDARY)
                    .build();

            HBoxAnt leadRow = HBoxAnt.create()
                    .spacing(12)
                    .align(Pos.CENTER_LEFT)
                    .children(
                            iconNode,
                            VBoxAnt.create()
                                    .spacing(4)
                                    .children(titleNode, descNode)
                                    .build()
                    );

            VBox cardBody = VBoxAnt.create()
                    .spacing(0)
                    .children(leadRow.build())
                    .build();

            if (onAction != null && !feat.key().isEmpty()) {
                cardBody.setOnMouseClicked(e -> onAction.accept(feat.key()));
                cardBody.getStyleClass().add(JfxStyles.FEATURE_CARD_CLICKABLE);
            }

            return SurfaceAnt.create()
                    .bordered(true)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .content(cardBody)
                    .gap(8)
                    .build();
        }
    }
}
