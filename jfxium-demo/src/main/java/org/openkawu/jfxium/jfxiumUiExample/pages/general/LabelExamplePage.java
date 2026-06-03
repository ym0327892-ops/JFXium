package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.control.IconAnt;

/**
 * Label 文本 —— 最基础的文本控件（继承式 + 链式 build）。
 *
 * <p>展示 LabelAnt 的 4 类用法：基础文本 / 语义色 / 换行 / 带图标。</p>
 */
public class LabelExamplePage extends VBoxAnt {

    public LabelExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Label 文本")
                .description("最基础的文本控件。继承自原生 Label，支持链式配置，也可被业务继承当基类。")
                .sections(
                        basicSection(),
                        typeSection(),
                        wrapSection(),
                        graphicSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                LabelAnt.create("普通文本").build()
        );
        String code = """
                Label text = LabelAnt.create("普通文本").build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "create(text) 创建文本，build() 返回自身（也是 Label，可省略）。", code, demo);
    }

    private Node typeSection() {
        Node demo = Demos.row(
                LabelAnt.create("默认").build(),
                LabelAnt.create("次要").secondary().build(),
                LabelAnt.create("成功").success().build(),
                LabelAnt.create("警告").warning().build(),
                LabelAnt.create("危险").danger().build(),
                LabelAnt.create("禁用").disabledColor().build()
        );
        String code = """
                LabelAnt.create("次要").secondary().build();
                LabelAnt.create("成功").success().build();
                LabelAnt.create("警告").warning().build();
                LabelAnt.create("危险").danger().build();
                LabelAnt.create("禁用").disabledColor().build();
                """;
        return Demos.sectionWithCode("2. 语义色",
                "文字色走 styleClass（复用 typography 语义），随主题自动适配，不硬编码颜色。", code, demo);
    }

    private Node wrapSection() {
        LabelAnt wrapped = LabelAnt.create(
                "这是一段较长的文本，开启 wrap(true) 后会在容器宽度不足时自动换行，"
                        + "而不是被裁切或溢出容器。适合放在卡片、表单说明等场景。")
                .wrap(true)
                .build();
        wrapped.setMaxWidth(360);
        String code = """
                LabelAnt label = LabelAnt.create("较长的文本……")
                        .wrap(true)
                        .build();
                label.setMaxWidth(360);
                """;
        return Demos.sectionWithCode("3. 自动换行",
                "wrap(true) 开启文本换行，配合 maxWidth 约束宽度。", code, wrapped);
    }

    private Node graphicSection() {
        Node demo = Demos.row(
                LabelAnt.create("带图标", IconAnt.path(IconAnt.Path.HOME, 16)).build(),
                LabelAnt.create("图标在右")
                        .graphic(IconAnt.path(IconAnt.Path.BELL, 16))
                        .contentDisplay(ContentDisplay.RIGHT)
                        .build()
        );
        String code = """
                // 图标在左（默认）
                LabelAnt.create("带图标", IconAnt.path(IconAnt.Path.HOME, 16)).build();

                // 图标在右
                LabelAnt.create("图标在右")
                        .graphic(IconAnt.path(IconAnt.Path.BELL, 16))
                        .contentDisplay(ContentDisplay.RIGHT)
                        .build();
                """;
        return Demos.sectionWithCode("4. 带图标",
                "graphic() 设置图标节点，contentDisplay() 控制图标相对文字的位置。", code, demo);
    }
}
