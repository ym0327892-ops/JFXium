package org.openkawu.jfxium.component.control;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * JFXium Separator 组件 - 分隔线（继承式，双工厂模式）。
 *
 * <p><b>定位</b>：视觉分隔线控件，继承自 {@link Separator}，
 * 跟 {@link ComboBoxAnt} / {@link InputAnt} 同款「双工厂模式」——
 * 既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式</h3>
 * <pre>{@code
 * // 水平分隔线
 * Separator hSep = SeparatorAnt.create()
 *     .orientation(Orientation.HORIZONTAL)
 *     .build();
 *
 * // 垂直分隔线
 * Separator vSep = SeparatorAnt.create()
 *     .orientation(Orientation.VERTICAL)
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class ToolbarDivider extends SeparatorAnt {
 *     public ToolbarDivider() {
 *         orientation(Orientation.VERTICAL);
 *     }
 * }
 * }</pre>
 */
public class SeparatorAnt extends Separator {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（默认 HORIZONTAL）。 */
    public static SeparatorAnt create() {
        return new SeparatorAnt();
    }

    /** 工厂入口（指定方向）。 */
    public static SeparatorAnt create(Orientation orientation) {
        return new SeparatorAnt(orientation);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SeparatorAnt() {
        super();
        init();
    }

    public SeparatorAnt(Orientation orientation) {
        super(orientation);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.JFX_SEPARATOR);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置方向（HORIZONTAL / VERTICAL）。 */
    public SeparatorAnt orientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    /** 设置水平对齐方式。 */
    public SeparatorAnt halignment(HPos halignment) {
        setHalignment(halignment);
        return this;
    }

    /** 设置垂直对齐方式。 */
    public SeparatorAnt valignment(VPos valignment) {
        setValignment(valignment);
        return this;
    }

    // ============================================================
    // Builder 终结
    // ============================================================

    /** Builder 模式终结调用——返回自身。 */
    public SeparatorAnt build() {
        return this;
    }
}
