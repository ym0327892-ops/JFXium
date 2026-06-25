package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChoiceBoxAnt 单元测试 —— 覆盖 items、value、size、disabled、onSelect、bindValue 双向绑定和链式。
 *
 * <p>与 {@link ComboBoxAntTest} 镜像：两者都是继承式 control + 双向 bindValue。
 * ChoiceBox 额外覆盖 build() 返回 self（不调 build 也能直接用）。</p>
 */
@DisplayName("ChoiceBoxAnt")
class ChoiceBoxAntTest extends JfxTestBase {

    // ============================================================
    // 基本结构
    // ============================================================

    @Test
    @DisplayName("create() 挂默认 styleClass 且无可选值")
    void create_defaults() {
        ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create().build();
        assertTrue(cb.getStyleClass().contains(JfxStyles.JFX_CHOICE_BOX));
        assertNull(cb.getValue());
        assertEquals(0, cb.getItems().size());
    }

    @Test
    @DisplayName("build() 返回自身（继承式 control 双工厂模式）")
    void build_returnsThis() {
        ChoiceBoxAnt<String> cb = ChoiceBoxAnt.create();
        assertSame(cb, cb.build());
    }

    @Test
    @DisplayName("不调 build() 也可直接用（this 即是控件）")
    void usableWithoutBuild() {
        ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create();
        cb.items("A", "B", "C");
        // 不调 build()，cb 本身已是 ChoiceBox，items 已生效
        assertEquals(3, cb.getItems().size());
    }

    // ============================================================
    // items
    // ============================================================

    @Nested
    @DisplayName("items")
    class Items {

        @Test
        @DisplayName("items(varargs) 设置选项")
        void varargs() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B", "C").build();
            assertEquals(3, cb.getItems().size());
            assertEquals("A", cb.getItems().get(0));
            assertEquals("C", cb.getItems().get(2));
        }

        @Test
        @DisplayName("items(ObservableList) 设置选项")
        void observableList() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items(FXCollections.observableArrayList("X", "Y", "Z"))
                    .build();
            assertEquals(3, cb.getItems().size());
            assertEquals("Z", cb.getItems().get(2));
        }

        @Test
        @DisplayName("items(0 个) 不抛异常")
        void empty() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items().build();
            assertEquals(0, cb.getItems().size());
        }

        @Test
        @DisplayName("items 后再 value 设置选中")
        void itemsThenValue() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("light", "dark", "compact")
                    .value("dark")
                    .build();
            assertEquals("dark", cb.getValue());
        }
    }

    // ============================================================
    // value
    // ============================================================

    @Nested
    @DisplayName("value")
    class Value {

        @Test
        @DisplayName("value 设置当前选中值")
        void sets() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B", "C")
                    .value("B").build();
            assertEquals("B", cb.getValue());
        }

        @Test
        @DisplayName("value(null) 清空选中")
        void nullClears() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B")
                    .value("A")
                    .value(null)
                    .build();
            assertNull(cb.getValue());
        }
    }

    // ============================================================
    // size
    // ============================================================

    @Nested
    @DisplayName("size")
    class SizeTests {

        @Test
        @DisplayName("size(SMALL) 挂 SIZE_SMALL")
        void small() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .size(Size.SMALL).build();
            assertTrue(cb.getStyleClass().contains(JfxStyles.SIZE_SMALL));
        }

        @Test
        @DisplayName("size(LARGE) 挂 SIZE_LARGE")
        void large() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .size(Size.LARGE).build();
            assertTrue(cb.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }

        @Test
        @DisplayName("size(DEFAULT) 不挂额外 size 类")
        void default_() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .size(Size.DEFAULT).build();
            assertFalse(cb.getStyleClass().contains(JfxStyles.SIZE_SMALL));
            assertFalse(cb.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }

        @Test
        @DisplayName("size 切换幂等：重复 small/large 不重复挂")
        void idempotent() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .size(Size.SMALL)
                    .size(Size.LARGE)
                    .build();
            long largeCount = cb.getStyleClass().stream()
                    .filter(JfxStyles.SIZE_LARGE::equals).count();
            long smallCount = cb.getStyleClass().stream()
                    .filter(JfxStyles.SIZE_SMALL::equals).count();
            assertEquals(1, largeCount);
            assertEquals(0, smallCount);
        }
    }

    // ============================================================
    // disabled
    // ============================================================

    @Nested
    @DisplayName("disabled")
    class Disabled {

        @Test
        @DisplayName("disabled(true) 禁用")
        void true_() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .disabled(true).build();
            assertTrue(cb.isDisable());
        }

        @Test
        @DisplayName("disabled(false) 不禁用")
        void false_() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .disabled(false).build();
            assertFalse(cb.isDisable());
        }
    }

    // ============================================================
    // onSelect
    // ============================================================

    @Nested
    @DisplayName("onSelect")
    class OnSelect {

        @Test
        @DisplayName("onSelect 监听选中变化")
        void listens() {
            AtomicReference<String> captured = new AtomicReference<>();
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B", "C")
                    .onSelect(captured::set)
                    .build();
            cb.setValue("B");
            assertEquals("B", captured.get());
        }

        @Test
        @DisplayName("onSelect(null) 安全（不抛异常）")
        void nullSafe() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A")
                    .onSelect(null)
                    .build();
            assertNotNull(cb);
        }
    }

    // ============================================================
    // bindValue
    // ============================================================

    @Nested
    @DisplayName("bindValue")
    class BindValue {

        @Test
        @DisplayName("bindValue 双向绑定：控件→Property")
        void controlToProperty() {
            ObjectProperty<String> prop = new SimpleObjectProperty<>("");
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B", "C")
                    .bindValue(prop).build();
            cb.setValue("B");
            assertEquals("B", prop.get());
        }

        @Test
        @DisplayName("bindValue 双向绑定：Property→控件")
        void propertyToControl() {
            ObjectProperty<String> prop = new SimpleObjectProperty<>("");
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("A", "B", "C")
                    .bindValue(prop).build();
            prop.set("C");
            assertEquals("C", cb.getValue());
        }

        @Test
        @DisplayName("bindValue 初始同步：Property 初值 → 控件 value")
        void initialSync() {
            ObjectProperty<String> prop = new SimpleObjectProperty<>("init");
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("init", "A", "B")
                    .bindValue(prop)
                    .build();
            assertEquals("init", cb.getValue());
        }

        @Test
        @DisplayName("bindValue(null) 不抛异常")
        void nullSafe() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .bindValue((Property<String>) null).build();
            assertNotNull(cb);
            // 仍可正常使用
            cb.items("A", "B");
            cb.setValue("A");
            assertEquals("A", cb.getValue());
        }

        @Test
        @DisplayName("bindValue 多次 set 同步正确")
        void multipleChanges() {
            ObjectProperty<String> prop = new SimpleObjectProperty<>("");
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("X", "Y", "Z")
                    .bindValue(prop).build();
            prop.set("X");
            assertEquals("X", cb.getValue());
            cb.setValue("Y");
            assertEquals("Y", prop.get());
            prop.set("Z");
            assertEquals("Z", cb.getValue());
            assertEquals("Z", prop.get());
        }
    }

    // ============================================================
    // 全链式
    // ============================================================

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        ObjectProperty<String> prop = new SimpleObjectProperty<>("light");
        ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                .items("light", "dark", "compact")
                .value("dark")
                .size(Size.LARGE)
                .disabled(false)
                .onSelect(v -> {})
                .bindValue(prop)
                .build();
        // bindBidirectional: 初始同步后 prop → cb
        assertEquals("light", cb.getValue());
        assertEquals("light", prop.get());
        assertNotNull(cb);
        assertTrue(cb.getStyleClass().contains(JfxStyles.JFX_CHOICE_BOX));
        assertTrue(cb.getStyleClass().contains(JfxStyles.SIZE_LARGE));
    }
}
