module org.openkawu.jfxium {
    requires javafx.controls;
    requires javafx.fxml;
    // M19.18 i18n 入口 Messages.java 使用 java.util.logging 打缺失 key 的 WARNING
    requires java.logging;

    exports org.openkawu.jfxium.core.token;
    exports org.openkawu.jfxium.core.theme;
    exports org.openkawu.jfxium.core.css;
    exports org.openkawu.jfxium.core.animation;
    exports org.openkawu.jfxium.core.layout;
    exports org.openkawu.jfxium.core.i18n;
    exports org.openkawu.jfxium.component;
    exports org.openkawu.jfxium.component.base;
}
