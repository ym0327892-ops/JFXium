package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * 单布局示例页基类 —— 每个布局组件独立成菜单页时复用统一外壳。
 */
abstract class AbstractLayoutExamplePage extends VBoxAnt {

    protected AbstractLayoutExamplePage(String title, String description, Node... sections) {
        spacing(0).children(PageTemplate.create()
                .title(title)
                .description(description)
                .sections(sections)
                .padding(24)
                .build());
    }
}
