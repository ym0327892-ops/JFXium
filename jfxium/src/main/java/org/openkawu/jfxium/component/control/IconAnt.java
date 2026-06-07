package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 极简内置图标。两种渲染模式：
 * <ul>
 *   <li>{@link Symbol} —— Unicode 字符模式，仅用于窗口控制等基础符号（CLOSE/CHECK/箭头等）</li>
 *   <li>{@link Path} —— SVG path 模式（M11 新增），用于业务图标（USER/SETTINGS/CHART 等）</li>
 * </ul>
 *
 * <p>SVG path 数据来自 Material Icons / Ant Design Icons（MIT），24x24 viewBox。
 * 通过 JavaFX {@code -fx-shape} 渲染，零依赖、矢量缩放清晰。</p>
 *
 * <h3>使用</h3>
 * <pre>{@code
 * // 基础符号
 * Node closeIcon = IconAnt.symbol(IconAnt.Symbol.CLOSE);
 *
 * // 业务图标
 * Node userIcon = IconAnt.path(IconAnt.Path.USER);
 * Node bigChart = IconAnt.path(IconAnt.Path.CHART, 24);
 * }</pre>
 *
 * <h3>颜色控制</h3>
 * 颜色由 LESS 统一管理，禁止在 Java 端写死：
 * <ul>
 *   <li>{@code .jfx-icon}（Symbol 模式）—— {@code -fx-text-fill}</li>
 *   <li>{@code .jfx-icon-path}（Path 模式）—— {@code -fx-background-color}</li>
 * </ul>
 * 默认色 {@code -color-fg-default}，需主题色等其它变体请挂额外 styleClass。
 *
 */
public class IconAnt {

    /** Unicode 字符图标（窗口控制、箭头、勾选等基础符号）。 */
    public enum Symbol {
        CLOSE("\u00d7"),
        MAXIMIZE("\u25a1"),
        MINIMIZE("\u2013"),
        RESTORE("\u2750"),
        CHECK("\u2713"),
        CROSS("\u2715"),
        INFO("\u2139"),
        WARNING("\u26a0"),
        ARROW_UP("\u2191"),
        ARROW_DOWN("\u2193"),
        ARROW_LEFT("\u2190"),
        ARROW_RIGHT("\u2192"),
        PLUS("+"),
        MINUS("\u2212"),
        DOT("\u2022"),
        MENU("\u2630");

        private final String ch;

        Symbol(String ch) {
            this.ch = ch;
        }

        public String getChar() {
            return ch;
        }
    }

    /**
     * SVG path 业务图标。所有 path 都是 24×24 viewBox。
     * 来源：Material Icons / Ant Design Icons（MIT License）。
     */
    public enum Path {
        /** 仪表盘（柱状指示） */
        DASHBOARD("M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"),

        /** 单人 */
        USER("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"),

        /** 多人（团队） */
        USERS("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"),

        /** 设置（齿轮） */
        SETTINGS("M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94 0 .31.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"),

        /** 退出登录（向右箭头出门） */
        LOGOUT("M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"),

        /** 通知铃铛 */
        BELL("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"),

        /** 搜索（放大镜） */
        SEARCH("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"),

        /** 首页（房子） */
        HOME("M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"),

        /** 文件 */
        FILE("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"),

        /** 柱状图 */
        CHART("M5 9.2h3V19H5zM10.6 5h2.8v14h-2.8zm5.6 8H19v6h-2.8z"),

        /** 加号（PLUS 的 SVG 版，用于按钮内嵌） */
        PLUS("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"),

        /** 编辑（笔） */
        EDIT("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"),

        /** 删除（垃圾桶） */
        DELETE("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");

        private final String svgPath;

        Path(String svgPath) {
            this.svgPath = svgPath;
        }

        public String getPath() {
            return svgPath;
        }
    }

    public static Node symbol(Symbol symbol) {
        return symbol(symbol, 16);
    }

    public static Node symbol(Symbol symbol, int size) {
        Label label = new Label(symbol.getChar());
        // Font 是结构性属性（字体族 + 字号），保留在 Java；颜色走 LESS
        label.setFont(Font.font("Segoe UI Symbol", size));
        label.getStyleClass().add(JfxStyles.ICON);
        StackPane pane = new StackPane(label);
        pane.setPrefSize(size, size);
        return pane;
    }

    /** SVG 业务图标，默认 16px。 */
    public static Region path(Path icon) {
        return path(icon, 16);
    }

    /**
     * SVG 业务图标，自定义尺寸。
     *
     * <p>实现说明：用 {@code Region} + {@code -fx-shape} 渲染 SVG path。
     * 颜色由 LESS {@code .jfx-icon-path} 控制（默认 {@code -color-fg-default}）。
     * shape 字符串太长，无法用 styleClass 维护，故 {@code -fx-shape} 写在 inline style；
     * 颜色不在 inline，避免硬编码（符合 SKILL #1 变量优先原则）。</p>
     */
    public static Region path(Path icon, int size) {
        Region node = new Region();
        node.getStyleClass().add(JfxStyles.ICON_PATH);
        // -fx-shape 是结构性属性（图形定义），不是颜色，可以走 inline
        node.setStyle("-fx-shape: \"" + icon.getPath() + "\";");
        node.setPrefSize(size, size);
        node.setMinSize(size, size);
        node.setMaxSize(size, size);
        return node;
    }
}
