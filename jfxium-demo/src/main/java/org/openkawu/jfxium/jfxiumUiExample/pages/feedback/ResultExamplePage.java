package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.ResultAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Result 结果页 —— 成功 / 错误警告 / 带额外内容。
 */
public class ResultExamplePage extends VBoxAnt {

    public ResultExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Result 结果页")
                .description("用于反馈一系列操作任务的处理结果。")
                .sections(
                        successSection(),
                        errorSection(),
                        extraSection()
                )
                .padding(24)
                .build());
    }

    private Node successSection() {
        VBox result = ResultAnt.success("提交成功", "您的申请已提交，预计 1-3 个工作日内审核完成。")
                .build();
        String code = """
                VBox result = ResultAnt.success("提交成功", "您的申请已提交，预计 1-3 个工作日内审核完成。")
                        .build();
                """;
        return Demos.sectionWithCode("1. 成功", "success() 快捷方法创建成功结果页。", code, result);
    }

    private Node errorSection() {
        Node row = Demos.column(
                ResultAnt.error("提交失败", "请检查网络连接后重试。").build(),
                ResultAnt.warning("警告", "您的账户余额不足，请及时充值。").build()
        );
        String code = """
                ResultAnt.error("提交失败", "请检查网络连接后重试。").build();
                ResultAnt.warning("警告", "您的账户余额不足，请及时充值。").build();
                """;
        return Demos.sectionWithCode("2. 错误与警告", "error() / warning() 快捷方法。", code, row);
    }

    private Node extraSection() {
        HBox buttons = Demos.row(
                ButtonAnt.create("返回首页").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("查看详情").build()
        );
        VBox result = ResultAnt.create()
                .status(ResultAnt.Status.SUCCESS)
                .title("付款成功")
                .subTitle("订单号: 2024010112345，预计 2 小时内发货。")
                .extra(buttons)
                .build();
        String code = """
                HBox buttons = new HBox(12,
                        ButtonAnt.create("返回首页").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("查看详情").build()
                );
                VBox result = ResultAnt.create()
                        .status(ResultAnt.Status.SUCCESS)
                        .title("付款成功")
                        .subTitle("订单号: 2024010112345")
                        .extra(buttons)
                        .build();
                """;
        return Demos.sectionWithCode("3. 带额外内容", "extra() 可放置操作按钮组。", code, result);
    }
}
