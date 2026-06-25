package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.CarouselAnt;

import java.util.function.Supplier;

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
                        fadeSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node autoplaySection() {
        Node carousel = CarouselAnt.create()
                .items(slide("Slide 1"), slide("Slide 2"), slide("Slide 3"), slide("Slide 4"))
                .autoplay()
                .build();
        String code = """
                Node carousel = CarouselAnt.create()
                        .items(slide1, slide2, slide3, slide4)
                        .autoplay()
                        .build();
                """;
        return Demos.sectionWithCode("1. 自动播放", "autoplay() 开启自动轮播，鼠标悬停暂停。", code, carousel);
    }

    private Node manualSection() {
        Node carousel = CarouselAnt.create()
                .items(slide("Page A"), slide("Page B"), slide("Page C"))
                .build();
        String code = """
                Node carousel = CarouselAnt.create()
                        .items(pageA, pageB, pageC)
                        .build();
                """;
        return Demos.sectionWithCode("2. 手动导航", "默认显示箭头和指示点，点击切换。", code, carousel);
    }

    private Node fadeSection() {
        Node carousel = CarouselAnt.create()
                .items(slide("Fade 1"), slide("Fade 2"), slide("Fade 3"))
                .effect(CarouselAnt.Effect.FADE)
                .autoplay()
                .build();
        String code = """
                Node carousel = CarouselAnt.create()
                        .items(fade1, fade2, fade3)
                        .effect(CarouselAnt.Effect.FADE)
                        .autoplay()
                        .build();
                """;
        return Demos.sectionWithCode("3. 渐变效果", "effect(FADE) 使用淡入淡出切换动画。", code, carousel);
    }

    /**
     * 4. 交互演示 —— 通过左侧控件实时改变 Carousel 的自动播放 / 切换效果 / 指示点位置。
     *
     * <p>CarouselAnt 无 Controller，所有属性变更均通过 build 重建生效。</p>
     */
    private Node playgroundSection() {
        Binder<String> autoplayBinder     = PlayGround.binder("off");
        Binder<String> effectBinder       = PlayGround.binder("scroll");
        Binder<String> dotPositionBinder  = PlayGround.binder("bottom");

        Supplier<Node> factory = () -> {
            CarouselAnt.Builder b = CarouselAnt.create()
                    .items(slide("Slide A"), slide("Slide B"), slide("Slide C"), slide("Slide D"))
                    .effect(parseEffect(effectBinder.get()))
                    .dotPosition(parseDotPosition(dotPositionBinder.get()));
            if (parseBool(autoplayBinder.get())) {
                b = b.autoplay();
            }
            return b.build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Carousel 的自动播放 / 切换效果 / 指示点位置 —— CarouselAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("自动播放", PlayGround.segmented(autoplayBinder,
                                PlayGround.entry("off", "关"),
                                PlayGround.entry("on",  "开"))),
                        PlayGround.row("切换效果", PlayGround.segmented(effectBinder,
                                PlayGround.entry("scroll", "滑动"),
                                PlayGround.entry("fade",   "渐变"))),
                        PlayGround.row("指示点位置", PlayGround.segmented(dotPositionBinder,
                                PlayGround.entry("top",    "顶部"),
                                PlayGround.entry("center", "中间"),
                                PlayGround.entry("bottom", "底部")))));
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

    private static CarouselAnt.Effect parseEffect(String v) {
        if (v == null) return CarouselAnt.Effect.SCROLL;
        return "fade".equalsIgnoreCase(v) ? CarouselAnt.Effect.FADE : CarouselAnt.Effect.SCROLL;
    }

    private static CarouselAnt.DotPosition parseDotPosition(String v) {
        if (v == null) return CarouselAnt.DotPosition.BOTTOM;
        return switch (v) {
            case "top"    -> CarouselAnt.DotPosition.TOP;
            case "center" -> CarouselAnt.DotPosition.CENTER;
            default       -> CarouselAnt.DotPosition.BOTTOM;
        };
    }

    /** 创建简单的彩色 slide 占位 */
    private Node slide(String text) {
        Label label = TypographyAnt.text(text).build();
        label.getStyleClass().add("jfx-demo-col-block");
        label.getStyleClass().add("jfx-demo-col-dark");
        label.setMinHeight(200);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }
}
