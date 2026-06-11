package org.openkawu.jfxium.demo.admin.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.component.composite.*;
import org.openkawu.jfxium.component.control.*;
import org.openkawu.jfxium.core.theme.ThemeManager;

/**
 * SettingsPage —— 系统设置，展示 Descriptions / ColorPicker / ToggleButton / Segmented 等。
 */
public class SettingsPage extends VBox {

    public SettingsPage() {
        setPadding(new javafx.geometry.Insets(24));
        setSpacing(20);
        getChildren().addAll(
                new Label("⚙️ 系统设置"),
                themeSection(),
                displaySection()
        );
    }

    private Node themeSection() {
        ColorPickerAnt picker = ColorPickerAnt.create()
                .value(Color.valueOf("#1677ff"))
                .build();
        VBox content = new VBox(8, new Label("品牌色"), picker);
        picker.setOnAction(e -> ThemeManager.getInstance().setPrimaryColor(toHex(picker.getValue())));

        Node desc = DescriptionsAnt.create()
                .item("应用名称", "JFXium Admin Demo")
                .item("版本号", "1.0-SNAPSHOT")
                .item("JDK", "Java 21")
                .item("框架", "JavaFX 21.0.6")
                .build();

        return GroupBoxAnt.create()
                .title("品牌色")
                .content(new VBox(12, content, desc))
                .build();
    }

    private Node displaySection() {
        ToggleButton toggle = ToggleButtonAnt.create("紧凑模式").build();

        HBox seg = SegmentedAnt.create()
                .option("default", "默认")
                .option("compact", "紧凑")
                .build();

        return GroupBoxAnt.create()
                .title("显示")
                .content(new VBox(12, new Label("视图切换"), seg, toggle))
                .build();
    }

    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }
}
