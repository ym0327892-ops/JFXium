package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Message 全局消息 —— 4 种类型、3 个位置、loading 持久 + 手动关闭。
 *
 * <p>对标 Ant Design Message：操作反馈 / 自动消失 / 不打断用户当前任务。
 * 跟 {@code Notification} 的边界：Message 短而轻、无标题、自动消失；
 * Notification 重而显眼、带标题描述、可手动关闭。</p>
 */
public class MessageExamplePage extends VBoxAnt {

    public MessageExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Message 全局消息")
                .description("操作反馈类型的轻提示，3 秒后自动消失，不打断用户操作。")
                .sections(
                        typesSection(),
                        positionsSection(),
                        loadingSection(),
                        durationSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 4 种类型：success / error / warning / info。 */
    private Node typesSection() {
        Node row = Demos.row(
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS)
                        .onClick(e -> MessageAnt.success("操作成功")).build(),
                ButtonAnt.create("错误").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> MessageAnt.error("操作失败")).build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> MessageAnt.warning("注意检查输入")).build(),
                ButtonAnt.create("提示")
                        .onClick(e -> MessageAnt.info("这是一条普通提示")).build()
        );
        String code = """
                // 静态便捷方法，一行搞定
                MessageAnt.success("操作成功");
                MessageAnt.error("操作失败");
                MessageAnt.warning("注意检查输入");
                MessageAnt.info("这是一条普通提示");
                """;
        return Demos.sectionWithCode("1. 类型",
                "MessageAnt.success / error / warning / info —— 静态方法，最常用的 4 种语义。",
                code, row);
    }

    /** 2. 三种显示位置。 */
    private Node positionsSection() {
        Node row = Demos.row(
                ButtonAnt.create("顶部 TOP（默认）")
                        .onClick(e -> MessageAnt.create()
                                .content("这条消息从顶部滑入")
                                .type(MessageAnt.Type.INFO)
                                .position(MessageAnt.Position.TOP)
                                .build().show()).build(),
                ButtonAnt.create("底部 BOTTOM")
                        .onClick(e -> MessageAnt.create()
                                .content("这条消息从底部滑入")
                                .type(MessageAnt.Type.SUCCESS)
                                .position(MessageAnt.Position.BOTTOM)
                                .build().show()).build(),
                ButtonAnt.create("中间 CENTER")
                        .onClick(e -> MessageAnt.create()
                                .content("中间消息只显示一条，新消息会替换旧的")
                                .type(MessageAnt.Type.WARNING)
                                .position(MessageAnt.Position.CENTER)
                                .build().show()).build()
        );
        String code = """
                MessageAnt.create()
                        .content("这条消息从顶部滑入")
                        .type(MessageAnt.Type.INFO)
                        .position(MessageAnt.Position.TOP)      // TOP / BOTTOM / CENTER
                        .build()
                        .show();
                """;
        return Demos.sectionWithCode("2. 显示位置",
                "TOP / BOTTOM / CENTER —— 中间位置只展示一条，新消息替换旧消息。",
                code, row);
    }

    /** 3. Loading 消息：duration=0 不自动消失，需手动 close()。 */
    private Node loadingSection() {
        Node btn = ButtonAnt.create("开始加载（3 秒后自动结束）")
                .onClick(e -> {
                    // duration=0 表示不自动关闭，要业务方自己 close()
                    MessageAnt.MessageResult loading = MessageAnt.create()
                            .content("数据加载中...")
                            .type(MessageAnt.Type.LOADING)
                            .duration(0)
                            .build();
                    loading.show();

                    // 模拟 3 秒后请求完成
                    new Thread(() -> {
                        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                        javafx.application.Platform.runLater(() -> {
                            loading.close();
                            MessageAnt.success("加载完成");
                        });
                    }).start();
                }).build();
        String code = """
                // duration(0) 不自动消失，需手动 close()
                MessageAnt.MessageResult loading = MessageAnt.create()
                        .content("数据加载中...")
                        .type(MessageAnt.Type.LOADING)
                        .duration(0)
                        .build();
                loading.show();

                // 业务完成后关闭 loading，再发新消息
                loading.close();
                MessageAnt.success("加载完成");
                """;
        return Demos.sectionWithCode("3. Loading 持久消息",
                "duration(0) 不自动消失；业务完成后手动调 close() 关闭，再发新消息反馈结果。",
                code, btn);
    }

    /** 4. 自定义持续时长。 */
    private Node durationSection() {
        Node row = Demos.row(
                ButtonAnt.create("1 秒")
                        .onClick(e -> MessageAnt.create()
                                .content("1 秒后消失").type(MessageAnt.Type.INFO)
                                .duration(1).build().show()).build(),
                ButtonAnt.create("默认 3 秒")
                        .onClick(e -> MessageAnt.info("3 秒后消失")).build(),
                ButtonAnt.create("8 秒")
                        .onClick(e -> MessageAnt.create()
                                .content("8 秒后消失").type(MessageAnt.Type.INFO)
                                .duration(8).build().show()).build()
        );
        String code = """
                // 自定义持续时长（单位：秒）
                MessageAnt.create()
                        .content("1 秒后消失")
                        .type(MessageAnt.Type.INFO)
                        .duration(1)
                        .build()
                        .show();

                // duration(0) 表示不自动关闭
                """;
        return Demos.sectionWithCode("4. 持续时长",
                "duration(秒) —— 自定义停留时长，0 表示不自动关闭。",
                code, row);
    }
}
