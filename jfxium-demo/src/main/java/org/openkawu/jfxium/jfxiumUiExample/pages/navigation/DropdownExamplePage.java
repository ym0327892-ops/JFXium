package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.DropdownAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Dropdown 下拉菜单 —— 基础菜单 / 带分隔与禁用项。
 *
 * <p><b>关键点</b>：DropdownAnt.create()...build() 返回的是 DropdownResult，<b>不是 Node</b>。
 * 它会把点击事件绑定到 trigger 节点上 —— 把 trigger（按钮）放进页面，点击即弹出菜单。</p>
 */
public class DropdownExamplePage extends VBoxAnt {

    public DropdownExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Dropdown 下拉菜单")
                .description("点击触发节点弹出菜单，常用于「更多操作」等场景。")
                .sections(basicSection(), dividerSection(), clickedSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node trigger = ButtonAnt.create("操作菜单 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("edit", "编辑")
                .item("copy", "复制")
                .item("delete", "删除")
                .onSelect(key -> MessageAnt.info("选中：" + key))
                .build();   // 返回 DropdownResult，已把点击事件绑到 trigger
        String code = """
                Node trigger = ButtonAnt.create("操作菜单 ▾").build();
                DropdownAnt.create()
                        .trigger(trigger)
                        .item("edit", "编辑")
                        .item("copy", "复制")
                        .item("delete", "删除")
                        .onSelect(key -> MessageAnt.info("选中：" + key))
                        .build();   // 把 trigger 放进页面，点击即弹出
                """;
        return Demos.sectionWithCode("1. 基础菜单",
                "trigger(node) 指定触发节点，item(...) 追加菜单项，onSelect 拿到选中的 key。",
                code, trigger);
    }

    private Node dividerSection() {
        Node trigger = ButtonAnt.create("更多 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("profile", "个人资料")
                .item("settings", "设置")
                .divider()
                .item("disabled", "暂不可用", true)
                .item("logout", "退出登录")
                .onSelect(key -> MessageAnt.info("选中：" + key))
                .build();
        String code = """
                DropdownAnt.create()
                        .trigger(trigger)
                        .item("profile", "个人资料")
                        .item("settings", "设置")
                        .divider()                       // 分隔线
                        .item("disabled", "暂不可用", true)  // 禁用项
                        .item("logout", "退出登录")
                        .onSelect(key -> MessageAnt.info("选中：" + key))
                        .build();
                """;
        return Demos.sectionWithCode("2. 分隔线与禁用项",
                "divider() 插入分隔线；item(key, label, true) 第三参数为禁用标记。",
                code, trigger);
    }

    /**
     * 3. 显示点击的菜单项 —— 把点击结果回填到结果 Label。
     *
     * <p><b>BUG #54 已修复</b>：DropdownAnt 新增 {@code onSelectItem(Consumer<MenuItem>)} 回调，
     * 直接回传完整 {@link DropdownAnt.MenuItem}（可同时取 key 与 label），
     * 不再需要 demo 侧自己维护一份 key→label 映射。</p>
     */
    private Node clickedSection() {
        Label result = new Label("点击的菜单项：(未点击)");
        Node trigger = ButtonAnt.create("操作菜单 ▾").build();
        DropdownAnt.create()
                .trigger(trigger)
                .item("edit", "编辑")
                .item("copy", "复制")
                .item("delete", "删除")
                // onSelectItem 直接给出完整 MenuItem，可取 label，无需自己映射
                .onSelectItem(item -> result.setText(
                        "点击的菜单项：" + item.getLabel() + "（key=" + item.getKey() + "）"))
                .build();
        Node demo = Demos.column(trigger, result);
        String code = """
                Label result = new Label("点击的菜单项：(未点击)");
                Node trigger = ButtonAnt.create("操作菜单 ▾").build();
                DropdownAnt.create()
                        .trigger(trigger)
                        .item("edit", "编辑")
                        .item("copy", "复制")
                        .item("delete", "删除")
                        // BUG #54 修复：onSelectItem 直接回传完整 MenuItem，可取 label
                        .onSelectItem(item -> result.setText(
                                "点击的菜单项：" + item.getLabel() + "（key=" + item.getKey() + "）"))
                        .build();
                """;
        return Demos.sectionWithCode("3. 显示点击的菜单项",
                "onSelectItem(item -> ...) 回传完整 MenuItem，可同时取 getKey() 与 getLabel()（修复了原 onSelect 只给 key 的缺口）。",
                code, demo);
    }
}
