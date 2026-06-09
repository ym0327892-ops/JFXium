package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TreeTableAnt;

/**
 * TreeTable 树表格 —— 基础层级 / 文件管理器 / 多选。
 */
public class TreeTableExamplePage extends VBoxAnt {

    public TreeTableExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TreeTable 树表格")
                .description("表格与树形结构的结合体，支持层级展开/折叠，常用于文件管理、组织架构等场景。")
                .sections(
                        basicSection(),
                        fileManagerSection(),
                        multiSelectSection()
                )
                .padding(24)
                .build());
    }

    // 数据模型
    public static class FileNode {
        private final String name;
        private final String size;
        private final String modified;
        public FileNode(String name, String size, String modified) {
            this.name = name; this.size = size; this.modified = modified;
        }
        public String getName() { return name; }
        public String getSize() { return size; }
        public String getModified() { return modified; }
    }

    private Node basicSection() {
        TreeTableAnt<FileNode> demo = TreeTableAnt.<FileNode>create()
                .column("名称", "name", 200)
                .column("大小", "size", 100)
                .column("修改时间", "modified", 160)
                .root(new FileNode("根目录", "-", "-"))
                    .child(new FileNode("src", "-", "2024-01-01"))
                        .child(new FileNode("Main.java", "2KB", "2024-01-02"))
                        .endChild()
                        .child(new FileNode("Utils.java", "1KB", "2024-01-02"))
                        .endChild()
                    .endChild()
                    .child(new FileNode("pom.xml", "1KB", "2024-01-01"))
                    .endChild()
                    .child(new FileNode("README.md", "0.5KB", "2024-01-03"))
                    .endChild()
                .endRoot()
                .expandToLevel(2)
                .build();
        demo.setPrefHeight(250);
        String code = """
                TreeTableAnt.<FileNode>create()
                    .column("名称", "name", 200)
                    .column("大小", "size", 100)
                    .column("修改时间", "modified", 160)
                    .root(new FileNode("根目录", "-", "-"))
                        .child(new FileNode("src", ...))
                            .child(new FileNode("Main.java", ...))
                            .endChild()
                        .endChild()
                        .child(new FileNode("pom.xml", ...))
                        .endChild()
                    .endRoot()
                    .expandToLevel(2)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "column() 定义列，root().child()...endChild() 构建树形层级，expandToLevel(n) 展开到指定层级。",
                code, demo);
    }

    private Node fileManagerSection() {
        TreeTableAnt<FileNode> demo = TreeTableAnt.<FileNode>create()
                .column("名称", "name", 220)
                .column("大小", "size", 100)
                .column("修改时间", "modified", 160)
                .root(new FileNode("我的项目", "-", "-"))
                    .child(new FileNode("src/main/java", "-", "2024-06-01"))
                        .child(new FileNode("App.java", "5KB", "2024-06-08"))
                        .endChild()
                        .child(new FileNode("Controller.java", "12KB", "2024-06-07"))
                        .endChild()
                    .endChild()
                    .child(new FileNode("src/main/resources", "-", "2024-06-01"))
                        .child(new FileNode("application.properties", "0.3KB", "2024-06-01"))
                        .endChild()
                        .child(new FileNode("logback.xml", "1KB", "2024-06-02"))
                        .endChild()
                    .endChild()
                    .child(new FileNode("target", "-", "2024-06-08"))
                    .endChild()
                .endRoot()
                .expandAll()
                .onSelect(node -> System.out.println("选中：" + node.getName()))
                .build();
        demo.setPrefHeight(280);
        String code = """
                TreeTableAnt.<FileNode>create()
                    .column("名称", "name", 220)
                    .column("大小", "size", 100)
                    .column("修改时间", "modified", 160)
                    .root(new FileNode("我的项目", ...))
                        .child(...).endChild()
                        .child(...)
                            .child(...).endChild()
                        .endChild()
                    .endRoot()
                    .expandAll()
                    .onSelect(node -> System.out.println(node.getName()))
                    .build();
                """;
        return Demos.sectionWithCode("2. 文件管理器风格",
                "expandAll() 全部展开；onSelect() 监听选中回调。",
                code, demo);
    }

    private Node multiSelectSection() {
        TreeTableAnt<FileNode> demo = TreeTableAnt.<FileNode>create()
                .column("名称", "name", 200)
                .column("大小", "size", 100)
                .root(new FileNode("文件列表", "-", "-"))
                    .child(new FileNode("报告.docx", "15KB", "2024-06-01"))
                    .endChild()
                    .child(new FileNode("数据.xlsx", "32KB", "2024-06-02"))
                    .endChild()
                    .child(new FileNode("图片.png", "128KB", "2024-06-03"))
                    .endChild()
                .endRoot()
                .multiSelect(true)
                .expandAll()
                .build();
        demo.setPrefHeight(180);
        String code = """
                TreeTableAnt.<FileNode>create()
                    .column("名称", "name", 200)
                    .column("大小", "size", 100)
                    .root(...)
                        .child(...).endChild()
                    .endRoot()
                    .multiSelect(true)
                    .expandAll()
                    .build();
                """;
        return Demos.sectionWithCode("3. 多选模式",
                "multiSelect(true) 启用多选（Ctrl+Click / Shift+Click）。",
                code, demo);
    }
}
