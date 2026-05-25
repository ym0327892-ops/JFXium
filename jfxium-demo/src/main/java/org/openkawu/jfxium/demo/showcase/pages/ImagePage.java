package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ImageAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Image 图片展示页（M19.15）。
 *
 * <p>说明：所有 src 用占位 URL；如果运行时无法访问网络，会自动 fallback 到占位图。</p>
 */
public class ImagePage implements ShowcasePage {

    @Override public String   key()      { return "image"; }
    @Override public String   title()    { return "Image 图片"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    private static final String DEMO_URL = "https://via.placeholder.com/200x150/4096ff/ffffff?text=JFXium";

    @Override
    public Node getView() {
        Label pageTitle = new Label("Image 图片");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("图片展示 + 加载占位 + 错误兜底 + 圆角 / objectFit。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionFallback(),
                        sectionBorderRadius(),
                        sectionObjectFit()
                )
                .build();
    }

    private Node sectionBasic() {
        Node img = ImageAnt.create()
                .src(DEMO_URL)
                .width(200).height(150)
                .alt("JFXium Logo")
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础（src + 尺寸）")
                .description(".src + .width/.height —— 加载远程图片，加载中显示占位")
                .demo(img)
                .code("""
                        Node img = ImageAnt.create()
                            .src("https://example.com/logo.png")
                            .width(200).height(150)
                            .alt("JFXium Logo")
                            .build();
                        """)
                .build();
    }

    private Node sectionFallback() {
        Node img = ImageAnt.create()
                .src("https://example.com/this-url-does-not-exist-404.png")
                .fallback("https://via.placeholder.com/200x150/cccccc/666666?text=404")
                .width(200).height(150)
                .alt("加载失败")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：加载失败 fallback")
                .description(".fallback(url) —— 主图加载失败时显示备用图，避免空白")
                .demo(img)
                .code("""
                        ImageAnt.create()
                            .src("https://example.com/maybe-broken.png")
                            .fallback("https://example.com/placeholder.png")
                            .width(200).height(150)
                            .build();
                        """)
                .build();
    }

    private Node sectionBorderRadius() {
        Node squareImg = ImageAnt.create().src(DEMO_URL).width(140).height(140).build();
        Node roundedImg = ImageAnt.create().src(DEMO_URL).width(140).height(140).borderRadius(8).build();
        Node circleImg = ImageAnt.create().src(DEMO_URL).width(140).height(140).borderRadius(70).build();

        HBox row = HBoxBuilder.create().spacing(20).children(squareImg, roundedImg, circleImg).build();

        return ShowcaseSection.create()
                .title("场景 3：圆角 / 圆形")
                .description(".borderRadius(N) —— 设为半径 = 一半时变成圆形（用作头像）")
                .demo(row)
                .code("""
                        ImageAnt.create().src(url).width(140).height(140).build();                  // 方形
                        ImageAnt.create().src(url).width(140).height(140).borderRadius(8).build();  // 圆角
                        ImageAnt.create().src(url).width(140).height(140).borderRadius(70).build(); // 圆形
                        """)
                .build();
    }

    private Node sectionObjectFit() {
        Node fill = ImageAnt.create().src(DEMO_URL).width(200).height(120).objectFit("fill").build();
        Node contain = ImageAnt.create().src(DEMO_URL).width(200).height(120).objectFit("contain").build();
        Node cover = ImageAnt.create().src(DEMO_URL).width(200).height(120).objectFit("cover").build();

        VBox col = VBoxBuilder.create().spacing(12).children(
                grayLabel("fill — 拉伸填满（可能变形）"), fill,
                grayLabel("contain — 完整显示，留白"), contain,
                grayLabel("cover — 填满裁剪（不变形，会裁掉一部分）"), cover
        ).build();

        return ShowcaseSection.create()
                .title("场景 4：objectFit 三种填充模式")
                .description(".objectFit(\"fill\" | \"contain\" | \"cover\") —— 与 CSS 同语义")
                .demo(col)
                .code("""
                        ImageAnt.create().src(url).objectFit("fill").build();      // 拉伸
                        ImageAnt.create().src(url).objectFit("contain").build();   // 等比留白
                        ImageAnt.create().src(url).objectFit("cover").build();     // 等比裁剪
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }
}
