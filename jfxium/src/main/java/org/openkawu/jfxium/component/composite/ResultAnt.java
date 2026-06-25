package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.base.ResultDisplay;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 结果页组件 - 对标 Ant Design Result。
 *
 * <p><b>定位</b>：操作结果反馈页，包含状态图标 + 标题 + 副标题 + 可选操作按钮，
 * 常用于表单提交后、支付完成、404/403/500 等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>状态</b>：SUCCESS / ERROR / INFO / WARNING / NOT_FOUND / FORBIDDEN / INTERNAL_ERROR</li>
 *   <li><b>标题 + 副标题</b>：双行文案</li>
 *   <li><b>操作区</b>：extra(Node) 或快捷方法 extraButton(text, action)</li>
 *   <li><b>静态快捷</b>：success() / error() / info() / warning()</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 提交成功页
 * VBox result = ResultAnt.success("提交成功", "订单已发送至仓库")
 *     .extraButton("返回订单列表", () -> navigateToOrders())
 *     .build();
 *
 * // 404 页
 * VBox notFound = ResultAnt.create()
 *     .status(ResultAnt.Status.NOT_FOUND)
 *     .title("404")
 *     .subTitle("抱歉，您访问的页面不存在")
 *     .extraButton("返回首页", () -> navigateHome())
 *     .build();
 * }</pre>
 */
public class ResultAnt {

    public enum Status {
        SUCCESS, ERROR, INFO, WARNING, NOT_FOUND, FORBIDDEN, INTERNAL_ERROR
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Status status = Status.INFO;
        private String title = "";
        private String subTitle = "";
        private Node extra = null;

        public Builder status(Status status) {
            this.status = status != null ? status : Status.INFO;
            return this;
        }

        public Builder title(String title) {
            this.title = TextUtils.safeText(title);
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.subTitle = TextUtils.safeText(subTitle);
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder extraButton(String text, Runnable action) {
            this.extra = ButtonAnt.create(text)
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(action != null ? e -> action.run() : null)
                .build();
            return this;
        }

        public VBox build() {
            VBox result = new ResultDisplay.Builder()
                .status(convertStatus(status))
                .title(title)
                .subTitle(subTitle)
                .extra(extra)
                .build();
            applyStyles(result);
            return result;
        }

        private ResultDisplay.Status convertStatus(Status status) {
            Status effectiveStatus = status != null ? status : Status.INFO;
            return switch (effectiveStatus) {
                case SUCCESS -> ResultDisplay.Status.SUCCESS;
                case ERROR -> ResultDisplay.Status.ERROR;
                case INFO -> ResultDisplay.Status.INFO;
                case WARNING -> ResultDisplay.Status.WARNING;
                case NOT_FOUND -> ResultDisplay.Status.NOT_FOUND;
                case FORBIDDEN -> ResultDisplay.Status.FORBIDDEN;
                case INTERNAL_ERROR -> ResultDisplay.Status.INTERNAL_ERROR;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder success(String title, String subTitle) {
        return new Builder().status(Status.SUCCESS).title(title).subTitle(subTitle);
    }

    public static Builder error(String title, String subTitle) {
        return new Builder().status(Status.ERROR).title(title).subTitle(subTitle);
    }

    public static Builder info(String title, String subTitle) {
        return new Builder().status(Status.INFO).title(title).subTitle(subTitle);
    }

    public static Builder warning(String title, String subTitle) {
        return new Builder().status(Status.WARNING).title(title).subTitle(subTitle);
    }
}
