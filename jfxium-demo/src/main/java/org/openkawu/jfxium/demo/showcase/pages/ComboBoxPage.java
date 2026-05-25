package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ComboBoxAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * ComboBox 展示页（M19.6.2）。
 */
public class ComboBoxPage implements ShowcasePage {

    @Override public String   key()      { return "combo-box"; }
    @Override public String   title()    { return "ComboBox 下拉选择"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    public record User(String name, String role) {
        @Override public String toString() { return name + " · " + role; }
    }

    @Override
    public Node getView() {
        Label pageTitle = new Label("ComboBox 下拉选择");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("最常用下拉选择框。支持泛型、可编辑、三档尺寸（M19.5 加）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionPlaceholder(),
                        sectionEditable(),
                        sectionGenericObject(),
                        sectionSizes(),
                        sectionDisabled()
                )
                .build();
    }

    private Node sectionBasic() {
        ComboBox<String> cb = ComboBoxAnt.<String>create()
                .items("北京", "上海", "广州", "深圳")
                .value("北京")
                .onChange(v -> System.out.println("选中：" + v))
                .build();
        cb.setPrefWidth(180);

        return ShowcaseSection.create()
                .title("场景 1：基础用法（String 泛型）")
                .description("最常见的下拉选择——挑一个城市")
                .demo(cb)
                .code("""
                        ComboBox<String> cb = ComboBoxAnt.<String>create()
                            .items("北京", "上海", "广州", "深圳")
                            .value("北京")
                            .onChange(v -> reload(v))
                            .build();
                        """)
                .build();
    }

    private Node sectionPlaceholder() {
        ComboBox<String> cb = ComboBoxAnt.<String>create()
                .items("管理员", "编辑", "访客")
                .placeholder("请选择角色")
                .build();
        cb.setPrefWidth(180);

        return ShowcaseSection.create()
                .title("场景 2：占位符（未选中时显示）")
                .description("未选中任何项时显示 placeholder 提示文字")
                .demo(cb)
                .code("""
                        ComboBox<String> cb = ComboBoxAnt.<String>create()
                            .items("管理员", "编辑", "访客")
                            .placeholder("请选择角色")
                            .build();
                        """)
                .build();
    }

    private Node sectionEditable() {
        ComboBox<String> cb = ComboBoxAnt.<String>create()
                .items("React", "Vue", "Angular", "Svelte")
                .placeholder("可输入或选择")
                .editable(true)
                .build();
        cb.setPrefWidth(220);

        return ShowcaseSection.create()
                .title("场景 3：可编辑（editable）")
                .description("editable=true 时用户可以直接输入自定义值，回车提交")
                .demo(cb)
                .code("""
                        ComboBox<String> cb = ComboBoxAnt.<String>create()
                            .items("React", "Vue", "Angular", "Svelte")
                            .placeholder("可输入或选择")
                            .editable(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionGenericObject() {
        ComboBox<User> cb = ComboBoxAnt.<User>create()
                .items(
                        new User("张三", "管理员"),
                        new User("李四", "编辑"),
                        new User("王五", "访客")
                )
                .placeholder("选择用户")
                .onChange(u -> {
                    if (u != null) System.out.println("选中：" + u.name());
                })
                .build();
        cb.setPrefWidth(220);

        return ShowcaseSection.create()
                .title("场景 4：自定义对象（泛型）")
                .description("ComboBox 是泛型容器，可以装任意自定义对象——下拉显示走 toString()")
                .demo(cb)
                .code("""
                        record User(String name, String role) {
                            @Override public String toString() { return name + " · " + role; }
                        }

                        ComboBox<User> cb = ComboBoxAnt.<User>create()
                            .items(new User("张三", "管理员"), new User("李四", "编辑"))
                            .onChange(u -> System.out.println(u.name()))
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        ComboBox<String> small = ComboBoxAnt.<String>create()
                .items("A", "B", "C").value("A")
                .size(ComboBoxAnt.Size.SMALL).build();
        small.setPrefWidth(120);

        ComboBox<String> def = ComboBoxAnt.<String>create()
                .items("A", "B", "C").value("A").build();
        def.setPrefWidth(140);

        ComboBox<String> large = ComboBoxAnt.<String>create()
                .items("A", "B", "C").value("A")
                .size(ComboBoxAnt.Size.LARGE).build();
        large.setPrefWidth(160);

        HBox row = HBoxBuilder.create().spacing(12).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 5：三档尺寸（M19.5 修正高度对齐 + 新增 Size API）")
                .description("与 Input/Button 完全一致的 SMALL / DEFAULT / LARGE 三档——同一行混用三者高度齐平")
                .demo(row)
                .code("""
                        ComboBoxAnt.<String>create().items("A","B").size(ComboBoxAnt.Size.SMALL).build();
                        ComboBoxAnt.<String>create().items("A","B").build();
                        ComboBoxAnt.<String>create().items("A","B").size(ComboBoxAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionDisabled() {
        ComboBox<String> enabled = ComboBoxAnt.<String>create()
                .items("A", "B").value("A").build();
        enabled.setPrefWidth(140);
        ComboBox<String> disabled = ComboBoxAnt.<String>create()
                .items("A", "B").value("A").disabled(true).build();
        disabled.setPrefWidth(140);

        HBox row = HBoxBuilder.create().spacing(12).children(enabled, disabled).build();

        return ShowcaseSection.create()
                .title("场景 6：禁用态")
                .description("disabled 状态下整体变浅，不响应点击")
                .demo(row)
                .code("""
                        ComboBoxAnt.<String>create().items("A","B").disabled(true).build();
                        """)
                .build();
    }
}
