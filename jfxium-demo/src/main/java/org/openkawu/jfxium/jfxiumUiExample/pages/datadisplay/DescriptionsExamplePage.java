package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.DescriptionsAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

/**
 * Descriptions 描述列表 —— 基础 / 带边框 / 多列。
 */
public class DescriptionsExamplePage extends VBoxAnt {

    public DescriptionsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Descriptions 描述列表")
                .description("成组展示只读信息，常用于详情页的字段展示。")
                .sections(basicSection(), borderedSection(), multiColumnSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node desc = DescriptionsAnt.create()
                .title("用户信息")
                .item("姓名", "张三")
                .item("电话", "13800138000")
                .item("地址", "北京市朝阳区")
                .build();
        String code = """
                DescriptionsAnt.create()
                        .title("用户信息")
                        .item("姓名", "张三")
                        .item("电话", "13800138000")
                        .item("地址", "北京市朝阳区")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认 3 列水平布局。", code, desc);
    }

    private Node borderedSection() {
        Node desc = DescriptionsAnt.create()
                .title("订单详情")
                .bordered()
                .item("订单号", "20250101001")
                .item("状态", "已发货")
                .item("金额", "¥ 128.00")
                .item("收货地址", "上海市浦东新区世纪大道 100 号", 3)
                .build();
        String code = """
                DescriptionsAnt.create()
                        .title("订单详情")
                        .bordered()
                        .item("订单号", "20250101001")
                        .item("状态", "已发货")
                        .item("金额", "¥ 128.00")
                        .item("收货地址", "上海市浦东新区世纪大道 100 号", 3)
                        .build();
                """;
        return Demos.sectionWithCode("2. 带边框",
                "bordered() 启用边框模式；item 第三个参数 span 控制跨列。", code, desc);
    }

    private Node multiColumnSection() {
        Node desc = DescriptionsAnt.create()
                .title("系统信息")
                .column(2)
                .item("操作系统", "macOS 15")
                .item("浏览器", "Chrome 130")
                .item("分辨率", "2560x1440")
                .item("语言", "zh-CN")
                .build();
        String code = """
                DescriptionsAnt.create()
                        .title("系统信息")
                        .column(2)
                        .item("操作系统", "macOS 15")
                        .item("浏览器", "Chrome 130")
                        .item("分辨率", "2560x1440")
                        .item("语言", "zh-CN")
                        .build();
                """;
        return Demos.sectionWithCode("3. 自定义列数",
                "column(2) 设置为 2 列布局。", code, desc);
    }

    /**
     * 4. 交互演示 —— 通过左侧控件实时改变 Descriptions 的边框 / 列数 / 尺寸 / 布局方向。
     *
     * <p>DescriptionsAnt 无 Controller，所有属性变更均通过 build 重建生效。</p>
     */
    private Node playgroundSection() {
        Binder<String> borderedBinder = PlayGround.binder("on");
        Binder<String> columnBinder   = PlayGround.binder("3");
        Binder<String> sizeBinder     = PlayGround.binder("default");
        Binder<String> layoutBinder   = PlayGround.binder("horizontal");

        Supplier<Node> factory = () -> DescriptionsAnt.create()
                .title("用户信息")
                .bordered(parseBool(borderedBinder.get()))
                .column(parseInt(columnBinder.get(), 3, 1, 6))
                .size(parseSize(sizeBinder.get()))
                .layout(parseLayout(layoutBinder.get()))
                .item("姓名", "张三")
                .item("电话", "13800138000")
                .item("邮箱", "zhangsan@example.com")
                .item("地址", "北京市朝阳区某街道 100 号", 3)
                .build();

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Descriptions 的边框、列数、尺寸、布局方向 —— DescriptionsAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("带边框", PlayGround.segmented(borderedBinder,
                                PlayGround.entry("off", "无"),
                                PlayGround.entry("on",  "有"))),
                        PlayGround.row("列数", PlayGround.segmented(columnBinder,
                                PlayGround.entry("1", "1"),
                                PlayGround.entry("2", "2"),
                                PlayGround.entry("3", "3"),
                                PlayGround.entry("4", "4"))),
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("middle",  "中等"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("布局方向", PlayGround.segmented(layoutBinder,
                                PlayGround.entry("horizontal", "水平"),
                                PlayGround.entry("vertical",   "垂直")))));
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

    private static int parseInt(String v, int fallback, int min, int max) {
        if (v == null || v.isBlank()) return fallback;
        try {
            int i = Integer.parseInt(v.trim());
            if (i < min) return min;
            if (i > max) return max;
            return i;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small"   -> Size.SMALL;
            case "middle"  -> Size.MIDDLE;
            case "large"   -> Size.LARGE;
            default        -> Size.DEFAULT;
        };
    }

    private static DescriptionsAnt.Layout parseLayout(String v) {
        if (v == null) return DescriptionsAnt.Layout.HORIZONTAL;
        return "vertical".equalsIgnoreCase(v) ? DescriptionsAnt.Layout.VERTICAL : DescriptionsAnt.Layout.HORIZONTAL;
    }
}
