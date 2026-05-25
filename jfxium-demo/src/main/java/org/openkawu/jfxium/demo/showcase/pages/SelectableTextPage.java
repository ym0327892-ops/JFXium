package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.SelectableTextAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * SelectableText 展示页（M19.7）。
 *
 * <p>5 个 Section：单行 / 多行自动换行 / 语义类型 / 错误详情场景 / 字段标签场景。</p>
 */
public class SelectableTextPage implements ShowcasePage {

    @Override public String   key()      { return "selectable-text"; }
    @Override public String   title()    { return "SelectableText 可复制文本"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("SelectableText 可复制文本");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("看起来像 Label，但用户能拖动选中文字、Ctrl+C 复制——错误详情/邮箱/订单号/日志片段的标配");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionSingleLine(),
                        sectionMultiLineWrap(),
                        sectionBordered(),
                        sectionSemanticTypes(),
                        sectionErrorDetail(),
                        sectionFieldLabels()
                )
                .build();
    }

    // ============================================================
    // 1. 单行（最简）
    // ============================================================
    private Node sectionSingleLine() {
        Node email = SelectableTextAnt.create("zhangsan@example.com").build();
        Node token = SelectableTextAnt.create("sk-proj-1234567890abcdefghijklmnopqr").build();
        Node order = SelectableTextAnt.create("ORDER-2026-05-24-00897").build();

        VBox col = VBoxBuilder.create()
                .spacing(8)
                .children(
                        labeled("邮箱：", email),
                        labeled("Token：", token),
                        labeled("订单号：", order)
                )
                .build();

        return ShowcaseSection.create()
                .title("场景 1：单行（鼠标拖动选中、Ctrl+C 复制）")
                .description("看上去就是普通文字，但你可以选中并复制——尤其适合邮箱/Token/订单号等长字符串")
                .demo(col)
                .code("""
                        Node email = SelectableTextAnt.create("zhangsan@example.com").build();
                        Node token = SelectableTextAnt.create("sk-proj-...").build();
                        """)
                .build();
    }

    // ============================================================
    // 2. 多行 + 自动换行 + 自动高度
    // ============================================================
    private Node sectionMultiLineWrap() {
        String longText = "这是一段较长的文本内容，会根据容器最大宽度自动换行。"
                + "即使你不知道用户的屏幕分辨率，也不会被截断。"
                + "组件会根据文本量自动撑开高度——不会出现内部滚动条。";

        Node t = SelectableTextAnt.create(longText)
                .multiline(true)
                .wrap(true)
                .maxWidth(420)
                .build();

        return ShowcaseSection.create()
                .title("场景 2：多行 + 自动换行 + 自动高度")
                .description("multiline(true) + wrap(true) + maxWidth(...)：超出宽度自动折行，高度按内容撑开")
                .demo(t)
                .code("""
                        Node detail = SelectableTextAnt.create(longText)
                            .multiline(true)
                            .wrap(true)
                            .maxWidth(420)
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 3. 边框开关 + 焦点蓝边（M19.7.3）
    // ============================================================
    private Node sectionBordered() {
        String json = "{\n"
                + "  \"id\": 1024,\n"
                + "  \"name\": \"张三\",\n"
                + "  \"role\": \"admin\",\n"
                + "  \"createdAt\": \"2026-05-24T03:14:00Z\"\n"
                + "}";

        Node defaultBorder = SelectableTextAnt.create(json)
                .multiline(true).wrap(false).maxWidth(420)
                .style("-fx-font-family: 'JetBrains Mono', 'Consolas', monospace; -fx-font-size: 12px;")
                .build();

        Node noBorder = SelectableTextAnt.create(json)
                .multiline(true).wrap(false).maxWidth(420)
                .bordered(false)
                .style("-fx-font-family: 'JetBrains Mono', 'Consolas', monospace; -fx-font-size: 12px;")
                .build();

        VBox col = VBoxBuilder.create()
                .spacing(12)
                .children(
                        sub("默认（带边框）：灰色描边提示「可选区域」；点击或选中文字后边框变蓝", defaultBorder),
                        sub("关掉边框（.bordered(false)）：完全裸文本，看上去像 Label；选中时仍有蓝边作为反馈", noBorder)
                )
                .build();

        return ShowcaseSection.create()
                .title("场景 3：边框开关 + 焦点蓝边（M19.7.3）")
                .description("默认带灰色边框提示用户「可选可复制」；点击文字后边框变蓝告诉用户「你已选中」。可用 .bordered(false) 关掉默认灰边")
                .demo(col)
                .code("""
                        // 默认（带灰色边框 + 焦点蓝边）
                        SelectableTextAnt.create(json).multiline(true).build();

                        // 关掉默认灰边——但焦点蓝边仍会显示
                        SelectableTextAnt.create(json)
                            .multiline(true)
                            .bordered(false)
                            .build();
                        """)
                .build();
    }

    private static VBox sub(String label, Node n) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return VBoxBuilder.create().spacing(4).children(l, n).build();
    }

    // ============================================================
    // 4. 语义类型（DEFAULT / SECONDARY / SUCCESS / WARNING / ERROR）
    // ============================================================
    private Node sectionSemanticTypes() {
        Node def = SelectableTextAnt.create("DEFAULT — 普通正文").build();
        Node sec = SelectableTextAnt.create("SECONDARY — 次要说明（灰色）").type(SelectableTextAnt.Type.SECONDARY).build();
        Node suc = SelectableTextAnt.create("SUCCESS — 操作已完成（绿色）").type(SelectableTextAnt.Type.SUCCESS).build();
        Node warn = SelectableTextAnt.create("WARNING — 注意检查（橙色）").type(SelectableTextAnt.Type.WARNING).build();
        Node err = SelectableTextAnt.create("ERROR — 操作失败（红色）").type(SelectableTextAnt.Type.ERROR).build();

        VBox col = VBoxBuilder.create().spacing(6).children(def, sec, suc, warn, err).build();

        return ShowcaseSection.create()
                .title("场景 4：语义类型（5 种文字颜色）")
                .description("用 .type(Type) 表达语义—— SECONDARY/SUCCESS/WARNING/ERROR 对应灰/绿/橙/红")
                .demo(col)
                .code("""
                        SelectableTextAnt.create("...").type(SelectableTextAnt.Type.SECONDARY).build();
                        SelectableTextAnt.create("...").type(SelectableTextAnt.Type.SUCCESS).build();
                        SelectableTextAnt.create("...").type(SelectableTextAnt.Type.WARNING).build();
                        SelectableTextAnt.create("...").type(SelectableTextAnt.Type.ERROR).build();
                        """)
                .build();
    }

    // ============================================================
    // 5. 错误详情面板（结合 AlertAnt）
    // ============================================================
    private Node sectionErrorDetail() {
        String stack = "java.lang.NullPointerException: Cannot invoke \"getName()\" because \"user\" is null\n"
                + "    at com.example.UserService.findById(UserService.java:42)\n"
                + "    at com.example.UserController.getUser(UserController.java:18)\n"
                + "    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)";

        Label title = new Label("操作失败");
        title.setStyle("-fx-font-weight: 600; -fx-text-fill: -color-danger-emphasis;");

        Node briefMsg = SelectableTextAnt.create("无法加载用户信息，请稍后重试。").build();

        Label detailHeader = new Label("详细信息：");
        detailHeader.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

        Node detail = SelectableTextAnt.create(stack)
                .multiline(true)
                .wrap(true)
                .maxWidth(560)
                .type(SelectableTextAnt.Type.ERROR)
                .style("-fx-font-family: 'JetBrains Mono', 'Consolas', monospace; -fx-font-size: 12px;")
                .build();

        VBox panel = VBoxBuilder.create()
                .spacing(8)
                .padding(16, 16, 16, 16)
                .children(title, briefMsg, detailHeader, detail)
                .build();
        panel.setStyle("-fx-background-color: -color-danger-subtle; -fx-border-color: -color-danger-muted; -fx-border-width: 1; -fx-background-radius: 6; -fx-border-radius: 6;");
        panel.setMaxWidth(600);

        return ShowcaseSection.create()
                .title("场景 5：错误详情面板（Alert + 可复制 stack trace）")
                .description("简短描述 + 详细堆栈——用户报障时能直接拖选 stack 复制贴到聊天/工单里")
                .demo(panel)
                .code("""
                        Node briefMsg = SelectableTextAnt.create("无法加载用户信息，请稍后重试。").build();

                        Node detail = SelectableTextAnt.create(stackTrace)
                            .multiline(true)
                            .wrap(true)
                            .maxWidth(560)
                            .type(SelectableTextAnt.Type.ERROR)
                            .style("-fx-font-family: 'Consolas'; -fx-font-size: 12px;")
                            .build();
                        """)
                .build();
    }

    // ============================================================
    // 6. 字段标签场景（与 Label 标签搭配）
    // ============================================================
    private Node sectionFieldLabels() {
        VBox col = VBoxBuilder.create()
                .spacing(8)
                .children(
                        labeled("姓名：", SelectableTextAnt.create("张三").build()),
                        labeled("身份证：", SelectableTextAnt.create("110101199001011234").build()),
                        labeled("API Key：",
                                SelectableTextAnt.create("ak-2026-prod-9f8e7d6c5b4a3z2y1x0w")
                                        .style("-fx-font-family: 'JetBrains Mono', 'Consolas', monospace;")
                                        .build()),
                        labeled("备注：",
                                SelectableTextAnt.create("VIP 用户，下单不限频次。优先级高。客服编号 #C-208。")
                                        .multiline(true).wrap(true).maxWidth(400)
                                        .type(SelectableTextAnt.Type.SECONDARY)
                                        .build())
                )
                .build();

        return ShowcaseSection.create()
                .title("场景 6：详情面板字段标签")
                .description("「字段名 + 可复制值」是详情页/个人中心/订单详情的高频组合")
                .demo(col)
                .code("""
                        // 单行字段
                        labeled("身份证：", SelectableTextAnt.create("110101...").build());

                        // 等宽字体（适合 Token / Hash）
                        SelectableTextAnt.create("ak-...")
                            .style("-fx-font-family: 'Consolas';")
                            .build();

                        // 多行 + 次要颜色
                        SelectableTextAnt.create(remark)
                            .multiline(true).wrap(true).maxWidth(400)
                            .type(SelectableTextAnt.Type.SECONDARY)
                            .build();
                        """)
                .build();
    }

    /** 辅助：Label + 节点的水平组合。 */
    private static HBox labeled(String label, Node value) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-min-width: 80px;");
        HBox h = HBoxBuilder.create()
                .spacing(8)
                .align(Pos.TOP_LEFT)
                .children(l, value)
                .build();
        return h;
    }
}
