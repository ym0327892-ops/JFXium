package org.openkawu.jfxium.component.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 底部状态栏组件（M19.50 重构）— 继承式 + 双工厂模式。
 *
 * <p><b>定位</b>：窗口底部信息与操作栏，左侧信息 + 中间进度 + 右侧状态/操作。
 * 对标 VS Code / IDEA 底栏，既可展示只读信息（编码、行列号），也可放置可点击操作项
 * （语言切换、分支选择、通知等）。JavaFX 原生无此组件，ControlsFX 才有。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>三栏布局</b>：左(info) / 中(progress) / 右(status)，自动弹性分布</li>
 *   <li><b>信息文本</b>：info(text) 左侧信息标签</li>
 *   <li><b>进度条</b>：progress(value) 中间进度显示（0.0~1.0）</li>
 *   <li><b>状态文本</b>：status(text) 右侧状态标签</li>
 *   <li><b>可点击操作项</b>：action(text, onClick) 添加可点击的交互项，自带 hover 高亮</li>
 *   <li><b>自定义节点</b>：left(Node) / center(Node) / right(Node) 可放任意节点</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#STATUS_BAR} 系列 LESS 样式，高度由 {@code @status-bar-height} token 控制</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * // 基础状态栏
 * StatusBarAnt statusBar = StatusBarAnt.create()
 *     .info("就绪")
 *     .status("UTF-8 | LF | Java")
 *     .build();
 *
 * // 带进度条
 * StatusBarAnt withProgress = StatusBarAnt.create()
 *     .info("正在上传...")
 *     .progress(0.45)
 *     .status("45%")
 *     .build();
 *
 * // 自定义节点
 * StatusBarAnt custom = StatusBarAnt.create()
 *     .left(new Label("Git: main"))
 *     .center(new ProgressBar(0.3))
 *     .right(new Label("行 42, 列 15"))
 *     .build();
 *
 * // 可点击操作项（对标 VS Code 底栏）
 * StatusBarAnt interactive = StatusBarAnt.create()
 *     .info("就绪")
 *     .action("UTF-8", () -> chooseEncoding())
 *     .action("LF", () -> changeLineEnding())
 *     .status("行 42, 列 15")
 *     .build();
 *
 * // 运行时动态更新
 * StatusBarAnt bar = ...;
 * bar.updateInfo("新信息");
 * bar.updateProgress(0.8);
 * bar.updateStatus("完成");
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class EditorStatusBar extends StatusBarAnt {
 *     public EditorStatusBar() {
 *         info("就绪");
 *         action("UTF-8", () -> chooseEncoding());
 *         status("行 42, 列 15");
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link HBox} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>运行时 + 构建时双 API</b>：{@code updateXxx} 运行时更新，{@code info/status/progress} 构建时设置</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class StatusBarAnt extends HBox
        implements LayoutCommon<StatusBarAnt>, DisabledSupport<StatusBarAnt> {

    /** 进度条默认宽度。 */
    private static final double PROGRESS_BAR_WIDTH = 120;

    private final HBox leftBox;
    private final HBox centerBox;
    private final HBox rightBox;
    private ProgressBar progressBar;
    private Label infoLabel;
    private Label statusLabel;

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（默认空状态栏）。 */
    public static StatusBarAnt create() {
        return new StatusBarAnt();
    }

    /** 工厂入口（带初始 info/status 文本）。 */
    public static StatusBarAnt create(String info, String status) {
        StatusBarAnt bar = new StatusBarAnt();
        if (info != null) bar.updateInfo(info);
        if (status != null) bar.updateStatus(status);
        return bar;
    }

    // ============================================================
    // 构造函数（公开，便于业务继承）
    // ============================================================

    public StatusBarAnt() {
        super();
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add(JfxStyles.STATUS_BAR);

        // 左栏
        leftBox = new HBox();
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getStyleClass().add(JfxStyles.STATUS_BAR_LEFT);
        HBox.setHgrow(leftBox, Priority.SOMETIMES);

        // 中栏
        centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getStyleClass().add(JfxStyles.STATUS_BAR_CENTER);
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        // 右栏
        rightBox = new HBox();
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.getStyleClass().add(JfxStyles.STATUS_BAR_RIGHT);
        HBox.setHgrow(rightBox, Priority.SOMETIMES);

        getChildren().addAll(leftBox, centerBox, rightBox);
    }

    // ============================================================
    // 流式 API（构建时）
    // ============================================================

    /**
     * 设置左侧信息文本（首次调用创建 Label，后续调用更新文本）。
     */
    public StatusBarAnt info(String text) {
        updateInfo(text);
        return this;
    }

    /**
     * 设置右侧状态文本（首次调用创建 Label，后续调用更新文本）。
     */
    public StatusBarAnt status(String text) {
        updateStatus(text);
        return this;
    }

    /**
     * 设置中间进度（0.0 ~ 1.0；负数表示隐藏）。
     */
    public StatusBarAnt progress(double value) {
        updateProgress(value);
        return this;
    }

    /**
     * 添加可点击操作项（内联按钮，契合 {@code @status-bar-height} 28px 高度，对标 VS Code 底栏按钮）。
     * 视觉剥离委托给 {@link JfxStyles#BUTTON_INLINE}，语义与尺寸走 ButtonAnt 的 {@code LINK + XS} 档。
     */
    public StatusBarAnt action(String text, Runnable onClick) {
        ButtonAnt btn = ButtonAnt.create(text)
                .type(ButtonAnt.Type.LINK)
                .size(Size.XS)
                .styleClass(JfxStyles.BUTTON_INLINE, JfxStyles.STATUS_BAR_ACTION);
        if (onClick != null) {
            btn.setOnAction(e -> onClick.run());
        }
        rightBox.getChildren().add(btn);
        return this;
    }

    /**
     * 添加左侧自定义节点。
     */
    public StatusBarAnt left(Node node) {
        if (node != null) {
            leftBox.getChildren().add(node);
        }
        return this;
    }

    /**
     * 添加中间自定义节点。
     */
    public StatusBarAnt center(Node node) {
        if (node != null) {
            centerBox.getChildren().add(node);
        }
        return this;
    }

    /**
     * 添加右侧自定义节点。
     */
    public StatusBarAnt right(Node node) {
        if (node != null) {
            rightBox.getChildren().add(node);
        }
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 运行时动态更新（构建后修改状态）
    // ============================================================

    /** 更新左侧信息文本。 */
    public void updateInfo(String text) {
        if (infoLabel == null) {
            infoLabel = LabelAnt.create(TextUtils.safeText(text)).build();
            leftBox.getChildren().add(infoLabel);
        } else {
            infoLabel.setText(TextUtils.safeText(text));
        }
    }

    /** 更新中间进度条（0.0 ~ 1.0，负数表示隐藏）。 */
    public void updateProgress(double value) {
        if (progressBar == null) {
            progressBar = new ProgressBar(value < 0 ? ProgressBar.INDETERMINATE_PROGRESS : value);
            progressBar.setPrefWidth(PROGRESS_BAR_WIDTH);
            if (value < 0) {
                progressBar.setVisible(false);
            }
            centerBox.getChildren().add(progressBar);
        } else {
            if (value < 0) {
                progressBar.setVisible(false);
            } else {
                progressBar.setVisible(true);
                progressBar.setProgress(value);
            }
        }
    }

    /** 更新右侧状态文本。 */
    public void updateStatus(String text) {
        if (statusLabel == null) {
            statusLabel = LabelAnt.create(TextUtils.safeText(text)).build();
            rightBox.getChildren().add(statusLabel);
        } else {
            statusLabel.setText(TextUtils.safeText(text));
        }
    }

    // ============================================================
    // 构建
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>StatusBarAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public StatusBarAnt build() {
        return this;
    }
}