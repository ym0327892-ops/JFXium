package org.openkawu.jfxium.component.composite;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.PopupPositioner;
import org.openkawu.jfxium.core.util.Spacers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * JFXium 日期时间选择器 - 对标 Ant Design DatePicker {@code showTime}。
 *
 * <p><b>定位</b>：只读输入框 + 弹出面板（左侧日期面板 + 右侧时间面板并排），
 * 用于「预约时间」「任务截止」「数据导出范围」等需要日期 + 时刻的业务场景。</p>
 *
 * <h2>面板结构</h2>
 * <pre>
 *   ┌────────────────────────────────────────────────┐
 *   │  [ 日历月份视图 ]  │  时    分    秒           │   ← body：日期区 │ 时间区（中缝一道细分隔线）
 *   │                    │  0 0  0 0  0 0            │      每段「十位 + 个位」两列，各恒 10 行、不需滚动
 *   │                    │  ⋮    ⋮    ⋮              │
 *   ├────────────────────────────────────────────────┤
 *   │                              [此刻][确定]      │   ← 共用页脚
 * </pre>
 * <p>日期面板内嵌 {@link CalendarAnt}（压平其自身边框，避免与外层弹层双框），
 * 时间面板复用 {@link TimePickerAnt} 的 {@code TimePanel}，因此与
 * {@code TimePickerAnt} 的面板视觉/交互完全一致。</p>
 *
 * <h2>format 语义</h2>
 * <p>模式串必须同时包含<b>日期字段</b>（{@code y}/{@code M}/{@code d}）与
 * <b>时间字段</b>（{@code H}/{@code m}/{@code s}），否则 {@code build()} 抛出
 * {@link IllegalArgumentException}（只有时间请用 {@link TimePickerAnt}，
 * 只有日期请用 {@code DatePickerAnt}）。模式串中 {@code h} 会被规范化为 {@code H}，
 * 需要上午/下午时带上 {@code a}。</p>
 * <ul>
 *   <li>{@code "yyyy-MM-dd HH:mm:ss"} → 日期 + 时:分:秒（默认）</li>
 *   <li>{@code "yyyy-MM-dd HH:mm"} → 日期 + 时:分</li>
 *   <li>{@code "yyyy-MM-dd HH:mm a"} → 日期 + 24 小时列 + 上午/下午列</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node picker = DateTimePickerAnt.create()
 *     .value(LocalDateTime.of(2026, 10, 11, 9, 30))
 *     .format("yyyy-MM-dd HH:mm")
 *     .onChange(dt -> System.out.println("选中：" + dt))
 *     .build();
 * }</pre>
 */
public class DateTimePickerAnt {

    /** Controller 在触发器容器 {@code getProperties()} 中的键。 */
    private static final String CONTROLLER_KEY = DateTimePickerAnt.class.getName() + ".controller";

    /** 默认格式：日期 + 时:分:秒。 */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateTimePickerAnt() {
        // 工厂类，禁止实例化
    }

    /** 工厂入口。 */
    public static Builder create() {
        return new Builder();
    }

    /**
     * 取回 {@link Builder#build()} 产物的控制器，用于构建后改值 / 打开面板。
     *
     * @param node {@code build()} 返回的容器节点
     * @return 控制器；节点不是本组件的产物时返回 {@code null}
     */
    public static Controller controllerOf(Node node) {
        if (node == null) {
            return null;
        }
        Object ctrl = node.getProperties().get(CONTROLLER_KEY);
        return ctrl instanceof Controller controller ? controller : null;
    }

    /** 模式串规范化：{@code null}/空白串 → {@link #DEFAULT_FORMAT}；{@code h} → {@code H}。 */
    static String normalizePattern(String pattern) {
        return (pattern == null || pattern.isBlank()) ? DEFAULT_FORMAT : pattern.replace('h', 'H');
    }

    /**
     * 校验模式串含日期字段。
     *
     * @throws IllegalArgumentException 模式串只有时间部分
     */
    static void requireDateField(String pattern) {
        boolean date = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == 'y' || c == 'M' || c == 'd' || c == 'u') {
                date = true;
                break;
            }
        }
        if (!date) {
            throw new IllegalArgumentException(
                    "DateTimePickerAnt.format(\"" + pattern + "\") 不含日期字段（y/M/d），"
                            + "只有时间请改用 TimePickerAnt");
        }
    }

    // ============================================================
    // Builder
    // ============================================================

    public static class Builder extends AbstractStyleBuilder<Builder> {

        private LocalDateTime value = null;
        private String format = DEFAULT_FORMAT;
        private boolean allowClear = true;
        private Size size = Size.DEFAULT;
        private Consumer<LocalDateTime> onChange = null;
        private ObjectProperty<LocalDateTime> bindProperty = null;
        private boolean editable = true;

        /** 初始日期时间；{@code null} 表示未选中（显示 placeholder）。 */
        public Builder value(LocalDateTime value) {
            this.value = value;
            return this;
        }

        /**
         * 触发器是否支持<b>键盘直接录入日期与时间</b>（默认 {@code true}）。
         *
         * <p>开启后整串字段都可敲数字：年月日时分秒共用同一套「光标定位 → 数字写入」语义，
         * 因此点在哪一段上敲，改的就是那一段（面板仍可用于点选）。
         * 模式串若无法建立数字段映射（如含 {@code z} 时区名），键盘录入自动不启用，
         * 退化为纯面板点选 —— 不会出现半截可编辑的输入框。</p>
         */
        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        /** 段位格式串，见类注释「format 语义」。 */
        public Builder format(String format) {
            this.format = format;
            return this;
        }

        /** 是否显示清除按钮（默认 {@code true}）。 */
        public Builder allowClear(boolean allowClear) {
            this.allowClear = allowClear;
            return this;
        }

        /** 尺寸档位（作用于触发器输入框）。 */
        public Builder size(Size size) {
            this.size = ApplySizeUtil.normalize(size);
            return this;
        }

        /** 变化回调（面板「确定」提交时触发；清除时回调 {@code null}）。 */
        public Builder onChange(Consumer<LocalDateTime> onChange) {
            this.onChange = onChange;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值（提交时回写，外部变更时同步显示）。 */
        public Builder bindValue(ObjectProperty<LocalDateTime> property) {
            this.bindProperty = property;
            return this;
        }

        public StackPane build() {
            String pattern = normalizePattern(format);
            // 语法校验（非法模式串在此抛出）
            DateTimeFormatter formatter = TimePickerAnt.compileFormatter(pattern);
            // 时间字段校验 + 段位可见性（与 TimePickerAnt 共用同一套语义）
            TimePickerAnt.Visibility vis = TimePickerAnt.parseVisibility(pattern);
            requireDateField(pattern);

            // 绑定了 property 时以 property 为准（与 TimePickerAnt 一致）；否则用显式 value
            LocalDateTime initial = bindProperty != null ? bindProperty.get() : value;

            String effectivePlaceholder = hasPlaceholder()
                    ? placeholder
                    : Messages.get("datetimepicker.placeholder");
            TimePickerAnt.Trigger trigger = new TimePickerAnt.Trigger(
                    JfxStyles.DATE_TIME_PICKER, JfxStyles.TIME_PICKER_FIELD,
                    effectivePlaceholder, size, allowClear);
            if (!hasPlaceholder()) {
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        trigger.field.setPromptText(Messages.get("datetimepicker.placeholder")));
            }
            // 输入框宽度按「格式化后的样本长度」给足，避免空值时塌成一根窄条
            trigger.field.setPrefColumnCount(
                    Math.max(8, formatter.format(LocalDateTime.of(2026, 1, 1, 22, 33, 44)).length() + 1));

            // ---- 左侧：日期面板（内嵌 CalendarAnt） ----
            LocalDate initialDate = initial != null ? initial.toLocalDate() : LocalDate.now();
            final LocalDate[] pickedDate = {initialDate};
            VBox datePanel = new VBox();
            datePanel.getStyleClass().add(JfxStyles.DATE_TIME_PICKER_DATE_PANEL);
            CalendarAnt.Builder calendarBuilder = CalendarAnt.create()
                    .value(initialDate)
                    .onSelect(d -> pickedDate[0] = d);
            if (initial != null) {
                calendarBuilder.selectedDate(initialDate);
            }
            VBox calendar = calendarBuilder.build();
            datePanel.getChildren().add(calendar);

            // ---- 右侧：时间面板（复用 TimePickerAnt 的面板） ----
            TimePickerAnt.TimePanel timePanel = new TimePickerAnt.TimePanel(vis, false, true);
            timePanel.setValue(initial != null ? initial.toLocalTime() : null);

            HBox body = new HBox(datePanel, timePanel);
            body.setAlignment(Pos.TOP_LEFT);
            body.getStyleClass().add(JfxStyles.DATE_TIME_PICKER_BODY);

            // ---- 共用页脚：此刻 / 确定 ----
            ButtonAnt nowBtn = ButtonAnt.create(Messages.get("timepicker.now"))
                    .type(ButtonAnt.Type.TEXT)
                    .size(Size.SMALL);
            nowBtn.getStyleClass().add(JfxStyles.TIME_PANEL_NOW);
            ButtonAnt okBtn = ButtonAnt.create(Messages.get("timepicker.ok"))
                    .type(ButtonAnt.Type.PRIMARY)
                    .size(Size.SMALL);
            HBox footer = new HBox(Spacers.grow(), nowBtn, okBtn);
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.getStyleClass().add(JfxStyles.TIME_PANEL_FOOTER);

            VBox shell = new VBox(body, footer);
            // 外壳独占弹层框架（圆角/边框/阴影/内边距）；内层时间面板以 embedded 模式并入，杜绝双框
            shell.getStyleClass().add(JfxStyles.TIME_PANEL);
            shell.getStyleClass().add(JfxStyles.DATE_TIME_PICKER_SHELL);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.getContent().add(shell);
            PopupPositioner.inheritStylesheets(popup, trigger.root);

            final LocalDateTime[] committed = {initial};

            final TimeTextMask[] mask = {null};

            Runnable syncTriggerText = () -> {
                if (mask[0] != null) {
                    mask[0].display(committed[0]);
                } else {
                    trigger.field.setText(committed[0] == null ? "" : formatter.format(committed[0]));
                }
            };

            // 把面板（日期 + 时间）整体拨到某个时刻，不提交
            Consumer<LocalDateTime> setPanelValue = dt -> {
                LocalDateTime target = dt != null ? dt : LocalDateTime.now();
                pickedDate[0] = target.toLocalDate();
                CalendarAnt.controllerOf(calendar).setSelectedDate(pickedDate[0]);
                CalendarAnt.controllerOf(calendar).setValue(pickedDate[0]);
                timePanel.setValue(target.toLocalTime());
            };

            nowBtn.setOnAction(e -> setPanelValue.accept(LocalDateTime.now()));


            // 提交一个明确的时刻（面板拼出来的、或字段里未提交的编辑）
            Consumer<LocalDateTime> commitValue = target -> {
                committed[0] = target;
                pickedDate[0] = target.toLocalDate();
                syncTriggerText.run();
                popup.hide();
                if (bindProperty != null) {
                    bindProperty.set(committed[0]);
                }
                if (onChange != null) {
                    onChange.accept(committed[0]);
                }
            };
            // 「确定」优先采纳字段里尚未提交的编辑（用户敲了数字既没按 Enter 也没失焦，
            // 直接点确定）；没有这样的编辑时才用面板拼出的值。
            // 掩码会把「点确定」当成一次显式提交，因此不会出现「我明明输了 23:45，点确定跳回 07:15」。
            okBtn.setOnAction(e -> {
                LocalDateTime pending = mask[0] != null ? mask[0].pendingEdit() : null;
                commitValue.accept(pending != null
                        ? pending
                        : LocalDateTime.of(pickedDate[0], timePanel.getValue()));
            });


            // 键盘录入：整串接管（年月日时分秒同属一个掩码），
            // 因此「点到月份上敲 11」改的就是月份 —— 不再是只有时间后缀能改、
            // 日期前缀只读（点前缀里任何位置都会折算成小时段的第 0 位）
            if (editable && TimeTextMask.supports(pattern)) {
                mask[0] = new TimeTextMask(trigger.field, pattern,
                        () -> committed[0],
                        () -> committed[0] != null && committed[0].getHour() >= 12 ? 1 : 0,
                        // 掩码的 preCommit 契约是 Consumer<LocalDateTime>；真正的回写放在 postCommit
                        dt -> { },
                        newValue -> {
                            committed[0] = newValue;
                            syncTriggerText.run();
                            popup.hide();
                            if (bindProperty != null) {
                                bindProperty.set(committed[0]);
                            }
                            if (onChange != null) {
                                onChange.accept(committed[0]);
                            }
                        });
                // 键盘录入实时联动右侧时间面板与左侧日历
                mask[0].setOnPreview(dt -> {
                    if (dt != null && popup.isShowing()) {
                        setPanelValue.accept(dt);
                    }
                });
            } else {
                trigger.field.setEditable(false);
                popup.setHideOnEscape(true);
            }
            syncTriggerText.run();

            // Esc 收面板 + 丢弃未提交的编辑（必须放在掩码创建之后，否则 mask[0] 还是 null，
            // 这段守卫会整段跳过、弹层自带的 hideOnEscape 继续在捕获阶段抢走 Esc）
            if (mask[0] != null) {
                popup.setHideOnEscape(false);
                mask[0].setOnEscape(() -> {
                    if (popup.isShowing()) {
                        popup.hide();
                    }
                });
                shell.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        e.consume();
                        mask[0].cancel();
                        popup.hide();
                    }
                });
            }

            trigger.onToggle(() -> {
                if (Boolean.TRUE.equals(disable)) {
                    return;
                }
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    if (committed[0] != null) {
                        setPanelValue.accept(committed[0]);
                    }
                    PopupPositioner.showBelow(popup, trigger.root);
                }
            });

            trigger.onClear(() -> {
                committed[0] = null;
                syncTriggerText.run();
                if (bindProperty != null) {
                    bindProperty.set(null);
                }
                if (onChange != null) {
                    onChange.accept(null);
                }
            });

            trigger.bindAffordances(Bindings.createBooleanBinding(
                    () -> committed[0] != null, trigger.field.textProperty()));

            if (bindProperty != null) {
                bindProperty.addListener((obs, ov, nv) -> {
                    committed[0] = nv;
                    syncTriggerText.run();
                });
            }

            if (Boolean.TRUE.equals(disable)) {
                trigger.field.setDisable(true);
            }

            applyStyles(trigger.root);

            Controller controller = new Controller(committed, timePanel, popup, trigger.root,
                    syncTriggerText, setPanelValue);
            trigger.root.getProperties().put(CONTROLLER_KEY, controller);
            return trigger.root;
        }
    }

    // ============================================================
    // Controller
    // ============================================================

    /**
     * 运行时控制器：构建后改值 / 打开面板 / 读取当前值。
     *
     * <p>取回方式：{@code DateTimePickerAnt.controllerOf(node)}。</p>
     */
    public static final class Controller {

        private final LocalDateTime[] committed;
        private final TimePickerAnt.TimePanel timePanel;
        private final Popup popup;
        private final Node anchor;
        private final Runnable syncTriggerText;
        private final Consumer<LocalDateTime> setPanelValue;

        Controller(LocalDateTime[] committed, TimePickerAnt.TimePanel timePanel, Popup popup,
                   Node anchor, Runnable syncTriggerText, Consumer<LocalDateTime> setPanelValue) {
            this.committed = committed;
            this.timePanel = timePanel;
            this.popup = popup;
            this.anchor = anchor;
            this.syncTriggerText = syncTriggerText;
            this.setPanelValue = setPanelValue;
        }

        /** 当前已提交的值；未选中时为 {@code null}。 */
        public LocalDateTime getValue() {
            return committed[0];
        }

        /** 设置已提交的值并刷新触发器显示（不触发 {@code onChange}）。 */
        public void setValue(LocalDateTime value) {
            committed[0] = value;
            if (value != null) {
                setPanelValue.accept(value);
            }
            syncTriggerText.run();
        }

        /** 面板是否正在显示。 */
        public boolean isPanelShowing() {
            return popup.isShowing();
        }

        /** 展开面板（把面板拨到当前已提交值）。 */
        public void showPanel() {
            if (popup.isShowing()) {
                return;
            }
            if (committed[0] != null) {
                setPanelValue.accept(committed[0]);
            }
            PopupPositioner.showBelow(popup, anchor);
        }

        /** 收起面板（若在显示中）。 */
        public void hidePanel() {
            popup.hide();
        }

        /** 时间面板节点（供测试与二次封装使用）。 */
        public Node getTimePanelNode() {
            return timePanel;
        }
    }
}
