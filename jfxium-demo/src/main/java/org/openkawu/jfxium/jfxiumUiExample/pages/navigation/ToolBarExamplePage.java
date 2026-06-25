package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.ToolBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * ToolBar 工具栏 —— 基础按钮 / 带文本 / 分隔线 + 弹性填充 / 垂直。
 *
 * <p>默认基准是 32px；模板本身负责留白，内部按钮默认比容器基准小一档，通常走 SMALL。
 * 如果是更紧的侧边栏或状态区，再显式切 XS / LINK。</p>
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
                        verticalSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node newIcon = TypographyAnt.text("📄").build();
        Node openIcon = TypographyAnt.text("📂").build();
        Node saveIcon = TypographyAnt.text("💾").build();

        Node demo = ToolBarAnt.create()
                .button(newIcon, "新建", () -> MessageAnt.info("已点击: 新建"))
                .button(openIcon, "打开", () -> MessageAnt.info("已点击: 打开"))
                .button(saveIcon, "保存", () -> MessageAnt.info("已点击: 保存"))
                .build();
        String code = """
                ToolBarAnt.create()
                    .button(newIcon, "新建", () -> MessageAnt.info("已点击: 新建"))
                    .button(openIcon, "打开", () -> MessageAnt.info("已点击: 打开"))
                    .button(saveIcon, "保存", () -> MessageAnt.info("已点击: 保存"))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础图标按钮",
                "button(icon, tooltip, action) 添加图标按钮，默认对齐 32px 工具栏高度。",
                code, demo);
    }

    private Node textSection() {
        Node newIcon = TypographyAnt.text("📄").build();
        Node openIcon = TypographyAnt.text("📂").build();
        Node saveIcon = TypographyAnt.text("💾").build();

        Node demo = ToolBarAnt.create()
                .button("新建", newIcon, "新建文件", () -> MessageAnt.info("已点击: 新建文件"))
                .button("打开", openIcon, "打开文件", () -> MessageAnt.info("已点击: 打开文件"))
                .button("保存", saveIcon, "保存文件", () -> MessageAnt.info("已点击: 保存文件"))
                .build();
        String code = """
                ToolBarAnt.create()
                    .button("新建", newIcon, "新建文件", () -> MessageAnt.info("已点击: 新建文件"))
                    .button("打开", openIcon, "打开文件", () -> MessageAnt.info("已点击: 打开文件"))
                    .button("保存", saveIcon, "保存文件", () -> MessageAnt.info("已点击: 保存文件"))
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
                .button(cutIcon, "剪切", () -> MessageAnt.info("已点击: 剪切"))
                .button(copyIcon, "复制", () -> MessageAnt.info("已点击: 复制"))
                .button(pasteIcon, "粘贴", () -> MessageAnt.info("已点击: 粘贴"))
                .divider()
                .button(settingsIcon, "设置", () -> MessageAnt.info("已点击: 设置"))
                .spacer()
                .button(TypographyAnt.text("❓").build(), "帮助", () -> MessageAnt.info("已点击: 帮助"))
                .build();
        String code = """
                ToolBarAnt.create()
                    .button(cutIcon, "剪切", () -> MessageAnt.info("已点击: 剪切"))
                    .button(copyIcon, "复制", () -> MessageAnt.info("已点击: 复制"))
                    .button(pasteIcon, "粘贴", () -> MessageAnt.info("已点击: 粘贴"))
                    .divider()
                    .button(settingsIcon, "设置", () -> MessageAnt.info("已点击: 设置"))
                    .spacer()
                    .button(helpIcon, "帮助", () -> MessageAnt.info("已点击: 帮助"))
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
                .button(selectIcon, "选择", () -> MessageAnt.info("已点击: 选择"))
                .button(penIcon, "画笔", () -> MessageAnt.info("已点击: 画笔"))
                .button(eraserIcon, "橡皮", () -> MessageAnt.info("已点击: 橡皮"))
                .divider()
                .button(fillIcon, "填充", () -> MessageAnt.info("已点击: 填充"))
                .build();
        String code = """
                ToolBarAnt.create()
                    .orientation(Orientation.VERTICAL)
                    .button(selectIcon, "选择", () -> MessageAnt.info("已点击: 选择"))
                    .button(penIcon, "画笔", () -> MessageAnt.info("已点击: 画笔"))
                    .button(eraserIcon, "橡皮", () -> MessageAnt.info("已点击: 橡皮"))
                    .build();
                """;
        return Demos.sectionWithCode("4. 垂直工具栏",
                "orientation(VERTICAL) 创建垂直方向工具栏，侧栏宽度默认 32px；按钮通常比容器小一档，若要更密再显式 small / xs。",
                code, demo);
    }

    /** 5. PlayGround：实时调整 orientation / mode / divider / spacer。 */
    private Node playgroundSection() {
        Binder<String> orientation = PlayGround.binder("horizontal");
        Binder<String> mode = PlayGround.binder("icon");
        Binder<String> divider = PlayGround.binder("yes");
        Binder<String> spacer = PlayGround.binder("yes");
        return PlayGround.rebindRebuild(
                () -> buildToolBar(orientation.get(), mode.get(), divider.get(), spacer.get()),
                "方向 / 模式 / 分隔 / 弹性",
                PlayGround.row("方向", PlayGround.segmented(orientation,
                        PlayGround.entry("horizontal", "水平"),
                        PlayGround.entry("vertical", "垂直"))),
                PlayGround.row("按钮模式", PlayGround.segmented(mode,
                        PlayGround.entry("icon", "仅图标"),
                        PlayGround.entry("text", "图标+文本"))),
                PlayGround.row("显示分隔线", PlayGround.segmented(divider,
                        PlayGround.entry("yes", "显示"),
                        PlayGround.entry("no", "隐藏"))),
                PlayGround.row("弹性填充", PlayGround.segmented(spacer,
                        PlayGround.entry("yes", "有"),
                        PlayGround.entry("no", "无"))));
    }

    private Node buildToolBar(String orientation, String mode, String divider, String spacer) {
        Orientation orient = "vertical".equals(orientation)
                ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        Node cutIcon = TypographyAnt.text("✂️").build();
        Node copyIcon = TypographyAnt.text("📋").build();
        Node pasteIcon = TypographyAnt.text("📌").build();
        Node helpIcon = TypographyAnt.text("❓").build();

        ToolBarAnt tb = ToolBarAnt.create(orient);
        if ("text".equals(mode)) {
            tb.button("剪切", cutIcon, "剪切", () -> MessageAnt.info("已点击: 剪切"));
            tb.button("复制", copyIcon, "复制", () -> MessageAnt.info("已点击: 复制"));
            tb.button("粘贴", pasteIcon, "粘贴", () -> MessageAnt.info("已点击: 粘贴"));
        } else {
            tb.button(cutIcon, "剪切", () -> MessageAnt.info("已点击: 剪切"));
            tb.button(copyIcon, "复制", () -> MessageAnt.info("已点击: 复制"));
            tb.button(pasteIcon, "粘贴", () -> MessageAnt.info("已点击: 粘贴"));
        }
        if ("yes".equals(divider)) {
            tb.divider();
        }
        if ("yes".equals(spacer)) {
            tb.spacer();
            tb.button(helpIcon, "帮助", () -> MessageAnt.info("已点击: 帮助"));
        }
        return tb.build();
    }
}
