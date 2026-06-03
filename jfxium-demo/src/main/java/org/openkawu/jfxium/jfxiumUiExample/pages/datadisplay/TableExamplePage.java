package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.List;
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
                        sortableSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .data(SAMPLE_DATA)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name).end()
                        .numberColumn("年龄", Person::age).end()
                        .column("邮箱", Person::email).end()
                        .data(dataList)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础表格", "最简单的表格用法。", code, demo);
    }

    private Node stripedBorderedSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .data(SAMPLE_DATA)
                .striped(true)
                .borders(TableAnt.Border.BOTH)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name).end()
                        .numberColumn("年龄", Person::age).end()
                        .column("邮箱", Person::email).end()
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
                        .column("姓名", Person::name).end()
                        .numberColumn("年龄", Person::age).end()
                        .data(SAMPLE_DATA)
                        .size(TableAnt.Size.SMALL)
                        .build(),
                TableAnt.<Person>create()
                        .column("姓名", Person::name).end()
                        .numberColumn("年龄", Person::age).end()
                        .data(SAMPLE_DATA)
                        .size(TableAnt.Size.LARGE)
                        .build()
        );
        String code = """
                // 紧凑
                TableAnt.<Person>create()...size(TableAnt.Size.SMALL).build();
                // 宽松
                TableAnt.<Person>create()...size(TableAnt.Size.LARGE).build();
                """;
        return Demos.sectionWithCode("3. 尺寸",
                "SMALL（紧凑）/ MIDDLE（默认）/ LARGE（宽松）。",
                code, demo);
    }

    private Node sortableSection() {
        Node demo = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .data(SAMPLE_DATA)
                .sortable(true)
                .build();
        String code = """
                TableAnt.<Person>create()
                        .column("姓名", Person::name).end()
                        .numberColumn("年龄", Person::age).end()
                        .column("邮箱", Person::email).end()
                        .data(dataList)
                        .sortable(true)
                        .build();
                """;
        return Demos.sectionWithCode("4. 可排序",
                "sortable(true) 启用列头点击排序。",
                code, demo);
    }
}
