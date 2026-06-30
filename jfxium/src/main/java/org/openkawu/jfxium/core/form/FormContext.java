package org.openkawu.jfxium.core.form;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 表单上下文（M19.23）。
 *
 * <p>持有所有 named items 的当前值 / 校验错误 / 字段联动监听。
 * 每个 FormAnt.Builder 内部创建一个 FormContext，build 时通过 {@link FormResult} 暴露给外部。</p>
 *
 * <h2>核心能力</h2>
 * <ul>
 *   <li>统一从各种 JavaFX 控件中提取 / 写入"当前值"（TextField / ComboBox / CheckBox / DatePicker 等）</li>
 *   <li>每个字段一个 {@code valueProperty}，外部可监听做联动</li>
 *   <li>每个字段一个 {@code errorProperty}，UI 显示校验错误</li>
 *   <li>{@link #validate()} 跑所有字段所有规则，返回是否全部通过</li>
 * </ul>
 */
public class FormContext {

    private final ObservableMap<String, ObjectProperty<Object>> values =
            FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableMap<String, StringProperty> errors =
            FXCollections.observableMap(new LinkedHashMap<>());
    private final Map<String, java.util.List<Rule>> rulesByName = new LinkedHashMap<>();
    private final Map<String, Node> controlsByName = new LinkedHashMap<>();
    private boolean validateOnChange = false;

    /**
     * 注册字段（FormAnt 内部 build 时调用）。
     * @param name        字段 key
     * @param control     JavaFX 控件
     * @param rules       校验规则列表
     */
    public void registerField(String name, Node control, java.util.List<Rule> rules) {
        if (name == null || name.isEmpty() || control == null) return;
        controlsByName.put(name, control);
        rulesByName.put(name, rules != null ? rules : java.util.Collections.emptyList());

        ObjectProperty<Object> valueProp = new SimpleObjectProperty<>(extractValue(control));
        StringProperty errorProp = new SimpleStringProperty("");
        values.put(name, valueProp);
        errors.put(name, errorProp);

        // 监听控件值变化，同步到 valueProp
        bindControlToProperty(control, valueProp, name);
    }

    /** 设置 onChange 自动校验（由 FormAnt.Builder 在 build 时调用）。 */
    public void setValidateTrigger(boolean onChange) {
        this.validateOnChange = onChange;
    }

    /** 读取字段当前值（按字段 name）。 */
    public Object getValue(String name) {
        ObjectProperty<Object> prop = values.get(name);
        return prop != null ? prop.get() : null;
    }

    /** 写入字段值（同步到控件）。 */
    public void setValue(String name, Object value) {
        Node control = controlsByName.get(name);
        ObjectProperty<Object> prop = values.get(name);
        if (control == null || prop == null) return;
        applyValueToControl(control, value);
        prop.set(value);
    }

    /** 暴露字段值的 Property，外部可监听做字段联动。 */
    public ObjectProperty<Object> valueProperty(String name) {
        return values.get(name);
    }

    /** 暴露字段错误的 StringProperty。空字符串表示无错误。 */
    public StringProperty errorProperty(String name) {
        return errors.get(name);
    }

    /** 取所有字段值（key→value 快照）。 */
    public Map<String, Object> getValues() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        values.forEach((k, p) -> snapshot.put(k, p.get()));
        return snapshot;
    }

    /**
     * 跑全部字段全部规则。失败的字段写错误消息到 errorProperty。
     * @return true 表示全部通过
     */
    public boolean validate() {
        boolean allPassed = true;
        for (Map.Entry<String, java.util.List<Rule>> entry : rulesByName.entrySet()) {
            String name = entry.getKey();
            Object value = getValue(name);
            String error = "";
            for (Rule rule : entry.getValue()) {
                String err = rule.check(value);
                if (err != null) {
                    error = err;
                    allPassed = false;
                    break;   // 一个字段只显示第一条失败的消息
                }
            }
            errors.get(name).set(error);
        }
        return allPassed;
    }

    /** 校验单个字段。 */
    public boolean validateField(String name) {
        Object value = getValue(name);
        java.util.List<Rule> rules = rulesByName.getOrDefault(name, java.util.Collections.emptyList());
        StringProperty errProp = errors.get(name);
        if (errProp == null) return true;

        for (Rule rule : rules) {
            String err = rule.check(value);
            if (err != null) {
                errProp.set(err);
                return false;
            }
        }
        errProp.set("");
        return true;
    }

    /** 重置所有字段错误信息（不改值）。 */
    public void clearErrors() {
        errors.values().forEach(p -> p.set(""));
    }

    /** 重置所有字段值为 null/空 + 清除所有错误。 */
    public void reset() {
        controlsByName.forEach((name, control) -> applyValueToControl(control, null));
        values.values().forEach(p -> p.set(null));
        clearErrors();
    }

    /** 重置单个字段值为 null/空 + 清除其错误。 */
    public void resetField(String name) {
        Node control = controlsByName.get(name);
        ObjectProperty<Object> prop = values.get(name);
        StringProperty errProp = errors.get(name);
        if (control != null) applyValueToControl(control, null);
        if (prop != null) prop.set(null);
        if (errProp != null) errProp.set("");
    }

    /**
     * 字段联动：当 dependency 字段值变化时调用 handler。
     * handler 收到 (新值, FormContext)，可在内部用 {@link #setValue} / {@link #validateField} 等。
     */
    public void onChange(String dependency, BiConsumer<Object, FormContext> handler) {
        ObjectProperty<Object> prop = values.get(dependency);
        if (prop == null || handler == null) return;
        prop.addListener((obs, oldVal, newVal) -> handler.accept(newVal, this));
    }

    // ============================================================
    // 控件值的提取 / 写入：覆盖最常用的几种 JavaFX 控件
    // ============================================================

    private static Object extractValue(Node control) {
        if (control instanceof TextInputControl tic) return tic.getText();
        if (control instanceof CheckBox cb)          return cb.isSelected();
        if (control instanceof RadioButton rb)       return rb.isSelected();
        if (control instanceof ToggleButton tb)      return tb.isSelected();
        if (control instanceof ComboBox<?> cb)       return cb.getValue();
        if (control instanceof DatePicker dp)        return dp.getValue();
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applyValueToControl(Node control, Object value) {
        if (control instanceof TextInputControl tic) {
            tic.setText(value == null ? "" : value.toString());
        } else if (control instanceof CheckBox cb) {
            cb.setSelected(Boolean.TRUE.equals(value));
        } else if (control instanceof RadioButton rb) {
            rb.setSelected(Boolean.TRUE.equals(value));
        } else if (control instanceof ToggleButton tb) {
            tb.setSelected(Boolean.TRUE.equals(value));
        } else if (control instanceof ComboBox cb) {
            cb.setValue(value);
        } else if (control instanceof DatePicker dp) {
            dp.setValue(value instanceof LocalDate ld ? ld : null);
        }
    }

    private void bindControlToProperty(Node control, ObjectProperty<Object> prop, String name) {
        if (control instanceof TextInputControl tic) {
            tic.textProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        } else if (control instanceof CheckBox cb) {
            cb.selectedProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        } else if (control instanceof RadioButton rb) {
            rb.selectedProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        } else if (control instanceof ToggleButton tb) {
            tb.selectedProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        } else if (control instanceof ComboBox<?> cb) {
            cb.valueProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        } else if (control instanceof DatePicker dp) {
            dp.valueProperty().addListener((obs, ov, nv) -> { prop.set(nv); onChangeValidate(name); });
        }
        // 其他控件类型不绑定（业务可手动 setValue 同步）
    }

    private void onChangeValidate(String name) {
        if (validateOnChange && name != null) {
            validateField(name);
        }
    }
}
