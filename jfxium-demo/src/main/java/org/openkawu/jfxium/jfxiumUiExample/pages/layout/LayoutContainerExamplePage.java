package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.ResizablePanelAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.layout.AnchorPaneAnt;
import org.openkawu.jfxium.component.layout.BorderPaneAnt;
import org.openkawu.jfxium.component.layout.FlowPaneAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.ScrollPaneAnt;
import org.openkawu.jfxium.component.layout.SpaceAnt;
import org.openkawu.jfxium.component.layout.SplitPaneAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.TextFlowAnt;
import org.openkawu.jfxium.component.layout.TilePaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * 布局容器综合页 —— VBox / HBox / BorderPane / StackPane / SplitPane / ScrollPane
 * / FlowPane / TilePane / TextFlow / AnchorPane 的 spacing / alignment / grow /
 * padding 等「布局内样式」用法。
 */
public class LayoutContainerExamplePage extends VBoxAnt {

    public LayoutContainerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("布局容器")
                .description("常用布局容器与「布局内样式」综合演示：间距 / 对齐 / 弹性 / 填充 / 叠层 / 分栏。")
                .sections(
                        vboxSection(),
                        hboxSection(),
                        borderPaneSection(),
                        stackPaneSection(),
                        splitPaneSection(),
                        scrollPaneSection(),
                        flowTileSection(),
                        textFlowSection(),
                        anchorSection(),
                        spaceSection(),
                        surfaceSection(),
                        resizableSection()
                )
                .padding(24)
                .build());
    }

    // 视觉块 helper：给容器填色块节点，方便看间距/对齐效果
    private Node chip(String text) {
        return Demos.placeholder(text, Background.SUBTLE);
    }

    private Node vboxSection() {
        Node demo = VBoxAnt.create()
                .spacing(12)
                .children(
                        chip("VBox 子项 1"),
                        chip("VBox 子项 2"),
                        chip("VBox 子项 3")
                )
                .build();
        String code = """
                VBoxAnt.create()
                        .spacing(12)              // 纵向间距
                        .children(chip1, chip2, chip3)
                        .build();
                """;
        return Demos.sectionWithCode("1. VBox 纵向布局", "垂直堆叠 + 纵向间距。", code, demo);
    }

    private Node hboxSection() {
        // HBox：横向 + 弹性右推（spacer 撑开）
        Node spacer = new javafx.scene.layout.Region();
        Node demo = HBoxAnt.create()
                .spacing(8)
                .children(
                        ButtonAnt.create("左").build(),
                        ButtonAnt.create("中").build(),
                        spacer,
                        ButtonAnt.create("右（弹性推右）").type(ButtonAnt.Type.PRIMARY).build()
                )
                .build();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        String code = """
                HBoxAnt.create()
                        .spacing(8)
                        .children(btn1, btn2, spacer, btnRight)   // spacer 弹性撑开
                        .build();
                // 让 spacer 吃掉剩余宽度，右按钮被推到最右
                HBox.setHgrow(spacer, Priority.ALWAYS);
                """;
        return Demos.sectionWithCode("2. HBox 横向布局", "水平排列 + Hgrow 弹性填充（工具栏经典写法）。", code, demo);
    }

    private Node borderPaneSection() {
        Node demo = BorderPaneAnt.create()
                .top(chip("Top 顶栏"))
                .left(chip("Left 侧栏"))
                .center(chip("Center 内容"))
                .right(chip("Right 右栏"))
                .bottom(chip("Bottom 底栏"))
                .build();
        String code = """
                BorderPaneAnt.create()
                        .top(header).left(sider).center(content)
                        .right(extra).bottom(statusBar)
                        .build();   // 五区布局：应用/后台页面主骨架
                """;
        return Demos.sectionWithCode("3. BorderPane 五区骨架",
                "上/下/左/右/中五区，构成后台页面主骨架。", code, demo);
    }

    private Node stackPaneSection() {
        // StackPane 叠层：一张底 + 一个 "加载遮罩"
        Node demo = StackPaneAnt.create()
                .children(
                        Demos.placeholder("StackPane 内容", Background.SUBTLE),
                        Demos.placeholder("⌛ 叠在上层的浮层", Background.LAYOUT)
                )
                .build();
        String code = """
                StackPaneAnt.create()
                        .children(content, overlay)   // 后面的节点叠在内容上方
                        .build();                     // 常用于浮层 / 遮罩 / 角标叠加
                """;
        return Demos.sectionWithCode("4. StackPane 叠层", "节点按添加顺序叠放，后置者在上（遮罩/浮层）。", code, demo);
    }

    private Node splitPaneSection() {
        Node demo = SplitPaneAnt.create()
                .direction(SplitPaneAnt.Direction.HORIZONTAL)
                .items(
                        Demos.placeholder("左面板", Background.SUBTLE),
                        Demos.placeholder("右面板", Background.SUBTLE)
                )
                .dividerPositions(0.4)
                .build();
        String code = """
                SplitPaneAnt.create()
                        .direction(HORIZONTAL)
                        .items(leftPanel, rightPanel)
                        .dividerPositions(0.4)     // 分隔条初始在 40%
                        .build();                    // 可拖拽调整宽度
                """;
        return Demos.sectionWithCode("5. SplitPane 拖拽分栏",
                "可拖拽分隔条调整左右（或上下）面板宽度，IDE 风格的灵感来源。", code, demo);
    }

    private Node scrollPaneSection() {
        Node demo = ScrollPaneAnt.create()
                .content(VBoxAnt.create()
                        .spacing(8)
                        .padding(12)
                        .children(chip("滚动内容 1"), chip("滚动内容 2"), chip("滚动内容 3"),
                                chip("滚动内容 4"), chip("滚动内容 5"), chip("滚动内容 6"))
                        .build())
                .build();
        String code = """
                ScrollPaneAnt.create()
                        .content(longContent)   // 内容超高时出现滚动条
                        .build();
                """;
        return Demos.sectionWithCode("6. ScrollPane 滚动视图", "内容超出可视区时可滚动的容器。", code, demo);
    }

    private Node flowTileSection() {
        Node flow = FlowPaneAnt.create()
                .children(chip("1"), chip("2"), chip("3"), chip("4"), chip("5"), chip("6"))
                .build();
        Node tile = TilePaneAnt.create()
                .children(chip("A"), chip("B"), chip("C"), chip("D"), chip("E"), chip("F"))
                .build();
        Node demo = VBoxAnt.create()
                .spacing(12)
                .children(
                        TypographyAnt.text("FlowPane · 流式换行").build(), flow,
                        TypographyAnt.text("TilePane · 等尺寸网格平铺").build(), tile)
                .build();
        String code = """
                FlowPaneAnt.create().children(...)   // 放不下自动换行，子项不等宽
                TilePaneAnt.create().children(...)   // 等宽高网格平铺
                """;
        return Demos.sectionWithCode("7. FlowPane / TilePane", "流式换行 与 等尺寸网格 平铺。", code, demo);
    }

    private Node textFlowSection() {
        // TextFlow 富文本：行内混排（普通文字 + 强调 + 链接样）
        Node demo = TextFlowAnt.create(
                TypographyAnt.text("这是一段 ").build(),
                TypographyAnt.text("富文本").type(TypographyAnt.TextColor.PRIMARY).build(),
                TypographyAnt.text(" 混排——支持同一行内不同样式（常用于段落、log、说明）。").build()
        ).build();
        String code = """
                TextFlowAnt.create(
                        TypographyAnt.text("这是一段 ").build(),
                        TypographyAnt.text("富文本").type(PRIMARY).build(),
                        TypographyAnt.text(" 混排…").build()
                ).build();   // 段落级文字排版
                """;
        return Demos.sectionWithCode("8. TextFlow 富文本排版", "同一行内多段文字不同样式 / 字体 / 颜色。", code, demo);
    }

    private Node anchorSection() {
        Node content = Demos.placeholder("内容（四边锚定，窗口缩放跟随）", Background.SUBTLE);
        Node footer = Demos.placeholder("底部状态条（bottomAnchor）", Background.LAYOUT);
        AnchorPaneAnt demo = AnchorPaneAnt.create()
                .children(content, footer)
                .anchor(content, 0.0, 0.0, 40.0, 0.0)   // 内容顶/左右贴边, 底部留 40 给状态条
                .bottomAnchor(footer, 0.0)
                .build();
        demo.setMinHeight(110);
        String code = """
                AnchorPaneAnt.create()
                        .children(content, footer)
                        .anchor(content, 0, 0, 40, 0)   // top,right,bottom,left
                        .bottomAnchor(footer, 0)
                        .build();   // 边沿/四角锚定，跟随窗口尺寸缩放
                """;
        return Demos.sectionWithCode("9. AnchorPane 锚点定位", "固定子节点到四角 / 边、填满或让出空间（复杂布局常用）。", code, demo);
    }

    private Node spaceSection() {
        // Space：统一间距排布（横向）
        Node demo = SpaceAnt.create()
                .size(12)
                .children(
                        ButtonAnt.create("按钮 A").build(),
                        ButtonAnt.create("按钮 B").build(),
                        ButtonAnt.create("按钮 C").build()
                )
                .build();
        String code = """
                SpaceAnt.create()
                        .size(12)                       // 统一间距
                        .children(btnA, btnB, btnC)
                        .build();                        // 无需手动给每个按钮加 margin
                """;
        return Demos.sectionWithCode("10. Space 间距排布", "Ant Design Space —— 统一、平均地给一组兄弟节点加间距。", code, demo);
    }

    private Node surfaceSection() {
        // Surface：材质卡片容器（标题 + 额外操作 + 内容）
        Node demo = SurfaceAnt.create()
                .title("Surface 材质卡片")
                .extra(ButtonAnt.create("操作").type(ButtonAnt.Type.LINK).build())
                .content(VBoxAnt.create()
                        .spacing(8)
                        .children(
                                TypographyAnt.text("带标题、头部额外操作区的内容卡片外壳。").build(),
                                TypographyAnt.text("常用于侧栏面板 / 分组设置 / 浮动内容块。").type(TypographyAnt.TextColor.SECONDARY).build())
                        .build())
                .build();
        String code = """
                SurfaceAnt.create()
                        .title("标题")
                        .extra(操作按钮)          // 头部右侧
                        .content(body)
                        .build();                  // 卡片式材质容器
                """;
        return Demos.sectionWithCode("11. Surface 材质卡片", "带标题 + 头部操作区的内容卡片，常用于面板 / 分组。", code, demo);
    }

    private Node resizableSection() {
        // ResizablePanel：可拖拽调整尺寸的面板
        Node demo = ResizablePanelAnt.create()
                .content(Demos.placeholder("拖拽右/下边缘调整大小", Background.SUBTLE))
                .mode(ResizablePanelAnt.Mode.BOTH)
                .prefWidth(320)
                .minWidth(160)
                .minHeight(80)
                .build();
        String code = """
                ResizablePanelAnt.create()
                        .content(detailView)
                        .mode(BOTH)                // 可同时拖右/下边缘
                        .prefWidth(320).minWidth(160)
                        .build();                  // 侧边详情面板 / 底部控制台
                """;
        return Demos.sectionWithCode("12. ResizablePanel 可调面板", "拖拽边缘调整宽度/高度，带 min/max 约束。", code, demo);
    }
}