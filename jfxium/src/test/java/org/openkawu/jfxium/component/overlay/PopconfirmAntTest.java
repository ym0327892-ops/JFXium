package org.openkawu.jfxium.component.overlay;

import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PopconfirmAnt 单元测试 —— 覆盖 Builder 链式 / target=null 时 show() no-op / hide() 幂等。
 *
 * <p>overlay 组件依赖 Stage/Popup 做实际渲染,本测试聚焦 <b>Builder 配置 + 运行时守卫</b>
 * (如 show() 在 target=null 时静默退出),不做端到端弹窗行为(那是 acceptance 测试范畴)。</p>
 */
@DisplayName("PopconfirmAnt")
class PopconfirmAntTest extends JfxTestBase {

    @Test
    @DisplayName("create().build() 返回非空 Popconfirm")
    void build_basic() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定删除？")
                .description("删除后无法恢复")
                .build();
        assertNotNull(p);
    }

    @Test
    @DisplayName("title/description 可空(默认空串)")
    void build_emptyStrings() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create().build();
        assertNotNull(p);
    }

    @Test
    @DisplayName("show() 在 target=null 时静默 no-op,不抛异常")
    void show_nullTarget_noop() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .build();
        // 没有 target,show() 应该直接 return,不应该 NPE
        assertDoesNotThrow(p::show);
    }

    @Test
    @DisplayName("hide() 在未 show 时幂等 no-op")
    void hide_beforeShow_noop() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .build();
        // 内部 popup 仍为 null,hide() 应当幂等
        assertDoesNotThrow(p::hide);
    }

    @Test
    @DisplayName("多次 show()/hide() 序列不抛异常(null target 场景)")
    void showHide_sequence() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .build();
        assertDoesNotThrow(() -> {
            p.show();
            p.hide();
            p.show();
            p.hide();
        });
    }

    @Test
    @DisplayName("target(Node) 设置后不抛异常(只测 builder 行为,不实际弹窗)")
    void target_set() {
        Label target = new Label("target");
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .target(target)
                .build();
        assertNotNull(p);
        // 不调用 show(),因为 target 未添加到 Scene,localToScreen 会 NPE
    }

    @Test
    @DisplayName("onConfirm/onCancel 回调注册不抛异常")
    void callbacks_registered() {
        AtomicBoolean confirmCalled = new AtomicBoolean(false);
        AtomicBoolean cancelCalled = new AtomicBoolean(false);
        AtomicReference<Boolean> lastConfirm = new AtomicReference<>();
        AtomicReference<Boolean> lastCancel = new AtomicReference<>();

        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .onConfirm(ok -> {
                    confirmCalled.set(true);
                    lastConfirm.set(ok);
                })
                .onCancel(cancel -> {
                    cancelCalled.set(true);
                    lastCancel.set(cancel);
                })
                .build();
        assertNotNull(p);
        // 回调内部状态:此处只能验证 builder 不抛异常,不验证触发(show 涉及 Stage 跳过)
        assertFalse(confirmCalled.get());
        assertFalse(cancelCalled.get());
    }

    @Test
    @DisplayName("okText/cancelText null = 用 i18n 默认值(null target show 不弹窗)")
    void okCancelText_default() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .okText(null)   // null = 走 i18n 默认值
                .cancelText(null)
                .build();
        assertNotNull(p);
        assertDoesNotThrow(p::show);
    }

    @Test
    @DisplayName("okText/cancelText 自定义文本")
    void okCancelText_custom() {
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("确定？")
                .okText("确认")
                .cancelText("再想想")
                .build();
        assertNotNull(p);
    }

    @Test
    @DisplayName("全链式串联:title/description/ok/cancel/onConfirm/onCancel/target")
    void fullChain() {
        Label target = new Label("trigger");
        PopconfirmAnt.Popconfirm p = PopconfirmAnt.create()
                .title("提交审批？")
                .description("提交后不可修改")
                .okText("提交")
                .cancelText("再想想")
                .onConfirm(ok -> {})
                .onCancel(cancel -> {})
                .target(target)
                .build();
        assertNotNull(p);
    }
}
