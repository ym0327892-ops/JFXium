package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.composite.HBarAnt;

/**
 * HBar 横向条状容器 —— 两段 / 三段 / 间距 / 分割线 / 高度 / 对齐 / 多节点。
 *
 * <p>HBarAnt 是项目最底层的「横向条状容器」
 *（左 + 弹性 spacer + 右，可选 center），Card/Modal/Drawer 的 header/footer 全部基于它。
 * 模板层负责默认高度与 padding，栏内操作控件一般再比容器基准小一档。</p>
 */
public class HBarExamplePage extends VBoxAnt {

    public HBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("HBar 横向条状容器")
                .description("左-中-右三段式布局原子，语义上对应 HBarAnt。HBar 本体不钳固定高度，尺寸由内容 / padding / minH 决定。模板层定高度，栏内按钮通常比容器再小一档。")
                .sections(
                        twoSegmentSection(),
                        threeSegmentSection(),
                        multiNodeSection(),
                        gapSection(),
                        borderSection(),
                        paddingSection(),
                        sizingSection(),
                        alignmentSection(),
                        realWorldSection(),
                        edgeCaseSection(),
                        styleCustomSection(),
                        universalBorderSection()
                )
                .padding(24)
                .build());
    }

    // ============================================================
    // 1. 两段式（左 + 右）—— 不设 center，自动退化
    // ============================================================
    private Node twoSegmentSection() {
        Node demo = HBarAnt.create()
                .left(new Label("左侧标题"))
                .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        String code = """
                HBarAnt.create()
                        .left(new Label("左侧标题"))
                        .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                        .build();
                """;
        return Demos.sectionWithCode("1. 两段式（左+右）",
                "只设 left + right，中间由弹性 spacer 自动撑开。不调 center() 即自动退化。",
                code, demo);
    }

    // ============================================================
    // 2. 三段式（左 + 中 + 右）—— center 真正居中
    // ============================================================
    private Node threeSegmentSection() {
        Node demo = HBarAnt.create()
                .left(new Label("品牌"))
                .center(new Label("居中内容"))
                .right(ButtonAnt.create("设置").build())
                .build();
        String code = """
                HBarAnt.create()
                        .left(new Label("品牌"))
                        .center(new Label("居中内容"))
                        .right(ButtonAnt.create("设置").build())
                        .build();
                """;
        return Demos.sectionWithCode("2. 三段式（左+中+右）",
                "left + center + right 完整三段。左右 spacer 宽度对称，center 真正居中。",
                code, demo);
    }

    // ============================================================
    // 3. 多节点 per 段 —— 每段允许多个 Node 水平排列
    // ============================================================
    private Node multiNodeSection() {
        Node demo = HBarAnt.create()
                .left(
                        IconAnt.path(IconAnt.Path.USERS, 16),
                        TypographyAnt.text("用户管理").build()
                )
                .right(
                        ButtonAnt.create("导出").build(),
                        ButtonAnt.create("新增").type(ButtonAnt.Type.PRIMARY).build()
                )
                .gap(12)
                .build();
        String code = """
                HBarAnt.create()
                        .left(
                                IconAnt.path(IconAnt.Path.USERS, 16),
                                TypographyAnt.text("用户管理").build()
                        )
                        .right(
                                ButtonAnt.create("导出").build(),
                                ButtonAnt.create("新增").type(ButtonAnt.Type.PRIMARY).build()
                        )
                        .build();
                """;
        return Demos.sectionWithCode("3. 每段多节点",
                "left/center/right 均接受可变参数，按添加顺序水平排列。典型场景：工具栏左侧图标+文字、右侧多按钮。",
                code, demo);
    }

    // ============================================================
    // 4. 自定义间距 gap
    // ============================================================
    private Node gapSection() {
        Node demo1 = HBarAnt.create()
                .left(new Label("默认 gap=8"))
                .right(ButtonAnt.create("A").build(), ButtonAnt.create("B").build())
                .build();
        Node demo2 = HBarAnt.create()
                .left(new Label("gap=24"))
                .right(ButtonAnt.create("A").build(), ButtonAnt.create("B").build())
                .gap(24)
                .build();
        Node demo3 = HBarAnt.create()
                .left(new Label("gap=2（紧凑）"))
                .right(ButtonAnt.create("A").build(), ButtonAnt.create("B").build())
                .gap(2)
                .build();
        String code = """
                HBarAnt.create()
                        .left(new Label("gap=24"))
                        .right(btn1, btn2)
                        .gap(24)    // 控制各段之间的水平间距（默认 8）
                        .build();
                """;
        return Demos.sectionWithCode("4. 自定义间距 gap",
                "gap() 控制 HBox 内子节点间距。默认 8，可调为任意值。",
                code, Demos.column(demo1, demo2, demo3));
    }

    // ============================================================
    // 5. 上下分割线 borderBottom / borderTop
    // ============================================================
    private Node borderSection() {
        // 5a. 底部分割线（Card header 风格）
        Node bottomBar = HBarAnt.create()
                .left(new Label("Card 标题栏"))
                .right(ButtonAnt.create("更多").build())
                .padding(8, 12, 8, 12)
                .borderBottom()
                .build();
        // 5b. 顶部分割线（Dialog footer 风格）
        Node topBar = HBarAnt.create()
                .left(new Label("已选 3 项"))
                .right(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("确认").type(ButtonAnt.Type.PRIMARY).build()
                )
                .padding(12, 16, 12, 16)
                .borderTop()
                .build();
        // 5c. 上下双线（表格工具栏风格）
        Node bothBar = HBarAnt.create()
                .left(new Label("数据列表"))
                .right(ButtonAnt.create("刷新").build())
                .padding(10, 16, 10, 16)
                .borderTop()
                .borderBottom()
                .build();

        Node row = Demos.column(
                Demos.labeled("borderBottom", bottomBar),
                Demos.labeled("borderTop", topBar),
                Demos.labeled("borderTop + borderBottom", bothBar)
        );
        String code = """
                // Card header：底部分割线
                HBarAnt.create()
                        .left(new Label("Card 标题栏"))
                        .right(ButtonAnt.create("更多").build())
                        .padding(8, 12, 8, 12)
                        .borderBottom()
                        .build();

                // Dialog footer：顶部分割线
                HBarAnt.create()
                        .left(new Label("已选 3 项"))
                        .right(cancelBtn, confirmBtn)
                        .padding(12, 16, 12, 16)
                        .borderTop()
                        .build();

                // 上下双线
                HBarAnt.create()
                        .left(new Label("数据列表"))
                        .right(refreshBtn)
                        .borderTop().borderBottom()
                        .build();
                """;
        return Demos.sectionWithCode("5. 上下分割线",
                "borderBottom() 加底部 1px 分隔线，borderTop() 加顶部。可同时启用。颜色跟随主题 -color-border-muted。",
                code, row);
    }

    // ============================================================
    // 6. padding 控制高度/边距
    // ============================================================
    private Node paddingSection() {
        Node demo1 = HBarAnt.create()
                .left(new Label("padding(4)"))
                .right(ButtonAnt.create("按钮").build())
                .padding(4)
                .borderBottom()
                .build();
        Node demo2 = HBarAnt.create()
                .left(new Label("padding(8,16,8,16)"))
                .right(ButtonAnt.create("按钮").build())
                .padding(8, 16, 8, 16)
                .borderBottom()
                .build();
        Node demo3 = HBarAnt.create()
                .left(new Label("padding(16,24,16,24)"))
                .right(ButtonAnt.create("按钮").build())
                .padding(16, 24, 16, 24)
                .borderBottom()
                .build();

        Node row = Demos.column(demo1, demo2, demo3);
        String code = """
                // 四边相同
                HBarAnt.create().padding(8).build();

                // 四边独立：上 右 下 左（PC 思维：高度由上下 padding 自控）
                HBarAnt.create()
                        .padding(8, 16, 8, 16)   // top=8, right=16, bottom=8, left=16
                        .build();
                """;
        return Demos.sectionWithCode("6. padding 边距控制",
                "padding(double) 四边相同；padding(t,r,b,l) 四边独立。高度由 top/bottom padding 自控（PC UI 标准 §B.3）。",
                code, row);
    }

    // ============================================================
    // 7. 高度控制 minHeight / prefHeight / maxWidth
    // ============================================================
    private Node sizingSection() {
        Node demo1 = HBarAnt.create()
                .left(new Label("minHeight=48"))
                .right(ButtonAnt.create("按钮").build())
                .minH(48)
                .borderBottom()
                .build();
        Node demo2 = HBarAnt.create()
                .left(new Label("prefHeight=64 + padding"))
                .right(ButtonAnt.create("按钮").build())
                .prefH(64)
                .padding(12, 16, 12, 16)
                .borderBottom()
                .build();
        Node demo3 = HBarAnt.create()
                .left(new Label("maxWidth=400"))
                .right(ButtonAnt.create("按钮").build())
                .maxW(400)
                .borderBottom()
                .build();

        Node row = Demos.column(
                Demos.labeled("minHeight", demo1),
                Demos.labeled("prefHeight", demo2),
                Demos.labeled("maxWidth", demo3)
        );
        String code = """
                HBarAnt.create()
                        .left(new Label("固定高度"))
                        .right(btn)
                        .minH(48)          // 最小高度
                        .prefH(64)         // 首选高度
                        .maxW(400)          // 最大宽度
                        .build();
                """;
        return Demos.sectionWithCode("7. 尺寸控制",
                "HBarAnt 语义的条状容器没有默认固定高度；minHeight/prefHeight 只是显式约束。配合 padding 可实现精确尺寸。",
                code, row);
    }

    // ============================================================
    // 8. 对齐方式 alignment
    // ============================================================
    private Node alignmentSection() {
        Node demo1 = HBarAnt.create()
                .left(new Label("CENTER_LEFT（默认）"))
                .right(ButtonAnt.create("操作").build())
                .minH(48)
                .borderBottom()
                .build();
        Node demo2 = HBarAnt.create()
                .left(new Label("CENTER"))
                .right(ButtonAnt.create("操作").build())
                .alignment(Pos.CENTER)
                .minH(48)
                .borderBottom()
                .build();
        Node demo3 = HBarAnt.create()
                .left(new Label("BOTTOM_LEFT"))
                .right(ButtonAnt.create("操作").build())
                .alignment(Pos.BOTTOM_LEFT)
                .minH(48)
                .borderBottom()
                .build();

        Node row = Demos.column(demo1, demo2, demo3);
        String code = """
                HBarAnt.create()
                        .left(new Label("居中对齐"))
                        .right(btn)
                        .alignment(Pos.CENTER)      // 垂直居中（默认 CENTER_LEFT）
                        .minH(48)
                        .build();
                """;
        return Demos.sectionWithCode("8. 对齐方式 alignment",
                "alignment() 控制 HBox 纵向对齐。默认 CENTER_LEFT（垂直居中+水平左对齐），通常无需修改。",
                code, row);
    }

    // ============================================================
    // 9. 真实场景组合 —— 多属性混合，模拟实际 UI 模式
    // ============================================================
    private Node realWorldSection() {
        // 9a. 表格工具栏：左搜索+筛选，右操作按钮，底部1px线
        Node tableToolbar = HBarAnt.create()
                .left(
                        ButtonAnt.create("全部").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("已发布").build(),
                        ButtonAnt.create("草稿").build()
                )
                .right(
                        ButtonAnt.create("批量删除").type(ButtonAnt.Type.DANGER).build(),
                        ButtonAnt.create("新建").type(ButtonAnt.Type.PRIMARY).build()
                )
                .padding(8, 16, 8, 16)
                .borderBottom()
                .build();

        // 9b. 对话框底部：左提示文字，右取消+确认，顶部1px线
        Node dialogFooter = HBarAnt.create()
                .left(TypographyAnt.text("请确认以上信息").type(TypographyAnt.Type.SECONDARY).build())
                .right(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("确认删除").type(ButtonAnt.Type.DANGER).build()
                )
                .padding(12, 16, 12, 16)
                .gap(16)
                .borderTop()
                .build();

        // 9c. App 顶栏：Logo 左 + 搜索居中 + 用户菜单右
        Node appHeader = HBarAnt.create()
                .left(
                        IconAnt.path(IconAnt.Path.DASHBOARD, 20),
                        TypographyAnt.text("Admin").build()
                )
                .center(ButtonAnt.create("🔍  搜索…").type(ButtonAnt.Type.OUTLINED).build())
                .right(
                        IconAnt.path(IconAnt.Path.BELL, 18),
                        IconAnt.path(IconAnt.Path.USERS, 18)
                )
                .padding(0, 16, 0, 16)
                .minH(56)
                .gap(16)
                .build();

        // 9d. 卡标题栏+关闭：标题左，关闭按钮右，底部1px线
        Node cardHeader = HBarAnt.create()
                .left(TypographyAnt.title("订单详情", 5).build())
                .right(ButtonAnt.create("✕").type(ButtonAnt.Type.TEXT).build())
                .padding(12, 16, 12, 16)
                .borderBottom()
                .build();

        Node row = Demos.column(
                Demos.labeled("表格工具栏", tableToolbar),
                Demos.labeled("对话框底部", dialogFooter),
                Demos.labeled("App 顶栏（三段）", appHeader),
                Demos.labeled("卡标题栏", cardHeader)
        );
        String code = """
                // 表格工具栏：左筛选 + 右操作，底部分隔线
                HBarAnt.create()
                        .left(filterBtn1, filterBtn2)
                        .right(deleteBtn, addBtn)
                        .padding(8, 16, 8, 16)
                        .borderBottom()
                        .build();

                // 对话框底部：左提示 + 右取消/确认，顶部分隔线
                HBarAnt.create()
                        .left(hintText)
                        .right(cancelBtn, confirmBtn)
                        .padding(12, 16, 12, 16)
                        .borderTop()
                        .build();

                // App 顶栏：左 Logo + 中搜索 + 右图标，三段式
                HBarAnt.create()
                        .left(logoIcon, brandName)
                        .center(searchBtn)
                        .right(notifyIcon, userIcon)
                        .minH(56).gap(16)
                        .build();

                // 卡标题栏：左标题 + 右关闭，底部 1px 线
                HBarAnt.create()
                        .left(titleLabel)
                        .right(closeBtn)
                        .padding(12, 16, 12, 16)
                        .borderBottom()
                        .build();
                """;
        return Demos.sectionWithCode("9. 真实场景组合",
                "多属性混合使用的 4 个典型模式——表格工具栏、对话框底部、App 顶栏、卡标题栏。每个都是 padding + border + gap 的组合。",
                code, row);
    }

    // ============================================================
    // 10. 边界情况 —— 单段、极端 gap、border 参数切换
    // ============================================================
    private Node edgeCaseSection() {
        // 10a. 仅左段
        Node onlyLeft = HBarAnt.create()
                .left(new Label("仅 left，右侧 spacer 自动撑满"))
                .borderBottom()
                .padding(6, 12, 6, 12)
                .build();

        // 10b. 仅右段（右对齐）
        Node onlyRight = HBarAnt.create()
                .right(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build()
                )
                .padding(8, 16, 8, 16)
                .borderBottom()
                .build();

        // 10c. 仅中段
        Node onlyCenter = HBarAnt.create()
                .center(TypographyAnt.text("← 左右 spacer 对称，内容真正居中 →").build())
                .minH(40)
                .borderBottom()
                .build();

        // 10d. gap(0) 零间距 + 多按钮
        Node zeroGap = HBarAnt.create()
                .left(new Label("gap=0"))
                .right(
                        ButtonAnt.create("A").build(),
                        ButtonAnt.create("B").build(),
                        ButtonAnt.create("C").build()
                )
                .gap(0)
                .padding(4, 8, 4, 8)
                .borderBottom()
                .build();

        // 10e. borderBottom(false) 显式关闭（默认就是 false）
        Node noBorder = HBarAnt.create()
                .left(new Label("borderBottom(false) —— 无线"))
                .right(ButtonAnt.create("操作").build())
                .padding(4, 12, 4, 12)
                .borderBottom(false)
                .build();

        Node row = Demos.column(
                Demos.labeled("仅左段", onlyLeft),
                Demos.labeled("仅右段（右对齐按钮组）", onlyRight),
                Demos.labeled("仅中段（居中文字）", onlyCenter),
                Demos.labeled("gap(0) 零间距", zeroGap),
                Demos.labeled("borderBottom(false)", noBorder)
        );
        String code = """
                // 仅左段：spacer 撑到右侧，内容左对齐
                HBarAnt.create().left(content).build();

                // 仅右段：spacer 从左撑到右段，内容右对齐
                HBarAnt.create().right(btn1, btn2).build();

                // 仅中段：左右对称 spacer，内容真正居中
                HBarAnt.create().center(content).build();

                // gap(0)：按钮紧贴排列
                HBarAnt.create().right(btnA, btnB, btnC).gap(0).build();

                // borderBottom(false)：显式关闭底部分隔线
                HBarAnt.create().left(label).right(btn)
                        .borderBottom(false).build();
                """;
        return Demos.sectionWithCode("10. 边界情况与参数切换",
                "单段模式、gap(0) 零间距、borderBottom(false) 显式关闭——验证组件在各极端输入下的表现。",
                code, row);
    }

    // ============================================================
    // 11. styleClass 自定义 —— 继承 AbstractStyleBuilder 的能力
    // ============================================================
    private Node styleCustomSection() {
        // 11a. 通过 styleClass 加自定义背景色
        Node customBg = HBarAnt.create()
                .left(new Label("自定义 styleClass：蓝色背景"))
                .right(ButtonAnt.create("操作").build())
                .padding(8, 16, 8, 16)
                .styleClass("jfx-demo-bar-accent")
                .build();

        // 11b. 通过 style 加行内样式（仅非 CSS 变量安全值）
        Node customStyle = HBarAnt.create()
                .left(new Label("行内 style：圆角 + 背景"))
                .right(ButtonAnt.create("操作").build())
                .padding(8, 16, 8, 16)
                .style("-fx-background-radius: 6px; -fx-background-color: #f6f8fa;")
                .build();

        // 11c. 组合：styleClass + style + borderBottom
        Node combo = HBarAnt.create()
                .left(new Label("组合：圆角卡片 + 底部分隔线"))
                .right(ButtonAnt.create("更多").build())
                .padding(10, 16, 10, 16)
                .styleClass("jfx-demo-bar-card")
                .style("-fx-background-radius: 8px;")
                .borderBottom()
                .build();

        Node row = Demos.column(
                Demos.labeled("styleClass 自定义", customBg),
                Demos.labeled("style 行内样式", customStyle),
                Demos.labeled("styleClass + style + borderBottom", combo)
        );
        String code = """
                // 通过 styleClass 挂项目自定义 CSS
                HBarAnt.create()
                        .left(label).right(btn)
                        .styleClass("my-custom-bar")
                        .build();

                // 通过 style 直接写内联（仅允许纯值，禁止 CSS 变量！）
                HBarAnt.create()
                        .left(label).right(btn)
                        .style("-fx-background-radius: 6px;")
                        .build();

                // styleClass + style + borderBottom 三者叠加
                HBarAnt.create()
                        .left(label).right(btn)
                        .styleClass("my-card-bar")
                        .style("-fx-background-radius: 8px;")
                        .borderBottom()
                        .build();
                """;
        return Demos.sectionWithCode("11. styleClass / style 自定义",
                "HBarAnt 继承 AbstractStyleBuilder，支持 .styleClass() 挂自定义 CSS、.style() 写行内样式（仅允许纯值，禁止 CSS 变量！），可与 borderBottom 叠加。",
                code, row);
    }

    // ============================================================
    // 12. 通用边框 —— 所有 AbstractStyleBuilder 子类零成本获得
    // ============================================================
    private Node universalBorderSection() {
        // 12a. 左侧分割线
        Node leftBorder = HBarAnt.create()
                .left(new Label("左侧分割线 (borderLeft)"))
                .right(ButtonAnt.create("操作").build())
                .padding(8, 16, 8, 16)
                .borderLeft()
                .build();

        // 12b. 右侧分割线
        Node rightBorder = HBarAnt.create()
                .left(new Label("右侧分割线 (borderRight)"))
                .right(ButtonAnt.create("操作").build())
                .padding(8, 16, 8, 16)
                .borderRight()
                .build();

        // 12c. 左右双线 + 底部线
        Node lrbBorder = HBarAnt.create()
                .left(new Label("左 + 右 + 底线"))
                .right(ButtonAnt.create("操作").build())
                .padding(8, 16, 8, 16)
                .borderLeft()
                .borderRight()
                .borderBottom()
                .build();

        // 12d. 四边全开（卡片式）
        Node fourSides = HBarAnt.create()
                .left(new Label("四边全开（卡片风格）"))
                .right(ButtonAnt.create("操作").build())
                .padding(10, 16, 10, 16)
                .borderTop().borderBottom()
                .borderLeft().borderRight()
                .style("-fx-background-radius: 6px;")
                .build();

        Node row = Demos.column(
                Demos.labeled("borderLeft", leftBorder),
                Demos.labeled("borderRight", rightBorder),
                Demos.labeled("borderLeft + borderRight + borderBottom", lrbBorder),
                Demos.labeled("四边全开 + 圆角（卡片风格）", fourSides)
        );
        String code = """
                // 左侧分割线
                HBarAnt.create()
                        .left(label).right(btn)
                        .borderLeft()
                        .build();

                // 右侧分割线
                HBarAnt.create()
                        .left(label).right(btn)
                        .borderRight()
                        .build();

                // 四边全开 + 圆角（卡片式容器）
                HBarAnt.create()
                        .left(label).right(btn)
                        .borderTop().borderBottom()
                        .borderLeft().borderRight()
                        .style("-fx-background-radius: 6px;")
                        .build();
                """;
        return Demos.sectionWithCode("12. 通用边框（全 Builder 组件可用）",
                "borderTop/bottom/left/right 在 AbstractStyleBuilder 基类实现。所有用 Builder 模式的组件（HBarAnt、GroupBoxAnt、FlexAnt、GridAnt、FormAnt…）全部零成本继承。颜色走 -color-border-default，随主题自动切换。",
                code, row);
    }
}
