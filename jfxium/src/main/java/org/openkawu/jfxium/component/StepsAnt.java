package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Steps - 对标 Ant Design Steps。
 *
 * 重构：steps 容器 / circle / number / title / description / line 全部走 LESS，
 * 状态（finished / current / wait）通过修饰类 {@code steps-finished/current/wait} 切换。
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

    public static class Builder {
        private List<Step> steps = new ArrayList<>();
        private int current = 0;
        private Direction direction = Direction.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private boolean responsive = true;

        public Builder step(String title) { steps.add(new Step(title, null, null)); return this; }
        public Builder step(String title, String description) { steps.add(new Step(title, description, null)); return this; }
        public Builder step(String title, String description, String icon) { steps.add(new Step(title, description, icon)); return this; }
        public Builder current(int current) { this.current = current; return this; }
        public Builder direction(Direction direction) { this.direction = direction; return this; }
        public Builder size(Size size) { this.size = size; return this; }

        public Node build() {
            return direction == Direction.HORIZONTAL ? buildHorizontal() : buildVertical();
        }

        private HBox buildHorizontal() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.TOP_CENTER);
            container.getStyleClass().add(CssClasses.STEPS);

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                State state = stateFor(i);

                VBox stepBox = new VBox(8);
                stepBox.setAlignment(Pos.CENTER);
                stepBox.setPrefWidth(200);
                stepBox.getStyleClass().add(CssClasses.STEPS_ITEM);
                HBox.setHgrow(stepBox, Priority.ALWAYS);

                HBox iconBox = new HBox(0);
                iconBox.setAlignment(Pos.CENTER);
                iconBox.getChildren().add(makeStepIcon(stepSize, state, i + 1));

                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 60, 0);
                    line.getStyleClass().add(CssClasses.STEPS_LINE);
                    if (state == State.FINISHED) {
                        line.getStyleClass().add(CssClasses.STEPS_STATE_FINISHED);
                    }
                    iconBox.getChildren().add(line);
                }
                stepBox.getChildren().add(iconBox);

                stepBox.getChildren().add(makeTitle(step.title, state));
                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.getStyleClass().add(CssClasses.STEPS_DESCRIPTION);
                    stepBox.getChildren().add(descLabel);
                }
                container.getChildren().add(stepBox);
            }
            return container;
        }

        private VBox buildVertical() {
            VBox container = new VBox(0);
            container.setAlignment(Pos.TOP_LEFT);
            container.getStyleClass().add(CssClasses.STEPS_VERTICAL);

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                State state = stateFor(i);

                HBox stepBox = new HBox(12);
                stepBox.setAlignment(Pos.TOP_LEFT);
                stepBox.getStyleClass().add(CssClasses.STEPS_ITEM);

                VBox leftBox = new VBox(0);
                leftBox.setAlignment(Pos.TOP_CENTER);
                leftBox.setPrefWidth(stepSize);
                leftBox.getChildren().add(makeStepIcon(stepSize, state, i + 1));

                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 0, 40);
                    line.getStyleClass().add(CssClasses.STEPS_LINE);
                    if (state == State.FINISHED) {
                        line.getStyleClass().add(CssClasses.STEPS_STATE_FINISHED);
                    }
                    leftBox.getChildren().add(line);
                }
                stepBox.getChildren().add(leftBox);

                VBox contentBox = new VBox(4);
                contentBox.setAlignment(Pos.TOP_LEFT);
                contentBox.getChildren().add(makeTitle(step.title, state));
                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.getStyleClass().add(CssClasses.STEPS_DESCRIPTION);
                    contentBox.getChildren().add(descLabel);
                }
                stepBox.getChildren().add(contentBox);
                container.getChildren().add(stepBox);
            }
            return container;
        }

        /** 生成圆圈 + 数字（颜色由 LESS 状态修饰类切换）*/
        private StackPane makeStepIcon(int stepSize, State state, int number) {
            Circle circle = new Circle(stepSize / 2.0);
            circle.getStyleClass().add(CssClasses.STEPS_CIRCLE);
            circle.getStyleClass().add(stateClass(state));

            Label numberLabel = new Label(String.valueOf(number));
            numberLabel.getStyleClass().add(CssClasses.STEPS_NUMBER);
            numberLabel.getStyleClass().add(stateClass(state));
            // font-size 与 stepSize 联动（动态属性，留 inline）
            numberLabel.setStyle("-fx-font-size: " + (stepSize * 0.4) + "px;");

            StackPane iconPane = new StackPane();
            iconPane.getChildren().addAll(circle, numberLabel);
            return iconPane;
        }

        private Label makeTitle(String text, State state) {
            Label titleLabel = new Label(text);
            titleLabel.getStyleClass().add(CssClasses.STEPS_TITLE);
            titleLabel.getStyleClass().add(stateClass(state));
            return titleLabel;
        }

        private State stateFor(int i) {
            if (i < current) return State.FINISHED;
            if (i == current) return State.CURRENT;
            return State.WAIT;
        }

        private static String stateClass(State state) {
            return switch (state) {
                case FINISHED -> CssClasses.STEPS_STATE_FINISHED;
                case CURRENT -> CssClasses.STEPS_STATE_CURRENT;
                case WAIT -> CssClasses.STEPS_STATE_WAIT;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
