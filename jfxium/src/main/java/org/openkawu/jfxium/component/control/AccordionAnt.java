package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 手风琴组件 - 对标 Ant Design Collapse（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：可折叠面板容器，包装 JavaFX {@link Accordion}，
 * 多个 {@link TitledPane} 同时只展开一个（互斥折叠）。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>添加多个折叠面板（标题 + 内容）</li>
 *   <li>支持直接传入自定义 {@link TitledPane}（用 {@link TitledPaneAnt} 构建）</li>
 *   <li>自动互斥展开（同时只开一个面板）</li>
 *   <li>继承 {@link AbstractStyleBuilder}，支持 {@code .styleClass()} / {@code .style()}</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>FAQ 问答列表（问题折叠展开）</li>
 *   <li>设置面板（分组折叠高级选项）</li>
 *   <li>侧边栏导航（分类折叠子菜单）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础用法
 * Accordion accordion = AccordionAnt.create()
 *     .pane("面板一", new Label("内容一"))
 *     .pane("面板二", new Label("内容二"))
 *     .pane("面板三", new Label("内容三"))
 *     .build();
 *
 * // 结合 TitledPaneAnt 自定义面板
 * Accordion advanced = AccordionAnt.create()
 *     .pane(TitledPaneAnt.create()
 *         .title("高级设置")
 *         .content(settingsForm)
 *         .expanded(false)
 *         .build())
 *     .build();
 * }</pre>
 *
 * <h2>与 CollapseAnt 的区别</h2>
 * <ul>
 *   <li>{@code AccordionAnt} —— JavaFX 原生互斥折叠（同时只开一个）</li>
 *   <li>{@code CollapseAnt} —— 对标 Ant Design Collapse，支持多个同时展开（accordion 模式可选）</li>
 * </ul>
 */
public class AccordionAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<TitledPane> panes = new ArrayList<>();

        private Builder() {}

        public Builder pane(String title, Node content) {
            TitledPane pane = new TitledPane(title, content);
            panes.add(pane);
            return this;
        }

        public Builder pane(TitledPane pane) {
            panes.add(pane);
            return this;
        }

        public Accordion build() {
            Accordion accordion = new Accordion();
            accordion.getPanes().addAll(panes);
            accordion.getStyleClass().add("jfx-accordion");
            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(accordion);
            return accordion;
        }
    }
}
