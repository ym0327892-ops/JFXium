package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.openkawu.jfxium.component.composite.DescriptionsAnt;
import org.openkawu.jfxium.component.composite.TitleBarAnt;
import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.CheckBoxAnt;
import org.openkawu.jfxium.component.control.ToolBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.BorderPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.IconPath;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * TitleBarAnt / WindowControlAnt —— Win11 自绘窗口外壳示例。
 *
 * <p>本页回答两个问题：**长什么样**（三种状态的静态预览）与
 * **真的能用吗**（打开真实无边框窗口，拖动 / 双击最大化 / 关闭拦截全部实跑）。</p>
 *
 * <p>配色与尺寸全部来自主题令牌，换主题只换设计语言：Ant 系主题下关闭按钮悬停是 Ant 红，
 * Fluent 下是 WinUI 危险色；紧凑密度下按钮从 46×32 收到 40×28。</p>
 */
public class WindowChromeExamplePage extends VBoxAnt {

    public WindowChromeExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TitleBar / WindowControl 窗口外壳")
                .description("自绘无边框窗口（StageStyle.UNDECORATED）的标题栏与窗口按钮。"
                        + "拖动移动、双击最大化/还原、关闭走标准 WINDOW_CLOSE_REQUEST（应用可拦截）；"
                        + "尺寸与配色走主题令牌，Ant / Fluent 系与紧凑密度自动适配。")
                .sections(
                        liveSection(),
                        statesSection(),
                        slotsSection(),
                        tokenSection()
                )
                .padding(24)
                .build());
    }

    // ============================================================
    // 1. 真实窗口
    // ============================================================

    private Node liveSection() {
        BooleanProperty veto = new SimpleBooleanProperty(false);

        Node open = ButtonAnt.create("打开无边框窗口")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> openWindow(false, veto))
                .build();
        Node openFixed = ButtonAnt.create("打开固定尺寸窗口（无最大化）")
                .onClick(e -> openWindow(true, veto))
                .build();
        Node vetoBox = CheckBoxAnt.create("拦截关闭（模拟未保存修改）")
                .bindValue(veto)
                .build();

        String code = """
                Stage stage = new Stage(StageStyle.UNDECORATED);

                TitleBarAnt bar = TitleBarAnt.create()
                    .icon(IconPath.create(IconPath.FOLDER))
                    .title("我的应用")
                    .stage(stage)           // 拖动 + 双击最大化 + 三个窗口按钮
                    .build();

                // 固定尺寸窗口：关掉最大化，并让 Stage 不可缩放
                stage.setResizable(false);
                bar.controls().maximizeVisible(false);

                // 关闭可被应用拦截（关闭按钮同样走这条路径）
                stage.setOnCloseRequest(e -> {
                    if (hasUnsavedChanges() && !confirmDiscard()) e.consume();
                });

                BorderPaneAnt root = BorderPaneAnt.create();
                root.setTop(bar);
                root.setCenter(content);
                stage.setScene(new Scene(root, 720, 420));
                stage.show();
                """;

        return Demos.sectionWithCode("1. 真实窗口（可拖动 / 最大化 / 关闭）",
                "打开窗口后：拖标题栏移动、双击标题栏最大化/还原、右上角按钮生效。"
                        + "勾选「拦截关闭」后再点关闭 —— 窗口会被留下（consume 掉 WINDOW_CLOSE_REQUEST）。",
                code,
                Demos.column(
                        Demos.row(open, openFixed, vetoBox)));
    }

    /** 打开演示窗口；{@code veto} 为 true 时消费关闭请求。 */
    private void openWindow(boolean fixedSize, BooleanProperty veto) {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        if (getScene() != null && getScene().getWindow() != null) {
            stage.initOwner(getScene().getWindow());
        }

        if (fixedSize) {
            stage.setResizable(false);
        }

        TitleBarAnt bar = TitleBarAnt.create()
                .icon(IconPath.create(IconPath.FOLDER))
                .title(fixedSize ? "JFXium — 固定尺寸窗口" : "JFXium — 无边框窗口")
                .left(HBarAnt.create()
                        .left(ButtonAnt.create("新建").type(ButtonAnt.Type.TEXT).build(),
                                ButtonAnt.create("保存").type(ButtonAnt.Type.TEXT).build())
                        .build())
                .right(TagAnt.create("张工").build())
                .stage(stage)
                .build();
        bar.controls().maximizeVisible(!fixedSize);

        Label hint = TypographyAnt.text(fixedSize
                        ? "固定尺寸：最大化按钮已隐藏，Stage 不可缩放"
                        : "拖动标题栏移动 · 双击标题栏最大化/还原")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        VBoxAnt content = VBoxAnt.create()
                .padding(24)
                .spacing(12)
                .background(Background.LAYOUT)
                .children(
                        TypographyAnt.text("窗口内容区").build(),
                        hint,
                        ButtonAnt.create("关闭")
                                .onClick(e -> stage.close())
                                .build());

        stage.setOnCloseRequest(e -> {
            if (veto.get()) {
                e.consume();
                hint.setText("关闭已被拦截：勾选了「拦截关闭」（未保存修改场景）");
            }
        });

        BorderPaneAnt root = BorderPaneAnt.create();
        root.setTop(bar);
        root.setCenter(content);

        Scene scene = new Scene(root, fixedSize ? 520 : 720, fixedSize ? 320 : 420);
        ThemeManager.getInstance().registerScene(scene);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    // ============================================================
    // 2. 三种状态预览（未绑定 Stage，只画外观）
    // ============================================================

    private Node statesSection() {
        Node normal = preview("普通窗口", "最大化按钮显示方框字形", false, true);
        Node maximized = preview("已最大化", "字形切为「还原」叠框，可访问名字同步变「还原」", true, true);
        Node fixed = preview("固定尺寸", "隐藏最大化按钮，不占位", false, false);

        return Demos.section("2. 状态预览",
                "三块都是真实 TitleBarAnt / WindowControlAnt，只是没绑定 Stage —— "
                        + "拖不动、点不动（按钮无目标窗口），用来核对不同状态下的字形与布局。",
                Demos.column(normal, maximized, fixed));
    }

    private Node preview(String title, String note, boolean maximized, boolean showMaximize) {
        TitleBarAnt bar = TitleBarAnt.create()
                .icon(IconPath.create(IconPath.FOLDER))
                .title(title)
                .build();
        bar.controls().maximized(maximized).maximizeVisible(showMaximize);

        VBoxAnt shell = VBoxAnt.create()
                .spacing(0)
                .children(
                        bar,
                        VBoxAnt.create()
                                .padding(12)
                                .children(TypographyAnt.text(note)
                                        .type(TypographyAnt.TextColor.SECONDARY)
                                        .build())
                                .build());
        shell.getStyleClass().add("jfx-demo-window-shell");
        shell.prefW(420).maxW(420);
        return shell;
    }

    // ============================================================
    // 3. 三段式插槽：left / center / right 可放任意组件
    // ============================================================

    private Node slotsSection() {
        // left = HBarAnt（文本操作）
        Node leftBar = previewWithBar(
                "left = HBarAnt",
                TitleBarAnt.create()
                        .title("我的应用")
                        .left(HBarAnt.create()
                                .left(ButtonAnt.create("新建").type(ButtonAnt.Type.TEXT).build(),
                                        ButtonAnt.create("打开").type(ButtonAnt.Type.TEXT).build(),
                                        ButtonAnt.create("保存").type(ButtonAnt.Type.TEXT).build())
                                .build())
                        .right(TagAnt.create("张工").build())
                        .build(),
                "标题右侧塞一整条 HBarAnt，窗口按钮仍贴最右");

        // left = ToolBarAnt（图标工具栏，注意把垂直 padding 压到 0 以适配 32px 栏高）
        Node toolBar = previewWithBar(
                "left = ToolBarAnt",
                TitleBarAnt.create()
                        .title("编辑器")
                        .left(ToolBarAnt.create()
                                .button(IconPath.create(IconPath.FOLDER), "打开工程", () -> { })
                                .button(IconPath.create(IconPath.STAR), "收藏", () -> { })
                                .padding(0, 4, 0, 4)
                                .build())
                        .build(),
                "ToolBarAnt 默认垂直 padding 是按普通工具栏算的，塞进 32px 标题栏需 padding(0,4,0,4)");

        // center = SegmentedAnt（真正居中的视图切换）
        Node centered = previewWithBar(
                "center = SegmentedAnt",
                TitleBarAnt.create()
                        .left(ButtonAnt.create("文件").type(ButtonAnt.Type.TEXT).build())
                        .center(SegmentedAnt.create()
                                .size(Size.SMALL)
                                .option("edit", "编辑")
                                .option("run", "运行")
                                .build())
                        .build(),
                "中段左右各一个弹性空隙；标题留空后左侧完全交给 left(...)");

        String code = """
                // left：标题右侧的操作区（ToolBarAnt / HBarAnt / 搜索框…）
                TitleBarAnt.create()
                    .title("我的应用")
                    .left(HBarAnt.create()
                            .left(ButtonAnt.create("新建").type(ButtonAnt.Type.TEXT).build(),
                                  ButtonAnt.create("保存").type(ButtonAnt.Type.TEXT).build())
                            .build())
                    // center：真正居中（左右两侧宽度相当时精确居中）
                    .center(SegmentedAnt.create().size(Size.SMALL)
                            .option("edit", "编辑").option("run", "运行").build())
                    // right：窗口按钮之前的右区
                    .right(TagAnt.create("张工").build())
                    .stage(stage)
                    .build();
                """;

        return Demos.sectionWithCode("3. 三段式插槽（left / center / right）",
                "标题栏本质是条状容器：left 放工具栏，center 放标签/视图切换，right 放账号；"
                        + "窗口按钮组永远最右贴边。标题不想要就不调 title()（空标题不占位）。",
                code,
                Demos.column(leftBar, toolBar, centered));
    }

    /** 预览用：把标题栏 + 说明装进一个「假窗口」壳。 */
    private Node previewWithBar(String caption, TitleBarAnt bar, String note) {
        VBoxAnt shell = VBoxAnt.create()
                .spacing(0)
                .children(
                        bar,
                        VBoxAnt.create()
                                .padding(12)
                                .spacing(4)
                                .children(
                                        TypographyAnt.text(caption).build(),
                                        TypographyAnt.text(note)
                                                .type(TypographyAnt.TextColor.SECONDARY)
                                                .build())
                                .build());
        shell.getStyleClass().add("jfx-demo-window-shell");
        shell.prefW(560).maxW(560);
        return shell;
    }

    // ============================================================
    // 3. 令牌与行为
    // ============================================================

    private Node tokenSection() {
        return Demos.section("4. 令牌与行为口径",
                "尺寸走 variables-base 的 @title-bar-height / @window-control-button-*，"
                        + "颜色走语义槽（不写死任何 rgba），所以换主题即换观感。",
                DescriptionsAnt.create()
                        .title("WindowControlAnt / TitleBarAnt")
                        .column(1)
                        .bordered()
                        .item("按钮尺寸",
                                "46 × @control-height（默认 46×32）；紧凑密度按 46:32 同比例收窄为 40×28")
                        .item("字形",
                                "WinUI 3 caption glyph，2048 网格缩放 10/2048：最小化 10×1，其余 10×10")
                        .item("悬停 / 按压",
                                "前景色 6% / 4% 叠加（WinUI SubtleFillColorSecondary/Tertiary 同构）")
                        .item("关闭悬停",
                                "主题危险色 + 对偶留白色：Ant 系为 Ant 红，Fluent 亮色 #c50f1f，"
                                        + "Fluent 暗色为提亮粉 + 黑字")
                        .item("三段式插槽",
                                "left / center / right 与 HBarAnt 同语义；标题为空时不占位，"
                                        + "窗口按钮组始终最右贴边")
                        .item("关闭语义",
                                "发 WINDOW_CLOSE_REQUEST → 未消费才 hide()；应用可在 setOnCloseRequest 里拦截")
                        .item("最大化同步",
                                "监听 Stage.maximizedProperty：双击标题栏 / 系统菜单 / setMaximized 都会同步字形与可访问名字")
                        .item("与 WinUI 的差异",
                                "标题栏按钮可 Tab 聚焦（自绘外壳里不可聚焦会让键盘/读屏用户无法关窗），配双色焦点环")
                        .item("未实现",
                                "最大化时拖动不会先还原再跟手；标题文字不自动跟随 Stage.title")
                        .build());
    }
}
