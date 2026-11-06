package org.openkawu.jfxium.component.control;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 列宽契约回归测试 —— 见 PROJECT_BUG.md #184。
 *
 * <p>{@code columnWidth(pref, min, max)} 的语义是"当前列宽度被钉在 [min, max] 内"。
 * 但 JavaFX 的 {@code TableColumn} 只在 {@code doSetWidth} 里做单次钳制，
 * 不校验三者之间的先后矛盾，因此这里锁定两条不变量：</p>
 * <ol>
 *   <li>{@code min == max} 时，列宽在任何表格宽度、任何 resize 策略下都恒等于该值；</li>
 *   <li>非法区间（{@code min > max}、非有限值）必须被归一化成自洽状态
 *       （{@code min <= width <= max}）。</li>
 * </ol>
 */
@DisplayName("列宽契约 (columnWidth 三参)")
class ColumnWidthContractTest extends JfxTestBase {

    public record Row(String a, String b, String c, String d) {
    }

    private static TableAnt<Row> fixedColumns(TableAnt.Resize resize) {
        return TableAnt.<Row>create()
                .column("A", Row::a).columnWidth(100, 100, 100)
                .column("B", Row::b).columnWidth(100, 100, 100)
                .column("C", Row::c).columnWidth(100, 100, 100)
                .column("D", Row::d).columnWidth(100, 100, 100)
                .resizePolicy(resize)
                .data(List.of(new Row("1", "2", "3", "4")))
                .build();
    }

    private static void layout(TableAnt<Row> table, double sceneWidth) {
        VBox root = new VBox(table);
        Scene scene = new Scene(root, sceneWidth, 300);
        scene.getStylesheets().add(ColumnWidthContractTest.class
                .getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        root.applyCss();
        root.layout();
        root.applyCss();
        root.layout();
    }

    @Test
    @DisplayName("min == max：表格宽 800 / 320、两种 resize 策略下列宽恒为 100")
    void minEqualsMaxPinsWidth() {
        runOnFxThreadAndWait(() -> {
            for (TableAnt.Resize resize : new TableAnt.Resize[]{
                    TableAnt.Resize.CONSTRAINED, TableAnt.Resize.UNCONSTRAINED}) {
                for (double sceneWidth : new double[]{800, 320}) {
                    TableAnt<Row> table = fixedColumns(resize);
                    layout(table, sceneWidth);
                    String ctx = resize + " @ " + sceneWidth + "px";
                    for (TableColumn<Row, ?> c : table.getColumns()) {
                        assertEquals(100, c.getWidth(), 0.5, ctx + " 列 " + c.getText() + " 宽度必须被钉在 100");
                        assertEquals(100, c.getMinWidth(), 0.5, ctx + " min");
                        assertEquals(100, c.getMaxWidth(), 0.5, ctx + " max");
                    }
                }
            }
        });
    }

    @Test
    @DisplayName("min > max：归一化为 min <= max，宽度落在区间内")
    void invertedRangeSelfHeals() {
        runOnFxThreadAndWait(() -> {
            TableAnt<Row> table = TableAnt.<Row>create()
                    .column("A", Row::a).columnWidth(100, 120, 80)
                    .data(List.of(new Row("1", "2", "3", "4")))
                    .build();
            layout(table, 800);

            TableColumn<Row, ?> c = table.getColumns().get(0);
            assertTrue(c.getMaxWidth() >= c.getMinWidth(),
                    "max(" + c.getMaxWidth() + ") 不得小于 min(" + c.getMinWidth() + ")");
            assertTrue(c.getWidth() >= c.getMinWidth() && c.getWidth() <= c.getMaxWidth(),
                    "width(" + c.getWidth() + ") 必须落在 [" + c.getMinWidth() + ", " + c.getMaxWidth() + "]");
        });
    }

    @Test
    @DisplayName("pref 超出区间：钳到 max，不越界")
    void prefClampedIntoRange() {
        runOnFxThreadAndWait(() -> {
            TableAnt<Row> table = TableAnt.<Row>create()
                    .column("A", Row::a).columnWidth(200, 100, 120)
                    .data(List.of(new Row("1", "2", "3", "4")))
                    .build();
            layout(table, 800);

            TableColumn<Row, ?> c = table.getColumns().get(0);
            assertEquals(120, c.getWidth(), 0.5, "pref 200 超出 max 120 时应钳到 120");
            assertEquals(120, c.getMaxWidth(), 0.5, "max 不应被改动");
        });
    }
}
