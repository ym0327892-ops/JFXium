package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import org.openkawu.jfxium.component.layout.ScrollPaneAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.BackTopAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * BackTop 回到顶部 —— 基础用法 / 自定义位置。
 */
public class BackTopExamplePage extends VBoxAnt {

    public BackTopExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("BackTop 回到顶部")
                .description("滚动到一定高度后显示的浮动按钮，点击后平滑回到页面顶部。")
                .sections(
                        basicSection(),
                        positionSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        // 构造一个可滚动区域来演示 BackTop
        VBoxAnt content = VBoxAnt.create().spacing(4);
        for (int i = 1; i <= 50; i++) {
            content.children(TypographyAnt.text("第 " + i + " 行 - 向下滚动查看 BackTop 按钮").build());
        }
        content.padding(16);

        ScrollPane scrollPane = ScrollPaneAnt.create().content(content).build();
        scrollPane.setPrefHeight(200);
        scrollPane.getStyleClass().add("jfx-demo-scroll-demo");

        // 用 StackPane 包裹，放置 BackTop
        StackPaneAnt wrapper = StackPaneAnt.create().children(scrollPane).build();
        wrapper.childAlign(scrollPane, Pos.TOP_LEFT);

        BackTopAnt.install(scrollPane);

        String code = """
                // BackTopAnt.install() 快捷安装到 ScrollPane
                BackTopAnt.install(scrollPane);

                // 或手动构造
                BackTopAnt.create()
                    .target(scrollPane)
                    .visibilityHeight(200)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "BackTopAnt.install(scrollPane) 快捷安装到 ScrollPane；滚动超过默认 400px 时显示。向下滚动试试。",
                code, wrapper);
    }

    private Node positionSection() {
        VBoxAnt content = VBoxAnt.create().spacing(4);
        for (int i = 1; i <= 30; i++) {
            content.children(TypographyAnt.text("条目 #" + i + " - 滚动以显示自定义位置的 BackTop").build());
        }
        content.padding(16);

        ScrollPane scrollPane = ScrollPaneAnt.create().content(content).build();
        scrollPane.setPrefHeight(180);

        StackPaneAnt wrapper = StackPaneAnt.create().children(scrollPane).build();
        wrapper.childAlign(scrollPane, Pos.TOP_LEFT);

        // 手动构造，自定义位置
        Node backTop = BackTopAnt.create()
                .target(scrollPane)
                .visibilityHeight(100)
                .bottom(20).right(20)
                .build();
        wrapper.childAlign(backTop, Pos.BOTTOM_RIGHT);
        wrapper.margin(backTop, new Insets(0, 20, 20, 0));
        wrapper.getChildren().add(backTop);

        String code = """
                BackTopAnt.create()
                    .target(scrollPane)
                    .visibilityHeight(100)
                    .bottom(20).right(20)
                    .build();
                """;
        return Demos.sectionWithCode("2. 自定义位置与触发高度",
                "visibilityHeight(n) 设置触发显示高度；bottom(n)/right(n) 控制右下角偏移位置。",
                code, wrapper);
    }

    /** 3. PlayGround：实时调整 visibilityHeight / bottom / right。 */
    private Node playgroundSection() {
        Binder<String> visibilityHeight = PlayGround.binder("40");
        Binder<String> bottom = PlayGround.binder("20");
        Binder<String> right = PlayGround.binder("20");
        return PlayGround.rebindRebuild(
                () -> buildBackTopSection(visibilityHeight.get(), bottom.get(), right.get()),
                "触发高度 / 位置偏移",
                PlayGround.row("触发高度", PlayGround.textField(visibilityHeight, "40", "滚动超过此 px 显示")),
                PlayGround.row("距下 bottom", PlayGround.textField(bottom, "20", "右下角底部偏移 px")),
                PlayGround.row("距右 right", PlayGround.textField(right, "20", "右下角右侧偏移 px")));
    }

    private Node buildBackTopSection(String visibilityHeightStr, String bottomStr, String rightStr) {
        int visibilityHeight;
        try { visibilityHeight = Math.max(0, Integer.parseInt(visibilityHeightStr)); }
        catch (NumberFormatException e) { visibilityHeight = 40; }
        double bottom;
        try { bottom = Math.max(0, Double.parseDouble(bottomStr)); }
        catch (NumberFormatException e) { bottom = 20.0; }
        double right;
        try { right = Math.max(0, Double.parseDouble(rightStr)); }
        catch (NumberFormatException e) { right = 20.0; }

        // 50 行内容 + 200px 滚动容器，保证足够滚动距离
        VBoxAnt content = VBoxAnt.create().spacing(4);
        for (int i = 1; i <= 50; i++) {
            content.children(TypographyAnt.text("第 " + i + " 行 - 滚动查看 BackTop").build());
        }
        content.padding(16);

        ScrollPane scrollPane = ScrollPaneAnt.create().content(content).build();
        scrollPane.setPrefHeight(200);

        StackPaneAnt wrapper = StackPaneAnt.create().children(scrollPane).build();
        wrapper.childAlign(scrollPane, Pos.TOP_LEFT);

        Node backTop = BackTopAnt.create()
                .target(scrollPane)
                .visibilityHeight(visibilityHeight)
                .bottom(bottom).right(right)
                .build();
        wrapper.childAlign(backTop, Pos.BOTTOM_RIGHT);
        wrapper.margin(backTop, new Insets(0, right, bottom, 0));
        wrapper.getChildren().add(backTop);
        return wrapper;
    }
}