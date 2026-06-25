package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

import org.openkawu.jfxium.component.control.MentionsAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Mentions 提及 —— 基础 @ 提及 / 自定义触发符。
 */
public class MentionsExamplePage extends VBoxAnt {

    public MentionsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Mentions 提及")
                .description("输入 @ 触发符后弹出候选列表的文本域，用于评论、任务分配、聊天等 @ 提及场景。")
                .sections(
                        basicSection(),
                        hashtagSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        TextArea demo = MentionsAnt.create()
                .placeholder("输入 @ 提及用户...")
                .prefix("@")
                .option("zhangsan", "张三")
                .option("lisi", "李四")
                .option("wangwu", "王五")
                .option("zhaoliu", "赵六")
                .option("sunqi", "孙七")
                .onSelect(value -> MessageAnt.info("选中了：" + value))
                .rows(4)
                .build();
        demo.setMaxHeight(120);

        String code = """
                MentionsAnt.create()
                    .placeholder("输入 @ 提及用户...")
                    .prefix("@")
                    .option("zhangsan", "张三")
                    .option("lisi", "李四")
                    .option("wangwu", "王五")
                    .onSelect(value -> MessageAnt.info("选中了：" + value))
                    .rows(4)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础 @ 提及",
                "prefix() 设置触发符（默认 @）；option(value, label) 添加候选项；输入 @ 后弹出候选列表。",
                code, demo);
    }

    private Node hashtagSection() {
        TextArea demo = MentionsAnt.create()
                .placeholder("输入 # 选择标签...")
                .prefix("#")
                .option("urgent", "紧急")
                .option("bug", "缺陷")
                .option("feature", "新功能")
                .option("docs", "文档")
                .option("refactor", "重构")
                .onSelect(value -> MessageAnt.info("标签：" + value))
                .rows(4)
                .build();
        demo.setMaxHeight(120);

        String code = """
                MentionsAnt.create()
                    .placeholder("输入 # 选择标签...")
                    .prefix("#")
                    .option("urgent", "紧急")
                    .option("bug", "缺陷")
                    .option("feature", "新功能")
                    .onSelect(value -> MessageAnt.info("标签：" + value))
                    .rows(4)
                    .build();
                """;
        return Demos.sectionWithCode("2. 自定义触发符 #",
                "prefix(\"#\") 可改为 # 等触发符，适用于话题标签、任务编号等场景。",
                code, demo);
    }
}
