package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.openkawu.jfxium.component.CarouselAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Carousel 走马灯展示页（M19.11）。
 */
public class CarouselPage implements ShowcasePage {

    @Override public String   key()      { return "carousel"; }
    @Override public String   title()    { return "Carousel 走马灯"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Carousel 走马灯");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("循环播放一组内容 —— 首页 banner、产品图、轮播广告。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionAutoplay(),
                        sectionFadeEffect(),
                        sectionNoArrows(),
                        sectionDotPosition()
                )
                .build();
    }

    private static Node slide(String text, String bg) {
        StackPane s = new StackPane(new Label(text));
        s.setStyle("-fx-background-color: " + bg + "; -fx-min-height: 180; -fx-pref-height: 180;");
        s.setAlignment(Pos.CENTER);
        ((Label) s.getChildren().get(0)).setStyle(
                "-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: 700;");
        return s;
    }

    private Node sectionBasic() {
        Node c = CarouselAnt.create()
                .items(
                        slide("Slide 1", "-color-accent-emphasis"),
                        slide("Slide 2", "-color-success-emphasis"),
                        slide("Slide 3", "-color-warning-emphasis"),
                        slide("Slide 4", "-color-danger-emphasis")
                )
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(560);

        return ShowcaseSection.create()
                .title("场景 1：基础（默认带左右箭头 + 底部圆点）")
                .description("默认 SCROLL 滑动效果，箭头切换 + 底部圆点指示")
                .demo(c)
                .code("""
                        Node c = CarouselAnt.create()
                            .items(slide1, slide2, slide3, slide4)
                            .build();
                        """)
                .build();
    }

    private Node sectionAutoplay() {
        Node c = CarouselAnt.create()
                .items(
                        slide("自动 1", "-color-accent-emphasis"),
                        slide("自动 2", "-color-success-emphasis"),
                        slide("自动 3", "-color-warning-emphasis")
                )
                .autoplay()
                .autoplayInterval(Duration.seconds(2))
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(560);

        return ShowcaseSection.create()
                .title("场景 2：自动播放（2 秒一张）")
                .description(".autoplay() + .autoplayInterval(Duration) —— 首页 banner 标配")
                .demo(c)
                .code("""
                        CarouselAnt.create()
                            .items(...)
                            .autoplay()
                            .autoplayInterval(Duration.seconds(2))
                            .build();
                        """)
                .build();
    }

    private Node sectionFadeEffect() {
        Node c = CarouselAnt.create()
                .items(
                        slide("淡入 1", "-color-accent-emphasis"),
                        slide("淡入 2", "-color-success-emphasis"),
                        slide("淡入 3", "-color-warning-emphasis")
                )
                .effect(CarouselAnt.Effect.FADE)
                .autoplay()
                .autoplayInterval(Duration.seconds(2))
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(560);

        return ShowcaseSection.create()
                .title("场景 3：淡入淡出效果（FADE）")
                .description(".effect(Effect.FADE) —— 切换时透明度过渡，更柔和")
                .demo(c)
                .code("""
                        CarouselAnt.create()
                            .items(...)
                            .effect(CarouselAnt.Effect.FADE)
                            .autoplay()
                            .build();
                        """)
                .build();
    }

    private Node sectionNoArrows() {
        Node c = CarouselAnt.create()
                .items(
                        slide("仅圆点 1", "-color-accent-emphasis"),
                        slide("仅圆点 2", "-color-success-emphasis"),
                        slide("仅圆点 3", "-color-warning-emphasis")
                )
                .noArrows()
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(560);

        return ShowcaseSection.create()
                .title("场景 4：无箭头（noArrows）")
                .description("只保留底部圆点，更简洁；适合次要 banner")
                .demo(c)
                .code("""
                        CarouselAnt.create()
                            .items(...)
                            .noArrows()
                            .build();
                        """)
                .build();
    }

    private Node sectionDotPosition() {
        Node top = CarouselAnt.create()
                .items(
                        slide("TOP 1", "-color-accent-emphasis"),
                        slide("TOP 2", "-color-success-emphasis"),
                        slide("TOP 3", "-color-warning-emphasis")
                )
                .dotPosition(CarouselAnt.DotPosition.TOP)
                .build();
        ((javafx.scene.layout.Region) top).setMaxWidth(560);

        Node center = CarouselAnt.create()
                .items(
                        slide("CENTER 1", "-color-accent-emphasis"),
                        slide("CENTER 2", "-color-success-emphasis"),
                        slide("CENTER 3", "-color-warning-emphasis")
                )
                .dotPosition(CarouselAnt.DotPosition.CENTER)
                .build();
        ((javafx.scene.layout.Region) center).setMaxWidth(560);

        VBox both = new VBox(16, top, center);

        return ShowcaseSection.create()
                .title("场景 5：dots 位置（TOP / CENTER / BOTTOM）")
                .description(".dotPosition(...) —— 默认 BOTTOM；TOP 与 CENTER 适配特殊 banner 需求")
                .demo(both)
                .code("""
                        CarouselAnt.create()
                            .items(...)
                            .dotPosition(CarouselAnt.DotPosition.TOP)     // 顶部
                            .dotPosition(CarouselAnt.DotPosition.CENTER)  // 中间
                            .dotPosition(CarouselAnt.DotPosition.BOTTOM)  // 底部（默认）
                            .build();
                        """)
                .build();
    }
}
