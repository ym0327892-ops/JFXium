package org.openkawu.jfxium.jfxiumUiExample.view;

import javafx.scene.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.openkawu.jfxium.component.composite.MenuAnt;

/**
 * 极简路由注册表 —— key → 懒加载页面工厂。
 *
 * <p><b>分类对齐 Ant Design</b>：使用 {@link Category} 枚举把所有示例页归入 6 大类
 * （通用 / 布局 / 导航 / 数据录入 / 数据展示 / 反馈），跟 antd 文档侧栏一致，
 * 用户找组件时不需要重新建立心智模型。</p>
 *
 * <p><b>为什么用 Supplier 而不是 Node 实例</b>：</p>
 * <ul>
 *   <li>页面 Node 用一次就丢，避免长期持有数据 / listener 导致泄漏</li>
 *   <li>每次切换重新构造，保证状态干净（路由切回来不会"看见上次的滚动位置"）</li>
 *   <li>启动开销分摊到使用时，冷启动更快</li>
 * </ul>
 *
 * <p><b>非线程安全</b>：所有调用都在 FX Thread 内。</p>
 */
public class PageRegistry {

    /**
     * Ant Design 风格的组件大类。声明顺序决定菜单展示顺序。
     */
    public enum Category {
        GENERAL("通用"),
        LAYOUT("布局"),
        TEMPLATE("工程模板"),
        NAVIGATION("导航"),
        DATA_ENTRY("数据录入"),
        DATA_DISPLAY("数据展示"),
        FEEDBACK("反馈");

        private final String label;
        Category(String label) { this.label = label; }
        public String label() { return label; }
    }

    /** key 顺序保留 → 让 build menu 能按注册顺序生成菜单。 */
    private final Map<String, Entry> entries = new LinkedHashMap<>();

    /**
     * 注册一个页面。
     *
     * @param key      唯一路由 key（同时作为 MenuAnt item key）
     * @param title    菜单显示名
     * @param category 组件大类（null 表示顶层非分类项，如首页）
     * @param factory  页面工厂——每次切到该路由时调用一次
     */
    public PageRegistry register(String key, String title, Category category, Supplier<Node> factory) {
        entries.put(key, new Entry(key, title, category, factory));
        return this;
    }

    /** 获取所有条目（保持注册顺序）。 */
    public Map<String, Entry> entries() {
        return entries;
    }

    /** 构建指定 key 的页面 Node；key 不存在返回 null。 */
    public Node build(String key) {
        Entry e = entries.get(key);
        return e == null ? null : e.factory().get();
    }

    /**
     * 路由项 —— 演进时如需加 icon 等字段，扩这个 record 即可。
     */
    public record Entry(String key, String title, Category category, Supplier<Node> factory) {}
}
