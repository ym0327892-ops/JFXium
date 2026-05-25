package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.WatermarkAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * WatermarkAnt 组件展示页（M8）。
 *
 * <p>覆盖 M8 P0 + P2 全部能力：</p>
 * <ul>
 *   <li>文字水印（单行 / 多行）</li>
 *   <li>图片水印</li>
 *   <li>自定义 Node 水印（Supplier 工厂方法）</li>
 *   <li>样式自定义（rotate / opacity / fontSize / gapX / gapY）</li>
 *   <li>防删除保护（preventRemoval）</li>
 * </ul>
 */
public class WatermarkPage implements ShowcasePage {

    @Override public String   key()      { return "watermark"; }
    @Override public String   title()    { return "Watermark 水印"; }
    @Override public Category category() { return Category.OTHER; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Watermark 水印");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("在内容上叠加水印，常用于文档保护、版权声明、防截图溯源等场景。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create()
                .spacing(8)
                .children(pageTitle, pageDesc)
                .build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasicText(),
                        sectionMultiLineText(),
                        sectionCustomStyle(),
                        sectionCustomNode(),
                        sectionPreventRemoval()
                )
                .build();
    }

    /** 创建一块统一的"内容容器"，用作水印底层内容。 */
    private VBox sampleContent(String... lines) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setMinHeight(160);
        box.setPrefHeight(160);
        box.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-muted;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;"
        );
        for (String line : lines) {
            Label l = new Label(line);
            l.setStyle("-fx-text-fill: -color-fg-muted;");
            box.getChildren().add(l);
        }
        return box;
    }

    // ============================================================
    // 1. 单行文字水印（最常用）
    // ============================================================
    private Node sectionBasicText() {
        StackPane wm = WatermarkAnt.create()
                .content(sampleContent("这是一段需要保护的内容", "水印自动平铺，旋转 -22°"))
                .text("机密文档")
                .build();
        wm.setMaxWidth(560);

        return ShowcaseSection.create()
                .title("单行文字水印（最常用）")
                .description("默认旋转 -22° / 透明度 0.15 / 字号 16 / 间距 100×100")
                .demo(wm)
                .code("""
                        StackPane watermarked = WatermarkAnt.create()
                            .content(myContent)        // 必须：被水印覆盖的内容
                            .text("机密文档")           // 文字水印
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 多行文字水印
    // ============================================================
    private Node sectionMultiLineText() {
        StackPane wm = WatermarkAnt.create()
                .content(sampleContent("多行水印示例", "可以同时显示多行文字"))
                .text("JFXium Demo", "内部资料 · 请勿外传")
                .build();
        wm.setMaxWidth(560);

        return ShowcaseSection.create()
                .title("多行文字水印")
                .description("text(String...) 多个参数自动渲染成多行水印（行间距 4px）")
                .demo(wm)
                .code("""
                        StackPane watermarked = WatermarkAnt.create()
                            .content(myContent)
                            .text("JFXium Demo", "内部资料 · 请勿外传")
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 3. 自定义样式（rotate / opacity / fontSize / gap）
    // ============================================================
    private Node sectionCustomStyle() {
        StackPane wm1 = WatermarkAnt.create()
                .content(sampleContent("低透明度 + 大间距", "旋转 -45°"))
                .text("CONFIDENTIAL")
                .rotate(-45)
                .opacity(0.08)
                .fontSize(18)
                .gapX(160)
                .gapY(120)
                .build();
        wm1.setPrefWidth(420);

        StackPane wm2 = WatermarkAnt.create()
                .content(sampleContent("高透明度 + 紧凑", "旋转 0°（水平）"))
                .text("DRAFT")
                .rotate(0)
                .opacity(0.25)
                .fontSize(14)
                .gapX(120)
                .gapY(60)
                .build();
        wm2.setPrefWidth(420);

        HBox grid = new HBox(16);
        grid.getChildren().addAll(wm1, wm2);

        return ShowcaseSection.create()
                .title("自定义样式")
                .description("rotate / opacity / fontSize / gapX / gapY 任意组合")
                .demo(grid)
                .code("""
                        // 大间距、低透明度、倾斜
                        WatermarkAnt.create()
                            .content(myContent)
                            .text("CONFIDENTIAL")
                            .rotate(-45)        // 旋转角度（默认 -22）
                            .opacity(0.08)      // 透明度（默认 0.15）
                            .fontSize(18)       // 字号（默认 16）
                            .gapX(160)          // 水平间距（默认 100）
                            .gapY(120)          // 垂直间距（默认 100）
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 4. 自定义 Node 水印
    // ============================================================
    private Node sectionCustomNode() {
        StackPane wm = WatermarkAnt.create()
                .content(sampleContent("自定义水印节点：可以是任意 Node", "本例用 HBox 组合 Logo + 用户名"))
                .customNode(() -> {
                    HBox box = new HBox(6);
                    box.setAlignment(Pos.CENTER);
                    Label logo = new Label("⬢");
                    logo.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-accent-emphasis;");
                    Label text = new Label("JFXium @user1");
                    text.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-default;");
                    box.getChildren().addAll(logo, text);
                    return box;
                })
                .opacity(0.18)
                .gapX(180)
                .gapY(100)
                .build();
        wm.setMaxWidth(560);

        return ShowcaseSection.create()
                .title("自定义 Node 水印")
                .description("customNode(Supplier<Node>) 接受任意 JavaFX 节点工厂；常用于 Logo + 用户名水印")
                .demo(wm)
                .code("""
                        StackPane watermarked = WatermarkAnt.create()
                            .content(myContent)
                            .customNode(() -> {
                                HBox box = new HBox(6);
                                Label logo = new Label("⬢");
                                Label text = new Label("JFXium @user1");
                                box.getChildren().addAll(logo, text);
                                return box;
                            })
                            .opacity(0.18)
                            .gapX(180)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. 防删除保护
    // ============================================================
    private Node sectionPreventRemoval() {
        StackPane wm = WatermarkAnt.create()
                .content(sampleContent(
                        "防删除保护：水印层被外部移除时会自动恢复",
                        "适用于强制保护场景（文档水印、版权声明）"))
                .text("⚠ 受保护")
                .preventRemoval()           // 启用监听 + 自动恢复
                .opacity(0.12)
                .build();
        wm.setMaxWidth(560);

        return ShowcaseSection.create()
                .title("防删除保护（M8 P2）")
                .description("preventRemoval() 监听 root 子节点变化，水印层被移除时自动重建")
                .demo(wm)
                .code("""
                        StackPane watermarked = WatermarkAnt.create()
                            .content(myContent)
                            .text("⚠ 受保护")
                            .preventRemoval()       // 关键：启用监听
                            .opacity(0.12)
                            .build();
                        // 此后即使有人调用 watermarked.getChildren().remove(...) 移除水印层，
                        // 也会被自动恢复（控制台会打日志）。
                        """)
                .build();
    }
}
