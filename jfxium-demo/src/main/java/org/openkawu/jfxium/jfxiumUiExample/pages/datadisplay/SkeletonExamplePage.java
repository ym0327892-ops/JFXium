package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.SkeletonAnt;

/**
 * Skeleton 骨架屏 —— 基础形状 / 段落 / 头像 + 文本。
 */
public class SkeletonExamplePage extends VBoxAnt {

    public SkeletonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Skeleton 骨架屏")
                .description("内容加载过程中的占位效果，比 loading 转圈更平滑。")
                .sections(basicSection(), paragraphSection(), avatarSection(), loadingSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(160).height(16).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.ROUNDED).width(120).height(48).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR).width(48).height(48).build()
        );
        String code = """
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(160).height(16).build();
                SkeletonAnt.create().variant(SkeletonAnt.Variant.ROUNDED).width(120).height(48).build();
                SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR).width(48).height(48).build();
                """;
        return Demos.sectionWithCode("1. 基础形状",
                "TEXT / ROUNDED / RECTANGULAR / CIRCULAR 四种形状。", code, demo);
    }

    private Node paragraphSection() {
        Node demo = SkeletonAnt.paragraph(3, 280, 14);
        String code = """
                // 多行段落骨架，最后一行自动收窄
                SkeletonAnt.paragraph(3, 280, 14);
                """;
        return Demos.sectionWithCode("2. 段落",
                "paragraph(lines, width, lineHeight) 生成多行文本占位。", code, demo);
    }

    private Node avatarSection() {
        Node demo = SkeletonAnt.avatarText();
        String code = """
                // 圆形头像 + 两行文本，模拟列表项加载态
                SkeletonAnt.avatarText();
                """;
        return Demos.sectionWithCode("3. 头像 + 文本",
                "avatarText() 组合圆形头像和文本行，适合列表项占位。", code, demo);
    }

    private Node loadingSection() {
        Node realContent = VBoxAnt.create()
                .spacing(8)
                .children(
                        TypographyAnt.title("用户资料", 5).build(),
                        TypographyAnt.text("姓名：张三").build(),
                        TypographyAnt.text("部门：技术部").build(),
                        TypographyAnt.text("职位：高级 Java 工程师").build()
                )
                .padding(16)
                .build();
        Node skeleton = SkeletonAnt.avatarText();
        skeleton.setVisible(false);
        skeleton.setManaged(false);
        Node loadingPane = StackPaneAnt.create().children(realContent, skeleton).build();

        ButtonAnt loadBtn = ButtonAnt.create("模拟加载")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    realContent.setVisible(false);
                    realContent.setManaged(false);
                    skeleton.setVisible(true);
                    skeleton.setManaged(true);
                    PauseTransition pt = new PauseTransition(Duration.seconds(2));
                    pt.setOnFinished(ev -> {
                        skeleton.setVisible(false);
                        skeleton.setManaged(false);
                        realContent.setVisible(true);
                        realContent.setManaged(true);
                    });
                    pt.play();
                })
                .build();

        Node demo = Demos.column(loadingPane, loadBtn);
        String code = """
                Node loadingPane = StackPaneAnt.create().children(realContent, skeleton).build();
                skeleton.setVisible(false);
                skeleton.setManaged(false);
                // 加载中/完成时只切换 visible + managed，不替换节点
                """;
        return Demos.sectionWithCode("4. 模拟加载",
                "点击按钮模拟数据加载：先显示骨架屏占位，2 秒后自动切换为真实内容。",
                code, demo);
    }

    // ============================================================
    // 5. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Skeleton 的 4 个维度：形状 / 宽度 / 高度 / 动画。
     *
     * <p>SkeletonAnt 是 Builder 模式（{@code build()} 返回 {@link StackPane}），
     * 没有 Controller 暴露，所有属性（variant / width / height / animated）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— StackPane 重建，开销极小，且保证所有属性变更生效。</p>
     *
     * <p>尺寸档：small = 80×16 / medium = 160×32 / large = 240×48。
     * CIRCULAR 变体自动取宽高的较小值作为正方形边长，避免显示成椭圆。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> variant  = PlayGround.binder("text");
        Binder<String> width    = PlayGround.binder("medium");
        Binder<String> height   = PlayGround.binder("medium");
        Binder<String> animated = PlayGround.binder("true");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            SkeletonAnt.Variant v = parseVariant(variant.get());
            double w = parseSize(width.get());
            double h = parseSize(height.get());
            // 圆形 variant 强制正方形
            if (v == SkeletonAnt.Variant.CIRCULAR) {
                double s = Math.min(w, h);
                w = h = s;
            }
            SkeletonAnt.Builder b = SkeletonAnt.create().variant(v).width(w).height(h);
            if (!parseBool(animated.get())) {
                b.noAnimation();
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Skeleton 的形状 / 宽度 / 高度 / 动画 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("形状", PlayGround.segmented(variant,
                                PlayGround.entry("text",        "文本"),
                                PlayGround.entry("circular",    "圆形"),
                                PlayGround.entry("rectangular", "矩形"),
                                PlayGround.entry("rounded",     "圆角"))),
                        PlayGround.row("宽度", PlayGround.segmented(width,
                                PlayGround.entry("small",  "小 (80)"),
                                PlayGround.entry("medium", "中 (160)"),
                                PlayGround.entry("large",  "大 (240)"))),
                        PlayGround.row("高度", PlayGround.segmented(height,
                                PlayGround.entry("small",  "小 (16)"),
                                PlayGround.entry("medium", "中 (32)"),
                                PlayGround.entry("large",  "大 (48)"))),
                        PlayGround.row("动画", PlayGround.segmented(animated,
                                PlayGround.entry("true",  "开"),
                                PlayGround.entry("false", "关")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v != null && "true".equalsIgnoreCase(v);
    }

    private static SkeletonAnt.Variant parseVariant(String v) {
        if (v == null) return SkeletonAnt.Variant.TEXT;
        return switch (v) {
            case "circular"    -> SkeletonAnt.Variant.CIRCULAR;
            case "rectangular" -> SkeletonAnt.Variant.RECTANGULAR;
            case "rounded"     -> SkeletonAnt.Variant.ROUNDED;
            default            -> SkeletonAnt.Variant.TEXT;
        };
    }

    private static double parseSize(String v) {
        if (v == null) return 160;
        return switch (v) {
            case "small"  -> 80;
            case "large"  -> 240;
            default       -> 160;
        };
    }
}
