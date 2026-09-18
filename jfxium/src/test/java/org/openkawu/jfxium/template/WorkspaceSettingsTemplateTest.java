package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.theme.ThemeColor;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.component.overlay.DrawerAnt;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WorkspaceSettingsTemplate")
class WorkspaceSettingsTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("主题 / 主色 / 水印开关都能正确回显与触发")
    void buildAndBind_safe() {
        ThemeManager mgr = ThemeManager.getInstance();
        ThemeManager.Family oldFamily = mgr.getCurrentFamily();
        boolean oldDark = mgr.isDark();
        ThemeDensity oldDensity = mgr.getDensity();
        String oldHex = mgr.getCurrentThemeColor().getHexColor();

        try {
            mgr.setFamily(ThemeManager.Family.ANT_DESIGN);
            mgr.setDark(true);
            mgr.setDensity(ThemeDensity.COMPACT);
            mgr.setPrimaryColor(ThemeColor.Preset.ORANGE);

            AtomicBoolean watermarkVisible = new AtomicBoolean(false);
            VBox root = WorkspaceSettingsTemplate.create()
                    .themeManager(mgr)
                    .watermarkVisibleSupplier(watermarkVisible::get)
                    .onWatermarkVisibleChanged(watermarkVisible::set)
                    .build();

            assertNotNull(root);
            assertEquals(5, root.getChildren().size(), "标题 + 4 个设置分组");

            ComboBox<?> familySelect = findComboBox(root, ThemeManager.Family.values().length);
            assertNotNull(familySelect);
            assertEquals(ThemeManager.Family.ANT_DESIGN, familySelect.getValue());

            ComboBox<?> colorSelect = findComboBox(root, ThemeColor.Preset.values().length);
            assertNotNull(colorSelect);
            assertEquals(ThemeColor.Preset.ORANGE, colorSelect.getValue());

            HBox watermarkRow = findRow(root, "显示水印");
            assertNotNull(watermarkRow);
            HBox watermarkSwitch = (HBox) watermarkRow.getChildren().get(1);
            StackPane switchPane = (StackPane) watermarkSwitch.getChildren().get(0);
            assertFalse(switchPane.getStyleClass().contains(JfxStyles.SWITCH_SELECTED),
                    "supplier=false 时开关应默认关闭");

            assertNotNull(switchPane.getOnMouseClicked());
            switchPane.getOnMouseClicked().handle(null);
            assertTrue(watermarkVisible.get(), "点击开关应触发回调");
        } finally {
            mgr.setFamily(oldFamily);
            mgr.setDark(oldDark);
            mgr.setDensity(oldDensity);
            mgr.setPrimaryColor(oldHex);
        }
    }

    @Test
    @DisplayName("drawer helper 可一行构建设置抽屉")
    void drawer_helperBuildsDrawer() {
        DrawerAnt.DrawerResult result = WorkspaceSettingsTemplate.drawer(420, () -> true, visible -> {});

        assertNotNull(result);
    }

    private static ComboBox<?> findComboBox(VBox root, int expectedItemCount) {
        for (Node node : root.getChildren()) {
            ComboBox<?> found = findComboBoxRecursive(node, expectedItemCount);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static ComboBox<?> findComboBoxRecursive(Node node, int expectedItemCount) {
        if (node instanceof ComboBox<?> comboBox && comboBox.getItems().size() == expectedItemCount) {
            return comboBox;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                ComboBox<?> found = findComboBoxRecursive(child, expectedItemCount);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static HBox findRow(VBox root, String labelText) {
        for (Node node : root.getChildren()) {
            HBox found = findRowRecursive(node, labelText);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static HBox findRowRecursive(Node node, String labelText) {
        if (node instanceof Label label && labelText.equals(label.getText()) && label.getParent() instanceof HBox row) {
            return row;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                HBox found = findRowRecursive(child, labelText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
