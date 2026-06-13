package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.PaginationAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Pagination 分页 —— 基础 / 当前页 / 翻页回调。
 */
public class PaginationExamplePage extends VBoxAnt {

    public PaginationExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Pagination 分页")
                .description("分页器，封装 JavaFX 原生 Pagination，按页索引切换内容。")
                .sections(basicSection(), currentPageSection(), onChangeSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = PaginationAnt.create()
                .pageCount(10)
                .build();
        String code = """
                PaginationAnt.create()
                        .pageCount(10)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "pageCount(...) 设置总页数，默认从第 0 页开始。",
                code, demo);
    }

    private Node currentPageSection() {
        Node demo = PaginationAnt.create()
                .pageCount(20)
                .currentPage(3)
                .maxPageIndicatorCount(5)
                .build();
        String code = """
                PaginationAnt.create()
                        .pageCount(20)
                        .currentPage(3)               // 初始停在第 3 页
                        .maxPageIndicatorCount(5)     // 最多显示 5 个页码按钮
                        .build();
                """;
        return Demos.sectionWithCode("2. 当前页与页码数量",
                "currentPage(...) 指定初始页；maxPageIndicatorCount(...) 控制可见页码个数。",
                code, demo);
    }

    private Node onChangeSection() {
        Node demo = PaginationAnt.create()
                .pageCount(10)
                .onChange(page -> MessageAnt.info("切换到第 " + (page + 1) + " 页"))
                .build();
        String code = """
                PaginationAnt.create()
                        .pageCount(10)
                        .onChange(page -> System.out.println("切到第 " + page + " 页"))
                        .build();
                """;
        return Demos.sectionWithCode("3. 翻页回调",
                "onChange(...) 在当前页变化时触发，参数为新的页索引（从 0 开始）。",
                code, demo);
    }
}
