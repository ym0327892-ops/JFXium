package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.CollapseAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 手风琴组件 - 对标 Ant Design Collapse accordion 模式。
 *
 * <p><b>定位</b>：CollapseAnt 的互斥折叠快捷入口。始终以 accordion 模式运行
 * （同时只展开一个面板），内部 100% 委托 {@link CollapseAnt}。</p>
 *
 * <p>如需多面板同时展开 / 禁用单面板 / 展开动画定制，请直接使用 {@link CollapseAnt}。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox accordion = AccordionAnt.create()
 *     .pane("面板一", new Label("内容一"))
 *     .pane("面板二", new Label("内容二"), true)        // 禁用该面板
 *     .activeKey("面板一")                              // 默认展开
 *     .build();
 * }</pre>
 *
 * <h2>与 CollapseAnt 的关系</h2>
 * <ul>
 *   <li>{@code AccordionAnt.create().pane("A", contentA).build()} ≈ {@code CollapseAnt.create().accordion(true).panel("a", "A", contentA).build()}</li>
 *   <li>AccordionAnt 是 CollapseAnt 的委托包装，输出完全一致（VBox + Timeline 动画）</li>
 * </ul>
 */
public class AccordionAnt {

    /** Pane 自动 key 前缀。 */
    private static final String KEY_PREFIX = "accordion-";

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private record PaneEntry(String key, String title, Node content, boolean disabled) {}
        private final List<PaneEntry> entries = new ArrayList<>();
        private final List<String> activeKeys = new ArrayList<>();
        private int keyCounter = 1;

        private Builder() {}

        /** 添加一个面板。 */
        public Builder pane(String title, Node content) {
            return pane(title, content, false);
        }

        /**
         * 添加一个面板，并指定是否禁用（禁用的面板点击无响应 + jfx-collapse-disabled 样式）。
         *
         * @param title    面板标题
         * @param content  面板内容（任意 Node）
         * @param disabled true 时禁用，false 时正常
         */
        public Builder pane(String title, Node content, boolean disabled) {
            entries.add(new PaneEntry(KEY_PREFIX + keyCounter++, title, content, disabled));
            return this;
        }

        /**
         * 默认展开指定 key 对应的面板（多个 key 仅取第一个生效，accordion 模式互斥）。
         * key 对应 pane 的添加顺序（内部生成 "accordion-1", "accordion-2", ...），
         * 或通过 {@link #activeKeys(List)} 传入自定义 key 映射。
         */
        public Builder activeKey(String key) {
            if (key != null && !key.isEmpty()) {
                this.activeKeys.add(key);
            }
            return this;
        }

        /** 默认展开多个 key（accordion 模式互斥，实际生效的仍是第一个）。 */
        public Builder activeKeys(List<String> keys) {
            if (keys != null) {
                this.activeKeys.addAll(keys);
            }
            return this;
        }

        /**
         * 内部委托 CollapseAnt accordion 模式构建。
         * 返回 {@link VBox}（与 CollapseAnt 一致），不再返回 JavaFX Accordion。
         */
        public VBox build() {
            CollapseAnt.Builder collapseBuilder = CollapseAnt.create().accordion(true);
            for (PaneEntry entry : entries) {
                collapseBuilder.panel(entry.key(), entry.title(), entry.content(), entry.disabled());
            }
            if (!activeKeys.isEmpty()) {
                collapseBuilder.activeKeys(activeKeys);
            }
            VBox collapse = collapseBuilder.build();
            applyStyles(collapse);
            return collapse;
        }
    }
}
