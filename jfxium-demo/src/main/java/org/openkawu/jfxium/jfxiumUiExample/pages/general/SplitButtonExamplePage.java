package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import org.openkawu.jfxium.component.SplitButtonAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * SplitButton 分裂按钮 —— 基础（主操作 + 备选）/ 带菜单项。
 */
public class SplitButtonExamplePage extends VBoxAnt {

    public SplitButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("SplitButton 分裂按钮")
                .description("左侧主体触发默认动作，右侧箭头弹出备选动作菜单。")
                .sections(basicSection(), menuSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = SplitButtonAnt.create("保存")
                .onClick(e -> System.out.println("保存"))
                .item("保存并新建", e -> System.out.println("保存并新建"))
                .item("保存并退出", e -> System.out.println("保存并退出"))
                .build();
        String code = """
                SplitButtonAnt.create("保存")
                        .onClick(e -> save())
                        .item("保存并新建", e -> saveAndNew())
                        .item("保存并退出", e -> saveAndExit())
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "onClick 是主按钮点击，item 是右侧箭头下拉的备选动作。", code, demo);
    }

    private Node menuSection() {
        Node demo = SplitButtonAnt.create("运行")
                .onClick(e -> System.out.println("运行"))
                .item("调试", e -> System.out.println("调试"))
                .separator()
                .item("性能分析", e -> System.out.println("性能分析"))
                .itemDisabled("远程运行（不可用）")
                .build();
        String code = """
                SplitButtonAnt.create("运行")
                        .onClick(e -> run())
                        .item("调试", e -> debug())
                        .separator()
                        .item("性能分析", e -> profile())
                        .itemDisabled("远程运行（不可用）")
                        .build();
                """;
        return Demos.sectionWithCode("2. 带菜单项",
                "separator 分隔线、itemDisabled 禁用项组织更丰富的备选动作。", code, demo);
    }
}
