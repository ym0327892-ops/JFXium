package org.openkawu.jfxium.core.token;

public sealed interface DesignToken permits ColorToken, SpacingToken, RadiusToken, TypographyToken, ShadowToken, AnimationToken {
    String name();
}
