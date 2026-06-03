package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import org.openkawu.jfxium.component.control.TableAnt;
import org.openkawu.jfxium.component.control.PaginationAnt;
import org.openkawu.jfxium.component.composite.FormAnt;
import org.openkawu.jfxium.component.composite.BarAnt;

/**
 * CrudTemplate - 通用三段式业务页骨架（M18 新增）。
 *
 * <p><b>定位</b>：admin 后台 90% 业务页的通用模板。<br>
 * 不仅适用于 CRUD 列表，也适用于表单页、详情页、仪表盘——
 * 只要符合"上工具栏 + 中间主内容 + 下分页/状态栏"的形态都能用。</p>
 *
 * <h2>整体结构</h2>
 * <pre>
 * ┌─────────────────────────────────────────────┐
 * │ Title（可选大标题）                            │
 * ├─────────────────────────────────────────────┤
 * │ Topbar（HBox 三段式）                         │
 * │   topLeft                  spacer  topRight │
 * │   [搜索] [筛选下拉]                  [新增] [刷新] │
 * ├─────────────────────────────────────────────┤
 * │                                             │
 * │ Body（任意 Node）                             │
 * │   TableView / Form / Detail / Chart / ...   │
 * │                                             │
 * ├─────────────────────────────────────────────┤
 * │ Bottombar（HBox 三段式）                      │
 * │   bottomLeft               spacer  bottomRight │
 * │   [共 N 条]                  [Pagination] [10/页] │
 * └─────────────────────────────────────────────┘
 * </pre>
 *
 * <h2>使用示例 - CRUD 列表页</h2>
 * <pre>{@code
 * TableView<User> table = TableAnt.<User>create()...build();
 * Pagination pagination = PaginationAnt.create()...build();
 * Label totalLabel = new Label("共 57 条");
 *
 * BorderPane page = CrudTemplate.create()
 *     .title("用户管理")
 *     .topLeft(searchField, roleCombo, statusCombo)   // 左：筛选
 *     .topRight(refreshBtn, addBtn)                    // 右：操作
 *     .body(table)                                     // 中：表格
 *     .bottomLeft(totalLabel)                          // 左：总条数
 *     .bottomRight(pagination, pageSizeCombo)          // 右：分页
 *     .build();
 * }</pre>
 *
 * <h2>使用示例 - 表单页</h2>
 * <pre>{@code
 * VBox form = ...;       // FormAnt 或自己拼
 * BorderPane page = CrudTemplate.create()
 *     .title("新增用户")
 *     .body(form)
 *     .bottomRight(cancelBtn, submitBtn)
 *     .build();
 * }</pre>
 *
 * <h2>使用示例 - 仪表盘</h2>
 * <pre>{@code
 * GridPane statsGrid = ...;
 * BorderPane page = CrudTemplate.create()
 *     .title("数据概览")
 *     .topRight(refreshBtn, exportBtn)
 *     .body(statsGrid)
 *     .build();
 * }</pre>
 *
 * <h2>设计取舍</h2>
 * <ul>
 *   <li>底层 BorderPane（top/center/bottom）—— 顶/底高度自适应内容，center 撑满</li>
 *   <li>topbar/bottombar 内部用 HBox + Region spacer 实现"左+弹性占位+右"</li>
 *   <li>title 是可选的，没设就不渲染（topbar 直接顶到顶部）</li>
 *   <li>topbar / bottombar 内任一边没节点时不渲染该侧（避免空 HBox 占位）</li>
 *   <li>整个组件支持 .bordered / .shadow / .padding 装饰</li>
 * </ul>
 */
public class CrudTemplate {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title;
        private final List<Node> topLeft = new ArrayList<>();
        private final List<Node> topRight = new ArrayList<>();
        private Node body;
        private final List<Node> bottomLeft = new ArrayList<>();
        private final List<Node> bottomRight = new ArrayList<>();

        // 装饰
        private boolean bordered = false;
        private double topbarSpacing = 8;
        private double bottombarSpacing = 8;
        private double sectionGap = 16;     // title / topbar / body / bottombar 之间间距

        private Builder() {}

        // ============================================================
        // 标题
        // ============================================================

        /** 顶部大标题（可选）。设为 null 或不调用即不渲染。 */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        // ============================================================
        // 顶部工具栏（三段式）
        // ============================================================

        /** 顶部左侧节点（按添加顺序水平排列；常用于搜索/筛选）。 */
        public Builder topLeft(Node... nodes) {
            if (nodes != null) for (Node n : nodes) topLeft.add(n);
            return this;
        }

        /** 顶部右侧节点（按添加顺序水平排列；常用于操作按钮）。 */
        public Builder topRight(Node... nodes) {
            if (nodes != null) for (Node n : nodes) topRight.add(n);
            return this;
        }

        // ============================================================
        // 主内容
        // ============================================================

        /** 中间主内容区（任意 Node：TableView / Form / Detail / Chart 等）。 */
        public Builder body(Node body) {
            this.body = body;
            return this;
        }

        // ============================================================
        // 底部工具栏（三段式）
        // ============================================================

        /** 底部左侧节点（常用于"共 N 条"等状态文字）。 */
        public Builder bottomLeft(Node... nodes) {
            if (nodes != null) for (Node n : nodes) bottomLeft.add(n);
            return this;
        }

        /** 底部右侧节点（常用于分页器、每页条数）。 */
        public Builder bottomRight(Node... nodes) {
            if (nodes != null) for (Node n : nodes) bottomRight.add(n);
            return this;
        }

        // ============================================================
        // 装饰
        // ============================================================

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder topbarSpacing(double spacing) {
            this.topbarSpacing = spacing;
            return this;
        }

        public Builder bottombarSpacing(double spacing) {
            this.bottombarSpacing = spacing;
            return this;
        }

        /** title / topbar / body / bottombar 之间的垂直间距（默认 16）。 */
        public Builder sectionGap(double gap) {
            this.sectionGap = gap;
            return this;
        }

        // ============================================================
        // 构建
        // ============================================================

        public BorderPane build() {
            BorderPane root = new BorderPane();
            root.getStyleClass().add(CssClasses.CRUD_TEMPLATE);
            if (bordered) {
                root.getStyleClass().add(CssClasses.CARD_BORDERED);
            }

            // ========== 顶部 = title + topbar（VBox 组合）==========
            VBox top = new VBox(sectionGap);
            top.setAlignment(Pos.TOP_LEFT);

            if (title != null && !title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(CssClasses.CRUD_TEMPLATE_TITLE);
                top.getChildren().add(titleLabel);
            }

            if (!topLeft.isEmpty() || !topRight.isEmpty()) {
                HBox topbar = buildBar(topLeft, topRight, topbarSpacing);
                topbar.getStyleClass().add(CssClasses.CRUD_TEMPLATE_TOPBAR);
                top.getChildren().add(topbar);
            }

            // 仅当顶部有任意内容才设置
            if (!top.getChildren().isEmpty()) {
                BorderPane.setMargin(top, new Insets(0, 0, sectionGap, 0));
                root.setTop(top);
            }

            // ========== 中部 = body ==========
            if (body != null) {
                if (!body.getStyleClass().contains(CssClasses.CRUD_TEMPLATE_BODY)) {
                    body.getStyleClass().add(CssClasses.CRUD_TEMPLATE_BODY);
                }
                root.setCenter(body);
            }

            // ========== 底部 = bottombar ==========
            if (!bottomLeft.isEmpty() || !bottomRight.isEmpty()) {
                HBox bottombar = buildBar(bottomLeft, bottomRight, bottombarSpacing);
                bottombar.getStyleClass().add(CssClasses.CRUD_TEMPLATE_BOTTOMBAR);
                BorderPane.setMargin(bottombar, new Insets(sectionGap, 0, 0, 0));
                root.setBottom(bottombar);
            }

            // 应用 user style
            applyStyles(root);
            return root;
        }

        /**
         * 构建一行二段式工具栏：[ left... ]  spacer  [ right... ]
         * <p>M19 重构：复用 BarAnt 组件（消除内部 buildBar 重复实现）。</p>
         */
        private HBox buildBar(List<Node> left, List<Node> right, double spacing) {
            return BarAnt.create()
                    .left(left.toArray(new Node[0]))
                    .right(right.toArray(new Node[0]))
                    .gap(spacing)
                    .build();
        }
    }
}
