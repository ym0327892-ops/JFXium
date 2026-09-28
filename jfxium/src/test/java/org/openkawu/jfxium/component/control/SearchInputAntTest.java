package org.openkawu.jfxium.component.control;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.token.Size;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchInputAnt 单元测试 —— 覆盖清除按钮显隐、清除语义、回车搜索回调、allowClear、
 * 双向绑定，以及 <b>布局宽度恒定</b> 契约。
 *
 * <p>宽度类断言必须真正跑到 CSS + layout：清除按钮若参与 HBox 布局，出现时会撑宽组件
 * （实测 +28px），空 ↔ 有内容切换即抖动。这是本组件最易回归的点，故需真实场景验证。</p>
 */
class SearchInputAntTest extends JfxTestBase {

    /** 把组件放进真实 Scene 并完成一次 CSS + layout —— 否则宽度全是 0，断言失去意义。 */
    private static Pane layoutInScene(SearchInputAnt search) {
        Pane root = new Pane(search);
        Scene scene = new Scene(root, 640, 200);
        scene.getStylesheets().add(SearchInputAntTest.class.getResource(
                "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        root.applyCss();
        root.layout();
        return root;
    }

    @Nested
    @DisplayName("清除按钮显隐")
    class VisibilityTests {

        @Test
        @DisplayName("空内容不显示，有内容显示")
        void followsContent() {
            runOnFxThreadAndWait(() -> {
                SearchInputAnt search = SearchInputAnt.create().build();
                assertFalse(search.isClearVisible(), "空内容不应显示清除按钮");

                search.text("Tabs");
                assertTrue(search.isClearVisible(), "有内容应显示清除按钮");

                search.clear();
                assertFalse(search.isClearVisible(), "清空后应隐藏清除按钮");
            });
        }

        @Test
        @DisplayName("allowClear(false) 按钮移出子树，且不再随内容出现")
        void allowClearFalseRemovesButton() {
            runOnFxThreadAndWait(() -> {
                SearchInputAnt search = SearchInputAnt.create().allowClear(false).build();
                assertFalse(search.getChildren().contains(search.clearButton()),
                        "allowClear(false) 应把按钮移出子树");

                search.text("Tabs");
                assertFalse(search.isClearVisible(), "allowClear(false) 时不应随内容出现");

                search.allowClear(true);
                assertTrue(search.getChildren().contains(search.clearButton()), "重新开启应挂回子树");
                assertTrue(search.isClearVisible(), "已有内容时重新开启应立刻可见");
            });
        }
    }

    @Nested
    @DisplayName("交互")
    class InteractionTests {

        @Test
        @DisplayName("回车回调拿到当前文本；onChange 每次文本变化触发一次")
        void searchAndChangeCallbacks() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<String> searched = new AtomicReference<>();
                AtomicInteger changes = new AtomicInteger();
                SearchInputAnt search = SearchInputAnt.create()
                        .onSearch(searched::set)
                        .onChange(t -> changes.incrementAndGet())
                        .build();

                search.text("Tabs");
                search.input().fireEvent(new javafx.event.ActionEvent());
                assertEquals("Tabs", searched.get(), "回车应把当前文本交给 onSearch");
                assertEquals(1, changes.get(), "一次赋值应只触发一次 onChange");
            });
        }

        @Test
        @DisplayName("禁用状态下 clear() 不生效")
        void clearIgnoredWhenDisabled() {
            runOnFxThreadAndWait(() -> {
                SearchInputAnt search = SearchInputAnt.create().text("Tabs").build();
                search.input().disabled(true);
                search.clear();
                assertEquals("Tabs", search.text(), "禁用时不应被清空");
            });
        }

        @Test
        @DisplayName("bindValue 双向绑定")
        void bindValue() {
            runOnFxThreadAndWait(() -> {
                javafx.beans.property.SimpleStringProperty prop =
                        new javafx.beans.property.SimpleStringProperty("init");
                SearchInputAnt search = SearchInputAnt.create().bindValue(prop).build();
                assertEquals("init", search.text());
                prop.set("outside");
                assertEquals("outside", search.text(), "外部写入应同步到输入框");
            });
        }
    }

    @Nested
    @DisplayName("布局宽度恒定（清除按钮必须悬浮，不参与父容器布局）")
    class LayoutTests {

        @Test
        @DisplayName("空 → 有内容 → 清除，组件宽度三态一致")
        void widthStableAcrossClearToggle() {
            runOnFxThreadAndWait(() -> {
                SearchInputAnt search = SearchInputAnt.create().placeholder("搜索组件…").build();
                Pane root = layoutInScene(search);

                double empty = search.getWidth();
                search.text("Tabs");
                root.applyCss(); root.layout();
                double filled = search.getWidth();
                search.clear();
                root.applyCss(); root.layout();
                double cleared = search.getWidth();

                assertTrue(empty > 0, "组件应已完成布局（宽度 > 0）");
                assertEquals(empty, filled, 0.5,
                        "出现清除按钮不应改变组件宽度（并排布局的典型症状是 +28px）");
                assertEquals(empty, cleared, 0.5, "清除后宽度应与初始一致");
            });
        }

        @Test
        @DisplayName("三种尺寸下宽度都不受清除按钮影响")
        void widthStableForAllSizes() {
            runOnFxThreadAndWait(() -> {
                for (Size size : new Size[]{Size.SMALL, Size.DEFAULT, Size.LARGE}) {
                    SearchInputAnt search = SearchInputAnt.create().size(size).build();
                    Pane root = layoutInScene(search);

                    double empty = search.getWidth();
                    search.text("compact");
                    root.applyCss(); root.layout();

                    assertEquals(empty, search.getWidth(), 0.5,
                            size + " 尺寸下出现清除按钮不应改变宽度");
                }
            });
        }

        @Test
        @DisplayName("输入框右侧恒定预留清除按钮空间，文字不钻到按钮底下")
        void reservedAffixSpace() {
            runOnFxThreadAndWait(() -> {
                SearchInputAnt search = SearchInputAnt.create().build();
                Pane root = layoutInScene(search);

                double emptyPadding = search.input().getInsets().getRight();
                assertTrue(emptyPadding > 0, "无清除按钮时也应预留右侧空间（否则文字会随按钮出现而跳动）");

                search.text("ant-design-input-allowClear");
                root.applyCss(); root.layout();

                assertEquals(emptyPadding, search.input().getInsets().getRight(), 0.5,
                        "有无清除按钮时右侧预留必须一致");

                // 按钮落在预留区内：getBoundsInParent() 已含 StackPane 的布局位移，
                // 不要再叠加 layoutX（会重复计数）。
                double buttonRight = search.clearButton().getBoundsInParent().getMaxX();
                assertTrue(buttonRight <= search.getWidth() + 0.5,
                        "清除按钮不应溢出组件右边界：buttonRight=" + buttonRight
                                + " containerWidth=" + search.getWidth());
            });
        }
    }
}
