package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.UploadAnt;

/**
 * Upload 上传 —— 按钮上传 / 拖拽上传。
 */
public class UploadExamplePage extends VBoxAnt {

    public UploadExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Upload 上传")
                .description("文件选择上传组件，支持点击和拖拽两种方式。")
                .sections(
                        selectSection(),
                        dragSection()
                )
                .padding(24)
                .build());
    }

    private Node selectSection() {
        VBox upload = UploadAnt.create()
                .type(UploadAnt.Type.SELECT)
                .multiple()
                .onChange(files -> System.out.println("已选择 " + files.size() + " 个文件"))
                .build();
        String code = """
                VBox upload = UploadAnt.create()
                        .type(UploadAnt.Type.SELECT)
                        .multiple()
                        .onChange(files -> System.out.println("已选择 " + files.size() + " 个文件"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 按钮上传", "点击按钮弹出文件选择对话框，支持多选。", code, upload);
    }

    private Node dragSection() {
        VBox upload = UploadAnt.create()
                .type(UploadAnt.Type.DRAG)
                .dragText("点击或拖拽文件到此区域上传")
                .hintText("支持单个或批量上传")
                .multiple()
                .build();
        String code = """
                VBox upload = UploadAnt.create()
                        .type(UploadAnt.Type.DRAG)
                        .dragText("点击或拖拽文件到此区域上传")
                        .hintText("支持单个或批量上传")
                        .multiple()
                        .build();
                """;
        return Demos.sectionWithCode("2. 拖拽上传", "type(DRAG) 显示拖拽区域，支持拖放文件。", code, upload);
    }
}
