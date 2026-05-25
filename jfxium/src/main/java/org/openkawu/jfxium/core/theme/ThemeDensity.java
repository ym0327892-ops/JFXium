package org.openkawu.jfxium.core.theme;

/**
 * Theme density mode, inspired by Ant Design Compact Theme.
 * Controls the spacing and sizing scale of components.
 */
public enum ThemeDensity {
    /**
     * Default density - standard padding and component sizes.
     * Equivalent to Ant Design default algorithm (sizeStep=4, controlHeight=32).
     */
    DEFAULT,

    /**
     * Compact density - reduced padding and smaller component sizes.
     * Equivalent to Ant Design compact algorithm (sizeStep=2, controlHeight=28).
     * All paddings, margins, and heights are reduced by ~25-30%.
     */
    COMPACT
}
