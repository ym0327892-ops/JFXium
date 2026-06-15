package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 平铺布局组件 - 对标 Ant Design 的平铺/缩略图网格。
 *
 * <p><b>定位</b>：平铺排列容器，继承自 JavaFX {@link TilePane}。
 * 子节点按固定行列数平铺排列，常用于缩略图网格、图标面板、图片墙等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>行列数</b>：prefColumns(n) / prefRows(n) 设置首选行列数</li>
 *   <li><b>方向</b>：orientation(HORIZONTAL/VERTICAL) 排列方向</li>
 *   <li><b>间距</b>：hgap(vgap) / vgap(vgap) 行列间距</li>
 *   <li><b>对齐</b>：alignment(Pos) 整体对齐方式</li>
 *   <li><b>子节点</b>：children(Node...) 批量添加子节点</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#TILE_PANE} LESS 样式</li>
 * </ul>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 TilePaneAnt）</li>
 *   <li><b>双重身份</b>：是 TilePane 也是工厂——继承自 {@link TilePane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class TilePaneAnt extends TilePane implements LayoutCommon<TilePaneAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口。 */
    public static TilePaneAnt create() {
        return new TilePaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public TilePaneAnt() {
        super();
        getStyleClass().add(JfxStyles.TILE_PANE);
    }

    // ============================================================
    // 流式配置（TilePane 特有业务方法）
    // ============================================================

    public TilePaneAnt prefColumns(int columns) {
        setPrefColumns(columns);
        return this;
    }

    public TilePaneAnt prefRows(int rows) {
        setPrefRows(rows);
        return this;
    }

    public TilePaneAnt orientation(Orientation orientation) {
        if (orientation != null) {
            setOrientation(orientation);
        }
        return this;
    }

    public TilePaneAnt hgap(double gap) {
        setHgap(gap);
        return this;
    }

    public TilePaneAnt vgap(double gap) {
        setVgap(gap);
        return this;
    }

    public TilePaneAnt gap(double gap) {
        setHgap(gap);
        setVgap(gap);
        return this;
    }

    public TilePaneAnt alignment(Pos pos) {
        if (pos != null) {
            setAlignment(pos);
        }
        return this;
    }

    public TilePaneAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null) {
                    getChildren().add(node);
                }
            }
        }
        return this;
    }

    public TilePaneAnt add(Node node) {
        if (node != null) {
            getChildren().add(node);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<TilePaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 TilePaneAnt）
    // ============================================================

    public TilePaneAnt build() {
        return this;
    }
}
