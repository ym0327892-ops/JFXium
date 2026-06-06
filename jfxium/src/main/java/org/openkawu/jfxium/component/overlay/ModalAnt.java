package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.function.Consumer;

/**
 * JFXium 对话框组件 - 对标 Ant Design Modal 6.x 规范。
 *
 * <p><b>定位</b>：屏幕中央弹出的模态对话框，适合需要用户明确响应的场景（确认、警告、表单输入）。
 * 比 DrawerAnt 更适合简短交互，比 PopconfirmAnt 更重（支持自定义 footer）。</p>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>遮罩层：rgba(0,0,0,0.45)，覆盖整个视口</li>
 *   <li>内容区：垂直居中或顶部偏移（centered），宽度默认 520px，圆角 8px</li>
 *   <li>头部：标题 + 关闭按钮，关闭按钮位置可配：LEFT / RIGHT（默认）/ NONE</li>
 *   <li>默认 footer：取消 + 确定按钮（支持 i18n / confirmLoading / 自定义文案）</li>
 *   <li>动画：Zoom(0.9→1) + Fade，200ms ease-out</li>
 *   <li>ESC 关闭 / 点击遮罩关闭（均可配置禁用）</li>
 *   <li>静态快捷方法：info() / confirm()</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 确认对话框
 * ModalAnt.confirm("确认删除？", "删除后无法恢复", ownerNode, () -> doDelete());
 *
 * // Builder 用法：自定义 footer
 * ModalAnt.create()
 *     .title("编辑用户")
 *     .content(editFormNode)
 *     .width(640)
 *     .onOk(() -> saveUser())
 *     .build()
 *     .open(ownerNode);
 *
 * // 无 footer + 不居中
 * ModalAnt.create()
 *     .title("系统提示")
 *     .content("当前版本已过期，请更新")
 *     .centered(false)
 *     .noFooter()
 *     .build()
 *     .open(ownerNode);
 * }</pre>
 *
 * @see DrawerAnt 抽屉式面板（适合大量内容展示）
 * @see PopconfirmAnt 轻量气泡确认框
 */
public class ModalAnt {

    /**
     * 关闭按钮在 header 中的位置。
     * <ul>
     *   <li>{@link #LEFT}：左上（Drawer 风格）</li>
     *   <li>{@link #RIGHT}：右上（Ant Design Modal 默认）</li>
     *   <li>{@link #NONE}：不显示关闭按钮，依靠点击遮罩 / ESC / 默认 footer 的 Cancel 按钮关闭</li>
     * </ul>
     * 选择 {@link #NONE} 时，{@code maskClosable} 与 {@code keyboard} 不能同时为 false
     * 且没有自定义 footer——否则 build() 会抛出 {@link IllegalStateException}。
     */
    public enum ClosePlacement {
        LEFT, RIGHT, NONE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node content = null;
        private boolean maskClosable = true;
        private int width = 520;
        private Consumer<Boolean> onClose = null;
        private Node footer = null;
        private boolean centered = true;
        private boolean keyboard = true;
        private boolean confirmLoading = false;
        // null = 用 i18n 默认值；非 null = 调用方显式指定
        private String okText = null;
        private String cancelText = null;
        private Runnable onOk = null;
        private boolean noDefaultFooter = false;
        // 默认 RIGHT，对齐 Ant Design Modal 标准
        private ClosePlacement closePlacement = ClosePlacement.RIGHT;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder content(String text) {
            this.content = new Label(text);
            return this;
        }

        public Builder maskClosable(boolean maskClosable) {
            this.maskClosable = maskClosable;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder onClose(Consumer<Boolean> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            this.noDefaultFooter = true;
            return this;
        }

        public Builder noFooter() {
            this.noDefaultFooter = true;
            this.footer = null;
            return this;
        }

        public Builder centered(boolean centered) {
            this.centered = centered;
            return this;
        }

        public Builder centered() {
            return centered(true);
        }

        public Builder keyboard(boolean keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        /**
         * 设置关闭按钮位置。默认 {@link ClosePlacement#RIGHT}（Ant Design Modal 标准）。
         * 选择 {@link ClosePlacement#NONE} 时必须保留至少一种关闭路径
         * （maskClosable=true / keyboard=true / 默认 Cancel footer），否则 build() 抛异常。
         */
        public Builder closePlacement(ClosePlacement closePlacement) {
            this.closePlacement = closePlacement;
            return this;
        }

        public Builder confirmLoading(boolean confirmLoading) {
            this.confirmLoading = confirmLoading;
            return this;
        }

        public Builder okText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder onOk(Runnable onOk) {
            this.onOk = onOk;
            return this;
        }

        public ModalResult build() {
            // 防呆：closePlacement=NONE 时必须保留至少一种关闭路径。
            // Modal 的默认 footer 含 Cancel 按钮，可作为关闭路径，所以"无 close + 无 mask + 无 ESC"
            // 仅在用户主动 noFooter() 或 footer(自定义节点不含关闭逻辑) 时才出问题。
            // 这里只校验最严格情况：NONE + 无 mask + 无 ESC + 用户禁用了默认 footer。
            if (closePlacement == ClosePlacement.NONE
                    && !maskClosable && !keyboard
                    && noDefaultFooter && footer == null) {
                throw new IllegalStateException(
                        "ModalAnt: closePlacement=NONE 时必须保留至少一种关闭路径"
                                + "（maskClosable / keyboard / 默认 Cancel footer / 自定义 footer），"
                                + "否则用户将无法关闭对话框。");
            }
            return new ModalResult(this);
        }
    }

    public static class ModalResult {
        private final Builder config;
        private Stage stage;
        private StackPane overlay;
        private VBox modalPanel;
        private boolean isOpen = false;

        ModalResult(Builder config) {
            this.config = config;
        }

        public void open(Node owner) {
            if (isOpen) return;
            isOpen = true;

            javafx.stage.Window ownerWindow = owner.getScene().getWindow();

            // 创建遮罩层 Stage（无装饰，透明背景）
            stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);

            // 遮罩层覆盖整个屏幕
            overlay = new StackPane();
            overlay.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_MASK);
            overlay.setPrefSize(ownerWindow.getWidth(), ownerWindow.getHeight());

            // 创建 Modal 面板
            modalPanel = createModalPanel();
            modalPanel.setMaxWidth(config.width);
            modalPanel.setMinWidth(config.width);

            overlay.getChildren().add(modalPanel);
            if (config.centered) {
                StackPane.setAlignment(modalPanel, Pos.CENTER);
            } else {
                StackPane.setAlignment(modalPanel, Pos.TOP_CENTER);
                StackPane.setMargin(modalPanel, new Insets(100, 0, 0, 0));
            }

            // 点击遮罩关闭
            if (config.maskClosable) {
                overlay.setOnMouseClicked(e -> {
                    if (e.getTarget() == overlay) {
                        close();
                    }
                });
            }

            // 键盘 ESC 关闭
            if (config.keyboard) {
                overlay.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        close();
                        e.consume();
                    }
                });
                overlay.setFocusTraversable(true);
            }

            Scene scene = new Scene(overlay);
            scene.setFill(Color.TRANSPARENT);

            // 绑定主题 CSS
            String themeCss = owner.getScene().getStylesheets().stream()
                .filter(s -> s.contains("theme-"))
                .findFirst()
                .orElse(null);
            if (themeCss != null) {
                scene.getStylesheets().add(themeCss);
            }

            stage.setScene(scene);
            stage.setWidth(ownerWindow.getWidth());
            stage.setHeight(ownerWindow.getHeight());
            stage.setX(ownerWindow.getX());
            stage.setY(ownerWindow.getY());

            // 跟随 owner 窗口移动和 resize
            javafx.beans.value.ChangeListener<Number> widthListener = (obs, old, val) -> {
                overlay.setPrefSize(val.doubleValue(), ownerWindow.getHeight());
                stage.setWidth(val.doubleValue());
            };
            javafx.beans.value.ChangeListener<Number> heightListener = (obs, old, val) -> {
                overlay.setPrefSize(ownerWindow.getWidth(), val.doubleValue());
                stage.setHeight(val.doubleValue());
            };
            javafx.beans.value.ChangeListener<Number> xListener = (obs, old, val) -> stage.setX(val.doubleValue());
            javafx.beans.value.ChangeListener<Number> yListener = (obs, old, val) -> stage.setY(val.doubleValue());

            ownerWindow.widthProperty().addListener(widthListener);
            ownerWindow.heightProperty().addListener(heightListener);
            ownerWindow.xProperty().addListener(xListener);
            ownerWindow.yProperty().addListener(yListener);

            stage.setOnHidden(e -> {
                ownerWindow.widthProperty().removeListener(widthListener);
                ownerWindow.heightProperty().removeListener(heightListener);
                ownerWindow.xProperty().removeListener(xListener);
                ownerWindow.yProperty().removeListener(yListener);
            });

            stage.show();
            overlay.requestFocus();

            animateIn();
        }

        public void close() {
            if (!isOpen) return;
            isOpen = false;

            animateOut(() -> {
                if (stage != null) {
                    stage.hide();
                }
                if (config.onClose != null) {
                    config.onClose.accept(false);
                }
            });
        }

        private VBox createModalPanel() {
            VBox panel = new VBox(0);
            // Modal 用圆角面板（Drawer 不带圆角）
            panel.getStyleClass().addAll(
                    org.openkawu.jfxium.core.css.CssClasses.MODAL,
                    org.openkawu.jfxium.core.css.CssClasses.OVERLAY_PANEL,
                    org.openkawu.jfxium.core.css.CssClasses.OVERLAY_PANEL_ROUNDED
            );

            // Header
            if (!config.title.isEmpty()) {
                HBox header = createHeader();
                panel.getChildren().add(header);
            }

            // Body
            if (config.content != null) {
                VBox body = new VBox(config.content);
                body.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_BODY);
                VBox.setVgrow(body, Priority.ALWAYS);
                panel.getChildren().add(body);
            }

            // Footer
            if (config.noDefaultFooter && config.footer != null) {
                HBox footerBox = createCustomFooter(config.footer);
                panel.getChildren().add(footerBox);
            } else if (!config.noDefaultFooter) {
                HBox defaultFooter = createDefaultFooter();
                panel.getChildren().add(defaultFooter);
            }

            return panel;
        }

        private HBox createHeader() {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_HEADER);

            // 关闭按钮：根据 closePlacement 决定渲染位置
            // - LEFT: 在 title 之前
            // - RIGHT: 在 title 之后（Ant Modal 默认）
            // - NONE: 不渲染
            javafx.scene.control.Button closeBtn = null;
            if (config.closePlacement != ClosePlacement.NONE) {
                closeBtn = new javafx.scene.control.Button("×");
                closeBtn.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_CLOSE_BTN);
                closeBtn.setOnAction(e -> close());
            }

            // 1. 左侧关闭按钮（可选）
            if (closeBtn != null && config.closePlacement == ClosePlacement.LEFT) {
                header.getChildren().add(closeBtn);
            }

            // 2. 标题文本（默认占自己宽度，不抢空间）
            Label titleLabel = new Label(config.title);
            titleLabel.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_TITLE);
            header.getChildren().add(titleLabel);

            // 3. 弹性填充（关键：把右侧推到最右）
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            header.getChildren().add(spacer);

            // 4. 右侧关闭按钮（可选）
            if (closeBtn != null && config.closePlacement == ClosePlacement.RIGHT) {
                header.getChildren().add(closeBtn);
            }

            return header;
        }

        private HBox createDefaultFooter() {
            // null 表示调用方未指定，走 i18n 默认值
            String resolvedCancel = config.cancelText != null ? config.cancelText : Messages.get("modal.cancel");
            String resolvedOk = config.okText != null ? config.okText : Messages.get("modal.ok");
            javafx.scene.control.Button cancelBtn = ButtonAnt.create(resolvedCancel)
                .type(ButtonAnt.Type.DEFAULT)
                .onClick(e -> close())
                .build();

            javafx.scene.control.Button okBtn = ButtonAnt.create(resolvedOk)
                .type(ButtonAnt.Type.PRIMARY)
                .loading(config.confirmLoading)
                .onClick(e -> {
                    if (config.onOk != null) {
                        config.onOk.run();
                    }
                    close();
                })
                .build();

            // 监听 locale 变化：若未显式指定 ok/cancel 文案，需同步刷新
            if (config.cancelText == null) {
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        cancelBtn.setText(Messages.get("modal.cancel")));
            }
            if (config.okText == null) {
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        okBtn.setText(Messages.get("modal.ok")));
            }

            HBox footer = new HBox(8, cancelBtn, okBtn);
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_FOOTER);
            return footer;
        }

        private HBox createCustomFooter(Node footerContent) {
            HBox footer = new HBox(footerContent);
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_FOOTER);
            return footer;
        }

        private void animateIn() {
            modalPanel.setOpacity(0);
            modalPanel.setScaleX(0.9);
            modalPanel.setScaleY(0.9);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modalPanel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), modalPanel);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1);
            scale.setToY(1);
            scale.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, scale);
            pt.play();
        }

        private void animateOut(Runnable onFinished) {
            FadeTransition fade = new FadeTransition(Duration.millis(150), modalPanel);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setInterpolator(Interpolator.EASE_IN);

            ScaleTransition scale = new ScaleTransition(Duration.millis(150), modalPanel);
            scale.setFromX(1);
            scale.setFromY(1);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.setInterpolator(Interpolator.EASE_IN);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, scale);
            pt.setOnFinished(e -> onFinished.run());
            pt.play();
        }
    }

    public static void info(String title, String content, Node owner) {
        create().title(title).content(content).build().open(owner);
    }

    public static void confirm(String title, String content, Node owner, Runnable onOk) {
        create()
            .title(title)
            .content(content)
            .onOk(onOk)
            .build()
            .open(owner);
    }
}
