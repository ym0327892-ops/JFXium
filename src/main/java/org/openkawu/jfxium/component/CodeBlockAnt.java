package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * JFXium CodeBlock 组件
 * 用于显示可复制的报错信息/代码片段
 *
 * 特性：
 * - 等宽字体显示
 * - 可复制内容
 * - 看起来不像输入框（区别于 TextArea）
 * - 支持标题和语言标识
 *
 * 使用示例：
 * <pre>{@code
 * Node codeBlock = CodeBlockAnt.create()
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

    public static class Builder {
        private String language = "";
        private String content = "";
        private boolean copyable = true;
        private boolean showLineNumbers = false;
        private String title = "";
        private Consumer<String> onCopy = null;
        private String style = "";

        private Builder() {}

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
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
            this.title = title;
            return this;
        }

        public Builder onCopy(Consumer<String> handler) {
            this.onCopy = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Node build() {
            VBox container = new VBox(0);
            container.getStyleClass().add("jfx-codeblock");
            container.setStyle("-fx-background-color: -color-bg-subtle; " +
                              "-fx-border-color: -color-border-muted; " +
                              "-fx-border-width: 1px; " +
                              "-fx-border-radius: 6px; " +
                              "-fx-background-radius: 6px;");

            // 头部：标题 + 语言标签 + 复制按钮
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(8, 12, 8, 12));
            header.setStyle("-fx-background-color: -color-border-muted; " +
                           "-fx-background-radius: 6px 6px 0 0;");

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-weight: 600; " +
                                   "-fx-font-size: 13px; " +
                                   "-fx-text-fill: -color-fg-default;");
                header.getChildren().add(titleLabel);
            }

            if (!language.isEmpty()) {
                Label langLabel = new Label(language);
                langLabel.setStyle("-fx-font-size: 11px; " +
                                  "-fx-text-fill: -color-fg-muted; " +
                                  "-fx-font-family: 'Consolas', 'Monaco', monospace;");
                header.getChildren().add(langLabel);
            }

            HBox.setHgrow(new javafx.scene.layout.Region(), Priority.ALWAYS);

            if (copyable) {
                Button copyBtn = new Button("复制");
                copyBtn.setStyle("-fx-font-size: 11px; " +
                                "-fx-padding: 2px 8px; " +
                                "-fx-background-color: -color-bg-default; " +
                                "-fx-border-color: -color-border-default; " +
                                "-fx-border-width: 1px; " +
                                "-fx-border-radius: 4px; " +
                                "-fx-background-radius: 4px; " +
                                "-fx-cursor: hand;");
                copyBtn.setOnAction(e -> {
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
                    clipboardContent.putString(content);
                    clipboard.setContent(clipboardContent);
                    copyBtn.setText("已复制!");
                    javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1500));
                    pause.setOnFinished(ev -> copyBtn.setText("复制"));
                    pause.play();
                    if (onCopy != null) {
                        onCopy.accept(content);
                    }
                });
                header.getChildren().add(copyBtn);
            }

            container.getChildren().add(header);

            // 内容区域 - 使用 Label 显示，支持滚动
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setStyle("-fx-background-color: transparent; " +
                               "-fx-border-width: 0; " +
                               "-fx-padding: 12px;");
            scrollPane.setFitToWidth(true);

            Label codeLabel = new Label(content);
            codeLabel.setStyle("-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace; " +
                              "-fx-font-size: 13px; " +
                              "-fx-text-fill: -color-fg-default; " +
                              "-fx-wrap-text: true;");
            codeLabel.setPadding(new Insets(4));

            scrollPane.setContent(codeLabel);
            scrollPane.setPrefHeight(Math.min(300, Math.max(100, content.split("\n").length * 20 + 24)));

            container.getChildren().add(scrollPane);

            if (!style.isEmpty()) {
                container.setStyle(container.getStyle() + style);
            }

            return container;
        }
    }
}
