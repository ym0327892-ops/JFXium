package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
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
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 AnchorPaneAnt）</li>
 *   <li><b>双重身份</b>：是 AnchorPane 也是工厂——继承自 {@link AnchorPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class AnchorPaneAnt extends AbstractAnchorPaneAnt<AnchorPaneAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static AnchorPaneAnt create() {
        return new AnchorPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public AnchorPaneAnt() {
        super();
        getStyleClass().add(JfxStyles.ANCHOR_PANE);
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<AnchorPaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 AnchorPaneAnt）
    // ============================================================

}
