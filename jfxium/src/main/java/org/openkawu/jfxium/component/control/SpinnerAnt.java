package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.SpinAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 简单加载旋转组件 - 对标 Ant Design Spin 的 SPINNER 模式。
 *
 * <p><b>定位</b>：SpinAnt 的简化入口，仅暴露 {@code size()} 一个参数，
 * 内部委托 {@link SpinAnt} 的 SPINNER 指示器 + Timeline 自驱动动画，
 * 确保动画效果与 SpinAnt 完全一致。</p>
 *
 * <p>如需 tip 文字 / DOTS/BARS 指示器 / fullscreen / 内容包裹，请直接使用 {@link SpinAnt}。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 默认尺寸（32×32）
 * Node spinner = SpinnerAnt.create().build();
 *
 * // 自定义尺寸
 * Node large = SpinnerAnt.create().size(64).build();
 * }</pre>
 */
public class SpinnerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private double size = 32;

        private Builder() {}

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        /**
         * 委托 {@link SpinAnt} 的 SPINNER 模式构建旋转指示器。
         * 返回 {@link VBox}（与 SpinAnt 一致），不是 ProgressIndicator。
         */
        public VBox build() {
            SpinAnt.Size spinSize;
            if (size <= 24)       spinSize = SpinAnt.Size.SMALL;
            else if (size >= 48)  spinSize = SpinAnt.Size.LARGE;
            else                  spinSize = SpinAnt.Size.DEFAULT;

            VBox spin = SpinAnt.create()
                    .indicator(SpinAnt.Indicator.SPINNER)
                    .size(spinSize)
                    .build();
            applyStyles(spin);
            return spin;
        }

    }
}
