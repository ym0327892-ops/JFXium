package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;
import org.openkawu.jfxium.component.composite.SwitchAnt;
import org.openkawu.jfxium.component.control.ComboBoxAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.DrawerAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.theme.ThemeColor;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * WorkspaceSettingsTemplate - 工作台外观设置抽屉内容模板。
 *
 * <p>用于聚合工作台壳的常用全局设置：
 * 主题家族、明暗模式、密度、主色和水印。它的目标不是替代业务状态，
 * 而是把这类重复的“设置面板骨架”从 demo 里收口出来，方便多个工作台页复用。</p>
 */
public final class WorkspaceSettingsTemplate {

    private WorkspaceSettingsTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    /**
     * 一行构建工作台外观设置抽屉。
     *
     * <p>适合工程控制台 / 工作台壳常用的右侧设置面板，调用方只需要提供水印开关状态与回调。</p>
     */
    public static DrawerAnt.DrawerResult drawer(int width,
                                                Supplier<Boolean> watermarkVisibleSupplier,
                                                Consumer<Boolean> onWatermarkVisibleChanged) {
        return DrawerAnt.create()
                .title(Messages.get("settings.title"))
                .width(TextUtils.ensureAtLeastOne(width))
                .content(WorkspaceSettingsTemplate.create()
                        .watermarkVisibleSupplier(watermarkVisibleSupplier)
                        .onWatermarkVisibleChanged(onWatermarkVisibleChanged)
                        .build())
                .placement(DrawerAnt.Placement.RIGHT)
                .build();
    }

    /** 使用默认宽度的一行抽屉构建方法。 */
    public static DrawerAnt.DrawerResult drawer(Supplier<Boolean> watermarkVisibleSupplier,
                                                Consumer<Boolean> onWatermarkVisibleChanged) {
        return drawer(420, watermarkVisibleSupplier, onWatermarkVisibleChanged);
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private ThemeManager themeManager = ThemeManager.getInstance();
        private Supplier<Boolean> watermarkVisibleSupplier;
        private Consumer<Boolean> onWatermarkVisibleChanged;
        private String title = null;
        private String description = null;

        private Builder() {}

        /** 主题管理器，默认使用全局单例。 */
        public Builder themeManager(ThemeManager themeManager) {
            this.themeManager = themeManager != null ? themeManager : ThemeManager.getInstance();
            return this;
        }

        /** 水印显示状态读取器。null 时默认视为显示。 */
        public Builder watermarkVisibleSupplier(Supplier<Boolean> supplier) {
            this.watermarkVisibleSupplier = supplier;
            return this;
        }

        /** 水印切换回调。 */
        public Builder onWatermarkVisibleChanged(Consumer<Boolean> handler) {
            this.onWatermarkVisibleChanged = handler;
            return this;
        }

        /** 面板标题。null 视为空字符串(由 TextUtils.safeText 收口,见 P1-2)。 */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /** 面板说明。null 视为空字符串(由 TextUtils.safeText 收口,见 P1-2)。 */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public VBox build() {
            ThemeManager mgr = themeManager != null ? themeManager : ThemeManager.getInstance();
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .padding(16)
                    .children(
                            buildHeader(),
                            settingGroup(Messages.get("settings.group_design_language"),
                                    settingRow(Messages.get("settings.row_design_style"), buildFamilySelect(mgr))),
                            settingGroup(Messages.get("settings.group_appearance"),
                                    settingRow(Messages.get("settings.row_dark_mode"), buildDarkSwitch(mgr)),
                                    settingRow(Messages.get("settings.row_compact_mode"), buildDensitySwitch(mgr))),
                            settingGroup(Messages.get("settings.group_accent_color"),
                                    settingRow(Messages.get("settings.row_primary_color"), buildPrimaryColorSelect(mgr))),
                            settingGroup(Messages.get("settings.group_watermark"),
                                    settingRow(Messages.get("settings.row_show_watermark"), buildWatermarkSwitch()))
                    )
                    .build();

            applyStyles(root);
            return root;
        }

        private VBox buildHeader() {
            VBox header = VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(TextUtils.safeText(title, Messages.get("settings.title")), 3).build(),
                            TypographyAnt.text(TextUtils.safeText(description, Messages.get("settings.description")))
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();
            return header;
        }

        private Node buildFamilySelect(ThemeManager mgr) {
            ComboBox<ThemeManager.Family> familySelect = ComboBoxAnt.<ThemeManager.Family>create()
                    .items(ThemeManager.Family.values())
                    .value(mgr.getCurrentFamily())
                    .size(Size.SMALL)
                    .onChange(family -> {
                        if (family != null) {
                            mgr.setFamily(family);
                        }
                    })
                    .build();
            familySelect.setConverter(new StringConverter<>() {
                @Override
                public String toString(ThemeManager.Family value) {
                    return value == null ? "" : value.getDisplayName();
                }

                @Override
                public ThemeManager.Family fromString(String string) {
                    return null;
                }
            });
            return familySelect;
        }

        private Node buildDarkSwitch(ThemeManager mgr) {
            return SwitchAnt.create()
                    .selected(mgr.isDark())
                    .checkedText(Messages.get("settings.dark_on"))
                    .uncheckedText(Messages.get("settings.dark_off"))
                    .onChange(mgr::setDark)
                    .build();
        }

        private Node buildDensitySwitch(ThemeManager mgr) {
            return SwitchAnt.create()
                    .selected(mgr.getDensity() == ThemeDensity.COMPACT)
                    .checkedText(Messages.get("settings.compact_on"))
                    .uncheckedText(Messages.get("settings.compact_off"))
                    .onChange(checked -> mgr.setDensity(checked ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT))
                    .build();
        }

        private Node buildPrimaryColorSelect(ThemeManager mgr) {
            ComboBox<ThemeColor.Preset> colorSelect = ComboBoxAnt.<ThemeColor.Preset>create()
                    .items(ThemeColor.Preset.values())
                    .value(mgr.getCurrentPrimaryPreset())
                    .placeholder(mgr.getCurrentThemeColor().getHexColor())
                    .size(Size.SMALL)
                    .onChange(preset -> {
                        if (preset != null) {
                            mgr.setPrimaryColor(preset);
                        }
                    })
                    .build();
            colorSelect.setConverter(new StringConverter<>() {
                @Override
                public String toString(ThemeColor.Preset value) {
                    return value == null ? "" : value.getDisplayName();
                }

                @Override
                public ThemeColor.Preset fromString(String string) {
                    return null;
                }
            });
            return colorSelect;
        }

        private Node buildWatermarkSwitch() {
            boolean watermarkVisible = watermarkVisibleSupplier == null || Boolean.TRUE.equals(watermarkVisibleSupplier.get());
            return SwitchAnt.create()
                    .selected(watermarkVisible)
                    .checkedText("显示")
                    .uncheckedText("隐藏")
                    .onChange(checked -> {
                        if (onWatermarkVisibleChanged != null) {
                            onWatermarkVisibleChanged.accept(checked);
                        }
                    })
                    .build();
        }

        private VBox settingGroup(String title, Node... rows) {
            VBox content = VBoxAnt.create()
                    .spacing(12)
                    .children(rows)
                    .build();
            return GroupBoxAnt.create()
                    .title(title)
                    .content(content)
                    .bordered(true)
                    .build();
        }

        private HBoxAnt settingRow(String label, Node control) {
            HBoxAnt row = HBoxAnt.create()
                    .spacing(12)
                    .align(Pos.CENTER_LEFT)
                    .children(
                            TypographyAnt.text(TextUtils.safeText(label))
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build(),
                            control
                    )
                    .build();
            if (control instanceof ComboBox<?> comboBox) {
                HBox.setHgrow(comboBox, javafx.scene.layout.Priority.ALWAYS);
                comboBox.setMaxWidth(Double.MAX_VALUE);
            }
            return row;
        }
    }
}
