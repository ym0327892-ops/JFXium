package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium 抽屉组件 - 对标 Ant Design Drawer 6.x 规范。
 *
 * <p><b>定位</b>：从屏幕边缘滑入的覆盖式面板，适合详情展示、表单编辑、筛选面板等场景。
 * 比 ModalAnt 更适合展示大量内容，不挡住整个视口。</p>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>从屏幕边缘滑入，覆盖部分父窗体内容</li>
 *   <li>默认宽度 378px，LARGE 尺寸 736px</li>
 *   <li>关闭按钮位置可配：LEFT（Ant 默认）/ RIGHT / NONE</li>
 *   <li>头部：标题 + extra 操作区，底部边框</li>
 *   <li>内容：padding 24px，flex:1 自适应高度</li>
 *   <li>底部：可选 footer，顶部边框</li>
 *   <li>动画：Slide + Fade，250ms ease-out</li>
 *   <li>遮罩：rgba(0,0,0,0.45)，点击可关闭</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 右侧抽屉（默认）
 * DrawerAnt.create()
 *     .title("用户详情")
 *     .content(userDetailNode)
 *     .placement(DrawerAnt.Placement.RIGHT)
 *     .onClose(ok -> System.out.println("已关闭"))
 *     .build()
 *     .open(ownerNode);
 *
 * // 底部抽屉 + footer
 * DrawerAnt.create()
 *     .title("高级筛选")
 *     .content(filterFormNode)
 *     .placement(DrawerAnt.Placement.BOTTOM)
 *     .height(400)
 *     .footer(ButtonAnt.create("应用").type(ButtonAnt.Type.PRIMARY).build())
 *     .build()
 *     .open(ownerNode);
 *
 * // LARGE 尺寸 + 无关闭按钮
 * DrawerAnt.create()
 *     .title("文件预览")
 *     .content(filePreviewNode)
 *     .size(DrawerAnt.Size.LARGE)
 *     .closePlacement(DrawerAnt.ClosePlacement.NONE)
 *     .build()
 *     .open(ownerNode);
 * }</pre>
 *
 * @see ModalAnt 居中对话框（适合确认性操作）
 */
public class DrawerAnt {

    public enum Placement {
        LEFT, RIGHT, TOP, BOTTOM
    }

    public enum Size {
        DEFAULT, LARGE
    }

    /**
     * 关闭按钮在 header 中的位置。
     * <ul>
     *   <li>{@link #LEFT}：左上（Ant Design Drawer 默认行为）</li>
     *   <li>{@link #RIGHT}：右上（更接近 Web 习惯，与 Modal 一致）</li>
     *   <li>{@link #NONE}：不显示关闭按钮，依靠点击遮罩 / ESC / 自定义 footer 按钮关闭</li>
     * </ul>
     * 选择 {@link #NONE} 时，{@code maskClosable} 与 {@code keyboard} 不能同时为 false，
     * 否则 build() 会抛出 {@link IllegalStateException}（避免出现"无法关闭的弹窗"）。
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
        private Placement placement = Placement.RIGHT;
        private int width = 378;
        private int height = 378;
        private boolean maskClosable = true;
        private Consumer<Boolean> onClose = null;
        private Node footer = null;
        private Size size = Size.DEFAULT;
        private Node extra = null;
        private boolean keyboard = true;
        // 默认 LEFT，对齐 Ant Design Drawer 标准
        private ClosePlacement closePlacement = ClosePlacement.LEFT;

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

        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder maskClosable(boolean maskClosable) {
            this.maskClosable = maskClosable;
            return this;
        }

        public Builder onClose(Consumer<Boolean> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            if (size == Size.LARGE) {
                this.width = 736;
                this.height = 736;
            }
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder keyboard(boolean keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        /**
         * 设置关闭按钮位置。默认 {@link ClosePlacement#LEFT}（Ant Design Drawer 标准）。
         * 选择 {@link ClosePlacement#NONE} 时必须保留至少一种可关闭路径
         * （{@code maskClosable=true} 或 {@code keyboard=true}），否则 build() 会抛异常。
         */
        public Builder closePlacement(ClosePlacement closePlacement) {
            this.closePlacement = closePlacement;
            return this;
        }

        public DrawerResult build() {
            // 防呆：选了 NONE 但又禁用了所有可关闭路径 → 抛异常给开发者看，避免"无法关闭的弹窗"
            if (closePlacement == ClosePlacement.NONE && !maskClosable && !keyboard) {
                throw new IllegalStateException(
                        "DrawerAnt: closePlacement=NONE 时必须保留至少一种关闭路径"
                                + "（maskClosable=true 或 keyboard=true），"
                                + "否则用户将无法关闭抽屉。");
            }
            return new DrawerResult(this);
        }
    }

    public static class DrawerResult {
        private final Builder config;
        private Stage stage;
        private StackPane overlay;
        private VBox drawerPanel;
        private boolean isOpen = false;

        DrawerResult(Builder config) {
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

            // 创建 Drawer 面板
            drawerPanel = createDrawerPanel();
            positionPanel();

            overlay.getChildren().add(drawerPanel);
            StackPane.setAlignment(drawerPanel, getAlignment());

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
                if (config.placement == Placement.TOP || config.placement == Placement.BOTTOM) {
                    drawerPanel.setPrefWidth(val.doubleValue());
                    // 保持 max size 约束
                    drawerPanel.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                }
            };
            javafx.beans.value.ChangeListener<Number> heightListener = (obs, old, val) -> {
                overlay.setPrefSize(ownerWindow.getWidth(), val.doubleValue());
                stage.setHeight(val.doubleValue());
                if (config.placement == Placement.LEFT || config.placement == Placement.RIGHT) {
                    drawerPanel.setPrefHeight(val.doubleValue());
                    // 保持 max size 约束
                    drawerPanel.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                }
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

        private VBox createDrawerPanel() {
            VBox panel = new VBox(0);
            panel.getStyleClass().addAll(
                    org.openkawu.jfxium.core.css.CssClasses.DRAWER,
                    org.openkawu.jfxium.core.css.CssClasses.OVERLAY_PANEL
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
            if (config.footer != null) {
                HBox footerBox = createFooter(config.footer);
                panel.getChildren().add(footerBox);
            }

            return panel;
        }

        private HBox createHeader() {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_HEADER);

            // 关闭按钮：根据 closePlacement 决定渲染位置
            // - LEFT: 在 title 之前（Ant Drawer 默认）
            // - RIGHT: 在 title/extra 之后（Modal 风格）
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

            // 4. Extra 节点（用户自定义按钮组）
            if (config.extra != null) {
                header.getChildren().add(config.extra);
            }

            // 5. 右侧关闭按钮（可选）
            if (closeBtn != null && config.closePlacement == ClosePlacement.RIGHT) {
                header.getChildren().add(closeBtn);
            }

            return header;
        }

        private HBox createFooter(Node footerContent) {
            HBox footer = new HBox(footerContent);
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.OVERLAY_FOOTER);
            return footer;
        }

        private void positionPanel() {
            switch (config.placement) {
                case LEFT, RIGHT -> {
                    drawerPanel.setPrefWidth(config.width);
                    drawerPanel.setPrefHeight(overlay.getPrefHeight());
                    // 关键：防止 StackPane 拉伸（SKILL §20.1）
                    drawerPanel.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                    drawerPanel.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                }
                case TOP, BOTTOM -> {
                    drawerPanel.setPrefWidth(overlay.getPrefWidth());
                    drawerPanel.setPrefHeight(config.height);
                    // 关键：防止 StackPane 拉伸（SKILL §20.1）
                    drawerPanel.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                    drawerPanel.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                }
            }
        }

        private Pos getAlignment() {
            return switch (config.placement) {
                case LEFT -> Pos.CENTER_LEFT;
                case RIGHT -> Pos.CENTER_RIGHT;
                case TOP -> Pos.TOP_CENTER;
                case BOTTOM -> Pos.BOTTOM_CENTER;
            };
        }

        private void animateIn() {
            drawerPanel.setOpacity(0);

            FadeTransition fade = new FadeTransition(Duration.millis(250), drawerPanel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slide = new TranslateTransition(Duration.millis(250), drawerPanel);
            switch (config.placement) {
                case LEFT -> {
                    slide.setFromX(-config.width);
                    slide.setToX(0);
                }
                case RIGHT -> {
                    slide.setFromX(config.width);
                    slide.setToX(0);
                }
                case TOP -> {
                    slide.setFromY(-config.height);
                    slide.setToY(0);
                }
                case BOTTOM -> {
                    slide.setFromY(config.height);
                    slide.setToY(0);
                }
            }
            slide.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, slide);
            pt.play();
        }

        private void animateOut(Runnable onFinished) {
            FadeTransition fade = new FadeTransition(Duration.millis(200), drawerPanel);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setInterpolator(Interpolator.EASE_IN);

            TranslateTransition slide = new TranslateTransition(Duration.millis(200), drawerPanel);
            switch (config.placement) {
                case LEFT -> slide.setToX(-config.width);
                case RIGHT -> slide.setToX(config.width);
                case TOP -> slide.setToY(-config.height);
                case BOTTOM -> slide.setToY(config.height);
            }
            slide.setInterpolator(Interpolator.EASE_IN);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, slide);
            pt.setOnFinished(e -> onFinished.run());
            pt.play();
        }
    }
}
