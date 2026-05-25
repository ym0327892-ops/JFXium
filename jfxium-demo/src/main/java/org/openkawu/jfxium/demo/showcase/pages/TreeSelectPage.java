package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.TreeSelectAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.List;

/**
 * TreeSelect 树形选择展示页（M19.14）。
 */
public class TreeSelectPage implements ShowcasePage {

    @Override public String   key()      { return "tree-select"; }
    @Override public String   title()    { return "TreeSelect 树形选择"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("TreeSelect 树形选择");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("下拉树形选择 —— 部门 / 权限 / 分类等层级数据 + 单选场景。与 Cascader 区别：TreeSelect 是单一树展开/折叠，Cascader 是多级横向面板。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionDepartment(),
                        sectionPermission(),
                        sectionMultiple()
                )
                .build();
    }

    private static TreeSelectAnt.TreeNode buildDeptTree() {
        return new TreeSelectAnt.TreeNode("root", "全公司", List.of(
                new TreeSelectAnt.TreeNode("tech", "技术中心", List.of(
                        new TreeSelectAnt.TreeNode("frontend", "前端组"),
                        new TreeSelectAnt.TreeNode("backend", "后端组"),
                        new TreeSelectAnt.TreeNode("qa", "QA 组")
                )),
                new TreeSelectAnt.TreeNode("biz", "业务中心", List.of(
                        new TreeSelectAnt.TreeNode("ops", "运营组"),
                        new TreeSelectAnt.TreeNode("market", "市场组")
                )),
                new TreeSelectAnt.TreeNode("hr", "人事行政")
        ));
    }

    private Node sectionDepartment() {
        Node t = TreeSelectAnt.create()
                .placeholder("请选择部门")
                .tree(buildDeptTree())
                .onSelect(node -> MessageAnt.info("选中：" + node.getLabel()))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：部门选择")
                .description("典型 HR / OA 场景：选所属部门")
                .demo(t)
                .code("""
                        TreeSelectAnt.TreeNode root = new TreeSelectAnt.TreeNode("root", "全公司", List.of(
                            new TreeSelectAnt.TreeNode("tech", "技术中心", List.of(
                                new TreeSelectAnt.TreeNode("frontend", "前端组"),
                                new TreeSelectAnt.TreeNode("backend", "后端组")
                            )),
                            ...
                        ));

                        TreeSelectAnt.create()
                            .placeholder("请选择部门")
                            .tree(root)
                            .onSelect(node -> save(node))
                            .build();
                        """)
                .build();
    }

    private Node sectionPermission() {
        TreeSelectAnt.TreeNode permRoot = new TreeSelectAnt.TreeNode("all", "所有权限", List.of(
                new TreeSelectAnt.TreeNode("user", "用户管理", List.of(
                        new TreeSelectAnt.TreeNode("user.view", "查看"),
                        new TreeSelectAnt.TreeNode("user.edit", "编辑"),
                        new TreeSelectAnt.TreeNode("user.delete", "删除")
                )),
                new TreeSelectAnt.TreeNode("order", "订单管理", List.of(
                        new TreeSelectAnt.TreeNode("order.view", "查看"),
                        new TreeSelectAnt.TreeNode("order.refund", "退款")
                ))
        ));

        Node t = TreeSelectAnt.create()
                .placeholder("请选择权限")
                .tree(permRoot)
                .build();
        return ShowcaseSection.create()
                .title("场景 2：权限选择")
                .description("admin 角色配置 / 资源授权")
                .demo(t)
                .code("""
                        TreeSelectAnt.create()
                            .placeholder("请选择权限")
                            .tree(permTree)
                            .build();
                        """)
                .build();
    }

    private Node sectionMultiple() {
        Node t = TreeSelectAnt.create()
                .placeholder("可多选部门")
                .tree(buildDeptTree())
                .multiple(true)
                .onMultipleSelect(nodes -> MessageAnt.info("已选 " + nodes.size() + " 个部门"))
                .build();
        return ShowcaseSection.create()
                .title("场景 3：多选模式")
                .description(".multiple(true) —— 可勾选多个节点；适合权限批量赋予")
                .demo(t)
                .code("""
                        TreeSelectAnt.create()
                            .tree(deptTree)
                            .multiple(true)
                            .onMultipleSelect(nodes -> handleMulti(nodes))
                            .build();
                        """)
                .build();
    }
}
