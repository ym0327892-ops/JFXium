package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AccordionAnt;
import org.openkawu.jfxium.component.CollapseAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Collapse / Accordion 折叠面板展示页（M19.15）。
 *
 * <p>合并展示：CollapseAnt 是 Ant Design 风格的多面板折叠（可任意展开多个），
 * AccordionAnt 是 JavaFX 原生 Accordion 的封装（互斥展开，同时只展开一个）。</p>
 */
public class CollapsePage implements ShowcasePage {

    @Override public String   key()      { return "collapse"; }
    @Override public String   title()    { return "Collapse 折叠面板"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Collapse / Accordion 折叠面板");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("折叠面板 —— 设置页/帮助文档/FAQ。两种模式：Collapse 多面板可同时展开 / Accordion 互斥单展开。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionCollapse(),
                        sectionDefaultActive(),
                        sectionAccordionMode(),
                        sectionWithDisabled(),
                        sectionNativeAccordion()
                )
                .build();
    }

    private Node panelContent(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-padding: 12; -fx-text-fill: -color-fg-default;");
        l.setWrapText(true);
        return l;
    }

    private Node sectionCollapse() {
        VBox c = CollapseAnt.create()
                .panel("a", "面板 A", panelContent("这是面板 A 的内容。Collapse 默认所有面板都可折叠也可展开。"))
                .panel("b", "面板 B", panelContent("这是面板 B 的内容。"))
                .panel("c", "面板 C", panelContent("这是面板 C 的内容。"))
                .build();
        c.setMaxWidth(560);
        return ShowcaseSection.create()
                .title("场景 1：基础（CollapseAnt 多面板）")
                .description("默认所有面板可任意折叠/展开，互不影响")
                .demo(c)
                .code("""
                        VBox c = CollapseAnt.create()
                            .panel("a", "面板 A", contentA)
                            .panel("b", "面板 B", contentB)
                            .panel("c", "面板 C", contentC)
                            .build();
                        """)
                .build();
    }

    private Node sectionDefaultActive() {
        VBox c = CollapseAnt.create()
                .panel("install", "安装",
                        panelContent("使用 Maven：\n<dependency>\n  <groupId>org.openkawu</groupId>\n  <artifactId>jfxium</artifactId>\n</dependency>"))
                .panel("usage", "基础用法",
                        panelContent("ButtonAnt.create(\"点击\").type(Type.PRIMARY).build();"))
                .panel("theme", "主题切换",
                        panelContent("ThemeManager.getInstance().toggleTheme();"))
                .activeKey("install")
                .build();
        c.setMaxWidth(560);
        return ShowcaseSection.create()
                .title("场景 2：默认展开第一项")
                .description(".activeKey(\"install\") —— 进入页面时默认展开「安装」")
                .demo(c)
                .code("""
                        CollapseAnt.create()
                            .panel("install", "安装", contentInstall)
                            .panel("usage", "基础用法", contentUsage)
                            .activeKey("install")
                            .build();
                        """)
                .build();
    }

    private Node sectionAccordionMode() {
        VBox c = CollapseAnt.create()
                .panel("a", "FAQ 1：JFXium 是什么？",
                        panelContent("JFXium 是基于 JavaFX 21 的现代 UI 组件库，对标 Ant Design 6.x。"))
                .panel("b", "FAQ 2：和 AtlantaFX 区别？",
                        panelContent("AtlantaFX 是主题包，JFXium 是组件库 + 主题；后者提供 Builder API 和业务模板。"))
                .panel("c", "FAQ 3：支持哪些主题？",
                        panelContent("8 套：Light/Dark/MUI 系列 4 套/Shadcn/Cyberpunk/Custom。"))
                .accordion()
                .activeKey("a")
                .build();
        c.setMaxWidth(560);
        return ShowcaseSection.create()
                .title("场景 3：Accordion 互斥模式（FAQ 场景）")
                .description(".accordion() —— 同时只展开一个；适合 FAQ / 帮助")
                .demo(c)
                .code("""
                        CollapseAnt.create()
                            .panel("a", "FAQ 1", contentA)
                            .panel("b", "FAQ 2", contentB)
                            .accordion()             // 互斥展开
                            .activeKey("a")
                            .build();
                        """)
                .build();
    }

    private Node sectionWithDisabled() {
        VBox c = CollapseAnt.create()
                .panel("normal", "可正常展开", panelContent("这一项是正常的"))
                .panel("disabled", "禁用项（不可展开）", panelContent("内容看不到"), true)
                .panel("normal2", "另一项也可展开", panelContent("继续正常"))
                .build();
        c.setMaxWidth(560);
        return ShowcaseSection.create()
                .title("场景 4：禁用面板")
                .description(".panel(key, header, content, true) —— 禁用项灰显且不可展开")
                .demo(c)
                .code("""
                        CollapseAnt.create()
                            .panel("normal", "可正常展开", c1)
                            .panel("disabled", "禁用项", c2, true)
                            .build();
                        """)
                .build();
    }

    private Node sectionNativeAccordion() {
        Accordion acc = AccordionAnt.create()
                .pane("基本信息", panelContent("姓名 / 邮箱 / 电话"))
                .pane("收货地址", panelContent("默认地址 / 备用地址"))
                .pane("付款方式", panelContent("信用卡 / 支付宝 / 微信"))
                .build();
        acc.setMaxWidth(560);
        return ShowcaseSection.create()
                .title("场景 5：AccordionAnt（JavaFX 原生 Accordion 包装）")
                .description("底层是 JavaFX Accordion —— 适合需要原生 Accordion 行为时使用；通常推荐用 CollapseAnt.accordion() 替代")
                .demo(acc)
                .code("""
                        Accordion acc = AccordionAnt.create()
                            .pane("基本信息", content1)
                            .pane("收货地址", content2)
                            .build();
                        """)
                .build();
    }
}
