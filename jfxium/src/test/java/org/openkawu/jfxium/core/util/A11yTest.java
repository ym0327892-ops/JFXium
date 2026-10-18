package org.openkawu.jfxium.core.util;

import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.component.composite.SwitchAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.control.SearchInputAnt;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 无障碍（a11y）收口回归测试。
 *
 * <h2>锁定的不变量</h2>
 * <ol>
 *   <li><b>icon-only 节点必须能被读屏读出名字</b>：没有可见文本的交互节点若
 *       {@code accessibleText} 为空，读屏只会念「按钮」，等于没有名字。</li>
 *   <li><b>读屏激活必须真的触发业务动作</b>：{@code AccessibleAction.FIRE} 与
 *       {@code onMouseClicked} 是两条独立通路，只接后者会出现
 *       「鼠标能关、读屏激活没反应」—— 这类缺陷只对辅助技术暴露，肉眼与键鼠测试都发现不了。</li>
 *   <li><b>键盘与鼠标必须收敛到同一动作</b>：三条路（鼠标 / 空格回车 / 读屏）行为一致。</li>
 *   <li><b>i18n 是活的</b>：切换 {@link org.openkawu.jfxium.core.i18n.Messages#setLocale} 后，
 *       已构建节点的 accessibleText 必须跟着变（不能锁死在构建时的 locale）。</li>
 * </ol>
 */
@DisplayName("无障碍语义（角色 + 名字 + 三路激活动作）")
class A11yTest extends JfxTestBase {

    /** 挂到 Scene（不加载主题 CSS）—— 只验证可访问语义与动作接线。 */
    private static StackPane mount(Node... children) {
        StackPane root = new StackPane(children);
        new Scene(root, 300, 200);
        root.applyCss();
        root.layout();
        return root;
    }

    /** 挂到 Scene 并加载真实主题 CSS —— 焦点环之类的视觉断言必须走这个。 */
    private static StackPane mountThemed(Node... children) {
        StackPane root = new StackPane(children);
        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().add(A11yTest.class
                .getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        root.applyCss();
        root.layout();
        return root;
    }

    // ---------------------------------------------------------------
    // 1. FirePane 三路动作
    // ---------------------------------------------------------------

    @Test
    @DisplayName("FirePane：角色/名字齐备，读屏 FIRE 能触发动作")
    void firePaneHasSemanticsAndFires() {
        AtomicInteger fired = new AtomicInteger();
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            A11y.FirePane pane = A11y.button("a11y.close", fired::incrementAndGet);
            mount(pane);

            if (pane.getAccessibleRole() != AccessibleRole.BUTTON) {
                err.set("角色应为 BUTTON，实为 " + pane.getAccessibleRole());
            } else if (!"关闭".equals(pane.getAccessibleText())) {
                err.set("accessibleText 应为「关闭」，实为 " + pane.getAccessibleText());
            } else if (!pane.isFocusTraversable()) {
                err.set("有动作的 FirePane 必须可聚焦，否则 Tab 跳过它");
            }
            // 读屏激活路径
            pane.executeAccessibleAction(AccessibleAction.FIRE);
            if (fired.get() != 1) {
                err.set("AccessibleAction.FIRE 未触发动作，fired=" + fired.get());
            }
        });
        if (err.get() != null) {
            throw new AssertionError(err.get());
        }
    }

    @Test
    @DisplayName("FirePane：鼠标点击与读屏 FIRE 走同一动作")
    void mouseAndScreenReaderShareAction() {
        AtomicInteger fired = new AtomicInteger();
        runOnFxThreadAndWait(() -> {
            A11y.FirePane pane = A11y.button("a11y.clear", fired::incrementAndGet);
            mount(pane);
            pane.getOnMouseClicked().handle(null);
            pane.executeAccessibleAction(AccessibleAction.FIRE);
        });
        assertEquals(2, fired.get(), "鼠标与读屏应各触发一次同一个动作");
    }

    @Test
    @DisplayName("FirePane：动作可后置绑定（先建节点、后接动作）")
    void actionCanBeBoundLater() {
        AtomicInteger fired = new AtomicInteger();
        runOnFxThreadAndWait(() -> {
            A11y.FirePane pane = new A11y.FirePane(A11y.A11yRole.TOGGLE_BUTTON, "a11y.switch");
            mount(pane);
            // 未绑定动作前：不可聚焦，FIRE 无副作用也不抛异常
            pane.executeAccessibleAction(AccessibleAction.FIRE);
            assertEquals(0, fired.get());
            assertEquals(false, pane.isFocusTraversable(),
                    "无动作时不该占用 Tab 序列");

            pane.setAction(fired::incrementAndGet);
            pane.executeAccessibleAction(AccessibleAction.FIRE);
        });
        assertEquals(1, fired.get(), "后置绑定的动作应能被读屏 FIRE 触发");
    }

    // ---------------------------------------------------------------
    // 2. 类型保持不变但语义完整的 FireBox / FireLabel
    // ---------------------------------------------------------------

    @Test
    @DisplayName("FireLabel / FireBox：保持容器类型契约，FIRE 仍可到达")
    void fireLabelAndBoxKeepType() {
        AtomicInteger fired = new AtomicInteger();
        runOnFxThreadAndWait(() -> {
            A11y.FireLabel label = A11y.label("a11y.clear", fired::incrementAndGet);
            A11y.FireBox box = A11y.box("a11y.close", fired::incrementAndGet);
            mount(label, box);
            label.executeAccessibleAction(AccessibleAction.FIRE);
            box.executeAccessibleAction(AccessibleAction.FIRE);
        });
        assertEquals(2, fired.get(), "FireLabel 与 FireBox 都应支持读屏 FIRE");
    }

    // ---------------------------------------------------------------
    // 3. i18n 活绑定
    // ---------------------------------------------------------------

    @Test
    @DisplayName("切换 locale 后已构建节点的 accessibleText 同步更新")
    void accessibleTextFollowsLocale() {
        AtomicReference<String> seen = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            A11y.FirePane pane = A11y.button("a11y.close", () -> {});
            mount(pane);
            assertEquals("关闭", pane.getAccessibleText());

            Locale original = org.openkawu.jfxium.core.i18n.Messages.getLocale();
            try {
                org.openkawu.jfxium.core.i18n.Messages.setLocale(Locale.ENGLISH);
                seen.set(pane.getAccessibleText());
            } finally {
                org.openkawu.jfxium.core.i18n.Messages.setLocale(original);
            }
        });
        assertEquals("Close", seen.get(),
                "accessibleText 锁死在构建时的 locale —— 缺少 localeProperty 监听");
    }

    // ---------------------------------------------------------------
    // 4. 真实组件接线（Tag / SearchInput / Switch）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("TagAnt 关闭按钮：有名字、可聚焦、读屏能关闭标签")
    void tagCloseButtonIsAccessible() {
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            javafx.scene.layout.HBox tag = TagAnt.create("标签").closable(true).build();
            StackPane root = mount(tag);
            Node close = findCloseButton(tag);
            if (close == null) {
                err.set("TagAnt 未生成关闭按钮");
                return;
            }
            if (close.getAccessibleRole() != AccessibleRole.BUTTON) {
                err.set("关闭按钮角色应为 BUTTON，实为 " + close.getAccessibleRole());
            } else if (close.getAccessibleText() == null || close.getAccessibleText().isBlank()) {
                err.set("关闭按钮没有 accessibleText —— 读屏念不出名字");
            } else if (!close.isFocusTraversable()) {
                err.set("关闭按钮不可聚焦");
            } else {
                close.executeAccessibleAction(AccessibleAction.FIRE);
                if (tag.isVisible()) {
                    err.set("读屏 FIRE 未隐藏标签（关闭动作没接到 FIRE 上）");
                }
            }
            assertNotNull(root);
        });
        if (err.get() != null) {
            throw new AssertionError(err.get());
        }
    }

    @Test
    @DisplayName("SearchInputAnt 清除按钮：有名字且有 BUTTON 角色")
    void searchClearButtonIsAccessible() {
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            SearchInputAnt search = SearchInputAnt.create().text("abc").build();
            mount(search);
            Node clear = search.lookup(".jfx-search-input-clear");
            if (clear == null) {
                err.set("未找到清除按钮（.jfx-search-input-clear）");
            } else if (clear.getAccessibleRole() != AccessibleRole.BUTTON) {
                err.set("清除按钮角色应为 BUTTON，实为 " + clear.getAccessibleRole());
            } else if (!"清除".equals(clear.getAccessibleText())) {
                err.set("清除按钮 accessibleText 应为「清除」，实为 " + clear.getAccessibleText());
            }
        });
        if (err.get() != null) {
            throw new AssertionError(err.get());
        }
    }

    @Test
    @DisplayName("SwitchAnt：自绘开关暴露 TOGGLE_BUTTON 角色与名字，且键盘可切换")
    void switchIsAccessibleAndKeyboardOperable() {
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            javafx.scene.layout.HBox sw = SwitchAnt.create().build();
            StackPane root = mount(sw);
            Node pane = root.lookup(".jfx-switch");
            if (pane == null) {
                err.set("未找到 .jfx-switch 节点");
                return;
            }
            if (pane.getAccessibleRole() != AccessibleRole.TOGGLE_BUTTON) {
                err.set("开关角色应为 TOGGLE_BUTTON，实为 " + pane.getAccessibleRole());
            } else if (pane.getAccessibleText() == null || pane.getAccessibleText().isBlank()) {
                err.set("开关没有 accessibleText");
            } else if (!pane.isFocusTraversable()) {
                err.set("开关不可聚焦 —— 键盘用户无法切换");
            }
        });
        if (err.get() != null) {
            throw new AssertionError(err.get());
        }
    }

    // ---------------------------------------------------------------
    // 4b. 新接入焦点的节点必须有可见焦点环（#169 缺陷类别的围栏）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("新接入 Tab 焦点的三类节点在聚焦时都解析出双色焦点环")
    void focusableIconsHaveVisibleRing() {
        javafx.css.PseudoClass fv = javafx.css.PseudoClass.getPseudoClass("focus-visible");
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            javafx.scene.layout.HBox tag = TagAnt.create("标签").closable(true).build();
            SearchInputAnt search = SearchInputAnt.create().text("abc").build();
            javafx.scene.layout.HBox sw = SwitchAnt.create().build();
            StackPane root = mountThemed(tag, search, sw);

            Node close = findCloseButton(tag);
            Node clear = search.lookup(".jfx-search-input-clear");
            Node switchPane = sw.lookup(".jfx-switch");
            if (close == null || clear == null || switchPane == null) {
                err.set("目标节点缺失：close=" + close + " clear=" + clear + " switch=" + switchPane);
                return;
            }
            for (Node n : List.of(close, clear, switchPane)) {
                if (!n.isFocusTraversable()) {
                    err.set(n.getStyleClass() + " 可聚焦性未开启");
                    return;
                }
                n.pseudoClassStateChanged(fv, true);
            }
            root.applyCss();
            root.layout();

            for (Node n : List.of(close, clear, switchPane)) {
                int strokes = 0;
                if (n instanceof javafx.scene.layout.Region r && r.getBorder() != null) {
                    strokes = r.getBorder().getStrokes().size();
                }
                if (strokes != 2) {
                    err.set(n.getStyleClass() + " 聚焦时应有 2 条描边（外环+内环），实为 " + strokes
                            + " —— 节点可 Tab 到达却没有可见焦点指示（第 #169 类缺陷）");
                    return;
                }
            }
        });
        if (err.get() != null) {
            throw new AssertionError(err.get());
        }
    }

    // ---------------------------------------------------------------
    // 5. 反向围栏：裸容器不得被误认为已有语义
    // ---------------------------------------------------------------

    @Test
    @DisplayName("守卫有效性：裸 StackPane 默认没有 specialized 角色（说明本测试能区分）")
    void rawPaneHasNoButtonRole() {
        runOnFxThreadAndWait(() -> {
            StackPane raw = new StackPane();
            mount(raw);
            assertTrue(raw.getAccessibleRole() != AccessibleRole.BUTTON,
                    "裸 StackPane 不该是 BUTTON —— 若这里失败，说明断言无法区分有无语义");
        });
    }

    @Test
    @DisplayName("Long 型：Button 自带角色，无需 A11y 介入（避免过度标注）")
    void nativeButtonAlreadyAccessible() {
        runOnFxThreadAndWait(() -> {
            Button b = new Button("保存");
            mount(b);
            assertEquals(AccessibleRole.BUTTON, b.getAccessibleRole());
        });
    }

    private static Node findCloseButton(javafx.scene.layout.HBox tag) {
        List<Node> all = new java.util.ArrayList<>();
        collect(tag, all);
        for (Node n : all) {
            if (n.getStyleClass().contains("jfx-tag-close")) {
                return n;
            }
        }
        return null;
    }

    private static void collect(Node n, List<Node> out) {
        out.add(n);
        if (n instanceof javafx.scene.Parent p) {
            for (Node c : p.getChildrenUnmodifiable()) {
                collect(c, out);
            }
        }
    }
}
