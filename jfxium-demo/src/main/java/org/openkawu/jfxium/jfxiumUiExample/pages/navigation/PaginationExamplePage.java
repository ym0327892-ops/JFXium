package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.PaginationAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Pagination 分页 —— 基础 / 当前页 / 翻页回调。
 */
public class PaginationExamplePage extends VBoxAnt {

    public PaginationExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Pagination 分页")
                .description("分页器，封装 JavaFX 原生 Pagination，按页索引切换内容。")
                .sections(basicSection(), currentPageSection(), onChangeSection(), playgroundSection())
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
                        .onChange(page -> MessageAnt.info("切换到第 " + (page + 1) + " 页"))
                        .build();
                """;
        return Demos.sectionWithCode("3. 翻页回调",
                "onChange(...) 在当前页变化时触发，参数为新的页索引（从 0 开始）。",
                code, demo);
    }

    /**
     * 4. 交互演示 —— 通过左侧控件实时改变 Pagination 的总页数 / 当前页 / 页码指示器数量。
     *
     * <p>PaginationAnt 无 Controller，所有属性变更均通过 build 重建生效。</p>
     */
    private Node playgroundSection() {
        Binder<String> pageCountBinder     = PlayGround.binder("10");
        Binder<String> currentPageBinder   = PlayGround.binder("0");
        Binder<String> maxIndicatorBinder  = PlayGround.binder("5");

        Supplier<Node> factory = () -> {
            int total   = parseInt(pageCountBinder.get(), 10, 1, 100);
            int current = Math.min(parseInt(currentPageBinder.get(), 0, 0, 99), total - 1);
            int maxInd  = parseInt(maxIndicatorBinder.get(), 5, 1, 20);
            return PaginationAnt.create()
                    .pageCount(total)
                    .currentPage(current)
                    .maxPageIndicatorCount(maxInd)
                    .onChange(page -> MessageAnt.info("切换到第 " + (page + 1) + " 页"))
                    .build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Pagination 的总页数、当前页、页码指示器数量 —— PaginationAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("总页数", PlayGround.textField(pageCountBinder, "10", "输入总页数")),
                        PlayGround.row("当前页", PlayGround.textField(currentPageBinder, "0", "输入当前页（从 0 开始）")),
                        PlayGround.row("页码指示器数", PlayGround.textField(maxIndicatorBinder, "5", "输入可见页码数量"))));
    }

    private static int parseInt(String v, int fallback, int min, int max) {
        if (v == null || v.isBlank()) return fallback;
        try {
            int i = Integer.parseInt(v.trim());
            if (i < min) return min;
            if (i > max) return max;
            return i;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
