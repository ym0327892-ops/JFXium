package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ToggleButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * ToggleButton 切换按钮 —— 基础 / 互斥组。
 */
public class ToggleButtonExamplePage extends VBoxAnt {

    public ToggleButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ToggleButton 切换按钮")
                .description("具有「按下 / 弹起」两态的按钮，独立使用或加入互斥组。")
                .sections(basicSection(), groupSection(), mandatorySection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                ToggleButtonAnt.create("加粗").selected(true).onChange(sel -> MessageAnt.info("加粗：" + sel)).build(),
                ToggleButtonAnt.create("斜体").onChange(sel -> MessageAnt.info("斜体：" + sel)).build(),
                ToggleButtonAnt.create("下划线").onChange(sel -> MessageAnt.info("下划线：" + sel)).build()
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
        Label result = TypographyAnt.text("当前视图：列表").build();
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
                Label result = TypographyAnt.text("当前视图：列表").build();
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

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 ToggleButton 的 5 个维度：大小 / 形状 / 文字 / 状态 / 禁用。
     *
     * <p>ToggleButtonAnt 是 Builder 模式（{@code build()} 返回 JavaFX 原生 {@link ToggleButton}），
     * 没有 Controller 暴露，所有属性（size / shape / disabled / selected / text）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— ToggleButton 节点重建，但开销极小（一个 Node），且保证所有属性变更生效。</p>
     *
     * <p>形状属性：Builder 不暴露 Shape enum，而是提供 {@code rounded()} / {@code square()} 两个无参方法，
     * DEFAULT 形状即不调用任何方法 —— 这里通过 entry 值 {@code default / rounded / square} 区分。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> size     = PlayGround.binder("default");
        Binder<String> shape    = PlayGround.binder("default");
        Binder<String> text     = PlayGround.binder("加粗");
        Binder<String> state    = PlayGround.binder("unchecked");
        Binder<String> disabled = PlayGround.binder("false");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            ToggleButtonAnt b = ToggleButtonAnt.create(text.get());
            applySize(b, size.get());
            applyShape(b, shape.get());
            b.selected("checked".equals(state.get()));
            b.disabled(parseBool(disabled.get()));
            return b.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 ToggleButton 的大小 / 形状 / 文字 / 状态 / 禁用 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("大小", PlayGround.segmented(size,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("形状", PlayGround.segmented(shape,
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("rounded", "圆角"),
                                PlayGround.entry("square",  "方角"))),
                        PlayGround.row("文字", PlayGround.textField(text, text.get(), "ToggleButton 文字")),
                        PlayGround.row("状态", PlayGround.segmented(state,
                                PlayGround.entry("unchecked", "未选"),
                                PlayGround.entry("checked",   "已选"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("false", "启用"),
                                PlayGround.entry("true",  "禁用")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v != null && "true".equalsIgnoreCase(v);
    }

    private static void applySize(ToggleButtonAnt b, String v) {
        if (v == null) { b.size(Size.DEFAULT); return; }
        switch (v) {
            case "small"  -> b.size(Size.SMALL);
            case "large"  -> b.size(Size.LARGE);
            default       -> b.size(Size.DEFAULT);
        }
    }

    private static void applyShape(ToggleButtonAnt b, String v) {
        if (v == null) return;
        switch (v) {
            case "rounded" -> b.rounded();
            case "square"  -> b.square();
            default        -> { /* default 形状：什么都不调 */ }
        }
    }
}
