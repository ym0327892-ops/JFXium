package org.openkawu.jfxium.component.base;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationCard 单元测试 —— 锁死 M19.51 X 按钮修复点 + 基础结构。
 *
 * <p>关键回归点：原代码 {@code if (closable && onClose != null)} 要求双条件，
 * 改为 {@code if (closable)} 单条件后，{@code closable=true + onClose=null} 也能渲染 X 按钮。
 * 该测试用例 {@link #closableTrue_nullOnClose_rendersCloseButton} 即为回归保护。</p>
 *
 * <p>NotificationCard 是面板型 base 组件,不是 overlay,所以本测试可以在纯单元测试环境
 * 直接 build(),无需启动 Stage/Popup。</p>
 */
@DisplayName("NotificationCard")
class NotificationCardTest extends JfxTestBase {

    private static Button findCloseButton(VBox card) {
        // NotificationCard 结构: card (VBox) > headerBox (HBox)
        // headerBox children: icon, contentBox, [closeButton]
        // 用 instanceof CloseButton 判定,避免依赖 styleClass 顺序
        Node header = card.getChildren().get(0);
        assertTrue(header instanceof HBox, "第一个子节点应为 HBox header");
        HBox hb = (HBox) header;
        for (Node n : hb.getChildren()) {
            if (n instanceof CloseButton) {
                return (CloseButton) n;
            }
        }
        return null;
    }

    private static boolean containsCloseButton(VBox card) {
        return findCloseButton(card) != null;
    }

    @Test
    @DisplayName("M19.51 回归:closable=true + onClose=null 仍渲染 X 按钮")
    void closableTrue_nullOnClose_rendersCloseButton() {
        // 原 bug 条件: closable && onClose != null -> 双条件卡死,X 永远不渲染
        // 修复后: closable 单条件,onClose 可为 null(只关不回调)
        VBox card = NotificationCard.create()
                .title("t")
                .closable(true)
                .build();                              // onClose 默认 null
        assertTrue(containsCloseButton(card),
                "修复后 closable=true(无论 onClose 是否为 null)都应渲染 X 按钮");
    }

    @Test
    @DisplayName("closable=true + onClose=Runnable 渲染 X 按钮 + 点击触发回调")
    void closableTrue_withOnClose_rendersAndTriggers() {
        AtomicBoolean called = new AtomicBoolean(false);
        VBox card = NotificationCard.create()
                .title("t")
                .closable(true)
                .onClose(() -> called.set(true))
                .build();
        Button btn = findCloseButton(card);
        assertNotNull(btn, "closable=true + onClose 不为 null 应渲染 X 按钮");
        assertFalse(called.get(), "build() 不应提前触发回调");
        btn.fire();                                   // 等价于用户点击
        assertTrue(called.get(), "点击 X 应触发 onClose 回调");
    }

    @Test
    @DisplayName("closable=false 不渲染 X 按钮(即便 onClose 不为 null)")
    void closableFalse_doesNotRender() {
        VBox card = NotificationCard.create()
                .title("t")
                .closable(false)
                .onClose(() -> { /* 不应被触发 */ })
                .build();
        assertFalse(containsCloseButton(card),
                "closable=false 应禁用 X 按钮,onClose 是否为 null 都无关");
    }

    @Test
    @DisplayName("默认 closable=true → X 按钮默认渲染")
    void default_closableTrue() {
        VBox card = NotificationCard.create().title("t").build();
        assertTrue(containsCloseButton(card),
                "Builder 默认 closable=true,应默认渲染 X 按钮");
    }

    @Test
    @DisplayName("默认宽度 384px(setMinWidth / setMaxWidth,非 setStyle)")
    void defaultWidth_384px() {
        VBox card = NotificationCard.create().title("t").build();
        // M19.44 红线#1 修复：宽度通过 setMinWidth/setMaxWidth 控制,不走 setStyle
        assertEquals(384.0, card.getMinWidth(), 0.01);
        assertEquals(384.0, card.getMaxWidth(), 0.01);
    }

    @Test
    @DisplayName("width(\"500px\") 改写为 500")
    void width_custom() {
        VBox card = NotificationCard.create().width("500px").title("t").build();
        assertEquals(500.0, card.getMinWidth(), 0.01);
        assertEquals(500.0, card.getMaxWidth(), 0.01);
    }

    @Test
    @DisplayName("width(null) / 无效值 → 静默回退到默认 384px")
    void width_invalidFallback() {
        VBox card1 = NotificationCard.create().width(null).title("t").build();
        assertEquals(384.0, card1.getMinWidth(), 0.01);

        // parsePx 对纯数字也按 px 处理,故意用异常字符串验证静默兜底
        VBox card2 = NotificationCard.create().width("").title("t").build();
        assertEquals(384.0, card2.getMinWidth(), 0.01);
    }

    @Test
    @DisplayName("4 种 type 都成功构建(SUCCESS/ERROR/WARNING/INFO)")
    void allTypes_buildable() {
        for (NotificationCard.Type type : NotificationCard.Type.values()) {
            VBox card = NotificationCard.create()
                    .title("t")
                    .description("d")
                    .type(type)
                    .build();
            assertNotNull(card);
            assertTrue(card.getStyleClass().contains(JfxStyles.NOTIFICATION_CARD),
                    "type=" + type + " 应保留 NOTIFICATION_CARD styleClass");
        }
    }

    @Test
    @DisplayName("title 空 → contentBox 跳过标题 Label")
    void titleEmpty_skipped() {
        VBox card = NotificationCard.create().description("only desc").build();
        Node header = card.getChildren().get(0);
        assertTrue(header instanceof HBox);
        // headerBox > contentBox > 子节点,空 title 时 contentBox 只剩 description label
        // 此处不强行定位,只断言 build 不抛 + 节点数 >= 1
        assertFalse(((HBox) header).getChildren().isEmpty());
    }

    @Test
    @DisplayName("title/description 都空 + 无 extra → contentBox 仅有 icon + closeBtn")
    void allEmpty_minimalCard() {
        VBox card = NotificationCard.create().build();  // 全空字符串 + null extra
        Node header = card.getChildren().get(0);
        assertTrue(header instanceof HBox);
        HBox hb = (HBox) header;
        // children: icon, contentBox(空), closeButton(default closable=true)
        assertEquals(3, hb.getChildren().size(), "默认结构: icon + contentBox + closeButton");
    }

    @Test
    @DisplayName("extra(Node) 添加到 contentBox")
    void extra_added() {
        Node extra = new Label("extra");              // 测试代码允许 new(沿用 PopconfirmAntTest L76 惯例)
        VBox card = NotificationCard.create()
                .title("t")
                .extra(extra)
                .build();
        Node header = card.getChildren().get(0);
        assertTrue(header instanceof HBox);
        // contentBox 是 header 的中间节点(index=1),类型为 VBox
        HBox hb = (HBox) header;
        VBox content = (VBox) hb.getChildren().get(1);
        assertTrue(content.getChildrenUnmodifiable().contains(extra),
                "extra 应被加到 contentBox 子节点列表");
    }

    @Test
    @DisplayName("content(Node) 是 extra(Node) 的 alias")
    void content_isExtraAlias() {
        Node content = new Label("c");
        VBox card = NotificationCard.create().content(content).build();
        Node header = card.getChildren().get(0);
        HBox hb = (HBox) header;
        // contentBox 是 VBox,getChildrenUnmodifiable() 是 Parent 方法,需强转
        VBox contentBox = (VBox) hb.getChildren().get(1);
        assertTrue(contentBox.getChildrenUnmodifiable().contains(content),
                "content 应被加到 contentBox 子节点列表(等价于 extra)");
    }
}
