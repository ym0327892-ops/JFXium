package org.openkawu.jfxium.component.overlay;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Window;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.token.FontTokens;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * JFXium 模态弹窗组件 - 对标 JavaFX 原生 {@link Alert}，套用 JFXium 主题。
 *
 * <p><b>定位</b>：独立 Stage 模态对话框。调用方可自由选择两种风格：
 * <ul>
 *   <li><b>阻塞</b>：{@code showAndWait()} / {@code showAndWaitForOk()} —— 等待用户响应后返回</li>
 *   <li><b>非阻塞</b>：{@code .show()} + {@code onOk/onCancel/onResult} 回调 —— 弹窗立即返回，结果异步回调</li>
 * </ul>
 * 与 {@link NotificationAnt}（右下角自动消失通知）和 {@link MessageAnt}（顶部短暂提示）严格区分。</p>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li><b>继承</b>：{@code extends Alert} — 复用 JavaFX 原生 Alert 的 Dialog/Stage 生命周期</li>
 *   <li><b>主题</b>：dialog-pane 挂 {@code .jfx-alert-dialog} 根 class，LESS 端按 4 种 AlertType 切换配色</li>
 *   <li><b>图形</b>：默认用 Ikonli 字符图标代替 AntLantaFx 的 PNG 图标（4 个 type 各一个）</li>
 *   <li><b>owner</b>：构造时绑定父窗口，模态阻塞 + 跟随父窗口移动</li>
 *   <li><b>i18n</b>：默认 OK / Cancel 文案从 {@code Messages} 取，监听 locale 变化自动刷新</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 静态快捷 + 阻塞（最常见）
 * AlertAnt.info("保存成功", "记录已保存到服务器", ownerWindow).showAndWait();
 *
 * // 错误提示
 * AlertAnt.error("保存失败", "网络连接中断，请重试", ownerWindow).showAndWait();
 *
 * // 阻塞 + 自定义按钮文案
 * AlertAnt a = AlertAnt.confirm("删除确认", "此操作不可撤销", ownerWindow);
 * a.okText("删除").cancelText("再想想");
 * if (a.showAndWaitForOk()) { doDelete(); }
 *
 * // 非阻塞 + 回调（弹窗立即返回，关闭时异步回调）
 * AlertAnt.confirm("退出登录", "确定要退出登录吗？", ownerWindow)
 *     .onOk(() -> doLogout())
 *     .onCancel(() -> showToast("已取消"))
 *     .show();
 *
 * // 非阻塞 + 通用结果回调（处理 OK / Cancel / Close / 自定义按钮）
 * AlertAnt.warning("警告", "操作不可撤销", ownerWindow)
 *     .onResult(btn -> log.info("用户点了：{}", btn))
 *     .show();
 * }</pre>
 *
 * <h2>与 JavaFX 原生 Alert 的关系</h2>
 * <ul>
 *   <li>API 100% 兼容：show / showAndWait / getResult / setOnHidden / setResultConverter 等都可用</li>
 *   <li>非阻塞风格补充：onOk / onCancel / onResult 三个链式回调（内部包装 setOnHidden + getResult）</li>
 *   <li>主题覆盖：LESS 端通过 {@code .jfx-alert-dialog} 选择器改写 DialogPane 视觉</li>
 *   <li>默认 owner：构造时绑定，调用方不必再 initOwner()</li>
 * </ul>
 *
 * @see NotificationAnt 右下角自动消失通知
 * @see MessageAnt 顶部短暂提示
 * @see javafx.scene.control.Alert
 */
public class AlertAnt extends Alert {

    /** i18n key：AlertAnt 默认 OK 按钮文案 */
    private static final String I18N_OK = "alert.ok";
    /** i18n key：AlertAnt 默认 Cancel 按钮文案 */
    private static final String I18N_CANCEL = "alert.cancel";

    /** 缓存 dialog-pane 的 styleClass 挂载（onShowing 时一次即可） */
    private boolean styled = false;

    /** 非阻塞回调：OK 按钮被点击时触发（需配合 .show() 使用） */
    private Runnable onOkCallback;
    /** 非阻塞回调：Cancel 按钮被点击时触发（需配合 .show() 使用） */
    private Runnable onCancelCallback;
    /** 非阻塞回调：弹窗关闭时拿到最终 ButtonType（null 表示未设 result，如强制关闭） */
    private Consumer<ButtonType> onResultCallback;

    /**
     * 构造 AlertAnt（指定 AlertType + owner 父窗口）。
     *
     * @param type  JavaFX 原生 AlertType（INFORMATION / WARNING / CONFIRMATION / ERROR）
     * @param owner 父窗口，弹窗将 initOwner 此窗口并模态阻塞；传 null 则不绑定（不推荐）
     */
    public AlertAnt(AlertType type, Window owner) {
        super(type);
        // 绑定父窗口 + 模态阻塞
        if (owner != null) {
            initOwner(owner);
            initModality(javafx.stage.Modality.APPLICATION_MODAL);
        }
        // 默认按钮（OK + CANCEL，CONFIRMATION 会显示两个，其他只显示 OK）
        getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // 默认 OK / Cancel i18n 文案
        applyI18nButtonLabels();

        // 监听 locale 变化：未显式 override 时自动刷新
        Messages.localeProperty().addListener((obs, ov, nv) -> applyI18nButtonLabels());

        // onShowing 时挂 styleClass（DialogPane 此时已创建到 Scene）
        // 必须异步：JavaFX 在 showAndWait 内构造 dialog-pane 节点
        setOnShowing(e -> Platform.runLater(this::applyStyleClasses));
    }

    // ============================================================
    // 静态工厂
    // ============================================================

    /** 信息提示弹窗（INFORMATION + 单 OK 按钮）。 */
    public static AlertAnt info(String title, String content, Window owner) {
        AlertAnt a = new AlertAnt(AlertType.INFORMATION, owner);
        a.title(title).content(content);
        a.getButtonTypes().setAll(ButtonType.OK);
        return a;
    }

    /** 警告提示弹窗（WARNING + 单 OK 按钮）。 */
    public static AlertAnt warning(String title, String content, Window owner) {
        AlertAnt a = new AlertAnt(AlertType.WARNING, owner);
        a.title(title).content(content);
        a.getButtonTypes().setAll(ButtonType.OK);
        return a;
    }

    /** 错误提示弹窗（ERROR + 单 OK 按钮）。 */
    public static AlertAnt error(String title, String content, Window owner) {
        AlertAnt a = new AlertAnt(AlertType.ERROR, owner);
        a.title(title).content(content);
        a.getButtonTypes().setAll(ButtonType.OK);
        return a;
    }

    /** 确认对话框（CONFIRMATION + OK / Cancel 按钮）。 */
    public static AlertAnt confirm(String title, String content, Window owner) {
        AlertAnt a = new AlertAnt(AlertType.CONFIRMATION, owner);
        a.title(title).content(content);
        a.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        return a;
    }

    // ============================================================
    // 链式 setter（fluent API）
    // ============================================================

    /** 设置标题（即 JavaFX Alert 的 headerText）。 */
    public AlertAnt title(String title) {
        setHeaderText(TextUtils.safeText(title));
        return this;
    }

    /** 设置内容（即 JavaFX Alert 的 contentText）。 */
    public AlertAnt content(String content) {
        setContentText(TextUtils.safeText(content));
        return this;
    }

    /** 设置 OK 按钮文案（覆盖 i18n 默认）。 */
    public AlertAnt okText(String text) {
        setOkButtonLabel(text);
        return this;
    }

    /** 设置 Cancel 按钮文案（覆盖 i18n 默认）。 */
    public AlertAnt cancelText(String text) {
        setCancelButtonLabel(text);
        return this;
    }

    /**
     * 非阻塞回调：用户点击 OK 按钮时触发。
     *
     * <p>需配合 {@code .show()} 使用（若使用 {@code showAndWait()}，
     * 会在弹窗关闭时通过 setOnHidden 异步触发回调，
     * 调 showAndWait 本身会阻塞主线程 —— 适合异步任务、计时后台运行）。</p>
     *
     * <p>典型用法：</p>
     * <pre>{@code
     * AlertAnt.confirm("删除确认", "此操作不可撤销", ownerWindow)
     *     .onOk(() -> doDelete())
     *     .onCancel(() -> showToast("已取消"))
     *     .show();
     * }</pre>
     */
    public AlertAnt onOk(Runnable callback) {
        this.onOkCallback = callback;
        bindResultCallback();
        return this;
    }

    /** 非阻塞回调：用户点击 Cancel 按钮时触发（参见 {@link #onOk(Runnable)}）。 */
    public AlertAnt onCancel(Runnable callback) {
        this.onCancelCallback = callback;
        bindResultCallback();
        return this;
    }

    /**
     * 非阻塞回调：弹窗关闭时拿到最终结果 ButtonType（可能为 null ——例如点 X 强制关闭）。
     *
     * <p>比 onOk / onCancel 更通用，可同时处理 OK / Cancel / Close / 自定义按钮等所有场景。
     * 若同时设置了 onOk / onCancel / onResult，回调顺序为：先 onResult、后 onOk 或 onCancel。</p>
     */
    public AlertAnt onResult(Consumer<ButtonType> callback) {
        this.onResultCallback = callback;
        bindResultCallback();
        return this;
    }

    /** 自定义左侧图形节点（默认按 AlertType 用 Ikonli 字符图标）。 */
    public AlertAnt graphic(Node graphic) {
        setGraphic(graphic);
        return this;
    }

    // ============================================================
    // 内部：i18n 按钮文案 + styleClass 挂载
    // ============================================================

    /**
     * 同步 OK / Cancel 按钮文案。优先用调用方通过 okText/cancelText 显式设置的值，
     * 否则走 i18n（Messages.get）。
     */
    private void applyI18nButtonLabels() {
        applyI18nButton(ButtonType.OK, okOverride, I18N_OK);
        applyI18nButton(ButtonType.CANCEL, cancelOverride, I18N_CANCEL);
    }

    /** 缓存调用方显式设置的按钮文案（null = 走 i18n）。 */
    private String okOverride = null;
    private String cancelOverride = null;

    private void applyI18nButton(ButtonType type, String override, String i18nKey) {
        // 查找 DialogPane 中实际渲染该 ButtonType 的 Button 节点
        DialogPane pane = getDialogPane();
        Node btn = pane.lookupButton(type);
        if (btn instanceof javafx.scene.control.Button b) {
            String text = override != null ? override : Messages.get(i18nKey);
            b.setText(text);
        }
    }

    private void setOkButtonLabel(String text) {
        this.okOverride = text;
        applyI18nButton(ButtonType.OK, okOverride, I18N_OK);
    }

    private void setCancelButtonLabel(String text) {
        this.cancelOverride = text;
        applyI18nButton(ButtonType.CANCEL, cancelOverride, I18N_CANCEL);
    }

    /**
     * 把 lookup 出来的节点（如 .header-panel / .content）安全挂上 jfx- 子类 class。
     * lookup 在 JavaFX 内部结构变化或节点尚未渲染时可能返回 null，必须容忍。
     */
    private static void addClassIfPresent(DialogPane pane, String selector, String styleClass) {
        Node n = pane.lookup(selector);
        if (n != null) {
            n.getStyleClass().add(styleClass);
        }
    }

    /**
     * 给 DialogPane + 内部子节点挂 styleClass，让 LESS 端的选择器命中。
     * - dialog-pane 挂 {@code jfx-alert-dialog} 根 + 4 个 type 修饰类
     * - header-panel / content / button-bar / graphic-container 挂细化 class
     */
    private void applyStyleClasses() {
        if (styled) return;
        styled = true;

        DialogPane pane = getDialogPane();
        if (pane == null) return;

        // 根 class + 类型修饰类
        pane.getStyleClass().add(JfxStyles.ALERT_DIALOG);
        switch (getAlertType()) {
            case INFORMATION -> pane.getStyleClass().add(JfxStyles.ALERT_DIALOG_INFO);
            case WARNING     -> pane.getStyleClass().add(JfxStyles.ALERT_DIALOG_WARNING);
            case ERROR       -> pane.getStyleClass().add(JfxStyles.ALERT_DIALOG_ERROR);
            case CONFIRMATION -> pane.getStyleClass().add(JfxStyles.ALERT_DIALOG_CONFIRM);
            default -> { /* NONE 不加修饰类 */ }
        }

        // 内部子结构细化 class（用 lookup 而非 getChildren——避免破坏 JavaFX 内部布局）
        // 关键：必须容忍 null——NONE AlertType 未设 headerText 时 header-panel 不存在
        addClassIfPresent(pane, ".header-panel", JfxStyles.ALERT_DIALOG_HEADER);
        addClassIfPresent(pane, ".content",     JfxStyles.ALERT_DIALOG_CONTENT);
        addClassIfPresent(pane, ".button-bar",  JfxStyles.ALERT_DIALOG_BUTTON_BAR);
        Node graphic = pane.lookup(".graphic-container");
        if (graphic != null) {
            graphic.getStyleClass().add(JfxStyles.ALERT_DIALOG_GRAPHIC);
        }

        // 图形容器默认宽度钳制（JavaFX 默认 0 宽度让 icon 看不见）
        if (graphic instanceof Region r) {
            r.setMinWidth(48);
            r.setPrefWidth(48);
            r.setMaxWidth(48);
        }

        // 设置默认图形（若调用方未通过 graphic() 显式指定）
        if (getGraphic() == null) {
            setGraphic(defaultGraphicFor(getAlertType()));
        }
    }

    /**
     * 按 AlertType 返回 Ikonli 字符图形节点。
     *
     * <p>实现说明：</p>
     * <ul>
     *   <li>走 {@link LabelAnt#create(String)} 工厂而非 {@code new Label()}（红线 11）</li>
     *   <li>移除 {@code jfx-typography-text} 默认语义类 —— 此处是 icon 不是文本，
     *       应只受 {@link JfxStyles#ALERT_DIALOG_GRAPHIC_DEFAULT} 控制颜色/布局</li>
     *   <li>字体族通过 JavaFX {@link Font} API 设置（结构性属性），不走 setStyle（红线 1）</li>
     *   <li>CONFIRMATION 类型暂用 Unicode {@code "?"}（IconAnt.Symbol 暂无 HELP 枚举）</li>
     * </ul>
     */
    private static Node defaultGraphicFor(AlertType type) {
        String ch = switch (type) {
            case INFORMATION -> String.valueOf(IconAnt.Symbol.INFO.getChar());
            case WARNING     -> String.valueOf(IconAnt.Symbol.WARNING.getChar());
            case ERROR       -> String.valueOf(IconAnt.Symbol.CLOSE.getChar());
            case CONFIRMATION -> "?";
            default -> "?";
        };
        // 走 LabelAnt.create() 工厂（红线 11），移除文本语义类，挂图标语义类
        LabelAnt icon = LabelAnt.create(ch);
        icon.getStyleClass().remove(JfxStyles.TYPOGRAPHY_TEXT);
        icon.getStyleClass().add(JfxStyles.ALERT_DIALOG_GRAPHIC_DEFAULT);
        // 字体族 + 字号走 JavaFX Font API（结构性属性，不算 setStyle 红线）
        icon.setFont(Font.font(FontTokens.SYMBOL_FAMILY, 32));
        return icon;
    }

    // ============================================================
    // 便捷：把 showAndWait 包成返回 boolean 的形式
    // ============================================================

    /**
     * 显示并等待用户响应，返回是否点了 OK 按钮（CONFIRMATION 场景最常用）。
     * 直接调用 {@code showAndWait()} 仍可拿到 {@link Optional}<{@link ButtonType}>。
     */
    public boolean showAndWaitForOk() {
        Optional<ButtonType> r = showAndWait();
        return r.isPresent() && r.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    // ============================================================
    // 内部：非阻塞回调绑定（setOnHidden + getResult 分发）
    // ============================================================

    /**
     * 把 setOnHidden 绑定到回调分发逻辑。
     * 任意 onOk / onCancel / onResult 被调用时都会触发本方法，多次调用安全幂等。
     *
     * <p>分发顺序：先 onResult(ButtonType) ——包含 null；后 onOk 或 onCancel 分类回调。</p>
     */
    private void bindResultCallback() {
        setOnHidden(e -> {
            ButtonType result = getResult();
            // 1) 通用结果回调（先于 onOk/onCancel，便于业务方做拦截/打印日志）
            if (onResultCallback != null) {
                onResultCallback.accept(result);
            }
            // 2) 分类回调
            if (result == null) {
                return;
            }
            ButtonBar.ButtonData data = result.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                if (onOkCallback != null) {
                    onOkCallback.run();
                }
            } else if (data == ButtonBar.ButtonData.CANCEL_CLOSE) {
                if (onCancelCallback != null) {
                    onCancelCallback.run();
                }
            }
        });
    }
}