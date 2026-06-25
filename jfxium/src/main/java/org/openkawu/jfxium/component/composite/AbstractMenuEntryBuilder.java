package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;

/**
 * 菜单条目 Builder 公共契约（P1-S4 抽取）——
 * 集中 {@link MenuAnt.Builder} 与 {@link MenuAnt.SubMenuBuilder} 的共有 item / subMenu /
 * group / divider 重载，消除 8+ 处重载重复。
 *
 * <h2>为何用接口而非抽象类</h2>
 * <p>Java 单继承限制让抽象基类不可行：</p>
 * <ul>
 *   <li>{@link MenuAnt.Builder} 已经 {@code extends AbstractStyleBuilder<Builder>}
 *       （为了拿到 {@code applyStyles(root)}）</li>
 *   <li>{@link MenuAnt.SubMenuBuilder} 已经 {@code extends MenuItem}
 *       （为了参与渲染协议 buildInline / buildHorizontal）</li>
 * </ul>
 * <p>二者都不能再继承一个抽象基类。改用接口 + 默认方法（Java 8+），两个子类
 * {@code implements AbstractMenuEntryBuilder<SELF>} 即可共享 item / subMenu / group /
 * divider 全部默认实现。</p>
 *
 * <h2>核心抽象</h2>
 * <p><b>子项的 level = "我"的 level + 1</b>。</p>
 * <ul>
 *   <li>Builder（myLevel=0）：顶级 item 用 level=0；当前打开 subMenu 时，子 item 用
 *       {@code currentSubMenu.level + 1}</li>
 *   <li>SubMenuBuilder（myLevel=N）：子 item 用 {@code N + 1}</li>
 * </ul>
 * <p>两种实现语义等价，统一抽象为 {@link #childLevel()} 一个钩子。</p>
 *
 * <h2>接口职责</h2>
 * <ul>
 *   <li><b>默认方法</b>：item 4 重载 / subMenu 3 重载 / group(String) / divider() —— 返回
 *       SELF 或 SubMenuBuilder（依链式需要）</li>
 *   <li><b>抽象方法</b>（子类实现）：{@link #childLevel()} / {@link #addItem(MenuAnt.MenuItem)}
 *       / {@link #subMenu(String, String, Node)}</li>
 *   <li><b>默认 self() 协变辅助</b></li>
 * </ul>
 *
 * <h2>使用模式</h2>
 * <pre>{@code
 * // 顶级 Builder
 * public static class Builder extends AbstractStyleBuilder<Builder>
 *         implements AbstractMenuEntryBuilder<Builder> {
 *     private final List<MenuItem> items = new ArrayList<>();
 *
 *     &#64;Override public int childLevel() {
 *         return currentSubMenu == null ? 0 : currentSubMenu.level + 1;
 *     }
 *
 *     &#64;Override public void addItem(MenuAnt.MenuItem item) {
 *         if (currentSubMenu != null) currentSubMenu.children.add(item);
 *         else items.add(item);
 *     }
 *
 *     &#64;Override public SubMenuBuilder subMenu(String key, String text, Node icon) {
 *         int level = currentSubMenu == null ? 0 : currentSubMenu.level + 1;
 *         SubMenuBuilder sub = new SubMenuBuilder(key, text, icon, level, this, currentSubMenu);
 *         if (currentSubMenu != null) currentSubMenu.children.add(sub);
 *         else items.add(sub);
 *         currentSubMenu = sub;
 *         return sub;
 *     }
 * }
 *
 * // 嵌套 SubMenuBuilder
 * public static class SubMenuBuilder extends MenuItem
 *         implements AbstractMenuEntryBuilder<SubMenuBuilder> {
 *
 *     &#64;Override public int childLevel() { return level + 1; }
 *
 *     &#64;Override public void addItem(MenuAnt.MenuItem item) { children.add(item); }
 *
 *     &#64;Override public SubMenuBuilder subMenu(String key, String text, Node icon) {
 *         SubMenuBuilder sub = new SubMenuBuilder(key, text, icon, level + 1, rootBuilder, this);
 *         children.add(sub);
 *         rootBuilder.currentSubMenu = sub;
 *         return sub;
 *     }
 * }
 * }</pre>
 *
 * @param <SELF> 子类自身类型（协变返回）
 * @see MenuAnt.Builder
 * @see MenuAnt.SubMenuBuilder
 */
public interface AbstractMenuEntryBuilder<SELF extends AbstractMenuEntryBuilder<SELF>> {

    // ============================================================
    // item —— 4 重载统一为默认方法，子类只需提供 childLevel() 和 addItem()
    // ============================================================

    /** 添加菜单项（无 key / 无图标）。 */
    default SELF item(String text, Runnable onClick) {
        return item(null, text, null, onClick);
    }

    /** 添加菜单项（无 key / 带图标）。 */
    default SELF item(String text, Node icon, Runnable onClick) {
        return item(null, text, icon, onClick);
    }

    /** 添加菜单项（带 key / 无图标）。 */
    default SELF item(String key, String text, Runnable onClick) {
        return item(key, text, null, onClick);
    }

    /**
     * 添加一个菜单项（叶子节点）。
     *
     * <p>level 自动计算 = {@link #childLevel()}（"我"的 level + 1）。</p>
     *
     * @param key     选中 key（{@code null} = 无 key）
     * @param text    显示文字（{@code null} 内部归一为空字符串）
     * @param icon    图标（{@code null} = 无图标）
     * @param onClick 点击回调（{@code null} = 不绑定）
     */
    default SELF item(String key, String text, Node icon, Runnable onClick) {
        MenuAnt.MenuItem mi = new MenuAnt.MenuItem(key, text, icon, onClick, childLevel());
        addItem(mi);
        return self();
    }

    // ============================================================
    // group / divider —— 默认方法统一
    // ============================================================

    /**
     * 添加分组标题（INLINE 模式渲染成 muted caption，HORIZONTAL 模式自动跳过）。
     *
     * @param title 分组标题（{@code null} 内部归一为空字符串）
     */
    default SELF group(String title) {
        addItem(new MenuAnt.MenuGroup(title, childLevel()));
        return self();
    }

    /** 添加分割线。 */
    default SELF divider() {
        addItem(new MenuAnt.MenuDivider(childLevel()));
        return self();
    }

    // ============================================================
    // subMenu —— 3 个语法糖为默认方法；4-arg 版本返回 SubMenuBuilder 必须子类实现
    // ============================================================

    /** 创建子菜单（无 key / 无图标）。 */
    default MenuAnt.SubMenuBuilder subMenu(String text) {
        return subMenu(null, text, null);
    }

    /** 创建子菜单（无 key / 带图标）。 */
    default MenuAnt.SubMenuBuilder subMenu(String text, Node icon) {
        return subMenu(null, text, icon);
    }

    /** 创建子菜单（带 key / 无图标）。 */
    default MenuAnt.SubMenuBuilder subMenu(String key, String text) {
        return subMenu(key, text, null);
    }

    /**
     * 创建一个子菜单。
     *
     * <p>返回类型固定为 {@link MenuAnt.SubMenuBuilder}（不依赖 SELF 协变），
     * 调用方拿到 SubMenuBuilder 引用以便继续给子菜单添加 item / subMenu。</p>
     *
     * @param key  选中 key（{@code null} = 无 key）
     * @param text 显示文字（{@code null} 内部归一为空字符串）
     * @param icon 图标（{@code null} = 无图标）
     */
    MenuAnt.SubMenuBuilder subMenu(String key, String text, Node icon);

    // ============================================================
    // 抽象钩子 —— 子类必须实现这 3 个方法
    // ============================================================

    /**
     * 计算"我"作为父级时，子项应当使用的 level。
     *
     * <p>规则：<b>"我"的 level + 1</b>。</p>
     * <ul>
     *   <li>Builder 实现：{@code return currentSubMenu == null ? 0 : currentSubMenu.level + 1;}</li>
     *   <li>SubMenuBuilder 实现：{@code return level + 1;}</li>
     * </ul>
     */
    int childLevel();

    /**
     * 把一个叶子节点 / 分组 / 分割线添加到合适的容器。
     * <ul>
     *   <li>Builder：若当前有打开的 subMenu，加到 subMenu.children；否则加到顶层 items</li>
     *   <li>SubMenuBuilder：直接加到自己的 children</li>
     * </ul>
     */
    void addItem(MenuAnt.MenuItem item);

    // ============================================================
    // SELF 协变辅助（默认方法）
    // ============================================================

    /**
     * 返回 SELF 引用（协变），用于链式调用。
     * <p>典型场景：{@code return self();}</p>
     */
    @SuppressWarnings("unchecked")
    default SELF self() {
        return (SELF) this;
    }
}