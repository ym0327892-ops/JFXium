package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.TimelineAnt;

/**
 * Timeline 时间轴 —— 基础 / 彩色圆点 / 交替模式。
 */
public class TimelineExamplePage extends VBoxAnt {

    public TimelineExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Timeline 时间轴")
                .description("垂直展示的时间流信息，可用于记录事件历程。")
                .sections(
                        basicSection(),
                        colorSection(),
                        alternateSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        VBox timeline = TimelineAnt.create()
                .item("创建项目 2024-01-01")
                .item("完成初始化 2024-01-05")
                .item("发布 v1.0 2024-02-01")
                .item("用户突破 1000 2024-03-15")
                .build();
        String code = """
                VBox timeline = TimelineAnt.create()
                        .item("创建项目 2024-01-01")
                        .item("完成初始化 2024-01-05")
                        .item("发布 v1.0 2024-02-01")
                        .item("用户突破 1000 2024-03-15")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "简单的时间轴。", code, timeline);
    }

    private Node colorSection() {
        VBox timeline = TimelineAnt.create()
                .item("成功步骤", TimelineAnt.DotColor.GREEN)
                .item("进行中", TimelineAnt.DotColor.BLUE)
                .item("警告事件", TimelineAnt.DotColor.RED)
                .item("等待中", TimelineAnt.DotColor.GRAY)
                .build();
        String code = """
                VBox timeline = TimelineAnt.create()
                        .item("成功步骤", TimelineAnt.DotColor.GREEN)
                        .item("进行中", TimelineAnt.DotColor.BLUE)
                        .item("警告事件", TimelineAnt.DotColor.RED)
                        .item("等待中", TimelineAnt.DotColor.GRAY)
                        .build();
                """;
        return Demos.sectionWithCode("2. 彩色圆点", "通过 DotColor 设置不同颜色表示状态。", code, timeline);
    }

    private Node alternateSection() {
        VBox timeline = TimelineAnt.create()
                .mode(TimelineAnt.Mode.ALTERNATE)
                .item("需求评审", "2024-01-10")
                .item("开发完成", "2024-02-20")
                .item("测试通过", "2024-03-01")
                .item("正式上线", "2024-03-15")
                .build();
        String code = """
                VBox timeline = TimelineAnt.create()
                        .mode(TimelineAnt.Mode.ALTERNATE)
                        .item("需求评审", "2024-01-10")
                        .item("开发完成", "2024-02-20")
                        .item("测试通过", "2024-03-01")
                        .item("正式上线", "2024-03-15")
                        .build();
                """;
        return Demos.sectionWithCode("3. 交替模式", "mode(ALTERNATE) 让内容左右交替展示。", code, timeline);
    }
}
