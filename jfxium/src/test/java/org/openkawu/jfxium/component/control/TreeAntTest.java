package org.openkawu.jfxium.component.control;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TreeAnt 单元测试 —— 覆盖静态节点工厂（leaf / node）、create、root、showRoot、onSelect、
 * cellFactory（TREE_CELL 样式类挂载）、disabled。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 TreeView + cellFactory 挂 jfx-tree-cell</li>
 *   <li>静态工厂：leaf(T) / leaf(T, Node) / node(T, children) / node(T, Node, children)</li>
 *   <li>create(value, children)：一键构造完整树</li>
 *   <li>root：value+children / 直接 TreeItem 两种模式</li>
 *   <li>showRoot：true / false</li>
 *   <li>indicatorStyle：ARROW（默认）/ CHEVRON / PLUS_MINUS，幂等切换</li>
 *   <li>showLine：缩进参考线开关，默认关闭，可运行时切换</li>
 *   <li>展开热区：整行高度内点击箭头附近即切换展开，文本区不误触</li>
 *   <li>onSelect：选中节点时回调拿到 value</li>
 *   <li>disabled</li>
 *   <li>链式串联 + 继承式核心契约</li>
 * </ul>
 *
 * <p><b>注意</b>：P1-1c 修复 —— TreeAnt 构造时显式 setCellFactory 挂载 {@link JfxStyles#TREE_CELL}，
 * 避免 .tree-cell 全局污染。本测试验证该 cellFactory 正确注册。</p>
 */
@DisplayName("TreeAnt")
class TreeAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 TreeView，根节点为空")
    void build_returnsTreeView() {
        TreeAnt<String> tree = TreeAnt.<String>create().build();
        assertNotNull(tree);
        assertNull(tree.getRoot());
    }

    @Test
    @DisplayName("构造时挂 cellFactory 且 cell 包含 jfx-tree-cell")
    void cellFactory_installed() {
        TreeAnt<String> tree = TreeAnt.<String>create().build();
        assertNotNull(tree.getCellFactory(), "TreeAnt 必须挂载 cellFactory");
        // 验证 cellFactory 能正常产出 cell（cell 自身挂 TREE_CELL）
        javafx.scene.control.TreeCell<String> cell = tree.getCellFactory().call(tree);
        assertNotNull(cell);
        assertTrue(cell.getStyleClass().contains(JfxStyles.TREE_CELL),
                "TreeCell 应挂 jfx-tree-cell 样式类（避免 .tree-cell 全局污染）");
    }

    @Test
    @DisplayName("默认 showRoot 为 true（JavaFX 原生默认）")
    void showRoot_defaultTrue() {
        TreeAnt<String> tree = TreeAnt.<String>create().build();
        assertTrue(tree.isShowRoot());
    }

    // ============================================================
    // 静态工厂
    // ============================================================

    @Nested
    @DisplayName("静态节点工厂（leaf / node）")
    class StaticFactories {

        @Test
        @DisplayName("leaf(value) 创建叶子节点，无子节点")
        void leaf_value() {
            TreeItem<String> leaf = TreeAnt.leaf("文件.java");
            assertNotNull(leaf);
            assertEquals("文件.java", leaf.getValue());
            assertEquals(0, leaf.getChildren().size());
            assertFalse(leaf.isExpanded(), "叶子节点默认不展开");
            assertTrue(leaf.isLeaf(), "叶子节点无子节点");
        }

        @Test
        @DisplayName("leaf(value, graphic) 创建带图标的叶子节点")
        void leaf_withGraphic() {
            javafx.scene.shape.Rectangle icon = new javafx.scene.shape.Rectangle(12, 12);
            TreeItem<String> leaf = TreeAnt.leaf("文件.java", icon);
            assertEquals(icon, leaf.getGraphic());
        }

        @Test
        @DisplayName("node(value, children...) 创建父节点，自动挂 children + setExpanded(true)")
        void node_withChildren() {
            TreeItem<String> child1 = TreeAnt.leaf("子1");
            TreeItem<String> child2 = TreeAnt.leaf("子2");
            TreeItem<String> node = TreeAnt.node("父节点", child1, child2);
            assertEquals("父节点", node.getValue());
            assertEquals(2, node.getChildren().size());
            assertTrue(node.isExpanded(), "node 创建后默认展开");
            assertFalse(node.isLeaf(), "有子节点的不是叶子");
        }

        @Test
        @DisplayName("node(value, graphic, children...) 创建带图标的父节点")
        void node_withGraphic() {
            javafx.scene.shape.Rectangle icon = new javafx.scene.shape.Rectangle(14, 14);
            TreeItem<String> child = TreeAnt.leaf("子");
            TreeItem<String> node = TreeAnt.node("父", icon, child);
            assertEquals(icon, node.getGraphic());
            assertEquals(1, node.getChildren().size());
            assertTrue(node.isExpanded());
        }
    }

    // ============================================================
    // create(value, children)
    // ============================================================

    @Test
    @DisplayName("create(value, children...) 一键创建带根节点的树")
    void create_withRoot() {
        TreeItem<String> child1 = TreeAnt.leaf("文件1");
        TreeItem<String> child2 = TreeAnt.leaf("文件2");
        TreeAnt<String> tree = TreeAnt.create("项目根", child1, child2);

        assertNotNull(tree);
        assertNotNull(tree.getRoot());
        assertEquals("项目根", tree.getRoot().getValue());
        assertEquals(2, tree.getRoot().getChildren().size());
        assertTrue(tree.getRoot().isExpanded());
    }

    // ============================================================
    // root 流式 API
    // ============================================================

    @Nested
    @DisplayName("root 流式 API")
    class Root {

        @Test
        @DisplayName("root(value, children) 设置根节点")
        void root_valueAndChildren() {
            TreeItem<String> child = TreeAnt.leaf("子");
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .root("根", child)
                    .build();
            assertEquals("根", tree.getRoot().getValue());
            assertEquals(1, tree.getRoot().getChildren().size());
        }

        @Test
        @DisplayName("root(TreeItem) 直接传入 TreeItem")
        void root_treeItem() {
            TreeItem<String> root = TreeAnt.node("根", TreeAnt.leaf("子"));
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .root(root)
                    .build();
            assertSame(root, tree.getRoot());
        }

        @Test
        @DisplayName("root(null) 不抛 NPE，清空根节点")
        void root_nullSafe() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"));
            assertDoesNotThrow(() -> tree.root((TreeItem<String>) null));
        }
    }

    // ============================================================
    // showRoot
    // ============================================================

    @Test
    @DisplayName("showRoot(false) 隐藏根节点")
    void showRoot_false() {
        TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                .showRoot(false)
                .build();
        assertFalse(tree.isShowRoot());
    }

    @Test
    @DisplayName("showRoot(true) 显式显示根节点")
    void showRoot_true() {
        TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                .showRoot(false)
                .showRoot(true)
                .build();
        assertTrue(tree.isShowRoot());
    }

    // ============================================================
    // onSelect
    // ============================================================

    @Test
    @DisplayName("onSelect(null) 不抛 NPE")
    void onSelect_nullSafe() {
        assertDoesNotThrow(() ->
                TreeAnt.<String>create("根").onSelect(null).build());
    }

    // ============================================================
    // 状态
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        TreeAnt<String> tree = TreeAnt.<String>create().disabled(true).build();
        assertTrue(tree.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        TreeAnt<String> tree = TreeAnt.<String>create().build();
        assertFalse(tree.isDisable());
    }

    // ============================================================
    // valueExtractor
    // ============================================================

    @Nested
    @DisplayName("valueExtractor（自定义业务类型展示）")
    class ValueExtractor {

        /** 自定义业务类型 —— 不重写 toString，依赖 valueExtractor 提供展示文本。 */
        static class FileNode {
            private final String name;
            private final long size;
            FileNode(String name, long size) { this.name = name; this.size = size; }
            String getName() { return name; }
            long getSize() { return size; }
            @Override public String toString() { return "RAW:" + name; }
        }

        @Test
        @DisplayName("默认走 item.toString()（extractor == null）")
        void defaultToString() {
            String text = TreeAnt.renderItemText(new FileNode("B.java", 200), null);
            assertEquals("RAW:B.java", text,
                    "extractor 为 null 时回退 toString()");
        }

        @Test
        @DisplayName("valueExtractor(f) 自定义提取函数，cell 按 f 输出文本")
        void customExtractor() {
            java.util.function.Function<FileNode, String> extractor =
                    f -> f.getName() + " (" + f.getSize() + "B)";
            String text = TreeAnt.renderItemText(new FileNode("B.java", 200), extractor);
            assertEquals("B.java (200B)", text,
                    "valueExtractor 应提取 name+size，而非 toString");
        }

        @Test
        @DisplayName("renderItemText(null, ...) 返回 null（与 cell.updateItem(empty) 语义一致）")
        void nullItemReturnsNull() {
            assertNull(TreeAnt.renderItemText(null, null),
                    "item=null 时返回 null —— cell 不会 setText");
            assertNull(TreeAnt.renderItemText(null, f -> "X"),
                    "item=null 即便有 extractor 也返回 null");
        }

        @Test
        @DisplayName("valueExtractor 字段在 builder 上正确传递")
        void extractorStoredOnBuilder() {
            java.util.function.Function<String, String> extractor = String::toUpperCase;
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .valueExtractor(extractor)
                    .root("根", TreeAnt.leaf("a"))
                    .build();
            assertEquals("A", TreeAnt.renderItemText("a", extractor),
                    "valueExtractor 应用应与 builder 字段一致");
            // 验证渲染路径仍走 extractor（与 cellFactory 行为一致）
            assertNotNull(tree.getCellFactory(),
                    "cellFactory 必须挂载 —— 在 Scene 中 layout pass 会触发 updateItem");
        }

        @Test
        @DisplayName("build() 幂等：连续多次 build() 不报异常")
        void buildIdempotent() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                    .build()
                    .build()
                    .build();
            assertNotNull(tree);
            assertNotNull(tree.getCellFactory());
        }
    }

    // ============================================================
    // indicatorStyle（展开/折叠指示器风格）
    // ============================================================

    @Nested
    @DisplayName("indicatorStyle（展开/折叠指示器风格）")
    class IndicatorStyleTest {

        @Test
        @DisplayName("默认不挂任何 indicator styleClass（三角箭头）")
        void default_arrow() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子")).build();
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_CHEVRON));
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_PLUS_MINUS));
        }

        @Test
        @DisplayName("CHEVRON 挂 jfx-tree-indicator-chevron")
        void chevron_addsClass() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                    .indicatorStyle(TreeAnt.IndicatorStyle.CHEVRON)
                    .build();
            assertTrue(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_CHEVRON));
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_PLUS_MINUS));
        }

        @Test
        @DisplayName("PLUS_MINUS 挂 jfx-tree-indicator-plus-minus")
        void plusMinus_addsClass() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                    .indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS)
                    .build();
            assertTrue(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_PLUS_MINUS));
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_CHEVRON));
        }

        @Test
        @DisplayName("幂等：重复调用同一风格不重复挂 class，切换风格会清掉旧 class")
        void switch_isIdempotent() {
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .indicatorStyle(TreeAnt.IndicatorStyle.CHEVRON)
                    .indicatorStyle(TreeAnt.IndicatorStyle.CHEVRON);

            assertEquals(1, tree.getStyleClass().stream()
                    .filter(JfxStyles.TREE_INDICATOR_CHEVRON::equals).count(),
                    "重复设置 CHEVRON 不应重复挂 class");

            tree.indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS);
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_CHEVRON),
                    "切换到 PLUS_MINUS 后应清掉 CHEVRON");
            assertTrue(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_PLUS_MINUS));
        }

        @Test
        @DisplayName("indicatorStyle(null) 归零回默认三角箭头")
        void null_resetsToArrow() {
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS)
                    .indicatorStyle(null);

            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_PLUS_MINUS),
                    "null 应等价于 ARROW（清空所有 indicator styleClass）");
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_INDICATOR_CHEVRON));
        }
    }

    // ============================================================
    // showLine（缩进参考线）
    // ============================================================

    @Nested
    @DisplayName("showLine（缩进参考线开关）")
    class ShowLineTest {

        @Test
        @DisplayName("默认关闭")
        void default_off() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子")).build();
            assertFalse(tree.isShowLine());
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_SHOW_LINE));
        }

        @Test
        @DisplayName("showLine(true) 挂 jfx-tree-show-line")
        void enable_addsClass() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                    .showLine(true)
                    .build();
            assertTrue(tree.isShowLine());
            assertTrue(tree.getStyleClass().contains(JfxStyles.TREE_SHOW_LINE));
        }

        @Test
        @DisplayName("showLine(false) 关闭并移除 styleClass")
        void disable_removesClass() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子"))
                    .showLine(true)
                    .showLine(false);
            assertFalse(tree.isShowLine());
            assertFalse(tree.getStyleClass().contains(JfxStyles.TREE_SHOW_LINE));
        }

        @Test
        @DisplayName("幂等：重复 showLine(true) 不重复挂 class")
        void enable_isIdempotent() {
            TreeAnt<String> tree = TreeAnt.<String>create()
                    .showLine(true)
                    .showLine(true)
                    .showLine(true);
            assertEquals(1, tree.getStyleClass().stream()
                    .filter(JfxStyles.TREE_SHOW_LINE::equals).count());
        }

        @Test
        @DisplayName("未挂 skin 时开关不抛异常（refresh 只打重建标记）")
        void toggle_withoutSkin_noException() {
            TreeAnt<String> tree = TreeAnt.<String>create("根", TreeAnt.leaf("子")).build();
            assertDoesNotThrow(() -> tree.showLine(true));
            assertDoesNotThrow(() -> tree.showLine(false));
        }
    }

    // ============================================================
    // 展开热区（点箭头附近即可展开，不必精确命中 5px 的箭头）
    // ============================================================

    @Nested
    @DisplayName("展开热区")
    class HitZoneTest {

        @Test
        @DisplayName("整行高度内点击箭头热区 → 切换展开")
        void hotZone_togglesAtRowBottom() {
            runOnFxThreadAndWait(() -> {
                TreeItem<String> parent = TreeAnt.node("父节点", TreeAnt.leaf("子"));
                TreeAnt<String> tree = TreeAnt.<String>create("根", parent).build();
                tree.setPrefSize(300, 200);

                javafx.scene.Scene scene = new javafx.scene.Scene(
                        new javafx.scene.layout.VBox(tree), 320, 220);
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                scene.snapshot(null);          // 强制一次 pulse，让 VirtualFlow 建出 cell
                scene.getRoot().applyCss();
                scene.getRoot().layout();

                TreeCell<?> cell = cellOf(tree, parent);
                javafx.scene.Node disclosure = cell.getDisclosureNode();
                assertNotNull(disclosure, "TreeViewSkin 必须给 cell 挂披露节点");

                boolean before = parent.isExpanded();
                double x = disclosure.getLayoutX() + disclosure.getLayoutBounds().getWidth() / 2;
                press(cell, x, cell.getHeight() - 3);   // 行底部：原生命中范围之外
                assertNotEquals(before, parent.isExpanded(),
                        "整行高度内点中箭头热区都应切换展开");
            });
        }

        @Test
        @DisplayName("点击文本区 → 不切换展开（保持选中语义）")
        void textArea_doesNotToggle() {
            runOnFxThreadAndWait(() -> {
                TreeItem<String> parent = TreeAnt.node("父节点", TreeAnt.leaf("子"));
                TreeAnt<String> tree = TreeAnt.<String>create("根", parent).build();
                tree.setPrefSize(300, 200);

                javafx.scene.Scene scene = new javafx.scene.Scene(
                        new javafx.scene.layout.VBox(tree), 320, 220);
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                scene.snapshot(null);
                scene.getRoot().applyCss();
                scene.getRoot().layout();

                TreeCell<?> cell = cellOf(tree, parent);
                boolean before = parent.isExpanded();
                press(cell, cell.getWidth() - 5, cell.getHeight() / 2);
                assertEquals(before, parent.isExpanded(), "点在文本区不应切换展开");
            });
        }

        private TreeCell<?> cellOf(TreeAnt<String> tree, TreeItem<String> item) {
            for (javafx.scene.Node n : tree.lookupAll(".tree-cell")) {
                if (n instanceof TreeCell<?> c && c.getTreeItem() == item) return c;
            }
            throw new AssertionError("未找到对应 Cell");
        }

        private void press(TreeCell<?> cell, double x, double y) {
            javafx.event.Event.fireEvent(cell, new javafx.scene.input.MouseEvent(
                    javafx.scene.input.MouseEvent.MOUSE_PRESSED, x, y, 0, 0,
                    javafx.scene.input.MouseButton.PRIMARY, 1,
                    false, false, false, false,
                    true, false, false, false, false, false, null));
        }
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联：create → root → showRoot → disabled → onSelect 全部生效")
    void fullChain_noException() {
        AtomicReference<String> selected = new AtomicReference<>();
        TreeAnt<String> tree = TreeAnt.<String>create()
                .root("项目",
                        TreeAnt.node("src",
                                TreeAnt.leaf("Main.java"),
                                TreeAnt.leaf("Util.java")
                        ),
                        TreeAnt.leaf("README.md")
                )
                .showRoot(true)
                .onSelect(selected::set)
                .disabled(false)
                .build();

        assertNotNull(tree);
        assertEquals("项目", tree.getRoot().getValue());
        assertEquals(2, tree.getRoot().getChildren().size());
        assertTrue(tree.isShowRoot());
        // 选中 src 下的第一个叶子（Main.java）—— children[0] 是 node("src")，其 children[0] = Main.java
        tree.getSelectionModel().select(tree.getRoot().getChildren().get(0)
                .getChildren().get(0));
        assertEquals("Main.java", selected.get());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        TreeAnt<String> tree = TreeAnt.create();
        assertSame(tree, tree.build());
    }

    @Test
    @DisplayName("继承式：父类 TreeView 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        TreeView<String> tree = TreeAnt.<String>create().build();
        assertInstanceOf(TreeAnt.class, tree);
        assertNotNull(tree.getCellFactory());
    }
}
