package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium 树形组件 - 对标 Ant Design Tree（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：层级数据展示控件，包装 JavaFX {@link TreeView}，
 * 提供节点工厂方法（{@link #leaf} / {@link #node}）实现声明式树结构构建。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>声明式节点构建（{@code leaf()} / {@code node()} 静态工厂）</li>
 *   <li>节点图标支持（{@code leaf(value, graphic)} / {@code node(value, graphic, children)}）</li>
 *   <li>控制根节点可见性（{@code showRoot}）</li>
 *   <li>节点选择回调（{@code onSelect}）</li>
 *   <li>继承 {@link AbstractStyleBuilder}，支持 {@code .styleClass()} / {@code .style()}</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>文件目录树（文件管理器）</li>
 *   <li>组织架构展示（公司部门树）</li>
 *   <li>分类导航（商品分类、文章分类）</li>
 *   <li>权限树（角色功能权限勾选）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * TreeView<String> tree = TreeAnt.<String>create()
 *     .root("项目",
 *         TreeAnt.node("src",
 *             TreeAnt.node("main",
 *                 TreeAnt.leaf("App.java"),
 *                 TreeAnt.leaf("Config.java")
 *             ),
 *             TreeAnt.leaf("test")
 *         ),
 *         TreeAnt.leaf("pom.xml")
 *     )
 *     .showRoot(true)
 *     .onSelect(item -> System.out.println("选中：" + item))
 *     .build();
 * }</pre>
 *
 * <h2>与 TreeSelectAnt 的区别</h2>
 * <ul>
 *   <li>{@code TreeAnt} —— 嵌入式树形展示（始终可见）</li>
 *   <li>{@code TreeSelectAnt} —— 下拉式树形选择（点击后弹出）</li>
 * </ul>
 */
public class TreeAnt<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    /**
     * 创建叶子节点
     */
    public static <T> TreeItem<T> leaf(T value) {
        return new TreeItem<>(value);
    }

    /**
     * 创建叶子节点（带图标）
     */
    public static <T> TreeItem<T> leaf(T value, Node graphic) {
        TreeItem<T> item = new TreeItem<>(value, graphic);
        return item;
    }

    /**
     * 创建父节点
     */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    /**
     * 创建父节点（带图标）
     */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, Node graphic, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value, graphic);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private TreeItem<T> root;
        private boolean showRoot = true;
        private Consumer<T> onSelect;

        private Builder() {}

        /**
         * 设置根节点
         */
        @SafeVarargs
        public final Builder<T> root(T value, TreeItem<T>... children) {
            this.root = new TreeItem<>(value);
            this.root.getChildren().addAll(children);
            this.root.setExpanded(true);
            return this;
        }

        /**
         * 设置根节点（直接传入 TreeItem）
         */
        public Builder<T> root(TreeItem<T> root) {
            this.root = root;
            return this;
        }

        /**
         * 是否显示根节点
         */
        public Builder<T> showRoot(boolean show) {
            this.showRoot = show;
            return this;
        }

        /**
         * 设置选择回调
         */
        public Builder<T> onSelect(Consumer<T> handler) {
            this.onSelect = handler;
            return this;
        }

        public TreeView<T> build() {
            TreeView<T> treeView = new TreeView<>(root);
            treeView.setShowRoot(showRoot);

            // Selection listener
            if (onSelect != null) {
                treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        onSelect.accept(newVal.getValue());
                    }
                });
            }

            applyStyles(treeView);
            return treeView;
        }
    }
}
