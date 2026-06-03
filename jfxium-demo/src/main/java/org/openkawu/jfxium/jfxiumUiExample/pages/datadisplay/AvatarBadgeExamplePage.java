package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.BadgeAnt;
import org.openkawu.jfxium.component.composite.AvatarAnt;

/**
 * Avatar 头像 + Badge 徽标 —— 形状尺寸 / 文字图标 / 徽标 / 组合。
 */
public class AvatarBadgeExamplePage extends VBoxAnt {

    public AvatarBadgeExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Avatar 头像 + Badge 徽标")
                .description("头像用于展示用户或事物，徽标用于数字提示或状态标记，二者常组合使用。")
                .sections(avatarShapeSection(), avatarTypeSection(), badgeSection(), comboSection())
                .padding(24)
                .build());
    }

    private Node avatarShapeSection() {
        StackPane circle = AvatarAnt.create().text("U").size(AvatarAnt.Size.SMALL).build();
        StackPane circleM = AvatarAnt.create().text("U").build();
        StackPane circleL = AvatarAnt.create().text("U").size(AvatarAnt.Size.LARGE).build();
        StackPane circleXL = AvatarAnt.create().text("U").size(AvatarAnt.Size.XL).build();
        StackPane square = AvatarAnt.create().text("S").shape(AvatarAnt.Shape.SQUARE).build();
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
        StackPane textAvatar = AvatarAnt.create().text("JF").build();
        StackPane charAvatar = AvatarAnt.create("K").build();
        StackPane customSize = AvatarAnt.create().text("48").size(48).build();
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
        StackPane countBadge = BadgeAnt.create()
                .content(new Label("消息"))
                .count(5)
                .build();
        StackPane dotBadge = BadgeAnt.create()
                .content(new Label("通知"))
                .dot(true)
                .build();
        StackPane statusBadge = BadgeAnt.create()
                .content(new Label("在线"))
                .status(BadgeAnt.Status.SUCCESS)
                .build();
        Node demo = Demos.row(countBadge, dotBadge, statusBadge);
        String code = """
                BadgeAnt.create().content(new Label("消息")).count(5).build();
                BadgeAnt.create().content(new Label("通知")).dot(true).build();
                BadgeAnt.create().content(new Label("在线")).status(BadgeAnt.Status.SUCCESS).build();
                """;
        return Demos.sectionWithCode("3. Badge 徽标",
                "count 数字徽标；dot 红点；status 状态点（SUCCESS / WARNING / ERROR）。", code, demo);
    }

    private Node comboSection() {
        StackPane avatar = AvatarAnt.create().text("JF").size(AvatarAnt.Size.LARGE).build();
        StackPane badge = BadgeAnt.create()
                .content(avatar)
                .count(99)
                .build();

        StackPane avatar2 = AvatarAnt.create("K").build();
        StackPane dotBadge = BadgeAnt.create()
                .content(avatar2)
                .dot(true)
                .build();
        Node demo = Demos.row(badge, dotBadge);
        String code = """
                StackPane avatar = AvatarAnt.create().text("JF").size(AvatarAnt.Size.LARGE).build();
                BadgeAnt.create().content(avatar).count(99).build();

                StackPane avatar2 = AvatarAnt.create("K").build();
                BadgeAnt.create().content(avatar2).dot(true).build();
                """;
        return Demos.sectionWithCode("4. Avatar + Badge 组合",
                "BadgeAnt.content() 包裹 AvatarAnt，实现头像右上角徽标效果。", code, demo);
    }
}
