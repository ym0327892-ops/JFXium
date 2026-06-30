package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BadgeAnt")
class BadgeAntTest extends JfxTestBase {

    // ---------------------------------------------------------------
    // 基础创建
    // ---------------------------------------------------------------

    @Test
    @DisplayName("build() 返回 StackPane 并挂 jfx-badge")
    void build_returnsStackPane_withStyleClass() {
        StackPane badge = BadgeAnt.create().build();
        assertNotNull(badge);
        assertTrue(badge.getStyleClass().contains(JfxStyles.BADGE));
    }

    // ---------------------------------------------------------------
    // count 形态
    // ---------------------------------------------------------------

    @Test
    @DisplayName("count(5) 渲染 indicator 挂 jfx-badge-count")
    void count_5_rendersIndicatorWithCountClass() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("消息"))
                .count(5)
                .build();

        // content(1) + indicator(1) = 2 个子节点
        assertEquals(2, badge.getChildren().size());
        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_INDICATOR));
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_COUNT));
        assertEquals("5", indicator.getText());
    }

    @Test
    @DisplayName("count(0) 不渲染 indicator")
    void count_zero_noIndicator() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("消息"))
                .count(0)
                .build();

        // 仅 content 节点
        assertEquals(1, badge.getChildren().size());
    }

    @Test
    @DisplayName("count 负数也不渲染 indicator")
    void count_negative_noIndicator() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("消息"))
                .count(-3)
                .build();

        assertEquals(1, badge.getChildren().size());
    }

    // ---------------------------------------------------------------
    // dot 形态
    // ---------------------------------------------------------------

    @Test
    @DisplayName("dot(true) 渲染 indicator 挂 jfx-badge-dot")
    void dot_true_rendersDotIndicator() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("在线"))
                .dot(true)
                .build();

        assertEquals(2, badge.getChildren().size());
        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_INDICATOR));
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_DOT));
    }

    // ---------------------------------------------------------------
    // status 形态
    // ---------------------------------------------------------------

    @Test
    @DisplayName("status(SUCCESS) 渲染 indicator 挂 jfx-badge-status + jfx-badge-status-success")
    void status_success_rendersWithModifier() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("在线"))
                .status(BadgeAnt.Status.SUCCESS)
                .build();

        assertEquals(2, badge.getChildren().size());
        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_INDICATOR));
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_STATUS));
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_STATUS_SUCCESS));
    }

    @Test
    @DisplayName("status(ERROR) 挂 jfx-badge-status-error")
    void status_error_rendersWithErrorClass() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("离线"))
                .status(BadgeAnt.Status.ERROR)
                .build();

        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_STATUS_ERROR));
    }

    @Test
    @DisplayName("status(WARNING) 挂 jfx-badge-status-warning")
    void status_warning() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("警告"))
                .status(BadgeAnt.Status.WARNING)
                .build();

        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_STATUS_WARNING));
    }

    @Test
    @DisplayName("status(DEFAULT) 挂 jfx-badge-status-default")
    void status_default() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("默认"))
                .status(BadgeAnt.Status.DEFAULT)
                .build();

        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_STATUS_DEFAULT));
    }

    // ---------------------------------------------------------------
    // null 内容
    // ---------------------------------------------------------------

    @Test
    @DisplayName("content(null) 不抛异常")
    void content_null_noException() {
        StackPane badge = BadgeAnt.create()
                .content(null)
                .count(3)
                .build();
        assertNotNull(badge);
        // indicator 正常渲染
        assertEquals(1, badge.getChildren().size());
    }

    // ---------------------------------------------------------------
    // status 优先于 dot（count > 0 > dot > status 互斥）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("count > 0 优先于 dot → 渲染 count 形态")
    void count_overrides_dot() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("消息"))
                .count(10)
                .dot(true)
                .build();

        Label indicator = (Label) badge.getChildren().get(1);
        assertTrue(indicator.getStyleClass().contains(JfxStyles.BADGE_COUNT));
        assertFalse(indicator.getStyleClass().contains(JfxStyles.BADGE_DOT));
    }

    // ---------------------------------------------------------------
    // AbstractStyleBuilder 继承
    // ---------------------------------------------------------------

    @Test
    @DisplayName("padding 应用到 StackPane")
    void padding_appliedToStackPane() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("test"))
                .padding(8)
                .build();
        assertEquals(8, badge.getPadding().getTop());
    }

    @Test
    @DisplayName("styleClass 追加到 StackPane")
    void styleClass_appendedToStackPane() {
        StackPane badge = BadgeAnt.create()
                .content(new Label("test"))
                .styleClass("my-badge")
                .build();
        assertTrue(badge.getStyleClass().contains("my-badge"));
    }
}
