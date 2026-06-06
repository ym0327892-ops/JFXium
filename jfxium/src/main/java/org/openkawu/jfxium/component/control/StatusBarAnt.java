package org.openkawu.jfxium.component.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 底部状态栏组件 - 对标 VS Code / IDEA 底栏。
 *
 * <p><b>定位</b>：窗口底部信息展示栏，左侧信息 + 中间进度 + 右侧状态指示器。
 * JavaFX 原生无此组件，ControlsFX 才有。常用于 IDE、编辑器、管理后台等 PC 软件。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>三栏布局</b>：左(info) / 中(progress) / 右(status)，自动弹性分布</li>
 *   <li><b>信息文本</b>：info(text) 左侧信息标签</li>
 *   <li><b>进度条</b>：progress(value) 中间进度显示（0.0~1.0）</li>
 *   <li><b>状态文本</b>：status(text) 右侧状态标签</li>
 *   <li><b>自定义节点</b>：left(Node) / center(Node) / right(Node) 可放任意节点</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#STATUS_BAR} 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
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
 * }</pre>
 */
public class StatusBarAnt extends HBox {

    private final HBox leftBox;
    private final HBox centerBox;
    private final HBox rightBox;
    private ProgressBar progressBar;
    private Label infoLabel;
    private Label statusLabel;

    // ============================================================
    // 工厂入口
    // ============================================================

    public static StatusBarAnt create() {
        return new StatusBarAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public StatusBarAnt() {
        super(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add(JfxStyles.STATUS_BAR);

        // 左栏
        leftBox = new HBox(8);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getStyleClass().add(JfxStyles.STATUS_BAR_LEFT);
        HBox.setHgrow(leftBox, Priority.SOMETIMES);

        // 中栏
        centerBox = new HBox(8);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getStyleClass().add(JfxStyles.STATUS_BAR_CENTER);
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        // 右栏
        rightBox = new HBox(8);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.getStyleClass().add(JfxStyles.STATUS_BAR_RIGHT);
        HBox.setHgrow(rightBox, Priority.SOMETIMES);

        getChildren().addAll(leftBox, centerBox, rightBox);
    }

    // ============================================================
    // 快捷方法
    // ============================================================

    /** 左侧信息文本。 */
    public StatusBarAnt info(String text) {
        if (infoLabel == null) {
            infoLabel = new Label(text);
            leftBox.getChildren().add(infoLabel);
        } else {
            infoLabel.setText(text);
        }
        return this;
    }

    /** 中间进度条（0.0 ~ 1.0，-1 表示不显示）。 */
    public StatusBarAnt progress(double value) {
        if (progressBar == null) {
            progressBar = new ProgressBar(value);
            progressBar.setPrefWidth(120);
            centerBox.getChildren().add(progressBar);
        } else {
            if (value < 0) {
                progressBar.setVisible(false);
            } else {
                progressBar.setVisible(true);
                progressBar.setProgress(value);
            }
        }
        return this;
    }

    /** 右侧状态文本。 */
    public StatusBarAnt status(String text) {
        if (statusLabel == null) {
            statusLabel = new Label(text);
            rightBox.getChildren().add(statusLabel);
        } else {
            statusLabel.setText(text);
        }
        return this;
    }

    // ============================================================
    // 自定义节点
    // ============================================================

    public StatusBarAnt left(Node node) {
        leftBox.getChildren().add(node);
        return this;
    }

    public StatusBarAnt center(Node node) {
        centerBox.getChildren().add(node);
        return this;
    }

    public StatusBarAnt right(Node node) {
        rightBox.getChildren().add(node);
        return this;
    }

    // ============================================================
    // 更新方法（运行时动态更新）
    // ============================================================

    public void updateInfo(String text) {
        info(text);
    }

    public void updateProgress(double value) {
        progress(value);
    }

    public void updateStatus(String text) {
        status(text);
    }

    // ============================================================
    // 清空
    // ============================================================

    public StatusBarAnt clear() {
        leftBox.getChildren().clear();
        centerBox.getChildren().clear();
        rightBox.getChildren().clear();
        infoLabel = null;
        progressBar = null;
        statusLabel = null;
        return this;
    }
}
