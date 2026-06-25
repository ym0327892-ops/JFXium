package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.EmptyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Empty 空状态 —— 默认 / 自定义描述。
 */
public class EmptyExamplePage extends VBoxAnt {

    public EmptyExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Empty 空状态")
                .description("数据为空时的占位展示，比空白页面更友好。")
                .sections(
                        defaultSection(),
                        customSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node defaultSection() {
        Node empty = EmptyAnt.create().build();
        String code = """
                // 默认空状态（使用 i18n 默认描述）
                Node empty = EmptyAnt.create().build();
                """;
        return Demos.sectionWithCode("1. 默认空状态", "不传参数时使用默认图标和描述文字。", code, empty);
    }

    private Node customSection() {
        Node empty = EmptyAnt.create()
                .description("暂无搜索结果，请尝试其他关键词")
                .extraButton("重新搜索", () -> MessageAnt.info("重新搜索"))
                .build();
        String code = """
                Node empty = EmptyAnt.create()
                        .description("暂无搜索结果，请尝试其他关键词")
                        .extraButton("重新搜索", () -> { /* 操作 */ })
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义描述与操作",
                "通过 description() 自定义文案，extraButton() 添加操作按钮。", code, empty);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Empty 的 3 个维度：描述文字 / extra 类型 / 按钮文字。
     *
     * <p>EmptyAnt 是 Builder 模式（{@code build()} 返回 {@link VBox}），
     * 没有 Controller 暴露，所有属性（description / extra / extraButton）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— VBox 重建，开销可接受，且保证所有属性变更生效。</p>
     *
     * <p>extra 类型：{@code none} 不添加任何额外元素；
     * {@code button} 添加一个 ButtonAnt（文字可由 buttonText 控制）；
     * {@code custom} 添加一个 TypographyAnt 文字说明 + ButtonAnt。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> description = PlayGround.binder("暂无数据");
        Binder<String> extraType   = PlayGround.binder("button");
        Binder<String> buttonText  = PlayGround.binder("重新加载");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            EmptyAnt.Builder b = EmptyAnt.create().description(description.get());
            switch (extraType.get() == null ? "button" : extraType.get()) {
                case "none" -> { /* 不加 extra */ }
                case "custom" -> b.extra(TypographyAnt.text("提示：试试刷新或筛选").build())
                        .extraButton(buttonText.get(), () -> MessageAnt.info("点击：" + buttonText.get()));
                default      -> b.extraButton(buttonText.get(), () -> MessageAnt.info("点击：" + buttonText.get()));
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Empty 的描述文字 / extra 类型 / 按钮文字 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("描述文字", PlayGround.textField(description, description.get(), "空状态描述")),
                        PlayGround.row("extra 类型", PlayGround.segmented(extraType,
                                PlayGround.entry("none",   "无"),
                                PlayGround.entry("button", "按钮"),
                                PlayGround.entry("custom", "自定义"))),
                        PlayGround.row("按钮文字", PlayGround.textField(buttonText, buttonText.get(), "extraButton 文字"))));
    }
}
