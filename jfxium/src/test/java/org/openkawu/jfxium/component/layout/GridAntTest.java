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
    @DisplayName("响应式快捷 span 写法可自动换行并随断点恢复分栏")
    void responsiveColumnsWrapInsteadOfBeingTruncated() throws Exception {
        Label first = new Label("1");
        Label second = new Label("2");
        Label third = new Label("3");
        Label fourth = new Label("4");

        GridAnt.Builder builder = GridAnt.create()
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(first).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(second).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(third).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(fourth).responsive(24, 12, 12, 6, 6, 6)));

        VBox container = builder.build();
        Method rebuildRows = GridAnt.Builder.class.getDeclaredMethod(
                "rebuildRows", VBox.class, GridAnt.Breakpoint.class);
        rebuildRows.setAccessible(true);
        rebuildRows.invoke(builder, container, GridAnt.Breakpoint.XS);

        GridPane row = assertInstanceOf(GridPane.class, container.getChildren().get(0));
        assertEquals(4, row.getChildren().size());
        assertEquals(4, row.getRowConstraints().size());

        assertEquals(0, GridPane.getRowIndex(first));
        assertEquals(1, GridPane.getRowIndex(second));
        assertEquals(2, GridPane.getRowIndex(third));
        assertEquals(3, GridPane.getRowIndex(fourth));

        assertEquals(24, GridPane.getColumnSpan(first));
        assertEquals(24, GridPane.getColumnSpan(second));
        assertEquals(24, GridPane.getColumnSpan(third));
        assertEquals(24, GridPane.getColumnSpan(fourth));

        rebuildRows.invoke(builder, container, GridAnt.Breakpoint.LG);
        GridPane largeRow = assertInstanceOf(GridPane.class, container.getChildren().get(0));
        assertEquals(1, largeRow.getRowConstraints().size());
        assertEquals(6, GridPane.getColumnSpan(first));
        assertEquals(6, GridPane.getColumnSpan(second));
        assertEquals(6, GridPane.getColumnSpan(third));
        assertEquals(6, GridPane.getColumnSpan(fourth));
        assertEquals(0, GridPane.getRowIndex(first));
        assertEquals(0, GridPane.getRowIndex(second));
        assertEquals(0, GridPane.getRowIndex(third));
        assertEquals(0, GridPane.getRowIndex(fourth));
    }

    @Test
    @DisplayName("Row.align(null) 安全忽略，不覆盖默认对齐")
    void rowAlignNullIsIgnored() throws Exception {
        GridAnt.Row row = GridAnt.row().align(null);

        Method getAlignment = GridAnt.Row.class.getDeclaredMethod("getAlignment");
        getAlignment.setAccessible(true);

        assertEquals(javafx.geometry.Pos.CENTER_LEFT, getAlignment.invoke(row));
    }

    @Test
    @DisplayName("gutter 输入会钳制为非负值，避免负间距造成重叠布局")
    void negativeGuttersAreClampedToZero() {
        VBox container = GridAnt.create()
                .gutter(-8)
                .rowGutter(-4)
                .columnGutter(-2)
                .row(GridAnt.row().col(12, new Label("A")).col(12, new Label("B")))
                .build();

        GridPane row = assertInstanceOf(GridPane.class, container.getChildren().get(0));
        assertEquals(0, container.getSpacing());
        assertEquals(0, row.getHgap());
        assertEquals(0, row.getVgap());
    }

    @Test
    @DisplayName("响应式 Grid 首次 build 使用 XS 断点，避免入场景前先按超宽布局渲染")
    void responsiveGridBuildsWithXsBreakpointBeforeSceneAttachment() {
        Label left = new Label("L");
        Label right = new Label("R");

        VBox container = GridAnt.create()
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(left).xs(24).lg(12))
                        .col(GridAnt.col(right).xs(24).lg(12)))
                .build();

        GridPane row = assertInstanceOf(GridPane.class, container.getChildren().get(0));
        assertEquals(2, row.getRowConstraints().size());
        assertEquals(24, GridPane.getColumnSpan(left));
        assertEquals(24, GridPane.getColumnSpan(right));
        assertEquals(0, GridPane.getRowIndex(left));
        assertEquals(1, GridPane.getRowIndex(right));
    }
}
