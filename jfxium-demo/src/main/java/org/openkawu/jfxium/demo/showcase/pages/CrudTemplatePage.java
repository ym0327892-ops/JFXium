package org.openkawu.jfxium.demo.showcase.pages;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.ComboBoxAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.component.PaginationAnt;
import org.openkawu.jfxium.component.StatisticAnt;
import org.openkawu.jfxium.component.TableAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;
import org.openkawu.jfxium.template.CrudTemplate;

import java.util.List;

/**
 * CrudTemplate 展示页（M18）。
 *
 * <p>展示 4 种 body 类型：</p>
 * <ul>
 *   <li>列表页（TableView）—— 最经典 CRUD 场景</li>
 *   <li>表单页 —— 取消/提交按钮放底部右侧</li>
 *   <li>仪表盘 —— 统计卡矩阵 + 顶部刷新/导出</li>
 *   <li>极简（仅 body）—— title + 内容</li>
 * </ul>
 */
public class CrudTemplatePage implements ShowcasePage {

    @Override public String   key()      { return "crud-template"; }
    @Override public String   title()    { return "CrudTemplate 业务模板"; }
    @Override public Category category() { return Category.TEMPLATE; }

    public record User(int id, String name, String email, String role) {}

    @Override
    public Node getView() {
        Label pageTitle = new Label("CrudTemplate 业务模板");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("admin 后台 90% 业务页的通用三段式骨架。body 装什么都行——表格/表单/详情/图表。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionListPage(),
                        sectionFormPage(),
                        sectionDashboardPage(),
                        sectionMinimal()
                )
                .build();
    }

    // ============================================================
    // 1. 经典列表页：FilterBar + TableView + Pagination
    // ============================================================
    private Node sectionListPage() {
        // 假数据
        List<User> users = List.of(
                new User(1, "张三", "zhangsan@example.com", "管理员"),
                new User(2, "李四", "lisi@example.com", "编辑"),
                new User(3, "王五", "wangwu@example.com", "访客"),
                new User(4, "赵六", "zhaoliu@example.com", "管理员"),
                new User(5, "钱七", "qianqi@example.com", "编辑")
        );

        // 表格
        TableView<User> table = TableAnt.<User>create()
                .column("ID", u -> String.valueOf(u.id())).width(60).align(TableAnt.Align.RIGHT).end()
                .column("姓名", User::name).width(120).end()
                .column("邮箱", User::email).minWidth(200).end()
                .column("角色", User::role).width(100).end()
                .actionColumn("操作")
                    .action("编辑", u -> System.out.println("编辑 " + u.name()))
                    .action("删除", u -> System.out.println("删除 " + u.name())).danger()
                    .end()
                .data(users)
                .build();
        table.setPrefHeight(280);

        // FilterBar 部件
        TextField search = InputAnt.create().placeholder("搜索用户名/邮箱").build();
        search.setPrefWidth(200);
        ComboBox<String> roleCombo = ComboBoxAnt.<String>create().items("全部", "管理员", "编辑", "访客").value("全部").build();
        roleCombo.setPrefWidth(120);
        ComboBox<String> statusCombo = ComboBoxAnt.<String>create().items("全部", "启用", "禁用").value("全部").build();
        statusCombo.setPrefWidth(100);

        // Bottombar 部件
        Label totalLabel = new Label("共 " + users.size() + " 条");
        totalLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
        Pagination pagination = PaginationAnt.create().pageCount(6).currentPage(0).maxPageIndicatorCount(7).build();
        ComboBox<Integer> pageSizeCombo = ComboBoxAnt.<Integer>create().items(10, 20, 50, 100).value(10).build();
        pageSizeCombo.setPrefWidth(80);
        Label perPage = new Label("条/页");
        perPage.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");

        BorderPane page = CrudTemplate.create()
                .title("用户管理")
                .topLeft(search, roleCombo, statusCombo)
                .topRight(
                        ButtonAnt.create("刷新").build(),
                        ButtonAnt.create("新增用户").type(ButtonAnt.Type.PRIMARY)
                                .icon(IconAnt.path(IconAnt.Path.PLUS, 14)).build()
                )
                .body(table)
                .bottomLeft(totalLabel)
                .bottomRight(pagination, pageSizeCombo, perPage)
                .bordered(true)
                .build();

        return ShowcaseSection.create()
                .title("场景 1：列表页（最经典 CRUD）")
                .description("topLeft：搜索/筛选 / topRight：新增/刷新 / body：TableView / bottomLeft：总数 / bottomRight：分页")
                .demo(page)
                .code("""
                        TableView<User> table = TableAnt.<User>create()...build();
                        Pagination pagination = PaginationAnt.create()...build();

                        BorderPane page = CrudTemplate.create()
                            .title("用户管理")
                            .topLeft(search, roleCombo, statusCombo)
                            .topRight(refreshBtn, addBtn)
                            .body(table)
                            .bottomLeft(totalLabel)
                            .bottomRight(pagination, pageSizeCombo)
                            .bordered(true)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 表单页：表单 body + 底部右侧 取消/提交
    // ============================================================
    private Node sectionFormPage() {
        VBox form = VBoxBuilder.create()
                .spacing(12)
                .children(
                        labelInput("用户名", "请输入用户名"),
                        labelInput("邮箱", "name@example.com"),
                        labelInput("电话", "可选"),
                        labelInput("备注", "可选")
                )
                .build();
        form.setMaxWidth(420);

        BorderPane page = CrudTemplate.create()
                .title("新增用户")
                .body(form)
                .bottomRight(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY).build()
                )
                .bordered(true)
                .build();

        return ShowcaseSection.create()
                .title("场景 2：表单页")
                .description("没有 topbar / bottomLeft，仅 body（表单）+ bottomRight（取消/提交）")
                .demo(page)
                .code("""
                        VBox form = ...;       // 用 VBox 拼或 FormAnt

                        BorderPane page = CrudTemplate.create()
                            .title("新增用户")
                            .body(form)
                            .bottomRight(cancelBtn, submitBtn)
                            .bordered(true)
                            .build();
                        """)
                .build();
    }

    private VBox labelInput(String label, String placeholder) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: -color-fg-default;");
        TextField t = InputAnt.create().placeholder(placeholder).build();
        return VBoxBuilder.create().spacing(4).children(l, t).build();
    }

    // ============================================================
    // 3. 仪表盘：4 列统计卡 + 顶部刷新/导出
    // ============================================================
    private Node sectionDashboardPage() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        for (int i = 0; i < 4; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        grid.add(statCard("总用户", "1,234", "↑ 12%"), 0, 0);
        grid.add(statCard("今日订单", "89", "↓ 3%"), 1, 0);
        grid.add(statCard("月销售额", "¥125k", "↑ 8%"), 2, 0);
        grid.add(statCard("转化率", "23.4%", "↑ 1.2%"), 3, 0);

        BorderPane page = CrudTemplate.create()
                .title("数据概览")
                .topRight(
                        ButtonAnt.create("刷新").build(),
                        ButtonAnt.create("导出报表").type(ButtonAnt.Type.PRIMARY).build()
                )
                .body(grid)
                .bordered(true)
                .build();

        return ShowcaseSection.create()
                .title("场景 3：仪表盘（Dashboard）")
                .description("title + topRight 操作 + body 是 GridPane 统计卡矩阵；不需要 bottombar")
                .demo(page)
                .code("""
                        GridPane statsGrid = ...;     // 4 列统计卡

                        BorderPane page = CrudTemplate.create()
                            .title("数据概览")
                            .topRight(refreshBtn, exportBtn)
                            .body(statsGrid)
                            .bordered(true)
                            .build();
                        """)
                .build();
    }

    private VBox statCard(String title, String value, String trend) {
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");
        Label tr = new Label(trend);
        tr.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-success-emphasis;");
        return CardAnt.create()
                .content(VBoxBuilder.create().spacing(6).children(t, v, tr).build())
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
    }

    // ============================================================
    // 4. 极简：仅 title + body
    // ============================================================
    private Node sectionMinimal() {
        Label content = new Label("这里是任意 body 内容...");
        content.setStyle("-fx-text-fill: -color-fg-muted; -fx-padding: 24;");

        BorderPane page = CrudTemplate.create()
                .title("极简模板")
                .body(content)
                .bordered(true)
                .build();

        return ShowcaseSection.create()
                .title("场景 4：极简（仅 title + body）")
                .description("topbar/bottombar 全部为空时不渲染——CrudTemplate 自动适配，可作为通用容器使用")
                .demo(page)
                .code("""
                        BorderPane page = CrudTemplate.create()
                            .title("极简模板")
                            .body(anyContent)
                            .bordered(true)
                            .build();
                        """)
                .build();
    }
}
