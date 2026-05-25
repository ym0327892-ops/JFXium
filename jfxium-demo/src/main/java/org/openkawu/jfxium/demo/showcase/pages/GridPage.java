package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.GridAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * GridAnt 24 列栅格 + 响应式断点（M19.21）展示页。
 *
 * <p>本页聚焦真实 admin 业务场景，每个 section 的代码都可以直接复制到项目里用。</p>
 */
public class GridPage implements ShowcasePage {

    @Override public String   key()      { return "grid"; }
    @Override public String   title()    { return "Grid 栅格"; }
    @Override public Category category() { return Category.LAYOUT; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Grid 24 列栅格");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label(
                "对标 Ant Design Grid。M19.21 加 5 个标准响应式断点（xs/sm/md/lg/xl/xxl）。" +
                "下方三个场景都是 admin 后台高频模式 —— 拖动窗口宽度看响应式效果。"
        );
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");
        pageDesc.setWrapText(true);

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionAdminForm(),
                        sectionDashboardStats(),
                        sectionContentAside(),
                        sectionAutoWrap(),
                        sectionApiReference()
                )
                .build();
    }

    // ============================================================
    // 场景 4：自动 wrap（不指定 span，按容器宽度自动每行放 N 个）
    // ============================================================
    private Node sectionAutoWrap() {
        // FlowPane：一行放不下自动换行；每个子节点 prefWidth = 240，宽度变化时自动重新 wrap
        javafx.scene.layout.FlowPane flow = new javafx.scene.layout.FlowPane();
        flow.setHgap(16);
        flow.setVgap(16);
        flow.setPrefWrapLength(0);   // 让宽度由父容器决定，自动换行
        flow.getChildren().addAll(
                fixedCard("总用户", "8,492", "↑ 12.5%", true),
                fixedCard("今日订单", "234", "↓ 3.2%", false),
                fixedCard("月销售额", "¥125k", "↑ 8.4%", true),
                fixedCard("转化率", "23.4%", "↑ 1.2%", true)
        );

        return ShowcaseSection.create()
                .title("场景 4：自动 wrap（不指定 span，按容器宽度自动排列）")
                .description("用 JavaFX FlowPane —— 卡片宽度固定，容器窄时自动换行成 1/2 列，宽时排成 1×4。" +
                        "不需要 GridAnt 的 24 列语义，更适合 \"卡片数量不定\" 的场景")
                .demo(flow)
                .code("""
                        // 不需要 GridAnt —— 用 JavaFX 原生 FlowPane 即可
                        FlowPane flow = new FlowPane();
                        flow.setHgap(16);
                        flow.setVgap(16);
                        flow.getChildren().addAll(
                            statCard("总用户", "8,492", ...),
                            statCard("今日订单", "234", ...),
                            statCard("月销售额", "¥125k", ...),
                            statCard("转化率", "23.4%", ...)
                        );
                        // 容器变窄时自动换行；卡片本身不变形
                        """)
                .build();
    }

    /** 固定宽度的统计卡（FlowPane 子节点必须有 prefWidth 才能正确 wrap）。 */
    private Node fixedCard(String title, String value, String trend, boolean up) {
        Node card = statCard(title, value, trend, up);
        if (card instanceof javafx.scene.layout.Region r) {
            r.setMinWidth(220);
            r.setPrefWidth(220);
        }
        return card;
    }

    // ============================================================
    // 场景 1：admin 表单两栏布局
    // ============================================================
    private Node sectionAdminForm() {
        // 单个表单字段 = label + input 一对（不放 GridAnt 里，否则会被栅格再切一次）
        Node row = GridAnt.create()
                .gutter(16)
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(formItem("姓名", "请输入")).xs(24).sm(12))
                        .col(GridAnt.col(formItem("邮箱", "user@example.com")).xs(24).sm(12)))
                .row(GridAnt.row()
                        .col(GridAnt.col(formItem("公司", "请输入")).xs(24).sm(12))
                        .col(GridAnt.col(formItem("职位", "请输入")).xs(24).sm(12)))
                .row(GridAnt.row()
                        .col(GridAnt.col(formItem("备注", "可选")).xs(24)))
                .build();

        return ShowcaseSection.create()
                .title("场景 1：admin 表单两栏布局（xs 单栏 / sm+ 双栏）")
                .description("最常见的后台表单 —— 窄屏单栏可读、宽屏双栏密度高")
                .demo(row)
                .code("""
                        GridAnt.create()
                            .gutter(16)
                            .responsive()
                            .row(GridAnt.row()
                                .col(GridAnt.col(nameField).xs(24).sm(12))
                                .col(GridAnt.col(emailField).xs(24).sm(12)))
                            .row(GridAnt.row()
                                .col(GridAnt.col(companyField).xs(24).sm(12))
                                .col(GridAnt.col(titleField).xs(24).sm(12)))
                            .row(GridAnt.row()
                                .col(GridAnt.col(remarkField).xs(24)))   // 备注独占一行
                            .build();
                        """)
                .build();
    }

    private Node formItem(String label, String placeholder) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
        TextField input = InputAnt.create().placeholder(placeholder).build();
        input.setMaxWidth(Double.MAX_VALUE);
        return new VBox(6, l, input);
    }

    // ============================================================
    // 场景 2：Dashboard 统计卡矩阵
    // ============================================================
    private Node sectionDashboardStats() {
        Node row = GridAnt.create()
                .gutter(16)
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(statCard("总用户", "8,492", "↑ 12.5%", true)).xs(24).sm(12).lg(6))
                        .col(GridAnt.col(statCard("今日订单", "234", "↓ 3.2%", false)).xs(24).sm(12).lg(6))
                        .col(GridAnt.col(statCard("月销售额", "¥125k", "↑ 8.4%", true)).xs(24).sm(12).lg(6))
                        .col(GridAnt.col(statCard("转化率", "23.4%", "↑ 1.2%", true)).xs(24).sm(12).lg(6)))
                .build();

        return ShowcaseSection.create()
                .title("场景 2：Dashboard 统计卡矩阵（xs 1 列 / sm 2 列 / lg 4 列）")
                .description("admin 首页通用模式 —— 4 张数字卡随窗口宽度自适应")
                .demo(row)
                .code("""
                        GridAnt.create()
                            .gutter(16)
                            .responsive()
                            .row(GridAnt.row()
                                .col(GridAnt.col(card1).xs(24).sm(12).lg(6))
                                .col(GridAnt.col(card2).xs(24).sm(12).lg(6))
                                .col(GridAnt.col(card3).xs(24).sm(12).lg(6))
                                .col(GridAnt.col(card4).xs(24).sm(12).lg(6)))
                            .build();
                        """)
                .build();
    }

    private Node statCard(String title, String value, String trend, boolean up) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-muted;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");
        Label tr = new Label(trend);
        tr.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                (up ? "-color-success-emphasis" : "-color-danger-emphasis") + ";");
        VBox body = new VBox(6, t, v, tr);
        body.setStyle("-fx-padding: 16;");
        Node card = CardAnt.create().bordered(true).content(body).build();
        return card;
    }

    // ============================================================
    // 场景 3：内容 + 辅助侧栏
    // ============================================================
    private Node sectionContentAside() {
        Node main = mainContentBlock("正文内容（16 列）",
                "详情页 / 文章页 / 工单详情常用结构。窄屏时辅助信息会自动下沉到正文下方。");
        Node aside = asideBlock("辅助信息（8 列）",
                "可放：操作按钮、元信息、关联实体、状态标签等。");

        Node row = GridAnt.create()
                .gutter(16)
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(main).xs(24).md(16))
                        .col(GridAnt.col(aside).xs(24).md(8)))
                .build();

        return ShowcaseSection.create()
                .title("场景 3：内容 + 辅助侧栏（xs 单列 / md+ 16+8 双列）")
                .description("详情页 / 文章页骨架 —— 窄屏时辅助信息下沉，宽屏时右侧固定")
                .demo(row)
                .code("""
                        GridAnt.create()
                            .gutter(16)
                            .responsive()
                            .row(GridAnt.row()
                                .col(GridAnt.col(mainContent).xs(24).md(16))   // 主区
                                .col(GridAnt.col(asidePanel).xs(24).md(8)))    // 辅助
                            .build();
                        """)
                .build();
    }

    private Node mainContentBlock(String title, String desc) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");
        Label d = new Label(desc);
        d.setStyle("-fx-text-fill: -color-fg-muted;");
        d.setWrapText(true);
        VBox body = new VBox(8, t, d);
        body.setStyle("-fx-padding: 16; -fx-min-height: 120;");
        return CardAnt.create().bordered(true).content(body).build();
    }

    private Node asideBlock(String title, String desc) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");
        Label d = new Label(desc);
        d.setStyle("-fx-text-fill: -color-fg-muted;");
        d.setWrapText(true);
        VBox body = new VBox(8, t, d);
        body.setStyle("-fx-padding: 16; -fx-min-height: 120;" +
                "-fx-background-color: -color-bg-subtle;");
        return CardAnt.create().bordered(true).content(body).build();
    }

    // ============================================================
    // API 速查
    // ============================================================
    private Node sectionApiReference() {
        StringBuilder code = new StringBuilder();
        code.append("// 6 个标准断点（对齐 Ant Design / Bootstrap）\n");
        code.append("xs   < 576px      // 极窄（手机竖屏）\n");
        code.append("sm   ≥ 576px      // 窄（手机横屏）\n");
        code.append("md   ≥ 768px      // 平板\n");
        code.append("lg   ≥ 992px      // 桌面\n");
        code.append("xl   ≥ 1200px     // 大桌面\n");
        code.append("xxl  ≥ 1600px     // 超大桌面\n\n");
        code.append("// 回退规则：未设的断点向下查找最近有效值\n");
        code.append("// .xs(24).md(8)  →  sm 也是 24（继承 xs）；lg/xl/xxl 都是 8（继承 md）\n\n");
        code.append("// 老 API 完全兼容\n");
        code.append("GridAnt.row().col(12, node);             // 固定 span\n");
        code.append("GridAnt.row().col(8, 4, node);           // span=8, offset=4\n\n");
        code.append("// 不调 .responsive() 时所有断点退化为默认 span\n");
        code.append("// 调 .responsive() 才启用 Scene 宽度监听 + 跨断点重排");

        return ShowcaseSection.create()
                .title("API 速查")
                .description("断点定义 + 回退规则")
                .demo(new Label(""))
                .code(code.toString())
                .build();
    }
}
