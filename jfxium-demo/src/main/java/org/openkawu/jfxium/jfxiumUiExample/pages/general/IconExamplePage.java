package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Icon 图标 —— Symbol 字符图标 / Path SVG 图标。
 */
public class IconExamplePage extends VBoxAnt {

    public IconExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Icon 图标")
                .description("内置轻量图标，支持 Unicode 字符模式和 SVG Path 矢量模式。")
                .sections(symbolSection(), pathSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node symbolSection() {
        Node close = IconAnt.symbol(IconAnt.Symbol.CLOSE);
        Node check = IconAnt.symbol(IconAnt.Symbol.CHECK);
        Node info = IconAnt.symbol(IconAnt.Symbol.INFO);
        Node warning = IconAnt.symbol(IconAnt.Symbol.WARNING);
        Node plus = IconAnt.symbol(IconAnt.Symbol.PLUS);
        Node minus = IconAnt.symbol(IconAnt.Symbol.MINUS);
        Node arrowUp = IconAnt.symbol(IconAnt.Symbol.ARROW_UP);
        Node arrowDown = IconAnt.symbol(IconAnt.Symbol.ARROW_DOWN);
        Node menu = IconAnt.symbol(IconAnt.Symbol.MENU);
        Node demo = Demos.row(close, check, info, warning, plus, minus, arrowUp, arrowDown, menu);
        String code = """
                IconAnt.symbol(IconAnt.Symbol.CLOSE);
                IconAnt.symbol(IconAnt.Symbol.CHECK);
                IconAnt.symbol(IconAnt.Symbol.INFO);
                IconAnt.symbol(IconAnt.Symbol.WARNING);
                IconAnt.symbol(IconAnt.Symbol.PLUS);
                IconAnt.symbol(IconAnt.Symbol.MINUS);
                IconAnt.symbol(IconAnt.Symbol.ARROW_UP);
                IconAnt.symbol(IconAnt.Symbol.ARROW_DOWN);
                IconAnt.symbol(IconAnt.Symbol.MENU);
                """;
        return Demos.sectionWithCode("1. Symbol 字符图标",
                "Unicode 字符渲染，适合窗口控制、箭头等基础符号。", code, demo);
    }

    private Node pathSection() {
        Region home = IconAnt.path(IconAnt.Path.HOME, 20);
        Region user = IconAnt.path(IconAnt.Path.USER, 20);
        Region settings = IconAnt.path(IconAnt.Path.SETTINGS, 20);
        Region search = IconAnt.path(IconAnt.Path.SEARCH, 20);
        Region bell = IconAnt.path(IconAnt.Path.BELL, 20);
        Region chart = IconAnt.path(IconAnt.Path.CHART, 20);
        Region edit = IconAnt.path(IconAnt.Path.EDIT, 20);
        Region delete = IconAnt.path(IconAnt.Path.DELETE, 20);
        Region file = IconAnt.path(IconAnt.Path.FILE, 20);
        Node demo = Demos.row(home, user, settings, search, bell, chart, edit, delete, file);
        String code = """
                IconAnt.path(IconAnt.Path.HOME, 20);
                IconAnt.path(IconAnt.Path.USER, 20);
                IconAnt.path(IconAnt.Path.SETTINGS, 20);
                IconAnt.path(IconAnt.Path.SEARCH, 20);
                IconAnt.path(IconAnt.Path.BELL, 20);
                IconAnt.path(IconAnt.Path.CHART, 20);
                IconAnt.path(IconAnt.Path.EDIT, 20);
                IconAnt.path(IconAnt.Path.DELETE, 20);
                IconAnt.path(IconAnt.Path.FILE, 20);
                """;
        return Demos.sectionWithCode("2. Path SVG 图标",
                "SVG path 矢量渲染，24x24 viewBox，颜色由 LESS 控制。", code, demo);
    }

    /** 3. PlayGround：实时切换图标类型 + 具体图标，居中显示。 */
    private Node playgroundSection() {
        Binder<String> type = PlayGround.binder("symbol");
        Binder<String> icon = PlayGround.binder("CLOSE");

        return PlayGround.rebindRebuild(
                () -> buildIconNode(type.get(), icon.get()),
                "类型 / 图标",
                PlayGround.row("类型", PlayGround.segmented(type,
                        PlayGround.entry("symbol", "字符"),
                        PlayGround.entry("path", "矢量"))),
                PlayGround.row("图标", PlayGround.segmented(icon,
                        // Symbol
                        PlayGround.entry("CLOSE", "×"),
                        PlayGround.entry("CHECK", "√"),
                        PlayGround.entry("INFO", "i"),
                        PlayGround.entry("WARNING", "!"),
                        PlayGround.entry("PLUS", "+"),
                        PlayGround.entry("MINUS", "-"),
                        PlayGround.entry("ARROW_UP", "↑"),
                        PlayGround.entry("ARROW_DOWN", "↓"),
                        PlayGround.entry("MENU", "≡"),
                        // Path
                        PlayGround.entry("HOME", "⌂"),
                        PlayGround.entry("USER", "👤"),
                        PlayGround.entry("SETTINGS", "⚙"),
                        PlayGround.entry("SEARCH", "🔍"),
                        PlayGround.entry("BELL", "🔔"),
                        PlayGround.entry("CHART", "📊"),
                        PlayGround.entry("EDIT", "✎"),
                        PlayGround.entry("DELETE", "🗑"),
                        PlayGround.entry("FILE", "📄")
                ))
        );
    }

    /** 根据 type + icon 名生成单个图标节点，外面包 80x80 StackPane 让 Playground 展示区有大一些的居中区域。 */
    private Node buildIconNode(String type, String iconName) {
        Node iconNode;
        if ("path".equals(type)) {
            IconAnt.Path p = parsePath(iconName);
            iconNode = p != null ? IconAnt.path(p, 32) : IconAnt.path(IconAnt.Path.HOME, 32);
        } else {
            IconAnt.Symbol s = parseSymbol(iconName);
            iconNode = s != null ? IconAnt.symbol(s) : IconAnt.symbol(IconAnt.Symbol.CLOSE);
        }
        return StackPaneAnt.create().children(iconNode)
                .styleClass("jfx-demo-icon-stage").build();
    }

    private IconAnt.Symbol parseSymbol(String name) {
        for (IconAnt.Symbol s : IconAnt.Symbol.values()) {
            if (s.name().equals(name)) return s;
        }
        return null;
    }

    private IconAnt.Path parsePath(String name) {
        for (IconAnt.Path p : IconAnt.Path.values()) {
            if (p.name().equals(name)) return p;
        }
        return null;
    }
}