package org.openkawu.jfxium.component.layout;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 绝对定位布局组件 - 对标 CSS position: absolute。
 *
 * <p><b>定位</b>：绝对定位容器，继承自 JavaFX {@link AnchorPane}。
 * 子节点通过上下左右锚定值精确定位，常用于复杂仪表盘、拖拽设计器等需要
 * 像素级控制的场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>锚定</b>：anchor(node, top, right, bottom, left) 四边锚定</li>
 *   <li><b>快捷锚定</b>：topAnchor / bottomAnchor / leftAnchor / rightAnchor 单边设置</li>
 *   <li><b>居中</b>：center(node) 子节点居中</li>
 *   <li><b>全填充</b>：fill(node) 子节点填满容器</li>
 *   <li><b>子节点</b>：children(Node...) 批量添加</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#ANCHOR_PANE} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础绝对定位
 * AnchorPaneAnt pane = AnchorPaneAnt.create()
 *     .children(header, sidebar, content)
 *     .topAnchor(header, 0.0)
 *     .leftAnchor(sidebar, 0.0)
 *     .bottomAnchor(sidebar, 0.0)
 *     .anchor(content, 60.0, 0.0, 0.0, 200.0) // top, right, bottom, left
 *     .build();
 *
 * // 居中弹窗
 * AnchorPaneAnt dialog = AnchorPaneAnt.create()
 *     .children(overlay, popup)
 *     .fill(overlay)
 *     .center(popup)
 *     .build();
 * }</pre>
 */
public class AnchorPaneAnt extends AnchorPane {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static AnchorPaneAnt create() {
        return new AnchorPaneAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public AnchorPaneAnt() {
        super();
        getStyleClass().add(JfxStyles.ANCHOR_PANE);
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public AnchorPaneAnt anchor(Node node, Double top, Double right, Double bottom, Double left) {
        if (node != null) {
            if (top != null) setTopAnchor(node, top);
            if (right != null) setRightAnchor(node, right);
            if (bottom != null) setBottomAnchor(node, bottom);
            if (left != null) setLeftAnchor(node, left);
        }
        return this;
    }

    public AnchorPaneAnt topAnchor(Node node, double value) {
        if (node != null) setTopAnchor(node, value);
        return this;
    }

    public AnchorPaneAnt bottomAnchor(Node node, double value) {
        if (node != null) setBottomAnchor(node, value);
        return this;
    }

    public AnchorPaneAnt leftAnchor(Node node, double value) {
        if (node != null) setLeftAnchor(node, value);
        return this;
    }

    public AnchorPaneAnt rightAnchor(Node node, double value) {
        if (node != null) setRightAnchor(node, value);
        return this;
    }

    public AnchorPaneAnt center(Node node) {
        if (node != null) {
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
        return this;
    }

    public AnchorPaneAnt fill(Node node) {
        return center(node);
    }

    public AnchorPaneAnt children(Node... nodes) {
        if (nodes != null) {
            getChildren().addAll(nodes);
        }
        return this;
    }

    public AnchorPaneAnt add(Node node) {
        if (node != null) {
            getChildren().add(node);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    /** 追加一个 styleClass（幂等——重复调不会重复挂）。 */
    public AnchorPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass（变长重载）。 */
    public AnchorPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public AnchorPaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public AnchorPaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public AnchorPaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public AnchorPaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public AnchorPaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public AnchorPaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public AnchorPaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public AnchorPaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public AnchorPaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public AnchorPaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public AnchorPaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public AnchorPaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public AnchorPaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public AnchorPaneAnt id(String id) { setId(id); return this; }

    /** 同时设置首选宽高。 */
    public AnchorPaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最大宽高。 */
    public AnchorPaneAnt maxSize(double w, double h) { setMaxSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public AnchorPaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

    // ============================================================
    // 构建
    // ============================================================

    public AnchorPaneAnt build() {
        return this;
    }
}
