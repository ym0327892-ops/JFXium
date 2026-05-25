package org.openkawu.jfxium.demo.admin.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.container.VBoxBuilder;

/**
 * 占位页面：用于骨架阶段所有未实现的页面。
 *
 * <p>设计意图：M12.1 骨架先行，每个真页面后续单独实现。
 * 占位页提供统一外观，看到就知道"这页还没做"。</p>
 */
public class PlaceholderPage implements AdminPage {
    private final String key;
    private final String title;

    public PlaceholderPage(String key, String title) {
        this.key = key;
        this.title = title;
    }

    @Override public String key()    { return key; }
    @Override public String title()  { return title; }

    @Override
    public Node getView() {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 600;");

        Label hintLabel = new Label("（页面骨架占位，真实内容待 M12.2+ 实现）");
        hintLabel.setStyle("-fx-text-fill: -color-fg-muted;");

        Label keyLabel = new Label("page key: " + key);
        keyLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-family: monospace;");

        VBox view = VBoxBuilder.create()
                .spacing(12)
                .padding(40)
                .align(Pos.TOP_LEFT)
                .children(titleLabel, hintLabel, keyLabel)
                .build();
        return view;
    }
}
