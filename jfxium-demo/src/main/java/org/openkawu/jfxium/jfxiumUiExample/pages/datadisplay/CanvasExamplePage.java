package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.openkawu.jfxium.component.control.CanvasAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
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
                        sizeSection()
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
}
