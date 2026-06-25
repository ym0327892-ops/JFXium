package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.SplitMenuButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.MenuItemUtil;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * SplitMenuButton 分裂菜单按钮 —— 基础 / 带图标 / 尺寸 / 禁用。
 */
public class SplitMenuButtonExamplePage extends VBoxAnt {

    public SplitMenuButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("SplitMenuButton 分裂菜单按钮")
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
                SplitMenuButtonAnt.create()
                        .text("新建")
                        .items(
                                MenuItemUtil.item("新建文件"),
                                MenuItemUtil.item("新建文件夹"),
                                MenuItemUtil.item("新建项目")
                        )
                        .onAction(e -> MessageAnt.info("已执行快速新建"))
                        .build(),
                SplitMenuButtonAnt.create()
                        .text("分享")
                        .items(
                                MenuItemUtil.item("复制链接"),
                                MenuItemUtil.item("邮件分享"),
                                MenuItemUtil.item("导出 PDF")
                        )
                        .onAction(e -> MessageAnt.info("已执行复制链接"))
                        .build()
        );
        String code = """
                SplitMenuButtonAnt.create()
                    .text("新建")
                    .items(
                        MenuItemUtil.item("新建文件"),
                        MenuItemUtil.item("新建文件夹"),
                        MenuItemUtil.item("新建项目")
                    )
                    .onAction(e -> MessageAnt.info("已执行快速新建"))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "text() 设置按钮文字；items() 设置下拉菜单项；onAction() 设置左侧按钮点击回调。",
                code, demo);
    }

    private Node iconSection() {
        Node plusIcon = TypographyAnt.text("📄").build();
        Node shareIcon = TypographyAnt.text("📤").build();

        Node demo = Demos.row(
                SplitMenuButtonAnt.create()
                        .text("新建")
                        .graphic(plusIcon)
                        .items(MenuItemUtil.item("新建文件"), MenuItemUtil.item("新建文件夹"))
                        .build(),
                SplitMenuButtonAnt.create()
                        .text("分享")
                        .graphic(shareIcon)
                        .items(MenuItemUtil.item("复制链接"), MenuItemUtil.item("邮件"))
                        .build()
        );
        String code = """
                SplitMenuButtonAnt.create()
                    .text("新建")
                    .graphic(iconNode)
                    .items(
                        MenuItemUtil.item("新建文件"),
                        MenuItemUtil.item("新建文件夹")
                    )
                    .build();
                """;
        return Demos.sectionWithCode("2. 带图标",
                "graphic(node) 设置按钮前图标。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                SplitMenuButtonAnt.create()
                        .text("Small")
                        .items(MenuItemUtil.item("选项 1"), MenuItemUtil.item("选项 2"))
                        .size(Size.SMALL)
                        .build(),
                SplitMenuButtonAnt.create()
                        .text("Default")
                        .items(MenuItemUtil.item("选项 1"), MenuItemUtil.item("选项 2"))
                        .size(Size.DEFAULT)
                        .build(),
                SplitMenuButtonAnt.create()
                        .text("Large")
                        .items(MenuItemUtil.item("选项 1"), MenuItemUtil.item("选项 2"))
                        .size(Size.LARGE)
                        .build(),
                SplitMenuButtonAnt.create()
                        .text("Disabled")
                        .items(MenuItemUtil.item("选项 1"))
                        .disabled(true)
                        .build()
        );
        String code = """
                SplitMenuButtonAnt.create()
                    .text("Small")
                    .size(Size.SMALL)
                    .build();
                SplitMenuButtonAnt.create()
                    .text("Disabled")
                    .disabled(true)
                    .build();
                """;
        return Demos.sectionWithCode("3. 尺寸与禁用",
                "size(SMALL/DEFAULT/LARGE) 三种尺寸；disabled(true) 禁用整个按钮。",
                code, demo);
    }
}
