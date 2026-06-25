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

    private void init() {
        getStyleClass().add(JfxStyles.PAGINATION);
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