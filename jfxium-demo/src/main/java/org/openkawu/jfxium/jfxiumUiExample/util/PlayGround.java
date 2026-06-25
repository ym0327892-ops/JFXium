package org.openkawu.jfxium.jfxiumUiExample.util;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.FlowPaneAnt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * PlayGround 交互演示区工具（M19.PlayGround 引入）。
 *
 * <p><b>定位</b>：把「控制区 + 展示区」两栏布局封装成统一视觉，
 * 让 B 类示例页从「陈列式静态展示」升级为「可实时调控的交互演示」。</p>
 *
 * <p><b>整体结构</b>：</p>
 * <pre>
 *   ┌ GroupBox（带边框标题） ─────────────────────────────────┐
 *   │ Title + description                                      │
 *   │ ┌ Control (240px 定宽) ─┬ Display（填充剩余）─────────┐ │
 *   │ │  控件1                 │                              │ │
 *   │ │  控件2                 │     [实时反映控制结果的组件]  │ │
 *   │ │  ...                   │                              │ │
 *   │ └────────────────────────┴──────────────────────────────┘ │
 *   └─────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h2>三种实时更新策略</h2>
 * <p>根据目标组件的能力选择最适合的策略：</p>
 * <ol>
 *   <li><b>rebuild</b> —— 组件无 Controller（如 {@link org.openkawu.jfxium.component.layout.DividerAnt}）：
 *       控制区回调里重新调用 {@code build()}，用 {@link #replaceDisplay} 替换展示区节点。
 *       <b>推荐用</b> {@link #rebindRebuild} —— 自动处理「鸡生蛋」问题。</li>
 *   <li><b>modify+apply</b> —— 组件暴露 ModifyBuilder（如 {@link org.openkawu.jfxium.component.composite.TagAnt}）：
 *       控制区回调里调用 {@code modify(node).xxx().apply()}，原地修改无重建。
 *       <b>推荐用</b> {@link #rebindModify}。</li>
 *   <li><b>controller</b> —— 组件暴露 Controller（如 {@link SegmentedAnt}/{@link org.openkawu.jfxium.component.composite.ProgressAnt}）：
 *       控制区回调里调用 {@code controller.setXxx(...)}，原地更新状态。
 *       当前暂无对应 rebind 入口，需要时再用低级 API（{@link #create()}）手动拼接。</li>
 * </ol>
 *
 * <h2>声明式 API：Binder + Row + rebind</h2>
 * <p>为了消除样板代码（手动维护 {@code current[]} 状态数组 + 手动写 onChange lambda +
 * 手动处理 scaffold build 拿 display 引用），提供三件套：</p>
 * <ul>
 *   <li><b>Binder&lt;T&gt;</b> —— 单值状态盒子，控件只负责写它，display 重建只负责读它。</li>
 *   <li><b>Row</b> —— 控制行声明（label + control）。</li>
 *   <li><b>rebindRebuild / rebindModify</b> —— 入口，串联 binder + display 工厂 + 控制行。</li>
 * </ul>
 *
 * <p>典型用法（rebuild 模式，Divider）：</p>
 * <pre>{@code
 * // 1. 准备状态盒子
 * Binder<String> orientation = PlayGround.binder("horizontal");
 * Binder<String> position    = PlayGround.binder("center");
 *
 * // 2. 调 rebindRebuild —— 内部自动处理 scaffold build + 监听注册 + 二次 build
 * Node playground = PlayGround.rebindRebuild(
 *         () -> DividerAnt.create()
 *                 .orientation("vertical".equals(orientation.get()) ? VERTICAL : HORIZONTAL)
 *                 .position(parsePosition(position.get()))
 *                 .build(),
 *         "方向 / 位置",
 *         PlayGround.row("方向", PlayGround.segmented(orientation,
 *                 PlayGround.entry("horizontal", "水平"),
 *                 PlayGround.entry("vertical",   "垂直"))),
 *         PlayGround.row("位置", PlayGround.segmented(position,
 *                 PlayGround.entry("left",   "靠左"),
 *                 PlayGround.entry("center", "居中"),
 *                 PlayGround.entry("right",  "靠右"))));
 * }</pre>
 *
 * <p>典型用法（modify 模式，Tag）：</p>
 * <pre>{@code
 * HBox tag = TagAnt.create("Tag").build();
 * Binder<String> type = PlayGround.binder("default");
 *
 * Node playground = PlayGround.rebindModify(tag,
 *         () -> TagAnt.modify(tag).type(parseType(type.get())).apply(),
 *         "类型",
 *         PlayGround.row("类型", PlayGround.segmented(type,
 *                 PlayGround.entry("default", "Default"),
 *                 PlayGround.entry("primary", "Primary"))));
 * }</pre>
 *
 * <h2>低级 API：Builder + segmented(onChange) + displayOf + replaceDisplay</h2>
 * <p>遇到 rebind 不能处理的复杂场景（如自定义 Node 联动、TextField 文本输入框等），
 * 可退回 {@link #create()} + {@link #segmented(String, Consumer, Entry...)} +
 * {@link #replaceDisplay} 手动拼接 —— 此时样板代码会再次出现，但能力完全开放。</p>
 */
public final class PlayGround {

    /** Playground 节点 properties 上的 display 容器 key（用于回调中反向取 display）。 */
    private static final String DISPLAY_KEY = "jfxium.demo.playground.display";
    /** 控制控件 properties 上的 binder 引用 key（rebind 入口通过这个 key 提取 binder 列表）。 */
    private static final String BINDER_KEY = "jfxium.demo.playground.binder";

    private PlayGround() {}

    // ============================================================
    // 状态盒子 Binder<T>
    // ============================================================

    /**
     * 单值状态盒子 —— Playground 控制区「当前选中值」的统一持有器。
     *
     * <p>把「控件 onChange → 触发 display 重建/修改」拆成三段：</p>
     * <ul>
     *   <li>控件只负责：写 binder；</li>
     *   <li>binder 只负责：通知值变更监听器；</li>
     *   <li>监听器只负责：调用 factory / apply 让 display 跟随变化。</li>
     * </ul>
     * 三方解耦，样板代码消失。
     *
     * <p><b>使用约束</b>：Binder 实例必须在 PlayGround.rebindRebuild / rebindModify 调用
     * 之前创建（因为 rebind 需要遍历 control 控件 properties 提取 binder，控件创建时
     * 会把 binder 挂到 properties）。</p>
     */
    public static final class Binder<T> {
        private final List<Runnable> listeners = new ArrayList<>();
        private T value;

        Binder(T initial) {
            this.value = initial;
        }

        /** 当前值。 */
        public T get() { return value; }

        /** 写入新值；值发生变化时才通知监听器（equals 比较）。 */
        public void set(T v) {
            if (!Objects.equals(value, v)) {
                value = v;
                for (Runnable l : listeners) {
                    l.run();
                }
            }
        }

        /** rebindRebuild / rebindModify 内部用 —— 注册「值变更」监听器。 */
        void addListener(Runnable l) {
            listeners.add(l);
        }
    }

    /**
     * 创建 String 类型状态盒子（最常用：所有 Segmented 选项都是 String）。
     *
     * <p>String 化所有状态值，包括 Boolean（如 "on" / "off"）和 Enum（用 enum.name()）。
     * 这是为了统一「display 工厂 / apply 函数」里的取值写法 —— 不必为 boolean / enum
     * 各自建一个 Binder。</p>
     */
    public static Binder<String> binder(String initial) {
        return new Binder<>(initial);
    }

    // ============================================================
    // 控制行 Row
    // ============================================================

    /** 控制区一行 = (label + control 控件)。用于 {@link #rebindRebuild} / {@link #rebindModify}。 */
    public record Row(String label, Node control) {
        public static Row of(String label, Node control) {
            return new Row(label, control);
        }
    }

    /** 创建控制行。 */
    public static Row row(String label, Node control) {
        return new Row(label, control);
    }

    // ============================================================
    // 容器构造（低级 API）
    // ============================================================

    /** 创建一个 Playground 两栏布局构造器（低级 API；复杂场景才用得到）。 */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Playground 流式构造器（低级 API）。
     *
     * <p>build() 前必须调用一次 {@link #display(Node)} 设置展示区初始节点；
     * 控制区可多次调用 {@link #controlRow(String, Node)} / {@link #controlSection(String)} /
     * {@link #control(Node...)} 累积节点。</p>
     *
     * <p><b>推荐使用</b> {@link #rebindRebuild} / {@link #rebindModify} 替代，
     * 它们内部封装了 Builder + scaffold build + binder 监听注册的完整流程。</p>
     */
    public static final class Builder {
        private final List<Node> controlNodes = new ArrayList<>();
        private Node displayNode;
        private boolean compact = false;

        private Builder() {}

        // ---------- 控制区 ----------

        /**
         * 控制区单行：标签 + 控件。
         *
         * <p>标签走 {@code .jfx-demo-playground-row-label}，整行走 {@code .jfx-demo-playground-row}，
         * 与 CSS 中的 8px 间距、center-left 对齐联动。</p>
         *
         * <p><b>外层用 FlowPaneAnt 而非 HBox</b>（M19.PlayGround 决策）：
         * 当控件（如 7 选项 Segmented）总宽超过控制区可用宽度时，
         * 标签会自动换到下一行，让控件独占一行展示完整内容，
         * 避免 HBox 压缩导致的「文字截断/换行错乱」。</p>
         */
        public Builder controlRow(String label, Node control) {
            if (control == null) return this;
            Label labelNode = TypographyAnt.text(label != null ? label : "").build();
            labelNode.getStyleClass().add("jfx-demo-playground-row-label");
            // FlowPaneAnt：水平 flow + 自动 wrap；label/control 总宽超限时自动分两行
            FlowPaneAnt row = FlowPaneAnt.create()
                    .hgap(8)
                    .vgap(4)
                    .align(Pos.CENTER_LEFT);
            row.getStyleClass().add("jfx-demo-playground-row");
            row.getChildren().addAll(labelNode, control);
            controlNodes.add(row);
            return this;
        }

        /** 控制区分组小标题（弱化 11px 灰色字体，用于把多个 controlRow 分组）。 */
        public Builder controlSection(String label) {
            if (label == null || label.isBlank()) return this;
            Label sectionLabel = TypographyAnt.text(label).build();
            sectionLabel.getStyleClass().add("jfx-demo-playground-section-label");
            controlNodes.add(sectionLabel);
            return this;
        }

        /** 自由添加控制区节点（绕过 controlRow 包装；适合放自定义复杂节点）。 */
        public Builder control(Node... nodes) {
            if (nodes != null) {
                for (Node n : nodes) {
                    if (n != null) controlNodes.add(n);
                }
            }
            return this;
        }

        // ---------- 展示区 ----------

        /**
         * 设置展示区初始节点。
         *
         * <p>build() 前必须调用一次。如果后续要使用 {@link #replaceDisplay} 做实时替换，
         * 旧节点会被 GC，无需手动释放（JavaFX 单 parent 规则自动保证）。</p>
         */
        public Builder display(Node initial) {
            if (initial == null) {
                throw new IllegalArgumentException("display 节点不能为 null");
            }
            this.displayNode = initial;
            return this;
        }

        // ---------- 模式 ----------

        /** 紧凑模式：控制区内边距/间距更小，适合组件尺寸本身就很小的页面。 */
        public Builder compact() {
            this.compact = true;
            return this;
        }

        // ---------- 构建 ----------

        /**
         * 构建 Playground 节点（直接挂在 PageTemplate.sections(...) 里即可，
         * 内部已包好 VBox 容器，无需外层再包 GroupBox）。
         */
        public Node build() {
            if (displayNode == null) {
                throw new IllegalStateException(
                        "PlayGround.build() 前必须调用 .display(node) 设置展示区初始节点");
            }

            // 控制区 VBox
            VBox controlBox = VBarAnt.create()
                    .compact()
                    .gap(12)
                    .top(controlNodes.toArray(Node[]::new))
                    .build();
            controlBox.getStyleClass().add("jfx-demo-playground-control");

            // 展示区 StackPane（保留引用以便回调里 replaceDisplay）
            StackPane displayBox = new StackPane();
            displayBox.getStyleClass().add("jfx-demo-playground-display");
            displayBox.setAlignment(Pos.CENTER);
            displayBox.getChildren().add(displayNode);

            // 整体两栏 HBox
            HBox twoCols = new HBox(0);
            twoCols.setAlignment(Pos.TOP_LEFT);
            twoCols.getStyleClass().add("jfx-demo-playground");
            if (compact) twoCols.getStyleClass().add("jfx-demo-playground-compact");
            HBox.setHgrow(displayBox, Priority.ALWAYS);
            twoCols.getChildren().addAll(controlBox, displayBox);

            // 把 display 容器引用挂到 properties，便于回调中通过 displayOf(...) 反查
            twoCols.getProperties().put(DISPLAY_KEY, displayBox);
            return twoCols;
        }
    }

    // ============================================================
    // 运行时辅助（低级 API）
    // ============================================================

    /**
     * 从已构建的 Playground 节点获取 display 容器。
     *
     * <p>build() 时已经把 displayBox 挂到 properties，这里提供反查入口，
     * 让外部回调不用捕获闭包变量也能拿到 display 容器。</p>
     *
     * @param playgroundNode {@link Builder#build()} 的返回值
     * @return display 容器（StackPane）；不是 PlayGround 节点时返回 null
     */
    public static StackPane displayOf(Node playgroundNode) {
        if (playgroundNode == null) return null;
        Object v = playgroundNode.getProperties().get(DISPLAY_KEY);
        return v instanceof StackPane sp ? sp : null;
    }

    /**
     * 替换展示区节点（rebuild 策略核心 API，低级）。
     *
     * <p>无 Controller 组件适用 —— 在控制区回调里调用 {@code build()} 生成新节点，
     * 然后通过此方法替换展示区。旧节点会被 JavaFX 自动 GC。</p>
     *
     * @param display  {@link #displayOf(Node)} 获取的 display 容器
     * @param newDisplay 新的展示区节点（不能为 null）
     */
    public static void replaceDisplay(StackPane display, Node newDisplay) {
        if (display == null || newDisplay == null) return;
        display.getChildren().setAll(newDisplay);
    }

    // ============================================================
    // 控制区控件工厂
    // ============================================================

    /** SegmentedAnt 选项 entry（value + label）。 */
    public record Entry(String value, String label) {
        public static Entry of(String value, String label) {
            return new Entry(value, label);
        }
    }

    /** SegmentedAnt 选项 entry（value + label）的轻量静态别名。 */
    public static Entry entry(String value, String label) {
        return new Entry(value, label);
    }

    /**
     * 创建一个标准 Playground 用的 Segmented（低级 API：手动传 onChange）。
     *
     * <p>这是 rebind 体系之外的逃生口 —— 适合嵌入 rebind 流程以外的回调。
     * 绝大多数场景推荐用 {@link #segmented(Binder, Entry...)}。</p>
     *
     * <p>封装 Playground 场景下的常用默认：</p>
     * <ul>
     *   <li>尺寸 SMALL（让控制区更紧凑）</li>
     *   <li>如果 defaultValue 非空，自动 set selected</li>
     *   <li>onChange 回调透传</li>
     * </ul>
     *
     * @param defaultValue 默认选中 value（null 表示不预选）
     * @param onChange     选项变化回调
     * @param entries      选项列表（建议至少 2 个）
     * @return SegmentedAnt 控件（实际是 HBox）
     */
    public static Node segmented(String defaultValue, Consumer<String> onChange, Entry... entries) {
        if (entries == null || entries.length == 0) {
            throw new IllegalArgumentException("segmented 至少需要一个 entry");
        }
        SegmentedAnt.Builder b = SegmentedAnt.create()
                .size(Size.SMALL)
                .onChange(onChange);
        if (defaultValue != null && !defaultValue.isBlank()) {
            b.selected(defaultValue);
        }
        for (Entry e : entries) {
            b.option(e.value(), e.label());
        }
        return b.build();
    }

    /**
     * 创建一个绑定到 Binder 的 Segmented（推荐）。
     *
     * <p>选项变更时自动调用 {@code binder.set(value)}；{@link #rebindRebuild} /
     * {@link #rebindModify} 内部会遍历所有控制控件的 properties 提取 binder，
     * 并注册「值变更 → 重建/修改 display」监听。</p>
     *
     * @param binder 状态盒子（通常用 {@link #binder(String)} 创建）
     * @param entries 选项列表（至少 2 个）
     * @return Segmented Node；binder 引用挂在 properties 上（用于 rebind 提取）
     */
    public static Node segmented(Binder<String> binder, Entry... entries) {
        if (binder == null) {
            throw new IllegalArgumentException("binder 不能为 null");
        }
        if (entries == null || entries.length == 0) {
            throw new IllegalArgumentException("segmented 至少需要一个 entry");
        }
        String initial = binder.get();
        Node node = segmented(initial, binder::set, entries);
        node.getProperties().put(BINDER_KEY, binder);
        return node;
    }

    /**
     * 创建一个绑定到 Binder 的文本输入框（推荐用于可编辑文本属性，如 Tag.text / Input.value）。
     *
     * <p>输入变化时自动调用 {@code binder.set(text)}；rebind 入口会自动监听并触发
     * display 重建/修改。{@code initial} 通常等于 {@code binder.get()} —— 分开传是为了
     * 让调用点更清晰地表达「这是初始值」的语义。</p>
     *
     * <p>使用 {@code .jfx-demo-playground-textfield} 样式（240px 偏好宽度）。</p>
     *
     * @param binder 状态盒子
     * @param initial 初始文本（通常等于 binder 当前值）
     * @param prompt 占位提示文本（可空）
     * @return TextField；binder 引用挂在 properties 上
     */
    public static Node textField(Binder<String> binder, String initial, String prompt) {
        if (binder == null) {
            throw new IllegalArgumentException("binder 不能为 null");
        }
        TextField tf = new TextField(initial != null ? initial : "");
        if (prompt != null && !prompt.isBlank()) {
            tf.setPromptText(prompt);
        }
        tf.getStyleClass().add("jfx-demo-playground-textfield");
        tf.textProperty().addListener((obs, old, val) -> binder.set(val != null ? val : ""));
        tf.getProperties().put(BINDER_KEY, binder);
        return tf;
    }

    // ============================================================
    // rebind 入口（高级 API：Binder + Row + factory/apply）
    // ============================================================

    /**
     * rebuild 模式入口：display 工厂 + 控制行列表。
     *
     * <p>适用于无 Controller 的组件（典型代表：DividerAnt、GroupBoxAnt）。
     * 内部自动处理「鸡生蛋」问题：</p>
     * <ol>
     *   <li>先 build 一个空 Playground 拿 display 容器</li>
     *   <li>从所有控制控件提取 binder，注册「值变更 → 重建 display」监听</li>
     *   <li>再 build 完整 Playground（带 controlSection / Row / 真实 display）</li>
     *   <li>后续 binder 触发会自动调用 factory.get() + replaceDisplay</li>
     * </ol>
     *
     * <p><b>要求</b>：rows 里的 control 必须是用 {@link #segmented(Binder, Entry...)}
     * 创建的 —— 其他来源的 control 不会被监听（因为它不带 binder 引用）。</p>
     *
     * @param displayFactory display 节点工厂（每次调用都应反映 binder 当前值）
     * @param sectionTitle 可选控制区分组标题（null/blank 表示不分组）
     * @param rows 控制行列表（label + control）
     * @return 完整的 Playground 节点（可直接放到 Section 里）
     */
    public static Node rebindRebuild(Supplier<Node> displayFactory, String sectionTitle, Row... rows) {
        if (displayFactory == null) {
            throw new IllegalArgumentException("displayFactory 不能为 null");
        }
        rows = rows == null ? new Row[0] : rows;

        // 1. scaffold build：只装 display，拿 display 容器
        Node scaffold = PlayGround.create().display(displayFactory.get()).build();
        StackPane[] displayRef = new StackPane[]{displayOf(scaffold)};

        // 2. 提取所有 binder
        List<Binder<String>> binders = extractBinders(rows);

        // 3. 注册监听：binder.set → 重新 build display + replace
        for (Binder<String> b : binders) {
            b.addListener(() -> replaceDisplay(displayRef[0], displayFactory.get()));
        }

        // 4. 完整 build：带 controlSection + controlRow + 真实 display
        Builder b = PlayGround.create();
        if (sectionTitle != null && !sectionTitle.isBlank()) {
            b = b.controlSection(sectionTitle);
        }
        for (Row row : rows) {
            b = b.controlRow(row.label(), row.control());
        }
        Node finalBuilt = b.display(displayFactory.get()).build();

        // 5. 重写 display 引用 —— 后续 binder 触发会指向新 display
        displayRef[0] = displayOf(finalBuilt);

        return finalBuilt;
    }

    /**
     * modify 模式入口：目标节点 + modify apply + 控制行列表。
     *
     * <p>适用于暴露 ModifyBuilder 的组件（典型代表：TagAnt）。
     * 单次 build，无需 scaffold —— 因为 apply 改的是 target 节点本身，
     * 不需要替换 display 容器。</p>
     *
     * @param target 预先 build 好的目标节点（display 容器内唯一节点）
     * @param apply 修改逻辑（应从 binder 读值 + 调 modify().apply()）
     * @param sectionTitle 可选控制区分组标题（null/blank 表示不分组）
     * @param rows 控制行列表（label + control）
     * @return 完整的 Playground 节点
     */
    public static Node rebindModify(Node target, Runnable apply, String sectionTitle, Row... rows) {
        if (target == null) {
            throw new IllegalArgumentException("target 节点不能为 null");
        }
        if (apply == null) {
            throw new IllegalArgumentException("apply 不能为 null");
        }
        rows = rows == null ? new Row[0] : rows;

        // 1. 提取所有 binder
        List<Binder<String>> binders = extractBinders(rows);

        // 2. 注册监听：binder.set → apply.run()
        for (Binder<String> b : binders) {
            b.addListener(apply);
        }

        // 3. 单次 build
        Builder b = PlayGround.create();
        if (sectionTitle != null && !sectionTitle.isBlank()) {
            b = b.controlSection(sectionTitle);
        }
        for (Row row : rows) {
            b = b.controlRow(row.label(), row.control());
        }
        return b.display(target).build();
    }

    /**
     * controller 模式入口：目标节点 + controller apply + 控制行列表。
     *
     * <p>适用于暴露 Controller 的组件（典型代表：SegmentedAnt、MenuAnt、StatisticAnt 等 11 个）。
     * 控制区回调里调 {@code controller.setXxx(...)} 原地更新状态 —— 无重建、不闪烁、
     * display 节点引用始终有效。</p>
     *
     * <p>当前实现直接委托给 {@link #rebindModify}：两者在语义上是同一回事（都是「原地修改
     * display 节点，无重建」），入口分开只是为了让调用方代码自描述 —— 一眼就能看出是
     * modify 模式（{@code TagAnt.modify(tag).xxx().apply()}）还是 controller 模式
     * （{@code controller.setXxx(...)}）。</p>
     *
     * <p><b>两种 Controller 取法</b>：</p>
     * <ul>
     *   <li>9 个组件（Menu/Segmented/Statistic/Watermark/Tabs/QR/Progress/Image/Calendar）：
     *       直接用静态 {@code XxxAnt.controllerOf(target)} 取 controller，
     *       apply 里调 {@code controller.setXxx(...)}。</li>
     *   <li>StepsAnt / AnchorAnt：没有静态 controllerOf，只有 Builder 实例方法
     *       {@code builder.controller()}。建议外层先 build 拿到 builder 再传 apply，
     *       或者预先 build 后从 builder 实例取 controller。</li>
     * </ul>
     *
     * @param target 预先 build 好的目标节点（display 容器内唯一节点）
     * @param apply 修改逻辑（应从 binder 读值 + 调 controller.setXxx(...)）
     * @param sectionTitle 可选控制区分组标题（null/blank 表示不分组）
     * @param rows 控制行列表（label + control）
     * @return 完整的 Playground 节点
     */
    public static Node rebindController(Node target, Runnable apply, String sectionTitle, Row... rows) {
        return rebindModify(target, apply, sectionTitle, rows);
    }

    /** 从控制行中提取所有 binder（忽略非 segmented-binder 来源的 control）。 */
    private static List<Binder<String>> extractBinders(Row... rows) {
        List<Binder<String>> out = new ArrayList<>();
        for (Row row : rows) {
            if (row == null || row.control() == null) continue;
            Object b = row.control().getProperties().get(BINDER_KEY);
            if (b instanceof Binder<?> binder) {
                @SuppressWarnings("unchecked")
                Binder<String> sb = (Binder<String>) binder;
                out.add(sb);
            }
        }
        return out;
    }
}
