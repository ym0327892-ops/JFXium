# Requirements Document

## Introduction

为 JFXium 主框架（`jfxium/`，不含 `jfxium-demo/`）建立 i18n 国际化基础设施。当前框架内大量面向最终用户的字符串（按钮文案、占位符、空状态提示、错误信息等）以硬编码形式散落在 *Ant 组件和 *Template 模板中，导致无法切换语言、用户体验割裂（部分中文部分英文）。

本特性引入一套零依赖、基于 JDK `ResourceBundle` 的轻量 i18n 框架：

- **默认语言**：简体中文（`Locale.SIMPLIFIED_CHINESE`），符合项目主流用户群体
- **回退语言**：英文（`messages.properties` 与 `messages_en.properties`）
- **API 形态**：静态方法 `Messages.get(key)` / `Messages.get(key, args)` / `Messages.setLocale(Locale)`
- **运行时切换**：`Messages.setLocale()` 立即生效（通过 JavaFX `StringProperty` / 重渲染策略）
- **强约束写入 SKILL**：「面向用户字符串走 `Messages.get()`，禁止硬编码」

不引入第三方 i18n 库（保持项目 0 依赖原则）。

## Glossary

- **JFXium**：本项目，仿 Ant Design 风格的 JavaFX UI 组件库
- **\*Ant 组件**：JFXium 的原子控件，命名后缀为 `Ant`（如 `ButtonAnt`、`CodeBlockAnt`）
- **\*Template 模板**：JFXium 的业务模板，命名后缀为 `Template`（如 `LoginTemplate`、`CrudTemplate`、`DashboardTemplate`）
- **Messages**：本特性新增的 i18n 入口类，路径 `org.openkawu.jfxium.core.i18n.Messages`
- **i18n key**：i18n 字符串键，命名约定 `组件名.元素名`（如 `codeblock.copy`、`modal.ok`）
- **ResourceBundle**：JDK 自带的 i18n 资源加载机制（`java.util.ResourceBundle`）
- **fallback bundle**：`messages.properties`（无 locale 后缀），作为找不到对应 locale 时的兜底
- **运行时硬编码字符串**：在 `*.java` 源码中以字符串字面量形式出现、并最终被渲染到 UI 上（`setText` / `setPromptText` / `new Label(...)` 等）的字符串。**不包括**：javadoc 注释、Java 注释、内部异常消息（仅开发者可见）、styleClass 名称、CSS 变量名
- **showcase**：`jfxium-demo` 中的组件展示模块（`org.openkawu.jfxium.demo.showcase`）
- **I18nPage**：本特性新增的 showcase 演示页，演示运行时切换 locale 的效果

## Requirements

### Requirement 1: i18n 基础设施

**User Story:** 作为 JFXium 框架开发者，我希望有一个统一的 `Messages` 入口类，能够按 key 取出对应当前 Locale 的字符串，以便所有 *Ant 组件和 *Template 模板复用同一套 i18n 机制。

#### Acceptance Criteria

1. THE Messages SHALL 提供静态方法 `get(String key)`，返回当前 Locale 对应的字符串
2. THE Messages SHALL 提供静态方法 `get(String key, Object... args)`，使用 `MessageFormat.format` 进行参数化替换并返回结果字符串
3. THE Messages SHALL 提供静态方法 `setLocale(Locale locale)`，用于切换当前 Locale
4. THE Messages SHALL 提供静态方法 `getLocale()`，返回当前 Locale
5. WHEN Messages 类被首次加载时，THE Messages SHALL 将默认 Locale 初始化为 `Locale.SIMPLIFIED_CHINESE`
6. WHEN `Messages.get(key)` 被调用且 key 在当前 Locale 的 ResourceBundle 中存在，THE Messages SHALL 返回 ResourceBundle 中对应的字符串
7. IF `Messages.get(key)` 被调用且 key 在当前 Locale 的 ResourceBundle 中不存在，THEN THE Messages SHALL 回退到 `messages.properties`（无 locale 后缀的 fallback bundle）
8. IF `Messages.get(key)` 被调用且 key 在所有 ResourceBundle 中均不存在，THEN THE Messages SHALL 返回 key 本身（占位字符串），并通过 JDK Logger 在 WARNING 级别记录一次缺失日志
9. THE Messages SHALL 从 `org/openkawu/jfxium/i18n/messages` 这个 base name 加载 ResourceBundle
10. WHEN `Messages.setLocale(locale)` 被调用且传入的 locale 非 null，THE Messages SHALL 更新当前 Locale 并清空 ResourceBundle 缓存（使后续 get 调用重新加载对应 locale 的资源）

### Requirement 2: 资源文件提供

**User Story:** 作为 JFXium 框架开发者，我希望默认中文与英文翻译以 properties 文件形式集中管理，以便后续维护和扩展其他语言。

#### Acceptance Criteria

1. THE jfxium 模块 SHALL 在 `src/main/resources/org/openkawu/jfxium/i18n/messages.properties` 提供 fallback 资源文件，内容与 `messages_zh_CN.properties` 完全一致
2. THE jfxium 模块 SHALL 在 `src/main/resources/org/openkawu/jfxium/i18n/messages_zh_CN.properties` 提供简体中文资源文件
3. THE jfxium 模块 SHALL 在 `src/main/resources/org/openkawu/jfxium/i18n/messages_en.properties` 提供英文资源文件
4. THE 三份 resource 文件 SHALL 包含完全一致的 key 集合（不允许某个 key 在 zh_CN 存在而在 en 缺失）
5. THE properties 文件 SHALL 使用 UTF-8 编码（依赖 JDK 9+ 的 ResourceBundle 默认 UTF-8 行为）
6. THE properties 文件 SHALL 按组件分组放置，每组前以 `#` 注释标注组件名（如 `# CodeBlockAnt`）
7. THE i18n key 命名 SHALL 遵循 `组件名小写.元素名` 约定（如 `codeblock.copy`、`treeselect.placeholder`、`modal.ok`、`empty.description`、`pagination.prev`）

### Requirement 3: 运行时硬编码字符串迁移

**User Story:** 作为最终用户，我希望 JFXium 内置组件的所有面向我的文案（按钮、占位符、提示等）都能跟随当前 Locale 显示对应语言，而不是混杂中英文。

#### Acceptance Criteria

1. WHEN jfxium 主框架（`jfxium/src/main/java`）被全量 grep 扫描后，THE 框架 SHALL 不再包含任何运行时硬编码的、面向最终用户的中文或英文字符串字面量
2. THE CodeBlockAnt SHALL 通过 `Messages.get("codeblock.copy")` 取得复制按钮文案
3. THE CodeBlockAnt SHALL 通过 `Messages.get("codeblock.copied")` 取得"已复制"反馈文案
4. THE TreeSelectAnt SHALL 将 placeholder 默认值改为通过 `Messages.get("treeselect.placeholder")` 取得（仍允许调用方通过 `.placeholder(String)` 覆盖）
5. THE EmptyAnt SHALL 将 description 默认值改为通过 `Messages.get("empty.description")` 取得（仍允许调用方覆盖）
6. THE ModalAnt SHALL 将 `okText` / `cancelText` 默认值改为通过 `Messages.get("modal.ok")` / `Messages.get("modal.cancel")` 取得
7. THE PopconfirmAnt SHALL 将 `okText` / `cancelText` 默认值改为通过 `Messages.get("popconfirm.ok")` / `Messages.get("popconfirm.cancel")` 取得
8. THE UploadAnt SHALL 将 `buttonText` / `dragText` / `hintText` / "Error" 标签默认值改为通过 `Messages.get("upload.*")` 取得
9. THE TransferAnt SHALL 将 `titles` 默认值与 search 框 promptText 改为通过 `Messages.get("transfer.*")` 取得
10. THE LoginTemplate SHALL 将所有 banner / 表单文案（brandName / tagline / copyright / formTitle / formSubtitle / usernamePlaceholder / passwordPlaceholder / submitText / 错误前缀 / "还没账号？" / "立即注册"）默认值改为通过 `Messages.get("login.*")` 取得
11. THE DashboardTemplate SHALL 将"较上周"等内置文案改为通过 `Messages.get("dashboard.*")` 取得
12. WHERE 组件 Builder 已暴露字符串 setter（如 `placeholder(String)` / `okText(String)` / `description(String)`），THE 框架 SHALL 保留这些 setter 供调用方覆盖默认值
13. WHEN 调用方未显式调用字符串 setter，THE 组件 SHALL 使用 `Messages.get(...)` 返回的当前 Locale 文案作为默认值
14. IF 一段字符串只在内部异常消息或 javadoc 中出现且不会被渲染到 UI，THEN THE 框架 MAY 保持其原状不迁移到 i18n（迁移成本与收益不匹配）
15. THE 资源 bundle SHALL 同时提供 zh_CN 与 en 两种语言下、所有上述 key 的翻译

### Requirement 4: Locale 运行时切换

**User Story:** 作为应用集成方，我希望能在程序运行时切换 JFXium 的 Locale，UI 中已经显示的内置文案随之更新到新语言。

#### Acceptance Criteria

1. THE Messages SHALL 暴露一个 JavaFX `ReadOnlyObjectProperty<Locale>` 类型的 `localeProperty()`，反映当前 Locale
2. WHEN `Messages.setLocale(newLocale)` 被调用且 `newLocale` 与当前 Locale 不同，THE localeProperty SHALL 触发一次值变更通知
3. WHEN `Messages.setLocale(newLocale)` 被调用且 `newLocale` 与当前 Locale 相同，THE localeProperty SHALL NOT 触发值变更通知（避免无意义重渲染）
4. WHERE 组件需要随 Locale 切换自动更新文案，THE 组件 SHALL 通过监听 `Messages.localeProperty()` 在 listener 中重新调用 `Messages.get(key)` 刷新展示
5. THE Messages 的 setLocale 方法 SHALL 是线程安全的（写操作通过 JavaFX `Platform.runLater` 或加锁保证 localeProperty 在 JavaFX Application Thread 上变更）
6. IF `Messages.setLocale(null)` 被调用，THEN THE Messages SHALL 抛出 `NullPointerException`，并在异常消息中说明 locale 不能为 null

### Requirement 5: Showcase I18n 演示页

**User Story:** 作为 JFXium 评估者，我希望能在 showcase 中看到 i18n 切换的实际效果，以便理解这套机制如何工作、如何在我自己的项目中复用。

#### Acceptance Criteria

1. THE showcase SHALL 包含一个 `I18nPage`（位于 `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/pages/I18nPage.java`）
2. THE I18nPage SHALL 实现 `ShowcasePage` 接口
3. THE I18nPage SHALL 在 ShowcaseDemo 中被注册（在 ShowcaseDemo.start 的注册块中加 `frame.register(new I18nPage())`）
4. THE I18nPage SHALL 在页面顶部提供两个按钮（或一个 Segmented），分别为「中文 / English」
5. WHEN 用户点击「中文」按钮，THE I18nPage SHALL 调用 `Messages.setLocale(Locale.SIMPLIFIED_CHINESE)`
6. WHEN 用户点击「English」按钮，THE I18nPage SHALL 调用 `Messages.setLocale(Locale.ENGLISH)`
7. THE I18nPage SHALL 在同一页面上展示至少 4 个被 i18n 化的组件（CodeBlockAnt / TreeSelectAnt / EmptyAnt / ModalAnt 触发按钮）的实例，使切换 locale 时用户能直观看到文案变化
8. WHEN 用户切换 locale，THE I18nPage 中已展示组件的内置文案 SHALL 在不重新构建页面的前提下更新到新语言
9. THE I18nPage 的 ShowcasePage.category SHALL 返回 `Category.OTHER`（与 WatermarkPage 同分类）

### Requirement 6: 文档与 SKILL 同步

**User Story:** 作为 JFXium 后续维护者，我希望"面向用户字符串走 `Messages.get()`，禁止硬编码"作为强约束写入 SKILL，以便后续新增组件时自动遵守。

#### Acceptance Criteria

1. THE README.md SHALL 在「组件分类」或独立章节中说明 i18n 机制（默认 zh_CN、如何切换 Locale、如何扩展自定义 key）
2. THE README_CN.md SHALL 在对应章节同步增加 i18n 介绍
3. THE PLAN.md SHALL 在最新 milestone 章节追加一条记录，描述本特性的产出（新增 Messages 类、3 份 properties 文件、迁移的组件清单、新增 I18nPage）
4. THE SKILL.md（`.kiro/steering/项目约束与计划/SKILL.md`）SHALL 在「七、强制约束」中追加一条："面向最终用户的字符串必须走 `Messages.get(key)`，禁止在 *.java 源码中硬编码 UI 文案"
5. THE 组件组合规范 SKILL.md SHALL 在「四、反模式 / 红线」追加一条反模式："组件中硬编码 UI 文案"，并给出错误/正确示例
6. THE 文档更新 SHALL 给出 Messages 的最小使用示例（`Button btn = new Button(Messages.get("codeblock.copy"));`）

### Requirement 7: 构建与验证

**User Story:** 作为协作者，我希望本特性的所有产物能通过项目既定的编译命令验证通过，避免引入回归。

#### Acceptance Criteria

1. WHEN 执行 `mvn install -pl jfxium -DskipTests -q && mvn compile -pl jfxium-demo -q`，THE 命令 SHALL 以 exit code 0 完成
2. WHEN 执行 `mvn javafx:run -pl jfxium-demo -q`，THE 应用 SHALL 正常启动，且 ShowcaseDemo 主窗口可见、左侧菜单包含 I18nPage 入口
3. THE 本特性 SHALL NOT 引入除 JDK 自带 `java.util.ResourceBundle` / `java.text.MessageFormat` / `java.util.Locale` 之外的第三方 i18n 库依赖
4. THE 本特性 SHALL NOT 修改 `jfxium-demo/` 下除 `showcase/pages/I18nPage.java` 与 `showcase/ShowcaseDemo.java`（注册行）之外的任何文件
5. THE 资源文件 SHALL 通过单元/整合校验：测试或启动时遍历 zh_CN bundle 的全部 key，确认 en bundle 与 fallback bundle 也包含同名 key（无缺失）
