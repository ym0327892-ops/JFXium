package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;

import org.openkawu.jfxium.component.control.SpinnerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Spinner 旋转加载 —— 基础 / 尺寸。
 */
public class SpinnerExamplePage extends VBoxAnt {

    public SpinnerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spinner 旋转加载")
                .description("轻量级加载旋转指示器，包装 JavaFX ProgressIndicator，用于表示后台操作进行中。")
                .sections(
                        basicSection(),
                        sizeSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
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
                "SpinnerAnt.create().build() 默认 32px 旋转加载；size(n) 自定义尺寸。",
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
                "size() 从 24 到 96 的任意尺寸，适用于按钮内嵌、大屏加载等不同场景。",
                code, demo);
    }
}
