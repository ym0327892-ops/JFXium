package org.openkawu.jfxium.component.control;

import javafx.scene.control.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PaginationAnt 单元测试 —— 覆盖工厂创建、pageCount/currentPage 钳制、
 * maxPageIndicatorCount、onChange 监听器、disabled。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create / create(int) / 构造重载 + 默认 styleClass</li>
 *   <li>pageCount：正常设值 + 钳制（&lt;1 → 1）</li>
 *   <li>currentPage：正常设值 + 钳制（负数 → 0）+ 超过 pageCount 时被原生 Pagination 截断</li>
 *   <li>maxPageIndicatorCount</li>
 *   <li>onChange：Consumer&lt;Integer&gt; 接收 newIndex（含 null 不挂监听）</li>
 *   <li>disabled</li>
 *   <li>链式串联 + 继承式核心契约</li>
 * </ul>
 *
 * <p><b>注意</b>：Pagination 原生 setPageCount / setCurrentPageIndex 都会做边界处理；
 * PaginationAnt 在源头钳制 pageCount &gt;= 1 和 currentPage &gt;= 0，避免负数 / 零页崩溃。</p>
 */
@DisplayName("PaginationAnt")
class PaginationAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create() 默认 1 页 + 挂 jfx-pagination")
    void create_default() {
        PaginationAnt p = PaginationAnt.create().build();
        assertNotNull(p);
        assertEquals(1, p.getPageCount());
        assertTrue(p.getStyleClass().contains(JfxStyles.PAGINATION));
    }

    @Test
    @DisplayName("create(int) 设总页数")
    void create_withPageCount() {
        PaginationAnt p = PaginationAnt.create(10).build();
        assertEquals(10, p.getPageCount());
    }

    @Test
    @DisplayName("构造 PaginationAnt(pageCount, currentPageIndex) 同时设置页数 + 起始页")
    void constructor_pageCountAndCurrentPage() {
        PaginationAnt p = new PaginationAnt(20, 5);
        assertEquals(20, p.getPageCount());
        assertEquals(5, p.getCurrentPageIndex());
    }

    @Test
    @DisplayName("构造 PaginationAnt(0) 钳制为 1")
    void constructor_zero_clamped() {
        PaginationAnt p = new PaginationAnt(0);
        assertEquals(1, p.getPageCount());
    }

    @Test
    @DisplayName("构造 PaginationAnt(-5) 钳制为 1")
    void constructor_negative_clamped() {
        PaginationAnt p = new PaginationAnt(-5);
        assertEquals(1, p.getPageCount());
    }

    @Test
    @DisplayName("构造 PaginationAnt(10, -3) 钳制 currentPage 为 0")
    void constructor_negativeCurrentPage_clamped() {
        PaginationAnt p = new PaginationAnt(10, -3);
        assertEquals(0, p.getCurrentPageIndex());
    }

    // ============================================================
    // pageCount
    // ============================================================

    @Nested
    @DisplayName("pageCount —— 钳制 >= 1")
    class PageCount {

        @Test
        @DisplayName("pageCount(10) 设总页数")
        void pageCount_normal() {
            PaginationAnt p = PaginationAnt.create().pageCount(10).build();
            assertEquals(10, p.getPageCount());
        }

        @Test
        @DisplayName("pageCount(0) 钳制为 1")
        void pageCount_zero_clamped() {
            PaginationAnt p = PaginationAnt.create().pageCount(0).build();
            assertEquals(1, p.getPageCount());
        }

        @Test
        @DisplayName("pageCount(-5) 钳制为 1")
        void pageCount_negative_clamped() {
            PaginationAnt p = PaginationAnt.create().pageCount(-5).build();
            assertEquals(1, p.getPageCount());
        }

        @Test
        @DisplayName("pageCount(1) 不钳制（最小有效值）")
        void pageCount_minValid() {
            PaginationAnt p = PaginationAnt.create().pageCount(1).build();
            assertEquals(1, p.getPageCount());
        }

        @Test
        @DisplayName("pageCount(50) 设大值生效")
        void pageCount_large() {
            PaginationAnt p = PaginationAnt.create().pageCount(50).build();
            assertEquals(50, p.getPageCount());
        }
    }

    // ============================================================
    // currentPage
    // ============================================================

    @Nested
    @DisplayName("currentPage —— 钳制 >= 0")
    class CurrentPage {

        @Test
        @DisplayName("currentPage(0) 合法最小值")
        void currentPage_zero() {
            PaginationAnt p = PaginationAnt.create(10).currentPage(0).build();
            assertEquals(0, p.getCurrentPageIndex());
        }

        @Test
        @DisplayName("currentPage(5) 设第 5 页（从 0 开始）")
        void currentPage_normal() {
            PaginationAnt p = PaginationAnt.create(10).currentPage(5).build();
            assertEquals(5, p.getCurrentPageIndex());
        }

        @Test
        @DisplayName("currentPage(-3) 钳制为 0")
        void currentPage_negative_clamped() {
            PaginationAnt p = PaginationAnt.create(10).currentPage(-3).build();
            assertEquals(0, p.getCurrentPageIndex());
        }

        @Test
        @DisplayName("currentPage(9) 在 pageCount=10 范围内不越界")
        void currentPage_lastPage() {
            PaginationAnt p = PaginationAnt.create(10).currentPage(9).build();
            assertEquals(9, p.getCurrentPageIndex());
        }
    }

    // ============================================================
    // maxPageIndicatorCount
    // ============================================================

    @Test
    @DisplayName("maxPageIndicatorCount(7) 设指示器数量")
    void maxPageIndicatorCount_set() {
        PaginationAnt p = PaginationAnt.create(20)
                .maxPageIndicatorCount(7)
                .build();
        assertEquals(7, p.getMaxPageIndicatorCount());
    }

    @Test
    @DisplayName("maxPageIndicatorCount 默认值（JavaFX 原生默认 10）")
    void maxPageIndicatorCount_default() {
        PaginationAnt p = PaginationAnt.create().build();
        assertEquals(10, p.getMaxPageIndicatorCount());
    }

    // ============================================================
    // onChange
    // ============================================================

    @Nested
    @DisplayName("onChange —— Consumer<Integer> 接收 newIndex")
    class OnChange {

        @Test
        @DisplayName("onChange 触发 currentPageIndex 变化时回调拿到新 index")
        void onChange_callbackFires() {
            AtomicInteger captured = new AtomicInteger(-1);
            PaginationAnt p = PaginationAnt.create(10)
                    .onChange(captured::set)
                    .build();
            p.setCurrentPageIndex(3);
            assertEquals(3, captured.get());
        }

        @Test
        @DisplayName("onChange 多次变化按序触发")
        void onChange_multipleEvents() {
            AtomicReference<String> log = new AtomicReference<>("");
            PaginationAnt p = PaginationAnt.create(20)
                    .onChange(v -> log.set(log.get() + v + ","))
                    .build();
            p.setCurrentPageIndex(2);
            p.setCurrentPageIndex(5);
            p.setCurrentPageIndex(8);
            assertEquals("2,5,8,", log.get());
        }

        @Test
        @DisplayName("onChange(null) 不抛 NPE，且不再注册监听器")
        void onChange_null_noListener() {
            PaginationAnt p = PaginationAnt.create(10)
                    .onChange(null)
                    .build();
            // 改 currentPage 不应抛异常
            assertDoesNotThrow(() -> p.setCurrentPageIndex(2));
        }
    }

    // ============================================================
    // 状态
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        PaginationAnt p = PaginationAnt.create(10).disabled(true).build();
        assertTrue(p.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        PaginationAnt p = PaginationAnt.create(10).build();
        assertFalse(p.isDisable());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联：create(pageCount) → maxIndicator → onChange → disabled 全部生效")
    void fullChain_noException() {
        AtomicInteger captured = new AtomicInteger(-1);
        PaginationAnt p = PaginationAnt.create(20)
                .pageCount(15)
                .currentPage(2)
                .maxPageIndicatorCount(7)
                .onChange(captured::set)
                .disabled(false)
                .build();

        assertEquals(15, p.getPageCount());
        assertEquals(2, p.getCurrentPageIndex());
        assertEquals(7, p.getMaxPageIndicatorCount());

        p.setCurrentPageIndex(10);
        assertEquals(10, captured.get());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        PaginationAnt p = PaginationAnt.create();
        assertSame(p, p.build());
    }

    @Test
    @DisplayName("继承式：父类 Pagination 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        Pagination p = PaginationAnt.create(10).build();
        assertInstanceOf(PaginationAnt.class, p);
        assertEquals(10, p.getPageCount());
    }
}
