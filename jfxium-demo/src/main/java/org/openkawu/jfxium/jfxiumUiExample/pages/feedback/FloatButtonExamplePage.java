package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.FloatButtonAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * FloatButton 悬浮按钮 —— 基础 / 类型 / 尺寸 / 提示。
 */
public class FloatButtonExamplePage extends VBoxAnt {

    public FloatButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("FloatButton 悬浮按钮")
                .description("固定在页面右下角的圆形快捷操作按钮，对标 Ant Design FloatButton。")
                .sections(
                        basicSection(),
                        typeSection(),
                        sizeSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(new Label("+"))
                        .tooltip("添加")
                        .onClick(() -> MessageAnt.info("点击悬浮按钮"))
                        .build(),
                FloatButtonAnt.create()
                        .icon(new Label("↑"))
                        .tooltip("回到顶部")
                        .build(),
                FloatButtonAnt.create()
                        .icon(new Label("?"))
                        .tooltip("帮助")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(new Label("+"))
                    .tooltip("添加")
                    .onClick(() -> System.out.println("click"))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "icon() 设置按钮图标；tooltip() 设置悬停提示；onClick() 设置点击回调。",
                code, demo);
    }

    private Node typeSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(new Label("+"))
                        .type(FloatButtonAnt.Type.DEFAULT)
                        .tooltip("Default")
                        .build(),
                FloatButtonAnt.create()
                        .icon(new Label("✦"))
                        .type(FloatButtonAnt.Type.PRIMARY)
                        .tooltip("Primary")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(new Label("+"))
                    .type(FloatButtonAnt.Type.DEFAULT)
                    .build();
                FloatButtonAnt.create()
                    .icon(new Label("✦"))
                    .type(FloatButtonAnt.Type.PRIMARY)
                    .build();
                """;
        return Demos.sectionWithCode("2. 类型",
                "type(DEFAULT/PRIMARY)：DEFAULT 为默认样式，PRIMARY 为主题色样式。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                FloatButtonAnt.create()
                        .icon(new Label("S"))
                        .size(40)
                        .tooltip("Small 40")
                        .build(),
                FloatButtonAnt.create()
                        .icon(new Label("M"))
                        .size(56)
                        .tooltip("Default 56")
                        .build(),
                FloatButtonAnt.create()
                        .icon(new Label("L"))
                        .size(72)
                        .tooltip("Large 72")
                        .build()
        );
        String code = """
                FloatButtonAnt.create()
                    .icon(new Label("S"))
                    .size(40)
                    .build();
                FloatButtonAnt.create()
                    .icon(new Label("M"))
                    .size(56)
                    .build();
                FloatButtonAnt.create()
                    .icon(new Label("L"))
                    .size(72)
                    .build();
                """;
        return Demos.sectionWithCode("3. 尺寸",
                "size(double) 自定义按钮大小（宽高相等，默认 56px）。",
                code, demo);
    }
}
