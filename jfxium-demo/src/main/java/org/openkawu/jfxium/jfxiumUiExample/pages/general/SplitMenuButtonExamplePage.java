package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.SplitButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * SplitButton 分割按钮 —— 基础 / 带图标 / 尺寸 / 禁用。
 */
public class SplitMenuButtonExamplePage extends VBoxAnt {

    public SplitMenuButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("SplitButton 分割按钮")
                .description("按钮 + 下拉菜单的复合控件：左侧主操作按钮（单击触发），右侧下拉箭头（弹出菜单项）。")
                .sections(
                        basicSection(),
                        iconSection(),
                        sizeSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SplitButtonAnt.create("新建")
                        .onClick(e -> MessageAnt.info("已执行快速新建"))
                        .item("新建文件", e -> MessageAnt.info("新建文件"))
                        .item("新建文件夹", e -> MessageAnt.info("新建文件夹"))
                        .item("新建项目", e -> MessageAnt.info("新建项目"))
                        .build(),
                SplitButtonAnt.create("分享")
                        .onClick(e -> MessageAnt.info("已执行复制链接"))
                        .item("复制链接", e -> MessageAnt.info("复制链接"))
                        .item("邮件分享", e -> MessageAnt.info("邮件分享"))
                        .item("导出 PDF", e -> MessageAnt.info("导出 PDF"))
                        .build()
        );
        String code = """
                SplitButtonAnt.create("新建")
                    .onClick(e -> MessageAnt.info("已执行快速新建"))
                    .item("新建文件", e -> MessageAnt.info("新建文件"))
                    .item("新建文件夹", e -> MessageAnt.info("新建文件夹"))
                    .item("新建项目", e -> MessageAnt.info("新建项目"))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "create(text) 设置按钮文字；item(label, handler) 添加下拉菜单项；onClick(handler) 设置左侧按钮点击回调。",
                code, demo);
    }

    private Node iconSection() {
        Node plusIcon = TypographyAnt.text("📄").build();
        Node shareIcon = TypographyAnt.text("📤").build();

        Node demo = Demos.row(
                SplitButtonAnt.create("新建")
                        .icon(plusIcon)
                        .item("新建文件", e -> MessageAnt.info("新建文件"))
                        .item("新建文件夹", e -> MessageAnt.info("新建文件夹"))
                        .build(),
                SplitButtonAnt.create("分享")
                        .icon(shareIcon)
                        .item("复制链接", e -> MessageAnt.info("复制链接"))
                        .item("邮件", e -> MessageAnt.info("邮件"))
                        .build()
        );
        String code = """
                SplitButtonAnt.create("新建")
                    .icon(iconNode)
                    .item("新建文件", e -> MessageAnt.info("新建文件"))
                    .item("新建文件夹", e -> MessageAnt.info("新建文件夹"))
                    .build();
                """;
        return Demos.sectionWithCode("2. 带图标",
                "icon(node) 设置按钮前图标。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                SplitButtonAnt.create("Small")
                        .size(Size.SMALL)
                        .item("选项 1", e -> {})
                        .item("选项 2", e -> {})
                        .build(),
                SplitButtonAnt.create("Default")
                        .size(Size.DEFAULT)
                        .item("选项 1", e -> {})
                        .item("选项 2", e -> {})
                        .build(),
                SplitButtonAnt.create("Large")
                        .size(Size.LARGE)
                        .item("选项 1", e -> {})
                        .item("选项 2", e -> {})
                        .build(),
                SplitButtonAnt.create("Disabled")
                        .disabled(true)
                        .item("选项 1", e -> {})
                        .build()
        );
        String code = """
                SplitButtonAnt.create("Small")
                    .size(Size.SMALL)
                    .item("选项 1", e -> {})
                    .item("选项 2", e -> {})
                    .build();
                SplitButtonAnt.create("Disabled")
                    .disabled(true)
                    .item("选项 1", e -> {})
                    .build();
                """;
        return Demos.sectionWithCode("3. 尺寸与禁用",
                "size(SMALL/DEFAULT/LARGE) 三种尺寸；disabled(true) 禁用整个按钮。",
                code, demo);
    }
}