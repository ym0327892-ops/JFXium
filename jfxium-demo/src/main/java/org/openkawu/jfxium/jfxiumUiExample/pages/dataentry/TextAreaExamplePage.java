package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.util.NumericUtils;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TextAreaAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * TextArea 多行输入 —— 基础 / 行数 / 禁用与只读。
 */
public class TextAreaExamplePage extends VBoxAnt {

    public TextAreaExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TextArea 多行输入")
                .description("多行文本输入框，适合备注、描述等较长内容。")
                .sections(basicSection(), rowsSection(), stateSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TextAreaAnt.create()
                .placeholder("请输入描述信息")
                .onChange(text -> MessageAnt.info("文本变化：" + text))
                .build();
        String code = """
                TextAreaAnt.create()
                        .placeholder("请输入描述信息")
                        .onChange(text -> MessageAnt.info("文本变化：" + text))
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

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 TextArea 的 3 个维度：行数 / 禁用 / 只读。
     *
     * <p>TextAreaAnt 继承自 JavaFX {@link javafx.scene.control.TextArea}，所有属性
     * （rows/disabled/editable/wrapText）都是直接修改 this，没有独立 Controller。采用
     * {@link PlayGround#rebindRebuild} —— 每次 binder 变化都重新 {@code create() + 链式 + build()}
     * 生成全新 TextArea 节点。文本框内已输入的内容在重建后<b>会丢失</b>，这与属性重建的语义一致
     * （只反映属性，不保留瞬时状态）。</p>
     */
    private Node playgroundSection() {
        Binder<String> rows     = PlayGround.binder("4");   // 2/4/6
        Binder<String> disabled = PlayGround.binder("no");  // yes/no
        Binder<String> readonly = PlayGround.binder("no");  // yes/no

        Supplier<Node> factory = () -> {
            int r = parseRows(rows.get());
            boolean d = "yes".equals(disabled.get());
            boolean ro = "yes".equals(readonly.get());
            return TextAreaAnt.create()
                    .placeholder("在右侧尝试输入、滚动观察行数变化")
                    .rows(r)
                    .disabled(d)
                    .editable(!ro)
                    .onChange(text -> MessageAnt.info("文本变化：" + text))
                    .build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 TextArea 的行数（小/中/大）、禁用状态、是否只读 —— 三种状态任意组合，右侧立刻看到效果。",
                PlayGround.rebindRebuild(factory, "行数 / 禁用 / 只读",
                        PlayGround.row("行数", PlayGround.segmented(rows,
                                PlayGround.entry("2", "2 行"),
                                PlayGround.entry("4", "4 行"),
                                PlayGround.entry("6", "6 行"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("no",  "启用"),
                                PlayGround.entry("yes", "禁用"))),
                        PlayGround.row("只读", PlayGround.segmented(readonly,
                                PlayGround.entry("no",  "可编辑"),
                                PlayGround.entry("yes", "只读")))));
    }

    private static int parseRows(String v) {
        if (v == null) return 4;
        try {
            int n = Integer.parseInt(v.trim());
            // 钳到 [1, 20],非法输入回退到 4
            return (int) NumericUtils.clamp(n, 1, 20, 4);
        } catch (NumberFormatException e) {
            return 4;
        }
    }
}
