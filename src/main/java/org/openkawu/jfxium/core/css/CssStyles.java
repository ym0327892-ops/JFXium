package org.openkawu.jfxium.core.css;

public final class CssStyles {

    private CssStyles() {}

    public static String generateFullStylesheet() {
        return BASE_STYLES;
    }

    private static final String BASE_STYLES = """
        /* ============================================
           JFXium Base Styles
           Inspired by AtlantaFX & GitHub Primer
           ============================================ */

        /* ---- Root & Global ---- */
        .root {
          -fx-font-family: "Inter", "Segoe UI", system-ui, sans-serif;
          -fx-font-size: 14px;
          -fx-text-fill: -jfx-fg-default;
          -fx-background-color: -jfx-bg-default;
        }

        /* ---- Button Base ---- */
        .button {
          -fx-font-family: "Inter", "Segoe UI", system-ui, sans-serif;
          -fx-font-size: 14px;
          -fx-cursor: hand;
          -fx-focus-traversable: false;
        }

        /* ---- Text Input Base ---- */
        .text-field {
          -fx-font-family: "Inter", "Segoe UI", system-ui, sans-serif;
          -fx-font-size: 14px;
          -fx-text-fill: -jfx-fg-default;
          -fx-background-color: -jfx-bg-default;
          -fx-border-color: -jfx-border-default;
          -fx-border-width: 1px;
          -fx-border-radius: 6px;
          -fx-background-radius: 6px;
          -fx-padding: 8px 12px;
        }
        .text-field:focused {
          -fx-border-color: -jfx-accent;
          -fx-effect: dropshadow(gaussian, -jfx-accent, 4, 0, 0, 0);
        }

        /* ---- Label Base ---- */
        .label {
          -fx-font-family: "Inter", "Segoe UI", system-ui, sans-serif;
          -fx-text-fill: -jfx-fg-default;
        }

        /* ============================================
           JFXium Button Component
           ============================================ */
        .jfx-button {
          -fx-font-size: 14px;
          -fx-font-weight: 400;
          -fx-text-fill: -jfx-fg-default;
          -fx-background-color: -jfx-bg-default;
          -fx-border-color: -jfx-border-default;
          -fx-border-width: 1px;
          -fx-border-radius: 6px;
          -fx-background-radius: 6px;
          -fx-padding: 8px 16px;
          -fx-cursor: hand;
        }
        .jfx-button:hover {
          -fx-border-color: -jfx-accent;
          -fx-text-fill: -jfx-accent;
          -fx-background-color: -jfx-bg-subtle;
        }
        .jfx-button:pressed {
          -fx-border-color: -jfx-accent-active;
          -fx-text-fill: -jfx-accent-active;
        }
        .jfx-button:disabled {
          -fx-opacity: 0.6;
          -fx-cursor: default;
        }

        /* Button Types */
        .jfx-button-primary {
          -fx-text-fill: white;
          -fx-background-color: -jfx-accent;
          -fx-border-color: -jfx-accent;
        }
        .jfx-button-primary:hover {
          -fx-background-color: -jfx-accent-hover;
          -fx-border-color: -jfx-accent-hover;
          -fx-text-fill: white;
        }
        .jfx-button-primary:pressed {
          -fx-background-color: -jfx-accent-active;
          -fx-border-color: -jfx-accent-active;
        }

        .jfx-button-dashed {
          -fx-border-style: dashed;
        }

        .jfx-button-text {
          -fx-background-color: transparent;
          -fx-border-color: transparent;
        }
        .jfx-button-text:hover {
          -fx-background-color: -jfx-bg-subtle;
        }

        .jfx-button-link {
          -fx-background-color: transparent;
          -fx-border-color: transparent;
          -fx-text-fill: -jfx-accent;
          -fx-underline: true;
        }
        .jfx-button-link:hover {
          -fx-text-fill: -jfx-accent-hover;
        }

        /* Button Sizes */
        .jfx-button-small {
          -fx-font-size: 12px;
          -fx-padding: 4px 8px;
        }
        .jfx-button-large {
          -fx-font-size: 16px;
          -fx-padding: 12px 20px;
        }

        /* Button Shapes */
        .jfx-button-rounded {
          -fx-border-radius: 9999px;
          -fx-background-radius: 9999px;
        }
        .jfx-button-square {
          -fx-border-radius: 0;
          -fx-background-radius: 0;
        }

        /* ============================================
           JFXium Input Component
           ============================================ */
        .jfx-input {
          -fx-font-size: 14px;
          -fx-text-fill: -jfx-fg-default;
          -fx-background-color: -jfx-bg-default;
          -fx-border-color: -jfx-border-default;
          -fx-border-width: 1px;
          -fx-border-radius: 6px;
          -fx-background-radius: 6px;
          -fx-padding: 8px 12px;
        }
        .jfx-input:focused {
          -fx-border-color: -jfx-accent;
          -fx-effect: dropshadow(gaussian, -jfx-accent, 4, 0, 0, 0);
        }
        .jfx-input:disabled {
          -fx-background-color: -jfx-bg-subtle;
          -fx-text-fill: -jfx-fg-disabled;
          -fx-opacity: 0.7;
        }
        .jfx-input-error {
          -fx-border-color: -jfx-error;
        }
        .jfx-input-error:focused {
          -fx-effect: dropshadow(gaussian, -jfx-error, 4, 0, 0, 0);
        }

        /* Input Sizes */
        .jfx-input-small {
          -fx-font-size: 12px;
          -fx-padding: 4px 8px;
        }
        .jfx-input-large {
          -fx-font-size: 16px;
          -fx-padding: 12px 16px;
        }

        /* ============================================
           JFXium Card Component
           ============================================ */
        .jfx-card {
          -fx-background-color: -jfx-bg-default;
          -fx-border-color: -jfx-border-subtle;
          -fx-border-width: 1px;
          -fx-border-radius: 8px;
          -fx-background-radius: 8px;
          -fx-padding: 16px;
        }
        .jfx-card-bordered {
          -fx-border-color: -jfx-border-default;
        }
        .jfx-card-hoverable:hover {
          -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);
          -fx-translate-y: -2;
        }
        .jfx-card-shadow-sm {
          -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);
        }
        .jfx-card-shadow-md {
          -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);
        }
        .jfx-card-shadow-lg {
          -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 16, 0, 0, 4);
        }
        .jfx-card-title {
          -fx-font-size: 16px;
          -fx-font-weight: 600;
          -fx-text-fill: -jfx-fg-default;
          -fx-padding: 0 0 8px 0;
        }
        .jfx-card-extra {
          -fx-font-size: 14px;
          -fx-text-fill: -jfx-fg-muted;
        }
        .jfx-card-content {
          -fx-font-size: 14px;
          -fx-text-fill: -jfx-fg-muted;
        }
        """;
}
