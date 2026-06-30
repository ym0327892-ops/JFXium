package org.openkawu.jfxium.component.overlay;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationAnt 单元测试 —— 覆盖静态便捷方法签名 / Builder 链式 / show() no-op
 * / 枚举完整性 / 关键 M19.51 修复点(closable=true 时 X 按钮渲染)。
 *
 * <p>overlay 组件依赖 Stage/Popup 做实际渲染,本测试不弹窗端到端跑(那是 acceptance 测试范畴),
 * 只验证 Builder 字段值配置 + show() 在无 Showing Stage 时静默 no-op + 静态便捷方法重载签名完整。</p>
 *
 * <p>修复点对应测试:<br>
 * 1. {@link NotificationAnt.Builder#closable(boolean)} 必须传透到 NotificationCard<br>
 * 2. {@link org.openkawu.jfxium.component.base.NotificationCard} closable=true 必渲染 X 按钮<br>
 * 由 NotificationCardTest 锁死修复点 L117,本测试侧重 Builder 链式与重载覆盖。</p>
 */
@DisplayName("NotificationAnt")
class NotificationAntTest extends JfxTestBase {

    // ============================================================
    // 反射工具：读 Builder private 字段验证 Builder 默认值
    // ============================================================

    private static Object builderField(NotificationAnt.Builder b, String name) throws Exception {
        Field f = NotificationAnt.Builder.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(b);
    }

    // ============================================================
    // 1. create() / build() 基础
    // ============================================================

    @Test
    @DisplayName("create() 返回非空 Builder")
    void create_returnsBuilder() {
        assertNotNull(NotificationAnt.create());
    }

    @Test
    @DisplayName("create().build() 返回非空 NotificationResult")
    void build_returnsResult() {
        NotificationAnt.NotificationResult result = NotificationAnt.create().build();
        assertNotNull(result);
    }

    // ============================================================
    // 2. 默认值（反射读 private 字段，锁死语义）
    // ============================================================

    @Test
    @DisplayName("Builder 默认值:type=INFO / placement=TOP_RIGHT / duration=4 / closable=true")
    void builder_defaults() throws Exception {
        NotificationAnt.Builder b = NotificationAnt.create();
        assertEquals(NotificationAnt.Type.INFO, builderField(b, "type"));
        assertEquals(NotificationAnt.Placement.TOP_RIGHT, builderField(b, "placement"));
        assertEquals(4, builderField(b, "durationSeconds"));
        assertEquals(true, builderField(b, "closable"));
        assertEquals("", builderField(b, "title"));
        assertEquals("", builderField(b, "description"));
    }

    // ============================================================
    // 3. 全链式串联
    // ============================================================

    @Test
    @DisplayName("全链式串联:title/description/type/placement/duration/closable/onClose/onClick/content 不抛")
    void fullChain_noException() {
        Node content = new Label("c");                 // 测试代码允许 new(沿用 PopconfirmAntTest L76 惯例)
        NotificationAnt.NotificationResult result = NotificationAnt.create()
                .title("标题")
                .description("描述")
                .type(NotificationAnt.Type.WARNING)
                .placement(NotificationAnt.Placement.BOTTOM_LEFT)
                .duration(0)                          // 永久停留
                .closable(true)
                .onClose(v -> { /* user closed */ })
                .onClick(v -> { /* user clicked */ })
                .content(content)
                .build();
        assertNotNull(result);
    }

    // ============================================================
    // 4. show() 在无 Stage 环境静默 no-op
    // ============================================================

    @Test
    @DisplayName("show() 在无 Showing Stage 时静默 no-op")
    void show_noStage_silentNoop() {
        NotificationAnt.NotificationResult result = NotificationAnt.create()
                .title("t")
                .build();
        // Platform.runLater 异步,runOnFxThreadAndWait 让任务在 FX 线程上跑完
        // Window.getWindows() 拿不到 Showing Stage → show() 内 window==null 静默 return
        runOnFxThreadAndWait(result::show);
    }

    @Test
    @DisplayName("4 个静态便捷方法 success/error/warning/info 不抛")
    void staticConvenience_allTypes() {
        runOnFxThreadAndWait(() -> {
            NotificationAnt.success("t", "d");
            NotificationAnt.error("t", "d");
            NotificationAnt.warning("t", "d");
            NotificationAnt.info("t", "d");
        });
    }

    @Test
    @DisplayName("duration 重载:success/error/warning/info 各支持自定义秒数")
    void staticConvenience_durationOverload() {
        runOnFxThreadAndWait(() -> {
            NotificationAnt.success("t", "d", 0);     // 永久停留
            NotificationAnt.error("t", "d", 10);
            NotificationAnt.warning("t", "d", 5);
            NotificationAnt.info("t", "d", 3);
        });
    }

    @Test
    @DisplayName("placement 重载:success/error/warning/info 各支持 4 个 Placement")
    void staticConvenience_placementOverload() {
        runOnFxThreadAndWait(() -> {
            for (NotificationAnt.Placement p : NotificationAnt.Placement.values()) {
                NotificationAnt.success("t", "d", p);
                NotificationAnt.error("t", "d", p);
                NotificationAnt.warning("t", "d", p);
                NotificationAnt.info("t", "d", p);
            }
        });
    }

    @Test
    @DisplayName("duration=0 + closable=true 永久停留(必有点 X 关闭)")
    void permanent_noAutoHide() {
        // 这是 M19.51 修复后的核心用法场景
        runOnFxThreadAndWait(() -> {
            NotificationAnt.create()
                    .title("危险操作确认")
                    .description("该操作不可撤销")
                    .type(NotificationAnt.Type.ERROR)
                    .duration(0)                      // 不自动消失
                    .closable(true)                   // 渲染 X 按钮
                    .build()
                    .show();
        });
    }

    @Test
    @DisplayName("closable=false 链式 + show() 不抛(不画 X,但仍走 show 路径)")
    void closableFalse_showNoop() {
        runOnFxThreadAndWait(() -> {
            NotificationAnt.create()
                    .title("t")
                    .closable(false)
                    .build()
                    .show();
        });
    }

    @Test
    @DisplayName("content(null) / onClose(null) / onClick(null) 都不抛")
    void nullParams_safe() {
        NotificationAnt.NotificationResult result = NotificationAnt.create()
                .title("t")
                .content(null)
                .onClose(null)
                .onClick(null)
                .build();
        assertNotNull(result);
    }

    // ============================================================
    // 5. 枚举完整性（防退化）
    // ============================================================

    @Test
    @DisplayName("Type 枚举齐全:SUCCESS / ERROR / WARNING / INFO")
    void typeEnum_complete() {
        assertEquals(4, NotificationAnt.Type.values().length);
        List<NotificationAnt.Type> expected = new ArrayList<>(Arrays.asList(
                NotificationAnt.Type.SUCCESS,
                NotificationAnt.Type.ERROR,
                NotificationAnt.Type.WARNING,
                NotificationAnt.Type.INFO));
        assertTrue(expected.containsAll(Arrays.asList(NotificationAnt.Type.values())));
    }

    @Test
    @DisplayName("Placement 枚举齐全:4 个角(TOP_LEFT/TOP_RIGHT/BOTTOM_LEFT/BOTTOM_RIGHT)")
    void placementEnum_complete() {
        assertEquals(4, NotificationAnt.Placement.values().length);
        List<NotificationAnt.Placement> expected = new ArrayList<>(Arrays.asList(
                NotificationAnt.Placement.TOP_LEFT,
                NotificationAnt.Placement.TOP_RIGHT,
                NotificationAnt.Placement.BOTTOM_LEFT,
                NotificationAnt.Placement.BOTTOM_RIGHT));
        assertTrue(expected.containsAll(Arrays.asList(NotificationAnt.Placement.values())));
    }

    // ============================================================
    // 6. NotificationResult.show() 等价于 NotificationAnt.show(builder) 路径
    // ============================================================

    @Test
    @DisplayName("NotificationResult.show() 调用不抛")
    void resultShow_noException() {
        NotificationAnt.NotificationResult result = NotificationAnt.create()
                .title("t")
                .build();
        runOnFxThreadAndWait(result::show);
    }

    @Test
    @DisplayName("反复 show() / 静态便捷反复调用 不抛(无 Stage 幂等)")
    void staticConvenience_repeatedSafe() {
        runOnFxThreadAndWait(() -> {
            for (int i = 0; i < 5; i++) {
                NotificationAnt.success("t", "d");
                NotificationAnt.error("t", "d", 0);
                NotificationAnt.warning("t", "d", NotificationAnt.Placement.TOP_LEFT);
            }
        });
    }

    // ============================================================
    // 7. Bug 3 修复点(M19.51 同族)回归锁死 —— box click handler 不再 hide
    // ============================================================
    //
    // 修复前 Bug 3:NotificationAnt.L421-428 在 box 的 setOnMouseClicked 末尾
    // 调用 hide(entry, config),导致:
    //   ① 点 body 任何位置都会关掉通知;
    //   ② 点 X 时事件 bubble-up 重复触发 hide;
    //   ③ content(Hyperlink) 等自定义节点被截胡,根本无法点击。
    //
    // 修复后:box click handler 只触发 onClick,不再 hide。
    // 由于 NotificationAnt.show() 依赖真实 Showing Stage 才能跑出 Popup,
    // 单元测试环境下 Window.getWindows() 为空 → show 走 no-op 路径,
    // 无法用 fireEvent 触发真实 click。所以改用源码层面锁死:在
    // setOnMouseClicked 出现的位置起,后续代码块不能再次出现 hide() 调用。

    @Test
    @DisplayName("Bug 3 修复:box click handler 不再调 hide(entry, config) —— 源码锁死回归")
    void bug3_noHideInBoxClickHandler() throws Exception {
        // 通过 .class 反查 .java 源码路径(测试期 class 已编译,源码在 src/main/java)
        java.net.URL classUrl = NotificationAnt.class.getResource("NotificationAnt.class");
        assertNotNull(classUrl, "NotificationAnt.class 必须在 classpath 中");
        // class 在 target/classes/.../NotificationAnt.class,源码在 src/main/java/.../NotificationAnt.java
        String classPath = classUrl.getPath();
        // /.../target/classes/org/openkawu/jfxium/component/overlay/NotificationAnt.class
        // → /.../src/main/java/org/openkawu/jfxium/component/overlay/NotificationAnt.java
        String srcPath = classPath.replace("/target/classes/", "/src/main/java/").replace(".class", ".java");
        // 处理 Windows 分隔符(本项目 macOS,但防御性写一下)
        srcPath = srcPath.replace("\\", "/");

        String src = Files.readString(Path.of(srcPath));
        int markerIdx = src.indexOf("notificationBox.setOnMouseClicked");
        assertTrue(markerIdx > 0,
                "源码必须包含 notificationBox.setOnMouseClicked 调用 —— 锁定 Bug 3 修复锚点");

        // 圈定 setOnMouseClicked 调用块:从 setOnMouseClicked 起到第一个 `);` 止。
        // 锁住单行简洁写法(避免 lambda 内调 hide);如果改回多行版,需同步调整本测试。
        int endIdx = src.indexOf(");", markerIdx);
        assertTrue(endIdx > markerIdx, "setOnMouseClicked 必须以 `);` 收尾");
        String lambdaBody = src.substring(markerIdx, endIdx + 2);
        assertFalse(lambdaBody.contains("hide("),
                "Bug 3 修复后:box click handler 内禁止再调 hide();"
                        + " 点 body 必须不关闭通知(只触发 onClick)。"
                        + " 找到的块片段:\n" + lambdaBody);
    }

    @Test
    @DisplayName("Bug 3 修复:NotificationAnt 顶部 JavaDoc 明确『点 body 不关闭』约定")
    void bug3_javadocDocumentsCloseSemantics() throws Exception {
        java.net.URL classUrl = NotificationAnt.class.getResource("NotificationAnt.class");
        assertNotNull(classUrl, "NotificationAnt.class 必须在 classpath 中");
        String classPath = classUrl.getPath();
        String srcPath = classPath.replace("/target/classes/", "/src/main/java/").replace(".class", ".java");
        srcPath = srcPath.replace("\\", "/");

        String src = Files.readString(Path.of(srcPath));
        // JavaDoc 必须说明 body click 不关闭 —— 这是新约定
        assertTrue(src.contains("点 body（非 X）") || src.contains("点 body(非 X)")
                        || src.contains("body 不关闭"),
                "JavaDoc 必须明确『点 body 不关闭』约定,防止后续误改回 hide();"
                        + " 当前源码未包含该说明");
    }

    @Test
    @DisplayName("Bug 3 修复:onClick=null 时 box click handler 根本不被注册(closable && onClick 双条件)")
    void bug3_onClickNull_noBoxClickHandler() throws Exception {
        // 源码层面:注册 box click 的条件必须同时含 closable && onClick != null
        // —— 否则绑一个啥也不做的 handler 是浪费
        java.net.URL classUrl = NotificationAnt.class.getResource("NotificationAnt.class");
        assertNotNull(classUrl, "NotificationAnt.class 必须在 classpath 中");
        String classPath = classUrl.getPath();
        String srcPath = classPath.replace("/target/classes/", "/src/main/java/").replace(".class", ".java");
        srcPath = srcPath.replace("\\", "/");

        String src = Files.readString(Path.of(srcPath));
        int markerIdx = src.indexOf("notificationBox.setOnMouseClicked");
        assertTrue(markerIdx > 0);

        // 上看 200 字符,锁定 if 条件行
        int lookbackStart = Math.max(0, markerIdx - 200);
        String ifLine = src.substring(lookbackStart, markerIdx);
        assertTrue(ifLine.contains("config.onClick != null"),
                "setOnMouseClicked 的注册条件必须包含 onClick != null,"
                        + " 避免 onClick=null 时绑空 handler;当前 if 条件片段:\n" + ifLine);
    }
}
