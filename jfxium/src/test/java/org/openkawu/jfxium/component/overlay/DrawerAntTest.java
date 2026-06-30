package org.openkawu.jfxium.component.overlay;

import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DrawerAnt")
class DrawerAntTest extends JfxTestBase {

    // ---------------------------------------------------------------
    // Builder 基础创建
    // ---------------------------------------------------------------

    @Test
    @DisplayName("create().build() 返回 DrawerResult")
    void build_returnsDrawerResult() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("标题")
                .content(new Label("内容"))
                .build();
        assertNotNull(result);
        assertInstanceOf(DrawerAnt.DrawerResult.class, result);
    }

    @Test
    @DisplayName("Builder 默认值: placement=RIGHT, size=DEFAULT, width=378")
    void builderDefaults_placementRight_width378() throws Exception {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("测试")
                .content(new Label("content"))
                .build();

        Field configField = result.getClass().getDeclaredField("config");
        configField.setAccessible(true);
        DrawerAnt.Builder config = (DrawerAnt.Builder) configField.get(result);

        Field placementField = config.getClass().getDeclaredField("placement");
        placementField.setAccessible(true);
        assertEquals(DrawerAnt.Placement.RIGHT, placementField.get(config));

        Field widthField = config.getClass().getDeclaredField("width");
        widthField.setAccessible(true);
        assertEquals(378, widthField.get(config));
    }

    @Test
    @DisplayName("size(LARGE) 宽度 736")
    void size_large_width736() throws Exception {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("LARGE")
                .content(new Label("content"))
                .size(DrawerAnt.Size.LARGE)
                .build();

        Field configField = result.getClass().getDeclaredField("config");
        configField.setAccessible(true);
        DrawerAnt.Builder config = (DrawerAnt.Builder) configField.get(result);

        Field widthField = config.getClass().getDeclaredField("width");
        widthField.setAccessible(true);
        assertEquals(736, widthField.get(config));
    }

    // ---------------------------------------------------------------
    // ClosePlacement 防呆校验
    // ---------------------------------------------------------------

    @Test
    @DisplayName("closePlacement(NONE) + maskClosable=false + keyboard=false → 抛异常")
    void closePlacementNone_noClosePath_throws() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                DrawerAnt.create()
                        .title("无法关闭")
                        .content(new Label("content"))
                        .closePlacement(DrawerAnt.ClosePlacement.NONE)
                        .maskClosable(false)
                        .keyboard(false)
                        .build()
        );
        assertTrue(ex.getMessage().contains("NONE") || ex.getMessage().contains("关闭"));
    }

    @Test
    @DisplayName("closePlacement(NONE) + maskClosable=true → 不抛异常")
    void closePlacementNone_withMaskClosable_ok() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("可点遮罩关闭")
                .content(new Label("content"))
                .closePlacement(DrawerAnt.ClosePlacement.NONE)
                .maskClosable(true)
                .keyboard(false)
                .build();
        assertNotNull(result);
    }

    @Test
    @DisplayName("closePlacement(NONE) + keyboard=true → 不抛异常")
    void closePlacementNone_withKeyboard_ok() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("ESC 关闭")
                .content(new Label("content"))
                .closePlacement(DrawerAnt.ClosePlacement.NONE)
                .maskClosable(false)
                .keyboard(true)
                .build();
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // Placement 变体
    // ---------------------------------------------------------------

    @Test
    @DisplayName("placement=LEFT 不抛异常")
    void placement_left() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("左侧")
                .content(new Label("content"))
                .placement(DrawerAnt.Placement.LEFT)
                .build();
        assertNotNull(result);
    }

    @Test
    @DisplayName("placement=TOP 不抛异常")
    void placement_top() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("顶部")
                .content(new Label("content"))
                .placement(DrawerAnt.Placement.TOP)
                .build();
        assertNotNull(result);
    }

    @Test
    @DisplayName("placement=BOTTOM + height(300)")
    void placement_bottom_withHeight() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("底部")
                .content(new Label("content"))
                .placement(DrawerAnt.Placement.BOTTOM)
                .height(300)
                .build();
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // 可选项: footer / extra / closePlacement
    // ---------------------------------------------------------------

    @Test
    @DisplayName("footer 传入节点不抛异常")
    void footer_withNode() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("有 footer")
                .content(new Label("content"))
                .footer(new Label("确定"))
                .build();
        assertNotNull(result);
    }

    @Test
    @DisplayName("closePlacement=RIGHT 不抛异常")
    void closePlacement_right() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("关闭在右")
                .content(new Label("content"))
                .closePlacement(DrawerAnt.ClosePlacement.RIGHT)
                .build();
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // AbstractStyleBuilder 继承
    // ---------------------------------------------------------------

    @Test
    @DisplayName("styleClass/padding 由 AbstractStyleBuilder 继承")
    void styleClass_and_padding_inherited() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("样式")
                .content(new Label("content"))
                .styleClass("my-custom")
                .padding(8)
                .build();
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // 链式全量
    // ---------------------------------------------------------------

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain() {
        DrawerAnt.DrawerResult result = DrawerAnt.create()
                .title("完整配置")
                .content(new Label("正文内容"))
                .placement(DrawerAnt.Placement.RIGHT)
                .width(500)
                .height(600)
                .size(DrawerAnt.Size.DEFAULT)
                .closePlacement(DrawerAnt.ClosePlacement.LEFT)
                .maskClosable(true)
                .keyboard(true)
                .footer(new Label("底部操作区"))
                .extra(new Label("额外节点"))
                .styleClass("full-drawer")
                .padding(12)
                .onClose(ok -> {})
                .build();
        assertNotNull(result);
    }
}
