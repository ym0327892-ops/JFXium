package org.openkawu.jfxium.core.util;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 布局辅助工具。
 * 提供弹性占位和固定尺寸占位功能。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 弹性占位（自动填充剩余空间）
 * Region spacer = Spacers.grow();
 *
 * // 固定尺寸占位
 * Region spacer = Spacers.spacer(100, 20);
 * }</pre>
 */
public final class Spacers {

    /**
     * 创建弹性占位区域。
     * 该 Region 会在 HBox 和 VBox 中自动填充剩余空间。
     *
     * @return 弹性占位 Region
     */
    public static Region grow() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        VBox.setVgrow(region, Priority.ALWAYS);
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
        return region;
    }

    /**
     * 创建固定尺寸占位区域。
     *
     * @param width  宽度
     * @param height 高度
     * @return 固定尺寸占位 Region
     */
    public static Region spacer(double width, double height) {
        Region region = new Region();
        region.setPrefSize(width, height);
        region.setMinSize(width, height);
        region.setMaxSize(width, height);
        return region;
    }
}
