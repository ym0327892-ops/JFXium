package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.TreeSelectAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * TreeSelect 树选择 —— 基础 / 多选。
 */
public class TreeSelectExamplePage extends VBoxAnt {

    public TreeSelectExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TreeSelect 树选择")
                .description("类似 Select，但选项来自一棵树形结构，可逐级展开。")
                .sections(basicSection(), multipleSection(), valueSection())
                .padding(24)
                .build());
    }

    /** 构造一棵「部门」树。 */
    private static TreeSelectAnt.TreeNode deptTree() {
        return new TreeSelectAnt.TreeNode("root", "公司", List.of(
                new TreeSelectAnt.TreeNode("tech", "技术部", List.of(
                        new TreeSelectAnt.TreeNode("fe", "前端组"),
                        new TreeSelectAnt.TreeNode("be", "后端组"))),
                new TreeSelectAnt.TreeNode("product", "产品部", List.of(
                        new TreeSelectAnt.TreeNode("design", "设计组")))));
    }

    private Node basicSection() {
        Node demo = TreeSelectAnt.create()
                .tree(deptTree())
                .placeholder("请选择部门")
                .onSelect(node -> {})
                .build();
        String code = """
                TreeSelectAnt.TreeNode root = new TreeSelectAnt.TreeNode("root", "公司", List.of(
                        new TreeSelectAnt.TreeNode("tech", "技术部", List.of(
                                new TreeSelectAnt.TreeNode("fe", "前端组"),
                                new TreeSelectAnt.TreeNode("be", "后端组")))));

                TreeSelectAnt.create()
                        .tree(root)
                        .placeholder("请选择部门")
                        .onSelect(node -> System.out.println(node.getLabel()))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "tree(root) 提供树根节点；点击箭头展开/折叠，点击节点选中回填。",
                code, demo);
    }

    private Node multipleSection() {
        Label result = new Label("已选：(未选择)");
        Node treeSelect = TreeSelectAnt.create()
                .tree(deptTree())
                .multiple(true)
                .placeholder("可多选")
                .onMultipleSelect(nodes -> {
                    if (nodes.isEmpty()) {
                        result.setText("已选：(未选择)");
                    } else {
                        StringBuilder sb = new StringBuilder("已选 " + nodes.size() + " 项：");
                        for (int i = 0; i < nodes.size(); i++) {
                            if (i > 0) sb.append("、");
                            sb.append(nodes.get(i).getLabel());
                        }
                        result.setText(sb.toString());
                    }
                })
                .build();
        Node demo = Demos.column(treeSelect, result);
        String code = """
                Label result = new Label("已选：(未选择)");
                TreeSelectAnt.create()
                        .tree(deptTree())
                        .multiple(true)
                        .placeholder("可多选")
                        // BUG #53 修复：多选回调已接线，点击切换选中态并回传所有已选节点
                        .onMultipleSelect(nodes -> result.setText("已选 " + nodes.size() + " 项"))
                        .build();
                """;
        return Demos.sectionWithCode("2. 多选",
                "multiple(true) 开启多选；点击节点切换选中态（选中行高亮），onMultipleSelect 回传所有已选节点。",
                code, demo);
    }

    /**
     * 3. 获取选中值 —— onSelect 回调拿到完整 TreeNode（既有 value 又有 label）。
     *
     * <p>这里同时显示 label 和 value，证明回调给的是节点对象本身，
     * 业务侧既能拿到展示文案也能拿到底层值。</p>
     */
    private Node valueSection() {
        Label result = new Label("选中节点：(未选择)");
        Node treeSelect = TreeSelectAnt.create()
                .tree(deptTree())
                .placeholder("请选择部门")
                .onSelect(node -> result.setText(
                        "选中节点：" + node.getLabel() + "（value=" + node.getValue() + "）"))
                .build();
        Node demo = Demos.column(treeSelect, result);
        String code = """
                Label result = new Label("选中节点：(未选择)");
                TreeSelectAnt.create()
                        .tree(deptTree())
                        .placeholder("请选择部门")
                        // node 是完整 TreeNode，既有 getLabel() 也有 getValue()
                        .onSelect(node -> result.setText(
                                "选中节点：" + node.getLabel() + "（value=" + node.getValue() + "）"))
                        .build();
                """;
        return Demos.sectionWithCode("3. 获取选中值",
                "onSelect(node -> ...) 给出完整 TreeNode，可同时取 label 与 value。",
                code, demo);
    }
}
