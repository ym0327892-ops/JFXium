package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.geometry.Orientation;
import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.DividerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Divider 分割线 —— 水平 / 带文本 / 文本位置 / 垂直。
 */
public class DividerExamplePage extends VBoxAnt {

    public DividerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Divider 分割线")
                .description("区隔内容的视觉分割线，支持水平/垂直方向、带文本标注、文本位置控制。")
                .sections(
                        basicSection(),
                        textSection(),
                        positionSection(),
                        verticalSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                TypographyAnt.text("上方内容").build(),
                DividerAnt.create().build(),
                TypographyAnt.text("下方内容").build()
        );
        String code = """
                DividerAnt.create().build();
                """;
        return Demos.sectionWithCode("1. 基础水平分割线",
                "无参 create() 默认生成水平分割线。",
                code, demo);
    }

    private Node textSection() {
        Node demo = Demos.column(
                Demos.column(
                        DividerAnt.create().text("居中文本").build()
                ),
                Demos.column(
                        DividerAnt.create().text("这是章节分隔").build()
                )
        );
        String code = """
                DividerAnt.create().text("居中文本").build();
                DividerAnt.create().text("这是章节分隔").build();
                """;
        return Demos.sectionWithCode("2. 带文本",
                "text(String) 在分割线中间显示文字标注，常用于'或'、章节名等。",
                code, demo);
    }

    private Node positionSection() {
        Node demo = Demos.column(
                DividerAnt.create().text("靠左").position(DividerAnt.Position.LEFT).build(),
                DividerAnt.create().text("居中（默认）").position(DividerAnt.Position.CENTER).build(),
                DividerAnt.create().text("靠右").position(DividerAnt.Position.RIGHT).build()
        );
        String code = """
                DividerAnt.create().text("靠左")
                        .position(DividerAnt.Position.LEFT).build();
                DividerAnt.create().text("居中（默认）")
                        .position(DividerAnt.Position.CENTER).build();
                DividerAnt.create().text("靠右")
                        .position(DividerAnt.Position.RIGHT).build();
                """;
        return Demos.sectionWithCode("3. 文本位置",
                "position(LEFT/CENTER/RIGHT) 控制分割线中文本的位置，对应 Ant Design 的 orientation 属性。",
                code, demo);
    }

    private Node verticalSection() {
        Node demo = Demos.row(
                TypographyAnt.text("项目一").build(),
                DividerAnt.create().vertical().build(),
                TypographyAnt.text("项目二").build(),
                DividerAnt.create().vertical().build(),
                TypographyAnt.text("项目三").build()
        );
        String code = """
                Node box = HBoxAnt.create().spacing(12).children(
                        TypographyAnt.text("项目一").build(),
                        DividerAnt.create().vertical().build(),
                        TypographyAnt.text("项目二").build(),
                        DividerAnt.create().vertical().build(),
                        TypographyAnt.text("项目三").build()
                );
                """;
        return Demos.sectionWithCode("4. 垂直分割线",
                "vertical() 创建垂直方向分割线，适合在横向排列的元素之间插入。",
                code, demo);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— rebuild 模式样板。
     *
     * <p>DividerAnt 没有暴露 modify()，所以控制区回调里需要重新调用 {@code build()}
     * 构造新节点，再替换展示区。{@link PlayGround#rebindRebuild} 把这套流程封装成
     * 「声明式 API」：准备 Binder 状态盒子 + 写 display 工厂 + 串 row 即可，
     * 鸡生蛋问题（先 build 拿 display → 注册监听 → 二次 build）由 rebindRebuild 内部处理。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> orientation = PlayGround.binder("horizontal");
        Binder<String> text        = PlayGround.binder("OR");
        Binder<String> position    = PlayGround.binder("center");

        // 2. display 工厂 —— 每次调用都反映 binder 当前值
        java.util.function.Supplier<Node> factory = () -> {
            String t = "empty".equals(text.get()) ? "" : text.get();
            return DividerAnt.create()
                    .orientation("vertical".equals(orientation.get())
                            ? javafx.geometry.Orientation.VERTICAL
                            : javafx.geometry.Orientation.HORIZONTAL)
                    .position(parsePosition(position.get()))
                    .text(t)
                    .build();
        };

        // 3. 串起来 —— rebindRebuild 内部自动 scaffold build + 监听注册 + 二次 build
        Node playground = PlayGround.rebindRebuild(factory, "方向 / 文本 / 位置",
                PlayGround.row("方向", PlayGround.segmented(orientation,
                        PlayGround.entry("horizontal", "水平"),
                        PlayGround.entry("vertical",   "垂直"))),
                PlayGround.row("文本", PlayGround.segmented(text,
                        PlayGround.entry("OR",      "OR"),
                        PlayGround.entry("section", "章节"),
                        PlayGround.entry("empty",   "(无文本)"))),
                PlayGround.row("位置", PlayGround.segmented(position,
                        PlayGround.entry("left",   "靠左"),
                        PlayGround.entry("center", "居中"),
                        PlayGround.entry("right",  "靠右"))));

        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Divider 的方向、文本、位置 —— 三大属性任意组合。",
                playground);
    }

    /** 文本位置字符串 → DividerAnt.Position 枚举。 */
    private static DividerAnt.Position parsePosition(String v) {
        if (v == null) return DividerAnt.Position.CENTER;
        return switch (v) {
            case "left"  -> DividerAnt.Position.LEFT;
            case "right" -> DividerAnt.Position.RIGHT;
            default      -> DividerAnt.Position.CENTER;
        };
    }
}
