package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StepsAnt")
class StepsAntTest extends JfxTestBase {

    // ---------------------------------------------------------------
    // 基础创建
    // ---------------------------------------------------------------

    @Test
    @DisplayName("create().step().build() 返回 HBox 并挂 jfx-steps")
    void build_returnsHBox_withStyleClass() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .step("步骤二")
                .build();
        assertNotNull(steps);
        assertInstanceOf(HBox.class, steps);
        assertTrue(steps.getStyleClass().contains(JfxStyles.STEPS));
    }

    @Test
    @DisplayName("空 steps 不抛异常")
    void build_emptySteps_noException() {
        Node steps = StepsAnt.create().build();
        assertNotNull(steps);
    }

    // ---------------------------------------------------------------
    // 方向
    // ---------------------------------------------------------------

    @Test
    @DisplayName("direction(VERTICAL) 返回 VBox")
    void direction_vertical_returnsVBox() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .step("步骤二")
                .direction(StepsAnt.Direction.VERTICAL)
                .build();
        assertInstanceOf(VBox.class, steps);
        assertTrue(steps.getStyleClass().contains(JfxStyles.STEPS_VERTICAL));
    }

    @Test
    @DisplayName("direction(null) 回退默认 HORIZONTAL")
    void direction_null_fallbackHorizontal() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .direction(null)
                .build();
        assertInstanceOf(HBox.class, steps);
    }

    // ---------------------------------------------------------------
    // current
    // ---------------------------------------------------------------

    @Test
    @DisplayName("current(1) → 第一步挂 STATE_FINISHED, 第二步挂 STATE_CURRENT, 第三步挂 STATE_WAIT")
    void current_1_stateClasses() {
        Node steps = StepsAnt.create()
                .step("第一步")
                .step("第二步", "进行中")
                .step("第三步")
                .current(1)
                .build();

        // 验证 HBox 根容器内有 3 个 step item
        HBox container = (HBox) steps;
        assertEquals(3, container.getChildren().size());

        // 每个 item 应挂 jfx-steps-item
        for (Node child : container.getChildren()) {
            assertTrue(child.getStyleClass().contains(JfxStyles.STEPS_ITEM));
        }
    }

    // ---------------------------------------------------------------
    // size
    // ---------------------------------------------------------------

    @Test
    @DisplayName("size(SMALL) 不抛异常")
    void size_small_noException() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .step("步骤二")
                .size(Size.SMALL)
                .build();
        assertNotNull(steps);
    }

    @Test
    @DisplayName("size(null) 回退默认")
    void size_null_fallbackDefault() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .size(null)
                .build();
        assertNotNull(steps);
    }

    // ---------------------------------------------------------------
    // step 重载
    // ---------------------------------------------------------------

    @Test
    @DisplayName("step(title, description) 两步重载")
    void step_withDescription() {
        Node steps = StepsAnt.create()
                .step("填写信息", "输入基本资料")
                .step("验证身份")
                .step("完成", "注册成功", "check")
                .build();
        assertNotNull(steps);
    }

    // ---------------------------------------------------------------
    // Controller
    // ---------------------------------------------------------------

    @Test
    @DisplayName("controller() 返回非 null 且 getCurrent/Total 正确")
    void controller_returnsCurrentAndTotal() {
        StepsAnt.Builder builder = StepsAnt.create()
                .step("第一步")
                .step("第二步")
                .step("第三步")
                .current(1);
        builder.build();
        StepsAnt.Controller ctrl = builder.controller();

        assertNotNull(ctrl);
        assertEquals(3, ctrl.getTotal());
        assertEquals(1, ctrl.getCurrent());
    }

    @Test
    @DisplayName("controller().next() 前进, prev() 后退")
    void controller_next_and_prev() {
        StepsAnt.Builder builder = StepsAnt.create()
                .step("第一步")
                .step("第二步")
                .step("第三步")
                .current(0);
        builder.build();
        StepsAnt.Controller ctrl = builder.controller();

        ctrl.next();
        assertEquals(1, ctrl.getCurrent());

        ctrl.next();
        assertEquals(2, ctrl.getCurrent());

        // 已是最后一步，next 不越界
        ctrl.next();
        assertEquals(2, ctrl.getCurrent());

        ctrl.prev();
        assertEquals(1, ctrl.getCurrent());

        ctrl.prev();
        assertEquals(0, ctrl.getCurrent());

        // 已是第一步，prev 不越界
        ctrl.prev();
        assertEquals(0, ctrl.getCurrent());
    }

    @Test
    @DisplayName("controller().setCurrent(2) 直接跳转")
    void controller_setCurrent() {
        StepsAnt.Builder builder = StepsAnt.create()
                .step("第一步")
                .step("第二步")
                .step("第三步")
                .current(0);
        builder.build();
        StepsAnt.Controller ctrl = builder.controller();

        ctrl.setCurrent(2);
        assertEquals(2, ctrl.getCurrent());

        // 越界不操作
        ctrl.setCurrent(-1);
        assertEquals(2, ctrl.getCurrent());

        ctrl.setCurrent(5);
        assertEquals(2, ctrl.getCurrent());
    }

    @Test
    @DisplayName("controller() 在 build() 之前调用抛异常")
    void controller_beforeBuild_throws() {
        StepsAnt.Builder builder = StepsAnt.create()
                .step("第一步")
                .step("第二步");
        assertThrows(IllegalStateException.class, builder::controller);
    }

    // ---------------------------------------------------------------
    // AbstractStyleBuilder 继承
    // ---------------------------------------------------------------

    @Test
    @DisplayName("styleClass 追加到根容器")
    void styleClass_appended() {
        HBox steps = (HBox) StepsAnt.create()
                .step("步骤一")
                .styleClass("my-steps")
                .build();
        assertTrue(steps.getStyleClass().contains("my-steps"));
        assertTrue(steps.getStyleClass().contains(JfxStyles.STEPS));
    }

    @Test
    @DisplayName("padding 应用到根容器")
    void padding_applied() {
        Node steps = StepsAnt.create()
                .step("步骤一")
                .padding(12)
                .build();
        assertEquals(12, ((HBox) steps).getPadding().getTop());
    }
}
