package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

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

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public enum Shape {
        DEFAULT, ROUND, SQUARE
    }

    /** 状态键名：把 build() 时的 type/size/shape/bordered 挂到节点 properties，便于 modify() 复用。 */
    private static final String STATE_KEY = "jfxium.tag.state";

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
            this.text = text;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape;
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
            HBox tag = new HBox(4);
            tag.setAlignment(javafx.geometry.Pos.CENTER);

            // 基础类（一次性，modify 不会清掉）
            tag.getStyleClass().add("tag");

            // 把当前状态存进 properties，便于 modify() 时无差别重新渲染
            tag.getProperties().put(STATE_KEY, new TagState(type, size, shape, bordered));

            // 应用类型修饰类 + inline 视觉（与 ModifyBuilder.apply() 共享同一段逻辑）
            applyVisualState(tag, type, size, shape, bordered);

            // Label
            Label label = new Label(text);
            label.getStyleClass().add("tag-label");
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

            return tag;
        }

        private StackPane createCloseButton() {
            StackPane closeBtn = new StackPane();
            closeBtn.getStyleClass().add("tag-close");
            closeBtn.setPrefSize(12, 12);
            closeBtn.setMaxSize(12, 12);

            SVGPath x = new SVGPath();
            x.setContent("M6 4.5L4.5 6 6 7.5 7.5 6 6 4.5z");
            x.getStyleClass().add("tag-close-icon");
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
        // 1. 清掉所有可能残留的状态类（保留 "tag" 基础类与用户自定义类）
        for (Type t : Type.values()) tag.getStyleClass().remove(t.name().toLowerCase());
        for (Size s : Size.values()) tag.getStyleClass().remove(s.name().toLowerCase());
        for (Shape sh : Shape.values()) tag.getStyleClass().remove(sh.name().toLowerCase());
        tag.getStyleClass().remove("no-border");

        // 2. 重新挂当前状态类
        tag.getStyleClass().add(type.name().toLowerCase());
        if (size != Size.DEFAULT) tag.getStyleClass().add(size.name().toLowerCase());
        if (shape != Shape.DEFAULT) tag.getStyleClass().add(shape.name().toLowerCase());
        if (!bordered) tag.getStyleClass().add("no-border");

        // 3. 颜色由 LESS 中的 .tag.primary / .tag.success 等复合选择器提供，无需在此拼接

        // 4. 拼 inline style（仅布局属性，无 CSS 变量）
        String padding = switch (size) {
            case SMALL -> "0 6px";
            case LARGE -> "4px 12px";
            default -> "2px 8px";
        };
        String radius = switch (shape) {
            case ROUND -> "9999px";
            case SQUARE -> "2px";
            default -> "4px";
        };
        String fontSize = switch (size) {
            case SMALL -> "12px";
            case LARGE -> "16px";
            default -> "14px";
        };

        StringBuilder style = new StringBuilder();
        if (bordered) {
            style.append("-fx-border-width: 1px;");
        }
        style.append(" -fx-padding: ").append(padding).append(";");
        style.append(" -fx-background-radius: ").append(radius).append(";");
        style.append(" -fx-border-radius: ").append(radius).append(";");
        style.append(" -fx-font-size: ").append(fontSize).append(";");
        tag.setStyle(style.toString());
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

            // 改文字：直接打到 build 时存的 label 上
            if (textSet) {
                Object label = tag.getProperties().get("jfxium.tag.label");
                if (label instanceof Label l) {
                    l.setText(text != null ? text : "");
                }
            }
            return tag;
        }
    }
}
