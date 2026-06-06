package org.openkawu.jfxium.component.composite;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 卡片组件 - 对标 Ant Design Card（组合式，Builder 模式，参考 AtlantaFX）。
 *
 * <p><b>定位</b>：内容承载容器，用于组织展示相关内容，
 * 支持标题、内容、封面、操作按钮、标签页等丰富功能。</p>
 *
 * <h2>功能特性（M10 增强）</h2>
 * <ul>
 *   <li><b>标题 + 额外操作</b>：title + extra 头部布局</li>
 *   <li><b>封面</b>：cover 图片展示</li>
 *   <li><b>底部操作区</b>：actions 按钮组</li>
 *   <li><b>标签页</b>：tabList 内置标签切换</li>
 *   <li><b>边框 / 悬浮</b>：bordered + hoverable</li>
 *   <li><b>尺寸</b>：MEDIUM / SMALL</li>
 *   <li><b>加载状态</b>：loading 骨架屏</li>
 *   <li><b>内嵌卡片</b>：type=INNER 用于卡片嵌套</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>列表卡片（用户卡片、商品卡片）</li>
 *   <li>详情卡片（基本信息 + 操作按钮）</li>
 *   <li>统计卡片（Dashboard 概览）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox card = CardAnt.create()
 *     .title("用户信息")
 *     .extra(ButtonAnt.create("编辑").type(ButtonAnt.Type.TEXT).build())
 *     .content(userDetailPanel)
 *     .bordered(true)
 *     .hoverable(true)
 *     .build();
 * }</pre>
 */
public class CardAnt {

    public enum Shadow {
        NONE,
        SMALL,
        MEDIUM,
        LARGE
    }

    public enum Size {
        MEDIUM,
        SMALL
    }

    public enum Type {
        DEFAULT,
        INNER
    }

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

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node extra;
        private Node content;
        private boolean bordered = false;
        private Shadow shadow = Shadow.NONE;
        private boolean hoverable = false;
        
        // M10 新增功能
        private boolean loading = false;
        private String coverImagePath;
        private Node coverNode;
        private List<Node> actions = new ArrayList<>();
        private List<TabItem> tabList = new ArrayList<>();
        private String activeTabKey;
        private String defaultActiveTabKey;
        private EventHandler<ActionEvent> onTabChange;
        private Node tabBarExtraContent;
        private Size size = Size.MEDIUM;
        private Type type = Type.DEFAULT;

        private Builder() {}

        // ========== 原有 API ==========
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder shadow(Shadow shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder hoverable(boolean hoverable) {
            this.hoverable = hoverable;
            return this;
        }

        // ========== M10 新增 API ==========

        /**
         * 设置加载状态（显示骨架屏）
         */
        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        /**
         * 设置封面图片路径
         */
        public Builder cover(String imagePath) {
            this.coverImagePath = imagePath;
            return this;
        }

        /**
         * 设置封面节点（自定义封面内容）
         */
        public Builder cover(Node coverNode) {
            this.coverNode = coverNode;
            return this;
        }

        /**
         * 添加底部操作按钮
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
         * 添加标签页
         */
        public Builder tab(String key, String label, Node content) {
            this.tabList.add(new TabItem(key, label, content));
            return this;
        }

        /**
         * 设置当前激活的标签页 key
         */
        public Builder activeTabKey(String key) {
            this.activeTabKey = key;
            return this;
        }

        /**
         * 设置默认激活的标签页 key
         */
        public Builder defaultActiveTabKey(String key) {
            this.defaultActiveTabKey = key;
            return this;
        }

        /**
         * 设置标签页切换回调
         */
        public Builder onTabChange(EventHandler<ActionEvent> handler) {
            this.onTabChange = handler;
            return this;
        }

        /**
         * 设置标签页额外内容
         */
        public Builder tabBarExtraContent(Node content) {
            this.tabBarExtraContent = content;
            return this;
        }

        /**
         * 设置卡片尺寸
         */
        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        /**
         * 设置卡片类型（内嵌卡片）
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public VBox build() {
            VBox card = new VBox();
            card.setSpacing(0);  // 各部分自己控制间距

            // ========== 基础样式类 ==========
            card.getStyleClass().add(JfxStyles.CARD);
            
            if (size == Size.SMALL) {
                card.getStyleClass().add(JfxStyles.CARD_SMALL);
            }
            
            if (type == Type.INNER) {
                card.getStyleClass().add(JfxStyles.CARD_INNER);
            }
            
            if (bordered) {
                card.getStyleClass().add(JfxStyles.CARD_BORDERED);
            }
            
            if (hoverable) {
                card.getStyleClass().add(JfxStyles.CARD_HOVERABLE);
            }
            
            switch (shadow) {
                case SMALL -> card.getStyleClass().add(JfxStyles.CARD_SHADOW_SM);
                case MEDIUM -> card.getStyleClass().add(JfxStyles.CARD_SHADOW_MD);
                case LARGE -> card.getStyleClass().add(JfxStyles.CARD_SHADOW_LG);
                case NONE -> {}  // 无阴影
            }

            // ========== Loading 状态：只显示骨架屏 ==========
            if (loading) {
                card.getChildren().add(buildLoadingSkeleton());
                applyStyles(card);
                return card;
            }

            // ========== 1. Cover（封面，在 header 之前）==========
            if (coverImagePath != null || coverNode != null) {
                Node cover = buildCover();
                if (cover != null) {
                    card.getChildren().add(cover);
                }
            }

            // ========== 2. Header（可选）==========
            if (shouldShowHeader()) {
                Node header = buildHeader();
                card.getChildren().add(header);
            }

            // ========== 3. Body（必选，但可以无内容）==========
            Node body = buildBody();
            card.getChildren().add(body);

            // ========== 4. Footer（可选）==========
            if (!actions.isEmpty()) {
                Node footer = buildFooter();
                card.getChildren().add(footer);
            }

            // 用户 style/styleClass 在所有内置类之后应用，便于覆盖
            applyStyles(card);
            return card;
        }

        /**
         * 判断是否需要显示 Header
         */
        private boolean shouldShowHeader() {
            return !title.isEmpty() || extra != null || !tabList.isEmpty();
        }

        /**
         * 构建 Header（标题栏 + 标签页导航）
         */
        private Node buildHeader() {
            VBox header = new VBox(0);
            header.getStyleClass().add(JfxStyles.CARD_HEADER);

            // 第一行：Title + Extra
            if (!title.isEmpty() || extra != null) {
                // 用 BarAnt 二段式（左标题 + 右 extra）
                javafx.scene.control.Label titleLabel = null;
                if (!title.isEmpty()) {
                    titleLabel = new javafx.scene.control.Label(title);
                    titleLabel.getStyleClass().add(JfxStyles.CARD_TITLE);
                }
                HBox titleRow = BarAnt.create()
                        .left(titleLabel)
                        .right(extra)
                        .gap(8)
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

        /**
         * 构建 Body（内容区）
         */
        private Node buildBody() {
            // 如果有标签页，Body 是 StackPane（用于切换内容）
            if (!tabList.isEmpty()) {
                StackPane body = new StackPane();
                body.getStyleClass().add(JfxStyles.CARD_BODY);
                
                // 显示当前激活的 tab 内容
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
            VBox body = new VBox(12);
            body.getStyleClass().add(JfxStyles.CARD_BODY);
            
            if (content != null) {
                if (content.getStyleClass().isEmpty()) {
                    content.getStyleClass().add(JfxStyles.CARD_CONTENT);
                }
                body.getChildren().add(content);
            }
            
            return body;
        }

        /**
         * 构建 Footer（底部操作区）
         */
        private Node buildFooter() {
            HBox footer = new HBox(0);
            footer.getStyleClass().add(JfxStyles.CARD_ACTIONS);
            footer.setAlignment(Pos.CENTER);

            for (Node action : actions) {
                StackPane actionItem = new StackPane(action);
                actionItem.getStyleClass().add(JfxStyles.CARD_ACTION_ITEM);
                actionItem.setAlignment(Pos.CENTER);
                HBox.setHgrow(actionItem, javafx.scene.layout.Priority.ALWAYS);
                actionItem.setMaxWidth(Double.MAX_VALUE);
                
                footer.getChildren().add(actionItem);
            }

            return footer;
        }

        /**
         * 构建 Tab Bar（标签页导航栏）
         */
        private Node buildTabBar() {
            HBox tabBar = new HBox(0);
            tabBar.getStyleClass().add(JfxStyles.CARD_TAB_BAR);

            // 标签按钮列表
            HBox tabButtons = new HBox(0);
            tabButtons.getStyleClass().add(JfxStyles.CARD_TAB_LIST);

            // 确定当前激活的 key
            String currentKey = activeTabKey != null ? activeTabKey :
                    (defaultActiveTabKey != null ? defaultActiveTabKey :
                            (!tabList.isEmpty() ? tabList.get(0).getKey() : null));

            // 创建标签按钮 — 用 userData 存储 key，避免依赖 children 索引顺序
            for (TabItem tabItem : tabList) {
                Label tabButton = new Label(tabItem.getLabel());
                tabButton.getStyleClass().add(JfxStyles.CARD_TAB_ITEM);
                tabButton.setUserData(tabItem.getKey()); // ← 存储 key 用于后续查找

                if (tabItem.getKey().equals(currentKey)) {
                    tabButton.getStyleClass().add(JfxStyles.CARD_TAB_ITEM_ACTIVE);
                }

                // 点击事件：切换 tab
                tabButton.setOnMouseClicked(e -> handleTabChange(tabItem.getKey(), tabButtons));

                tabButtons.getChildren().add(tabButton);
            }

            tabBar.getChildren().add(tabButtons);

            // Tab bar extra content（右侧额外内容）
            if (tabBarExtraContent != null) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                spacer.setMaxWidth(Double.MAX_VALUE);
                
                tabBar.getChildren().addAll(spacer, tabBarExtraContent);
            }

            return tabBar;
        }

        /**
         * 处理标签页切换 — 通过 userData 匹配 key，不依赖 children 索引顺序。
         *
         * <p>这样即使 tabButtons 中插入了 spacer 等其他节点，也能正确找到对应按钮。</p>
         */
        private void handleTabChange(String newKey, HBox tabButtons) {
            // 清除所有按钮的激活状态
            tabButtons.getChildren().forEach(btn -> {
                btn.getStyleClass().remove(JfxStyles.CARD_TAB_ITEM_ACTIVE);
            });

            // 通过 userData 匹配 key 找到目标按钮并激活
            tabButtons.getChildren().stream()
                    .filter(btn -> newKey.equals(btn.getUserData()))
                    .findFirst()
                    .ifPresent(btn -> btn.getStyleClass().add(JfxStyles.CARD_TAB_ITEM_ACTIVE));

            // 触发回调（用户需要自己处理内容切换）
            if (onTabChange != null) {
                ActionEvent event = new ActionEvent(tabButtons, null);
                onTabChange.handle(event);
            }
        }

        /**
         * 构建加载骨架屏
         */
        private VBox buildLoadingSkeleton() {
            VBox skeleton = new VBox(12);
            skeleton.getStyleClass().add(JfxStyles.CARD_BODY);
            skeleton.setPadding(new Insets(24));

            // 标题骨架
            if (!title.isEmpty()) {
                skeleton.getChildren().add(
                    SkeletonAnt.create()
                        .width(150)
                        .height(20)
                        .build()
                );
            }

            // 内容骨架（3 行）
            skeleton.getChildren().add(SkeletonAnt.paragraph(3, 300, 14));

            return skeleton;
        }

        /**
         * 构建封面
         */
        private Node buildCover() {
            if (coverNode != null) {
                StackPane coverContainer = new StackPane(coverNode);
                coverContainer.getStyleClass().add(JfxStyles.CARD_COVER);
                return coverContainer;
            }

            if (coverImagePath != null) {
                try {
                    Image image = new Image(coverImagePath);
                    ImageView imageView = new ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(Double.MAX_VALUE);  // 自适应宽度
                    
                    StackPane coverContainer = new StackPane(imageView);
                    coverContainer.getStyleClass().add(JfxStyles.CARD_COVER);
                    return coverContainer;
                } catch (Exception e) {
                    System.err.println("Failed to load cover image: " + coverImagePath);
                    return null;
                }
            }

            return null;
        }
    }
}