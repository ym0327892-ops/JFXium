package org.openkawu.jfxium.component.layout;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * SplitPaneAnt - 继承式 SplitPane 容器（M19.36 升级为双工厂模式）。
 *
 * <p>可拖拽分隔的多窗格的双工厂模式——既能当工厂用，也能被业务继承做"分屏页基类"。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * SplitPaneAnt split = SplitPaneAnt.create()
 *     .direction(SplitPaneAnt.Direction.HORIZONTAL)
 *     .items(leftPanel, rightPanel)
 *     .dividerPositions(0.3);
 * }</pre>
 *
 * <h3>2. 五条杠抓手（中间最长，两边递减）</h3>
 * <pre>{@code
 * SplitPaneAnt split = SplitPaneAnt.create()
 *     .gripStyle(SplitPaneAnt.GripStyle.LINES)
 *     .items(leftPanel, rightPanel);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 SplitPaneAnt）</li>
 *   <li><b>双重身份</b>：是 SplitPane 也是工厂——继承自 {@link SplitPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class SplitPaneAnt extends AbstractSplitPaneAnt<SplitPaneAnt> {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    /** 分隔条抓手样式。 */
    public enum GripStyle {
        /** 默认单条短杠（JavaFX 原生 grabber）。 */
        DEFAULT,
        /** 五条短线：中间最长，两侧依次变短。 */
        LINES
    }

    private static final String GRIP_PROPERTY = "jfxium.splitpane.grip";
    private static final String RULE_PROPERTY = "jfxium.splitpane.rule";
    private static final int[] GRIP_LENGTHS = {6, 10, 14, 10, 6};
    private static final int GRIP_LINE_THICKNESS = 2;

    private GripStyle gripStyle = GripStyle.DEFAULT;

    /** 工厂入口。 */
    public static SplitPaneAnt create() {
        return new SplitPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SplitPaneAnt() {
        getStyleClass().add(JfxStyles.SPLIT_PANE);
        installGripHooks();
    }

    public SplitPaneAnt(Node... items) {
        super(items);
        getStyleClass().add(JfxStyles.SPLIT_PANE);
        installGripHooks();
    }

    // ============================================================
    // 抓手样式
    // ============================================================

    /**
     * 设置分隔条抓手样式。
     *
     * <p>{@link GripStyle#LINES} 会在完整分隔线中间叠加五条与分隔线平行的短线：
     * 左右分栏（竖分隔线）显示五条竖线，上下分栏（横分隔线）显示五条横线，
     * 长度依次为 {@code 6 / 10 / 14 / 10 / 6}（中间最长）。</p>
     *
     * @param style 目标样式；null 按 {@link GripStyle#DEFAULT} 处理
     */
    public SplitPaneAnt gripStyle(GripStyle style) {
        this.gripStyle = style != null ? style : GripStyle.DEFAULT;
        getStyleClass().remove(JfxStyles.SPLIT_GRIP_LINES);
        if (gripStyle == GripStyle.LINES) {
            getStyleClass().add(JfxStyles.SPLIT_GRIP_LINES);
        }
        installGrips();
        return this;
    }

    /** 当前抓手样式。 */
    public GripStyle gripStyle() {
        return gripStyle;
    }

    private void installGripHooks() {
        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Platform.runLater(this::installGrips);
            }
        });
        orientationProperty().addListener((obs, oldOrientation, newOrientation) ->
                Platform.runLater(this::installGrips));
        getItems().addListener((ListChangeListener<Node>) change ->
                Platform.runLater(this::installGrips));
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::installGrips);
            }
        });
    }

    /** 安装/重装自定义抓手；包内可见便于单元测试直接驱动。 */
    void installGrips() {
        if (getSkin() == null) {
            return;
        }

        // 先移除旧抓手 / 旧自绘分隔线（skin 重建 / orientation 切换 / 样式切回 DEFAULT）
        for (Node divider : lookupAll(".split-pane-divider")) {
            Object oldGrip = divider.getProperties().remove(GRIP_PROPERTY);
            if (oldGrip instanceof Node grip && grip.getParent() instanceof Pane parent) {
                parent.getChildren().remove(grip);
            }
            Object oldRule = divider.getProperties().remove(RULE_PROPERTY);
            if (oldRule instanceof Node rule && rule.getParent() instanceof Pane parent) {
                parent.getChildren().remove(rule);
            }
        }

        if (gripStyle != GripStyle.LINES) {
            return;
        }

        boolean horizontalSplit = getOrientation() == Orientation.HORIZONTAL;
        for (Node divider : lookupAll(".split-pane-divider")) {
            // 完整分隔线由自绘 Line 负责：ContentDivider 在 vertical 方向 layoutHeight=0，
            // 单靠 -fx-background-color 会把整条线画没。
            addDividerRule(divider);

            Node host = divider.lookup(".vertical-grabber");
            if (host == null) {
                host = divider.lookup(".horizontal-grabber");
            }
            Pane hostPane = host instanceof Pane pane
                    ? pane
                    : (divider instanceof Pane pane ? pane : null);
            if (hostPane == null) {
                continue;
            }

            // 抓线与分隔线平行：左右分栏（竖分隔线）→ 五条竖线；上下分栏（横分隔线）→ 五条横线
            Region grip = horizontalSplit ? createVerticalGrip() : createHorizontalGrip();
            hostPane.getChildren().add(grip);
            divider.getProperties().put(GRIP_PROPERTY, grip);
        }
    }

    /** 在 divider 上自绘一条贯穿两端的 2px 分隔线（补 vertical 方向 layoutHeight=0 的坑）。 */
    private void addDividerRule(Node divider) {
        if (!(divider instanceof Pane pane)) {
            return;
        }
        Line rule = new Line();
        rule.getStyleClass().add(JfxStyles.SPLIT_GRIP_RULE);
        rule.setManaged(false);
        rule.setMouseTransparent(true);
        if (getOrientation() == Orientation.HORIZONTAL) {
            // 竖分隔线：x 居中，y 贯穿
            rule.startXProperty().bind(pane.widthProperty().divide(2));
            rule.endXProperty().bind(pane.widthProperty().divide(2));
            rule.setStartY(0);
            rule.endYProperty().bind(pane.heightProperty());
        } else {
            // 横分隔线：y 居中，x 贯穿
            rule.setStartX(0);
            rule.endXProperty().bind(pane.widthProperty());
            rule.startYProperty().bind(pane.heightProperty().divide(2));
            rule.endYProperty().bind(pane.heightProperty().divide(2));
        }
        pane.getChildren().add(0, rule);
        divider.getProperties().put(RULE_PROPERTY, rule);
    }

    /**
     * 上下分栏（horizontal divider）：五条横线，叠在默认 grabber 上。
     * 抓手本身设为 0 尺寸，靠 StackPane 拉满宿主后让线条向外溢出，避免撑大分隔条。
     */
    private Region createHorizontalGrip() {
        VBox grip = new VBox();
        grip.setAlignment(Pos.CENTER);
        grip.getStyleClass().add(JfxStyles.SPLIT_GRIP);
        for (int length : GRIP_LENGTHS) {
            Region line = new Region();
            line.getStyleClass().add(JfxStyles.SPLIT_GRIP_LINE);
            line.setPrefSize(length, GRIP_LINE_THICKNESS);
            line.setMinSize(length, GRIP_LINE_THICKNESS);
            line.setMaxSize(length, GRIP_LINE_THICKNESS);
            grip.getChildren().add(line);
        }
        prepareGrip(grip);
        return grip;
    }

    /** 左右分栏（vertical divider）：五条竖线。 */
    private Region createVerticalGrip() {
        HBox grip = new HBox();
        grip.setAlignment(Pos.CENTER);
        grip.getStyleClass().add(JfxStyles.SPLIT_GRIP);
        for (int length : GRIP_LENGTHS) {
            Region line = new Region();
            line.getStyleClass().add(JfxStyles.SPLIT_GRIP_LINE);
            line.setPrefSize(GRIP_LINE_THICKNESS, length);
            line.setMinSize(GRIP_LINE_THICKNESS, length);
            line.setMaxSize(GRIP_LINE_THICKNESS, length);
            grip.getChildren().add(line);
        }
        prepareGrip(grip);
        return grip;
    }

    private void prepareGrip(Region grip) {
        grip.setMouseTransparent(true);
        // pref=0 防止撑大 grabber；max=MAX 让 StackPane 把它拉满宿主，线条再向四周溢出
        grip.setPrefSize(0, 0);
        grip.setMinSize(0, 0);
        grip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<SplitPaneAnt> 默认实现
    // （节省 ~110 行重复模板代码，行为 100% 等价原 SplitPaneAnt）
    // ============================================================

}
