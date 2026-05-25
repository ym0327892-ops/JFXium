package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AutoCompleteAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AutoComplete 自动补全展示页（M19.14）。
 */
public class AutoCompletePage implements ShowcasePage {

    @Override public String   key()      { return "auto-complete"; }
    @Override public String   title()    { return "AutoComplete 自动补全"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("AutoComplete 自动补全");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("输入框 + 候选下拉 —— 比 ComboBox 更灵活，支持自定义过滤逻辑。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionCustomFilter(),
                        sectionObjectOptions(),
                        sectionMaxSuggestions()
                )
                .build();
    }

    private Node sectionBasic() {
        List<String> options = List.of("Java", "JavaScript", "Kotlin", "Scala", "Groovy", "Python", "Go", "Rust");
        Node a = AutoCompleteAnt.<String>create()
                .options(options)
                .placeholder("输入语言名称...")
                .onSelect(v -> MessageAnt.success("选中：" + v))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础用法（String 选项）")
                .description("默认按 contains 过滤——输入 \"j\" 弹出含 j 的所有项")
                .demo(a)
                .code("""
                        AutoCompleteAnt.<String>create()
                            .options(List.of("Java", "Kotlin", "Python"))
                            .placeholder("输入语言名称...")
                            .onSelect(v -> handle(v))
                            .build();
                        """)
                .build();
    }

    private Node sectionCustomFilter() {
        List<String> emails = List.of(
                "zhangsan@example.com", "lisi@example.com", "wangwu@example.com",
                "admin@gmail.com", "test@yahoo.com"
        );
        Node a = AutoCompleteAnt.<String>create()
                .options(emails)
                .placeholder("输入邮箱前缀...")
                .filter(input -> emails.stream()
                        .filter(e -> e.toLowerCase().startsWith(input.toLowerCase()))
                        .collect(Collectors.toList()))
                .build();
        return ShowcaseSection.create()
                .title("场景 2：自定义过滤逻辑（startsWith 替代 contains）")
                .description(".filter(Function<String, List<T>>) —— 输入 \"a\" 只显示 a 开头的")
                .demo(a)
                .code("""
                        List<String> emails = ...;
                        AutoCompleteAnt.<String>create()
                            .options(emails)
                            .filter(input -> emails.stream()
                                .filter(e -> e.toLowerCase().startsWith(input.toLowerCase()))
                                .collect(Collectors.toList()))
                            .build();
                        """)
                .build();
    }

    public record User(String name, String email) {}

    private Node sectionObjectOptions() {
        List<User> users = List.of(
                new User("张三", "zhangsan@example.com"),
                new User("李四", "lisi@example.com"),
                new User("王五", "wangwu@example.com")
        );
        Node a = AutoCompleteAnt.<User>create()
                .options(users)
                .optionToString(u -> u.name() + " (" + u.email() + ")")
                .placeholder("搜索用户...")
                .onSelect(u -> MessageAnt.info("选中：" + u.name()))
                .build();
        return ShowcaseSection.create()
                .title("场景 3：自定义对象 + optionToString")
                .description(".optionToString(...) 控制下拉项 + 输入框显示文本")
                .demo(a)
                .code("""
                        record User(String name, String email) {}

                        AutoCompleteAnt.<User>create()
                            .options(users)
                            .optionToString(u -> u.name() + " (" + u.email() + ")")
                            .onSelect(u -> handle(u))
                            .build();
                        """)
                .build();
    }

    private Node sectionMaxSuggestions() {
        List<String> longList = java.util.stream.IntStream.rangeClosed(1, 200)
                .mapToObj(i -> "Option " + i)
                .toList();
        Node a = AutoCompleteAnt.<String>create()
                .options(longList)
                .placeholder("输入数字 (1-200)，最多显示 5 条")
                .maxSuggestions(5)
                .build();
        return ShowcaseSection.create()
                .title("场景 4：限制最大候选数（maxSuggestions）")
                .description(".maxSuggestions(5) —— 海量数据时只显示前 N 条匹配")
                .demo(a)
                .code("""
                        AutoCompleteAnt.<String>create()
                            .options(largeList)
                            .maxSuggestions(5)
                            .build();
                        """)
                .build();
    }
}
