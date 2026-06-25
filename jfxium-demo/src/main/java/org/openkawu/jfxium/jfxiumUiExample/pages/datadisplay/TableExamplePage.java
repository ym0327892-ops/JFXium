package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.List;
import java.util.function.Supplier;
import org.openkawu.jfxium.component.control.TableAnt;

/**
 * Table 表格 —— 基础 / 斑马纹+边框 / 尺寸 / 排序。
 */
public class TableExamplePage extends VBoxAnt {

    /** 演示数据 record。 */
    record Person(String name, int age, String email) {}

    private static final List<Person> SAMPLE_DATA = List.of(
            new Person("张三", 28, "zhangsan@example.com"),
            new Person("李四", 32, "lisi@example.com"),
            new Person("王五", 24, "wangwu@example.com"),
            new Person("赵六", 36, "zhaoliu@example.com")
    );

    public TableExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Table 表格")
                .description("用于展示结构化数据，支持排序、斑马纹、多种尺寸。")
                .sections(
                        basicSection(),
                        stripedBorderedSection(),
                        sizeSection(),
                        sortableSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name)
                .numberColumn("年龄", Person::age)
                .column("邮箱", Person::email)
                .data(SAMPLE_DATA)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name)
                        .numberColumn("年龄", Person::age)
                        .column("邮箱", Person::email)
                        .data(dataList)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础表格", "最简单的表格用法。", code, demo);
    }

    private Node stripedBorderedSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name)
                .numberColumn("年龄", Person::age)
                .column("邮箱", Person::email)
                .data(SAMPLE_DATA)
                .striped(true)
                .borders(TableAnt.Border.BOTH)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name)
                        .numberColumn("年龄", Person::age)
                        .column("邮箱", Person::email)
                        .data(dataList)
                        .striped(true)
                        .borders(TableAnt.Border.BOTH)
                        .build();
                """;
        return Demos.sectionWithCode("2. 斑马纹 + 边框",
                "striped(true) 隔行变色；borders(BOTH) 显示完整边框。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.column(
                TableAnt.<Person>create()
                        .column("姓名", Person::name)
                        .numberColumn("年龄", Person::age)
                        .data(SAMPLE_DATA)
                        .size(Size.SMALL)
                        .build(),
                TableAnt.<Person>create()
                        .column("姓名", Person::name)
                        .numberColumn("年龄", Person::age)
                        .data(SAMPLE_DATA)
                        .size(Size.LARGE)
                        .build()
        );
        String code = """
                // 紧凑
                TableAnt.<Person>create().column("姓名", Person::name).numberColumn("年龄", Person::age).size(Size.SMALL).build();
                // 宽松
                TableAnt.<Person>create().column("姓名", Person::name).numberColumn("年龄", Person::age).size(Size.LARGE).build();
                """;
        return Demos.sectionWithCode("3. 尺寸",
                "SMALL（紧凑）/ MIDDLE（默认）/ LARGE（宽松）。",
                code, demo);
    }

    private Node sortableSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name)
                .numberColumn("年龄", Person::age)
                .column("邮箱", Person::email)
                .data(SAMPLE_DATA)
                .sortable(true)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name)
                        .numberColumn("年龄", Person::age)
                        .column("邮箱", Person::email)
                        .data(dataList)
                        .sortable(true)
                        .build();
                """;
        return Demos.sectionWithCode("4. 可排序",
                "sortable(true) 启用列头点击排序。",
                code, demo);
    }

    /** 5. 交互演示：实时切换 size / striped / borders / sortable。 */
    private Node playgroundSection() {
        Binder<String> sizeBinder = PlayGround.binder("middle");
        Binder<String> stripedBinder = PlayGround.binder("off");
        Binder<String> bordersBinder = PlayGround.binder("horizontal");
        Binder<String> sortableBinder = PlayGround.binder("on");
        Supplier<Node> factory = () -> TableAnt.<Person>create()
                .column("姓名", Person::name)
                .numberColumn("年龄", Person::age)
                .column("邮箱", Person::email)
                .data(SAMPLE_DATA)
                .size(parseSize(sizeBinder.get()))
                .striped("on".equals(stripedBinder.get()))
                .borders(parseBorder(bordersBinder.get()))
                .sortable("on".equals(sortableBinder.get()))
                .build();
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Table 的 size / striped / borders / sortable，表格实时重建反映配置。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small", "小"),
                                PlayGround.entry("middle", "默认"),
                                PlayGround.entry("large", "大"))),
                        PlayGround.row("斑马纹", PlayGround.segmented(stripedBinder,
                                PlayGround.entry("off", "关闭"),
                                PlayGround.entry("on", "开启"))),
                        PlayGround.row("边框", PlayGround.segmented(bordersBinder,
                                PlayGround.entry("none", "无"),
                                PlayGround.entry("horizontal", "横线"),
                                PlayGround.entry("vertical", "竖线"),
                                PlayGround.entry("both", "全部"))),
                        PlayGround.row("可排序", PlayGround.segmented(sortableBinder,
                                PlayGround.entry("on", "开启"),
                                PlayGround.entry("off", "关闭")))));
    }

    private static Size parseSize(String v) {
        if ("small".equals(v)) return Size.SMALL;
        if ("large".equals(v)) return Size.LARGE;
        return Size.MIDDLE;
    }

    private static TableAnt.Border parseBorder(String v) {
        if ("none".equals(v)) return TableAnt.Border.NONE;
        if ("vertical".equals(v)) return TableAnt.Border.VERTICAL;
        if ("both".equals(v)) return TableAnt.Border.BOTH;
        return TableAnt.Border.HORIZONTAL;
    }
}
