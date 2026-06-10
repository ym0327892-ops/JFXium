package org.openkawu.jfxium.component.overlay;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
 * JFXium 下拉菜单组件 - 对标 Ant Design Dropdown。
 *
 * <p><b>定位</b>：点击触发节点弹出菜单列表，支持图标、禁用、分隔线。
 * 与 SelectAnt（表单选择器）和 ContextMenuAnt（右键菜单）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>菜单项</b>：支持 key/label/icon/disabled 完整配置</li>
 *   <li><b>分隔线</b>：divider() 插入分组分隔线</li>
 *   <li><b>回调</b>：onSelect(key) + onSelectItem(MenuItem) 双回调（BUG #54）</li>
 *   <li><b>位置</b>：bottomLeft（默认）/ bottomRight / topLeft / topRight</li>
 *   <li><b>禁用</b>：disabled(true) 禁用整个下拉</li>
 *   <li><b>视觉</b>：走 JfxStyles.POPUP_MENU 系列，hover/disabled 由 LESS 伪类控制</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础用法：按钮触发下拉
 * ButtonAnt trigger = ButtonAnt.create("操作").build();
 * DropdownAnt.create()
 *     .trigger(trigger)
 *     .item("edit", "编辑")
 *     .item("copy", "复制")
 *     .divider()
 *     .item("delete", "删除", true)  // disabled
 *     .onSelect(key -> handleAction(key))
 *     .build();
 *
 * // 带图标 + onSelectItem 回调
 * DropdownAnt.create()
 *     .trigger(iconButton)
 *     .item("export", "导出", exportIcon)
 *     .item("print", "打印", printIcon)
 *     .onSelectItem(item -> System.out.println("选中：" + item.getLabel()))
 *     .build();
 * }</pre>
 *
 * @see PopconfirmAnt 气泡确认框（带确认/取消）
 */
public class DropdownAnt {

    public static class MenuItem {
        private final String key;
        private final String label;
        private final Node icon;
        private final boolean disabled;
        private final boolean divider;

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
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node trigger;
        private List<MenuItem> items = new ArrayList<>();
        private Consumer<String> onSelect = null;
        private Consumer<MenuItem> onSelectItem = null;
        private boolean disabled = false;
        private String placement = "bottomLeft";

        public Builder trigger(Node trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder item(String key, String label) {
            this.items.add(new MenuItem(key, label));
            return this;
        }

        public Builder item(String key, String label, Node icon) {
            this.items.add(new MenuItem(key, label, icon));
            return this;
        }

        public Builder item(String key, String label, boolean disabled) {
            this.items.add(new MenuItem(key, label, disabled));
            return this;
        }

        public Builder divider() {
            this.items.add(MenuItem.divider());
            return this;
        }

        public Builder items(List<MenuItem> items) {
            this.items = items;
            return this;
        }

        public Builder onSelect(Consumer<String> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        /**
         * 选中回调（完整菜单项）。
         *
         * <p>修复 BUG #54：原 {@link #onSelect(Consumer)} 只回传 item 的 key，
         * 调用方拿不到 label / icon，要显示「用户看得懂的文案」就得自己维护一份 key→label 映射。
         * 本回调直接回传整个 {@link MenuItem} 对象，可同时取 {@code getKey()} / {@code getLabel()}，
         * 与 TreeSelectAnt.onSelect(TreeNode) 的「回传完整对象」契约保持一致。</p>
         *
         * <p>两个回调可同时设置，点击时都会触发（onSelect 先、onSelectItem 后）。</p>
         */
        public Builder onSelectItem(Consumer<MenuItem> onSelectItem) {
            this.onSelectItem = onSelectItem;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder placement(String placement) {
            this.placement = placement;
            return this;
        }

        public DropdownResult build() {
            if (trigger == null) {
                throw new IllegalStateException("Trigger node is required");
            }
            return new DropdownResult(this);
        }
    }

    public static class DropdownResult {
        private final Builder config;
        private final Node trigger;
        private final Popup popup;
        private final VBox menu;

        DropdownResult(Builder config) {
            this.config = config;
            this.trigger = config.trigger;
            this.popup = new Popup();
            // Popup 有独立 Scene，不继承宿主节点的主题样式表。
            // 显示时把宿主 Scene 的 stylesheets 注入 Popup Scene，确保暗色等主题下文字/背景颜色正确。
            this.popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (isShowing && this.popup.getScene() != null && trigger.getScene() != null) {
                    this.popup.getScene().getStylesheets().setAll(trigger.getScene().getStylesheets());
                }
            });
            this.menu = createMenu();
            setupTrigger();
        }

        private VBox createMenu() {
            VBox menuBox = new VBox(0);
            // 走通用 popup-menu 样式（背景/边框/阴影/padding/min-width 全在 LESS）
            menuBox.getStyleClass().add(JfxStyles.POPUP_MENU);

            for (MenuItem item : config.items) {
                if (item.isDivider()) {
                    Region div = new Region();
                    div.getStyleClass().add(JfxStyles.POPUP_MENU_DIVIDER);
                    menuBox.getChildren().add(div);
                    continue;
                }

                HBox menuItem = new HBox(8);
                menuItem.setAlignment(Pos.CENTER_LEFT);
                menuItem.getStyleClass().add(JfxStyles.POPUP_MENU_ITEM);
                if (item.isDisabled()) {
                    menuItem.getStyleClass().add(JfxStyles.POPUP_MENU_ITEM_DISABLED);
                }

                if (item.getIcon() != null) {
                    menuItem.getChildren().add(item.getIcon());
                }
                Label label = new Label(item.getLabel());
                menuItem.getChildren().add(label);

                if (!item.isDisabled()) {
                    // 点击回调；hover 视觉由 LESS .jfx-popup-menu-item:hover 控制
                    menuItem.setOnMouseClicked(e -> {
                        popup.hide();
                        // 先回传 key（向下兼容），再回传完整 MenuItem（BUG #54：可取 label）
                        if (config.onSelect != null) {
                            config.onSelect.accept(item.getKey());
                        }
                        if (config.onSelectItem != null) {
                            config.onSelectItem.accept(item);
                        }
                    });
                }
                menuBox.getChildren().add(menuItem);
            }
            return menuBox;
        }

        private void setupTrigger() {
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(menu);
            trigger.setOnMouseClicked(e -> {
                if (config.disabled) return;
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    showAtTrigger();
                }
            });
        }

        private void showAtTrigger() {
            Bounds bounds = trigger.localToScreen(trigger.getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMaxY() + 4;
            if (config.placement.contains("Right")) {
                x = bounds.getMaxX() - 160;
            }
            if (config.placement.contains("top")) {
                y = bounds.getMinY() - menu.getHeight() - 4;
            }
            popup.show(trigger, x, y);
        }

        public Node getTrigger() { return trigger; }

        public void show() { showAtTrigger(); }

        public void hide() { popup.hide(); }
    }

    public static Builder create() {
        return new Builder();
    }
}
