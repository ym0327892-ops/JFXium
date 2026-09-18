package org.openkawu.jfxium.component.control;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 自绘图形容器组件 - 对标 HTML5 Canvas。
 *
 * <p><b>定位</b>：2D 绘图容器，继承自 JavaFX {@link Canvas}。
 * 提供 Builder 流式 API + 快捷绘图方法，支持自定义绘制回调。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>尺寸</b>：size(w, h) 设置画布大小</li>
 *   <li><b>自定义绘制</b>：onDraw(GraphicsContext) 回调，每次重绘时触发</li>
 *   <li><b>快捷方法</b>：clear()、fill(Color)、stroke(Color) 等常用操作</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#CANVAS} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础画布 + 自定义绘制
 * CanvasAnt canvas = CanvasAnt.create()
 *     .size(400, 300)
 *     .onDraw(gc -> {
 *         gc.setFill(Color.LIGHTBLUE);
 *         gc.fillRect(50, 50, 100, 100);
 *         gc.setStroke(Color.BLUE);
 *         gc.strokeRect(50, 50, 100, 100);
 *     })
 *     .build();
 *
 * // 动态重绘
 * CanvasAnt chart = CanvasAnt.create()
 *     .size(800, 400)
 *     .onDraw(gc -> drawChart(gc, data))
 *     .build();
 * chart.redraw(); // 手动触发重绘
 * }</pre>
 */
public class CanvasAnt extends Canvas {

    private Consumer<GraphicsContext> onDraw;

    // ============================================================
    // 工厂入口
    // ============================================================

    public static CanvasAnt create() {
        return new CanvasAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public CanvasAnt() {
        super();
        getStyleClass().add(JfxStyles.CANVAS);
    }

    public CanvasAnt(double width, double height) {
        super(width, height);
        getStyleClass().add(JfxStyles.CANVAS);
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public CanvasAnt size(double width, double height) {
        setWidth(width);
        setHeight(height);
        return this;
    }

    public CanvasAnt onDraw(Consumer<GraphicsContext> onDraw) {
        this.onDraw = onDraw;
        if (onDraw != null) {
            // 尺寸变化时自动重绘
            widthProperty().addListener((obs, old, val) -> redraw());
            heightProperty().addListener((obs, old, val) -> redraw());
            // 初始绘制
            javafx.application.Platform.runLater(this::redraw);
        }
        return this;
    }

    // ============================================================
    // 快捷绘图
    // ============================================================

    public GraphicsContext gc() {
        return getGraphicsContext2D();
    }

    public CanvasAnt clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        return this;
    }

    public CanvasAnt fill(Color color) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, getWidth(), getHeight());
        return this;
    }

    public CanvasAnt stroke(Color color) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(color);
        return this;
    }

    // ============================================================
    // 重绘
    // ============================================================

    public void redraw() {
        if (onDraw != null) {
            onDraw.accept(getGraphicsContext2D());
        }
    }

    // ============================================================
    // 构建
    // ============================================================

    public CanvasAnt build() {
        return this;
    }
}
