package org.openkawu.jfxium.jfxiumUiExample.util;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.CodeBlockAnt;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;
import org.openkawu.jfxium.template.ShowcaseSectionTemplate;

/**
 * 示例页面用的小工具集合 —— 把"一段说明 + 一组控件演示"封装成统一外观，
 * 避免每个示例页都重复 boilerplate。
 *
 * <p><b>设计意图</b>：</p>
 * <ul>
 *   <li>所有示例页都用 {@link #section(String, String, Node...)} 形成视觉一致的"白卡片+小标题+演示区"模式</li>
 *   <li>展示用的控件横向铺开用 {@link #row(Node...)}，纵向用 {@link #column(Node...)}</li>
 *   <li>样式 100% 走 styleClass / Background 体系，不写 inline setStyle 拼字符串</li>
 * </ul>
 */
public final class Demos {

    private Demos() {}

    /**
     * 单个 Section：标题 + 说明 + 演示节点（横向自动 wrap 用 HBox 多控件即可）。
     *
     * <p>底层是一张 GroupBox 分组框，让多个 section 在 PageTemplate 里能形成卡片列表式的视觉。</p>
     */
    public static Node section(String title, String description, Node... demoNodes) {
        VBox section = ShowcaseSectionTemplate.create()
                .title(title)
                .description(description)
                .body(demoNodes)
                .build();

        return GroupBoxAnt.create()
                .content(section)
                .bordered(true)
                .build();
    }

    /**
     * 带代码示例的 Section（M19.39 引入）—— 对标 Ant Design 文档站「示例 + 显示代码」的展示模式。
     *
     * <p><b>设计意图</b>：给学习者完整闭环——既看到组件效果，又能直接照抄源码学怎么写。</p>
     *
     * <p><b>渲染结构</b>：</p>
     * <pre>
     * ┌ Card ──────────────────────────────────┐
     * │ 标题 + 说明                              │
     * │ ┌ 演示控件 ────────────────────────────┐ │
     * │ ├──────────────────────────────────────┤ │
     * │ │ 〈〉 显示代码  （hyperlink，点击展开）   │ │
     * │ │   [展开后这里渲染 CodeBlockAnt（可复制）]│ │
     * │ └──────────────────────────────────────┘ │
     * └────────────────────────────────────────┘
     * </pre>
     *
     * <p><b>关于代码字符串的维护</b>：参数 {@code sourceCode} 是手工写的「教学代码」，
     * 跟实际 demo 调用代码<b>独立维护</b>。理由：</p>
     * <ul>
     *   <li>jar 部署后无法读取 .java 源码（反射只能看签名）</li>
     *   <li>教学代码可以做适度简化（去掉 {@code Demos.row(...)} 包装等示例特定噪音）</li>
     *   <li>Ant Design / Element Plus 文档站均采用此模式，5+ 年实战验证可维护</li>
     * </ul>
     * <p>若 {@code sourceCode} 为 null，行为退化为 {@link #section(String, String, Node...)}（不渲染 toggle）。</p>
     */
    public static Node sectionWithCode(String title, String description, String sourceCode, Node... demoNodes) {
        VBox content = ShowcaseSectionTemplate.create()
                .title(title)
                .description(description)
                .body(demoNodes)
                .build();

        Node codeToggle = buildCodeToggle(sourceCode);
        if (codeToggle != null) {
            content.getChildren().add(codeToggle);
        }

        return GroupBoxAnt.create()
                .content(content)
                .bordered(true)
                .build();
    }

    /**
     * 构造「〈〉 显示代码」可折叠区。点击 toggle 切换 CodeBlock 的可见性。
     *
     * <p>实现细节：用 {@link Label} + {@code setOnMouseClicked} 实现轻量级 toggle，
     * 不引入 {@code Hyperlink} 控件——保留默认按钮的视觉模式（避免下划线/紫色等）。
     * CodeBlock 默认 {@code managed=false} + {@code visible=false}，避免折叠态占位。</p>
     */
    private static Node buildCodeToggle(String sourceCode) {
        if (sourceCode == null || sourceCode.isBlank()) {
            return null;
        }

        // toggle 文本：箭头 + 文字
        Label toggle = TypographyAnt.text("〈〉 显示代码").build();
        toggle.getStyleClass().add("jfx-demo-code-toggle");

        // 代码块（默认隐藏 + 不占布局）
        Node codeBlock = CodeBlockAnt.create()
                .language(CodeBlockAnt.Language.JAVA)
                .code(sourceCode)
                .showLineNumbers(true)
                .theme(CodeBlockAnt.Theme.AUTO)
                .selectable(true)   // 示例代码：可自由拖选 + Ctrl+C 抄走片段（单色，但选区比高亮重要）
                .maxHeight(360)
                .build();
        codeBlock.setVisible(false);
        codeBlock.setManaged(false);

        // 容器：toggle 上、code 下
        // 顶部加一条细分隔线（与上方演示区视觉拉开）
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.getStyleClass().add("jfx-demo-code-separator");
        VBox.setMargin(sep, new javafx.geometry.Insets(4, 0, 4, 0));

        // 点击切换
        toggle.setOnMouseClicked(e -> {
            boolean show = !codeBlock.isVisible();
            codeBlock.setVisible(show);
            codeBlock.setManaged(show);
            toggle.setText(show ? "〈/〉 收起代码" : "〈〉 显示代码");
        });

        return VBarAnt.create()
                .compact()
                .top(sep, toggle, codeBlock)
                .build();
    }

    /**
     * 横向排列的演示行 —— 多个控件水平铺开，自动间距 12，垂直居中。
     *
     * <p>用 HBoxAnt 而非 FlowPane —— 演示场景一般控件少，HBox 更直观；
     * 控件多到换行的场景可以再上 FlowPaneAnt。</p>
     */
    public static HBox row(Node... children) {
        return HBoxAnt.create()
                .spacing(12)
                .align(javafx.geometry.Pos.CENTER_LEFT)
                .children(children);
    }

    /** 纵向排列的演示列。 */
    public static VBox column(Node... children) {
        return VBarAnt.create()
                .compact()
                .gap(8)
                .top(children)
                .build();
    }

    /**
     * 控件前面挂个小标签（用于"label: 控件"的成对展示）。
     */
    public static Node labeled(String label, Node control) {
        Label l = TypographyAnt.text(label).type(TypographyAnt.TextColor.SECONDARY).build();
        return HBoxAnt.create()
                .spacing(8)
                .align(javafx.geometry.Pos.CENTER_LEFT)
                .children(l, control);
    }

    /**
     * 给页面一个统一的"占位区域"——用于演示 BorderPane 五区位、SplitPane 等
     * 容器组件时填充各个槽位。
     *
     * @param text 展示文字
     * @param bg   背景色 enum；null 用 SUBTLE
     */
    public static Node placeholder(String text, Background bg) {
        VBoxAnt pane = VBoxAnt.create()
                .padding(24)
                .align(javafx.geometry.Pos.CENTER)
                .children(TypographyAnt.text(text).type(TypographyAnt.TextColor.SECONDARY).build());
        pane.setMinHeight(80);
        pane.background(bg != null ? bg : Background.SUBTLE);
        return pane;
    }

    /**
     * Grid 栅格示例专用色块（对齐 Ant Design Grid 文档的蓝色交替风格）。
     *
     * <p>奇数列深蓝、偶数列浅蓝，白色文字标注 span 值，一眼看出列宽比例。</p>
     *
     * @param text  显示文字（如 "col-12"）
     * @param dark  true=深蓝，false=浅蓝
     */
    public static Node colBlock(String text, boolean dark) {
        Label label = TypographyAnt.text(text).build();
        VBoxAnt pane = VBoxAnt.create()
                .align(javafx.geometry.Pos.CENTER)
                .children(label);
        pane.getStyleClass().add("jfx-demo-col-block");
        pane.getStyleClass().add(dark ? "jfx-demo-col-dark" : "jfx-demo-col-light");
        return pane;
    }
}
