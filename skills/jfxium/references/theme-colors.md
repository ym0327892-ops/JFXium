# 颜色与背景：必须走主题 token

JFXium 有 **11 套主题 × 明/暗 × compact**。任何写死的颜色（尤其是背景）都会在某套主题下变成
“白块 / 黑块 / 看不清的文字”。业务与 AI 代码只允许使用**语义 token**。

## 绝对禁止

| 禁止 | 为什么 |
|---|---|
| `setBackground(new Background(new BackgroundFill(Color.web("#fff"), ...)))` | 亮色正常，暗色主题出现白块 |
| `setStyle("-fx-background-color: #fff;")` | 绕过 LESS，主题切换不生效 |
| `setStyle("-fx-text-fill: #333;")` | 暗色下不可读 |
| LESS 里 `-fx-background-color: #f5f5f5;`（组件样式） | 切换主题后仍是浅灰 |
| `-fx-background-color: rgb(...)` / `rgba(...)` | 同上 |
| 自定义 `.card { -fx-background-color: white; }` 且未挂 `jfx-` 前缀 | 与 modena/主题冲突 |

> 例外：**内容色**（Canvas 绘图、ColorPicker 选中值、Chart 数据系列）可以出现固定色，
> 但它们是“数据”不是“界面底色”，需要人工确认明暗主题下仍可读。

## 正确做法

### Java：只选语义背景层，或挂 styleClass

```java
// 1) 用 Background 枚举（推荐，映射到主题 token）
VBoxAnt card = VBoxAnt.create().background(Background.DEFAULT).build();
VBoxAnt page = VBoxAnt.create().background(Background.LAYOUT).build();
VBoxAnt side = VBoxAnt.create().background(Background.SUBTLE).build();

// 2) 自定义区域：只挂 jfx- 前缀 styleClass，颜色写在 LESS
VBoxAnt banner = VBoxAnt.create().styleClass("jfx-promo-banner").build();
```

### LESS：只用 token 变量

```less
.jfx-promo-banner {
  -fx-background-color: -color-accent-subtle;   /* 强调色浅底，随主题 */
  -fx-border-color: -color-border-muted;
  -fx-text-fill: -color-fg-default;
}

.jfx-danger-zone {
  -fx-background-color: -color-danger-subtle;
  -fx-border-color: -color-danger-muted;
  -fx-text-fill: -color-danger-text;
}
```

## 语义 token 速查

| 用途 | token | 备注 |
|---|---|---|
| 页面最外层灰底 | `-color-bg-layout` | `Background.LAYOUT` |
| 次区域 / 侧栏 / 标题条 | `-color-bg-subtle` | `Background.SUBTLE` |
| 内容主体白底 | `-color-bg-default` | `Background.DEFAULT` |
| 浮层 / 弹窗 | `-color-bg-overlay` | 弹层内容底 |
| 凹槽 / 输入内底 | `-color-bg-inset` | |
| 遮罩 | `-color-bg-masked` | 半透明 |
| 正文文字 | `-color-fg-default` | |
| 次要文字 | `-color-fg-muted` | |
| 弱化文字 | `-color-fg-subtle` | |
| 强调底上的文字 | `-color-fg-on-emphasis` | 主色按钮文字 |
| 默认边框 | `-color-border-default` | |
| 弱边框 / 分隔线 | `-color-border-muted` | |
| 主色 | `-color-accent-emphasis` | hover `-color-accent-hover` / active `-color-accent-active` |
| 主色浅底 | `-color-accent-subtle` | 选中/高亮背景 |
| 成功 / 警告 / 危险 | `-color-success-*` / `-color-warning-*` / `-color-danger-*` | `emphasis / hover / active / text / subtle / muted` |

## 状态色也要走 token

```less
.jfx-promo-banner:hover { -fx-background-color: -color-accent-hover; }
.jfx-promo-banner:pressed { -fx-background-color: -color-accent-active; }
```

不要用 `:active` / `:focus`（JavaFX 伪类名不同），也不要用 `-fx-transition` / `box-shadow`。

## 验收清单（提交前）

- 切 Light / Dark / Fluent / Fluent Dark 四套主题，自定义区域没有白块、黑块、看不清的文字。
- 切 compact 后背景层与间距仍正常。
- 搜索代码：`setBackground(`、`setStyle("-fx-background`、`#` + 组件 LESS，确认无写死颜色。
- `java skills/jfxium/scripts/JfxLint.java <dir>` → `HARDCODED_BACKGROUND` 为 0。
