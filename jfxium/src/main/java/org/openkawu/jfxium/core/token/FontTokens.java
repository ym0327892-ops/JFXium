package org.openkawu.jfxium.core.token;

/**
 * 字体族字面常量集中管理。
 *
 * <p>JavaFX {@code Font.font(String family, ...)} 接受的 {@code family} 是字符串字面量，
 * 与 {@link TypographyToken}（语义枚举，描述 SM/MD/LG 等字号级别）职责不同：</p>
 * <ul>
 *   <li>{@link FontTokens} —— 字面常量，供 JavaFX Font API 直接消费</li>
 *   <li>{@link TypographyToken} —— 语义枚举，供业务层引用（未来可与 LESS {@code @font-family} 绑定）</li>
 * </ul>
 *
 * <p><b>为什么不用 {@code enum} 而用 {@code static final String}？</b><br>
 * 因为 JavaFX Font API 强制接受 {@code String}，枚举反而要在调用点写 {@code .name()}，
 * 既冗余又模糊（无法表达"这是 font family"语义）。集中在本类便于：
 * <ol>
 *   <li>统一修改一处生效所有引用</li>
 *   <li>未来切换主题/平台时只改本类</li>
 *   <li>Magic Value 集中点，Code Review 一眼可见</li>
 * </ol></p>
 *
 * <h3>字体族选择依据</h3>
 * <ul>
 *   <li>{@link #TEXT_FAMILY} —— 业务文本。JavaFX 在 macOS 上会自动解析为
 *       {@code .AppleSystemUIFont}，Windows 上为 {@code Segoe UI}，Linux 上为 {@code System}
 *       （具体由 JavaFX 内部决定）。在 LESS 端可通过 {@code -fx-font-family} 覆盖。</li>
 *   <li>{@link #SYMBOL_FAMILY} —— Unicode 符号专用字体（含箭头/勾选/数学符号）。
 *       "Segoe UI Symbol" 在 Windows / macOS 自带；Linux 用户需安装 noto-fonts-sym 备援。</li>
 *   <li>{@link #MONOSPACE_FAMILY} —— 等宽字体，用于代码块 / 表格数字对齐。</li>
 * </ul>
 *
 * <p><b>P0-F4 修复</b>:此前 IconAnt / TypographyAnt 直接写字面量
 * （{@code "Segoe UI Symbol"} / {@code "System"}），分散且无法统一切换。
 * 集中到本类。</p>
 *
 * <p><b>P1 修复</b>: 新增标题字号常量和行高常量，消除 TypographyAnt / LabelAnt
 * 中硬编码的 38/30/24/20/16/14 字号和 20 行高魔法值。</p>
 */
public final class FontTokens {

    private FontTokens() {
        // 静态常量类，禁止实例化
    }

    // ============================================================
    // 字体族
    // ============================================================

    /** 业务文本字体族（Label / TextField / Paragraph / Title 默认字体）。 */
    public static final String TEXT_FAMILY = "System";

    /** Unicode 符号字体族（箭头 / 勾选 / × / ✓ / ⓘ 等窗口控制符号）。
     *  对应 {@code org.kordamp.ikonli} 内置字体；与 Windows 自带 Segoe UI Symbol 兼容。 */
    public static final String SYMBOL_FAMILY = "Segoe UI Symbol";

    /** 等宽字体族（代码块 / 数字对齐表格）。 */
    public static final String MONOSPACE_FAMILY = "Monospace";

    // ============================================================
    // 标题字号（与 LESS @font-size-h1..h5 对齐）
    // ============================================================

    /** H1 标题字号（38px）。 */
    public static final double TITLE_H1 = 38;
    /** H2 标题字号（30px）。 */
    public static final double TITLE_H2 = 30;
    /** H3 标题字号（24px）。 */
    public static final double TITLE_H3 = 24;
    /** H4 标题字号（20px）。 */
    public static final double TITLE_H4 = 20;
    /** H5 标题字号（16px）。 */
    public static final double TITLE_H5 = 16;

    // ============================================================
    // 正文/行高（与 LESS @font-size-md / @line-height-normal 对齐）
    // ============================================================

    /** 正文字号（14px），对应 LESS {@code @font-size-md}。 */
    public static final double FONT_SIZE_MD = 14;

    /** 行高（20px），用于 ellipsis 高度计算。对应 {@code @font-size-md * @line-height-normal ≈ 14 * 1.4}。 */
    public static final double LINE_HEIGHT_NORMAL = 20;
}