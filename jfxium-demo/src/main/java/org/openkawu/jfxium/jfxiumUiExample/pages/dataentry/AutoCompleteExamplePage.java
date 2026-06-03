package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.util.List;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.AutoCompleteAnt;

/**
 * AutoComplete 自动完成 —— 基础 / 自定义选项。
 */
public class AutoCompleteExamplePage extends VBoxAnt {

    public AutoCompleteExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("AutoComplete 自动完成")
                .description("输入框带候选提示，随输入实时筛选候选项。")
                .sections(basicSection(), optionsSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = AutoCompleteAnt.<String>create()
                .placeholder("请输入")
                .options(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"))
                .build();
        String code = """
                AutoCompleteAnt.<String>create()
                        .placeholder("请输入")
                        .options(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "options(...) 提供候选列表，输入时自动过滤匹配项。",
                code, demo);
    }

    private Node optionsSection() {
        Node demo = AutoCompleteAnt.<String>create()
                .placeholder("输入邮箱前缀")
                .options(List.of("@gmail.com", "@163.com", "@qq.com", "@outlook.com"))
                .onSelect(opt -> {})
                .build();
        String code = """
                AutoCompleteAnt.<String>create()
                        .placeholder("输入邮箱前缀")
                        .options(List.of("@gmail.com", "@163.com", "@qq.com", "@outlook.com"))
                        .onSelect(opt -> System.out.println("选中：" + opt))
                        .build();
                """;
        return Demos.sectionWithCode("2. 选中回调",
                "onSelect(...) 在用户选中某个候选项时触发。",
                code, demo);
    }
}
