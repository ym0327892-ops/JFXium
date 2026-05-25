package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.component.SplitBarAnt;
import org.openkawu.jfxium.component.TagAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * SplitBarAnt 展示页（M19）。
 *
 * <p>5 个 Section 覆盖三段式 / 二段式 / 多节点 / 实战场景。</p>
 */
public class SplitBarPage implements ShowcasePage {

    @Override public String   key()      { return "split-bar"; }
    @Override public String   title()    { return "SplitBar 三段式布局"; }
    @Override public Category category() { return Category.LAYOUT; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("SplitBar 三段式布局");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("横向 左 / 中 / 右 三段标准布局。center 不传即退化为二段（左+右）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionThreeWay(),
                        sectionTwoWay(),
                        sectionMultiNode(),
                        sectionModalHeader(),
                        sectionAppHeader()
                )
                .build();
    }

    // ============================================================
    // 1. 三段：左 + 中 + 右
    // ============================================================
    private Node sectionThreeWay() {
        Label title = new Label("订单详情");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");

        HBox bar = SplitBarAnt.create()
                .left(ButtonAnt.create("← 返回").build())
                .center(title)
                .right(
                        ButtonAnt.create("编辑").build(),
                        ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build()
                )
                .build();
        wrapBar(bar);

        return ShowcaseSection.create()
                .title("场景 1：三段式（左 + 中 + 右）")
                .description("center 真正居中——典型的页面 / Modal / Drawer 顶部栏布局")
                .demo(bar)
                .code("""
                        HBox bar = SplitBarAnt.create()
                            .left(backBtn)
                            .center(titleLabel)
                            .right(editBtn, saveBtn)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 二段：不传 center 自动退化
    // ============================================================
    private Node sectionTwoWay() {
        TextField search = InputAnt.create().placeholder("搜索...").build();
        search.setPrefWidth(200);

        HBox bar = SplitBarAnt.create()
                .left(search, ButtonAnt.create("筛选").build())
                .right(
                        ButtonAnt.create("导出").build(),
                        ButtonAnt.create("新增").type(ButtonAnt.Type.PRIMARY).build()
                )
                .build();
        wrapBar(bar);

        return ShowcaseSection.create()
                .title("场景 2：二段式（不传 center 自动退化）")
                .description("center 为空时退化为 [左 + 弹性 spacer + 右]，等价于 ActionBar 但语义更清晰")
                .demo(bar)
                .code("""
                        HBox bar = SplitBarAnt.create()
                            .left(searchField, filterBtn)
                            .right(exportBtn, addBtn)
                            .build();           // 没调 .center(...) → 二段
                        """)
                .build();
    }

    // ============================================================
    // 3. 每段多节点
    // ============================================================
    private Node sectionMultiNode() {
        Label title = new Label("用户管理");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");
        Node icon = IconAnt.path(IconAnt.Path.USER, 18);

        HBox bar = SplitBarAnt.create()
                .left(icon, title, TagAnt.create("Pro").type(TagAnt.Type.SUCCESS).build())
                .center(
                        ButtonAnt.create("列表").type(ButtonAnt.Type.TEXT).build(),
                        ButtonAnt.create("卡片").type(ButtonAnt.Type.TEXT).build(),
                        ButtonAnt.create("详情").type(ButtonAnt.Type.TEXT).build()
                )
                .right(
                        ButtonAnt.create("?").type(ButtonAnt.Type.TEXT).build(),
                        ButtonAnt.create("⚙").type(ButtonAnt.Type.TEXT).build()
                )
                .gap(12)
                .build();
        wrapBar(bar);

        return ShowcaseSection.create()
                .title("场景 3：每段多节点")
                .description("每段都允许多节点（按添加顺序水平排列）—— left=图标+标题+Tag / center=Tab / right=图标按钮组")
                .demo(bar)
                .code("""
                        HBox bar = SplitBarAnt.create()
                            .left(icon, title, proTag)          // 左：图标+标题+Tag
                            .center(listBtn, cardBtn, detailBtn) // 中：视图切换
                            .right(helpBtn, settingsBtn)         // 右：图标按钮
                            .gap(12)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 4. Modal Header 实战场景
    // ============================================================
    private Node sectionModalHeader() {
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: -color-warning-emphasis; -fx-font-size: 12px;");
        Label modalTitle = new Label("确认删除");
        modalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");

        Node closeBtn = ButtonAnt.create("✕").type(ButtonAnt.Type.TEXT).build();

        HBox header = SplitBarAnt.create()
                .left(dot, modalTitle)
                .right(closeBtn)
                .build();

        // 演示外观：包成一个完整 Modal-like 卡片
        Label body = new Label("此操作不可恢复，确定要继续吗？");
        body.setStyle("-fx-padding: 16 0 16 0;");

        HBox footer = SplitBarAnt.create()
                .right(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("确定删除").type(ButtonAnt.Type.PRIMARY).build()
                )
                .build();

        VBox modal = new VBox(0);
        modal.setStyle("-fx-background-color: -color-bg-default; -fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 16;");
        modal.setMaxWidth(420);
        modal.getChildren().addAll(header, body, footer);

        return ShowcaseSection.create()
                .title("场景 4：Modal Header / Footer 拼装")
                .description("一个 Modal 通常用 2 个 SplitBar：顶部 [状态点+标题 ｜ 关闭]、底部 [｜ 取消+确认]")
                .demo(modal)
                .code("""
                        // Modal Header：左标题 + 右关闭
                        HBox header = SplitBarAnt.create()
                            .left(statusDot, titleLabel)
                            .right(closeBtn)
                            .build();

                        // Modal Footer：仅右侧操作
                        HBox footer = SplitBarAnt.create()
                            .right(cancelBtn, okBtn)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. App Header 实战场景
    // ============================================================
    private Node sectionAppHeader() {
        Label logo = new Label("JFXium");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: -color-accent-emphasis;");

        TextField appSearch = InputAnt.create().placeholder("全局搜索...").build();
        appSearch.setPrefWidth(360);

        Node user = TagAnt.create("张三").build();
        Node msg = ButtonAnt.create("消息").type(ButtonAnt.Type.TEXT).build();

        HBox header = SplitBarAnt.create()
                .left(logo)
                .center(appSearch)
                .right(msg, user)
                .build();
        header.setStyle("-fx-padding: 12 24 12 24; -fx-background-color: -color-bg-default; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1 0;");

        return ShowcaseSection.create()
                .title("场景 5：App Header（顶部全局栏）")
                .description("[Logo ｜ 搜索框 ｜ 消息+用户]——center 是搜索框时受 left/right 内容挤压会自然伸缩")
                .demo(header)
                .code("""
                        HBox header = SplitBarAnt.create()
                            .left(logo)
                            .center(searchField)
                            .right(msgBtn, userTag)
                            .build();
                        """)
                .build();
    }

    /** 给演示 bar 加点最低限度的视觉边界，让 demo 中能看到布局效果。 */
    private void wrapBar(HBox bar) {
        bar.setStyle("-fx-padding: 12 16 12 16; -fx-background-color: -color-bg-subtle; -fx-background-radius: 6;");
        bar.setMinHeight(48);
    }
}
