package org.openkawu.jfxium.component.control;

import javafx.scene.control.Pagination;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium 分页组件（M19.50 重构）— 包装 JavaFX {@link Pagination}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：页码导航控件，用于对大量数据分页展示。对标 Ant Design Pagination。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置总页数（{@link #pageCount(int)}）</li>
 *   <li>设置当前页（{@link #currentPage(int)}，从 0 开始）</li>
 *   <li>设置最大页码指示器数量（{@link #maxPageIndicatorCount(int)}）</li>
 *   <li>页码变化回调（{@link #onChange(Consumer)}）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>列表页底部分页导航（配合 TableAnt）</li>
 *   <li>图片浏览分页</li>
 *   <li>内容分步展示</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * Pagination pagination = PaginationAnt.create()
 *     .pageCount(10)
 *     .currentPage(0)
 *     .maxPageIndicatorCount(7)
 *     .onChange(page -> System.out.println("切换到第 " + (page + 1) + " 页"))
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class TablePagination extends PaginationAnt {
 *     public TablePagination() {
 *         pageCount(10);
 *         currentPage(0);
 *         onChange(page -> reloadTable(page));
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link Pagination} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class PaginationAnt extends Pagination
        implements LayoutCommon<PaginationAnt>, DisabledSupport<PaginationAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（默认 1 页）。 */
    public static PaginationAnt create() {
        return new PaginationAnt();
    }

    /** 工厂入口（带总页数）。 */
    public static PaginationAnt create(int pageCount) {
        return new PaginationAnt(pageCount);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public PaginationAnt() {
        super();
        setPageCount(1);
        init();
    }

    public PaginationAnt(int pageCount) {
        super(TextUtils.ensureAtLeastOne(pageCount));
        init();
    }

    public PaginationAnt(int pageCount, int currentPageIndex) {
        super(TextUtils.ensureAtLeastOne(pageCount), Math.max(0, currentPageIndex));
        init();
    }

    // ============================================================
    // Controller —— 运行时状态变更入口（M19.50+）
    // ============================================================

    /** {@link #controllerOf(Pagination)} 查找 key，挂载在 {@link #getProperties()}。 */
    private static final String PROPERTY_KEY = "jfxium.pagination.controller";

    /**
     * 从已构建的 Pagination 节点反向拿到 Controller。
     *
     * <p><b>宽容契约</b>：null / 非 PaginationAnt 构建 / 已被覆盖 → 返回 null，不抛异常。
     * 这与 MenuAnt 的严格契约不同 —— 因为 PaginationAnt 继承自 JavaFX {@link Pagination}，
     * 业务方可能用 {@code Pagination p = new PaginationAnt() ...} 多态持有，
     * 必须保证误用不崩。</p>
     *
     * <h3>典型用法</h3>
     * <pre>{@code
     * PaginationAnt p = PaginationAnt.create(20).onChange(idx -> reload(idx)).build();
     * PaginationAnt.Controller ctrl = PaginationAnt.controllerOf(p);
     * ctrl.next();          // 翻到下一页
     * ctrl.first();         // 首页
     * ctrl.setPageCount(50);// 异步加载后扩充总页数
     * }</pre>
     */
    public static Controller controllerOf(Pagination pagination) {
        if (pagination == null) return null;
        Object o = pagination.getProperties().get(PROPERTY_KEY);
        return o instanceof Controller c ? c : null;
    }

    /**
     * PaginationAnt 运行时状态变更 Controller。
     *
     * <p>所有翻页 / 修改总页数操作都通过 Controller 暴露，避免业务方直接
     * {@code cast children / 重新 build} 这类破坏封装的做法。</p>
     */
    public static class Controller {
        private final PaginationAnt self;

        Controller(PaginationAnt self) {
            this.self = self;
        }

        // ==================== 读 ====================

        /** 当前页索引（从 0 开始）。 */
        public int getCurrentPage() { return self.getCurrentPageIndex(); }

        /** 总页数。 */
        public int getPageCount() { return self.getPageCount(); }

        /** 当前是否在首页。 */
        public boolean isFirst() { return self.getCurrentPageIndex() == 0; }

        /** 当前是否在末页。 */
        public boolean isLast() { return self.getCurrentPageIndex() >= self.getPageCount() - 1; }

        // ==================== 写 ====================

        /**
         * 切换到指定页（newIndex 从 0 开始）。
         *
         * <p>越界 / 与当前页相同 → 直接返回 false，不触发 onChange。
         * 其余情况走 JavaFX 原生 {@link Pagination#setCurrentPageIndex(int)}，
         * <b>会</b>触发 {@link #onChange(java.util.function.Consumer)} 监听器
         * （与原生行为一致）。</p>
         *
         * @return true 表示真的切换了；false 表示被钳制 / 同页短路
         */
        public boolean setCurrentPage(int newIndex) {
            if (newIndex < 0 || newIndex >= self.getPageCount()) return false;
            if (newIndex == self.getCurrentPageIndex()) return false;
            self.setCurrentPageIndex(newIndex);
            return true;
        }

        /** 下一页。已在末页 → false。 */
        public boolean next() { return setCurrentPage(self.getCurrentPageIndex() + 1); }

        /** 上一页。已在首页 → false。 */
        public boolean prev() { return setCurrentPage(self.getCurrentPageIndex() - 1); }

        /** 首页。已在首页 → false。 */
        public boolean first() { return setCurrentPage(0); }

        /** 末页。已在末页 → false。 */
        public boolean last() { return setCurrentPage(self.getPageCount() - 1); }

        /**
         * 动态修改总页数。count &lt; 1 自动钳制为 1（与 {@link #pageCount(int)} 行为一致）。
         *
         * <p><b>注意</b>：如果新的 pageCount 小于当前页索引，JavaFX 原生
         * {@link Pagination#setPageCount(int)} 会自动把当前页钳制到最后一页。</p>
         *
         * @return 实际生效的总页数
         */
        public int setPageCount(int count) {
            return self.pageCount(count).getPageCount();
        }
    }

    private void init() {
        getStyleClass().add(JfxStyles.PAGINATION);
        // 把 Controller 挂到 properties，支持 PaginationAnt.controllerOf(this) 静态查找
        // init() 在所有 3 个构造函数中都调用，覆盖 create() / new PaginationAnt() / 继承式 3 种入口
        getProperties().put(PROPERTY_KEY, new Controller(this));
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /**
     * 设置总页数。count &lt; 1 自动钳制为 1。
     */
    public PaginationAnt pageCount(int pageCount) {
        setPageCount(TextUtils.ensureAtLeastOne(pageCount));
        return this;
    }

    /**
     * 设置当前页索引（从 0 开始）。负数钳制为 0。
     */
    public PaginationAnt currentPage(int currentPageIndex) {
        setCurrentPageIndex(Math.max(0, currentPageIndex));
        return this;
    }

    /**
     * 设置最大页码指示器数量。
     */
    public PaginationAnt maxPageIndicatorCount(int count) {
        setMaxPageIndicatorCount(count);
        return this;
    }

    /**
     * 页码变化回调（newIndex 从 0 开始）。
     */
    public PaginationAnt onChange(Consumer<Integer> handler) {
        if (handler != null) {
            currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
                handler.accept(newVal.intValue());
            });
        }
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 构建
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>PaginationAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public PaginationAnt build() {
        return this;
    }
}