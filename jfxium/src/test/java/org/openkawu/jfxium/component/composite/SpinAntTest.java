package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("SpinAnt")
class SpinAntTest extends JfxTestBase {

    @Test
    @DisplayName("size(null) / indicator(null) 回退默认值")
    void nullEnums_fallbackToDefaults() {
        VBox spin = SpinAnt.create()
                .size(null)
                .indicator(null)
                .build();

        assertNotNull(spin);
        assertEquals(1, spin.getChildren().size());
    }

    /** 可在 StackPane/HBox/VBox 里被拉伸填满的占位 Region。 */
    private Region fillableRegion() {
        Region r = new Region();
        r.setMinSize(0, 0);
        r.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return r;
    }

    private void layout(Node root) {
        root.getScene().getRoot().applyCss();
        root.getScene().getRoot().layout();
    }

    @Test
    @DisplayName("overlay 包裹后保留 VBox.vgrow，target 仍撑满高度")
    void overlay_vboxVgrow_preserved() {
        runOnFxThreadAndWait(() -> {
            VBox root = new VBox();
            Region child = fillableRegion();
            VBox.setVgrow(child, Priority.ALWAYS);
            root.getChildren().add(child);
            new Scene(root, 400, 400);
            layout(root);
            assertEquals(400, child.getHeight(), 1.0, "包裹前应撑满");

            SpinAnt.overlay(child);
            Node wrapper = child.getParent();
            layout(root);

            assertEquals(Priority.ALWAYS, VBox.getVgrow(wrapper), "vgrow 必须迁移到 wrapper");
            assertEquals(400, wrapper.getLayoutBounds().getHeight(), 1.0, "wrapper 应继续撑满 VBox");
            assertEquals(wrapper.getLayoutBounds().getHeight(), child.getLayoutBounds().getHeight(), 1.0,
                    "target 应填满 wrapper");
        });
    }

    @Test
    @DisplayName("overlay 包裹后保留 VBox.margin")
    void overlay_vboxMargin_preserved() {
        runOnFxThreadAndWait(() -> {
            VBox root = new VBox();
            Region child = fillableRegion();
            Insets margin = new Insets(3, 5, 7, 11);
            VBox.setMargin(child, margin);
            root.getChildren().add(child);
            new Scene(root, 400, 400);

            SpinAnt.overlay(child);
            Node wrapper = child.getParent();

            assertEquals(margin, VBox.getMargin(wrapper));
        });
    }

    @Test
    @DisplayName("overlay 包裹后保留 HBox.hgrow 与 margin，target 仍撑满宽度")
    void overlay_hboxHgrow_preserved() {
        runOnFxThreadAndWait(() -> {
            HBox root = new HBox();
            Region child = fillableRegion();
            HBox.setHgrow(child, Priority.ALWAYS);
            Insets margin = new Insets(2, 4, 6, 8);
            HBox.setMargin(child, margin);
            root.getChildren().add(child);
            new Scene(root, 400, 400);
            layout(root);
            assertEquals(388, child.getWidth(), 1.0, "包裹前应撑满(扣 margin)");

            SpinAnt.overlay(child);
            Node wrapper = child.getParent();
            layout(root);

            assertEquals(Priority.ALWAYS, HBox.getHgrow(wrapper), "hgrow 必须迁移到 wrapper");
            assertEquals(margin, HBox.getMargin(wrapper));
            assertEquals(388, wrapper.getLayoutBounds().getWidth(), 1.0, "wrapper 应继续撑满 HBox(扣 margin)");
        });
    }

    @Test
    @DisplayName("overlay 包裹后保留 GridPane 行列定位/跨度/对齐约束")
    void overlay_gridConstraints_preserved() {
        runOnFxThreadAndWait(() -> {
            GridPane grid = new GridPane();
            Region child = fillableRegion();
            GridPane.setRowIndex(child, 2);
            GridPane.setColumnIndex(child, 3);
            GridPane.setRowSpan(child, 2);
            GridPane.setColumnSpan(child, 4);
            GridPane.setHgrow(child, Priority.ALWAYS);
            GridPane.setVgrow(child, Priority.ALWAYS);
            GridPane.setMargin(child, new Insets(1, 2, 3, 4));
            grid.getChildren().add(child);
            new Scene(grid, 400, 400);

            SpinAnt.overlay(child);
            Node wrapper = child.getParent();

            assertEquals(2, GridPane.getRowIndex(wrapper));
            assertEquals(3, GridPane.getColumnIndex(wrapper));
            assertEquals(2, GridPane.getRowSpan(wrapper));
            assertEquals(4, GridPane.getColumnSpan(wrapper));
            assertEquals(Priority.ALWAYS, GridPane.getHgrow(wrapper));
            assertEquals(Priority.ALWAYS, GridPane.getVgrow(wrapper));
            assertEquals(new Insets(1, 2, 3, 4), GridPane.getMargin(wrapper));
        });
    }

    @Test
    @DisplayName("overlay(scene.getRoot()) 覆盖全窗口：wrapper 顶替为 root 且 target 撑满")
    void overlay_sceneRoot_supported() {
        runOnFxThreadAndWait(() -> {
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 400, 300);
            layout(root);

            SpinAnt.overlay(root);
            Parent wrapper = root.getParent();

            assertNotNull(wrapper, "target 应被包进 wrapper");
            assertSame(wrapper, scene.getRoot(), "wrapper 应顶替为新的 scene root");
            assertEquals(root, wrapper.getChildrenUnmodifiable().get(0), "target 应成为 wrapper 首个子节点");
            layout(root);
            assertEquals(400, root.getLayoutBounds().getWidth(), 1.0);
            assertEquals(300, root.getLayoutBounds().getHeight(), 1.0);
        });
    }
}
