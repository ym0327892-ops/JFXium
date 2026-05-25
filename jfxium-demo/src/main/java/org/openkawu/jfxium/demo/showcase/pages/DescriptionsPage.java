package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.DescriptionsAnt;
import org.openkawu.jfxium.component.SelectableTextAnt;
import org.openkawu.jfxium.component.TagAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Descriptions 描述列表展示页（M19.10）—— 详情页字段展示标配。
 */
public class DescriptionsPage implements ShowcasePage {

    @Override public String   key()      { return "descriptions"; }
    @Override public String   title()    { return "Descriptions 描述列表"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Descriptions 描述列表");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("详情页「label-value」键值对结构 —— 用户详情/订单详情/服务器信息标配。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionBordered(),
                        sectionVertical(),
                        sectionWithSpan(),
                        sectionWithCustomNode()
                )
                .build();
    }

    private Node sectionBasic() {
        VBox d = DescriptionsAnt.create()
                .title("用户信息")
                .item("用户名", "张三")
                .item("UID", "1024")
                .item("邮箱", "zhangsan@example.com")
                .item("注册时间", "2026-05-24 02:18")
                .column(2)
                .build();
        d.setMaxWidth(620);

        return ShowcaseSection.create()
                .title("场景 1：基础（2 列水平布局）")
                .description(".item(label, value) + .column(N) —— 默认水平 + N 列网格")
                .demo(d)
                .code("""
                        DescriptionsAnt.create()
                            .title("用户信息")
                            .item("用户名", "张三")
                            .item("UID", "1024")
                            .item("邮箱", "zhangsan@example.com")
                            .column(2)
                            .build();
                        """)
                .build();
    }

    private Node sectionBordered() {
        VBox d = DescriptionsAnt.create()
                .title("订单详情")
                .item("订单号", "ORDER-2026-0524-00897")
                .item("状态", "已发货")
                .item("金额", "¥1,280.00")
                .item("支付方式", "支付宝")
                .item("收货地址", "北京市海淀区中关村大街 1 号")
                .column(2)
                .bordered()
                .build();
        d.setMaxWidth(640);

        return ShowcaseSection.create()
                .title("场景 2：带边框（卡片式表格风）")
                .description(".bordered() —— 视觉更明显，适合需要明确边界的详情卡")
                .demo(d)
                .code("""
                        DescriptionsAnt.create()
                            .title("订单详情")
                            .item("订单号", "...").item("状态", "已发货")
                            .column(2)
                            .bordered()
                            .build();
                        """)
                .build();
    }

    private Node sectionVertical() {
        VBox d = DescriptionsAnt.create()
                .title("服务信息")
                .item("服务名", "user-service")
                .item("版本", "v2.5.0")
                .item("部署时间", "2026-05-24 02:18")
                .layout(DescriptionsAnt.Layout.VERTICAL)
                .column(3)
                .bordered()
                .build();
        d.setMaxWidth(640);

        return ShowcaseSection.create()
                .title("场景 3：垂直布局（label 在上，value 在下）")
                .description(".layout(VERTICAL) —— 适合 label 较长或 value 较窄的场景")
                .demo(d)
                .code("""
                        DescriptionsAnt.create()
                            .title("服务信息")
                            .item("服务名", "user-service")
                            .layout(DescriptionsAnt.Layout.VERTICAL)
                            .column(3)
                            .bordered()
                            .build();
                        """)
                .build();
    }

    private Node sectionWithSpan() {
        VBox d = DescriptionsAnt.create()
                .title("跨列展示")
                .item("用户名", "张三")
                .item("角色", "管理员")
                .item("备注", "VIP 用户，下单不限频次。优先级高。客服编号 #C-208。", 2)
                .item("最近登录", "2026-05-24 02:18")
                .item("登录 IP", "192.168.1.100")
                .column(2)
                .bordered()
                .build();
        d.setMaxWidth(640);

        return ShowcaseSection.create()
                .title("场景 4：跨列（span）")
                .description(".item(label, value, span) —— 长 value 跨多列显示")
                .demo(d)
                .code("""
                        DescriptionsAnt.create()
                            .item("备注", "长文本...", 2)   // 占 2 列
                            .column(2)
                            .build();
                        """)
                .build();
    }

    private Node sectionWithCustomNode() {
        VBox d = DescriptionsAnt.create()
                .title("自定义节点 value")
                .item("用户", new Label("张三"))
                .item("状态", TagAnt.create("已激活").type(TagAnt.Type.SUCCESS).build())
                .item("API Key",
                        SelectableTextAnt.create("ak-2026-prod-9f8e7d6c5b4a3z2y1x0w")
                                .style("-fx-font-family: 'Consolas';").build())
                .item("Token", TagAnt.create("v2.5.0").type(TagAnt.Type.PROCESSING).build())
                .column(2)
                .bordered()
                .build();
        d.setMaxWidth(640);

        return ShowcaseSection.create()
                .title("场景 5：value 是任意 Node（Tag / 可复制文本 / 链接）")
                .description(".item(label, Node) —— value 可以是 Tag/SelectableText/Link/任意自定义节点")
                .demo(d)
                .code("""
                        DescriptionsAnt.create()
                            .item("状态", TagAnt.create("已激活").type(Type.SUCCESS).build())
                            .item("API Key", SelectableTextAnt.create("ak-...").build())
                            .build();
                        """)
                .build();
    }
}
