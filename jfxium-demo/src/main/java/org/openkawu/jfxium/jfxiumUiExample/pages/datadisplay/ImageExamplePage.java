package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.ImageAnt;

/**
 * Image 图片 —— 占位 / 尺寸与圆角。
 *
 * <p>注：示例不依赖外部网络图片，未传 src 时 ImageAnt 自动展示 placeholder 文本，
 * 重点展示 API 用法（width / height / borderRadius / placeholder）。</p>
 */
public class ImageExamplePage extends VBoxAnt {

    public ImageExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Image 图片")
                .description("图片容器，支持占位、尺寸约束与圆角裁剪。")
                .sections(placeholderSection(), sizeSection(), dynamicSection())
                .padding(24)
                .build());
    }

    private Node placeholderSection() {
        Node demo = ImageAnt.create()
                .width(160).height(100)
                .placeholder("暂无图片")
                .build();
        String code = """
                // 未传 src 时展示 placeholder 占位文本
                ImageAnt.create()
                        .width(160).height(100)
                        .placeholder("暂无图片")
                        .build();
                """;
        return Demos.sectionWithCode("1. 占位",
                "未设置 src 时显示 placeholder 占位，避免空白。", code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                ImageAnt.create().width(120).height(120).placeholder("方形").build(),
                ImageAnt.create().width(120).height(120).borderRadius(12)
                        .placeholder("圆角").build()
        );
        String code = """
                ImageAnt.create().width(120).height(120).placeholder("方形").build();
                ImageAnt.create().width(120).height(120)
                        .borderRadius(12).placeholder("圆角").build();
                // 加载真实图片：ImageAnt.create().src("https://...").build();
                """;
        return Demos.sectionWithCode("2. 尺寸与圆角",
                "width / height 约束尺寸，borderRadius 裁剪圆角。", code, demo);
    }

    private Node dynamicSection() {
        Node image = ImageAnt.create().width(160).height(100).placeholder("占位图").build();
        ImageAnt.Controller controller = ImageAnt.controllerOf(image);
        boolean[] rounded = {false};

        ButtonAnt toggleBtn = ButtonAnt.create("切换样式")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    rounded[0] = !rounded[0];
                    controller.setBorderRadius(rounded[0] ? 16 : 0);
                    controller.setPlaceholder(rounded[0] ? "圆角" : "占位图");
                })
                .build();

        Node demo = Demos.column(image, toggleBtn);
        String code = """
                Node image = ImageAnt.create().width(160).height(100).placeholder("占位图").build();
                ImageAnt.Controller controller = ImageAnt.controllerOf(image);
                controller.setBorderRadius(16);
                controller.setPlaceholder("圆角");
                """;
        return Demos.sectionWithCode("3. 动态切换",
                "点击按钮在默认样式和圆角样式之间切换，通过 Controller 更新同一个图片节点。",
                code, demo);
    }
}
