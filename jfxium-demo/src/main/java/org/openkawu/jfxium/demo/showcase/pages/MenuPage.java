package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.MenuAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * MenuAnt 组件展示页（M14）。
 *
 * <p>覆盖：</p>
 * <ul>
 *   <li>INLINE 默认竖向（带子菜单嵌套）</li>
 *   <li>INLINE + 选中态（左侧主题色竖线）</li>
 *   <li>HORIZONTAL 顶导航（子菜单 Popup 下拉）</li>
 *   <li>HORIZONTAL + 选中态（底部主题色横线）</li>
 *   <li>多级嵌套菜单（subMenu 嵌套 subMenu）</li>
 * </ul>
 */
public class MenuPage implements ShowcasePage {

    @Override public String   key()      { return "menu"; }
    @Override public String   title()    { return "Menu 菜单"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Menu 菜单");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("支持 INLINE（侧栏）/ HORIZONTAL（顶导航）两种模式 + 选中态 + 多级嵌套（M14）");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create()
                .spacing(8)
                .children(pageTitle, pageDesc)
                .build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionInline(),
                        sectionInlineSelected(),
                        sectionHorizontal(),
                        sectionHorizontalSelected(),
                        sectionDark(),
                        sectionCollapsed(),
                        sectionDeepNested()
                )
                .build();
    }

    // ============================================================
    // 1. INLINE 基础（侧栏典型）
    // ============================================================
    private Node sectionInline() {
        Pane menu = MenuAnt.create()
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), () -> System.out.println("首页"))
                .item("users",     "用户管理", IconAnt.path(IconAnt.Path.USERS),     () -> System.out.println("用户管理"))
                .item("settings",  "设置",     IconAnt.path(IconAnt.Path.SETTINGS),  () -> System.out.println("设置"))
                .divider()
                .item("logout",    "退出登录", IconAnt.path(IconAnt.Path.LOGOUT),    () -> System.out.println("退出"))
                .build();
        menu.setMaxWidth(240);
        menu.setPrefWidth(240);

        return ShowcaseSection.create()
                .title("INLINE 侧栏菜单（默认）")
                .description("竖向布局，admin 侧栏典型用法。点击 item 触发 onClick 回调。")
                .demo(menu)
                .code("""
                        Pane menu = MenuAnt.create()
                            .item("dashboard", "首页",     icon1, () -> ...)
                            .item("users",     "用户管理", icon2, () -> ...)
                            .item("settings",  "设置",     icon3, () -> ...)
                            .divider()
                            .item("logout",    "退出登录", icon4, () -> ...)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. INLINE + 选中态
    // ============================================================
    private Node sectionInlineSelected() {
        Pane menu = MenuAnt.create()
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), null)
                .item("users",     "用户管理", IconAnt.path(IconAnt.Path.USERS),     null)
                .item("settings",  "设置",     IconAnt.path(IconAnt.Path.SETTINGS),  null)
                .selectedKey("users")                              // 用户管理为选中态
                .onSelect(key -> System.out.println("切到：" + key))
                .build();
        menu.setMaxWidth(240);
        menu.setPrefWidth(240);

        return ShowcaseSection.create()
                .title("INLINE + 选中态")
                .description("selectedKey(\"users\") 高亮当前页，左侧 3px 主题色竖线。点击其它项触发 onSelect。")
                .demo(menu)
                .code("""
                        Pane menu = MenuAnt.create()
                            .item("dashboard", "首页",     icon1, null)
                            .item("users",     "用户管理", icon2, null)
                            .item("settings",  "设置",     icon3, null)
                            .selectedKey("users")            // 当前选中的 key
                            .onSelect(key -> router.go(key)) // 切换回调
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 3. HORIZONTAL 顶导航
    // ============================================================
    private Node sectionHorizontal() {
        Pane menu = MenuAnt.create()
                .mode(MenuAnt.Mode.HORIZONTAL)
                .item("home",     "首页",     () -> System.out.println("首页"))
                .subMenu("products", "产品中心")
                    .item("p1", "产品 A", () -> System.out.println("产品 A"))
                    .item("p2", "产品 B", () -> System.out.println("产品 B"))
                    .item("p3", "产品 C", () -> System.out.println("产品 C"))
                    .endSubMenu()
                .subMenu("solutions", "解决方案")
                    .item("s1", "金融行业", () -> System.out.println("金融"))
                    .item("s2", "电商行业", () -> System.out.println("电商"))
                    .endSubMenu()
                .item("about",    "关于我们", () -> System.out.println("关于"))
                .item("contact",  "联系我们", () -> System.out.println("联系"))
                .build();

        return ShowcaseSection.create()
                .title("HORIZONTAL 顶导航")
                .description("横向布局，顶部导航栏典型用法。subMenu 点击后从下方 Popup 弹出子菜单。")
                .demo(menu)
                .code("""
                        Pane topNav = MenuAnt.create()
                            .mode(MenuAnt.Mode.HORIZONTAL)
                            .item("home", "首页", () -> ...)
                            .subMenu("products", "产品中心")
                                .item("p1", "产品 A", () -> ...)
                                .item("p2", "产品 B", () -> ...)
                                .endSubMenu()
                            .item("about", "关于我们", () -> ...)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 4. HORIZONTAL + 选中态
    // ============================================================
    private Node sectionHorizontalSelected() {
        Pane menu = MenuAnt.create()
                .mode(MenuAnt.Mode.HORIZONTAL)
                .item("home",     "首页",     null)
                .item("products", "产品中心", null)
                .item("about",    "关于我们", null)
                .item("contact",  "联系我们", null)
                .selectedKey("products")
                .onSelect(key -> System.out.println("切到：" + key))
                .build();

        return ShowcaseSection.create()
                .title("HORIZONTAL + 选中态")
                .description("当前页底部 3px 主题色横线。hover 时也有底线（更淡的主题色）。")
                .demo(menu)
                .code("""
                        Pane topNav = MenuAnt.create()
                            .mode(MenuAnt.Mode.HORIZONTAL)
                            .item("home",     "首页",     null)
                            .item("products", "产品中心", null)
                            .item("about",    "关于我们", null)
                            .selectedKey("products")
                            .onSelect(key -> router.go(key))
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. DARK 主题（M14.3）
    // ============================================================
    private Node sectionDark() {
        Pane menu = MenuAnt.create()
                .theme(MenuAnt.Theme.DARK)
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), null)
                .item("users",     "用户管理", IconAnt.path(IconAnt.Path.USERS),     null)
                .item("settings",  "设置",     IconAnt.path(IconAnt.Path.SETTINGS),  null)
                .divider()
                .item("logout",    "退出登录", IconAnt.path(IconAnt.Path.LOGOUT),    null)
                .selectedKey("users")
                .build();
        menu.setMaxWidth(240);
        menu.setPrefWidth(240);
        menu.setMinHeight(280);

        return ShowcaseSection.create()
                .title("DARK 主题（M14.3）")
                .description("admin 经典深色侧栏风格。背景 #001529，选中态用主题色填充。")
                .demo(menu)
                .code("""
                        Pane menu = MenuAnt.create()
                            .theme(MenuAnt.Theme.DARK)             // 关键
                            .item("dashboard", "首页",     icon1, null)
                            .item("users",     "用户管理", icon2, null)
                            ...
                            .selectedKey("users")
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 6. 折叠模式（M14.4）
    // ============================================================
    private Node sectionCollapsed() {
        Pane menu = MenuAnt.create()
                .collapsed(true)                                   // 折叠为 64px 宽，只显示图标
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), null)
                .item("users",     "用户管理", IconAnt.path(IconAnt.Path.USERS),     null)
                .subMenu("system", "系统", IconAnt.path(IconAnt.Path.SETTINGS))
                    .item("system.config", "全局配置", () -> System.out.println("配置"))
                    .item("system.log",    "系统日志", () -> System.out.println("日志"))
                    .endSubMenu()
                .divider()
                .item("logout",    "退出登录", IconAnt.path(IconAnt.Path.LOGOUT),    null)
                .selectedKey("users")
                .build();
        menu.setMinHeight(280);

        // 折叠 + DARK 叠加版
        Pane menuDark = MenuAnt.create()
                .collapsed(true)
                .theme(MenuAnt.Theme.DARK)
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), null)
                .item("users",     "用户管理", IconAnt.path(IconAnt.Path.USERS),     null)
                .subMenu("system", "系统", IconAnt.path(IconAnt.Path.SETTINGS))
                    .item("system.config", "全局配置", () -> System.out.println("配置"))
                    .item("system.log",    "系统日志", () -> System.out.println("日志"))
                    .endSubMenu()
                .item("logout",    "退出登录", IconAnt.path(IconAnt.Path.LOGOUT),    null)
                .selectedKey("users")
                .build();
        menuDark.setMinHeight(280);

        HBox demos = new HBox(24);
        demos.getChildren().addAll(menu, menuDark);

        return ShowcaseSection.create()
                .title("折叠模式（M14.4，仅 INLINE 有效）")
                .description("collapsed(true) 收窄为 64px 仅显示图标；subMenu 改用 Popup 从右侧弹出。可与 DARK 主题叠加。")
                .demo(demos)
                .code("""
                        Pane menu = MenuAnt.create()
                            .collapsed(true)             // 折叠为 64px 宽
                            .item("dashboard", "首页",     icon1, null)
                            .item("users",     "用户管理", icon2, null)
                            .subMenu("system", "系统", icon3)
                                .item("system.config", "全局配置", () -> ...)
                                .item("system.log",    "系统日志", () -> ...)
                                .endSubMenu()
                            .selectedKey("users")
                            .build();

                        // 折叠 + DARK 叠加：admin 经典样式
                        Pane menu = MenuAnt.create()
                            .collapsed(true)
                            .theme(MenuAnt.Theme.DARK)
                            .item(...)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 7. 多级嵌套
    // ============================================================
    private Node sectionDeepNested() {
        Pane menu = MenuAnt.create()
                .group("常用")
                .item("dashboard", "首页", IconAnt.path(IconAnt.Path.DASHBOARD), null)
                .divider()
                .group("业务")
                .subMenu("user", "用户管理", IconAnt.path(IconAnt.Path.USERS))
                    .item("user.list", "用户列表", () -> System.out.println("列表"))
                    .item("user.add",  "新增用户", () -> System.out.println("新增"))
                    .subMenu("user.role", "角色管理")
                        .item("user.role.list", "角色列表", () -> System.out.println("角色列表"))
                        .item("user.role.perm", "权限分配", () -> System.out.println("权限"))
                        .endSubMenu()
                    .endSubMenu()
                .subMenu("system", "系统设置", IconAnt.path(IconAnt.Path.SETTINGS))
                    .item("system.config", "全局配置", () -> System.out.println("配置"))
                    .item("system.log",    "系统日志", () -> System.out.println("日志"))
                    .endSubMenu()
                .build();
        menu.setMaxWidth(280);
        menu.setPrefWidth(280);

        return ShowcaseSection.create()
                .title("多级嵌套 + 分组 + 分割线")
                .description("subMenu 可任意嵌套；group 显示分组标题；divider 显示分隔线。")
                .demo(menu)
                .code("""
                        Pane menu = MenuAnt.create()
                            .group("常用")
                            .item("dashboard", "首页", icon, null)
                            .divider()
                            .group("业务")
                            .subMenu("user", "用户管理", iconUsers)
                                .item("user.list", "用户列表", () -> ...)
                                .subMenu("user.role", "角色管理")
                                    .item("user.role.list", "角色列表", () -> ...)
                                    .item("user.role.perm", "权限分配", () -> ...)
                                    .endSubMenu()
                                .endSubMenu()
                            .build();
                        """)
                .build();
    }
}
