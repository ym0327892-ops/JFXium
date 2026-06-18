---
description: JFXium 致命红线规则。所有 Java/LESS 代码必须遵守，违反直接打回。
alwaysApply: true
---

# 致命红线（违反直接打回）

1. **禁止 `setStyle()` 写颜色/px** → 走 styleClass + LESS
2. **禁止 CSS `box-shadow`** → 用 `-fx-effect: dropshadow(...)`
3. **禁止 CSS `:active` / `:focus`** → 用 `:pressed`(`:armed`) / `:focused`
4. **禁止 `-fx-transition` 用于交互状态**（动画类 `.fade-in` 等除外）→ 直接定义状态颜色，用伪类切换
5. **禁止容器吞 padding** → 组合控件容器 `-fx-padding: 0`，下放到子节点
6. **禁止 `@border-radius-full`(9999px) 用于尺寸未钳制的节点** → track/进度条用 `@border-radius-md`
7. **禁止 `build()` 返回类型撒谎** → 返回什么就是什么，不包不装
8. **禁止新建 styleClass 不带 `jfx-` 前缀** → 避免与 modena 冲突
9. **禁止 `.arrow` 节点只设颜色不设 shape** → 必须显式 `-fx-shape` + min/pref 尺寸
10. **禁止新增 public 类不同步 `module-info.java` exports** → 否则下游不可见
11. **禁止业务代码 `new` 原生 JavaFX 控件**（Label/CheckBox/Hyperlink/Button/TextField/ComboBox/RadioButton/TextArea/Slider/ProgressBar/TableView/TreeView/...） → 统一走 `XxxAnt.create(...)` 链式 API。框架内部 `extends XxxAnt` 的实现类与临时 helper 除外。理由：绕过 styleClass + LESS 主题系统 / 失去幂等 toggle / 无法享受封装特性（委托 / Bug 自愈 / i18n 收口）
