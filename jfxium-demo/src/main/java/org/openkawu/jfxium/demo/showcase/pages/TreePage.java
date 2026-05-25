package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.TreeAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Tree 树形组件展示页（M19.9）。
 */
public class TreePage implements ShowcasePage {

    @Override public String   key()      { return "tree"; }
    @Override public String   title()    { return "Tree 树形"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Tree 树形");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("admin 高频：部门树、权限树、文件目录、菜单树。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithIcons(),
                        sectionHideRoot(),
                        sectionOnSelect()
                )
                .build();
    }

    private Node sectionBasic() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root("我的项目",
                        TreeAnt.node("前端",
                                TreeAnt.leaf("React 应用"),
                                TreeAnt.leaf("Vue 应用"),
                                TreeAnt.leaf("静态站点")
                        ),
                        TreeAnt.node("后端",
                                TreeAnt.leaf("Java 服务"),
                                TreeAnt.leaf("Python 服务")
                        ),
                        TreeAnt.leaf("README.md")
                )
                .build();
        tree.setPrefSize(280, 280);

        return ShowcaseSection.create()
                .title("场景 1：基础树（文本节点）")
                .description("TreeAnt.node(...) 父节点 + .leaf(...) 叶子节点；最外层 .root() 包成 TreeView")
                .demo(tree)
                .code("""
                        TreeView<String> tree = TreeAnt.<String>create()
                            .root("我的项目",
                                TreeAnt.node("前端",
                                    TreeAnt.leaf("React 应用"),
                                    TreeAnt.leaf("Vue 应用")
                                ),
                                TreeAnt.node("后端",
                                    TreeAnt.leaf("Java 服务")
                                ),
                                TreeAnt.leaf("README.md")
                            )
                            .build();
                        """)
                .build();
    }

    private Node sectionWithIcons() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root(TreeAnt.node("admin", IconAnt.path(IconAnt.Path.DASHBOARD, 14),
                        TreeAnt.node("用户管理", IconAnt.path(IconAnt.Path.USERS, 14),
                                TreeAnt.leaf("用户列表", IconAnt.path(IconAnt.Path.USER, 14)),
                                TreeAnt.leaf("角色权限", IconAnt.path(IconAnt.Path.SETTINGS, 14))
                        ),
                        TreeAnt.node("系统设置", IconAnt.path(IconAnt.Path.SETTINGS, 14),
                                TreeAnt.leaf("通用配置", IconAnt.path(IconAnt.Path.FILE, 14)),
                                TreeAnt.leaf("通知设置", IconAnt.path(IconAnt.Path.BELL, 14))
                        )
                ))
                .build();
        tree.setPrefSize(280, 280);

        return ShowcaseSection.create()
                .title("场景 2：带图标（admin 菜单树）")
                .description(".node(value, graphic, ...) / .leaf(value, graphic) —— 图标在节点文字左侧")
                .demo(tree)
                .code("""
                        TreeView<String> tree = TreeAnt.<String>create()
                            .root("admin", IconAnt.path(IconAnt.Path.DASHBOARD, 14),
                                TreeAnt.node("用户管理", IconAnt.path(IconAnt.Path.USERS, 14),
                                    TreeAnt.leaf("用户列表", IconAnt.path(IconAnt.Path.USER, 14))
                                )
                            )
                            .build();
                        """)
                .build();
    }

    private Node sectionHideRoot() {
        TreeView<String> tree = TreeAnt.<String>create()
                .root("（隐藏的根）",
                        TreeAnt.leaf("项目 A"),
                        TreeAnt.leaf("项目 B"),
                        TreeAnt.leaf("项目 C")
                )
                .showRoot(false)
                .build();
        tree.setPrefSize(280, 200);

        return ShowcaseSection.create()
                .title("场景 3：隐藏根节点")
                .description(".showRoot(false) —— 一级分类无需顶层包装时使用")
                .demo(tree)
                .code("""
                        TreeView<String> tree = TreeAnt.<String>create()
                            .root("（隐藏的根）",
                                TreeAnt.leaf("项目 A"),
                                TreeAnt.leaf("项目 B")
                            )
                            .showRoot(false)
                            .build();
                        """)
                .build();
    }

    private Node sectionOnSelect() {
        Label out = new Label("当前未选中");
        out.setStyle("-fx-text-fill: -color-fg-muted;");

        TreeView<String> tree = TreeAnt.<String>create()
                .root("权限树",
                        TreeAnt.node("用户管理",
                                TreeAnt.leaf("查看"),
                                TreeAnt.leaf("新增"),
                                TreeAnt.leaf("编辑"),
                                TreeAnt.leaf("删除")
                        ),
                        TreeAnt.node("订单管理",
                                TreeAnt.leaf("查看"),
                                TreeAnt.leaf("退款")
                        )
                )
                .onSelect(value -> {
                    if (value != null) out.setText("已选中：" + value);
                })
                .build();
        tree.setPrefSize(280, 280);

        HBox row = HBoxBuilder.create().spacing(20).children(tree, out).build();

        return ShowcaseSection.create()
                .title("场景 4：选中回调")
                .description(".onSelect(value -> ...) —— 选权限/部门联动右侧详情区")
                .demo(row)
                .code("""
                        TreeView<String> tree = TreeAnt.<String>create()
                            .root(...)
                            .onSelect(value -> reloadDetail(value))
                            .build();
                        """)
                .build();
    }
}
