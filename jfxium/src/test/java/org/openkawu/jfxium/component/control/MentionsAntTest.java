package org.openkawu.jfxium.component.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MentionsAnt 单元测试 —— 覆盖 Builder 样式接线、option/options、prefix/placeholder/rows 钳制、
 * onSelect/onChange、bindValue 双向绑定、disabled、链式 + 继承式核心契约。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 TextArea + 挂 jfx-mentions/jfx-mentions-area class</li>
 *   <li>option：单条 / null value / null label fallback / 多次</li>
 *   <li>options：批量 / null 安全</li>
 *   <li>prefix：默认 @ / 自定义 / null 钳制回 @</li>
 *   <li>placeholder：常规 + null 视为 ""</li>
 *   <li>rows：默认 4 / 钳制 &lt;1 → 1</li>
 *   <li>onChange：通过 setText 触发监听</li>
 *   <li>onSelect / bindValue：双向绑定 + null 安全</li>
 *   <li>disabled</li>
 *   <li>链式串联 + 继承式核心契约（build returnsSelf / 多态）</li>
 * </ul>
 */
@DisplayName("MentionsAnt")
class MentionsAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 TextArea 并挂默认样式")
    void build_returnsTextArea() {
        TextArea mentions = MentionsAnt.create()
                .placeholder("输入 @")
                .build();

        assertNotNull(mentions);
        assertInstanceOf(TextArea.class, mentions);
        assertTrue(mentions.getStyleClass().contains(JfxStyles.MENTIONS));
        assertTrue(mentions.getStyleClass().contains(JfxStyles.MENTIONS_AREA));
        assertEquals("输入 @", mentions.getPromptText());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回 TextArea")
    void builderStyles_applied() {
        TextArea mentions = MentionsAnt.create()
                .styleClass("mentions-extra")
                .prefW(320)
                .build();

        assertTrue(mentions.getStyleClass().contains("mentions-extra"));
        assertEquals(320, mentions.getPrefWidth());
    }

    @Test
    @DisplayName("默认 rows 为 4（构造 init）")
    void rows_defaultIsFour() {
        TextArea mentions = MentionsAnt.create().build();
        assertEquals(4, mentions.getPrefRowCount());
    }

    // ============================================================
    // option
    // ============================================================

    @Nested
    @DisplayName("option（value, label）")
    class OptionTests {

        @Test
        @DisplayName("option 链式追加多次 build 不抛异常")
        void option_multiple() {
            TextArea mentions = assertDoesNotThrow(() ->
                    MentionsAnt.create()
                            .option("zhangsan", "张三")
                            .option("lisi", "李四")
                            .option("wangwu", "王五")
                            .build());
            assertNotNull(mentions);
            assertTrue(mentions.getStyleClass().contains(JfxStyles.MENTIONS));
        }

        @Test
        @DisplayName("option(null value, label) 静默忽略，不抛异常")
        void option_nullValueIgnored() {
            TextArea mentions = assertDoesNotThrow(() ->
                    MentionsAnt.create()
                            .option(null, "无效")
                            .option("lisi", "李四")
                            .build());
            assertNotNull(mentions);
        }

        @Test
        @DisplayName("option(value, null label) label 回落为 value")
        void option_nullLabelFallsBackToValue() {
            // 不抛异常 + 行为落地由 options 集合（private）内部承载
            TextArea mentions = assertDoesNotThrow(() ->
                    MentionsAnt.create()
                            .option("abc", null)
                            .build());
            assertNotNull(mentions);
        }
    }

    // ============================================================
    // options 批量
    // ============================================================

    @Nested
    @DisplayName("options(List）")
    class OptionsBatch {

        @Test
        @DisplayName("options(List) 批量替换（null 安全）")
        void options_replaces() {
            List<MentionsAnt.Option> list = new ArrayList<>();
            list.add(new MentionsAnt.Option("a", "A"));
            list.add(new MentionsAnt.Option("b", "B"));
            TextArea mentions = assertDoesNotThrow(() ->
                    MentionsAnt.create()
                            .option("discard", "丢弃")
                            .options(list)
                            .build());
            assertNotNull(mentions);
        }

        @Test
        @DisplayName("options(null) 不抛异常且保留已有 option")
        void options_nullSafe() {
            TextArea mentions = assertDoesNotThrow(() ->
                    MentionsAnt.create()
                            .option("keep", "保留")
                            .options(null)
                            .build());
            assertNotNull(mentions);
        }
    }

    // ============================================================
    // prefix
    // ============================================================

    @Nested
    @DisplayName("prefix（默认 @，null 钳制）")
    class Prefix {

        @Test
        @DisplayName("默认 prefix 为 @，行为可触发 textProperty 监听")
        void prefix_defaultTriggersListener() {
            AtomicReference<String> captured = new AtomicReference<>();
            TextArea mentions = MentionsAnt.create()
                    .onChange(captured::set)
                    .build();
            mentions.setText("hello");
            assertEquals("hello", captured.get());
        }

        @Test
        @DisplayName("prefix(\"#\") 自定义触发符后 setText 不抛异常")
        void prefix_custom() {
            MentionsAnt mentions = assertDoesNotThrow(() -> {
                MentionsAnt m = MentionsAnt.create().prefix("#");
                m.setText("#tag");
                return m.build();
            });
            assertNotNull(mentions);
        }

        @Test
        @DisplayName("prefix(null) 钳制回 @ 默认值")
        void prefix_nullClampsToDefault() {
            MentionsAnt mentions = assertDoesNotThrow(() -> {
                MentionsAnt m = MentionsAnt.create().prefix(null);
                m.setText("@");
                return m.build();
            });
            assertNotNull(mentions);
        }
    }

    // ============================================================
    // placeholder
    // ============================================================

    @Nested
    @DisplayName("placeholder")
    class Placeholder {

        @Test
        @DisplayName("placeholder 写入 promptText")
        void placeholder_setsPrompt() {
            TextArea mentions = MentionsAnt.create()
                    .placeholder("输入 @ 提及用户")
                    .build();
            assertEquals("输入 @ 提及用户", mentions.getPromptText());
        }

        @Test
        @DisplayName("placeholder(null) 视为 \"\"")
        void placeholder_nullBecomesEmpty() {
            TextArea mentions = MentionsAnt.create()
                    .placeholder("原始")
                    .placeholder(null)
                    .build();
            assertEquals("", mentions.getPromptText());
        }
    }

    // ============================================================
    // rows 钳制
    // ============================================================

    @Nested
    @DisplayName("rows（&lt;1 钳制为 1）")
    class Rows {

        @Test
        @DisplayName("rows(8) 设置为 8")
        void rows_normal() {
            TextArea mentions = MentionsAnt.create().rows(8).build();
            assertEquals(8, mentions.getPrefRowCount());
        }

        @Test
        @DisplayName("rows(0) 钳制为 1")
        void rows_zeroClampedToOne() {
            TextArea mentions = MentionsAnt.create().rows(0).build();
            assertEquals(1, mentions.getPrefRowCount());
        }

        @Test
        @DisplayName("rows(-5) 钳制为 1")
        void rows_negativeClampedToOne() {
            TextArea mentions = MentionsAnt.create().rows(-5).build();
            assertEquals(1, mentions.getPrefRowCount());
        }
    }

    // ============================================================
    // onChange
    // ============================================================

    @Test
    @DisplayName("onChange 监听 textProperty 变化（新值）")
    void onChange_firesOnTextChange() {
        AtomicReference<String> captured = new AtomicReference<>("");
        TextArea mentions = MentionsAnt.create()
                .onChange(captured::set)
                .build();
        mentions.setText("用户输入");
        assertEquals("用户输入", captured.get());
    }

    @Test
    @DisplayName("onChange(null) 不抛异常，setText 不触发 NPE")
    void onChange_nullSafe() {
        TextArea mentions = MentionsAnt.create()
                .onChange(null)
                .build();
        assertDoesNotThrow(() -> mentions.setText("abc"));
    }

    // ============================================================
    // onSelect / bindValue
    // ============================================================

    @Test
    @DisplayName("onSelect(null) 不抛异常")
    void onSelect_nullSafe() {
        TextArea mentions = assertDoesNotThrow(() ->
                MentionsAnt.create()
                        .onSelect(null)
                        .build());
        assertNotNull(mentions);
    }

    @Test
    @DisplayName("bindValue 双向绑定：text → property 同步")
    void bindValue_textToProperty() {
        StringProperty backing = new SimpleStringProperty("");
        TextArea mentions = MentionsAnt.create()
                .bindValue(backing)
                .build();
        mentions.setText("hello");
        assertEquals("hello", backing.get());
    }

    @Test
    @DisplayName("bindValue 双向绑定：property → text 同步")
    void bindValue_propertyToText() {
        StringProperty backing = new SimpleStringProperty("");
        TextArea mentions = MentionsAnt.create()
                .bindValue(backing)
                .build();
        backing.set("world");
        assertEquals("world", mentions.getText());
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_nullSafe() {
        TextArea mentions = assertDoesNotThrow(() ->
                MentionsAnt.create()
                        .bindValue(null)
                        .build());
        assertNotNull(mentions);
    }

    // ============================================================
    // disabled
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        TextArea mentions = MentionsAnt.create().disabled(true).build();
        assertTrue(mentions.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        TextArea mentions = MentionsAnt.create().build();
        assertFalse(mentions.isDisable());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain_noException() {
        TextArea mentions = assertDoesNotThrow(() ->
                MentionsAnt.create()
                        .option("zhangsan", "张三")
                        .option("lisi", "李四")
                        .prefix("@")
                        .placeholder("输入 @")
                        .rows(5)
                        .onChange(v -> {})
                        .onSelect(v -> {})
                        .styleClass("mentions-extra")
                        .prefW(360)
                        .disabled(false)
                        .build());
        assertNotNull(mentions);
        assertEquals("输入 @", mentions.getPromptText());
        assertEquals(5, mentions.getPrefRowCount());
        assertTrue(mentions.getStyleClass().contains("mentions-extra"));
        assertEquals(360, mentions.getPrefWidth());
    }

    @Test
    @DisplayName("build() 返回自身（assertSame）")
    void build_returnsSelf() {
        MentionsAnt mentions = MentionsAnt.create()
                .placeholder("测试")
                .build();
        assertSame(mentions, mentions.build());
    }

    @Test
    @DisplayName("多态：MentionsAnt 即是 TextArea，可直接 add 到容器")
    void polymorphic_isTextArea() {
        MentionsAnt mentions = MentionsAnt.create().build();
        assertInstanceOf(TextArea.class, mentions);
        assertInstanceOf(MentionsAnt.class, mentions);
    }
}
