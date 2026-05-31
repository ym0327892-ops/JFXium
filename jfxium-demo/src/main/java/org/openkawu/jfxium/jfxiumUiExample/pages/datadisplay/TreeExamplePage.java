package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.TreeView;
import org.openkawu.jfxium.component.TreeAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Tree 树形控件 —— 基础 / 默认展开 / 可选择。
 */
public class TreeExamplePage extends VBoxAnt {

    public TreeExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tree 树形控件")
                .description("用清晰的层级结构展示信息，可展开或折叠。")
                .sections(
                        basicSection(),
                        expandedSection(),
                        selectableSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root("根节点",
                        TreeAnt.node("父节点 1",
                                TreeAnt.leaf("子节点 1-1"),
                                TreeAnt.leaf("子节点 1-2")
                        ),
                        TreeAnt.leaf("父节点 2")
                )
                .build();
        tree.setPrefHeight(200);
        String code = """
                TreeView<String> tree = TreeAnt.<String>create()
                        .root("根节点",
                                TreeAnt.node("父节点 1",
                                        TreeAnt.leaf("子节点 1-1"),
                                        TreeAnt.leaf("子节点 1-2")
                                ),
                                TreeAnt.leaf("父节点 2")
                        )
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "最简单的树形结构。", code, tree);
    }

    private Node expandedSection() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root("公司",
                        TreeAnt.node("技术部",
                                TreeAnt.leaf("前端组"),
                                TreeAnt.leaf("后端组")
                        ),
                        TreeAnt.node("产品部",
                                TreeAnt.leaf("设计组"),
                                TreeAnt.leaf("运营组")
                        )
                )
                .showRoot(false)
                .build();
        tree.setPrefHeight(200);
        String code = """
                TreeView<String> tree = TreeAnt.<String>create()
                        .root("公司",
                                TreeAnt.node("技术部",
                                        TreeAnt.leaf("前端组"),
                                        TreeAnt.leaf("后端组")
                                ),
                                TreeAnt.node("产品部",
                                        TreeAnt.leaf("设计组"),
                                        TreeAnt.leaf("运营组")
                                )
                        )
                        .showRoot(false)
                        .build();
                """;
        return Demos.sectionWithCode("2. 隐藏根节点", "showRoot(false) 隐藏根节点，默认展开子节点。", code, tree);
    }

    private Node selectableSection() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root("文件夹",
                        TreeAnt.node("src",
                                TreeAnt.leaf("Main.java"),
                                TreeAnt.leaf("App.java")
                        ),
                        TreeAnt.leaf("README.md")
                )
                .onSelect(item -> System.out.println("选中: " + item))
                .build();
        tree.setPrefHeight(180);
        String code = """
                TreeView<String> tree = TreeAnt.<String>create()
                        .root("文件夹",
                                TreeAnt.node("src",
                                        TreeAnt.leaf("Main.java"),
                                        TreeAnt.leaf("App.java")
                                ),
                                TreeAnt.leaf("README.md")
                        )
                        .onSelect(item -> System.out.println("选中: " + item))
                        .build();
                """;
        return Demos.sectionWithCode("3. 可选择", "通过 onSelect 回调响应选中事件。", code, tree);
    }
}
