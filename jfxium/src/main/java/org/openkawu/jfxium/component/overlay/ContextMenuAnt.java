package org.openkawu.jfxium.component.overlay;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 右键菜单组件 - 对标 Ant Design Dropdown（右键触发场景）。
 *
 * <p><b>定位</b>：在目标元素上右键弹出的上下文菜单，支持分组、图标、快捷键提示、
 * 分隔线。与 {@link DropdownAnt}（左键点击下拉）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>触发</b>：绑定到任意 Node 的右键事件（CONTEXT_MENU）</li>
 *   <li><b>菜单项</b>：item(key, label, action) / item(key, label, icon, action)</li>
 *   <li><b>快捷键</b>：accelerator(KeyCombination) 显示快捷键提示</li>
 *   <li><b>分隔线</b>：divider() 插入分组分隔线</li>
 *   <li><b>禁用</b>：disabled(true) 禁用单项</li>
 *   <li><b>回调</b>：onSelect(key) / onSelectItem(MenuItem)</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#CONTEXT_MENU} 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 绑定到表格行右键
 * ContextMenuAnt.create()
 *     .item("view", "查看详情", () -> showDetail(rowData))
 *     .item("edit", "编辑", editIcon, () -> editRow(rowData))
 *     .divider()
 *     .item("copy", "复制", () -> copyRow(rowData))
 *     .item("paste", "粘贴", () -> pasteRow(rowData))
 *     .divider()
 *     .item("delete", "删除", deleteIcon, () -> deleteRow(rowData))
 *     .disabled(true)
 *     .target(tableRow)
 *     .build();
 *
 * // 带快捷键提示
 * ContextMenuAnt.create()
 *     .item("save", "保存", () -> save())
 *     .accelerator(KeyCombination.keyCombination("Ctrl+S"))
 *     .item("undo", "撤销", () -> undo())
 *     .accelerator(KeyCombination.keyCombination("Ctrl+Z"))
 *     .target(canvas)
 *     .build();
 * }</pre>
 *
 * @see DropdownAnt 左键点击下拉菜单
 */
public class ContextMenuAnt {

    public static class MenuItem {
        private final String key;
        private final String label;
        private final Node icon;
        private boolean disabled;
        private final boolean divider;
        private KeyCombination accelerator;

        public MenuItem(String key, String label) {
            this(key, label, null, false, false);
        }

        public MenuItem(String key, String label, Node icon) {
            this(key, label, icon, false, false);
        }

        public MenuItem(String key, String label, boolean disabled) {
            this(key, label, null, disabled, false);
        }

        private MenuItem(String key, String label, Node icon, boolean disabled, boolean divider) {
            this.key = key;
            this.label = label;
            this.icon = icon;
            this.disabled = disabled;
            this.divider = divider;
        }

        public static MenuItem divider() {
            return new MenuItem("", "", null, false, true);
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Node getIcon() { return icon; }
        public boolean isDisabled() { return disabled; }
        public boolean isDivider() { return divider; }
        public KeyCombination getAccelerator() { return accelerator; }
        public void setAccelerator(KeyCombination accelerator) { this.accelerator = accelerator; }
        public void setDisabled(boolean disabled) { this.disabled = disabled; }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node target;
        private List<MenuItem> items = new ArrayList<>();
        private Consumer<String> onSelect = null;
        private Consumer<MenuItem> onSelectItem = null;
        private MenuItem currentItem = null;

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder item(String key, String label, Runnable action) {
            MenuItem item = new MenuItem(key, label);
            this.items.add(item);
            this.currentItem = item;
            return this;
        }

        public Builder item(String key, String label, Node icon, Runnable action) {
            MenuItem item = new MenuItem(key, label, icon);
            this.items.add(item);
            this.currentItem = item;
            return this;
        }

        public Builder item(String key, String label, boolean disabled) {
            MenuItem item = new MenuItem(key, label, disabled);
            this.items.add(item);
            this.currentItem = item;
            return this;
        }

        public Builder divider() {
            this.items.add(MenuItem.divider());
            return this;
        }

        public Builder disabled(boolean disabled) {
            // BUG #131 修复：之前是死代码（函数体只有注释），现在真正写入当前 item。
            if (currentItem != null) {
                currentItem.setDisabled(disabled);
            }
            return this;
        }

        public Builder accelerator(KeyCombination combination) {
            if (currentItem != null) {
                currentItem.setAccelerator(combination);
            }
            return this;
        }

        public Builder accelerator(String shortcut) {
            if (currentItem != null) {
                currentItem.setAccelerator(KeyCombination.keyCombination(shortcut));
            }
            return this;
        }

        public Builder onSelect(Consumer<String> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder onSelectItem(Consumer<MenuItem> onSelectItem) {
            this.onSelectItem = onSelectItem;
            return this;
        }

        public ContextMenuResult build() {
            return new ContextMenuResult(this);
        }
    }

    public static class ContextMenuResult {
        private final Builder config;
        private final Popup popup;
        private final VBox menuBox;

        ContextMenuResult(Builder config) {
            this.config = config;
            this.popup = new Popup();
            this.menuBox = createMenu();
            setupTrigger();
        }

        private VBox createMenu() {
            VBox box = new VBox(0);
            box.getStyleClass().add(JfxStyles.CONTEXT_MENU);

            for (MenuItem item : config.items) {
                if (item.isDivider()) {
                    Region div = new Region();
                    div.getStyleClass().add(JfxStyles.CONTEXT_MENU_DIVIDER);
                    box.getChildren().add(div);
                    continue;
                }

                HBox row = new HBox();
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.getStyleClass().add(JfxStyles.CONTEXT_MENU_ITEM);
                if (item.isDisabled()) {
                    row.getStyleClass().add(JfxStyles.CONTEXT_MENU_ITEM_DISABLED);
                }

                // Icon
                if (item.getIcon() != null) {
                    row.getChildren().add(item.getIcon());
                } else {
                    // Placeholder for alignment
                    Region placeholder = new Region();
                    placeholder.setPrefSize(16, 16);
                    row.getChildren().add(placeholder);
                }

                // Label
                Label label = new Label(item.getLabel());
                row.getChildren().add(label);

                // Accelerator
                if (item.getAccelerator() != null) {
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    row.getChildren().add(spacer);
                    Label accelLabel = new Label(item.getAccelerator().getDisplayText());
                    accelLabel.getStyleClass().add(JfxStyles.CONTEXT_MENU_ACCELERATOR);
                    row.getChildren().add(accelLabel);
                }

                // Action
                if (!item.isDisabled()) {
                    row.setOnMouseClicked(e -> {
                        popup.hide();
                        if (config.onSelect != null) {
                            config.onSelect.accept(item.getKey());
                        }
                        if (config.onSelectItem != null) {
                            config.onSelectItem.accept(item);
                        }
                    });
                }

                box.getChildren().add(row);
            }

            return box;
        }

        private void setupTrigger() {
            if (config.target == null) return;

            popup.setAutoHide(true);
            popup.getContent().add(menuBox);

            config.target.setOnContextMenuRequested(e -> {
                popup.show(config.target, e.getScreenX(), e.getScreenY());
                e.consume();
            });
        }

        public void show(double screenX, double screenY) {
            popup.show(config.target, screenX, screenY);
        }

        public void hide() {
            popup.hide();
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
