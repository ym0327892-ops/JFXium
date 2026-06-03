# Design Document — form-ant-enhance

> 状态：**已实现并落地**（M19.39，PLAN.md P0+ 条目）。
> 本文档为「实现实况回填」，记录 4 个新 API 在 `FormAnt.java` 的最终落地方式，供验收对照。

## Overview

为 `FormAnt.Builder` 增 4 个业务高频 API：`header(Node)` / `footer(Node...)` / `footerAlign(Pos)` / `section(String)`。
核心设计取向：**最小侵入** —— 不动既有 `item(...)` 多重载、`buildResult()`、校验/联动（M19.23）逻辑，
新能力全部以「可选 slot + 混合 entries 序列」的方式叠加，未调用新 API 时渲染产物与增强前结构等价。

## 关键设计决策

### 1. entries 混合序列（section 的落地方式）

原 FormAnt 用 `List<FormItem>` 存表单项。本次改为 `List<Object> entries`，可同时存放 `FormItem` 与
`SectionMarker`（`record SectionMarker(String title)`）。三种 layout 渲染时用 `instanceof` 分流：

- `FormItem` → 正常渲染 label + wrapper
- `SectionMarker` → 渲染 `.form-section-title` 标题节点

**为什么不用「分组列表嵌套」**：混合序列保持「声明顺序 = 渲染顺序」的直觉，section 标记天然绑定其后续 item，
无需引入 Group 容器层级，改动面最小（仅 entries 类型 + 3 个 build 方法的循环分支）。

### 2. footer 统一用 `List<Node> footerNodes`

`footer(Node)` 与 `footer(Node...)` 都先 `footerNodes.clear()` 再填充 —— 实现「覆盖语义」（多次调用以最后一次为准），
且让单参/变长两个重载共用同一渲染路径（底部一个 `HBox(8)` + `setAlignment(footerAlign)`）。null 元素跳过。

### 3. header 用 VBox 包装挂 styleClass

`header(Node)` 存单个节点，渲染时套一层 `VBox` 并挂 `.form-header`，使 LESS 能控制 header 区 padding/背景/分隔线，
而不污染用户传入的 header 节点本身。null 时完全不创建容器（不占垂直空间）。

### 4. 样式 100% 走 LESS

新增 3 个 styleClass 常量（`CssClasses.FORM_HEADER/FORM_SECTION_TITLE/FORM_FOOTER`），
`theme-base.less` 的 Form 区追加 `.form-header` / `.form-section-title` / `.form-footer` 三段选择器，
仅用 `-color-*` 语义变量与 `@spacing-*` / `@font-size-*` 项目变量。FormAnt.java 内零 `setStyle`。

## 渲染产物结构

```
VBox .jfx-form .form-size-{small|default|large}
├── VBox .form-header            （仅 header != null 时）
│   └── <用户 header 节点>
├── Pane body                    （HORIZONTAL=GridPane / VERTICAL=VBox / INLINE=HBox）
│   ├── Label .form-section-title    （SectionMarker，INLINE 下忽略）
│   ├── <item label + wrapper>
│   └── ...
└── HBox .form-footer            （仅 footerNodes 非空时，alignment=footerAlign，spacing=8）
    └── <footer 节点们>
```

## 兼容性

- `build()` 仍返回 `VBox`，`buildResult()` 仍返回 `Result`（契约不变，SKILL #18 直接节点型）
- 老 `footer(Node)` 行为等价（内部走 footerNodes 单元素路径）
- 未调用任何新 API 时，节点树拓扑/styleClass/布局参数与增强前一致

## 影响文件（与 requirements Req 8 AC4/AC5 一致）

- `jfxium/.../component/FormAnt.java` —— 4 个新 API + entries 混合序列 + 渲染分支
- `jfxium/.../core/css/CssClasses.java` —— 3 个常量
- `jfxium/.../css/less/theme-base.less` —— 3 段选择器
- `jfxium-demo/.../pages/dataentry/FormExamplePage.java` —— 新增示例页（7 Section）
- `jfxium-demo/.../view/MainView.java` —— `registerPages()` 注册 `dataentry.form`
