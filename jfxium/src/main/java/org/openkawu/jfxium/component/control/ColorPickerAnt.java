package org.openkawu.jfxium.component.control;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.css.CssClasses;

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
 *   <li><b>视觉</b>：走 {@link CssClasses#COLOR_PICKER} LESS 样式</li>
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
 *     .size(ColorPickerAnt.Size.LARGE)
 *     .build();
 * }</pre>
 */
public class ColorPickerAnt extends ColorPicker {

    /** 尺寸枚举。 */
    public enum Size {
        DEFAULT, SMALL, LARGE
    }

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
        getStyleClass().add(CssClasses.COLOR_PICKER);
    }

    public ColorPickerAnt(Color value) {
        super(value);
        getStyleClass().add(CssClasses.COLOR_PICKER);
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public ColorPickerAnt value(Color value) {
        setValue(value);
        return this;
    }

    public ColorPickerAnt onChange(Consumer<Color> onChange) {
        valueProperty().addListener((obs, old, val) -> {
            if (val != null && onChange != null) {
                onChange.accept(val);
            }
        });
        return this;
    }

    public ColorPickerAnt size(Size size) {
        getStyleClass().removeAll(CssClasses.SIZE_SMALL, CssClasses.SIZE_LARGE);
        if (size == Size.SMALL) {
            getStyleClass().add(CssClasses.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            getStyleClass().add(CssClasses.SIZE_LARGE);
        }
        return this;
    }

    public ColorPickerAnt disabled(boolean disabled) {
        setDisable(disabled);
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
