package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.layout.AbstractHBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * HBarAnt - 横向条状容器（HBar 语义，左 / 中 / 右 三段式），基于 {@link AbstractHBoxAnt}。
 *
 * <p><b>定位</b>：项目最底层的「横向条状容器」。header / toolbar / footer / appbar
 * 全部基于它。把「左信息 + 弹性 spacer + 右操作」这一 PC admin 高频模式从「用户手写」
 * 提升为「组件复用」。</p>
 *
 * <p><b>命名说明（M19.52 由 BarAnt 直接改名）</b>：本组件就是最底层的横向条状容器。
 * 越底层越该短，三段式是它的用法不是名字；改成 HBarAnt 后，角色更一眼可见。</p>
 *
 * <h2>三段 vs 二段（自动退化）</h2>
 * <pre>
 *  center 不为空（三段，center 真正居中）：
 *    [ left... ]  spacer  [ center... ]  spacer  [ right... ]
 *
 *  center 为空（二段）：
 *    [ left... ]              spacer              [ right... ]
 * </pre>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li><b>Card header</b>：[标题] [] [更多按钮]（配 {@code .padding(8,12,8,12).borderBottom()}）</li>
 *   <li><b>Modal/Drawer header</b>：[标题] [] [关闭按钮]（配 {@code .padding(16,24,16,24).borderBottom()}）</li>
 *   <li><b>App header</b>：[Logo] [搜索框] [用户菜单]</li>
 *   <li><b>Dialog footer</b>：[] [] [取消/确认]（配 {@code .borderTop()}）</li>
 *   <li><b>FilterBar</b>：[筛选条件...] [] [新增/导出]（不传 center 即退化为二段）</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 *
 * <h3>三段：左 + 中 + 右</h3>
 * <pre>{@code
 * HBarAnt header = HBarAnt.create()
 *     .left(closeBtn)
 *     .center(titleLabel)
 *     .right(saveBtn, cancelBtn)
 *     .gap(8)
 *     .build();
 * }</pre>
 *
 * <h3>二段：左 + 右（不调 center 即可）</h3>
 * <pre>{@code
 * HBarAnt toolbar = HBarAnt.create()
 *     .left(searchField, roleCombo)
 *     .right(refreshBtn, addBtn)
 *     .build();
 * }</pre>
 *
 * <h3>Card header + 背景色切换</h3>
 * <pre>{@code
 * HBarAnt cardHeader = HBarAnt.create()
 *     .left(LabelAnt.create("时间范围").build())
 *     .right(settingsBtn)
 *     .padding(8, 12, 8, 12)
 *     .background(Background.SUBTLE)   // 继承自 LayoutCommon
 *     .borderRadius(Radius.LG)         // 继承自 LayoutCommon
 *     .build();
 * }</pre>
 *
 * <h2>设计取舍</h2>
 * <ul>
 *   <li>继承 {@link AbstractHBoxAnt}，build() 返回自身（HBarAnt IS-A HBox，不撒谎）</li>
 *   <li>自动获得 {@link LayoutCommon} 全部流式能力：background / borderRadius /
 *       borderXxx / padding / 尺寸 / 可见性等</li>
 *   <li>三段时 center <b>真正居中</b>：左/右两侧用独立 Region 做弹性 spacer，宽度对称分配</li>
 *   <li>受左/右宽度挤压时，center 会向较窄的一侧偏移——这是 flex 标准行为</li>
 *   <li>每段都允许多节点（按添加顺序水平排列），不传节点则该段为空但仅占一个 spacer 槽</li>
 *   <li>高度/边距由 {@code .padding(...)} 自控，不由外壳 CSS 钳死（PC UI 标准 §B.3）</li>
 * </ul>
 *
 * <p><b>替代关系</b>：取代了原 {@code ActionBarAnt}（顺序+spacer 模型）和原
 * {@code core.util.Headers} 工厂（仅 title+extra 二段），两者已于 M19 删除。
 * M19.52 从 {@code HBarAnt} 改名而来。</p>
 */
public class HBarAnt extends AbstractHBoxAnt<HBarAnt> {

    private final List<Node> leftNodes = new ArrayList<>();
    private final List<Node> centerNodes = new ArrayList<>();
    private final List<Node> rightNodes = new ArrayList<>();

    private HBarAnt() {
        setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        getStyleClass().add(JfxStyles.H_BAR);
        borderBottom(); // 默认开启底部分隔线
    }

    /** 工厂入口。 */
    public static HBarAnt create() {
        return new HBarAnt();
    }

    // ============================================================
    // 三段节点
    // ============================================================

    /** 左侧节点（按添加顺序水平排列）。多次调用累加。 */
    public HBarAnt left(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) leftNodes.add(n);
            }
        }
        return this;
    }

    /**
     * 中间节点（可选）。
     * <p>不调用或传空数组 → 退化为二段（左 + spacer + 右）。<br>
     * 调用且非空 → 三段（左 + spacer + 中 + spacer + 右），center 真正居中。</p>
     */
    public HBarAnt center(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) centerNodes.add(n);
            }
        }
        return this;
    }

    /** 右侧节点（按添加顺序水平排列）。多次调用累加。 */
    public HBarAnt right(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) rightNodes.add(n);
            }
        }
        return this;
    }

    // ============================================================
    // HBarAnt 特有
    // ============================================================

    /** 子节点间距（默认 8）。 */
    public HBarAnt gap(double gap) {
        setSpacing(Math.max(0, gap));
        return this;
    }

    /** 整体对齐方式（默认 CENTER_LEFT；通常无需修改）。 */
    public HBarAnt alignment(javafx.geometry.Pos alignment) {
        if (alignment != null) {
            setAlignment(alignment);
        }
        return this;
    }

    /** 顶部分割线。 */
    public HBarAnt borderTop() {
        return borderTop(true);
    }

    /** 顶部分割线（开关）。 */
    public HBarAnt borderTop(boolean on) {
        toggleStyleClass(JfxStyles.BAR_BORDER_TOP, on);
        return this;
    }

    /** 底部分割线。 */
    public HBarAnt borderBottom() {
        return borderBottom(true);
    }

    /** 底部分割线（开关）。 */
    public HBarAnt borderBottom(boolean on) {
        toggleStyleClass(JfxStyles.BAR_BORDER_BOTTOM, on);
        return this;
    }

    /** 左侧分割线。 */
    public HBarAnt borderLeft() {
        return borderLeft(true);
    }

    /** 左侧分割线（开关）。 */
    public HBarAnt borderLeft(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_LEFT, on);
        return this;
    }

    /** 右侧分割线。 */
    public HBarAnt borderRight() {
        return borderRight(true);
    }

    /** 右侧分割线（开关）。 */
    public HBarAnt borderRight(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_RIGHT, on);
        return this;
    }

    // ============================================================
    // LayoutCommon 桥接方法（二进制兼容性）
    // ============================================================
    //
    // 继承 AbstractHBoxAnt<HBarAnt> → LayoutCommon<HBarAnt> 默认方法在源码级正常编译。
    // 但旧调用方（或 getDeclaredMethod 反射）按 HBarAnt.xxx(): HBarAnt 精确签名查找时
    // 找不到 LayoutCommon 的默认方法，触发 NoSuchMethodError。
    // 以下桥接方法显式声明精确类型签名，同时为子类提供可 override 的入口。
    // （详见 Bug #123、Bug #138、HBarAntTest.backgroundBridgeMethodKeepsBinarySignature）

    @Override
    public HBarAnt background(org.openkawu.jfxium.core.css.Background background) {
        styleClass(background != null ? background.styleClass() : null);
        return this;
    }

    @Override
    public HBarAnt padding(double top, double right, double bottom, double left) {
        if (this instanceof javafx.scene.layout.Region r) {
            r.setPadding(new javafx.geometry.Insets(top, right, bottom, left));
        }
        return this;
    }

    @Override
    public HBarAnt borderRadius(org.openkawu.jfxium.core.builder.Radius radius) {
        getStyleClass().removeAll(JfxStyles.RADIUS_NONE, JfxStyles.RADIUS_SM, JfxStyles.RADIUS_LG);
        if (radius == null) return this;
        if (radius == org.openkawu.jfxium.core.builder.Radius.NONE) getStyleClass().add(JfxStyles.RADIUS_NONE);
        else if (radius == org.openkawu.jfxium.core.builder.Radius.SM) getStyleClass().add(JfxStyles.RADIUS_SM);
        else if (radius == org.openkawu.jfxium.core.builder.Radius.LG) getStyleClass().add(JfxStyles.RADIUS_LG);
        return this;
    }

    @Override
    public HBarAnt styleClass(String... styleClasses) {
        if (styleClasses != null) {
            for (String c : styleClasses) {
                if (c != null && !c.isEmpty() && !getStyleClass().contains(c)) {
                    getStyleClass().add(c);
                }
            }
        }
        return this;
    }

    @Override
    public HBarAnt style(String inlineStyle) {
        if (inlineStyle != null) setStyle(inlineStyle);
        return this;
    }

    @Override
    public HBarAnt visible(boolean visible) {
        setVisible(visible);
        return this;
    }

    // DisabledSupport.disabled(boolean) — 无需桥接，已被 DisabledSupport 默认方法覆盖

    @Override
    public HBarAnt managed(boolean managed) {
        setManaged(managed);
        return this;
    }

    @Override
    public HBarAnt opacity(double opacity) {
        setOpacity(opacity);
        return this;
    }

    @Override
    public HBarAnt id(String id) {
        setId(id);
        return this;
    }

    // ============================================================
    // 构建
    // ============================================================

    /** 组装三段式布局并返回自身。 */
    public HBarAnt build() {
        getChildren().clear();

        // 1. 左段
        getChildren().addAll(leftNodes);

        // 2. 第一个 spacer（左 → 中/右）
        getChildren().add(makeSpacer());

        // 3. 中段（可选）+ 第二个 spacer
        if (!centerNodes.isEmpty()) {
            getChildren().addAll(centerNodes);
            getChildren().add(makeSpacer());
        }

        // 4. 右段
        getChildren().addAll(rightNodes);

        return this;
    }

    /**
     * 创建一个弹性 spacer：HBox 内宽度自动撑满剩余空间。
     * 双重保险（Hgrow + maxWidth）—— 符合组件组合规范 3.1 标准模式。
     */
    private static Region makeSpacer() {
        Region spacer = new Region();
        spacer.getStyleClass().add(JfxStyles.H_BAR_SPACER);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMaxWidth(Double.MAX_VALUE);
        return spacer;
    }

    private void toggleStyleClass(String styleClass, boolean on) {
        if (on) {
            if (!getStyleClass().contains(styleClass)) {
                getStyleClass().add(styleClass);
            }
        } else {
            getStyleClass().remove(styleClass);
        }
    }

}
