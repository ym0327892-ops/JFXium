package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Steps Component
 * Inspired by Ant Design Steps
 */
public class JFXSteps {

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public enum Size {
        SMALL, DEFAULT
    }

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

        public Builder step(String title) {
            steps.add(new Step(title, null, null));
            return this;
        }

        public Builder step(String title, String description) {
            steps.add(new Step(title, description, null));
            return this;
        }

        public Builder step(String title, String description, String icon) {
            steps.add(new Step(title, description, icon));
            return this;
        }

        public Builder current(int current) {
            this.current = current;
            return this;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public javafx.scene.Node build() {
            if (direction == Direction.HORIZONTAL) {
                return buildHorizontal();
            } else {
                return buildVertical();
            }
        }

        private HBox buildHorizontal() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.TOP_CENTER);
            container.getStyleClass().add("steps");
            container.setStyle("-fx-padding: 16px 0;");

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                boolean isFinished = i < current;
                boolean isCurrent = i == current;
                boolean isWait = i > current;

                VBox stepBox = new VBox(8);
                stepBox.setAlignment(Pos.CENTER);
                stepBox.setPrefWidth(200);
                HBox.setHgrow(stepBox, javafx.scene.layout.Priority.ALWAYS);

                // Icon/Number
                HBox iconBox = new HBox(0);
                iconBox.setAlignment(Pos.CENTER);

                Circle circle = new Circle(stepSize / 2.0);
                String bgColor, textColor;

                if (isFinished) {
                    bgColor = "-color-accent-emphasis";
                    textColor = "-color-fg-on-emphasis";
                } else if (isCurrent) {
                    bgColor = "-color-bg-default";
                    textColor = "-color-accent-emphasis";
                } else {
                    bgColor = "-color-bg-default";
                    textColor = "-color-fg-subtle";
                }

                circle.setStyle("-fx-fill: " + bgColor + "; -fx-stroke: " + (isCurrent ? "-color-accent-emphasis" : "-color-border-default") + "; -fx-stroke-width: 1px;");

                Label numberLabel = new Label(String.valueOf(i + 1));
                numberLabel.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: " + (stepSize * 0.4) + "px; -fx-font-weight: 600;");

                StackPane iconPane = new StackPane();
                iconPane.getChildren().addAll(circle, numberLabel);
                iconBox.getChildren().add(iconPane);

                // Connector line
                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 60, 0);
                    line.setStyle("-fx-stroke: " + (isFinished ? "-color-accent-emphasis" : "-color-border-muted") + "; -fx-stroke-width: 1px;");
                    iconBox.getChildren().add(line);
                }

                stepBox.getChildren().add(iconBox);

                // Title
                Label titleLabel = new Label(step.title);
                titleLabel.setStyle("-fx-text-fill: " + (isWait ? "-color-fg-subtle" : "-color-fg-default") + "; -fx-font-size: 14px; -fx-font-weight: " + (isCurrent ? "600" : "400") + ";");
                stepBox.getChildren().add(titleLabel);

                // Description
                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                    stepBox.getChildren().add(descLabel);
                }

                container.getChildren().add(stepBox);
            }

            return container;
        }

        private VBox buildVertical() {
            VBox container = new VBox(0);
            container.setAlignment(Pos.TOP_LEFT);
            container.getStyleClass().add("steps-vertical");
            container.setStyle("-fx-padding: 16px;");

            int stepSize = size == Size.SMALL ? 24 : 32;

            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                boolean isFinished = i < current;
                boolean isCurrent = i == current;
                boolean isWait = i > current;

                HBox stepBox = new HBox(12);
                stepBox.setAlignment(Pos.TOP_LEFT);
                stepBox.setStyle("-fx-padding: 8px 0;");

                // Left: Icon/Number + connector
                VBox leftBox = new VBox(0);
                leftBox.setAlignment(Pos.TOP_CENTER);
                leftBox.setPrefWidth(stepSize);

                Circle circle = new Circle(stepSize / 2.0);
                String bgColor, textColor;

                if (isFinished) {
                    bgColor = "-color-accent-emphasis";
                    textColor = "-color-fg-on-emphasis";
                } else if (isCurrent) {
                    bgColor = "-color-bg-default";
                    textColor = "-color-accent-emphasis";
                } else {
                    bgColor = "-color-bg-default";
                    textColor = "-color-fg-subtle";
                }

                circle.setStyle("-fx-fill: " + bgColor + "; -fx-stroke: " + (isCurrent ? "-color-accent-emphasis" : "-color-border-default") + "; -fx-stroke-width: 1px;");

                Label numberLabel = new Label(String.valueOf(i + 1));
                numberLabel.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: " + (stepSize * 0.4) + "px; -fx-font-weight: 600;");

                StackPane iconPane = new StackPane();
                iconPane.getChildren().addAll(circle, numberLabel);
                leftBox.getChildren().add(iconPane);

                // Connector line
                if (i < steps.size() - 1) {
                    Line line = new Line(0, 0, 0, 40);
                    line.setStyle("-fx-stroke: " + (isFinished ? "-color-accent-emphasis" : "-color-border-muted") + "; -fx-stroke-width: 1px;");
                    leftBox.getChildren().add(line);
                }

                stepBox.getChildren().add(leftBox);

                // Right: Content
                VBox contentBox = new VBox(4);
                contentBox.setAlignment(Pos.TOP_LEFT);

                Label titleLabel = new Label(step.title);
                titleLabel.setStyle("-fx-text-fill: " + (isWait ? "-color-fg-subtle" : "-color-fg-default") + "; -fx-font-size: 14px; -fx-font-weight: " + (isCurrent ? "600" : "400") + ";");
                contentBox.getChildren().add(titleLabel);

                if (step.description != null) {
                    Label descLabel = new Label(step.description);
                    descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                    contentBox.getChildren().add(descLabel);
                }

                stepBox.getChildren().add(contentBox);
                container.getChildren().add(stepBox);
            }

            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
