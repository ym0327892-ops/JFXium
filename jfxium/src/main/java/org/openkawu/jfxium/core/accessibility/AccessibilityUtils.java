package org.openkawu.jfxium.core.accessibility;

import javafx.scene.Node;
import javafx.scene.control.*;

/**
 * Accessibility utilities for JFXium components.
 * Provides helper methods for ARIA labels, focus management, and keyboard navigation.
 */
public class AccessibilityUtils {

    /**
     * Set ARIA label for a node.
     */
    public static void setAriaLabel(Node node, String label) {
        node.setAccessibleText(label);
    }

    /**
     * Set ARIA description for a node.
     */
    public static void setAriaDescription(Node node, String description) {
        node.setAccessibleHelp(description);
    }

    /**
     * Set ARIA role for a node.
     */
    public static void setAriaRole(Node node, String role) {
        node.setAccessibleRoleDescription(role);
    }

    /**
     * Make a node focus traversable.
     */
    public static void makeFocusable(Node node) {
        node.setFocusTraversable(true);
    }

    /**
     * Configure a button with accessibility features.
     */
    public static void configureButton(ButtonBase button, String label, String description) {
        button.setAccessibleText(label);
        if (description != null) {
            button.setAccessibleHelp(description);
        }
        button.setFocusTraversable(true);
    }

    /**
     * Configure a text input with accessibility features.
     */
    public static void configureTextInput(TextInputControl input, String label, String placeholder) {
        input.setAccessibleText(label);
        input.setPromptText(placeholder);
        input.setFocusTraversable(true);
    }

    /**
     * Configure a labeled control with accessibility features.
     */
    public static void configureLabeled(Labeled labeled, String label, String description) {
        labeled.setAccessibleText(label);
        if (description != null) {
            labeled.setAccessibleHelp(description);
        }
    }

    /**
     * Set keyboard shortcut for a button.
     */
    public static void setKeyboardShortcut(ButtonBase button, javafx.scene.input.KeyCombination shortcut) {
        // Mnemonic parsing is enabled by default in JavaFX
        button.setMnemonicParsing(true);
    }

    /**
     * Add focus visible styling to a node.
     */
    public static void addFocusVisible(Node node) {
        node.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                node.setStyle(node.getStyle() + "-fx-focus-color: -color-accent-emphasis; -fx-faint-focus-color: -color-accent-subtle;");
            } else {
                node.setStyle(node.getStyle().replace("-fx-focus-color: -color-accent-emphasis; -fx-faint-focus-color: -color-accent-subtle;", ""));
            }
        });
    }
}
