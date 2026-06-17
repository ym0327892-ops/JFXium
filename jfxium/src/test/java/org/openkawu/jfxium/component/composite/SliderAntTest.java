package org.openkawu.jfxium.component.composite;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SliderAnt 单元测试 —— 覆盖 min/max/step/value、范围模式、marks、onChange 和 bindValue。
 */
@DisplayName("SliderAnt")
class SliderAntTest extends JfxTestBase {

    @Test
    @DisplayName("create().build() 返回非空 Node")
    void build_returnsNode() {
        Node slider = SliderAnt.create()
                .min(0).max(100).value(50)
                .build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("custom min/max/value 不抛异常")
    void customRange() {
        Node slider = SliderAnt.create()
                .min(10).max(200).value(50).step(10)
                .build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("NaN / 反转区间构建安全")
    void invalidRange_safe() {
        Node slider = SliderAnt.create()
                .min(Double.NaN)
                .max(Double.NaN)
                .value(Double.NaN)
                .step(Double.NaN)
                .build();

        Slider inner = findSlider(slider);
        assertEquals(0.0, inner.getMin(), 0.001);
        assertEquals(100.0, inner.getMax(), 0.001);
        assertEquals(0.0, inner.getValue(), 0.001);
    }

    @Test
    @DisplayName("vertical(true) 垂直模式")
    void vertical_true() {
        Node slider = SliderAnt.create()
                .vertical(true).build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("reverse(true) 反向")
    void reverse_true() {
        Node slider = SliderAnt.create()
                .reverse(true).build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("dots(true) 显示刻度点")
    void dots_true() {
        Node slider = SliderAnt.create()
                .dots(true).build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("marks 刻度标记")
    void marks() {
        Map<Double, String> marks = new LinkedHashMap<>();
        marks.put(0.0, "0%");
        marks.put(50.0, "50%");
        marks.put(100.0, "100%");
        Node slider = SliderAnt.create()
                .marks(marks).build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("tipFormatter 格式化提示")
    void tipFormatter() {
        Node slider = SliderAnt.create()
                .tipFormatter(v -> v.intValue() + "px")
                .tooltipVisible(true)
                .build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("range 双滑块模式")
    void range_mode() {
        Node slider = SliderAnt.create()
                .range()
                .defaultValue(new double[]{20, 80})
                .build();
        assertNotNull(slider);
    }

    // ---------- onChange ----------

    @Test
    @DisplayName("onChange 注册回调")
    void onChange_smoke() {
        Node slider = SliderAnt.create()
                .onChange(v -> {})
                .build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("onChangeComplete 注册回调")
    void onChangeComplete_smoke() {
        Node slider = SliderAnt.create()
                .onChangeComplete(v -> {})
                .build();
        assertNotNull(slider);
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 外部 DoubleProperty")
    void bindValue_smoke() {
        DoubleProperty prop = new SimpleDoubleProperty(30);
        Node slider = SliderAnt.create()
                .min(0).max(100)
                .bindValue(prop)
                .build();
        assertNotNull(slider);
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        Node slider = SliderAnt.create().bindValue(null).build();
        assertNotNull(slider);
    }

    // ---------- AbstractStyleBuilder ----------

    @Test
    @DisplayName("styleClass 追加到 wrapper")
    void styleClass_applied() {
        Node slider = SliderAnt.create().styleClass("my-slider").build();
        assertTrue(slider.getStyleClass().contains("my-slider"));
    }

    @Test
    @DisplayName("padding 应用")
    void padding_applied() {
        Node slider = SliderAnt.create().padding(10, 5, 10, 5).build();
        assertNotNull(slider);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联单滑块")
    void fullChain_single() {
        DoubleProperty prop = new SimpleDoubleProperty(25);
        Map<Double, String> marks = new LinkedHashMap<>();
        marks.put(0.0, "0");
        marks.put(100.0, "100");

        Node slider = SliderAnt.create()
                .min(0).max(100).value(50).step(5)
                .disabled(false)
                .marks(marks)
                .tipFormatter(v -> v.intValue() + "%")
                .onChange(v -> {})
                .bindValue(prop)
                .styleClass("custom-slider")
                .padding(4)
                .build();
        assertNotNull(slider);
    }

    private static Slider findSlider(Node node) {
        if (node instanceof Slider slider) {
            return slider;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                try {
                    return findSlider(child);
                } catch (AssertionError ignored) {
                    // continue searching
                }
            }
        }
        throw new AssertionError("SliderAnt should contain a Slider");
    }
}
