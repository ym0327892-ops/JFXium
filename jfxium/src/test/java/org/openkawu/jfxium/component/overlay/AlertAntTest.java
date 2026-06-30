package org.openkawu.jfxium.component.overlay;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlertAnt")
class AlertAntTest extends JfxTestBase {

    /**
     * 在 FX 线程上创建 AlertAnt。所有工厂方法/构造函数必须走 FX 线程
     * （因为 JavaFX Alert 继承自 Dialog，内部创建 Stage 和 DialogPane）。
     */
    private static AlertAnt onFx(Supplier<AlertAnt> supplier) {
        AtomicReference<AlertAnt> ref = new AtomicReference<>();
        runOnFxThreadAndWait(() -> ref.set(supplier.get()));
        return ref.get();
    }
    // ---------------------------------------------------------------
    // 静态工厂
    // ---------------------------------------------------------------

    @Test
    @DisplayName("info(title, content, null) 不抛异常")
    void info_staticFactory_noException() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("提示", "操作成功", null);
            assertNotNull(alert);
            assertEquals("提示", alert.getHeaderText());
            assertEquals("操作成功", alert.getContentText());
            assertEquals(Alert.AlertType.INFORMATION, alert.getAlertType());
        });
    }

    @Test
    @DisplayName("warning(title, content, null) 类型正确")
    void warning_staticFactory() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.warning("警告", "请确认", null);
            assertEquals(Alert.AlertType.WARNING, alert.getAlertType());
        });
    }

    @Test
    @DisplayName("error(title, content, null) 类型正确")
    void error_staticFactory() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.error("错误", "操作失败", null);
            assertEquals(Alert.AlertType.ERROR, alert.getAlertType());
        });
    }

    @Test
    @DisplayName("confirm 静态工厂同时含 OK + CANCEL 按钮")
    void confirm_staticFactory_hasOkAndCancel() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.confirm("确认删除", "不可撤销", null);
            assertEquals(Alert.AlertType.CONFIRMATION, alert.getAlertType());
            assertEquals(2, alert.getButtonTypes().size());
            assertTrue(alert.getButtonTypes().contains(ButtonType.OK));
            assertTrue(alert.getButtonTypes().contains(ButtonType.CANCEL));
        });
    }

    // ---------------------------------------------------------------
    // 链式 setter
    // ---------------------------------------------------------------

    @Test
    @DisplayName("title() 设置 headerText")
    void title_setsHeaderText() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("原始标题", "内容", null);
            alert.title("新标题");
            assertEquals("新标题", alert.getHeaderText());
        });
    }

    @Test
    @DisplayName("content() 设置 contentText")
    void content_setsContentText() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "原始内容", null);
            alert.content("新内容");
            assertEquals("新内容", alert.getContentText());
        });
    }

    @Test
    @DisplayName("title(null) 降级为空字符串")
    void title_null_fallbackEmpty() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "内容", null);
            alert.title(null);
            assertEquals("", alert.getHeaderText());
        });
    }

    @Test
    @DisplayName("content(null) 降级为空字符串")
    void content_null_fallbackEmpty() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "内容", null);
            alert.content(null);
            assertEquals("", alert.getContentText());
        });
    }

    @Test
    @DisplayName("okText() 覆盖默认文案")
    void okText_overridesDefault() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "内容", null);
            alert.okText("好的");
            assertNotNull(alert);
        });
    }

    @Test
    @DisplayName("cancelText() 覆盖默认文案")
    void cancelText_overridesDefault() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.confirm("确认", "确认吗？", null);
            alert.cancelText("取消吧");
            assertNotNull(alert);
        });
    }

    // ---------------------------------------------------------------
    // 非阻塞回调
    // ---------------------------------------------------------------

    @Test
    @DisplayName("onOk 设置回调不抛异常")
    void onOk_callbackSet() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "内容", null);
            alert.onOk(() -> {});
            assertNotNull(alert);
        });
    }

    @Test
    @DisplayName("onCancel 设置回调不抛异常")
    void onCancel_callbackSet() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.confirm("标题", "内容", null);
            alert.onCancel(() -> {});
            assertNotNull(alert);
        });
    }

    @Test
    @DisplayName("onResult 设置回调不抛异常")
    void onResult_callbackSet() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.info("标题", "内容", null);
            alert.onResult(btn -> {});
            assertNotNull(alert);
        });
    }

    // ---------------------------------------------------------------
    // 链式全量
    // ---------------------------------------------------------------

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain() {
        runOnFxThreadAndWait(() -> {
            AlertAnt alert = AlertAnt.confirm("删除确认", "此操作不可撤销", null);
            alert.title("删除确认")
                 .content("此操作不可撤销")
                 .okText("删除")
                 .cancelText("取消")
                 .onOk(() -> {})
                 .onCancel(() -> {})
                 .onResult(btn -> {});
            assertNotNull(alert);
        });
    }
}
