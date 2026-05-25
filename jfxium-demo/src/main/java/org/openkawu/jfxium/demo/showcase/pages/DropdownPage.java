package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AvatarAnt;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.DropdownAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Dropdown 展示页（M19.10）—— trigger + Popup 自定义弹层。
 */
public class DropdownPage implements ShowcasePage {

    @Override public String   key()      { return "dropdown"; }
    @Override public String   title()    { return "Dropdown 下拉浮层"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Dropdown 下拉浮层");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("点击任意 Node 触发 Popup 弹出菜单。与 MenuButton 区分：MenuButton 是「按钮+下拉」整体；Dropdown 是「任意 Node 挂下拉」。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithDivider(),
                        sectionWithDisabled(),
                        sectionOnAvatar()
                )
                .build();
    }

    private Node sectionBasic() {
        Node trigger = ButtonAnt.create("操作 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("edit", "编辑")
                .item("copy", "复制")
                .item("share", "分享")
                .onSelect(key -> MessageAnt.success("选中：" + key))
                .build();

        return ShowcaseSection.create()
                .title("场景 1：基础用法")
                .description("trigger 任意 Node + items 列表 + onSelect 回调")
                .demo(trigger)
                .code("""
                        Node trigger = ButtonAnt.create("操作 ▾").build();
                        DropdownAnt.create()
                            .trigger(trigger)
                            .item("edit", "编辑")
                            .item("copy", "复制")
                            .onSelect(key -> handle(key))
                            .build();
                        """)
                .build();
    }

    private Node sectionWithDivider() {
        Node trigger = ButtonAnt.create("更多 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("view", "查看")
                .item("edit", "编辑")
                .divider()
                .item("export", "导出")
                .item("share", "分享")
                .divider()
                .item("delete", "删除")
                .onSelect(key -> MessageAnt.info("选中：" + key))
                .build();

        return ShowcaseSection.create()
                .title("场景 2：分组分隔线")
                .description(".divider() 在菜单项之间插入分隔线 —— 区分功能组")
                .demo(trigger)
                .code("""
                        DropdownAnt.create()
                            .trigger(trigger)
                            .item("view", "查看")
                            .item("edit", "编辑")
                            .divider()
                            .item("export", "导出")
                            .divider()
                            .item("delete", "删除")
                            .build();
                        """)
                .build();
    }

    private Node sectionWithDisabled() {
        Node trigger = ButtonAnt.create("权限菜单 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("view", "查看")
                .item("edit", "编辑")
                .item("delete", "删除", true)
                .item("admin", "管理设置", true)
                .build();

        return ShowcaseSection.create()
                .title("场景 3：禁用项（权限场景）")
                .description(".item(key, label, disabled=true) —— 用户无权限时灰显但不消失")
                .demo(trigger)
                .code("""
                        DropdownAnt.create()
                            .trigger(trigger)
                            .item("view", "查看")
                            .item("edit", "编辑")
                            .item("delete", "删除", true)         // 灰显
                            .item("admin", "管理设置", true)
                            .build();
                        """)
                .build();
    }

    private Node sectionOnAvatar() {
        Node avatar = AvatarAnt.create("张").size(AvatarAnt.Size.LARGE).build();
        DropdownAnt.create()
                .trigger(avatar)
                .item("profile", "个人资料")
                .item("settings", "账户设置")
                .divider()
                .item("logout", "退出登录")
                .onSelect(key -> MessageAnt.info("用户菜单：" + key))
                .build();

        Label hint = new Label("点击头像试试 →");
        hint.setStyle("-fx-text-fill: -color-fg-muted;");

        HBox row = HBoxBuilder.create().spacing(12).children(hint, avatar).build();

        return ShowcaseSection.create()
                .title("场景 4：挂在 Avatar 上（admin header 经典）")
                .description("点击头像弹出用户菜单 —— admin 顶栏的标准做法")
                .demo(row)
                .code("""
                        Node avatar = AvatarAnt.create("张").size(AvatarAnt.Size.LARGE).build();
                        DropdownAnt.create()
                            .trigger(avatar)
                            .item("profile", "个人资料")
                            .item("settings", "账户设置")
                            .divider()
                            .item("logout", "退出登录")
                            .build();
                        """)
                .build();
    }
}
