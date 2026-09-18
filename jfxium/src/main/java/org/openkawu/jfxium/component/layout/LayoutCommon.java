package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * 继承式 layout 组件的统一流式 API 契约 —— 7 个 *Ant（VBox/HBox/FlowPane/SplitPane/
 * TilePane/StackPane/AnchorPane）通用方法的集中地。
 *
 * <h2>命名约定</h2>
 * <p>本接口名字 <b>不包含</b> "Ant" 字段，原因：
 * <ul>
 *   <li>它本身不是 {@code *Ant} 系列类 —— 是给 {@code *Ant} 用的<b>通用契约</b></li>
 *   <li>"Ant" 是实现类后缀（双工厂模式的具体产物），不应反向传染到抽象契约名</li>
 *   <li>避免读者误以为 "LayoutAnt 是 LayoutAntCommon 的父类" 这类错误的继承关系</li>
 * </ul>
 * </p>
 *
 * <h2>背景</h2>
 * 此前 7 个继承式 layout 文件各自重复实现了 {@code styleClass / style / background /
 * padding / borderXxx / borderRadius / maxW/H/minW/H/prefW/H / prefSize/maxSize/minSize /
 * visible/disable/managed/opacity/cursor/id} 等 25-30 个方法，总计约 200 行模板代码。
 * 每个 layout 文件改风格时都要动 7 处，不利于统一演进。
 *
 * <h2>设计</h2>
 * 采用 Java 8+ {@code default methods} + self-bounded 泛型：
 * <ul>
 *   <li>{@code <SELF extends LayoutCommon<SELF>>} —— 保证链式调用返回子类类型
 *       （{@code vbox.borderTop().padding(16).spacing(8)} 编译通过，{@code spacing} 仍能链下去）</li>
 *   <li>default methods 通过 {@code ((Node) this).xxx()} 调 {@link javafx.scene.Node} 公开 API
 *       （所有实现类都是 Node 子类，cast 安全）</li>
 *   <li>Region 特有方法（{@code setPadding(Insets)} / {@code setMaxWidth(double)} 等）
 *       用 Java 16+ pattern matching for instanceof 强转调用</li>
 *   <li>业务继承语义完全保留 —— {@code class HomeView extends VBoxAnt} 仍然成立，
 *       HomeView 构造里调 {@code padding(16).borderTop()} 等都是从接口拿到的 default 方法</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * public class VBoxAnt extends VBox implements LayoutCommon<VBoxAnt> {
 *     // 无需实现任何方法 —— 全部从接口 default 获得
 *     // 仅保留 VBoxAnt 特有的方法（spacing/align/children/vgrow/margin/create/build 等）
 * }
 * }</pre>
 *
 * <h2>为什么不直接 extends AbstractStyleBuilder</h2>
 * {@code AbstractStyleBuilder} 是 Builder 父类（{@code create().build()} 拿到 Node），
 * 业务继承它会跟 Builder 设计冲突（SELF 泛型 + {@code applyStyles} 工具方法）。
 * 本接口是双工厂模式专用 —— 业务可直接 {@code extends VBoxAnt} 当页面骨架基类。
 *
 * @param <SELF> 子类自身类型，确保链式调用返回子类引用
 * @see AbstractStyleBuilder
 */
public interface LayoutCommon<SELF extends LayoutCommon<SELF>> {

    // ============================================================
    // 视觉钩子：styleClass / style / background
    // ============================================================

    /**
     * 追加一个 styleClass（幂等——重复调不会重复挂）。
     */
    @SuppressWarnings("unchecked")
    default SELF styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !((Node) this).getStyleClass().contains(cls)) {
            ((Node) this).getStyleClass().add(cls);
        }
        return (SELF) this;
    }

    /**
     * 批量挂多个 styleClass（变长重载）。
     */
    @SuppressWarnings("unchecked")
    default SELF styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                styleClass(c);
            }
        }
        return (SELF) this;
    }

    /**
     * inline style（应急用，优先用 styleClass + LESS）。
     */
    @SuppressWarnings("unchecked")
    default SELF style(String style) {
        if (style != null) {
            ((Node) this).setStyle(style);
        }
        return (SELF) this;
    }

    /**
     * 设置背景层级（挂 {@link Background#styleClass()}）。
     */
    @SuppressWarnings("unchecked")
    default SELF background(Background bg) {
        if (bg != null) {
            styleClass(bg.styleClass());
        }
        return (SELF) this;
    }

    // ============================================================
    // padding（Region 特有方法，pattern matching for instanceof）
    // ============================================================

    /**
     * 设置统一的 padding（四边相同）。
     */
    @SuppressWarnings("unchecked")
    default SELF padding(double padding) {
        if (this instanceof Region r) {
            r.setPadding(new Insets(padding));
        }
        return (SELF) this;
    }

    /**
     * 设置 4 边各自的 padding。
     */
    @SuppressWarnings("unchecked")
    default SELF padding(double top, double right, double bottom, double left) {
        if (this instanceof Region r) {
            r.setPadding(new Insets(top, right, bottom, left));
        }
        return (SELF) this;
    }

    /**
     * 设置 Insets 对象。
     */
    @SuppressWarnings("unchecked")
    default SELF padding(Insets padding) {
        if (padding != null && this instanceof Region r) {
            r.setPadding(padding);
        }
        return (SELF) this;
    }

    // ============================================================
    // 方向性边框线（挂 BORDER_* styleClass）
    // ============================================================

    /** 顶部分割线。 */
    default SELF borderTop() {
        return styleClass(JfxStyles.BORDER_TOP);
    }

    /** 顶部分割线（开关）。 */
    @SuppressWarnings("unchecked")
    default SELF borderTop(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_TOP, on);
        return (SELF) this;
    }

    /** 底部分割线。 */
    default SELF borderBottom() {
        return styleClass(JfxStyles.BORDER_BOTTOM);
    }

    /** 底部分割线（开关）。 */
    @SuppressWarnings("unchecked")
    default SELF borderBottom(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_BOTTOM, on);
        return (SELF) this;
    }

    /** 左侧分割线。 */
    default SELF borderLeft() {
        return styleClass(JfxStyles.BORDER_LEFT);
    }

    /** 左侧分割线（开关）。 */
    @SuppressWarnings("unchecked")
    default SELF borderLeft(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_LEFT, on);
        return (SELF) this;
    }

    /** 右侧分割线。 */
    default SELF borderRight() {
        return styleClass(JfxStyles.BORDER_RIGHT);
    }

    /** 右侧分割线（开关）。 */
    @SuppressWarnings("unchecked")
    default SELF borderRight(boolean on) {
        toggleStyleClass(JfxStyles.BORDER_RIGHT, on);
        return (SELF) this;
    }

    private void toggleStyleClass(String cls, boolean on) {
        if (cls == null || cls.isEmpty()) {
            return;
        }
        if (on) {
            if (!((Node) this).getStyleClass().contains(cls)) {
                ((Node) this).getStyleClass().add(cls);
            }
        } else {
            ((Node) this).getStyleClass().remove(cls);
        }
    }

    // ============================================================
    // 圆角（挂 RADIUS_* styleClass；MD 为默认无需挂 class）
    // ============================================================

    /**
     * 设置圆角档位（NONE / SM / MD / LG）。
     * <p>MD 为默认值，无需挂任何 styleClass（与 LESS 端 {@code .jfx-radius-*} 覆盖策略一致）。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF borderRadius(Radius radius) {
        ((Node) this).getStyleClass().removeAll(JfxStyles.RADIUS_NONE, JfxStyles.RADIUS_SM, JfxStyles.RADIUS_LG);
        if (radius == null) {
            return (SELF) this;
        }
        if (radius == Radius.NONE) {
            ((Node) this).getStyleClass().add(JfxStyles.RADIUS_NONE);
        } else if (radius == Radius.SM) {
            ((Node) this).getStyleClass().add(JfxStyles.RADIUS_SM);
        } else if (radius == Radius.LG) {
            ((Node) this).getStyleClass().add(JfxStyles.RADIUS_LG);
        }
        // MD 为默认，无需额外 class
        return (SELF) this;
    }

    // ============================================================
    // 尺寸（短名便捷 API；与 AbstractStyleBuilder 长名版互补共存）
    // ============================================================

    /** 设置最大宽度。 */
    @SuppressWarnings("unchecked")
    default SELF maxW(double width) {
        if (this instanceof Region r) {
            r.setMaxWidth(width);
        }
        return (SELF) this;
    }

    /** 设置最大高度。 */
    @SuppressWarnings("unchecked")
    default SELF maxH(double height) {
        if (this instanceof Region r) {
            r.setMaxHeight(height);
        }
        return (SELF) this;
    }

    /** 设置最小宽度。 */
    @SuppressWarnings("unchecked")
    default SELF minW(double width) {
        if (this instanceof Region r) {
            r.setMinWidth(width);
        }
        return (SELF) this;
    }

    /** 设置最小高度。 */
    @SuppressWarnings("unchecked")
    default SELF minH(double height) {
        if (this instanceof Region r) {
            r.setMinHeight(height);
        }
        return (SELF) this;
    }

    /** 设置首选宽度。 */
    @SuppressWarnings("unchecked")
    default SELF prefW(double width) {
        if (this instanceof Region r) {
            r.setPrefWidth(width);
        }
        return (SELF) this;
    }

    /** 设置首选高度。 */
    @SuppressWarnings("unchecked")
    default SELF prefH(double height) {
        if (this instanceof Region r) {
            r.setPrefHeight(height);
        }
        return (SELF) this;
    }

    /** 同时设置首选宽高。 */
    @SuppressWarnings("unchecked")
    default SELF prefSize(double w, double h) {
        if (this instanceof Region r) {
            r.setPrefSize(w, h);
        }
        return (SELF) this;
    }

    /** 同时设置最大宽高。 */
    @SuppressWarnings("unchecked")
    default SELF maxSize(double w, double h) {
        if (this instanceof Region r) {
            r.setMaxSize(w, h);
        }
        return (SELF) this;
    }

    /** 同时设置最小宽高。 */
    @SuppressWarnings("unchecked")
    default SELF minSize(double w, double h) {
        if (this instanceof Region r) {
            r.setMinSize(w, h);
        }
        return (SELF) this;
    }

    // ============================================================
    // 通用三值便捷 API（min / pref / max 一键搞定，替代 3~6 次链式调用）
    // ============================================================

    /**
     * 死尺寸：min / pref / max 三值全部设为同一值。
     * <p>适用于固定尺寸节点（iconBox / avatar / spinner / 缩略图占位等），
     * 替代 {@code .minW(x).prefW(x).maxW(x).minH(x).prefH(x).maxH(x)} 六次链式调用。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF size(double size) {
        if (this instanceof Region r) {
            r.setMinSize(size, size);
            r.setPrefSize(size, size);
            r.setMaxSize(size, size);
        }
        return (SELF) this;
    }

    /**
     * 弹性宽度：一次设置 min / pref / max 三个值。
     * <p>替代 {@code .minW(min).prefW(pref).maxW(max)} 三次链式调用。</p>
     * <p>注意：调用方需保证 {@code min ≤ pref ≤ max}（JavaFX 自身约束），本方法不做 swap。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF width(double min, double pref, double max) {
        if (this instanceof Region r) {
            r.setMinWidth(min);
            r.setPrefWidth(pref);
            r.setMaxWidth(max);
        }
        return (SELF) this;
    }

    /**
     * 弹性高度：一次设置 min / pref / max 三个值。
     */
    @SuppressWarnings("unchecked")
    default SELF height(double min, double pref, double max) {
        if (this instanceof Region r) {
            r.setMinHeight(min);
            r.setPrefHeight(pref);
            r.setMaxHeight(max);
        }
        return (SELF) this;
    }

    /**
     * 弹性盒子：宽度和高度使用同一组 (min, pref, max) 三值。
     * <p>常见场景：bar / menu / list-item / spinner 等需要"最小可点击区域 + 弹性扩展"的节点，
     * 替代 {@code .minSize(min,min).prefSize(pref,pref).maxSize(max,max)} 三次链式调用。</p>
     * <p>与 {@link #size(double)} 的区别：本方法保留弹性区间，三值可不同。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF box(double min, double pref, double max) {
        if (this instanceof Region r) {
            r.setMinSize(min, min);
            r.setPrefSize(pref, pref);
            r.setMaxSize(max, max);
        }
        return (SELF) this;
    }

    // ============================================================
    // 高频节点属性（Node 公开方法）
    // ============================================================

    /** 设置可见性。 */
    @SuppressWarnings("unchecked")
    default SELF visible(boolean v) {
        ((Node) this).setVisible(v);
        return (SELF) this;
    }

    /** 设置禁用状态。 */
    @SuppressWarnings("unchecked")
    default SELF disable(boolean d) {
        ((Node) this).setDisable(d);
        return (SELF) this;
    }

    /** 设置是否受布局管理。 */
    @SuppressWarnings("unchecked")
    default SELF managed(boolean m) {
        ((Node) this).setManaged(m);
        return (SELF) this;
    }

    /** 设置透明度（0.0 ~ 1.0）。 */
    @SuppressWarnings("unchecked")
    default SELF opacity(double o) {
        ((Node) this).setOpacity(o);
        return (SELF) this;
    }

    /**
     * 设置鼠标光标。
     * <p>与原 VBoxAnt/HBoxAnt 行为一致：不检查 null，传 null 时由 JavaFX 自行处理。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF cursor(Cursor c) {
        ((Node) this).setCursor(c);
        return (SELF) this;
    }

    /**
     * 设置节点 ID。
     * <p>与原 VBoxAnt/HBoxAnt 行为一致：不检查 null，传 null 时由 JavaFX 自行处理。</p>
     */
    @SuppressWarnings("unchecked")
    default SELF id(String id) {
        ((Node) this).setId(id);
        return (SELF) this;
    }
}
