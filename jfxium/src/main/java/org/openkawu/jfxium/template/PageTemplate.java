package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PageTemplate - 通用展示页骨架（M19.33 引入）。
 *
 * <p><b>定位</b>：「大标题 + 描述 + 内容区」最简模板。覆盖 admin / showcase / 文档 / 设置等
 * <b>不需要 topbar/bottombar</b> 的简单展示场景。</p>
 *
 * <h2>跟其他 Template 的边界</h2>
 * <table border="1">
 *   <caption>JFXium 5 个 Template 适用场景对照</caption>
 *   <tr><th>模板</th><th>结构</th><th>典型场景</th></tr>
 *   <tr><td>{@link PageTemplate}</td><td>title + desc + body</td><td>展示页 / 文档 / 设置 / 任何简单内容页</td></tr>
 *   <tr><td>{@link CrudTemplate}</td><td>title + topbar + body + bottombar</td><td>列表 / 表单 / 仪表盘（带工具栏）</td></tr>
 *   <tr><td>{@link DashboardTemplate}</td><td>welcome + 统计卡矩阵 + 双栏底部</td><td>数据概览首页</td></tr>
 *   <tr><td>{@link WorkspaceTemplate}</td><td>brand + headerActions + sider + content + footer</td><td>工作台 / 后台壳 / 需要 header + sider 的生产系统</td></tr>
 *   <tr><td>{@link LoginTemplate}</td><td>banner + 表单</td><td>登录 / 注册</td></tr>
 * </table>
 *
 * <h2>整体结构</h2>
 * <pre>
 * ┌─────────────────────────────────────────┐
 * │ Title（大标题，28px / 700）               │
 * │ Description（描述文字，-color-fg-muted）   │
 * ├─────────────────────────────────────────┤
 * │                                         │
 * │ Body（任意 Node 或多个 Section 竖排）       │
 * │                                         │
 * └─────────────────────────────────────────┘
 * </pre>
 *
 * <h2>使用示例 - showcase 页</h2>
 * <pre>{@code
 * VBox page = PageTemplate.create()
 *     .title("Button 按钮")
 *     .description("可点击的交互元素，支持多种 type / size / shape")
 *     .section(buildBasicSection())
 *     .section(buildSizeSection())
 *     .section(buildShapeSection())
 *     .build();
 * }</pre>
 *
 * <h2>使用示例 - 设置页</h2>
 * <pre>{@code
 * VBox page = PageTemplate.create()
 *     .title("应用设置")
 *     .description("配置主题、语言、快捷键等偏好")
 *     .body(settingsForm)              // 单一 body 节点
 *     .build();
 * }</pre>
 *
 * <h2>使用示例 - 极简（仅标题）</h2>
 * <pre>{@code
 * VBox page = PageTemplate.create()
 *     .title("欢迎")
 *     .body(welcomeContent)
 *     .build();
 * // description 可选，未设置不渲染
 * }</pre>
 *
 * <h2>设计取舍</h2>
 * <ul>
 *   <li>底层 VBox（不是 BorderPane）—— 标题/描述/body 按顺序竖排，high 自适应</li>
 *   <li>所有视觉样式走 styleClass + LESS（{@link JfxStyles#PAGE_TEMPLATE_TITLE} / {@code _DESC}），
 *       <b>禁止 inline setStyle</b>——遵循项目 SKILL #1 强约束</li>
 *   <li>title 可选；description 可选；body 也可选——三者都不设就是个空 VBox</li>
 *   <li>{@link #section(Node)} 是 {@link #body(Node)} 的多节点版本，适合 showcase 多 Section 场景</li>
 *   <li>支持 {@code .padding(double)} 自定义内边距；默认 0（让父容器决定）</li>
 *   <li>支持 {@code .style(...) / .styleClass(...)} 用户自定义视觉钩子</li>
 * </ul>
 */
public class PageTemplate {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title;
        private String description;
        // body 单节点 vs sections 多节点：互斥但都可用，build 时合并处理
        private Node body;
        private final List<Node> sections = new ArrayList<>();

        // 装饰
        private double headerGap = 8;       // title ↔ description 间距
        private double sectionGap = 20;     // section/body 之间间距
        private double headerToBodyGap = 20;// header 整体 → body 之间间距
        private double padding = 0;         // 整体 padding（默认 0，让父容器决定）
        private Background background = Background.DEFAULT;  // M19.35 整体背景层级（默认白底）

        private Builder() {}

        // ============================================================
        // 内容
        // ============================================================

        /** 大标题（可选）。null 或不调用即不渲染。 */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /** 描述文字（可选）。null 或不调用即不渲染。 */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 主内容区（单一节点）。
         *
         * <p>跟 {@link #section(Node)} 的区别：body 是「整个内容区就一个节点」，
         * section 是「内容区由多个节点组成的列表」。两者都设时，body 在前、sections 在后。</p>
         */
        public Builder body(Node body) {
            this.body = body;
            return this;
        }

        /**
         * 添加一个内容 section（多个 section 按添加顺序竖排）。
         * 适合 showcase 页等「多区块独立展示」场景。
         */
        public Builder section(Node section) {
            if (section != null) sections.add(section);
            return this;
        }

        /** 批量添加 section（避免链式 .section().section().section() 噪音）。 */
        public Builder sections(Node... sectionNodes) {
            if (sectionNodes != null) {
                for (Node n : sectionNodes) {
                    if (n != null) sections.add(n);
                }
            }
            return this;
        }

        // ============================================================
        // 装饰
        // ============================================================

        /** title 与 description 之间的垂直间距（默认 8）。 */
        public Builder headerGap(double gap) {
            this.headerGap = TextUtils.safeNonNegative(gap, 0);
            return this;
        }

        /** body 内多个 section 之间的垂直间距（默认 20）。 */
        public Builder sectionGap(double gap) {
            this.sectionGap = TextUtils.safeNonNegative(gap, 0);
            return this;
        }

        /** header 与 body 之间的垂直间距（默认 20）。 */
        public Builder headerToBodyGap(double gap) {
            this.headerToBodyGap = TextUtils.safeNonNegative(gap, 0);
            return this;
        }

        /** 整体 padding（默认 0；父容器自带 padding 时不要重复设置）。 */
        public Builder padding(double padding) {
            this.padding = TextUtils.safeNonNegative(padding, 0);
            return this;
        }

        /**
         * 整体背景层级（M19.35）。
         *
         * <p>默认 {@code Background.DEFAULT}（白底），在 LAYOUT 灰底 Scene 上形成清晰的“页面卡”效果。
         * 常见用法：</p>
         * <ul>
         *   <li>不调用——使用默认白底（推荐）</li>
         *   <li>{@code Background.LAYOUT}——页面外层灰底</li>
         *   <li>{@code null} 或 {@code Background.TRANSPARENT}——透明，让父容器决定</li>
         * </ul>
         */
        public Builder background(Background background) {
            this.background = background;
            return this;
        }

        // ============================================================
        // 构建
        // ============================================================

        public VBox build() {
            VBox root = new VBox();
            root.getStyleClass().add(JfxStyles.PAGE_TEMPLATE);
            double resolvedPadding = TextUtils.safeNonNegative(padding, 0);
            if (resolvedPadding > 0) {
                root.setPadding(new Insets(resolvedPadding));
            }
            // M19.35 background：可选，挂上对应的 .jfx-bg-* styleClass
            if (background != null) {
                root.getStyleClass().add(background.styleClass());
            }

            // ========== Header（title + description）==========
            VBox header = buildHeader();
            if (header != null) {
                root.getChildren().add(header);
                VBox.setMargin(header, new Insets(0, 0, TextUtils.safeNonNegative(headerToBodyGap, 0), 0));
            }

            // ========== Body（单节点 + 多个 section 都支持）==========
            // 设计意图：body 在前 + sections 在后，让用户可以混用——
            //   .body(form).section(footerNotes)  也合法（虽然不常用）
            if (body != null) {
                Node b = body;
                if (!b.getStyleClass().contains(JfxStyles.PAGE_TEMPLATE_BODY)) {
                    b.getStyleClass().add(JfxStyles.PAGE_TEMPLATE_BODY);
                }
                root.getChildren().add(b);
            }

            for (int i = 0; i < sections.size(); i++) {
                Node section = sections.get(i);
                root.getChildren().add(section);
                // 除最后一个外，每个 section 后加间距（用 setMargin 而非 VBox.spacing，
                // 因为 spacing 会让 header→body 也变成 sectionGap，破坏 headerToBodyGap 语义）
                if (i < sections.size() - 1) {
                    VBox.setMargin(section, new Insets(0, 0, TextUtils.safeNonNegative(sectionGap, 0), 0));
                }
            }

            // 用户 style/styleClass 钩子：在内置 styleClass 之后应用，便于覆盖
            applyStyles(root);
            return root;
        }

        /**
         * 构建 header 区（title + description），返回 null 表示两者都未设置。
         * 设计意图：header 整体作为一个 VBox 单元，让外部 setMargin 控制 header 与 body 的距离。
         */
        private VBox buildHeader() {
            if ((title == null || title.isEmpty()) && (description == null || description.isEmpty())) {
                return null;
            }
            VBox header = new VBox(TextUtils.safeNonNegative(headerGap, 0));
            header.setAlignment(Pos.TOP_LEFT);
            header.getStyleClass().add(JfxStyles.PAGE_TEMPLATE_HEADER);

            if (title != null && !title.isEmpty()) {
                LabelAnt titleLabel = LabelAnt.create(title);
                titleLabel.getStyleClass().add(JfxStyles.PAGE_TEMPLATE_TITLE);
                header.getChildren().add(titleLabel);
            }
            if (description != null && !description.isEmpty()) {
                LabelAnt descLabel = LabelAnt.create(description);
                descLabel.getStyleClass().add(JfxStyles.PAGE_TEMPLATE_DESC);
                descLabel.setWrapText(true);
                header.getChildren().add(descLabel);
            }
            return header;
        }

        // safeSpacing 统一改用 TextUtils.safeNonNegative,见 P0-23。
    }
}
