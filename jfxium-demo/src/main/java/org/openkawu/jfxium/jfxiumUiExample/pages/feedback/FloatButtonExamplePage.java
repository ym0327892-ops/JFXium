package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.util.NumericUtils;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.FloatButtonAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * FloatButton 悬浮按钮 —— 基础 / 类型 / 尺寸 / 交互演示。
 */
public class FloatButtonExamplePage extends VBoxAnt {

    public FloatButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("FloatButton 悬浮按钮")
                .description("固定在页面右下角的圆形快捷操作按钮，对标 Ant Design FloatButton。")
                .sections(
                        basicSection(),
                        typeSection(),
                        sizeSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("+").build())
                        .tooltip("添加")
                        .onClick(() -> MessageAnt.info("点击悬浮按钮"))
                        .build(),
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("↑").build())
                        .tooltip("回到顶部")
                        .build(),
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("?").build())
                        .tooltip("帮助")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("+").build())
                    .tooltip("添加")
                    .onClick(() -> MessageAnt.info("点击悬浮按钮"))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "icon() 设置按钮图标；tooltip() 设置悬停提示；onClick() 设置点击回调。",
                code, demo);
    }

    private Node typeSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("+").build())
                        .type(FloatButtonAnt.Type.DEFAULT)
                        .tooltip("Default")
                        .build(),
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("✦").build())
                        .type(FloatButtonAnt.Type.PRIMARY)
                        .tooltip("Primary")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("+").build())
                    .type(FloatButtonAnt.Type.DEFAULT)
                    .build();
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("✦").build())
                    .type(FloatButtonAnt.Type.PRIMARY)
                    .build();
                """;
        return Demos.sectionWithCode("2. 类型",
                "type(DEFAULT/PRIMARY)：DEFAULT 为默认样式，PRIMARY 为主题色样式。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("S").build())
                        .size(40)
                        .tooltip("Small 40")
                        .build(),
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("M").build())
                        .size(56)
                        .tooltip("Default 56")
                        .build(),
                FloatButtonAnt.create()
                        .icon(TypographyAnt.text("L").build())
                        .size(72)
                        .tooltip("Large 72")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("S").build())
                    .size(40)
                    .build();
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("M").build())
                    .size(56)
                    .build();
                FloatButtonAnt.create()
                    .icon(TypographyAnt.text("L").build())
                    .size(72)
                    .build();
                """;
        return Demos.sectionWithCode("3. 尺寸",
                "size(double) 自定义按钮大小（宽高相等，默认 56px）。",
                code, demo);
    }

    /**
     * 4. 交互演示（PlayGround —— rebuildRebuild 模式 5 维度）。
     *
     * <p>FloatButtonAnt 没有 modify 入口（StackPane 在 build 期组装，size/clip/onClick 全部定死），
     * 故用 {@link PlayGround#rebindRebuild}。暴露 5 个维度：</p>
     * <ul>
     *   <li><b>图标</b> —— 4 个 Unicode 预设（+ / ↑ / ? / ✦）</li>
     *   <li><b>类型</b> —— DEFAULT / PRIMARY（影响主题色样式）</li>
     *   <li><b>尺寸</b> —— S(40) / M(56) / L(72) / XL(96) 四档</li>
     *   <li><b>提示</b> —— 文本输入，实时更新 tooltip</li>
     *   <li><b>点击事件</b> —— off / on（on 时点击弹出 MessageAnt.info 反馈）</li>
     * </ul>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> iconKey  = PlayGround.binder("plus");
        Binder<String> type     = PlayGround.binder("default");
        Binder<String> size     = PlayGround.binder("56");
        Binder<String> tooltip  = PlayGround.binder("悬浮按钮");
        Binder<String> clickOn  = PlayGround.binder("on");

        // 2. display 工厂：读 binder → 重 build
        Supplier<Node> factory = () -> {
            Node icon = parseIcon(iconKey.get());
            FloatButtonAnt.Type t = parseType(type.get());
            double s = parseSize(size.get());
            String tt = tooltip.get() != null ? tooltip.get() : "";
            FloatButtonAnt.Builder b = FloatButtonAnt.create()
                    .icon(icon)
                    .type(t)
                    .size(s)
                    .tooltip(tt);
            if ("on".equals(clickOn.get())) {
                b.onClick(() -> MessageAnt.info("点击了悬浮按钮：" + tt));
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 FloatButton 的图标 / 类型 / 尺寸 / 提示 / 点击反馈 —— 5 维度 rebuildRebuild 模式。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("图标", PlayGround.segmented(iconKey,
                                PlayGround.entry("plus",  "+"),
                                PlayGround.entry("up",    "↑"),
                                PlayGround.entry("help",  "?"),
                                PlayGround.entry("star",  "✦"))),
                        PlayGround.row("类型", PlayGround.segmented(type,
                                PlayGround.entry("default", "Default"),
                                PlayGround.entry("primary", "Primary"))),
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("40", "S (40)"),
                                PlayGround.entry("56", "M (56)"),
                                PlayGround.entry("72", "L (72)"),
                                PlayGround.entry("96", "XL (96)"))),
                        PlayGround.row("提示", PlayGround.textField(tooltip, tooltip.get(), "输入悬停提示")),
                        PlayGround.row("点击事件", PlayGround.segmented(clickOn,
                                PlayGround.entry("off", "关闭"),
                                PlayGround.entry("on",  "开启")))));
    }

    // ============================================================
    // helpers
    // ============================================================

    private static Node parseIcon(String key) {
        String ch = switch (key == null ? "" : key) {
            case "up"    -> "↑";
            case "help"  -> "?";
            case "star"  -> "✦";
            default      -> "+";
        };
        return TypographyAnt.text(ch).build();
    }

    private static FloatButtonAnt.Type parseType(String v) {
        if (v == null) return FloatButtonAnt.Type.DEFAULT;
        return "primary".equals(v) ? FloatButtonAnt.Type.PRIMARY : FloatButtonAnt.Type.DEFAULT;
    }

    private static double parseSize(String v) {
        if (v == null) return 56;
        try {
            double d = Double.parseDouble(v);
            // 与 Builder.size 钳制策略对齐：钳到 [1, 200],非法回退到 56
            return NumericUtils.clamp(d, 1, 200, 56);
        } catch (NumberFormatException e) {
            return 56;
        }
    }
}