package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AvatarAnt;
import org.openkawu.jfxium.component.BadgeAnt;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Avatar + Badge 展示页（M19.8）。
 *
 * <p>两者经常组合使用（头像 + 通知红点），合并到一页便于对比/演示。</p>
 */
public class AvatarBadgePage implements ShowcasePage {

    @Override public String   key()      { return "avatar-badge"; }
    @Override public String   title()    { return "Avatar / Badge"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Avatar / Badge");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("用户头像 + 红点徽标——admin header 的常驻组合。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionAvatarSizes(),
                        sectionAvatarShapes(),
                        sectionAvatarKinds(),
                        sectionBadgeBasic(),
                        sectionBadgeOnAvatar()
                )
                .build();
    }

    // ============================================================
    // Avatar
    // ============================================================
    private Node sectionAvatarSizes() {
        Node sm = AvatarAnt.create("张").size(AvatarAnt.Size.SMALL).build();
        Node def = AvatarAnt.create("李").build();
        Node lg = AvatarAnt.create("王").size(AvatarAnt.Size.LARGE).build();
        Node xl = AvatarAnt.create("赵").size(AvatarAnt.Size.XL).build();
        Node custom = AvatarAnt.create("钱").size(48).build();

        HBox row = HBoxBuilder.create().spacing(12).children(sm, def, lg, xl, custom).build();

        return ShowcaseSection.create()
                .title("场景 1：Avatar 尺寸（SMALL/DEFAULT/LARGE/XL + 自定义像素）")
                .description("4 档预设 + .size(int) 自定义任意像素")
                .demo(row)
                .code("""
                        AvatarAnt.create("张").size(AvatarAnt.Size.SMALL).build();
                        AvatarAnt.create("李").build();
                        AvatarAnt.create("王").size(AvatarAnt.Size.LARGE).build();
                        AvatarAnt.create("赵").size(AvatarAnt.Size.XL).build();
                        AvatarAnt.create("钱").size(48).build();
                        """)
                .build();
    }

    private Node sectionAvatarShapes() {
        Node circle = AvatarAnt.create("圆").size(AvatarAnt.Size.LARGE).shape(AvatarAnt.Shape.CIRCLE).build();
        Node square = AvatarAnt.create("方").size(AvatarAnt.Size.LARGE).shape(AvatarAnt.Shape.SQUARE).build();

        HBox row = HBoxBuilder.create().spacing(12).children(circle, square).build();

        return ShowcaseSection.create()
                .title("场景 2：Avatar 两种形状（CIRCLE / SQUARE）")
                .description("默认圆形——admin 头像首选")
                .demo(row)
                .code("""
                        AvatarAnt.create("圆").shape(AvatarAnt.Shape.CIRCLE).build();
                        AvatarAnt.create("方").shape(AvatarAnt.Shape.SQUARE).build();
                        """)
                .build();
    }

    private Node sectionAvatarKinds() {
        Node text = AvatarAnt.create("U").build();
        Node textCN = AvatarAnt.create("张三").build();
        Node colored = AvatarAnt.create("L").backgroundColor("-color-success-emphasis").build();

        HBox row = HBoxBuilder.create().spacing(12).children(text, textCN, colored).build();

        return ShowcaseSection.create()
                .title("场景 3：Avatar 文字内容 + 自定义背景")
                .description("中文/英文/单字符均可；.backgroundColor 用主题变量保持统一")
                .demo(row)
                .code("""
                        AvatarAnt.create("U").build();
                        AvatarAnt.create("张三").build();
                        AvatarAnt.create("L").backgroundColor("-color-success-emphasis").build();
                        """)
                .build();
    }

    // ============================================================
    // Badge
    // ============================================================
    private Node sectionBadgeBasic() {
        Node count5 = BadgeAnt.create()
                .content(ButtonAnt.create("通知").build())
                .count(5).build();
        Node count99 = BadgeAnt.create()
                .content(ButtonAnt.create("收件箱").build())
                .count(99).build();
        Node dot = BadgeAnt.create()
                .content(ButtonAnt.create("待办").build())
                .dot(true).build();
        Node statusOk = BadgeAnt.create()
                .content(new Label("服务 A"))
                .dot(true).status(BadgeAnt.Status.SUCCESS).build();
        Node statusWarn = BadgeAnt.create()
                .content(new Label("服务 B"))
                .dot(true).status(BadgeAnt.Status.WARNING).build();
        Node statusErr = BadgeAnt.create()
                .content(new Label("服务 C"))
                .dot(true).status(BadgeAnt.Status.ERROR).build();

        HBox row = HBoxBuilder.create().spacing(20).children(count5, count99, dot).build();
        HBox row2 = HBoxBuilder.create().spacing(20).children(statusOk, statusWarn, statusErr).build();

        return ShowcaseSection.create()
                .title("场景 4：Badge 基础（数字 / 红点 / 状态点）")
                .description("count(N) 显示数字，dot(true) 红点，status() 4 种状态色（默认/success/warning/error）")
                .demo(VBoxBuilder.create().spacing(12).children(row, row2).build())
                .code("""
                        BadgeAnt.create().content(btn).count(5).build();
                        BadgeAnt.create().content(btn).dot(true).build();
                        BadgeAnt.create().content(label).dot(true).status(BadgeAnt.Status.WARNING).build();
                        """)
                .build();
    }

    private Node sectionBadgeOnAvatar() {
        Node a1 = BadgeAnt.create()
                .content(AvatarAnt.create("张").size(AvatarAnt.Size.LARGE).build())
                .count(3).build();
        Node a2 = BadgeAnt.create()
                .content(AvatarAnt.create("李").size(AvatarAnt.Size.LARGE).build())
                .dot(true).build();
        Node a3 = BadgeAnt.create()
                .content(AvatarAnt.create("王").size(AvatarAnt.Size.LARGE).build())
                .dot(true).status(BadgeAnt.Status.SUCCESS).build();

        HBox row = HBoxBuilder.create().spacing(20).children(a1, a2, a3).build();

        return ShowcaseSection.create()
                .title("场景 5：Badge 套 Avatar（admin header 经典）")
                .description("头像 + 红点未读数——admin 顶部用户菜单的标准搭配")
                .demo(row)
                .code("""
                        BadgeAnt.create()
                            .content(AvatarAnt.create("张").size(AvatarAnt.Size.LARGE).build())
                            .count(3)
                            .build();
                        """)
                .build();
    }
}
