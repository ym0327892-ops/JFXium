package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.composite.ResizablePanelAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
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

/**
 * 布局演示 section 工厂 —— 每个布局组件独立成页时复用同一份演示代码。
 *
 * <p>每个方法返回一个 {@link Demos#sectionWithCode(String, String, String, Node...)}
 * 生成的完整 Section，页面类只需要把对应 Section 放进 PageTemplate。</p>
 */
final class LayoutSections {

    private LayoutSections() {}

    // ============================================================
    // 基础容器
    // ============================================================

    static Node vboxSection() {
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
        return Demos.sectionWithCode("VBox 纵向布局", "垂直堆叠 + 纵向间距。", code, demo);
    }

    static Node hboxSection() {
        Region spacer = new Region();
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
        return Demos.sectionWithCode("HBox 横向布局", "水平排列 + Hgrow 弹性填充（工具栏经典写法）。", code, demo);
    }

    static Node borderPaneSection() {
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
        return Demos.sectionWithCode("BorderPane 五区骨架",
                "上/下/左/右/中五区，构成后台页面主骨架。", code, demo);
    }

    static Node stackPaneSection() {
        VBoxAnt content = VBoxAnt.create()
                .padding(24)
                .spacing(8)
                .children(
                        TypographyAnt.text("底层内容").strong().build(),
                        TypographyAnt.text("StackPane 按添加顺序叠放，后置节点在上。")
                                .type(TypographyAnt.TextColor.SECONDARY).build());

        VBoxAnt overlay = VBoxAnt.create()
                .padding(24)
                .align(Pos.CENTER)
                .children(TypographyAnt.text("遮罩层（点按钮关闭）").build());
        overlay.getStyleClass().add("jfx-demo-stack-overlay");
        overlay.setVisible(false);
        overlay.setManaged(false);

        StackPaneAnt stack = StackPaneAnt.create().children(content, overlay);
        stack.setMinHeight(160);

        ButtonAnt toggle = ButtonAnt.create("显示遮罩")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    boolean show = !overlay.isVisible();
                    overlay.setVisible(show);
                    overlay.setManaged(show);
                    ((ButtonAnt) e.getSource()).setText(show ? "隐藏遮罩" : "显示遮罩");
                })
                .build();

        Node demo = VBoxAnt.create().spacing(12).children(toggle, stack);
        String code = """
                StackPaneAnt stack = StackPaneAnt.create()
                        .children(content, overlay);   // 后置节点叠在上层

                // 遮罩默认隐藏，按钮切换 visible/managed
                overlay.setVisible(false);
                overlay.setManaged(false);
                toggle.onClick(e -> {
                    boolean show = !overlay.isVisible();
                    overlay.setVisible(show);
                    overlay.setManaged(show);
                });
                """;
        return Demos.sectionWithCode("StackPane 叠层",
                "后置节点叠在上层；点按钮切换遮罩，演示可交互的层叠与显隐。", code, demo);
    }

    // ============================================================
    // 分栏 / 滚动
    // ============================================================

    static Node splitPaneSection() {
        Node horizontal = SplitPaneAnt.create()
                .direction(SplitPaneAnt.Direction.HORIZONTAL)
                .gripStyle(SplitPaneAnt.GripStyle.LINES)
                .items(
                        Demos.placeholder("左面板（五条杠抓手）", Background.SUBTLE),
                        Demos.placeholder("右面板", Background.SUBTLE)
                )
                .dividerPositions(0.4)
                .prefH(140)
                .build();

        Node vertical = SplitPaneAnt.create()
                .direction(SplitPaneAnt.Direction.VERTICAL)
                .gripStyle(SplitPaneAnt.GripStyle.LINES)
                .items(
                        Demos.placeholder("上方面板", Background.SUBTLE),
                        Demos.placeholder("下方面板", Background.SUBTLE)
                )
                .dividerPositions(0.4)
                .prefH(220)
                .build();

        Node demo = VBoxAnt.create().spacing(12).children(horizontal, vertical);
        String code = """
                SplitPaneAnt.create()
                        .direction(HORIZONTAL)
                        .gripStyle(SplitPaneAnt.GripStyle.LINES)  // 五条杠抓手（中间最长）
                        .items(leftPanel, rightPanel)
                        .dividerPositions(0.4)
                        .build();

                SplitPaneAnt.create()
                        .direction(VERTICAL)
                        .gripStyle(SplitPaneAnt.GripStyle.LINES)
                        .items(topPanel, bottomPanel)
                        .dividerPositions(0.4)
                        .build();
                """;
        return Demos.sectionWithCode("SplitPane 拖拽分栏",
                "可拖拽分隔条调整面板大小；GripStyle.LINES 用五条长短递减的线提示可拖动。", code, demo);
    }

    static Node scrollPaneSection() {
        ScrollPaneAnt demo = ScrollPaneAnt.create()
                .content(VBoxAnt.create()
                        .spacing(8)
                        .children(
                                chipW(60, "滚动项 1"), chipW(60, "滚动项 2"), chipW(60, "滚动项 3"),
                                chipW(60, "滚动项 4"), chipW(60, "滚动项 5"))
                        .build())
                .build();
        demo.setMaxHeight(170);
        String code = """
                ScrollPaneAnt.create()
                        .content(tallContent)   // 内容总高 > maxHeight 时出现滚动条
                        .maxHeight(170)          // 关键：钳制可视高度才可滚动
                        .build();
                """;
        return Demos.sectionWithCode("ScrollPane 滚动视图",
                "钳制高度后内容溢出即出现滚动条，可上下滚动。", code, demo);
    }

    // ============================================================
    // 流式 / 平铺 / 文本
    // ============================================================

    static Node flowPaneSection() {
        FlowPaneAnt flow = FlowPaneAnt.create()
                .children(
                        chipW(120, "宽 120"), chipW(80, "宽 80"), chipW(100, "宽 100"),
                        chipW(60, "宽 60"), chipW(140, "宽 140"), chipW(90, "宽 90"),
                        chipW(110, "宽 110"), chipW(70, "宽 70"))
                .build();
        flow.setMaxWidth(300);
        String code = """
                FlowPaneAnt.create()
                        .children(不等宽节点们)
                        .maxWidth(300)        // 钳制宽度 → 超宽自动换行
                        .build();
                """;
        return Demos.sectionWithCode("FlowPane 流式换行",
                "子项按自身宽度水平排列，放不下自动换行。", code, flow);
    }

    static Node tilePaneSection() {
        TilePaneAnt tile = TilePaneAnt.create()
                .prefColumns(4)
                .children(
                        chipW(70, "A"), chipW(70, "B"), chipW(70, "C"), chipW(70, "D"),
                        chipW(70, "E"), chipW(70, "F"), chipW(70, "G"), chipW(70, "H"))
                .build();
        tile.setMaxWidth(320);
        String code = """
                TilePaneAnt.create()
                        .prefColumns(4)         // 每行 4 列
                        .children(等宽单元们)
                        .build();               // 铺满一行自动换下一行
                """;
        return Demos.sectionWithCode("TilePane 网格平铺",
                "等尺寸单元按固定列数平铺，适合缩略图 / 卡牌网格。", code, tile);
    }

    static Node textFlowSection() {
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
        return Demos.sectionWithCode("TextFlow 富文本排版",
                "同一行内多段文字不同样式 / 字体 / 颜色。", code, demo);
    }

    // ============================================================
    // 锚点 / 间距 / 面板
    // ============================================================

    static Node anchorSection() {
        Node content = Demos.placeholder("内容（四边锚定，窗口缩放跟随）", Background.SUBTLE);
        Node footer = Demos.placeholder("底部状态条（bottomAnchor）", Background.LAYOUT);
        AnchorPaneAnt demo = AnchorPaneAnt.create()
                .children(content, footer)
                .anchor(content, 0.0, 0.0, 40.0, 0.0)
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
        return Demos.sectionWithCode("AnchorPane 锚点定位",
                "固定子节点到四角 / 边、填满或让出空间（复杂布局常用）。", code, demo);
    }

    static Node spaceSection() {
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
        return Demos.sectionWithCode("Space 间距排布",
                "Ant Design Space —— 统一、平均地给一组兄弟节点加间距。", code, demo);
    }

    static Node surfaceSection() {
        Node demo = SurfaceAnt.create()
                .title("Surface 材质卡片")
                .extra(ButtonAnt.create("操作").type(ButtonAnt.Type.LINK).build())
                .content(VBoxAnt.create()
                        .spacing(8)
                        .children(
                                TypographyAnt.text("带标题、头部额外操作区的内容卡片外壳。").build(),
                                TypographyAnt.text("常用于侧栏面板 / 分组设置 / 浮动内容块。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                        .build())
                .build();
        String code = """
                SurfaceAnt.create()
                        .title("标题")
                        .extra(操作按钮)          // 头部右侧
                        .content(body)
                        .build();                  // 卡片式材质容器
                """;
        return Demos.sectionWithCode("Surface 材质卡片",
                "带标题 + 头部操作区的内容卡片，常用于面板 / 分组。", code, demo);
    }

    static Node resizableSection() {
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
        return Demos.sectionWithCode("ResizablePanel 可调面板",
                "拖拽边缘调整宽度/高度，带 min/max 约束。", code, demo);
    }

    // ============================================================
    // helpers
    // ============================================================

    private static Node chip(String text) {
        return Demos.placeholder(text, Background.SUBTLE);
    }

    private static Node chipW(double width, String text) {
        Region n = (Region) Demos.placeholder(text, Background.SUBTLE);
        n.setPrefWidth(width);
        return n;
    }
}
