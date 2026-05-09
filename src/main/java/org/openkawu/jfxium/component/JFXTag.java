package org.openkawu.jfxium.component;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * JFXium Tag Component
 * Inspired by Ant Design Tag
 */
public class JFXTag {

    public enum Type {
        DEFAULT, PRIMARY, SUCCESS, WARNING, ERROR, PROCESSING
    }

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public enum Shape {
        DEFAULT, ROUND, SQUARE
    }

    public static class Builder {
        private String text = "";
        private Type type = Type.DEFAULT;
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.DEFAULT;
        private boolean closable = false;
        private boolean bordered = true;
        private Color customColor = null;
        private Runnable onClose = null;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder closable() {
            return closable(true);
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder noBorder() {
            return bordered(false);
        }

        public Builder color(Color color) {
            this.customColor = color;
            return this;
        }

        public Builder onClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public HBox build() {
            HBox tag = new HBox(4);
            tag.setAlignment(javafx.geometry.Pos.CENTER);

            // Base style class
            tag.getStyleClass().add("tag");
            tag.getStyleClass().add(type.name().toLowerCase());
            if (size != Size.DEFAULT) {
                tag.getStyleClass().add(size.name().toLowerCase());
            }
            if (shape != Shape.DEFAULT) {
                tag.getStyleClass().add(shape.name().toLowerCase());
            }
            if (!bordered) {
                tag.getStyleClass().add("no-border");
            }

            // Apply inline styles based on type
            applyTypeStyles(tag);

            // Label
            Label label = new Label(text);
            label.getStyleClass().add("tag-label");
            tag.getChildren().add(label);

            // Close button
            if (closable) {
                StackPane closeBtn = createCloseButton();
                closeBtn.setOnMouseClicked(e -> {
                    if (onClose != null) {
                        onClose.run();
                    }
                    tag.setVisible(false);
                    tag.setManaged(false);
                });
                tag.getChildren().add(closeBtn);
            }

            return tag;
        }

        private void applyTypeStyles(HBox tag) {
            String bgColor, textColor, borderColor;

            switch (type) {
                case PRIMARY -> {
                    bgColor = "-color-accent-subtle";
                    textColor = "-color-accent-emphasis";
                    borderColor = "-color-accent-muted";
                }
                case SUCCESS -> {
                    bgColor = "-color-success-subtle";
                    textColor = "-color-success-emphasis";
                    borderColor = "-color-success-muted";
                }
                case WARNING -> {
                    bgColor = "-color-warning-subtle";
                    textColor = "-color-warning-emphasis";
                    borderColor = "-color-warning-muted";
                }
                case ERROR -> {
                    bgColor = "-color-danger-subtle";
                    textColor = "-color-danger-emphasis";
                    borderColor = "-color-danger-muted";
                }
                case PROCESSING -> {
                    bgColor = "-color-accent-subtle";
                    textColor = "-color-accent-emphasis";
                    borderColor = "-color-accent-muted";
                }
                default -> {
                    bgColor = "-color-bg-subtle";
                    textColor = "-color-fg-default";
                    borderColor = "-color-border-default";
                }
            }

            StringBuilder style = new StringBuilder();
            style.append("-fx-background-color: ").append(bgColor).append(";");
            style.append(" -fx-text-fill: ").append(textColor).append(";");
            if (bordered) {
                style.append(" -fx-border-color: ").append(borderColor).append(";");
                style.append(" -fx-border-width: 1px;");
            }
            style.append(" -fx-padding: ").append(getPadding()).append(";");
            style.append(" -fx-background-radius: ").append(getRadius()).append(";");
            style.append(" -fx-border-radius: ").append(getRadius()).append(";");
            style.append(" -fx-font-size: ").append(getFontSize()).append(";");

            tag.setStyle(style.toString());
        }

        private String getPadding() {
            return switch (size) {
                case SMALL -> "0 6px";
                case LARGE -> "4px 12px";
                default -> "2px 8px";
            };
        }

        private String getRadius() {
            return switch (shape) {
                case ROUND -> "9999px";
                case SQUARE -> "2px";
                default -> "4px";
            };
        }

        private String getFontSize() {
            return switch (size) {
                case SMALL -> "12px";
                case LARGE -> "16px";
                default -> "14px";
            };
        }

        private StackPane createCloseButton() {
            StackPane closeBtn = new StackPane();
            closeBtn.getStyleClass().add("tag-close");
            closeBtn.setPrefSize(12, 12);
            closeBtn.setMaxSize(12, 12);

            SVGPath x = new SVGPath();
            x.setContent("M6 4.5L4.5 6 6 7.5 7.5 6 6 4.5z");
            x.setFill(Color.web("#8c959f"));
            closeBtn.getChildren().add(x);

            closeBtn.setOnMouseEntered(e -> closeBtn.setOpacity(0.8));
            closeBtn.setOnMouseExited(e -> closeBtn.setOpacity(1.0));

            return closeBtn;
        }
    }

    public static Builder create(String text) {
        return new Builder().text(text);
    }

    public static Builder create() {
        return new Builder();
    }
}
