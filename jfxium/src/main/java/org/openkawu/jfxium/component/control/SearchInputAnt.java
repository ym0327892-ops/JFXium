package org.openkawu.jfxium.component.control;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.component.layout.AbstractHBoxAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium 带清除按钮的输入框 - 对标 Ant Design Input allowClear（继承式组合）。
 *
 * <p><b>定位</b>：文本输入的「可清除」变体。{@link InputAnt} 继承自 {@link javafx.scene.control.TextField}
 * 无法自带兄弟按钮，因此本组件按项目既定的「继承式组合」范式提供：
 * 自身 {@code extends HBoxAnt} 承载布局能力，内部放一个 {@link InputAnt} + 一个清除按钮。</p>
 *
 * <h2>为什么不是 InputAnt 的一个 flag</h2>
 * <p>{@code InputAnt extends TextField} 是单一节点，没有可以挂清除按钮的父容器；
 * 硬塞只能改 {@code InputAnt} 的继承关系（破坏既有 {@code extends InputAnt} 的业务子类），
 * 或让 {@code size()} 之类的方法返回类型撒谎——两者都违反项目红线。</p>
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
public class SearchInputAnt extends AbstractHBoxAnt<SearchInputAnt> {

    private final InputAnt field = InputAnt.create();
    private final HBox clearBtn = new HBox();
    private boolean allowClear = true;
    private Consumer<String> onChange = null;
    private Consumer<String> onSearch = null;

    private SearchInputAnt() {
        super();
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add(JfxStyles.SEARCH_INPUT);

        field.getStyleClass().add(JfxStyles.SEARCH_INPUT_FIELD);
        HBox.setHgrow(field, Priority.ALWAYS);

        // 清除按钮：结构与图标由 IconAnt 收口，视觉走 LESS（.jfx-search-input-clear）
        clearBtn.setAlignment(Pos.CENTER);
        clearBtn.getStyleClass().add(JfxStyles.SEARCH_INPUT_CLEAR);
        clearBtn.getChildren().add(IconAnt.symbol(IconAnt.Symbol.CLOSE, 12));
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
