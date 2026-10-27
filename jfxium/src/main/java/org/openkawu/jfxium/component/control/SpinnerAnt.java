package org.openkawu.jfxium.component.control;

import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * JFXium 数字步进输入组件 - 原生 {@link Spinner} 的继承式包装（红线 #11）。
 *
 * <p><b>定位</b>：{@code SpinnerAnt} 就是 {@link Spinner Spinner&lt;Double&gt;}，
 * 提供范围 / 步长 / 精度 / 可编辑输入 / 值回调的链式 API，让业务代码不用再
 * {@code new Spinner<>(...)}（原生控件绕过主题体系与 Builder 约束）。</p>
 *
 * <h2>与其它"数字"组件的分工</h2>
 * <table border="1">
 *   <caption>三者互不替代</caption>
 *   <tr><th>组件</th><th>形态</th><th>适用场景</th></tr>
 *   <tr><td>{@code SpinnerAnt}</td><td>原生 {@link Spinner} 皮肤，本类</td>
 *       <td>需要 {@code valueProperty()} / JavaFX Spinner 语义、行进式步进</td></tr>
 *   <tr><td>{@code InputNumberAnt}</td><td>自绘 {@code HBox} + 输入框 + 上下按钮</td>
 *       <td>需要 prefix/suffix、对标 Ant Design InputNumber 的排版</td></tr>
 *   <tr><td>{@code SpinAnt} / {@code SpinnerAnt} 旧加载圈</td><td>旋转加载动画</td>
 *       <td>加载中指示，已统一到 {@code SpinAnt}</td></tr>
 * </table>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 经纬度：范围 -90~90，步长 0.01（精度自动取 2 位小数）
 * SpinnerAnt latitude = SpinnerAnt.create()
 *         .min(-90).max(90)
 *         .value(0).step(0.01)
 *         .onChange(v -> save(v))
 *         .build();                     // 默认可直接键盘输入
 *
 * // 只读（只能点箭头）
 * SpinnerAnt readOnly = SpinnerAnt.create()
 *         .min(0).max(100).value(10)
 *         .editable(false)
 *         .build();
 *
 * // 与 DoubleProperty 双向同步
 * SpinnerAnt count = SpinnerAnt.create()
 *         .min(0).max(999).value(1)
 *         .bindValue(orderCount)
 *         .build();
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>主题收口</b>：样式全部由 {@code .spinner} LESS 承担；本类不写 {@code setStyle()}</li>
 *   <li><b>键盘可达</b>：构造时 {@code setFocusTraversable(true)}，配合 LESS 删除
 *       {@code -fx-focus-traversable: false}，修复 Tab 无法进入 Spinner 的问题</li>
 *   <li><b>精度自动</b>：未显式调用 {@code precision(n)} 时按 {@code step} 的小数位推导；
 *       模型值与显示值同步取整，避免 {@code 0.1 + 0.2} 显示/模型不一致</li>
 *   <li><b>默认可键入</b>：构造时即 {@code editable(true)}（与 InputNumberAnt 对齐），
 *       {@code editable(false)} 切回只读；编辑器获焦时 {@code .spinner:focus-within}
 *       显示主色边框，可编辑状态可见</li>
 *   <li><b>输入安全</b>：可编辑模式下挂 {@link TextFormatter} 过滤非法字符；
 *       非法提交回退到当前值（JavaFX 默认 converter 会抛 {@code RuntimeException}）</li>
 *   <li><b>幂等</b>：{@code size(Size)} / {@code precision(n)} 重复调用不会重复挂 styleClass</li>
 * </ul>
 */
public class SpinnerAnt extends Spinner<Double> implements LayoutCommon<SpinnerAnt>, DisabledSupport<SpinnerAnt> {

    /** 可编辑输入的合法文本：可空、可单个负号，整数/小数任意位数（精度由过滤器二次限制）。 */
    private static final Pattern NUMERIC_INPUT = Pattern.compile("-?\\d*(\\.\\d*)?");

    /** 精度上限，避免异常 step 生成超长 DecimalFormat pattern。 */
    private static final int MAX_PRECISION = 10;

    /** 生效精度（小数位）。{@code -1} 不允许出现——构造时即由 step 推导。 */
    private int precision;

    /** 未显式调用 {@link #precision(int)} 时，{@code step()} 变更会重新推导精度。 */
    private boolean autoPrecision = true;

    /** 值取整监听器的重入护栏。 */
    private boolean roundingValue = false;

    private Consumer<Double> onChange = null;

    /** {@link TextFormatter} 只允许安装一次。 */
    private boolean filterInstalled = false;

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 默认：范围 ±∞、值 0、步长 1（精度 0）。 */
    public static SpinnerAnt create() {
        return new SpinnerAnt();
    }

    /** 与 {@code new Spinner<>(min, max, initialValue, stepSize)} 等价。 */
    public static SpinnerAnt create(double min, double max, double initialValue, double stepSize) {
        return new SpinnerAnt(min, max, initialValue, stepSize);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SpinnerAnt() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1);
    }

    public SpinnerAnt(double min, double max, double initialValue, double stepSize) {
        super(safeBound(min, true),
              safeBound(max, false),
              Double.isFinite(initialValue) ? initialValue : 0,
              TextUtils.safePositive(stepSize, 1));

        setFocusTraversable(true);
        this.precision = fractionDigitsOf(getStepSize());
        installConverter();
        installValueHook();
        installEditorHooks();
        // 开箱即可键盘输入（JavaFX Spinner 默认 editable=false，业务侧几乎都会踩）
        editable(true);
        setValueInternal(getValue() != null ? getValue() : 0);
        syncEditorText();
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置值（越界自动钳制到 [min, max]）。NaN 忽略。 */
    public SpinnerAnt value(double value) {
        if (!Double.isNaN(value)) {
            setValueInternal(value);
            syncEditorText();
        }
        return this;
    }

    /** 设置下界。NaN 忽略；若大于当前上界则同步抬高上界（保证 max ≥ min）。 */
    public SpinnerAnt min(double min) {
        SpinnerValueFactory.DoubleSpinnerValueFactory f = doubleFactory();
        if (f == null || Double.isNaN(min)) {
            return this;
        }
        if (min > f.getMax()) {
            f.setMax(min);
        }
        f.setMin(min);
        return this;
    }

    /** 设置上界。NaN 忽略；若小于当前下界则同步降低下界（保证 max ≥ min）。 */
    public SpinnerAnt max(double max) {
        SpinnerValueFactory.DoubleSpinnerValueFactory f = doubleFactory();
        if (f == null || Double.isNaN(max)) {
            return this;
        }
        if (max < f.getMin()) {
            f.setMin(max);
        }
        f.setMax(max);
        return this;
    }

    /**
     * 设置步长（必须为正有限数，非法值降级为 1）。
     *
     * <p>未显式指定精度时，精度随步长小数位自动重算（0.01 → 2 位）。</p>
     */
    public SpinnerAnt step(double step) {
        SpinnerValueFactory.DoubleSpinnerValueFactory f = doubleFactory();
        if (f == null) {
            return this;
        }
        f.setAmountToStepBy(TextUtils.safePositive(step, 1));
        if (autoPrecision) {
            applyPrecision(fractionDigitsOf(f.getAmountToStepBy()));
        }
        return this;
    }

    /**
     * 设置小数精度（显示位数 + 模型取整位数）。
     *
     * <p>显式调用后不再随 {@code step()} 自动变化；传负数表示恢复"按 step 自动"。</p>
     */
    public SpinnerAnt precision(int precision) {
        if (precision < 0) {
            this.autoPrecision = true;
            applyPrecision(fractionDigitsOf(getStepSize()));
        } else {
            this.autoPrecision = false;
            applyPrecision(precision);
        }
        return this;
    }

    /** 是否允许键盘输入（默认 true）。传 false 切回只读、只能点箭头。开启时安装数字过滤器。 */
    public SpinnerAnt editable(boolean editable) {
        setEditable(editable);
        if (editable) {
            installNumericFilter();
        }
        return this;
    }

    /** 编辑器占位文本（{@code null} → 空串）。 */
    public SpinnerAnt placeholder(String placeholder) {
        getEditor().setPromptText(TextUtils.safeText(placeholder));
        return this;
    }

    /** 值变化回调（取整后触发）。 */
    public SpinnerAnt onChange(Consumer<Double> onChange) {
        this.onChange = onChange;
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步；Property 越界时回写钳制后的值。 */
    public SpinnerAnt bindValue(DoubleProperty property) {
        if (property == null) {
            return this;
        }
        if (getValue() != null) {
            property.set(getValue());
        }
        valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                property.set(newValue.doubleValue());
            }
        });
        property.addListener((obs, oldValue, newValue) -> {
            if (newValue != null
                    && (getValue() == null || Double.compare(newValue.doubleValue(), getValue()) != 0)) {
                setValueInternal(newValue.doubleValue());
            }
        });
        return this;
    }

    /** 尺寸档位（幂等：先清旧 size 类再挂新类）。 */
    public SpinnerAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /**
     * Builder 模式终结调用——返回自身（{@code SpinnerAnt} 就是节点）。
     */
    public SpinnerAnt build() {
        return this;
    }

    // ============================================================
    // 内部实现
    // ============================================================

    private void installConverter() {
        SpinnerValueFactory<Double> factory = getValueFactory();
        if (factory != null) {
            factory.setConverter(createConverter(precision));
        }
    }

    private void installValueHook() {
        valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            double rounded = round(newValue, precision);
            if (Double.compare(rounded, newValue) != 0 && !roundingValue) {
                setValueInternal(rounded);
                return; // 重入后带着取整值再回调一次
            }
            if (onChange != null) {
                onChange.accept(newValue);
            }
        });
    }

    private void installEditorHooks() {
        // Spinner 自身在失焦时调用 commitValue()；此监听在其后把编辑器文本同步为合法格式，
        // 非法输入（converter 回退到当前值）也能被回滚。
        getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                syncEditorText();
            }
        });
        getEditor().addEventHandler(ActionEvent.ACTION, e -> syncEditorText());
    }

    private void installNumericFilter() {
        if (filterInstalled) {
            return;
        }
        filterInstalled = true;
        getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String next = change.getControlNewText();
            if (isIncompleteNumber(next)) {
                return change;
            }
            if (!NUMERIC_INPUT.matcher(next).matches()) {
                return null;
            }
            int dot = next.indexOf('.');
            if (dot >= 0 && next.length() - dot - 1 > precision) {
                return null;
            }
            return change;
        }));
    }

    private void applyPrecision(int digits) {
        this.precision = Math.max(0, Math.min(MAX_PRECISION, digits));
        installConverter();
        if (getValue() != null) {
            setValueInternal(getValue());
        }
        syncEditorText();
    }

    private void setValueInternal(double value) {
        SpinnerValueFactory<Double> factory = getValueFactory();
        if (factory == null) {
            return;
        }
        double normalized = clamp(round(clamp(value), precision));
        roundingValue = true;
        try {
            factory.setValue(normalized);
        } finally {
            roundingValue = false;
        }
    }

    private void syncEditorText() {
        SpinnerValueFactory<Double> factory = getValueFactory();
        if (factory == null || factory.getConverter() == null) {
            return;
        }
        getEditor().setText(factory.getConverter().toString(getValue()));
    }

    private double clamp(double value) {
        SpinnerValueFactory.DoubleSpinnerValueFactory factory = doubleFactory();
        if (factory == null || !Double.isFinite(value)) {
            return value;
        }
        return Math.max(factory.getMin(), Math.min(factory.getMax(), value));
    }

    private double getStepSize() {
        SpinnerValueFactory.DoubleSpinnerValueFactory factory = doubleFactory();
        return factory != null ? factory.getAmountToStepBy() : 1;
    }

    private SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory() {
        SpinnerValueFactory<Double> factory = getValueFactory();
        return factory instanceof SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory
                ? doubleFactory
                : null;
    }

    /** 允许编辑中的中间态："{@code -}"、"{@code .}"、"{@code -.}" 不应被过滤器拦掉。 */
    private static boolean isIncompleteNumber(String text) {
        return text.isEmpty() || "-".equals(text) || ".".equals(text) || "-.".equals(text);
    }

    private static double safeBound(double value, boolean lowerBound) {
        if (Double.isNaN(value)) {
            return lowerBound ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        return value;
    }

    private static int fractionDigitsOf(double step) {
        if (!Double.isFinite(step) || step == 0) {
            return 0;
        }
        int scale = BigDecimal.valueOf(Math.abs(step)).stripTrailingZeros().scale();
        return Math.max(0, Math.min(MAX_PRECISION, scale));
    }

    private static double round(double value, int digits) {
        if (!Double.isFinite(value)) {
            return value;
        }
        return BigDecimal.valueOf(value).setScale(digits, RoundingMode.HALF_UP).doubleValue();
    }

    private static String patternOf(int digits) {
        return digits <= 0 ? "0" : "0." + "0".repeat(digits);
    }

    private StringConverter<Double> createConverter(int digits) {
        DecimalFormat format = new DecimalFormat(patternOf(digits),
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        format.setGroupingUsed(false);
        return new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value == null ? "" : format.format(value);
            }

            @Override
            public Double fromString(String text) {
                if (text == null) {
                    return getValue();
                }
                String trimmed = text.trim();
                if (isIncompleteNumber(trimmed) || !NUMERIC_INPUT.matcher(trimmed).matches()) {
                    return getValue();
                }
                try {
                    return clamp(Double.parseDouble(trimmed));
                } catch (NumberFormatException ex) {
                    return getValue();
                }
            }
        };
    }
}
