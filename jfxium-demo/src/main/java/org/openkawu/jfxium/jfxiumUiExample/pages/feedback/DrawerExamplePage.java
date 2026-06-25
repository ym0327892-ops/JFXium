package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.DrawerAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

/**
 * Drawer 抽屉 —— 4 个方向滑入 + 自定义尺寸。
 *
 * <p>跟 Modal 的边界：Modal 居中弹窗、用户必须处理；Drawer 从屏幕边缘滑入、
 * 适合"详情页"、"筛选条件"、"设置面板"等不打断主操作的辅助内容。</p>
 *
 * <p><b>API 形式</b>：跟 Modal 一样是 Result 包装型 —— build() 返回 DrawerResult，
 * 必须再调 .open(owner) 才会显示。</p>
 */
public class DrawerExamplePage extends VBoxAnt {

    public DrawerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Drawer 抽屉")
                .description("从屏幕边缘滑入的辅助面板，常用于详情查看 / 表单编辑。")
                .sections(
                        placementSection(),
                        sizeSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 4 个方向的滑入。 */
    private Node placementSection() {
        Node row = Demos.row(
                ButtonAnt.create("从右侧滑入（默认）")
                        .type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> openSimple(DrawerAnt.Placement.RIGHT, "RIGHT"))
                        .build(),
                ButtonAnt.create("从左侧")
                        .onClick(e -> openSimple(DrawerAnt.Placement.LEFT, "LEFT"))
                        .build(),
                ButtonAnt.create("从顶部")
                        .onClick(e -> openSimple(DrawerAnt.Placement.TOP, "TOP"))
                        .build(),
                ButtonAnt.create("从底部")
                        .onClick(e -> openSimple(DrawerAnt.Placement.BOTTOM, "BOTTOM"))
                        .build()
        );
        String code = """
                DrawerAnt.create()
                        .title("Drawer 演示")
                        .content(contentNode)
                        .placement(DrawerAnt.Placement.RIGHT)   // LEFT / RIGHT / TOP / BOTTOM
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("1. 4 个方向",
                "placement(LEFT/RIGHT/TOP/BOTTOM) —— 决定从哪个方向滑入。",
                code, row);
    }

    /** 2. 自定义尺寸（width 影响左右抽屉，height 影响上下抽屉）。 */
    private Node sizeSection() {
        Node row = Demos.row(
                ButtonAnt.create("窄抽屉 320px")
                        .onClick(e -> DrawerAnt.create()
                                .title("窄抽屉")
                                .content("width(320) —— 适合简单详情。")
                                .placement(DrawerAnt.Placement.RIGHT)
                                .width(320)
                                .build()
                                .open((Node) e.getSource()))
                        .build(),
                ButtonAnt.create("宽抽屉 720px")
                        .onClick(e -> DrawerAnt.create()
                                .title("宽抽屉")
                                .content("width(720) —— 适合编辑表单。")
                                .placement(DrawerAnt.Placement.RIGHT)
                                .width(720)
                                .build()
                                .open((Node) e.getSource()))
                        .build()
        );
        String code = """
                // RIGHT/LEFT 时用 width 控制宽度
                DrawerAnt.create()
                        .title("窄抽屉")
                        .content("适合简单详情。")
                        .placement(DrawerAnt.Placement.RIGHT)
                        .width(320)
                        .build()
                        .open(ownerNode);

                // TOP/BOTTOM 时用 height 控制高度
                DrawerAnt.create()
                        .title("底部抽屉")
                        .content("适合筛选条件。")
                        .placement(DrawerAnt.Placement.BOTTOM)
                        .height(300)
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("2. 自定义宽度",
                "width(int) —— RIGHT/LEFT 时是宽度；TOP/BOTTOM 时改用 height(int)。",
                code, row);
    }

    /** 内部辅助：打开一个最朴素的 Drawer 演示某个 placement。 */
    private void openSimple(DrawerAnt.Placement placement, String label) {
        VBarAnt content = VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TypographyAnt.text("当前 placement = " + label).build(),
                        TypographyAnt.text("点击外部蒙层、按 ESC、或点击右上角 X 都可关闭。").build()
                )
                .build();

        // owner 传 stage 任意节点即可；这里用一个临时 Label 取 scene 即可
        // 实际项目用触发按钮自己作为 owner 更直接，见下面的 sizeSection
        Node owner = this;
        DrawerAnt.create()
                .title("Drawer 演示 - " + label)
                .content(content)
                .placement(placement)
                .build()
                .open(owner);
    }

    /**
     * Drawer 交互演示 —— 跟 Modal/Message 一样是 Result 包装型，无 Controller。
     *
     * <p>采用「按钮触发 + 配置预览」策略：display 容器里放一个"打开 Drawer"按钮，
     * 点击时根据当前所有 binder 的值动态 build() 一个新的 DrawerResult 并 open()。
     * 左侧控件只更新内部 Supplier 的闭包变量，不重建按钮本身。</p>
     */
    private Node playgroundSection() {
        Binder<String> placementBinder    = PlayGround.binder("RIGHT");
        Binder<String> sizeBinder         = PlayGround.binder("DEFAULT");
        Binder<String> widthBinder        = PlayGround.binder("378");
        Binder<String> heightBinder       = PlayGround.binder("378");
        Binder<String> closeBtnBinder     = PlayGround.binder("LEFT");
        Binder<String> maskClosableBinder = PlayGround.binder("on");

        Supplier<DrawerAnt.DrawerResult> drawerFactory = () -> DrawerAnt.create()
                .title("Drawer 交互演示")
                .content("placement=" + placementBinder.get()
                        + "\nsize=" + sizeBinder.get()
                        + "\nwidth=" + widthBinder.get() + "px / height=" + heightBinder.get() + "px"
                        + "\n关闭按钮位置=" + closeBtnBinder.get()
                        + "\n遮罩可关闭=" + maskClosableBinder.get())
                .placement(parsePlacement(placementBinder.get()))
                .size(parseDrawerSize(sizeBinder.get()))
                .width(parseInt(widthBinder.get(), 378, 200, 1200))
                .height(parseInt(heightBinder.get(), 378, 100, 800))
                .closePlacement(parseClosePlacement(closeBtnBinder.get()))
                .maskClosable(parseBool(maskClosableBinder.get()))
                .build();

        Supplier<Node> openBtnFactory = () -> ButtonAnt.create("打开 Drawer")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> drawerFactory.get().open((Node) e.getSource()))
                .build();

        return Demos.section("3. 交互演示",
                "通过左侧控件实时配置 Drawer 的 placement / size / width / height / 关闭按钮位置 / 遮罩可关闭 —— DrawerAnt 是 Result 包装型、无 Controller，配置项不会自动生效，必须点击右侧按钮才会以新配置弹出。",
                PlayGround.rebindRebuild(openBtnFactory, null,
                        PlayGround.row("placement", PlayGround.segmented(placementBinder,
                                PlayGround.entry("LEFT", "左侧"),
                                PlayGround.entry("RIGHT", "右侧"),
                                PlayGround.entry("TOP", "顶部"),
                                PlayGround.entry("BOTTOM", "底部"))),
                        PlayGround.row("size", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("DEFAULT", "默认"),
                                PlayGround.entry("LARGE", "大号"))),
                        PlayGround.row("width (px)", PlayGround.textField(widthBinder, "378", "宽度 200-1200")),
                        PlayGround.row("height (px)", PlayGround.textField(heightBinder, "378", "高度 100-800")),
                        PlayGround.row("关闭按钮位置", PlayGround.segmented(closeBtnBinder,
                                PlayGround.entry("LEFT", "左上"),
                                PlayGround.entry("RIGHT", "右上"),
                                PlayGround.entry("NONE", "不显示"))),
                        PlayGround.row("遮罩可关闭", PlayGround.segmented(maskClosableBinder,
                                PlayGround.entry("off", "禁用"),
                                PlayGround.entry("on", "启用")))));
    }

    private static DrawerAnt.Placement parsePlacement(String v) {
        if (v == null) return DrawerAnt.Placement.RIGHT;
        return switch (v.toUpperCase()) {
            case "LEFT"   -> DrawerAnt.Placement.LEFT;
            case "TOP"    -> DrawerAnt.Placement.TOP;
            case "BOTTOM" -> DrawerAnt.Placement.BOTTOM;
            default       -> DrawerAnt.Placement.RIGHT;
        };
    }

    private static DrawerAnt.Size parseDrawerSize(String v) {
        if (v == null) return DrawerAnt.Size.DEFAULT;
        return switch (v.toUpperCase()) {
            case "LARGE" -> DrawerAnt.Size.LARGE;
            default      -> DrawerAnt.Size.DEFAULT;
        };
    }

    private static DrawerAnt.ClosePlacement parseClosePlacement(String v) {
        if (v == null) return DrawerAnt.ClosePlacement.LEFT;
        return switch (v.toUpperCase()) {
            case "RIGHT" -> DrawerAnt.ClosePlacement.RIGHT;
            case "NONE"  -> DrawerAnt.ClosePlacement.NONE;
            default      -> DrawerAnt.ClosePlacement.LEFT;
        };
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

    private static int parseInt(String v, int fallback, int min, int max) {
        if (v == null || v.isBlank()) return fallback;
        try {
            int i = Integer.parseInt(v.trim());
            if (i < min) return min;
            if (i > max) return max;
            return i;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
