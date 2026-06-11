package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.control.SpinnerAnt;
import org.openkawu.jfxium.component.composite.SpinAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
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
                        vsSection()
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
}
