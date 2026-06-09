package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.ToolBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * ToolBar 工具栏 —— 基础按钮 / 带文本 / 分隔线 + 弹性填充 / 垂直。
 */
public class ToolBarExamplePage extends VBoxAnt {

    public ToolBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ToolBar 工具栏")
                .description("可定制工具栏，支持图标按钮组、文本按钮、分隔线、弹性填充、垂直方向。")
                .sections(
                        basicSection(),
                        textSection(),
                        spacerSection(),
                        verticalSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node newIcon = TypographyAnt.text("📄").build();
        Node openIcon = TypographyAnt.text("📂").build();
        Node saveIcon = TypographyAnt.text("💾").build();

        Node demo = ToolBarAnt.create()
                .button(newIcon, "新建", () -> System.out.println("新建"))
                .button(openIcon, "打开", () -> System.out.println("打开"))
                .button(saveIcon, "保存", () -> System.out.println("保存"));
        String code = """
                ToolBarAnt.create()
                    .button(newIcon, "新建", () -> ...)
                    .button(openIcon, "打开", () -> ...)
                    .button(saveIcon, "保存", () -> ...)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础图标按钮",
                "button(icon, tooltip, action) 添加图标按钮，悬停显示 tooltip。",
                code, demo);
    }

    private Node textSection() {
        Node newIcon = TypographyAnt.text("📄").build();
        Node openIcon = TypographyAnt.text("📂").build();
        Node saveIcon = TypographyAnt.text("💾").build();

        Node demo = ToolBarAnt.create()
                .button("新建", newIcon, "新建文件", () -> System.out.println("新建"))
                .button("打开", openIcon, "打开文件", () -> System.out.println("打开"))
                .button("保存", saveIcon, "保存文件", () -> System.out.println("保存"));
        String code = """
                ToolBarAnt.create()
                    .button("新建", newIcon, "新建文件", () -> ...)
                    .button("打开", openIcon, "打开文件", () -> ...)
                    .button("保存", saveIcon, "保存文件", () -> ...)
                    .build();
                """;
        return Demos.sectionWithCode("2. 带文本按钮",
                "button(text, icon, tooltip, action) 四参重载支持带文本标签的按钮。",
                code, demo);
    }

    private Node spacerSection() {
        Node cutIcon = TypographyAnt.text("✂️").build();
        Node copyIcon = TypographyAnt.text("📋").build();
        Node pasteIcon = TypographyAnt.text("📌").build();
        Node settingsIcon = TypographyAnt.text("⚙").build();

        Node demo = ToolBarAnt.create()
                .button(cutIcon, "剪切", () -> System.out.println("剪切"))
                .button(copyIcon, "复制", () -> System.out.println("复制"))
                .button(pasteIcon, "粘贴", () -> System.out.println("粘贴"))
                .divider()
                .button(settingsIcon, "设置", () -> System.out.println("设置"))
                .spacer()
                .button(TypographyAnt.text("❓").build(), "帮助", () -> System.out.println("帮助"));
        String code = """
                ToolBarAnt.create()
                    .button(cutIcon, "剪切", () -> ...)
                    .button(copyIcon, "复制", () -> ...)
                    .button(pasteIcon, "粘贴", () -> ...)
                    .divider()
                    .button(settingsIcon, "设置", () -> ...)
                    .spacer()
                    .button(helpIcon, "帮助", () -> ...)
                    .build();
                """;
        return Demos.sectionWithCode("3. 分隔线与弹性填充",
                "divider() 插入竖向分隔线；spacer() 将右侧内容推到最右。",
                code, demo);
    }

    private Node verticalSection() {
        Node selectIcon = TypographyAnt.text("🖱").build();
        Node penIcon = TypographyAnt.text("✏️").build();
        Node eraserIcon = TypographyAnt.text("🧹").build();
        Node fillIcon = TypographyAnt.text("🎨").build();

        Node demo = ToolBarAnt.create()
                .orientation(Orientation.VERTICAL)
                .button(selectIcon, "选择", () -> System.out.println("选择"))
                .button(penIcon, "画笔", () -> System.out.println("画笔"))
                .button(eraserIcon, "橡皮", () -> System.out.println("橡皮"))
                .divider()
                .button(fillIcon, "填充", () -> System.out.println("填充"));
        String code = """
                ToolBarAnt.create()
                    .orientation(Orientation.VERTICAL)
                    .button(selectIcon, "选择", () -> ...)
                    .button(penIcon, "画笔", () -> ...)
                    .button(eraserIcon, "橡皮", () -> ...)
                    .build();
                """;
        return Demos.sectionWithCode("4. 垂直工具栏",
                "orientation(VERTICAL) 创建垂直方向工具栏，常用于画图/编辑器侧边工具栏。",
                code, demo);
    }
}
