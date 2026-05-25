package org.openkawu.jfxium.demo.showcase.pages;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AvatarAnt;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.ListAnt;
import org.openkawu.jfxium.component.ListViewAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * List 列表展示页（M19.10）—— 包含 ListAnt（卡片式列表）和 ListViewAnt（原生列表）。
 */
public class ListPage implements ShowcasePage {

    @Override public String   key()      { return "list"; }
    @Override public String   title()    { return "List 列表"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("List 列表");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("两种列表：ListAnt 卡片式（标题+描述+头像+操作）/ ListViewAnt 原生（高性能滚动）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasicList(),
                        sectionListWithAvatar(),
                        sectionListWithHeader(),
                        sectionListView()
                )
                .build();
    }

    private Node sectionBasicList() {
        VBox list = ListAnt.create()
                .item("AntDesign 6.0", "新一代企业级 UI 设计语言")
                .item("Element Plus", "面向开发者的桌面端组件库")
                .item("Material UI", "Google 风格 React 组件库")
                .bordered(true)
                .split(true)
                .build();
        list.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("场景 1：基础列表（标题 + 描述）")
                .description(".item(title, description) + .bordered + .split —— 简单明了")
                .demo(list)
                .code("""
                        VBox list = ListAnt.create()
                            .item("AntDesign 6.0", "新一代企业级 UI 设计语言")
                            .item("Element Plus", "面向开发者的桌面端组件库")
                            .bordered(true)
                            .split(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionListWithAvatar() {
        VBox list = ListAnt.create()
                .item(AvatarAnt.create("张").build(),
                        "张三", "高级管理员 · 上次登录 2 小时前",
                        ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build())
                .item(AvatarAnt.create("李").build(),
                        "李四", "编辑 · 上次登录 1 天前",
                        ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build())
                .item(AvatarAnt.create("王").build(),
                        "王五", "访客 · 上次登录 1 周前",
                        ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build())
                .bordered(true)
                .split(true)
                .build();
        list.setMaxWidth(480);

        return ShowcaseSection.create()
                .title("场景 2：用户列表（头像 + 标题 + 描述 + 操作）")
                .description("admin 用户/会员/订阅人列表的高频组合")
                .demo(list)
                .code("""
                        ListAnt.create()
                            .item(AvatarAnt.create("张").build(),
                                  "张三", "高级管理员 · 上次登录 2 小时前",
                                  ButtonAnt.create("编辑").type(Type.LINK).build())
                            .bordered(true)
                            .split(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionListWithHeader() {
        VBox list = ListAnt.create()
                .header("最近活动")
                .item("张三 创建了订单 #00892", "2 分钟前")
                .item("李四 修改了用户配置", "1 小时前")
                .item("王五 上传了文件 doc.pdf", "今天 10:23")
                .footer("查看全部 →")
                .bordered(true)
                .split(true)
                .build();
        list.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("场景 3：带 Header / Footer")
                .description(".header / .footer 文本 —— 适合活动流、最近事件、通知列表")
                .demo(list)
                .code("""
                        ListAnt.create()
                            .header("最近活动")
                            .item("张三 创建了订单", "2 分钟前")
                            .item("李四 修改了配置", "1 小时前")
                            .footer("查看全部 →")
                            .bordered(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionListView() {
        ListView<String> lv = ListViewAnt.<String>create()
                .items(FXCollections.observableArrayList(
                        "Java", "Kotlin", "Scala", "Groovy", "Clojure",
                        "JavaScript", "TypeScript", "Python", "Ruby", "Go",
                        "Rust", "Swift", "C#", "F#", "Haskell"
                ))
                .onSelect(value -> MessageAnt.info("选中：" + value))
                .build();
        lv.setPrefSize(280, 220);

        return ShowcaseSection.create()
                .title("场景 4：ListViewAnt 原生列表（虚拟滚动，适合大数据量）")
                .description("底层是 JavaFX ListView —— 虚拟化滚动，1 万条数据也不卡")
                .demo(lv)
                .code("""
                        ListView<String> lv = ListViewAnt.<String>create()
                            .items(FXCollections.observableArrayList("Java", "Kotlin", ...))
                            .onSelect(value -> handle(value))
                            .build();
                        """)
                .build();
    }
}
