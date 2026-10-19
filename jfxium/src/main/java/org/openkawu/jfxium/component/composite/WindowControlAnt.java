package org.openkawu.jfxium.component.composite;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.layout.AbstractHBoxAnt;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

import java.util.function.Supplier;

/**
 * WindowControlAnt —— Windows 11 / WinUI 3 风格的窗口控制按钮组（最小化 / 最大化-还原 / 关闭）。
 *
 * <p><b>定位</b>：自绘窗口外壳（{@code StageStyle.UNDECORATED} / {@code TRANSPARENT}）右侧的
 * 三个标题栏按钮。JavaFX 原生窗口只提供系统装饰，想在自绘标题栏里保留
 * 「最小化 / 最大化 / 关闭」语义，就需要这组按钮 + 与 {@link Stage} 的状态绑定。</p>
 *
 * <h2>几何与配色</h2>
 * <ul>
 *   <li>按钮 46 × {@code @window-control-button-height}（默认 32，紧凑密度 28 → 同比例宽 40），
 *       由 {@code _window-control.less} 钉死尺寸（字形按 WinUI 2048 网格绘制，见
 *       {@link IconPath#WINDOW_GLYPH_SCALE}）</li>
 *   <li>字形 10×10（最小化为 10×1），取自 WinUI 3 caption buttons 的官方路径</li>
 *   <li>颜色全部走主题语义槽：字形 {@code -color-fg-default}、悬停/按压前景色叠加、
 *       关闭悬停 {@code -color-danger-emphasis} —— 11 套主题自动适配</li>
 * </ul>
 *
 * <h2>与 WinUI 的有意差异</h2>
 * <ul>
 *   <li><b>可 Tab 聚焦</b>：WinUI 的标题栏按钮 {@code IsTabStop=False}（属系统装饰，
 *       不需要应用层键盘焦点）。自绘标题栏里它们是应用自己的按钮，若不可聚焦，
 *       键盘/读屏用户就没有任何路径触发关闭 —— 故保留 {@code focusTraversable} 并配
 *       可见焦点环（项目 a11y 规范）。</li>
 *   <li><b>关闭悬停色</b>：用主题危险色而非硬编码 {@code #C42B1C}，使 Ant 系主题得到
 *       Ant 红、Fluent 暗色得到该主题的危险色（详见 LESS 头部说明）。</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Stage stage = new Stage(StageStyle.UNDECORATED);
 * WindowControlAnt controls = WindowControlAnt.create()
 *     .target(stage)                          // 绑定后自动同步最大化状态
 *     .maximizeVisible(false)                 // 固定尺寸窗口常见做法
 *     .build();
 *
 * // 关闭按钮行为 = 标准 JavaFX 关闭语义：先发 WINDOW_CLOSE_REQUEST，
 * // 应用可在 stage.setOnCloseRequest 里 consume() 拦截（如「有未保存修改」）。
 * stage.setOnCloseRequest(e -> {
 *     if (hasUnsavedChanges() && !confirmDiscard()) e.consume();
 * });
 * }</pre>
 *
 * <p><b>线程</b>：与 JavaFX 其余控件一致，仅在 FX 应用线程使用。</p>
 */
public class WindowControlAnt extends AbstractHBoxAnt<WindowControlAnt> {

    /** 关闭请求未被消费时，直接隐藏窗口（与平台标题栏关闭按钮一致）。 */
    private static final String KEY_MINIMIZE = "a11y.minimize";
    private static final String KEY_MAXIMIZE = "a11y.maximize";
    private static final String KEY_RESTORE = "a11y.restore";
    private static final String KEY_CLOSE = "a11y.close";

    private final ButtonAnt minimizeButton;
    private final ButtonAnt maximizeButton;
    private final ButtonAnt closeButton;

    /** 最大化按钮的字形节点 —— 在「最大化 / 还原」两态间换 path。 */
    private final SVGPath maximizeGlyph;

    /** 最大化按钮的可访问名字刷新入口（文案随状态在「最大化 / 还原」之间切换）。 */
    private final Runnable maximizeNameRefresh;

    /** 监听目标 Stage 的 maximized 属性，保证外部（双击标题栏 / 系统菜单）改状态时字形同步。 */
    private final ChangeListener<Boolean> maximizedListener =
            (obs, oldValue, newValue) -> applyMaximized(Boolean.TRUE.equals(newValue));

    private Stage target;
    private boolean maximized;
    /** 是否已把「最大化视觉态」落到节点上（构造后第一次应用不受幂等短路影响）。 */
    private boolean maximizedApplied;

    /**
     * 构造按钮组。
     *
     * <p>公开构造便于业务 {@code extends WindowControlAnt}；常规入口是 {@link #create()}。</p>
     */
    public WindowControlAnt() {
        setSpacing(0);
        setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        getStyleClass().add(JfxStyles.WINDOW_CONTROL);

        minimizeButton = captionButton(IconPath.windowMinimize(), JfxStyles.WINDOW_CONTROL_MINIMIZE);
        maximizeGlyph = IconPath.windowMaximize();
        maximizeButton = captionButton(maximizeGlyph, JfxStyles.WINDOW_CONTROL_MAXIMIZE);
        closeButton = captionButton(IconPath.windowClose(), JfxStyles.WINDOW_CONTROL_CLOSE);

        maximizeNameRefresh = bindAccessibleName(maximizeButton,
                () -> maximized ? KEY_RESTORE : KEY_MAXIMIZE);
        bindAccessibleName(minimizeButton, () -> KEY_MINIMIZE);
        bindAccessibleName(closeButton, () -> KEY_CLOSE);

        minimizeButton.setOnAction(e -> {
            if (target != null) {
                target.setIconified(true);
            }
        });
        maximizeButton.setOnAction(e -> {
            if (target != null) {
                target.setMaximized(!target.isMaximized());
            }
        });
        closeButton.setOnAction(e -> requestClose());
    }

    /** 工厂入口。 */
    public static WindowControlAnt create() {
        return new WindowControlAnt();
    }

    // ============================================================
    // 配置
    // ============================================================

    /**
     * 绑定目标窗口。
     *
     * <p>绑定后：三个按钮作用于该窗口，最大化按钮的字形/类名跟随
     * {@link Stage#maximizedProperty()}（双击标题栏、系统菜单、{@code setMaximized}
     * 都会触发同步）。传 {@code null} 解绑（按钮保持可见但点击无效果）。</p>
     *
     * <p>可重复调用：旧窗口的监听会被移除，不会叠加。</p>
     */
    public WindowControlAnt target(Stage stage) {
        if (this.target == stage) {
            return this;
        }
        if (this.target != null) {
            this.target.maximizedProperty().removeListener(maximizedListener);
        }
        this.target = stage;
        if (stage != null) {
            stage.maximizedProperty().addListener(maximizedListener);
            applyMaximized(stage.isMaximized());
        } else {
            applyMaximized(false);
        }
        return this;
    }

    /** 当前绑定的窗口（可能为 {@code null}）。 */
    public Stage getTarget() {
        return target;
    }

    /** 最小化按钮显示开关（默认显示）。关闭后不占位。 */
    public WindowControlAnt minimizeVisible(boolean visible) {
        toggleNode(minimizeButton, visible);
        return this;
    }

    /** 最大化按钮显示开关（默认显示）。固定尺寸窗口建议关闭。关闭后不占位。 */
    public WindowControlAnt maximizeVisible(boolean visible) {
        toggleNode(maximizeButton, visible);
        return this;
    }

    /** 关闭按钮显示开关（默认显示）。关闭后不占位。 */
    public WindowControlAnt closeVisible(boolean visible) {
        toggleNode(closeButton, visible);
        return this;
    }

    /**
     * 直接设置「已最大化」视觉态。
     *
     * <p>绑定 {@link Stage} 后窗口是唯一事实来源，通常无需手动调用；仅用于
     * 没有真实 Stage 的自绘外壳（例如内嵌的模拟窗口）。</p>
     */
    public WindowControlAnt maximized(boolean maximized) {
        applyMaximized(maximized);
        return this;
    }

    /** 当前是否为「已最大化」视觉态。 */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * 执行标准关闭流程：向目标窗口发 {@link WindowEvent#WINDOW_CLOSE_REQUEST}，
     * 未被消费（{@link Event#isConsumed()}）时 {@code hide()}。
     *
     * <p>与平台标题栏关闭按钮语义一致 —— 应用可在
     * {@code stage.setOnCloseRequest(...)} 里 {@code e.consume()} 拦截。</p>
     */
    public void requestClose() {
        if (target == null) {
            return;
        }
        WindowEvent event = new WindowEvent(target, WindowEvent.WINDOW_CLOSE_REQUEST);
        Event.fireEvent(target, event);
        if (!event.isConsumed()) {
            target.hide();
        }
    }

    // ============================================================
    // 构建
    // ============================================================

    /** 组装三个按钮（可重复调用，不累积子节点）。 */
    @Override
    public WindowControlAnt build() {
        getChildren().setAll(minimizeButton, maximizeButton, closeButton);
        return this;
    }

    // ============================================================
    // 内部
    // ============================================================

    private ButtonAnt captionButton(SVGPath glyph, String roleStyleClass) {
        glyph.getStyleClass().add(JfxStyles.WINDOW_CONTROL_GLYPH);
        ButtonAnt button = ButtonAnt.iconOnly(glyph);
        button.getStyleClass().addAll(JfxStyles.WINDOW_CONTROL_BUTTON, roleStyleClass);
        return button;
    }

    /**
     * 绑定可访问名字 —— 最大化按钮需要在两态间切换文案，故不用
     * {@link org.openkawu.jfxium.core.util.A11y#name}（它只绑定单个固定 key），
     * 改为「key 提供者 + 一次 locale 监听」，状态与语言变化都能刷新。
     */
    private static Runnable bindAccessibleName(ButtonBase button, Supplier<String> keySupplier) {
        button.setAccessibleRole(AccessibleRole.BUTTON);
        Runnable refresh = () -> button.setAccessibleText(Messages.get(keySupplier.get()));
        refresh.run();
        Messages.localeProperty().addListener((obs, oldLocale, newLocale) -> refresh.run());
        return refresh;
    }

    private static void toggleNode(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /** 切换最大化/还原视觉态（字形 + 类名 + 可访问名字）。幂等。 */
    private void applyMaximized(boolean value) {
        if (maximizedApplied && this.maximized == value) {
            return;
        }
        maximizedApplied = true;
        this.maximized = value;
        maximizeGlyph.setContent(value ? IconPath.WINDOW_RESTORE : IconPath.WINDOW_MAXIMIZE);
        maximizeButton.getStyleClass().removeAll(
                JfxStyles.WINDOW_CONTROL_MAXIMIZE, JfxStyles.WINDOW_CONTROL_RESTORE);
        maximizeButton.getStyleClass().add(
                value ? JfxStyles.WINDOW_CONTROL_RESTORE : JfxStyles.WINDOW_CONTROL_MAXIMIZE);
        maximizeNameRefresh.run();
    }
}
