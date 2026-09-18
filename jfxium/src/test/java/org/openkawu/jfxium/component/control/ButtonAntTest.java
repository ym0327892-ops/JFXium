package org.openkawu.jfxium.component.control;

import javafx.scene.control.ContentDisplay;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ButtonAnt 单元测试 —— 覆盖 Builder 创建、类型/尺寸/形状、状态、事件和链式调用。
 */
@DisplayName("ButtonAnt")
class ButtonAntTest extends JfxTestBase {

    // ---------- 工厂创建 ----------

    @Test
    @DisplayName("create() 返回非空 ButtonAnt 实例")
    void create_returnsNonNull() {
        ButtonAnt btn = ButtonAnt.create().build();
        assertNotNull(btn);
        assertEquals("", btn.getText());
    }

    @Test
    @DisplayName("create(text) 设置文本并挂默认 styleClass")
    void createWithText_setsTextAndDefaultClass() {
        ButtonAnt btn = ButtonAnt.create("提交").build();
        assertEquals("提交", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_DEFAULT));
    }

    @Test
    @DisplayName("create() 带 graphic 节点")
    void createWithGraphic() {
        Rectangle icon = new Rectangle(16, 16);
        ButtonAnt btn = new ButtonAnt("关闭", icon).build();
        assertEquals(icon, btn.getGraphic());
    }

    @Test
    @DisplayName("compactLink() 生成紧凑链接按钮")
    void compactLink_helper() {
        ButtonAnt btn = ButtonAnt.compactLink("查看").build();
        assertEquals("查看", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_LINK));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_XS));
    }

    @Test
    @DisplayName("link(text, size) 生成指定尺寸链接按钮")
    void link_helperWithSize() {
        ButtonAnt btn = ButtonAnt.link("查看", Size.SMALL).build();
        assertEquals("查看", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_LINK));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    @Test
    @DisplayName("iconOnly() 生成图标按钮")
    void iconOnly_helper() {
        Rectangle icon = new Rectangle(16, 16);
        ButtonAnt btn = ButtonAnt.iconOnly(icon).build();
        assertEquals(icon, btn.getGraphic());
        assertEquals(ContentDisplay.GRAPHIC_ONLY, btn.getContentDisplay());
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_TEXT));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_XS));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
    }

    // ---------- build() 返回自身 ----------

    @Test
    @DisplayName("build() 返回自身（this）")
    void build_returnsThis() {
        ButtonAnt btn = ButtonAnt.create();
        assertSame(btn, btn.build());
    }

    // ---------- text ----------

    @Test
    @DisplayName("text(null) 设为空字符串")
    void text_nullWritesEmpty() {
        ButtonAnt btn = ButtonAnt.create("OK").text(null).build();
        assertEquals("", btn.getText());
    }

    // ---------- type ----------

    @Test
    @DisplayName("type(PRIMARY) 挂 BUTTON_ACCENT，清除 DEFAULT")
    void type_primary() {
        ButtonAnt btn = ButtonAnt.create().type(ButtonAnt.Type.PRIMARY).build();
        assertFalse(btn.getStyleClass().contains(JfxStyles.BUTTON_DEFAULT),
                "PRIMARY 后不应含 BUTTON_DEFAULT");
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_ACCENT));
    }

    @Test
    @DisplayName("type(DANGER) 挂 default+danger")
    void type_danger() {
        ButtonAnt btn = ButtonAnt.create().type(ButtonAnt.Type.DANGER).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_DEFAULT));
        assertTrue(btn.getStyleClass().contains("danger"));
    }

    @Test
    @DisplayName("type 幂等：连续调用不重复挂类")
    void type_idempotent() {
        ButtonAnt btn = ButtonAnt.create()
                .type(ButtonAnt.Type.OUTLINED)
                .type(ButtonAnt.Type.DANGER)
                .type(ButtonAnt.Type.TEXT)
                .build();
        // 最终应只剩 TEXT — 不含多余的 residual styleClass
        assertEquals(2, btn.getStyleClass().size(),
                "TEXT 应只挂 jfx-btn + jfx-btn-text");
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_TEXT));
    }

    @Test
    @DisplayName("type 支持全部 10 种类型")
    void type_allTypes() {
        for (ButtonAnt.Type t : ButtonAnt.Type.values()) {
            ButtonAnt btn = ButtonAnt.create().type(t).build();
            assertNotNull(btn); // 不抛异常即可
        }
    }

    // ---------- size ----------

    @Test
    @DisplayName("size(DEFAULT/MIDDLE) 挂 SIZE_MIDDLE")
    void size_middle() {
        ButtonAnt btnDefault = ButtonAnt.create().size(Size.DEFAULT).build();
        ButtonAnt btnMiddle = ButtonAnt.create().size(Size.MIDDLE).build();
        assertTrue(btnDefault.getStyleClass().contains(JfxStyles.SIZE_MIDDLE));
        assertTrue(btnMiddle.getStyleClass().contains(JfxStyles.SIZE_MIDDLE));
    }

    @Test
    @DisplayName("size(SMALL) 挂 SIZE_SMALL")
    void size_small() {
        ButtonAnt btn = ButtonAnt.create().size(Size.SMALL).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    @Test
    @DisplayName("size(XS) 挂 SIZE_XS")
    void size_xs() {
        ButtonAnt btn = ButtonAnt.create().size(Size.XS).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_XS));
    }

    @Test
    @DisplayName("size(LARGE) 挂 SIZE_LARGE")
    void size_large() {
        ButtonAnt btn = ButtonAnt.create().size(Size.LARGE).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
    }

    @Test
    @DisplayName("size 幂等：SMALL→XS→LARGE 只挂 LARGE")
    void size_idempotent() {
        ButtonAnt btn = ButtonAnt.create().size(Size.SMALL)
                .size(Size.XS)
                .size(Size.LARGE).build();
        assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
        assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_XS));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
    }

    @Test
    @DisplayName("TEXT / LINK 同样支持四档 size")
    void size_appliesToTextAndLink() {
        ButtonAnt text = ButtonAnt.create("文字")
                .type(ButtonAnt.Type.TEXT)
                .size(Size.XS)
                .build();
        ButtonAnt link = ButtonAnt.create("链接")
                .type(ButtonAnt.Type.LINK)
                .size(Size.SMALL)
                .build();

        assertTrue(text.getStyleClass().contains(JfxStyles.BUTTON_TEXT));
        assertTrue(text.getStyleClass().contains(JfxStyles.SIZE_XS));
        assertTrue(link.getStyleClass().contains(JfxStyles.BUTTON_LINK));
        assertTrue(link.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    // ---------- shape ----------

    @Test
    @DisplayName("shape(ROUNDED) 挂 SHAPE_ROUNDED")
    void shape_rounded() {
        ButtonAnt btn = ButtonAnt.create().shape(ButtonAnt.Shape.ROUNDED).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
    }

    @Test
    @DisplayName("rounded() 快捷方法等价 shape(ROUNDED)")
    void rounded_shortcut() {
        ButtonAnt btn = ButtonAnt.create().rounded().build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
    }

    @Test
    @DisplayName("square() 快捷方法等价 shape(SQUARE)")
    void square_shortcut() {
        ButtonAnt btn = ButtonAnt.create().square().build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
    }

    // ---------- 状态 ----------

    @Test
    @DisplayName("disabled(true) 设置禁用")
    void disabled_true() {
        ButtonAnt btn = ButtonAnt.create().disabled(true).build();
        assertTrue(btn.isDisable());
    }

    @Test
    @DisplayName("loading(true) 设置禁用")
    void loading_true() {
        ButtonAnt btn = ButtonAnt.create().loading(true).build();
        assertTrue(btn.isDisable());
    }

    @Test
    @DisplayName("ghost(true) 挂 BUTTON_GHOST")
    void ghost_true() {
        ButtonAnt btn = ButtonAnt.create().ghost(true).build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_GHOST));
    }

    @Test
    @DisplayName("ghost() 无参快捷方法")
    void ghost_noArg() {
        ButtonAnt btn = ButtonAnt.create().ghost().build();
        assertTrue(btn.getStyleClass().contains(JfxStyles.BUTTON_GHOST));
    }

    @Test
    @DisplayName("ghost(false) 清除 BUTTON_GHOST")
    void ghost_falseRemoves() {
        ButtonAnt btn = ButtonAnt.create().ghost().ghost(false).build();
        assertFalse(btn.getStyleClass().contains(JfxStyles.BUTTON_GHOST));
    }

    @Test
    @DisplayName("block(true) 设置 MAX_VALUE 宽度")
    void block_true() {
        ButtonAnt btn = ButtonAnt.create().block(true).build();
        assertEquals(Double.MAX_VALUE, btn.getMaxWidth(), 0.01);
    }

    @Test
    @DisplayName("block(false) 恢复默认宽度")
    void block_false() {
        ButtonAnt btn = ButtonAnt.create().block().block(false).build();
        assertTrue(btn.getMaxWidth() < Double.MAX_VALUE);
    }

    // ---------- 图标 / 事件 ----------

    @Test
    @DisplayName("icon 设置 graphic")
    void icon_setsGraphic() {
        Rectangle icon = new Rectangle(16, 16);
        ButtonAnt btn = ButtonAnt.create().icon(icon).build();
        assertEquals(icon, btn.getGraphic());
    }

    @Test
    @DisplayName("contentDisplay 设置图标位置")
    void contentDisplay() {
        ButtonAnt btn = ButtonAnt.create().contentDisplay(ContentDisplay.RIGHT).build();
        assertEquals(ContentDisplay.RIGHT, btn.getContentDisplay());
    }

    @Test
    @DisplayName("onClick 注册 ActionEvent handler")
    void onClick_registersHandler() {
        ButtonAnt btn = ButtonAnt.create().onClick(e -> {}).build();
        assertNotNull(btn.getOnAction());
    }

    // ---------- 链式调用 ----------

    @Test
    @DisplayName("全部链式方法串联不抛异常")
    void fullChain_noException() {
        Rectangle icon = new Rectangle(16, 16);
        ButtonAnt btn = ButtonAnt.create("按钮")
                .type(ButtonAnt.Type.PRIMARY)
                .size(Size.MIDDLE)
                .rounded()
                .icon(icon)
                .contentDisplay(ContentDisplay.LEFT)
                .disabled(false)
                .ghost(false)
                .block(false)
                .onClick(e -> {})
                .styleClass("my-btn")
                .style("")
                .build();
        assertNotNull(btn);
        assertEquals("按钮", btn.getText());
    }

    // ---------- styleClass ----------

    @Test
    @DisplayName("styleClass(String) 幂等追加")
    void styleClass_idempotent() {
        ButtonAnt btn = ButtonAnt.create().styleClass("cls-a").styleClass("cls-a").build();
        long count = btn.getStyleClass().stream().filter("cls-a"::equals).count();
        assertEquals(1, count, "重复调 styleClass 不应重复挂");
    }

    @Test
    @DisplayName("styleClass(String...) 批量追加")
    void styleClass_varargs() {
        ButtonAnt btn = ButtonAnt.create().styleClass("a", "b", "c").build();
        assertTrue(btn.getStyleClass().contains("a"));
        assertTrue(btn.getStyleClass().contains("b"));
        assertTrue(btn.getStyleClass().contains("c"));
    }

    // ---------- 业务继承场景 ----------

    @Test
    @DisplayName("业务继承：子类构建后的流式 API 仍可用")
    void subclassInheritance() {
        class PrimarySubmit extends ButtonAnt {
            PrimarySubmit() {
                text("提交");
                type(ButtonAnt.Type.PRIMARY);
                size(Size.LARGE);
            }
        }
        PrimarySubmit btn = new PrimarySubmit();
        // 继承后构建，再链式改属性
        btn.size(Size.SMALL).disabled(true);
        assertEquals("提交", btn.getText());
        assertTrue(btn.isDisable());
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }
}
