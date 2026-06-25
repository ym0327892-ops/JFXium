package org.openkawu.jfxium.core.form;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * 表单校验规则（M19.23）。
 *
 * <p>常见规则提供静态工厂方法；自定义规则用 {@link #custom(Predicate, String)}。</p>
 *
 * <h2>使用</h2>
 * <pre>{@code
 * FormAnt.create()
 *     .item("用户名", usernameField, "username")
 *         .rule(Rule.required("用户名不能为空"))
 *         .rule(Rule.minLength(3, "至少 3 个字符"))
 *         .rule(Rule.pattern("^[a-zA-Z0-9_]+$", "仅支持字母、数字、下划线"))
 *     .end()
 *     .build();
 * }</pre>
 */
public final class Rule {

    private final Predicate<Object> validator;
    private final String message;

    private Rule(Predicate<Object> validator, String message) {
        this.validator = Objects.requireNonNull(validator, "validator");
        this.message = TextUtils.safeText(message);
    }

    /**
     * 校验单个值是否通过；通过返回 null，未通过返回错误消息。
     */
    public String check(Object value) {
        return validator.test(value) ? null : message;
    }

    public String getMessage() {
        return message;
    }

    // ============================================================
    // 静态工厂方法（常见规则）
    // ============================================================

    /** 必填：非 null 且字符串形式不为空白。 */
    public static Rule required(String message) {
        return new Rule(v -> v != null && !v.toString().isBlank(), message);
    }

    /** 必填，使用默认消息。 */
    public static Rule required() {
        return required("此项必填");
    }

    /** 最小长度（基于字符串长度）。 */
    public static Rule minLength(int min, String message) {
        return new Rule(v -> v != null && v.toString().length() >= min, message);
    }

    /** 最大长度。 */
    public static Rule maxLength(int max, String message) {
        return new Rule(v -> v == null || v.toString().length() <= max, message);
    }

    /** 长度区间 [min, max]。 */
    public static Rule lengthBetween(int min, int max, String message) {
        return new Rule(v -> {
            if (v == null) return false;
            int len = v.toString().length();
            return len >= min && len <= max;
        }, message);
    }

    /** 正则匹配（v.toString() 必须 fully match）。 */
    public static Rule pattern(String regex, String message) {
        Pattern p = Pattern.compile(regex);
        return new Rule(v -> v != null && p.matcher(v.toString()).matches(), message);
    }

    /** 邮箱（简单 RFC 5322 子集）。 */
    public static Rule email(String message) {
        return pattern("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$", message);
    }

    /** 邮箱，使用默认消息。 */
    public static Rule email() {
        return email("请输入有效的邮箱地址");
    }

    /**
     * 数值范围 [min, max]（包含两端）。空值不通过；非数值不通过。
     */
    public static Rule range(double min, double max, String message) {
        return new Rule(v -> {
            if (v == null) return false;
            try {
                double d = Double.parseDouble(v.toString());
                return d >= min && d <= max;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, message);
    }

    /**
     * 自定义 Predicate 校验。validator 返回 true 表示通过。
     */
    public static Rule custom(Predicate<Object> validator, String message) {
        return new Rule(validator, message);
    }
}
