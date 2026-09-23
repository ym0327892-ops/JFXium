package org.openkawu.jfxium.component.overlay;

import javafx.geometry.Pos;
import javafx.scene.Node;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

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
 *   <li><b>i18n</b>：title / ok / cancel 文案支持走 {@link Messages#localeProperty()} 自动刷新（仅当调用方未显式指定时）</li>
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

    // null = 用 i18n 默认值；非 null = 调用方显式指定
    // 字段初始化为 null（不在声明时调 Messages.get），确保 localeProperty() 切换时能动态刷新
    private String title = null;
    private String message;
    private String defaultValue = "";
    private String placeholder;
    private String okText = null;
    private String cancelText = null;
    private Consumer<String> onConfirm;
    private Runnable onCancel;
    private int width = 400;

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
        this.defaultValue = TextUtils.safeText(defaultValue);
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

    /** 设置弹框宽度，默认 400px。 */
    public PromptDialogAnt width(int width) {
        this.width = width;
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
        // 懒解析 i18n 文案（避免在字段初始化时锁死 locale）
        String resolvedTitle = title != null ? title : Messages.get("prompt.title");
        String resolvedOk = okText != null ? okText : Messages.get("prompt.ok");
        String resolvedCancel = cancelText != null ? cancelText : Messages.get("prompt.cancel");

        // 内容容器：垂直布局，左对齐
        VBoxAnt content = VBoxAnt.create()
                .align(Pos.CENTER_LEFT)
                .styleClass(JfxStyles.PROMPT_DIALOG);

        // 消息文本
        if (message != null && !message.isEmpty()) {
            LabelAnt msgLabel = LabelAnt.create(message)
                    .styleClass(JfxStyles.PROMPT_DIALOG_MESSAGE)
                    .build();
            content.children(msgLabel);
        }

        // 输入框：走 InputAnt（封装 TextField + styleClass + 主题适配）
        InputAnt input = InputAnt.create(defaultValue)
                .styleClass(JfxStyles.PROMPT_DIALOG_INPUT);
        if (placeholder != null) {
            input.placeholder(placeholder);
        }
        content.children(input);

        // 按钮区
        HBoxAnt buttons = HBoxAnt.create()
                .align(Pos.CENTER_RIGHT)
                .styleClass(JfxStyles.PROMPT_DIALOG_FOOTER);

        // 先创建 Result 占位（modalResult 稍后注入），按钮直接调 result.close() 保证点完即关
        PromptDialogResult result = new PromptDialogResult();

        ButtonAnt cancelBtn = ButtonAnt.create(resolvedCancel)
                .type(ButtonAnt.Type.DEFAULT)
                .onClick(e -> {
                    if (onCancel != null) onCancel.run();
                    result.close();
                })
                .build();

        ButtonAnt okBtn = ButtonAnt.create(resolvedOk)
                .type(ButtonAnt.Type.ACCENT)
                .onClick(e -> {
                    if (onConfirm != null) onConfirm.accept(input.getText());
                    result.close();
                })
                .build();

        buttons.children(cancelBtn, okBtn);

        // 按钮作为 Modal 的自定义 footer（footer() 内部会置 noDefaultFooter=true），
        // 否则 Modal 还会再渲染一份默认 footer，出现 4 个按钮，
        // 且用户点的默认确定按钮只走 onOk（null），导致 onConfirm 不触发、输入值丢失。
        ModalAnt.ModalResult modalResult = ModalAnt.create()
            .title(resolvedTitle)
            .content(content)
            .footer(buttons)
            .width(width)
            .build();

        // title / 按钮的 locale 监听统一走 ModalResult 登记，随 stage hidden 自动注销（防泄漏）。
        if (title == null) {
            modalResult.onTitleLocaleChange(() -> Messages.get("prompt.title"));
        }
        if (cancelText == null) {
            modalResult.bindToLocale((obs, ov, nv) -> cancelBtn.setText(Messages.get("prompt.cancel")));
        }
        if (okText == null) {
            modalResult.bindToLocale((obs, ov, nv) -> okBtn.setText(Messages.get("prompt.ok")));
        }

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
