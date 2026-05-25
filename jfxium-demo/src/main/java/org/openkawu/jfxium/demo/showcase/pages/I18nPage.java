package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.CodeBlockAnt;
import org.openkawu.jfxium.component.EmptyAnt;
import org.openkawu.jfxium.component.ModalAnt;
import org.openkawu.jfxium.component.PopconfirmAnt;
import org.openkawu.jfxium.component.TransferAnt;
import org.openkawu.jfxium.component.TreeSelectAnt;
import org.openkawu.jfxium.component.UploadAnt;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.Arrays;
import java.util.Locale;

/**
 * I18nPage - i18n 国际化机制演示页（M19.18）。
 *
 * <p><b>核心演示</b>：用顶部「中文 / English」开关切换 {@link Messages#setLocale(Locale)}，
 * 观察页面上各个内置组件的默认文案如何随 Locale 自动刷新。</p>
 *
 * <p><b>覆盖组件</b>（默认走 i18n 文案）：</p>
 * <ul>
 *   <li>CodeBlockAnt - 复制按钮</li>
 *   <li>TreeSelectAnt - placeholder</li>
 *   <li>EmptyAnt - 默认描述</li>
 *   <li>ModalAnt / PopconfirmAnt - OK / Cancel 默认文案</li>
 *   <li>UploadAnt - 拖拽区文字、Hint</li>
 *   <li>TransferAnt - 源/目标默认标题、search 提示</li>
 * </ul>
 */
public class I18nPage implements ShowcasePage {

    @Override public String   key()      { return "i18n"; }
    @Override public String   title()    { return "I18n 国际化"; }
    @Override public Category category() { return Category.OTHER; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("I18n 国际化");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");

        Label pageDesc = new Label(
                "JFXium 内置 i18n 机制：默认 Locale 为简体中文（zh_CN），"
                + "通过 Messages.setLocale(Locale) 运行时切换。"
                + "所有 *Ant / *Template 的默认文案自动跟随。"
        );
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");
        pageDesc.setWrapText(true);

        VBox header = new VBox(8, pageTitle, pageDesc);

        return new VBox(20,
                header,
                sectionLocaleSwitcher(),
                sectionEmpty(),
                sectionTreeSelect(),
                sectionCodeBlock(),
                sectionModalAndPopconfirm(),
                sectionUpload(),
                sectionTransfer(),
                sectionApi()
        );
    }

    /** 顶部 Locale 切换条 —— 点击立即生效 */
    private Node sectionLocaleSwitcher() {
        Label currentLabel = new Label();
        currentLabel.setStyle("-fx-text-fill: -color-accent-emphasis; -fx-font-weight: 600;");

        // 初始 + 监听刷新
        Runnable refreshCurrent = () -> currentLabel.setText(
                "当前 Locale: " + Messages.getLocale().toLanguageTag()
        );
        refreshCurrent.run();
        Messages.localeProperty().addListener((obs, ov, nv) -> refreshCurrent.run());

        Button zhBtn = ButtonAnt.create("中文 (zh_CN)")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> Messages.setLocale(Locale.SIMPLIFIED_CHINESE))
                .build();
        Button enBtn = ButtonAnt.create("English (en)")
                .onClick(e -> Messages.setLocale(Locale.ENGLISH))
                .build();

        HBox bar = new HBox(8, zhBtn, enBtn, spacer(), currentLabel);
        bar.setAlignment(Pos.CENTER_LEFT);

        return ShowcaseSection.create()
                .title("Locale 切换器")
                .description("点击按钮切换语言，观察下方组件默认文案的实时变化。")
                .demo(bar)
                .code("""
                        // 切换到英文
                        Messages.setLocale(Locale.ENGLISH);

                        // 切回简体中文
                        Messages.setLocale(Locale.SIMPLIFIED_CHINESE);

                        // 监听变化
                        Messages.localeProperty().addListener((obs, oldL, newL) -> {
                            // 自定义组件在这里刷新自己的文案
                        });
                        """)
                .build();
    }

    private Node sectionEmpty() {
        // 默认：不传 description，走 i18n 默认（zh_CN: "暂无数据" / en: "No Data"）
        Node empty = EmptyAnt.create().build();

        return ShowcaseSection.create()
                .title("EmptyAnt 默认描述")
                .description("未指定 description 时，使用 Messages.get(\"empty.description\")。")
                .demo(empty)
                .code("""
                        Node empty = EmptyAnt.create().build();
                        // zh_CN: "暂无数据"
                        // en:    "No Data"
                        """)
                .build();
    }

    private Node sectionTreeSelect() {
        TreeSelectAnt.TreeNode root = new TreeSelectAnt.TreeNode("root", "Root");
        root.getChildren().add(new TreeSelectAnt.TreeNode("a", "Item A"));
        root.getChildren().add(new TreeSelectAnt.TreeNode("b", "Item B"));

        Node ts = TreeSelectAnt.create()
                .tree(root)
                .build();

        return ShowcaseSection.create()
                .title("TreeSelectAnt placeholder")
                .description("未指定 placeholder 时，使用 Messages.get(\"treeselect.placeholder\")。")
                .demo(ts)
                .code("""
                        Node ts = TreeSelectAnt.create()
                            .tree(rootNode)
                            .build();
                        // zh_CN placeholder: "请选择"
                        // en    placeholder: "Please select"
                        """)
                .build();
    }

    private Node sectionCodeBlock() {
        Node code = CodeBlockAnt.create()
                .language("Java")
                .content("Messages.setLocale(Locale.ENGLISH);\nString s = Messages.get(\"codeblock.copy\");")
                .copyable(true)
                .build();

        return ShowcaseSection.create()
                .title("CodeBlockAnt 复制按钮")
                .description("复制按钮文案（含\"已复制!\"反馈）随 Locale 切换。")
                .demo(code)
                .code("""
                        Node code = CodeBlockAnt.create()
                            .content("...")
                            .copyable(true)
                            .build();
                        // 按钮文案:
                        //   zh_CN: "复制" / 点击后 "已复制!"
                        //   en:    "Copy" / 点击后 "Copied!"
                        """)
                .build();
    }

    private Node sectionModalAndPopconfirm() {
        Button openModal = ButtonAnt.create("打开 Modal").build();
        openModal.setOnAction(e -> ModalAnt.create()
                .title("Demo Modal")
                .content("默认 OK / Cancel 文案随 Locale 自动切换。")
                .build()
                .open(openModal));

        Button popTarget = ButtonAnt.create("Popconfirm 触发器").build();
        PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
                .title("确认操作？")
                .description("默认 Yes / No 文案走 i18n。")
                .target(popTarget)
                .build();
        popTarget.setOnAction(e -> popconfirm.show());

        HBox bar = new HBox(8, openModal, popTarget);

        return ShowcaseSection.create()
                .title("ModalAnt / PopconfirmAnt 默认按钮文案")
                .description("ModalAnt OK/Cancel、PopconfirmAnt Yes/No 都走 i18n 默认值。")
                .demo(bar)
                .code("""
                        ModalAnt.create()
                            .title("...")
                            .content("...")
                            .build()
                            .open(owner);
                        // 默认按钮:
                        //   zh_CN: 取消 / 确定
                        //   en:    Cancel / OK
                        """)
                .build();
    }

    private Node sectionUpload() {
        Node up = UploadAnt.create()
                .type(UploadAnt.Type.DRAG)
                .build();

        return ShowcaseSection.create()
                .title("UploadAnt 拖拽提示")
                .description("拖拽区主标题、Hint 走 i18n。")
                .demo(up)
                .code("""
                        Node up = UploadAnt.create()
                            .type(UploadAnt.Type.DRAG)
                            .build();
                        // zh_CN: "点击或拖拽文件到此区域上传" / "支持单个或批量上传"
                        // en:    "Click or drag file..." / "Support for single or bulk upload"
                        """)
                .build();
    }

    private Node sectionTransfer() {
        TransferAnt.Builder<String> tb = new TransferAnt.Builder<>();
        Node transfer = tb.dataSource(Arrays.asList("Apple", "Banana", "Cherry", "Durian"))
                .showSearch()
                .build();

        return ShowcaseSection.create()
                .title("TransferAnt 默认标题与 search 提示")
                .description("未调用 .titles(...) 时，源/目标标题走 i18n；search 框 placeholder 同样。")
                .demo(transfer)
                .code("""
                        Node transfer = new TransferAnt.Builder<String>()
                            .dataSource(Arrays.asList("Apple", "Banana"))
                            .showSearch()
                            .build();
                        // zh_CN 标题: 源列表 / 目标列表 / 搜索
                        // en    标题: Source / Target / Search
                        """)
                .build();
    }

    private Node sectionApi() {
        return ShowcaseSection.create()
                .title("API 速查")
                .description("Messages 静态门面（org.openkawu.jfxium.core.i18n.Messages）。")
                .demo(new Label(""))
                .code("""
                        // 取一段固定文案
                        String copy = Messages.get("codeblock.copy");

                        // 参数化（{0}/{1} 占位符替换）
                        String items = Messages.get("transfer.items", 5);

                        // 切换 Locale（默认 zh_CN）
                        Messages.setLocale(Locale.ENGLISH);
                        Messages.setLocale(Locale.SIMPLIFIED_CHINESE);

                        // 当前 Locale
                        Locale cur = Messages.getLocale();

                        // 监听变化（让自定义组件随 locale 刷新）
                        Messages.localeProperty().addListener((obs, ov, nv) -> {
                            myLabel.setText(Messages.get("my.custom.key"));
                        });
                        """)
                .build();
    }

    private static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        r.setMaxWidth(Double.MAX_VALUE);
        return r;
    }
}
