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
 *     .pane("面板二", new Label("内容二"))
 *     .pane("面板三", new Label("内容三"))
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

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private record PaneEntry(String key, String title, Node content) {}
        private final List<PaneEntry> entries = new ArrayList<>();
        private int keyCounter = 1;

        private Builder() {}

        public Builder pane(String title, Node content) {
            entries.add(new PaneEntry("accordion-" + keyCounter++, title, content));
            return this;
        }

        /**
         * 内部委托 CollapseAnt accordion 模式构建。
         * 返回 {@link VBox}（与 CollapseAnt 一致），不再返回 JavaFX Accordion。
         */
        public VBox build() {
            CollapseAnt.Builder collapseBuilder = CollapseAnt.create().accordion(true);
            for (PaneEntry entry : entries) {
                collapseBuilder.panel(entry.key(), entry.title(), entry.content());
            }
            VBox collapse = collapseBuilder.build();
            applyStyles(collapse);
            return collapse;
        }
    }
}
