package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.IconPath;
import org.openkawu.jfxium.core.util.NumericUtils;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.List;

/**
 * JFXium 标签组件 - 对标 Ant Design Tag（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：小型语义化标签，用于标记状态、分类、属性等，
 * 支持多种类型 / 尺寸 / 形状变体。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>类型</b>：DEFAULT / PRIMARY / SUCCESS / WARNING / ERROR / PROCESSING</li>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / LARGE</li>
 *   <li><b>形状</b>：DEFAULT / ROUND / SQUARE</li>
 *   <li><b>边框</b>：bordered 可选</li>
 *   <li><b>可关闭</b>：closable 带关闭按钮 + onClose 回调</li>
 *   <li><b>可修改</b>：modify() 方法支持 build 后再改类型 / 尺寸</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>状态标签（已发布 / 草稿 / 已下线）</li>
 *   <li>属性标签（热门 / 新品 / VIP）</li>
 *   <li>表格内联标签（操作结果、用户角色）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node tag = TagAnt.create("已发布")
 *     .type(TagAnt.Type.SUCCESS)
 *     .size(TagAnt.Size.SMALL)
 *     .build();
 *
 * Node closable = TagAnt.create("可删除")
 *     .type(TagAnt.Type.ERROR)
 *     .closable(true)
 *     .onClose(() -> System.out.println("已关闭"))
 *     .build();
 * }</pre>
 */
public class TagAnt {

    public enum Type {
        DEFAULT, PRIMARY, SUCCESS, WARNING, ERROR, PROCESSING
    }

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    public enum Shape {
        DEFAULT, ROUND, SQUARE
    }

    /** 状态键名：把 build() 时的 type/size/shape/bordered 挂到节点 properties，便于 modify() 复用。 */
    private static final String STATE_KEY = "jfxium.tag.state";

    /** 自定义颜色键：用户调 {@code .color(c)} 时把 Color 存到 properties，供 modify 复用。 */
    private static final String CUSTOM_COLOR_KEY = "jfxium.tag.customColor";

    /** 内部状态对象（不可变 record，便于复用 + 日志清晰）。 */
    private record TagState(Type type, Size size, Shape shape, boolean bordered) {}

    /**
     * 再编辑已构建的 Tag（M19.27 modify+apply 模式）。
     *
     * <p>TagAnt 是「业务状态机」的高频组件——订单状态切色（待付款 → 已付款 → 已发货）、
     * 任务优先级切色（低 → 中 → 高）等场景每天都在写。{@code modify} 直接对原生 {@link HBox}
     * 做无痛切换：清旧 styleClass + 重新计算 inline style，避免重建节点导致的闪烁。</p>
     *
     * <pre>{@code
     * HBox statusTag = TagAnt.create("待审核").type(TagAnt.Type.WARNING).build();
     *
     * // 审核通过后切成绿色
     * approveBtn.setOnAction(e ->
     *     TagAnt.modify(statusTag).type(TagAnt.Type.SUCCESS).text("已通过").apply());
     * }</pre>
     *
     * @param tag {@link #create()} 构建出的 HBox，不能为 null
     */
    public static ModifyBuilder modify(HBox tag) {
        if (tag == null) {
            throw new NullPointerException("tag 不能为 null");
        }
        return new ModifyBuilder(tag);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String text = "";
        private Type type = Type.DEFAULT;
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.DEFAULT;
        private boolean closable = false;
        private boolean bordered = true;
        private Color customColor = null;
        private Runnable onClose = null;

        public Builder text(String text) {
            this.text = TextUtils.safeText(text);
            return this;
        }

        public Builder type(Type type) {
            this.type = type != null ? type : Type.DEFAULT;
            return this;
        }

        public Builder size(Size size) {
            this.size = size != null ? size : Size.DEFAULT;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape != null ? shape : Shape.DEFAULT;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder closable() {
            return closable(true);
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder noBorder() {
            return bordered(false);
        }

        public Builder color(Color color) {
            this.customColor = color;
            return this;
        }

        public Builder onClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public HBox build() {
            HBox tag = new HBox();
            tag.setAlignment(javafx.geometry.Pos.CENTER);

            // 基础类（一次性，modify 不会清掉）
            tag.getStyleClass().add(JfxStyles.TAG);

            // 把当前状态存进 properties，便于 modify() 时无差别重新渲染
            tag.getProperties().put(STATE_KEY, new TagState(type, size, shape, bordered));

            // 自定义颜色：存到 properties 供 modify 复用
            if (customColor != null) {
                tag.getProperties().put(CUSTOM_COLOR_KEY, customColor);
            }

            // 应用类型修饰类 + inline 视觉（与 ModifyBuilder.apply() 共享同一段逻辑）
            applyVisualState(tag, type, size, shape, bordered);

            // 应用自定义颜色：BUG #131 修复——customColor 之前是死字段，现挂 TAG_HAS_COLOR 类 + setStyle 覆背景/字色
            applyCustomColor(tag, customColor);

            // Label
            Label label = new Label(text);
            label.getStyleClass().add(JfxStyles.TAG_LABEL);
            tag.getChildren().add(label);
            // 把 label 也挂到 properties，modify().text(...) 时直接 setText 不重建
            tag.getProperties().put("jfxium.tag.label", label);

            // Close button
            if (closable) {
                StackPane closeBtn = createCloseButton();
                closeBtn.setOnMouseClicked(e -> {
                    if (onClose != null) {
                        onClose.run();
                    }
                    tag.setVisible(false);
                    tag.setManaged(false);
                });
                tag.getChildren().add(closeBtn);
            }

            applyStyles(tag);
            return tag;
        }

        private StackPane createCloseButton() {
            StackPane closeBtn = new StackPane();
            closeBtn.getStyleClass().add(JfxStyles.TAG_CLOSE);
            closeBtn.setPrefSize(12, 12);
            closeBtn.setMaxSize(12, 12);

            SVGPath x = IconPath.closeX();
            x.getStyleClass().add(JfxStyles.TAG_CLOSE_ICON);
            closeBtn.getChildren().add(x);

            closeBtn.setOnMouseEntered(e -> closeBtn.setOpacity(0.8));
            closeBtn.setOnMouseExited(e -> closeBtn.setOpacity(1.0));

            return closeBtn;
        }
    }

    public static Builder create(String text) {
        return new Builder().text(text);
    }

    public static Builder create() {
        return new Builder();
    }

    // ---------------------------------------------------------
    // Shared visual rendering（Builder.build() / ModifyBuilder.apply() 共用）
    // ---------------------------------------------------------

    /**
     * 应用 Tag 的 type / size / shape / bordered 完整视觉状态。
     *
     * <p>步骤：清掉所有可能的状态 styleClass → 重新挂上当前状态的 styleClass → 重写 inline style。
     * 颜色三元组通过 styleClass + LESS 定义（避免 setStyle 中 CSS 变量导致 ClassCastException），
     * inline style 只负责 padding/radius/font-size/border-width 等布局属性。</p>
     */
    private static void applyVisualState(HBox tag, Type type, Size size, Shape shape, boolean bordered) {
        // 1. 清掉所有可能残留的状态类（保留 JfxStyles.TAG 基础类与用户自定义类）
        for (String stateCls : TAG_STATE_CLASSES) {
            tag.getStyleClass().remove(stateCls);
        }

        // 2. 重新挂当前状态类（按 JfxStyles 常量走，避免裸名——红线 #8）
        tag.getStyleClass().add(typeStyleClass(type));
        String sizeCls = sizeStyleClass(size);
        if (sizeCls != null) tag.getStyleClass().add(sizeCls);
        String shapeCls = shapeStyleClass(shape);
        if (shapeCls != null) tag.getStyleClass().add(shapeCls);
        if (!bordered) tag.getStyleClass().add(JfxStyles.TAG_BORDERLESS);

        // 3. 颜色、padding、radius、font-size 全部由 LESS 复合选择器提供，无需 setStyle（红线 #1）
        // 4. 圆角处理也下沉到 .jfx-tag-rounded（红线的 #6 不适用：Tag 是 size-clamped 节点）
    }

    /**
     * 应用 Tag 的自定义颜色：染背景 + 文本色。
     *
     * <p>对标 Ant Design Tag presetColors 语义：用户传任意 Color，把 Tag 染成该色。
     * 文本色完全由 LESS 接管（{@code .jfx-tag.jfx-tag-has-color} 选择器已定义
     * {@code -fx-text-fill: -color-fg-on-emphasis}），背景色通过 inline setStyle 写入
     * ——因为是用户运行时传入的颜色 token，无法预先在 LESS 中枚举。
     * 这是 setStyle 写颜色在 JFXium 中唯一被允许的场景：颜色来源是 API 参数而非主题派生。</p>
     */
    private static void applyCustomColor(HBox tag, Color color) {
        if (color != null) {
            if (!tag.getStyleClass().contains(JfxStyles.TAG_HAS_COLOR)) {
                tag.getStyleClass().add(JfxStyles.TAG_HAS_COLOR);
            }
            // 只覆写 background-color，保留调用方通过 Builder/style() 传入的其他 inline style。
            tag.setStyle(upsertStyleDeclaration(tag.getStyle(), "-fx-background-color", toHexColor(color)));
        } else {
            tag.getStyleClass().remove(JfxStyles.TAG_HAS_COLOR);
            tag.setStyle(removeStyleDeclaration(tag.getStyle(), "-fx-background-color"));
        }
    }

    private static String upsertStyleDeclaration(String style, String property, String value) {
        String withoutProperty = removeStyleDeclaration(style, property);
        String declaration = property + ": " + value + ";";
        if (withoutProperty == null || withoutProperty.isBlank()) {
            return declaration;
        }
        return withoutProperty.endsWith(";") ? withoutProperty + " " + declaration : withoutProperty + "; " + declaration;
    }

    private static String removeStyleDeclaration(String style, String property) {
        if (style == null || style.isBlank()) {
            return null;
        }
        StringBuilder kept = new StringBuilder();
        String propertyLower = property.toLowerCase();
        for (String declaration : style.split(";")) {
            String trimmed = declaration.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int colonIndex = trimmed.indexOf(':');
            if (colonIndex < 0) {
                continue;
            }
            String name = trimmed.substring(0, colonIndex).trim();
            if (name.toLowerCase().equals(propertyLower)) {
                continue;
            }
            if (kept.length() > 0) {
                kept.append("; ");
            }
            kept.append(trimmed);
        }
        return kept.length() == 0 ? null : kept.append(';').toString();
    }

    /** Color → CSS hex (#rrggbb)。Alpha 不输出，因为 Tag 是 opaque。 */
    private static String toHexColor(Color color) {
        return String.format("#%02x%02x%02x",
                clampByte(color.getRed()),
                clampByte(color.getGreen()),
                clampByte(color.getBlue()));
    }

    /**
     * 把 0..1 的色道值钳制到 0..255 的整数。集中处理越界、NaN 防御。
     *
     * <p>先把 channel 钳到 [0,1]（NaN/Infinity 由 NumericUtils.clamp 回退到 0），
     * 再线性映射到 [0,255]。比手写 {@code Double.isFinite + Math.round + clamp} 更紧凑。</p>
     */
    private static int clampByte(double channel) {
        return (int) Math.round(NumericUtils.clamp(channel, 0, 1, 0) * 255);
    }

    /** Tag 所有可能的状态 styleClass 集合（用于清理 applyVisualState 中的旧状态）。 */
    private static final List<String> TAG_STATE_CLASSES = List.of(
            JfxStyles.TAG_DEFAULT, JfxStyles.TAG_PRIMARY, JfxStyles.TAG_SUCCESS,
            JfxStyles.TAG_PROCESSING, JfxStyles.TAG_ERROR, JfxStyles.TAG_WARNING,
            JfxStyles.TAG_SMALL, JfxStyles.TAG_LARGE,
            JfxStyles.TAG_ROUNDED, JfxStyles.TAG_SQUARE,
            JfxStyles.TAG_BORDERLESS, JfxStyles.TAG_HAS_COLOR
    );

    private static String typeStyleClass(Type t) {
        Type effectiveType = t != null ? t : Type.DEFAULT;
        return switch (effectiveType) {
            case PRIMARY    -> JfxStyles.TAG_PRIMARY;
            case SUCCESS    -> JfxStyles.TAG_SUCCESS;
            case WARNING    -> JfxStyles.TAG_WARNING;
            case ERROR      -> JfxStyles.TAG_ERROR;
            case PROCESSING -> JfxStyles.TAG_PROCESSING;
            default         -> JfxStyles.TAG_DEFAULT;
        };
    }

    private static String sizeStyleClass(Size s) {
        Size effectiveSize = s != null ? s : Size.DEFAULT;
        return switch (effectiveSize) {
            case SMALL -> JfxStyles.TAG_SMALL;
            case LARGE -> JfxStyles.TAG_LARGE;
            default    -> null;
        };
    }

    private static String shapeStyleClass(Shape sh) {
        Shape effectiveShape = sh != null ? sh : Shape.DEFAULT;
        return switch (effectiveShape) {
            case ROUND  -> JfxStyles.TAG_ROUNDED;
            case SQUARE -> JfxStyles.TAG_SQUARE;
            default     -> null;
        };
    }

    /**
     * Tag 的再编辑入口（M19.27）。
     *
     * <p>支持改：type / size / shape / bordered / text。{@link #apply()} 返回**原 HBox 实例**。
     * 未调用的属性沿用 build 时的值（从 properties 读出），避免误清掉用户的状态。</p>
     */
    public static class ModifyBuilder {
        private final HBox tag;
        private Type type;
        private boolean typeSet = false;
        private Size size;
        private boolean sizeSet = false;
        private Shape shape;
        private boolean shapeSet = false;
        private Boolean bordered;
        private String text;
        private boolean textSet = false;
        private Color color;
        private boolean colorSet = false;

        ModifyBuilder(HBox tag) {
            this.tag = tag;
        }

        public ModifyBuilder type(Type type) {
            this.type = type;
            this.typeSet = true;
            return this;
        }

        public ModifyBuilder size(Size size) {
            this.size = size;
            this.sizeSet = true;
            return this;
        }

        public ModifyBuilder shape(Shape shape) {
            this.shape = shape;
            this.shapeSet = true;
            return this;
        }

        public ModifyBuilder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public ModifyBuilder text(String text) {
            this.text = text;
            this.textSet = true;
            return this;
        }

        public ModifyBuilder color(Color color) {
            this.color = color;
            this.colorSet = true;
            return this;
        }

        /** 应用所有修改到原 Tag 上。返回原 HBox 实例。 */
        public HBox apply() {
            // 取出 build 时存的状态作为基线（如果不是 TagAnt.create() 出来的节点，State 为 null，按默认值兜底）
            TagState old = (TagState) tag.getProperties().get(STATE_KEY);
            Type effType = typeSet ? (type != null ? type : Type.DEFAULT)
                                   : (old != null ? old.type() : Type.DEFAULT);
            Size effSize = sizeSet ? (size != null ? size : Size.DEFAULT)
                                   : (old != null ? old.size() : Size.DEFAULT);
            Shape effShape = shapeSet ? (shape != null ? shape : Shape.DEFAULT)
                                      : (old != null ? old.shape() : Shape.DEFAULT);
            boolean effBordered = bordered != null ? bordered
                                                   : (old != null ? old.bordered() : true);

            // 重写视觉状态 + 更新 state
            applyVisualState(tag, effType, effSize, effShape, effBordered);
            tag.getProperties().put(STATE_KEY, new TagState(effType, effSize, effShape, effBordered));

            // 自定义颜色：与 build() 保持一致。优先取 modify 传入，否则从 properties 读出上次的颜色
            Color effColor;
            if (colorSet) {
                effColor = color;
            } else {
                Object cached = tag.getProperties().get(CUSTOM_COLOR_KEY);
                effColor = cached instanceof Color ? (Color) cached : null;
            }
            if (effColor != null) {
                tag.getProperties().put(CUSTOM_COLOR_KEY, effColor);
            } else {
                tag.getProperties().remove(CUSTOM_COLOR_KEY);
            }
            applyCustomColor(tag, effColor);

            // 改文字：直接打到 build 时存的 label 上
            if (textSet) {
                Object label = tag.getProperties().get("jfxium.tag.label");
                if (label instanceof Label l) {
                    l.setText(TextUtils.safeText(text));
                }
            }
            return tag;
        }
    }
}
