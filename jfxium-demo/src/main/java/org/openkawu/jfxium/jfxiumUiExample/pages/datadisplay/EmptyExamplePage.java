package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.EmptyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Empty 空状态 —— 默认 / 自定义描述。
 */
public class EmptyExamplePage extends VBoxAnt {

    public EmptyExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Empty 空状态")
                .description("数据为空时的占位展示，比空白页面更友好。")
                .sections(
                        defaultSection(),
                        customSection()
                )
                .padding(24)
                .build());
    }

    private Node defaultSection() {
        VBox empty = EmptyAnt.create().build();
        String code = """
                // 默认空状态（使用 i18n 默认描述）
                VBox empty = EmptyAnt.create().build();
                """;
        return Demos.sectionWithCode("1. 默认空状态", "不传参数时使用默认图标和描述文字。", code, empty);
    }

    private Node customSection() {
        VBox empty = EmptyAnt.create()
                .description("暂无搜索结果，请尝试其他关键词")
                .extraButton("重新搜索", () -> MessageAnt.info("重新搜索"))
                .build();
        String code = """
                VBox empty = EmptyAnt.create()
                        .description("暂无搜索结果，请尝试其他关键词")
                        .extraButton("重新搜索", () -> { /* 操作 */ })
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义描述与操作",
                "通过 description() 自定义文案，extraButton() 添加操作按钮。", code, empty);
    }
}
