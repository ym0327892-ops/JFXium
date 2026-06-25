package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 提及组件（M19.50 重构）— 包装 JavaFX {@link TextArea} + {@link Popup}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：输入时 @ 提及用户/对象的文本框，输入触发符（默认 {@code @}）后弹出候选列表，
 * 选中后自动插入。对标 Ant Design Mentions。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>自定义触发符（默认 {@code @}，可改为 {@code #} / {@code $} 等）</li>
 *   <li>候选项配置（value + label）</li>
 *   <li>选中回调（{@link #onSelect(Consumer)}）+ 文本变化回调（{@link #onChange(Consumer)}）</li>
 *   <li>自定义占位文本 + 行数</li>
 *   <li>所有视觉样式走 LESS（{@link JfxStyles#MENTIONS} + {@link JfxStyles#POPUP_MENU}）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>评论框 @ 用户</li>
 *   <li>任务分配 @ 负责人</li>
 *   <li>聊天输入 @ 群成员</li>
 *   <li>代码注释 # 标签引用</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * TextArea mentions = MentionsAnt.create()
 *     .placeholder("输入 @ 提及用户...")
 *     .prefix("@")
 *     .option("zhangsan", "张三")
 *     .option("lisi", "李四")
 *     .option("wangwu", "王五")
 *     .onSelect(value -> System.out.println("选中了：" + value))
 *     .rows(4)
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class CommentInput extends MentionsAnt {
 *     public CommentInput() {
 *         placeholder("输入 @ 提及用户...");
 *         prefix("@");
 *         option("zhangsan", "张三");
 *         option("lisi", "李四");
 *         onSelect(value -> notifyMention(value));
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link TextArea} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class MentionsAnt extends TextArea
        implements LayoutCommon<MentionsAnt>, DisabledSupport<MentionsAnt> {

    /** 默认行数。 */
    private static final int DEFAULT_ROWS = 4;
    /** 候选项面板宽度。 */
    private static final double OPTIONS_PANEL_WIDTH = 200;
    /** Popup 与输入框的垂直间距。 */
    private static final double POPUP_OFFSET_Y = 4;

    /** 候选菜单项（value + label 配对）。 */
    public static class Option {
        private final String value;
        private final String label;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
    }

    /** 默认触发符。 */
    private static final String DEFAULT_PREFIX = "@";

    private final List<Option> options = new ArrayList<>();
    private String prefix = DEFAULT_PREFIX;
    private Consumer<String> onSelect;
    private Consumer<String> onChange;
    private String placeholder;
    private StringProperty bindProperty;

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（默认 4 行，默认触发符 @）。 */
    public static MentionsAnt create() {
        return new MentionsAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务继承）
    // ============================================================

    public MentionsAnt() {
        super();
        init();
    }

    private void init() {
        getStyleClass().addAll(JfxStyles.MENTIONS, JfxStyles.MENTIONS_AREA);
        setPrefRowCount(DEFAULT_ROWS);

        // 双向绑定（在初始值设置之后）
        if (bindProperty != null) {
            Bindings.bindBidirectional(textProperty(), bindProperty);
        }

        // 构造 Popup（延迟到场景可用时再展示）
        Popup popup = new Popup();
        popup.setAutoHide(true);

        // Popup 有独立 Scene，不继承宿主节点的主题样式表。
        // 显示时把宿主 Scene 的 stylesheets 注入 Popup Scene，确保暗色等主题下文字/背景颜色正确。
        popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing && popup.getScene() != null && getScene() != null) {
                popup.getScene().getStylesheets().setAll(getScene().getStylesheets());
            }
        });

        VBox optionsPanel = new VBox(0);
        optionsPanel.getStyleClass().add(JfxStyles.POPUP_MENU);
        optionsPanel.setPrefWidth(OPTIONS_PANEL_WIDTH);
        popup.getContent().add(optionsPanel);

        textProperty().addListener((obs, oldVal, newVal) -> {
            if (onChange != null) {
                onChange.accept(newVal);
            }

            if (newVal.endsWith(prefix)) {
                optionsPanel.getChildren().clear();
                for (Option option : options) {
                    HBox row = new HBox();
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.getStyleClass().add(JfxStyles.POPUP_MENU_ITEM);
                    LabelAnt label = LabelAnt.create(option.getLabel());
                    row.getChildren().add(label);
                    // hover 由 LESS 控制
                    row.setOnMouseClicked(e -> {
                        String currentText = getText();
                        String newText = currentText.substring(0, currentText.length() - prefix.length())
                                + prefix + option.getValue() + " ";
                        setText(newText);
                        positionCaret(newText.length());
                        popup.hide();
                        if (onSelect != null) {
                            onSelect.accept(option.getValue());
                        }
                    });
                    optionsPanel.getChildren().add(row);
                }

                if (!options.isEmpty()) {
                    Bounds bounds = localToScreen(getBoundsInLocal());
                    popup.show(this, bounds.getMinX(), bounds.getMaxY() + POPUP_OFFSET_Y);
                }
            } else if (!newVal.contains(prefix)) {
                popup.hide();
            }
        });
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /**
     * 添加候选菜单项。
     */
    public MentionsAnt option(String value, String label) {
        if (value != null) {
            options.add(new Option(value, TextUtils.safeText(label, value)));
        }
        return this;
    }

    /**
     * 批量设置候选菜单项。
     */
    public MentionsAnt options(List<Option> options) {
        if (options != null) {
            this.options.clear();
            this.options.addAll(options);
        }
        return this;
    }

    /**
     * 设置触发符（默认 @）。
     */
    public MentionsAnt prefix(String prefix) {
        this.prefix = TextUtils.safeText(prefix, DEFAULT_PREFIX);
        return this;
    }

    /**
     * 设置占位文本。null 安全（null 视为空串）。
     */
    public MentionsAnt placeholder(String text) {
        this.placeholder = text;
        setPromptText(TextUtils.safeText(text));
        return this;
    }

    /**
     * 设置行数。
     */
    public MentionsAnt rows(int rows) {
        setPrefRowCount(Math.max(1, rows));
        return this;
    }

    /**
     * 选中回调（用户从候选菜单中选择了某项后触发）。
     */
    public MentionsAnt onSelect(Consumer<String> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    /**
     * 文本变化回调。
     */
    public MentionsAnt onChange(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /**
     * 双向绑定：控件值 ↔ Property 值实时同步。
     */
    public MentionsAnt bindValue(StringProperty property) {
        this.bindProperty = property;
        if (property != null) {
            Bindings.bindBidirectional(textProperty(), property);
        }
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 构建
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>MentionsAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public MentionsAnt build() {
        return this;
    }
}