package org.openkawu.jfxium.component.overlay;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 快速输入/选择弹框组件 - 对标 Ant Design Modal.confirm / prompt。
 *
 * <p><b>定位</b>：轻量级弹框，用于快速获取用户输入（文本/选择）。
 * 基于 {@link ModalAnt} 封装，提供单行输入、确认/取消按钮、回调机制。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>标题</b>：title(String) 弹框标题</li>
 *   <li><b>提示文本</b>：message(String) 输入框上方说明文字</li>
 *   <li><b>默认值</b>：defaultValue(String) 输入框默认值</li>
 *   <li><b>占位符</b>：placeholder(String) 输入框占位提示</li>
 *   <li><b>回调</b>：onConfirm(String) / onCancel() 确认/取消回调</li>
 *   <li><b>按钮文字</b>：okText(String) / cancelText(String) 自定义按钮文案</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#PROMPT_DIALOG} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础 prompt
 * PromptDialogAnt.show(stage)
 *     .title("请输入名称")
 *     .message("输入后点击确认保存")
 *     .placeholder("例如：张三")
 *     .onConfirm(name -> System.out.println("输入：" + name))
 *     .build();
 *
 * // 带默认值 + 自定义按钮
 * PromptDialogAnt.show(stage)
 *     .title("修改备注")
 *     .defaultValue("旧备注")
 *     .okText("保存")
 *     .cancelText("放弃")
 *     .onConfirm(text -> saveRemark(text))
 *     .onCancel(() -> System.out.println("取消"))
 *     .build();
 * }</pre>
 */
public class PromptDialogAnt {

    private String title = "提示";
    private String message;
    private String defaultValue = "";
    private String placeholder;
    private String okText = "确认";
    private String cancelText = "取消";
    private Consumer<String> onConfirm;
    private Runnable onCancel;

    private final Stage owner;

    // ============================================================
    // 工厂入口
    // ============================================================

    public static PromptDialogAnt show(Stage owner) {
        return new PromptDialogAnt(owner);
    }

    // ============================================================
    // 构造函数
    // ============================================================

    private PromptDialogAnt(Stage owner) {
        this.owner = owner;
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public PromptDialogAnt title(String title) {
        this.title = title;
        return this;
    }

    public PromptDialogAnt message(String message) {
        this.message = message;
        return this;
    }

    public PromptDialogAnt defaultValue(String defaultValue) {
        this.defaultValue = defaultValue != null ? defaultValue : "";
        return this;
    }

    public PromptDialogAnt placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public PromptDialogAnt okText(String okText) {
        this.okText = okText;
        return this;
    }

    public PromptDialogAnt cancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public PromptDialogAnt onConfirm(Consumer<String> onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public PromptDialogAnt onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    // ============================================================
    // 构建并显示
    // ============================================================

    /**
     * 构建弹框并返回 {@link PromptDialogResult}，调用方可通过 {@code result.close()} 程序化关闭。
     * @return PromptDialogResult（可关闭弹框）
     */
    public PromptDialogResult build() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER_LEFT);
        content.getStyleClass().add(JfxStyles.PROMPT_DIALOG);

        // 消息文本
        if (message != null && !message.isEmpty()) {
            Label msgLabel = new Label(message);
            msgLabel.getStyleClass().add(JfxStyles.PROMPT_DIALOG_MESSAGE);
            content.getChildren().add(msgLabel);
        }

        // 输入框
        TextField input = new TextField(defaultValue);
        input.getStyleClass().add(JfxStyles.PROMPT_DIALOG_INPUT);
        if (placeholder != null) {
            input.setPromptText(placeholder);
        }
        content.getChildren().add(input);

        // 按钮区
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getStyleClass().add(JfxStyles.PROMPT_DIALOG_FOOTER);

        PromptDialogResult result = new PromptDialogResult();

        Button cancelBtn = new Button(cancelText);
        cancelBtn.getStyleClass().addAll(JfxStyles.BUTTON_DEFAULT);
        cancelBtn.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            result.close();
        });

        Button okBtn = new Button(okText);
        okBtn.getStyleClass().addAll(JfxStyles.BUTTON_ACCENT);
        okBtn.setOnAction(e -> {
            if (onConfirm != null) onConfirm.accept(input.getText());
            result.close();
        });

        buttons.getChildren().addAll(cancelBtn, okBtn);
        content.getChildren().add(buttons);

        // 使用 ModalAnt 显示
        ModalAnt.ModalResult modalResult = ModalAnt.create()
            .title(title)
            .content(content)
            .width(400)
            .build();

        result.modalResult = modalResult;

        // 需要 owner Node，这里用空 Label 占位（实际使用时需要传入）
        javafx.scene.Node ownerNode = new javafx.scene.control.Label();
        modalResult.open(ownerNode);

        return result;
    }

    /**
     * Prompt 弹框结果封装，提供程序化关闭能力。
     */
    public static class PromptDialogResult {
        private ModalAnt.ModalResult modalResult;

        /** 程序化关闭弹框。 */
        public void close() {
            if (modalResult != null) {
                modalResult.close();
            }
        }
    }
}
