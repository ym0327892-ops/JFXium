package org.openkawu.jfxium.component.composite;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import org.openkawu.jfxium.core.builder.Radius;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 分组框组件 —— 对标桌面端 GroupBox / TitledBorder。
 *
 * <p><b>定位</b>：带标题边框的内容容器，用于将逻辑相关的控件（输入框、复选框等）
 * 组织在同一视觉区域中。适用于表单分组、配置面板等桌面场景。</p>
 *
 * <p><b>与 Web Card 的区别</b>：桌面 GroupBox 强调「边框 + 标题」的分组语义，
 * 而非封面图/阴影/悬浮等 Web 卡片装饰。整体更紧凑、桌面原生感更强。</p>
 *
 * <h2>功能</h2>
 * <ul>
 *   <li><b>标题 + 额外操作</b>：title + extra 头部布局</li>
 *   <li><b>内容区</b>：content 承载分组控件</li>
 *   <li><b>底部操作区</b>：actions 按钮组</li>
 *   <li><b>标签页</b>：tabList 内置标签切换</li>
 *   <li><b>边框</b>：bordered 控制边框显隐（桌面 GroupBox 默认有边框）</li>
 *   <li><b>尺寸</b>：MEDIUM / SMALL</li>
 *   <li><b>内嵌模式</b>：type=INNER 用于分组框嵌套</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>表单分组（基本信息 / 联系方式各一个 GroupBox）</li>
 *   <li>配置面板（Modal 里分组设置项）</li>
 *   <li>Dashboard 统计卡片（配合 content 包裹 Statistic 组件）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox group = GroupBoxAnt.create()
 *     .title("基本信息")
 *     .content(userInfoPanel)
 *     .bordered(true)
 *     .build();
 * }</pre>
 */
public class GroupBoxAnt {

    /**
     * 标签页项
     */
    public static class TabItem {
        private final String key;
        private final String label;
        private final Node content;

        public TabItem(String key, String label, Node content) {
            this.key = key;
            this.label = label;
            this.content = content;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Node getContent() { return content; }
    }

    public enum Size {
        MEDIUM,
        SMALL
    }

    public enum Type {
        DEFAULT,
        INNER
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node extra;
        private Node content;
        private boolean bordered = false;
        private Size size = Size.MEDIUM;
        private Type type = Type.DEFAULT;
        private List<Node> actions = new ArrayList<>();
        private List<TabItem> tabList = new ArrayList<>();
        private String activeTabKey;
        private String defaultActiveTabKey;
        private EventHandler<ActionEvent> onTabChange;
        private Node tabBarExtraContent;
        private boolean headerBackground = true;
        private boolean headerBorder = true;
        private boolean hoverable = true;

        private Builder() {}

        /** 设置标题文本。 */
        public Builder title(String title) {
            this.title = title != null ? title : "";
            return this;
        }

        /** 设置标题右侧额外操作节点。 */
        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        /** 设置内容节点。 */
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /** 是否显示边框（默认 false，桌面 GroupBox 建议 true）。 */
        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        /** 设置卡片尺寸。 */
        public Builder size(Size size) {
            this.size = size != null ? size : Size.MEDIUM;
            return this;
        }

        /** 设置卡片类型（内嵌）。 */
        public Builder type(Type type) {
            this.type = type != null ? type : Type.DEFAULT;
            return this;
        }

        /**
         * 添加底部操作按钮。
         * 底部操作栏每个按钮均分宽度（居中排列），符合桌面 GroupBox 操作区惯例。
         */
        public Builder actions(Node... actions) {
            if (actions != null) {
                for (Node action : actions) {
                    this.actions.add(action);
                }
            }
            return this;
        }

        /**
         * 添加一个标签页。多次调用添加多个 tab，按添加顺序从左到右排列。
         *
         * @param key     标签页唯一标识
         * @param label   显示标题
         * @param content 该 tab 对应的内容节点
         */
        public Builder tab(String key, String label, Node content) {
            this.tabList.add(new TabItem(key, label, content));
            return this;
        }

        /**
         * 设置当前激活的标签页 key（受控模式）。
         * 设此值后由业务控制激活状态，需配合 {@link #onTabChange} 同步更新。
         */
        public Builder activeTabKey(String key) {
            this.activeTabKey = key;
            return this;
        }

        /**
         * 设置默认激活的标签页 key（非受控模式）。
         * 仅首次渲染生效；不设则默认激活第一个 tab。
         */
        public Builder defaultActiveTabKey(String key) {
            this.defaultActiveTabKey = key;
            return this;
        }

        /**
         * 设置标签页切换回调。
         * 触发时机：用户点击 tab 按钮后。
         * 受控模式下业务在此回调中更新 {@link #activeTabKey}。
         */
        public Builder onTabChange(EventHandler<ActionEvent> handler) {
            this.onTabChange = handler;
            return this;
        }

        /**
         * 设置标签栏右侧额外内容（如"更多"按钮）。
         */
        public Builder tabBarExtraContent(Node content) {
            this.tabBarExtraContent = content;
            return this;
        }
        
        /**
         * 设置 header 是否显示背景色（默认 true，底色 + 底部分割线）。
         * 传 false 可得到极简的纯文字标题。
         */
        public Builder headerBackground(boolean on) {
            this.headerBackground = on;
            return this;
        }
        
        /**
         * 设置 header 底部分割线是否显示（默认 true）。
         * 通常跟 {@link #headerBackground} 保持联动（headerBackground 关时也关）。
         */
        public Builder headerBorder(boolean on) {
            this.headerBorder = on;
            return this;
        }
        
        /**
         * 设置是否启用悬停效果（默认 true）。
         * 悬停时添加轻阴影（shadow-sm），无底色变化/缩放，保持桌面克制。
         */
        public Builder hoverable(boolean on) {
            this.hoverable = on;
            return this;
        }

        public VBox build() {
            VBox group = new VBox();
            group.setSpacing(0);

            // 保留原默认 SM 圆角行为（100% 等价原实现）
            if (this.radius == null) this.radius = Radius.SM;

            // ========== 基础样式类 ==========
            group.getStyleClass().add(JfxStyles.GROUP_BOX);

            if (size == Size.SMALL) {
                group.getStyleClass().add(JfxStyles.GROUP_BOX_SMALL);
            }

            if (type == Type.INNER) {
                group.getStyleClass().add(JfxStyles.GROUP_BOX_INNER);
            }

            if (bordered) {
                group.getStyleClass().add(JfxStyles.GROUP_BOX_BORDERED);
            }

            if (hoverable) {
                group.getStyleClass().add(JfxStyles.GROUP_BOX_HOVERABLE);
            }

            // ========== 1. Header（标题行 + 标签栏，可选）==========
            if (shouldShowHeader()) {
                Node header = buildHeader();
                group.getChildren().add(header);
            }

            // ========== 2. Body（内容区）==========
            Node body = buildBody();
            group.getChildren().add(body);

            // ========== 3. Footer（底部操作区，可选）==========
            if (!actions.isEmpty()) {
                Node footer = buildFooter();
                group.getChildren().add(footer);
            }

            applyStyles(group);
            return group;
        }

        private boolean shouldShowHeader() {
            return !title.isEmpty() || extra != null || !tabList.isEmpty();
        }

        private Node buildHeader() {
            VBox header = new VBox(0);
            header.getStyleClass().add(JfxStyles.GROUP_BOX_HEADER);
            if (headerBackground) {
                header.getStyleClass().add(JfxStyles.GROUP_BOX_HEADER_BG);
            }
            if (headerBorder) {
                header.getStyleClass().add(JfxStyles.GROUP_BOX_HEADER_BORDER);
            }

            // 第一行：Title + Extra
            if (!title.isEmpty() || extra != null) {
                HBox titleRow = BarAnt.create()
                        .left(!title.isEmpty() ? buildTitleLabel() : null)
                        .right(extra)
                        .gap(8)
                        .borderBottom(false)
                        .build();
                header.getChildren().add(titleRow);
            }

            // 第二行：Tab Bar（如果有标签页）
            if (!tabList.isEmpty()) {
                Node tabBar = buildTabBar();
                header.getChildren().add(tabBar);
            }

            return header;
        }

        private Label buildTitleLabel() {
            Label label = new Label(title);
            label.getStyleClass().add(JfxStyles.GROUP_BOX_TITLE);
            return label;
        }

        private Node buildBody() {
            // 如果有标签页，Body 是 StackPane（用于切换内容）
            if (!tabList.isEmpty()) {
                StackPane body = new StackPane();
                body.getStyleClass().add(JfxStyles.GROUP_BOX_BODY);

                String currentKey = activeTabKey != null ? activeTabKey :
                        (defaultActiveTabKey != null ? defaultActiveTabKey :
                                (!tabList.isEmpty() ? tabList.get(0).getKey() : null));

                for (TabItem tabItem : tabList) {
                    if (tabItem.getKey().equals(currentKey) && tabItem.getContent() != null) {
                        body.getChildren().add(tabItem.getContent());
                        break;
                    }
                }

                return body;
            }

            // 普通 Body（VBox）
            VBox body = new VBox();
            body.getStyleClass().add(JfxStyles.GROUP_BOX_BODY);

            if (content != null) {
                if (content.getStyleClass().isEmpty()) {
                    content.getStyleClass().add(JfxStyles.GROUP_BOX_CONTENT);
                }
                body.getChildren().add(content);
            }

            return body;
        }

        private Node buildFooter() {
            HBox footer = new HBox(0);
            footer.getStyleClass().add(JfxStyles.GROUP_BOX_ACTIONS);
            footer.setAlignment(Pos.CENTER);

            for (Node action : actions) {
                StackPane actionItem = new StackPane(action);
                actionItem.getStyleClass().add(JfxStyles.GROUP_BOX_ACTION_ITEM);
                actionItem.setAlignment(Pos.CENTER);
                HBox.setHgrow(actionItem, Priority.ALWAYS);
                actionItem.setMaxWidth(Double.MAX_VALUE);

                footer.getChildren().add(actionItem);
            }

            return footer;
        }

        private Node buildTabBar() {
            HBox tabBar = new HBox(0);
            tabBar.getStyleClass().add(JfxStyles.GROUP_BOX_TAB_BAR);

            HBox tabButtons = new HBox(0);
            tabButtons.getStyleClass().add(JfxStyles.GROUP_BOX_TAB_LIST);

            String currentKey = activeTabKey != null ? activeTabKey :
                    (defaultActiveTabKey != null ? defaultActiveTabKey :
                            (!tabList.isEmpty() ? tabList.get(0).getKey() : null));

            for (TabItem tabItem : tabList) {
                Label tabButton = new Label(tabItem.getLabel());
                tabButton.getStyleClass().add(JfxStyles.GROUP_BOX_TAB_ITEM);
                tabButton.setUserData(tabItem.getKey());

                if (tabItem.getKey().equals(currentKey)) {
                    tabButton.getStyleClass().add(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE);
                }

                tabButton.setOnMouseClicked(e -> handleTabChange(tabItem.getKey(), tabButtons));

                tabButtons.getChildren().add(tabButton);
            }

            tabBar.getChildren().add(tabButtons);

            if (tabBarExtraContent != null) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                spacer.setMaxWidth(Double.MAX_VALUE);
                tabBar.getChildren().addAll(spacer, tabBarExtraContent);
            }

            return tabBar;
        }

        private void handleTabChange(String newKey, HBox tabButtons) {
            tabButtons.getChildren().forEach(btn ->
                btn.getStyleClass().remove(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE));

            tabButtons.getChildren().stream()
                    .filter(btn -> newKey.equals(btn.getUserData()))
                    .findFirst()
                    .ifPresent(btn -> btn.getStyleClass().add(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE));

            if (onTabChange != null) {
                ActionEvent event = new ActionEvent(tabButtons, null);
                onTabChange.handle(event);
            }
        }
    }
}
