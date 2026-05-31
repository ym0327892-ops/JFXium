package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.DescriptionsAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Descriptions 描述列表 —— 基础 / 带边框 / 多列。
 */
public class DescriptionsExamplePage extends VBoxAnt {

    public DescriptionsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Descriptions 描述列表")
                .description("成组展示只读信息，常用于详情页的字段展示。")
                .sections(basicSection(), borderedSection(), multiColumnSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        VBox desc = DescriptionsAnt.create()
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
        VBox desc = DescriptionsAnt.create()
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
        VBox desc = DescriptionsAnt.create()
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
}
