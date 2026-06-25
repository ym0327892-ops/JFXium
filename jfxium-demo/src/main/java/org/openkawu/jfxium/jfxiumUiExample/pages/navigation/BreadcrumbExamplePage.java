package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.BreadcrumbAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Breadcrumb 面包屑 —— 基础 / 分隔符 / 可点击。
 */
public class BreadcrumbExamplePage extends VBoxAnt {

    public BreadcrumbExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Breadcrumb 面包屑")
                .description("显示当前页面在层级结构中的位置，支持自定义分隔符和点击导航。")
                .sections(
                        basicSection(),
                        separatorSection(),
                        clickableSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = BreadcrumbAnt.create()
                .item("首页")
                .item("列表页")
                .item("详情页")
                .build();
        String code = """
                BreadcrumbAnt.create()
                        .item("首页")
                        .item("列表页")
                        .item("详情页")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "最简单的面包屑，最后一项为当前页。",
                code, demo);
    }

    private Node separatorSection() {
        Node demo = Demos.column(
                BreadcrumbAnt.create()
                        .separator("/")
                        .item("首页")
                        .item("应用中心")
                        .item("应用详情")
                        .build(),
                BreadcrumbAnt.create()
                        .separator(">")
                        .item("Home")
                        .item("Category")
                        .item("Detail")
                        .build()
        );
        String code = """
                BreadcrumbAnt.create()
                        .separator("/")
                        .item("首页").item("应用中心").item("应用详情")
                        .build();
                BreadcrumbAnt.create()
                        .separator(">")
                        .item("Home").item("Category").item("Detail")
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义分隔符",
                "separator(str) 自定义层级之间的分隔符号。",
                code, demo);
    }

    private Node clickableSection() {
        Node demo = BreadcrumbAnt.create()
                .item("首页", item -> MessageAnt.info("导航：" + item))
                .item("用户管理", item -> MessageAnt.info("导航：" + item))
                .item("用户详情")
                .build();
        String code = """
                BreadcrumbAnt.create()
                        .item("首页", item -> navigate("home"))
                        .item("用户管理", item -> navigate("users"))
                        .item("用户详情")   // 当前页不可点击
                        .build();
                """;
        return Demos.sectionWithCode("3. 可点击导航",
                "带回调的 item 可点击跳转；最后一项通常不设回调表示当前页。",
                code, demo);
    }

    private Node playgroundSection() {
        Binder<String> separatorBinder  = PlayGround.binder("/");
        Binder<String> clickableBinder  = PlayGround.binder("off");
        Binder<String> hrefBinder       = PlayGround.binder("off");
        Binder<String> sizeBinder       = PlayGround.binder("default");  // compact/default/loose
        Binder<String> radiusBinder     = PlayGround.binder("none");     // none/sm/md/lg
        Binder<String> borderBinder     = PlayGround.binder("off");      // off/on

        Supplier<Node> factory = () -> {
            String sep = separatorBinder.get();
            if (sep == null || sep.isBlank()) sep = "/";

            BreadcrumbAnt.Builder b = BreadcrumbAnt.create().separator(sep);
            if (parseBool(clickableBinder.get())) {
                b.item("首页", item -> MessageAnt.info("导航：首页"));
                b.item("用户管理", item -> MessageAnt.info("导航：用户管理"));
            } else if (parseBool(hrefBinder.get())) {
                b.item("首页", "/home");
                b.item("用户管理", "/users");
            } else {
                b.item("首页");
                b.item("用户管理");
            }
            b.item("用户详情");

            // 尺寸档位 —— 通过 padding + prefHeight 模拟 compact/default/loose
            String size = sizeBinder.get();
            if ("compact".equals(size)) {
                b.padding(4, 8, 4, 8);
                b.prefHeight(24);
            } else if ("loose".equals(size)) {
                b.padding(8, 16, 8, 16);
                b.prefHeight(40);
            } else {
                b.prefHeight(32);
            }

            // 圆角档位 —— 复用 AbstractStyleBuilder.radius(Radius)
            b.radius(parseRadius(radiusBinder.get()));

            // 底边线 —— 复用 AbstractStyleBuilder.borderBottom(boolean)
            if (parseBool(borderBinder.get())) {
                b.borderBottom();
            }

            return b.build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Breadcrumb 的分隔符、中间项形态（纯文本 / 链接 / 可点击）、尺寸档位（紧凑 padding+24px / 默认 32px / 宽松 padding+40px）、圆角档位（NONE / SM / MD / LG）、底边线 —— BreadcrumbAnt 无 Controller，所有属性变更均通过 build 重建生效。注意最后一项在 build 中始终渲染为 Label 表示当前页，无法直接控制其点击态。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("分隔符", PlayGround.textField(separatorBinder, "/", "输入分隔符，如 / > ·")),
                        PlayGround.row("中间项形态", PlayGround.segmented(clickableBinder,
                                PlayGround.entry("off", "纯文本"),
                                PlayGround.entry("on",  "可点击"))),
                        PlayGround.row("中间项带链接", PlayGround.segmented(hrefBinder,
                                PlayGround.entry("off", "关闭"),
                                PlayGround.entry("on",  "开启"))),
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("compact", "紧凑 (24px)"),
                                PlayGround.entry("default", "默认 (32px)"),
                                PlayGround.entry("loose",   "宽松 (40px)"))),
                        PlayGround.row("圆角", PlayGround.segmented(radiusBinder,
                                PlayGround.entry("none", "直角"),
                                PlayGround.entry("sm",   "小 (4px)"),
                                PlayGround.entry("md",   "中 (6px)"),
                                PlayGround.entry("lg",   "大 (8px)"))),
                        PlayGround.row("底边线", PlayGround.segmented(borderBinder,
                                PlayGround.entry("off", "无"),
                                PlayGround.entry("on",  "显示")))));
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

    private static Radius parseRadius(String v) {
        if ("sm".equals(v)) return Radius.SM;
        if ("md".equals(v)) return Radius.MD;
        if ("lg".equals(v)) return Radius.LG;
        return Radius.NONE;
    }
}
