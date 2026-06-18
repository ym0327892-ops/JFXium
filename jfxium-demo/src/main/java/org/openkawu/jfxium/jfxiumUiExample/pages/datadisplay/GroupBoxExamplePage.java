package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;

/**
 * GroupBox 分组框 —— 基础 / 边框 / extra 操作。
 */
public class GroupBoxExamplePage extends VBoxAnt {

    public GroupBoxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("GroupBox 分组框")
                .description("带标题边框的内容容器，用于将相关控件组织在同一视觉区域。")
                .sections(
                        basicSection(),
                        borderedSection(),
                        extraSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                GroupBoxAnt.create()
                        .title("基本信息")
                        .content(TypographyAnt.text("这里放置一组相关输入控件。").build())
                        .bordered(true)
                        .build()
        );
        String code = """
                GroupBoxAnt.create()
                        .title("基本信息")
                        .content(TypographyAnt.text("这里放置一组相关输入控件。").build())
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础分组框", "带边框和标题的桌面风格分组框。", code, demo);
    }

    private Node borderedSection() {
        Node demo = Demos.row(
                GroupBoxAnt.create()
                        .title("联系方式")
                        .content(TypographyAnt.text("邮箱、电话等联系信息。").build())
                        .bordered(true)
                        .build()
        );
        String code = """
                GroupBoxAnt.create()
                        .title("联系方式")
                        .content(TypographyAnt.text("邮箱、电话等联系信息。").build())
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("2. 边框分组框",
                "bordered(true) 显示边框。",
                code, demo);
    }

    private Node extraSection() {
        Node extraBtn = ButtonAnt.create("更多").type(ButtonAnt.Type.LINK).build();
        Node demo = Demos.row(
                GroupBoxAnt.create()
                        .title("配置面板")
                        .extra(extraBtn)
                        .content(TypographyAnt.text("标题右侧可放置额外操作按钮。").build())
                        .bordered(true)
                        .build()
        );
        String code = """
                Node extraBtn = ButtonAnt.create("更多")
                        .type(ButtonAnt.Type.LINK).build();
                GroupBoxAnt.create()
                        .title("配置面板")
                        .extra(extraBtn)
                        .content(TypographyAnt.text("标题右侧操作区。").build())
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("3. Extra 操作区",
                "extra(node) 在分组框标题栏右侧放置额外操作。",
                code, demo);
    }
}
