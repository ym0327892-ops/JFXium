package org.openkawu.jfxium.demo.showcase.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.TableAnt;
import org.openkawu.jfxium.component.TagAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.Comparator;
import java.util.List;

/**
 * TableAnt 组件展示页。
 *
 * <p>覆盖 M11 + M11.1 全部功能：</p>
 * <ul>
 *   <li>5 种列类型（column / numberColumn / booleanColumn / nodeColumn / actionColumn）</li>
 *   <li>列宽 + 拖拽 + min/max</li>
 *   <li>排序（默认/自定义比较器/默认排序列）</li>
 *   <li>对齐（align/headerAlign/contentAlign 三种 API）</li>
 *   <li>4 种边框模式（NONE/HORIZONTAL/VERTICAL/BOTH）</li>
 *   <li>隐藏表头（showHeader）</li>
 *   <li>选择列、斑马纹、紧凑模式</li>
 *   <li>UNCONSTRAINED 列宽策略</li>
 * </ul>
 */
public class TablePage implements ShowcasePage {

    @Override public String   key()      { return "table"; }
    @Override public String   title()    { return "Table 表格"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    /** 简化数据模型：所有 Section 共用。 */
    public record Person(int id, String name, int age, String email, boolean vip) {}

    private static final List<Person> SAMPLE = List.of(
            new Person(1, "张三", 28, "zhangsan@example.com", true),
            new Person(2, "李四", 34, "lisi@example.com", false),
            new Person(3, "王五", 22, "wangwu@example.com", true),
            new Person(4, "赵六", 41, "zhaoliu@example.com", false),
            new Person(5, "钱七", 29, "qianqi@example.com", true)
    );

    @Override
    public Node getView() {
        // 页面标题区
        Label pageTitle = new Label("Table 表格");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("展示行列数据，是 admin 后台的核心组件。M11 + M11.1 双层链式 API。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create()
                .spacing(8)
                .children(pageTitle, pageDesc)
                .build();

        // 各 Section
        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionColumnTypes(),
                        sectionWidthAndAlign(),
                        sectionSplitAlign(),
                        sectionSorting(),
                        sectionBorders(),
                        sectionSizes(),
                        sectionSelectableAndStriped(),
                        sectionNoHeader(),
                        sectionActionColumn()
                )
                .build();
    }

    // ============================================================
    // 1. 基础用法
    // ============================================================
    private Node sectionBasic() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("ID", p -> String.valueOf(p.id())).end()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .data(SAMPLE)
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("基础用法")
                .description("最简表格，每个列必须以 .end() 结束才能链式继续")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("ID", p -> String.valueOf(p.id())).end()
                            .column("姓名", Person::name).end()
                            .numberColumn("年龄", Person::age).end()
                            .column("邮箱", Person::email).end()
                            .data(SAMPLE)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 5 种列类型
    // ============================================================
    private Node sectionColumnTypes() {
        TableView<Person> table = TableAnt.<Person>create()
                // 文本列
                .column("姓名", Person::name).end()
                // 数字列（默认右对齐）
                .numberColumn("年龄", Person::age).end()
                // 布尔列（自动渲染勾选框）
                .booleanColumn("VIP", Person::vip).end()
                // 自定义节点列
                .nodeColumn("等级", p -> {
                    String text = p.age() > 30 ? "高级" : "普通";
                    TagAnt.Type type = p.age() > 30 ? TagAnt.Type.PRIMARY : TagAnt.Type.DEFAULT;
                    return TagAnt.create().text(text).type(type).build();
                }).align(TableAnt.Align.CENTER).end()
                // 操作列糖
                .actionColumn("操作")
                    .action("编辑", p -> System.out.println("编辑：" + p.name()))
                    .action("删除", p -> System.out.println("删除：" + p.name())).danger()
                    .end()
                .data(SAMPLE)
                .build();
        table.setPrefHeight(260);

        return ShowcaseSection.create()
                .title("5 种列类型")
                .description("文本列 / 数字列（自动右对齐）/ 布尔列（勾选框）/ 自定义节点列 / 操作列糖")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("姓名", Person::name).end()
                            .numberColumn("年龄", Person::age).end()
                            .booleanColumn("VIP", Person::vip).end()
                            .nodeColumn("等级", p -> {
                                String text = p.age() > 30 ? "高级" : "普通";
                                TagAnt.Type type = p.age() > 30 ? TagAnt.Type.PRIMARY : TagAnt.Type.DEFAULT;
                                return TagAnt.create().text(text).type(type).build();
                            }).align(TableAnt.Align.CENTER).end()
                            .actionColumn("操作")
                                .action("编辑", p -> {...})
                                .action("删除", p -> {...}).danger()
                                .end()
                            .data(SAMPLE)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 3. 列宽 + 对齐
    // ============================================================
    private Node sectionWidthAndAlign() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("ID", p -> String.valueOf(p.id()))
                    .width(60)                          // 固定宽度
                    .align(TableAnt.Align.RIGHT)
                    .end()
                .column("姓名", Person::name)
                    .width(120, 80, 200)                // pref/min/max
                    .end()
                .column("邮箱", Person::email)
                    .minWidth(220)                      // 仅设最小宽度
                    .end()
                .numberColumn("年龄", Person::age)
                    .width(80)
                    .resizable(false)                   // 禁止拖拽改宽
                    .end()
                .data(SAMPLE)
                .resizePolicy(TableAnt.Resize.UNCONSTRAINED)  // 列宽自由
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("列宽 + 对齐")
                .description("width(pref) / width(pref,min,max) / minWidth / maxWidth / resizable / align")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("ID", p -> String.valueOf(p.id()))
                                .width(60).align(TableAnt.Align.RIGHT).end()
                            .column("姓名", Person::name)
                                .width(120, 80, 200).end()           // pref/min/max
                            .column("邮箱", Person::email)
                                .minWidth(220).end()
                            .numberColumn("年龄", Person::age)
                                .width(80).resizable(false).end()    // 禁止拖宽
                            .data(SAMPLE)
                            .resizePolicy(TableAnt.Resize.UNCONSTRAINED)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 4. 表头与内容拆分对齐（M11.1 新增）
    // ============================================================
    private Node sectionSplitAlign() {
        TableView<Person> table = TableAnt.<Person>create()
                // 表头居中、内容右对齐（数字列经典用法）
                .column("ID", p -> String.valueOf(p.id()))
                    .headerAlign(TableAnt.Align.CENTER)
                    .contentAlign(TableAnt.Align.RIGHT)
                    .width(80)
                    .end()
                // 表头居左、内容居中
                .column("姓名", Person::name)
                    .headerAlign(TableAnt.Align.LEFT)
                    .contentAlign(TableAnt.Align.CENTER)
                    .width(120)
                    .end()
                // 表头居右、内容居左（罕见但能演示能力）
                .column("邮箱", Person::email)
                    .headerAlign(TableAnt.Align.RIGHT)
                    .contentAlign(TableAnt.Align.LEFT)
                    .end()
                .data(SAMPLE)
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("表头与内容拆分对齐（M11.1）")
                .description("headerAlign 和 contentAlign 独立设置；老 align(...) 等价同时设两者")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            // 表头居中、内容右对齐（数字列经典用法）
                            .column("ID", p -> String.valueOf(p.id()))
                                .headerAlign(TableAnt.Align.CENTER)
                                .contentAlign(TableAnt.Align.RIGHT)
                                .width(80)
                                .end()
                            .column("姓名", Person::name)
                                .headerAlign(TableAnt.Align.LEFT)
                                .contentAlign(TableAnt.Align.CENTER)
                                .end()
                            ...
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. 排序
    // ============================================================
    private Node sectionSorting() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("ID", p -> String.valueOf(p.id()))
                    .width(60)
                    .end()
                .column("姓名", Person::name).end()
                // 数字列默认能排序
                .numberColumn("年龄", Person::age)
                    .sorter(Comparator.comparingInt(Number::intValue))   // 自定义比较器
                    .end()
                .column("邮箱", Person::email)
                    .sortable(false)                                     // 禁止此列排序
                    .end()
                .data(SAMPLE)
                .defaultSortBy("年龄", TableColumn.SortType.DESCENDING)   // 启动默认排序
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("排序")
                .description("点击列头切换排序；可禁用单列、自定义比较器、设置启动默认排序列")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("ID", p -> String.valueOf(p.id())).width(60).end()
                            .column("姓名", Person::name).end()
                            .numberColumn("年龄", Person::age)
                                .sorter(Comparator.comparingInt(Number::intValue))
                                .end()
                            .column("邮箱", Person::email)
                                .sortable(false)                                  // 禁止排序
                                .end()
                            .data(SAMPLE)
                            .defaultSortBy("年龄", TableColumn.SortType.DESCENDING)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 6. 4 种边框模式（M11.1 新增）
    // ============================================================
    private Node sectionBorders() {
        // 4 个小表格并排展示 4 种边框模式
        VBox grid = new VBox(16);
        grid.getChildren().addAll(
                borderDemo("Border.NONE", TableAnt.Border.NONE, "无任何分割线，极简风格"),
                borderDemo("Border.HORIZONTAL（默认）", TableAnt.Border.HORIZONTAL, "仅行底横线，admin 默认风格"),
                borderDemo("Border.VERTICAL", TableAnt.Border.VERTICAL, "仅列竖线，数据列对齐重要时"),
                borderDemo("Border.BOTH", TableAnt.Border.BOTH, "横竖都有，Excel 风格")
        );

        return ShowcaseSection.create()
                .title("内容区分割线 4 种模式（M11.1）")
                .description("borders(Border) 替代老的 bordered(boolean)，提供 4 种语义清晰的模式")
                .demo(grid)
                .code("""
                        // 4 种模式任选其一
                        .borders(TableAnt.Border.NONE)        // 无分割线
                        .borders(TableAnt.Border.HORIZONTAL)  // 默认：仅横线
                        .borders(TableAnt.Border.VERTICAL)    // 仅竖线
                        .borders(TableAnt.Border.BOTH)        // 横+竖（Excel）
                        """)
                .build();
    }

    private VBox borderDemo(String title, TableAnt.Border border, String hint) {
        Label tlabel = new Label(title);
        tlabel.setStyle("-fx-font-weight: 600;");
        Label hlabel = new Label(hint);
        hlabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

        TableView<Person> table = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .data(SAMPLE.subList(0, 3))
                .borders(border)
                .build();
        table.setPrefHeight(160);

        VBox box = new VBox(4);
        box.getChildren().addAll(tlabel, hlabel, table);
        return box;
    }

    // ============================================================
    // 6.5 三档尺寸（M11.2）
    // ============================================================
    private Node sectionSizes() {
        VBox grid = new VBox(16);
        grid.getChildren().addAll(
                sizeDemo("Size.SMALL", TableAnt.Size.SMALL, "表头 40 / 行高 36 / padding 6×8 / 字号 13（mini 表格）"),
                sizeDemo("Size.MIDDLE（默认）", TableAnt.Size.MIDDLE, "表头 48 / 行高 48 / padding 8×12 / 字号 14"),
                sizeDemo("Size.LARGE", TableAnt.Size.LARGE, "表头 56 / 行高 56 / padding 14×16 / 字号 15（宽松）")
        );

        return ShowcaseSection.create()
                .title("三档尺寸（M11.2）")
                .description("size(SMALL/MIDDLE/LARGE) 对齐 Ant Design Table size。老 compact(true) 等价 SMALL。")
                .demo(grid)
                .code("""
                        // 任选其一
                        .size(TableAnt.Size.SMALL)    // 紧凑（mini）
                        .size(TableAnt.Size.MIDDLE)   // 默认
                        .size(TableAnt.Size.LARGE)    // 宽松
                        """)
                .build();
    }

    private VBox sizeDemo(String title, TableAnt.Size size, String hint) {
        Label tlabel = new Label(title);
        tlabel.setStyle("-fx-font-weight: 600;");
        Label hlabel = new Label(hint);
        hlabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

        TableView<Person> table = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .booleanColumn("VIP", Person::vip).end()
                .data(SAMPLE.subList(0, 3))
                .size(size)
                .build();

        VBox box = new VBox(4);
        box.getChildren().addAll(tlabel, hlabel, table);
        return box;
    }

    // ============================================================
    // 7. 选择列 + 斑马纹 + 紧凑模式
    // ============================================================
    private Node sectionSelectableAndStriped() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .numberColumn("年龄", Person::age).end()
                .column("邮箱", Person::email).end()
                .booleanColumn("VIP", Person::vip).end()
                .data(SAMPLE)
                .selectable(true)        // 首列勾选框（多选）
                .striped(true)           // 斑马纹
                .compact(true)           // 紧凑行高（48 → 36）
                .build();
        table.setPrefHeight(240);

        return ShowcaseSection.create()
                .title("选择列 + 斑马纹 + 紧凑模式")
                .description("selectable(true) 多选 / striped(true) 斑马纹 / compact(true) 行高 36px")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("姓名", Person::name).end()
                            .numberColumn("年龄", Person::age).end()
                            ...
                            .data(SAMPLE)
                            .selectable(true)        // 首列勾选框（多选）
                            .striped(true)           // 斑马纹
                            .compact(true)           // 紧凑行高
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 8. 隐藏表头（M11.1 新增）
    // ============================================================
    private Node sectionNoHeader() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("姓名", Person::name).end()
                .column("邮箱", Person::email).end()
                .numberColumn("年龄", Person::age).end()
                .data(SAMPLE)
                .showHeader(false)       // 隐藏表头
                .borders(TableAnt.Border.HORIZONTAL)
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("隐藏表头（M11.1）")
                .description("showHeader(false) 完全隐藏表头行；适合卡片式列表、嵌入式数据展示")
                .demo(table)
                .code("""
                        TableView<Person> table = TableAnt.<Person>create()
                            .column("姓名", Person::name).end()
                            ...
                            .data(SAMPLE)
                            .showHeader(false)        // 隐藏表头
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 9. actionColumn 完整玩法
    // ============================================================
    private Node sectionActionColumn() {
        TableView<Person> table = TableAnt.<Person>create()
                .column("ID", p -> String.valueOf(p.id())).width(60).end()
                .column("姓名", Person::name).end()
                .actionColumn("操作")
                    .action("查看", p -> System.out.println("查看：" + p.name()))
                    .action("编辑", p -> System.out.println("编辑：" + p.name()))
                    .action("禁用", p -> System.out.println("禁用：" + p.name()))
                    .action("删除", p -> System.out.println("删除：" + p.name()))
                        .danger()        // 修饰最后一个 action
                    .width(220)
                    .spacing(12)
                    .end()
                .data(SAMPLE)
                .build();
        table.setPrefHeight(220);

        return ShowcaseSection.create()
                .title("actionColumn 操作列糖")
                .description(".action(label, handler) 加按钮；.danger() 修饰最后一个为红色；.width / .spacing 自定义")
                .demo(table)
                .code("""
                        .actionColumn("操作")
                            .action("查看", p -> ...)
                            .action("编辑", p -> ...)
                            .action("禁用", p -> ...)
                            .action("删除", p -> ...)
                                .danger()        // 修饰最后一个 action
                            .width(220)
                            .spacing(12)
                            .end()
                        """)
                .build();
    }
}
