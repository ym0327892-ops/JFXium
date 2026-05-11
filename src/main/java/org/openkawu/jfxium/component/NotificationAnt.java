package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JFXium Notification Component
 * Inspired by Ant Design Notification
 * A notification message displayed at the corner of the screen.
 * Notifications stack vertically at their placement corner.
 */
public class NotificationAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public enum Placement {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    // Track active notifications per placement
    private static final Map<Placement, List<NotificationEntry>> activeNotifications = new HashMap<>();
    private static final int SPACING = 70; // Vertical spacing between notifications

    static {
        for (Placement p : Placement.values()) {
            activeNotifications.put(p, new ArrayList<>());
        }
    }

    private static class NotificationEntry {
        final Popup popup;
        final HBox box;

        NotificationEntry(Popup popup, HBox box) {
            this.popup = popup;
            this.box = box;
        }
    }

    public static class Builder {
        private String title = "";
        private String description = "";
        private Node content = null;
        private Type type = Type.INFO;
        private Placement placement = Placement.TOP_RIGHT;
        private int durationSeconds = 4;
        private boolean closable = true;
        private Consumer<Void> onClose = null;
        private Consumer<Void> onClick = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder duration(int seconds) {
            this.durationSeconds = seconds;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder onClose(Consumer<Void> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder onClick(Consumer<Void> onClick) {
            this.onClick = onClick;
            return this;
        }

        public void show() {
            NotificationAnt.show(this);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    // Convenience methods
    public static void success(String title, String description) {
        create().title(title).description(description).type(Type.SUCCESS).show();
    }

    public static void error(String title, String description) {
        create().title(title).description(description).type(Type.ERROR).show();
    }

    public static void warning(String title, String description) {
        create().title(title).description(description).type(Type.WARNING).show();
    }

    public static void info(String title, String description) {
        create().title(title).description(description).type(Type.INFO).show();
    }

    private static void show(Builder config) {
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Window window = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(w -> w instanceof javafx.stage.Stage)
                .findFirst()
                .orElse(null);

            if (window == null) return;

            List<NotificationEntry> list = activeNotifications.get(config.placement);

            // Create notification panel
            HBox notificationBox = createNotificationBox(config);

            // Create popup
            Popup popup = new Popup();
            popup.getContent().add(notificationBox);

            // Calculate position based on placement
            double[] pos = calculatePosition(window, config.placement, list.size());
            popup.setX(pos[0]);
            popup.setY(pos[1]);

            popup.show(window);

            // Add to active list
            NotificationEntry entry = new NotificationEntry(popup, notificationBox);
            list.add(entry);

            // Animate in
            notificationBox.setOpacity(0);
            boolean fromLeft = config.placement == Placement.TOP_LEFT || config.placement == Placement.BOTTOM_LEFT;
            notificationBox.setTranslateX(fromLeft ? -20 : 20);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), notificationBox);
            slideIn.setFromX(fromLeft ? -20 : 20);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fadeIn, slideIn);
            pt.play();

            // Auto hide
            if (config.durationSeconds > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(config.durationSeconds));
                delay.setOnFinished(e -> hide(entry, config));
                delay.play();
            }

            // Click to close
            if (config.closable) {
                notificationBox.setOnMouseClicked(e -> {
                    if (config.onClick != null) {
                        config.onClick.accept(null);
                    }
                    hide(entry, config);
                });
            }
        });
    }

    private static double[] calculatePosition(javafx.stage.Window window, Placement placement, int index) {
        double x, y;
        double width = 384; // Notification width
        double margin = 24;

        switch (placement) {
            case TOP_LEFT -> {
                x = window.getX() + margin;
                y = window.getY() + margin + (index * SPACING);
            }
            case TOP_RIGHT -> {
                x = window.getX() + window.getWidth() - width - margin;
                y = window.getY() + margin + (index * SPACING);
            }
            case BOTTOM_LEFT -> {
                x = window.getX() + margin;
                y = window.getY() + window.getHeight() - margin - (index * SPACING) - 80;
            }
            case BOTTOM_RIGHT -> {
                x = window.getX() + window.getWidth() - width - margin;
                y = window.getY() + window.getHeight() - margin - (index * SPACING) - 80;
            }
            default -> {
                x = window.getX() + window.getWidth() - width - margin;
                y = window.getY() + margin + (index * SPACING);
            }
        }
        return new double[]{x, y};
    }

    private static void hide(NotificationEntry entry, Builder config) {
        List<NotificationEntry> list = activeNotifications.get(config.placement);
        if (!list.contains(entry)) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            entry.popup.hide();
            list.remove(entry);
            repositionNotifications(config.placement);
        });
        fadeOut.play();
    }

    private static void repositionNotifications(Placement placement) {
        javafx.stage.Window window = javafx.stage.Window.getWindows().stream()
            .filter(javafx.stage.Window::isShowing)
            .filter(w -> w instanceof javafx.stage.Stage)
            .findFirst()
            .orElse(null);

        if (window == null) return;

        List<NotificationEntry> list = activeNotifications.get(placement);
        for (int i = 0; i < list.size(); i++) {
            double[] pos = calculatePosition(window, placement, i);
            list.get(i).popup.setX(pos[0]);
            list.get(i).popup.setY(pos[1]);
        }
    }

    private static HBox createNotificationBox(Builder config) {
        HBox box = new HBox(12);
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle(
            "-fx-background-color: -color-bg-overlay;" +
            "-fx-padding: 16px 24px;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-color: -color-border-default;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
            "-fx-min-width: 384px;" +
            "-fx-max-width: 384px;"
        );

        // Icon
        SVGPath icon = new SVGPath();
        icon.setContent(getIconPath(config.type));
        icon.setStyle("-fx-fill: " + getIconColor(config.type) + ";");
        icon.setTranslateY(2);
        box.getChildren().add(icon);

        // Content
        VBox contentBox = new VBox(4);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        if (!config.title.isEmpty()) {
            Label titleLabel = new Label(config.title);
            titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 16px; -fx-font-weight: 600;");
            contentBox.getChildren().add(titleLabel);
        }

        if (!config.description.isEmpty()) {
            Label descLabel = new Label(config.description);
            descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px;");
            descLabel.setWrapText(true);
            contentBox.getChildren().add(descLabel);
        }

        if (config.content != null) {
            contentBox.getChildren().add(config.content);
        }

        box.getChildren().add(contentBox);

        return box;
    }

    private static String getIconPath(Type type) {
        return switch (type) {
            case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
            case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
            case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
            case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
        };
    }

    private static String getIconColor(Type type) {
        return switch (type) {
            case SUCCESS -> "-color-success-emphasis";
            case ERROR -> "-color-danger-emphasis";
            case WARNING -> "-color-warning-emphasis";
            case INFO -> "-color-accent-emphasis";
        };
    }
}
