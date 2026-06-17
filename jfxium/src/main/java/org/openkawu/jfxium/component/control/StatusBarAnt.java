package org.openkawu.jfxium.component.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 底部状态栏组件 - 对标 VS Code / IDEA 底栏。
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

    public static Builder create() {
        return new Builder();
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
    // 运行时动态更新（构建后修改状态）
    // ============================================================

    /** 更新左侧信息文本。 */
    public void updateInfo(String text) {
        if (infoLabel == null) {
            infoLabel = new Label(text);
            leftBox.getChildren().add(infoLabel);
        } else {
            infoLabel.setText(text);
        }
    }

    /** 更新中间进度条（0.0 ~ 1.0，-1 表示隐藏）。 */
    public void updateProgress(double value) {
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
    }

    /** 更新右侧状态文本。 */
    public void updateStatus(String text) {
        if (statusLabel == null) {
            statusLabel = new Label(text);
            rightBox.getChildren().add(statusLabel);
        } else {
            statusLabel.setText(text);
        }
    }

    // ============================================================
    // 包内配置方法（供 Builder 使用）
    // ============================================================

    void doInfo(String text) {
        if (infoLabel == null) {
            infoLabel = new Label(text);
            leftBox.getChildren().add(infoLabel);
        } else {
            infoLabel.setText(text);
        }
    }

    void doProgress(double value) {
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
    }

    void doStatus(String text) {
        if (statusLabel == null) {
            statusLabel = new Label(text);
            rightBox.getChildren().add(statusLabel);
        } else {
            statusLabel.setText(text);
        }
    }

    void doLeft(Node node) { leftBox.getChildren().add(node); }
    void doCenter(Node node) { centerBox.getChildren().add(node); }
    void doRight(Node node) { rightBox.getChildren().add(node); }

    // ============================================================
    // Builder
    // ============================================================

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String info;
        private String status;
        private double progress = -1;
        private final List<Node> leftNodes = new ArrayList<>();
        private final List<Node> centerNodes = new ArrayList<>();
        private final List<Node> rightNodes = new ArrayList<>();

        /** 左侧信息文本。 */
        public Builder info(String text) {
            this.info = text;
            return this;
        }

        /** 右侧状态文本。 */
        public Builder status(String text) {
            this.status = text;
            return this;
        }

        /** 添加可点击操作项（内联按钮：透明背景、无边框、微 padding，契合 {@code @status-bar-height} 28px 高度，对标 VS Code 底栏按钮）。
         *  视觉剥离委托给 {@link JfxStyles#BUTTON_INLINE}，键盘可达/accessibility 由原生 Button 保证。 */
        public Builder action(String text, Runnable onClick) {
            Button btn = new Button(text);
            btn.getStyleClass().addAll(JfxStyles.BUTTON_INLINE, JfxStyles.STATUS_BAR_ACTION);
            if (onClick != null) {
                btn.setOnAction(e -> onClick.run());
            }
            rightNodes.add(btn);
            return this;
        }

        /** 中间进度条（0.0 ~ 1.0）。 */
        public Builder progress(double value) {
            this.progress = value;
            return this;
        }

        /** 自定义左侧节点。 */
        public Builder left(Node node) {
            leftNodes.add(node);
            return this;
        }

        /** 自定义中间节点。 */
        public Builder center(Node node) {
            centerNodes.add(node);
            return this;
        }

        /** 自定义右侧节点。 */
        public Builder right(Node node) {
            rightNodes.add(node);
            return this;
        }

        /** 构建 StatusBarAnt。 */
        public StatusBarAnt build() {
            StatusBarAnt bar = new StatusBarAnt();
            if (info != null) bar.doInfo(info);
            if (status != null) bar.doStatus(status);
            if (progress >= 0) bar.doProgress(progress);
            for (Node n : leftNodes) bar.doLeft(n);
            for (Node n : centerNodes) bar.doCenter(n);
            for (Node n : rightNodes) bar.doRight(n);
            applyStyles(bar);
            return bar;
        }
    }
}
