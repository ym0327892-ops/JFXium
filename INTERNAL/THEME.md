# JFXium 主题系统文档

## 概述

JFXium 主题系统参考 AtlantaFX 和 Ant Design 的设计理念，采用 CSS 文件驱动的方式，支持 Light/Dark 主题切换。

## 架构设计

### 三层 Token 体系

```
Base Token → Semantic Token → Component Token
```

#### Base Token（基础令牌）

定义颜色梯度、字体、间距等基础设计元素。

```css
/* 颜色梯度 */
-color-base-0: #ffffff;
-color-base-1: #f6f8fa;
/* ... */
-color-base-10: #1f2328;

/* 强调色 */
-color-accent-0: #e6f4ff;
-color-accent-1: #bae0ff;
/* ... */
-color-accent-9: #0958d9;

/* 功能色 */
-color-success-5: #52c41a;
-color-warning-5: #faad14;
-color-danger-5: #ff4d4f;
```

#### Semantic Token（语义令牌）

定义具有语义的颜色，如前景色、背景色、边框色。

```css
/* 前景色 */
-color-fg-default: rgba(0, 0, 0, 0.88);
-color-fg-muted: rgba(0, 0, 0, 0.65);
-color-fg-subtle: rgba(0, 0, 0, 0.45);

/* 背景色 */
-color-bg-default: #ffffff;
-color-bg-subtle: #f6f8fa;
-color-bg-inset: #f3f4f6;
-color-bg-layout: #f0f2f5;  /* 页面最外层底色，各主题独立定义 */

/* 边框色 */
-color-border-default: #d9d9d9;
-color-border-muted: #f0f0f0;

/* 强调色 */
-color-accent-emphasis: #1677ff;
-color-accent-subtle: #e6f4ff;
-color-accent-muted: #bae0ff;

/* 功能色 */
-color-success-emphasis: #52c41a;
-color-warning-emphasis: #faad14;
-color-danger-emphasis: #ff4d4f;
```

#### Component Token（组件令牌）

组件特定的样式定义。

```css
/* Button */
.button {
  -fx-background-color: -color-border-default, -color-bg-default;
  -fx-text-fill: -color-fg-default;
}

.button.primary {
  -fx-background-color: -color-accent-emphasis, -color-accent-emphasis;
  -fx-text-fill: white;
}
```

## 主题切换

### 使用 ThemeManager

```java
// 应用 Light 主题
ThemeManager.getInstance().applyTheme(new LightTheme());

// 应用 Dark 主题
ThemeManager.getInstance().applyTheme(new DarkTheme());

// 切换主题
ThemeManager.getInstance().toggleTheme();

// 获取当前主题
Theme currentTheme = ThemeManager.getInstance().getCurrentTheme();
```

### 创建自定义主题

```java
public class CustomTheme implements Theme {
    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/org/openkawu/jfxium/css/custom-theme.css").toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
```

### 自定义 CSS 文件

```css
/* custom-theme.css */
.root {
  /* 修改基础颜色 */
  -color-accent-emphasis: #ff6b6b;
  -color-accent-subtle: #ffe0e0;
  
  /* 修改语义颜色 */
  -color-fg-default: #333333;
  -color-bg-default: #fafafa;
}

/* 修改组件样式 */
.button.primary {
  -fx-background-color: -color-accent-emphasis;
  -fx-text-fill: white;
}
```

## CSS 类名常量

使用 `JfxStyles` 集中管理 CSS 类名，避免拼写错误。

```java
// 使用常量
button.getStyleClass().add(JfxStyles.BUTTON_PRIMARY);

// 而不是硬编码字符串
button.getStyleClass().add("primary");  // 不推荐
```

### 常用类名

| 常量 | 值 | 说明 |
|------|-----|------|
| `BUTTON_DEFAULT` | `"default"` | 默认按钮 |
| `BUTTON_PRIMARY` | `"primary"` | 主要按钮 |
| `BUTTON_OUTLINED` | `"outlined"` | 边框按钮 |
| `BUTTON_DASHED` | `"dashed"` | 虚线按钮 |
| `BUTTON_TEXT` | `"text"` | 文本按钮 |
| `BUTTON_LINK` | `"link"` | 链接按钮 |
| `SIZE_SMALL` | `"small"` | 小尺寸 |
| `SIZE_LARGE` | `"large"` | 大尺寸 |
| `SHAPE_ROUNDED` | `"rounded"` | 圆角形状 |
| `SHAPE_SQUARE` | `"square"` | 方形形状 |
| `CARD` | `"card"` | 卡片 |
| `CARD_BORDERED` | `"bordered"` | 带边框卡片 |
| `CARD_SHADOW_MD` | `"shadow-md"` | 中等阴影 |
| `INPUT` | `"input"` | 输入框 |
| `INPUT_SMALL` | `"small"` | 小输入框 |
| `INPUT_LARGE` | `"large"` | 大输入框 |

## 主题文件结构

```
css/
├── theme-light.css    # Light 主题
├── theme-dark.css     # Dark 主题
└── custom-theme.css   # 自定义主题（可选）
```

### theme-light.css 结构

```css
/* 1. Base Tokens */
.root {
  -color-base-0: #ffffff;
  /* ... */
}

/* 2. Semantic Tokens */
.root {
  -color-fg-default: rgba(0, 0, 0, 0.88);
  /* ... */
}

/* 3. Component Styles */
.button { /* ... */ }
.input { /* ... */ }
.card { /* ... */ }
```

## 最佳实践

### 1. 使用 Token 而不是硬编码颜色

```css
/* 推荐 */
.button {
  -fx-background-color: -color-accent-emphasis;
}

/* 不推荐 */
.button {
  -fx-background-color: #1677ff;
}
```

### 2. 使用语义化命名

```css
/* 推荐 */
-color-fg-default: rgba(0, 0, 0, 0.88);
-color-bg-default: #ffffff;

/* 不推荐 */
-color-black: #000000;
-color-white: #ffffff;
```

### 3. 保持 Token 一致性

```css
/* 推荐 */
-color-accent-emphasis: #1677ff;
-color-accent-subtle: #e6f4ff;
-color-accent-muted: #bae0ff;

/* 不推荐 */
-color-primary: #1677ff;
-color-primary-light: #e6f4ff;
```

### 4. 组件样式使用 Token

```css
/* 推荐 */
.button.primary {
  -fx-background-color: -color-accent-emphasis;
}

/* 不推荐 */
.button.primary {
  -fx-background-color: #1677ff;
}
```

## 自定义主题示例

### 创建企业主题

```css
/* enterprise-theme.css */
.root {
  /* 企业品牌色 */
  -color-accent-emphasis: #003366;
  -color-accent-subtle: #e6f0ff;
  -color-accent-muted: #b3d1ff;
  
  /* 企业字体 */
  -fx-font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}

/* 企业按钮样式 */
.button.primary {
  -fx-background-color: -color-accent-emphasis;
  -fx-text-fill: white;
  -fx-font-weight: 600;
}
```

### 创建暗色主题

```css
/* dark-theme.css */
.root {
  /* 暗色背景 */
  -color-bg-default: #1a1a1a;
  -color-bg-subtle: #2d2d2d;
  -color-bg-inset: #0d0d0d;
  
  /* 亮色文字 */
  -color-fg-default: rgba(255, 255, 255, 0.88);
  -color-fg-muted: rgba(255, 255, 255, 0.65);
  -color-fg-subtle: rgba(255, 255, 255, 0.45);
  
  /* 暗色边框 */
  -color-border-default: #434343;
  -color-border-muted: #303030;
}
```

## 运行时主题切换

```java
// 在应用中切换主题
Button toggleBtn = JFXButton.create("Toggle Theme")
    .onClick(e -> ThemeManager.getInstance().toggleTheme())
    .build();

// 根据系统主题自动切换
if (isSystemDarkMode()) {
    ThemeManager.getInstance().applyTheme(new DarkTheme());
} else {
    ThemeManager.getInstance().applyTheme(new LightTheme());
}
```

## 主题预览

在 Playground 中预览主题效果：

```java
// Playground 中已集成主题切换
ThemeManager.getInstance().applyTheme(new LightTheme());
// 或
ThemeManager.getInstance().applyTheme(new DarkTheme());
```

## 注意事项

1. **CSS 变量兼容性**: JavaFX 21 不支持标准 CSS `var()` 函数，使用 looked-up colors（`-color-*`）
2. **性能**: 避免在运行时频繁切换主题，建议在应用启动时设置主题
3. **自定义样式**: 自定义样式应放在组件样式之后，确保优先级
4. **测试**: 在 Light 和 Dark 主题下测试所有组件，确保对比度足够