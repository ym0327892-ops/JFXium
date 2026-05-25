package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.MenuButtonAnt;
import org.openkawu.jfxium.component.SplitButtonAnt;
import org.openkawu.jfxium.component.ToggleButtonAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * 按钮族（M19.6）：ToggleButton / MenuButton / SplitButton 集中展示。
 *
 * <p>三个组件都是按钮家族成员但语义不同：</p>
 * <ul>
 *   <li>ToggleButton —— 按下/弹起切换（独立或互斥组）</li>
 *   <li>MenuButton —— 整体点击弹下拉菜单</li>
 *   <li>SplitButton —— 左主操作 + 右下拉</li>
 * </ul>
 */
public class ButtonGroupPage implements ShowcasePage {

    @Override public String   key()      { return "button-group"; }
    @Override public String   title()    { return "Toggle/Menu/SplitButton"; }
    @Override public Category category() { return Category.GENERAL; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Toggle / Menu / SplitButton");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("按钮族三种特殊形态：切换按钮、菜单按钮、分割按钮（M19.6 新增）");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionToggleStandalone(),
                        sectionToggleGroup(),
                        sectionToggleSizes(),
                        sectionMenuButton(),
                        sectionMenuButtonSizes(),
                        sectionMenuButtonArrowStyles(),
                        sectionSplitButton(),
                        sectionSplitButtonSizes()
                )
                .build();
    }

    // ============================================================
    // 1. ToggleButton 独立切换
    // ============================================================
    private Node sectionToggleStandalone() {
        Node bold = ToggleButtonAnt.create("加粗")
                .selected(true)
                .onChange(sel -> System.out.println("加粗：" + sel))
                .build();
        Node italic = ToggleButtonAnt.create("斜体").build();
        Node underline = ToggleButtonAnt.create("下划线").build();

        HBox row = HBoxBuilder.create().spacing(8).children(bold, italic, underline).build();

        return ShowcaseSection.create()
                .title("场景 1：ToggleButton 独立切换")
                .description("各自独立的按下/弹起状态，常见于编辑器工具栏（加粗/斜体/下划线）")
                .demo(row)
                .code("""
                        ToggleButton bold = ToggleButtonAnt.create("加粗")
                            .selected(true)
                            .onChange(sel -> System.out.println("加粗：" + sel))
                            .build();
                        ToggleButton italic = ToggleButtonAnt.create("斜体").build();
                        ToggleButton underline = ToggleButtonAnt.create("下划线").build();
                        """)
                .build();
    }

    // ============================================================
    // 2. ToggleButton 互斥组（ToggleGroup）
    // ============================================================
    private Node sectionToggleGroup() {
        ToggleGroup viewGroup = new ToggleGroup();
        Node listView = ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build();
        Node cardView = ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build();
        Node detailView = ToggleButtonAnt.create("详情").toggleGroup(viewGroup).build();

        HBox row = HBoxBuilder.create().spacing(0).children(listView, cardView, detailView).build();

        return ShowcaseSection.create()
                .title("场景 2：ToggleButton 互斥组（ToggleGroup）")
                .description("加入同一 ToggleGroup 实现互斥，类似 Radio 但视觉上是按钮形态——admin 视图切换的标配")
                .demo(row)
                .code("""
                        ToggleGroup viewGroup = new ToggleGroup();
                        ToggleButton listView = ToggleButtonAnt.create("列表")
                            .toggleGroup(viewGroup).selected(true).build();
                        ToggleButton cardView = ToggleButtonAnt.create("卡片")
                            .toggleGroup(viewGroup).build();
                        ToggleButton detailView = ToggleButtonAnt.create("详情")
                            .toggleGroup(viewGroup).build();
                        """)
                .build();
    }

    // ============================================================
    // 3. ToggleButton 三档尺寸
    // ============================================================
    private Node sectionToggleSizes() {
        Node small = ToggleButtonAnt.create("Small").size(ToggleButtonAnt.Size.SMALL).build();
        Node def = ToggleButtonAnt.create("Default").build();
        Node large = ToggleButtonAnt.create("Large").size(ToggleButtonAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 3：ToggleButton 三档尺寸")
                .description("与 Button/Input 完全一致的 SMALL / DEFAULT / LARGE 三档")
                .demo(row)
                .code("""
                        ToggleButtonAnt.create("Small").size(ToggleButtonAnt.Size.SMALL).build();
                        ToggleButtonAnt.create("Default").build();
                        ToggleButtonAnt.create("Large").size(ToggleButtonAnt.Size.LARGE).build();
                        """)
                .build();
    }

    // ============================================================
    // 4. MenuButton 基础
    // ============================================================
    private Node sectionMenuButton() {
        Node bulkActions = MenuButtonAnt.create("批量操作")
                .item("导出", e -> System.out.println("导出"))
                .item("删除", e -> System.out.println("删除"))
                .separator()
                .item("移动到...", e -> System.out.println("移动"))
                .itemDisabled("（仅管理员）归档")
                .build();

        Node moreBtn = MenuButtonAnt.create("更多")
                .item("设置", e -> System.out.println("设置"))
                .item("帮助", e -> System.out.println("帮助"))
                .item("关于", e -> System.out.println("关于"))
                .build();

        HBox row = HBoxBuilder.create().spacing(12).children(bulkActions, moreBtn).build();

        return ShowcaseSection.create()
                .title("场景 4：MenuButton（菜单按钮）")
                .description("外观像普通按钮，点击弹出下拉菜单选择其中一项操作——admin 高频")
                .demo(row)
                .code("""
                        MenuButton bulkActions = MenuButtonAnt.create("批量操作")
                            .item("导出", e -> exportSelected())
                            .item("删除", e -> deleteSelected())
                            .separator()
                            .item("移动到...", e -> moveSelected())
                            .itemDisabled("（仅管理员）归档")
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 5. MenuButton 三档尺寸
    // ============================================================
    private Node sectionMenuButtonSizes() {
        Node small = MenuButtonAnt.create("小").size(MenuButtonAnt.Size.SMALL)
                .item("项 1", null).item("项 2", null).build();
        Node def = MenuButtonAnt.create("中")
                .item("项 1", null).item("项 2", null).build();
        Node large = MenuButtonAnt.create("大").size(MenuButtonAnt.Size.LARGE)
                .item("项 1", null).item("项 2", null).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 5：MenuButton 三档尺寸")
                .description("与 Button 完全一致的 SMALL / DEFAULT / LARGE 三档")
                .demo(row)
                .code("""
                        MenuButtonAnt.create("小").size(MenuButtonAnt.Size.SMALL)...build();
                        MenuButtonAnt.create("中")...build();
                        MenuButtonAnt.create("大").size(MenuButtonAnt.Size.LARGE)...build();
                        """)
                .build();
    }

    // ============================================================
    // 6. MenuButton 箭头样式（CHEVRON / TRIANGLE / NONE）
    // ============================================================
    private Node sectionMenuButtonArrowStyles() {
        Node chevron = MenuButtonAnt.create("Chevron（默认）")
                .arrowStyle(MenuButtonAnt.ArrowStyle.CHEVRON)
                .item("项 1", null).item("项 2", null).build();

        Node triangle = MenuButtonAnt.create("Triangle")
                .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                .item("项 1", null).item("项 2", null).build();

        Node noArrow = MenuButtonAnt.create("无箭头")
                .noArrow()
                .item("项 1", null).item("项 2", null).build();

        HBox row = HBoxBuilder.create().spacing(8).children(chevron, triangle, noArrow).build();

        return ShowcaseSection.create()
                .title("场景 6：MenuButton 箭头样式")
                .description("CHEVRON（Ant Design 风格细 V 形，默认）/ TRIANGLE（实心三角，AtlantaFX 风格）/ NONE（不显示箭头，常用于纯图标按钮）")
                .demo(row)
                .code("""
                        // Ant Design 风格（默认）
                        MenuButtonAnt.create("Chevron").arrowStyle(MenuButtonAnt.ArrowStyle.CHEVRON)...build();

                        // 实心三角
                        MenuButtonAnt.create("Triangle").arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)...build();

                        // 不显示箭头（语法糖 .noArrow()）
                        MenuButtonAnt.create("无箭头").noArrow()...build();
                        """)
                .build();
    }

    // ============================================================
    // 7. SplitButton 基础
    // ============================================================
    private Node sectionSplitButton() {
        Node save = SplitButtonAnt.create("保存")
                .onClick(e -> System.out.println("保存（主操作）"))
                .item("保存并新建", e -> System.out.println("保存并新建"))
                .item("保存并退出", e -> System.out.println("保存并退出"))
                .build();

        Node run = SplitButtonAnt.create("运行")
                .onClick(e -> System.out.println("运行"))
                .item("调试", e -> System.out.println("调试"))
                .item("性能分析", e -> System.out.println("性能分析"))
                .separator()
                .item("配置...", e -> System.out.println("配置"))
                .build();

        HBox row = HBoxBuilder.create().spacing(12).children(save, run).build();

        return ShowcaseSection.create()
                .title("场景 7：SplitButton（分割按钮）")
                .description("左侧主操作 + 右侧下拉箭头——常见于「保存/保存并...」、「运行/调试」等同类操作")
                .demo(row)
                .code("""
                        SplitMenuButton save = SplitButtonAnt.create("保存")
                            .onClick(e -> save())                      // 主按钮点击
                            .item("保存并新建", e -> saveAndNew())      // 下拉项 1
                            .item("保存并退出", e -> saveAndExit())     // 下拉项 2
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 8. SplitButton 三档尺寸
    // ============================================================
    private Node sectionSplitButtonSizes() {
        Node small = SplitButtonAnt.create("Small").size(SplitButtonAnt.Size.SMALL)
                .item("项 1", null).item("项 2", null).build();
        Node def = SplitButtonAnt.create("Default")
                .item("项 1", null).item("项 2", null).build();
        Node large = SplitButtonAnt.create("Large").size(SplitButtonAnt.Size.LARGE)
                .item("项 1", null).item("项 2", null).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 8：SplitButton 三档尺寸")
                .description("与 Button 完全一致的 SMALL / DEFAULT / LARGE 三档")
                .demo(row)
                .code("""
                        SplitButtonAnt.create("Small").size(SplitButtonAnt.Size.SMALL)...build();
                        SplitButtonAnt.create("Default")...build();
                        SplitButtonAnt.create("Large").size(SplitButtonAnt.Size.LARGE)...build();
                        """)
                .build();
    }
}
