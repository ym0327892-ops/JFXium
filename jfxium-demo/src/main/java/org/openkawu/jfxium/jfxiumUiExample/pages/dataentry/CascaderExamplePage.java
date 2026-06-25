package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.CascaderAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Cascader 级联选择 —— 基础多级 / 自定义占位符。
 */
public class CascaderExamplePage extends VBoxAnt {

    public CascaderExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Cascader 级联选择")
                .description("从一组相关联的数据集中进行选择，逐级展开下一级选项。")
                .sections(basicSection(), placeholderSection(), valueSection(), playgroundSection())
                .padding(24)
                .build());
    }

    /** 构造一份「省 / 市 / 区」三级示例数据。 */
    private static List<CascaderAnt.Option> regionOptions() {
        return List.of(
                new CascaderAnt.Option("zhejiang", "浙江", List.of(
                        new CascaderAnt.Option("hangzhou", "杭州", List.of(
                                new CascaderAnt.Option("xihu", "西湖区"),
                                new CascaderAnt.Option("yuhang", "余杭区"))),
                        new CascaderAnt.Option("ningbo", "宁波", List.of(
                                new CascaderAnt.Option("haishu", "海曙区"))))),
                new CascaderAnt.Option("jiangsu", "江苏", List.of(
                        new CascaderAnt.Option("nanjing", "南京", List.of(
                                new CascaderAnt.Option("xuanwu", "玄武区"))))));
    }

    private Node basicSection() {
        Node demo = CascaderAnt.create()
                .options(regionOptions())
                .onChange(values -> MessageAnt.info("选中路径：" + String.join(" / ", values)))
                .build();
        String code = """
                CascaderAnt.create()
                        .options(List.of(
                                new CascaderAnt.Option("zhejiang", "浙江", List.of(
                                        new CascaderAnt.Option("hangzhou", "杭州", List.of(
                                                new CascaderAnt.Option("xihu", "西湖区")))))))
                        .onChange(values -> MessageAnt.info("选中路径：" + String.join(" / ", values)))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "Option 嵌套 children 形成多级；点击逐级展开，叶子节点选中后回填路径。",
                code, demo);
    }

    private Node placeholderSection() {
        Node demo = CascaderAnt.create()
                .options(regionOptions())
                .placeholder("请选择所在地区")
                .build();
        String code = """
                CascaderAnt.create()
                        .options(regionOptions())
                        .placeholder("请选择所在地区")
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义占位符",
                "placeholder(...) 自定义未选择时的灰色提示文字。",
                code, demo);
    }

    /**
     * 3. 获取选中值 —— onChange 回调拿到的是「值路径」（value 列表，非 label）。
     *
     * <p>注意：CascaderAnt.onChange 给出的是各级 Option 的 value（如 [zhejiang, hangzhou, xihu]），
     * 而输入框里回填的是 label 路径（浙江 / 杭州 / 西湖区）。这里把 value 列表 join 后显示，
     * 让用户确认拿到的是「值」而不是显示文案。</p>
     */
    private Node valueSection() {
        Label result = TypographyAnt.text("选中值：(未选择)").build();
        Node cascader = CascaderAnt.create()
                .options(regionOptions())
                .placeholder("请选择所在地区")
                .onChange(values -> result.setText("选中值：" + String.join(" / ", values)))
                .build();
        Node demo = Demos.column(cascader, result);
        String code = """
                Label result = TypographyAnt.text("选中值：(未选择)").build();
                CascaderAnt.create()
                        .options(regionOptions())
                        .placeholder("请选择所在地区")
                        // values 是各级 value（如 [zhejiang, hangzhou, xihu]），不是 label
                        .onChange(values -> result.setText("选中值：" + String.join(" / ", values)))
                        .build();
                """;
        return Demos.sectionWithCode("3. 获取选中值",
                "onChange(values -> ...) 在选中叶子节点时回调，给出各级 value 列表（注意是 value 编码，不是 label 文案）。",
                code, demo);
    }

    /**
     * 4. 交互演示 —— 通过左侧文本框实时改变 Cascader 的占位符。
     *
     * <p>CascaderAnt 主要是多级选择逻辑，可玩维度有限；这里演示占位符的动态切换效果。</p>
     */
    private Node playgroundSection() {
        Binder<String> placeholderBinder = PlayGround.binder("请选择所在地区");

        Supplier<Node> factory = () -> {
            String p = placeholderBinder.get();
            return CascaderAnt.create()
                    .options(regionOptions())
                    .placeholder(p.isEmpty() ? "请选择" : p)
                    .onChange(values -> MessageAnt.info("选中路径：" + String.join(" / ", values)))
                    .build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧文本框实时改变 Cascader 的占位符 —— CascaderAnt 主要是多级选择逻辑，可玩维度有限。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("占位符", PlayGround.textField(placeholderBinder, "请选择所在地区", "输入占位符文本"))));
    }
}
