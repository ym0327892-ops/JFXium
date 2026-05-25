package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AlertAnt;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Alert 展示页（M19.8）。
 */
public class AlertPage implements ShowcasePage {

    @Override public String   key()      { return "alert"; }
    @Override public String   title()    { return "Alert 警告提示"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Alert 警告提示");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("4 种语义类型 + 可关闭 + 图标 + Banner 模式。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionTypes(),
                        sectionWithIcon(),
                        sectionClosable(),
                        sectionBanner(),
                        sectionWithAction()
                )
                .build();
    }

    private Node sectionTypes() {
        Node ok = AlertAnt.success("操作成功", "你已成功保存设置。").build();
        Node info = AlertAnt.info("提示", "新版本已发布，请及时更新。").build();
        Node warn = AlertAnt.warning("警告", "磁盘剩余空间不足 10%。").build();
        Node err = AlertAnt.error("错误", "无法连接到服务器，请稍后重试。").build();

        VBox col = VBoxBuilder.create().spacing(8).children(ok, info, warn, err).build();

        return ShowcaseSection.create()
                .title("场景 1：4 种语义类型")
                .description("success / info / warning / error —— 静态工厂方法直接拿")
                .demo(col)
                .code("""
                        AlertAnt.success("操作成功", "你已成功保存设置。").build();
                        AlertAnt.info("提示", "新版本已发布。").build();
                        AlertAnt.warning("警告", "磁盘空间不足。").build();
                        AlertAnt.error("错误", "无法连接服务器。").build();
                        """)
                .build();
    }

    private Node sectionWithIcon() {
        Node a = AlertAnt.info("提示", "showIcon(true) 在标题前显示图标，可读性更强。")
                .showIcon(true).build();
        Node b = AlertAnt.warning("警告", "showIcon(false) 时仅显示文字。")
                .showIcon(false).build();
        VBox col = VBoxBuilder.create().spacing(8).children(a, b).build();

        return ShowcaseSection.create()
                .title("场景 2：图标开关")
                .description("showIcon(true) 增强语义识别；适合需要快速扫读的场景")
                .demo(col)
                .code("""
                        AlertAnt.info("提示", "...").showIcon(true).build();
                        AlertAnt.warning("警告", "...").showIcon(false).build();
                        """)
                .build();
    }

    private Node sectionClosable() {
        Node a = AlertAnt.info("提示", "右侧有关闭按钮，点击后整个 Alert 移除")
                .closable(true)
                .onClose(() -> System.out.println("Alert 关闭"))
                .build();

        return ShowcaseSection.create()
                .title("场景 3：可关闭")
                .description("closable(true) + onClose 回调")
                .demo(a)
                .code("""
                        AlertAnt.info("提示", "...")
                            .closable(true)
                            .onClose(() -> log("closed"))
                            .build();
                        """)
                .build();
    }

    private Node sectionBanner() {
        Node a = AlertAnt.warning("公告", "系统将于今晚 23:00 维护，预计 30 分钟，请提前保存。")
                .banner().showIcon(true).build();

        return ShowcaseSection.create()
                .title("场景 4：Banner 模式")
                .description("banner() 让 Alert 变成无圆角、无内边距的横幅样式——常用于全局公告")
                .demo(a)
                .code("""
                        AlertAnt.warning("公告", "系统维护...")
                            .banner()
                            .showIcon(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionWithAction() {
        Node a = AlertAnt.info("有新版本可用", "v2.5.0 已发布，含 12 项改进")
                .action(ButtonAnt.create("立即更新").type(ButtonAnt.Type.LINK).build())
                .build();

        return ShowcaseSection.create()
                .title("场景 5：自定义 action 按钮")
                .description("action(Node)：在 Alert 右侧加一个用户操作（典型的「立即更新」「了解详情」）")
                .demo(a)
                .code("""
                        AlertAnt.info("有新版本可用", "...")
                            .action(ButtonAnt.create("立即更新").type(ButtonAnt.Type.LINK).build())
                            .build();
                        """)
                .build();
    }
}
