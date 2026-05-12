package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.NotificationCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public enum Placement {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private static final Map<Placement, NotificationContainer> containers = new HashMap<>();

    static {
        for (Placement p : Placement.values()) {
            containers.put(p, new NotificationContainer(p));
        }
    }

    private static class NotificationEntry {
        final Popup popup;
        final VBox box;

        NotificationEntry(Popup popup, VBox box) {
            this.popup = popup;
            this.box = box;
        }
    }

    private static class NotificationContainer {
        final Placement placement;
        final VBox container;
        final Popup popup;
        final List<NotificationEntry> entries = new ArrayList<>();
        Window window;

        NotificationContainer(Placement placement) {
            this.placement = placement;
            this.container = new VBox(10);
            this.container.setAlignment(Pos.TOP_LEFT);
            this.container.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

            this.popup = new Popup();
            this.popup.getContent().add(container);
            this.popup.setAutoHide(false);
            this.popup.setHideOnEscape(false);
        }

        void addEntry(NotificationEntry entry) {
            entries.add(entry);
            container.getChildren().add(entry.box);
            updatePosition();
        }

        void removeEntry(NotificationEntry entry) {
            entries.remove(entry);
            container.getChildren().remove(entry.box);
            updatePosition();
        }

        void updatePosition() {
            if (window == null || !window.isShowing()) return;

            double x, y;
            double margin = 24;
            double width = 384;

            Bounds bounds = window.getScene().getRoot().getLayoutBounds();
            switch (placement) {
                case TOP_LEFT -> {
                    x = window.getX() + margin;
                    y = window.getY() + margin;
                }
                case TOP_RIGHT -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + margin;
                }
                case BOTTOM_LEFT -> {
                    x = window.getX() + margin;
                    y = window.getY() + window.getHeight() - margin - container.getHeight();
                }
                case BOTTOM_RIGHT -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + window.getHeight() - margin - container.getHeight();
                }
                default -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + margin;
                }
            }

            if (!popup.isShowing()) {
                popup.show(window);
            }
            popup.setX(x);
            popup.setY(y);
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
            Window window = Window.getWindows().stream()
                .filter(Window::isShowing)
                .filter(w -> w instanceof javafx.stage.Stage)
                .findFirst()
                .orElse(null);

            if (window == null) return;

            NotificationContainer container = containers.get(config.placement);
            container.window = window;

            NotificationCard.Builder cardBuilder = new NotificationCard.Builder()
                .title(config.title)
                .description(config.description)
                .type(convertType(config.type))
                .closable(config.closable)
                .content(config.content);

            VBox notificationBox = cardBuilder.build();

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.getContent().add(notificationBox);

            NotificationEntry entry = new NotificationEntry(popup, notificationBox);

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

            if (config.durationSeconds > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(config.durationSeconds));
                delay.setOnFinished(e -> hide(entry, config));
                delay.play();
            }

            if (config.closable) {
                notificationBox.setOnMouseClicked(e -> {
                    if (config.onClick != null) {
                        config.onClick.accept(null);
                    }
                    hide(entry, config);
                });
            }

            container.addEntry(entry);
        });
    }

    private static void hide(NotificationEntry entry, Builder config) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            entry.popup.hide();
            NotificationContainer container = containers.get(config.placement);
            container.removeEntry(entry);
        });
        fadeOut.play();
    }

    private static NotificationCard.Type convertType(Type type) {
        return switch (type) {
            case SUCCESS -> NotificationCard.Type.SUCCESS;
            case ERROR -> NotificationCard.Type.ERROR;
            case WARNING -> NotificationCard.Type.WARNING;
            case INFO -> NotificationCard.Type.INFO;
        };
    }
}
