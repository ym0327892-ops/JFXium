package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.ResultAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Result 操作结果展示页（M19.12）。
 */
public class ResultPage implements ShowcasePage {

    @Override public String   key()      { return "result"; }
    @Override public String   title()    { return "Result 操作结果"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Result 操作结果");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("整页操作结果反馈 —— 表单提交后、支付成功、404 / 403 / 500 错误页。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionSuccess(),
                        sectionError(),
                        sectionWarning(),
                        section404(),
                        section403_500()
                )
                .build();
    }

    private Node sectionSuccess() {
        Node r = ResultAnt.create()
                .status(ResultAnt.Status.SUCCESS)
                .title("提交成功")
                .subTitle("订单 ORDER-2026-0524-00897 已生成，预计 1-2 个工作日发货")
                .extraButton("查看订单", () -> MessageAnt.info("跳转到订单详情"))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：成功（SUCCESS）")
                .description("绿色 ✓ 图标 + 标题 + 副标题 + 主操作按钮")
                .demo(r)
                .code("""
                        ResultAnt.create()
                            .status(ResultAnt.Status.SUCCESS)
                            .title("提交成功")
                            .subTitle("订单 #00897 已生成")
                            .extraButton("查看订单", () -> goOrder())
                            .build();
                        """)
                .build();
    }

    private Node sectionError() {
        Node r = ResultAnt.create()
                .status(ResultAnt.Status.ERROR)
                .title("提交失败")
                .subTitle("请检查网络连接后重试。错误代码：NET_TIMEOUT")
                .extraButton("重新提交", () -> MessageAnt.info("重试中..."))
                .build();
        return ShowcaseSection.create()
                .title("场景 2：错误（ERROR）")
                .description("红色 ✕ —— 操作失败 / 异常情况")
                .demo(r)
                .code("""
                        ResultAnt.create()
                            .status(ResultAnt.Status.ERROR)
                            .title("提交失败")
                            .subTitle("...")
                            .extraButton("重新提交", () -> retry())
                            .build();
                        """)
                .build();
    }

    private Node sectionWarning() {
        Node r = ResultAnt.create()
                .status(ResultAnt.Status.WARNING)
                .title("有未保存的修改")
                .subTitle("你有 3 处未提交的修改，建议先保存再离开")
                .extra(HBoxBuilder.create().spacing(8).children(
                        ButtonAnt.create("放弃修改").build(),
                        ButtonAnt.create("保存并离开").type(ButtonAnt.Type.PRIMARY).build()
                ).build())
                .build();
        return ShowcaseSection.create()
                .title("场景 3：警告（WARNING）+ 自定义 extra 按钮组")
                .description(".extra(Node) 替代默认按钮 —— 多个操作并列")
                .demo(r)
                .code("""
                        ResultAnt.create()
                            .status(ResultAnt.Status.WARNING)
                            .title("有未保存的修改")
                            .extra(HBoxBuilder.create().spacing(8).children(
                                ButtonAnt.create("放弃修改").build(),
                                ButtonAnt.create("保存并离开").type(Type.PRIMARY).build()
                            ).build())
                            .build();
                        """)
                .build();
    }

    private Node section404() {
        Node r = ResultAnt.create()
                .status(ResultAnt.Status.NOT_FOUND)
                .title("404")
                .subTitle("抱歉，你访问的页面不存在")
                .extraButton("回到首页", () -> MessageAnt.info("返回首页"))
                .build();
        return ShowcaseSection.create()
                .title("场景 4：404 页面")
                .description(".status(NOT_FOUND) —— 路由不存在的标准页")
                .demo(r)
                .code("""
                        ResultAnt.create()
                            .status(ResultAnt.Status.NOT_FOUND)
                            .title("404")
                            .subTitle("抱歉，你访问的页面不存在")
                            .extraButton("回到首页", () -> router.go("/"))
                            .build();
                        """)
                .build();
    }

    private Node section403_500() {
        Node r403 = ResultAnt.create()
                .status(ResultAnt.Status.FORBIDDEN)
                .title("403")
                .subTitle("抱歉，你无权访问此页面")
                .build();
        Node r500 = ResultAnt.create()
                .status(ResultAnt.Status.INTERNAL_ERROR)
                .title("500")
                .subTitle("抱歉，服务器出错了")
                .build();

        VBox col = VBoxBuilder.create().spacing(20).children(r403, r500).build();

        return ShowcaseSection.create()
                .title("场景 5：403 / 500 错误页")
                .description("FORBIDDEN（无权限）/ INTERNAL_ERROR（服务器错误）")
                .demo(col)
                .code("""
                        ResultAnt.create().status(ResultAnt.Status.FORBIDDEN)...build();
                        ResultAnt.create().status(ResultAnt.Status.INTERNAL_ERROR)...build();
                        """)
                .build();
    }
}
