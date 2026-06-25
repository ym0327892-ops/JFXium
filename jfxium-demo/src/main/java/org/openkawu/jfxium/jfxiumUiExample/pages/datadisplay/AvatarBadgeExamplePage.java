package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.BadgeAnt;
import org.openkawu.jfxium.component.composite.AvatarAnt;

import java.util.function.Supplier;

/**
 * Avatar 头像 + Badge 徽标 —— 形状尺寸 / 文字图标 / 徽标 / 组合。
 */
public class AvatarBadgeExamplePage extends VBoxAnt {

    public AvatarBadgeExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Avatar 头像 + Badge 徽标")
                .description("头像用于展示用户或事物，徽标用于数字提示或状态标记，二者常组合使用。")
                .sections(avatarShapeSection(), avatarTypeSection(), badgeSection(), comboSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node avatarShapeSection() {
        Node circle = AvatarAnt.create().text("U").size(AvatarAnt.Size.SMALL).build();
        Node circleM = AvatarAnt.create().text("U").build();
        Node circleL = AvatarAnt.create().text("U").size(AvatarAnt.Size.LARGE).build();
        Node circleXL = AvatarAnt.create().text("U").size(AvatarAnt.Size.XL).build();
        Node square = AvatarAnt.create().text("S").shape(AvatarAnt.Shape.SQUARE).build();
        Node demo = Demos.row(circle, circleM, circleL, circleXL, square);
        String code = """
                AvatarAnt.create().text("U").size(AvatarAnt.Size.SMALL).build();
                AvatarAnt.create().text("U").build();  // DEFAULT
                AvatarAnt.create().text("U").size(AvatarAnt.Size.LARGE).build();
                AvatarAnt.create().text("U").size(AvatarAnt.Size.XL).build();
                AvatarAnt.create().text("S").shape(AvatarAnt.Shape.SQUARE).build();
                """;
        return Demos.sectionWithCode("1. 形状与尺寸",
                "CIRCLE / SQUARE 两种形状；SMALL / DEFAULT / LARGE / XL 四档尺寸。", code, demo);
    }

    private Node avatarTypeSection() {
        Node textAvatar = AvatarAnt.create().text("JF").build();
        Node charAvatar = AvatarAnt.create("K").build();
        Node customSize = AvatarAnt.create().text("48").size(48).build();
        Node demo = Demos.row(textAvatar, charAvatar, customSize);
        String code = """
                AvatarAnt.create().text("JF").build();
                AvatarAnt.create("K").build();
                AvatarAnt.create().text("48").size(48).build();  // 自定义像素尺寸
                """;
        return Demos.sectionWithCode("2. 文字头像",
                "text() 设置文字内容（最多显示 2 字符）；size(int) 自定义像素尺寸。", code, demo);
    }

    private Node badgeSection() {
        Node countBadge = BadgeAnt.create()
                .content(TypographyAnt.text("消息").build())
                .count(5)
                .build();
        Node dotBadge = BadgeAnt.create()
                .content(TypographyAnt.text("通知").build())
                .dot(true)
                .build();
        Node statusBadge = BadgeAnt.create()
                .content(TypographyAnt.text("在线").build())
                .status(BadgeAnt.Status.SUCCESS)
                .build();
        Node demo = Demos.row(countBadge, dotBadge, statusBadge);
        String code = """
                BadgeAnt.create().content(TypographyAnt.text("消息").build()).count(5).build();
                BadgeAnt.create().content(TypographyAnt.text("通知").build()).dot(true).build();
                BadgeAnt.create().content(TypographyAnt.text("在线").build()).status(BadgeAnt.Status.SUCCESS).build();
                """;
        return Demos.sectionWithCode("3. Badge 徽标",
                "count 数字徽标；dot 红点；status 状态点（SUCCESS / WARNING / ERROR）。", code, demo);
    }

    private Node comboSection() {
        Node avatar = AvatarAnt.create().text("JF").size(AvatarAnt.Size.LARGE).build();
        Node badge = BadgeAnt.create()
                .content(avatar)
                .count(99)
                .build();

        Node avatar2 = AvatarAnt.create("K").build();
        Node dotBadge = BadgeAnt.create()
                .content(avatar2)
                .dot(true)
                .build();
        Node demo = Demos.row(badge, dotBadge);
        String code = """
                Node avatar = AvatarAnt.create().text("JF").size(AvatarAnt.Size.LARGE).build();
                BadgeAnt.create().content(avatar).count(99).build();

                Node avatar2 = AvatarAnt.create("K").build();
                BadgeAnt.create().content(avatar2).dot(true).build();
                """;
        return Demos.sectionWithCode("4. Avatar + Badge 组合",
                "BadgeAnt.content() 包裹 AvatarAnt，实现头像右上角徽标效果。", code, demo);
    }

    /**
     * 5. 交互演示 —— 通过左侧控件实时改变 Avatar 的尺寸 / 形状，以及 Badge 的类型 / 数字。
     *
     * <p>AvatarAnt / BadgeAnt 均无 Controller，所有属性变更均通过 build 重建生效。</p>
     */
    private Node playgroundSection() {
        Binder<String> avatarSizeBinder  = PlayGround.binder("default");
        Binder<String> avatarShapeBinder = PlayGround.binder("circle");
        Binder<String> badgeTypeBinder   = PlayGround.binder("count");
        Binder<String> badgeCountBinder  = PlayGround.binder("5");

        Supplier<Node> factory = () -> {
            Node avatar = AvatarAnt.create()
                    .text("JF")
                    .size(parseAvatarSize(avatarSizeBinder.get()))
                    .shape(parseAvatarShape(avatarShapeBinder.get()))
                    .build();
            BadgeAnt.Builder b = BadgeAnt.create().content(avatar);
            switch (badgeTypeBinder.get()) {
                case "dot"    -> b = b.dot(true);
                case "status" -> b = b.status(BadgeAnt.Status.SUCCESS);
                case "count"  -> b = b.count(parseInt(badgeCountBinder.get(), 5, 0, 999));
                default       -> { /* none：不加 indicator */ }
            }
            return b.build();
        };

        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Avatar 的尺寸 / 形状，以及 Badge 的类型 / 数字 —— 两者均无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("头像尺寸", PlayGround.segmented(avatarSizeBinder,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"),
                                PlayGround.entry("xl",      "超大"))),
                        PlayGround.row("头像形状", PlayGround.segmented(avatarShapeBinder,
                                PlayGround.entry("circle", "圆形"),
                                PlayGround.entry("square", "方形"))),
                        PlayGround.row("徽标类型", PlayGround.segmented(badgeTypeBinder,
                                PlayGround.entry("none",   "无"),
                                PlayGround.entry("count",  "数字"),
                                PlayGround.entry("dot",    "圆点"),
                                PlayGround.entry("status", "状态"))),
                        PlayGround.row("徽标数字", PlayGround.textField(badgeCountBinder, "5", "输入徽标数字"))));
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

    private static AvatarAnt.Size parseAvatarSize(String v) {
        if (v == null) return AvatarAnt.Size.DEFAULT;
        return switch (v) {
            case "small"   -> AvatarAnt.Size.SMALL;
            case "large"   -> AvatarAnt.Size.LARGE;
            case "xl"      -> AvatarAnt.Size.XL;
            default        -> AvatarAnt.Size.DEFAULT;
        };
    }

    private static AvatarAnt.Shape parseAvatarShape(String v) {
        if (v == null) return AvatarAnt.Shape.CIRCLE;
        return "square".equalsIgnoreCase(v) ? AvatarAnt.Shape.SQUARE : AvatarAnt.Shape.CIRCLE;
    }
}
