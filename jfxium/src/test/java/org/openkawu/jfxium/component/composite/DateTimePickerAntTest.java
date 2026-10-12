package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateTimePickerAnt 单元测试 —— 日期 + 时间组合面板的核心契约。
 *
 * <p>覆盖：模式串校验（必须含日期字段）、值往返、Controller 构建后改值、
 * bindValue 双向绑定、清除语义、面板在弹层中真实渲染（Popup 独立 Scene 也需拿到样式）。</p>
 */
class DateTimePickerAntTest extends JfxTestBase {

    @Nested
    @DisplayName("格式串校验")
    class FormatTests {

        @Test
        @DisplayName("只有时间字段（无 y/M/d）直接报错，提示改用 TimePickerAnt")
        void rejectsTimeOnlyPattern() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> DateTimePickerAnt.create().format("HH:mm:ss").build());
            assertTrue(ex.getMessage().contains("TimePickerAnt"),
                    "报错信息应指路到 TimePickerAnt，实际： " + ex.getMessage());
        }

        @Test
        @DisplayName("null / 空串 / 空白串回落到默认格式")
        void defaultFallback() {
            assertEquals(DateTimePickerAnt.DEFAULT_FORMAT,
                    DateTimePickerAnt.normalizePattern(null));
            assertEquals(DateTimePickerAnt.DEFAULT_FORMAT,
                    DateTimePickerAnt.normalizePattern(""));
            assertEquals(DateTimePickerAnt.DEFAULT_FORMAT,
                    DateTimePickerAnt.normalizePattern("   "));
        }

        @Test
        @DisplayName("12 小时制符号 h 规范化为 H")
        void twelveHourSymbolNormalized() {
            assertEquals("yyyy-MM-dd HH:mm", DateTimePickerAnt.normalizePattern("yyyy-MM-dd hh:mm"));
        }
    }

    @Nested
    @DisplayName("值与交互")
    class ValueTests {

        private StackPane build(ObjectProperty<LocalDateTime> prop,
                                AtomicReference<LocalDateTime> captured) {
            DateTimePickerAnt.Builder b = DateTimePickerAnt.create()
                    .format("yyyy-MM-dd HH:mm")
                    .value(LocalDateTime.of(2026, 10, 11, 9, 30));
            if (prop != null) {
                b.bindValue(prop);
            }
            if (captured != null) {
                b.onChange(captured::set);
            }
            return b.build();
        }

        @Test
        @DisplayName("触发器显示格式化文本，Controller 回读初始值")
        void initialValue() {
            runOnFxThreadAndWait(() -> {
                StackPane node = build(null, null);
                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                assertNotNull(ctrl);
                assertEquals(LocalDateTime.of(2026, 10, 11, 9, 30), ctrl.getValue());
                javafx.scene.Node field = node.lookup("." + JfxStyles.TIME_PICKER_FIELD);
                assertNotNull(field);
                assertEquals("2026-10-11 09:30",
                        ((javafx.scene.control.TextField) field).getText());
            });
        }

        @Test
        @DisplayName("setValue 更新值且不触发 onChange")
        void setValueSilent() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                StackPane node = build(null, captured);
                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                ctrl.setValue(LocalDateTime.of(2026, 12, 25, 18, 0));
                assertEquals(LocalDateTime.of(2026, 12, 25, 18, 0), ctrl.getValue());
                assertNull(captured.get(), "程序化 setValue 不应触发 onChange");
            });
        }

        @Test
        @DisplayName("清除按钮把值置空并回调 null")
        void clearSemantics() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured =
                        new AtomicReference<>(LocalDateTime.of(2026, 10, 11, 9, 30));
                StackPane node = build(null, captured);
                javafx.scene.Node clear = node.lookup("." + JfxStyles.TIME_PICKER_CLEAR);
                assertNotNull(clear);
                clear.getOnMouseClicked().handle(new javafx.scene.input.MouseEvent(
                        javafx.scene.input.MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                        javafx.scene.input.MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, false, false, false, null));
                assertNull(captured.get());
                assertNull(DateTimePickerAnt.controllerOf(node).getValue());
            });
        }

        @Test
        @DisplayName("bindValue 初始取自 property，外部变更同步回控件")
        void bindValueRoundTrip() {
            runOnFxThreadAndWait(() -> {
                ObjectProperty<LocalDateTime> prop = new SimpleObjectProperty<>(
                        LocalDateTime.of(2026, 1, 2, 3, 4));
                StackPane node = build(prop, null);
                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                assertEquals(LocalDateTime.of(2026, 1, 2, 3, 4), ctrl.getValue(),
                        "初始值取自 property（property 优先于 value）");
                prop.set(LocalDateTime.of(2026, 6, 7, 8, 9));
                assertEquals(LocalDateTime.of(2026, 6, 7, 8, 9), ctrl.getValue(),
                        "外部 property 变更应同步到控件（与 TimePickerAnt 同一语义）");
            });
        }
    }

    @Nested
    @DisplayName("渲染与布局契约")
    class LayoutTests {

        @Test
        @DisplayName("触发器宽度恒定：空 ↔ 有值不抖动（叠加层方案）")
        void widthStable() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build();
                Pane root = new Pane(node);
                Scene scene = new Scene(root, 720, 200);
                scene.getStylesheets().add(DateTimePickerAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();
                double empty = node.getLayoutBounds().getWidth();
                assertTrue(empty > 0);

                DateTimePickerAnt.controllerOf(node)
                        .setValue(LocalDateTime.of(2026, 12, 31, 23, 59));
                node.applyCss();
                node.layout();
                assertEquals(empty, node.getLayoutBounds().getWidth(), 0.5,
                        "空 ↔ 有值切换不应改变宽度");
            });
        }

        @Test
        @DisplayName("面板可在真实 Scene 中打开（Popup 独立 Scene 亦继承样式表）")
        void panelOpens() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build();
                Pane root = new Pane(node);
                Scene scene = new Scene(root, 720, 200);
                scene.getStylesheets().add(DateTimePickerAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                ctrl.showPanel();
                assertTrue(ctrl.isPanelShowing(), "面板应处于显示状态");
                // 面板内容（左侧日历 + 右侧时间面板）都应挂上样式
                assertNotNull(ctrl.getTimePanelNode());
                assertTrue(ctrl.getTimePanelNode().getStyleClass().contains(JfxStyles.TIME_PANEL));
                ctrl.hidePanel();
                assertFalse(ctrl.isPanelShowing());
            });
        }

        @Test
        @DisplayName("去双框：内层时间面板自身不画边框/阴影，只由外壳画一套")
        void embeddedPanelDrawsNoOwnFrame() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build();
                Pane root = new Pane(node);
                Scene scene = new Scene(root, 720, 200);
                scene.getStylesheets().add(DateTimePickerAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                ctrl.showPanel();
                javafx.scene.Node embedded = findEmbedded(ctrl);
                assertNotNull(embedded, "内层时间面板应带 " + JfxStyles.TIME_PANEL_EMBEDDED + " 类");

                javafx.scene.layout.Region r = (javafx.scene.layout.Region) embedded;
                assertNull(r.getEffect(), "嵌入模式不应保留阴影，否则与外壳叠成双框");
                if (r.getBorder() != null) {
                    for (javafx.scene.layout.BorderStroke s : r.getBorder().getStrokes()) {
                        assertEquals(javafx.scene.paint.Color.TRANSPARENT, s.getTopStroke(),
                                "嵌入模式四条边都不应着色（分隔线由 body 的左边框给出）");
                    }
                }
                ctrl.hidePanel();
            });
        }

        /**
         * 在弹层内容里定位带 {@code jfx-time-panel-embedded} 的节点。
         *
         * <p>注意不能按 {@code .jfx-time-panel} 定位：外壳同时带
         * {@code jfx-time-panel} 与 {@code jfx-date-time-picker-shell} 两个类，
         * 单类名 {@code lookup} 会命中外壳本身。</p>
         */
        private javafx.scene.Node findEmbedded(DateTimePickerAnt.Controller ctrl) {
            javafx.scene.Node timePanel = ctrl.getTimePanelNode();
            for (javafx.stage.Window win : javafx.stage.Window.getWindows()) {
                if (win instanceof javafx.stage.Popup p && p.isShowing()) {
                    javafx.scene.Parent shell = (javafx.scene.Parent) p.getContent().get(0);
                    shell.applyCss();
                    shell.layout();
                    return shell.lookup("." + JfxStyles.TIME_PANEL_EMBEDDED);
                }
            }
            return null;
        }

        @Test
        @DisplayName("两侧表头带同高：星期表头 与 段标题带 必须承接同一条行线（含紧凑主题）")
        void headerBandsShareOneHeight() {
            for (String css : new String[]{"theme-light.css", "theme-light-compact.css"}) {
                runOnFxThreadAndWait(() -> {
                    StackPane node = DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build();
                    Pane root = new Pane(node);
                    Scene scene = new Scene(root, 720, 200);
                    scene.getStylesheets().add(DateTimePickerAntTest.class.getResource(
                            "/org/openkawu/jfxium/css/" + css).toExternalForm());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();

                    DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                    ctrl.showPanel();
                    javafx.scene.Parent shell = null;
                    for (javafx.stage.Window win : javafx.stage.Window.getWindows()) {
                        if (win instanceof javafx.stage.Popup p && p.isShowing()) {
                            shell = (javafx.scene.Parent) p.getContent().get(0);
                        }
                    }
                    assertNotNull(shell, "面板应已显示");
                    shell.applyCss();
                    shell.layout();

                    // 星期表头取自内嵌的原生面板（.day-name-cell），不再是自绘的 .jfx-calendar-day-header
                    javafx.scene.Node weekday = shell.lookup(".day-name-cell");
                    javafx.scene.Node unitHeader = shell.lookup("." + JfxStyles.TIME_PANEL_UNIT_HEADER);
                    assertNotNull(unitHeader, "应有段标题带");

                    double a = weekday.getLayoutBounds().getHeight();
                    double b = unitHeader.getLayoutBounds().getHeight();
                    // 必须逐主题校验：默认密度下两者恰好都是 40，只有紧凑主题才会暴露
                    // 「段标题带随 token 收紧、星期表头被 Java 硬编码钉住」造成的错位。
                    assertEquals(a, b, 0.5,
                            css + " 下两侧表头带必须同高，否则日期区与时间区从第一条行线起就错位"
                                    + "（星期表头 " + a + " / 段标题带 " + b + "）");
                    ctrl.hidePanel();
                });
            }
        }
    }
}
