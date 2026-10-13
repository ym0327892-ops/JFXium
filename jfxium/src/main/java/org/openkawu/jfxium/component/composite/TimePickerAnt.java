package org.openkawu.jfxium.component.composite;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.control.ListViewAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.PopupPositioner;
import org.openkawu.jfxium.core.util.Spacers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 时间选择器 - 对标 Ant Design TimePicker。
 *
 * <p><b>定位</b>：只读输入框 + 弹出时间面板，用于日程安排、提醒设置、营业时间配置等场景。</p>
 *
 * <h2>交互契约（M21.29 重构）</h2>
 * <ul>
 *   <li><b>弹出面板</b>：点击输入框弹出面板，列内点选即可，不再依赖原生 {@code Spinner}
 *       的上下箭头反复步进（旧方案选 30 分要连点 30 次，且三个盒子各占 72px、
 *       秒段在 {@code HH:mm} 下会被静默丢弃）</li>
 *   <li><b>两级数字列</b>：时 / 分 / 秒每段拆成「十位 + 个位」两列，每列<b>恒 10 行</b>（0-9），
 *       点一次即定值、全程无滚动条（M21.32 取代了首版的三列长列表：选 47 分不必在 60 行里滚动）。
 *       超出该位合法范围的行渲染为空行、点之无效，因此「第 n 行就是数字 n」</li>
 *   <li><b>此刻 / 确定</b>：页脚「此刻」把面板拨到当前时间；「确定」提交并收起面板
 *       （提交时才触发 {@link Builder#onChange} 与回写绑定）</li>
 *   <li><b>可清除</b>：{@link Builder#allowClear(boolean)} 默认开启，有值时悬停出现
 *       清除按钮，点它回到未选中（placeholder 空文本）并回调 {@code null}；
 *       此后重新打开面板，显示的是**当前时刻**这一未提交默认值，而不是 {@code 00:00}
 *       —— 后者会被误读成「清除把时间变成了 0 点」（BUG #168）</li>
 *   <li><b>宽度恒定</b>：清除按钮与下拉箭头都是叠加层（{@code USE_PREF_SIZE} 钳制），
 *       不参与父布局；「空 ↔ 有值 / 悬停 ↔ 离开」切换时组件宽度不抖（沿用 BUG #152 结论）</li>
 * </ul>
 *
 * <h2>format 语义</h2>
 * <p>{@link Builder#format(String)} 接收「段位选择器」：按 {@code H/h}（时）、
 * {@code m}（分）、{@code s}（秒）、{@code a}（上午/下午）在模式串中的<b>出现与否</b>
 * 决定显示哪几段，值的渲染交给 {@link DateTimeFormatter}。因此：</p>
 * <ul>
 *   <li>{@code "HH:mm:ss"} → 时:分:秒（默认）</li>
 *   <li>{@code "HH:mm"} → 时:分（无秒列）</li>
 *   <li>{@code "H:m:s"} → 时:分:秒（单双字母等价，不会像旧实现那样静默吞段）</li>
 *   <li>{@code "HH:mm a"} → 24 小时列 + 上午/下午列</li>
 * </ul>
 *
 * <p><b>12 小时制</b>：需要上午/下午选择时在格式串里带上 {@code a}（如 {@code "HH:mm a"}），
 * 此时「时」列自动变为 1-12，值仍存 24 小时制。JDK 的 12 小时制符号 {@code h} 会被规范化成
 * {@code H}，避免 {@code "hh:mm"} 在 14:05 被 JDK 渲染成 {@code "02:05"} 的语义坑。</p>
 *
 * <p><b>非法格式</b>：不含任何时间字段的模式串（如 {@code "yyyy"}）在 {@code build()}
 * 抛出 {@link IllegalArgumentException}，而不是静默降级成残缺界面。</p>
 *
 * <p><b>未显示的段</b>：被 format 隐藏的段在提交时按 {@code 0} 计入
 * （如 {@code "HH:mm"} 提交 {@code 09:30:00}），这是 format 控制精度的预期语义。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node start = TimePickerAnt.create()
 *     .value(LocalTime.of(9, 30))
 *     .format("HH:mm")
 *     .placeholder("开始时间")
 *     .onChange(t -> System.out.println("选中：" + t))
 *     .build();
 * }</pre>
 *
 * <h2>构建后改值</h2>
 * <pre>{@code
 * Node picker = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
 * TimePickerAnt.controllerOf(picker).setValue(LocalTime.of(18, 0));
 * }</pre>
 */
public class TimePickerAnt {

    /** Controller 在触发器容器 {@code getProperties()} 中的键。 */
    private static final String CONTROLLER_KEY = TimePickerAnt.class.getName() + ".controller";

    /** 默认格式：时:分:秒。 */
    public static final String DEFAULT_FORMAT = "HH:mm:ss";

    private TimePickerAnt() {
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

    // ============================================================
    // 格式解析（包级可见：DateTimePickerAnt 复用同一套语义）
    // ============================================================

    /**
     * 把格式化模式串规范化为「JFXium 时间语义」。
     *
     * <p>本组件的面板「时」列固定 00-23（不支持 12 小时制的 1-12 列原样），因此把 JDK
     * {@link DateTimeFormatter} 的 12 小时制符号 {@code h} 统一映射为 24 小时制 {@code H}。
     * 需要上午/下午时用 {@code a} 显式声明。</p>
     */
    static String normalizePattern(String pattern) {
        return (pattern == null || pattern.isBlank()) ? DEFAULT_FORMAT : pattern.replace('h', 'H');
    }

    /**
     * 解析格式串的段位可见性。
     *
     * <p>注意：与 JDK 一致，本方法不区分模式串里的引号字面量（如 {@code "'H'"} 会被当成
     * 小时字段），这是本组件的简化约定。</p>
     *
     * @param pattern 任意格式串；{@code null}/空串 → 默认 {@link #DEFAULT_FORMAT}
     * @throws IllegalArgumentException 模式串语法非法，或不含任何时间字段
     */
    static Visibility parseVisibility(String pattern) {
        String effective = normalizePattern(pattern);
        // 先校验模式串语法本身合法（非法模式串在此抛出）
        DateTimeFormatter.ofPattern(effective, Messages.getLocale());

        boolean hour = false;
        boolean minute = false;
        boolean second = false;
        boolean meridiem = false;
        for (int i = 0; i < effective.length(); i++) {
            switch (effective.charAt(i)) {
                case 'H' -> hour = true;
                case 'm' -> minute = true;
                case 's' -> second = true;
                case 'a' -> meridiem = true;
                default -> { /* 其它字段与字面量不影响段位 */ }
            }
        }
        if (!hour && !minute && !second) {
            throw new IllegalArgumentException(
                    "TimePickerAnt.format(\"" + pattern + "\") 不含任何时间字段（H/m/s），"
                            + "请改用如 \"HH:mm:ss\" / \"HH:mm\" / \"HH\" 的格式串");
        }
        // 时是时间选择的最小不可分割单位：只写了分/秒也必须能选到时，否则值无法表达
        return new Visibility(effective, true, minute, second, meridiem);
    }

    /** 用给定模式串编译格式化器（{@link DateTimePickerAnt} 复用）。 */
    static DateTimeFormatter compileFormatter(String normalizedPattern) {
        return DateTimeFormatter.ofPattern(normalizedPattern, Messages.getLocale());
    }

    /** 格式串解析结果：规范化模式串 + 各段位可见性。 */
    static final class Visibility {
        final String pattern;
        final boolean hour;
        final boolean minute;
        final boolean second;
        final boolean meridiem;

        Visibility(String pattern, boolean hour, boolean minute, boolean second, boolean meridiem) {
            this.pattern = pattern;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.meridiem = meridiem;
        }
    }

    // ============================================================
    // 触发器骨架（DateTimePickerAnt 复用，保证两控件外观与行为一致）
    // ============================================================

    /**
     * 触发器：只读输入框 + 悬浮的清除按钮 / 下拉箭头。
     *
     * <p>清除按钮与箭头都是 {@link StackPane} 上的<b>叠加层</b>（{@code USE_PREF_SIZE}
     * 钳制尺寸），不参与父级布局宽度计算，因此「空 ↔ 有值 / 悬停 ↔ 离开」切换时组件宽度恒定
     * （沿用 BUG #152 在 SearchInputAnt 上的结论）。输入框右侧预留内边距由 LESS
     * （{@code .text-field.jfx-time-picker-field}）恒定给出，文字提前让位。</p>
     */
    static final class Trigger {

        final StackPane root = new StackPane();
        final InputAnt field = InputAnt.create();
        private final Label clearBtn = new Label();
        private final Region arrow = new Region();

        Trigger(String styleClass, String fieldStyleClass, String placeholderText, Size size,
                boolean allowClear) {
            root.setAlignment(Pos.CENTER_LEFT);
            root.getStyleClass().add(styleClass);

            field.getStyleClass().add(fieldStyleClass);
            field.setEditable(false);
            field.setFocusTraversable(true);
            field.setPromptText(placeholderText);
            // 让文本域撑满容器宽度：TextField 默认 maxWidth=pref，不撑满会导致
            // StackPane 里右端预留区被算进容器最小宽度之外
            field.setMaxWidth(Double.MAX_VALUE);
            ApplySizeUtil.apply(field, size);

            arrow.getStyleClass().add(JfxStyles.TIME_PICKER_ARROW);
            arrow.setMouseTransparent(true);
            pinToRight(arrow);

            clearBtn.getStyleClass().add(JfxStyles.TIME_PICKER_CLEAR);
            clearBtn.setGraphic(IconAnt.symbol(IconAnt.Symbol.CLOSE, 12));
            clearBtn.setFocusTraversable(true);
            clearBtn.setVisible(false);
            pinToRight(clearBtn);

            root.getChildren().add(field);
            if (allowClear) {
                root.getChildren().add(clearBtn);
            }
            root.getChildren().add(arrow);
        }

        private static void pinToRight(Node node) {
            StackPane.setAlignment(node, Pos.CENTER_RIGHT);
            StackPane.setMargin(node, new Insets(0, 6, 0, 0));
            if (node instanceof Region region) {
                // 叠加层必须钳到自身 pref 尺寸，否则会被 StackPane 拉伸吞掉整行
                region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            }
        }

        /** 有值且鼠标悬停时才把箭头让位给清除按钮（其余时刻箭头常驻）。 */
        void bindAffordances(javafx.beans.value.ObservableBooleanValue hasValue) {
            clearBtn.visibleProperty().bind(Bindings.and(hasValue, root.hoverProperty()));
            clearBtn.managedProperty().bind(clearBtn.visibleProperty());
            arrow.visibleProperty().bind(clearBtn.visibleProperty().not());
        }

        void onClear(Runnable action) {
            clearBtn.setOnMouseClicked(e -> {
                action.run();
                e.consume();
            });
            // 键盘可达：空格 / 回车触发清除
            clearBtn.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case SPACE, ENTER -> {
                        action.run();
                        e.consume();
                    }
                    default -> { /* 其它按键不处理 */ }
                }
            });
        }

        /**
         * 点击输入框区域切换面板显隐。
         *
         * <p>单击照旧开面板（不改鼠标用户的肌肉记忆），同时把焦点留在输入框上，
         * 因此面板弹着也能直接敲数字录入。右侧箭头同样可开面板。</p>
         *
         * <p><b>清除按钮不需要（也不能）在这里拦截</b>：它与输入框是 {@link StackPane}
         * 下的<b>兄弟节点</b>，点击本就不会冒泡到输入框上，所以「点 × 不会顺带开面板」
         * 是布局天然保证的。曾经在这里加过一条
         * {@code clearBtn.addEventFilter(MOUSE_CLICKED, Event::consume)} 来表达同一意图，
         * 结果把 {@link #onClear} 挂在<b>同一节点、同一事件类型</b>上的处理器一起吃掉
         * （过滤器先于处理器执行），清除按钮从此变成纯装饰 —— 点了没反应。</p>
         */
        void onToggle(Runnable action) {
            arrow.setOnMouseClicked(e -> {
                action.run();
                e.consume();
            });
            field.setOnMouseClicked(e -> {
                action.run();
                field.requestFocus();
            });
        }
    }

    // ============================================================
    // Builder
    // ============================================================

    public static class Builder extends AbstractStyleBuilder<Builder> {

        private LocalTime value = null;
        private String format = DEFAULT_FORMAT;
        private boolean allowClear = true;
        private Size size = Size.DEFAULT;
        private Consumer<LocalTime> onChange = null;
        private ObjectProperty<LocalTime> bindProperty = null;
        private boolean editable = true;

        /** 初始时间；{@code null} 表示未选中（显示 placeholder）。 */
        public Builder value(LocalTime value) {
            this.value = value;
            return this;
        }

        /** 段位格式串，见类注释「format 语义」。{@code null}/空串 → 默认 {@code HH:mm:ss}。 */
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

        /** 时间变化回调（面板「确定」提交时触发；清除时回调 {@code null}）。 */
        public Builder onChange(Consumer<LocalTime> onChange) {
            this.onChange = onChange;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值（提交时回写，外部变更时同步显示）。 */
        public Builder bindValue(ObjectProperty<LocalTime> property) {
            this.bindProperty = property;
            return this;
        }
        /**
         * 触发器是否支持<b>键盘直接录入</b>（默认 {@code true}）。
         *
         * <p>开启后输入框可敲数字：段满或已定型自动跳段、{@code ↑↓} 步进、
         * {@code Enter} 提交、{@code Esc} 回退、失焦时值有变化则提交。
         * 关闭则恢复为只读显示，面板点选仍是唯一入口。</p>
         */
        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public StackPane build() {
            Visibility vis = parseVisibility(format);
            DateTimeFormatter formatter = compileFormatter(vis.pattern);

            // 绑定了 property 时以 property 为准（与旧实现一致）；否则用显式 value
            LocalTime initial = bindProperty != null ? bindProperty.get() : value;

            String effectivePlaceholder = hasPlaceholder()
                    ? placeholder
                    : Messages.get("timepicker.placeholder");
            Trigger trigger = new Trigger(JfxStyles.TIME_PICKER, JfxStyles.TIME_PICKER_FIELD,
                    effectivePlaceholder, size, allowClear);
            if (!hasPlaceholder()) {
                // i18n 懒解析：locale 切换时刷新 placeholder，不锁死初始 Locale
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        trigger.field.setPromptText(Messages.get("timepicker.placeholder")));
            }

            TimePanel panel = new TimePanel(vis, true);
            panel.setValue(initial);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.getContent().add(panel);
            // Popup 有独立 Scene，不继承宿主样式表；不注入则暗色主题下面板丢样式
            PopupPositioner.inheritStylesheets(popup, trigger.root);

            // 已提交值（唯一事实来源）：面板上的选择在「确定」前不影响它
            final LocalTime[] committed = {initial};

            final TimeTextMask[] mask = {null};

            // 单向同步：已提交值 → 触发器文本。掩码模式下由掩码渲染，
            // 否则用格式化器直出（两者都覆盖前缀/后缀，因此不能各自「补写」
            // 而是每次整体重绘）
            java.util.function.Consumer<LocalTime> syncTriggerText = value -> {
                if (mask[0] != null) {
                    // 掩码的模型是 LocalDateTime，本组件只喂时间部分
                    mask[0].display(value == null ? null : java.time.LocalDateTime.of(java.time.LocalDate.now(), value));
                } else {
                    trigger.field.setText(value == null ? "" : formatter.format(value));
                }
            };

            // 键盘录入的实时预览：面板可见时跟随显示（只改面板，不提交）
            java.util.function.Consumer<java.time.LocalDateTime> previewFromField = dt -> {
                if (dt != null) {
                    panel.syncFromTyping(dt.toLocalTime());
                }
            };

            java.util.function.Consumer<LocalTime> commitFromField = newValue -> {
                committed[0] = newValue;
                syncTriggerText.accept(newValue);
                popup.hide();
                if (bindProperty != null) {
                    bindProperty.set(newValue);
                }
                if (onChange != null) {
                    onChange.accept(newValue);
                }
            };

            if (editable && TimeTextMask.supports(vis.pattern)) {
                // 掩码需要「已提交值」作为回退基准与时段来源。本组件只持有 LocalTime，
                // 因此用面板当前日期把它补成 LocalDateTime（日期部分不参与提交）
                java.time.LocalDate today = java.time.LocalDate.now();
                java.util.function.Supplier<java.time.LocalDateTime> baseline =
                        () -> committed[0] == null ? null : java.time.LocalDateTime.of(today, committed[0]);
                mask[0] = new TimeTextMask(trigger.field, vis.pattern, baseline,
                        () -> committed[0] != null && committed[0].getHour() >= 12 ? 1 : 0,
                        java.time.LocalDateTime::toLocalTime,
                        dt -> commitFromField.accept(dt.toLocalTime()));
                mask[0].setOnPreview(previewFromField);
            } else {
                trigger.field.setEditable(false);
                popup.setHideOnEscape(true);
            }
            syncTriggerText.accept(initial);


            trigger.onToggle(() -> {
                if (Boolean.TRUE.equals(disable)) {
                    return;
                }
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    if (committed[0] != null) {
                        // 已提交值才回拨面板；为空时保留面板上的既有选择（见 setValue 注释）
                        panel.setValue(committed[0]);
                    }
                    PopupPositioner.showBelow(popup, trigger.root);
                }
            });

            // Esc 收面板 + 丢弃未提交的编辑。
            //
            // 掩码模式关掉弹层自带的 hideOnEscape：Popup 的「Esc 关层」走捕获阶段，
            // 优先于触发框上的过滤器，会让掩码既收不到 Esc、又留下没提交的数字，
            // 那串数字随后在失焦时被静默提交 —— 等于 Esc 反而保存了用户的输入。
            // 因此统一由「掩码还原 + 这里收面板」一条路径处理；无掩码时才交回给弹层。
            if (mask[0] != null) {
                // hideOnEscape 默认就是 true，且它挂在「宿主窗口」上按捕获阶段处理，
                // 会抢在触发框的过滤器之前把 Esc 吃掉 —— 必须显式关掉，
                // 把这条路径整段交给掩码（还原文本）来收尾。
                popup.setHideOnEscape(false);
                mask[0].setOnEscape(() -> {
                    if (popup.isShowing()) {
                        popup.hide();
                    }
                });
            }

            // 「确定」：提交 → 回写绑定 → onChange → 收起
            //
            // 面板可能是在用户「敲了数字但既没按 Enter 也没失焦」的状态下被点确定的。
            // 那种编辑只存在于字段文本里，掩码把它折进模型后才提交 —— 否则用户视角是
            // 「我明明输了 23:45，点确定却跳回 07:15」。
            panel.setOnCommit(newValue -> {
                if (mask[0] != null) {
                    java.time.LocalDateTime pending = mask[0].pendingEdit();
                    if (pending != null) {
                        newValue = pending.toLocalTime();
                    }
                }
                commitFromField.accept(newValue);
            });

            // 清除：回到未选中（placeholder），并通知外部
            trigger.onClear(() -> {
                committed[0] = null;
                syncTriggerText.accept(null);
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
                    syncTriggerText.accept(nv);
                });
            }

            if (Boolean.TRUE.equals(disable)) {
                trigger.field.setDisable(true);
            }

            applyStyles(trigger.root);

            Controller controller = new Controller(committed, panel, popup, trigger.root,
                    value -> syncTriggerText.accept(value));
            trigger.root.getProperties().put(CONTROLLER_KEY, controller);
            return trigger.root;
        }
    }

    // ============================================================
    // 时间面板（时 / 分 / 秒 三列 + 页脚）
    // ============================================================

    /**
     * 时间选择面板：段标题下一条可滚动列表（时 00-23 / 分 00-59 / 秒 00-59），页脚「此刻 / 确定」。
     *
     * <p><b>交互对标 Ant Design</b>：三列各含该段的<b>全部合法取值</b>，列高恒等于日期网格高，
     * 当前值自动滚到可见位置。选择因此是一次定点点击，而不是「先滚再瞄准」。</p>
     *
     * <p><b>为什么不用十位/个位两列</b>：两列方案用「恒 10 行、无滚动条」换了「列高只有内容高」，
     * 于是整块面板比左侧日期面板矮一大截（见 BUG #163 的密度对齐问题），且 24 要被读成
     * 「2 | 4」两个独立数字。Ant 的三列 + 缩窄滚动条方案保住了完整两位数与面板高度，
     * 代价是列可滚动 —— 但列表会自行把选中项滚进视野，落点是定点而非滚动。（M21.37）</p>
     *
     * <p>包级可见的内部构件，由 {@link TimePickerAnt} 与 {@link DateTimePickerAnt}
     * 共用（两者内嵌的是同一个面板，保证面板视觉与交互完全一致）。</p>
     */
    static final class TimePanel extends VBox {

        /** 上午/下午列的项目数：只有「上午 / 下午」两项。 */
        private static final int MERIDIEM_ITEM_COUNT = 2;

        private final Visibility vis;
        private final boolean twelveHour;

        /** 每段一条列表：项值即该段的十进制取值（12 小时制下时列为 1-12）。 */
        private ListViewAnt<Object> hourList;
        private ListViewAnt<Object> minuteList;
        private ListViewAnt<Object> secondList;
        private ListViewAnt<Object> meridiemList;

        private int hour;
        private int minute;
        private int second;

        /** 同步标志：{@link #applySelection()} 期间屏蔽列表选择事件回灌。 */
        private boolean syncing = false;
        /** 「确定」提交回调；由宿主组件接线。 */
        private Consumer<LocalTime> onCommit = null;

        /**
         * @param vis        格式串解析出的段位可见性
         * @param showFooter 是否渲染「此刻 / 确定」页脚（DateTimePicker 复用页脚，故可关闭）
         */
        TimePanel(Visibility vis, boolean showFooter) {
            this(vis, showFooter, false);
        }

        /**
         * @param vis        格式串解析出的段位可见性
         * @param showFooter 是否渲染「此刻 / 确定」页脚
         * @param embedded   是否嵌入别人的弹层：为 {@code true} 时去掉自身圆角/边框/阴影/内边距，
         *                   由宿主（DateTimePicker）统一画一个壳，避免「框中框」
         */
        TimePanel(Visibility vis, boolean showFooter, boolean embedded) {
            this.vis = vis;
            this.twelveHour = vis.meridiem;

            getStyleClass().add(JfxStyles.TIME_PANEL);
            if (embedded) {
                getStyleClass().add(JfxStyles.TIME_PANEL_EMBEDDED);
            }
            setFillWidth(true);

            HBox units = new HBox();
            units.setAlignment(Pos.TOP_LEFT);

            if (twelveHour) {
                units.getChildren().add(buildMeridiemUnit());
            }

            // 时：一次给全 0-23（12 小时制 1-12，钟点没有 0 点）。整段取值同列，读出来就是完整两位数。
            hourList = buildValueColumn("hour-list", twelveHour ? 1 : 0, twelveHour ? 12 : 23, 1,
                    this::setHour);
            units.getChildren().add(unit("timepicker.hour", hourList));

            if (vis.minute) {
                minuteList = buildValueColumn("minute-list", 0, 59, 0, this::setMinute);
                units.getChildren().add(unit("timepicker.minute", minuteList));
            }
            if (vis.second) {
                secondList = buildValueColumn("second-list", 0, 59, 0, this::setSecond);
                units.getChildren().add(unit("timepicker.second", secondList));
            }

            getChildren().add(units);

            if (showFooter) {
                ButtonAnt nowBtn = ButtonAnt.create(Messages.get("timepicker.now"))
                        .type(ButtonAnt.Type.TEXT)
                        .size(Size.SMALL);
                nowBtn.getStyleClass().add(JfxStyles.TIME_PANEL_NOW);
                nowBtn.setOnAction(e -> runNow());

                ButtonAnt okBtn = ButtonAnt.create(Messages.get("timepicker.ok"))
                        .type(ButtonAnt.Type.PRIMARY)
                        .size(Size.SMALL);
                okBtn.setOnAction(e -> commit());

                HBox footer = new HBox(Spacers.grow(), nowBtn, okBtn);
                footer.setAlignment(Pos.CENTER_RIGHT);
                footer.getStyleClass().add(JfxStyles.TIME_PANEL_FOOTER);
                getChildren().add(footer);
            }
        }

        /** 一个段（时/分/秒）：标题带 + 一条取值列表。 */
        private VBox unit(String i18nKey, ListViewAnt<Object> list) {
            VBox unit = new VBox();
            unit.getStyleClass().add(JfxStyles.TIME_PANEL_UNIT);

            LabelAnt header = LabelAnt.create(Messages.get(i18nKey));
            header.getStyleClass().add(JfxStyles.TIME_PANEL_UNIT_HEADER);
            header.setMaxWidth(Double.MAX_VALUE);
            header.setAlignment(Pos.CENTER);

            unit.getChildren().addAll(header, list);
            return unit;
        }

        /** AM/PM：单列两项（项为 {@link Boolean}，渲染成 上午/下午），返回整个段容器。 */
        private VBox buildMeridiemUnit() {
            VBox unit = new VBox();
            unit.getStyleClass().add(JfxStyles.TIME_PANEL_UNIT);
            unit.getStyleClass().add(JfxStyles.TIME_PANEL_MERIDIEM);

            LabelAnt header = LabelAnt.create(Messages.get("timepicker.meridiem"));
            header.getStyleClass().add(JfxStyles.TIME_PANEL_UNIT_HEADER);
            header.setMaxWidth(Double.MAX_VALUE);
            header.setAlignment(Pos.CENTER);

            ListViewAnt<Object> list = buildList(List.of(Boolean.FALSE, Boolean.TRUE), 0);
            list.getStyleClass().add(JfxStyles.TIME_PANEL_MERIDIEM_ITEM);
            // AM/PM 需要把 Boolean 渲染成上午/下午，故单独替换单元格工厂
            list.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null
                            : Messages.get(Boolean.TRUE.equals(item) ? "timepicker.pm" : "timepicker.am"));
                }
            });
            list.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
                if (!syncing && nv != null) {
                    onMeridiemPicked(Boolean.TRUE.equals(nv));
                }
            });
            unit.getChildren().addAll(header, list);
            meridiemList = list;
            return unit;
        }

        /**
         * 构建一条取值列：项为 {@code from..to} 的连续整数，点选即把该段定为选中值。
         *
         * @param name 用于定位的列名（{@code hour-list} / {@code minute-list} / {@code second-list}）
         * @param from 起始值（含）
         * @param to   结束值（含）
         * @param padTo 渲染时最少补零到几位（时列在 24 小时制下补成 {@code 00}；
         *              12 小时制的 1-12 不补零，与 Ant 的 12 小时制展示一致）
         * @param pick 点选回调（收到的是十进制取值，不是行号）
         */
        private ListViewAnt<Object> buildValueColumn(String name, int from, int to, int padTo,
                                                     Consumer<Integer> pick) {
            List<Object> items = new ArrayList<>(to - from + 1);
            for (int i = from; i <= to; i++) {
                items.add(Integer.valueOf(i));
            }
            ListViewAnt<Object> list = buildList(items, padTo);
            list.setId(name);
            list.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
                if (!syncing && nv != null) {
                    pick.accept((Integer) nv);
                }
            });
            return list;
        }

        /**
         * 构建一个受控列表：固定样式类、单选、不可键盘聚焦。
         *
         * @param padTo 整数项渲染成至少 {@code padTo} 位（0 表示按自然位数）
         */
        private static ListViewAnt<Object> buildList(List<Object> items, int padTo) {
            ListViewAnt<Object> list = ListViewAnt.create();
            list.items(FXCollections.observableArrayList(items));
            list.selectionMode(SelectionMode.SINGLE);
            list.getStyleClass().add(JfxStyles.TIME_PANEL_LIST);
            // 方向键不应改变选中行：点选即定值，键盘导航留在输入框上
            list.setFocusTraversable(false);
            // 空项必须显式为 null，否则 ListView 会渲染出 "null" 字样（BUG #160 教训）
            list.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else if (item instanceof Integer i) {
                        setText(padTo > 0 ? String.format("%0" + padTo + "d", i)
                                : String.valueOf(i));
                    } else {
                        setText(String.valueOf(item));
                    }
                }
            });
            return list;
        }

        /** 12 小时制下 hour 的显示值（0 → 12）。 */
        private static int display12Hour(int hour24) {
            int h = hour24 % 12;
            return h == 0 ? 12 : h;
        }

        /** 12 小时制显示值钳回 1-12。 */
        private static int clampHour12(int h12) {
            return Math.min(12, Math.max(1, h12));
        }

        /** 12 小时制显示值 + 当前上午/下午 → 24 小时制。 */
        private int to24Hour(int h12) {
            int h = clampHour12(h12) % 12;
            boolean pm = meridiemList != null
                    && Boolean.TRUE.equals(meridiemList.getSelectionModel().getSelectedItem());
            return pm ? h + 12 : h;
        }

        // ------------------------------------------------------------
        // 选择处理：各列写模型（提交由「确定」统一完成）
        // ------------------------------------------------------------

        /** 时列点选：12 小时制下收到的是 1-12 的钟点，需按当前半天折算。 */
        private void setHour(int picked) {
            if (!twelveHour) {
                hour = Math.min(23, Math.max(0, picked));
                return;
            }
            hour = to24Hour(picked);
        }

        private void setMinute(int candidate) {
            minute = Math.min(59, Math.max(0, candidate));
        }

        private void setSecond(int candidate) {
            second = Math.min(59, Math.max(0, candidate));
        }

        /** 上午/下午切换：保持 12 小时制的钟点数字，只换半天。 */
        private void onMeridiemPicked(boolean pm) {
            int h12 = hour % 12;
            hour = pm ? h12 + 12 : h12;
        }

        private void runNow() {
            setValue(LocalTime.now());
        }

        // ------------------------------------------------------------
        // 值同步
        // ------------------------------------------------------------

        /** 面板当前值（未显示的段按 0 计）。 */
        LocalTime getValue() {
            return LocalTime.of(hour, vis.minute ? minute : 0, vis.second ? second : 0);
        }

        /**
         * 面板可见时跟随触发器里的键盘录入。
         *
         * <p>不这样做就会出现「框里已经是 23:45，面板还停在 07:15」的割裂：
         * 用户敲完看一眼面板确认，看到的是旧值；接着点「确定」还会把旧值提交回去。
         * 偏离已提交值时面板只是跟随显示，不主动提交。</p>
         */
        void syncFromTyping(LocalTime time) {
            if (time == null || !isVisible()) {
                return;
            }
            setValue(time);
        }

        /**
         * 把面板选择同步到给定时间；{@code null} 表示「未选中」。
         *
         * <p><b>未选中时取当前时刻，而不是 00:00</b>。清除按钮把值置空后重新打开面板，
         * 用户看到的是面板的一次全新默认（尚未提交，框里仍是空的）：此刻最贴近
         * 「我现在想选几点」的默认就是当前时间。演示 00:00 会让用户以为「点 × 把时间
         * 变成 0 点了」（BUG #168）。这与左邻的日期侧一致 —— 日期面板在未选中时
         * 落在今天（{@code CalendarAnt.value(null) → LocalDate.now()}，
         * {@code DateTimePickerAnt} 同样取 {@code LocalDate.now()}）。</p>
         *
         * <p>注意它只影响面板显示，不改已提交值：面板值要等用户点「确定」才提交。</p>
         */
        void setValue(LocalTime time) {
            LocalTime target = time != null ? time : LocalTime.now().withNano(0);
            hour = target.getHour();
            minute = target.getMinute();
            second = target.getSecond();
            applySelection();
        }

        /** 把模型值反映到各列并把选中项滚进视野（屏蔽事件回灌）。 */
        private void applySelection() {
            syncing = true;
            try {
                if (meridiemList != null) {
                    selectValue(meridiemList, hour >= 12 ? Boolean.TRUE : Boolean.FALSE,
                            hour >= 12 ? "下午" : "上午");
                }
                selectValue(hourList, twelveHour ? display12Hour(hour) : hour, "时");
                selectValue(minuteList, minute, "分");
                selectValue(secondList, second, "秒");
            } finally {
                syncing = false;
            }
        }

        /**
         * 选中某列取值，并把该行滚进视野。
         *
         * <p><b>为什么必须显式滚动</b>：JavaFX 的 {@code ListView} 只在改动选中<b>索引</b>
         * （{@code select(int)}）时自动滚动；走 {@code select(Object)} 这条按值选中的路径，
         * 选中确实生效、列表却停在原处 —— 于是面板打开时可能看到「09:30 高亮在视野外」，
         * 与「列高只有 8 行、其余靠滚」的滚动列方案直接冲突。</p>
         *
         * <p><b>上午/下午列例外：它不需要滚动，也不能滚</b>。该列只有 2 项，且高度被
         * LESS 钳在 {@code 2 × @time-panel-cell-height}（见 {@code .jfx-time-panel-unit
         * .jfx-time-panel-meridiem .jfx-time-panel-list}），两行本就同屏。而
         * {@code scrollTo(i)} 会把第 {@code i} 行<b>顶到视口首行</b> —— 哪怕它本来就在
         * 视口内：选中「下午」（第 2 行）会把「上午」整行顶出视口且不再回落，用户看到的
         * 就是「选完下午，上午不见了」，此后无法改回上午。时/分/秒列有 24-60 行，顶到
         * 首行仍属将目标行置中的正常行为，故只有 2 行的时段列跳过滚动。</p>
         */
        private static void selectValue(ListViewAnt<Object> col, Object value, String label) {
            if (col == null) {
                return;
            }
            if (!col.getItems().contains(value)) {
                throw new IllegalStateException("时间面板「" + label + "」列缺少取值 " + value);
            }
            col.getSelectionModel().select(value);
            if (col.getItems().size() > MERIDIEM_ITEM_COUNT) {
                col.scrollTo(value);
            }
        }

        void setOnCommit(Consumer<LocalTime> onCommit) {
            this.onCommit = onCommit;
        }

        /** 触发「确定」提交（页脚按钮调用）。 */
        void commit() {
            if (onCommit != null) {
                onCommit.accept(getValue());
            }
        }
    }

    // ============================================================
    // Controller
    // ============================================================

    /**
     * 运行时控制器：构建后改值 / 打开面板 / 读取当前值。
     *
     * <p>取回方式：{@code TimePickerAnt.controllerOf(node)}。</p>
     */
    public static final class Controller {

        private final LocalTime[] committed;
        private final TimePanel panel;
        private final Popup popup;
        private final Node anchor;
        private final java.util.function.Consumer<LocalTime> syncTriggerText;

        Controller(LocalTime[] committed, TimePanel panel, Popup popup, Node anchor,
                   java.util.function.Consumer<LocalTime> syncTriggerText) {
            this.committed = committed;
            this.panel = panel;
            this.popup = popup;
            this.anchor = anchor;
            this.syncTriggerText = syncTriggerText;
        }

        /** 当前已提交的值；未选中时为 {@code null}。 */
        public LocalTime getValue() {
            return committed[0];
        }

        /** 设置已提交的值并刷新触发器显示（不触发 {@code onChange}）。 */
        public void setValue(LocalTime value) {
            committed[0] = value;
            panel.setValue(value);
            syncTriggerText.accept(value);
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
            panel.setValue(committed[0]);
            PopupPositioner.showBelow(popup, anchor);
        }

        /** 收起面板（若在显示中）。 */
        public void hidePanel() {
            popup.hide();
        }

        /** 面板节点（供测试与二次封装使用）。 */
        public Node getPanelNode() {
            return panel;
        }
    }
}
