package org.openkawu.jfxium.component.control;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TreeTableAnt 展开热区测试。
 *
 * <p>背景：JavaFX 的 {@code TreeTableRowBehavior} 没有实现 {@code handleDisclosureNode}，
 * 点击 disclosure 箭头默认完全没有反应（只能双击行）。TreeTableAnt 的行工厂补了热区。</p>
 */
@DisplayName("TreeTableAnt 展开热区")
class TreeTableAntTest extends JfxTestBase {

    @Test
    @DisplayName("点击箭头（含整行高度内偏下位置）→ 切换展开/折叠")
    void arrowClick_togglesExpansion() {
        runOnFxThreadAndWait(() -> {
            TreeTableRow<?> row = layoutAndFindParentRow();
            TreeItem<String> parent = treeItemOf(row);
            Node disclosure = row.getDisclosureNode();
            assertNotNull(disclosure, "非叶子行必须有 disclosure 节点");

            boolean before = parent.isExpanded();
            double x = disclosure.getLayoutX() + disclosure.getLayoutBounds().getWidth() / 2;
            press(row, x, row.getHeight() - 3);      // 行底部：原本完全没有反应
            assertNotEquals(before, parent.isExpanded(), "点箭头热区应切换展开");
        });
    }

    @Test
    @DisplayName("点击行右侧文本区 → 不切换展开")
    void textAreaClick_doesNotToggle() {
        runOnFxThreadAndWait(() -> {
            TreeTableRow<?> row = layoutAndFindParentRow();
            TreeItem<String> parent = treeItemOf(row);
            boolean before = parent.isExpanded();
            press(row, row.getWidth() - 8, row.getHeight() / 2);
            assertEquals(before, parent.isExpanded(), "文本区点击不应切换展开");
        });
    }

    // ============================================================
    // 工具
    // ============================================================

    private static TreeItem<String> treeItemOf(TreeTableRow<?> row) {
        @SuppressWarnings("unchecked")
        TreeItem<String> item = (TreeItem<String>) row.getTreeItem();
        return item;
    }

    private static TreeTableRow<?> layoutAndFindParentRow() {
        TreeTableAnt<String> table = TreeTableAnt.create();
        // 无列时 TreeTableView 不会创建行；给一列最简单的内容列
        javafx.scene.control.TreeTableColumn<String, String> col =
                new javafx.scene.control.TreeTableColumn<>("名称");
        col.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue() == null ? null : cd.getValue().getValue()));
        table.getColumns().add(col);

        TreeItem<String> parent = new TreeItem<>("父节点");
        parent.getChildren().add(new TreeItem<>("子节点"));
        TreeItem<String> root = new TreeItem<>("根");
        root.getChildren().add(parent);
        root.setExpanded(true);
        table.setRoot(root);
        table.setPrefSize(320, 200);

        Scene scene = new Scene(new VBox(table), 340, 220);
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        scene.snapshot(null);
        scene.getRoot().applyCss();
        scene.getRoot().layout();

        for (Node n : table.lookupAll(".tree-table-row-cell")) {
            if (n instanceof TreeTableRow<?> row
                    && row.getTreeItem() == parent) {
                return row;
            }
        }
        throw new AssertionError("未找到父节点所在行");
    }

    private static void press(TreeTableRow<?> row, double x, double y) {
        Event.fireEvent(row, new MouseEvent(
                MouseEvent.MOUSE_PRESSED, x, y, 0, 0,
                MouseButton.PRIMARY, 1,
                false, false, false, false,
                true, false, false, false, false, false, null));
    }
}
