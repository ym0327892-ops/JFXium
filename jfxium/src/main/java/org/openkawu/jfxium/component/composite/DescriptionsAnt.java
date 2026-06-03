package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Descriptions - 对标 Ant Design Descriptions。
 *
 * 重构：title / label / content 全部走 LESS（{@code descriptions-*}），
 * size 通过 {@code descriptions-small/middle/large} 修饰类切换字号与 padding，
 * bordered 模式通过 {@code descriptions-bordered} 修饰类启用边框。
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
        Node content;
        int span = 1;

        public Item(String label, Node content, int span) {
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

        public Builder title(String title) { this.title = title; return this; }
        public Builder layout(Layout layout) { this.layout = layout; return this; }
        public Builder size(Size size) { this.size = size; return this; }
        public Builder column(int column) { this.column = column; return this; }
        public Builder bordered(boolean bordered) { this.bordered = bordered; return this; }
        public Builder bordered() { return bordered(true); }

        public Builder item(String label, String content) {
            items.add(new Item(label, new Label(content), 1));
            return this;
        }

        public Builder item(String label, Node content) {
            items.add(new Item(label, content, 1));
            return this;
        }

        public Builder item(String label, String content, int span) {
            items.add(new Item(label, new Label(content), span));
            return this;
        }

        public Builder item(String label, Node content, int span) {
            items.add(new Item(label, content, span));
            return this;
        }

        public VBox build() {
            VBox container = new VBox(0);
            container.getStyleClass().add(CssClasses.DESCRIPTIONS);
            // 尺寸修饰类
            String sizeClass = switch (size) {
                case SMALL -> CssClasses.DESCRIPTIONS_SIZE_SMALL;
                case MIDDLE -> CssClasses.DESCRIPTIONS_SIZE_MIDDLE;
                case LARGE -> CssClasses.DESCRIPTIONS_SIZE_LARGE;
                default -> null;
            };
            if (sizeClass != null) container.getStyleClass().add(sizeClass);
            if (bordered) container.getStyleClass().add(CssClasses.DESCRIPTIONS_BORDERED);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(CssClasses.DESCRIPTIONS_TITLE);
                container.getChildren().add(titleLabel);
            }

            if (layout == Layout.HORIZONTAL) {
                container.getChildren().add(buildHorizontal());
            } else {
                container.getChildren().add(buildVertical());
            }
            return container;
        }

        private GridPane buildHorizontal() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(CssClasses.DESCRIPTIONS_GRID);
            grid.setHgap(0);
            grid.setVgap(0);

            int currentRow = 0;
            int currentCol = 0;

            for (Item item : items) {
                Label label = new Label(item.label);
                label.getStyleClass().add(CssClasses.DESCRIPTIONS_LABEL);

                Node content = item.content;
                // content 如果是 Label，自动挂 styleClass
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add(CssClasses.DESCRIPTIONS_CONTENT);
                }

                int labelSpan = 1;
                int contentSpan = item.span;

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
            container.getStyleClass().add(CssClasses.DESCRIPTIONS_VERTICAL);

            int currentCol = 0;
            HBox rowBox = null;

            for (Item item : items) {
                if (currentCol == 0) {
                    rowBox = new HBox(0);
                    rowBox.setAlignment(Pos.TOP_LEFT);
                }

                VBox itemBox = new VBox(4);
                itemBox.setAlignment(Pos.TOP_LEFT);
                HBox.setHgrow(itemBox, Priority.ALWAYS);

                Label label = new Label(item.label);
                label.getStyleClass().add(CssClasses.DESCRIPTIONS_LABEL);
                itemBox.getChildren().add(label);

                Node content = item.content;
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add(CssClasses.DESCRIPTIONS_CONTENT);
                }
                itemBox.getChildren().add(content);

                rowBox.getChildren().add(itemBox);
                currentCol++;

                if (currentCol >= column) {
                    container.getChildren().add(rowBox);
                    currentCol = 0;
                }
            }

            if (currentCol > 0 && rowBox != null) {
                container.getChildren().add(rowBox);
            }
            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
