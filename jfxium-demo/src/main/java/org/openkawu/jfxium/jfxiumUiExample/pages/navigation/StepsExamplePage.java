package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.StepsAnt;

/**
 * Steps 步骤条 —— 基础 / 状态 / 垂直方向。
 */
public class StepsExamplePage extends VBoxAnt {

    public StepsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Steps 步骤条")
                .description("引导用户按照流程完成任务的导航条。")
                .sections(
                        basicSection(),
                        statusSection(),
                        verticalSection(),
                        interactiveSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node steps = StepsAnt.create()
                .step("登录", "填写账号密码")
                .step("验证", "短信验证码")
                .step("完成", "注册成功")
                .current(1)
                .build();
        String code = """
                Node steps = StepsAnt.create()
                        .step("登录", "填写账号密码")
                        .step("验证", "短信验证码")
                        .step("完成", "注册成功")
                        .current(1)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "current(1) 表示当前在第二步。", code, steps);
    }

    private Node statusSection() {
        Node steps = StepsAnt.create()
                .step("已完成")
                .step("进行中")
                .step("等待中")
                .step("等待中")
                .current(1)
                .build();
        String code = """
                Node steps = StepsAnt.create()
                        .step("已完成")
                        .step("进行中")
                        .step("等待中")
                        .step("等待中")
                        .current(1)
                        .build();
                """;
        return Demos.sectionWithCode("2. 步骤状态",
                "current 之前为 finished，当前为 current，之后为 wait。", code, steps);
    }

    private Node verticalSection() {
        Node steps = StepsAnt.create()
                .direction(StepsAnt.Direction.VERTICAL)
                .step("提交订单", "2024-01-01 10:00")
                .step("支付完成", "2024-01-01 10:05")
                .step("商家发货", "等待发货")
                .current(2)
                .build();
        String code = """
                Node steps = StepsAnt.create()
                        .direction(StepsAnt.Direction.VERTICAL)
                        .step("提交订单", "2024-01-01 10:00")
                        .step("支付完成", "2024-01-01 10:05")
                        .step("商家发货", "等待发货")
                        .current(2)
                        .build();
                """;
        return Demos.sectionWithCode("3. 垂直方向", "direction(VERTICAL) 切换为竖向步骤条。", code, steps);
    }

    /**
     * 4. 交互式步进 —— 上一步 / 下一步按钮驱动步骤前进后退。
     *
     * <p><b>BUG #51 已修复</b>：StepsAnt 现提供运行时 {@link StepsAnt.Controller}，
     * 通过 {@code build()} 后调 {@code controller()} 拿到，再用 {@code setCurrent / next / prev}
     * 切换当前步骤，<b>不再重建整个 steps 节点</b>（保留布局/动画状态）。</p>
     */
    private Node interactiveSection() {
        int[] uiTotal = {3};

        // 构建一次，拿到 runtime 控制器
        StepsAnt.Builder builder = StepsAnt.create()
                .step("登录", "填写账号密码")
                .step("验证", "短信验证码")
                .step("完成", "注册成功")
                .current(0);
        Node steps = builder.build();
        StepsAnt.Controller ctrl = builder.controller();

        Label hint = new Label("当前步骤：1 / " + uiTotal[0]);

        Node prevBtn = ButtonAnt.create("上一步").onClick(e -> {
            ctrl.prev();   // runtime 切换，不重建
            hint.setText("当前步骤：" + (ctrl.getCurrent() + 1) + " / " + ctrl.getTotal());
        }).build();

        Node nextBtn = ButtonAnt.create("下一步").type(ButtonAnt.Type.PRIMARY).onClick(e -> {
            ctrl.next();   // runtime 切换，不重建
            hint.setText("当前步骤：" + (ctrl.getCurrent() + 1) + " / " + ctrl.getTotal());
        }).build();

        Node demo = Demos.column(steps, Demos.row(prevBtn, nextBtn, hint));
        String code = """
                // BUG #51 已修复：StepsAnt 提供运行时 Controller，无需重建节点
                StepsAnt.Builder builder = StepsAnt.create()
                        .step("登录", "填写账号密码")
                        .step("验证", "短信验证码")
                        .step("完成", "注册成功")
                        .current(0);
                Node steps = builder.build();
                StepsAnt.Controller ctrl = builder.controller();

                ButtonAnt.create("下一步").type(ButtonAnt.Type.PRIMARY).onClick(e -> {
                    ctrl.next();   // runtime 切换当前步骤，不重建整个步骤条
                }).build();

                ButtonAnt.create("上一步").onClick(e -> ctrl.prev()).build();
                // 也可直接跳转：ctrl.setCurrent(2);
                """;
        return Demos.sectionWithCode("4. 交互式步进",
                "点「下一步 / 上一步」驱动步骤前进后退。StepsAnt.controller() 提供运行时 setCurrent/next/prev，不重建节点。",
                code, demo);
    }
}
