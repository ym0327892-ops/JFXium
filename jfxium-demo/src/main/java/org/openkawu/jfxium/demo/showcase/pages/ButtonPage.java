package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * ButtonAnt 组件展示页。
 *
 * <p>ButtonAnt 是 admin 高频组件，10 种 Type + 3 种 Size + 2 种 Shape +
 * ghost / block / loading / disabled / icon，组合空间最大。</p>
 */
public class ButtonPage implements ShowcasePage {

    @Override public String   key()      { return "button"; }
    @Override public String   title()    { return "Button 按钮"; }
    @Override public Category category() { return Category.GENERAL; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Button 按钮");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("最常用的交互组件。10 种类型 + 3 种尺寸 + 多种状态/形状变体。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create()
                .spacing(8)
                .children(pageTitle, pageDesc)
                .build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionTypes(),
                        sectionSizes(),
                        sectionShapes(),
                        sectionStates(),
                        sectionGhost(),
                        sectionBlock(),
                        sectionWithIcon(),
                        sectionLoading()
                )
                .build();
    }

    // ============================================================
    // 1. 10 种类型
    // ============================================================
    private Node sectionTypes() {
        FlowPane row = new FlowPane(8, 8);
        row.getChildren().addAll(
                ButtonAnt.create("Default").type(ButtonAnt.Type.DEFAULT).build(),
                ButtonAnt.create("Primary").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("Accent").type(ButtonAnt.Type.ACCENT).build(),
                ButtonAnt.create("Success").type(ButtonAnt.Type.SUCCESS).build(),
                ButtonAnt.create("Warning").type(ButtonAnt.Type.WARNING).build(),
                ButtonAnt.create("Danger").type(ButtonAnt.Type.DANGER).build(),
                ButtonAnt.create("Outlined").type(ButtonAnt.Type.OUTLINED).build(),
                ButtonAnt.create("Dashed").type(ButtonAnt.Type.DASHED).build(),
                ButtonAnt.create("Text").type(ButtonAnt.Type.TEXT).build(),
                ButtonAnt.create("Link").type(ButtonAnt.Type.LINK).build()
        );

        return ShowcaseSection.create()
                .title("10 种按钮类型")
                .description("DEFAULT / PRIMARY / ACCENT / SUCCESS / WARNING / DANGER / OUTLINED / DASHED / TEXT / LINK")
                .demo(row)
                .code("""
                        ButtonAnt.create("Primary").type(ButtonAnt.Type.PRIMARY).build();
                        ButtonAnt.create("Success").type(ButtonAnt.Type.SUCCESS).build();
                        ButtonAnt.create("Danger") .type(ButtonAnt.Type.DANGER) .build();
                        ButtonAnt.create("Dashed") .type(ButtonAnt.Type.DASHED) .build();
                        ButtonAnt.create("Text")   .type(ButtonAnt.Type.TEXT)   .build();
                        ButtonAnt.create("Link")   .type(ButtonAnt.Type.LINK)   .build();
                        // 等等...共 10 种
                        """)
                .build();
    }

    // ============================================================
    // 2. 3 种尺寸
    // ============================================================
    private Node sectionSizes() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                ButtonAnt.create("Small").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.SMALL).build(),
                ButtonAnt.create("Default").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.DEFAULT).build(),
                ButtonAnt.create("Large").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.LARGE).build()
        );

        return ShowcaseSection.create()
                .title("3 种尺寸")
                .description("Size.SMALL / Size.DEFAULT（默认）/ Size.LARGE，适配不同密度场景")
                .demo(row)
                .code("""
                        ButtonAnt.create("Small")  .size(ButtonAnt.Size.SMALL)  .build();
                        ButtonAnt.create("Default").size(ButtonAnt.Size.DEFAULT).build();
                        ButtonAnt.create("Large")  .size(ButtonAnt.Size.LARGE) .build();
                        """)
                .build();
    }

    // ============================================================
    // 3. 形状变体（默认 / rounded / square）
    // ============================================================
    private Node sectionShapes() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                ButtonAnt.create("默认").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("圆角").type(ButtonAnt.Type.PRIMARY).rounded().build(),
                ButtonAnt.create("方形").type(ButtonAnt.Type.PRIMARY).square().build()
        );

        return ShowcaseSection.create()
                .title("形状变体")
                .description("默认 / .rounded() 全圆角 / .square() 直角")
                .demo(row)
                .code("""
                        ButtonAnt.create("圆角").rounded().build();
                        ButtonAnt.create("方形").square().build();
                        """)
                .build();
    }

    // ============================================================
    // 4. 状态（normal / disabled）
    // ============================================================
    private Node sectionStates() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                ButtonAnt.create("正常").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("禁用").type(ButtonAnt.Type.PRIMARY).disabled(true).build(),
                ButtonAnt.create("正常").type(ButtonAnt.Type.DEFAULT).build(),
                ButtonAnt.create("禁用").type(ButtonAnt.Type.DEFAULT).disabled(true).build()
        );

        return ShowcaseSection.create()
                .title("Disabled 禁用状态")
                .description("disabled(true) 禁用按钮，会自动降低不透明度并阻止点击")
                .demo(row)
                .code("""
                        ButtonAnt.create("禁用").disabled(true).build();
                        """)
                .build();
    }

    // ============================================================
    // 5. Ghost 幽灵按钮
    // ============================================================
    private Node sectionGhost() {
        // ghost 一般在彩色背景上使用，这里用一块深色背景演示
        FlowPane row = new FlowPane(8, 8);
        row.setStyle(
                "-fx-background-color: -color-accent-emphasis;" +
                "-fx-padding: 24;" +
                "-fx-background-radius: 4;"
        );
        row.getChildren().addAll(
                ButtonAnt.create("Primary").type(ButtonAnt.Type.PRIMARY).ghost().build(),
                ButtonAnt.create("Default").type(ButtonAnt.Type.DEFAULT).ghost().build(),
                ButtonAnt.create("Danger").type(ButtonAnt.Type.DANGER).ghost().build()
        );

        return ShowcaseSection.create()
                .title("Ghost 幽灵按钮")
                .description("透明背景 + 边框/文字主题色，常用于彩色背景上（如 banner / 渐变区）")
                .demo(row)
                .code("""
                        ButtonAnt.create("Primary").type(ButtonAnt.Type.PRIMARY).ghost().build();
                        ButtonAnt.create("Danger") .type(ButtonAnt.Type.DANGER) .ghost().build();
                        """)
                .build();
    }

    // ============================================================
    // 6. Block 块级按钮
    // ============================================================
    private Node sectionBlock() {
        Button block = ButtonAnt.create("Block 按钮（宽度占满父容器）")
                .type(ButtonAnt.Type.PRIMARY)
                .block()
                .build();

        VBox container = VBoxBuilder.create()
                .spacing(8)
                .children(block)
                .build();
        container.setPrefWidth(420);
        container.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("Block 块级按钮")
                .description("block() 让按钮宽度占满父容器，常用于表单提交按钮、卡片底部 CTA")
                .demo(container)
                .code("""
                        Button submit = ButtonAnt.create("立即提交")
                            .type(ButtonAnt.Type.PRIMARY)
                            .block()                  // 宽度占满父容器
                            .size(ButtonAnt.Size.LARGE)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 7. 带图标
    // ============================================================
    private Node sectionWithIcon() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                ButtonAnt.create("新增")
                        .type(ButtonAnt.Type.PRIMARY)
                        .icon(IconAnt.path(IconAnt.Path.PLUS, 14))
                        .build(),
                ButtonAnt.create("编辑")
                        .icon(IconAnt.path(IconAnt.Path.EDIT, 14))
                        .build(),
                ButtonAnt.create("删除")
                        .type(ButtonAnt.Type.DANGER)
                        .icon(IconAnt.path(IconAnt.Path.DELETE, 14))
                        .build(),
                ButtonAnt.create("搜索")
                        .type(ButtonAnt.Type.OUTLINED)
                        .icon(IconAnt.path(IconAnt.Path.SEARCH, 14))
                        .build()
        );

        return ShowcaseSection.create()
                .title("带图标")
                .description(".icon(Node) 在按钮文字前添加图标；用 IconAnt.path(...) 提供 SVG 图标")
                .demo(row)
                .code("""
                        ButtonAnt.create("新增")
                            .type(ButtonAnt.Type.PRIMARY)
                            .icon(IconAnt.path(IconAnt.Path.PLUS, 14))
                            .build();

                        ButtonAnt.create("删除")
                            .type(ButtonAnt.Type.DANGER)
                            .icon(IconAnt.path(IconAnt.Path.DELETE, 14))
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 8. Loading 加载状态
    // ============================================================
    private Node sectionLoading() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                ButtonAnt.create("提交中...")
                        .type(ButtonAnt.Type.PRIMARY)
                        .loading(true)
                        .build(),
                ButtonAnt.create("处理中...")
                        .loading(true)
                        .build(),
                ButtonAnt.create("加载中...")
                        .type(ButtonAnt.Type.DASHED)
                        .loading(true)
                        .build()
        );

        return ShowcaseSection.create()
                .title("Loading 加载状态")
                .description("loading(true) 自动禁用按钮（防止重复提交），常配合自定义 loadingIcon 使用")
                .demo(row)
                .code("""
                        Button btn = ButtonAnt.create("提交中...")
                            .type(ButtonAnt.Type.PRIMARY)
                            .loading(true)
                            .build();
                        // 实际使用：异步任务完成后用 ButtonAnt 重建按钮回到 normal 态，
                        // 或直接 setDisable(false) + setText("提交")
                        """)
                .build();
    }
}
