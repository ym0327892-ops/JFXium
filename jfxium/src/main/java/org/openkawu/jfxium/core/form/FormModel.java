package org.openkawu.jfxium.core.form;

import javafx.beans.property.*;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 表单数据模型（对标 Flutter FormState / WPF ViewModel）。
 *
 * <p>把散落的 Property 收拢成一个容器，支持批量取值、回填、重置、监听。
 * 配合 {@code InputAnt.create().bindValue(model.stringField("username")).build()} 使用。</p>
 *
 * <pre>{@code
 * FormModel model = FormModel.create()
 *     .field("username", "")
 *     .field("email", "")
 *     .field("age", 0)
 *     .field("rememberMe", false);
 *
 * // 取值
 * String name = model.getString("username");
 * Map<String, Object> all = model.getAll();
 *
 * // 回填（编辑页）
 * model.setAll(Map.of("username", "张三", "email", "test@test.com"));
 *
 * // 重置
 * model.reset();
 *
 * // 监听单字段
 * model.onChange("username", newVal -> System.out.println("changed: " + newVal));
 * }</pre>
 */
public class FormModel {

    private final Map<String, Property<?>> fields = new LinkedHashMap<>();
    private final Map<String, Object> defaults = new LinkedHashMap<>();

    private FormModel() {}

    public static FormModel create() {
        return new FormModel();
    }

    // ============================================================
    // 字段声明
    // ============================================================

    /** 声明一个 String 字段。 */
    public FormModel field(String name, String defaultValue) {
        String safeDefault = TextUtils.safeText(defaultValue);
        StringProperty prop = new SimpleStringProperty(safeDefault);
        fields.put(name, prop);
        defaults.put(name, safeDefault);
        return this;
    }

    /** 声明一个 int 字段。 */
    public FormModel field(String name, int defaultValue) {
        IntegerProperty prop = new SimpleIntegerProperty(defaultValue);
        fields.put(name, prop);
        defaults.put(name, defaultValue);
        return this;
    }

    /** 声明一个 double 字段。 */
    public FormModel field(String name, double defaultValue) {
        DoubleProperty prop = new SimpleDoubleProperty(defaultValue);
        fields.put(name, prop);
        defaults.put(name, defaultValue);
        return this;
    }

    /** 声明一个 boolean 字段。 */
    public FormModel field(String name, boolean defaultValue) {
        BooleanProperty prop = new SimpleBooleanProperty(defaultValue);
        fields.put(name, prop);
        defaults.put(name, defaultValue);
        return this;
    }

    // ============================================================
    // Property 访问器（配合 bindValue 使用）
    // ============================================================

    /** 获取 String 字段的 Property（用于 InputAnt.bindValue）。 */
    public StringProperty stringField(String name) {
        Property<?> p = fields.get(name);
        if (p instanceof StringProperty sp) return sp;
        throw new IllegalArgumentException("字段 '" + name + "' 不是 StringProperty");
    }

    /** 获取 Boolean 字段的 Property（用于 CheckBoxAnt.bindValue）。 */
    public BooleanProperty booleanField(String name) {
        Property<?> p = fields.get(name);
        if (p instanceof BooleanProperty bp) return bp;
        throw new IllegalArgumentException("字段 '" + name + "' 不是 BooleanProperty");
    }

    /** 获取 Integer 字段的 Property。 */
    public IntegerProperty intField(String name) {
        Property<?> p = fields.get(name);
        if (p instanceof IntegerProperty ip) return ip;
        throw new IllegalArgumentException("字段 '" + name + "' 不是 IntegerProperty");
    }

    /** 获取 Double 字段的 Property（用于 SliderAnt.bindValue / InputNumberAnt.bindValue）。 */
    public DoubleProperty doubleField(String name) {
        Property<?> p = fields.get(name);
        if (p instanceof DoubleProperty dp) return dp;
        throw new IllegalArgumentException("字段 '" + name + "' 不是 DoubleProperty");
    }

    /** 获取任意类型的 Property（泛型访问）。 */
    @SuppressWarnings("unchecked")
    public <T> Property<T> field(String name) {
        return (Property<T>) fields.get(name);
    }

    // ============================================================
    // 取值
    // ============================================================

    /** 取 String 字段当前值。 */
    public String getString(String name) {
        return stringField(name).get();
    }

    /** 取 boolean 字段当前值。 */
    public boolean getBoolean(String name) {
        return booleanField(name).get();
    }

    /** 取 int 字段当前值。 */
    public int getInt(String name) {
        return intField(name).get();
    }

    /** 取 double 字段当前值。 */
    public double getDouble(String name) {
        return doubleField(name).get();
    }

    /** 批量取所有字段当前值。 */
    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Property<?>> entry : fields.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getValue());
        }
        return result;
    }

    // ============================================================
    // 设值
    // ============================================================

    /** 批量设值（编辑页回填）。只设 map 里包含的字段，其余不动。 */
    @SuppressWarnings("unchecked")
    public void setAll(Map<String, Object> values) {
        if (values == null) return;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Property<?> prop = fields.get(entry.getKey());
            if (prop == null) continue;
            Object val = entry.getValue();
            if (prop instanceof StringProperty sp && val instanceof String s) {
                sp.set(s);
            } else if (prop instanceof BooleanProperty bp && val instanceof Boolean b) {
                bp.set(b);
            } else if (prop instanceof IntegerProperty ip && val instanceof Number n) {
                ip.set(n.intValue());
            } else if (prop instanceof DoubleProperty dp && val instanceof Number n) {
                dp.set(n.doubleValue());
            }
        }
    }

    // ============================================================
    // 重置
    // ============================================================

    /** 重置所有字段到声明时的默认值。 */
    @SuppressWarnings("unchecked")
    public void reset() {
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            Property<?> prop = fields.get(entry.getKey());
            Object def = entry.getValue();
            if (prop instanceof StringProperty sp && def instanceof String s) {
                sp.set(s);
            } else if (prop instanceof BooleanProperty bp && def instanceof Boolean b) {
                bp.set(b);
            } else if (prop instanceof IntegerProperty ip && def instanceof Number n) {
                ip.set(n.intValue());
            } else if (prop instanceof DoubleProperty dp && def instanceof Number n) {
                dp.set(n.doubleValue());
            }
        }
    }

    // ============================================================
    // 监听
    // ============================================================

    /** 监听单字段变化。 */
    @SuppressWarnings("unchecked")
    public void onChange(String name, Consumer<Object> listener) {
        Property<?> prop = fields.get(name);
        if (prop == null) throw new IllegalArgumentException("字段 '" + name + "' 不存在");
        prop.addListener((obs, oldVal, newVal) -> listener.accept(newVal));
    }

    // ============================================================
    // 工具
    // ============================================================

    /** 是否包含指定字段。 */
    public boolean has(String name) {
        return fields.containsKey(name);
    }

    /** 字段数量。 */
    public int size() {
        return fields.size();
    }
}
