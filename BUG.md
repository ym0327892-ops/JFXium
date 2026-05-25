# 问题修复状态（2026-05-17 全部完成）

> 模仿 Ant Design 组件，与 AtlantaFX（[GitHub](https://github.com/mkpaz/atlantafx)）做对照参考。

## 已修复问题

| # | 问题 | 状态 | 修复日期 |
|---|------|------|----------|
| 1 | 输入框 Hover 效果修正 | ✅ 已修复 | 2026-05-12 |
| 2 | Slider 超出边框 | ✅ 已修复 | 2026-05-12 |
| 3 | TextArea 报错信息组件 | ✅ 已修复 | 2026-05-12 |
| 4 | Switch 显示问题 | ✅ 已修复 | 2026-05-12 |
| 5 | MUI 主题阴影空白 | ✅ 已修复 | 2026-05-12 |
| 6 | 组件小型化 | ✅ 已完成 | 2026-05-12 |
| 7 | Spinner 焦点变大 BUG | ✅ 已修复 | 2026-05-12 |
| 8 | 输入框焦点变大 BUG | ✅ 已修复 | 2026-05-12 |
| 10 | Anchor vs Tabs 重复 | ✅ 已分析 | 2026-05-12 |
| 11 | DatePicker 样式丑陋 | ✅ 已修复 | 2026-05-12 |
| 12 | TimePicker 宽度太短 | ✅ 已修复 | 2026-05-12 |
| 13 | ColorPicker 超出边框 | ✅ 已修复 | 2026-05-12 |
| 14 | TreeSelect 无法选中节点 | ✅ 已修复 | 2026-05-12 |
| 15 | InputNumber 按钮无效 | ✅ 已修复 | 2026-05-12 |
| 16 | Calendar 布局问题 | ✅ 已修复 | 2026-05-12 |
| 17 | Popover 点击不消失 | ✅ 已修复 | 2026-05-12 |
| 18 | Drawer 与 Ant 设计一致 | ✅ 已修复 | 2026-05-12 |
| 19 | Animation 没变化 | ✅ 已修复 | 2026-05-12 |
| 20 | BackTop 展示内容 | ✅ 已完善 | 2026-05-12 |
| 21 | Table 行选中色与文字对比度差（怀疑 LESS 未编译） | ✅ 已修复 | 2026-05-17 |
| 22 | MUI 主题输入框 Hover/Focus 时文字看不清 | ✅ 已修复 | 2026-05-17 |
| 23 | 输入框获焦尺寸抖动复检（最终对齐 SKILL：边框变色 + 外阴影，无尺寸变化） | ✅ 已复检 | 2026-05-17 |

## 修复说明（2026-05-17 批次）

### #21 Table 行选中色与文字对比度差
- **现象**：作者吐槽"其他主题色每行选中=主题色，文字看不到"，怀疑 LESS 未编译。
- **复检结论**：LESS 已编译，但选中色绑定到中性色（`@color-base-1`），视觉上几乎和默认背景同色，"看起来没选中"。
- **修复**：将 `theme-base.less` 中 `-color-cell-bg-selected` 从 `@color-base-1` 改绑到 `@color-accent-subtle`（极浅主题色，对齐 Ant Design `controlItemBgActive` token）；新增 `.table-row-cell:selected:hover` 与 `.tree-table-row-cell:selected:hover` 规则，对齐 `controlItemBgActiveHover`。
- **效果**：换主题色时选中色自动跟随，文字色保持默认前景色对比度安全。

### #22 MUI 主题输入框文字硬编码导致换肤失效
- **根因**：`theme-mui.less` 输入类组件 normal 块硬编码 `-fx-text-fill: rgba(0, 0, 0, 0.88);`；其他三个 mui 子主题（mui-compact、mui-dark、mui-dark-compact）此前已经修对（移除该行），唯独 mui.less 漏修，作者反复修了 7-8 次但根因没被找到。
- **修复**：删除 `theme-mui.less:331` 那行硬编码，让文字色继承 `theme-base.less` 的 `-color-fg-default` 语义变量。
- **效果**：和其他三个 mui 子主题保持一致，符合 SKILL 强约束 #1（禁止硬编码）。

### #23 输入框焦点效果复检
- **现象**：作者怀疑"获焦后框体微型变大"，但记录里 #7、#8 已修复。
- **复检结论**：`theme-base.less` 实现完全合规——`:focused` 只改 `border-color` 和 `-fx-effect`，不改 `border-width` 也不改 `padding`，无尺寸抖动。但 4 个 mui 主题在 `:focused` 块里写了 `-fx-effect: none;`，违反 SKILL 强约束 #5"焦点效果统一为边框变色 + 外阴影"。
- **修复**：4 个 mui 主题（mui、mui-compact、mui-dark、mui-dark-compact）的 `:focused` 块统一为 `border-color: -color-accent-emphasis; border-width: 1px; effect: dropshadow(...)`；mui.less 顺手把硬编码 `#1976d2` 改回语义变量，并清理冗余的 `focus-color/faint-focus-color: transparent`（base 已全局处理）。
- **效果**：所有主题焦点反馈一致，无尺寸抖动，符合 SKILL 强约束 #5。

## 修复统计

- **总计问题**：23 个
- **已修复**：22 个
- **分析后无需处理**：1 个（#10 Anchor/Tabs 非重复组件）

---

## 🎯 项目级里程碑：SKILL #1 全合规重构（2026-05-17）

继 #21/#22/#23 三条具体 bug 修复之后，对全项目做了**一次性深度清扫**，
彻底消除 inline `setStyle("-fx-...: -color-...")` 硬编码注入。详见 [PLAN.md](PLAN.md) 第四章里程碑。

**核心数据**：
- 已重构 *Ant 组件：48 个
- 接入公共 `AbstractStyleBuilder<SELF>` 基类：28 个
- 项目级 inline color 注入：**29 个文件 → 0 个文件**
- 顺手修复 3 处隐性 bug：SwitchAnt cursor 残留、CodeBlockAnt spacer 死代码、DividerAnt 文档撒谎

**已知遗留**（详见 PLAN.md P0/P1 计划）：
- SpinAnt 的 `Color.web("#1677ff")` 硬编码（JavaFX Shape API 限制）
- AlertBanner 孤儿类去留
- AnchorAnt / StatisticAnt 的 `Color`-based API（保留兼容）
