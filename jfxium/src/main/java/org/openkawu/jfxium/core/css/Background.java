package org.openkawu.jfxium.core.css;

/**
 * 通用背景层级枚举（M19.35 引入）。
 *
 * <p>用于容器组件的背景色标记——业务用枚举挑层级，框架挂对应的 styleClass，
 * 视觉规则全部走 LESS（{@code .jfx-bg-*} 选择器）。</p>
 *
 * <h2>5 个层级</h2>
 * <table border="1">
 *   <caption>背景层级对照</caption>
 *   <tr><th>层级</th><th>LESS 变量</th><th>用途</th></tr>
 *   <tr><td>{@link #DEFAULT}</td><td>-color-bg-default</td>
 *       <td>主内容（白）—— 卡片内容、表单、表格行</td></tr>
 *   <tr><td>{@link #SUBTLE}</td><td>-color-bg-subtle</td>
 *       <td>次要区（浅灰）—— 表头、Sider、Card 头、内嵌强调块</td></tr>
 *   <tr><td>{@link #LAYOUT}</td><td>-color-bg-layout</td>
 *       <td>页面外缘（灰）—— AppShell 外围、ScrollContainer 背景</td></tr>
 *   <tr><td>{@link #INSET}</td><td>-color-bg-inset</td>
 *       <td>凹陷区（深灰）—— 代码块、嵌入预览区</td></tr>
 *   <tr><td>{@link #TRANSPARENT}</td><td>transparent</td>
 *       <td>透明（让父容器决定）</td></tr>
 * </table>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // admin 行业惯例：sider 灰 / content 白
 * AppShellAnt.create()
 *     .sider(menu).siderBackground(Background.SUBTLE)
 *     .content(body).contentBackground(Background.DEFAULT)
 *     .build();
 *
 * // PageTemplate 整体放在灰底之上
 * PageTemplate.create()
 *     .title("...")
 *     .background(Background.LAYOUT)
 *     .body(content)
 *     .build();
 * }</pre>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li><b>不超过 3 层色阶</b>：单页同时使用 ≤ 3 种 Background，避免视觉嘈杂</li>
 *   <li><b>容器选 LAYOUT/SUBTLE，内容选 DEFAULT</b>：体现"容器 vs 内容"的层级</li>
 *   <li><b>主题切换跟随</b>：每个值绑定 LESS 变量，dark 模式自动适配</li>
 * </ul>
 */
public enum Background {

    /** 主内容白底（{@code -color-bg-default}）。 */
    DEFAULT(JfxStyles.BG_DEFAULT),

    /** 次要区浅灰（{@code -color-bg-subtle}）—— 表头 / Sider / Card 头部。 */
    SUBTLE(JfxStyles.BG_SUBTLE),

    /** 页面外缘灰（{@code -color-bg-layout}）—— AppShell 外围。 */
    LAYOUT(JfxStyles.BG_LAYOUT),

    /** 凹陷深灰（{@code -color-bg-inset}）—— 代码块 / 嵌入预览。 */
    INSET(JfxStyles.BG_INSET),

    /** 透明（让父容器决定背景）。 */
    TRANSPARENT(JfxStyles.BG_TRANSPARENT);

    private final String styleClass;

    Background(String styleClass) {
        this.styleClass = styleClass;
    }

    /** 此层级对应的 styleClass 名（例如 {@code "jfx-bg-subtle"}）。 */
    public String styleClass() {
        return styleClass;
    }
}
