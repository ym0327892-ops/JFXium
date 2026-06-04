package org.openkawu.jfxium.core.util;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * 输入限制工厂（M19.30 引入）。
 *
 * <p>JavaFX 标准做法是 {@link TextFormatter} + {@link UnaryOperator}{@code <Change>}，
 * 但样板代码长且容易写错。本工具集封装项目 admin 场景里 80% 高频的输入限制需求，
 * 直接 {@code setTextFormatter(TextFormatters.xxx())} 即可。</p>
 *
 * <h2>核心场景</h2>
 * <pre>{@code
 * // === 高频（前 5 名）===
 *
 * // 1. 整数（含负号）—— 库存、数量、金额（分）
 * TextField qty = InputAnt.create().build();
 * qty.setTextFormatter(TextFormatters.integerOnly());
 *
 * // 2. 正整数（无负号）—— 年龄、页数
 * age.setTextFormatter(TextFormatters.positiveInteger());
 *
 * // 3. 浮点数 —— 价格、百分比、坐标
 * price.setTextFormatter(TextFormatters.decimal(2));        // 最多 2 位小数
 *
 * // 4. 长度限制 —— 用户名、订单号
 * username.setTextFormatter(TextFormatters.maxLength(20));
 *
 * // 5. 整数范围 —— 1-100、年龄 0-150
 * level.setTextFormatter(TextFormatters.integerRange(0, 100));
 *
 * // === 中频（业务格式类）===
 *
 * // 6. 中国大陆手机号 —— 注册、登录
 * phone.setTextFormatter(TextFormatters.chinaPhoneNumber());
 *
 * // 7. 邮箱合法字符集 —— 注册、找回密码
 * email.setTextFormatter(TextFormatters.emailChars());
 *
 * // 8. 中国身份证号 —— 实名认证
 * idCard.setTextFormatter(TextFormatters.chinaIdCard());
 *
 * // 9. 仅大写字母 —— 验证码、车牌
 * captcha.setTextFormatter(TextFormatters.uppercaseLetters());
 *
 * // 10. 禁止空白 —— 用户名、URL
 * username.setTextFormatter(TextFormatters.noWhitespace());
 *
 * // === 业务格式（专项）===
 *
 * // 11. ISO 日期 YYYY-MM-DD
 * dateText.setTextFormatter(TextFormatters.isoDate());
 *
 * // 12. IPv4 地址
 * ipText.setTextFormatter(TextFormatters.ipv4());
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>允许过程态</b>：例如 {@link #integerOnly()} 允许只输入"-"还没补数字的中间状态，
 *       否则用户输负号会立即被拒</li>
 *   <li><b>允许空字符串</b>：用户可以清空输入框（"数字 + null"语义其实是空字符串而非 Java null）</li>
 *   <li><b>返回 null 表示拒绝</b>：遵循 {@link TextFormatter} 标准约定</li>
 *   <li><b>不绑死 InputAnt</b>：所有方法返回标准 {@link TextFormatter}，可作用于任意 {@code TextField}</li>
 * </ul>
 */
public final class TextFormatters {

    private TextFormatters() {}

    // ============================================================
    // 数值类型限制
    // ============================================================

    /**
     * 整数限制（含负号）。
     *
     * <p>允许：空字符串、单个"-"、纯数字、负数。
     * 拒绝：字母、特殊字符、多个负号、负号在中间。</p>
     */
    public static TextFormatter<String> integerOnly() {
        // 正则：可选负号开头 + 0 个或多个数字
        Pattern p = Pattern.compile("-?\\d*");
        return new TextFormatter<>(filterByPattern(p));
    }

    /**
     * 正整数限制（不允许负号）。
     *
     * <p>允许：空字符串、纯数字。
     * 拒绝：负号、字母、特殊字符。</p>
     */
    public static TextFormatter<String> positiveInteger() {
        Pattern p = Pattern.compile("\\d*");
        return new TextFormatter<>(filterByPattern(p));
    }

    /**
     * 浮点数限制。
     *
     * <p>允许：空字符串、单个"-"、整数、小数。
     * 拒绝：超过指定小数位数、多个小数点、字母。</p>
     *
     * @param maxDecimals 最多小数位数（必须 ≥ 0；传 0 等价于 {@link #integerOnly()}）
     * @throws IllegalArgumentException 当 {@code maxDecimals < 0}
     */
    public static TextFormatter<String> decimal(int maxDecimals) {
        if (maxDecimals < 0) {
            throw new IllegalArgumentException("maxDecimals 必须 >= 0，实际传入：" + maxDecimals);
        }
        // 正则解释：
        //   -?           可选负号
        //   \d*          整数部分（0 个或多个数字，允许 ".5" 这种省略 0 的写法）
        //   (\.\d{0,N})? 可选的小数部分（点 + 最多 N 位数字）
        Pattern p = Pattern.compile("-?\\d*(\\.\\d{0," + maxDecimals + "})?");
        return new TextFormatter<>(filterByPattern(p));
    }

    /**
     * 浮点数限制（无小数位限制）。等价于 {@code decimal(Integer.MAX_VALUE)}。
     */
    public static TextFormatter<String> decimal() {
        Pattern p = Pattern.compile("-?\\d*\\.?\\d*");
        return new TextFormatter<>(filterByPattern(p));
    }

    // ============================================================
    // 长度限制
    // ============================================================

    /**
     * 最大长度限制。新文本长度超过 {@code max} 时拒绝输入。
     *
     * <p>实现细节：通过 {@code change.getControlNewText().length() <= max} 判定，
     * 而非 {@code change.getText().length()}（后者只是本次新增/替换的子串）。</p>
     *
     * @param max 最大字符数（含空格、换行；必须 ≥ 0）
     * @throws IllegalArgumentException 当 {@code max < 0}
     */
    public static TextFormatter<String> maxLength(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max 必须 >= 0，实际传入：" + max);
        }
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= max ? change : null
        );
    }

    // ============================================================
    // 范围限制
    // ============================================================

    /**
     * 整数范围限制（含边界）。
     *
     * <p>过程态宽松（允许只输入"-"或前缀），最终值会被限制到 [{@code min}, {@code max}] 区间。
     * 与 {@link #integerOnly()} 的区别：本方法多一层范围检查。</p>
     *
     * @param min 最小值（含）
     * @param max 最大值（含）
     * @throws IllegalArgumentException 当 {@code min > max}
     */
    public static TextFormatter<Integer> integerRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    "min 必须 <= max，实际传入：min=" + min + ", max=" + max);
        }
        return new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                change -> {
                    String newText = change.getControlNewText();
                    // 过程态：允许空、允许仅有负号
                    if (newText.isEmpty() || newText.equals("-")) {
                        return change;
                    }
                    try {
                        int value = Integer.parseInt(newText);
                        return (value >= min && value <= max) ? change : null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
        );
    }

    // ============================================================
    // 业务格式限制（中频场景）
    // ============================================================

    /**
     * 中国大陆手机号（11 位数字）。
     *
     * <p>限制策略：纯数字 + 最多 11 位。
     * 不严格校验"1 开头"或"第二位 3-9"——避免用户改错号码时输入卡死。
     * 完整校验交给业务提交时执行。</p>
     *
     * <p>用例：注册、登录、找回密码、收货人信息。</p>
     */
    public static TextFormatter<String> chinaPhoneNumber() {
        // 仅允许：空 / 1 个或多个数字，且总长度 <= 11
        return new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 11) return null;
            return t.matches("\\d*") ? change : null;
        });
    }

    /**
     * 邮箱合法字符集 + 长度限制（默认 254 位，符合 RFC 5321 上限）。
     *
     * <p>限制策略：允许字母、数字、点、@、下划线、连字符、加号；禁止空格和中文。
     * 邮箱完整校验（@ 必须有且仅有一个 / 域名格式 / 后缀长度等）必须在提交时执行——
     * 输入过程做不到。</p>
     *
     * <p>用例：注册、登录、找回密码。</p>
     */
    public static TextFormatter<String> emailChars() {
        return emailChars(254);
    }

    /**
     * 邮箱合法字符集 + 自定义长度上限。
     *
     * @param maxLength 最大字符数（必须 ≥ 0）
     */
    public static TextFormatter<String> emailChars(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength 必须 >= 0，实际传入：" + maxLength);
        }
        // 邮箱合法字符：字母数字 + . @ _ - +
        Pattern p = Pattern.compile("[A-Za-z0-9.@_+\\-]*");
        return new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > maxLength) return null;
            return p.matcher(t).matches() ? change : null;
        });
    }

    /**
     * 中国大陆居民身份证号（18 位）。
     *
     * <p>限制策略：前 17 位仅数字，第 18 位可为数字或大写"X"，总长度 ≤ 18。
     * 不校验校验码合法性（需要业务侧调 GB 11643-1999 算法）。</p>
     *
     * <p>用例：实名认证、银行开户、订票。</p>
     */
    public static TextFormatter<String> chinaIdCard() {
        // 正则：最多 17 位数字 + 可选的最后一位（数字或 X）
        Pattern p = Pattern.compile("\\d{0,17}|\\d{17}[\\dXx]?");
        return new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 18) return null;
            return p.matcher(t).matches() ? change : null;
        });
    }

    /**
     * 仅允许大写字母 A-Z。
     *
     * <p>用例：验证码、车牌字段、优惠券码。</p>
     */
    public static TextFormatter<String> uppercaseLetters() {
        Pattern p = Pattern.compile("[A-Z]*");
        return new TextFormatter<>(filterByPattern(p));
    }

    /**
     * 禁止任何空白字符（空格 / Tab / 换行）。
     *
     * <p>用例：用户名、密码、邮箱、URL。常与 {@link #maxLength(int)} 配合使用。</p>
     *
     * <pre>{@code
     * // 用户名：禁空格 + 最长 20
     * username.setTextFormatter(TextFormatters.noWhitespace());
     * // 邮箱也可用，更宽松（字符集不限）
     * email.setTextFormatter(TextFormatters.noWhitespace());
     * }</pre>
     */
    public static TextFormatter<String> noWhitespace() {
        // \S* = 0 个或多个非空白字符
        Pattern p = Pattern.compile("\\S*");
        return new TextFormatter<>(filterByPattern(p));
    }

    /**
     * ISO 日期文本（YYYY-MM-DD）。
     *
     * <p>限制策略：4 位年 + 短横 + 2 位月 + 短横 + 2 位日的渐进格式。
     * 允许过程态：{@code "2"} / {@code "20"} / {@code "2026"} / {@code "2026-"} / {@code "2026-05"} 等。
     * 不校验月份 1-12 或日期合法性（2 月 30 日等），完整校验交给业务侧调
     * {@link java.time.LocalDate#parse(CharSequence)}。</p>
     *
     * <p>用例：admin 表单的纯文本日期（DatePicker 不适用的场景，如批量导入预览框）。</p>
     */
    public static TextFormatter<String> isoDate() {
        // 渐进格式：年(0-4 位)[-月(0-2 位)[-日(0-2 位)]]
        Pattern p = Pattern.compile("\\d{0,4}(-\\d{0,2}(-\\d{0,2})?)?");
        return new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 10) return null;  // YYYY-MM-DD = 10 字符
            return p.matcher(t).matches() ? change : null;
        });
    }

    /**
     * IPv4 地址（点分十进制，简化版）。
     *
     * <p>限制策略：4 段数字 + 3 个点的渐进格式，每段最多 3 位数字。
     * 允许过程态：{@code "192"} / {@code "192."} / {@code "192.168"} / {@code "192.168.1.1"} 等。
     * 不校验每段 0-255 范围（{@code "999.999.999.999"} 也能输进去）——
     * 完整校验交给业务侧调 {@link java.net.InetAddress#getByName(String)}。</p>
     *
     * <p>用例：网络配置、服务器地址录入。</p>
     */
    public static TextFormatter<String> ipv4() {
        // 渐进格式：段(0-3 位)[.段[.段[.段]]]，最多 4 段
        Pattern p = Pattern.compile("\\d{0,3}(\\.\\d{0,3}(\\.\\d{0,3}(\\.\\d{0,3})?)?)?");
        return new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 15) return null;  // 255.255.255.255 = 15 字符
            return p.matcher(t).matches() ? change : null;
        });
    }

    // ============================================================
    // 内部 helper
    // ============================================================

    /** 用正则匹配新文本——匹配则放行，不匹配则拒绝。 */
    private static UnaryOperator<Change> filterByPattern(Pattern pattern) {
        return change -> pattern.matcher(change.getControlNewText()).matches() ? change : null;
    }
}
