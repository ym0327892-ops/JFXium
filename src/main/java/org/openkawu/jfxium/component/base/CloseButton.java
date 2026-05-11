package org.openkawu.jfxium.component.base;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * 可复用的关闭按钮组件
 * 
 * 用于 Modal, Drawer, Message, Notification, Popover 等
 */
public class CloseButton extends Button {

    public CloseButton() {
        this(null);
    }

    public CloseButton(Runnable onClose) {
        setText("\u00d7");
        getStyleClass().add("close-button");
        
        setOnAction(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });
    }

    public static CloseButton create() {
        return new CloseButton();
    }

    public static CloseButton create(Runnable onClose) {
        return new CloseButton(onClose);
    }
}
