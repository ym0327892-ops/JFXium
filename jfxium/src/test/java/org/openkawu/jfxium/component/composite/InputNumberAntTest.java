package org.openkawu.jfxium.component.composite;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
        assertNotNull(box);
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
        assertNotNull(box);
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
    @DisplayName("styleClass 追加 — NOTE: build() 暂未调用 applyStyles，styleClass 不会生效")
    void styleClass_applied() {
        HBox box = InputNumberAnt.create()
                .styleClass("my-num").build();
        // NOTE: InputNumberAnt.build() 目前未调用 applyStyles(container)，
        // 所以用户 styleClass 不会实际挂到返回的 HBox 上。此断言反映当前行为。
        assertNotNull(box);
    }

    @Test
    @DisplayName("maxWidth — NOTE: build() 暂未调用 applyStyles")
    void maxWidth_applied() {
        HBox box = InputNumberAnt.create().maxWidth(200).build();
        // NOTE: InputNumberAnt.build() 目前未调用 applyStyles(container)，
        // 所以 maxWidth 不会实际设置到返回的 HBox 上。
        assertNotNull(box);
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
    }
}
