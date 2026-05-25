package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.BreadcrumbAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Breadcrumb 面包屑展示页（M19.11）。
 */
public class BreadcrumbPage implements ShowcasePage {

    @Override public String   key()      { return "breadcrumb"; }
    @Override public String   title()    { return "Breadcrumb 面包屑"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Breadcrumb 面包屑");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("展示当前路径 + 各级可点回 —— admin 详情页/层级页面顶部标配。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithCallback(),
                        sectionCustomSeparator(),
                        sectionDeepPath()
                )
                .build();
    }

    private Node sectionBasic() {
        Node b = BreadcrumbAnt.create()
                .items("首页", "用户管理", "用户详情")
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础（默认 / 分隔符）")
                .description(".items(...) 一次性传入字符串数组")
                .demo(b)
                .code("""
                        BreadcrumbAnt.create()
                            .items("首页", "用户管理", "用户详情")
                            .build();
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Node b = BreadcrumbAnt.create()
                .item("首页", item -> MessageAnt.info("回到：首页"))
                .item("用户管理", item -> MessageAnt.info("回到：用户管理"))
                .item("用户详情")  // 当前页，不可点
                .build();
        return ShowcaseSection.create()
                .title("场景 2：可点击回跳")
                .description(".item(title, onClick) —— 当前页通常不传 onClick（不可点）")
                .demo(b)
                .code("""
                        BreadcrumbAnt.create()
                            .item("首页", item -> router.go("/"))
                            .item("用户管理", item -> router.go("/users"))
                            .item("用户详情")    // 当前页
                            .build();
                        """)
                .build();
    }

    private Node sectionCustomSeparator() {
        Node b1 = BreadcrumbAnt.create()
                .separator(">")
                .items("首页", "订单", "详情")
                .build();
        Node b2 = BreadcrumbAnt.create()
                .separator("→")
                .items("Step 1", "Step 2", "Step 3")
                .build();
        Node b3 = BreadcrumbAnt.create()
                .separator("·")
                .items("App", "Module", "Page")
                .build();

        VBox col = VBoxBuilder.create().spacing(10).children(b1, b2, b3).build();

        return ShowcaseSection.create()
                .title("场景 3：自定义分隔符")
                .description(".separator(str) —— `>` / `→` / `·` 任意字符")
                .demo(col)
                .code("""
                        BreadcrumbAnt.create().separator(">").items(...).build();
                        BreadcrumbAnt.create().separator("→").items(...).build();
                        BreadcrumbAnt.create().separator("·").items(...).build();
                        """)
                .build();
    }

    private Node sectionDeepPath() {
        Node b = BreadcrumbAnt.create()
                .items("首页", "组织架构", "技术中心", "产品研发部", "前端组", "张三")
                .build();
        return ShowcaseSection.create()
                .title("场景 4：深层路径")
                .description("多级层级也能优雅展示")
                .demo(b)
                .code("""
                        BreadcrumbAnt.create()
                            .items("首页", "组织架构", "技术中心", "产品研发部", "前端组", "张三")
                            .build();
                        """)
                .build();
    }
}
