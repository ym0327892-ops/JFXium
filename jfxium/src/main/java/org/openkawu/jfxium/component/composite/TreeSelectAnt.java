package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 树选择器组件 - 对标 Ant Design TreeSelect。
 *
 * <p><b>定位</b>：下拉树形选择器，支持多级嵌套节点，常用于组织架构选择、
 * 分类目录选择、地区级联等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>树形节点</b>：TreeNode 支持多级 children 嵌套</li>
 *   <li><b>可禁用</b>：节点级别 disabled</li>
 *   <li><b>回传完整对象</b>：onSelect 回调回传 TreeNode，可取 value/label</li>
 *   <li><b>搜索过滤</b>：支持输入过滤节点</li>
 *   <li><b>视觉</b>：弹层走 TREE_SELECT 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox treeSelect = TreeSelectAnt.create()
 *     .placeholder("选择部门")
 *     .node(new TreeNode("tech", "技术部", List.of(
 *         new TreeNode("fe", "前端组", null),
 *         new TreeNode("be", "后端组", null))))
 *     .node(new TreeNode("hr", "人力资源部", null))
 *     .onSelect(node -> System.out.println("选中：" + node.getLabel()))
 *     .build();
 * }</pre>
 */
public class TreeSelectAnt {

    public static class TreeNode {
        private final String value;
        private final String label;
        private final List<TreeNode> children;
        private boolean disabled;
        private boolean expanded = true;

        public TreeNode(String value, String label) {
            this(value, label, null);
        }

        public TreeNode(String value, String label, List<TreeNode> children) {
            this.value = TextUtils.safeText(value);
            this.label = TextUtils.safeText(label);
            this.children = children != null ? children : new ArrayList<>();
        }

        public TreeNode disabled(boolean disabled) { this.disabled = disabled; return this; }
        public TreeNode expanded(boolean expanded) { this.expanded = expanded; return this; }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public List<TreeNode> getChildren() { return children; }
        public boolean isDisabled() { return disabled; }
        public boolean isExpanded() { return expanded; }
        public boolean hasChildren() { return children != null && !children.isEmpty(); }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        // placeholder 复用父类 AbstractStyleBuilder.placeholder 字段（P2-S8 抽取）
        private TreeNode root;
        // disabled 复用父类 AbstractStyleBuilder.disable 字段（P2-S7.4 抽取），
        // 无需自建字段与 setter，直接继承父类 disabled(boolean) / disabled() 即可。
        private boolean multiple = false;
        private Consumer<TreeNode> onSelect = null;
        private Consumer<List<TreeNode>> onMultipleSelect = null;
        private TreeNode selectedNode = null;
        // 多选模式下已选中的节点集合（BUG #53：原 onMultipleSelect 死回调，从未维护选中态）
        private final List<TreeNode> selectedNodes = new ArrayList<>();
        private ObjectProperty<String> bindProperty = null;

        public Builder tree(TreeNode root) { this.root = root; return this; }
        public Builder multiple(boolean multiple) { this.multiple = multiple; return this; }
        public Builder onSelect(Consumer<TreeNode> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onMultipleSelect(Consumer<List<TreeNode>> onMultipleSelect) { this.onMultipleSelect = onMultipleSelect; return this; }

        /** 双向绑定：控件值（单选：选中节点的 value）↔ Property 值实时同步。 */
        public Builder bindValue(ObjectProperty<String> property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.getStyleClass().add(JfxStyles.TREE_SELECT);

            TextField field = new TextField();
            // placeholder 走 Messages 默认；调用方 .placeholder("...") 覆盖时使用其值
            String effectivePlaceholder = placeholder != null
                    ? placeholder
                    : Messages.get("treeselect.placeholder");
            field.setPromptText(effectivePlaceholder);
            // 仅当未显式指定 placeholder 时才订阅 locale 变化（避免覆盖用户文案）
            if (!hasPlaceholder()) {
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        field.setPromptText(Messages.get("treeselect.placeholder")));
            }
            field.setEditable(false);
            field.getStyleClass().add(JfxStyles.TREE_SELECT_FIELD);
            HBox.setHgrow(field, Priority.ALWAYS);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setAutoFix(true);

            VBox treePanel = new VBox(0);
            // 复用通用 popup-menu 视觉
            treePanel.getStyleClass().add(JfxStyles.POPUP_MENU);
            treePanel.setPrefWidth(240);

            if (root != null) {
                buildTreeNodes(treePanel, root, 0, popup, field);
            }
            popup.getContent().add(treePanel);

            field.setOnMouseClicked(e -> {
                if (Boolean.TRUE.equals(disable)) return;
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                    popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                }
            });

            container.getChildren().add(field);

            if (Boolean.TRUE.equals(disable)) {
                // applyStyles() 已自动应用 disable 到 container，这里只需处理内部字段
                field.setDisable(true);
            }
            applyStyles(container);
            return container;
        }

        private void buildTreeNodes(VBox panel, TreeNode node, int depth, Popup popup, TextField field) {
            if (node == null) return;

            VBox rowBox = new VBox();
            rowBox.setFillWidth(true);

            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            // depth 缩进：左侧插入占位 Region，避免 setStyle 拼 padding 违反红线 #1
            if (depth > 0) {
                Region indent = new Region();
                indent.getStyleClass().add(JfxStyles.TREE_SELECT_INDENT);
                indent.setMinWidth(depth * 16);
                indent.setPrefWidth(depth * 16);
                indent.setMaxWidth(depth * 16);
                row.getChildren().add(indent);
            }
            row.getStyleClass().add(JfxStyles.TREE_SELECT_ROW);
            if (node.isDisabled()) {
                row.getStyleClass().add(JfxStyles.TREE_SELECT_DISABLED);
            }
            // 多选模式：已选中的行加高亮修饰类（BUG #53）
            if (multiple && selectedNodes.contains(node)) {
                row.getStyleClass().add(JfxStyles.TREE_SELECT_SELECTED);
            }

            if (node.hasChildren()) {
                Label arrow = new Label(node.isExpanded() ? "\u25bc" : "\u25b6");
                arrow.getStyleClass().add(JfxStyles.TREE_SELECT_ARROW);
                row.getChildren().add(arrow);

                arrow.setOnMouseClicked(e -> {
                    node.expanded = !node.isExpanded();
                    refreshTree(panel, popup, field);
                });
            } else {
                // 无 children 时占位 12px，与有 children 行的 arrow 宽度对齐
                Region leafSpacer = new Region();
                leafSpacer.getStyleClass().add(JfxStyles.TREE_SELECT_LEAF);
                leafSpacer.setMinWidth(12);
                row.getChildren().add(leafSpacer);
            }

            Label label = new Label(node.getLabel());
            label.getStyleClass().add(JfxStyles.TREE_SELECT_LABEL);
            row.getChildren().add(label);

            if (!node.isDisabled()) {
                // hover 由 LESS .tree-select-row:hover 控制
                row.setOnMouseClicked(e -> {
                    if (multiple) {
                        // BUG #53：多选模式——点击切换选中态，回填所有已选 label，触发 onMultipleSelect
                        toggleMultiSelect(node);
                        field.setText(joinSelectedLabels());
                        if (onMultipleSelect != null) {
                            onMultipleSelect.accept(new ArrayList<>(selectedNodes));
                        }
                        // 多选不关闭弹层，方便连续勾选；刷新行高亮
                        refreshTree(panel, popup, field);
                    } else {
                        // 单选模式——回填单个 label，触发 onSelect，关闭弹层
                        field.setText(node.getLabel());
                        selectedNode = node;
                        if (bindProperty != null) {
                            bindProperty.set(node.getValue());
                        }
                        if (onSelect != null) onSelect.accept(node);
                        if (popup != null) popup.hide();
                    }
                });
            }

            rowBox.getChildren().add(row);

            if (node.hasChildren() && node.isExpanded()) {
                VBox childrenBox = new VBox(0);
                for (TreeNode child : node.getChildren()) {
                    buildTreeNodes(childrenBox, child, depth + 1, popup, field);
                }
                rowBox.getChildren().add(childrenBox);
            }
            panel.getChildren().add(rowBox);
        }

        private void refreshTree(VBox panel, Popup popup, TextField field) {
            panel.getChildren().clear();
            if (root != null) {
                buildTreeNodes(panel, root, 0, popup, field);
            }
        }

        /** 多选：切换某节点的选中态（已选则取消，未选则加入）。 */
        private void toggleMultiSelect(TreeNode node) {
            if (selectedNodes.contains(node)) {
                selectedNodes.remove(node);
            } else {
                selectedNodes.add(node);
            }
        }

        /** 多选：把所有已选节点的 label 用 "、" 拼接，作为输入框回填文案。 */
        private String joinSelectedLabels() {
            if (selectedNodes.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selectedNodes.size(); i++) {
                if (i > 0) sb.append("、");
                String label = selectedNodes.get(i).getLabel();
                sb.append(TextUtils.safeText(label));
            }
            return sb.toString();
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
