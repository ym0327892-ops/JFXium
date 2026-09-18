package org.openkawu.jfxium.component.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ListViewAnt 单元测试 —— 覆盖工厂创建 / items / selectionMode / onSelect / onDoubleClick /
 * placeholder / fixedCellSize / editable / disabled / 链式串联 / 继承式契约。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create() / 构造 ObservableList / 默认 styleClass</li>
 *   <li>items：可变参数 / ObservableList / 泛型</li>
 *   <li>placeholder：Node 占位符</li>
 *   <li>selectionMode：SINGLE / MULTIPLE / null 防护</li>
 *   <li>onSelect：handler 触发 / null 不挂监听</li>
 *   <li>onDoubleClick：handler 不为 null 时 install</li>
 *   <li>fixedCellSize / editable / disabled</li>
 *   <li>链式串联 + 继承式核心契约（build() 返回 this + 多态兼容 + 泛型）</li>
 * </ul>
 */
@DisplayName("ListViewAnt")
class ListViewAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create() 空 items + 挂 jfx-list-view")
    void create_default() {
        ListViewAnt<String> list = ListViewAnt.<String>create().build();
        assertNotNull(list);
        assertTrue(list.getStyleClass().contains(JfxStyles.JFX_LIST_VIEW));
        assertNotNull(list.getItems());
        assertTrue(list.getItems().isEmpty());
    }

    @Test
    @DisplayName("构造 ListViewAnt(ObservableList) 接受预填充列表")
    void constructor_withObservableList() {
        ObservableList<String> items = FXCollections.observableArrayList("a", "b", "c");
        ListViewAnt<String> list = new ListViewAnt<>(items);
        assertEquals(3, list.getItems().size());
        assertEquals("a", list.getItems().get(0));
        assertTrue(list.getStyleClass().contains(JfxStyles.JFX_LIST_VIEW));
    }

    @Test
    @DisplayName("构造 ListViewAnt(null) 仍挂样式不抛 NPE（JavaFX ListView 自身接受 null）")
    void constructor_nullObservableList() {
        // JavaFX ListView(null) 是合法的：表示无 items
        assertDoesNotThrow(() -> {
            ListViewAnt<String> list = new ListViewAnt<>((ObservableList<String>) null);
            assertTrue(list.getStyleClass().contains(JfxStyles.JFX_LIST_VIEW));
        });
    }

    // ============================================================
    // items
    // ============================================================

    @Test
    @DisplayName("items(T...) 可变参数设数据")
    void items_varargs() {
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .items("x", "y", "z")
                .build();
        assertEquals(3, list.getItems().size());
        assertEquals("x", list.getItems().get(0));
        assertEquals("z", list.getItems().get(2));
    }

    @Test
    @DisplayName("items(ObservableList) 重设数据")
    void items_observableList() {
        ObservableList<Integer> nums = FXCollections.observableArrayList(1, 2, 3, 4);
        ListViewAnt<Integer> list = ListViewAnt.<Integer>create()
                .items(nums)
                .build();
        assertEquals(4, list.getItems().size());
        assertEquals(Integer.valueOf(4), list.getItems().get(3));
    }

    @Test
    @DisplayName("items() 多次调用后覆盖生效")
    void items_overrides() {
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .items("first")
                .items("second", "third")
                .build();
        assertEquals(2, list.getItems().size());
        assertEquals("second", list.getItems().get(0));
    }

    @Test
    @DisplayName("items() 空数组 → 空列表")
    void items_empty() {
        ListViewAnt<String> list = ListViewAnt.<String>create().items().build();
        assertTrue(list.getItems().isEmpty());
    }

    // ============================================================
    // placeholder
    // ============================================================

    @Test
    @DisplayName("placeholder(Node) 设置占位符")
    void placeholder_setNode() {
        Node ph = LabelAnt.create("暂无数据").build();
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .placeholder(ph)
                .build();
        assertSame(ph, list.getPlaceholder());
    }

    @Test
    @DisplayName("placeholder(null) 允许（清空占位符）")
    void placeholder_null() {
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .placeholder(null)
                .build();
        assertNull(list.getPlaceholder());
    }

    // ============================================================
    // selectionMode
    // ============================================================

    @Nested
    @DisplayName("selectionMode —— SINGLE / MULTIPLE")
    class SelectionModeTest {

        @Test
        @DisplayName("selectionMode(SINGLE) 设单选")
        void single() {
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .selectionMode(SelectionMode.SINGLE)
                    .build();
            assertEquals(SelectionMode.SINGLE, list.getSelectionModel().getSelectionMode());
        }

        @Test
        @DisplayName("selectionMode(MULTIPLE) 设多选")
        void multiple() {
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .selectionMode(SelectionMode.MULTIPLE)
                    .build();
            assertEquals(SelectionMode.MULTIPLE, list.getSelectionModel().getSelectionMode());
        }

        @Test
        @DisplayName("selectionMode(null) 不抛 NPE（JavaFX 内部应处理）")
        void null_safe() {
            // 文档化的防御编程：null 入参应被钳制到默认（避免 NPE 穿透到 switch）
            // 这里不强制要求 —— 只断言不崩
            assertDoesNotThrow(() -> {
                ListViewAnt.<String>create().selectionMode(null).build();
            });
        }
    }

    // ============================================================
    // onSelect
    // ============================================================

    @Nested
    @DisplayName("onSelect —— 选中项变更回调")
    class OnSelect {

        @Test
        @DisplayName("onSelect(handler) 选中变更时回调拿到新值")
        void onSelect_callbackFires() {
            AtomicReference<String> captured = new AtomicReference<>();
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .items("a", "b", "c")
                    .onSelect(captured::set)
                    .build();

            list.getSelectionModel().select(1);
            assertEquals("b", captured.get());
        }

        @Test
        @DisplayName("onSelect 多次选中按序触发")
        void onSelect_multipleEvents() {
            AtomicReference<String> log = new AtomicReference<>("");
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .items("a", "b", "c")
                    .onSelect(v -> log.set(log.get() + v + ","))
                    .build();

            list.getSelectionModel().select(0);
            list.getSelectionModel().select(2);
            list.getSelectionModel().select(1);
            assertEquals("a,c,b,", log.get());
        }

        @Test
        @DisplayName("onSelect(null) 不抛 NPE + 不挂监听")
        void onSelect_null_noListener() {
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .items("a")
                    .onSelect(null)
                    .build();
            assertDoesNotThrow(() -> list.getSelectionModel().select(0));
        }
    }

    // ============================================================
    // onDoubleClick
    // ============================================================

    @Test
    @DisplayName("onDoubleClick(handler) install 不抛异常")
    void onDoubleClick_installsHandler() {
        AtomicInteger clickCount = new AtomicInteger(0);
        // 双击模拟需要完整的 Scene + 鼠标事件，单元测试仅断言 install 不崩
        // + handler 字段被赋值（通过反射或行为验证：未选中时不抛）
        assertDoesNotThrow(() -> {
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .items("x")
                    .onDoubleClick(v -> clickCount.incrementAndGet())
                    .build();
            // 未选中时调用不抛
            list.getOnMouseClicked();
        });
        // clickCount 不变（因为没真正触发双击），这里仅断言不崩
        assertEquals(0, clickCount.get());
    }

    @Test
    @DisplayName("onDoubleClick(null) 不抛 NPE（handler==null 时不调用）")
    void onDoubleClick_null() {
        assertDoesNotThrow(() -> {
            ListViewAnt<String> list = ListViewAnt.<String>create()
                    .items("y")
                    .onDoubleClick(null)
                    .build();
            list.getOnMouseClicked();
        });
    }

    // ============================================================
    // fixedCellSize / editable / disabled
    // ============================================================

    @Test
    @DisplayName("fixedCellSize(24.0) 设固定高度")
    void fixedCellSize_set() {
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .fixedCellSize(24.0)
                .build();
        assertEquals(24.0, list.getFixedCellSize());
    }

    @Test
    @DisplayName("editable(true) 设可编辑")
    void editable_true() {
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .editable(true)
                .build();
        assertTrue(list.isEditable());
    }

    @Test
    @DisplayName("editable 默认 false")
    void editable_default() {
        ListViewAnt<String> list = ListViewAnt.<String>create().build();
        assertFalse(list.isEditable());
    }

    @Test
    @DisplayName("disabled(true) 设 isDisable")
    void disabled_true() {
        ListViewAnt<String> list = ListViewAnt.<String>create().disabled(true).build();
        assertTrue(list.isDisable());
    }

    @Test
    @DisplayName("disabled 默认 false")
    void disabled_default() {
        ListViewAnt<String> list = ListViewAnt.<String>create().build();
        assertFalse(list.isDisable());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式：create → items → selectionMode → fixedCellSize → editable → disabled 全部生效")
    void fullChain_noException() {
        AtomicReference<String> captured = new AtomicReference<>();
        ListViewAnt<String> list = ListViewAnt.<String>create()
                .items("a", "b", "c")
                .selectionMode(SelectionMode.SINGLE)
                .fixedCellSize(28.0)
                .onSelect(captured::set)
                .editable(true)
                .disabled(false)
                .build();

        assertEquals(3, list.getItems().size());
        assertEquals(SelectionMode.SINGLE, list.getSelectionModel().getSelectionMode());
        assertEquals(28.0, list.getFixedCellSize());
        assertTrue(list.isEditable());
        assertFalse(list.isDisable());

        list.getSelectionModel().select(2);
        assertEquals("c", captured.get());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        ListViewAnt<String> list = ListViewAnt.<String>create();
        assertSame(list, list.build());
    }

    @Test
    @DisplayName("继承式：父类 ListView 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        ListView<String> lv = ListViewAnt.<String>create()
                .items("a", "b")
                .build();
        assertInstanceOf(ListViewAnt.class, lv);
        assertEquals(2, lv.getItems().size());
        assertTrue(lv.getStyleClass().contains(JfxStyles.JFX_LIST_VIEW));
    }

    @Test
    @DisplayName("泛型兼容：Integer 类型正常工作")
    void generic_integer() {
        ListViewAnt<Integer> list = ListViewAnt.<Integer>create()
                .items(1, 2, 3, 4, 5)
                .build();
        assertEquals(5, list.getItems().size());
        assertEquals(Integer.valueOf(3), list.getItems().get(2));
    }
}