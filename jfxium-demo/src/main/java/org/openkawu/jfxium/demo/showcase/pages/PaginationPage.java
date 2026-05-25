package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.PaginationAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Pagination 展示页（M19.8）。
 */
public class PaginationPage implements ShowcasePage {

    @Override public String   key()      { return "pagination"; }
    @Override public String   title()    { return "Pagination 分页器"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Pagination 分页器");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("admin 列表页底部标配。底层是 JavaFX Pagination + 主题化样式。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionLargePages(),
                        sectionFewPages(),
                        sectionWithCallback()
                )
                .build();
    }

    private Node sectionBasic() {
        Pagination p = PaginationAnt.create()
                .pageCount(10)
                .currentPage(0)
                .maxPageIndicatorCount(7)
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础用法")
                .description("10 页，最多显示 7 个页码指示器")
                .demo(p)
                .code("""
                        Pagination p = PaginationAnt.create()
                            .pageCount(10)
                            .currentPage(0)
                            .maxPageIndicatorCount(7)
                            .build();
                        """)
                .build();
    }

    private Node sectionLargePages() {
        Pagination p = PaginationAnt.create()
                .pageCount(100)
                .currentPage(42)
                .maxPageIndicatorCount(7)
                .build();
        return ShowcaseSection.create()
                .title("场景 2：大数据量分页")
                .description("100 页 + 当前在第 42 页——长列表的标准场景")
                .demo(p)
                .code("""
                        PaginationAnt.create().pageCount(100).currentPage(42).build();
                        """)
                .build();
    }

    private Node sectionFewPages() {
        Pagination p = PaginationAnt.create()
                .pageCount(3)
                .currentPage(0)
                .build();
        return ShowcaseSection.create()
                .title("场景 3：小数据量")
                .description("3 页时只显示对应数量的页码")
                .demo(p)
                .code("""
                        PaginationAnt.create().pageCount(3).build();
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Label out = new Label("当前页：1");
        out.setStyle("-fx-text-fill: -color-fg-muted;");

        Pagination p = PaginationAnt.create()
                .pageCount(20)
                .currentPage(0)
                .onChange(page -> out.setText("当前页：" + (page + 1)))
                .build();

        VBox col = VBoxBuilder.create().spacing(8).children(p, out).build();

        return ShowcaseSection.create()
                .title("场景 4：分页切换回调")
                .description(".onChange(page -> ...)：用户切换页面时同步加载数据")
                .demo(col)
                .code("""
                        Pagination p = PaginationAnt.create()
                            .pageCount(20)
                            .onChange(page -> reload(page))
                            .build();
                        """)
                .build();
    }
}
