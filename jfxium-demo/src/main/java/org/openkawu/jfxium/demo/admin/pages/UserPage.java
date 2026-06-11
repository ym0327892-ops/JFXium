package org.openkawu.jfxium.demo.admin.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.*;
import org.openkawu.jfxium.component.composite.*;
import org.openkawu.jfxium.component.control.*;
import org.openkawu.jfxium.component.overlay.ModalAnt;
import org.openkawu.jfxium.component.overlay.PopconfirmAnt;
import org.openkawu.jfxium.core.css.Background;

/**
 * UserPage —— 用户管理 CRUD，集成：
 * TableAnt + TagAnt + BadgeAnt + EmptyAnt + PopconfirmAnt + ModalAnt +
 * InputAnt + ComboBoxAnt + SwitchAnt + UploadAnt + RateAnt + SplitButtonAnt。
 */
public class UserPage extends StackPane {

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private TableView<User> table;
    private final StackPane tableArea = new StackPane();
    private final StackPane emptyArea = new StackPane();

    public record User(String name, String email, String role, String status, String department, int rating) {}

    private static final ObservableList<String> ROLE_OPTIONS =
            FXCollections.observableArrayList("管理员", "编辑", "观察者");
    private static final ObservableList<String> DEPT_OPTIONS =
            FXCollections.observableArrayList("技术部", "运营部", "产品部", "设计部", "市场部");
    private static final ObservableList<String> STATUS_OPTIONS =
            FXCollections.observableArrayList("全部", "启用", "禁用");

    @SuppressWarnings({"unchecked", "rawtypes"})
    public UserPage() {
        setPadding(new Insets(24));

        users.addAll(
                new User("张三", "zhangsan@example.com", "管理员", "启用", "技术部", 5),
                new User("李四", "lisi@example.com", "编辑", "启用", "运营部", 4),
                new User("王五", "wangwu@example.com", "观察者", "禁用", "产品部", 3)
        );

        // Empty state
        emptyArea.getChildren().add(EmptyAnt.create()
                .description("暂无用户数据，点击 [新增] 添加")
                .build());
        emptyArea.getStyleClass().add(Background.DEFAULT.styleClass());

        // Table
        table = buildTable();

        // Toolbar
        HBox toolbar = buildToolbar();

        tableArea.getChildren().add(table);
        tableArea.getStyleClass().add(Background.DEFAULT.styleClass());

        VBox root = new VBox(12, toolbar, tableArea);
        updateEmptyState();
        getChildren().add(root);
    }

    private HBox buildToolbar() {
        InputAnt search = InputAnt.create().placeholder("搜索用户...");
        search.setPrefWidth(200);

        ComboBoxAnt<String> statusFilter = ComboBoxAnt.create();
        statusFilter.getItems().addAll(STATUS_OPTIONS);
        statusFilter.setValue("全部");

        SplitMenuButton exportBtn = SplitButtonAnt.create("导出")
                .item("导出 CSV", e -> System.out.println("导出 CSV"))
                .item("导出 Excel", e -> System.out.println("导出 Excel"))
                .build();

        ButtonAnt addBtn = ButtonAnt.create("新增用户").type(ButtonAnt.Type.PRIMARY).build();
        addBtn.setOnAction(e -> showUserForm(null));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return new HBox(8.0, search, statusFilter, spacer, exportBtn, addBtn);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private TableView<User> buildTable() {
        return TableAnt.<User>create()
                .column("用户名", User::name)
                    .width(120).end()
                .column("邮箱", User::email)
                    .width(200).end()
                .nodeColumn("角色", user -> TagAnt.create(user.role())
                        .type(switch (user.role()) {
                            case "管理员" -> TagAnt.Type.PRIMARY;
                            case "编辑" -> TagAnt.Type.SUCCESS;
                            default -> TagAnt.Type.DEFAULT;
                        }).build())
                    .width(100).end()
                .nodeColumn("状态", user -> BadgeAnt.create()
                        .dot(true)
                        .status("启用".equals(user.status()) ? BadgeAnt.Status.SUCCESS : BadgeAnt.Status.ERROR)
                        .content(new Label(user.status()))
                        .build())
                    .width(80).end()
                .column("部门", User::department)
                    .width(100).end()
                .nodeColumn("评分", user -> RateAnt.create().value(user.rating()).disabled(true).count(5).build())
                    .width(150).end()
                .actionColumn("操作")
                    .action("编辑", user -> showUserForm(user))
                    .action("删除", user -> {
                        // We don't have a Node anchor here, use table
                        showDeleteConfirm(user);
                    })
                    .width(120).end()
                .data(users)
                .build();
    }

    private void showDeleteConfirm(User user) {
        PopconfirmAnt.create()
                .title("确认删除？")
                .description("将永久删除用户 " + user.name() + "，不可恢复。")
                .okText("确认删除")
                .cancelText("取消")
                .target(table)
                .onConfirm(confirmed -> {
                    if (confirmed) {
                        users.remove(user);
                        table.refresh();
                        updateEmptyState();
                    }
                })
                .build()
                .show();
    }

    private void showUserForm(User existing) {
        InputAnt nameField = InputAnt.create().placeholder("用户名");
        if (existing != null) nameField.text(existing.name());

        InputAnt emailField = InputAnt.create().placeholder("邮箱");
        if (existing != null) emailField.text(existing.email());

        ComboBoxAnt<String> roleSelect = ComboBoxAnt.create();
        roleSelect.getItems().addAll(ROLE_OPTIONS);
        roleSelect.setValue(existing != null ? existing.role() : "编辑");

        ComboBoxAnt<String> deptSelect = ComboBoxAnt.create();
        deptSelect.getItems().addAll(DEPT_OPTIONS);
        deptSelect.setValue(existing != null ? existing.department() : "技术部");

        Node enabledSwitch = SwitchAnt.create()
                .selected(existing == null || "启用".equals(existing.status()))
                .build();

        Node rateField = RateAnt.create()
                .value(existing != null ? existing.rating() : 3)
                .count(5)
                .build();

        UploadAnt.Builder uploadBuilder = UploadAnt.create()
                .buttonText("上传头像")
                .accept("image/*");
        VBox uploadNode = uploadBuilder.build();

        VBox formBody = new VBox(12);
        formBody.setPadding(new Insets(8, 0, 8, 0));
        formBody.getChildren().addAll(
                formRow("用户名", nameField),
                formRow("邮箱", emailField),
                formRow("角色", roleSelect),
                formRow("部门", deptSelect),
                formRow("启用", enabledSwitch),
                formRow("评分", rateField),
                formRow("头像", uploadNode)
        );

        ModalAnt.ModalResult result = ModalAnt.create()
                .title(existing != null ? "编辑用户" : "新增用户")
                .content(formBody)
                .okText("保存")
                .width(480)
                .onOk(() -> {
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String role = roleSelect.getValue();
                    String dept = deptSelect.getValue();
                    if (existing != null) users.remove(existing);
                    users.add(new User(name, email, role, "启用", dept, 3));
                    table.refresh();
                    updateEmptyState();
                })
                .build();
        result.open(this);
    }

    private static HBox formRow(String label, Node field) {
        Label lbl = new Label(label);
        lbl.setPrefWidth(80);
        lbl.setAlignment(Pos.CENTER_RIGHT);
        lbl.setPadding(new Insets(0, 8, 0, 0));
        HBox.setHgrow(field, Priority.ALWAYS);
        return new HBox(8, lbl, field);
    }

    private void updateEmptyState() {
        if (users.isEmpty()) {
            tableArea.getChildren().setAll(emptyArea);
        } else {
            tableArea.getChildren().setAll(table);
        }
    }
}
