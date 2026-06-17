package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.layout.AbstractVBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * VBarAnt - 竖向条状容器（上 / 中 / 下 三段式）。
 *
 * <p>它是 {@link HBarAnt} 的竖向对称版本，适合卡片、侧栏面板、设置页这类
 * 「上部标题 + 中间内容 + 底部操作」的组合场景。</p>
 *
 * <h2>三段 vs 两段（自动退化）</h2>
 * <pre>
 *  center 不为空（三段）：
 *    [ top... ]
 *    spacer
 *    [ center... ]
 *    spacer
 *    [ bottom... ]
 *
 *  center 为空（两段）：
 *    [ top... ]
 *    spacer
 *    [ bottom... ]
 * </pre>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li><b>Card</b>：header / body / footer</li>
 *   <li><b>Side panel</b>：顶部标题 / 中部内容 / 底部按钮</li>
 *   <li><b>Settings block</b>：分组标题 / 表单区 / 保存区</li>
 * </ul>
 */
public class VBarAnt extends AbstractVBoxAnt<VBarAnt> {

    private final List<Node> topNodes = new ArrayList<>();
    private final List<Node> centerNodes = new ArrayList<>();
    private final List<Node> bottomNodes = new ArrayList<>();

    public VBarAnt() {
        super();
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add(JfxStyles.V_BAR);
    }

    /** 工厂入口。 */
    public static VBarAnt create() {
        return new VBarAnt();
    }

    // ============================================================
    // 三段节点
    // ============================================================

    /** 顶部节点（按添加顺序纵向排列）。多次调用累加。 */
    public VBarAnt top(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) {
                    topNodes.add(n);
                }
            }
        }
        return this;
    }

    /**
     * 中间节点（可选）。
     * <p>不调用或传空数组 → 退化为两段（top + spacer + bottom）。<br>
     * 调用且非空 → 三段（top + spacer + center + spacer + bottom），center 真正居中。</p>
     */
    public VBarAnt center(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) {
                    centerNodes.add(n);
                }
            }
        }
        return this;
    }

    /** 底部节点（按添加顺序纵向排列）。多次调用累加。 */
    public VBarAnt bottom(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) {
                    bottomNodes.add(n);
                }
            }
        }
        return this;
    }

    // ============================================================
    // VBarAnt 特有
    // ============================================================

    /** 子节点间距（默认 8）。 */
    public VBarAnt gap(double gap) {
        spacing(gap);
        return this;
    }

    /** 整体对齐方式（默认 TOP_LEFT）。 */
    public VBarAnt alignment(Pos alignment) {
        align(alignment);
        return this;
    }

    // ============================================================
    // 构建
    // ============================================================

    /** 组装竖向三段式布局并返回自身。 */
    @Override
    public VBarAnt build() {
        getChildren().clear();

        // 1. 顶段
        getChildren().addAll(topNodes);

        // 2. 第一个 spacer（上 → 中/下）
        getChildren().add(makeSpacer());

        // 3. 中段（可选）+ 第二个 spacer
        if (!centerNodes.isEmpty()) {
            getChildren().addAll(centerNodes);
            getChildren().add(makeSpacer());
        }

        // 4. 底段
        getChildren().addAll(bottomNodes);

        return this;
    }

    /**
     * 创建一个弹性 spacer：VBox 内高度自动撑满剩余空间。
     * 双重保险（Vgrow + maxHeight）—— 符合组件组合规范标准模式。
     */
    private static Region makeSpacer() {
        Region spacer = new Region();
        spacer.getStyleClass().add(JfxStyles.V_BAR_SPACER);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        spacer.setMaxHeight(Double.MAX_VALUE);
        return spacer;
    }
}
