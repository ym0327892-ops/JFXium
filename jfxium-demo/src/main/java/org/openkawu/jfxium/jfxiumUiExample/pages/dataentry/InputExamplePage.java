package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.SearchInputAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

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
                        placeholderSection(),
                        allowClearSection(),
                        playgroundSection()
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
                InputAnt.create().size(Size.SMALL).placeholder("Small").build(),
                InputAnt.create().placeholder("Default").build(),
                InputAnt.create().size(Size.LARGE).placeholder("Large").build()
        );
        String code = """
                InputAnt.create().size(Size.SMALL).placeholder("Small").build();
                InputAnt.create().placeholder("Default").build();
                InputAnt.create().size(Size.LARGE).placeholder("Large").build();
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

    private Node allowClearSection() {
        // 搜索框：清除按钮 + 回车提交，对标 Ant Design Input allowClear
        SearchInputAnt search = SearchInputAnt.create()
                .placeholder("搜索组件…")
                .text("Tabs")
                .build();
        Node demo = Demos.row(
                search,
                TypographyAnt.text("输入内容后出现清除按钮；回车触发 onSearch")
                        .type(TypographyAnt.TextColor.SECONDARY).build()
        );
        String code = """
                SearchInputAnt.create()
                        .placeholder("搜索组件…")
                        .onSearch(keyword -> System.out.println("搜索：" + keyword))
                        .build();
                """;
        return Demos.sectionWithCode("5. 可清除输入框（allowClear）",
                "有内容时显示清除按钮，回车提交搜索关键词；allowClear(false) 可关闭。",
                code, demo);
    }

    private Node playgroundSection() {
        Binder<String> sizeBinder = PlayGround.binder("default");
        Binder<String> stateBinder = PlayGround.binder("normal");
        Binder<String> placeholderBinder = PlayGround.binder("请输入内容");
        Supplier<Node> factory = () -> InputAnt.create()
                .size(parseSize(sizeBinder.get()))
                .disabled("disabled".equals(stateBinder.get()))
                .readOnly("readonly".equals(stateBinder.get()))
                .placeholder(placeholderBinder.get().isEmpty() ? "请输入内容" : placeholderBinder.get())
                .build();
        return Demos.section("6. 交互演示",
                "通过左侧控件实时改变输入框的尺寸、状态和占位符。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small", "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large", "大"))),
                        PlayGround.row("状态", PlayGround.segmented(stateBinder,
                                PlayGround.entry("normal", "正常"),
                                PlayGround.entry("disabled", "禁用"),
                                PlayGround.entry("readonly", "只读"))),
                        PlayGround.row("占位符", PlayGround.textField(placeholderBinder, "请输入内容", "输入占位符文本"))));
    }

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small" -> Size.SMALL;
            case "large" -> Size.LARGE;
            default -> Size.DEFAULT;
        };
    }
}
