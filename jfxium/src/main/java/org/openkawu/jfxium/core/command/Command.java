package org.openkawu.jfxium.core.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 命令模式接口（对标 WPF ICommand）—— 封装「操作 + 是否可执行」的统一抽象。
 *
 * <p>典型用法：绑定到 ButtonAnt，按钮自动根据 canExecute 状态 disable/enable。</p>
 *
 * <pre>{@code
 * Command saveCmd = Command.of(
 *     () -> service.save(model),
 *     form.validProperty()    // 表单有效时才能提交
 * );
 *
 * ButtonAnt.create("保存").command(saveCmd).build();
 * // 按钮自动 disable 当 form.validProperty() == false
 * }</pre>
 */
public interface Command {

    /** 执行命令。 */
    void execute();

    /**
     * 是否可执行（true = 可执行，false = 禁用）。
     * 绑定到按钮的 disableProperty（取反）。
     */
    BooleanProperty canExecuteProperty();

    /** 便捷：当前是否可执行。 */
    default boolean canExecute() {
        return canExecuteProperty().get();
    }

    /**
     * 快速创建：永远可执行的命令。
     */
    static Command of(Runnable action) {
        return of(action, new SimpleBooleanProperty(true));
    }

    /**
     * 快速创建：可执行性由外部 BooleanProperty 控制。
     *
     * @param action 执行逻辑
     * @param canExecute true 时可执行，false 时按钮自动 disable
     */
    static Command of(Runnable action, BooleanProperty canExecute) {
        return new Command() {
            @Override
            public void execute() {
                if (canExecute.get()) {
                    action.run();
                }
            }

            @Override
            public BooleanProperty canExecuteProperty() {
                return canExecute;
            }
        };
    }
}
