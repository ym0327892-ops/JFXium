package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.TextField;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Tag 标签 —— 类型 / 尺寸 / 形状 / 可关闭。
 */
public class TagExamplePage extends VBoxAnt {

    public TagExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tag 标签")
                .description("用于标记和分类的小标签组件。")
                .sections(
                        typeSection(),
                        sizeSection(),
                        shapeSection(),
                        closableSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node typeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Success").type(TagAnt.Type.SUCCESS).build(),
                TagAnt.create().text("Warning").type(TagAnt.Type.WARNING).build(),
                TagAnt.create().text("Error").type(TagAnt.Type.ERROR).build(),
                TagAnt.create().text("Processing").type(TagAnt.Type.PROCESSING).build()
        );
        String code = """
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Success").type(TagAnt.Type.SUCCESS).build();
                TagAnt.create().text("Warning").type(TagAnt.Type.WARNING).build();
                TagAnt.create().text("Error").type(TagAnt.Type.ERROR).build();
                TagAnt.create().text("Processing").type(TagAnt.Type.PROCESSING).build();
                """;
        return Demos.sectionWithCode("1. 类型",
                "不同语义类型对应不同颜色。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Small").size(Size.SMALL).build(),
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Large").size(Size.LARGE).build()
        );
        String code = """
                TagAnt.create().text("Small").size(Size.SMALL).build();
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Large").size(Size.LARGE).build();
                """;
        return Demos.sectionWithCode("2. 尺寸", "SMALL / DEFAULT / LARGE。", code, demo);
    }

    private Node shapeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Round").shape(TagAnt.Shape.ROUND).build()
        );
        String code = """
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Round").shape(TagAnt.Shape.ROUND).build();
                """;
        return Demos.sectionWithCode("3. 形状",
                "DEFAULT（小圆角）/ ROUND（全圆角胶囊）。",
                code, demo);
    }

    private Node closableSection() {
        Node demo = Demos.row(
                TagAnt.create().text("可关闭").closable(true).onClose(() -> MessageAnt.info("Tag 已关闭")).build(),
                TagAnt.create().text("Success 可关闭")
                        .type(TagAnt.Type.SUCCESS).closable(true).onClose(() -> MessageAnt.success("Success Tag 已关闭")).build()
        );
        String code = """
                TagAnt.create().text("可关闭")
                        .closable(true)
                        .onClose(() -> MessageAnt.info("Tag 已关闭"))
                        .build();
                """;
        return Demos.sectionWithCode("4. 可关闭",
                "closable(true) 显示关闭按钮；onClose 回调处理关闭逻辑。",
                code, demo);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— modify+apply 模式样板。
     *
     * <p>TagAnt 提供了 {@link TagAnt#modify(javafx.scene.layout.HBox)} 入口，
     * 回调里调用 {@code modify(tag).xxx().apply()} 原地修改 —— 无重建、不闪烁、
     * 状态完全保留（text 不动则文字不变、bordered 切换不影响文本）。</p>
     *
     * <p>{@link PlayGround#rebindModify} 把这套流程封装成「声明式 API」：
     * 准备 Binder 状态盒子 + 写 modify apply + 串 row 即可，
     * rebindModify 内部会自动提取 binder 注册监听。</p>
     */
    private Node playgroundSection() {
        // 1. 预先 build 出一个 Tag 节点 —— 它就是 display 里唯一要存在的 Node
        javafx.scene.layout.HBox tag = TagAnt.create("Tag")
                .type(TagAnt.Type.DEFAULT)
                .size(Size.DEFAULT)
                .shape(TagAnt.Shape.DEFAULT)
                .bordered(true)
                .build();

        // 2. 状态盒子
        Binder<String> type     = PlayGround.binder("default");
        Binder<String> size     = PlayGround.binder("default");
        Binder<String> shape    = PlayGround.binder("default");
        Binder<String> bordered = PlayGround.binder("on");
        Binder<String> text     = PlayGround.binder("Tag");

        // 3. modify apply —— 读 binder 状态 → 原地修改 tag 节点
        Runnable apply = () -> TagAnt.modify(tag)
                .type(parseType(type.get()))
                .size(parseSize(size.get()))
                .shape(parseShape(shape.get()))
                .bordered("on".equals(bordered.get()))
                .text(text.get())
                .apply();

        // 4. 串起来 —— rebindModify 内部自动提取 binder 并注册监听
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Tag 的类型 / 尺寸 / 形状 / 边框 / 文本 —— 原地修改无重建。",
                PlayGround.rebindModify(tag, apply, null,
                        PlayGround.row("类型", PlayGround.segmented(type,
                                PlayGround.entry("default",    "Default"),
                                PlayGround.entry("primary",    "Primary"),
                                PlayGround.entry("success",    "Success"),
                                PlayGround.entry("warning",    "Warning"),
                                PlayGround.entry("error",      "Error"),
                                PlayGround.entry("processing", "Processing"))),
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("small",   "Small"),
                                PlayGround.entry("default", "Default"),
                                PlayGround.entry("large",   "Large"))),
                        PlayGround.row("形状", PlayGround.segmented(shape,
                                PlayGround.entry("default", "Default"),
                                PlayGround.entry("round",   "Round"),
                                PlayGround.entry("square",  "Square"))),
                        PlayGround.row("边框", PlayGround.segmented(bordered,
                                PlayGround.entry("on",  "有边框"),
                                PlayGround.entry("off", "无边框"))),
                        PlayGround.row("文本", PlayGround.textField(text, text.get(), "输入标签文本"))));
    }

    // ============================================================
    // 枚举解析 helpers
    // ============================================================

    private static TagAnt.Type parseType(String v) {
        if (v == null) return TagAnt.Type.DEFAULT;
        return switch (v) {
            case "primary"    -> TagAnt.Type.PRIMARY;
            case "success"    -> TagAnt.Type.SUCCESS;
            case "warning"    -> TagAnt.Type.WARNING;
            case "error"      -> TagAnt.Type.ERROR;
            case "processing" -> TagAnt.Type.PROCESSING;
            default           -> TagAnt.Type.DEFAULT;
        };
    }

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small" -> Size.SMALL;
            case "large" -> Size.LARGE;
            default      -> Size.DEFAULT;
        };
    }

    private static TagAnt.Shape parseShape(String v) {
        if (v == null) return TagAnt.Shape.DEFAULT;
        return switch (v) {
            case "round"  -> TagAnt.Shape.ROUND;
            case "square" -> TagAnt.Shape.SQUARE;
            default       -> TagAnt.Shape.DEFAULT;
        };
    }
}
