---
description: LESS 文件编写规范。编辑 .less 文件时自动触发。
globs: "**/*.less"
---

# LESS 编写规范

## 颜色

- **所有颜色必须使用语义变量**，禁止硬编码 hex 值
  - ✅ `-fx-background-color: -color-accent-emphasis;`
  - ❌ `-fx-background-color: #1677ff;`

- **每个主题色必须定义完整 0-9 级色阶**

## 属性

- **所有属性必须以 `-fx-` 开头**
  - ✅ `-fx-padding: 10 15 10 15;`
  - ❌ `padding: 10 15 10 15;`

- **禁止使用以下 Web CSS 属性**：
  - `box-shadow` → 用 `-fx-effect: dropshadow(gaussian, color, blur, spread, x, y)`
  - `transition` → 直接定义状态颜色
  - `outline` → 用 `-fx-background-insets` 背景层叠
  - `flex` → 用 `HBox.setHgrow()` / `VBox.setVgrow()`

## 伪类

- **Web `:active`** → JavaFX `:pressed` 或 `:armed`
- **Web `:focus`** → JavaFX `:focused`
- **选择** → JavaFX `:selected`
- **`:armed` 和 `:pressed` 必须同时定义**

## 尺寸

- **所有高度/padding 必须使用 token**
  - ✅ `-fx-min-height: @control-height;`
  - ❌ `-fx-min-height: 32px;`

- **`@border-radius-full`(9999px) 仅限 width 和 height 都被 min/max 钳死的节点**
  - ✅ thumb 14×14, badge-dot 8×8
  - ❌ track, 进度条等靠父布局拉伸的节点

## 圆角

- **修改圆角时必须同时覆盖两个属性**
  - ✅ `-fx-background-radius: 0; -fx-border-radius: 0;`
  - ❌ 只覆盖 background-radius

## 选择器

- **复合选择器（`.A.B`）** — 同一节点同时具备多个类，**无空格**
- **后代选择器（`.A .B`）** — 父子关系，**有空格**
- **所有规则必须挂在组件根 styleClass 后代**，禁止全局泛污染
  - ✅ `.jfx-avatar .avatar-label { ... }`
  - ❌ `.label { ... }`（影响所有 Label）

## 组合控件

- **容器 `-fx-padding: 0`**，padding 下放到 `.label` 和 `.arrow-button`
- **`.arrow` 节点必须显式设 `-fx-shape` + min/pref 尺寸**
- **背景层叠实现边框**：`-fx-background-color: borderColor, fillColor; -fx-background-insets: -2, 0;`
