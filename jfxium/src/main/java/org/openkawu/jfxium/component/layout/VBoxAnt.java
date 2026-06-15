package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * VBoxAnt - 继承式 VBox 容器（M19.36 引入）。
 *
 * <p><b>定位</b>：垂直布局容器的"双工厂模式"——既能当工厂用，也能被业务继承。
 * 跟 {@code VBoxBuilder} 互补：</p>
 * <ul>
 *   <li>{@code VBoxBuilder.create().build()} —— 拿到 VBox 当字段或参数用</li>
 *   <li>{@code VBoxAnt.create()} —— 拿到 VBoxAnt（仍是 VBox），可继续链式</li>
 *   <li>{@code class HomeView extends VBoxAnt} —— 业务直接继承当"页面基类"</li>
 * </ul>
 *
 * <h2>跟 VBoxBuilder 的区别</h2>
 * <table border="1">
 *   <caption>双方对比</caption>
 *   <tr><th>维度</th><th>VBoxBuilder</th><th>VBoxAnt</th></tr>
 *   <tr><td>build() 返回</td><td>VBox（不可继承 Builder 系）</td><td>VBoxAnt extends VBox</td></tr>
 *   <tr><td>业务可继承</td><td>❌ Builder 模式不能继承</td><td>✅ 推荐用法</td></tr>
 *   <tr><td>API 风格</td><td>create().xxx().build() 三段式</td><td>create().xxx() 两段式（无 build）</td></tr>
 *   <tr><td>使用场景</td><td>构造一次性容器</td><td>页面骨架基类 / 复杂业务容器</td></tr>
 * </table>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法（替代 VBoxBuilder 的场景）</h3>
 * <pre>{@code
 * VBoxAnt root = VBoxAnt.create()
 *     .spacing(16)
 *     .padding(24)
 *     .align(Pos.CENTER)
 *     .background(Background.LAYOUT)
 *     .children(label, button);
 * container.getChildren().add(root);   // VBoxAnt 就是 VBox，直接用
 * }</pre>
 *
 * <h3>2. 业务继承用法（页面骨架基类）</h3>
 * <pre>{@code
 * public class HomeView extends VBoxAnt {
 *     public HomeView(String currentUser) {
 *         spacing(16);
 *         padding(24);
 *         background(Background.LAYOUT);
 *         children(
 *             new Label("欢迎，" + currentUser),
 *             buildDashboard()
 *         );
 *     }
 * }
 * // 用法：new HomeView("alice") 直接当 VBox 添加进容器
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 VBox 也是工厂——继承自 javafx.scene.layout.VBox，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式（{@code spacing(16).padding(24)} 在子类构造里也成立）</li>
 *   <li><b>幂等性</b>：{@code background(SUBTLE)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>跟 *Ant 风格一致</b>：流式方法名跟 {@link VBoxBuilder} 完全对齐（spacing / padding / align / children）</li>
 * </ul>
 */
public class VBoxAnt extends VBox implements LayoutCommon<VBoxAnt> {

    /** 工厂入口。等价于 {@code new VBoxAnt()}，提供链式风格。 */
    public static VBoxAnt create() {
        return new VBoxAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static VBoxAnt create(Node... children) {
        return new VBoxAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public VBoxAnt() {
        super();
    }

    public VBoxAnt(Node... children) {
        super();
        this.children(children);
    }

    public VBoxAnt(double spacing) {
        super(spacing);
    }

    public VBoxAnt(double spacing, Node... children) {
        super(spacing);
        this.children(children);
    }

    // ============================================================
    // 流式 API（跟 VBoxBuilder 对齐）
    // ============================================================

    /** 设置子节点之间的垂直间距。 */
    public VBoxAnt spacing(double spacing) {
        setSpacing(spacing);
        return this;
    }

    /** 设置子节点对齐方式。 */
    public VBoxAnt align(Pos alignment) {
        if (alignment != null) {
            setAlignment(alignment);
        }
        return this;
    }

    /** 添加子节点（追加，不清旧）。null 节点会被过滤。 */
    public VBoxAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** VBox 是否让子节点水平撑满（默认 true）。 */
    public VBoxAnt fillWidth(boolean fill) {
        setFillWidth(fill);
        return this;
    }

    /** 给指定子节点设置垂直拉伸优先级。 */
    public VBoxAnt vgrow(Node child, Priority priority) {
        if (child != null && priority != null) {
            VBox.setVgrow(child, priority);
        }
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public VBoxAnt margin(Node child, Insets margin) {
        if (child != null) {
            VBox.setMargin(child, margin);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<VBoxAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 VBoxAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。
     *
     * <p>VBoxAnt 既是工厂也是节点：调用 {@link #build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟 {@link VBoxBuilder#build()} 等老式 Builder 完全对齐——
     * 业务代码两种风格都能用：</p>
     * <pre>{@code
     * VBoxAnt root = VBoxAnt.create().spacing(16).children(a, b).build();   // 显式 build
     * VBoxAnt root = VBoxAnt.create().spacing(16).children(a, b);            // 省略 build 也行
     * }</pre>
     *
     * <p>业务继承场景下不需要调 build()——{@code this} 就是 VBoxAnt。</p>
     */
    public VBoxAnt build() {
        return this;
    }
}
