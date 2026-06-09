# 边线规范(Border)

> 目标:统一 JFXium 全量边线实现,解决"突兀/显眼/对比度过高"问题。
> 适用范围:所有自定义组件的 1px / Npx 边线(分割线、容器边框、控件外框)。
> 不适用:大于 2px 的描边、装饰性边框(用 `-fx-effect: dropshadow`)。

---

## 1. 核心结论速查

| 你要做的事 | 用什么 | 写在 LESS 的位置 |
|----------|--------|-----------------|
| 交互控件外框(Button/Input/ComboBox) | `-fx-background-insets` 背景层叠 | `components/_button.less` 等 |
| 布局容器边线(BarAnt/GroupBoxAnt/Section) | 原生 `-fx-border-*` | `components/_bar.less` 等 |
| 纯分割线(Separator/Divider) | 原生 `-fx-border-*` 或 Region 节点 | `components/_divider.less` |
| 卡片/分组内**柔和**分割 | 原生 `-fx-border-*` + `.border-muted` 类 | `components/_groupbox.less` |

> 记忆里有一句话很关键:**「交互控件用背景层叠,布局层用原生 border」**,这是分层红线,不是建议。

---

## 2. 两种实现方式对比

### 方式 A:原生 `-fx-border-*`

```less
.border-bottom {
    -fx-border-color: -color-border-muted;
    -fx-border-width: 0 0 1px 0;
    -fx-border-style: solid;   /* 必须显式声明,JavaFX 不会默认 */
}
```

**特点**
- 占外 1px(布局层可接受)
- 1px 直线在 Retina/HiDPI 下可能发虚
- 必须显式 `-fx-border-style: solid`,否则不渲染(已踩坑)

### 方式 B:`-fx-background-insets` 背景层叠(伪边框)

```less
.button {
    -fx-background-color:
        -color-border-default,    /* 外层:边框色 */
        -color-bg-container;      /* 内层:填充色 */
    -fx-background-insets: 0, 1px; /* 外 1px 露出来当边框 */
    -fx-background-radius: 6px, 4px;
}
```

**特点**
- 不占外部空间(交互控件必需)
- 圆角无锯齿
- 多状态切换高效(伪类覆盖 `background-color` 即可)
- **负 inset 会导致占位与可见区错位 → 布局容器禁用**

---

## 3. 颜色档位系统(关键,解决"突兀"问题)

> 历史值 `-color-border-default = #d9d9d9` 在白底上几乎不可见,导致两个极端:
> - 调高对比 → 边线"突兀、刺眼"
> - 保持默认 → 边线"看不见、以为没生效"
>
> 解法:**学 AtlantaFX,做 3 档语义化颜色**,按场景选用,而不是只有一个 `default`。

### LESS 变量定义(在 `variables-base.less`)

```less
/* 边框 3 档:从重到轻 */
@color-border-emphasis: @gray-7;  /* #1677ff / 强调边框(选中态、错误态) */
@color-border-default:  @gray-3;  /* #d9d9d9 / 默认容器边框 */
@color-border-muted:    @gray-2;  /* #e5e7eb / 柔和分割(浅色主题) */
@color-border-subtle:   @gray-1;  /* #f0f0f0 / 微弱分割(几乎隐形) */
```

### 三档语义对照

| 档位 | 浅色值 | 暗色值 | 用途 |
|------|--------|--------|------|
| `-color-border-emphasis` | `#1677ff` | `#3080ff` | 聚焦环、错误边框、强调选中态 |
| `-color-border-default` | `#d9d9d9` | `#30363d` | 默认容器外框、GroupBoxAnt 边框 |
| `-color-border-muted` | `#e5e7eb` | `#21262d` | 卡片内分割、BarAnt 底边线 |
| `-color-border-subtle` | `#f0f0f0` | `#1a1d22` | 表格行间线、最弱分割 |

### 全局 CSS class(参考 AtlantaFX)

```less
.border-default { -fx-border-color: -color-border-default; }
.border-muted   { -fx-border-color: -color-border-muted; }
.border-subtle  { -fx-border-color: -color-border-subtle; }
```

**用法**:在 Builder 上 `.styleClass("border-muted")` 即可,无需手写颜色。

---

## 4. 抗锯齿方案(1px 边线在 HiDPI 屏发虚)

**问题**:JavaFX 的 1px border 在 Retina/125% 缩放下,会渲染成 2px 等效宽度,边缘锯齿明显,显得"硬、刺眼"。

**方案 A:用 `0.5px` 视觉边线**(推荐,布局层)

```less
.subtle-divider {
    -fx-border-color: -color-border-muted;
    -fx-border-width: 0 0 1px 0;
    -fx-border-style: solid;
    -fx-border-insets: 0 0 -0.5 0;   /* 关键:负 inset 让 1px 视觉变 0.5px */
}
```

**方案 B:改用背景层叠(交互控件)**

```less
.button {
    -fx-background-color: -color-border-muted, -color-bg-container;
    -fx-background-insets: 0, 1px;
}
```

**方案 C:避免在主体内容区画 1px 实线**
- 用 `padding` + 背景色块代替"线"
- 用 `-fx-effect: dropshadow(gaussian, -color-border-muted, 0.5, 0, 0, 1)` 做 0.5px 投影

---

## 5. 嵌套场景规范(避免"双重边线")

### 5.1 BarAnt 在 GroupBoxAnt 内部

**问题**:BarAnt 默认有底部边线,GroupBoxAnt 的 header 容器也有底部分割线,叠加变 2px 硬线。

**规范**:
```java
GroupBoxAnt.create()
    .header(bar -> bar
        .title("标题")
        .borderBottom(false)   // 必须:关闭 BarAnt 默认底边线
        .build())
```

> **记忆里已有**:「BarAnt 默认仅启用 borderBottom,GroupBoxAnt 内嵌时必须 `.borderBottom(false)`」

### 5.2 GroupBoxAnt 嵌套 GroupBoxAnt

- 外层 GroupBoxAnt 的边线档位用 `border-default`
- 内层 GroupBoxAnt 的边线档位用 `border-muted`
- 形成"外重内轻"的视觉层级

### 5.3 CardAnt / Section 套在 BarAnt 上方

- BarAnt 顶边线:`.borderTop(false)`(避免与 CardAnt 底边线重叠)
- BarAnt 底边线:保留 `border-muted` 档位

---

## 6. 链式 API 用法(4 方向独立控制)

`AbstractStyleBuilder` 基类已统一 4 个方法,**所有 Builder 组件零成本获得**:

```java
BarAnt.create()
    .borderTop(true)        // 等价 .styleClass("jfx-border-top")
    .borderBottom(true)     // 等价 .styleClass("jfx-border-bottom")
    .borderLeft(false)
    .borderRight(false)
    .borderColor("#e5e7eb") // 可选,覆盖主题色
    .borderWidth(1)         // 可选,默认 1
    .build();
```

**默认行为**(防止突兀):
- `borderTop()` 默认 `false`(不画顶线)
- `borderBottom()` 默认 `true`(画底部分割)
- `borderLeft/Right()` 默认 `false`
- 默认颜色档位:`-color-border-muted`(柔和,非默认)

> 这条默认策略修正了"边线突兀"的根本原因:**默认颜色从 `default` 改为 `muted`**,默认方向从"四边全开"改为"仅底边"。

---

## 7. 常见错误 & 调试

| 现象 | 真正原因 | 修复 |
|------|---------|------|
| 边线"看不见" | `#d9d9d9` 在浅底对比度不足 | 换 `border-muted` 档位或加 `border-width: 2` |
| 边线"突兀刺眼" | 用了 `border-default` 强调档 | 降为 `border-muted` 或 `border-subtle` |
| 边线"发虚/锯齿" | 1px 在 HiDPI 屏渲染抖动 | 用 0.5px 视觉边线(负 `border-insets`) |
| 边线"加不上" | 缺 `-fx-border-style: solid` | 显式声明 `solid` |
| 嵌套处"双线" | BarAnt + 父容器都有底边线 | 内层 `.borderBottom(false)` |
| 容器 padding 错位 | 用 `-fx-background-insets` 负值做布局层边线 | 改用原生 `-fx-border-*` |

### 调试技巧

```less
/* 临时覆盖,确认是不是颜色问题 */
-fx-border-color: red !important;
```

不是颜色问题 → 检查 styleClass 是否添加、CSS 是否编译进 theme。
是颜色问题 → 按"档位系统"小节换档。

---

## 8. 红线提醒(来自 `.qoder/rules/red-lines.md`)

1. 禁止 `setStyle()` 写颜色/px → 走 styleClass + LESS
2. 禁止 CSS `box-shadow` → 用 `-fx-effect: dropshadow(...)`
3. 禁止 `:active` / `:focus` → 用 `:pressed` / `:focused`
4. 禁止容器吞 padding → 组合控件容器 `-fx-padding: 0`
5. 禁止 `@border-radius-full`(9999px)用于尺寸未钳制节点
6. 禁止新建 styleClass 不带 `jfx-` 前缀
   - 本规范新增 class 命名:`jfx-border-default` / `jfx-border-muted` / `jfx-border-subtle`

---

## 9. 落地清单(给实施人)

- [ ] 在 `variables-base.less` 增加 3 档颜色 token
- [ ] 在 `theme-base.less` 增加 3 个 `.jfx-border-*` 全局 class
- [ ] 修改 `AbstractStyleBuilder.borderXxx()` 默认值:`borderTop=false`、`borderColor=-color-border-muted`
- [ ] 修改 BarAnt 默认:`borderBottom` 启用,但颜色走 `muted` 档
- [ ] 全量扫描现有 `components/_*.less`,把硬编码的 `#d9d9d9` 替换为 `-color-border-muted`
- [ ] 在 `JfxStyles.java` 增加 3 个常量:`BORDER_DEFAULT` / `BORDER_MUTED` / `BORDER_SUBTILE`
