package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Message Component
 * Inspired by Ant Design Message
 * A lightweight feedback message displayed at the top of the screen.
 * Messages stack vertically, max 5 visible at a time.
 */
public class MessageAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO, LOADING
    }

    private static final int MAX_MESSAGES = 5;
    private static final List<MessageEntry> activeMessages = new ArrayList<>();

    private static class MessageEntry {
        final Popup popup;
        final HBox box;
        final long showTime;

        MessageEntry(Popup popup, HBox box) {
            this.popup = popup;
            this.box = box;
            this.showTime = System.currentTimeMillis();
        }
    }

    public static void show(String content) {
        show(content, Type.INFO, 3);
    }

    public static void success(String content) {
        show(content, Type.SUCCESS, 3);
    }

    public static void error(String content) {
        show(content, Type.ERROR, 3);
    }

    public static void warning(String content) {
        show(content, Type.WARNING, 3);
    }

    public static void info(String content) {
        show(content, Type.INFO, 3);
    }

    public static void loading(String content) {
        show(content, Type.LOADING, 0);
    }

    public static void show(String content, Type type, int durationSeconds) {
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Window window = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(w -> w instanceof javafx.stage.Stage)
                .findFirst()
                .orElse(null);

            if (window == null) return;

            // Remove oldest if exceeding max
            while (activeMessages.size() >= MAX_MESSAGES) {
                MessageEntry oldest = activeMessages.remove(0);
                hideMessage(oldest);
            }

            // Create message panel
            HBox messageBox = createMessageBox(content, type);

            // Create popup
            Popup popup = new Popup();
            popup.getContent().add(messageBox);

            // Calculate position - stack vertically
            double x = window.getX() + (window.getWidth() - 300) / 2; // Center horizontally, assume width ~300
            double y = window.getY() + 24 + (activeMessages.size() * 50); // Stack down, 50px spacing

            popup.setX(x);
            popup.setY(y);

            popup.show(window);

            // Add to active list
            MessageEntry entry = new MessageEntry(popup, messageBox);
            activeMessages.add(entry);

            // Animate in
            messageBox.setOpacity(0);
            messageBox.setTranslateY(-20);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), messageBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), messageBox);
            slideIn.setFromY(-20);
            slideIn.setToY(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fadeIn, slideIn);
            pt.play();

            // Auto hide
            if (durationSeconds > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(durationSeconds));
                delay.setOnFinished(e -> {
                    activeMessages.remove(entry);
                    hideMessage(entry);
                    repositionMessages(window);
                });
                delay.play();
            }
        });
    }

    private static void hideMessage(MessageEntry entry) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> entry.popup.hide());
        fadeOut.play();
    }

    private static void repositionMessages(javafx.stage.Window window) {
        double baseX = window.getX() + (window.getWidth() - 300) / 2;
        double baseY = window.getY() + 24;
        for (int i = 0; i < activeMessages.size(); i++) {
            MessageEntry entry = activeMessages.get(i);
            entry.popup.setX(baseX);
            entry.popup.setY(baseY + (i * 50));
        }
    }

    private static HBox createMessageBox(String content, Type type) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        box.setStyle(
            "-fx-background-color: -color-bg-overlay;" +
            "-fx-padding: 10px 16px;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-color: -color-border-default;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        // Icon
        if (type != Type.LOADING) {
            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(type));
            icon.setStyle("-fx-fill: " + getIconColor(type) + ";");
            box.getChildren().add(icon);
        }

        // Content
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px;");
        box.getChildren().add(contentLabel);

        return box;
    }

    private static String getIconPath(Type type) {
        return switch (type) {
            case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
            case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
            case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
            case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
            case LOADING -> "M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z";
        };
    }

    private static String getIconColor(Type type) {
        return switch (type) {
            case SUCCESS -> "-color-success-emphasis";
            case ERROR -> "-color-danger-emphasis";
            case WARNING -> "-color-warning-emphasis";
            case INFO -> "-color-accent-emphasis";
            case LOADING -> "-color-accent-emphasis";
        };
    }
}
