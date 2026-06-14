package org.openkawu.jfxium.layout;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 应用骨架组件。
 *
 * <h2>结构</h2>
 * <pre>
 * ┌─────────────────────────────┐
 * │  Header (top)               │
 * ├──────┬──────────────────────┤
 * │ Sider│  Content (center)    │
 * │      │                      │
 * ├──────┴──────────────────────┤
 * │  Footer (bottom)            │
 * └─────────────────────────────┘
 * </pre>
 *
 * <h2>M19.22 增强：Sider 折叠 + 响应式自适应</h2>
 * <ul>
 *   <li>{@link Builder#collapsible(boolean)}：启用 Sider 折叠能力（折叠时 sider 缩到 collapsedWidth）</li>
 *   <li>{@link Builder#collapsed(boolean)}：初始折叠状态</li>
 *   <li>{@link Builder#collapsedWidth(double)}：折叠后的宽度（默认 64px，对齐 admin 行业惯例）</li>
 *   <li>{@link Builder#breakpoint(GridAnt.Breakpoint)}：Scene 宽度小于此断点时自动折叠（lg/md 等）</li>
 *   <li>{@link Builder#onCollapseChange(Consumer)}：折叠状态变化回调</li>
 *   <li>{@link Builder#trigger(boolean)}：是否在 sider 底部显示折叠触发按钮（默认 true）</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>基础（不折叠）</h3>
 * <pre>{@code
 * BorderPane shell = AppShellAnt.create()
 *     .header(headerNode)
 *     .sider(menuNode, 240)
 *     .content(pageNode)
 *     .build();
 * }</pre>
 *
 * <h3>可折叠 + 响应式自适应</h3>
 * <pre>{@code
 * BorderPane shell = AppShellAnt.create()
 *     .header(headerNode)
 *     .sider(menuNode, 240)
 *     .collapsible(true)                          // 启用折叠
 *     .breakpoint(GridAnt.Breakpoint.LG)          // < 992px 自动折叠
 *     .collapsedWidth(64)                          // 折叠后 64px
 *     .onCollapseChange(c -> log("collapsed=" + c))
 *     .content(pageNode)
 *     .build();
 * }</pre>
 *
 * <h3>外部控制折叠状态（与自定义按钮联动）</h3>
 * <pre>{@code
 * AppShellAnt.Result result = AppShellAnt.create()
 *     .sider(menuNode, 240)
 *     .collapsible(true)
 *     .trigger(false)                             // 不显示内置触发器
 *     .content(pageNode)
 *     .buildResult();
 *
 * // 自定义触发：让 Header 上的汉堡按钮控制
 * myHamburgerBtn.setOnAction(e -> result.toggle());
 * BorderPane shell = result.getRoot();
 * }</pre>
 */
public class AppShellAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node header;
        private Node sider;
        private double siderWidth = 240;
        private double collapsedWidth = 64;
        private Node content;
        private Node footer;

        // M19.22 折叠相关
        private boolean collapsible = false;
        private boolean collapsed = false;
        private boolean trigger = true;
        private GridAnt.Breakpoint breakpoint;
        private Consumer<Boolean> onCollapseChange;

        // M19.35 各区域背景层级
        // 默认：sider=SUBTLE（灰底导航区），content=DEFAULT（白底内容区），
        // header/footer=null（透明，继承父容器），遵循 admin 行业惯例。
        // 显式设 null 或 Background.TRANSPARENT 可覆盖默认值。
        private Background headerBackground;
        private Background siderBackground = Background.SUBTLE;
        private Background contentBackground = Background.DEFAULT;
        private Background footerBackground;

        private Builder() {}

        public Builder header(Node header) { this.header = header; return this; }

        public Builder sider(Node sider) { return sider(sider, siderWidth); }

        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = width;
            return this;
        }

        public Builder content(Node content) { this.content = content; return this; }

        public Builder footer(Node footer) { this.footer = footer; return this; }

        /** 启用 Sider 可折叠（默认 false）。 */
        public Builder collapsible(boolean collapsible) {
            this.collapsible = collapsible;
            return this;
        }

        public Builder collapsible() { return collapsible(true); }

        /** 初始折叠状态（默认 false 展开）。 */
        public Builder collapsed(boolean collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        /** 折叠后 Sider 宽度（默认 64px）。 */
        public Builder collapsedWidth(double width) {
            this.collapsedWidth = width;
            return this;
        }

        /**
         * 响应式断点：Scene 宽度小于此断点的 minWidth 时自动折叠。
         * 例如 {@code breakpoint(LG)} 表示 &lt; 992px 自动折叠。
         * 不设则不启用响应式（仅手动 toggle）。
         */
        public Builder breakpoint(GridAnt.Breakpoint breakpoint) {
            this.breakpoint = breakpoint;
            return this;
        }

        /** 是否显示 sider 底部内置折叠触发按钮（默认 true）。 */
        public Builder trigger(boolean trigger) {
            this.trigger = trigger;
            return this;
        }

        // ============================================================
        // 各区域背景层级（M19.35）
        // ============================================================
        // 默认：sider=SUBTLE，content=DEFAULT，header/footer=null。
        // 可通过 .xxxBackground(null) 或 Background.TRANSPARENT 覆盖。

        /** Header 区域背景层级（M19.35）。默认 null（透明，继承父容器）。 */
        public Builder headerBackground(Background bg) {
            this.headerBackground = bg;
            return this;
        }

        /** Sider 区域背景层级（M19.35）。默认 {@code Background.SUBTLE}（admin 行业惯例：灰底导航区）。 */
        public Builder siderBackground(Background bg) {
            this.siderBackground = bg;
            return this;
        }

        /** Content 区域背景层级（M19.35）。默认 {@code Background.DEFAULT}（admin 行业惯例：白底内容区）。 */
        public Builder contentBackground(Background bg) {
            this.contentBackground = bg;
            return this;
        }

        /** Footer 区域背景层级（M19.35）。默认 null（透明，继承父容器）。 */
        public Builder footerBackground(Background bg) {
            this.footerBackground = bg;
            return this;
        }

        /** 折叠状态变化回调。 */
        public Builder onCollapseChange(Consumer<Boolean> handler) {
            this.onCollapseChange = handler;
            return this;
        }

        /**
         * 直接构建 BorderPane（向下兼容老 API）。
         * 想要外部控制折叠状态请用 {@link #buildResult()}。
         */
        public BorderPane build() {
            return buildResult().getRoot();
        }

        /**
         * 构建并返回 Result 对象，包含 root + 可编程控制折叠的句柄（M19.22）。
         */
        public Result buildResult() {
            BorderPane shell = new BorderPane();
            shell.getStyleClass().add(JfxStyles.APP_SHELL);

            if (header != null) {
                header.getStyleClass().add(JfxStyles.APP_SHELL_HEADER);
                applyBackgroundClass(header, headerBackground);
                shell.setTop(header);
            }

            if (footer != null) {
                footer.getStyleClass().add(JfxStyles.APP_SHELL_FOOTER);
                applyBackgroundClass(footer, footerBackground);
                shell.setBottom(footer);
            }

            // 折叠状态用 BooleanProperty 持有，方便外部监听 + Result 公开 toggle/setCollapsed
            BooleanProperty collapsedProp = new SimpleBooleanProperty(collapsed);

            HBox center = null;
            VBox siderWrapper = null;
            Region siderRegion = null;

            if (sider != null) {
                sider.getStyleClass().add(JfxStyles.APP_SHELL_SIDER);
                applyBackgroundClass(sider, siderBackground);

                if (collapsible) {
                    // 折叠模式：用 VBox wrapper 把 sider 内容 + trigger 按钮垂直排好
                    siderWrapper = new VBox();
                    siderWrapper.getStyleClass().add(JfxStyles.APP_SHELL_SIDER);
                    // 让 wrapper 也跟着 sider 用同一 background，避免 trigger 按钮区出现裸色
                    applyBackgroundClass(siderWrapper, siderBackground);
                    // 让 sider 内容占据剩余高度，trigger 钉在底部
                    VBox.setVgrow(sider, Priority.ALWAYS);
                    siderWrapper.getChildren().add(sider);

                    if (trigger) {
                        Button triggerBtn = createTriggerButton(collapsedProp);
                        siderWrapper.getChildren().add(triggerBtn);
                    }

                    siderRegion = siderWrapper;
                } else {
                    // 不可折叠：保持原有 sider 直接放进 center
                    siderRegion = sider instanceof Region r ? r : null;
                }

                // 设置 sider 宽度（按当前 collapsed 决定）
                applyWidth(siderRegion, collapsedProp.get() ? collapsedWidth : siderWidth);

                // 监听 collapsed 变化：动态改宽度 + 切 styleClass + 触发回调
                final Region finalSiderRegion = siderRegion;
                final Node finalSider = sider;
                collapsedProp.addListener((obs, oldVal, newVal) -> {
                    applyWidth(finalSiderRegion, newVal ? collapsedWidth : siderWidth);
                    // 在 sider 节点和 wrapper 上挂 collapsed styleClass，方便 LESS 切换内部细节
                    toggleStyleClass(finalSider, JfxStyles.APP_SHELL_SIDER + "-collapsed", newVal);
                    if (finalSiderRegion != null && finalSiderRegion != finalSider) {
                        toggleStyleClass(finalSiderRegion,
                                JfxStyles.APP_SHELL_SIDER + "-collapsed", newVal);
                    }
                    if (onCollapseChange != null) {
                        onCollapseChange.accept(newVal);
                    }
                });
                // 初始 collapsed 也要挂上 styleClass
                if (collapsedProp.get()) {
                    toggleStyleClass(sider, JfxStyles.APP_SHELL_SIDER + "-collapsed", true);
                    if (siderRegion != null && siderRegion != sider) {
                        toggleStyleClass(siderRegion, JfxStyles.APP_SHELL_SIDER + "-collapsed", true);
                    }
                }

                center = new HBox();
                center.getChildren().add(siderRegion != null ? siderRegion : sider);
                if (content != null) {
                    content.getStyleClass().add(JfxStyles.APP_SHELL_CONTENT);
                    applyBackgroundClass(content, contentBackground);
                    center.getChildren().add(content);
                    HBox.setHgrow(content, Priority.ALWAYS);
                }
                shell.setCenter(center);
            } else if (content != null) {
                content.getStyleClass().add(JfxStyles.APP_SHELL_CONTENT);
                applyBackgroundClass(content, contentBackground);
                shell.setCenter(content);
            }

            // 响应式断点：Scene 宽度小于阈值自动折叠
            if (collapsible && breakpoint != null && breakpoint != GridAnt.Breakpoint.XS) {
                attachBreakpointListener(shell, collapsedProp);
            }

            applyStyles(shell);
            return new Result(shell, collapsedProp);
        }

        /** 内置触发按钮：折叠时显示 ›, 展开时显示 ‹。 */
        private Button createTriggerButton(BooleanProperty collapsedProp) {
            Button btn = new Button(collapsedProp.get() ? "›" : "‹");
            btn.getStyleClass().addAll(JfxStyles.APP_SHELL_SIDER_TRIGGER, JfxStyles.BUTTON_BASE);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> collapsedProp.set(!collapsedProp.get()));
            // 同步按钮文字与折叠状态
            collapsedProp.addListener((obs, ov, nv) -> btn.setText(nv ? "›" : "‹"));
            return btn;
        }

        /** 监听 Scene 宽度，跨断点时自动 toggle 折叠状态。 */
        private void attachBreakpointListener(BorderPane shell, BooleanProperty collapsedProp) {
            final double threshold = breakpoint.getMinWidth();
            ChangeListener<Number> widthListener = (obs, oldVal, newVal) -> {
                if (newVal == null) return;
                boolean shouldCollapse = newVal.doubleValue() < threshold;
                if (shouldCollapse != collapsedProp.get()) {
                    collapsedProp.set(shouldCollapse);
                }
            };

            shell.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (oldScene != null) oldScene.widthProperty().removeListener(widthListener);
                if (newScene != null) {
                    newScene.widthProperty().addListener(widthListener);
                    // 入场景时立即按当前宽度判断
                    boolean shouldCollapse = newScene.getWidth() < threshold;
                    if (shouldCollapse != collapsedProp.get()) {
                        collapsedProp.set(shouldCollapse);
                    }
                }
            });
        }

        /** 同步设置 Region 三档宽度，保证 sider 不被 HBox 拉伸。 */
        private static void applyWidth(Region region, double width) {
            if (region == null) return;
            region.setMinWidth(width);
            region.setPrefWidth(width);
            region.setMaxWidth(width);
        }

        private static void toggleStyleClass(Node node, String cls, boolean on) {
            if (node == null) return;
            if (on) {
                if (!node.getStyleClass().contains(cls)) node.getStyleClass().add(cls);
            } else {
                node.getStyleClass().remove(cls);
            }
        }

        /**
         * 把 Background 枚举对应的 styleClass 挂到 node 上。
         * bg == null 时 no-op（保持原色，向后兼容）。
         */
        private static void applyBackgroundClass(Node node, Background bg) {
            if (node == null || bg == null) return;
            if (!node.getStyleClass().contains(bg.styleClass())) {
                node.getStyleClass().add(bg.styleClass());
            }
        }
    }

    /**
     * AppShell 构建结果（M19.22）。
     * <p>包含 root BorderPane + 可编程控制折叠状态的 API。</p>
     * <p>如果不需要外部控制，直接用 {@link Builder#build()} 拿 BorderPane 即可。</p>
     */
    public static class Result {
        private final BorderPane root;
        private final BooleanProperty collapsed;

        Result(BorderPane root, BooleanProperty collapsed) {
            this.root = root;
            this.collapsed = collapsed;
        }

        public BorderPane getRoot() { return root; }

        /** 当前是否折叠。 */
        public boolean isCollapsed() { return collapsed.get(); }

        /** 切换折叠状态。 */
        public void toggle() { collapsed.set(!collapsed.get()); }

        /** 显式设置折叠状态。 */
        public void setCollapsed(boolean value) { collapsed.set(value); }

        /** 折叠状态 property（可用于绑定 / 监听）。 */
        public BooleanProperty collapsedProperty() { return collapsed; }
    }
}
