package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.SpinnerAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Spinner 数字步进 —— 原生 Spinner 包装（SpinnerAnt）。
 *
 * <p>覆盖：基础 / 范围与步长 / 精度 / 可编辑 / 尺寸 / 双向绑定 / 禁用。</p>
 */
public class SpinnerExamplePage extends VBoxAnt {

    public SpinnerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spinner 数字步进")
                .description("SpinnerAnt 继承 Spinner<Double>：范围 / 步长 / 精度 / 可编辑 / 双向绑定，"
                        + "主题、键盘焦点与箭头几何由 .spinner LESS 收口。")
                .sections(
                        choiceSection(),
                        basicSection(),
                        rangeStepSection(),
                        precisionSection(),
                        editableSection(),
                        sizeSection(),
                        bindSection(),
                        disabledSection()
                )
                .padding(24)
                .build());
    }

    /**
     * 0. 选型建议 —— 明确 SpinnerAnt 与 InputNumberAnt / SpinAnt 的推荐边界。
     */
    private Node choiceSection() {
        return Demos.section("选型建议",
                "常规数字输入（表单 / 金额 / 百分比）优先 InputNumberAnt：自绘大热区 + prefix/suffix，"
                        + "FormAnt 校验与 reset 已接线。\n"
                        + "需要 JavaFX Spinner 语义（valueProperty、increment()、第三方 API 要求 Spinner 类型、"
                        + "原生皮肤）再用 SpinnerAnt；进 FormAnt 时请自行 bindValue(DoubleProperty) 接值。\n"
                        + "加载动画用 SpinAnt（SPINNER / DOTS / BARS）。");
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SpinnerAnt.create().build(),
                SpinnerAnt.create().value(5).build(),
                SpinnerAnt.create().value(42).prefW(140).build()
        );
        String code = """
                SpinnerAnt.create().build();                      // 默认值 0，步长 1
                SpinnerAnt.create().value(5).build();
                SpinnerAnt.create().value(42).prefW(140).build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认范围 ±∞、值 0、步长 1（精度 0）；可直接键盘输入，也可点右侧上下箭头步进。", code, demo);
    }

    private Node rangeStepSection() {
        Node demo = Demos.row(
                SpinnerAnt.create(-90, 90, 0, 0.01).prefW(120).build(),
                SpinnerAnt.create(0, 100, 20, 5).prefW(120).build(),
                SpinnerAnt.create(0, 1, 0.5, 0.1).prefW(120).build()
        );
        String code = """
                // 经纬度：±90，步长 0.01（精度自动 2 位）
                SpinnerAnt.create(-90, 90, 0, 0.01).build();

                // 0~100，每次 +/- 5
                SpinnerAnt.create(0, 100, 20, 5).build();

                // 0~1，步长 0.1
                SpinnerAnt.create(0, 1, 0.5, 0.1).build();
                """;
        return Demos.sectionWithCode("2. 范围与步长",
                "越界值自动钳制到 [min, max]；步长小数位决定默认精度。", code, demo);
    }

    private Node precisionSection() {
        Node demo = Demos.row(
                SpinnerAnt.create(0, 100, 3.14159, 0.001).precision(3).prefW(120).build(),
                SpinnerAnt.create(0, 10, 3.6, 1).precision(0).prefW(120).build(),
                SpinnerAnt.create(0, 100, 3.14159, 0.1).prefW(120).build()
        );
        String code = """
                // 显式 3 位小数
                SpinnerAnt.create(0, 100, 3.14159, 0.001).precision(3).build();

                // 整数显示，模型值 HALF_UP 取整（3.6 → 4）
                SpinnerAnt.create(0, 10, 3.6, 1).precision(0).build();

                // 未指定 precision：按 step 0.1 自动推导为 1 位
                SpinnerAnt.create(0, 100, 3.14159, 0.1).build();
                """;
        return Demos.sectionWithCode("3. 精度",
                "precision(n) 同时约束显示与模型取整；precision(-1) 恢复“按 step 自动”。", code, demo);
    }

    private Node editableSection() {
        SpinnerAnt editable = SpinnerAnt.create(-90, 90, 0, 0.01)
                .placeholder("输入经纬度")
                .prefW(140)
                .build();
        Node demo = Demos.row(
                editable,
                SpinnerAnt.create(-90, 90, 0, 0.01).editable(false).prefW(140).build()
        );
        String code = """
                // 默认可键入：数字过滤器拦截非法字符，非法提交回退当前值
                SpinnerAnt.create(-90, 90, 0, 0.01)
                        .placeholder("输入经纬度")
                        .build();

                // editable(false) 切回只读，只能点箭头
                SpinnerAnt.create(-90, 90, 0, 0.01).editable(false).build();
                """;
        return Demos.sectionWithCode("4. 键盘输入 / 只读",
                "默认可直接键入，失焦 / 回车提交，非法输入自动回滚；editable(false) 只保留箭头步进。", code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                SpinnerAnt.create(0, 100, 10, 1).size(Size.SMALL).prefW(110).build(),
                SpinnerAnt.create(0, 100, 10, 1).prefW(110).build(),
                SpinnerAnt.create(0, 100, 10, 1).size(Size.LARGE).prefW(110).build()
        );
        String code = """
                SpinnerAnt.create(0, 100, 10, 1).size(Size.SMALL).build();
                SpinnerAnt.create(0, 100, 10, 1).build();          // DEFAULT
                SpinnerAnt.create(0, 100, 10, 1).size(Size.LARGE).build();
                """;
        return Demos.sectionWithCode("5. 尺寸",
                "SMALL / DEFAULT / LARGE 走 .spinner.small / .spinner.large，复用输入框 padding 与字号 token。",
                code, demo);
    }

    private Node bindSection() {
        DoubleProperty quantity = new SimpleDoubleProperty(2);
        Label valueLabel = TypographyAnt.text("quantity = 2.0").build();

        SpinnerAnt bound = SpinnerAnt.create(0, 99, quantity.get(), 1)
                .bindValue(quantity)
                .onChange(v -> valueLabel.setText("quantity = " + v))
                .prefW(120)
                .build();

        ButtonAnt plus = ButtonAnt.create("quantity +1")
                .onClick(e -> quantity.set(quantity.get() + 1))
                .build();

        Node demo = Demos.row(Demos.column(bound, plus), valueLabel);
        String code = """
                DoubleProperty quantity = new SimpleDoubleProperty(2);

                SpinnerAnt bound = SpinnerAnt.create(0, 99, quantity.get(), 1)
                        .bindValue(quantity)
                        .onChange(v -> log(v))
                        .build();

                // 外部改 Property，Spinner 同步；Spinner 步进也会回写 Property
                quantity.set(quantity.get() + 1);
                """;
        return Demos.sectionWithCode("6. 双向绑定与回调",
                "bindValue(DoubleProperty) 双向同步；onChange 在取整后回调，外部按钮改 Property 也能驱动控件。",
                code, demo);
    }

    private Node disabledSection() {
        Node demo = Demos.row(
                SpinnerAnt.create(0, 100, 10, 1).disabled(true).prefW(120).build(),
                SpinnerAnt.create(0, 100, 10, 1).prefW(120).build()
        );
        String code = """
                SpinnerAnt.create(0, 100, 10, 1).disabled(true).build();
                SpinnerAnt.create(0, 100, 10, 1).build();
                """;
        return Demos.sectionWithCode("7. 禁用",
                "disabled(true) 统一走全局 *:disabled opacity，颜色与输入框一致。", code, demo);
    }
}
