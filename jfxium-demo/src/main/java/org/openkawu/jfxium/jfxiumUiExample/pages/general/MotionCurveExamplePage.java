package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.DesktopNotificationAnt;
import org.openkawu.jfxium.component.overlay.DrawerAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.overlay.ModalAnt;
import org.openkawu.jfxium.component.overlay.NotificationAnt;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.util.AnimationDuration;
import org.openkawu.jfxium.core.util.MotionCurve;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * MotionCurve 动效曲线 —— 时长令牌管「多久」，本页演示的曲线管「怎么走」（PROJECT_BUG.md #175）。
 *
 * <p>曲线取 Fluent 2 官方三次贝塞尔，三档语义：</p>
 * <ul>
 *   <li>{@link MotionCurve#ENTER}（curveDecelerateMid）—— 入场「快起缓收」</li>
 *   <li>{@link MotionCurve#EXIT}（curveAccelerateMid）—— 退场「缓起快收」</li>
 *   <li>{@link MotionCurve#STANDARD}（curveEasyEase）—— 原地/位移，两端对称</li>
 * </ul>
 *
 * <p>页内演示盒用 JavaFX 原生 {@code Transition + setInterpolator(...)} 驱动，与库里
 * 19 处组件动效（Modal / Drawer / Message / Notification / DesktopNotification / Carousel /
 * BackTop）是同一套机制 —— 所以这里看到的节奏就是真实组件的节奏。</p>
 */
public class MotionCurveExamplePage extends VBoxAnt {

    /** 三档曲线的可选项（key, 显示名, 曲线）。 */
    private static final Object[][] CURVES = {
            {"enter", "ENTER 入场", MotionCurve.ENTER},
            {"exit", "EXIT 退场", MotionCurve.EXIT},
            {"standard", "STANDARD 位移", MotionCurve.STANDARD},
    };

    /** 时长档位（key, 显示名, 时长）。 */
    private static final Object[][] DURATIONS = {
            {"fast", "FAST 200ms", AnimationDuration.FAST},
            {"normal", "NORMAL 250ms", AnimationDuration.NORMAL},
            {"slide", "SLIDE 400ms", AnimationDuration.SLIDE},
    };

    public MotionCurveExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("MotionCurve 动效曲线")
                .description("AnimationDuration 管「多久」，MotionCurve 管「怎么走」—— 取 Fluent 2 官方三次贝塞尔，入场快起缓收、退场缓起快收。")
                .sections(
                        threeCurvesSection(),
                        playgroundSection(),
                        realWorldSection(),
                        builtinCompareSection(),
                        durationCompareSection(),
                        lastFrameSection()
                )
                .padding(24)
                .build());
    }

    // ============================================================
    // 1. 三档语义
    // ============================================================

    private Node threeCurvesSection() {
        HBoxAnt cards = HBoxAnt.create().spacing(16).align(Pos.TOP_LEFT);
        for (Object[] c : CURVES) {
            cards.children(curveCard((String) c[1], (Interpolator) c[2]));
        }

        String code = """
                // 入场：快起缓收（元素出现在用户面前）
                fade.setInterpolator(MotionCurve.ENTER);

                // 退场：缓起快收（元素从用户面前离开）
                fade.setInterpolator(MotionCurve.EXIT);

                // 原地/位置位移（如滚动到顶）：两端对称
                new KeyValue(vvalueProperty(), 0, MotionCurve.STANDARD);
                """;
        return Demos.sectionWithCode("1. 三档语义",
                "点「播放」反复看：ENTER 起步就有明显位移、末段缓缓收住；EXIT 相反，先慢后快「抽走」。",
                code, cards);
    }

    /**
     * 单档曲线卡片：曲线名 + 公式/语义说明 + 演示盒 + 播放/重置。
     *
     * @param name   演示盒上的标题
     * @param curve  该档曲线
     */
    private Node curveCard(String name, Interpolator curve) {
        String formula;
        String hint;
        String values;
        if (curve == MotionCurve.ENTER) {
            formula = "curveDecelerateMid (0,0,0,1)";
            hint = "快起缓收：前 10% 时间走完 44.6% 行程";
            values = "f(0.1)=0.446    f(0.5)=0.890";
        } else if (curve == MotionCurve.EXIT) {
            formula = "curveAccelerateMid (1,0,1,1)";
            hint = "缓起快收：前 10% 只走 0.3%，末段才加速";
            values = "f(0.1)=0.003    f(0.5)=0.110";
        } else {
            formula = "curveEasyEase (0.33,0,0.67,1)";
            hint = "两端对称，易入易出";
            values = "f(0.1)=0.028    f(0.5)=0.500";
        }

        MotionBox box = new MotionBox(name, curve, -60, 0, 0.92, AnimationDuration.NORMAL);

        return VBoxAnt.create().spacing(8).align(Pos.TOP_LEFT).children(
                TypographyAnt.text(formula).type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text(hint).type(TypographyAnt.TextColor.SECONDARY).build(),
                box,
                Demos.row(
                        ButtonAnt.create("播放").type(ButtonAnt.Type.PRIMARY).onClick(e -> box.play()).build(),
                        ButtonAnt.create("重置").onClick(e -> box.reset()).build()),
                TypographyAnt.text(values).type(TypographyAnt.TextColor.SECONDARY).build());
    }

    // ============================================================
    // 2. 自由组合（曲线 × 时长 × 动作）
    // ============================================================

    private Node playgroundSection() {
        MotionBox box = new MotionBox("右滑入", MotionCurve.ENTER, -90, 0, 0.95, AnimationDuration.NORMAL);

        Node curveSeg = SegmentedAnt.create()
                .option("enter", "ENTER")
                .option("exit", "EXIT")
                .option("standard", "STANDARD")
                .selected("enter")
                .onChange(key -> box.setCurve(curveOf(key)))
                .build();

        Node durationSeg = SegmentedAnt.create()
                .option("fast", "200ms")
                .option("normal", "250ms")
                .option("slide", "400ms")
                .selected("normal")
                .onChange(key -> box.setDuration(durationOf(key)))
                .build();

        Node actionSeg = SegmentedAnt.create()
                .option("fade", "淡入")
                .option("slide", "右滑入")
                .option("zoom", "缩放进入")
                .selected("slide")
                .onChange(key -> box.setMotion(switch (key) {
                    case "fade" -> new double[] {0, 0, 1.0};
                    case "zoom" -> new double[] {0, 0, 0.75};
                    default -> new double[] {-90, 0, 0.95};
                }))
                .build();

        // 控制区用 Grid 左右分栏，而不是固定宽度的控制列：
        // Segmented 三个选项（ENTER/EXIT/STANDARD）天然比控制列宽，窄列会被裁掉尾部选项。
        Node playground = GridAnt.create().gutter(24)
                .row(GridAnt.row()
                        .col(13, controlColumn(curveSeg, durationSeg, actionSeg, box))
                        .col(11, previewPlate(box)))
                .build();

        String code = """
                // 曲线与时长都是入参，同一个 Transition 换 token 即换手感
                TranslateTransition slide = new TranslateTransition(duration, node);
                slide.setFromX(-90);
                slide.setToX(0);
                slide.setInterpolator(curve);   // MotionCurve.ENTER / EXIT / STANDARD
                slide.play();
                """;
        return Demos.sectionWithCode("2. 自由组合",
                "换曲线、换时长、换动作，再点播放 —— 曲线决定「节奏」，时长决定「总耗时」，两者正交。",
                code, playground);
    }

    /** 控制列：三组 Segmented + 播放/重置。 */
    private Node controlColumn(Node curveSeg, Node durationSeg, Node actionSeg, MotionBox box) {
        return VBoxAnt.create().spacing(12).align(Pos.TOP_LEFT).children(
                labeledControl("曲线", curveSeg),
                labeledControl("时长", durationSeg),
                labeledControl("动作", actionSeg),
                Demos.row(
                        ButtonAnt.create("播放").type(ButtonAnt.Type.PRIMARY).onClick(e -> box.play()).build(),
                        ButtonAnt.create("重置").onClick(e -> box.reset()).build()));
    }

    private Node labeledControl(String label, Node control) {
        // 左对齐、不拉伸：Segmented 默认 maxWidth 吃满列宽，会显得空旷（选项挤在中间）
        if (control instanceof Region r) {
            r.setMaxWidth(Region.USE_PREF_SIZE);
        }
        return VBoxAnt.create().spacing(6).align(Pos.TOP_LEFT).children(
                TypographyAnt.text(label).type(TypographyAnt.TextColor.SECONDARY).build(),
                control);
    }

    /** 右侧预览板：内凹底色，让演示盒的移动有参照物（对齐 PlayGround 展示区观感）。 */
    private Node previewPlate(MotionBox box) {
        StackPaneAnt plate = StackPaneAnt.create().align(Pos.CENTER).children(box);
        plate.background(Background.INSET);
        plate.setMinHeight(200);
        plate.setPrefHeight(220);
        return plate;
    }

    // ============================================================
    // 3. 真实组件（库里 19 处接线点）
    // ============================================================

    private Node realWorldSection() {
        Node row = Demos.row(
                ButtonAnt.create("Modal 弹窗").onClick(e -> ModalAnt.create()
                        .title("Modal 入场")
                        .content("面板走 MotionCurve.ENTER（淡入 + 0.9→1 缩放），关闭走 MotionCurve.EXIT。")
                        .build().open((Node) e.getSource())).build(),
                ButtonAnt.create("Drawer 抽屉").onClick(e -> DrawerAnt.create()
                        .title("Drawer 入场")
                        .content("平移 + 淡入都走 MotionCurve.ENTER；关闭走 MotionCurve.EXIT。")
                        .placement(DrawerAnt.Placement.RIGHT)
                        .width(360)
                        .build().open((Node) e.getSource())).build(),
                ButtonAnt.create("Message 消息")
                        .onClick(e -> MessageAnt.success("Message 走 MotionCurve.ENTER")).build(),
                ButtonAnt.create("Notification 通知")
                        .onClick(e -> NotificationAnt.info("Notification", "滑入 + 淡入走 MotionCurve.ENTER")).build(),
                ButtonAnt.create("桌面通知")
                        .onClick(e -> DesktopNotificationAnt.info("桌面通知", "系统角落弹层同样走 MotionCurve.ENTER")).build());

        String code = """
                // 库里 7 个组件的 19 处动效已统一走 MotionCurve
                fade.setInterpolator(MotionCurve.ENTER);   // ModalAnt.animateIn
                scale.setInterpolator(MotionCurve.ENTER);
                ...
                fade.setInterpolator(MotionCurve.EXIT);    // ModalAnt.animateOut
                """;
        return Demos.sectionWithCode("3. 真实组件",
                "这 5 个按钮触发的是已接线的组件动效（不是演示盒）—— 看到的就是库里的真实节奏。",
                code, row);
    }

    // ============================================================
    // 4. 与 JavaFX 内置 EASE_OUT 对照
    // ============================================================

    private Node builtinCompareSection() {
        MotionBox fluent = new MotionBox("MotionCurve.ENTER", MotionCurve.ENTER,
                -60, 0, 1.0, AnimationDuration.NORMAL);
        MotionBox builtin = new MotionBox("内置 EASE_OUT", Interpolator.EASE_OUT,
                -60, 0, 1.0, AnimationDuration.NORMAL);

        VBoxAnt content = VBoxAnt.create().spacing(16).children(
                Demos.row(
                        ButtonAnt.create("同时播放").type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> {
                                    fluent.play();
                                    builtin.play();
                                }).build(),
                        ButtonAnt.create("重置").onClick(e -> {
                            fluent.reset();
                            builtin.reset();
                        }).build()),
                GridAnt.create().gutter(16)
                        .row(GridAnt.row()
                                .col(12, labeledBox("MotionCurve.ENTER",
                                        "Fluent curveDecelerateMid (0,0,0,1)", fluent))
                                .col(12, labeledBox("JavaFX Interpolator.EASE_OUT",
                                        "近似线性（x=0.1 只走 0.111）", builtin)))
                        .build());

        String code = """
                // MotionCurve.ENTER = curveDecelerateMid (0,0,0,1) ——「快起缓收」
                Interpolator.SPLINE(0.0, 0.0, 0.0, 1.0);

                // 内置 EASE_OUT 实测近似线性：前 10% 时间只走 11.1% 行程，几乎看不出缓动
                Interpolator.EASE_OUT;
                """;
        return Demos.sectionWithCode("4. 为什么不用内置档",
                "同一次播放、同 250ms：左侧起步就冲出去再收住，右侧近似匀速 —— 这就是「缓动语义丢失」。",
                code, content);
    }

    private Node labeledBox(String title, String desc, MotionBox box) {
        return VBoxAnt.create().spacing(6).children(
                TypographyAnt.text(title).build(),
                TypographyAnt.text(desc).type(TypographyAnt.TextColor.SECONDARY).build(),
                box);
    }

    // ============================================================
    // 5. 同曲线 · 不同时长
    // ============================================================

    private Node durationCompareSection() {
        VBoxAnt rows = VBoxAnt.create().spacing(10);
        LabelAnt status = LabelAnt.create("点「同时播放」对比三档时长").build();

        MotionBox[] boxes = new MotionBox[DURATIONS.length];
        for (int i = 0; i < DURATIONS.length; i++) {
            Object[] d = DURATIONS[i];
            // 盒子标题留空：左侧已有档位标签，避免同一行出现两次 "FAST 200ms"
            MotionBox box = new MotionBox("", MotionCurve.ENTER, -60, 0, 1.0, (Duration) d[2]);
            boxes[i] = box;
            LabelAnt tag = LabelAnt.create((String) d[1]).build();
            tag.getStyleClass().add("jfx-demo-motion-duration-label");
            rows.children(HBoxAnt.create().spacing(12).align(Pos.CENTER_LEFT).children(tag, box));
        }

        ButtonAnt playAll = ButtonAnt.create("同时播放").type(ButtonAnt.Type.PRIMARY).build();
        playAll.setOnAction(e -> {
            for (MotionBox box : boxes) {
                box.play();
            }
            // 一条 16ms 帧的计时器：直观看出「谁先停稳」
            Timeline clock = new Timeline();
            for (int ms = 0; ms <= 400; ms += 16) {
                final int elapsed = ms;
                clock.getKeyFrames().add(new KeyFrame(Duration.millis(ms),
                        ev -> status.setText("已过 " + elapsed + " ms")));
            }
            clock.setOnFinished(ev -> status.setText("全部停稳 —— 曲线相同，只是总耗时不同"));
            clock.play();
        });

        VBoxAnt content = VBoxAnt.create().spacing(12).children(
                Demos.row(playAll, ButtonAnt.create("重置").onClick(e -> {
                    for (MotionBox box : boxes) {
                        box.reset();
                    }
                    status.setText("已重置");
                }).build()),
                rows, status);

        String code = """
                // 曲线相同、时长不同：位移节奏一致，只是总耗时不同 —— 这就是「正交维度」
                new FadeTransition(AnimationDuration.FAST, node);    // 200ms
                new FadeTransition(AnimationDuration.NORMAL, node);  // 250ms
                new FadeTransition(AnimationDuration.SLIDE, node);   // 400ms
                """;
        return Demos.sectionWithCode("5. 同曲线 · 不同时长",
                "三条动画同一曲线、不同时长同时起跑 —— 下方的毫秒计数能看出谁先停稳。",
                code, content);
    }

    // ============================================================
    // 6. 末帧对照（t=90%）
    // ============================================================

    private Node lastFrameSection() {
        HBoxAnt row = HBoxAnt.create().spacing(16).align(Pos.TOP_LEFT);
        for (Object[] c : CURVES) {
            Interpolator curve = (Interpolator) c[2];
            MotionBox box = new MotionBox((String) c[1], curve, 0, 0, 1.0, AnimationDuration.NORMAL);
            box.preview(0.9);
            row.children(VBoxAnt.create().spacing(6).children(
                    box,
                    TypographyAnt.text(String.format("f(0.90) = %.3f", curve.interpolate(0.0, 1.0, 0.9)))
                            .type(TypographyAnt.TextColor.SECONDARY).build()));
        }

        String code = """
                // 同一时刻 t=0.9，三档曲线给出的进度完全不同：
                MotionCurve.ENTER.interpolate(0, 1, 0.9);     // 0.997 —— 早到终点，静待收尾
                MotionCurve.EXIT.interpolate(0, 1, 0.9);      // 0.554 —— 还在半路，正在加速离开
                MotionCurve.STANDARD.interpolate(0, 1, 0.9);  // 0.972
                """;
        return Demos.sectionWithCode("6. 末帧对照 t=90%",
                "动画进行到 90% 时冻结一帧：ENTER 几乎已到位、EXIT 才走到一半 —— 「先慢后快」的直观证据。",
                code, row);
    }

    // ============================================================
    // 工具
    // ============================================================

    private static Interpolator curveOf(String key) {
        for (Object[] c : CURVES) {
            if (c[0].equals(key)) {
                return (Interpolator) c[2];
            }
        }
        return MotionCurve.ENTER;
    }

    private static Duration durationOf(String key) {
        for (Object[] d : DURATIONS) {
            if (d[0].equals(key)) {
                return (Duration) d[2];
            }
        }
        return AnimationDuration.NORMAL;
    }

    // ============================================================
    // 演示盒：按曲线重放「淡入 + 位移 + 缩放」
    // ============================================================

    /**
     * 演示盒 —— 按给定曲线重放「淡入 + 位移 + 缩放」的一张卡片。
     *
     * <p><b>孵化原因</b>：JFXium 没有「可配置曲线的动效演示」控件。本页需要反复重放同一动效，
     * 只用 JavaFX 原生 {@code Transition}（与库内组件同一机制）实现，不引入新的库内 API。</p>
     */
    private static final class MotionBox extends StackPaneAnt {

        private final LabelAnt card;
        private Interpolator curve;
        private Duration duration;
        /** 起始位移 / 缩放：{dx, dy, scale}，终态恒为 {0, 0, 1}。 */
        private double[] motion;

        MotionBox(String title, Interpolator curve, double dx, double dy, double scale, Duration duration) {
            this.card = LabelAnt.create(title == null ? "" : title).build();
            this.curve = curve != null ? curve : MotionCurve.ENTER;
            this.duration = duration != null ? duration : AnimationDuration.NORMAL;
            this.motion = new double[] {dx, dy, scale};
            card.getStyleClass().add("jfx-demo-motion-card");
            align(Pos.CENTER);
            children(card);
            reset();
        }

        void setCurve(Interpolator curve) {
            if (curve != null) {
                this.curve = curve;
            }
        }

        void setDuration(Duration duration) {
            if (duration != null) {
                this.duration = duration;
            }
        }

        void setMotion(double[] motion) {
            if (motion != null && motion.length == 3) {
                this.motion = motion;
            }
        }

        /** 播放「入场」：从初始态到终态。 */
        void play() {
            // 先落回起点，避免从中途状态起播
            applyProgress(0);
            FadeTransition fade = new FadeTransition(duration, card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(curve);

            TranslateTransition slide = new TranslateTransition(duration, card);
            slide.setFromX(motion[0]);
            slide.setFromY(motion[1]);
            slide.setToX(0);
            slide.setToY(0);
            slide.setInterpolator(curve);

            ScaleTransition zoom = new ScaleTransition(duration, card);
            zoom.setFromX(motion[2]);
            zoom.setFromY(motion[2]);
            zoom.setToX(1);
            zoom.setToY(1);
            zoom.setInterpolator(curve);

            new ParallelTransition(fade, slide, zoom).play();
        }

        /** 冻结在给定进度（0~1）的一帧上 —— 用于「末帧对照」。 */
        void preview(double progress) {
            double p = Double.isFinite(progress) ? Math.max(0, Math.min(1, progress)) : 1;
            applyProgress(p);
        }

        /** 静止回终态（不透明、无位移、无缩放）。 */
        void reset() {
            card.setOpacity(1);
            card.setTranslateX(0);
            card.setTranslateY(0);
            card.setScaleX(1);
            card.setScaleY(1);
        }

        private void applyProgress(double progress) {
            card.setOpacity(curve.interpolate(0.0, 1.0, progress));
            card.setTranslateX(motion[0] * (1 - progress));
            card.setTranslateY(motion[1] * (1 - progress));
            double s = motion[2] + (1 - motion[2]) * progress;
            card.setScaleX(s);
            card.setScaleY(s);
        }
    }
}
