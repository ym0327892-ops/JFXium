package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.SpinnerAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.SpinAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Spinner 旋转加载 —— 简化入口，内部委托 SpinAnt SPINNER 模式。
 */
public class SpinnerExamplePage extends VBoxAnt {

    public SpinnerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spinner 旋转加载")
                .description("SpinAnt 的简化入口，size() 自动映射到 SMALL/DEFAULT/LARGE 三档。")
                .sections(
                        basicSection(),
                        sizeSection(),
                        vsSection(),
                        toggleSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        // SpinnerAnt 内部使用 SpinAnt 的 SPINNER 模式 + Timeline 自驱动动画
        Node demo = Demos.row(
                SpinnerAnt.create().build(),
                SpinnerAnt.create().size(48).build(),
                SpinnerAnt.create().size(64).build()
        );
        String code = """
                SpinnerAnt.create().build();
                SpinnerAnt.create().size(48).build();
                SpinnerAnt.create().size(64).build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "SpinnerAnt.create().build() 默认 32px 旋转加载；size(n) 自定义尺寸。内部委托 SpinAnt。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                SpinnerAnt.create().size(24).build(),
                SpinnerAnt.create().size(32).build(),
                SpinnerAnt.create().size(48).build(),
                SpinnerAnt.create().size(72).build(),
                SpinnerAnt.create().size(96).build()
        );
        String code = """
                SpinnerAnt.create().size(24).build();
                SpinnerAnt.create().size(32).build();  // 默认
                SpinnerAnt.create().size(48).build();
                SpinnerAnt.create().size(72).build();
                SpinnerAnt.create().size(96).build();
                """;
        return Demos.sectionWithCode("2. 多种尺寸",
                "size() ≤24→SMALL, ≥48→LARGE, 其余→DEFAULT。",
                code, demo);
    }

    private Node vsSection() {
        // 对比：SpinnerAnt 和 SpinAnt 效果一致
        Node spin = SpinAnt.create().build();
        Node spinner = SpinnerAnt.create().build();
        Node demo = Demos.row(spin, spinner);
        String code = """
                SpinAnt.create().build();    // 完整 API
                SpinnerAnt.create().build(); // 简化入口，效果相同
                """;
        return Demos.sectionWithCode("3. 与 SpinAnt 对比",
                "SpinnerAnt 是 SpinAnt SPINNER 模式的简化入口，动画效果完全一致。",
                code, demo);
    }

    private Node toggleSection() {
        Node spinner = SpinnerAnt.create().size(48).build();
        boolean[] visible = {true};

        ButtonAnt[] toggleBtnArr = new ButtonAnt[1];
        toggleBtnArr[0] = ButtonAnt.create("隐藏")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    visible[0] = !visible[0];
                    spinner.setVisible(visible[0]);
                    spinner.setManaged(visible[0]);
                    toggleBtnArr[0].setText(visible[0] ? "隐藏" : "显示");
                })
                .build();

        Node demo = Demos.column(spinner, toggleBtnArr[0]);
        String code = """
                // build 后可通过 setVisible / setManaged 控制显隐
                Node spinner = SpinnerAnt.create().size(48).build();
                spinner.setVisible(false);
                spinner.setManaged(false);
                """;
        return Demos.sectionWithCode("4. 显示/隐藏",
                "点击按钮切换 Spinner 的显示和隐藏，演示运行时状态控制。",
                code, demo);
    }

    /**
     * 5. 交互演示（PlayGround —— rebuildRebuild 模式最小用例）。
     *
     * <p>Spinner 只有一个属性 {@code size}，且无 modify 入口 —— 任一档位切换都需
     * 重新 build 节点，故采用 {@link PlayGround#rebindRebuild}。本节作为「单 binder +
     * factory」的最小样板：1 个 Binder + 1 个 Row + 5 档 size → 重建 SpinnerAnt。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子：5 档 size（24/32/48/72/96）
        Binder<String> size = PlayGround.binder("32");

        // 2. display 工厂：读 binder → 重 build
        Supplier<Node> factory = () -> {
            final double s = parseSize(size.get());
            return SpinnerAnt.create().size(s).build();
        };

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Spinner 尺寸 —— 单一维度 rebuildRebuild 模式最小用例。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("24",  "24 (SMALL)"),
                                PlayGround.entry("32",  "32 (DEFAULT)"),
                                PlayGround.entry("48",  "48 (LARGE)"),
                                PlayGround.entry("72",  "72"),
                                PlayGround.entry("96",  "96")))));
    }

    private static double parseSize(String v) {
        if (v == null) return 32;
        try { return Math.max(8, Math.min(200, Double.parseDouble(v))); }
        catch (NumberFormatException e) { return 32; }
    }
}
