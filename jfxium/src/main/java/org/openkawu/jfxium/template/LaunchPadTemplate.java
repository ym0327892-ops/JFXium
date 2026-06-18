package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.AvatarAnt;
import org.openkawu.jfxium.component.composite.SurfaceAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * LaunchPadTemplate - 首页/控制台常用快捷入口模板。
 *
 * <p>用于把一组高频入口做成“标题 + 说明 + 卡片网格”的启动板，
 * 适合工程首页、工作台首页、后台控制台首页这类需要直接跳转常用页面的场景。</p>
 */
public final class LaunchPadTemplate {

    private static final String DEFAULT_TITLE = "快捷入口";
    private static final String DEFAULT_DESCRIPTION = "把最常用的页面和模板摆在首页，减少来回找目录的成本。";
    private static final String DEFAULT_ACTION_TEXT = "打开";

    private LaunchPadTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Item(String key, String title, String description, IconAnt.Path icon, String actionText) {}

        private String title = DEFAULT_TITLE;
        private String description = DEFAULT_DESCRIPTION;
        private int columns = 2;
        private double gap = 16;
        private final List<Item> items = new ArrayList<>();
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = title != null ? title : DEFAULT_TITLE;
            return this;
        }

        public Builder description(String description) {
            this.description = description != null ? description : DEFAULT_DESCRIPTION;
            return this;
        }

        public Builder columns(int columns) {
            this.columns = Math.max(1, columns);
            return this;
        }

        public Builder gap(double gap) {
            this.gap = Double.isFinite(gap) ? Math.max(0, gap) : 16;
            return this;
        }

        public Builder onAction(Consumer<String> handler) {
            this.onAction = handler;
            return this;
        }

        public Builder item(String key, String title, String description, IconAnt.Path icon) {
            return item(key, title, description, icon, DEFAULT_ACTION_TEXT);
        }

        public Builder item(String key, String title, String description, IconAnt.Path icon, String actionText) {
            items.add(new Item(
                    key != null ? key : "",
                    title != null ? title : "",
                    description != null ? description : "",
                    icon != null ? icon : IconAnt.Path.DASHBOARD,
                    actionText != null ? actionText : DEFAULT_ACTION_TEXT
            ));
            return this;
        }

        public VBox build() {
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .children(buildHeader(), buildGrid())
                    .build();
            applyStyles(root);
            return root;
        }

        private VBox buildHeader() {
            return VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(title, 4).build(),
                            TypographyAnt.text(description)
                                    .type(TypographyAnt.Type.SECONDARY)
                                    .build()
                    )
                    .build();
        }

        private Node buildGrid() {
            if (items.isEmpty()) {
                return VBoxAnt.create().build();
            }

            GridAnt.Row row = GridAnt.row().align(Pos.TOP_LEFT);
            int span = Math.max(1, 24 / columns);
            for (Item item : items) {
                row.col(GridAnt.col(buildCard(item))
                        .xs(24)
                        .sm(24)
                        .md(span)
                        .lg(span)
                        .xl(span)
                        .xxl(span));
            }

            return GridAnt.create()
                    .gutter(gap)
                    .responsive()
                    .row(row)
                    .build();
        }

        private Node buildCard(Item item) {
            ButtonAnt action = ButtonAnt.link(item.actionText(), ButtonAnt.Size.SMALL)
                    .onClick(e -> fire(item.key()))
                    .build();
            action.setDisable(onAction == null);

            VBox body = VBoxAnt.create()
                    .spacing(8)
                    .children(
                            HBoxAnt.create()
                                    .spacing(12)
                                    .align(Pos.CENTER_LEFT)
                                    .children(
                                            AvatarAnt.create()
                                                    .icon(IconAnt.path(item.icon(), 18))
                                                    .shape(AvatarAnt.Shape.SQUARE)
                                                    .size(40)
                                                    .build(),
                                            VBoxAnt.create()
                                                    .spacing(2)
                                                    .children(
                                                            TypographyAnt.text(item.title()).build(),
                                                            TypographyAnt.text(item.description())
                                                                    .type(TypographyAnt.Type.SECONDARY)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build(),
                            TypographyAnt.text("路由：" + item.key())
                                    .type(TypographyAnt.Type.SECONDARY)
                                    .build()
                    )
                    .build();

            return SurfaceAnt.create()
                    .title(item.title())
                    .extra(action)
                    .shadow(SurfaceAnt.Shadow.SMALL)
                    .bordered(true)
                    .content(body)
                    .gap(8)
                    .build();
        }

        private void fire(String key) {
            if (onAction != null) {
                onAction.accept(key);
            }
        }
    }
}
