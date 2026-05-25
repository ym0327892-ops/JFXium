package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.StepsAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Steps 步骤条展示页（M19.10）。
 */
public class StepsPage implements ShowcasePage {

    @Override public String   key()      { return "steps"; }
    @Override public String   title()    { return "Steps 步骤条"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Steps 步骤条");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("引导用户按顺序完成任务流——多步表单、订单流程、注册流程的标配。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithDescription(),
                        sectionVertical(),
                        sectionSizes(),
                        sectionFinished()
                )
                .build();
    }

    private Node sectionBasic() {
        Node steps = StepsAnt.create()
                .step("填写信息")
                .step("确认订单")
                .step("完成")
                .current(1)
                .build();

        return ShowcaseSection.create()
                .title("场景 1：基础水平步骤条（current=1）")
                .description("第 0 步 finished / 第 1 步 current / 第 2 步 wait")
                .demo(steps)
                .code("""
                        StepsAnt.create()
                            .step("填写信息")
                            .step("确认订单")
                            .step("完成")
                            .current(1)
                            .build();
                        """)
                .build();
    }

    private Node sectionWithDescription() {
        Node steps = StepsAnt.create()
                .step("登录", "使用账号密码")
                .step("验证", "短信/邮件验证码")
                .step("完成", "跳转到首页")
                .current(1)
                .build();

        return ShowcaseSection.create()
                .title("场景 2：带描述")
                .description(".step(title, description) —— 步骤下加说明")
                .demo(steps)
                .code("""
                        StepsAnt.create()
                            .step("登录", "使用账号密码")
                            .step("验证", "短信/邮件验证码")
                            .step("完成", "跳转到首页")
                            .current(1)
                            .build();
                        """)
                .build();
    }

    private Node sectionVertical() {
        Node steps = StepsAnt.create()
                .step("提交申请", "上传资料并提交")
                .step("初审", "1-2 个工作日")
                .step("终审", "3-5 个工作日")
                .step("完成", "等待结果通知")
                .current(2)
                .direction(StepsAnt.Direction.VERTICAL)
                .build();

        return ShowcaseSection.create()
                .title("场景 3：垂直步骤条")
                .description(".direction(VERTICAL) —— 长流程或步骤多时纵向展示更清晰")
                .demo(steps)
                .code("""
                        StepsAnt.create()
                            .step("提交申请", "上传资料并提交")
                            .step("初审", "1-2 个工作日")
                            .direction(StepsAnt.Direction.VERTICAL)
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = StepsAnt.create()
                .step("Small")
                .step("Step")
                .step("Demo")
                .current(1)
                .size(StepsAnt.Size.SMALL)
                .build();
        Node def = StepsAnt.create()
                .step("Default")
                .step("Step")
                .step("Demo")
                .current(1)
                .build();

        VBox col = VBoxBuilder.create().spacing(20).children(
                grayLabel("SMALL"), small,
                grayLabel("DEFAULT"), def
        ).build();

        return ShowcaseSection.create()
                .title("场景 4：尺寸（SMALL / DEFAULT）")
                .description("仅两档；SMALL 用于嵌入紧凑面板")
                .demo(col)
                .code("""
                        StepsAnt.create().step(...).size(StepsAnt.Size.SMALL).build();
                        StepsAnt.create().step(...).build();
                        """)
                .build();
    }

    private Node sectionFinished() {
        Node steps = StepsAnt.create()
                .step("已完成")
                .step("已完成")
                .step("已完成")
                .current(3) // 大于 step 数 = 全部 finished
                .build();

        return ShowcaseSection.create()
                .title("场景 5：全部已完成（current=stepCount）")
                .description("current 大于等于 step 数 → 所有步骤都标记为 finished")
                .demo(steps)
                .code("""
                        StepsAnt.create()
                            .step("步骤 A").step("步骤 B").step("步骤 C")
                            .current(3)   // ≥ 3 → 全部 finished
                            .build();
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }
}
