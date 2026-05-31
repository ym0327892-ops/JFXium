# Requirements Document

## Introduction

为 JFXium 框架的 `FormAnt` 组件补足 4 项业务高频痛点的 Builder API（PLAN.md P0+ 文档反馈条目）：

- `header(Node)` —— 顶部 banner 区（重要提示 / 标题图 / 用户信息插槽）
- `footer(Node...)` —— 变长重载，支持「取消 / 重置 / 提交」多按钮 footer
- `footerAlign(Pos)` —— footer 对齐方式（默认 `Pos.CENTER_RIGHT`，业务方常需 `CENTER_LEFT` / `CENTER`）
- `section(String)` —— 长表单分段标题（如「基本信息」「联系方式」「权限设置」）

同时在 `jfxium-demo` 内新建 `FormExamplePage` 注册到「数据录入」分类，作为 FormAnt 增强能力的回归测试与文档配方参考（SKILL 第 22 条「示例项目即回归测试」）。

本特性遵循三条强约束：

1. **API 兼容**：FormAnt 现有 `footer(Node)` 单参方法、`item(...)` 多重载、`buildResult()` 等所有公共 API 必须保留。新增 API 不得改变既有调用的运行时行为。
2. **样式分层**：所有颜色 / 字号 / 边框 / padding / 分隔线走 LESS（`theme-base.less`），FormAnt.java 与 FormPage.java 内**禁止**出现 `setStyle("-fx-...")` 拼字符串（项目约束 SKILL §5.1 / 组件组合规范 §1.2 / §4.3 / §4.5 / §4.6）。
3. **Builder 返回类型契约**：FormAnt 属于「直接节点型」（项目约束 SKILL #18），`build()` 继续返回 `VBox`，`buildResult()` 继续返回 `Result`，新增 API 不改变这一契约。

本特性不引入第三方依赖，不修改 FormAnt 校验 / 联动 / 嵌套表单（M19.23）已有能力，不调整 LESS 主题色阶。

## Glossary

- **JFXium**：本项目，对标 Ant Design 风格的 JavaFX UI 组件库
- **FormAnt**：JFXium 表单组件，路径 `org.openkawu.jfxium.component.FormAnt`
- **FormAnt.Builder**：FormAnt 的链式构造器，对应 `FormAnt.create()` 返回值
- **FormAnt.Result**：FormAnt `buildResult()` 的返回包装类，含 `root` 与 `FormContext`
- **header 区**：FormAnt 渲染产物中位于全部表单项之上的可选 slot 区域
- **footer 区**：FormAnt 渲染产物中位于全部表单项之下的可选按钮区
- **section 标题**：长表单内部的分组标题节点，按声明顺序与后续 `item(...)` 调用绑定，分组其下方表单项直至下一个 `section(...)` 调用或表单结束
- **Pos**：JavaFX 的 `javafx.geometry.Pos` 枚举（含 `CENTER_LEFT` / `CENTER` / `CENTER_RIGHT` 等）
- **LESS**：本项目使用的样式预处理语言，源文件位于 `jfxium/src/main/resources/org/openkawu/jfxium/css/less/`
- **CssClasses**：JFXium 集中管理 styleClass 字符串常量的工具类，路径 `org.openkawu.jfxium.core.css.CssClasses`
- **PageRegistry**：`jfxium-demo` 路由注册表，按 `Category` 分类注册示例页（路径 `org.openkawu.jfxium.jfxiumUiExample.view.PageRegistry`）
- **DATA_ENTRY 分类**：`PageRegistry.Category.DATA_ENTRY`，菜单显示名「数据录入」
- **FormExamplePage**：本特性新增的示例页，路径 `jfxium-demo/src/main/java/org/openkawu/jfxium/jfxiumUiExample/pages/dataentry/FormExamplePage.java`
- **运行时硬编码字符串**：在 `*.java` 源码中以字符串字面量形式出现、并最终被渲染到 UI 上（`new Label(...)` / `setText(...)` / `setPromptText(...)` / Builder 默认文案等）的字符串

## Requirements

> **分层标识**：每条 Requirement 标注 [核心 API] 或 [辅助 / 配套]。
> [核心 API] = `FormAnt.Builder` 公共 API 增强 + 渲染逻辑 + LESS 样式（必须）。
> [辅助 / 配套] = Showcase 示例页 + 文档同步 + 编译验证（必须，但属于配套）。

### Requirement 1: [核心 API] header(Node) 顶部 banner 区

**User Story:** 作为业务开发者，我想在表单顶部插入一个自定义节点（如重要提示卡片、标题图、用户信息块），以便在不嵌套额外容器的前提下完成「banner + 表单 + 按钮组」的常见组合。

#### Acceptance Criteria

1. THE FormAnt.Builder SHALL 提供公共方法 `header(Node header)`，返回 `Builder` 自身以支持链式调用
2. WHEN `header(Node)` 被调用且参数非 null，THE FormAnt.Builder SHALL 记录该 header 节点用于后续 `build()` / `buildResult()` 渲染
3. WHEN `header(Node)` 未被调用或被传入 null，THE FormAnt SHALL NOT 在渲染产物中插入 header 区域（不创建任何包装容器、不占用任何垂直空间）
4. WHEN `build()` 或 `buildResult()` 在 header 非 null 时被调用，THE FormAnt SHALL 在渲染产物的最顶部插入 header 节点，位置先于全部 section / item / footer
5. THE FormAnt SHALL 为 header 包装容器挂接 styleClass `form-header`，使 LESS 端可控制 header 区域的 padding / 背景 / 分隔线
6. IF `header(Node)` 被调用多次，THEN THE FormAnt.Builder SHALL 仅保留最后一次调用传入的 header 节点（覆盖语义，与现有 `footer(Node)` 行为一致）

### Requirement 2: [核心 API] footer(Node...) 变长重载

**User Story:** 作为业务开发者，我想在 footer 区一次性放置多个按钮（如「取消 / 重置 / 提交」），无需手动拼 HBox，也不破坏现有 `footer(Node)` 单参调用方。

#### Acceptance Criteria

1. THE FormAnt.Builder SHALL 提供公共方法 `footer(Node... nodes)`，返回 `Builder` 自身以支持链式调用
2. WHEN `footer(Node...)` 被调用且数组非 null 且至少包含 1 个非 null 元素，THE FormAnt.Builder SHALL 记录这些节点用于后续 footer 区渲染；当条件不满足（数组为 null / 空 / 全部元素为 null）时，THE FormAnt.Builder SHALL NOT 记录任何 footer 节点（保持 footer 列表为先前状态）
3. WHEN `build()` 或 `buildResult()` 在 footer 含至少 1 个节点时被调用，THE FormAnt SHALL 在渲染产物的最底部插入一个 HBox 容器，按调用时的传入顺序放置全部节点
4. THE FormAnt SHALL 为该 HBox 容器挂接 styleClass `form-footer`
5. THE FormAnt SHALL 将 footer HBox 的相邻节点间距设为 `8` 像素（与 Ant Design Form 的按钮组默认间距一致）
6. IF `footer(Node...)` 被传入 null 数组或空数组，THEN THE FormAnt SHALL NOT 在渲染产物中插入 footer 区域
7. IF `footer(Node...)` 被传入的数组中包含 null 元素，THEN THE FormAnt SHALL 跳过 null 元素仅插入非 null 节点
8. WHEN 既调用了 `footer(Node)` 又调用了 `footer(Node...)`，THE FormAnt.Builder SHALL 以最后一次调用为准（覆盖语义）

### Requirement 3: [核心 API] footerAlign(Pos) 对齐配置

**User Story:** 作为业务开发者，我想配置 footer 按钮组的对齐方向（右对齐用于 admin 提交、左对齐用于向导步骤、居中用于登录确认），以匹配不同业务场景的视觉规范。

#### Acceptance Criteria

1. THE FormAnt.Builder SHALL 提供公共方法 `footerAlign(Pos align)`，返回 `Builder` 自身以支持链式调用
2. THE FormAnt.Builder SHALL 将 `footerAlign` 的默认值设为 `Pos.CENTER_RIGHT`（保留与 M19.23 之前完全一致的渲染行为）
3. WHEN `footerAlign(Pos)` 未被显式调用且 footer 区被渲染，THE FormAnt SHALL 将 footer HBox 的 alignment 设为 `Pos.CENTER_RIGHT`
4. WHEN `footerAlign(Pos)` 以非 null 参数被调用且 footer 区被渲染，THE FormAnt SHALL 将 footer HBox 的 alignment 设为传入的 Pos 值
5. IF `footerAlign(null)` 被调用，THEN THE FormAnt.Builder SHALL 保留先前的 align 值不变（防御性，避免渲染时空指针）
6. THE FormAnt SHALL 在 footer 区域为空时忽略 `footerAlign` 配置（不创建空 HBox）

### Requirement 4: [核心 API] section(String) 分段标题

**User Story:** 作为业务开发者，我想在长表单中按业务语义把字段分组（如「基本信息」「联系方式」「权限设置」），以便用户视觉上能快速定位字段所在区块。

#### Acceptance Criteria

1. THE FormAnt.Builder SHALL 提供公共方法 `section(String title)`，返回 `Builder` 自身以支持链式调用
2. WHEN `section(String)` 被调用且 title 非 null，THE FormAnt.Builder SHALL 在内部声明序列中插入一个 section 标记，记录其 title 与在表单项序列中的相对位置
3. WHEN `build()` 或 `buildResult()` 被调用，THE FormAnt SHALL 在渲染产物中按 section 标记的相对位置插入一个 section 标题节点，使其后续直到下一个 section 标记（或表单结束）的全部 item 在视觉上属于同一分组
4. THE FormAnt SHALL 为 section 标题节点挂接 styleClass `form-section-title`，使 LESS 端可控制其字号 / 字色 / 上下间距 / 分隔线
5. WHERE 表单 layout 为 `Layout.HORIZONTAL` 或 `Layout.VERTICAL`，THE FormAnt SHALL 渲染 section 标题节点
6. WHERE 表单 layout 为 `Layout.INLINE`，THE FormAnt SHALL 忽略 `section(String)` 调用（INLINE 是单行 inline 表单，分段在视觉上无意义）
7. IF `section(String)` 被以 null 或空字符串调用，THEN THE FormAnt.Builder SHALL 忽略该调用（不插入 section 标记）
8. WHEN 同一 FormAnt.Builder 中 `section(String)` 被多次调用，THE FormAnt SHALL 按调用顺序渲染对应数量的 section 标题节点
9. THE FormAnt SHALL 为相邻两个 section 之间提供视觉分隔（通过 LESS 控制 `.form-section-title` 的 `-fx-padding-top` 或上边框，**不**通过 Java 端 `setStyle` 实现）

### Requirement 5: [核心 API] 既有 API 向下兼容

**User Story:** 作为既有 FormAnt 调用方，我希望升级到本特性后，所有未使用新 API 的调用代码无需任何修改即可继续工作。

#### Acceptance Criteria

1. THE FormAnt.Builder SHALL 保留现有方法 `footer(Node)` 的方法签名与运行时行为（与 M19.23 之前的渲染产物在结构上等价）
2. THE FormAnt.Builder SHALL 保留现有方法 `item(String, Node)` / `item(String, Node, boolean)` / `item(String, Node, boolean, String)` / `item(String, Node, boolean, String, ValidateStatus)` / `item(String, Node, String)` 的方法签名与运行时行为
3. THE FormAnt.Builder SHALL 保留 `layout(Layout)` / `size(Size)` / `colon(boolean)` / `labelAlign(Align)` / `labelCol(int)` / `wrapperCol(int)` 全部既有方法
4. THE FormAnt.Builder.build() SHALL 继续返回 `VBox` 类型
5. THE FormAnt.Builder.buildResult() SHALL 继续返回 `FormAnt.Result` 类型，且 `Result.getRoot()` / `Result.context()` / `Result.validate()` / `Result.getValues()` / `Result.onChange(...)` 行为不变
6. WHEN 调用方未调用 `header(...)` / `footer(Node...)` / `footerAlign(...)` / `section(...)` 四个新 API 之一，THE FormAnt SHALL 渲染出与本特性引入前结构等价的产物（节点树拓扑、styleClass 集合、布局参数完全一致）
7. THE FormAnt 的 `Layout` / `Size` / `Align` / `ValidateStatus` 公共枚举 SHALL 不被修改、删除或重命名

### Requirement 6: [核心 API] 样式分层与命名空间

**User Story:** 作为框架维护者，我希望本特性新增的视觉样式（header 区背景、section 标题字号、footer 间距）100% 通过 LESS 控制，以便后续主题切换、暗色模式、紧凑模式自动适配。

#### Acceptance Criteria

1. THE FormAnt.java SHALL NOT 调用 `setStyle("-fx-...")` 形式的 inline 样式拼字符串（覆盖全部本特性新增代码）
2. THE FormAnt.java SHALL 通过 `Node.getStyleClass().add(...)` 为 header / section / footer 节点挂接 styleClass，挂接的常量 SHALL 来自 `CssClasses`
3. THE CssClasses SHALL 新增至少 2 个常量：`FORM_HEADER`（值 `"form-header"`） 与 `FORM_SECTION_TITLE`（值 `"form-section-title"`）
4. THE theme-base.less SHALL 在 `Form Layout Helpers` 区域追加 `.form-header` 与 `.form-section-title` 选择器规则
5. THE theme-base.less 中 `.form-header` 与 `.form-section-title` 选择器 SHALL 仅使用 `-color-*` 主题语义变量与 `@spacing-*` / `@font-size-*` 项目变量，不出现硬编码 hex 颜色或硬编码像素值（项目约束 SKILL §5.1 / §1.2）；本约束作用于 LESS 文件，FormAnt.java 与 FormExamplePage.java 中由 AC1 / Req 7 AC5 已禁止 `setStyle("-fx-...")`，不再额外约束 Java 端的 hex / 像素字面量
6. THE theme-base.less 中本特性新增的全部选择器 SHALL 以 `.form` 命名空间前缀（如 `.form-header` / `.form-section-title`）开头，避免污染全局 styleClass 命名空间

### Requirement 7: [辅助 / 配套] FormExamplePage 示例页

**User Story:** 作为 JFXium 评估者 / 业务开发者，我希望在 `jfxium-demo` 中能直接看到 FormAnt 增强后的全部能力示例，以便复制粘贴到自己的项目中。

#### Acceptance Criteria

1. THE jfxium-demo SHALL 新增类 `FormExamplePage`，路径 `jfxium-demo/src/main/java/org/openkawu/jfxium/jfxiumUiExample/pages/dataentry/FormExamplePage.java`
2. THE FormExamplePage SHALL 提供一个公共无参构造函数，使其可作为 `Supplier<Node>` 在 `PageRegistry.register(...)` 中以方法引用形式注册
3. THE MainView.registerPages() SHALL 通过 `registry.register("dataentry.form", "Form 表单", PageRegistry.Category.DATA_ENTRY, FormExamplePage::new)` 注册该页面到「数据录入」分类
4. THE FormExamplePage SHALL 在同一页面中至少演示以下 7 个 Section：
   1. 基础表单（HORIZONTAL layout，3 字段，无 header / 无 section / 单按钮 footer）
   2. 校验示例（required + minLength + custom rule，初始加载时不展示任何错误文案；仅在用户点击「校验」按钮或字段失焦后，THE FormExamplePage SHALL 触发 `result.validate()` 并由 FormAnt 渲染对应字段的错误文案）
   3. 字段联动（password / confirm 两字段，password 变化时 THE FormExamplePage SHALL 自动触发 confirm 字段的重新校验，无需用户额外点击校验按钮即可观察到联动效果；联动产生的错误文案与 Section 2 一致仅在用户主动校验后才显示）
   4. header + footer 组合（header 放置一段提示节点、footer 含三按钮右对齐）
   5. 多按钮 footer（footer 含「取消 / 重置 / 提交」三个 ButtonAnt，右对齐）
   6. section 分段（至少 3 个 section：「基本信息」「联系方式」「权限设置」，每段 2-3 字段）
   7. 三种 layout 对比（同样 2 字段，分别用 HORIZONTAL / VERTICAL / INLINE 渲染对比）
5. THE FormExamplePage SHALL NOT 在自身代码中出现 `setStyle("-fx-...")` 拼字符串调用
6. THE FormExamplePage SHALL 通过 `ButtonAnt` / `InputAnt` / `CardAnt` 等既有 *Ant 组件构建演示节点，不直接 new 原生 JavaFX `Button` / `TextField`（与既有 ExamplePage 风格保持一致）
7. WHERE FormExamplePage 内部展示的演示文案被渲染到 UI 上，THE FormExamplePage SHALL 优先复用 `Messages.get("form.*")`（若 i18n key 缺失则允许临时硬编码中文，但需在代码注释标记 TODO，待后续 i18n 化）

### Requirement 8: [辅助 / 配套] 编译与运行时验证

**User Story:** 作为协作者，我希望本特性的全部产物能通过项目既定的编译命令验证通过，避免引入回归。

#### Acceptance Criteria

1. WHEN 执行 `mvn install -pl jfxium -DskipTests -q && mvn compile -pl jfxium-demo -q`，THE 命令 SHALL 以 exit code 0 完成
2. WHEN 启动 jfxium-demo，THE MainView 左侧菜单 SHALL 在「数据录入」分类下展示「Form 表单」入口
3. WHEN 用户点击「Form 表单」菜单项，THE 内容区 SHALL 渲染 FormExamplePage 且至少前 3 个 Section 可见无异常（FormExamplePage 仅在用户点击该菜单后才被实例化与渲染，未点击前不进入场景图）
4. THE 本特性 SHALL NOT 修改 `jfxium/` 模块除以下文件之外的任何文件：
   - `src/main/java/org/openkawu/jfxium/component/FormAnt.java`
   - `src/main/java/org/openkawu/jfxium/core/css/CssClasses.java`
   - `src/main/resources/org/openkawu/jfxium/css/less/theme-base.less`
   - （可选）`src/main/resources/org/openkawu/jfxium/i18n/messages*.properties`（若新增 form.* i18n key）
5. THE 本特性 SHALL NOT 修改 `jfxium-demo/` 模块除以下文件之外的任何文件：
   - `src/main/java/org/openkawu/jfxium/jfxiumUiExample/pages/dataentry/FormExamplePage.java`（新增）
   - `src/main/java/org/openkawu/jfxium/jfxiumUiExample/view/MainView.java`（仅修改 `registerPages()` 方法体与 import 段）
6. WHEN 对修改后的 `jfxium/src/main/java/org/openkawu/jfxium/component/FormAnt.java` 执行 `grep -nE 'setStyle\("-fx-'`，THE 命令 SHALL 返回零结果
7. WHEN 对新增的 `FormExamplePage.java` 执行 `grep -nE 'setStyle\("-fx-'`，THE 命令 SHALL 返回零结果
