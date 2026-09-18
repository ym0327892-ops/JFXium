package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TreeSelectAnt")
class TreeSelectAntTest extends JfxTestBase {

    @Test
    @DisplayName("TreeNode null value / label 构建安全")
    void treeNode_nullSafe() {
        TreeSelectAnt.TreeNode node = new TreeSelectAnt.TreeNode(null, null, null);

        assertEquals("", node.getValue());
        assertEquals("", node.getLabel());
        assertNotNull(node.getChildren());
    }

    @Test
    @DisplayName("null tree / placeholder 构建安全")
    void build_nullValues_safe() {
        TreeSelectAnt.TreeNode root = new TreeSelectAnt.TreeNode(null, null, List.of(
                new TreeSelectAnt.TreeNode(null, null, null)
        ));

        HBox treeSelect = TreeSelectAnt.create()
                .placeholder(null)
                .tree(root)
                .multiple(true)
                .build();

        assertNotNull(treeSelect);
        assertTrue(treeSelect.getStyleClass().contains(JfxStyles.TREE_SELECT));
        TextField field = findTextField(treeSelect);
        assertNotNull(field);
    }

    private static TextField findTextField(Node node) {
        if (node instanceof TextField textField) {
            return textField;
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                TextField found = findTextField(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
