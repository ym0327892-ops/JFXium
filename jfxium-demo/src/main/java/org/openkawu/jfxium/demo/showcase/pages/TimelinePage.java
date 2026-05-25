package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.TimelineAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Timeline 时间轴展示页（M19.10）。
 */
public class TimelinePage implements ShowcasePage {

    @Override public String   key()      { return "timeline"; }
    @Override public String   title()    { return "Timeline 时间轴"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Timeline 时间轴");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("展示时间序列事件——日志、操作记录、订单状态流。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithLabel(),
                        sectionDotColors(),
                        sectionPending(),
                        sectionAlternate()
                )
                .build();
    }

    private Node sectionBasic() {
        Node t = TimelineAnt.create()
                .item("创建项目")
                .item("初始化代码仓库")
                .item("第一次提交")
                .item("部署到测试环境")
                .build();

        return ShowcaseSection.create()
                .title("场景 1：基础时间轴")
                .description(".item(content) —— 默认蓝色圆点 + 内容")
                .demo(t)
                .code("""
                        TimelineAnt.create()
                            .item("创建项目")
                            .item("初始化代码仓库")
                            .item("第一次提交")
                            .build();
                        """)
                .build();
    }

    private Node sectionWithLabel() {
        Node t = TimelineAnt.create()
                .item("创建订单", "2026-05-24 09:30")
                .item("订单已支付", "2026-05-24 09:31")
                .item("商家发货", "2026-05-24 14:18")
                .item("已收货", "2026-05-25 10:42")
                .build();

        return ShowcaseSection.create()
                .title("场景 2：带时间标签")
                .description(".item(content, label) —— 内容 + 时间，订单/物流流程标配")
                .demo(t)
                .code("""
                        TimelineAnt.create()
                            .item("创建订单", "2026-05-24 09:30")
                            .item("订单已支付", "2026-05-24 09:31")
                            .item("商家发货", "2026-05-24 14:18")
                            .build();
                        """)
                .build();
    }

    private Node sectionDotColors() {
        Node t = TimelineAnt.create()
                .item("创建项目", "今天 09:00", TimelineAnt.DotColor.BLUE)
                .item("代码已提交", "今天 10:30", TimelineAnt.DotColor.GREEN)
                .item("⚠️ 单元测试失败", "今天 11:00", TimelineAnt.DotColor.RED)
                .item("待 review", "今天 11:30", TimelineAnt.DotColor.GRAY)
                .build();

        return ShowcaseSection.create()
                .title("场景 3：4 种圆点颜色（BLUE / GREEN / RED / GRAY）")
                .description("用颜色区分事件类型 —— 进行中/成功/失败/等待")
                .demo(t)
                .code("""
                        TimelineAnt.create()
                            .item("创建项目", "...", TimelineAnt.DotColor.BLUE)
                            .item("代码已提交", "...", TimelineAnt.DotColor.GREEN)
                            .item("单元测试失败", "...", TimelineAnt.DotColor.RED)
                            .item("待 review", "...", TimelineAnt.DotColor.GRAY)
                            .build();
                        """)
                .build();
    }

    private Node sectionPending() {
        Node t = TimelineAnt.create()
                .item("提交申请", "已完成")
                .item("审核中", "进行中")
                .pending("等待最终批准...")
                .build();

        return ShowcaseSection.create()
                .title("场景 4：Pending 进行中状态")
                .description(".pending(text) —— 末尾加一个旋转 loading + 自定义文字")
                .demo(t)
                .code("""
                        TimelineAnt.create()
                            .item("提交申请", "已完成")
                            .item("审核中", "进行中")
                            .pending("等待最终批准...")
                            .build();
                        """)
                .build();
    }

    private Node sectionAlternate() {
        Node t = TimelineAnt.create()
                .item("项目立项", "Q1 2026")
                .item("启动会议", "Q1 2026")
                .item("MVP 上线", "Q2 2026")
                .item("正式发布", "Q3 2026")
                .mode(TimelineAnt.Mode.ALTERNATE)
                .build();

        return ShowcaseSection.create()
                .title("场景 5：交替模式（Mode.ALTERNATE）")
                .description("内容交替出现在轴的左/右两侧 —— 适合时间线类年终总结/产品路线图")
                .demo(t)
                .code("""
                        TimelineAnt.create()
                            .item("项目立项", "Q1 2026")
                            .item("启动会议", "Q1 2026")
                            .mode(TimelineAnt.Mode.ALTERNATE)
                            .build();
                        """)
                .build();
    }
}
