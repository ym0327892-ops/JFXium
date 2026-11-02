package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.TabsAnt;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Tabs 标签页 —— 基础线条 / 卡片类型 / 位置。
 */
public class TabsExamplePage extends VBoxAnt {

    public TabsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tabs 标签页")
                .description("选项卡切换组件，支持线条、卡片样式、多种位置、可关闭和运行时增删。")
                .sections(
                        basicLineSection(),
                        cardTypeSection(),
                        placementSection(),
                        closableDynamicSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicLineSection() {
        Node demo = TabsAnt.create()
                .tab("tab1", "标签一", TypographyAnt.text("标签一的内容").build())
                .tab("tab2", "标签二", TypographyAnt.text("标签二的内容").build())
                .tab("tab3", "标签三", TypographyAnt.text("标签三的内容").build())
                .build();
        String code = """
                TabsAnt.create()
                        .tab("tab1", "标签一", TypographyAnt.text("标签一的内容").build())
                        .tab("tab2", "标签二", TypographyAnt.text("标签二的内容").build())
                        .tab("tab3", "标签三", TypographyAnt.text("标签三的内容").build())
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础线条样式",
                "默认线条（LINE）样式的标签页。",
                code, demo);
    }

    private Node cardTypeSection() {
        Node demo = TabsAnt.create()
                .type(TabsAnt.Type.CARD)
                .tab("card1", "卡片一", TypographyAnt.text("卡片一的内容").build())
                .tab("card2", "卡片二", TypographyAnt.text("卡片二的内容").build())
                .tab("card3", "卡片三", TypographyAnt.text("卡片三的内容").build())
                .build();
        String code = """
                TabsAnt.create()
                        .type(TabsAnt.Type.CARD)
                        .tab("card1", "卡片一", TypographyAnt.text("卡片一的内容").build())
                        .tab("card2", "卡片二", TypographyAnt.text("卡片二的内容").build())
                        .build();
                """;
        return Demos.sectionWithCode("2. 卡片类型",
                "type(CARD) 卡片风格标签页。",
                code, demo);
    }

    private Node placementSection() {
        Node demo = TabsAnt.create()
                .tab("t1", "可用标签", TypographyAnt.text("这个标签可以正常切换").build())
                .tab("t2", "禁用标签", TypographyAnt.text("这个标签被禁用了").build(), true)
                .tab("t3", "另一个标签", TypographyAnt.text("第三个标签的内容").build())
                .onChange(key -> MessageAnt.info("已切换到: " + key))
                .build();
        String code = """
                TabsAnt.create()
                        .tab("t1", "可用标签", content1)
                        .tab("t2", "禁用标签", content2, true)  // disabled
                        .tab("t3", "另一个标签", content3)
                        .onChange(key -> MessageAnt.info("已切换到: " + key))
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用标签 + onChange",
                "tab 第 4 参数 disabled=true 禁用该标签；onChange 监听切换事件。",
                code, demo);
    }

    // ============================================================
    // 4. 可关闭 + 动态增删
    // ============================================================

    private Node closableDynamicSection() {
        TabsAnt.Controller[] holder = new TabsAnt.Controller[1];
        AtomicInteger seq = new AtomicInteger(3);

        Node tabs = TabsAnt.create()
                .closable(true)
                .tab("tab1", "标签一", TypographyAnt.text("可通过 × 关闭").build())
                .tab("tab2", "标签二", TypographyAnt.text("可通过 × 关闭").build())
                .tab("tab3", "固定标签", TypographyAnt.text("该标签不可关闭").build(), false, false)
                .onClose(key -> MessageAnt.info("已关闭: " + key))
                .build();
        holder[0] = TabsAnt.controllerOf(tabs);

        ButtonAnt add = ButtonAnt.create("动态新增")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    int n = seq.incrementAndGet();
                    holder[0].addTab("tab" + n, "标签" + n,
                            TypographyAnt.text("动态新增的标签 " + n).build());
                })
                .build();

        ButtonAnt closeAll = ButtonAnt.create("关闭全部")
                .onClick(e -> holder[0].closeAll())
                .build();

        Node demo = VBoxAnt.create().spacing(12).children(
                tabs,
                HBoxAnt.create().spacing(8).children(add, closeAll));

        String code = """
                Node tabs = TabsAnt.create()
                        .closable(true)
                        .tab("a", "标签一", contentA)
                        .tab("b", "固定", contentB, false, false)   // 不可关闭
                        .onClose(key -> ...)
                        .build();

                TabsAnt.Controller ctrl = TabsAnt.controllerOf(tabs);
                ctrl.addTab("c", "标签三", contentC);   // 动态新增
                ctrl.closeAll();                       // 关闭全部可关闭 tab
                """;
        return Demos.sectionWithCode("4. 可关闭 + 动态增删",
                "closable 渲染 ×；Controller.addTab 运行时新增；右侧菜单可用 overflowMenu / showCloseCurrent|Others|All 控制显示。",
                code, demo);
    }

    // ============================================================
    // 5. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Tabs 的 3 个维度：类型（LINE/CARD）、位置（TOP/BOTTOM）、
     * 当前选中 tab。
     *
     * <p>TabsAnt 的 Controller 只能运行时切换当前 tab（{@code setCurrent / selectByKey / next / prev}），
     * 改不了 type / tabPlacement / size 等 build 期属性 —— 这些只能 rebuild。所以整个 playground
     * 采用 {@link PlayGround#rebuildRebuild} 策略：任一 binder 变化都重 build 一棵新 tabs 树。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> type      = PlayGround.binder("LINE");
        Binder<String> position  = PlayGround.binder("TOP");
        Binder<String> activeKey = PlayGround.binder("tab1");

        // 2. display 工厂 —— 每次 binder 变化都重 build
        Supplier<Node> factory = () -> {
            TabsAnt.Builder b = TabsAnt.create()
                    .tab("tab1", "标签一", TypographyAnt.text("标签一的内容").build())
                    .tab("tab2", "标签二", TypographyAnt.text("标签二的内容").build())
                    .tab("tab3", "标签三", TypographyAnt.text("标签三的内容").build())
                    .tab("tab4", "标签四（禁用）", TypographyAnt.text("禁用 tab").build(), true)
                    .onChange(key -> MessageAnt.info("已切换到: " + key));
            b.type(parseType(type.get()));
            b.tabPlacement(parsePlacement(position.get()));
            // BUG #144：activeKey 已是 Builder API，不再需要 build 后用 Controller 补救
            b.activeKey(activeKey.get());
            return b.build();
        };

        // 3. 串起来 —— rebuild 入口负责 scaffold build + binder 监听注册
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Tabs 的样式 / 位置 / 当前选中 —— type 与 tabPlacement 是 build 期属性，每次都重建。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("类型", PlayGround.segmented(type,
                                PlayGround.entry("LINE", "线条"),
                                PlayGround.entry("CARD", "卡片"))),
                        PlayGround.row("位置", PlayGround.segmented(position,
                                PlayGround.entry("TOP",    "顶部"),
                                PlayGround.entry("BOTTOM", "底部"))),
                        PlayGround.row("当前", PlayGround.segmented(activeKey,
                                PlayGround.entry("tab1", "标签一"),
                                PlayGround.entry("tab2", "标签二"),
                                PlayGround.entry("tab3", "标签三")))));
    }

    // ============================================================
    // 枚举解析 helpers
    // ============================================================

    private static TabsAnt.Type parseType(String v) {
        if (v == null) return TabsAnt.Type.LINE;
        return "CARD".equals(v) ? TabsAnt.Type.CARD : TabsAnt.Type.LINE;
    }

    private static TabsAnt.TabPlacement parsePlacement(String v) {
        if (v == null) return TabsAnt.TabPlacement.TOP;
        return switch (v) {
            case "BOTTOM" -> TabsAnt.TabPlacement.BOTTOM;
            default       -> TabsAnt.TabPlacement.TOP;
        };
    }
}
