package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.UploadAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

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
                        dragSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node selectSection() {
        Node upload = UploadAnt.create()
                .type(UploadAnt.Type.SELECT)
                .multiple()
                .onChange(files -> MessageAnt.info("已选择 " + files.size() + " 个文件"))
                .build();
        String code = """
                Node upload = UploadAnt.create()
                        .type(UploadAnt.Type.SELECT)
                        .multiple()
                        .onChange(files -> MessageAnt.info("已选择 " + files.size() + " 个文件"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 按钮上传", "点击按钮弹出文件选择对话框，支持多选。", code, upload);
    }

    private Node dragSection() {
        Node upload = UploadAnt.create()
                .type(UploadAnt.Type.DRAG)
                .dragText("点击或拖拽文件到此区域上传")
                .hintText("支持单个或批量上传")
                .multiple()
                .build();
        String code = """
                Node upload = UploadAnt.create()
                        .type(UploadAnt.Type.DRAG)
                        .dragText("点击或拖拽文件到此区域上传")
                        .hintText("支持单个或批量上传")
                        .multiple()
                        .build();
                """;
        return Demos.sectionWithCode("2. 拖拽上传", "type(DRAG) 显示拖拽区域，支持拖放文件。", code, upload);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Upload 的 4 个维度：触发方式 / 列表类型 / 多选 / 是否显示列表。
     *
     * <p>UploadAnt 是组合式 Builder 模式，所有属性都是 build 时确定，没有 Controller；
     * 这里采用 {@link PlayGround#rebindRebuild} 模式 —— 每次 binder 变化都重新
     * {@code create() + 链式 + build()} 生成全新的 Upload 节点。已选择/已上传的文件列表
     * 在重建后会清空，这与属性重建的语义一致（只反映属性，不保留瞬时数据）。</p>
     */
    private Node playgroundSection() {
        Binder<String> typeBinder        = PlayGround.binder("select");    // select/drag
        Binder<String> listTypeBinder    = PlayGround.binder("text");      // text/picture/picture-card
        Binder<String> multipleBinder    = PlayGround.binder("yes");       // yes/no
        Binder<String> showListBinder    = PlayGround.binder("yes");       // yes/no

        Supplier<Node> factory = () -> {
            UploadAnt.Type type = "drag".equals(typeBinder.get())
                    ? UploadAnt.Type.DRAG : UploadAnt.Type.SELECT;
            UploadAnt.ListType listType = parseListType(listTypeBinder.get());
            boolean multiple = "yes".equals(multipleBinder.get());
            boolean showList = "yes".equals(showListBinder.get());

            UploadAnt.Builder b = UploadAnt.create()
                    .type(type)
                    .listType(listType)
                    .multiple(multiple)
                    .showUploadList(showList)
                    .onChange(files -> MessageAnt.info(
                            "[" + type + " · " + listType.name().toLowerCase()
                                    + "] 已选择 " + files.size() + " 个文件"));
            if (type == UploadAnt.Type.DRAG) {
                b.dragText("点击或拖拽文件到此区域上传");
                b.hintText("支持单个或批量上传");
            }
            return b.build();
        };

        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Upload 的触发方式（按钮 / 拖拽）、列表样式（文本 / 图片 / 卡片）、是否多选、是否显示文件列表 —— 四种状态任意组合，右侧立刻看到效果。",
                PlayGround.rebindRebuild(factory, "触发方式 / 列表类型 / 多选 / 显示列表",
                        PlayGround.row("触发方式", PlayGround.segmented(typeBinder,
                                PlayGround.entry("select", "按钮"),
                                PlayGround.entry("drag",   "拖拽"))),
                        PlayGround.row("列表类型", PlayGround.segmented(listTypeBinder,
                                PlayGround.entry("text",        "文本"),
                                PlayGround.entry("picture",     "图片"),
                                PlayGround.entry("picture-card","卡片"))),
                        PlayGround.row("多选", PlayGround.segmented(multipleBinder,
                                PlayGround.entry("no",  "单选"),
                                PlayGround.entry("yes", "多选"))),
                        PlayGround.row("显示列表", PlayGround.segmented(showListBinder,
                                PlayGround.entry("yes", "显示"),
                                PlayGround.entry("no",  "隐藏")))));
    }

    private static UploadAnt.ListType parseListType(String v) {
        if ("picture".equals(v))      return UploadAnt.ListType.PICTURE;
        if ("picture-card".equals(v)) return UploadAnt.ListType.PICTURE_CARD;
        return UploadAnt.ListType.TEXT;
    }
}