package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.layout.AbstractStackPaneAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium 带清除按钮的输入框 - 对标 Ant Design Input allowClear（继承式组合）。
 *
 * <p><b>定位</b>：文本输入的「可清除」变体。{@link InputAnt} 继承自 {@link javafx.scene.text.TextField}
 * 无法自带兄弟按钮，因此本组件按项目既定的「继承式组合」范式提供：
 * 自身 {@code extends StackPaneAnt} 承载布局能力，内部放一个 {@link InputAnt}
 * + 一个<b>悬浮</b>在其右端的清除按钮。</p>
 *
 * <h2>为什么清除按钮是「悬浮」而不是「并排」</h2>
 * <p>它是<b>叠加层</b>（{@code StackPane} 子节点 + {@code USE_PREF_SIZE} 尺寸钳制），
 * 不参与父容器布局。若做成 {@code HBox} 的并排子节点，按钮的出现/消失会直接改变
 * 组件自身宽度（实测 ±28px），空 ↔ 有内容的切换会把整行布局顶得来回抖 ——
 * 这正是「清除后长度变短、X 号还占着长度」的成因。</p>
 * <p>Ant Design 的解法是<b>恒定预留</b>右侧空间：输入框右内边距始终等于按钮宽度
 * （{@code @search-input-affix-width}），有无按钮都一样。文字提前让位，
 * 因此视觉上按钮「盖在」留白区，宽度恒定不抖动。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * SearchInputAnt search = SearchInputAnt.create()
 *     .placeholder("搜索组件…")
 *     .onSearch(kw -> System.out.println("回车搜索：" + kw))
 *     .build();
 * }</pre>
 *
 * <h2>清除按钮的显隐</h2>
 * 仅在有内容时可见（对齐 Ant Design）。{@code allowClear(false)} 则整个按钮不创建。
 */
public class SearchInputAnt extends AbstractStackPaneAnt<SearchInputAnt> {

    private final InputAnt field = InputAnt.create();
    private final HBox clearBtn = new HBox();
    private boolean allowClear = true;
    private Consumer<String> onChange = null;
    private Consumer<String> onSearch = null;

    private SearchInputAnt() {
        super();
        setAlignment(Pos.CENTER_RIGHT);
        getStyleClass().add(JfxStyles.SEARCH_INPUT);

        field.getStyleClass().add(JfxStyles.SEARCH_INPUT_FIELD);

        // 清除按钮：结构与图标由 IconAnt 收口，视觉走 LESS（.jfx-search-input-clear）
        clearBtn.setAlignment(Pos.CENTER);
        clearBtn.getStyleClass().add(JfxStyles.SEARCH_INPUT_CLEAR);
        clearBtn.getChildren().add(IconAnt.symbol(IconAnt.Symbol.CLOSE, 12));
        // 叠加层必须钳到自身 pref 尺寸：否则 StackPane 会把它拉伸到容器大小，
        // 对齐方式（CENTER_RIGHT 右端其对）失效，点击热区也会糊满整行。
        clearBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        clearBtn.setOnMouseClicked(e -> clear());
        // 键盘可达：清除按钮是可聚焦控件之外的交互元素，需显式支持空格/回车触发
        clearBtn.setFocusTraversable(true);
        clearBtn.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE, ENTER -> clear();
                default -> { /* 其它按键不处理 */ }
            }
        });

        getChildren().addAll(field, clearBtn);

        // 显隐随内容变化（含程序化 setText / 双向绑定）
        field.textProperty().addListener((obs, oldText, newText) -> {
            syncClearVisibility();
            if (onChange != null) onChange.accept(newText);
        });
        field.setOnAction(e -> {
            if (onSearch != null) onSearch.accept(field.getText());
        });

        syncClearVisibility();
    }

    /** 工厂入口。 */
    public static SearchInputAnt create() {
        return new SearchInputAnt();
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 占位提示。 */
    public SearchInputAnt placeholder(String placeholder) {
        field.placeholder(placeholder);
        return this;
    }

    /** 初始文本。 */
    public SearchInputAnt text(String text) {
        field.text(text);
        return this;
    }

    /** 尺寸（作用于内部输入框与整体高度）。 */
    public SearchInputAnt size(Size size) {
        field.size(size);
        ApplySizeUtil.apply(getStyleClass(), size);
        return this;
    }

    /** 是否显示清除按钮（默认 true）。 */
    public SearchInputAnt allowClear(boolean allowClear) {
        this.allowClear = allowClear;
        if (!allowClear) {
            getChildren().remove(clearBtn);
        } else if (!getChildren().contains(clearBtn)) {
            getChildren().add(clearBtn);
        }
        syncClearVisibility();
        return this;
    }

    /** 文本变化回调。 */
    public SearchInputAnt onChange(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /** 回车（提交）回调 —— 搜索框的核心交互。 */
    public SearchInputAnt onSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
        return this;
    }

    /** 双向绑定：内部输入框文本 ↔ Property。 */
    public SearchInputAnt bindValue(StringProperty property) {
        field.bindValue(property);
        return this;
    }

    // ============================================================
    // 状态查询 / 操作
    // ============================================================

    /** 当前文本。 */
    public String text() {
        return field.getText();
    }

    /** 清空文本（清除按钮点击 / 外部调用共用同一入口）。 */
    public void clear() {
        if (field.isDisabled()) return;
        field.setText("");
    }

    /** 内部输入框——需要更深定制（选中范围、焦点、文本格式化）时使用。 */
    public InputAnt input() {
        return field;
    }

    /**
     * 清除按钮节点——需要自定义图标或参与测试时使用。
     * 注意：{@code allowClear(false)} 时该节点会被移出子树。
     */
    public Node clearButton() {
        return clearBtn;
    }

    /** 清除按钮是否可见（有内容 + 允许清除）。 */
    public boolean isClearVisible() {
        return clearBtn.isVisible();
    }

    private void syncClearVisibility() {
        boolean show = allowClear && !TextUtils.safeText(field.getText()).isEmpty();
        clearBtn.setVisible(show);
        clearBtn.setManaged(show);
    }
}
