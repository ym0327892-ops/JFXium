package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 描述列表组件 - 对标 Ant Design Descriptions。
 *
 * <p><b>定位</b>：以列表形式展示多个字段的详情（标签 + 值对），常用于详情页、
 * 用户资料、订单详情等场景。支持水平/垂直布局、多种尺寸、带边框模式。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>布局</b>：HORIZONTAL（默认）/ VERTICAL（标签在上、值在下）</li>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / MIDDLE / LARGE</li>
 *   <li><b>边框</b>：bordered(true) 启用表格风格边框</li>
 *   <li><b>列数</b>：column(n) 控制每行显示几个字段</li>
 *   <li><b>标题</b>：title(text) 顶部标题</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox desc = DescriptionsAnt.create()
 *     .title("用户信息")
 *     .bordered(true)
 *     .column(2)
 *     .item("姓名", "张三")
 *     .item("邮箱", "zhangsan@example.com")
 *     .item("角色", "管理员")
 *     .item("状态", "在线")
 *     .build();
 * }</pre>
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

    public static class Builder extends AbstractStyleBuilder<Builder> {
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
            container.getStyleClass().add(JfxStyles.DESCRIPTIONS);
            // 尺寸修饰类
            String sizeClass = switch (size) {
                case SMALL -> JfxStyles.DESCRIPTIONS_SIZE_SMALL;
                case MIDDLE -> JfxStyles.DESCRIPTIONS_SIZE_MIDDLE;
                case LARGE -> JfxStyles.DESCRIPTIONS_SIZE_LARGE;
                default -> null;
            };
            if (sizeClass != null) container.getStyleClass().add(sizeClass);
            if (bordered) container.getStyleClass().add(JfxStyles.DESCRIPTIONS_BORDERED);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(JfxStyles.DESCRIPTIONS_TITLE);
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
            grid.getStyleClass().add(JfxStyles.DESCRIPTIONS_GRID);
            grid.setHgap(0);
            grid.setVgap(0);

            int currentRow = 0;
            int currentCol = 0;

            for (Item item : items) {
                Label label = new Label(item.label);
                label.getStyleClass().add(JfxStyles.DESCRIPTIONS_LABEL);

                Node content = item.content;
                // content 如果是 Label，自动挂 styleClass
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add(JfxStyles.DESCRIPTIONS_CONTENT);
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
            container.getStyleClass().add(JfxStyles.DESCRIPTIONS_VERTICAL);

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
                label.getStyleClass().add(JfxStyles.DESCRIPTIONS_LABEL);
                itemBox.getChildren().add(label);

                Node content = item.content;
                if (content instanceof Label contentLabel) {
                    contentLabel.getStyleClass().add(JfxStyles.DESCRIPTIONS_CONTENT);
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
