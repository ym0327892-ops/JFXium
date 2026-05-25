package org.openkawu.jfxium.demo.admin;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.demo.admin.pages.AdminPage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Admin 模块的简易页面路由器。
 *
 * <p>设计取舍（Karpathy #2 极简至上）：
 * 不引入 URL/Hash/历史栈等复杂概念，只做最朴素的"key → page → 切换 view"。
 * 后续真要加（前进/后退/参数传递）再扩展。</p>
 *
 * <p>用法：</p>
 * <pre>{@code
 * Router router = new Router();
 * router.register(new DashboardPage());
 * router.register(new UserListPage());
 * router.go("user.list");           // 切换到用户列表
 * Node view = router.getOutlet();    // 把这个 outlet 放到 AppShell.content
 * }</pre>
 */
public class Router {

    /** 注册的所有页面，按注册顺序保留（用于侧边菜单生成）。 */
    private final Map<String, AdminPage> pages = new LinkedHashMap<>();

    /** 内容挂载点：永远是这个 StackPane，内部 children 切换。 */
    private final StackPane outlet = new StackPane();

    /** 当前激活页 key（null 表示尚未导航）。 */
    private String currentKey;

    /** 切换前钩子（接 page key）。常用于更新菜单选中态、打日志等。 */
    private java.util.function.Consumer<String> onChange;

    public Router() {
        outlet.getStyleClass().add("admin-outlet");
    }

    /** 注册一个页面。重复 key 会覆盖。 */
    public Router register(AdminPage page) {
        pages.put(page.key(), page);
        return this;
    }

    /** 监听路由切换。 */
    public Router onChange(java.util.function.Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /** 切换到指定 key 的页面。key 不存在则忽略并打日志。 */
    public void go(String key) {
        AdminPage page = pages.get(key);
        if (page == null) {
            System.err.println("[Router] 未注册的页面 key: " + key);
            return;
        }
        Node view = page.getView();
        outlet.getChildren().setAll(view);
        currentKey = key;
        if (onChange != null) onChange.accept(key);
    }

    /** 返回内容挂载节点。把这个塞进 AppShell.content。 */
    public StackPane getOutlet() {
        return outlet;
    }

    public String currentKey() { return currentKey; }

    /** 按注册顺序返回所有页面（生成菜单用）。 */
    public java.util.Collection<AdminPage> pages() {
        return pages.values();
    }
}
