package org.openkawu.jfxium;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

/**
 * JavaFX 测试基类
 * 初始化 JavaFX 工具包
 */
public abstract class JavaFXTestBase {

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already initialized
        }
    }
}