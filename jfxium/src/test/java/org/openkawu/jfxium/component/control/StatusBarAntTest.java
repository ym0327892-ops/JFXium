package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StatusBarAnt 单元测试 —— 覆盖三栏布局、info/status/progress、action 按钮、
 * left/center/right 自定义节点、运行时 updateInfo/Progress/Status、disabled、链式。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 StatusBarAnt + 挂 jfx-status-bar / 三栏 styleClass</li>
 *   <li>create(info, status)：含 null 安全</li>
 *   <li>info / status / progress：创建 Label/ProgressBar 并更新</li>
 *   <li>progress 负数隐藏</li>
 *   <li>action：按钮追加 + onAction 触发 + null handler 安全</li>
 *   <li>left / center / right：自定义节点 + null 安全</li>
 *   <li>运行时 updateInfo / updateProgress / updateStatus</li>
 *   <li>disabled</li>
 *   <li>链式串联 + 继承式核心契约</li>
 * </ul>
 */
@DisplayName("StatusBarAnt")
class StatusBarAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 StatusBarAnt 且挂 jfx-status-bar")
    void build_returnsStatusBarAnt() {
        StatusBarAnt bar = StatusBarAnt.create().build();
        assertNotNull(bar);
        assertInstanceOf(HBox.class, bar);
        assertTrue(bar.getStyleClass().contains(JfxStyles.STATUS_BAR));
    }

    @Test
    @DisplayName("默认三栏（left/center/right）已挂 styleClass")
    void defaultThreeColumns() {
        StatusBarAnt bar = StatusBarAnt.create().build();
        // 三栏布局：leftBox + centerBox + rightBox
        assertEquals(3, bar.getChildren().size());
        Node left = bar.getChildren().get(0);
        Node center = bar.getChildren().get(1);
        Node right = bar.getChildren().get(2);
        assertInstanceOf(HBox.class, left);
        assertInstanceOf(HBox.class, center);
        assertInstanceOf(HBox.class, right);
        assertTrue(left.getStyleClass().contains(JfxStyles.STATUS_BAR_LEFT));
        assertTrue(center.getStyleClass().contains(JfxStyles.STATUS_BAR_CENTER));
        assertTrue(right.getStyleClass().contains(JfxStyles.STATUS_BAR_RIGHT));
    }

    // ============================================================
    // create(info, status)
    // ============================================================

    @Nested
    @DisplayName("create(info, status)")
    class CreateWithInit {

        @Test
        @DisplayName("create(info, status) 同时设置左右两栏 Label")
        void create_withInitValues() {
            StatusBarAnt bar = StatusBarAnt.create("就绪", "UTF-8").build();

            HBox left = (HBox) bar.getChildren().get(0);
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(1, left.getChildren().size());
            assertEquals(1, right.getChildren().size());
            assertInstanceOf(Label.class, left.getChildren().get(0));
            assertInstanceOf(Label.class, right.getChildren().get(0));
            assertEquals("就绪", ((Label) left.getChildren().get(0)).getText());
            assertEquals("UTF-8", ((Label) right.getChildren().get(0)).getText());
        }

        @Test
        @DisplayName("create(null, null) 不抛异常且栏内为空")
        void create_nullsSafe() {
            StatusBarAnt bar = assertDoesNotThrow(() ->
                    StatusBarAnt.create(null, null).build());
            assertNotNull(bar);
            HBox left = (HBox) bar.getChildren().get(0);
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(0, left.getChildren().size());
            assertEquals(0, right.getChildren().size());
        }
    }

    // ============================================================
    // info / status
    // ============================================================

    @Nested
    @DisplayName("info / status")
    class InfoStatus {

        @Test
        @DisplayName("info(text) 首次创建左侧 Label")
        void info_createsLabel() {
            StatusBarAnt bar = StatusBarAnt.create().info("就绪").build();
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(1, left.getChildren().size());
            assertInstanceOf(Label.class, left.getChildren().get(0));
            assertEquals("就绪", ((Label) left.getChildren().get(0)).getText());
        }

        @Test
        @DisplayName("info 多次调用复用同一个 Label")
        void info_updatesLabel() {
            StatusBarAnt bar = StatusBarAnt.create()
                    .info("首次")
                    .info("更新")
                    .build();
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(1, left.getChildren().size());
            assertEquals("更新", ((Label) left.getChildren().get(0)).getText());
        }

        @Test
        @DisplayName("info(null) 视为 \"\" 不抛异常")
        void info_nullSafe() {
            StatusBarAnt bar = assertDoesNotThrow(() ->
                    StatusBarAnt.create()
                            .info("原始")
                            .info(null)
                            .build());
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(1, left.getChildren().size());
            assertEquals("", ((Label) left.getChildren().get(0)).getText());
        }

        @Test
        @DisplayName("status(text) 首次创建右侧 Label")
        void status_createsLabel() {
            StatusBarAnt bar = StatusBarAnt.create().status("UTF-8").build();
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(1, right.getChildren().size());
            assertInstanceOf(Label.class, right.getChildren().get(0));
            assertEquals("UTF-8", ((Label) right.getChildren().get(0)).getText());
        }
    }

    // ============================================================
    // progress
    // ============================================================

    @Nested
    @DisplayName("progress")
    class ProgressTests {

        @Test
        @DisplayName("progress(0.5) 创建中间 ProgressBar")
        void progress_createsBar() {
            StatusBarAnt bar = StatusBarAnt.create().progress(0.5).build();
            HBox center = (HBox) bar.getChildren().get(1);
            assertEquals(1, center.getChildren().size());
            assertInstanceOf(ProgressBar.class, center.getChildren().get(0));
            assertEquals(0.5, ((ProgressBar) center.getChildren().get(0)).getProgress(), 0.0001);
        }

        @Test
        @DisplayName("progress(-1) 创建后立即隐藏（visible=false）")
        void progress_negativeHides() {
            StatusBarAnt bar = StatusBarAnt.create().progress(-1).build();
            HBox center = (HBox) bar.getChildren().get(1);
            ProgressBar pb = (ProgressBar) center.getChildren().get(0);
            assertFalse(pb.isVisible());
        }

        @Test
        @DisplayName("progress(0.5) → progress(-1) 后续隐藏")
        void progress_thenHide() {
            StatusBarAnt bar = StatusBarAnt.create()
                    .progress(0.5)
                    .progress(-1)
                    .build();
            HBox center = (HBox) bar.getChildren().get(1);
            ProgressBar pb = (ProgressBar) center.getChildren().get(0);
            assertEquals(0.5, pb.getProgress(), 0.0001);
            assertFalse(pb.isVisible());
        }
    }

    // ============================================================
    // action
    // ============================================================

    @Nested
    @DisplayName("action(text, onClick)")
    class ActionTests {

        @Test
        @DisplayName("action 追加按钮且 onAction 可触发")
        void action_registersHandler() {
            boolean[] fired = {false};
            StatusBarAnt bar = StatusBarAnt.create()
                    .action("UTF-8", () -> fired[0] = true)
                    .build();
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(1, right.getChildren().size());
            Button btn = (Button) right.getChildren().get(0);
            assertEquals("UTF-8", btn.getText());
            assertNotNull(btn.getOnAction());
            btn.getOnAction().handle(new ActionEvent());
            assertTrue(fired[0]);
        }

        @Test
        @DisplayName("action 的 Runnable 可为 null，且不会绑定空回调")
        void action_allowsNullRunnable() {
            StatusBarAnt bar = StatusBarAnt.create()
                    .action("UTF-8", null)
                    .build();

            Node rightBox = bar.getChildren().get(2);
            assertInstanceOf(HBox.class, rightBox);

            HBox right = (HBox) rightBox;
            assertEquals(1, right.getChildren().size());
            assertInstanceOf(Button.class, right.getChildren().get(0));
            Button btn = (Button) right.getChildren().get(0);
            assertNull(btn.getOnAction());
        }

        @Test
        @DisplayName("action 多次追加保持顺序")
        void action_multiple() {
            StatusBarAnt bar = StatusBarAnt.create()
                    .action("UTF-8", () -> {})
                    .action("LF", () -> {})
                    .action("Java", () -> {})
                    .build();
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(3, right.getChildren().size());
            assertEquals("UTF-8", ((Button) right.getChildren().get(0)).getText());
            assertEquals("LF", ((Button) right.getChildren().get(1)).getText());
            assertEquals("Java", ((Button) right.getChildren().get(2)).getText());
        }
    }

    // ============================================================
    // left / center / right 自定义节点
    // ============================================================

    @Nested
    @DisplayName("left / center / right(Node)")
    class CustomNode {

        @Test
        @DisplayName("left(Node) 添加到左栏")
        void left_addsToLeftBox() {
            Label custom = new Label("Git: main");
            StatusBarAnt bar = StatusBarAnt.create().left(custom).build();
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(1, left.getChildren().size());
            assertEquals(custom, left.getChildren().get(0));
        }

        @Test
        @DisplayName("left(null) 静默忽略")
        void left_nullIgnored() {
            StatusBarAnt bar = StatusBarAnt.create().left(null).build();
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(0, left.getChildren().size());
        }

        @Test
        @DisplayName("center(Node) 添加到中栏")
        void center_addsToCenterBox() {
            ProgressBar pb = new ProgressBar(0.3);
            StatusBarAnt bar = StatusBarAnt.create().center(pb).build();
            HBox center = (HBox) bar.getChildren().get(1);
            assertEquals(1, center.getChildren().size());
            assertEquals(pb, center.getChildren().get(0));
        }

        @Test
        @DisplayName("right(Node) 添加到右栏")
        void right_addsToRightBox() {
            Label custom = new Label("行 42, 列 15");
            StatusBarAnt bar = StatusBarAnt.create().right(custom).build();
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(1, right.getChildren().size());
            assertEquals(custom, right.getChildren().get(0));
        }

        @Test
        @DisplayName("right(null) 静默忽略")
        void right_nullIgnored() {
            StatusBarAnt bar = StatusBarAnt.create().right(null).build();
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(0, right.getChildren().size());
        }
    }

    // ============================================================
    // 运行时 updateInfo / updateProgress / updateStatus
    // ============================================================

    @Nested
    @DisplayName("运行时 updateInfo / updateProgress / updateStatus")
    class RuntimeUpdates {

        @Test
        @DisplayName("updateInfo(text) 首次创建 Label，后续复用更新")
        void updateInfo() {
            StatusBarAnt bar = StatusBarAnt.create().build();
            bar.updateInfo("初始");
            bar.updateInfo("更新");
            HBox left = (HBox) bar.getChildren().get(0);
            assertEquals(1, left.getChildren().size());
            assertEquals("更新", ((Label) left.getChildren().get(0)).getText());
        }

        @Test
        @DisplayName("updateProgress(0.5) 创建 ProgressBar，后续 updateProgress(0.8) 复用")
        void updateProgress() {
            StatusBarAnt bar = StatusBarAnt.create().build();
            bar.updateProgress(0.5);
            bar.updateProgress(0.8);
            HBox center = (HBox) bar.getChildren().get(1);
            assertEquals(1, center.getChildren().size());
            ProgressBar pb = (ProgressBar) center.getChildren().get(0);
            assertEquals(0.8, pb.getProgress(), 0.0001);
        }

        @Test
        @DisplayName("updateProgress 负数切换 visible=false")
        void updateProgress_negativeHides() {
            StatusBarAnt bar = StatusBarAnt.create().build();
            bar.updateProgress(0.5);
            bar.updateProgress(-1);
            HBox center = (HBox) bar.getChildren().get(1);
            ProgressBar pb = (ProgressBar) center.getChildren().get(0);
            assertFalse(pb.isVisible());
        }

        @Test
        @DisplayName("updateStatus(text) 首次创建 Label，后续复用更新")
        void updateStatus() {
            StatusBarAnt bar = StatusBarAnt.create().build();
            bar.updateStatus("初始");
            bar.updateStatus("更新");
            HBox right = (HBox) bar.getChildren().get(2);
            assertEquals(1, right.getChildren().size());
            assertEquals("更新", ((Label) right.getChildren().get(0)).getText());
        }
    }

    // ============================================================
    // disabled
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        StatusBarAnt bar = StatusBarAnt.create().disabled(true).build();
        assertTrue(bar.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        StatusBarAnt bar = StatusBarAnt.create().build();
        assertFalse(bar.isDisable());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain_noException() {
        Label customLeft = new Label("Git: main");
        Label customRight = new Label("行 42, 列 15");
        StatusBarAnt bar = assertDoesNotThrow(() ->
                StatusBarAnt.create()
                        .info("就绪")
                        .progress(0.45)
                        .status("UTF-8")
                        .action("UTF-8", () -> {})
                        .action("LF", () -> {})
                        .left(customLeft)
                        .center(null)
                        .right(customRight)
                        .styleClass("status-extra")
                        .disabled(false)
                        .build());
        assertNotNull(bar);
        assertTrue(bar.getStyleClass().contains(JfxStyles.STATUS_BAR));
        // 左栏有 info Label + customLeft = 2
        HBox left = (HBox) bar.getChildren().get(0);
        assertEquals(2, left.getChildren().size());
        // 中栏有 progressBar = 1
        HBox center = (HBox) bar.getChildren().get(1);
        assertEquals(1, center.getChildren().size());
        // 右栏有 status Label + 2 action 按钮 + customRight = 4
        HBox right = (HBox) bar.getChildren().get(2);
        assertEquals(4, right.getChildren().size());
    }

    @Test
    @DisplayName("build() 返回自身（assertSame）")
    void build_returnsSelf() {
        StatusBarAnt bar = StatusBarAnt.create()
                .info("测试")
                .build();
        assertSame(bar, bar.build());
    }

    @Test
    @DisplayName("多态：StatusBarAnt 即是 HBox，可直接 add 到容器")
    void polymorphic_isHBox() {
        StatusBarAnt bar = StatusBarAnt.create().build();
        assertInstanceOf(HBox.class, bar);
        assertInstanceOf(StatusBarAnt.class, bar);
    }
}
