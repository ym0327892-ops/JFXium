package org.openkawu.jfxium.component;

import javafx.scene.control.Pagination;

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

    public static class Builder {
        private int pageCount = 1;
        private int currentPage = 0;
        private int maxPageIndicatorCount = 10;
        private Consumer<Integer> onChange;
        private String style = "";

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

        public Builder style(String style) {
            this.style = style;
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

            if (!style.isEmpty()) {
                pagination.setStyle(style);
            }

            return pagination;
        }
    }
}
