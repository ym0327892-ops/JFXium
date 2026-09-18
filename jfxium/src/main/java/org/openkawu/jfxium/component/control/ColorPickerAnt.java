package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.Bindings;

import java.util.function.Consumer;

/**
 * JFXium 颜色选择器组件 - 对标 Ant Design ColorPicker。
 *
 * <p><b>定位</b>：颜色选择控件，支持色板选择、十六进制输入、透明度调节。
 * JavaFX 原生有 {@link ColorPicker}，JFXium 提供 Builder 流式 API + 主题化封装。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>默认值</b>：value(Color) 设置初始颜色</li>
 *   <li><b>回调</b>：onChange(Color) 颜色变化时触发</li>
 *   <li><b>禁用</b>：disabled(true)</li>
 *   <li><b>尺寸</b>：size(Size.SMALL/DEFAULT/LARGE)</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#COLOR_PICKER} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础颜色选择器
 * ColorPickerAnt picker = ColorPickerAnt.create()
 *     .value(Color.BLUE)
 *     .onChange(color -> System.out.println("选中：" + color.toString()))
 *     .build();
 *
 * // 大尺寸 + 禁用
 * ColorPickerAnt large = ColorPickerAnt.create()
 *     .value(Color.web("#1677ff"))
 *     .size(Size.LARGE)
 *     .build();
 * }</pre>
 */
public class ColorPickerAnt extends ColorPicker implements LayoutCommon<ColorPickerAnt>, DisabledSupport<ColorPickerAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    // ============================================================
    // 工厂入口
    // ============================================================

    public static ColorPickerAnt create() {
        return new ColorPickerAnt();
    }

    public static ColorPickerAnt create(Color value) {
        return new ColorPickerAnt(value);
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public ColorPickerAnt() {
        super();
        init();
    }

    public ColorPickerAnt(Color value) {
        super(value);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.COLOR_PICKER);
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public ColorPickerAnt value(Color value) {
        setValue(value);
        return this;
    }

    public ColorPickerAnt onChange(Consumer<Color> onChange) {
        Bindings.onChange(valueProperty(), onChange);
        return this;
    }

    public ColorPickerAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public ColorPickerAnt bindValue(ObjectProperty<Color> property) {
        Bindings.bindBidirectional(valueProperty(), property);
        return this;
    }

    public ColorPickerAnt promptText(String text) {
        setPromptText(text);
        return this;
    }

    // ============================================================
    // 构建
    // ============================================================

    public ColorPickerAnt build() {
        return this;
    }
}
