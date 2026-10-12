package org.openkawu.jfxium.component.composite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 选择器文本掩码 —— 让触发器的输入框可以<b>直接敲数字</b>，不必开面板逐位点选。
 *
 * <p>M21.33 引入的「键盘优先」入口：桌面工具用户手上是键盘，用面板设一个
 * {@code 2026-10-23 09:03:40} 最少要点十几次，直接输入则敲满位数即可完成。</p>
 *
 * <h2>整串录入（不是「只接管时间后缀」）</h2>
 * <p>文本按模式串<b>完整</b>分段：{@code yyyy-MM-dd HH:mm:ss} 有 6 段
 * （年/月/日/时/分/秒），每一段都能用光标定位、逐位改写。这一点是硬要求：
 * 日期与时间若分成「面板管一半、键盘管一半」两套状态，就会出现
 * 「点到月份上敲数字，改的却是小时」这类看得见、改不着的错位
 * （旧版把日期做成只读前缀，前缀里的任何点击都被折算成「数字序列第 0 位」，
 * 于是年/月/日的点击统统落进小时段）。</p>
 *
 * <h2>模型：一个光标 + 各段位数</h2>
 * <p>文本由两个量确定性推出，没有第三种状态：</p>
 * <ul>
 *   <li>{@code count[段]} —— 该段<b>当前显示位数</b>（0 = 该段及之后全空）；</li>
 *   <li>{@code cursor} —— 光标在扁平数字序列里的位置（等同「插在第几个数字前」）。</li>
 * </ul>
 * <p>段值不再是独立状态，而是<b>显示文本本身</b>（{@link #parse} 直接读渲染出来的数字）。
 * 这样「看到的就是提交的」，不存在显示与内部值分叉的可能。</p>
 *
 * <h3>光标只有一条规则</h3>
 * <p>{@code cursor} 是数字串里真实的插入点，换算成屏幕坐标与键盘归属用的是<b>同一条规则</b>：
 * 边界（前几段位数之和 + 本段位数）属于右侧段。因此</p>
 * <ul>
 *   <li>把光标放在「本段最后一个数字之后、分隔符之前」是<b>做不到的</b> —— 那个位置按定义已在下一段；</li>
 *   <li>插入符画在分隔符<b>左边</b>（贴住刚录完的数字），键盘却落在分隔符<b>右边</b>的段；</li>
 *   <li>所以← / → 与数字键永远指向同一段，不会出现「屏幕上光标在小时段、敲下去改的却是分钟段」。</li>
 * </ul>
 *
 * <h3>数字键语义</h3>
 * <p>光标停在第 {@code k} 段时，敲一个数字的含义由「它落在段内第几位」唯一决定：</p>
 * <table><caption>数字键语义</caption>
 *   <tr><th>光标位置</th><th>点击</th><th>结果</th></tr>
 *   <tr><td>段内第 1 位</td><td>{@code 9}（9 时）</td><td><b>整段替换</b>为该值并前进（显示的 {@code 07} 不需要先框选）</td></tr>
 *   <tr><td>段内第 2 位</td><td>{@code 2} {@code 3}</td><td>组成两位（23 时），值越界则整段替换（{@code 29} → 9 时，而不是钳成 23 时）</td></tr>
 *   <tr><td>段末边界（本段已满）</td><td>{@code 3}</td><td>数字进<b>下一段</b>（{@code 07:15} 后再敲 → {@code 07:15:3}）</td></tr>
 * </table>
 * <p>关键在于<b>整段替换由光标位置决定，而不是靠选中态</b> —— 直接敲就是替换。
 * 日期段与时间段共用同一套语义，因此「点月份 → 敲 11」得到的就是 11 月。
 * 焦点进来时默认停在第一个时间段（{@link #initialDigitIndex}），点一下就改指向。</p>
 *
 * <h2>按键契约</h2>
 * <table><caption>键位</caption>
 *   <tr><th>按键</th><th>行为</th></tr>
 *   <tr><td>0-9</td><td>写入光标处（见上表）；写满一段自动跳到下一段</td></tr>
 *   <tr><td>退格</td><td>删除光标左边一个数字，光标左移；到头的段退回本段第 1 位</td></tr>
 *   <tr><td>Delete</td><td>删除光标右边一个数字</td></tr>
 *   <tr><td>← →</td><td>按数字位移动（跨段时跳过段首的自动补零，不冻结在该位）</td></tr>
 *   <tr><td>Home End</td><td>跳到数字序列首 / 尾</td></tr>
 *   <tr><td>↑ ↓</td><td>光标所在段 ±1（{@code Shift} 为 ±10），越界回绕</td></tr>
 *   <tr><td>Enter</td><td>提交；空字段不提交</td></tr>
 *   <tr><td>Esc</td><td>还原为已提交值，并通知弹层收起面板</td></tr>
 *   <tr><td>失焦</td><td>与已提交值不同则提交，否则还原（不留「显示值 ≠ 实际值」的谎言）</td></tr>
 * </table>
 *
 * <h2>粘贴</h2>
 * <p>可直接把 {@code 2026-10-23 23:45:00} / {@code 2345} 贴进字段：只取其中的数字，
 * 从光标处起按位覆盖，多余部分丢弃，绝不整段顶掉已提交值。贴不进去的剪贴板内容
 * （一个数字都没有）保持原样不动。</p>
 *
 * <h2>适用性判定（{@link #supports}）</h2>
 * <p>段宽、分隔符、后缀都从格式化器对参考时刻的<b>实际输出</b>反推，因此引号字面量、
 * 中文分隔符、{@code a} 展开成的「上午/下午」都自动适配。但这要求渲染出的数字段数与
 * 模式串里的数字字段数<b>一致</b>；对不上时（如毫秒 {@code SSS} 多出一段、
 * {@code z} 时区名原本就不是数字）{@link #supports} 返回 {@code false}，
 * 调用方应保持只读 —— 宁可退化成面板点选，也不给出错位的录入。</p>
 *
 * <p>模式串的日期与时间<b>先后顺序不限</b>：{@code HH:mm yyyy-MM-dd} 同样按字段出现顺序分段，
 * 因此不再要求「时间必须在末尾」。</p>
 *
 * <p><b>12 小时制</b>：格式串带 {@code a} 时，钟点段按 1-12 录入（{@link #meridiemIndex}
 * 提供当前是上午还是下午），后缀由格式化器按该时刻真实渲染。</p>
 */
final class TimeTextMask {

    /**
     * 掩码的一段：位数、取值范围、字段类型（y/M/d/H/m/s）。
     *
     * <p>{@code field} 是该段落到 {@link LocalDateTime} 上的哪个字段，
     * 提交时据此把各段数字组装回一个时刻。</p>
     */
    private record Seg(int width, int maxValue, char kind, ChronoField field) {
    }

    /** 渲染探针：各字段取互不相同的数字，便于反推段宽与分隔符。 */
    private static final LocalDateTime PROBE = LocalDateTime.of(2026, 10, 12, 15, 45, 57);

    private final TextField field;
    private final Seg[] segs;
    private final DateTimeFormatter formatter;
    /** 数字序列之前的字面量（如 {@code "at "}）；通常为空串。 */
    private final String prefix;
    /** 段间字面量（分隔符）；长度为 {@code segs.length - 1}。 */
    private final List<String> separators;
    /** 当前时段：0 上午 / 1 下午（仅 12 小时制有意义）。 */
    private final IntSupplier meridiemIndex;
    /** 已提交值（Enter/Esc/失焦的比对与回退基准）；可为 {@code null}（未选中）。 */
    private final Supplier<LocalDateTime> baseline;
    /** 提交前回调：先把新值交给宿主（面板联动），再由掩码完成提交。 */
    private final Consumer<LocalDateTime> preCommit;
    /** 提交后回调：宿主收尾（收起弹层、回写绑定、触发 onChange）；可为 {@code null}。 */
    private final Consumer<LocalDateTime> postCommit;
    /** Esc 的额外动作（弹层控件用来同步关面板）；可为 {@code null}。 */
    private Runnable onEscape;
    /** 编辑时的实时预览回调（弹层控件用来让面板跟随键盘录入）；可为 {@code null}。 */
    private Consumer<LocalDateTime> onPreview;

    /**
     * 各段<b>当前显示位数</b>；0 表示该段及其后全部为空。
     *
     * <p>与「已敲入几位」是同一件事 —— 显示本身就是状态，段值直接读显示文本，
     * 因此不存在「内部值」与「看到的数字」分叉的余地。</p>
     */
    private final int[] count;
    /** 各段的模型值（{@code -1} = 未敲入）；显示位数由 {@code count} 决定。 */
    private final int[] value;
    /** 光标位置：扁平数字序列里的插入点（0..总位数）。 */
    private int cursor;
    /** 自绘制期间为 {@code true}，用于屏蔽 caret / text 监听器的回声。 */
    private boolean syncing;
    /** 鼠标按下事件已在等待平台把 caret 摆到按下位置。 */
    private boolean clickPending;
    /**
     * 用户是否<b>亲手</b>放过光标（点击 / 拖选）。
     *
     * <p>这是「这个 caret 到底算不算用户意图」的唯一判据。焦点切换、布局、程序化写入
     * 都会让平台把 caret 归零，那些都不是意图；若照单全收，默认落点（第一个时间段）
     * 会被平台那一下归零顶掉，症状就是「敲数字改的是年份」。</p>
     */
    private boolean userPlacedCaret;

    /**
     * @param field         目标输入框（本类会把它置为可编辑）
     * @param pattern       规范模式串（整串，含日期与时间字段）
     * @param baseline      已提交值（Enter/Esc/失焦的比对与回退基准）；可为 {@code null}
     * @param meridiemIndex 当前时段（0 上午 / 1 下午）；24 小时制可传 {@code null}
     * @param preCommit     提交前回调：先更新宿主状态，掩码随后才落地提交
     * @param postCommit    提交后回调（收尾）；可为 {@code null}
     * @throws IllegalArgumentException 模式串不受支持（调用前应先用 {@link #supports} 判定）
     */
    TimeTextMask(TextField field, String pattern, Supplier<LocalDateTime> baseline,
                 IntSupplier meridiemIndex,
                 Consumer<LocalDateTime> preCommit, Consumer<LocalDateTime> postCommit) {
        Seg[] plan = segmentsOf(pattern);
        if (plan == null) {
            throw new IllegalArgumentException("TimeTextMask 不支持的模式串（渲染段数与字段数不一致）：" + pattern);
        }
        this.field = field;
        this.segs = plan;
        this.formatter = TimePickerAnt.compileFormatter(pattern);
        this.meridiemIndex = meridiemIndex != null ? meridiemIndex : () -> 0;
        this.baseline = baseline != null ? baseline : () -> null;
        this.preCommit = preCommit;
        this.postCommit = postCommit;

        Layout layout = layoutOf(formatProbe(this.formatter));
        this.prefix = layout.prefix;
        this.separators = layout.separators;
        this.count = new int[segs.length];
        this.value = new int[segs.length];
        Arrays.fill(value, -1);

        field.setEditable(true);
        field.setFocusTraversable(true);
        // 用事件过滤器抢占原生编辑：数字写入/删除全走分段模型，
        // 保证「光标永远停在某个数字段上」这一不变量
        field.addEventFilter(KeyEvent.KEY_PRESSED, this::handlePressed);
        field.caretPositionProperty().addListener((obs, ov, nv) -> {
            if (!syncing && userPlacedCaret) {
                syncCaretFromField();
            }
        });
        field.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!e.isControlDown() && !e.isMetaDown()) {
                e.consume();
            }
        });
        // 鼠标点击/拖选：平台在行为处理阶段才把 caret 挪到按下位置，
        // 因此不能在事件里直接读，要等这一轮事件处理结束
        field.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            userPlacedCaret = true;
            clickPending = true;
            Platform.runLater(this::applyPendingClick);
        });
        field.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, e -> {
            userPlacedCaret = true;
            clickPending = true;
            Platform.runLater(this::applyPendingClick);
        });
        // caret 变化不总是用户意图：获得焦点时平台会把它归零（Tab 进来也会），
        // 程序化写入也会动它。因此只在「不在焦点切换中」时才把它当意图采纳 ——
        // 否则默认定位（第一个时间段）会被平台那次归零抽走，敲数字改的是年份
        field.focusedProperty().addListener((obs, ov, nv) -> {
            if (Boolean.TRUE.equals(nv)) {
                Platform.runLater(() -> {
                    if (clickPending) {
                        // 点击进来的：以按下位置为准（平台这时才把 caret 摆好）
                        clickPending = false;
                        syncCaretFromField();
                        return;
                    }
                    if (!userPlacedCaret) {
                        // Tab / 程序化聚焦：落回默认段（键盘用户聚焦即可敲时间）
                        cursor = initialDigitIndex();
                    }
                    // TextField 获得焦点时默认全选；这里必须撤掉 ——
                    // 全选会让用户以为「敲下去整串替换」，而实际只有光标所在那一段被替换
                    field.deselect();
                    refresh();
                });
            } else {
                clickPending = false;
                userPlacedCaret = false;
                commitOrRevert();
            }
        });
        // 旁路写入自愈：粘贴、剪切、拖拽、撤销都会绕过 KEY_TYPED 直接改文本，
        // 而分段模型并不知道这些字符。文本框的唯一事实来源是模型，
        // 因此检测到外部改写就立刻按模型重绘（否则文本与值会各说各话）。
        field.textProperty().addListener((obs, ov, nv) -> {
            if (syncing) {
                return;
            }
            // 旁路写入（右键菜单粘贴、鼠标中键、拖拽、程序化 setText）：
            //   · 里面有数字 → 按数字逐位折进模型（用户粘贴一整串时间就是想填进去）
            //   · 一个数字都没有 → 按模型重绘，把这次写入丢掉
            // 两条路都落到「文本缓冲没有独立真相」这条不变量上。
            if (digitsOf(nv == null ? "" : nv).isEmpty()) {
                refresh();
            } else {
                pasteDigits(nv);
            }
        });

        display(baseline.get());
    }

    // ============================================================
    // 适用性判定与分段计划
    // ============================================================

    /**
     * 注册 Esc 的额外动作（弹层控件用来关面板）。
     *
     * <p>掩码会把 {@code ESCAPE} 消费掉，因此 {@code Popup} 自己的
     * <em>Esc 关层</em>不再有机会生效 —— 必须由掩码代转，否则「Esc 收面板」
     * 这条约定只在没有掩码的字段上成立。</p>
     */
    void setOnEscape(Runnable action) {
        this.onEscape = action;
    }

    /**
     * 注册实时预览回调：每次编辑都把「当前显示推出来的时刻」回调出去。
     *
     * <p>弹层控件用它让面板跟随键盘录入。不这么做就会出现「框里是 23:45、
     * 面板还停在 07:15」的割裂 —— 用户敲完低头看面板确认，看到的是旧值。</p>
     */
    void setOnPreview(Consumer<LocalDateTime> action) {
        this.onPreview = action;
    }

    /**
     * 该模式串能否键盘录入。
     *
     * <p>判据是「格式化器渲染出的数字段数 == 模式串里的数字字段数」。
     * {@code "yyyy-MM-dd HH:mm:ss"} 通过；含无法映射到数字段的字段（如 {@code z} 时区名）
     * 时返回 {@code false}，调用方应保持只读。</p>
     */
    static boolean supports(String pattern) {
        return segmentsOf(pattern) != null;
    }

    /**
     * 分段计划：段数、段宽、取值范围。
     *
     * <p>段序与模式串里数字字段的出现顺序一致，段宽取自格式化器的<b>实际输出</b>
     * （{@code H} 只写一个字母时 0-23 仍需两位才能表达）。渲染出的数字段数与字段数
     * 不一致时返回 {@code null}（无法建立一一映射）。</p>
     */
    private static Seg[] segmentsOf(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return null;
        }
        DateTimeFormatter formatter;
        String rendered;
        try {
            formatter = TimePickerAnt.compileFormatter(pattern);
            rendered = formatProbe(formatter);
        } catch (RuntimeException ex) {
            return null;
        }
        char[] kinds = digitFieldKinds(pattern);
        if (kinds.length == 0) {
            return null;
        }
        List<String> runs = digitRuns(rendered);
        if (runs.size() != kinds.length) {
            return null;
        }
        boolean hasMeridiem = pattern.indexOf('a') >= 0;
        Seg[] segs = new Seg[kinds.length];
        for (int i = 0; i < segs.length; i++) {
            int width = runs.get(i).length();
            segs[i] = switch (kinds[i]) {
                case 'y', 'u' -> new Seg(width, width >= 3 ? 9999 : 99, 'y', ChronoField.YEAR);
                case 'M' -> new Seg(width, width == 1 ? 9 : 12, 'M', ChronoField.MONTH_OF_YEAR);
                case 'd' -> new Seg(width, width == 1 ? 9 : 31, 'd', ChronoField.DAY_OF_MONTH);
                case 'H' -> new Seg(width, hasMeridiem ? 12 : 23, 'H', ChronoField.HOUR_OF_DAY);
                case 'm' -> new Seg(width, width == 1 ? 9 : 59, 'm', ChronoField.MINUTE_OF_HOUR);
                default -> new Seg(width, width == 1 ? 9 : 59, 's', ChronoField.SECOND_OF_MINUTE);
            };
        }
        return segs;
    }

    /**
     * 模式串里会渲染成数字的字段，按出现顺序排列（{@code 'a'} 这类渲染成文字的字段不算）。
     *
     * <p>连续同名字母是<b>一个</b>字段（{@code "yyyy"} 是年、{@code "MM"} 是月），
     * 因此同名字母只登记一次；引号内的字面量（如 {@code "'年'"}）跳过，与 JDK 语义一致。</p>
     */
    private static char[] digitFieldKinds(String pattern) {
        List<Character> kinds = new ArrayList<>(4);
        boolean inQuote = false;
        char prev = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\'') {
                inQuote = !inQuote;
                prev = 0;
                continue;
            }
            if (inQuote) {
                prev = 0;
                continue;
            }
            if (isDigitFieldLetter(c)) {
                if (c != prev) {
                    kinds.add(c);
                }
                prev = c;
            } else {
                prev = 0;
            }
        }
        char[] out = new char[kinds.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = kinds.get(i);
        }
        return out;
    }

    /** 会渲染成数字的字段字母（{@code 'a'} 渲染成「上午 / 下午」等文字，不计入）。 */
    private static boolean isDigitFieldLetter(char c) {
        return c == 'y' || c == 'u' || c == 'M' || c == 'd' || c == 'H' || c == 'm' || c == 's';
    }

    /** 参考时刻渲染出的整串文本（探针）：段宽、分隔符、后缀都由它反推。 */
    private static String formatProbe(DateTimeFormatter formatter) {
        return formatter.format(PROBE);
    }

    /** 渲染文本里的数字段（连续数字为一组）。 */
    private static List<String> digitRuns(String rendered) {
        List<String> runs = new ArrayList<>(4);
        int i = 0;
        while (i < rendered.length()) {
            if (!Character.isDigit(rendered.charAt(i))) {
                i++;
                continue;
            }
            int start = i;
            while (i < rendered.length() && Character.isDigit(rendered.charAt(i))) {
                i++;
            }
            runs.add(rendered.substring(start, i));
        }
        return runs;
    }

    /** 探针渲染结果里的段间字面量与前置字面量。 */
    private record Layout(List<String> separators, String prefix) {
    }

    private static Layout layoutOf(String probe) {
        List<String> tokens = new ArrayList<>(8);
        StringBuilder sb = new StringBuilder();
        boolean digitTok = !probe.isEmpty() && Character.isDigit(probe.charAt(0));
        for (int i = 0; i < probe.length(); i++) {
            char c = probe.charAt(i);
            boolean d = Character.isDigit(c);
            if (d != digitTok) {
                tokens.add(sb.toString());
                sb.setLength(0);
                digitTok = d;
            }
            sb.append(c);
        }
        tokens.add(sb.toString());

        String prefix = "";
        int i = 0;
        if (!probe.isEmpty() && !Character.isDigit(probe.charAt(0))) {
            prefix = tokens.get(0);
            i = 1;
        }
        List<String> separators = new ArrayList<>(4);
        for (; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (!Character.isDigit(t.charAt(0))) {
                // 末段之后 → 本地化后缀（如「 上午」），渲染时按实际值单独取
                if (i == tokens.size() - 1) {
                    break;
                }
                separators.add(t);
            }
        }
        return new Layout(separators, prefix);
    }

    // ============================================================
    // 渲染
    // ============================================================

    /**
     * 由 {@code count} 确定性重绘文本。
     *
     * <p>位数直接来自 {@code count}（只在「已敲入」与「已满」两种状态间取值），
     * 不做任何 padding 猜测：{@code 2} 就是一位（用户正在敲 23 时），
     * {@code 9} 就是两位（9 时，24 小时制下再补一位必然越界）。</p>
     */
    private String render() {
        StringBuilder out = new StringBuilder(prefix);
        boolean any = false;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] == 0) {
                break;
            }
            any = true;
            out.append(digitsOfSegment(i));
            if (i < segs.length - 1) {
                // 只给「下一段已经有数字」的位置补分隔符。
                // 光标停在还没敲入的段上时靠插入符表达位置，不额外画悬空冒号
                //（TEXTFIELD 里游离的 ':' 比「没有分隔符」更让人困惑）
                if (count[i + 1] > 0) {
                    out.append(separators.get(i));
                }
            } else if (count[i] == segs[i].width) {
                out.append(tail());
            }
        }
        return any ? out.toString() : "";
    }

    /** 第 {@code i} 段当前显示的 {@code count[i]} 位数字。 */
    private String digitsOfSegment(int i) {
        return pad(segmentValue(i), count[i]);
    }

    /** 第 {@code i} 段当前显示的值（读显示文本，越界字符不会出现）。 */
    private int segmentValue(int i) {
        int v = read(i);
        return v > segs[i].maxValue ? 0 : v;
    }

    /** 末段之后的本地化后缀（12 小时制的「 上午 / 下午」）；取自格式化器对该时刻的渲染。 */
    private String tail() {
        String rendered = formatter.format(displayValue());
        int lastDigit = -1;
        for (int i = 0; i < rendered.length(); i++) {
            if (Character.isDigit(rendered.charAt(i))) {
                lastDigit = i;
            }
        }
        return rendered.substring(lastDigit + 1);
    }

    /** 段前只读区的字符数。 */
    private int prefixLength() {
        return prefix.length();
    }

    /**
     * 焦点进来时的默认光标位：第一个<b>时间</b>段（没有时间段则是第 1 段）。
     *
     * <p>日期 + 时间的组合框里，改时间的频率远高于改日期（日期另有日历面板），
     * 因此默认把插入点放在「时」上，敲数字就是改时间。但这只是<b>默认</b>：
     * 点到哪一段，插入点就落在哪一段 —— 两者不会再互相覆盖。</p>
     *
     * <p>按各段<b>满位</b>宽度累计，与当前显示位数无关，
     * 因此空字段也指向同一个逻辑位置。</p>
     */
    private int initialDigitIndex() {
        int seen = 0;
        for (Seg seg : segs) {
            if (seg.kind == 'H' || seg.kind == 'm' || seg.kind == 's') {
                return seen;
            }
            seen += seg.width;
        }
        return 0;
    }

    // ============================================================
    // 数字缓冲：读写与几何
    // ============================================================

    /** 读第 {@code i} 段当前值；未敲入时 0。 */
    private int read(int i) {
        String d = rawDigits(i);
        return d.isEmpty() ? 0 : Integer.parseInt(d);
    }

    /** 第 {@code i} 段在模型里的原始数字串，位数 = {@code count[i]}。 */
    private String rawDigits(int i) {
        return pad(modelValue(i), count[i]);
    }

    /**
     * 第 {@code i} 段的模型值。
     *
     * <p>独立于 {@code count} 保存，避免「左移一位」这类操作丢信息；
     * 显示位数由 {@code count} 决定，因此 {@code value=23, count=1} 会显示 {@code 2}。</p>
     */
    private int modelValue(int i) {
        return value[i] < 0 ? 0 : value[i];
    }

    /** 写第 {@code i} 段的值。越界仅被记录，读取时按 0 处理（见 {@link #segmentValue}）。 */
    private void write(int i, int v) {
        value[i] = Math.max(0, Math.min(v, 9999));
    }

    /** 第 {@code i} 段的显示起始下标（含分隔符，按当前显示推）。 */
    private int segStart(int i) {
        int pos = prefixLength();
        for (int k = 0; k < i; k++) {
            int len = count[k];
            if (len == 0) {
                break;
            }
            pos += len;
            if (count[k + 1] > 0) {
                pos += separators.get(k).length();
            }
        }
        return pos;
    }

    /** 光标所在段（由 {@link #cursor} 推出）。 */
    private int segAtCursor() {
        return segForDigitIndex(cursor);
    }

    /**
     * 数字下标落在哪一段。
     *
     * <p>光标是数字串里真实的插入点，边界（{@code index == 前几段位数之和 + 本段位数}）
     * 因此按定义属于右侧那一段：光标画在「下一段的第一个数字之前」，键盘落在下一段。
     * 这与屏幕上看到的插入符位置一致，也是 Windows 时间控件的行为。</p>
     *
     * <p>本段还没写满时（{@code count[i] < width}），段末边界仍属于本段里最后一个数字之后，
     * 于是「敲 2 得 2、再敲 3」会继续补成本段内容，不会跳到下一段。</p>
     */
    private int segForDigitIndex(int index) {
        int seen = 0;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] == 0) {
                return i;
            }
            if (index < seen + count[i]) {
                return i;
            }
            if (index == seen + count[i] && count[i] < segs[i].width) {
                return i;
            }
            seen += count[i];
        }
        return segs.length - 1;
    }

    /** 光标在第 {@code i} 段内的位次（0 = 段首）。 */
    private int slotAtCursor(int i) {
        int seen = 0;
        for (int k = 0; k < i; k++) {
            seen += count[k];
        }
        return Math.max(0, cursor - seen);
    }

    /** 光标在扁平数字序列里的下标（与该段内位次互逆）。 */
    private int cursorTo(int seg, int slot) {
        int seen = 0;
        for (int k = 0; k < seg; k++) {
            seen += count[k];
        }
        return seen + slot;
    }

    /** 扁平数字序列总位数。 */
    private int totalDigits() {
        int n = 0;
        for (int c : count) {
            n += c;
        }
        return n;
    }

    /**
     * 扁平数字下标 → 显示坐标。
     *
     * <p>插入符贴在「刚录过的那串数字」末尾显示（段末边界画在分隔符左侧），
     * 因此段末与段内最后一格都是有效位置、按 ← 每次都会动一格。
     * 键盘归属另有一套（见 {@link #segForDigitIndex}）：段末的那一位落到下一段，
     * 等价于「在分隔符处录入自动跳过」。两者分工不同但都只有一条规则，不会互相打架。</p>
     */
    private int displayIndexFor(int digitIndex) {
        int seen = 0;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] == 0) {
                break;
            }
            if (digitIndex < seen + count[i]) {
                return segStart(i) + (digitIndex - seen);
            }
            if (digitIndex == seen + count[i]) {
                // 段末边界：按同一规则归右侧段。下一段还没内容时它和段末是同一个位置
                return i < segs.length - 1 && count[i + 1] > 0
                        ? segStart(i + 1)
                        : segStart(i) + count[i];
            }
            seen += count[i];
        }
        int last = segs.length - 1;
        while (last > 0 && count[last] == 0) {
            last--;
        }
        return segStart(last) + count[last];
    }

    /** 扁平数字下标 ← 显示坐标（分隔符吸附到其右侧的数字）。 */
    private int digitIndexFor(int displayIndex) {
        int pos = prefixLength();
        int seen = 0;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] == 0) {
                break;
            }
            for (int k = 0; k < count[i]; k++) {
                if (pos >= displayIndex) {
                    return seen + k;
                }
                pos++;
            }
            if (i < segs.length - 1 && count[i + 1] > 0) {
                if (pos >= displayIndex) {
                    return seen + count[i];
                }
                pos += separators.get(i).length();
            }
            seen += count[i];
        }
        return totalDigits();
    }

    // ============================================================
    // 按键
    // ============================================================

    private void handlePressed(KeyEvent e) {
        if (e.isConsumed() || field.isDisabled()) {
            return;
        }
        switch (e.getCode()) {
            case ENTER -> {
                e.consume();
                submit();
            }
            case ESCAPE -> {
                e.consume();
                revert();
                if (onEscape != null) {
                    onEscape.run();
                }
            }
            case BACK_SPACE -> {
                e.consume();
                deleteBeforeCursor();
            }
            case DELETE -> {
                e.consume();
                deleteAtCursor();
            }
            case LEFT -> {
                e.consume();
                moveCursor(-1);
            }
            case RIGHT -> {
                e.consume();
                moveCursor(1);
            }
            case HOME -> {
                e.consume();
                cursor = 0;
                refresh();
            }
            case END -> {
                e.consume();
                cursor = totalDigits();
                refresh();
            }
            case UP -> {
                e.consume();
                step(e.isShiftDown() ? 10 : 1);
            }
            case DOWN -> {
                e.consume();
                step(e.isShiftDown() ? -10 : -1);
            }
            case TAB -> {
                // 焦点系统处理
            }
            default -> {
                String text = e.getText();
                if (text != null && text.length() == 1 && Character.isDigit(text.charAt(0))) {
                    e.consume();
                    typeDigit(text.charAt(0) - '0');
                }
                // 其余按键不拦截（复制/粘贴等快捷键仍可用）；
                // 真正的文本插入由 KEY_TYPED 过滤器统一挡掉
            }
        }
    }

    /**
     * 在光标处写入一个数字。
     *
     * <p>语义由「光标在段内第几位」唯一决定（见类注释表格）：段首 = 整段替换，
     * 段中 = 补成两位，段满 = 左移一位后进入。任何一步都不查「已选中了没有」，
     * 因此行为可预判 —— 这也是上一版最难用的地方。</p>
     */
    private void typeDigit(int digit) {
        int seg = segAtCursor();
        // 段末边界属于右侧段，因此本段位次最多到 count（写满的段不允许再往后取一位）
        int slot = Math.min(slotAtCursor(seg), count[seg]);
        // 光标停在一段已写满的末尾：插入点后面的数字属于下一段，
        // 因此这一位要给下一段（07|:15 再敲 3 → 07:3，而不是把小时挤掉一位）
        if (count[seg] > 0 && slot >= segs[seg].width && seg < segs.length - 1) {
            seg++;
            slot = 0;
        }
        int width = segs[seg].width;
        int max = segs[seg].maxValue;

        if (slot == 0) {
            // 段首：整段替换 —— 值就是这个数字。敲 9 得 9 时，而不是先摆成 90 时再等人补第二位
            //（9x 时越界，等不来合法的第二位，只会让文本先跳一下）。
            // 四位年这类宽段例外：一位一位长出来（2 → 23 → 234 → 2345），不补零
            write(seg, digit);
            count[seg] = digit * 10 > max || width == 1 ? width : 1;
        } else {
            // 段中：新数字与其左侧数字组成新值，右侧数字被丢弃（插入点右边才是被替换的部分）。
            // 组成值越界（2、9 → 29 时）则该数字单独成立并整段替换。
            String left = rawDigits(seg).substring(0, Math.min(slot, count[seg]));
            int combined = Integer.parseInt(left + digit);
            write(seg, combined > max ? digit : combined);
            count[seg] = Math.min(width, slot + 1);
        }
        // 段满后光标自然落到下一段段首：cursorTo 落在边界上，而边界按定义属于右侧段
        cursor = cursorTo(seg, count[seg]);
        refresh();
    }

    /** 光标左移一位；已在段首则退回上一段的末位（把光标移回该段第 1 位）。 */
    private void moveCursor(int delta) {
        cursor = Math.max(0, Math.min(totalDigits(), cursor + delta));
        refresh();
    }

    /** 退格：删掉光标左边一个数字，光标随之左移。 */
    private void deleteBeforeCursor() {
        if (cursor <= 0) {
            return;
        }
        deleteDigitAt(cursor - 1);
        cursor--;
        refresh();
    }

    /** Delete：删掉光标处一个数字，光标不动。 */
    private void deleteAtCursor() {
        if (cursor >= totalDigits()) {
            return;
        }
        deleteDigitAt(cursor);
        refresh();
    }

    /** 把扁平数字序列第 {@code index} 位删掉，其余左移；整段被删空时其后各段一并清空。 */
    private void deleteDigitAt(int index) {
        StringBuilder sb = new StringBuilder(allDigits());
        sb.deleteCharAt(index);
        applyDigits(sb.toString());
    }

    /** 数字序列整体重排到各段（超出段宽则截断）。 */
    private void applyDigits(String digits) {
        Arrays.fill(count, 0);
        int pos = 0;
        for (int i = 0; i < segs.length && pos < digits.length(); i++) {
            int take = Math.min(segs[i].width, digits.length() - pos);
            // 优先取满一段，但末尾不足一段时保持原位（不要跨段偷位）
            count[i] = take;
            write(i, Integer.parseInt(digits.substring(pos, pos + take)));
            pos += take;
        }
    }

    /** 当前显示的全部数字（无分隔符）。 */
    private String allDigits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segs.length; i++) {
            sb.append(rawDigits(i));
        }
        return sb.toString();
    }

    /**
     * ↑/↓：光标所在段 ±{@code delta}，越界回绕。
     *
     * <p>段首若有空段先补零（{@code 0} 显示为 {@code 00}），否则秒有值、
     * 分却空着会渲染出洞。</p>
     */
    private void step(int delta) {
        int seg = segAtCursor();
        // 键盘语义是「对刚才录的那一段步进」：连按 ↑↑ 不该因为光标停在段末边界上
        // 就在第二步跳到下一段（09:30 按 ↑ 变 10:30，再按 ↓ 应该回到 09:30 而不是 10:29）。
        // 因此步进后把光标留在本段内（最后一个数字之前），而不是边界上。
        if (count[seg] == 0) {
            int left = seg - 1;
            while (left >= 0 && count[left] == 0) {
                left--;
            }
            if (left >= 0) {
                seg = left;
            }
        }
        for (int i = 0; i < seg; i++) {
            if (count[i] == 0) {
                write(i, 0);
                count[i] = segs[i].width;
            }
        }
        int span = segs[seg].maxValue + 1;
        int cur = modelValue(seg);
        write(seg, ((cur + delta) % span + span) % span);
        count[seg] = segs[seg].width;
        // 停在段内（最后一个数字之前）而不是段末边界 —— 边界按定义属于右侧段，
        // 停在边界上会让「同一段的连续步进」在第二步换段
        cursor = cursorTo(seg, Math.max(0, count[seg] - 1));
        refresh();
    }

    // ============================================================
    // 光标同步（鼠标点击 / 拖选）
    // ============================================================

    /** 鼠标按下那一轮结束后，把按下位置折算成光标（平台此时才把 caret 摆好）。 */
    private void applyPendingClick() {
        if (clickPending) {
            clickPending = false;
            syncCaretFromField();
        }
    }

    private void syncCaretFromField() {
        if (syncing) {
            return;
        }
        cursor = digitIndexFor(field.getCaretPosition());
    }

    /**
     * 供外部（已提交值回写、回退）调用：把文本重绘为某个值，光标回到第 1 段第 1 位。
     *
     * <p>回写后光标落在「直接敲数字即整段替换」的位置，用户接着录入不需要先按 Home。</p>
     */
    void display(LocalDateTime value) {
        applyValue(value);
        cursor = initialDigitIndex();
        refresh();
    }

    /**
     * 由外部值重绘，但<b>保持光标</b>。
     *
     * <p>面板（日历 / 时间面板）改动日期时间后走这条：文本要跟着更新，但用户手上
     * 正在录入的那一位不能被抽走。与 {@link #display} 的差别只有光标是否复位，
     * 因此「键盘录入」与「面板点选」不会互相把对方的位置踢掉。</p>
     */
    void adopt(LocalDateTime value) {
        applyValue(value);
        refresh();
    }

    /** 把模型整体设为某个值（各段位数取满）；{@code null} 清空。 */
    private void applyValue(LocalDateTime time) {
        Arrays.fill(count, 0);
        Arrays.fill(value, -1);
        if (time == null) {
            return;
        }
        for (int i = 0; i < segs.length; i++) {
            count[i] = segs[i].width;
            write(i, fieldValueOf(segs[i], time));
        }
    }

    /** 某段在给定时刻上的显示值（12 小时制的钟点段按 1-12）。 */
    private static int fieldValueOf(Seg seg, LocalDateTime time) {
        return switch (seg.kind) {
            case 'y' -> seg.width >= 3 ? time.getYear() : time.getYear() % 100;
            case 'M' -> time.getMonthValue();
            case 'd' -> time.getDayOfMonth();
            case 'H' -> seg.maxValue == 12 ? (time.getHour() % 12 == 0 ? 12 : time.getHour() % 12) : time.getHour();
            case 'm' -> time.getMinute();
            default -> time.getSecond();
        };
    }

    /** 重绘并落光标（唯一改文本的地方）。 */
    private void refresh() {
        normalizeCounts();
        String text = render();
        int caret = Math.min(displayIndexFor(Math.min(cursor, totalDigits())), text.length());
        syncing = true;
        try {
            field.setText(text);
            field.positionCaret(caret);
        } finally {
            syncing = false;
        }
        if (onPreview != null) {
            onPreview.accept(parse());
        }
    }

    /**
     * 收口各段位数并填掉中间的空段。
     *
     * <p>位数超过段宽只可能来自重排（粘贴/删除后整体左移），钳回段宽；
     * 不再把「未写满」一律补成满位 —— 那会让 4 位年敲到一半突然补零（{@code 23} → {@code 0023}），
     * 用户的下一位数字落点跟着变。</p>
     *
     * <p>中间空段按 0 补齐：只在「前面有内容、后面也有内容」时才补，
     * 末尾的空段保持空白（否则光敲一个小时就会带出 {@code :00}）。</p>
     */
    private void normalizeCounts() {
        int last = -1;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] > segs[i].width) {
                count[i] = segs[i].width;
            }
            if (count[i] > 0) {
                last = i;
            }
        }
        for (int i = 0; i < last; i++) {
            if (count[i] == 0) {
                write(i, 0);
                count[i] = segs[i].width;
            }
        }
    }

    /**
     * 字段里「已显示但还没提交」的编辑；没有这样的编辑时返回 {@code null}。
     *
     * <p>用于「面板开着时敲了数字、随后点面板确定」这条路径：此时用户既没按 Enter
     * 也没失焦，字段里是一次没提交过的编辑。调用方要优先采纳它，否则用户视角是
     * 「我明明输了 23:45，点确定却跳回 07:15」。</p>
     *
     * <p>字段与已提交值一致时必须返回 {@code null} —— 否则「面板里选了值、再点确定」
     * 这条完全正常的路径会被字段里那个还没重绘的旧值顶掉。</p>
     */
    LocalDateTime pendingEdit() {
        LocalDateTime shown = parse();
        LocalDateTime base = baseline.get();
        if (shown == null) {
            return null;
        }
        return shown.equals(base) ? null : shown;
    }

    /** 由当前显示拼出的时刻（缺段用基准值补，12 小时制按当前时段换算）。 */
    private LocalDateTime displayValue() {
        // 基准缺失时用「今天 00:00」，绝不能用分段探针那个时刻：
        // 探针的 15:45:57 会从空段里漏进提交值（敲一个 9:30 提交出 09:30:57）
        LocalDateTime base = baseline.get() != null
                ? baseline.get()
                : LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDate date = base.toLocalDate();
        LocalTime time = base.toLocalTime();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        int hour = time.getHour();
        int minute = time.getMinute();
        // 秒的缺省是「整分」而不是此刻的秒数：敲一个 9:30 不该带出 :57
        int second = totalDigits() > 0 ? 0 : time.getSecond();
        boolean twelveHour = false;
        for (int i = 0; i < segs.length; i++) {
            if (count[i] == 0) {
                // 没敲入的段沿用基准值（已经显示的段一律以显示为准，
                // 否则基准值会盖掉用户刚敲进去的数字）
                continue;
            }
            int v = segmentValue(i);
            switch (segs[i].kind) {
                case 'y' -> year = segs[i].width >= 3 ? v : 2000 + v % 100;
                case 'M' -> month = v;
                case 'd' -> day = v;
                case 'H' -> {
                    hour = v;
                    twelveHour = segs[i].maxValue == 12;
                }
                case 'm' -> minute = v;
                default -> second = v;
            }
        }
        if (twelveHour) {
            hour = Math.min(hour, 12) % 12 + meridiemIndex.getAsInt() * 12;
        }
        return LocalDateTime.of(safeDate(year, month, day), safeTime(hour, minute, second));
    }

    /** 组装日期：月份/日期越界（如 2 月 31 日）即时收敛到合法值，而不是抛异常。 */
    private static LocalDate safeDate(int year, int month, int day) {
        int y = Math.max(1, Math.min(9999, year));
        int m = Math.max(1, Math.min(12, month));
        YearMonth ym = YearMonth.of(y, m);
        return ym.atDay(Math.max(1, Math.min(ym.lengthOfMonth(), day)));
    }

    private static LocalTime safeTime(int hour, int minute, int second) {
        try {
            return LocalTime.of(hour, minute, second);
        } catch (RuntimeException ex) {
            return LocalTime.MIDNIGHT;
        }
    }

    /**
     * 把剪贴板文本按<b>数字逐位</b>写入当前光标处。
     *
     * <p>{@code 2026-10-23 23:45:00} / {@code 2345} 都能贴：只取数字，从光标处按位覆盖，
     * 多余部分丢弃。不做「整串顶替」是有意的 —— 剪贴板里混着别的文本时，
     * 整串顶替会把已提交值一起抹掉。</p>
     *
     * <p>一个数字都没有（纯文本）时原样不动，不产生任何副作用。</p>
     */
    void pasteDigits(String raw) {
        if (raw == null) {
            return;
        }
        String digits = digitsOf(raw);
        if (digits.isEmpty()) {
            return;
        }
        // 空段先补一个 0 占位：光标落在空段时几何量还没有依据
        if (totalDigits() == 0) {
            write(0, 0);
            count[0] = 1;
            cursor = 0;
        }
        for (int i = 0; i < digits.length(); i++) {
            typeDigit(digits.charAt(i) - '0');
        }
    }

    // ============================================================
    // 提交 / 回退
    // ============================================================

    private void submit() {
        LocalDateTime parsed = parse();
        if (parsed == null || totalDigits() == 0) {
            revert();
            return;
        }
        int keep = cursor;
        // 先让宿主更新状态（面板联动 / 提交回写），掩码随后落地
        if (preCommit != null) {
            preCommit.accept(parsed);
        }
        if (postCommit != null) {
            postCommit.accept(parsed);
        }
        // 提交后控件会回写值（adopt 会按值重绘）。用户按 Enter 只是确认，
        // 不该让插入点跳走 —— 否则「继续微调」每次都得重新定位。
        adopt(parsed);
        cursor = Math.min(keep, totalDigits());
        refresh();
    }

    private void commitOrRevert() {
        LocalDateTime parsed = parse();
        LocalDateTime current = baseline.get();
        if (parsed == null || totalDigits() == 0 || parsed.equals(current)) {
            revert();
        } else {
            submit();
        }
    }

    private void revert() {
        display(baseline.get());
    }

    /** 取消录入：丢弃未提交的编辑，文本回到已提交值（弹层关闭时调用）。 */
    void cancel() {
        revert();
    }

    /**
     * 解析当前显示（缺段用基准值补，段值越界返回 {@code null}）。
     *
     * <p>直接读<b>渲染出来的数字</b>：显示值即提交值，不再维护第二份真相，
     * 因此「框里看着是 25 时、提交出来却是别的」这种分叉在结构上不可能发生。</p>
     *
     * <p>空字段返回 {@code null}：不把「什么都没敲」当成午夜提交，
     * 否则清空后失焦会静默产生一个虚构值。</p>
     */
    LocalDateTime parse() {
        for (int i = 0; i < segs.length; i++) {
            if (count[i] > 0 && read(i) > segs[i].maxValue) {
                return null;
            }
            // 年必须四位齐全：把「刚敲了一位 2」当成公元 2 年提交，是比拒绝更糟的结果
            if (segs[i].kind == 'y' && count[i] > 0 && count[i] < segs[i].width) {
                return null;
            }
        }
        return totalDigits() > 0 ? displayValue() : null;
    }

    // ============================================================
    // 工具
    // ============================================================

    static String digitsOf(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String pad(int v, int width) {
        if (width <= 0) {
            return "";
        }
        String s = Integer.toString(Math.max(0, v));
        if (s.length() >= width) {
            return s.substring(s.length() - width);
        }
        return "0".repeat(width - s.length()) + s;
    }
}
