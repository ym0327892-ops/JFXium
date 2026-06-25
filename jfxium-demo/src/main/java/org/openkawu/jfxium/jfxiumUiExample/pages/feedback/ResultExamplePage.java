package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.ResultAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Result 结果页 —— 成功 / 错误警告 / 带额外内容 / 交互演示。
 */
public class ResultExamplePage extends VBoxAnt {

    public ResultExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Result 结果页")
                .description("用于反馈一系列操作任务的处理结果。")
                .sections(
                        successSection(),
                        errorSection(),
                        extraSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node successSection() {
        Node result = ResultAnt.success("提交成功", "您的申请已提交，预计 1-3 个工作日内审核完成。")
                .build();
        String code = """
                Node result = ResultAnt.success("提交成功", "您的申请已提交，预计 1-3 个工作日内审核完成。")
                        .build();
                """;
        return Demos.sectionWithCode("1. 成功", "success() 快捷方法创建成功结果页。", code, result);
    }

    private Node errorSection() {
        Node row = Demos.column(
                ResultAnt.error("提交失败", "请检查网络连接后重试。").build(),
                ResultAnt.warning("警告", "您的账户余额不足，请及时充值。").build()
        );
        String code = """
                ResultAnt.error("提交失败", "请检查网络连接后重试。").build();
                ResultAnt.warning("警告", "您的账户余额不足，请及时充值。").build();
                """;
        return Demos.sectionWithCode("2. 错误与警告", "error() / warning() 快捷方法。", code, row);
    }

    private Node extraSection() {
        Node buttons = Demos.row(
                ButtonAnt.create("返回首页").type(ButtonAnt.Type.PRIMARY).onClick(e -> MessageAnt.success("返回首页")).build(),
                ButtonAnt.create("查看详情").onClick(e -> MessageAnt.info("查看详情")).build()
        );
        Node result = ResultAnt.create()
                .status(ResultAnt.Status.SUCCESS)
                .title("付款成功")
                .subTitle("订单号: 2024010112345，预计 2 小时内发货。")
                .extra(buttons)
                .build();
        String code = """
                Node buttons = Demos.row(
                        ButtonAnt.create("返回首页").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("查看详情").build()
                );
                Node result = ResultAnt.create()
                        .status(ResultAnt.Status.SUCCESS)
                        .title("付款成功")
                        .subTitle("订单号: 2024010112345")
                        .extra(buttons)
                        .build();
                """;
        return Demos.sectionWithCode("3. 带额外内容", "extra() 可放置操作按钮组。", code, result);
    }

    /**
     * 4. 交互演示（PlayGround —— rebuildRebuild 模式 4 维度）。
     *
     * <p>ResultAnt 没有 modify 入口（内部委托 {@code ResultDisplay.Builder}，且
     * status / title / subTitle 都在 build 期定死），故用 {@link PlayGround#rebindRebuild}。
     * 暴露 4 个维度：</p>
     * <ul>
     *   <li><b>状态</b> —— 7 档 Status 枚举（SUCCESS/ERROR/INFO/WARNING/NOT_FOUND/...）</li>
     *   <li><b>标题</b> —— 文本输入，实时更新</li>
     *   <li><b>副标题</b> —— 文本输入，实时更新</li>
     *   <li><b>操作区</b> —— off / on（on 时显示「返回首页」「查看详情」两按钮）</li>
     * </ul>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> status    = PlayGround.binder("success");
        Binder<String> title     = PlayGround.binder("提交成功");
        Binder<String> subTitle  = PlayGround.binder("您的申请已提交，预计 1-3 个工作日内审核完成。");
        Binder<String> extraOn   = PlayGround.binder("on");

        // 2. display 工厂：读 binder → 重 build
        Supplier<Node> factory = () -> {
            ResultAnt.Status s = parseStatus(status.get());
            String t = title.get() != null ? title.get() : "";
            String st = subTitle.get() != null ? subTitle.get() : "";
            ResultAnt.Builder b = ResultAnt.create()
                    .status(s)
                    .title(t)
                    .subTitle(st);
            if ("on".equals(extraOn.get())) {
                Node buttons = Demos.row(
                        ButtonAnt.create("返回首页").type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> MessageAnt.success("返回首页")).build(),
                        ButtonAnt.create("查看详情")
                                .onClick(e -> MessageAnt.info("查看详情")).build()
                );
                b.extra(buttons);
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Result 的状态 / 标题 / 副标题 / 操作区 —— 4 维度 rebuildRebuild 模式。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("状态", PlayGround.segmented(status,
                                PlayGround.entry("success",         "Success"),
                                PlayGround.entry("error",           "Error"),
                                PlayGround.entry("info",            "Info"),
                                PlayGround.entry("warning",         "Warning"),
                                PlayGround.entry("not_found",       "404"),
                                PlayGround.entry("forbidden",       "403"),
                                PlayGround.entry("internal_error",  "500"))),
                        PlayGround.row("标题",  PlayGround.textField(title, title.get(), "输入 Result 标题")),
                        PlayGround.row("副标题", PlayGround.textField(subTitle, subTitle.get(), "输入 Result 副标题")),
                        PlayGround.row("操作区", PlayGround.segmented(extraOn,
                                PlayGround.entry("off", "隐藏"),
                                PlayGround.entry("on",  "显示")))));
    }

    private static ResultAnt.Status parseStatus(String v) {
        if (v == null) return ResultAnt.Status.SUCCESS;
        return switch (v) {
            case "error"          -> ResultAnt.Status.ERROR;
            case "info"           -> ResultAnt.Status.INFO;
            case "warning"        -> ResultAnt.Status.WARNING;
            case "not_found"      -> ResultAnt.Status.NOT_FOUND;
            case "forbidden"      -> ResultAnt.Status.FORBIDDEN;
            case "internal_error" -> ResultAnt.Status.INTERNAL_ERROR;
            default               -> ResultAnt.Status.SUCCESS;
        };
    }
}
