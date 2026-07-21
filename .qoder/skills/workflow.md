---
name: workflow
description: >
  JFXium 项目构建、测试、调试与发布工作流。在执行构建命令、运行测试、
  调试问题或准备发布时使用。涵盖 Maven 命令、LESS 编译、Bug 修复流程。
---

# JFXium 开发工作流

## 一、构建命令

```bash
# 构建核心库（含 LESS → CSS 编译）
./mvnw install -pl jfxium -DskipTests -q

# 运行 Showcase Demo
./mvnw javafx:run -pl jfxium-demo

# 运行全部测试
./mvnw test -pl jfxium

# 运行单个测试类
./mvnw test -pl jfxium -Dtest=ClassName

# 运行单个测试方法
./mvnw test -pl jfxium -Dtest=ClassName#methodName

# 全量清理构建
./mvnw clean install -DskipTests
```

## 二、LESS 编译流程

LESS 文件在 `jfxium/src/main/resources/org/openkawu/jfxium/css/less/` 下，由 **jlessc**（纯 Java）在 `generate-resources` 阶段自动编译。

```
variables-{name}.less → 定义颜色 token
variables-base.less   → 定义尺寸/间距 token + mixins
theme-base.less       → @import "components/_index"
theme-{name}.less     → @import variables + theme-base
```

### LESS 编译陷阱（BUG #64）

如果 `.less` 修改后编译产物没变化：
1. 在 CSS 输出文件加一个标记字符串
2. 重新运行 `mvn generate-resources -pl jfxium`
3. 检查标记是否被覆盖 → 没被覆盖说明 groovy-maven-plugin "假成功"

## 三、主题体系

**6 个主题 CSS**：light, dark, light-compact, dark-compact, shadcn, custom

前 4 个有 Java wrapper（`*Theme.java`：Light / Dark / Shadcn / Custom；light-compact / dark-compact 无独立 wrapper，走 `setDensity` 密度切换），可通过 `ThemeManager.applyTheme()` 切换。
shadcn / custom 为脱管主题，通常直接通过 `scene.getStylesheets().add(...)` 加载。

### ThemeManager 状态机

三轴：Family(Ant/MUI) × dark(boolean) × compact(boolean) = 8 种组合。

```java
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().registerScene(scene);
ThemeManager.getInstance().setPrimaryColor("#ff5722");  // 运行时换主色
```

## 四、新组件开发流程

1. **在 `JfxStyles.java` 添加 styleClass 常量**（`jfx-` 前缀）
2. **创建 `components/_xxx.less`**（组件样式文件）
3. **在 `components/_index.less` 注册** `@import`
4. **创建 Java 类**（继承 AbstractStyleBuilder）
5. **在 `module-info.java` 确认 exports**
6. **在 demo 中添加展示**

## 五、Bug 修复流程

遵循**双向溯源原则**（SKILL §22）：

1. **先表层修复**让用户跑起来（不阻塞）
2. **同时双向追问**：
   - demo 侧：当前 workaround
   - 源头侧：jfxium 框架是否有 API 缺失 / 默认行为不对
3. **该补 API 就补 API，该改默认就改默认**
4. **记录到 `PROJECT_BUG.md`**（序号递增，当前 #65）

### 判断必须修源头的信号

- demo 代码出现手动 key→label 映射表
- 为切状态每次 rebuild 整个节点
- inline `setStyle("-fx-...: -color-...")` 拼字符串
- 回调声明了但 build() 没接线（死回调）
- 反复 cast 类型 `(VBox) component.build()`

## 六、module-info.java 同步规则

新增 public 类时必须同步：
1. `module-info.java` 的 `exports` 声明
2. 如新增 `requires` 依赖，同步 `jfxium/pom.xml`

## 七、关键文件索引

| 文件 | 用途 |
|------|------|
| `AGENTS.md` | AI 工作指南（项目全貌 + 红线） |
| `PROJECT_PLAN.md` | 开发计划与进度 |
| `PROJECT_BUG.md` | Bug 追踪（序号递增） |
| `PROJECT_ACCEPTANCE.md` | QA 验收清单 |
| `JfxStyles.java` | styleClass 常量集中管理 |
| `components/_index.less` | LESS 组件注册入口 |

## 八、Demo 入口

```
mainClass: org.openkawu.jfxium.jfxiumUiExample.JfxiumUiExampleApp
ShowcaseDemo: org.openkawu.jfxium.demo.showcase.ShowcaseDemo
AdminDemo: org.openkawu.jfxium.demo.admin.AdminDemo
```
