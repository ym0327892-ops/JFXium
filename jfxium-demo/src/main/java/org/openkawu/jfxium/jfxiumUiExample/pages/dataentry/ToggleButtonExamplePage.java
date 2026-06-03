package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ToggleButtonAnt;

/**
 * ToggleButton 切换按钮 —— 基础 / 互斥组。
 */
public class ToggleButtonExamplePage extends VBoxAnt {

    public ToggleButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ToggleButton 切换按钮")
                .description("具有「按下 / 弹起」两态的按钮，独立使用或加入互斥组。")
                .sections(basicSection(), groupSection(), mandatorySection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                ToggleButtonAnt.create("加粗").selected(true).onChange(sel -> {}).build(),
                ToggleButtonAnt.create("斜体").onChange(sel -> {}).build(),
                ToggleButtonAnt.create("下划线").onChange(sel -> {}).build()
        );
        String code = """
                ToggleButtonAnt.create("加粗").selected(true).onChange(sel -> applyBold(sel)).build();
                ToggleButtonAnt.create("斜体").onChange(sel -> applyItalic(sel)).build();
                ToggleButtonAnt.create("下划线").onChange(sel -> applyUnderline(sel)).build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "每个按钮独立切换，selected(true) 设置初始选中态，onChange 监听切换。",
                code, demo);
    }

    private Node groupSection() {
        ToggleGroup viewGroup = new ToggleGroup();
        Node demo = Demos.row(
                ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build(),
                ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build(),
                ToggleButtonAnt.create("表格").toggleGroup(viewGroup).build()
        );
        String code = """
                ToggleGroup viewGroup = new ToggleGroup();
                ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build();
                ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build();
                ToggleButtonAnt.create("表格").toggleGroup(viewGroup).build();
                """;
        return Demos.sectionWithCode("2. 互斥组",
                "加入同一个 ToggleGroup，组内只能选中一个（与 RadioButton 同模式）。",
                code, demo);
    }

    /**
     * 3. 必选模式（不可全不选）—— 用 ToggleButtonAnt.mandatoryGroup() 替代 new ToggleGroup()。
     *
     * <p>原生 ToggleGroup 允许点击当前选中项把它取消，导致「全不选」。admin 视图切换器
     * （列表/卡片/表格）要求永远选中一个，mandatoryGroup() 保证点击当前项不会取消选中。
     * 结果 Label 通过每个按钮的 onChange（选中时）显示当前选择。</p>
     */
    private Node mandatorySection() {
        Label result = new Label("当前视图：列表");
        // 关键：用 mandatoryGroup() 而不是 new ToggleGroup()
        ToggleGroup viewGroup = ToggleButtonAnt.mandatoryGroup();
        Node demo = Demos.column(
                Demos.row(
                        ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true)
                                .onChange(sel -> { if (sel) result.setText("当前视图：列表"); }).build(),
                        ToggleButtonAnt.create("卡片").toggleGroup(viewGroup)
                                .onChange(sel -> { if (sel) result.setText("当前视图：卡片"); }).build(),
                        ToggleButtonAnt.create("表格").toggleGroup(viewGroup)
                                .onChange(sel -> { if (sel) result.setText("当前视图：表格"); }).build()
                ),
                result
        );
        String code = """
                Label result = new Label("当前视图：列表");
                // 用 mandatoryGroup() 保证「永远选中一个」，点当前项不会取消
                ToggleGroup viewGroup = ToggleButtonAnt.mandatoryGroup();
                ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true)
                        .onChange(sel -> { if (sel) result.setText("当前视图：列表"); }).build();
                ToggleButtonAnt.create("卡片").toggleGroup(viewGroup)
                        .onChange(sel -> { if (sel) result.setText("当前视图：卡片"); }).build();
                ToggleButtonAnt.create("表格").toggleGroup(viewGroup)
                        .onChange(sel -> { if (sel) result.setText("当前视图：表格"); }).build();
                """;
        return Demos.sectionWithCode("3. 必选模式（不可全不选）",
                "用 ToggleButtonAnt.mandatoryGroup() 替代 new ToggleGroup()：点击当前选中项不会取消，始终保留一个选中。结果 Label 显示当前选择。",
                code, demo);
    }
}
