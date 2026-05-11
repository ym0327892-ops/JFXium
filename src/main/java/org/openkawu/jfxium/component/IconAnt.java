package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * JFXium 极简内置图标 - 仅提供窗口控制等最基础符号
 *
 * 基于 Unicode 字符，零依赖，零 SVG。
 * 如需丰富图标，请引入 Ikonli 库：
 * <pre>{@code
 * // Ikonli 示例
 * import org.kordamp.ikonli.javafx.FontIcon;
 * import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
 *
 * FontIcon icon = new FontIcon(AntDesignIconsFilled.HOME);
 * icon.setIconSize(24);
 * }</pre>
 *
 * 本类内置符号：
 * <pre>{@code
 * // 窗口控制
 * Node close = IconAnt.symbol(IconAnt.Symbol.CLOSE);      // ×
 * Node max = IconAnt.symbol(IconAnt.Symbol.MAXIMIZE);     // □
 * Node min = IconAnt.symbol(IconAnt.Symbol.MINIMIZE);     // –
 * Node restore = IconAnt.symbol(IconAnt.Symbol.RESTORE);  // ❐
 *
 * // 状态
 * Node check = IconAnt.symbol(IconAnt.Symbol.CHECK);      // ✓
 * Node cross = IconAnt.symbol(IconAnt.Symbol.CROSS);      // ✕
 * Node info = IconAnt.symbol(IconAnt.Symbol.INFO);        // ℹ
 * Node warn = IconAnt.symbol(IconAnt.Symbol.WARNING);     // ⚠
 *
 * // 箭头
 * Node up = IconAnt.symbol(IconAnt.Symbol.ARROW_UP);      // ↑
 * Node down = IconAnt.symbol(IconAnt.Symbol.ARROW_DOWN);  // ↓
 * Node left = IconAnt.symbol(IconAnt.Symbol.ARROW_LEFT);  // ←
 * Node right = IconAnt.symbol(IconAnt.Symbol.ARROW_RIGHT);// →
 *
 * // 其他
 * Node plus = IconAnt.symbol(IconAnt.Symbol.PLUS);        // +
 * Node minus = IconAnt.symbol(IconAnt.Symbol.MINUS);      // −
 * Node dot = IconAnt.symbol(IconAnt.Symbol.DOT);          // •
 * Node menu = IconAnt.symbol(IconAnt.Symbol.MENU);        // ☰
 * }</pre>
 */
public class IconAnt {

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

    public static Node symbol(Symbol symbol) {
        return symbol(symbol, 16);
    }

    public static Node symbol(Symbol symbol, int size) {
        Label label = new Label(symbol.getChar());
        label.setFont(Font.font("Segoe UI Symbol", size));
        label.setStyle("-fx-text-fill: -color-fg-default;");
        StackPane pane = new StackPane(label);
        pane.setPrefSize(size, size);
        return pane;
    }
}
