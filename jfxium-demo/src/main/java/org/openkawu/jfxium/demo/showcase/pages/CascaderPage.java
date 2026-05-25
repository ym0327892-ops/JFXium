package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CascaderAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.List;

/**
 * Cascader 级联选择展示页（M19.12）。
 */
public class CascaderPage implements ShowcasePage {

    @Override public String   key()      { return "cascader"; }
    @Override public String   title()    { return "Cascader 级联选择"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Cascader 级联选择");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("多级树形数据选择 —— 省市区 / 部门组织 / 商品分类。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionRegion(),
                        sectionDepartment(),
                        sectionWithSearch()
                )
                .build();
    }

    private static List<CascaderAnt.Option> regionData() {
        return List.of(
                new CascaderAnt.Option("bj", "北京市", List.of(
                        new CascaderAnt.Option("hd", "海淀区"),
                        new CascaderAnt.Option("cy", "朝阳区"),
                        new CascaderAnt.Option("xc", "西城区")
                )),
                new CascaderAnt.Option("sh", "上海市", List.of(
                        new CascaderAnt.Option("pd", "浦东新区"),
                        new CascaderAnt.Option("xh", "徐汇区"),
                        new CascaderAnt.Option("hp", "黄浦区")
                )),
                new CascaderAnt.Option("gd", "广东省", List.of(
                        new CascaderAnt.Option("gz", "广州市", List.of(
                                new CascaderAnt.Option("th", "天河区"),
                                new CascaderAnt.Option("yx", "越秀区")
                        )),
                        new CascaderAnt.Option("sz", "深圳市", List.of(
                                new CascaderAnt.Option("ns", "南山区"),
                                new CascaderAnt.Option("ft", "福田区")
                        ))
                ))
        );
    }

    private Node sectionRegion() {
        Node c = CascaderAnt.create()
                .options(regionData())
                .placeholder("请选择省/市/区")
                .onChange(path -> MessageAnt.info("选中：" + String.join(" / ", path)))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：省市区三级")
                .description("典型应用 —— 省/市/区三级地址选择")
                .demo(c)
                .code("""
                        List<CascaderAnt.Option> options = List.of(
                            new CascaderAnt.Option("bj", "北京市", List.of(
                                new CascaderAnt.Option("hd", "海淀区"),
                                new CascaderAnt.Option("cy", "朝阳区")
                            )),
                            ...
                        );
                        CascaderAnt.create()
                            .options(options)
                            .placeholder("请选择省/市/区")
                            .onChange(path -> handleAddress(path))
                            .build();
                        """)
                .build();
    }

    private Node sectionDepartment() {
        List<CascaderAnt.Option> deps = List.of(
                new CascaderAnt.Option("tech", "技术中心", List.of(
                        new CascaderAnt.Option("frontend", "前端组"),
                        new CascaderAnt.Option("backend", "后端组"),
                        new CascaderAnt.Option("qa", "QA 组")
                )),
                new CascaderAnt.Option("biz", "业务中心", List.of(
                        new CascaderAnt.Option("ops", "运营组"),
                        new CascaderAnt.Option("market", "市场组")
                ))
        );

        Node c = CascaderAnt.create()
                .options(deps)
                .placeholder("请选择部门")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：组织部门选择")
                .description("HR / OA 系统的部门选择标准模式")
                .demo(c)
                .code("""
                        CascaderAnt.create()
                            .options(departments)
                            .placeholder("请选择部门")
                            .build();
                        """)
                .build();
    }

    private Node sectionWithSearch() {
        Node c = CascaderAnt.create()
                .options(regionData())
                .placeholder("可搜索的级联选择")
                .showSearch(true)
                .allowClear(true)
                .build();
        return ShowcaseSection.create()
                .title("场景 3：带搜索 + 清除")
                .description(".showSearch(true) + .allowClear(true) —— 数据多时输入关键词快速定位")
                .demo(c)
                .code("""
                        CascaderAnt.create()
                            .options(regionData())
                            .placeholder("可搜索的级联选择")
                            .showSearch(true)
                            .allowClear(true)
                            .build();
                        """)
                .build();
    }
}
