package org.openkawu.jfxium.component.overlay;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 快速输入弹框组件 - 对标 Ant Design Modal.confirm / prompt。
 *
 * <p><b>定位</b>：轻量级弹框，用于快速获取用户输入（文本）。
 * 基于 {@link ModalAnt} 封装，提供单行输入、确认/取消按钮、回调机制。</p>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li><b>API 范式</b>：{@code PromptDialogAnt.create()...build().open(ownerNode)} —— 与 {@link ModalAnt} 完全一致</li>
 *   <li><b>build() 不含副作用</b>：仅构建 {@link PromptDialogResult}，需要显式 {@code .open(Node)} 才显示</li>
 *   <li><b>owner 必须是已挂载到 Scene 的 Node</b>：用于定位所属 Stage（取 {@code owner.getScene().getWindow()}）</li>
 *   <li><b>回调</b>：onConfirm(String) / onCancel() 确认/取消时触发</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#PROMPT_DIALOG} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础 prompt
 * PromptDialogAnt.create()
 *     .title("请输入名称")
 *     .message("输入后点击确认保存")
 *     .placeholder("例如：张三")
 *     .onConfirm(name -> System.out.println("输入：" + name))
 *     .build()
 *     .open(ownerNode);
 *
 * // 带默认值 + 自定义按钮
 * PromptDialogAnt.create()
 *     .title("修改备注")
 *     .defaultValue("旧备注")
 *     .okText("保存")
 *     .cancelText("放弃")
 *     .onConfirm(text -> saveRemark(text))
 *     .onCancel(() -> System.out.println("取消"))
 *     .build()
 *     .open(ownerNode);
 * }</pre>
 *
 * @see ModalAnt 标准对话框（PromptDialogAnt 的底层）
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

    // ============================================================
    // 工厂入口
    // ============================================================

    /**
     * 入口工厂。与 {@link ModalAnt#create()} 保持完全一致的范式。
     */
    public static PromptDialogAnt create() {
        return new PromptDialogAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    private PromptDialogAnt() {
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
    // 构建
    // ============================================================

    /**
     * 构建弹框并返回 {@link PromptDialogResult}。
     * <p><b>不产生显示副作用</b>——必须再调 {@code result.open(ownerNode)} 才会弹出。</p>
     *
     * @return PromptDialogResult（持有内部 ModalResult，可通过 {@code .close()} 程序化关闭）
     */
    public PromptDialogResult build() {
        VBox content = new VBox();
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
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getStyleClass().add(JfxStyles.PROMPT_DIALOG_FOOTER);

        // 先创建 Result 占位（modalResult 稍后注入），按钮直接调 result.close() 保证点完即关
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

        // 使用 ModalAnt 构建（不打开，等 .open() 显式触发）
        ModalAnt.ModalResult modalResult = ModalAnt.create()
            .title(title)
            .content(content)
            .width(400)
            .build();

        result.setModalResult(modalResult);

        return result;
    }

    // ============================================================
    // Result
    // ============================================================

    /**
     * Prompt 弹框结果封装。{@link #open(Node)} 显式显示；{@link #close()} 程序化关闭。
     */
    public static class PromptDialogResult {
        private ModalAnt.ModalResult modalResult;

        PromptDialogResult() {
        }

        void setModalResult(ModalAnt.ModalResult modalResult) {
            this.modalResult = modalResult;
        }

        /**
         * 显示弹框。owner 必须是已挂载到 Scene 的任意 Node（用于定位所属 Window）。
         * 典型用法：{@code .open((Node) e.getSource())}。
         *
         * @param owner 触发弹框的 Node（按钮 / 菜单项等）
         */
        public void open(Node owner) {
            if (owner == null) {
                throw new IllegalArgumentException(
                        "PromptDialogAnt.open(owner) 的 owner 不能为 null —— "
                                + "请传入当前已挂载到 Scene 的 Node（通常是触发按钮）");
            }
            modalResult.open(owner);
        }

        /** 程序化关闭弹框。 */
        public void close() {
            if (modalResult != null) {
                modalResult.close();
            }
        }
    }
}
