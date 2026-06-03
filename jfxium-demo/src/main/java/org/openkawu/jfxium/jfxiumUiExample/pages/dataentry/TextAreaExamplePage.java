package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TextAreaAnt;

/**
 * TextArea 多行输入 —— 基础 / 行数 / 禁用与只读。
 */
public class TextAreaExamplePage extends VBoxAnt {

    public TextAreaExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TextArea 多行输入")
                .description("多行文本输入框，适合备注、描述等较长内容。")
                .sections(basicSection(), rowsSection(), stateSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TextAreaAnt.create()
                .placeholder("请输入描述信息")
                .onChange(text -> {})
                .build();
        String code = """
                TextAreaAnt.create()
                        .placeholder("请输入描述信息")
                        .onChange(text -> System.out.println(text))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "placeholder(...) 设置占位提示，onChange 监听内容变化。",
                code, demo);
    }

    private Node rowsSection() {
        Node demo = Demos.row(
                TextAreaAnt.create().rows(2).placeholder("2 行").build(),
                TextAreaAnt.create().rows(5).placeholder("5 行").build()
        );
        String code = """
                TextAreaAnt.create().rows(2).placeholder("2 行").build();
                TextAreaAnt.create().rows(5).placeholder("5 行").build();
                """;
        return Demos.sectionWithCode("2. 行数",
                "rows(...) 设置默认可见行数（pref row count）。",
                code, demo);
    }

    private Node stateSection() {
        Node demo = Demos.row(
                TextAreaAnt.create().disabled(true).text("禁用状态").build(),
                TextAreaAnt.create().editable(false).text("只读状态").build()
        );
        String code = """
                TextAreaAnt.create().disabled(true).text("禁用状态").build();
                TextAreaAnt.create().editable(false).text("只读状态").build();
                """;
        return Demos.sectionWithCode("3. 禁用与只读",
                "disabled(true) 完全不可交互；editable(false) 可选中复制但不可编辑。",
                code, demo);
    }
}
