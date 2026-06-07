# JFXium 动画系统文档

## 概述

JFXium 动画系统提供了一套常用的 UI 动画效果，对标 Ant Design 动画规范。所有动画都使用了合适的缓动函数和时长，确保流畅自然的用户体验。

## 动画时长

| 常量 | 时长 | 使用场景 |
|------|------|----------|
| `DURATION_FAST` | 150ms | 快速反馈（按钮点击、小元素变化） |
| `DURATION_NORMAL` | 250ms | 一般过渡（淡入淡出、滑动） |
| `DURATION_SLOW` | 350ms | 复杂动画（模态框弹出、页面切换） |

## 缓动函数

- **ease-out**: 用于进入动画（元素出现）
- **ease-in-out**: 用于退出动画（元素消失）

## 淡入淡出动画

### fadeIn - 淡入

```java
// 使用默认时长（250ms）
JFXAnimation.fadeIn(node).play();

// 自定义时长
JFXAnimation.fadeIn(node, Duration.millis(500)).play();
```

### fadeOut - 淡出

```java
// 使用默认时长
JFXAnimation.fadeOut(node).play();

// 自定义时长
JFXAnimation.fadeOut(node, Duration.millis(300)).play();
```

**使用场景**: 页面切换、元素显示/隐藏、提示信息

## 缩放动画

### scaleIn - 缩放进入

```java
JFXAnimation.scaleIn(node).play();
```

### scaleOut - 缩放退出

```java
JFXAnimation.scaleOut(node).play();
```

**使用场景**: 模态框弹出、下拉菜单、气泡提示

## 弹出动画

### popIn - 弹出进入（缩放 + 淡入）

```java
JFXAnimation.popIn(node).play();
```

### popOut - 弹出退出（缩放 + 淡出）

```java
JFXAnimation.popOut(node).play();
```

**使用场景**: 对话框、通知、悬浮卡片

## 滑入动画

### slideInFromBottom - 从底部滑入

```java
JFXAnimation.slideInFromBottom(node).play();
```

### slideInFromTop - 从顶部滑入

```java
JFXAnimation.slideInFromTop(node).play();
```

### slideInFromLeft - 从左侧滑入

```java
JFXAnimation.slideInFromLeft(node).play();
```

### slideInFromRight - 从右侧滑入

```java
JFXAnimation.slideInFromRight(node).play();
```

**使用场景**: 侧边栏、抽屉、列表项、页面切换

## 自定义动画构建器

使用 `AnimationBuilder` 可以组合多种动画效果：

```java
JFXAnimation.builder(node)
    .fade(0, 1)           // 从透明到不透明
    .translateY(20, 0)    // 从下方20像素移动到原位
    .scale(0.9, 1)        // 从0.9缩放到1
    .duration(Duration.millis(300))
    .onFinished(() -> System.out.println("Animation complete!"))
    .play();
```

### 构建器方法

| 方法 | 说明 |
|------|------|
| `duration(Duration)` | 设置动画时长 |
| `fade(double, double)` | 淡入淡出（从, 到） |
| `translateX(double, double)` | X轴位移 |
| `translateY(double, double)` | Y轴位移 |
| `scale(double, double)` | 等比缩放 |
| `scaleX(double, double)` | X轴缩放 |
| `scaleY(double, double)` | Y轴缩放 |
| `onFinished(Runnable)` | 动画完成回调 |

## 实际示例

### 模态框动画

```java
// 显示模态框
JFXAnimation.builder(modalContent)
    .fade(0, 1)
    .scale(0.9, 1)
    .duration(Duration.millis(200))
    .play();

// 隐藏模态框
JFXAnimation.builder(modalContent)
    .fade(1, 0)
    .scale(1, 0.9)
    .duration(Duration.millis(200))
    .onFinished(() -> modalStage.close())
    .play();
```

### 列表项进入动画

```java
for (int i = 0; i < items.size(); i++) {
    Node item = items.get(i);
    item.setOpacity(0);
    item.setTranslateY(20);
    
    JFXAnimation.builder(item)
        .fade(0, 1)
        .translateY(20, 0)
        .duration(Duration.millis(250))
        .play();
    
    // 添加延迟，实现依次进入效果
    PauseTransition delay = new PauseTransition(Duration.millis(i * 50));
    delay.setOnFinished(e -> JFXAnimation.builder(item).play());
    delay.play();
}
```

### 页面切换动画

```java
// 当前页面淡出
JFXAnimation.fadeOut(currentPage, Duration.millis(200)).setOnFinished(e -> {
    // 切换页面
    showPage(newPage);
    
    // 新页面淡入
    JFXAnimation.fadeIn(newPage, Duration.millis(200)).play();
});
```

### 提示信息动画

```java
// 显示提示
JFXAnimation.slideInFromTop(alert).play();

// 3秒后自动消失
PauseTransition delay = new PauseTransition(Duration.seconds(3));
delay.setOnFinished(e -> {
    JFXAnimation.fadeOut(alert).setOnFinished(event -> {
        container.getChildren().remove(alert);
    });
});
delay.play();
```

## 最佳实践

1. **保持动画时长一致**: 同类动画使用相同时长，保持用户体验一致性
2. **避免过多动画**: 不要同时播放太多动画，避免性能问题和视觉混乱
3. **提供关闭选项**: 对于重要信息，提供跳过动画的选项
4. **考虑可访问性**: 尊重用户的 `prefers-reduced-motion` 设置
5. **测试性能**: 在低端设备上测试动画性能，必要时简化动画

## 性能优化

- 使用 `setCache(true)` 缓存动画节点
- 避免在动画中修改布局
- 使用 `TranslateTransition` 而不是修改 `layoutX/Y`
- 对于复杂动画，考虑使用 `Canvas` 或 `WebView`