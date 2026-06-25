package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium SplitMenuButton 组件 - 分裂式菜单按钮（继承式，双工厂模式）。
 *
 * <p><b>定位</b>：按钮 + 下拉菜单的复合控件，继承自 {@link SplitMenuButton}，
 * 跟 {@link ComboBoxAnt} / {@link ButtonAnt} 同款「双工厂模式」——
 * 既能当工厂链式构建，也能被业务继承。</p>
 *
 * <p>左侧是主操作按钮（单击触发 {@code onAction}），右侧是下拉箭头（弹出菜单项列表）。
 * 典型场景：新建按钮 → 新建文件 / 新建文件夹 / 新建项目。</p>
 *
 * @deprecated 推荐使用功能更完整的 {@link SplitButtonAnt}（shape/arrow/icon/contentDisplay 等）。
 *             两者均包装 {@link SplitMenuButton}，功能重叠。本类仅保留向后兼容。
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式</h3>
 * <pre>{@code
 * SplitMenuButton btn = SplitMenuButtonAnt.create()
 *     .text("新建")
 *     .graphic(plusIcon)
 *     .items(
 *         new MenuItem("新建文件"),
 *         new MenuItem("新建文件夹"),
 *         new MenuItem("新建项目")
 *     )
 *     .onAction(e -> quickCreate())
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class NewButton extends SplitMenuButtonAnt {
 *     public NewButton() {
 *         text("新建");
 *         items(new MenuItem("文件"), new MenuItem("文件夹"));
 *         onAction(e -> quickCreate());
 *     }
 * }
 * }</pre>
 */
@Deprecated
public class SplitMenuButtonAnt extends SplitMenuButton implements DisabledSupport<SplitMenuButtonAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口。 */
    public static SplitMenuButtonAnt create() {
        return new SplitMenuButtonAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SplitMenuButtonAnt() {
        super();
        getStyleClass().add(JfxStyles.JFX_SPLIT_MENU_BUTTON);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置按钮文本。 */
    public SplitMenuButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /** 设置按钮图形（图标）。 */
    public SplitMenuButtonAnt graphic(Node graphic) {
        setGraphic(graphic);
        return this;
    }

    /** 批量设置菜单项（清空旧项）。 */
    public SplitMenuButtonAnt items(MenuItem... items) {
        getItems().clear();
        if (items != null) {
            getItems().addAll(items);
        }
        return this;
    }

    /** 追加菜单项。 */
    public SplitMenuButtonAnt addItem(MenuItem item) {
        getItems().add(item);
        return this;
    }

    /** 设置主按钮点击回调（左侧按钮）。 */
    public SplitMenuButtonAnt onAction(EventHandler<ActionEvent> handler) {
        setOnAction(handler);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。
     */
    public SplitMenuButtonAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    // ============================================================
    // Builder 终结
    // ============================================================

    /** Builder 模式终结调用——返回自身。 */
    public SplitMenuButtonAnt build() {
        return this;
    }
}
