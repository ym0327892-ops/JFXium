package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.CanvasAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Canvas 画布 —— 基础绘图 / 自定义尺寸。
 */
public class CanvasExamplePage extends VBoxAnt {

    public CanvasExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Canvas 画布")
                .description("JavaFX Canvas 的 Builder 封装，支持自定义宽高和绘图回调。")
                .sections(
                        basicSection(),
                        sizeSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = CanvasAnt.create()
                .size(300, 150)
                .onDraw(gc -> {
                    // 背景
                    gc.setFill(Color.web("#f0f5ff"));
                    gc.fillRect(0, 0, 300, 150);

                    // 矩形
                    gc.setFill(Color.web("#1677ff"));
                    gc.fillRect(20, 20, 80, 60);

                    // 圆形
                    gc.setFill(Color.web("#52c41a"));
                    gc.fillOval(140, 20, 70, 70);

                    // 文字
                    gc.setFill(Color.web("#333333"));
                    gc.strokeText("Canvas 基础绘图", 230, 80);

                    // 线条
                    gc.setStroke(Color.web("#ff4d4f"));
                    gc.setLineWidth(2);
                    gc.strokeLine(20, 120, 280, 120);
                })
                .build();

        String code = """
                CanvasAnt.create()
                    .width(300).height(150)
                    .draw(gc -> {
                        gc.setFill(Color.web("#1677ff"));
                        gc.fillRect(20, 20, 80, 60);
                        gc.setFill(Color.web("#52c41a"));
                        gc.fillOval(140, 20, 70, 70);
                    })
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础绘图",
                "width()/height() 设置画布尺寸；draw(gc -> {}) 在回调中使用 GraphicsContext 绘制图形。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.column(
                CanvasAnt.create()
                        .size(400, 80)
                        .onDraw(gc -> {
                            gc.setFill(Color.web("#e6f4ff"));
                            gc.fillRect(0, 0, 400, 80);
                            gc.setFill(Color.web("#1677ff"));
                            gc.fillText("宽画布：400×80", 10, 30);
                            gc.strokeLine(10, 50, 390, 50);
                        })
                        .build(),
                CanvasAnt.create()
                        .size(200, 200)
                        .onDraw(gc -> {
                            gc.setFill(Color.web("#fff7e6"));
                            gc.fillRect(0, 0, 200, 200);
                            gc.setFill(Color.web("#fa8c16"));
                            gc.fillText("方画布：200×200", 10, 30);
                            gc.setFill(Color.web("#fa8c16"));
                            gc.fillOval(50, 60, 100, 100);
                        })
                        .build()
        );
        String code = """
                CanvasAnt.create()
                    .width(400).height(80)
                    .draw(gc -> { ... })
                    .build();

                CanvasAnt.create()
                    .width(200).height(200)
                    .draw(gc -> { ... })
                    .build();
                """;
        return Demos.sectionWithCode("2. 自定义尺寸",
                "宽高可任意设置，适用于图表绘制、自定义涂鸦、进度条可视化等场景。",
                code, demo);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Canvas 的 3 个维度：图形 / 笔触粗细 / 颜色。
     *
     * <p>CanvasAnt 无 Controller（继承自 JavaFX Canvas），所有属性（size / onDraw）
     * 都是 build-time。所以采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都
     * 重新 {@code build()} + 重新设置 onDraw —— Canvas 是 Leaf 节点，重建开销极低。</p>
     *
     * <p>说明：onDraw 回调里的颜色 / 线宽是回调捕获的闭包值，每次 build 都用 binder 当前值
     * 重新生成回调，确保视觉与控件一致。</p>
     */
    private Node playgroundSection() {
        Binder<String> shape     = PlayGround.binder("rect");
        Binder<String> lineWidth = PlayGround.binder("2");
        Binder<String> color     = PlayGround.binder("#1677ff");

        Supplier<Node> factory = () -> {
            double w = parseLineWidth(lineWidth.get());
            Color c  = parseColor(color.get());
            String s = shape.get();
            return CanvasAnt.create()
                    .size(320, 160)
                    .onDraw(gc -> {
                        // 背景
                        gc.setFill(Color.web("#fafafa"));
                        gc.fillRect(0, 0, 320, 160);

                        gc.setFill(c);
                        gc.setStroke(c);
                        gc.setLineWidth(w);

                        switch (s) {
                            case "circle" -> {
                                gc.fillOval(110, 30, 100, 100);
                            }
                            case "line" -> {
                                gc.strokeLine(20, 80, 300, 80);
                            }
                            default -> {
                                gc.fillRect(110, 30, 100, 100);
                            }
                        }
                        // 文本
                        gc.setFill(Color.web("#333333"));
                        gc.setStroke(Color.web("#333333"));
                        gc.setLineWidth(1);
                        gc.strokeText("形状：" + s + " · 线宽：" + w + " · 颜色：" + color.get(), 12, 145);
                    })
                    .build();
        };

        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Canvas 的图形 / 笔触粗细 / 颜色 —— CanvasAnt 无 Controller，所有属性变更均通过 build + onDraw 重置生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("图形", PlayGround.segmented(shape,
                                PlayGround.entry("rect",   "矩形"),
                                PlayGround.entry("circle", "圆形"),
                                PlayGround.entry("line",   "线条"))),
                        PlayGround.row("笔触粗细", PlayGround.textField(lineWidth, "2", "1~8")),
                        PlayGround.row("颜色", PlayGround.textField(color, "#1677ff", "#RRGGBB"))));
    }

    private static double parseLineWidth(String v) {
        if (v == null) return 2.0;
        try {
            double d = Double.parseDouble(v.trim());
            if (Double.isNaN(d) || Double.isInfinite(d)) return 2.0;
            return Math.max(1.0, Math.min(8.0, d));
        } catch (NumberFormatException e) {
            return 2.0;
        }
    }

    private static Color parseColor(String v) {
        if (v == null || v.isBlank()) return Color.web("#1677ff");
        try {
            return Color.web(v.trim());
        } catch (IllegalArgumentException e) {
            return Color.web("#1677ff");
        }
    }
}
