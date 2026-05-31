package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import org.openkawu.jfxium.component.AlertAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Alert 警告提示 —— 4 种类型 + 可关闭 + Banner 模式。
 *
 * <p>跟 Message / Notification 的边界：Alert 是<b>常驻</b>在页面里的提示框，
 * 跟随页面内容滚动；Message / Notification 是浮在页面之上的临时弹层。</p>
 */
public class AlertExamplePage extends VBoxAnt {

    public AlertExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Alert 警告提示")
                .description("常驻在页面中的状态提示框，适合表单错误汇总、版本公告等场景。")
                .sections(
                        typesSection(),
                        titleSection(),
                        closableSection(),
                        bannerSection()
                )
                .padding(24)
                .build());
    }

    private Node typesSection() {
        Node a = AlertAnt.success("操作成功！数据已保存到服务器。").build();
        Node b = AlertAnt.info("这是一条提示信息，仅供参考。").build();
        Node c = AlertAnt.warning("即将到达免费额度上限，请及时升级。").build();
        Node d = AlertAnt.error("提交失败，请检查网络后重试。").build();
        String code = """
                AlertAnt.success("操作成功！数据已保存到服务器。").build();
                AlertAnt.info("这是一条提示信息，仅供参考。").build();
                AlertAnt.warning("即将到达免费额度上限，请及时升级。").build();
                AlertAnt.error("提交失败，请检查网络后重试。").build();
                """;
        return Demos.sectionWithCode("1. 4 种类型",
                "AlertAnt.success / info / warning / error —— 静态工厂直接出 Builder。",
                code, Demos.column(a, b, c, d));
    }

    private Node titleSection() {
        Node a = AlertAnt.success("成功", "数据已保存，并同步到所有节点。").build();
        Node b = AlertAnt.warning("注意",
                "你正在编辑的内容尚未保存，离开页面前请先点保存。").build();
        String code = """
                // 二参重载：title + message
                AlertAnt.success("成功", "数据已保存，并同步到所有节点。").build();
                AlertAnt.warning("注意", "你正在编辑的内容尚未保存，离开页面前请先点保存。").build();
                """;
        return Demos.sectionWithCode("2. 带标题",
                "二参重载 success(title, message) —— 标题加粗一行，详细信息次一行。",
                code, Demos.column(a, b));
    }

    private Node closableSection() {
        Node a = AlertAnt.info("可关闭的提示", "点击右侧 × 关闭这条 Alert。")
                .closable(true).build();
        String code = """
                AlertAnt.info("可关闭的提示", "点击右侧 × 关闭这条 Alert。")
                        .closable(true)
                        .build();
                """;
        return Demos.sectionWithCode("3. 可关闭",
                "closable(true) —— 右侧出现关闭按钮，用户可手动收起提示。",
                code, a);
    }

    private Node bannerSection() {
        Node a = AlertAnt.warning("Banner 模式 —— 通常用于页面顶部的全局公告，没有圆角和阴影。")
                .banner().build();
        String code = """
                AlertAnt.warning("Banner 模式 —— 通常用于页面顶部的全局公告。")
                        .banner()
                        .build();
                """;
        return Demos.sectionWithCode("4. Banner 模式",
                "banner() —— 通栏样式，无圆角，常用于页面顶部全局公告。",
                code, a);
    }
}
