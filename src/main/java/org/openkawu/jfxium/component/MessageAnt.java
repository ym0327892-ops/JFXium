package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.MessageCard;

import java.util.ArrayList;
import java.util.List;

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

            while (activeMessages.size() >= MAX_MESSAGES) {
                MessageEntry oldest = activeMessages.remove(0);
                hideMessage(oldest);
            }

            HBox messageBox = new MessageCard.Builder()
                .content(content)
                .type(convertType(type))
                .build();

            Popup popup = new Popup();
            popup.getContent().add(messageBox);

            double x = window.getX() + (window.getWidth() - 300) / 2;
            double y = window.getY() + 24 + (activeMessages.size() * 50);

            popup.setX(x);
            popup.setY(y);

            popup.show(window);

            MessageEntry entry = new MessageEntry(popup, messageBox);
            activeMessages.add(entry);

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

    private static MessageCard.Type convertType(Type type) {
        return switch (type) {
            case SUCCESS -> MessageCard.Type.SUCCESS;
            case ERROR -> MessageCard.Type.ERROR;
            case WARNING -> MessageCard.Type.WARNING;
            case INFO -> MessageCard.Type.INFO;
            case LOADING -> MessageCard.Type.LOADING;
        };
    }
}
