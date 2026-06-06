package org.openkawu.jfxium.component.control;

import javafx.scene.control.ProgressIndicator;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 加载旋转组件 - 对标 Ant Design Spin（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：加载指示器，包装 JavaFX {@link ProgressIndicator}（不确定进度旋转模式），
 * 用于表示后台操作正在进行中。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>可自定义尺寸（默认 32×32）</li>
 *   <li>继承 {@link AbstractStyleBuilder}，支持 {@code .styleClass()} / {@code .style()}</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>页面加载占位（居中显示）</li>
 *   <li>按钮加载状态（配合 ButtonAnt.loading）</li>
 *   <li>对话框内容加载中（嵌入 ModalAnt/DrawerAnt）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 默认尺寸（32×32）
 * ProgressIndicator spinner = SpinnerAnt.create().build();
 *
 * // 自定义尺寸
 * ProgressIndicator large = SpinnerAnt.create()
 *     .size(64)
 *     .build();
 * }</pre>
 *
 * <h2>与 SpinAnt 的区别</h2>
 * <ul>
 *   <li>{@code SpinnerAnt} —— 简单加载旋转（包装原生 ProgressIndicator）</li>
 *   <li>{@code SpinAnt} —— 对标 Ant Design Spin，支持加载内容包裹、自定义指示器</li>
 * </ul>
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

        public ProgressIndicator build() {
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setPrefSize(size, size);
            spinner.getStyleClass().add("jfx-spinner");
            applyStyles(spinner);
            return spinner;
        }
    }
}
