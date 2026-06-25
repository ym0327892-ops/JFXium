package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SwitchAnt;

/**
 * Switch 开关 —— 基础 / 形状 / 文字与禁用 / 交互演示。
 */
public class SwitchExamplePage extends VBoxAnt {

    public SwitchExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Switch 开关")
                .description("用于切换两种状态（开/关）的开关组件。")
                .sections(
                        basicSection(),
                        shapeSection(),
                        disabledTextSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SwitchAnt.create().build(),
                SwitchAnt.create().selected(true).build()
        );
        String code = """
                SwitchAnt.create().build();
                SwitchAnt.create().selected(true).build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认关闭和默认开启。", code, demo);
    }

    private Node shapeSection() {
        Node demo = Demos.row(
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).selected(true).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).selected(true).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).selected(true).build()
        );
        String code = """
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).selected(true).build();
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).selected(true).build();
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).selected(true).build();
                """;
        return Demos.sectionWithCode("2. 形状",
                "PILL（胶囊，默认）/ ROUNDED（圆角）/ SQUARE（方角）。",
                code, demo);
    }

    private Node disabledTextSection() {
        Node demo = Demos.row(
                SwitchAnt.create().disabled(true).selected(true).build(),
                SwitchAnt.create().checkedText("开").uncheckedText("关").selected(true).build(),
                SwitchAnt.create().checkedText("开").uncheckedText("关").build()
        );
        String code = """
                SwitchAnt.create().disabled(true).selected(true).build();
                SwitchAnt.create().checkedText("开").uncheckedText("关").selected(true).build();
                SwitchAnt.create().checkedText("开").uncheckedText("关").build();
                """;
        return Demos.sectionWithCode("3. 禁用与文字",
                "disabled(true) 禁用；checkedText/uncheckedText 显示开关状态文字。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Switch 的 5 个维度：开关状态 / 形状 / 禁用 / 开文字 / 关文字。
     *
     * <p>SwitchAnt 没有 Controller（没有 controllerOf 入口），且 Builder 字段
     * （shape / disabled / checkedText / uncheckedText）全部是 build-time。
     * 所以采用 {@link PlayGround#rebuildRebuild}：每次值变更都重新 {@code build()}
     * —— Switch 节点本身重建，但尺寸较小（HBox + 几个 Region），性能可接受，
     * 且保证所有属性变更都生效（这是 Controller 模式做不到的，因为 Controller 没有的话
     * 无法 setShape）。</p>
     *
     * <p>说明：selected 是「逻辑状态」本可以通过 callback 体现，但 SwitchAnt 也没有
     * 暴露 onChange 之外的 Controller，所以一并走 rebuild —— 视觉上反而更直接：
     * 选 关 / 开 后立刻看到 thumb 滑到对应位置 + 颜色变化。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> selected     = PlayGround.binder("true");
        Binder<String> shape        = PlayGround.binder("pill");
        Binder<String> disabled     = PlayGround.binder("false");
        Binder<String> checkedText  = PlayGround.binder("ON");
        Binder<String> uncheckedText = PlayGround.binder("OFF");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> SwitchAnt.create()
                .selected(parseBool(selected.get()))
                .shape(parseShape(shape.get()))
                .disabled(parseBool(disabled.get()))
                .checkedText(checkedText.get())
                .uncheckedText(uncheckedText.get())
                .build();

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Switch 的开关状态 / 形状 / 禁用 / 文字 —— SwitchAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("开关状态", PlayGround.segmented(selected,
                                PlayGround.entry("true",  "开"),
                                PlayGround.entry("false", "关"))),
                        PlayGround.row("形状", PlayGround.segmented(shape,
                                PlayGround.entry("pill",    "胶囊"),
                                PlayGround.entry("rounded", "圆角"),
                                PlayGround.entry("square",  "方角"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("false", "启用"),
                                PlayGround.entry("true",  "禁用"))),
                        PlayGround.row("开文字", PlayGround.textField(checkedText, checkedText.get(), "开关 ON 时显示")),
                        PlayGround.row("关文字", PlayGround.textField(uncheckedText, uncheckedText.get(), "开关 OFF 时显示"))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v == null || "true".equalsIgnoreCase(v);
    }

    private static SwitchAnt.Shape parseShape(String v) {
        if (v == null) return SwitchAnt.Shape.PILL;
        return switch (v) {
            case "rounded" -> SwitchAnt.Shape.ROUNDED;
            case "square"  -> SwitchAnt.Shape.SQUARE;
            default        -> SwitchAnt.Shape.PILL;
        };
    }
}
