package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.ChoiceBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * ChoiceBox 选择框 —— 基础 / 尺寸 / 禁用 / 回调。
 */
public class ChoiceBoxExamplePage extends VBoxAnt {

    public ChoiceBoxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ChoiceBox 选择框")
                .description("轻量级下拉选择控件，比 ComboBox 更精简，适用选项较少的场景（如主题切换、语言选择）。")
                .sections(
                        basicSection(),
                        sizeSection(),
                        disabledSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                Demos.labeled("主题", ChoiceBoxAnt.<String>create()
                        .items("亮色", "暗色", "自动")
                        .value("亮色")
                        .onSelect(v -> System.out.println("选中：" + v))
                        .build()),
                Demos.labeled("语言", ChoiceBoxAnt.<String>create()
                        .items("简体中文", "English", "日本語")
                        .value("简体中文")
                        .build())
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("亮色", "暗色", "自动")
                    .value("亮色")
                    .onSelect(v -> System.out.println(v))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "items() 设置选项；value() 设置默认选中；onSelect() 监听选中变更。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                ChoiceBoxAnt.<String>create()
                        .items("Small", "Option 2")
                        .value("Small")
                        .size(ChoiceBoxAnt.Size.SMALL)
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("Default", "Option 2")
                        .value("Default")
                        .size(ChoiceBoxAnt.Size.DEFAULT)
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("Large", "Option 2")
                        .value("Large")
                        .size(ChoiceBoxAnt.Size.LARGE)
                        .build()
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("Small", "Option 2")
                    .size(ChoiceBoxAnt.Size.SMALL)
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("Default", "Option 2")
                    .size(ChoiceBoxAnt.Size.DEFAULT)
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("Large", "Option 2")
                    .size(ChoiceBoxAnt.Size.LARGE)
                    .build();
                """;
        return Demos.sectionWithCode("2. 尺寸",
                "size(SMALL/DEFAULT/LARGE) 三种尺寸，与其他控件一致。",
                code, demo);
    }

    private Node disabledSection() {
        Node demo = Demos.row(
                ChoiceBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .value("选项一")
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .value("选项一")
                        .disabled(true)
                        .build()
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("选项一", "选项二", "选项三")
                    .value("选项一")
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("选项一", "选项二", "选项三")
                    .disabled(true)
                    .build();
                """;
        return Demos.sectionWithCode("3. 禁用状态",
                "disabled(true) 禁用选择框，单击不弹出下拉。",
                code, demo);
    }
}
