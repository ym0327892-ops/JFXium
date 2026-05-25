package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * JFXium 页面布局组件 - 对标 Ant Design Layout
 *
 * <h2>设计说明</h2>
 * 本组件与 {@link AppShellAnt} 在功能上高度重叠（均为 Header + Sider + Content + Footer 经典布局）。
 * 为了避免维护两套几乎相同的实现，{@code LayoutAnt} 已重构为 {@link AppShellAnt} 的轻量委托：
 *
 * <ul>
 *   <li>API 保持向下兼容（{@code create()/header/sider/content/footer/build()}）</li>
 *   <li>底层 styleClass、布局逻辑、扩展钩子全部复用 {@link AppShellAnt}</li>
 *   <li>新代码请直接使用 {@link AppShellAnt}，提供更丰富的 {@code style/styleClass} 钩子</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 基础布局：Header + Content + Footer
 * BorderPane layout = LayoutAnt.create()
 *     .header(new Label("顶部导航"))
 *     .content(new Label("主要内容"))
 *     .footer(new Label("底部版权"))
 *     .build();
 *
 * // 带侧边栏的布局（默认宽度 200）
 * BorderPane layout = LayoutAnt.create()
 *     .sider(new Label("侧边菜单"), 200)
 *     .header(new Label("顶部"))
 *     .content(new Label("内容"))
 *     .build();
 * }</pre>
 *
 * @see AppShellAnt 推荐使用，提供更完整的 API
 */
public class LayoutAnt {

    /** LayoutAnt 默认侧边栏宽度，保留历史值 200，与 AppShellAnt 默认 240 不同。*/
    private static final double DEFAULT_SIDER_WIDTH = 200;

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        // 注意：不直接持有 AppShellAnt.Builder，而是把参数累积到本地，
        // 在 build() 时一次性传给 AppShellAnt。这样可以保留本组件特有的默认值（如 siderWidth=200）。
        private Node header;
        private Node sider;
        private double siderWidth = DEFAULT_SIDER_WIDTH;
        private Node content;
        private Node footer;

        private Builder() {}

        /** 设置顶部区域。*/
        public Builder header(Node header) {
            this.header = header;
            return this;
        }

        /**
         * 设置侧边栏（保留显式宽度参数版本）。
         * @param sider 侧边栏内容
         * @param width 侧边栏宽度
         */
        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = width;
            return this;
        }

        /** 设置侧边栏，使用默认宽度 {@value #DEFAULT_SIDER_WIDTH}。*/
        public Builder sider(Node sider) {
            return sider(sider, DEFAULT_SIDER_WIDTH);
        }

        /** 设置内容区域。*/
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /** 设置底部区域。*/
        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        public BorderPane build() {
            // 委托给 AppShellAnt：完整复用其 styleClass 体系、扩展钩子和布局逻辑，
            // 避免出现两套独立但几乎相同的实现导致的维护负担和行为漂移。
            AppShellAnt.Builder shell = AppShellAnt.create()
                    .header(header)
                    .content(content)
                    .footer(footer);
            if (sider != null) {
                shell.sider(sider, siderWidth);
            }
            return shell.build();
        }
    }
}
