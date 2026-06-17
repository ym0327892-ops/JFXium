package org.openkawu.jfxium.demo.bind;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.composite.AlertAnt;
import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.component.composite.InputNumberAnt;
import org.openkawu.jfxium.component.composite.SliderAnt;
import org.openkawu.jfxium.component.composite.SwitchAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.ChoiceBoxAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;

/**
 * bindValue API 能力展示 Demo —— 演示「控件值 ↔ Property 双向同步」的核心场景。
 *
 * <p><b>核心价值</b>：用单一 Property 作为数据源，多个控件绑定到它即可自动同步。
 * 这与「创建控件后手动 {@code setOnChange → prop.set(x)}」相比省去了一半胶水代码，
 * 也避免了"忘了同步某一边"的常见 bug。</p>
 *
 * <p><b>覆盖控件</b>：</p>
 * <ul>
 *   <li>{@link InputNumberAnt}（composite Builder，{@code DoubleProperty}）</li>
 *   <li>{@link SliderAnt}（composite Builder，{@code DoubleProperty}）</li>
 *   <li>{@link SwitchAnt}（composite Builder，{@code BooleanProperty}）</li>
 *   <li>{@link ChoiceBoxAnt}（继承式 control，{@code Property<T>} 泛型）</li>
 * </ul>
 *
 * <p><b>运行</b>：在 IDE 跑 {@link #main(String[])}，或：
 * <pre>./mvnw javafx:run -pl jfxium-demo \
 *     -Djavafx.mainClass=org.openkawu.jfxium.demo.bind.BindValueShowcaseDemo</pre>
 * </p>
 */
public class BindValueShowcaseDemo extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add(JfxStyles.BG_LAYOUT);

        root.setTop(buildToolbar());

        ScrollPane scroll = new ScrollPane(buildShowcase());
        scroll.setFitToWidth(true);
        scroll.setPadding(new Insets(0));
        root.setCenter(scroll);

        Scene scene = new Scene(root, 1080, 820);
        ThemeManager mgr = ThemeManager.getInstance();
        mgr.registerScene(scene);
        mgr.applyTheme(new LightTheme());

        stage.setTitle("bindValue Showcase — JFXium");
        stage.setScene(scene);
        stage.show();
    }

    // ============================================================
    // 工具条
    // ============================================================

    private Node buildToolbar() {
        return HBarAnt.create()
            .left(
                LabelAnt.create().text(" bindValue 双向绑定 — 单 Property 多控件同步 ").build()
            )
            .right(
                LabelAnt.create().text(" 拖滑块 / 改输入框 / 切开关 / 选下拉，关联控件实时跟随 ").build()
            )
            .padding(8, 16, 8, 16)
            .build();
    }

    // ============================================================
    // 主展示区
    // ============================================================

    private Node buildShowcase() {
        VBoxAnt root = VBoxAnt.create()
            .spacing(28)
            .padding(24, 24, 24, 24)
            .styleClass(JfxStyles.BG_LAYOUT)
            .children(
                sectionTitle("1. 数值类：InputNumber ↔ Slider 共享 DoubleProperty"),
                sectionNumericDemo(),

                sectionTitle("2. 开关类：Switch BooleanProperty 驱动 Alert 可见性"),
                sectionBooleanDemo(),

                sectionTitle("3. 选择类：ChoiceBox Property<T> 双向驱动 Label 显示"),
                sectionChoiceDemo(),

                sectionTitle("4. 综合：单 Property 同时驱动 InputNumber + Slider（数值联动）"),
                sectionCombinedDemo()
            )
            .build();
        return root;
    }

    // ============================================================
    // Section 1: 数值类（InputNumber ↔ Slider）
    // ============================================================

    private static final String SECTION1_CODE = """
            // 单一 Property 作为「数据源」—— 多控件共享,自动同步
            DoubleProperty volume = new SimpleDoubleProperty(50);

            // Slider 绑 Property —— 拖滑块时 volume 变
            SliderAnt.create()
                .min(0).max(100)
                .bindValue(volume)
                .build();

            // InputNumber 绑同一个 Property —— 输入数字时 volume 变
            InputNumberAnt.create()
                .min(0).max(100)
                .value(50)
                .bindValue(volume)
                .build();
            """;

    private Node sectionNumericDemo() {
        // 共享 Property
        DoubleProperty volume = new SimpleDoubleProperty(50);

        // Slider 绑 Property
        Node slider = SliderAnt.create()
            .min(0).max(100)
            .bindValue(volume)
            .tooltipVisible(true)
            .build();

        // InputNumber 绑同一个 Property
        Node input = InputNumberAnt.create()
            .min(0).max(100)
            .value(50)
            .prefix("🔊 ")
            .suffix("%")
            .bindValue(volume)
            .build();

        // 实时显示 Property 当前值（用 Listener 监 property 变化写到 Label）
        Label valueLabel = new Label();
        valueLabel.getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
        valueLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
            () -> "当前值: " + (int) volume.get() + "%", volume));

        HBox row = Demos.row(
            labeled("Slider:", slider),
            labeled("InputNumber:", input),
            valueLabel
        );

        return Demos.sectionWithCode(
            "数值类双向绑定",
            "Slider 与 InputNumber 共享同一个 DoubleProperty,改任一控件 → 另一个 + 当前值显示实时跟随。",
            SECTION1_CODE,
            row
        );
    }

    // ============================================================
    // Section 2: 开关类（Switch 驱动 Alert 可见性）
    // ============================================================

    private static final String SECTION2_CODE = """
            // BooleanProperty 驱动 Switch + Alert 可见性
            BooleanProperty pushEnabled = new SimpleBooleanProperty(false);

            // Switch 绑 Property —— 切换时 pushEnabled 变
            SwitchAnt.create()
                .bindValue(pushEnabled)
                .checkedText("已开启")
                .uncheckedText("已关闭")
                .build();

            // Alert 的 visible 绑同一个 Property —— Switch 切换 → Alert 显隐
            Node alert = AlertAnt.success("您将收到推送通知").build();
            alert.visibleProperty().bind(pushEnabled);
            alert.managedProperty().bind(pushEnabled);
            """;

    private Node sectionBooleanDemo() {
        BooleanProperty pushEnabled = new SimpleBooleanProperty(false);

        Node switchBox = SwitchAnt.create()
            .bindValue(pushEnabled)
            .checkedText("已开启")
            .uncheckedText("已关闭")
            .build();

        Node alert = AlertAnt.success("您将收到推送通知").build();
        // Alert 可见性绑 Property —— Switch 开/关 → Alert 显隐
        alert.visibleProperty().bind(pushEnabled);
        alert.managedProperty().bind(pushEnabled);

        VBox col = Demos.column(
            Demos.row(labeled("推送通知:", switchBox)),
            alert
        );

        return Demos.sectionWithCode(
            "开关类双向绑定",
            "Switch 状态同步到 BooleanProperty,后者驱动 Alert 的 visible/managed —— 切开关即时显示/隐藏提示条。",
            SECTION2_CODE,
            col
        );
    }

    // ============================================================
    // Section 3: 选择类（ChoiceBox + Label）
    // ============================================================

    private static final String SECTION3_CODE = """
            // StringProperty 驱动 ChoiceBox + Label
            StringProperty theme = new SimpleStringProperty("light");

            // ChoiceBox 绑 Property —— 选主题时 theme 变
            ChoiceBoxAnt.<String>create()
                .items("light", "dark", "compact")
                .value("light")
                .bindValue(theme)
                .size(ChoiceBoxAnt.Size.SMALL)
                .build();

            // Label.text 绑同一个 Property —— theme 变 → 文字跟随
            LabelAnt.create()
                .text("当前主题: " + theme.get())
                .build();
            label.textProperty().bind(Bindings.createStringBinding(
                () -> "当前主题: " + theme.get(), theme));
            """;

    private Node sectionChoiceDemo() {
        StringProperty theme = new SimpleStringProperty("light");

        Node choiceBox = ChoiceBoxAnt.<String>create()
            .items("light", "dark", "compact")
            .value("light")
            .bindValue(theme)
            .size(ChoiceBoxAnt.Size.SMALL)
            .build();

        Label themeLabel = LabelAnt.create()
            .text("当前主题: " + theme.get())
            .build();
        themeLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
            () -> "当前主题: " + theme.get(), theme));

        // 额外加一个「重置」按钮改 Property → ChoiceBox + Label 都跟随
        ButtonAnt resetBtn = ButtonAnt.create("重置为 light")
            .type(ButtonAnt.Type.DEFAULT)
            .onClick(e -> theme.set("light"))
            .build();

        HBox row = Demos.row(
            labeled("主题:", choiceBox),
            themeLabel,
            resetBtn
        );

        return Demos.sectionWithCode(
            "选择类双向绑定",
            "ChoiceBox 选择同步到 StringProperty,后者驱动 Label.text —— 选下拉或点「重置」按钮都即时反映。",
            SECTION3_CODE,
            row
        );
    }

    // ============================================================
    // Section 4: 综合（InputNumber + Slider 联动）
    // ============================================================

    private static final String SECTION4_CODE = """
            // 同一 DoubleProperty 绑两个数值控件 + 一个 Label 镜像
            DoubleProperty price = new SimpleDoubleProperty(99.0);

            // Slider: 拖动调价
            SliderAnt.create()
                .min(0).max(500).value(99)
                .bindValue(price)
                .marks(Map.of(0.0, "0", 100.0, "100", 500.0, "500"))
                .build();

            // InputNumber: 精确输入
            InputNumberAnt.create()
                .min(0).max(500)
                .value(99)
                .precision(2)
                .prefix("¥")
                .bindValue(price)
                .build();

            // 标签: 镜像显示
            Label priceLabel = new Label();
            priceLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("最终价格: ¥%.2f", price.get()), price));
            """;

    private Node sectionCombinedDemo() {
        DoubleProperty price = new SimpleDoubleProperty(99.0);

        Node slider = SliderAnt.create()
            .min(0).max(500).value(99)
            .bindValue(price)
            .marks(java.util.Map.of(0.0, "0", 100.0, "100", 500.0, "500"))
            .build();

        Node input = InputNumberAnt.create()
            .min(0).max(500)
            .value(99)
            .precision(2)
            .prefix("¥")
            .bindValue(price)
            .build();

        Label priceLabel = new Label();
        priceLabel.getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
        priceLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
            () -> String.format("最终价格: ¥%.2f", price.get()), price));

        VBox col = Demos.column(
            Demos.row(labeled("滑动调价:", slider)),
            Demos.row(labeled("精确输入:", input), priceLabel)
        );

        return Demos.sectionWithCode(
            "综合数值联动",
            "同一 DoubleProperty 同时驱动 Slider(粗调) + InputNumber(精调) + Label(镜像) —— 改任一处其余全部跟随。",
            SECTION4_CODE,
            col
        );
    }

    // ============================================================
    // 通用工具
    // ============================================================

    private static Node labeled(String label, Node control) {
        return Demos.labeled(label, control);
    }

    private static Node sectionTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(JfxStyles.TYPOGRAPHY_TITLE);
        return l;
    }

    // ============================================================
    // Entry
    // ============================================================

    public static void main(String[] args) {
        launch(args);
    }
}
