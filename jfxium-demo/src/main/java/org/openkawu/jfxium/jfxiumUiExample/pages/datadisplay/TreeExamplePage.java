package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TreeAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

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
                        selectableSection(),
                        indicatorSection(),
                        guideLineSection()
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
                .onSelect(item -> MessageAnt.info("选中: " + item))
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
                        .onSelect(item -> MessageAnt.info("选中: " + item))
                        .build();
                """;
        return Demos.sectionWithCode("3. 可选择", "通过 onSelect 回调响应选中事件。", code, tree);
    }

    private Node indicatorSection() {
        TreeView<String> chevron = TreeAnt.<String>create()
                .root("文件夹", folderNodes())
                .indicatorStyle(TreeAnt.IndicatorStyle.CHEVRON)
                .build();
        chevron.setPrefSize(220, 160);

        TreeView<String> plusMinus = TreeAnt.<String>create()
                .root("文件夹", folderNodes())
                .indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS)
                .build();
        plusMinus.setPrefSize(220, 160);

        String code = """
                // 节点工厂默认展开；折叠一个父节点才能同时看到 + 与 −
                TreeItem<String> docs = TreeAnt.node("docs", TreeAnt.leaf("guide.md"));
                docs.setExpanded(false);

                // V 形箭头
                TreeAnt.<String>create()
                        .root("文件夹", TreeAnt.node("src", TreeAnt.leaf("Main.java")), docs)
                        .indicatorStyle(TreeAnt.IndicatorStyle.CHEVRON)
                        .build();

                // 加减号（Windows 资源管理器风格）
                TreeAnt.<String>create()
                        .root("文件夹", TreeAnt.node("src", TreeAnt.leaf("Main.java")), docs)
                        .indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS)
                        .build();

                // 不调用 indicatorStyle 时为默认三角箭头
                """;
        return Demos.sectionWithCode("4. 指示器风格",
                "indicatorStyle 切换展开/折叠符号：ARROW（默认三角）/ CHEVRON（V 形）/ PLUS_MINUS（加减号）。",
                code, Demos.row(chevron, plusMinus));
    }

    /** 每个指示器示例共用的节点结构：一个展开父节点 + 一个折叠父节点。 */
    private TreeItem<String>[] folderNodes() {
        TreeItem<String> src = TreeAnt.node("src",
                TreeAnt.leaf("Main.java"),
                TreeAnt.leaf("App.java"));
        TreeItem<String> docs = TreeAnt.node("docs",
                TreeAnt.leaf("guide.md"),
                TreeAnt.leaf("api.md"));
        docs.setExpanded(false);
        return new TreeItem[]{src, docs};
    }

    private Node guideLineSection() {
        TreeAnt<String> tree = TreeAnt.<String>create()
                .root("项目",
                        TreeAnt.node("src",
                                TreeAnt.node("main",
                                        TreeAnt.leaf("App.java"),
                                        TreeAnt.leaf("Util.java")
                                ),
                                TreeAnt.node("test",
                                        TreeAnt.leaf("AppTest.java")
                                )
                        ),
                        TreeAnt.leaf("pom.xml")
                )
                .showLine(true)
                .build();
        tree.setPrefSize(280, 200);

        String code = """
                // showLine(true)：每层缩进画一条竖向参考线（默认关闭，可随时开关）
                TreeAnt<String> tree = TreeAnt.<String>create()
                        .root("项目",
                                TreeAnt.node("src",
                                        TreeAnt.node("main",
                                                TreeAnt.leaf("App.java")
                                        )
                                ),
                                TreeAnt.leaf("pom.xml")
                        )
                        .showLine(true)
                        .build();

                // 运行时关闭
                tree.showLine(false);
                """;
        return Demos.sectionWithCode("5. 缩进参考线",
                "showLine(true) 在每一层缩进处画竖向参考线，帮助定位层级；默认关闭，可随时开关。",
                code, tree);
    }
}
