package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * JFXium 日历组件 —— 对标 Ant Design Calendar。
 *
 * <h2>月视图 = 原生 DatePicker 面板（唯一实现）</h2>
 * <p>月视图**不再自绘日期网格**，而是通过 JavaFX 公开 API
 * {@link DatePickerSkin#getPopupContent()} 取得原生 {@code date-picker-popup} 面板并内嵌。
 * 好处是与 {@link org.openkawu.jfxium.component.control.DatePickerAnt}、
 * {@link DateTimePickerAnt} 共用同一套面板实现 ——
 * 本地化星期（{@code 周一…周日}，随 {@link Locale}）、modena 的月/年 ±1 spinner、
 * 今日/选中/非本月配色、键盘导航全部一致，且永久不会再各写一套而漂移。</p>
 *
 * <h2>年视图（YEAR）</h2>
 * <p>JavaFX 21 的原生面板**没有**月/年选择面板（{@code month-year-pane} 里的月份、年份标签
 * 是静态文本，点击无效果），因此「年视图」无从沿用原生实现 —— 按约定保**样式一致**：
 * 自绘的月份按钮沿用相同的色板 / 尺寸 token（{@code @calendar-row-height} 等），
 * 只保留为「快速跳月」的便捷视图，不宣称与原生面板像素等同。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox calendar = CalendarAnt.create()
 *     .value(LocalDate.now())
 *     .onSelect(date -> System.out.println("选中：" + date))
 *     .build();
 * }</pre>
 */
public class CalendarAnt {
    private static final String CONTROLLER_KEY = CalendarAnt.class.getName() + ".controller";

    /** 内嵌原生面板时挂的 styleClass：用于压掉其自带边框/圆角/阴影（避免与外框叠成双框） */
    private static final String EMBEDDED = "jfx-calendar-embedded";
    private static final String NATIVE_POPUP = "date-picker-popup";

    public enum Mode {
        MONTH, YEAR
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private LocalDate value = LocalDate.now();
        private LocalDate selectedDate = null;
        private Mode mode = Mode.MONTH;
        private Consumer<LocalDate> onSelect = null;
        private Consumer<LocalDate> onPanelChange = null;
        private boolean fullscreen = true;

        // 月视图由原生面板承载：DatePicker 只作为「面板宿主 + 状态载体」，不进入场景树
        private DatePicker host;
        private Node nativePanel;
        private VBox root;
        /** 原生面板当前的显示月份：由 spinner 事件回填，供 getValue() / onPanelChange 用 */
        private YearMonth shownMonth;
        /** 程序化写值（Controller / 年视图跳月）期间抑制 onSelect —— 只有用户点日期才该回调 */
        private boolean suppressSelect;

        public Builder value(LocalDate value) { this.value = value != null ? value : LocalDate.now(); return this; }
        public Builder selectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; return this; }
        public Builder mode(Mode mode) { this.mode = mode != null ? mode : Mode.MONTH; return this; }
        public Builder onSelect(Consumer<LocalDate> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onPanelChange(Consumer<LocalDate> onPanelChange) { this.onPanelChange = onPanelChange; return this; }
        public Builder fullscreen(boolean fullscreen) { this.fullscreen = fullscreen; return this; }

        public VBox build() {
            VBox calendar = new VBox(0);
            root = calendar;
            calendar.getStyleClass().add(JfxStyles.CALENDAR);

            if (fullscreen) {
                HBox.setHgrow(calendar, Priority.ALWAYS);
                VBox.setVgrow(calendar, Priority.ALWAYS);
            }

            // 原生面板只建一次：切换视图时只替换场景树里的子节点，悬挂在 CalendarAnt 上的
            // listener（valueProperty / spinner 事件）不会随重建反复叠加。
            nativePanel = createNativePanel();

            rebuild();
            calendar.getProperties().put(CONTROLLER_KEY, new Controller(this));
            applyStyles(calendar);
            return calendar;
        }

        /**
         * 通过公开 API 取得原生日期面板并接管其状态。
         *
         * <p>注意：{@code DatePickerSkin.getPopupContent()} 是 JavaFX 公开签名（自 8u40 起），
         * 返回节点类型未公开但节点本身是普通 {@code Node}，挂进场景树是受支持的用法。</p>
         */
        private Node createNativePanel() {
            host = new DatePicker(value);
            if (selectedDate != null) {
                host.setValue(selectedDate);
            }
            Node content = new DatePickerSkin(host).getPopupContent();
            content.getStyleClass().add(EMBEDDED);
            shownMonth = YearMonth.from(host.getValue() != null ? host.getValue() : value);

            // 选中日期 → 上报（原生面板点选唯一会改 valueProperty 的路径）
            host.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    return;
                }
                selectedDate = newValue;
                value = newValue;
                if (onSelect != null && !suppressSelect) {
                    onSelect.accept(newValue);
                }
            });

            // 导航（月/年 spinner 的 ±1）：只改「显示月份」，不改 value —— 用事件过滤器捕捉，
            // 使 onPanelChange / getValue 跟随真实显示的月份，而不是停留在选中日期。
            content.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
                if (e.getTarget() instanceof Button) {
                    YearMonth before = shownMonth;
                    // 事件分发完成后原生面板才刷新，故延后一拍读取新月份
                    javafx.application.Platform.runLater(() -> {
                        YearMonth after = readShownMonth();
                        if (after != null && !after.equals(before)) {
                            shownMonth = after;
                            if (onPanelChange != null) {
                                onPanelChange.accept(after.atDay(1));
                            }
                        }
                    });
                }
            });
            return content;
        }

        /** 从原生面板的月份标签反读当前显示月份；解析失败时退回已知值 */
        private YearMonth readShownMonth() {
            if (nativePanel == null) {
                return shownMonth;
            }
            String monthText = null;
            String yearText = null;
            int i = 0;
            for (Node n : nativePanel.lookupAll(".spinner .label")) {
                String t = ((Label) n).getText();
                if (i == 0) monthText = t;
                else if (i == 1) yearText = t;
                i++;
            }
            if (yearText == null) {
                // 只有月份标签可读时（无年份 spinner）保持当前年份
                return shownMonth == null ? null
                        : YearMonth.of(shownMonth.getYear(), shownMonth.getMonth());
            }
            try {
                int year = Integer.parseInt(yearText.replaceAll("\\D", ""));
                java.time.Month month = parseMonth(monthText, year);
                return month == null ? shownMonth : YearMonth.of(year, month);
            } catch (NumberFormatException ex) {
                return shownMonth;
            }
        }

        /** 程序化把原生面板拨到某日期：不触发 onSelect（那是用户点日期的语义） */
        private void setHostDateQuietly(LocalDate date) {
            if (host == null || date == null) {
                return;
            }
            suppressSelect = true;
            try {
                host.setValue(date);
            } finally {
                suppressSelect = false;
            }
        }

        /** 按当前 Locale 的长/短月份名反解月份序号 */
        private static java.time.Month parseMonth(String text, int year) {
            if (text == null) {
                return null;
            }
            String t = text.trim();
            for (java.time.Month m : java.time.Month.values()) {
                if (m.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()).equals(t)
                        || m.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).equals(t)) {
                    return m;
                }
            }
            // 退路：按固定顺序的月份名表匹配（原生面板在某些 Locale 下会加后缀）
            for (java.time.Month m : java.time.Month.values()) {
                String full = m.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
                if (!full.isEmpty() && t.startsWith(full.substring(0, Math.min(1, full.length())))) {
                    return m;
                }
            }
            return null;
        }

        private void rebuild() {
            if (root == null) {
                return;
            }
            if (mode == Mode.YEAR) {
                root.getChildren().setAll(buildYearView());
                return;
            }
            // 原生面板是固定栅格（7 列）：非全屏时不要用 VBox 把子节点拉满宽度
            // （否则行被拉宽、日期与星期标签间距失衡）。VBox 的 pref 宽本就等于子节点
            // pref 宽，故只要关掉 fillWidth 即可「跟内容走」——**不要**自己算 prefWidth，
            // rebuild 可能发生在入场景（CSS 落地）之前，测到的宽是未套样式的假值。
            root.setFillWidth(fullscreen);
            root.getChildren().setAll(nativePanel);
        }

        /**
         * 年视图：JavaFX 原生面板没有月/年选择面板（见类注释），按「样式一致」自绘。
         * 3 列 × 4 行，列宽取 {@code @calendar-month-btn-width}（= 7 × 格宽 ÷ 3），
         * 与日期网格同宽，切视图时不横向跳动。
         */
        private VBox buildYearView() {
            VBox yearView = new VBox();
            yearView.getStyleClass().add(JfxStyles.CALENDAR_YEAR_VIEW);

            HBox nav = new HBox();
            nav.setAlignment(javafx.geometry.Pos.CENTER);
            nav.getStyleClass().add(JfxStyles.CALENDAR_HEADER);
            Button prevBtn = createNavButton("<");
            prevBtn.setOnAction(e -> shiftYear(-1));
            Button nextBtn = createNavButton(">");
            nextBtn.setOnAction(e -> shiftYear(1));
            Label yearLabel = new Label(String.valueOf(value.getYear()));
            yearLabel.getStyleClass().add(JfxStyles.CALENDAR_HEADER_LABEL);
            nav.getChildren().addAll(prevBtn, yearLabel, nextBtn);

            GridPane grid = new GridPane();
            grid.getStyleClass().add(JfxStyles.CALENDAR_GRID);
            // 间距/内边距交给 CSS（与月视图的原生网格同源），此处只定比例：
            // 3 列等宽 + 4 行等高，行再 vgrow 撑满 @calendar-grid-height，
            // 使年视图与月视图的整块日历**同宽同高**，切换不跳（见 CSS token 注释）。
            for (int i = 0; i < 3; i++) {
                javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
                col.setPercentWidth(100.0 / 3.0);
                col.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(col);
            }
            for (int r = 0; r < 4; r++) {
                javafx.scene.layout.RowConstraints row = new javafx.scene.layout.RowConstraints();
                row.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(row);
            }

            String[] months = monthNames();
            for (int i = 0; i < 12; i++) {
                int month = i + 1;
                Button monthBtn = new Button(months[i]);
                monthBtn.getStyleClass().add(JfxStyles.CALENDAR_MONTH_BTN);
                if (value.getMonthValue() == month) {
                    monthBtn.getStyleClass().add(JfxStyles.CALENDAR_MONTH_BTN_CURRENT);
                }
                monthBtn.setOnAction(e -> {
                    value = value.withMonth(month);
                    mode = Mode.MONTH;
                    setHostDateQuietly(value);
                    rebuild();
                    if (onPanelChange != null) {
                        onPanelChange.accept(value);
                    }
                });
                grid.add(monthBtn, i % 3, i / 3);
            }
            yearView.getChildren().addAll(nav, grid);
            return yearView;
        }

        /** 月份名随 Locale（默认 zh_CN → 一月…十二月），与原生面板一致 */
        private static String[] monthNames() {
            java.time.Month[] all = java.time.Month.values();
            String[] names = new String[all.length];
            for (int i = 0; i < all.length; i++) {
                names[i] = all[i].getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault());
            }
            return names;
        }

        private void shiftYear(int delta) {
            value = value.plusYears(delta);
            rebuild();
            if (onPanelChange != null) {
                onPanelChange.accept(value);
            }
        }

        /** 年视图导航按钮：视觉与 hover 由 LESS .jfx-calendar-nav-btn 控制 */
        private Button createNavButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(JfxStyles.CALENDAR_NAV_BTN);
            return btn;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Controller controllerOf(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("CalendarAnt.controllerOf(node) 的 node 不能为 null");
        }
        Object controller = node.getProperties().get(CONTROLLER_KEY);
        if (controller instanceof Controller calendarController) {
            return calendarController;
        }
        throw new IllegalArgumentException("node 不是 CalendarAnt.build() 返回的日历组件");
    }

    public static class Controller {
        private final Builder builder;

        private Controller(Builder builder) {
            this.builder = builder;
        }

        /** 当前显示 / 选中的日期 */
        public LocalDate getValue() {
            return builder.value;
        }

        public void setValue(LocalDate value) {
            builder.value = value != null ? value : LocalDate.now();
            // 无论当前是不是月视图都同步宿主：切回月视图时面板已经是正确月份
            builder.setHostDateQuietly(builder.value);
            builder.rebuild();
        }

        public LocalDate getSelectedDate() {
            return builder.selectedDate;
        }

        public void setSelectedDate(LocalDate selectedDate) {
            builder.selectedDate = selectedDate;
            builder.value = selectedDate != null ? selectedDate : builder.value;
            builder.setHostDateQuietly(selectedDate);
            builder.rebuild();
        }

        public Mode getMode() {
            return builder.mode;
        }

        public void setMode(Mode mode) {
            builder.mode = mode != null ? mode : Mode.MONTH;
            builder.rebuild();
        }

        /** 原生面板当前显示到的月份（与选中日期可不同，如翻页后未点选） */
        public YearMonth getShownMonth() {
            return builder.mode == Mode.MONTH ? builder.shownMonth : YearMonth.from(builder.value);
        }
    }
}
