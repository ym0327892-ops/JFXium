package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * HBoxAnt - 继承式 HBox 容器（M19.36 引入）。
 *
 * <p>水平布局的双工厂模式——既能当工厂用，也能被业务继承。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * HBoxAnt toolbar = HBoxAnt.create()
 *     .spacing(8)
 *     .align(Pos.CENTER_LEFT)
 *     .background(Background.SUBTLE)
 *     .children(searchField, filterCombo, addBtn);
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class ToolBar extends HBoxAnt {
 *     public ToolBar() {
 *         spacing(8);
 *         padding(8, 16, 8, 16);
 *         align(Pos.CENTER_LEFT);
 *         children(searchField, filterCombo, addBtn);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~140 行重复模板代码，行为 100% 等价原 HBoxAnt）</li>
 *   <li><b>双重身份</b>：是 HBox 也是工厂——继承自 {@link HBox}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class HBoxAnt extends HBox implements LayoutCommon<HBoxAnt> {

    /** 工厂入口。 */
    public static HBoxAnt create() {
        return new HBoxAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static HBoxAnt create(Node... children) {
        return new HBoxAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public HBoxAnt() {
        super();
    }

    public HBoxAnt(Node... children) {
        super(children);
    }

    public HBoxAnt(double spacing) {
        super(spacing);
    }

    public HBoxAnt(double spacing, Node... children) {
        super(spacing, children);
    }

    // ============================================================
    // 流式 API（HBox 特有业务方法）
    // ============================================================

    /** 设置子节点之间的水平间距。 */
    public HBoxAnt spacing(double spacing) {
        setSpacing(spacing);
        return this;
    }

    /** 设置子节点对齐方式（默认 {@code CENTER_LEFT}）。 */
    public HBoxAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    /** 批量添加子节点（null 节点会被过滤）。 */
    public HBoxAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** HBox 是否让子节点垂直撑满（默认 true）。 */
    public HBoxAnt fillHeight(boolean fill) {
        setFillHeight(fill);
        return this;
    }

    /** 给指定子节点设置水平拉伸优先级。 */
    public HBoxAnt hgrow(Node child, Priority priority) {
        HBox.setHgrow(child, priority);
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public HBoxAnt margin(Node child, Insets margin) {
        HBox.setMargin(child, margin);
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<HBoxAnt> 默认实现
    // （节省 ~140 行重复模板代码，行为 100% 等价原 HBoxAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public HBoxAnt build() {
        return this;
    }
}
