module org.openkawu.jfxium {
    requires javafx.controls;
    requires javafx.fxml;
    // M19.18 i18n 入口 Messages.java 使用 java.util.logging 打缺失 key 的 WARNING
    requires java.logging;


    exports org.openkawu.jfxium;
    exports org.openkawu.jfxium.core.token;
    exports org.openkawu.jfxium.core.theme;
    exports org.openkawu.jfxium.core.builder;
    exports org.openkawu.jfxium.core.style;
    exports org.openkawu.jfxium.core.layout;
    exports org.openkawu.jfxium.core.i18n;
    exports org.openkawu.jfxium.core.command;
    exports org.openkawu.jfxium.core.form;
    exports org.openkawu.jfxium.core.util;
    exports org.openkawu.jfxium.component.control;
    exports org.openkawu.jfxium.component.composite;
    exports org.openkawu.jfxium.component.overlay;
    exports org.openkawu.jfxium.component.base;
    exports org.openkawu.jfxium.component.layout;
    exports org.openkawu.jfxium.layout;
    exports org.openkawu.jfxium.template;
}
