package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.ImageAnt;

/**
 * Image 图片 —— 占位 / 尺寸与圆角 / 交互演示。
 *
 * <p>注：示例不依赖外部网络图片，未传 src 时 ImageAnt 自动展示 placeholder 文本，
 * 重点展示 API 用法（width / height / borderRadius / placeholder）。</p>
 */
public class ImageExamplePage extends VBoxAnt {

    public ImageExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Image 图片")
                .description("图片容器，支持占位、尺寸约束与圆角裁剪。")
                .sections(placeholderSection(), sizeSection(), dynamicSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node placeholderSection() {
        Node demo = ImageAnt.create()
                .width(160).height(100)
                .placeholder("暂无图片")
                .build();
        String code = """
                // 未传 src 时展示 placeholder 占位文本
                ImageAnt.create()
                        .width(160).height(100)
                        .placeholder("暂无图片")
                        .build();
                """;
        return Demos.sectionWithCode("1. 占位",
                "未设置 src 时显示 placeholder 占位，避免空白。", code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                ImageAnt.create().width(120).height(120).placeholder("方形").build(),
                ImageAnt.create().width(120).height(120).borderRadius(12)
                        .placeholder("圆角").build()
        );
        String code = """
                ImageAnt.create().width(120).height(120).placeholder("方形").build();
                ImageAnt.create().width(120).height(120)
                        .borderRadius(12).placeholder("圆角").build();
                // 加载真实图片：ImageAnt.create().src("https://...").build();
                """;
        return Demos.sectionWithCode("2. 尺寸与圆角",
                "width / height 约束尺寸，borderRadius 裁剪圆角。", code, demo);
    }

    private Node dynamicSection() {
        Node image = ImageAnt.create().width(160).height(100).placeholder("占位图").build();
        ImageAnt.Controller controller = ImageAnt.controllerOf(image);
        boolean[] rounded = {false};

        ButtonAnt toggleBtn = ButtonAnt.create("切换样式")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    rounded[0] = !rounded[0];
                    controller.setBorderRadius(rounded[0] ? 16 : 0);
                    controller.setPlaceholder(rounded[0] ? "圆角" : "占位图");
                })
                .build();

        Node demo = Demos.column(image, toggleBtn);
        String code = """
                Node image = ImageAnt.create().width(160).height(100).placeholder("占位图").build();
                ImageAnt.Controller controller = ImageAnt.controllerOf(image);
                controller.setBorderRadius(16);
                controller.setPlaceholder("圆角");
                """;
        return Demos.sectionWithCode("3. 动态切换",
                "点击按钮在默认样式和圆角样式之间切换，通过 Controller 更新同一个图片节点。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Image 的 4 个维度：图片来源 / 尺寸 / 圆角 / 占位文本。
     *
     * <p>ImageAnt 提供静态 {@code controllerOf(Node)}，Controller 暴露
     * {@code setSrc / setSize / setBorderRadius / setPlaceholder / setAlt}。
     * 这些方法都通过 {@code Builder#render(container)} 原地刷新子节点 —— StackPane
     * 容器不重建，display 节点引用始终有效。所以采用 {@link PlayGround#rebindController}：
     * apply Runnable 内部调 controller.setXxx，无重建、无闪烁。</p>
     *
     * <p>说明：Controller 的尺寸接口是 {@code setSize(w, h)}（统一接口），不支持单独
     * 改 width / height —— 故 playground 用一个 segmented 同时控制宽高（同值方形）。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> src          = PlayGround.binder("");
        Binder<String> size         = PlayGround.binder("160");
        Binder<String> borderRadius = PlayGround.binder("0");
        Binder<String> placeholder  = PlayGround.binder("暂无图片");

        // 2. 预 build display 并取 controller（controllerOf 要求 node 已 build）
        Node image = ImageAnt.create()
                .src(src.get())
                .width(parseSize(size.get()))
                .height(parseSize(size.get()))
                .borderRadius(parseRadius(borderRadius.get()))
                .placeholder(placeholder.get())
                .build();
        ImageAnt.Controller controller = ImageAnt.controllerOf(image);

        // 3. apply Runnable —— 读 binder → 调 controller.setXxx（StackPane 子节点原地刷新）
        Runnable apply = () -> {
            double s = parseSize(size.get());
            controller.setSrc(emptyToNull(src.get()));
            controller.setSize(s, s);
            controller.setBorderRadius(parseRadius(borderRadius.get()));
            controller.setPlaceholder(emptyToNull(placeholder.get()));
        };

        // 4. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Image 的来源 / 尺寸 / 圆角 / 占位文本 —— Controller 在原 StackPane 内重渲子节点，不重建容器。",
                PlayGround.rebindController(image, apply, null,
                        PlayGround.row("图片来源", PlayGround.textField(src, src.get(), "输入 URL（留空显示占位）")),
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("120", "120×120"),
                                PlayGround.entry("160", "160×160"),
                                PlayGround.entry("200", "200×200"),
                                PlayGround.entry("240", "240×240"))),
                        PlayGround.row("圆角", PlayGround.segmented(borderRadius,
                                PlayGround.entry("0",  "无"),
                                PlayGround.entry("4",  "4px"),
                                PlayGround.entry("8",  "8px"),
                                PlayGround.entry("16", "16px"))),
                        PlayGround.row("占位文本", PlayGround.textField(placeholder, placeholder.get(), "占位时显示的文本"))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static int parseSize(String v) {
        if (v == null) return 160;
        try { return Math.max(40, Math.min(400, Integer.parseInt(v))); }
        catch (NumberFormatException e) { return 160; }
    }

    private static double parseRadius(String v) {
        if (v == null) return 0;
        try { return Math.max(0, Math.min(64, Integer.parseInt(v))); }
        catch (NumberFormatException e) { return 0; }
    }

    /** 空串视为 null —— Controller 内部据此显示 placeholder/fallback。 */
    private static String emptyToNull(String v) {
        return (v == null || v.isEmpty()) ? null : v;
    }
}
