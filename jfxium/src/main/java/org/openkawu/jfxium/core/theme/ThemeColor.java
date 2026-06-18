package org.openkawu.jfxium.core.theme;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主题色工具 —— 支持运行时动态更换主色。
 *
 * <p>提供 11 个预设色板（对齐 Ant Design 色彩体系），并可根据任意 hex 颜色
 * 自动生成 0-9 色阶（亮色 / 暗色各一套）。</p>
 */
public class ThemeColor {

    /**
     * 预设色板 —— 对齐 Ant Design 色彩体系。
     */
    public enum Preset {
        BLUE("#1677ff", "Ant Design Blue"),
        PURPLE("#722ed1", "Ant Design Purple"),
        CYAN("#13c2c2", "Ant Design Cyan"),
        GREEN("#52c41a", "Ant Design Green"),
        MAGENTA("#eb2f96", "Ant Design Magenta"),
        RED("#f5222d", "Ant Design Red"),
        ORANGE("#fa8c16", "Ant Design Orange"),
        GOLD("#faad14", "Ant Design Gold"),
        LIME("#a0d911", "Ant Design Lime"),
        GEEKBLUE("#2f54eb", "Ant Design GeekBlue"),
        VOLCANO("#fa541c", "Ant Design Volcano");

        private final String hexColor;
        private final String displayName;

        Preset(String hexColor, String displayName) {
            this.hexColor = hexColor;
            this.displayName = displayName;
        }

        public String getHexColor() {
            return hexColor;
        }

        public String getDisplayName() {
            return displayName;
        }

        /**
         * 按 hex 颜色反查预设色板。
         * <p>用于 UI 回显当前主题色；若不是内置预设则返回 null。</p>
         */
        public static Preset fromHex(String hex) {
            if (hex == null || hex.isBlank()) {
                return null;
            }
            for (Preset preset : values()) {
                if (preset.hexColor.equalsIgnoreCase(hex)) {
                    return preset;
                }
            }
            return null;
        }
    }

    private String hexColor;

    /** 亮色色阶 LRU 缓存，key=hex 颜色，value=10 色阶数组。上限 32。 */
    private static final Map<String, String[]> LIGHT_SCALE_CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(32, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String[]> eldest) {
                    return size() > 32;
                }
            });

    /** 暗色色阶 LRU 缓存。 */
    private static final Map<String, String[]> DARK_SCALE_CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(32, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String[]> eldest) {
                    return size() > 32;
                }
            });

    public ThemeColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public ThemeColor(Preset preset) {
        this.hexColor = preset.getHexColor();
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    /**
     * 从主色生成 0-9 亮色色阶。
     * <p>
     * 色阶规则（对齐 Ant Design）：
     * <ul>
     *   <li>i=0：最浅（极浅背景，~95% 白 + 5% 主色）</li>
     *   <li>i=4：浅色调</li>
     *   <li>i=5：base（主色本身）</li>
     *   <li>i=9：最深</li>
     * </ul>
     * <p>
     * 历史 bug：旧实现 {@code factor = 1 - i*0.18} 在 i=0 时算出
     * {@code interpolate(white, 0) = base 本色}，导致 accent-0 = 主色实色，
     * menu-item-selected 选中态背景（绑到 accent-subtle = accent-0）变实色把文字吞掉。
     */
    public String[] generateColorScale() {
        String[] cached = LIGHT_SCALE_CACHE.get(hexColor);
        if (cached != null) return cached;

        Color base = Color.web(hexColor);
        String[] scale = new String[10];

        // 0-4：渐进从浅到 base（i=0 最接近白，i=4 接近 base 但仍偏浅）
        // mix 是「base 占比」：i=0 → 5% base + 95% white，i=4 → 70% base + 30% white
        double[] lightMix = {0.05, 0.20, 0.35, 0.50, 0.70};
        for (int i = 0; i < 5; i++) {
            Color c = Color.WHITE.interpolate(base, lightMix[i]);
            scale[i] = toHex(c);
        }

        // base
        scale[5] = hexColor;

        // 6-9：从 base 渐进到最深（mix 是「black 占比」）
        double[] darkMix = {0.15, 0.30, 0.50, 0.70};
        for (int i = 6; i < 10; i++) {
            Color c = base.interpolate(Color.BLACK, darkMix[i - 6]);
            scale[i] = toHex(c);
        }

        LIGHT_SCALE_CACHE.put(hexColor, scale);
        return scale;
    }

    /**
     * 从主色生成 0-9 暗色色阶。
     * <p>
     * 暗色主题色阶规则：
     * <ul>
     *   <li>i=0：最深（极深背景，base 混 70% 黑）</li>
     *   <li>i=4：base 混轻黑</li>
     *   <li>i=5：base（视觉上稍提亮 20% 适配暗背景）</li>
     *   <li>i=9：最浅（白底）</li>
     * </ul>
     */
    public String[] generateDarkColorScale() {
        String[] cached = DARK_SCALE_CACHE.get(hexColor);
        if (cached != null) return cached;

        Color base = Color.web(hexColor);
        String[] scale = new String[10];

        // 0-4：从最深到接近 base（mix 是「black 占比」，i=0 最深，i=4 微深）
        double[] darkMix = {0.70, 0.50, 0.30, 0.15, 0.05};
        for (int i = 0; i < 5; i++) {
            Color c = base.interpolate(Color.BLACK, darkMix[i]);
            scale[i] = toHex(c);
        }

        // 5-9：暗色主题主色稍提亮（让选中态在深底上更醒目）
        scale[5] = toHex(base.interpolate(Color.WHITE, 0.20));
        scale[6] = toHex(base.interpolate(Color.WHITE, 0.35));
        scale[7] = toHex(base.interpolate(Color.WHITE, 0.50));
        scale[8] = toHex(base.interpolate(Color.WHITE, 0.65));
        scale[9] = toHex(base.interpolate(Color.WHITE, 0.80));

        DARK_SCALE_CACHE.put(hexColor, scale);
        return scale;
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
