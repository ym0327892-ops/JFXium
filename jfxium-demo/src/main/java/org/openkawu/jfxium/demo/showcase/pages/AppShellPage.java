package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AppShellAnt;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.GridAnt;
import org.openkawu.jfxium.component.MenuAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * AppShellAnt 应用骨架展示页（M19.22 增强 Sider 折叠 + breakpoint 自适应）。
 */
public class AppShellPage implements ShowcasePage {

    @Override public String   key()      { return "app-shell"; }
    @Override public String   title()    { return "AppShell 应用骨架"; }
    @Override public Category category() { return Category.LAYOUT; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("AppShell 应用骨架");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label(
                "Header / Sider / Content / Footer 五区位骨架。M19.22 加 Sider 折叠 + breakpoint 自适应。"
        );
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");
        pageDesc.setWrapText(true);

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionCollapsible(),
                        sectionExternalControl(),
                        sectionBreakpoint()
                )
                .build();
    }

    /** 一个简单 sider menu 节点。 */
    private Node sampleMenu() {
        return MenuAnt.create()
                .item("dashboard", "Dashboard", () -> {})
                .item("users", "Users", () -> {})
                .item("settings", "Settings", () -> {})
                .selectedKey("dashboard")
                .build();
    }

    /** 一个简单的 header 节点。 */
    private HBox sampleHeader(String title) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMaxWidth(Double.MAX_VALUE);
        Label user = new Label("admin");
        user.setStyle("-fx-text-fill: -color-fg-muted;");
        HBox h = new HBox(8, t, spacer, user);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-padding: 8 16; -fx-background-color: -color-bg-container;" +
                "-fx-border-color: transparent transparent -color-border-muted transparent;" +
                "-fx-border-width: 0 0 1 0;");
        return h;
    }

    /** 一个简单的 content placeholder。 */
    private StackPane samplePlaceholder(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted;");
        StackPane sp = new StackPane(l);
        sp.setStyle("-fx-background-color: -color-bg-layout;");
        return sp;
    }

    private Node sectionBasic() {
        BorderPane shell = AppShellAnt.create()
                .header(sampleHeader("基础（不可折叠）"))
                .sider(sampleMenu(), 200)
                .content(samplePlaceholder("Content 区域"))
                .build();
        shell.setMinHeight(280);
        shell.setPrefHeight(280);
        shell.setStyle("-fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 4;");

        return ShowcaseSection.create()
                .title("基础用法（向下兼容）")
                .description("不启用 collapsible 时，Sider 固定宽度，与老版本完全一致")
                .demo(shell)
                .code("""
                        BorderPane shell = AppShellAnt.create()
                            .header(headerNode)
                            .sider(menuNode, 200)
                            .content(pageNode)
                            .build();
                        """)
                .build();
    }

    private Node sectionCollapsible() {
        BorderPane shell = AppShellAnt.create()
                .header(sampleHeader("可折叠（点击底部 ‹/› 切换）"))
                .sider(sampleMenu(), 200)
                .collapsible(true)
                .collapsedWidth(64)
                .content(samplePlaceholder("点击 sider 底部 ‹ 按钮折叠 / 展开"))
                .build();
        shell.setMinHeight(320);
        shell.setPrefHeight(320);
        shell.setStyle("-fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 4;");

        return ShowcaseSection.create()
                .title("可折叠 Sider（M19.22）")
                .description(".collapsible(true) 启用 + 内置底部 ‹/› 触发按钮。折叠时缩到 collapsedWidth")
                .demo(shell)
                .code("""
                        AppShellAnt.create()
                            .sider(menu, 200)
                            .collapsible(true)
                            .collapsedWidth(64)              // 折叠后宽度
                            .onCollapseChange(c -> log(c))   // 状态变化回调
                            .content(page)
                            .build();
                        """)
                .build();
    }

    private Node sectionExternalControl() {
        AppShellAnt.Result result = AppShellAnt.create()
                .header(sampleHeader("外部控制"))
                .sider(sampleMenu(), 200)
                .collapsible(true)
                .trigger(false)                                 // 不显示内置按钮
                .content(samplePlaceholder("用上方的「外部按钮」控制折叠"))
                .buildResult();
        BorderPane shell = result.getRoot();
        shell.setMinHeight(280);
        shell.setPrefHeight(280);
        shell.setStyle("-fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 4;");

        Node toggleBtn = ButtonAnt.create("外部 toggle")
                .onClick(e -> result.toggle())
                .build();

        VBox col = new VBox(8, toggleBtn, shell);

        return ShowcaseSection.create()
                .title("外部控制折叠（buildResult）")
                .description(".trigger(false) 隐藏内置按钮，用 result.toggle() 让 header 上的汉堡按钮控制")
                .demo(col)
                .code("""
                        AppShellAnt.Result result = AppShellAnt.create()
                            .sider(menu, 200)
                            .collapsible(true)
                            .trigger(false)
                            .content(page)
                            .buildResult();

                        // 自定义按钮控制
                        myButton.setOnAction(e -> result.toggle());
                        result.collapsedProperty().addListener(...);
                        """)
                .build();
    }

    private Node sectionBreakpoint() {
        BorderPane shell = AppShellAnt.create()
                .header(sampleHeader("响应式自适应（拖窗口宽度）"))
                .sider(sampleMenu(), 220)
                .collapsible(true)
                .breakpoint(GridAnt.Breakpoint.LG)              // < 992px 自动折叠
                .content(samplePlaceholder("拖动窗口窄于 992px 时 sider 自动折叠"))
                .build();
        shell.setMinHeight(280);
        shell.setPrefHeight(280);
        shell.setStyle("-fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 4;");

        return ShowcaseSection.create()
                .title("响应式断点自适应（M19.22）")
                .description(".breakpoint(Breakpoint.LG) —— Scene 宽度 < 992px 时 sider 自动折叠（复用 GridAnt 断点）")
                .demo(shell)
                .code("""
                        AppShellAnt.create()
                            .sider(menu, 220)
                            .collapsible(true)
                            .breakpoint(GridAnt.Breakpoint.LG)   // < 992px 自动折叠
                            .content(page)
                            .build();
                        """)
                .build();
    }
}
