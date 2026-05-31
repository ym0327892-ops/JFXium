package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Input 输入框 —— 基础 / 尺寸 / 状态 / 占位符。
 */
public class InputExamplePage extends VBoxAnt {

    public InputExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Input 输入框")
                .description("基础文本输入组件，支持多种尺寸和状态。")
                .sections(
                        basicSection(),
                        sizeSection(),
                        stateSection(),
                        placeholderSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                InputAnt.create().placeholder("请输入内容").build(),
                InputAnt.create().text("带默认值").build()
        );
        String code = """
                InputAnt.create().placeholder("请输入内容").build();
                InputAnt.create().text("带默认值").build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "最简单的输入框。", code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                InputAnt.create().size(InputAnt.Size.SMALL).placeholder("Small").build(),
                InputAnt.create().placeholder("Default").build(),
                InputAnt.create().size(InputAnt.Size.LARGE).placeholder("Large").build()
        );
        String code = """
                InputAnt.create().size(InputAnt.Size.SMALL).placeholder("Small").build();
                InputAnt.create().placeholder("Default").build();
                InputAnt.create().size(InputAnt.Size.LARGE).placeholder("Large").build();
                """;
        return Demos.sectionWithCode("2. 三种尺寸", "SMALL / DEFAULT / LARGE 三档。", code, demo);
    }

    private Node stateSection() {
        Node demo = Demos.row(
                InputAnt.create().disabled(true).text("禁用状态").build(),
                InputAnt.create().readOnly(true).text("只读状态").build()
        );
        String code = """
                InputAnt.create().disabled(true).text("禁用状态").build();
                InputAnt.create().readOnly(true).text("只读状态").build();
                """;
        return Demos.sectionWithCode("3. 禁用与只读",
                "disabled(true) 完全不可交互；readOnly(true) 可选中复制但不可编辑。",
                code, demo);
    }

    private Node placeholderSection() {
        Node demo = Demos.row(
                InputAnt.create().placeholder("用户名").build(),
                InputAnt.create().placeholder("请输入邮箱地址").build()
        );
        String code = """
                InputAnt.create().placeholder("用户名").build();
                InputAnt.create().placeholder("请输入邮箱地址").build();
                """;
        return Demos.sectionWithCode("4. 占位提示",
                "placeholder 在输入框为空时显示灰色提示文字。",
                code, demo);
    }
}
