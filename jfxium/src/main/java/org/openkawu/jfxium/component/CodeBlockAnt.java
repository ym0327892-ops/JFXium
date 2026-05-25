package org.openkawu.jfxium.component;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.function.Consumer;

/**
 * JFXium CodeBlock 组件 - 用于显示可复制的代码片段或报错信息。
 *
 * <h2>修复说明</h2>
 * 原实现 7 处 inline {@code setStyle}：container/header/title/lang/copyBtn/scrollPane/codeLabel
 * 全部拼字符串注入字体/颜色/边框/圆角/cursor，**完全没有一行 LESS 选择器**。
 *
 * 此外原代码有一处死代码：
 * <pre>{@code
 * HBox.setHgrow(new javafx.scene.layout.Region(), Priority.ALWAYS);
 * }</pre>
 * 创建了 Region 但从未加入容器，意图是把 copyBtn 推到右侧但实际不生效。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>所有视觉样式（背景/边框/圆角/字体/颜色）搬到 LESS 的 {@code .jfx-codeblock-*} 选择器</li>
 *   <li>修复死代码：把 spacer Region 真的加到 header 中</li>
 *   <li>copy 按钮 hover 效果由 LESS 提供（原来没有 hover 反馈）</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * Node block = CodeBlockAnt.create()
 *     .language("Error")
 *     .content("java.lang.NullPointerException: ...")
 *     .copyable(true)
 *     .build();
 * }</pre>
 */
public class CodeBlockAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String language = "";
        private String content = "";
        private boolean copyable = true;
        private boolean showLineNumbers = false;
        private String title = "";
        private Consumer<String> onCopy = null;

        private Builder() {}

        public Builder language(String language) {
            this.language = language != null ? language : "";
            return this;
        }

        public Builder content(String content) {
            this.content = content != null ? content : "";
            return this;
        }

        public Builder copyable(boolean copyable) {
            this.copyable = copyable;
            return this;
        }

        public Builder copyable() {
            return copyable(true);
        }

        public Builder showLineNumbers(boolean show) {
            this.showLineNumbers = show;
            return this;
        }

        public Builder title(String title) {
            this.title = title != null ? title : "";
            return this;
        }

        public Builder onCopy(Consumer<String> handler) {
            this.onCopy = handler;
            return this;
        }

        public Node build() {
            VBox container = new VBox(0);
            container.getStyleClass().add(CssClasses.CODEBLOCK);

            // 头部：title + lang + spacer + copy
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(CssClasses.CODEBLOCK_HEADER);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(CssClasses.CODEBLOCK_TITLE);
                header.getChildren().add(titleLabel);
            }

            if (!language.isEmpty()) {
                Label langLabel = new Label(language);
                langLabel.getStyleClass().add(CssClasses.CODEBLOCK_LANG);
                header.getChildren().add(langLabel);
            }

            // spacer：把 copyBtn 推到右边。原代码创建了 Region 但忘了 add 到容器，
            // 这里修复死代码：真的把 spacer 加进 header.children
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().add(spacer);

            if (copyable) {
                // 文案走 Messages —— 默认 zh_CN 显示"复制"，切到 en 显示"Copy"
                Button copyBtn = new Button(Messages.get("codeblock.copy"));
                copyBtn.getStyleClass().add(CssClasses.CODEBLOCK_COPY_BTN);
                // 监听 Locale 变化：未点击时若用户切语言，按钮文案需要同步刷新
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        copyBtn.setText(Messages.get("codeblock.copy")));
                copyBtn.setOnAction(e -> {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(content);
                    clipboard.setContent(clipboardContent);
                    copyBtn.setText(Messages.get("codeblock.copied"));
                    // 1.5 秒后恢复按钮文字，给用户操作反馈
                    PauseTransition pause = new PauseTransition(Duration.millis(1500));
                    pause.setOnFinished(ev -> copyBtn.setText(Messages.get("codeblock.copy")));
                    pause.play();
                    if (onCopy != null) {
                        onCopy.accept(content);
                    }
                });
                header.getChildren().add(copyBtn);
            }

            container.getChildren().add(header);

            // 内容区：ScrollPane 包 Label，等宽字体显示
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.getStyleClass().add(CssClasses.CODEBLOCK_SCROLL);
            scrollPane.setFitToWidth(true);

            Label codeLabel = new Label(content);
            codeLabel.getStyleClass().add(CssClasses.CODEBLOCK_CONTENT);
            // padding 是局部细节，仅 codeLabel 内部小留白，保留在 Java
            codeLabel.setPadding(new Insets(4));

            scrollPane.setContent(codeLabel);
            // 高度根据行数估算，限定在 [100, 300] 之间，避免短代码占位过大或长代码撑爆
            scrollPane.setPrefHeight(Math.min(300, Math.max(100, content.split("\n").length * 20 + 24)));

            container.getChildren().add(scrollPane);

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(container);
            return container;
        }
    }
}
