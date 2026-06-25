package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.ContextMenuAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * ContextMenu 右键菜单 —— 基础右键 / 图标 / 分隔线 / 快捷键。
 */
public class ContextMenuExamplePage extends VBoxAnt {

    public ContextMenuExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ContextMenu 右键菜单")
                .description("绑定到目标元素的右键上下文菜单，支持分组、图标、快捷键提示。")
                .sections(
                        basicSection(),
                        iconSection(),
                        acceleratorSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Label target = TypographyAnt.text("在此区域右键点击").build();
        target.setPrefSize(300, 120);
        target.getStyleClass().add("jfx-demo-dashed-border");

        ContextMenuAnt.ContextMenuResult ctx = ContextMenuAnt.create()
                .item("view", "查看详情", () -> MessageAnt.info("查看详情"))
                .item("edit", "编辑", () -> MessageAnt.info("编辑"))
                .divider()
                .item("copy", "复制", () -> MessageAnt.info("复制"))
                .item("paste", "粘贴", () -> MessageAnt.info("粘贴"))
                .divider()
                .item("delete", "删除", () -> MessageAnt.warning("删除"))
                .target(target)
                .build();

        String code = """
                ContextMenuAnt.create()
                    .item("view", "查看详情", () -> ...)
                    .item("edit", "编辑", () -> ...)
                    .divider()
                    .item("copy", "复制", () -> ...)
                    .item("paste", "粘贴", () -> ...)
                    .divider()
                    .item("delete", "删除", () -> ...)
                    .target(targetNode)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "item(key, label, action) 定义菜单项；divider() 插入分组分隔线；target() 绑定触发节点。",
                code, target);
    }

    private Node iconSection() {
        Label target = TypographyAnt.text("右键此区域查看带图标的菜单").build();
        target.setPrefSize(300, 120);
        target.getStyleClass().add("jfx-demo-dashed-border");

        Node editIcon = TypographyAnt.text("✏️").build();
        Node copyIcon = TypographyAnt.text("📋").build();
        Node deleteIcon = TypographyAnt.text("🗑").build();

        ContextMenuAnt.create()
                .item("edit", "编辑", editIcon, () -> MessageAnt.info("编辑"))
                .item("copy", "复制", copyIcon, () -> MessageAnt.info("复制"))
                .divider()
                .item("delete", "删除", deleteIcon, () -> MessageAnt.warning("删除"))
                .target(target)
                .build();

        String code = """
                ContextMenuAnt.create()
                    .item("edit", "编辑", editIcon, () -> ...)
                    .item("copy", "复制", copyIcon, () -> ...)
                    .divider()
                    .item("delete", "删除", deleteIcon, () -> ...)
                    .target(targetNode)
                    .build();
                """;
        return Demos.sectionWithCode("2. 带图标",
                "item(key, label, icon, action) 四参重载支持在每个菜单项前显示图标。",
                code, target);
    }

    private Node acceleratorSection() {
        Label target = TypographyAnt.text("右键此区域查看快捷键提示").build();
        target.setPrefSize(300, 120);
        target.getStyleClass().add("jfx-demo-dashed-border");

        ContextMenuAnt.create()
                .item("save", "保存", () -> MessageAnt.success("保存"))
                .accelerator("Ctrl+S")
                .item("undo", "撤销", () -> MessageAnt.info("撤销"))
                .accelerator("Ctrl+Z")
                .item("redo", "重做", () -> MessageAnt.info("重做"))
                .accelerator("Ctrl+Shift+Z")
                .divider()
                .item("selectAll", "全选", () -> MessageAnt.info("全选"))
                .accelerator("Ctrl+A")
                .target(target)
                .build();

        String code = """
                ContextMenuAnt.create()
                    .item("save", "保存", () -> ...)
                    .accelerator("Ctrl+S")
                    .item("undo", "撤销", () -> ...)
                    .accelerator("Ctrl+Z")
                    .item("redo", "重做", () -> ...)
                    .accelerator("Ctrl+Shift+Z")
                    .divider()
                    .item("selectAll", "全选", () -> ...)
                    .accelerator("Ctrl+A")
                    .target(targetNode)
                    .build();
                """;
        return Demos.sectionWithCode("3. 快捷键提示",
                "accelerator(shortcut) 在每个 item() 后设置对应快捷键提示（显示在菜单项右侧）。",
                code, target);
    }
}
