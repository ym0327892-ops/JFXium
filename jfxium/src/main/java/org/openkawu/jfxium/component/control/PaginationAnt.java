package org.openkawu.jfxium.component.control;

import javafx.scene.control.Pagination;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium 分页组件 - 对标 Ant Design Pagination（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：页码导航控件，包装 JavaFX {@link Pagination}，
 * 用于对大量数据分页展示。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置总页数（{@code pageCount}）</li>
 *   <li>设置当前页（{@code currentPage}，从 0 开始）</li>
 *   <li>设置最大页码指示器数量（{@code maxPageIndicatorCount}）</li>
 *   <li>页码变化回调（{@code onChange}）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>列表页底部分页导航（配合 TableAnt）</li>
 *   <li>图片浏览分页</li>
 *   <li>内容分步展示</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Pagination pagination = PaginationAnt.create()
 *     .pageCount(10)
 *     .currentPage(0)
 *     .maxPageIndicatorCount(7)
 *     .onChange(page -> System.out.println("切换到第 " + (page + 1) + " 页"))
 *     .build();
 * }</pre>
 */
public class PaginationAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private int pageCount = 1;
        private int currentPage = 0;
        private int maxPageIndicatorCount = 10;
        private Consumer<Integer> onChange;

        private Builder() {}

        public Builder pageCount(int pageCount) {
            this.pageCount = Math.max(1, pageCount);
            return this;
        }

        public Builder currentPage(int currentPage) {
            this.currentPage = Math.max(0, currentPage);
            return this;
        }

        public Builder maxPageIndicatorCount(int count) {
            this.maxPageIndicatorCount = count;
            return this;
        }

        public Builder onChange(Consumer<Integer> handler) {
            this.onChange = handler;
            return this;
        }

        public Pagination build() {
            Pagination pagination = new Pagination(pageCount, currentPage);
            pagination.setMaxPageIndicatorCount(maxPageIndicatorCount);
            pagination.getStyleClass().add("jfx-pagination");

            if (onChange != null) {
                pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal.intValue());
                });
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(pagination);
            return pagination;
        }
    }
}
