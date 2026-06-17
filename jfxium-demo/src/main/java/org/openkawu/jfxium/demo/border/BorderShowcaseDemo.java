package org.openkawu.jfxium.demo.border;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.theme.DarkTheme;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.MuiDarkTheme;
import org.openkawu.jfxium.core.theme.MuiLightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;

/**
 * 边线 (Border) 能力展示 Demo。
 *
 * <p>对应规范文档: {@code INTERNAL/BORDER.md}。本 Demo 演示 4 方向独立控制、
 * HBarAnt 默认行为、GroupBoxAnt.bordered 模式、嵌套规范场景。</p>
 *
 * <p><b>运行:</b> 在 IDE 跑 {@link #main(String[])},或:
 * <pre>./mvnw javafx:run -pl jfxium-demo \
 *     -Djavafx.mainClass=org.openkawu.jfxium.demo.border.BorderShowcaseDemo</pre>
 * </p>
 *
 * <p><b>扩展点(待补):</b> 3 档色 token (emphasis/default/muted/subtle) 与
 * 对应 styleClass 常量 {@code BORDER_DEFAULT / BORDER_MUTED / BORDER_SUBTLE}
 * 尚未在 JfxStyles.java 中定义;当前边线颜色由 LESS 默认 {@code -color-border-muted} 提供。
 * 详见 BORDER.md §3。</p>
 */
public class BorderShowcaseDemo extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add(JfxStyles.BG_LAYOUT);

        // 顶部工具条: 主题切换
        root.setTop(buildToolbar());

        // 中心: 5 个对比区
        ScrollPane scroll = new ScrollPane(buildShowcase());
        scroll.setFitToWidth(true);
        scroll.setPadding(new Insets(0));
        root.setCenter(scroll);

        Scene scene = new Scene(root, 1000, 760);

        scene.getStylesheets().add(getClass()
                .getResource("/org/openkawu/jfxium/jfxiumUiExample/demo.css")
                .toExternalForm());

        // [Quick Test] demo.css styleClass — 改一个数字重跑即可, 不用碰库内主题文件
        // -----------------------------------------------------------------------
        // 修复前(看不出边框):  background-radius: 4px, 3px   ← Layer 0 比基础 6px 还小, Layer 1 几乎吞掉 Layer 0
        // 修复后(本版本):      background-radius: 6px, 3px   ← Layer 0 = 基础 6px, 内缩 2px 露 2px 边
        //   - Layer 0: -color-border-default 浅灰, radius 6px (跟 .jfx-group-box 基础对齐, 圆角处无错位)
        //   - Layer 1: -color-bg-default 白,    内缩 2px,   radius 3px (差 3px 露出 2px 边)
        //   - dropshadow: 1px gaussian 外发光, 增强 2px 边框在浅灰背景上的可见性
        //   - 显式清掉原生 -fx-border, 避免双绘制
        //
        // ⚠️ 想换宽度直接改 `2px` 那个数字:
        //   - 改 1px:  边框更细, dropshadow 必须调到 2 才看得清
        //   - 改 3px:  边框更粗, 圆角差同步改 4px(insets 3px, radius 3px, 边 3px)


        ThemeManager mgr = ThemeManager.getInstance();
        mgr.registerScene(scene);
        mgr.applyTheme(new LightTheme());

        stage.setTitle("Border Showcase — JFXium");
        stage.setScene(scene);
        stage.show();
    }

    // ============================================================
    // 工具条
    // ============================================================

    private Node buildToolbar() {
        return HBarAnt.create()
            .left(
                LabelAnt.create().text(" 主题: ").build(),
                themeButton("Light", () -> ThemeManager.getInstance().applyTheme(new LightTheme())),
                themeButton("MUI Light", () -> ThemeManager.getInstance().applyTheme(new MuiLightTheme())),
                themeButton("MUI Dark",  () -> ThemeManager.getInstance().applyTheme(new MuiDarkTheme())),
                themeButton("Dark",      () -> ThemeManager.getInstance().applyTheme(new DarkTheme()))
            )
            .right(
                LabelAnt.create().text(" 提示: 各区对比边线方向 / 档位 ").build()
            )
            .gap(8)
            .padding(8, 16, 8, 16)
            .build();
    }

    private static ButtonAnt themeButton(String label, Runnable onClick) {
        return ButtonAnt.create(label)
            .type(ButtonAnt.Type.DEFAULT)
            .onClick(e -> onClick.run())
            .build();
    }

    // ============================================================
    // 主展示区
    // ============================================================

    private Node buildShowcase() {
        VBoxAnt root = VBoxAnt.create()
            .spacing(28)
            .padding(24, 24, 24, 24)
            .styleClass(JfxStyles.BG_LAYOUT)
            .children(
                sectionTitle("1. 4 方向独立控制 — borderTop / borderBottom / borderLeft / borderRight"),
                sectionDirDemo(),

                sectionTitle("2. HBarAnt 边线 4 种组合 — 默认底边线 / 顶线 / 上下双线 / 全关"),
                sectionBarDemo(),

                sectionTitle("3. GroupBoxAnt.bordered 模式 — false(默认无外框) vs true(1px 外框)"),
                sectionGroupBoxBorderedDemo(),

                sectionTitle("4. 嵌套规范 — HBarAnt 嵌进 GroupBoxAnt header(必须 .borderBottom(false))"),
                sectionNestingDemo(),

                sectionTitle("5. 嵌套层级 + 圆角接缝对比 — 左: 伪边框(.jfx-border-pseudo 修复) | 右: 原生 .bordered (有接缝)"),
                sectionLayeredDemo()
            )
            .build();
        return root;
    }

    // ============================================================
    // Section 1: 4 方向独立控制
    // ============================================================

    /**
     * 4 个小盒子,分别只开 1 条边,展示 4 方向独立。
     * 盒子大小 100x60,内部居中显示方向名。
     */
    private Node sectionDirDemo() {
        HBoxAnt row = HBoxAnt.create()
            .spacing(16)
            .align(Pos.CENTER_LEFT)
            .children(
                dirBox("borderTop",    true,  false, false, false),
                dirBox("borderBottom", false, true,  false, false),
                dirBox("borderLeft",   false, false, true,  false),
                dirBox("borderRight",  false, false, false, true),
                dirBox("ALL(4 边)",     true,  true,  true,  true),
                dirBox("NONE(无边线)", false, false, false, false)
            )
            .build();
        return wrapWithBg(row);
    }

    /**
     * 制造一个固定尺寸的小盒子,只开某条边。
     */
    private static Node dirBox(String label, boolean top, boolean bottom, boolean left, boolean right) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(110, 60);
        box.setMinSize(110, 60);
        box.getStyleClass().add(JfxStyles.BG_DEFAULT);
        // padding 推到 16,让边线和文字有空间
        box.setPadding(new Insets(6));

        Label name = new Label(label);
        name.getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
        box.getChildren().add(name);

        // 关键: 4 方向链式 API(继承自 AbstractStyleBuilder)
        // 任何一个参数为 false → 走 .borderTop(false) 关闭
        if (top)    box.getStyleClass().add(JfxStyles.BORDER_TOP);
        if (bottom) box.getStyleClass().add(JfxStyles.BORDER_BOTTOM);
        if (left)   box.getStyleClass().add(JfxStyles.BORDER_LEFT);
        if (right)  box.getStyleClass().add(JfxStyles.BORDER_RIGHT);

        return box;
    }

    // ============================================================
    // Section 2: HBarAnt 4 种边线组合
    // ============================================================

    private Node sectionBarDemo() {
        VBoxAnt col = VBoxAnt.create()
            .spacing(0)
            .children(
                // ① 默认: borderBottom(true) — HBarAnt Builder 构造里默认开启
                HBarAnt.create()
                    .left(LabelAnt.create().text(" 默认 (borderBottom=true) ").build())
                    .gap(0)
                    .padding(8, 12, 8, 12)
                    .build(),

                // ② borderTop(true)
                HBarAnt.create()
                    .left(LabelAnt.create().text(" .borderTop() ").build())
                    .gap(0)
                    .padding(8, 12, 8, 12)
                    .borderTop()
                    .build(),

                // ③ 上下双线
                HBarAnt.create()
                    .left(LabelAnt.create().text(" .borderTop() + .borderBottom() ").build())
                    .gap(0)
                    .padding(8, 12, 8, 12)
                    .borderTop()
                    .borderBottom()
                    .build(),

                // ④ 全关
                HBarAnt.create()
                    .left(LabelAnt.create().text(" .borderBottom(false) (全关) ").build())
                    .gap(0)
                    .padding(8, 12, 8, 12)
                    .borderBottom(false)
                    .build()
            )
            .build();
        return wrapWithBg(col);
    }

    // ============================================================
    // Section 3: GroupBoxAnt.bordered 对比
    // ============================================================

    private Node sectionGroupBoxBorderedDemo() {
        HBoxAnt row = HBoxAnt.create()
            .spacing(16)
            .align(Pos.TOP_LEFT)
            .children(
                GroupBoxAnt.create()
                    .title("GroupBox 不带边框")
                    .content(simpleContent("bordered=false (默认)"))
                    .build(),

                GroupBoxAnt.create()
                    .title("GroupBox 带边框")
                    .content(simpleContent("bordered=true (1px 外框)"))
                    .bordered(true)
                    .build()
            )
            .build();
        return wrapWithBg(row);
    }

    private static Node simpleContent(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
        l.setPadding(new Insets(8, 12, 8, 12));
        return l;
    }

    // ============================================================
    // Section 4: 嵌套规范 — HBarAnt 嵌进 GroupBoxAnt header
    // ============================================================

    /**
     * 演示 GroupBoxAnt.extra() 接收任意 Node 时的边线规范:
     * 嵌进去的 HBarAnt 必须 .borderBottom(false),否则与 GroupBoxAnt 的 header 自带底部分割线叠加。
     */
    private Node sectionNestingDemo() {
        HBoxAnt row = HBoxAnt.create()
            .spacing(16)
            .align(Pos.TOP_LEFT)
            .children(
                // ❌ 反例(故意保留底边线,看效果)
                GroupBoxAnt.create()
                    .title("❌ 反例: header 内 HBarAnt 未关 borderBottom")
                    .extra(
                        HBarAnt.create()
                            .left(LabelAnt.create().text(" 操作 ").build())
                            .right(ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build())
                            .gap(8)
                            .padding(2, 8, 2, 8)
                            .build()   // 留默认 borderBottom=true → 与 GroupBoxAnt header 底边线叠加
                    )
                    .content(simpleContent("反例内容区(头部会出现 2px 硬线)"))
                    .bordered(true)
                    .build(),

                // ✅ 正例
                GroupBoxAnt.create()
                    .title("✅ 正例: header 内 HBarAnt .borderBottom(false)")
                    .extra(
                        HBarAnt.create()
                            .left(LabelAnt.create().text(" 操作 ").build())
                            .right(ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build())
                            .gap(8)
                            .padding(2, 8, 2, 8)
                            .borderBottom(false)   // 关键: 关闭,避免与 header 底边线叠加
                            .build()
                    )
                    .content(simpleContent("正例内容区(头部 1px 柔和分割)"))
                    .bordered(true)
                    .build()
            )
            .build();
        return wrapWithBg(row);
    }

    // ============================================================
    // Section 5: 嵌套层级 + 圆角接缝对比
    //   左内层: 伪边框 .jfx-border-pseudo (background-stacking, 圆角无接缝)
    //   右内层: 原生 .bordered             (有圆角接缝, 缺角)
    // ============================================================

    private Node sectionLayeredDemo() {
        // ✅ 伪边框: 走 background-stacking, 圆角处无接缝
        //   demo.css 快速验证：用两层背景代替 -fx-border-*，并用 padding 占位 1px
        //   （background-insets 不占布局空间，否则有底色的 header/body 会盖住边框）
        Node pseudoBox = GroupBoxAnt.create()
            .title("✅ 伪边框(background-stacking) — 圆角无接缝")
            .content(simpleContent("两层背景: 底=边框色 顶=填充色内缩1px, 内外圆角同心无接缝"))
            .bordered(false)   // 关闭原生边框，避免与背景叠加产生双线
            .build();
        pseudoBox.getStyleClass().add("jfx-demo-border-pseudo");

        // 内层: 两个子 GroupBoxAnt(分别用不同边框实现, 用 VBoxAnt 装成一列)
        Node innerColumn = VBoxAnt.create()
            .spacing(8)
            .children(
                pseudoBox,

                // ❌ 原生 .bordered: 圆角接缝明显
                GroupBoxAnt.create()
                    .title("❌ 原生 .bordered — 圆角处有接缝(左上/右上缺角)")
                    .content(simpleContent("原生 -fx-border-* + background-radius 渲染管线分离, 4 角接缝"))
                    .bordered(true)
                    .build()
            )
            .build();

        // 外层 GroupBoxAnt.content() 接 VBoxAnt, 演示"卡片套卡片"的层级
        // 注: GroupBoxAnt.build() 返回 VBox (非 GroupBoxAnt 自身)
        Node outer = GroupBoxAnt.create()
            .title("外层卡片(原生 .bordered)")
            .content(innerColumn)
            .bordered(true)
            .build();

        return wrapWithBg(outer);
    }

    // ============================================================
    // 通用容器(给 section 加 BG_LAYOUT 背景 + 内边距)
    // ============================================================

    private static Node wrapWithBg(Node child) {
        VBox wrap = new VBox(child);
        wrap.getStyleClass().add(JfxStyles.BG_SUBTLE);
        wrap.setPadding(new Insets(16));
        return wrap;
    }

    private static Node sectionTitle(String text) {
        Label l = new Label(text);
        // 仅走 styleClass + LESS, 禁止 setStyle 写颜色/px (红线 1)
        l.getStyleClass().add(JfxStyles.TYPOGRAPHY_TITLE);
        return l;
    }

    // ============================================================
    // Entry
    // ============================================================

    public static void main(String[] args) {
        launch(args);
    }
}
