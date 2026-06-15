package org.openkawu.jfxium.component.layout;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GridAnt 单元测试")
class GridAntTest extends JfxTestBase {

    @Test
    @DisplayName("响应式重建前会先解绑旧行里的子节点，避免重复 parent 异常")
    void rebuildRowsDetachesOldParentsBeforeRebuild() throws Exception {
        Label left = new Label("L");
        Label right = new Label("R");

        GridAnt.Builder builder = GridAnt.create()
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(left).xs(24).lg(12))
                        .col(GridAnt.col(right).xs(24).lg(12)));

        VBox container = builder.build();
        GridPane oldRow = assertInstanceOf(GridPane.class, container.getChildren().get(0));

        Method rebuildRows = GridAnt.Builder.class.getDeclaredMethod(
                "rebuildRows", VBox.class, GridAnt.Breakpoint.class);
        rebuildRows.setAccessible(true);

        assertDoesNotThrow(() -> rebuildRows.invoke(builder, container, GridAnt.Breakpoint.XS));
        assertNotSame(oldRow, container.getChildren().get(0));
        assertSame(container.getChildren().get(0), left.getParent());
        assertSame(container.getChildren().get(0), right.getParent());
    }

    @Test
    @DisplayName("Row.align(null) 安全忽略，不覆盖默认对齐")
    void rowAlignNullIsIgnored() throws Exception {
        GridAnt.Row row = GridAnt.row().align(null);

        Method getAlignment = GridAnt.Row.class.getDeclaredMethod("getAlignment");
        getAlignment.setAccessible(true);

        assertEquals(javafx.geometry.Pos.CENTER_LEFT, getAlignment.invoke(row));
    }
}
