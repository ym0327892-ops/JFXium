package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.UploadAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Upload 文件上传展示页（M19.11）。
 */
public class UploadPage implements ShowcasePage {

    @Override public String   key()      { return "upload"; }
    @Override public String   title()    { return "Upload 文件上传"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Upload 文件上传");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("两种触发方式（点击 / 拖拽）+ 三种文件展示（TEXT / PICTURE / PICTURE_CARD）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionSelect(),
                        sectionDrag(),
                        sectionMultiple(),
                        sectionAccept(),
                        sectionListType()
                )
                .build();
    }

    private Node sectionSelect() {
        Node u = UploadAnt.create()
                .type(UploadAnt.Type.SELECT)
                .buttonText("点击上传")
                .build();
        return ShowcaseSection.create()
                .title("场景 1：按钮触发（默认）")
                .description("点击按钮弹出文件选择器")
                .demo(u)
                .code("""
                        UploadAnt.create()
                            .type(UploadAnt.Type.SELECT)
                            .buttonText("点击上传")
                            .build();
                        """)
                .build();
    }

    private Node sectionDrag() {
        Node u = UploadAnt.create()
                .type(UploadAnt.Type.DRAG)
                .dragText("点击或拖拽文件到此区域")
                .hintText("支持单文件上传，最大 10MB")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：拖拽上传")
                .description("DRAG 类型 —— 大块虚线区域，支持文件拖入")
                .demo(u)
                .code("""
                        UploadAnt.create()
                            .type(UploadAnt.Type.DRAG)
                            .dragText("点击或拖拽文件到此区域")
                            .hintText("支持单文件上传，最大 10MB")
                            .build();
                        """)
                .build();
    }

    private Node sectionMultiple() {
        Node u = UploadAnt.create()
                .multiple()
                .buttonText("选择多个文件")
                .build();
        return ShowcaseSection.create()
                .title("场景 3：多文件上传")
                .description(".multiple() —— 文件选择器允许多选；上传列表逐个展示")
                .demo(u)
                .code("""
                        UploadAnt.create()
                            .multiple()
                            .buttonText("选择多个文件")
                            .build();
                        """)
                .build();
    }

    private Node sectionAccept() {
        Node u = UploadAnt.create()
                .accept("*.png,*.jpg,*.jpeg")
                .buttonText("仅图片")
                .hintText("仅支持 PNG / JPG 格式")
                .build();
        return ShowcaseSection.create()
                .title("场景 4：限制文件类型（accept）")
                .description(".accept(\"*.png,*.jpg,*.jpeg\") —— 在文件选择器里只显示这些类型")
                .demo(u)
                .code("""
                        UploadAnt.create()
                            .accept("*.png,*.jpg,*.jpeg")
                            .buttonText("仅图片")
                            .build();
                        """)
                .build();
    }

    private Node sectionListType() {
        Node u = UploadAnt.create()
                .type(UploadAnt.Type.SELECT)
                .listType(UploadAnt.ListType.PICTURE_CARD)
                .multiple()
                .buttonText("+ 上传图片")
                .build();
        return ShowcaseSection.create()
                .title("场景 5：图片墙模式（listType=PICTURE_CARD）")
                .description("已上传文件以图片卡片形式展示 —— 头像、商品图、相册场景标配")
                .demo(u)
                .code("""
                        UploadAnt.create()
                            .type(UploadAnt.Type.SELECT)
                            .listType(UploadAnt.ListType.PICTURE_CARD)
                            .multiple()
                            .build();
                        """)
                .build();
    }
}
