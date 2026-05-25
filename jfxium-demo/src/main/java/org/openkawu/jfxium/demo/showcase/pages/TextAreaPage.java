package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.TextAreaAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * TextArea 多行输入展示页（M19.14）。
 */
public class TextAreaPage implements ShowcasePage {

    @Override public String   key()      { return "text-area"; }
    @Override public String   title()    { return "TextArea 多行输入"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("TextArea 多行输入");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("多行文本输入 —— 评论、备注、长文本字段。与 InputAnt 单行对比。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionRows(),
                        sectionWrap(),
                        sectionDisabled(),
                        sectionReadOnly()
                )
                .build();
    }

    private Node sectionBasic() {
        TextArea t = TextAreaAnt.create()
                .placeholder("请输入备注...")
                .rows(4)
                .build();
        t.setMaxWidth(420);
        return ShowcaseSection.create()
                .title("场景 1：基础（4 行）")
                .description("默认 wrap=true 自动换行")
                .demo(t)
                .code("""
                        TextArea t = TextAreaAnt.create()
                            .placeholder("请输入备注...")
                            .rows(4)
                            .build();
                        """)
                .build();
    }

    private Node sectionRows() {
        TextArea r3 = TextAreaAnt.create().placeholder("3 行").rows(3).build();
        TextArea r6 = TextAreaAnt.create().placeholder("6 行").rows(6).build();
        TextArea r10 = TextAreaAnt.create().placeholder("10 行").rows(10).build();

        VBox col = VBoxBuilder.create().spacing(12).children(r3, r6, r10).build();
        col.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("场景 2：不同行数")
                .description(".rows(N) —— 控制可见行数；超出可滚动")
                .demo(col)
                .code("""
                        TextAreaAnt.create().rows(3).build();
                        TextAreaAnt.create().rows(6).build();
                        TextAreaAnt.create().rows(10).build();
                        """)
                .build();
    }

    private Node sectionWrap() {
        TextArea wrap = TextAreaAnt.create()
                .text("这是一段较长的内容会根据宽度自动换行折叠显示，不需要手动换行符。")
                .wrapText(true).rows(3).build();
        TextArea noWrap = TextAreaAnt.create()
                .text("这是一段较长的内容不会自动换行，超出宽度会出现水平滚动条；适合代码/JSON 等场景。")
                .wrapText(false).rows(3).build();

        VBox col = VBoxBuilder.create().spacing(12).children(
                grayLabel("wrapText=true（默认，自动换行）"), wrap,
                grayLabel("wrapText=false（不换行，超出滚动）"), noWrap
        ).build();
        col.setMaxWidth(420);

        return ShowcaseSection.create()
                .title("场景 3：换行控制")
                .description(".wrapText(false) —— 代码/JSON 场景关闭自动换行")
                .demo(col)
                .code("""
                        TextAreaAnt.create().wrapText(true).build();   // 默认
                        TextAreaAnt.create().wrapText(false).build();  // 代码场景
                        """)
                .build();
    }

    private Node sectionDisabled() {
        TextArea t = TextAreaAnt.create()
                .text("此区域已禁用，不可编辑也不可聚焦")
                .disabled(true).rows(3).build();
        t.setMaxWidth(420);
        return ShowcaseSection.create()
                .title("场景 4：禁用态")
                .description("disabled(true) —— 整体变浅，不响应点击")
                .demo(t)
                .code("""
                        TextAreaAnt.create()
                            .text("此区域已禁用")
                            .disabled(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionReadOnly() {
        TextArea t = TextAreaAnt.create()
                .text("此区域只读 —— 可以选择/复制文字，但不能编辑")
                .editable(false).rows(3).build();
        t.setMaxWidth(420);
        return ShowcaseSection.create()
                .title("场景 5：只读（仅可复制）")
                .description(".editable(false) —— 比 disabled 更友好；用户能选中复制")
                .demo(t)
                .code("""
                        TextAreaAnt.create()
                            .text("此区域只读")
                            .editable(false)
                            .build();
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }
}
