package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.TransferAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.List;

/**
 * Transfer 穿梭框展示页（M19.12）。
 */
public class TransferPage implements ShowcasePage {

    @Override public String   key()      { return "transfer"; }
    @Override public String   title()    { return "Transfer 穿梭框"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Transfer 穿梭框");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("两栏数据穿梭 —— 权限分配、用户加入团队、角色赋权场景。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithSearch(),
                        sectionRoles()
                )
                .build();
    }

    private Node sectionBasic() {
        List<String> data = List.of("张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十");
        Node t = TransferAnt.<String>create()
                .dataSource(data)
                .targetKeys(List.of("张三", "王五"))
                .titles("待选用户", "已选用户")
                .onChange(target -> MessageAnt.info("当前已选：" + target))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础穿梭")
                .description("左侧待选 / 右侧已选；中间箭头按钮转移")
                .demo(t)
                .code("""
                        TransferAnt.<String>create()
                            .dataSource(allUsers)
                            .targetKeys(selectedUsers)
                            .titles("待选用户", "已选用户")
                            .onChange(target -> save(target))
                            .build();
                        """)
                .build();
    }

    private Node sectionWithSearch() {
        List<String> data = List.of(
                "Java", "Kotlin", "Scala", "Groovy", "Clojure",
                "JavaScript", "TypeScript", "Python", "Ruby", "Go",
                "Rust", "Swift", "C#", "F#", "Haskell"
        );
        Node t = TransferAnt.<String>create()
                .dataSource(data)
                .targetKeys(List.of("Java", "Kotlin"))
                .titles("可选语言", "已掌握")
                .showSearch()
                .build();
        return ShowcaseSection.create()
                .title("场景 2：带搜索（数据多时必备）")
                .description(".showSearch() —— 两栏顶部都加搜索框")
                .demo(t)
                .code("""
                        TransferAnt.<String>create()
                            .dataSource(allLanguages)
                            .targetKeys(myLanguages)
                            .showSearch()
                            .build();
                        """)
                .build();
    }

    private Node sectionRoles() {
        List<String> roles = List.of(
                "用户管理", "订单管理", "商品管理", "财务管理",
                "权限配置", "数据导出", "系统日志", "API 文档", "审计日志"
        );
        Node t = TransferAnt.<String>create()
                .dataSource(roles)
                .targetKeys(List.of("用户管理", "订单管理"))
                .titles("可分配权限", "已分配")
                .showSearch()
                .onChange(target -> MessageAnt.success("权限已更新：共 " + target.size() + " 项"))
                .build();
        return ShowcaseSection.create()
                .title("场景 3：权限分配（admin 高频场景）")
                .description("角色赋权 / 用户加组 / 资源授权 —— admin 后台经典 Transfer 用法")
                .demo(t)
                .code("""
                        TransferAnt.<String>create()
                            .dataSource(allPermissions)
                            .targetKeys(rolePermissions)
                            .titles("可分配权限", "已分配")
                            .showSearch()
                            .onChange(target -> updateRolePermissions(target))
                            .build();
                        """)
                .build();
    }
}
