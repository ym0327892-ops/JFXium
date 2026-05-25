package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.SkeletonAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Skeleton 骨架屏展示页（M19.10）—— 加载占位标配。
 */
public class SkeletonPage implements ShowcasePage {

    @Override public String   key()      { return "skeleton"; }
    @Override public String   title()    { return "Skeleton 骨架屏"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Skeleton 骨架屏");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("数据加载中的占位 —— 比 Spinner 更优雅，预览出页面结构。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionVariants(),
                        sectionAnimation(),
                        sectionTextLines(),
                        sectionUserCard(),
                        sectionTableRows()
                )
                .build();
    }

    private Node sectionVariants() {
        Node text = SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(240).build();
        Node circle = SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR)
                .width(48).height(48).build();
        Node rect = SkeletonAnt.create().variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(240).height(120).build();
        Node rounded = SkeletonAnt.create().variant(SkeletonAnt.Variant.ROUNDED)
                .width(240).height(120).build();

        VBox col = VBoxBuilder.create().spacing(16).children(
                grayLabel("TEXT — 文本占位"), text,
                grayLabel("CIRCULAR — 头像/图标占位"), circle,
                grayLabel("RECTANGULAR — 图片/卡片占位"), rect,
                grayLabel("ROUNDED — 圆角矩形（按钮/卡片）"), rounded
        ).build();

        return ShowcaseSection.create()
                .title("场景 1：4 种形状（TEXT / CIRCULAR / RECTANGULAR / ROUNDED）")
                .description("用 .variant() 选合适的占位形状")
                .demo(col)
                .code("""
                        SkeletonAnt.create().variant(Variant.TEXT).width(240).build();
                        SkeletonAnt.create().variant(Variant.CIRCULAR).width(48).height(48).build();
                        SkeletonAnt.create().variant(Variant.RECTANGULAR).width(240).height(120).build();
                        SkeletonAnt.create().variant(Variant.ROUNDED).width(240).height(120).build();
                        """)
                .build();
    }

    private Node sectionAnimation() {
        Node anim = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(240).height(40).animated(true).build();
        Node still = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(240).height(40).noAnimation().build();

        VBox col = VBoxBuilder.create().spacing(12).children(
                grayLabel("有动画（默认）—— 闪烁渐变"), anim,
                grayLabel("无动画（.noAnimation()）—— 静态灰块"), still
        ).build();

        return ShowcaseSection.create()
                .title("场景 2：动画开关")
                .description(".animated(false) / .noAnimation() —— 静态版本适合预渲染或减少干扰")
                .demo(col)
                .code("""
                        SkeletonAnt.create().variant(...).animated(true).build();
                        SkeletonAnt.create().variant(...).noAnimation().build();
                        """)
                .build();
    }

    private Node sectionTextLines() {
        VBox lines = VBoxBuilder.create().spacing(8).children(
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(360).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(320).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(280).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(180).build()
        ).build();

        return ShowcaseSection.create()
                .title("场景 3：模拟段落（多行宽度递减）")
                .description("段落最后一行通常较短 —— 用宽度递减的多行 TEXT 模拟")
                .demo(lines)
                .code("""
                        VBox lines = VBoxBuilder.create().spacing(8).children(
                            SkeletonAnt.create().variant(Variant.TEXT).width(360).build(),
                            SkeletonAnt.create().variant(Variant.TEXT).width(320).build(),
                            SkeletonAnt.create().variant(Variant.TEXT).width(280).build(),
                            SkeletonAnt.create().variant(Variant.TEXT).width(180).build()
                        ).build();
                        """)
                .build();
    }

    private Node sectionUserCard() {
        Node avatar = SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR)
                .width(48).height(48).build();
        VBox info = VBoxBuilder.create().spacing(6).children(
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(140).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(220).build()
        ).build();

        HBox row = HBoxBuilder.create().spacing(12).children(avatar, info).build();
        VBox card = CardAnt.create().content(row).bordered(true).build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("场景 4：用户卡占位（admin 用户列表加载中）")
                .description("Avatar(圆) + Title(text) + Subtitle(text) —— 模拟即将出现的内容")
                .demo(card)
                .code("""
                        Node avatar = SkeletonAnt.create().variant(Variant.CIRCULAR).width(48).height(48).build();
                        VBox info = VBoxBuilder.create().spacing(6).children(
                            SkeletonAnt.create().variant(Variant.TEXT).width(140).build(),
                            SkeletonAnt.create().variant(Variant.TEXT).width(220).build()
                        ).build();
                        HBox row = HBoxBuilder.create().spacing(12).children(avatar, info).build();
                        """)
                .build();
    }

    private Node sectionTableRows() {
        VBox rows = new VBox(8);
        for (int i = 0; i < 5; i++) {
            HBox row = new HBox(12);
            row.getChildren().addAll(
                    SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(60).build(),
                    SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(120).build(),
                    SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(180).build(),
                    SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(80).build()
            );
            rows.getChildren().add(row);
        }

        return ShowcaseSection.create()
                .title("场景 5：表格行占位（admin 列表加载中）")
                .description("用多个等高的 TEXT 占位模拟表格行 —— 等数据回来再 setData()")
                .demo(rows)
                .code("""
                        for (int i = 0; i < 5; i++) {
                            HBox row = new HBox(12);
                            row.getChildren().addAll(
                                SkeletonAnt.create().variant(Variant.TEXT).width(60).build(),
                                SkeletonAnt.create().variant(Variant.TEXT).width(120).build(),
                                ...
                            );
                            rows.getChildren().add(row);
                        }
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }
}
