# JFXium 人工验收清单

> 生成日期：2026-06-08　｜　对应进度：M19.54（全面规则审计），BUG 表 #1–#72 全闭环
> 验收基准：**default 尺寸**（SKILL 密度约束——large 不验收，compact 需可用）
> 代码层 / 编译层已实测通过（jfxium + jfxium-demo 双零报错）；本清单为 **UI 层人工验收**。

## 0. 启动

```bash
./mvnw install -pl jfxium -DskipTests -q      # 先装主框架（含 LESS 编译）
./mvnw javafx:run -pl jfxium-demo             # 启动 demo
```

- [ ] demo 正常启动，进入「首页」无异常
- [ ] 左侧菜单 7 大分类齐全（通用 / 布局 / 导航 / 数据录入 / 数据展示 / 反馈），共 72 个示例页（M19.54 新增 6 个原生控件包装：ChoiceBox / ListView / Separator / SplitMenuButton / BorderPane / TextFlow）
- [ ] 顶栏「亮/暗」「紧凑」「退出」按钮可点

## 1. 全局主题切换（每套都扫一遍核心页）

> 顶栏点「亮/暗」切明暗、「紧凑」切密度。共 11 套：light / dark / light-compact / dark-compact / mui / mui-dark / mui-compact / mui-dark-compact / shadcn / cyberpunk / custom。

- [ ] 切换每套主题，组件颜色整体跟随，无「写死的颜色」突兀残留
- [ ] 暗色系下文字 / 边框 / 选中态对比度安全（看得清）
- [ ] 紧凑模式：控件高度收紧（Button/Input ~28px），但布局不破

## 2. 🔴 重点回归页（对应已修 bug，必看）

> 这些页直接对应 BUG 表 #41–#72 的修复点，验收时**重点确认问题确实没了**。
>
> **M19.54 全面规则审计注**：本轮修复（commit `6ee94e8`）以**代码层红线合规**为主（jfx- 前缀 / setStyle 改 styleClass / mui 主题 .alert-* 改名 / _pagination.less 修复 / module-info exports / MuiTheme 重命名），UI 行为无显著变化——验收时**重点观察现有控件的 className / 结构 / 编译告警是否干净**即可。

- [ ] **SelectableText（通用）** #41：单行拖选**无蓝色边框**、选区文字**不发虚/不重叠**
- [ ] **Watermark（通用）** #2：基础 + 「机密文档」两段 demo 都能看到水印平铺、有用法代码
- [ ] **SplitButton（通用）** #43：按钮**四角无空白、边线连续**（主按钮 + 箭头无缝）
- [ ] **Steps（导航）** #46/#51：有「上一步/下一步」交互，点击高亮**实时移动**（非静态）
- [ ] **Anchor（导航）** #46/#52：点击锚点高亮条**自动移动**到当前项
- [ ] **Dropdown（导航）** #54：选中后能拿到 **label**（不是只显示「点了 key」）
- [ ] **Slider（数据录入）** #56：**范围模式不溢出**容器，轨道不再「横穿窗口」
- [ ] **DatePicker（数据录入）** #44/#57/#39/#49：**单边框**、日期文字**显示全**、点击能弹日历、弹层左右箭头可见
- [ ] **InputNumber / Cascader / TreeSelect / ColorPicker / TimePicker（数据录入）** #45/#53/#50：选中后都能取到 value（结果栏有回显）；TreeSelect 多选可勾多项
- [ ] **ColorPicker（数据录入）** #55：点「自定义颜色…」对话框内 RGB/HSB slider **不超宽**、排版整齐
- [ ] **ToggleButton（数据录入）** #48：有「必选一个、不可全不选」模式（mandatoryGroup）
- [ ] **Transfer（数据录入）** #47：有操作提示 + 结果栏，左右穿梭可操作
- [ ] **Form（数据录入）** M19.39：header / 多按钮 footer / section 分段 / 校验联动 4 项均正常（详见 spec acceptance.md）
- [ ] **CodeBlock（通用）** #32/#33/#34：行号与代码对齐、有「复制」按钮、selectable 模式可拖选部分复制
- [ ] **Card（数据展示）** #38：紧凑模式下 body padding 确实收紧
- [ ] **Table（数据展示）** #37：`striped` 斑马纹生效；选中行文字对比度安全（#21）
- [ ] **ChoiceBox（数据录入）** #72：原生 JavaFX 控件包装，jfx- 前缀 styleClass 渲染、Builder API 完整
- [ ] **ListView（数据展示）** #72：原生 JavaFX 控件包装，列表选中态 + 占位
- [ ] **Separator（数据展示）** #72：原生 JavaFX 控件包装，水平/垂直分隔线渲染正确
- [ ] **SplitMenuButton（通用）** #72：原生 JavaFX 控件包装，主按钮 + 下拉箭头组合无样式冲突
- [ ] **BorderPane（布局）** #72：原生 JavaFX 控件包装，5 区域（top/left/center/right/bottom）布局正确
- [ ] **TextFlow（通用）** #72：原生 JavaFX 控件包装，多样式文本流式排版

## 3. 逐分类页清单（勾选浏览，default 尺寸不破即可）

### 通用（11）
- [ ] Button　- [ ] CodeBlock　- [ ] Typography　- [ ] Icon　- [ ] Segmented
- [ ] Watermark　- [ ] SelectableText　- [ ] MenuButton　- [ ] SplitButton
- [ ] **SplitMenuButton**（#72 M19.54 新增）　- [ ] **TextFlow**（#72 M19.54 新增）

### 布局（4）
- [ ] Grid（响应式断点）　- [ ] Flex　- [ ] SplitBar
- [ ] **BorderPane**（#72 M19.54 新增）

### 导航（7）
- [ ] Menu　- [ ] Tabs　- [ ] Breadcrumb　- [ ] Steps　- [ ] Dropdown　- [ ] Pagination　- [ ] Anchor

### 数据录入（21）
- [ ] Form　- [ ] Input　- [ ] Switch　- [ ] Select　- [ ] Checkbox　- [ ] Radio
- [ ] Slider　- [ ] DatePicker　- [ ] InputNumber　- [ ] Transfer　- [ ] Upload
- [ ] AutoComplete　- [ ] Cascader　- [ ] TreeSelect　- [ ] ColorPicker　- [ ] TimePicker
- [ ] Rate　- [ ] TextArea　- [ ] ToggleButton
- [ ] **ChoiceBox**（#72 M19.54 新增）

### 数据展示（20）
- [ ] Table　- [ ] Tag　- [ ] Card　- [ ] Avatar+Badge　- [ ] Progress　- [ ] Collapse
- [ ] Descriptions　- [ ] Tree　- [ ] List　- [ ] Timeline　- [ ] Carousel　- [ ] Empty
- [ ] Statistic　- [ ] QRCode　- [ ] Image　- [ ] Calendar　- [ ] Skeleton　- [ ] Popover
- [ ] **ListView**（#72 M19.54 新增）　- [ ] **Separator**（#72 M19.54 新增）

### 反馈（9）
- [ ] Message　- [ ] Notification　- [ ] Modal　- [ ] Drawer　- [ ] Alert
- [ ] Spin　- [ ] Popconfirm　- [ ] Result　- [ ] Tooltip

## 4. 验收结论

| 维度 | 结果 |
|---|---|
| 编译 / 代码层 | ✅ 已通过（自动实测） |
| 主题切换（11 套） | ⏳ 待勾 |
| 重点回归页（#41–#72） | ⏳ 待勾 |
| 逐分类浏览（72 页） | ⏳ 待勾 |

> 验收中若发现新问题：按 SKILL §22「示例项目即回归测试」双向溯源——
> 既记录 demo 表现，也追问框架源头 API 是否有缺口，新问题登记到 PROJECT_BUG.md 续编号（从 #73 起）。
