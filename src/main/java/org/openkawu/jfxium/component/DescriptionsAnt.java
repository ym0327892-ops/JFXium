package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Descriptions Component
 * Inspired by Ant Design Descriptions
 * Display multiple read-only fields in groups.
 */
public class DescriptionsAnt {

    public enum Layout {
        HORIZONTAL, VERTICAL
    }

    public enum Size {
        SMALL, DEFAULT, MIDDLE, LARGE
    }

    public static class Item {
        String label;
        javafx.scene.Node content;
        int span = 1;

        public Item(String label, javafx.scene.Node content, int span) {
            this.label = label;
            this.content = content;
            this.span = span;
        }
    }

    public static class Builder {
        private String title = "";
        private Layout layout = Layout.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private int column = 3;
        private boolean bordered = false;
        private List<Item> items = new ArrayList<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder column(int column) {
            this.column = column;
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder bordered() {
            return bordered(true);
        }

        public Builder item(String label, String content) {
            items.add(new Item(label, new Label(content), 1));
            return this;
        }

        public Builder item(String label, javafx.scene.Node content) {
            items.add(new Item(label, content, 1));
            return this;
        }

        public Builder item(String label, String content, int span) {
            items.add(new Item(label, new Label(content), span));
            return this;
        }

        public Builder item(String label, javafx.scene.Node content, int span) {
            items.add(new Item(label, content, span));
            return this;
        }

        public VBox build() {
            VBox container = new VBox(0);
            container.getStyleClass().add("descriptions");

            // Title
            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default; -fx-padding: 0 0 16px 0;");
                container.getChildren().add(titleLabel);
            }

            // Content
            if (layout == Layout.HORIZONTAL) {
                container.getChildren().add(buildHorizontal());
            } else {
                container.getChildren().add(buildVertical());
            }

            return container;
        }

        private GridPane buildHorizontal() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add("descriptions-grid");
            grid.setHgap(0);
            grid.setVgap(0);

            int currentRow = 0;
            int currentCol = 0;

            for (Item item : items) {
                // Label cell
                Label label = new Label(item.label);
                label.getStyleClass().add("descriptions-label");
                String labelStyle = getLabelStyle();
                label.setStyle(labelStyle);

                // Content cell
                javafx.scene.Node content = item.content;
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add("descriptions-content");
                    contentLabel.setStyle(getContentStyle());
                }

                if (bordered) {
                    label.setStyle(labelStyle + "-fx-border-color: -color-border-default; -fx-border-width: 1px; -fx-border-radius: 0;");
                    if (content instanceof Label contentLabel) {
                        contentLabel.setStyle(getContentStyle() + "-fx-border-color: -color-border-default; -fx-border-width: 1px; -fx-border-radius: 0;");
                    } else {
                        if (content instanceof HBox hbox) {
                            hbox.setStyle("-fx-border-color: -color-border-default; -fx-border-width: 1px; -fx-padding: " + getPadding() + ";");
                        } else if (content instanceof VBox vbox) {
                            vbox.setStyle("-fx-border-color: -color-border-default; -fx-border-width: 1px; -fx-padding: " + getPadding() + ";");
                        }
                    }
                }

                // Calculate colspan
                int labelSpan = 1;
                int contentSpan = item.span;

                // Check if fits in current row
                if (currentCol + labelSpan + contentSpan > column * 2) {
                    currentRow++;
                    currentCol = 0;
                }

                grid.add(label, currentCol, currentRow);
                GridPane.setColumnSpan(label, labelSpan);
                grid.add(content, currentCol + labelSpan, currentRow);
                GridPane.setColumnSpan(content, contentSpan);

                currentCol += labelSpan + contentSpan;
                if (currentCol >= column * 2) {
                    currentRow++;
                    currentCol = 0;
                }
            }

            return grid;
        }

        private VBox buildVertical() {
            VBox container = new VBox(0);
            container.getStyleClass().add("descriptions-vertical");

            int currentRow = 0;
            int currentCol = 0;
            HBox rowBox = null;

            for (Item item : items) {
                if (currentCol == 0) {
                    rowBox = new HBox(0);
                    rowBox.setAlignment(Pos.TOP_LEFT);
                }

                VBox itemBox = new VBox(4);
                itemBox.setAlignment(Pos.TOP_LEFT);
                HBox.setHgrow(itemBox, javafx.scene.layout.Priority.ALWAYS);

                // Label
                Label label = new Label(item.label);
                label.getStyleClass().add("descriptions-label");
                label.setStyle(getLabelStyle());
                itemBox.getChildren().add(label);

                // Content
                javafx.scene.Node content = item.content;
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add("descriptions-content");
                    contentLabel.setStyle(getContentStyle());
                }
                itemBox.getChildren().add(content);

                if (bordered) {
                    itemBox.setStyle("-fx-border-color: -color-border-default; -fx-border-width: 1px; -fx-padding: " + getPadding() + ";");
                }

                rowBox.getChildren().add(itemBox);
                currentCol++;

                if (currentCol >= column) {
                    container.getChildren().add(rowBox);
                    currentCol = 0;
                }
            }

            // Add remaining items
            if (currentCol > 0 && rowBox != null) {
                container.getChildren().add(rowBox);
            }

            return container;
        }

        private String getLabelStyle() {
            return "-fx-text-fill: -color-fg-muted; -fx-font-size: " + getFontSize() + "; -fx-padding: " + getPadding() + "; -fx-font-weight: 400;";
        }

        private String getContentStyle() {
            return "-fx-text-fill: -color-fg-default; -fx-font-size: " + getFontSize() + "; -fx-padding: " + getPadding() + "; -fx-font-weight: 500;";
        }

        private String getFontSize() {
            return switch (size) {
                case SMALL -> "12px";
                case MIDDLE -> "13px";
                case LARGE -> "16px";
                default -> "14px";
            };
        }

        private String getPadding() {
            return switch (size) {
                case SMALL -> "4px 8px";
                case MIDDLE -> "8px 12px";
                case LARGE -> "16px 24px";
                default -> "8px 16px";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
