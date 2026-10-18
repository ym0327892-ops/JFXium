package org.openkawu.jfxium.core.util;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.core.i18n.Messages;

/**
 * 无障碍（a11y）收口 —— 给「自绘 / icon-only 交互节点」补齐屏幕阅读器语义。
 *
 * <h2>为什么需要这个类</h2>
 * <p>JavaFX 原生控件（{@code Button}、{@code CheckBox}、{@code TextField} …）自带
 * {@link AccessibleRole} 与 {@code accessibleText}，屏幕阅读器开箱可用。但 JFXium 里
 * 大量交互面是<b>自绘容器</b>：{@code StackPane} / {@code HBox} / 图标节点挂
 * {@code setOnMouseClicked}。它们对辅助技术是「沉默的矩形」—— 读不出名字、读不出角色、
 * 也无从激活。</p>
 *
 * <p>典型漏洞：某图标按钮同时支持鼠标点击与空格/回车（<b>键盘可达</b>），但没设 role 与
 * accessibleText —— 屏幕阅读器只念「区域」，且激活时无响应。<b>键盘可达 ≠ 读屏可达</b>：
 * 前者靠 {@code focusTraversable} + {@code setOnKeyPressed}，后者要靠
 * {@code AccessibleRole} + {@code AccessibleAction#FIRE}。本类把三件事绑成一次调用，
 * 杜绝「修了一半」。</p>
 *
 * <h2>三要素</h2>
 * <ol>
 *   <li><b>角色</b>：{@link A11yRole}。没有角色，辅助技术不知道能干什么。</li>
 *   <li><b>名字</b>：{@code accessibleText}。icon-only 节点没有可见文本，必须显式提供。
 *       只用 {@code accessibleRoleDescription} 是常见错误 —— 那读的是「这是什么控件」，
 *       不是「这个控件叫什么」，读屏会念成「按钮，按钮」。</li>
 *   <li><b>激活动作</b>：{@link AccessibleAction#FIRE}。读屏的「激活」走它，不走
 *       {@code onMouseClicked}。JavaFX 原生 {@code ButtonBase} 正是实现
 *       {@code executeAccessibleAction} 转发到 {@code fire()}。</li>
 * </ol>
 *
 * <h2>为什么需要 {@link FirePane}</h2>
 * <p>{@link AccessibleAction} 是<b>枚举而非 EventType</b>，无法 {@code addEventHandler}；
 * 而 {@code executeAccessibleAction} 是 {@link Node} 的普通方法，普通 {@code StackPane}
 * 无法就地覆盖。因此第 3 要素只能靠一个覆写了该方法的容器承载 —— 即
 * {@link FirePane}。项目自建的图标按钮（标签关闭、搜索清除、回到顶部）都是
 * {@code StackPane}，换成 {@link FirePane} 是 drop-in。</p>
 *
 * <h2>i18n 契约</h2>
 * <p>传入 message key 而非字面量：文案在<b>调用时</b>解析（不是类加载时，避免锁死 locale），
 * 并随 {@link Messages#localeProperty()} 变化自动重解析 —— 与项目既有的
 * 「懒解析 + 监听 locale」范式一致（见 {@code TreeSelectAnt}）。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // icon-only 按钮：语义 + 动作 + 键盘 + FIRE 一次到位
 * A11y.FirePane close = A11y.button("a11y.close", this::hide);
 * close.getChildren().add(IconAnt.symbol(IconAnt.Symbol.CLOSE, 12));
 *
 * // 只需能被读出名字（节点已有自己的交互实现）
 * A11y.name(node, A11yRole.BUTTON, "a11y.search");
 * }</pre>
 */
public final class A11y {

    /** icon-only 节点常用的角色（限定集合，避免调用方随手传不合适的角色）。 */
    public enum A11yRole {
        BUTTON(AccessibleRole.BUTTON),
        TOGGLE_BUTTON(AccessibleRole.TOGGLE_BUTTON),
        CHECK_BOX(AccessibleRole.CHECK_BOX),
        TEXT_FIELD(AccessibleRole.TEXT_FIELD),
        HYPERLINK(AccessibleRole.HYPERLINK);

        private final AccessibleRole role;

        A11yRole(AccessibleRole role) {
            this.role = role;
        }

        public AccessibleRole role() {
            return role;
        }
    }

    private A11y() {
        // 工具类禁止实例化
    }

    /**
     * 带 {@link AccessibleAction#FIRE} 支持的容器 —— 供 icon-only 交互节点使用。
     *
     * <p>除覆写 {@code executeAccessibleAction} 外，同时把「空格 / 回车」与
     * 「鼠标点击」收敛到同一个 {@link Runnable}，保证三条输入路径行为一致；
     * 否则会出现「鼠标能触发、读屏激活没反应」这种只对辅助技术暴露的跛脚行为。</p>
     */
    public static class FirePane extends StackPaneAnt {

        /**
         * 可空的动作槽 —— 允许「先建节点、稍后绑定动作」。
         *
         * <p>需要这个是因为自绘开关的翻转逻辑依赖组件自己的 {@code valueProperty}，
         * 而该属性在容器建好之后才创建。若构造时就要求动作非空，调用方只能先塞一个
         * 占位 lambda 再替换 —— 那会同时把鼠标/键盘处理器也写两遍，
         * 三路（鼠标/键盘/读屏）就又有分叉的机会。这里改为动作统一存储、
         * 处理器只装一次并读取当前值。</p>
         */
        private Runnable action;

        public FirePane(A11yRole role, String messageKey) {
            getStyleClass().add("jfx-a11y-fire-pane");
            setAlignment(Pos.CENTER);
            bindSemantics(this, role, messageKey);
        }

        public FirePane(A11yRole role, String messageKey, Runnable action) {
            this(role, messageKey);
            setAction(action);
        }

        /**
         * 绑定/替换激活动作 —— 鼠标点击、键盘空格回车、读屏 FIRE 三条路都读同一个槽，
         * 因此只在这里更新一次即可，不存在三处分叉。
         */
        public FirePane setAction(Runnable action) {
            this.action = action;
            setFocusTraversable(action != null);
            setOnMouseClicked(action == null ? null : e -> run());
            setOnKeyPressed(action == null ? null : e -> {
                if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                    run();
                    e.consume();
                }
            });
            return this;
        }

        private void run() {
            if (action != null) {
                action.run();
            }
        }

        @Override
        public void executeAccessibleAction(AccessibleAction accessibleAction, Object... parameters) {
            // 读屏的「激活」到此 —— 必须与鼠标/键盘走同一条路。
            if (accessibleAction == AccessibleAction.FIRE) {
                run();
                return;
            }
            super.executeAccessibleAction(accessibleAction, parameters);
        }
    }

    /** 建一个 icon-only 按钮容器（角色 BUTTON + 名字 + 动作 + 键盘 + FIRE）。 */
    public static FirePane button(String messageKey, Runnable action) {
        return new FirePane(A11yRole.BUTTON, messageKey, action);
    }

    /** 建一个 icon-only 开关容器（角色 TOGGLE_BUTTON + 名字 + 动作 + 键盘 + FIRE）。 */
    public static FirePane toggle(String messageKey, Runnable action) {
        return new FirePane(A11yRole.TOGGLE_BUTTON, messageKey, action);
    }

    /**
     * 覆写了 {@link Node#executeAccessibleAction} 的 {@link HBoxAnt} —— 读屏 FIRE 可到达。
     *
     * <p>部分组件的图标按钮是 {@code HBox} 类型，无法换成 {@link FirePane}。
     * 本类在保持调用方原有类型契约的前提下补齐三路动作。
     * 兼容「先建节点、稍后绑定动作」（见 {@link #setAction}）。</p>
     */
    public static class FireBox extends HBoxAnt {

        private Runnable action;

        public FireBox(A11yRole role, String messageKey, Runnable action) {
            bindSemantics(this, role, messageKey);
            setAction(action);
        }

        /** 绑定/替换动作 —— 三路（鼠标/键盘/读屏）都读同一个槽，只在此更新一次。 */
        public FireBox setAction(Runnable newAction) {
            this.action = newAction;
            setFocusTraversable(newAction != null);
            setOnMouseClicked(newAction == null ? null : e -> run());
            setOnKeyPressed(newAction == null ? null : e -> {
                if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                    run();
                    e.consume();
                }
            });
            return this;
        }

        private void run() {
            if (action != null) {
                action.run();
            }
        }

        @Override
        public void executeAccessibleAction(AccessibleAction accessibleAction, Object... parameters) {
            if (accessibleAction == AccessibleAction.FIRE) {
                run();
                return;
            }
            super.executeAccessibleAction(accessibleAction, parameters);
        }
    }

    /**
     * 覆写了 {@link Node#executeAccessibleAction} 的 {@link LabelAnt} —— 读屏 FIRE 可到达
     * （用 {@code Label} + {@code IconAnt} 图形作图标按钮的组件适用）。见 {@link FireBox}。
     */
    public static class FireLabel extends LabelAnt {

        private Runnable action;

        public FireLabel(A11yRole role, String messageKey, Runnable action) {
            bindSemantics(this, role, messageKey);
            setAction(action);
        }

        public FireLabel setAction(Runnable newAction) {
            this.action = newAction;
            setFocusTraversable(newAction != null);
            setOnMouseClicked(newAction == null ? null : e -> run());
            setOnKeyPressed(newAction == null ? null : e -> {
                if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                    run();
                    e.consume();
                }
            });
            return this;
        }

        private void run() {
            if (action != null) {
                action.run();
            }
        }

        @Override
        public void executeAccessibleAction(AccessibleAction accessibleAction, Object... parameters) {
            if (accessibleAction == AccessibleAction.FIRE) {
                run();
                return;
            }
            super.executeAccessibleAction(accessibleAction, parameters);
        }
    }

    /** 建一个 icon-only 的 {@link FireBox}（角色 BUTTON）。 */
    public static FireBox box(String messageKey, Runnable action) {
        return new FireBox(A11yRole.BUTTON, messageKey, action);
    }

    /** 建一个 icon-only 的 {@link FireLabel}（角色 BUTTON）。 */
    public static FireLabel label(String messageKey, Runnable action) {
        return new FireLabel(A11yRole.BUTTON, messageKey, action);
    }

    /**
     * 给已有节点补「能被读出的角色 + 名字」（不接管焦点与动作）。
     *
     * <p>适用场景：节点已有自己的交互/焦点实现，只缺可访问语义；或节点是纯展示型，
     * 不该被 Tab 到。</p>
     */
    public static Node name(Node node, A11yRole role, String messageKey) {
        if (node == null) {
            return null;
        }
        bindSemantics(node, role, messageKey);
        return node;
    }

    /**
     * 让自绘开关的状态变化通知读屏。
     *
     * <p>开关有了角色与名字后，读屏仍只会念「开关」，不说「已打开 / 已关闭」——
     * 状态要靠平台读取 {@code SELECTED} 属性，而自绘节点不会自动发变更通知。
     * 这里订阅布尔来源并在<b>均衡状态</b>（false→true / true→false）时通知一次；
     * {@code SELECTED} 属性本身由节点（或读屏查询时的上下文）提供。</p>
     *
     * @param node     目标节点（角色应为 {@link A11yRole#TOGGLE_BUTTON} / {@code CHECK_BOX}）
     * @param selected 布尔来源（通常是组件 valueProperty）
     */
    public static void bindSelected(Node node, ObservableValue<Boolean> selected) {
        if (node == null || selected == null) {
            return;
        }
        selected.addListener((obs, oldValue, newValue) -> {
            // 仅在真正跨过 true/false 边界时通知，避免重复赋值也触发读屏播报。
            if (oldValue == null || newValue == null || oldValue.booleanValue() != newValue.booleanValue()) {
                node.notifyAccessibleAttributeChanged(javafx.scene.AccessibleAttribute.SELECTED);
            }
        });
    }

    /** 便于测试与调试：读取某节点的可访问名字。 */
    public static String nameOf(Node node) {
        return node == null ? null : node.getAccessibleText();
    }

    /** 便于测试与调试：读取某节点的可访问角色。 */
    public static AccessibleRole roleOf(Node node) {
        return node == null ? null : node.getAccessibleRole();
    }

    // ------------------------------------------------------------------
    // 内部：角色 + 名字（i18n 活绑定）
    // ------------------------------------------------------------------

    private static void bindSemantics(Node node, A11yRole role, String messageKey) {
        node.setAccessibleRole((role != null ? role : A11yRole.BUTTON).role());

        String key = (messageKey == null || messageKey.isBlank()) ? "" : messageKey;
        // 立即解析一次：否则在第一次 locale 变更前 accessibleText 一直是 null，
        // 「打开即朗读」场景下读屏读不到名字。
        node.setAccessibleText(Messages.get(key));
        Messages.localeProperty().addListener((obs, oldLocale, newLocale) ->
                node.setAccessibleText(Messages.get(key)));
    }
}
