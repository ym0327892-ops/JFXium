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
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.Callbacks;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

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

    // i18n keys: launchpad.title, launchpad.description, launchpad.action

    private LaunchPadTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Item(String key, String title, String description, IconAnt.Path icon, String actionText) {}

        private String title = null;
        private String description = null;
        private int columns = 2;
        private double gap = 16;
        private final List<Item> items = new ArrayList<>();
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("launchpad.title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("launchpad.description"));
            return this;
        }

        public Builder columns(int columns) {
            this.columns = (int) TextUtils.ensureAtLeastOne(columns);
            return this;
        }

        public Builder gap(double gap) {
            this.gap = TextUtils.safeNonNegative(gap, 16);
            return this;
        }

        public Builder onAction(Consumer<String> handler) {
            this.onAction = handler;
            return this;
        }

        public Builder item(String key, String title, String description, IconAnt.Path icon) {
            return item(key, title, description, icon, Messages.get("launchpad.action"));
        }

        public Builder item(String key, String title, String description, IconAnt.Path icon, String actionText) {
            items.add(new Item(
                    TextUtils.safeText(key),
                    TextUtils.safeText(title),
                    TextUtils.safeText(description),
                    icon != null ? icon : IconAnt.Path.DASHBOARD,
                    TextUtils.safeText(actionText, Messages.get("launchpad.action"))
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
            String resolvedTitle = TextUtils.safeText(title, Messages.get("launchpad.title"));
            String resolvedDescription = TextUtils.safeText(description, Messages.get("launchpad.description"));
            return VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(resolvedTitle, 4).build(),
                            TypographyAnt.text(resolvedDescription)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();
        }

        private Node buildGrid() {
            if (items.isEmpty()) {
                return VBoxAnt.create().build();
            }

            GridAnt.Row row = GridAnt.row().align(Pos.TOP_LEFT);
            int span = (int) TextUtils.ensureAtLeastOne(24 / columns);
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
            ButtonAnt action = ButtonAnt.link(item.actionText(), Size.SMALL)
                    .onClick(e -> Callbacks.fire(onAction, item.key()))
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
                                                                    .type(TypographyAnt.TextColor.SECONDARY)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build(),
                            TypographyAnt.text("路由：" + item.key())
                                    .type(TypographyAnt.TextColor.SECONDARY)
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
    }
}
