package org.openkawu.jfxium.component.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InputAnt 单元测试 —— 覆盖文本、占位、尺寸、禁用、只读和 bindValue 双向绑定。
 */
@DisplayName("InputAnt")
class InputAntTest extends JfxTestBase {

    // ---------- 工厂 ----------

    @Test
    @DisplayName("create() 返回非空 TextField")
    void create_returnsNonNull() {
        InputAnt input = InputAnt.create().build();
        assertNotNull(input);
        assertEquals("", input.getText());
    }

    @Test
    @DisplayName("create(text) 设置初始文本")
    void createWithText() {
        InputAnt input = InputAnt.create("hello").build();
        assertEquals("hello", input.getText());
    }

    @Test
    @DisplayName("build() 返回自身")
    void build_returnsThis() {
        InputAnt input = InputAnt.create();
        assertSame(input, input.build());
    }

    // ---------- text / placeholder ----------

    @Test
    @DisplayName("text(null) 设为空字符串")
    void text_nullWritesEmpty() {
        InputAnt input = InputAnt.create("old").text(null).build();
        assertEquals("", input.getText());
    }

    @Test
    @DisplayName("placeholder 设置 promptText")
    void placeholder() {
        InputAnt input = InputAnt.create().placeholder("请输入").build();
        assertEquals("请输入", input.getPromptText());
    }

    @Test
    @DisplayName("placeholder(null) 清空")
    void placeholder_null() {
        InputAnt input = InputAnt.create().placeholder("请输入")
                .placeholder(null).build();
        assertEquals("", input.getPromptText());
    }

    // ---------- size ----------

    @Test
    @DisplayName("size(SMALL) 挂 SIZE_SMALL")
    void size_small() {
        InputAnt input = InputAnt.create().size(InputAnt.Size.SMALL).build();
        assertTrue(input.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    @Test
    @DisplayName("size 幂等")
    void size_idempotent() {
        InputAnt input = InputAnt.create()
                .size(InputAnt.Size.SMALL)
                .size(InputAnt.Size.LARGE)
                .build();
        assertFalse(input.getStyleClass().contains(JfxStyles.SIZE_SMALL));
        assertTrue(input.getStyleClass().contains(JfxStyles.SIZE_LARGE));
    }

    // ---------- 状态 ----------

    @Test
    @DisplayName("disabled(true) 禁用输入框")
    void disabled_true() {
        InputAnt input = InputAnt.create().disabled(true).build();
        assertTrue(input.isDisable());
    }

    @Test
    @DisplayName("readOnly(true) 不可编辑但可选")
    void readOnly_true() {
        InputAnt input = InputAnt.create().readOnly(true).build();
        assertFalse(input.isEditable());
    }

    @Test
    @DisplayName("readOnly(false) 恢复可编辑")
    void readOnly_false() {
        InputAnt input = InputAnt.create().readOnly(true)
                .readOnly(false).build();
        assertTrue(input.isEditable());
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 双向绑定：控件→Property")
    void bindValue_controlToProperty() {
        StringProperty prop = new SimpleStringProperty("");
        InputAnt input = InputAnt.create().bindValue(prop).build();
        input.setText("新值");
        assertEquals("新值", prop.get());
    }

    @Test
    @DisplayName("bindValue 双向绑定：Property→控件")
    void bindValue_propertyToControl() {
        StringProperty prop = new SimpleStringProperty("");
        InputAnt input = InputAnt.create().bindValue(prop).build();
        prop.set("外部设置");
        assertEquals("外部设置", input.getText());
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null_noError() {
        InputAnt input = InputAnt.create().bindValue(null).build();
        assertNotNull(input);
    }

    // ---------- styleClass ----------

    @Test
    @DisplayName("styleClass 幂等追加")
    void styleClass_idempotent() {
        InputAnt input = InputAnt.create().styleClass("x").styleClass("x").build();
        long count = input.getStyleClass().stream().filter("x"::equals).count();
        assertEquals(1, count);
    }

    // ---------- 链式调用 ----------

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain() {
        StringProperty prop = new SimpleStringProperty("initial");
        InputAnt input = InputAnt.create("默认值")
                .placeholder("请输入")
                .size(InputAnt.Size.LARGE)
                .disabled(false)
                .readOnly(false)
                .bindValue(prop)
                .styleClass("custom-input")
                .build();
        assertNotNull(input);
        // bindBidirectional: 初始化时 prop 的值会同步到控件
        assertEquals("initial", input.getText());
        assertEquals("initial", prop.get());
    }

    // ---------- 业务继承 ----------

    @Test
    @DisplayName("业务继承：子类构建后链式 API 仍可用")
    void subclassInheritance() {
        class SearchInput extends InputAnt {
            SearchInput() {
                placeholder("搜索...");
                size(Size.SMALL);
            }
        }
        SearchInput input = new SearchInput();
        input.disabled(true);
        assertTrue(input.isDisable());
        assertTrue(input.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }
}
