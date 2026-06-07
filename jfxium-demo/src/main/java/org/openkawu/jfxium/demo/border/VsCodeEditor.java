package org.openkawu.jfxium.demo.border;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.openkawu.jfxium.component.composite.BarAnt;
import org.openkawu.jfxium.component.composite.TabsAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.control.MenuBarAnt;
import org.openkawu.jfxium.component.control.TextAreaAnt;
import org.openkawu.jfxium.component.control.TooltipAnt;
import org.openkawu.jfxium.component.control.TreeAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.SplitPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * VS Code 风格编辑器布局示例。
 *
 * <p>由 6 个区域组成：</p>
 * <ol>
 *   <li>顶部菜单栏 {@link MenuBarAnt}</li>
 *   <li>多标签编辑器（{@link TabsAnt} CARD 模式）</li>
 *   <li>侧边栏（Activity Bar + 文件树 {@link TreeAnt}）</li>
 *   <li>代码编辑区（{@link TextAreaAnt} + {@link JfxStyles#CODE_EDITOR}）</li>
 *   <li>底部终端面板（{@link TextAreaAnt} + {@link JfxStyles#CODE_EDITOR_TERMINAL}）</li>
 *   <li>状态栏 {@link BarAnt}</li>
 * </ol>
 *
 * <p>整体通过 {@link SplitPaneAnt} 嵌套实现可拖拽分屏。</p>
 *
 * <p><b>已知 jfxium gap：</b>本类继承自 {@link BorderPane} 而非 jfxium 的 Ant 容器，
 * 因 jfxium 当前未提供 BorderPaneAnt 包装（这是一个组件层 gap — 后续可在
 * <code>component.layout</code> 包里补 <code>BorderPaneAnt</code>）。
 * 视觉风格通过 <code>jfx-bg-layout</code> styleClass 走主题色 token，符合 jfxium 规范。</p>
 */
public class VsCodeEditor extends BorderPane {

    public VsCodeEditor() {
        // 整体 IDE 底色走主题的 layout 层级（light=#fafafa / dark=#0d1117），
        // 跟 BG_DEFAULT(白/编辑器) / BG_SUBTLE(深色面板) 形成三层视觉层次。
        getStyleClass().add(JfxStyles.BG_LAYOUT);

        // ① 顶部菜单栏 —— 注意 menu() 返回 MenuBuilder，链式收尾 endMenu()
        MenuBarAnt menuBar = MenuBarAnt.create()
            .menu("File")
                .item("New File", () -> newFile())
                .item("Open...", () -> openFile())
                .divider()
                .item("Save", () -> saveFile())
                .item("Save All", () -> saveAll())
                .divider()
                .item("Exit", () -> exitApp())
                .endMenu()
            .menu("Edit")
                .item("Undo", () -> undo())
                .item("Redo", () -> redo())
                .divider()
                .item("Cut", () -> cut())
                .item("Copy", () -> copy())
                .item("Paste", () -> paste())
                .endMenu()
            .menu("View")
                .item("Toggle Terminal", () -> toggleTerminal())
                .item("Toggle Sidebar", () -> toggleSidebar())
                .endMenu()
            .menu("Help")
                .item("About", () -> showAbout())
                .endMenu();
        setTop(menuBar);

        // ③ 侧边栏 —— 文件树（jfx-bg-default 浅底，跟编辑器背景一致，视觉连续）
        TreeView<String> fileTree = TreeAnt.<String>create()
            .root("my-project",
                TreeAnt.node("src",
                    TreeAnt.leaf("Main.java"),
                    TreeAnt.leaf("App.java"),
                    TreeAnt.leaf("Utils.java")),
                TreeAnt.node("resources",
                    TreeAnt.leaf("style.css"),
                    TreeAnt.leaf("app.properties")),
                TreeAnt.node("test",
                    TreeAnt.leaf("MainTest.java")))
            .onSelect(this::openInTab)
            .build();
        fileTree.setPrefWidth(240);
        fileTree.getStyleClass().add(JfxStyles.BG_DEFAULT);

        // ③ 左侧 Activity Bar（图标导航栏）—— subtle 深色面板，VS Code 风格
        VBoxAnt activityBar = VBoxAnt.create()
            .spacing(0)
            .align(Pos.TOP_CENTER)
            .padding(8, 4, 8, 4)
            .children(
                buildIconButton(AntDesignIconsOutlined.FILE_TEXT, "Explorer"),
                buildIconButton(AntDesignIconsOutlined.SEARCH, "Search"),
                buildIconButton(AntDesignIconsOutlined.CODE, "Source Control"),
                buildIconButton(AntDesignIconsOutlined.BUG, "Run & Debug"),
                buildIconButton(AntDesignIconsOutlined.APPSTORE, "Extensions")
            )
            .build();
        activityBar.getStyleClass().add(JfxStyles.BG_SUBTLE);

        HBoxAnt sidebar = HBoxAnt.create()
            .spacing(0)
            .styleClass(JfxStyles.BG_SUBTLE)   // 侧边栏整体走 subtle 风格（与 Activity Bar 一致）
            .children(activityBar, fileTree)
            .build();

        // ② + ④ 编辑区 = TabsAnt (CARD 模式)
        Node editorArea = TabsAnt.create()
            .type(TabsAnt.Type.CARD)
            .tab("welcome", "Welcome", createCodeArea("Welcome to JFXium VS Code Demo!"))
            .tab("main", "Main.java", createCodeArea(
                "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello\");\n    }\n}"))
            .tab("app", "App.java", createCodeArea(
                "public class App {\n    // JFXium application entry\n}"))
            .build();

        // ⑤ 底部终端面板 —— jfx-code-terminal 风格（-color-bg-inset 凹陷底，主题切换自动反色）
        TextAreaAnt terminal = TextAreaAnt.create("$ ")
            .editable(true)   // 模拟终端可输入命令
            .styleClass(JfxStyles.CODE_EDITOR_TERMINAL)
            .build();
        // 终端初始高度由下方 SplitPane 的 dividerPositions(0.7) 控制，无需显式 setPrefHeight

        // 中心区域 = 编辑器 + 终端（垂直 SplitPane，可拖拽调高度）
        SplitPaneAnt centerSplit = SplitPaneAnt.create()
            .direction(SplitPaneAnt.Direction.VERTICAL)
            .items(editorArea, terminal)
            .dividerPositions(0.7)
            .build();

        // 左右分割 = 侧边栏 | 编辑区
        SplitPaneAnt mainSplit = SplitPaneAnt.create()
            .direction(SplitPaneAnt.Direction.HORIZONTAL)
            .items(sidebar, centerSplit)
            .dividerPositions(0.2)
            .build();
        setCenter(mainSplit);

        // ⑥ 状态栏 —— BarAnt 三段式（left + 自动 spacer + right），顶部加 borderTop 分割线
        // ⚠️ 注意：BarAnt 内部已在 left/right 之间插入弹性 Region spacer（HBox.setHgrow(ALWAYS)），
        // 不要再额外插 SpaceAnt —— 那样 spacer 会被夹在 right() 段内，且 BarAnt 内部还会再加一次，
        // 结果是右段内部被撑开，left/right 段之间的弹性失效。
        HBox statusBar = BarAnt.create()
            .left(LabelAnt.create().text(" UTF-8 ").build())
            .left(LabelAnt.create().text(" LF ").build())
            .right(LabelAnt.create().text("Ln 1, Col 1 ").build())
            .right(LabelAnt.create().text(" Java ").build())
            .gap(0)
            .padding(2, 12, 2, 12)
            .borderTop()       // 状态栏顶部 1px 分割线
            .build();
        setBottom(statusBar);
    }

    /**
     * 构造一个图标 + 悬浮提示的图标按钮（VS Code Activity Bar 风格）。
     */
    private static ButtonAnt buildIconButton(AntDesignIconsOutlined icon, String tooltip) {
        ButtonAnt btn = ButtonAnt.create()
            .type(ButtonAnt.Type.TEXT)
            .icon(new FontIcon(icon))
            .build();
        TooltipAnt.create(tooltip).install(btn);
        return btn;
    }

    /**
     * 构造一个可编辑代码区（用 jfx-code-editor 风格，融入 IDE 白色背景）。
     * 字体/字号/选区色/焦点环统一在 _code-editor.less 中定义，无 setStyle。
     */
    private Node createCodeArea(String content) {
        return TextAreaAnt.create(content)
            .wrapText(false)    // IDE 风格不自动换行，靠水平滚动
            .styleClass(JfxStyles.CODE_EDITOR)
            .build();
    }

    // ============================================================
    // 菜单回调桩 —— 真实业务可换成 command/handler
    // ============================================================

    private void newFile() { /* TODO */ }
    private void openFile() { /* TODO */ }
    private void saveFile() { /* TODO */ }
    private void saveAll() { /* TODO */ }
    private void exitApp() { /* TODO */ }
    private void undo() { /* TODO */ }
    private void redo() { /* TODO */ }
    private void cut() { /* TODO */ }
    private void copy() { /* TODO */ }
    private void paste() { /* TODO */ }
    private void toggleTerminal() { /* TODO */ }
    private void toggleSidebar() { /* TODO */ }
    private void showAbout() { /* TODO */ }
    private void openInTab(String file) { /* TODO */ }
}
