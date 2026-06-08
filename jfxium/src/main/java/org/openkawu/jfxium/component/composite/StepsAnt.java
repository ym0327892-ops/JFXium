package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 步骤条组件 - 对标 Ant Design Steps。
 *
 * <p><b>定位</b>：引导用户按流程完成任务的导航条，常用于注册流程、订单状态、
 * 向导式表单等场景。支持水平/垂直布局、多种尺寸。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>方向</b>：HORIZONTAL（默认）/ VERTICAL</li>
 *   <li><b>尺寸</b>：SMALL / DEFAULT</li>
 *   <li><b>步骤状态</b>：finished / current / wait，自动通过修饰类切换颜色</li>
 *   <li><b>当前步</b>：current(n) 指定当前步骤索引</li>
 *   <li><b>步骤项</b>：支持 title + description + 可选自定义图标</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox steps = StepsAnt.create()
 *     .step("填写信息", "基本资料")
 *     .step("验证身份", "手机/邮箱")
 *     .step("完成注册", "")
 *     .current(1)  // 第二步进行中
 *     .build();
 * }</pre>
 */
public class StepsAnt {

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public enum Size {
        SMALL, DEFAULT
    }

    private enum State { FINISHED, CURRENT, WAIT }

    public static class Step {
        String title;
        String description;
        String icon;

        public Step(String title, String description, String icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Step> steps = new ArrayList<>();
        private int current = 0;
        private Direction direction = Direction.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private boolean responsive = true;
        // runtime 控制器：build() 后装配，支持不重建节点切换当前步骤（BUG #51）
        private Controller controller;

        public Builder step(String title) { steps.add(new Step(title, null, null)); return this; }
        public Builder step(String title, String description) { steps.add(new Step(title, description, null)); return this; }
        public Builder step(String title, String description, String icon) { steps.add(new Step(title, description, icon)); return this; }
        public Builder current(int current) { this.current = current; return this; }
        public Builder direction(Direction direction) { this.direction = direction; return this; }
        public Builder size(Size size) { this.size = size; return this; }

        public Node build() {
            // 每次 build 装配一个新的 Controller，持有所有状态相关节点引用
            this.controller = new Controller(steps.size(), current);
            return direction == Direction.HORIZONTAL ? buildHorizontal() : buildVertical();
        }

        /**
         * 拿到 runtime 控制器（必须在 {@link #build()} 之后调用）。
         *
         * <p>用例：交互式步进（上一步/下一步）只需调 {@link Controller#setCurrent(int)} 刷新高亮，
         * 不必 rebuild 整个步骤条节点（对齐 MenuAnt.Controller 的 runtime 模式，BUG #51）。</p>
         */
        public Controller controller() {
            if (controller == null) {
                throw new IllegalStateException("controller() 必须在 build() 之后调用");
            }
            return controller;
        }

        private HBox buildHorizontal() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.TOP_CENTER);
            container.getStyleClass().add(JfxStyles.STEPS);

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                State state = stateFor(i);
                StepNodes sn = controller.stepNodes.get(i);

                VBox stepBox = new VBox(8);
                stepBox.setAlignment(Pos.CENTER);
                stepBox.setPrefWidth(200);
                stepBox.getStyleClass().add(JfxStyles.STEPS_ITEM);
                HBox.setHgrow(stepBox, Priority.ALWAYS);

                HBox iconBox = new HBox(0);
                iconBox.setAlignment(Pos.CENTER);
                iconBox.getChildren().add(makeStepIcon(stepSize, state, i + 1, sn));

                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 60, 0);
                    line.getStyleClass().add(JfxStyles.STEPS_LINE);
                    if (state == State.FINISHED) {
                        line.getStyleClass().add(JfxStyles.STEPS_STATE_FINISHED);
                    }
                    sn.line = line;
                    iconBox.getChildren().add(line);
                }
                stepBox.getChildren().add(iconBox);

                stepBox.getChildren().add(makeTitle(step.title, state, sn));
                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.getStyleClass().add(JfxStyles.STEPS_DESCRIPTION);
                    stepBox.getChildren().add(descLabel);
                }
                container.getChildren().add(stepBox);
            }
            return container;
        }

        private VBox buildVertical() {
            VBox container = new VBox(0);
            container.setAlignment(Pos.TOP_LEFT);
            container.getStyleClass().add(JfxStyles.STEPS_VERTICAL);

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                State state = stateFor(i);
                StepNodes sn = controller.stepNodes.get(i);

                HBox stepBox = new HBox(12);
                stepBox.setAlignment(Pos.TOP_LEFT);
                stepBox.getStyleClass().add(JfxStyles.STEPS_ITEM);

                VBox leftBox = new VBox(0);
                leftBox.setAlignment(Pos.TOP_CENTER);
                leftBox.setPrefWidth(stepSize);
                leftBox.getChildren().add(makeStepIcon(stepSize, state, i + 1, sn));

                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 0, 40);
                    line.getStyleClass().add(JfxStyles.STEPS_LINE);
                    if (state == State.FINISHED) {
                        line.getStyleClass().add(JfxStyles.STEPS_STATE_FINISHED);
                    }
                    sn.line = line;
                    leftBox.getChildren().add(line);
                }
                stepBox.getChildren().add(leftBox);

                VBox contentBox = new VBox(4);
                contentBox.setAlignment(Pos.TOP_LEFT);
                contentBox.getChildren().add(makeTitle(step.title, state, sn));
                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.getStyleClass().add(JfxStyles.STEPS_DESCRIPTION);
                    contentBox.getChildren().add(descLabel);
                }
                stepBox.getChildren().add(contentBox);
                container.getChildren().add(stepBox);
            }
            return container;
        }

        /** 生成圆圈 + 数字（颜色由 LESS 状态修饰类切换；引用存入 sn 供 runtime 切换）*/
        private StackPane makeStepIcon(int stepSize, State state, int number, StepNodes sn) {
            Circle circle = new Circle(stepSize / 2.0);
            circle.getStyleClass().add(JfxStyles.STEPS_CIRCLE);
            circle.getStyleClass().add(stateClass(state));

            Label numberLabel = new Label(String.valueOf(number));
            numberLabel.getStyleClass().add(JfxStyles.STEPS_NUMBER);
            numberLabel.getStyleClass().add(stateClass(state));
            // font-size 由 .jfx-steps-number / .jfx-steps-number.jfx-steps-small 修饰类控制（避免红线 #1）
            if (stepSize == 24) {
                numberLabel.getStyleClass().add(JfxStyles.STEPS_SMALL);
            }

            sn.circle = circle;
            sn.number = numberLabel;

            StackPane iconPane = new StackPane();
            iconPane.getChildren().addAll(circle, numberLabel);
            return iconPane;
        }

        private Label makeTitle(String text, State state, StepNodes sn) {
            Label titleLabel = new Label(text);
            titleLabel.getStyleClass().add(JfxStyles.STEPS_TITLE);
            titleLabel.getStyleClass().add(stateClass(state));
            sn.title = titleLabel;
            return titleLabel;
        }

        private State stateFor(int i) {
            if (i < current) return State.FINISHED;
            if (i == current) return State.CURRENT;
            return State.WAIT;
        }

        private static String stateClass(State state) {
            return switch (state) {
                case FINISHED -> JfxStyles.STEPS_STATE_FINISHED;
                case CURRENT -> JfxStyles.STEPS_STATE_CURRENT;
                case WAIT -> JfxStyles.STEPS_STATE_WAIT;
            };
        }
    }

    /** 单个步骤的状态相关节点引用（供 Controller runtime 切换 styleClass，BUG #51）。 */
    private static class StepNodes {
        Circle circle;
        Label number;
        Label title;
        Line line;   // 该步骤右侧/下方的连接线，可能为 null（最后一步）
    }

    /**
     * 步骤条运行时控制器：在不重建节点的前提下切换当前步骤（BUG #51）。
     *
     * <p>对齐 MenuAnt.Controller 的 runtime 模式。{@link #setCurrent(int)} 直接更新各步骤
     * circle/number/title 的状态修饰类（finished/current/wait）和连接线高亮，
     * 调用方拿到的 Node 引用始终有效，不丢动画/布局状态。</p>
     *
     * <pre>{@code
     * StepsAnt.Builder b = StepsAnt.create().step("A").step("B").step("C").current(0);
     * Node steps = b.build();
     * StepsAnt.Controller ctrl = b.controller();
     * ctrl.next();            // 前进到下一步
     * ctrl.setCurrent(2);     // 直接跳到第三步
     * }</pre>
     */
    public static class Controller {
        private final List<StepNodes> stepNodes = new ArrayList<>();
        private final int total;
        private int current;

        Controller(int total, int current) {
            this.total = total;
            this.current = current;
            for (int i = 0; i < total; i++) {
                stepNodes.add(new StepNodes());
            }
        }

        /** 当前步骤下标（0-based）。 */
        public int getCurrent() {
            return current;
        }

        /** 总步骤数。 */
        public int getTotal() {
            return total;
        }

        /**
         * 切换当前步骤：重算所有步骤状态并更新 styleClass，不重建节点。
         * 越界（&lt;0 或 ≥total）时无操作。
         */
        public void setCurrent(int newCurrent) {
            if (newCurrent < 0 || newCurrent >= total) return;
            this.current = newCurrent;
            for (int i = 0; i < stepNodes.size(); i++) {
                State state = stateFor(i);
                StepNodes sn = stepNodes.get(i);
                applyState(sn.circle, state);
                applyState(sn.number, state);
                applyState(sn.title, state);
                // 连接线：仅当本步骤已完成时高亮
                if (sn.line != null) {
                    sn.line.getStyleClass().remove(JfxStyles.STEPS_STATE_FINISHED);
                    if (state == State.FINISHED) {
                        sn.line.getStyleClass().add(JfxStyles.STEPS_STATE_FINISHED);
                    }
                }
            }
        }

        /** 前进到下一步（已是最后一步则无操作）。 */
        public void next() {
            setCurrent(current + 1);
        }

        /** 后退到上一步（已是第一步则无操作）。 */
        public void prev() {
            setCurrent(current - 1);
        }

        private State stateFor(int i) {
            if (i < current) return State.FINISHED;
            if (i == current) return State.CURRENT;
            return State.WAIT;
        }

        /** 移除三种状态修饰类后挂上目标状态类（节点为 null 时跳过）。 */
        private static void applyState(Node node, State state) {
            if (node == null) return;
            node.getStyleClass().removeAll(
                    JfxStyles.STEPS_STATE_FINISHED,
                    JfxStyles.STEPS_STATE_CURRENT,
                    JfxStyles.STEPS_STATE_WAIT);
            node.getStyleClass().add(switch (state) {
                case FINISHED -> JfxStyles.STEPS_STATE_FINISHED;
                case CURRENT -> JfxStyles.STEPS_STATE_CURRENT;
                case WAIT -> JfxStyles.STEPS_STATE_WAIT;
            });
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
