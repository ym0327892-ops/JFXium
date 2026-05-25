package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * CardAnt 组件展示页（M10）。
 *
 * <p>覆盖 M10 全部 9 个功能：</p>
 * <ul>
 *   <li>基础用法（title + extra + content）</li>
 *   <li>4 档阴影（NONE / SMALL / MEDIUM / LARGE）</li>
 *   <li>2 档尺寸（MEDIUM / SMALL）</li>
 *   <li>2 种类型（DEFAULT / INNER 内嵌卡片）</li>
 *   <li>bordered + hoverable</li>
 *   <li>actions 底部操作按钮</li>
 *   <li>tabList 标签页</li>
 *   <li>loading 加载状态（骨架屏）</li>
 *   <li>cover 封面（图片路径 + 自定义 Node）</li>
 * </ul>
 */
public class CardPage implements ShowcasePage {

    @Override public String   key()      { return "card"; }
    @Override public String   title()    { return "Card 卡片"; }
    @Override public Category category() { return Category.LAYOUT; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Card 卡片");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("通用卡片容器，支持封面、标签页、加载状态、底部操作等。M10 9 大功能全展示。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create()
                .spacing(8)
                .children(pageTitle, pageDesc)
                .build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionShadows(),
                        sectionSizes(),
                        sectionTypes(),
                        sectionHoverable(),
                        sectionActions(),
                        sectionTabs(),
                        sectionLoading(),
                        sectionCover()
                )
                .build();
    }

    // ============================================================
    // 1. 基础用法
    // ============================================================
    private Node sectionBasic() {
        VBox card = CardAnt.create()
                .title("基础卡片")
                .content(new Label("这是卡片内容。最常用的形态。"))
                .bordered(true)
                .build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("基础用法")
                .description("title + content 是最常用的两个配置；bordered(true) 加边框")
                .demo(card)
                .code("""
                        VBox card = CardAnt.create()
                            .title("基础卡片")
                            .content(new Label("这是卡片内容。"))
                            .bordered(true)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 4 档阴影
    // ============================================================
    private Node sectionShadows() {
        HBox grid = new HBox(16);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.getChildren().addAll(
                shadowDemo("Shadow.NONE",   CardAnt.Shadow.NONE),
                shadowDemo("Shadow.SMALL",  CardAnt.Shadow.SMALL),
                shadowDemo("Shadow.MEDIUM", CardAnt.Shadow.MEDIUM),
                shadowDemo("Shadow.LARGE",  CardAnt.Shadow.LARGE)
        );

        return ShowcaseSection.create()
                .title("4 档阴影")
                .description("Shadow.NONE / SMALL / MEDIUM / LARGE 控制卡片悬浮感")
                .demo(grid)
                .code("""
                        .shadow(CardAnt.Shadow.NONE)     // 无阴影
                        .shadow(CardAnt.Shadow.SMALL)    // 小阴影（默认推荐）
                        .shadow(CardAnt.Shadow.MEDIUM)   // 中等阴影
                        .shadow(CardAnt.Shadow.LARGE)    // 大阴影（强悬浮感）
                        """)
                .build();
    }

    private VBox shadowDemo(String label, CardAnt.Shadow shadow) {
        VBox card = CardAnt.create()
                .title(label)
                .content(new Label("内容..."))
                .bordered(true)
                .shadow(shadow)
                .build();
        card.setPrefWidth(180);
        return card;
    }

    // ============================================================
    // 3. 尺寸
    // ============================================================
    private Node sectionSizes() {
        HBox grid = new HBox(16);
        VBox medium = CardAnt.create()
                .title("MEDIUM（默认）")
                .content(new Label("padding 24px / 标题 16px"))
                .bordered(true).shadow(CardAnt.Shadow.SMALL)
                .size(CardAnt.Size.MEDIUM)
                .build();
        medium.setPrefWidth(240);

        VBox small = CardAnt.create()
                .title("SMALL")
                .content(new Label("padding 12px / 标题 14px"))
                .bordered(true).shadow(CardAnt.Shadow.SMALL)
                .size(CardAnt.Size.SMALL)
                .build();
        small.setPrefWidth(240);

        grid.getChildren().addAll(medium, small);

        return ShowcaseSection.create()
                .title("2 档尺寸")
                .description("Size.MEDIUM（默认 padding 24px）/ Size.SMALL（紧凑 padding 12px）")
                .demo(grid)
                .code("""
                        .size(CardAnt.Size.MEDIUM)   // 默认
                        .size(CardAnt.Size.SMALL)    // 紧凑
                        """)
                .build();
    }

    // ============================================================
    // 4. 类型（DEFAULT / INNER）
    // ============================================================
    private Node sectionTypes() {
        VBox innerCard = CardAnt.create()
                .title("内嵌卡片")
                .content(new Label("用 INNER 类型嵌在外层卡片里"))
                .type(CardAnt.Type.INNER)
                .size(CardAnt.Size.SMALL)
                .build();

        VBox outerContent = VBoxBuilder.create()
                .spacing(12)
                .children(
                        new Label("外层卡片正文..."),
                        new Label("下面嵌入一个内嵌卡片："),
                        innerCard
                )
                .build();

        VBox outer = CardAnt.create()
                .title("外层 DEFAULT 卡片")
                .content(outerContent)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        outer.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("DEFAULT vs INNER 类型")
                .description("INNER 类型用于嵌入在其它卡片内，背景更浅，无阴影")
                .demo(outer)
                .code("""
                        VBox innerCard = CardAnt.create()
                            .title("内嵌卡片")
                            .content(new Label("..."))
                            .type(CardAnt.Type.INNER)        // 关键
                            .size(CardAnt.Size.SMALL)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. Hoverable
    // ============================================================
    private Node sectionHoverable() {
        VBox card = CardAnt.create()
                .title("可悬停卡片")
                .content(new Label("鼠标移上来时阴影加深，常用于卡片列表"))
                .bordered(true)
                .hoverable(true)
                .build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("Hoverable 悬停反馈")
                .description("hoverable(true) 鼠标 hover 时阴影加深，提示可点击")
                .demo(card)
                .code("""
                        VBox card = CardAnt.create()
                            .title("可悬停卡片")
                            .content(new Label("..."))
                            .bordered(true)
                            .hoverable(true)              // 关键
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 6. Actions 底部操作
    // ============================================================
    private Node sectionActions() {
        Button btnEdit  = ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build();
        Button btnShare = ButtonAnt.create("分享").type(ButtonAnt.Type.LINK).build();
        Button btnMore  = ButtonAnt.create("更多").type(ButtonAnt.Type.LINK).build();

        VBox card = CardAnt.create()
                .title("带底部操作的卡片")
                .content(new Label("内容区..."))
                .actions(btnEdit, btnShare, btnMore)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("Actions 底部操作")
                .description("底部一排操作按钮，等分宽度。常用于卡片列表的「编辑/分享/更多」")
                .demo(card)
                .code("""
                        Button btnEdit  = ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build();
                        Button btnShare = ButtonAnt.create("分享").type(ButtonAnt.Type.LINK).build();
                        Button btnMore  = ButtonAnt.create("更多").type(ButtonAnt.Type.LINK).build();

                        VBox card = CardAnt.create()
                            .title("带底部操作的卡片")
                            .content(new Label("..."))
                            .actions(btnEdit, btnShare, btnMore)    // 关键
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 7. Tabs 标签页
    // ============================================================
    private Node sectionTabs() {
        Label tab1Content = new Label("📊 这是数据概览的内容...");
        Label tab2Content = new Label("📈 这是趋势分析的内容...");
        Label tab3Content = new Label("⚙ 这是配置项的内容...");

        VBox card = CardAnt.create()
                .title("带标签页的卡片")
                .tab("overview",  "概览",   tab1Content)
                .tab("analytics", "分析",   tab2Content)
                .tab("settings",  "配置",   tab3Content)
                .defaultActiveTabKey("overview")
                .onTabChange(e -> System.out.println("切换标签"))
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        card.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("Tabs 标签页")
                .description("卡片内嵌标签页，点击切换内容；defaultActiveTabKey 设置默认激活页")
                .demo(card)
                .code("""
                        VBox card = CardAnt.create()
                            .title("带标签页的卡片")
                            .tab("overview",  "概览",   tab1Content)
                            .tab("analytics", "分析",   tab2Content)
                            .tab("settings",  "配置",   tab3Content)
                            .defaultActiveTabKey("overview")
                            .onTabChange(e -> System.out.println("切换"))
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 8. Loading 加载状态
    // ============================================================
    private Node sectionLoading() {
        VBox card = CardAnt.create()
                .title("正在加载...")
                .loading(true)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("Loading 加载状态")
                .description("loading(true) 显示骨架屏占位，等真实内容加载完成后切换")
                .demo(card)
                .code("""
                        VBox card = CardAnt.create()
                            .title("正在加载...")
                            .loading(true)             // 关键
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 9. Cover 封面
    // ============================================================
    private Node sectionCover() {
        // 自定义 Node 封面（用一块色块代替图片，避免依赖外部资源）
        javafx.scene.layout.Region coverNode = new javafx.scene.layout.Region();
        coverNode.setMinHeight(120);
        coverNode.setStyle("-fx-background-color: linear-gradient(to bottom right, -color-accent-3, -color-accent-emphasis 60%, -color-accent-7);");

        VBox card = CardAnt.create()
                .cover(coverNode)
                .title("封面卡片")
                .content(new Label("封面 + 标题 + 内容 + 底部操作的完整搭配"))
                .actions(
                        ButtonAnt.create("详情").type(ButtonAnt.Type.LINK).build(),
                        ButtonAnt.create("收藏").type(ButtonAnt.Type.LINK).build()
                )
                .bordered(true)
                .shadow(CardAnt.Shadow.MEDIUM)
                .hoverable(true)
                .build();
        card.setMaxWidth(360);

        return ShowcaseSection.create()
                .title("Cover 封面")
                .description("cover(Node) 在卡片顶部加封面区域；可用图片路径 cover(String) 或自定义 Node")
                .demo(card)
                .code("""
                        // 1. 自定义 Node 封面（任意 JavaFX 节点）
                        Region coverNode = new Region();
                        coverNode.setMinHeight(120);
                        coverNode.setStyle("-fx-background-color: ...;");

                        VBox card = CardAnt.create()
                            .cover(coverNode)            // 自定义 Node
                            .title("封面卡片")
                            .content(...)
                            .actions(detailBtn, favBtn)
                            .build();

                        // 2. 也可以传图片路径
                        // .cover("/images/cover.jpg")
                        """)
                .build();
    }
}
