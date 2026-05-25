package org.openkawu.jfxium.demo.showcase;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.AppShellAnt;
import org.openkawu.jfxium.component.MenuAnt;
import org.openkawu.jfxium.component.SegmentedAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.ScrollPaneBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.core.theme.DarkCompactTheme;
import org.openkawu.jfxium.core.theme.DarkTheme;
import org.openkawu.jfxium.core.theme.LightCompactTheme;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.MuiTheme;
import org.openkawu.jfxium.core.theme.Theme;
import org.openkawu.jfxium.core.theme.ThemeColor;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.util.Spacers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Showcase 主框架：顶部主题工具栏 + 左侧分组菜单 + 右侧组件展示区。
 *
 * <pre>
 * ┌─────────────────────────────────────────────────────────────┐
 * │ ⬢ JFXium Showcase   spacer  [主题] [紧凑] [色板:Blue ▾] 🌗   │
 * ├──────────┬──────────────────────────────────────────────────┤
 * │  Sider   │                                                  │
 * │ ▸ 通用   │     当前组件展示区                                 │
 * │ ▸ 数据   │     - 标题                                        │
 * │   ...    │     - 多个 ShowcaseSection（变体卡片）             │
 * └──────────┴──────────────────────────────────────────────────┘
 * </pre>
 */
public class ShowcaseFrame {

    public static final double WIDTH  = 1320;
    public static final double HEIGHT = 820;

    private final Stage stage = new Stage();
    private final Map<String, ShowcasePage> pages = new LinkedHashMap<>();

    private final ScrollPane contentScroll = new ScrollPane();
    private String currentKey;
    private HBox siderContainer;
    private final ThemeManager themeManager = ThemeManager.getInstance();
    // 菜单展开状态持久化（跨 rebuildSider）
    private final java.util.Set<String> expandedCategoryKeys = new java.util.LinkedHashSet<>();
    private MenuAnt.ExpandMode menuExpandMode = MenuAnt.ExpandMode.MULTIPLE;

    public ShowcaseFrame register(ShowcasePage page) {
        pages.put(page.key(), page);
        return this;
    }

    public void show() {
        if (pages.isEmpty()) {
            throw new IllegalStateException("ShowcaseFrame 至少需要注册一个 ShowcasePage");
        }

        HBox header = buildHeader();
        VBox sider = buildSider();

        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroll.setStyle("-fx-background: -color-bg-layout; -fx-background-color: -color-bg-layout;");

        BorderPane shell = AppShellAnt.create()
                .header(header)
                .sider(sider, 240)
                .content(contentScroll)
                .build();

        if (shell.getCenter() instanceof HBox center) {
            this.siderContainer = center;
        }

        // 默认显示第一个组件
        navigateTo(pages.keySet().iterator().next());

        Scene scene = new Scene(shell, WIDTH, HEIGHT);
        themeManager.applyTheme(new LightTheme());
        themeManager.registerScene(scene);

        stage.setScene(scene);
        stage.setTitle("JFXium Showcase - 组件展示");
        stage.setMinWidth(1024);
        stage.setMinHeight(640);
        stage.centerOnScreen();
        stage.show();
    }

    /** 切换到指定组件展示页。 */
    public void navigateTo(String key) {
        ShowcasePage page = pages.get(key);
        if (page == null) return;

        VBox padded = VBoxBuilder.create()
                .padding(24, 32, 24, 32)
                .spacing(16)
                .children(page.getView())
                .build();
        padded.setStyle("-fx-background-color: -color-bg-layout;");
        contentScroll.setContent(padded);
        contentScroll.setVvalue(0);

        this.currentKey = key;
        // 同步分类展开态：让用户始终能看到自己在哪
        String catKey = findCategoryKeyOf(key);
        if (catKey != null) {
            if (menuExpandMode == MenuAnt.ExpandMode.EXCLUSIVE) {
                expandedCategoryKeys.clear();
            }
            expandedCategoryKeys.add(catKey);
        }
        rebuildSider();
    }

    /** 路由切换 / 主题切换后重建 sider，让 selectedKey 跟随当前路由。 */
    private void rebuildSider() {
        if (siderContainer == null) return;
        VBox newSider = buildSider();
        newSider.setPrefWidth(240);
        newSider.setMinWidth(240);
        newSider.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.APP_SHELL_SIDER);
        if (!siderContainer.getChildren().isEmpty()) {
            siderContainer.getChildren().set(0, newSider);
        }
    }

    // ============================================================
    // Header：logo + 主题工具栏
    // ============================================================
    private HBox buildHeader() {
        Label logo = new Label("⬢ JFXium Showcase");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        return HBoxBuilder.create()
                .spacing(12)
                .padding(8, 16, 8, 16)
                .align(Pos.CENTER_LEFT)
                .children(
                        logo,
                        Spacers.grow(),
                        buildExpandModeSwitch(),
                        buildThemeFamilySwitch(),
                        buildDensitySwitch(),
                        buildColorPicker(),
                        buildLightDarkToggle()
                )
                .build();
    }

    /** 菜单展开模式：可同时展开多个 / 手风琴互斥 */
    private HBox buildExpandModeSwitch() {
        SegmentedAnt.Builder seg = SegmentedAnt.create()
                .option("multi", "多展开")
                .option("acc", "手风琴")
                .selected(menuExpandMode == MenuAnt.ExpandMode.EXCLUSIVE ? "acc" : "multi")
                .onChange(value -> {
                    menuExpandMode = "acc".equals(value)
                            ? MenuAnt.ExpandMode.EXCLUSIVE
                            : MenuAnt.ExpandMode.MULTIPLE;
                    // 切到手风琴时只保留当前路由所在的分类展开（如有），其他收起
                    if (menuExpandMode == MenuAnt.ExpandMode.EXCLUSIVE) {
                        String currentCat = findCategoryKeyOf(currentKey);
                        expandedCategoryKeys.clear();
                        if (currentCat != null) expandedCategoryKeys.add(currentCat);
                    }
                    rebuildSider();
                });
        return seg.build();
    }

    /** 找到 pageKey 所属的分类 key（Category.name()），用于手风琴模式同步。 */
    private String findCategoryKeyOf(String pageKey) {
        if (pageKey == null) return null;
        ShowcasePage page = pages.get(pageKey);
        return page != null ? page.category().name() : null;
    }

    /** 主题家族切换：Ant Design / MUI（决定主题文件来源） */
    private HBox buildThemeFamilySwitch() {
        SegmentedAnt.Builder seg = SegmentedAnt.create()
                .option("ant", "Ant")
                .option("mui", "MUI")
                .selected(isMuiFamily() ? "mui" : "ant")
                .onChange(value -> {
                    if ("ant".equals(value)) {
                        themeManager.applyTheme(isCurrentDark() ? new DarkTheme() : new LightTheme());
                    } else {
                        themeManager.switchToMui();
                    }
                    refreshAccent();
                });
        return seg.build();
    }

    /** 紧凑度切换：默认 / 紧凑 */
    private HBox buildDensitySwitch() {
        SegmentedAnt.Builder seg = SegmentedAnt.create()
                .option("normal", "默认")
                .option("compact", "紧凑")
                .selected(themeManager.isCompact() ? "compact" : "normal")
                .onChange(value -> {
                    boolean wantCompact = "compact".equals(value);
                    if (wantCompact != themeManager.isCompact()) {
                        themeManager.toggleCompact();
                        refreshAccent();
                    }
                });
        return seg.build();
    }

    /** 主题色选择：11 种 Ant Design 预设 */
    private HBox buildColorPicker() {
        HBox box = new HBox(4);
        box.setAlignment(Pos.CENTER_LEFT);

        Label tag = new Label("主题色");
        tag.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-muted;");
        box.getChildren().add(tag);

        for (ThemeColor.Preset preset : ThemeColor.Preset.values()) {
            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(8);
            dot.setStyle(
                    "-fx-fill: " + preset.getHexColor() + ";" +
                    "-fx-cursor: hand;" +
                    "-fx-stroke: -color-border-default;" +
                    "-fx-stroke-width: 1;"
            );
            // tooltip
            javafx.scene.control.Tooltip.install(dot,
                    new javafx.scene.control.Tooltip(preset.getDisplayName()));
            dot.setOnMouseClicked(e -> themeManager.setPrimaryColor(preset));
            box.getChildren().add(dot);
        }

        return box;
    }

    /** 亮 / 暗切换 */
    private Label buildLightDarkToggle() {
        Label toggle = new Label(isCurrentDark() ? "☀" : "🌗");
        toggle.setStyle("-fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        toggle.setOnMouseClicked(e -> {
            themeManager.toggleTheme();
            toggle.setText(isCurrentDark() ? "☀" : "🌗");
            refreshAccent();
        });
        javafx.scene.control.Tooltip.install(toggle,
                new javafx.scene.control.Tooltip("切换亮 / 暗"));
        return toggle;
    }

    /** 主题家族切换 / 亮暗切换后，重新把当前 accent 应用到新主题 */
    private void refreshAccent() {
        ThemeColor cur = themeManager.getCurrentThemeColor();
        if (cur != null && cur.getHexColor() != null) {
            themeManager.setPrimaryColor(cur.getHexColor());
        }
    }

    private boolean isCurrentDark() {
        Theme t = themeManager.getCurrentTheme();
        return t != null && t.getType() == Theme.ThemeType.DARK;
    }

    private boolean isMuiFamily() {
        return "MUI".equals(themeManager.getCurrentThemeFamily());
    }

    // ============================================================
    // Sider：按 Category 分组渲染成可折叠 subMenu
    // ============================================================
    private VBox buildSider() {
        // 按 Category 顺序分组
        Map<ShowcasePage.Category, List<ShowcasePage>> grouped = new TreeMap<>();
        for (ShowcasePage page : pages.values()) {
            grouped.computeIfAbsent(page.category(), k -> new ArrayList<>()).add(page);
        }

        MenuAnt.Builder menu = MenuAnt.create()
                .expandMode(menuExpandMode)
                .expandedKeys(expandedCategoryKeys)
                .onExpandChange(keys -> {
                    expandedCategoryKeys.clear();
                    expandedCategoryKeys.addAll(keys);
                });
        for (Map.Entry<ShowcasePage.Category, List<ShowcasePage>> e : grouped.entrySet()) {
            // 每个分类一个 subMenu —— 可折叠 / 含当前 selectedKey 时自动展开
            MenuAnt.SubMenuBuilder sub = menu.subMenu(e.getKey().name(), e.getKey().getLabel());
            for (ShowcasePage p : e.getValue()) {
                String key = p.key();
                sub.item(key, p.title(), () -> navigateTo(key));
            }
            sub.endSubMenu();
        }

        if (currentKey != null) {
            menu.selectedKey(currentKey);
        }

        VBox siderMenu = (VBox) menu.build();

        ScrollPane scroll = ScrollPaneBuilder.create()
                .content(siderMenu)
                .fitToWidth(true)
                .hbarPolicy(ScrollPane.ScrollBarPolicy.NEVER)
                .vbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)
                .build();
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox container = new VBox(scroll);
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        return container;
    }
}
