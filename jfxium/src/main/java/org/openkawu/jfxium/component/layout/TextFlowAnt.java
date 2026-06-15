package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * TextFlowAnt - 继承式 TextFlow 容器。
 *
 * <p>富文本流式布局的双工厂模式——典型场景：多段文本拼接（不同样式 Label）、
 * 内联图标+文字混合排版、图文混排。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * TextFlowAnt flow = TextFlowAnt.create()
 *     .lineSpacing(4)
 *     .children(iconLabel, textLabel, linkLabel)
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class RichParagraph extends TextFlowAnt {
 *     public RichParagraph() {
 *         lineSpacing(6);
 *         textAlignment(TextAlignment.JUSTIFY);
 *         children(new Label("第一段"), new Label("第二段"));
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现</li>
 *   <li><b>双重身份</b>：是 TextFlow 也是工厂——继承自 {@link TextFlow}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class TextFlowAnt extends TextFlow implements LayoutCommon<TextFlowAnt> {

    /** 工厂入口。 */
    public static TextFlowAnt create() {
        return new TextFlowAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static TextFlowAnt create(Node... children) {
        return new TextFlowAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public TextFlowAnt() {
        super();
    }

    public TextFlowAnt(Node... children) {
        super();
        this.children(children);
    }

    // ============================================================
    // 流式 API（TextFlow 特有业务方法）
    // ============================================================

    /** 批量添加子节点（追加，不清旧）。null 节点会被过滤。 */
    public TextFlowAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** 设置行间距。 */
    public TextFlowAnt lineSpacing(double lineSpacing) {
        setLineSpacing(lineSpacing);
        return this;
    }

    /** 设置文本对齐方式。 */
    public TextFlowAnt textAlignment(TextAlignment alignment) {
        setTextAlignment(alignment);
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<TextFlowAnt> 默认实现
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public TextFlowAnt build() {
        return this;
    }
}
