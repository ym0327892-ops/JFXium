package org.openkawu.jfxium.component.control;

import javafx.scene.control.Hyperlink;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 超链接组件 - 对标 Ant Design Typography.Link。
 *
 * <p><b>定位</b>：文本超链接，支持点击跳转、回调、禁用状态。
 * JavaFX 原生有 {@link Hyperlink}，JFXium 提供 Builder 流式 API + 主题化封装。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>文本</b>：text(String) 链接文案</li>
 *   <li><b>点击回调</b>：onClick(action) 点击时触发</li>
 *   <li><b>禁用</b>：disabled(true)</li>
 *   <li><b>下划线</b>：underline(true) 始终显示下划线</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#HYPERLINK} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 基础超链接
 * HyperlinkAnt link = HyperlinkAnt.create("查看文档")
 *     .onClick(() -> openBrowser("https://docs.example.com"))
 *     .build();
 *
 * // 禁用状态
 * HyperlinkAnt disabled = HyperlinkAnt.create("已失效")
 *     .disabled(true)
 *     .build();
 * }</pre>
 *
 * @see LabelAnt 普通文本标签（不可点击）
 * @see ButtonAnt.Type#LINK 链接风格按钮（带按钮样式）
 */
public class HyperlinkAnt extends Hyperlink {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static HyperlinkAnt create(String text) {
        return new HyperlinkAnt(text);
    }

    public static HyperlinkAnt create() {
        return new HyperlinkAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public HyperlinkAnt() {
        super();
        getStyleClass().add(JfxStyles.HYPERLINK);
    }

    public HyperlinkAnt(String text) {
        super(text);
        getStyleClass().add(JfxStyles.HYPERLINK);
    }

    // ============================================================
    // 链式配置
    // ============================================================

    public HyperlinkAnt text(String text) {
        setText(text);
        return this;
    }

    public HyperlinkAnt onClick(Runnable action) {
        if (action != null) {
            setOnAction(e -> action.run());
        }
        return this;
    }

    public HyperlinkAnt onClick(Consumer<HyperlinkAnt> action) {
        if (action != null) {
            setOnAction(e -> action.accept(this));
        }
        return this;
    }

    public HyperlinkAnt disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    public HyperlinkAnt underline(boolean underline) {
        setUnderline(underline);
        return this;
    }

    public HyperlinkAnt visited(boolean visited) {
        setVisited(visited);
        return this;
    }

    // ============================================================
    // 构建（可选，用于统一 API 风格）
    // ============================================================

    public HyperlinkAnt build() {
        return this;
    }
}
