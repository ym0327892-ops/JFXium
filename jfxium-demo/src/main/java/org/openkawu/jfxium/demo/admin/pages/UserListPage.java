package org.openkawu.jfxium.demo.admin.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AvatarAnt;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.ComboBoxAnt;
import org.openkawu.jfxium.component.FilterBarAnt;
import org.openkawu.jfxium.component.PaginationAnt;
import org.openkawu.jfxium.component.TableAnt;
import org.openkawu.jfxium.component.TagAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.core.util.Spacers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户列表页（M12.4 P2.1 主线核心页）。
 *
 * <pre>
 * ┌──────────────────────────────────────────────────────────┐
 * │ 上：FilterBar                                             │
 * │   [搜索] [角色▾] [状态▾]   spacer   [刷新] [新增 +]       │
 * ├──────────────────────────────────────────────────────────┤
 * │ 中：TableAnt（M11 高级版）                                  │
 * │   选 │ ID │ 头像 │ 用户名 │ 邮箱 │ 角色 │ 状态 │ 时间 │ 操作 │
 * ├──────────────────────────────────────────────────────────┤
 * │ 下：[共 N 条]   spacer   [Pagination]   [每页▾]            │
 * └──────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <p>本页设计为"复制粘贴模板"：</p>
 * <ul>
 *   <li>客户端搜索/过滤（实际项目可替换为服务端调用）</li>
 *   <li>所有交互都通过回调串起来：FilterBar 修改条件 → reload() → Table 重渲染 → Pagination 重算</li>
 *   <li>状态/角色用 TagAnt 着色，操作列用 actionColumn 糖</li>
 * </ul>
 */
public class UserListPage implements AdminPage {

    @Override public String key()   { return "user.list"; }
    @Override public String title() { return "用户管理"; }

    // ============================================================
    // 假数据模型
    // ============================================================
    public record User(int id, String name, String email, String role, Status status,
                       LocalDateTime createdAt) {
        public enum Status { ACTIVE, DISABLED, PENDING }
    }

    /** 全量假数据（生产中替换为服务端拉取）。 */
    private final List<User> allUsers = generateMockUsers(57);

    /** 过滤后的列表（驱动 Table 的真正数据）。 */
    private final ObservableList<User> filteredUsers = FXCollections.observableArrayList();

    /** 当前过滤条件。 */
    private String keyword = "";
    private String roleFilter = "全部";
    private User.Status statusFilter = null;  // null 表示"全部"

    /** 分页参数。 */
    private int pageSize = 10;
    private int currentPage = 0;  // 0-based

    /** UI 引用：底部"共 N 条"和分页器要在重渲染时刷新。 */
    private Label totalLabel;
    private Pagination paginationCtrl;
    private TableView<User> tableView;
    private VBox tableHost;  // 包 table 的容器，便于整表替换

    @Override
    public Node getView() {
        // 上：FilterBar
        Node top = buildFilterBar();

        // 中：TableAnt（先初始化，再放进 host）
        applyFilters();   // 初次加载
        tableView = buildTable();
        tableHost = new VBox(tableView);
        tableHost.setStyle("-fx-background-color: -color-bg-default;");

        // 下：分页 + 总条数
        Node bottom = buildBottomBar();

        // 整页 BorderPane 上中下
        BorderPane page = new BorderPane();
        BorderPane.setMargin(top, new Insets(0, 0, 16, 0));
        BorderPane.setMargin(bottom, new Insets(16, 0, 0, 0));
        page.setTop(top);
        page.setCenter(wrapInCard(tableHost));
        page.setBottom(bottom);
        page.setPadding(new Insets(24));
        page.setStyle("-fx-background-color: -color-bg-layout;");
        return page;
    }

    // ============================================================
    // 顶部 FilterBar
    // ============================================================
    private Node buildFilterBar() {
        // 角色筛选
        ComboBox<String> roleCombo = ComboBoxAnt.<String>create()
                .items("全部", "管理员", "编辑", "访客")
                .value("全部")
                .onChange(v -> {
                    roleFilter = v;
                    currentPage = 0;
                    reload();
                })
                .build();
        roleCombo.setPrefWidth(120);

        // 状态筛选
        ComboBox<String> statusCombo = ComboBoxAnt.<String>create()
                .items("全部", "启用", "禁用", "待审核")
                .value("全部")
                .onChange(v -> {
                    statusFilter = switch (v) {
                        case "启用"   -> User.Status.ACTIVE;
                        case "禁用"   -> User.Status.DISABLED;
                        case "待审核" -> User.Status.PENDING;
                        default       -> null;
                    };
                    currentPage = 0;
                    reload();
                })
                .build();
        statusCombo.setPrefWidth(120);

        return FilterBarAnt.create()
                .search("搜索用户名/邮箱", 240, kw -> {
                    keyword = (kw == null) ? "" : kw.trim();
                    currentPage = 0;
                    reload();
                })
                .filter("角色", roleCombo)
                .filter("状态", statusCombo)
                .action("刷新", this::reload)
                .actionPrimary("新增用户", "+", () -> System.out.println("[Demo] 新增用户（待实现）"))
                .build();
    }

    // ============================================================
    // 中间 TableAnt（M11 高级版）
    // ============================================================
    private TableView<User> buildTable() {
        return TableAnt.<User>create()
                .column("ID", u -> String.valueOf(u.id()))
                    .width(60).align(TableAnt.Align.RIGHT).end()

                .nodeColumn("头像", u -> AvatarAnt.create(initials(u.name()))
                        .size(AvatarAnt.Size.SMALL)
                        .build())
                    .width(70).align(TableAnt.Align.CENTER).end()

                .column("用户名", User::name)
                    .width(120).end()

                .column("邮箱", User::email)
                    .minWidth(200).end()

                .nodeColumn("角色", u -> roleTag(u.role()))
                    .width(90).align(TableAnt.Align.CENTER).end()

                .nodeColumn("状态", u -> statusTag(u.status()))
                    .width(90).align(TableAnt.Align.CENTER).end()

                .column("创建时间", u -> u.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .width(150)
                    .headerAlign(TableAnt.Align.CENTER)     // 表头居中
                    .contentAlign(TableAnt.Align.RIGHT)     // 时间右对齐（数据列习惯）
                    .end()

                .actionColumn("操作")
                    .action("编辑", u -> System.out.println("[Demo] 编辑：" + u.name()))
                    .action("详情", u -> System.out.println("[Demo] 详情：" + u.name()))
                    .action("删除", u -> System.out.println("[Demo] 删除：" + u.name()))
                        .danger()
                    .width(180)
                    .end()

                .data(filteredUsers)
                .striped(true)
                .borders(TableAnt.Border.HORIZONTAL)   // 仅横线（admin 默认风格）
                .selectable(true)
                .resizePolicy(TableAnt.Resize.UNCONSTRAINED)
                .defaultSortBy("ID", TableColumn.SortType.ASCENDING)
                .build();
    }

    /** 角色标签（不同角色不同颜色）。 */
    private Node roleTag(String role) {
        TagAnt.Type type = switch (role) {
            case "管理员" -> TagAnt.Type.PRIMARY;
            case "编辑"   -> TagAnt.Type.PROCESSING;
            case "访客"   -> TagAnt.Type.DEFAULT;
            default       -> TagAnt.Type.DEFAULT;
        };
        return TagAnt.create().text(role).type(type).build();
    }

    /** 状态标签（启用/禁用/待审核 着色）。 */
    private Node statusTag(User.Status status) {
        return switch (status) {
            case ACTIVE   -> TagAnt.create().text("启用").type(TagAnt.Type.SUCCESS).build();
            case DISABLED -> TagAnt.create().text("禁用").type(TagAnt.Type.ERROR).build();
            case PENDING  -> TagAnt.create().text("待审核").type(TagAnt.Type.WARNING).build();
        };
    }

    /** 把 Table 包到 Card 里（视觉上更"独立块"，符合 admin 习惯）。 */
    private Node wrapInCard(Node inner) {
        VBox card = CardAnt.create()
                .content(inner)
                .bordered(true)
                .shadow(CardAnt.Shadow.SMALL)
                .build();
        return card;
    }

    // ============================================================
    // 底部分页 + 总条数 + 每页条数
    // ============================================================
    private Node buildBottomBar() {
        totalLabel = new Label();
        totalLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
        updateTotalLabel();

        // PaginationAnt 拿原生 Pagination
        paginationCtrl = PaginationAnt.create()
                .pageCount(totalPages())
                .currentPage(currentPage)
                .maxPageIndicatorCount(7)
                .onChange(idx -> {
                    currentPage = idx;
                    refreshPage();
                })
                .build();

        // 每页条数
        ComboBox<Integer> pageSizeCombo = ComboBoxAnt.<Integer>create()
                .items(10, 20, 50, 100)
                .value(pageSize)
                .onChange(v -> {
                    if (v != null) {
                        pageSize = v;
                        currentPage = 0;
                        reload();
                    }
                })
                .build();
        pageSizeCombo.setPrefWidth(90);

        Label perPageLabel = new Label("条/页");
        perPageLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");

        HBox right = HBoxBuilder.create()
                .spacing(8)
                .align(Pos.CENTER_RIGHT)
                .children(paginationCtrl, pageSizeCombo, perPageLabel)
                .build();

        return HBoxBuilder.create()
                .spacing(0)
                .align(Pos.CENTER_LEFT)
                .children(totalLabel, Spacers.grow(), right)
                .build();
    }

    // ============================================================
    // 数据流：搜索/过滤 → reload → 重算分页 → 刷新当前页
    // ============================================================
    /** 重新过滤并刷新页面（FilterBar/PageSize/翻页都走这里）。 */
    private void reload() {
        applyFilters();
        // 防越界：当过滤后总页数变少时把 currentPage 拉回最后一页
        int tp = totalPages();
        if (currentPage >= tp) currentPage = Math.max(0, tp - 1);

        if (paginationCtrl != null) {
            paginationCtrl.setPageCount(tp);
            paginationCtrl.setCurrentPageIndex(currentPage);
        }
        refreshPage();
        updateTotalLabel();
    }

    /** 应用搜索 + 角色 + 状态过滤，把结果存到 filteredUsers。 */
    private void applyFilters() {
        List<User> result = new ArrayList<>(allUsers.size());
        String kw = keyword.toLowerCase();
        for (User u : allUsers) {
            if (!kw.isEmpty()) {
                if (!u.name().toLowerCase().contains(kw)
                        && !u.email().toLowerCase().contains(kw)) continue;
            }
            if (!"全部".equals(roleFilter) && !roleFilter.equals(u.role())) continue;
            if (statusFilter != null && statusFilter != u.status()) continue;
            result.add(u);
        }
        filteredUsers.setAll(result);
    }

    /** 把 filteredUsers 切片成"当前页可见的那部分"，刷到 Table。 */
    private void refreshPage() {
        if (tableView == null) return;
        int from = currentPage * pageSize;
        int to = Math.min(from + pageSize, filteredUsers.size());
        if (from > to) from = to;
        tableView.setItems(FXCollections.observableArrayList(filteredUsers.subList(from, to)));
    }

    private int totalPages() {
        if (filteredUsers.isEmpty()) return 1;
        return (filteredUsers.size() + pageSize - 1) / pageSize;
    }

    private void updateTotalLabel() {
        totalLabel.setText("共 " + filteredUsers.size() + " 条");
    }

    // ============================================================
    // 工具
    // ============================================================
    private static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        return name.codePointAt(0) > 127 ? name.substring(0, 1)
                                         : name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    /** 生成 N 条假数据。 */
    private static List<User> generateMockUsers(int count) {
        String[] firstNames = {"张", "李", "王", "赵", "刘", "陈", "杨", "黄", "周", "吴",
                              "Alice", "Bob", "Charlie", "Diana", "Eve"};
        String[] lastNames = {"伟", "芳", "娜", "强", "敏", "静", "磊", "洋", "艳", "勇"};
        String[] roles = {"管理员", "编辑", "访客"};
        User.Status[] statuses = User.Status.values();

        List<User> list = new ArrayList<>(count);
        java.util.Random rnd = new java.util.Random(42);  // 固定种子，每次结果一致
        LocalDateTime base = LocalDateTime.of(2026, 5, 1, 9, 0);
        for (int i = 1; i <= count; i++) {
            String first = firstNames[rnd.nextInt(firstNames.length)];
            String name = first.codePointAt(0) > 127
                    ? first + lastNames[rnd.nextInt(lastNames.length)]
                    : first;
            String email = "user" + i + "@example.com";
            String role = roles[rnd.nextInt(roles.length)];
            User.Status status = statuses[rnd.nextInt(statuses.length)];
            LocalDateTime created = base.plusHours(rnd.nextInt(720));
            list.add(new User(i, name, email, role, status, created));
        }
        return list;
    }
}
