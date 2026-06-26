package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ColorPickerAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

/**
 * ColorPicker 颜色选择 —— 基础 / 带默认值。
 */
public class ColorPickerExamplePage extends VBoxAnt {

    public ColorPickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ColorPicker 颜色选择")
                .description("提供颜色选取的输入控件，封装 JavaFX 原生 ColorPicker。")
                .sections(basicSection(), defaultValueSection(), valueSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = ColorPickerAnt.create()
                .onChange(color -> MessageAnt.info("当前颜色：" + toHex(color)))
                .build();
        String code = """
                ColorPickerAnt.create()
                        .onChange(color -> MessageAnt.info(toHex(color)))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认白色，点击展开调色板选择颜色，onChange 回调拿到新值。",
                code, demo);
    }

    private Node defaultValueSection() {
        Node demo = Demos.row(
                ColorPickerAnt.create().value(Color.web("#1677ff")).build(),
                ColorPickerAnt.create().value(Color.web("#52c41a")).build()
        );
        String code = """
                ColorPickerAnt.create().value(Color.web("#1677ff")).build();
                ColorPickerAnt.create().value(Color.web("#52c41a")).build();
                """;
        return Demos.sectionWithCode("2. 默认值",
                "value(Color) 指定初始颜色。",
                code, demo);
    }

    /**
     * 3. 获取选中颜色 —— onChange 回调拿到 JavaFX Color，转成 hex 字符串显示。
     *
     * <p>onChange 给的是 Color 对象，业务里常需要 hex 串（如 #1677FF），
     * 这里用一个小工具把 Color 转成 hex 并实时显示在结果 Label 上。</p>
     */
    private Node valueSection() {
        Label result = TypographyAnt.text("当前颜色：#FFFFFF").build();
        Node picker = ColorPickerAnt.create()
                .value(Color.web("#1677ff"))
                .onChange(color -> result.setText("当前颜色：" + toHex(color)))
                .build();
        Node demo = Demos.column(picker, result);
        String code = """
                Label result = TypographyAnt.text("当前颜色：#FFFFFF").build();
                ColorPickerAnt.create()
                        .value(Color.web("#1677ff"))
                        .onChange(color -> result.setText("当前颜色：" + toHex(color)))
                        .build();

                // Color -> #RRGGBB
                static String toHex(Color c) {
                    return String.format("#%02X%02X%02X",
                            (int) Math.round(c.getRed() * 255),
                            (int) Math.round(c.getGreen() * 255),
                            (int) Math.round(c.getBlue() * 255));
                }
                """;
        return Demos.sectionWithCode("3. 获取选中颜色",
                "onChange(color -> ...) 给出 JavaFX Color，转成 hex 字符串后显示在结果 Label。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 ColorPicker 的 3 个维度：尺寸 / 禁用 / 默认颜色。
     *
     * <p>ColorPickerAnt 继承自 JavaFX {@link javafx.scene.control.ColorPicker}，
     * 所有属性（size/value/disabled）都是直接修改 this，没有独立 Controller。采用
     * {@link PlayGround#rebindRebuild} —— 每次 binder 变化都重新 {@code create() + 链式 + build()}
     * 生成全新的 ColorPicker 节点。</p>
     *
     * <p>展示区用 VBox 同时挂 picker + 结果 Label，让 onChange 的 hex 字符串有地方可见；
     * 重建时 VBox 复用同一个 Label 引用（避免 TextField 一样每次失去焦点）。</p>
     */
    private Node playgroundSection() {
        Binder<String> sizeBinder     = PlayGround.binder("default");  // default/small/large
        Binder<String> disabledBinder = PlayGround.binder("no");      // yes/no
        Binder<String> colorBinder    = PlayGround.binder("#1677ff"); // 4 个候选默认色

        // 结果 Label —— 复用同一个实例，避免 onChange 闭包失效
        final Label[] resultRef = new Label[]{TypographyAnt.text("当前颜色：#1677FF").build()};

        Supplier<Node> displayFactory = () -> {
            Size size = parseSize(sizeBinder.get());
            boolean disabled = "yes".equals(disabledBinder.get());
            Color defaultColor = parseColor(colorBinder.get());
            resultRef[0].setText("当前颜色：" + toHex(defaultColor));

            Node picker = ColorPickerAnt.create()
                    .value(defaultColor)
                    .size(size)
                    .disabled(disabled)
                    .onChange(color -> resultRef[0].setText("当前颜色：" + toHex(color)))
                    .build();

            VBoxAnt box = VBoxAnt.create();
            box.setSpacing(8);
            box.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().addAll(picker, resultRef[0]);
            return box;
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 ColorPicker 的尺寸（大 / 默认 / 小）、禁用状态、默认颜色 —— 三种状态任意组合，右侧立刻看到效果，下方 Label 同步显示当前颜色 hex。",
                PlayGround.rebindRebuild(displayFactory, "尺寸 / 禁用 / 默认颜色",
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabledBinder,
                                PlayGround.entry("no",  "启用"),
                                PlayGround.entry("yes", "禁用"))),
                        PlayGround.row("默认颜色", PlayGround.segmented(colorBinder,
                                PlayGround.entry("#ffffff", "白色"),
                                PlayGround.entry("#1677ff", "蓝色"),
                                PlayGround.entry("#52c41a", "绿色"),
                                PlayGround.entry("#ff4d4f", "红色")))));
    }

    /** Color 转 #RRGGBB hex 字符串。 */
    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }

    private static Size parseSize(String v) {
        if ("small".equals(v)) return Size.SMALL;
        if ("large".equals(v)) return Size.LARGE;
        return Size.DEFAULT;
    }

    private static Color parseColor(String hex) {
        if (hex == null || hex.isBlank()) return Color.web("#1677ff");
        try {
            return Color.web(hex);
        } catch (IllegalArgumentException ex) {
            return Color.web("#1677ff");
        }
    }
}