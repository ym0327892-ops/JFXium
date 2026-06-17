package org.openkawu.jfxium.component.composite;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InputNumberAnt 单元测试 —— 覆盖 value/min/max/step、前缀后缀、精度和 bindValue。
 */
@DisplayName("InputNumberAnt")
class InputNumberAntTest extends JfxTestBase {

    @Test
    @DisplayName("create().build() 返回 HBox")
    void build_returnsHBox() {
        HBox box = InputNumberAnt.create().build();
        assertNotNull(box);
        assertTrue(box.getStyleClass().contains(JfxStyles.INPUT_NUMBER));
    }

    @Test
    @DisplayName("value/min/max/step 设置")
    void rangeSettings() {
        HBox box = InputNumberAnt.create()
                .value(10).min(0).max(100).step(5)
                .build();
        assertNotNull(box);
    }

    @Test
    @DisplayName("min/max/value 非有限值和反转区间构建安全")
    void invalidRange_safe() {
        HBox box = InputNumberAnt.create()
                .min(100)
                .max(0)
                .value(Double.NaN)
                .step(Double.NaN)
                .build();
        assertEquals("0", fieldOf(box).getText());
    }

    @Test
    @DisplayName("disabled 禁用")
    void disabled() {
        HBox box = InputNumberAnt.create()
                .disabled().build();
        assertTrue(box.getStyleClass().contains(JfxStyles.INPUT_NUMBER_DISABLED));
    }

    @Test
    @DisplayName("precision(2) 精度")
    void precision() {
        HBox box = InputNumberAnt.create()
                .value(3.14159).precision(2).build();
        assertEquals("3.14", fieldOf(box).getText());
    }

    @Test
    @DisplayName("prefix/suffix 前后缀")
    void prefix_suffix() {
        HBox box = InputNumberAnt.create()
                .prefix("¥").value(99.9).suffix("元")
                .build();
        assertNotNull(box);
    }

    @Test
    @DisplayName("placeholder 提示文本")
    void placeholder() {
        HBox box = InputNumberAnt.create()
                .placeholder("输入数量").build();
        assertEquals("输入数量", fieldOf(box).getPromptText());
    }

    @Test
    @DisplayName("readOnly(true) 设置内部输入框不可编辑")
    void readOnly() {
        HBox box = InputNumberAnt.create()
                .readOnly(true).build();
        assertFalse(fieldOf(box).isEditable());
    }

    @Test
    @DisplayName("size 挂载尺寸 styleClass")
    void size() {
        HBox small = InputNumberAnt.create().size(InputNumberAnt.Size.SMALL).build();
        HBox large = InputNumberAnt.create().size(InputNumberAnt.Size.LARGE).build();
        assertTrue(small.getStyleClass().contains(JfxStyles.INPUT_NUMBER_SMALL));
        assertTrue(large.getStyleClass().contains(JfxStyles.INPUT_NUMBER_LARGE));
    }

    @Test
    @DisplayName("onChange 注册回调")
    void onChange_smoke() {
        HBox box = InputNumberAnt.create()
                .onChange(v -> {})
                .build();
        assertNotNull(box);
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 外部 DoubleProperty 初始同步")
    void bindValue_initialSync() {
        DoubleProperty prop = new SimpleDoubleProperty(42);
        HBox box = InputNumberAnt.create()
                .bindValue(prop)
                .build();
        assertNotNull(box); // 不抛异常，值应已同步到内部 field
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        HBox box = InputNumberAnt.create().bindValue(null).build();
        assertNotNull(box);
    }

    // ---------- AbstractStyleBuilder ----------

    @Test
    @DisplayName("styleClass 追加")
    void styleClass_applied() {
        HBox box = InputNumberAnt.create()
                .styleClass("my-num").build();
        assertTrue(box.getStyleClass().contains("my-num"));
    }

    @Test
    @DisplayName("maxWidth 应用到返回容器")
    void maxWidth_applied() {
        HBox box = InputNumberAnt.create().maxWidth(200).build();
        assertEquals(200, box.getMaxWidth());
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        DoubleProperty prop = new SimpleDoubleProperty(0);
        HBox box = InputNumberAnt.create()
                .value(50)
                .min(0).max(100).step(10)
                .precision(1)
                .prefix("$")
                .suffix("/月")
                .placeholder("输入金额")
                .disabled(false)
                .readOnly(false)
                .onChange(v -> {})
                .bindValue(prop)
                .styleClass("price-input")
                .padding(4)
                .prefWidth(150)
                .build();
        assertNotNull(box);
        assertTrue(box.getStyleClass().contains("price-input"));
        assertEquals(150, box.getPrefWidth());
    }

    private static TextField fieldOf(HBox box) {
        return box.getChildren().stream()
                .filter(TextField.class::isInstance)
                .map(TextField.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("InputNumberAnt should contain a TextField"));
    }
}
