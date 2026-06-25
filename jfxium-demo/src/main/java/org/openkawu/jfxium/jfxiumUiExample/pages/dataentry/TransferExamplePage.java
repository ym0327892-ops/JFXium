package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.List;
import org.openkawu.jfxium.component.composite.TransferAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Transfer 穿梭框 —— 基础 / 带搜索。
 */
public class TransferExamplePage extends VBoxAnt {

    public TransferExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Transfer 穿梭框")
                .description("双栏穿梭选择框，用于在两组数据间移动选项。")
                .sections(
                        basicSection(),
                        searchSection(),
                        valueSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node transfer = TransferAnt.<String>create()
                .dataSource(List.of("选项 1", "选项 2", "选项 3", "选项 4", "选项 5"))
                .targetKeys(List.of("选项 3"))
                .titles("可选项", "已选项")
                .build();
        String code = """
                Node transfer = TransferAnt.<String>create()
                        .dataSource(List.of("选项 1", "选项 2", "选项 3", "选项 4", "选项 5"))
                        .targetKeys(List.of("选项 3"))
                        .titles("可选项", "已选项")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "左右两栏穿梭。操作步骤：先点选左侧项目（可多选），再点中间「>」箭头移动到右侧；反向同理点「<」。",
                code, transfer);
    }

    private Node searchSection() {
        Node transfer = TransferAnt.<String>create()
                .dataSource(List.of("北京", "上海", "广州", "深圳", "杭州", "成都"))
                .targetKeys(List.of("上海"))
                .titles("城市列表", "已选城市")
                .showSearch()
                .build();
        String code = """
                Node transfer = TransferAnt.<String>create()
                        .dataSource(List.of("北京", "上海", "广州", "深圳", "杭州", "成都"))
                        .targetKeys(List.of("上海"))
                        .titles("城市列表", "已选城市")
                        .showSearch()
                        .build();
                """;
        return Demos.sectionWithCode("2. 带搜索", "showSearch() 在列表顶部添加搜索框。", code, transfer);
    }

    /**
     * 3. 获取右侧列表 —— onChange 回调给出当前「已选项」(target) 列表。
     *
     * <p>每次穿梭（点中间箭头移动项目）触发 onChange，结果 Label 实时显示右侧已选项内容。</p>
     */
    private Node valueSection() {
        Label result = TypographyAnt.text("已选项：[选项 3]").build();
        Node transfer = TransferAnt.<String>create()
                .dataSource(List.of("选项 1", "选项 2", "选项 3", "选项 4", "选项 5"))
                .targetKeys(List.of("选项 3"))
                .titles("可选项", "已选项")
                .onChange(target -> result.setText("已选项：" + target))
                .build();
        Node demo = Demos.column(transfer, result);
        String code = """
                Label result = TypographyAnt.text("已选项：[选项 3]").build();
                Node transfer = TransferAnt.<String>create()
                        .dataSource(List.of("选项 1", "选项 2", "选项 3", "选项 4", "选项 5"))
                        .targetKeys(List.of("选项 3"))
                        .titles("可选项", "已选项")
                        // 每次穿梭后回调，target 是当前右侧已选项列表
                        .onChange(target -> result.setText("已选项：" + target))
                        .build();
                """;
        return Demos.sectionWithCode("3. 获取右侧列表",
                "先点选左侧项目，再点中间箭头移动；onChange(target -> ...) 给出当前右侧已选项列表，结果 Label 实时显示。",
                code, demo);
    }
}
