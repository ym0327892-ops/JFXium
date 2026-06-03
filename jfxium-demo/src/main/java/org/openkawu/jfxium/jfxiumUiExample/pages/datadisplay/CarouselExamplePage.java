package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.CarouselAnt;

/**
 * Carousel 走马灯 —— 自动播放 / 手动导航 / 自定义内容。
 */
public class CarouselExamplePage extends VBoxAnt {

    public CarouselExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Carousel 走马灯")
                .description("一组平铺的内容区域，可自动轮播或手动切换。")
                .sections(
                        autoplaySection(),
                        manualSection(),
                        fadeSection()
                )
                .padding(24)
                .build());
    }

    private Node autoplaySection() {
        StackPane carousel = CarouselAnt.create()
                .items(slide("Slide 1"), slide("Slide 2"), slide("Slide 3"), slide("Slide 4"))
                .autoplay()
                .build();
        String code = """
                StackPane carousel = CarouselAnt.create()
                        .items(slide1, slide2, slide3, slide4)
                        .autoplay()
                        .build();
                """;
        return Demos.sectionWithCode("1. 自动播放", "autoplay() 开启自动轮播，鼠标悬停暂停。", code, carousel);
    }

    private Node manualSection() {
        StackPane carousel = CarouselAnt.create()
                .items(slide("Page A"), slide("Page B"), slide("Page C"))
                .build();
        String code = """
                StackPane carousel = CarouselAnt.create()
                        .items(pageA, pageB, pageC)
                        .build();
                """;
        return Demos.sectionWithCode("2. 手动导航", "默认显示箭头和指示点，点击切换。", code, carousel);
    }

    private Node fadeSection() {
        StackPane carousel = CarouselAnt.create()
                .items(slide("Fade 1"), slide("Fade 2"), slide("Fade 3"))
                .effect(CarouselAnt.Effect.FADE)
                .autoplay()
                .build();
        String code = """
                StackPane carousel = CarouselAnt.create()
                        .items(fade1, fade2, fade3)
                        .effect(CarouselAnt.Effect.FADE)
                        .autoplay()
                        .build();
                """;
        return Demos.sectionWithCode("3. 渐变效果", "effect(FADE) 使用淡入淡出切换动画。", code, carousel);
    }

    /** 创建简单的彩色 slide 占位 */
    private Node slide(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("jfx-demo-col-block");
        label.getStyleClass().add("jfx-demo-col-dark");
        label.setMinHeight(200);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }
}
