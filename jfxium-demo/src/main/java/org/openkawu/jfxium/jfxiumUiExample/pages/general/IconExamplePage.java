package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Icon 图标 —— Symbol 字符图标 / Path SVG 图标。
 */
public class IconExamplePage extends VBoxAnt {

    public IconExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Icon 图标")
                .description("内置轻量图标，支持 Unicode 字符模式和 SVG Path 矢量模式。")
                .sections(symbolSection(), pathSection())
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
}
