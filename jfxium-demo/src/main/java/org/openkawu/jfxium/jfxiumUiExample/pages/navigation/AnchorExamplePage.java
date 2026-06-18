package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.AnchorAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Anchor 锚点 —— 基础。
 *
 * <p>Anchor 需要真实滚动容器配合做「滚动定位」才有完整意义，这里聚焦展示其渲染与
 * activeKey 高亮、点击回调（onChange）—— 不依赖外部滚动目标。</p>
 */
public class AnchorExamplePage extends VBoxAnt {

    public AnchorExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Anchor 锚点")
                .description("用于展示页面内的导航锚点列表，支持高亮当前项与点击回调。")
                .sections(verticalSection(), horizontalSection(), activeSection())
                .padding(24)
                .build());
    }

    private Node verticalSection() {
        Node demo = AnchorAnt.create()
                .item("intro", "介绍", "#intro")
                .item("install", "安装", "#install")
                .item("usage", "用法", "#usage")
                .activeKey("install")
                .onChange(key -> {})
                .build();
        String code = """
                AnchorAnt.create()
                        .item("intro", "介绍", "#intro")
                        .item("install", "安装", "#install")
                        .item("usage", "用法", "#usage")
                        .activeKey("install")          // 高亮当前项
                        .onChange(key -> scrollTo(key))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "item(key, title, href) 追加锚点；activeKey(...) 高亮当前项；onChange 拿到点击的 key。",
                code, demo);
    }

    private Node horizontalSection() {
        Node demo = AnchorAnt.create()
                .direction(AnchorAnt.Direction.HORIZONTAL)
                .item("a", "概览", "#a")
                .item("b", "详情", "#b")
                .item("c", "评论", "#c")
                .activeKey("a")
                .build();
        String code = """
                AnchorAnt.create()
                        .direction(AnchorAnt.Direction.HORIZONTAL)
                        .item("a", "概览", "#a")
                        .item("b", "详情", "#b")
                        .item("c", "评论", "#c")
                        .activeKey("a")
                        .build();
                """;
        return Demos.sectionWithCode("2. 水平方向",
                "direction(HORIZONTAL) 让锚点横向排列。",
                code, demo);
    }

    /**
     * 3. 显示当前激活锚点 —— 点击锚点时高亮条自动移动 + 把 activeKey 显示到结果 Label。
     *
     * <p><b>BUG #52 已修复</b>：Anchor 现提供运行时 {@link AnchorAnt.Controller}，点击锚点会自动
     * 调 {@code setActiveKey} 移动高亮条（无需重建）；业务也可在滚动定位时主动调
     * {@code controller().setActiveKey(key)} 同步高亮。</p>
     */
    private Node activeSection() {
        Label result = TypographyAnt.text("当前激活：(未点击)").build();
        Node anchor = AnchorAnt.create()
                .item("intro", "介绍", "#intro")
                .item("install", "安装", "#install")
                .item("usage", "用法", "#usage")
                .activeKey("intro")
                .onChange(key -> result.setText("当前激活：" + key))
                .build();
        Node demo = Demos.column(anchor, result);
        String code = """
                Label result = TypographyAnt.text("当前激活：(未点击)").build();
                AnchorAnt.create()
                        .item("intro", "介绍", "#intro")
                        .item("install", "安装", "#install")
                        .item("usage", "用法", "#usage")
                        .activeKey("intro")
                        // 点击锚点会自动移动高亮条（BUG #52 修复）
                        .onChange(key -> result.setText("当前激活：" + key))
                        .build();

                // 滚动定位场景可主动同步高亮：
                // AnchorAnt.Builder b = AnchorAnt.create()...;
                // VBox a = b.build();
                // b.controller().setActiveKey("usage");
                """;
        return Demos.sectionWithCode("3. 显示当前激活锚点",
                "点击锚点高亮条自动移动（运行时 Controller.setActiveKey），onChange 同时拿到被点击的 key。",
                code, demo);
    }
}
