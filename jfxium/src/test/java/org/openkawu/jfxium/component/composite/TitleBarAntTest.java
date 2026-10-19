package org.openkawu.jfxium.component.composite;

import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TitleBarAnt} —— 自绘 Win11 标题栏。
 *
 * <p>锁三件事：</p>
 * <ol>
 *   <li><b>结构</b>：图标可选，标题 / 弹性空隙 / 窗口按钮顺序固定，build 幂等</li>
 *   <li><b>窗口交互</b>：拖动位移 = 鼠标位移、双击切换最大化、栏内控件按下不拖动</li>
 *   <li><b>解析后的 CSS</b>：32px 高、与内容同色 + 1px 底边、窗口按钮右贴边、
 *       标题 12px —— 这些只在真机解析时能验出来</li>
 * </ol>
 */
@DisplayName("TitleBarAnt 单元测试")
class TitleBarAntTest extends JfxTestBase {

    private static final String FLUENT = "/org/openkawu/jfxium/css/theme-fluent.css";

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    private static StackPane mount(Scene[] holder, String themeCss, Node content) {
        StackPane root = new StackPane(content);
        Scene scene = new Scene(root, 600, 300);
        scene.getStylesheets().add(
                TitleBarAntTest.class.getResource(themeCss).toExternalForm());
        holder[0] = scene;
        root.applyCss();
        root.layout();
        return root;
    }

    /** 构造一个只带屏幕坐标的合成鼠标事件（x/y 与 screen 相同，便于断言）。 */
    private static MouseEvent mouse(EventType<MouseEvent> type, double screenX, double screenY,
                                    int clickCount, Node pick) {
        return new MouseEvent(type, 0, 0, screenX, screenY, MouseButton.PRIMARY, clickCount,
                false, false, false, false,
                true, false, false,
                false, false, false,
                new PickResult(pick, 0, 0));
    }

    private static Stage stageAt(double x, double y) {
        Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        return stage;
    }

    private static void assertColor(Color expected, Paint actual, String label) {
        assertNotNull(actual, label + "：没有解析到颜色");
        assertTrue(actual instanceof Color, label + "：不是纯色，实为 " + actual);
        Color c = (Color) actual;
        assertEquals(expected.getRed(), c.getRed(), 0.01, label + " 红分量不符：" + c);
        assertEquals(expected.getGreen(), c.getGreen(), 0.01, label + " 绿分量不符：" + c);
        assertEquals(expected.getBlue(), c.getBlue(), 0.01, label + " 蓝分量不符：" + c);
        assertEquals(expected.getOpacity(), c.getOpacity(), 0.01, label + " 透明度不符：" + c);
    }

    // ---------------------------------------------------------------
    // 1. 结构
    // ---------------------------------------------------------------

    @Test
    @DisplayName("无图标：子节点 = [标题, 空隙, 窗口按钮]")
    void structureWithoutIcon() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title("我的应用").build();

            assertTrue(bar.getStyleClass().contains(JfxStyles.TITLE_BAR));
            assertEquals(3, bar.getChildren().size());

            Label title = (Label) bar.lookup("." + JfxStyles.TITLE_BAR_TITLE);
            assertNotNull(title);
            assertEquals("我的应用", title.getText());
            assertSame(bar.controls(), bar.getChildren().get(2));
        });
    }

    @Test
    @DisplayName("有图标：图标在最左并自动挂 .jfx-title-bar-icon")
    void iconIsFirstAndTagged() {
        runOnFxThreadAndWait(() -> {
            SVGPath icon = IconPath.create(IconPath.FOLDER);

            TitleBarAnt bar = TitleBarAnt.create().icon(icon).title("t").build();

            assertEquals(4, bar.getChildren().size());
            assertSame(icon, bar.getChildren().get(0));
            assertTrue(icon.getStyleClass().contains(JfxStyles.TITLE_BAR_ICON));
        });
    }

    @Test
    @DisplayName("标题 null → 空串（防御性默认，不抛 NPE）")
    void nullTitleBecomesEmpty() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title(null).build();

            Label title = (Label) bar.lookup("." + JfxStyles.TITLE_BAR_TITLE);
            assertEquals("", title.getText());
        });
    }

    @Test
    @DisplayName("build 幂等：重复调用不累积子节点")
    void buildIsIdempotent() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title("t");

            bar.build();
            bar.build();

            assertEquals(3, bar.getChildren().size());
        });
    }

    // ---------------------------------------------------------------
    // 1b. 三段式插槽（left / center / right）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("left：位于标题之后、弹性空隙之前（可放 ToolBarAnt / HBarAnt）")
    void leftNodesSitBetweenTitleAndSpacer() {
        runOnFxThreadAndWait(() -> {
            HBarAnt tools = HBarAnt.create()
                    .left(LabelAnt.create("工具").build())
                    .build();

            TitleBarAnt bar = TitleBarAnt.create().title("t").left(tools).build();

            assertEquals(4, bar.getChildren().size());
            assertSame(tools, bar.getChildren().get(1));
            assertTrue(bar.getChildren().get(2) instanceof Region, "left 之后应是弹性空隙");
            assertSame(bar.controls(), bar.getChildren().get(3));
        });
    }

    @Test
    @DisplayName("center：两侧各一个弹性空隙，真正居中")
    void centerCreatesTwoSpacers() {
        runOnFxThreadAndWait(() -> {
            Label tabs = LabelAnt.create("标签页").build();

            TitleBarAnt bar = TitleBarAnt.create().title("t").center(tabs).build();

            // [标题, spacer, center, spacer, controls]
            assertEquals(5, bar.getChildren().size());
            assertTrue(bar.getChildren().get(1) instanceof Region);
            assertSame(tabs, bar.getChildren().get(2));
            assertTrue(bar.getChildren().get(3) instanceof Region);
            assertSame(bar.controls(), bar.getChildren().get(4));
        });
    }

    @Test
    @DisplayName("right：位于窗口按钮之前，按钮组始终最右")
    void rightNodesSitBeforeWindowControls() {
        runOnFxThreadAndWait(() -> {
            Label avatar = LabelAnt.create("张").build();

            TitleBarAnt bar = TitleBarAnt.create().title("t").right(avatar).build();

            int last = bar.getChildren().size() - 1;
            assertSame(avatar, bar.getChildren().get(last - 1));
            assertSame(bar.controls(), bar.getChildren().get(last));
        });
    }

    @Test
    @DisplayName("标题为空时不占位（managed=false），左侧可直接交给 left(...)")
    void emptyTitleDoesNotTakeSpace() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().build();
            Label title = (Label) bar.lookup("." + JfxStyles.TITLE_BAR_TITLE);
            assertNotNull(title, "标题节点应保留，便于后续 title(...) 复用");
            assertFalse(title.isManaged(), "空标题不参与布局");
            assertFalse(title.isVisible());

            bar.title("有标题").build();
            assertTrue(title.isManaged());
            assertTrue(title.isVisible());
        });
    }

    @Test
    @DisplayName("windowControls(false)：整组窗口按钮隐藏且不占位")
    void windowControlsCanBeHidden() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title("t").windowControls(false).build();

            assertFalse(bar.controls().isManaged());
            assertFalse(bar.controls().isVisible());

            bar.windowControls(true);
            assertTrue(bar.controls().isManaged());
        });
    }

    @Test
    @DisplayName("插槽节点可多次累加，且 build 幂等不重复")
    void slotsAccumulateAndRebuildIsIdempotent() {
        runOnFxThreadAndWait(() -> {
            Label a = LabelAnt.create("A").build();
            Label b = LabelAnt.create("B").build();

            TitleBarAnt bar = TitleBarAnt.create()
                    .title("t")
                    .left(a)
                    .left(b)
                    .build();

            int expected = 5; // 标题 + A + B + spacer + controls
            assertEquals(expected, bar.getChildren().size());

            bar.build();
            assertEquals(expected, bar.getChildren().size(), "重复 build 不得累积");
            assertSame(a, bar.getChildren().get(1));
            assertSame(b, bar.getChildren().get(2));
        });
    }

    // ---------------------------------------------------------------
    // 2. 窗口交互
    // ---------------------------------------------------------------

    @Test
    @DisplayName("拖动标题栏 → 窗口按鼠标位移平移")
    void dragMovesStage() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).title("t").build();

            bar.fireEvent(mouse(MouseEvent.MOUSE_PRESSED, 500, 300, 1, bar));
            bar.fireEvent(mouse(MouseEvent.MOUSE_DRAGGED, 560, 340, 1, bar));
            bar.fireEvent(mouse(MouseEvent.MOUSE_RELEASED, 560, 340, 1, bar));

            assertEquals(160.0, stage.getX(), 0.01);
            assertEquals(140.0, stage.getY(), 0.01);
        });
    }

    @Test
    @DisplayName("draggable(false) → 拖动不生效")
    void dragDisabled() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).draggable(false).build();

            bar.fireEvent(mouse(MouseEvent.MOUSE_PRESSED, 500, 300, 1, bar));
            bar.fireEvent(mouse(MouseEvent.MOUSE_DRAGGED, 560, 340, 1, bar));

            assertEquals(100.0, stage.getX(), 0.01);
            assertEquals(100.0, stage.getY(), 0.01);
        });
    }

    @Test
    @DisplayName("未绑定 Stage → 拖动/双击均为无操作")
    void noStageMeansNoDrag() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title("t").build();

            bar.fireEvent(mouse(MouseEvent.MOUSE_PRESSED, 500, 300, 1, bar));
            bar.fireEvent(mouse(MouseEvent.MOUSE_DRAGGED, 560, 340, 1, bar));
            bar.fireEvent(mouse(MouseEvent.MOUSE_CLICKED, 500, 300, 2, bar));

            // 不该抛异常即可；再用一个绑定窗口确认「双击确实会最大化」，避免空过
            Stage stage = stageAt(100, 100);
            TitleBarAnt bound = TitleBarAnt.create().stage(stage).build();
            bound.fireEvent(mouse(MouseEvent.MOUSE_CLICKED, 500, 300, 2, bound));
            assertTrue(stage.isMaximized());
        });
    }

    @Test
    @DisplayName("按下点落在栏内按钮上时不拖动窗口")
    void pressingWindowButtonDoesNotDrag() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).build();
            Button close = (Button) bar.lookup("." + JfxStyles.WINDOW_CONTROL_CLOSE);
            assertNotNull(close);

            close.fireEvent(mouse(MouseEvent.MOUSE_PRESSED, 500, 300, 1, close));
            bar.fireEvent(mouse(MouseEvent.MOUSE_DRAGGED, 560, 340, 1, bar));

            assertEquals(100.0, stage.getX(), 0.01);
            assertEquals(100.0, stage.getY(), 0.01);
        });
    }

    @Test
    @DisplayName("left 插槽内的按钮不触发拖动（工具栏按钮可正常点击）")
    void pressingLeftSlotButtonDoesNotDrag() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            Button save = ButtonAnt.create("保存").build();
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).left(save).build();

            save.fireEvent(mouse(MouseEvent.MOUSE_PRESSED, 500, 300, 1, save));
            bar.fireEvent(mouse(MouseEvent.MOUSE_DRAGGED, 560, 340, 1, bar));

            assertEquals(100.0, stage.getX(), 0.01, "点工具栏按钮不该把窗口拖走");
            assertEquals(100.0, stage.getY(), 0.01);
        });
    }

    @Test
    @DisplayName("双击标题栏 → 最大化；再双击 → 还原")
    void doubleClickTogglesMaximize() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).build();

            bar.fireEvent(mouse(MouseEvent.MOUSE_CLICKED, 500, 300, 2, bar));
            assertTrue(stage.isMaximized());

            bar.fireEvent(mouse(MouseEvent.MOUSE_CLICKED, 500, 300, 2, bar));
            assertFalse(stage.isMaximized());
        });
    }

    @Test
    @DisplayName("不可缩放窗口双击不最大化")
    void doubleClickRespectsResizable() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            stage.setResizable(false);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).build();

            bar.fireEvent(mouse(MouseEvent.MOUSE_CLICKED, 500, 300, 2, bar));

            assertFalse(stage.isMaximized());
        });
    }

    @Test
    @DisplayName("stage() 同时接线窗口按钮（最大化按钮跟随窗口）")
    void stageWiresWindowControls() {
        runOnFxThreadAndWait(() -> {
            Stage stage = stageAt(100, 100);
            TitleBarAnt bar = TitleBarAnt.create().stage(stage).build();
            assertSame(stage, bar.controls().getTarget());

            stage.setMaximized(true);

            assertTrue(bar.controls().isMaximized());
        });
    }

    // ---------------------------------------------------------------
    // 3. 解析后的 CSS
    // ---------------------------------------------------------------

    @Test
    @DisplayName("外观：32px 高、与内容同色的底 + 1px 分隔线、窗口按钮右贴边、标题 12px")
    void resolvedAppearance() {
        runOnFxThreadAndWait(() -> {
            TitleBarAnt bar = TitleBarAnt.create().title("我的应用").build();
            Scene[] holder = new Scene[1];
            mount(holder, FLUENT, bar);
            holder[0].getRoot().layout();

            assertEquals(32.0, bar.getHeight(), 0.01, "标题栏高应为 @title-bar-height");

            Paint background = bar.getBackground() == null ? null
                    : bar.getBackground().getFills().get(0).getFill();
            assertNotNull(background, "标题栏应有背景色");
            assertColor(Color.web("#f3f3f3"), background,
                    "标题栏应与窗口内容同色（Fluent bg-layout）");

            assertNotNull(bar.getBorder(), "默认应有底部分隔线");
            assertEquals(1, bar.getBorder().getStrokes().size());
            assertColor(Color.web("#ebebeb"),
                    bar.getBorder().getStrokes().get(0).getBottomStroke(),
                    "底边线应为 border-muted");

            Label title = (Label) bar.lookup("." + JfxStyles.TITLE_BAR_TITLE);
            assertEquals(12.0, title.getFont().getSize(), 0.01,
                    "标题字号应为 @font-size-sm（WinUI caption 12px）");

            // 窗口按钮右贴边：右边界与标题栏右边界重合（Win11 无右内边距）
            Bounds controls = bar.controls().getBoundsInParent();
            assertEquals(bar.getWidth(), controls.getMaxX(), 0.5,
                    "窗口按钮组应贴右边缘，实为 " + controls + " vs 宽 " + bar.getWidth());
        });
    }
}
