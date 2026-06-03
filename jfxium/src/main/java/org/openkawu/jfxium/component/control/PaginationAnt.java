package org.openkawu.jfxium.component.control;

import javafx.scene.control.Pagination;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium Pagination Component
 * 封装 JavaFX Pagination
 *
 * Usage:
 * <pre>{@code
 * Pagination pagination = PaginationAnt.create()
 *     .pageCount(10)
 *     .currentPage(0)
 *     .onChange(page -> System.out.println("Page: " + page))
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
