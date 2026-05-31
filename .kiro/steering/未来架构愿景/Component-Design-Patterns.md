---
inclusion: manual
---

# JFXium 组件设计模式（v2 重构参考）

> **状态**：未来架构愿景，**不是当前规范**。
> **作用**：v2 大版本重构时回看的设计调研。当前 v1 保持现有 `create + modify` 模式不变。
>
> 本文沉淀于 2026-05-26，由 ButtonAnt 设计哲学讨论而来，记录了 JavaFX 第三方 UI 库的主流设计模式调研、JFXium 现状评估和未来演进方向。

---

## 一、JavaFX 第三方 UI 库 4 大主流设计模式

调研主流 JavaFX UI 库后总结，**业界没有标准答案——分组件用不同策略**。

### 模式 1：CSS-only + Styles 常量

**代表**：[AtlantaFX](https://github.com/mkpaz/atlantafx)（针对原生控件）

**哲学**：不重新发明轮子，直接给原生 Button 涂样式。

**典型代码**：
```java
// AtlantaFX 的整套 API 就是一个常量表
public final class Styles {
    public static final String ACCENT = "accent";
    public static final String SUCCESS = "success";
    public static final String DANGER = "danger";
    public static final String BUTTON_OUTLINED = "button-outlined";
    public static final String SMALL = "small";
    public static final String LARGE = "large";
    // ...
}

// 用户用法
Button btn = new Button("提交");
btn.getStyleClass().addAll(Styles.ACCENT, Styles.LARGE);
```

| 优点 | 缺点 |
|---|---|
| 极轻量（一个常量表 + CSS） | 状态切换分散（add/remove styleClass 散落各处） |
| 100% 原生兼容 | 没有类型安全的 type/size 枚举 |
| FXML / SceneBuilder 友好 | 用户得记字符串，IDE 不补全 |
| 用户可继承任意 Button 子类 | 不能给开发者额外便利方法 |

来源说明：以上 API 设计参考 [AtlantaFX 仓库 base 模块的 theme/Styles.java](https://github.com/mkpaz/atlantafx)，具体内容已 paraphrase。Content was rephrased for compliance with licensing restrictions.

---

### 模式 2：自定义 Control + Skin（JavaFX 官方推荐）

**代表**：AtlantaFX 的新控件（`Card` / `ToggleSwitch` / `RingProgressIndicator`）+ [ControlsFX](https://github.com/controlsfx/controlsfx) 全家桶

**哲学**：对 JavaFX 没有的新控件，按官方架构实现 —— `Control` 类做 Model，`Skin` 类做 View。

**典型代码**（AtlantaFX Card）：
```java
public class Card extends Control {
    public Card() {
        super();
        getStyleClass().add("card");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardSkin(this);
    }

    // 用 Property 暴露所有可变状态
    private final ObjectProperty<Node> header = new SimpleObjectProperty<>(this, "header");
    public ObjectProperty<Node> headerProperty() { return header; }
    public Node getHeader() { return header.get(); }
    public void setHeader(Node header) { this.header.set(header); }
}

// 用户用法
Card card = new Card();
card.setHeader(new Label("标题"));
card.headerProperty().bind(viewModel.titleProperty().map(Label::new));
```

| 优点 | 缺点 |
|---|---|
| JavaFX 官方架构，零摩擦 | 工程量大（每组件至少 2 个类：Xxx + XxxSkin）|
| FXML 原生支持 | 必须继承 Control 或更具体类（多继承死锁） |
| Property 模型 → 响应式天然支持 | 不适合"轻量主题装饰"场景 |
| Skin 跟 Model 解耦，可换皮 | 学习曲线陡（需理解 Skin 生命周期） |

来源说明：以上设计参考 [JavaFX 26 javadoc 关于 Control 的说明](https://openjfx.io/javadoc/26/javafx.controls/javafx/scene/control/package-summary.html) 和 AtlantaFX Card 源码，具体内容已 paraphrase。Content was rephrased for compliance with licensing restrictions.

---

### 模式 3：直接 extends 原生控件

**代表**：[JFoenix](https://github.com/sshahine/JFoenix)、[MaterialFX](https://github.com/palexdev/MaterialFX)

**哲学**：直接继承 JavaFX 现有控件，加自己的 Property 和样式，"开箱即用"。

**典型代码**（JFXButton 模式）：
```java
public class JFXButton extends Button {
    public enum ButtonType { FLAT, RAISED }

    private final ObjectProperty<ButtonType> buttonType =
        new SimpleObjectProperty<>(ButtonType.FLAT);

    public JFXButton(String text) {
        super(text);
        getStyleClass().add("jfx-button");
        // listener: buttonType 变化 → 切 styleClass
    }

    public ObjectProperty<ButtonType> buttonTypeProperty() { return buttonType; }
    public void setButtonType(ButtonType type) { buttonType.set(type); }
}

// 用户用法
JFXButton btn = new JFXButton("提交");
btn.setButtonType(JFXButton.ButtonType.RAISED);
container.getChildren().add(btn);   // 直接当 Button 用
```

| 优点 | 缺点 |
|---|---|
| 零仪式感（`new JFXButton()` 即可用） | 锁死继承链（多继承死锁） |
| FXML 原生支持 | 用户能调 Button 任意 setter（`setStyle("-fx-bg")` 破坏主题） |
| Property 模型 → 响应式 | 修复 JavaFX 原生 Button 的 bug 时容易打架 |
| 类型即接口，传 API 无 unwrap | JFoenix 已停止维护就是反面教材 |

---

### 模式 4：工厂 + Builder（JFXium 当前模式）

**代表**：JFXium、Lombok `@Builder`、Apache CLI 的 `Option.Builder`

**哲学**：把构造和修改分离成两条链，Fluent API 让代码读起来像 DSL。

**典型代码**（ButtonAnt 现状）：
```java
public class ButtonAnt {
    public static Builder create(String text) { ... }
    public static ModifyBuilder modify(Button btn) { ... }

    public static class Builder { ... .build() returns Button }
    public static class ModifyBuilder { ... .apply() returns Button }
}

// 用户用法
Button btn = ButtonAnt.create("提交").type(PRIMARY).size(LARGE).build();
ButtonAnt.modify(btn).type(DANGER).apply();
```

| 优点 | 缺点 |
|---|---|
| 返回原生 Button——零生态摩擦 | 失去 Property 模型 → 响应式要手写 listener |
| 跟项目其他 *Ant 风格统一 | FXML 不友好（FXML 创建不了 Builder） |
| 一过性——用完 Builder 就 GC | 双 API（create + modify）需共享原语保持同步 |
| API 表面整洁——IDE 补全干净 | 不能在 build() 之后通过 ButtonAnt 修改 |

---

## 二、业界主流库对照

| 库 | 主要模式 | 例子 |
|---|---|---|
| **AtlantaFX** | 模式 1（Button）+ 模式 2（Card 等新控件） | 原生控件用 1，新控件用 2 |
| **ControlsFX** | 模式 2 | Rating, BreadcrumbBar, PopOver 全是新控件 |
| **JFoenix** | 模式 3 | `JFXButton extends Button`（已停止维护） |
| **MaterialFX** | 模式 3 | `MFXButton`、`MFXTextField` |
| **JFXium** | 模式 4 | `ButtonAnt.create().build()` |

**关键洞察**：**「混合策略」才是主流**。

```
原生控件（Button / Label / TextField）：
    → 模式 1 / 模式 3 / 模式 4 都行，没有绝对优劣

JavaFX 没有的新控件（Card / Rating / Calendar）：
    → 几乎所有库都选模式 2（自定义 Control + Skin）
```

AtlantaFX 是教科书示例 —— 同一个库内不同组件用不同模式：原生 Button 走模式 1，新控件 Card 走模式 2。

---

## 三、JFXium 当前位置评估

### 选了模式 4（工厂 + Builder）

**当前 ButtonAnt 设计骨架**：
```java
ButtonAnt.create("X").type(...).size(...).build()    // 创建
ButtonAnt.modify(btn).type(...).apply()              // 修改
```

### 真实优势（去掉滤镜后剩下的硬通货）

1. **返回原生 Button，跟 JavaFX 生态零摩擦**
   - `getChildren().add(btn)` / `toolBar.getItems().add(btn)` / `ButtonBar.setButtonData(btn, ...)` 全部直接喂参数
   - 用户拿到 Button 后**想干嘛干嘛**——这是优点，不是缺点（详见第四章）

2. **跟项目其他 47 个 *Ant 组件风格完全一致**
   - 所有组件都是 `XxxAnt.create()....build()` → Node
   - 心智模型零分裂，学一个等于学全部

3. **批量配置 + 一次构建（事务性语义）**
   - 配置 N 个属性，最后一次 build()——心智模型清晰
   - 「构造完才出现完整产物」是经典 Builder 哲学

4. **sentinel 模式让 modify 语义清晰**
   - 「未调用 setter 不动」字面表达准确
   - 包装类等其他模式难以达成等价语义

5. **apply() 是事务边界**
   - 多属性修改一次重渲染，避免中间态
   - 未来若引入动画过渡这点会更重要

6. **静态工厂的可发现性**
   - `XxxAnt.create()` / `XxxAnt.modify()` 在 IDE 中作为顶层入口被组织得整齐

7. **零持有：用户拿到 Button 就走了**
   - Builder 用完 GC，没有遗留 wrapper 对象
   - 跟包装类（必须持续持有 Wrapper 引用）形成对比

8. **`modify()` 是显式动词**
   - 业务代码里 `ButtonAnt.modify(save).type(X).apply()` 明显标记**动态修改的边界**
   - 包装类 `save.setType(X)` 视觉上不区分「初始化」vs「动态切换」

### 真实代价（要诚实暴露）

1. **build/apply 双路径同步问题**（M19.29 已解决）
   - 历史 bug：M19.28 ghost inline 残留就是 Builder.build 改了 ModifyBuilder.apply 没同步
   - **解法**：抽出共享渲染原语 `applyXxxStyleClasses(Button, Xxx)`，两条路径共用

2. **ModifyBuilder 暴露面易落后于 Builder**
   - 历史问题：Builder 加了 ghost / shape，ModifyBuilder 漏了
   - **解法**：每次新增 Builder 属性都同步检查 ModifyBuilder

3. **不支持响应式 `bind()`**
   - 业务想"按钮跟随 form.dirtyProperty() 变色"必须手写 ChangeListener
   - 当前项目实战中没用过响应式，所以这个代价 = 0

4. **状态分散**
   - button 当前 type 要扫 styleClass 才能查询
   - 项目实战中没有"读当前状态"的需求

---

## 四、关键洞察：「build 之后用户想干嘛干嘛」是优点

### 历史误区（已修正）

之前曾把"白名单 API 保护实现细节"列为 ButtonAnt 优点，**这个认知是错的**。

**真相**：
```java
Button btn = ButtonAnt.create("X").build();
btn.setStyle("-fx-bg: red");           // ✅ 没人挡得住
btn.setOnMouseEntered(...);             // ✅
btn.setSkin(myCustomSkin);              // ✅
btn.setTooltip(new Tooltip("..."));     // ✅ 这反而是常见正当需求
```

→ "白名单"只在 Builder 调用阶段成立，**build 之后的 Button 完全裸奔**。项目 demo 自己都到处 `setStyle("-fx-...: -color-...")`。

### 修正后的认知：**「全暴露」是优点不是缺点**

**理由**：

1. **Builder 不可能覆盖 Button 全部 80+ 属性**
   - 用户必然有 tooltip / disableProperty / setOnMouseEntered / setMaxWidth 等需求
   - Builder 不暴露，用户**必须**走 raw API 补足
   - 强行限制只会导致 API 表面"看起来干净"，实际用户工作流被打断

2. **业界主流（AtlantaFX / JFoenix / Material UI）都选「全暴露 + 信任开发者」**
   - 因为限制只在构造期有效，没有实质保护
   - 让用户有「ButtonAnt 是个轻量加强器」而不是「ButtonAnt 是个独立类型」的心智模型

3. **JavaFX 标准 setter 跟 Builder API 不冲突**
   - `btn.setText(s)` / `ButtonAnt.modify(btn).text(s).apply()` 两条路径都 OK
   - 用户按需选择，不必强制走 Builder

4. **保持原生类型 = 保持互操作性**
   - Button 类型可以传给任何只接 `Button` 的第三方 API
   - 包装类 / 继承类做不到这点

### 重新整理「Factory + Builder」模式的优点（去除滤镜）

| # | 好处 | 真假 |
|---|---|---|
| 1 | 返回原生 Button，跟 JavaFX 生态零摩擦 | ✅ 真 |
| 2 | 跟其他 47 个 *Ant 风格统一 | ✅ 真 |
| 3 | 批量配置 + 一次构建（事务性） | ✅ 真 |
| 4 | ~~白名单 API 保护实现细节~~ | ❌ **假**（删除）|
| 5 | sentinel 模式语义清晰 | ✅ 真 |
| 6 | apply() 是事务边界 | ✅ 真 |
| 7 | 静态工厂可发现性高 | ✅ 真 |
| 8 | 零持有，用完 GC | ✅ 真 |
| 9 | modify() 是显式动词 | ✅ 真 |
| 10 | 历史成本是 0 | ✅ 真 |
| 11 | **build 后用户想干嘛干嘛**（API 自由） | ✅ **真**（新增）|

→ 9 个真优点 + 1 个新增的「全暴露自由度」 + 1 个删除的「白名单」。**整体优势仍然成立，且更诚实**。

---

## 五、6 方案完整对比（v2 决策时回看）

为 v2 重构记录的完整对比表。每个方案的实施代码示例参见本仓库 `BUG.md` #27/#28 上下文的对话沉淀。

| 维度 | 当前（Factory+Builder） | A 持有 Button 字段 | B 不可变 Spec | C Decorator | D extends Button | E 包装类 |
|---|---|---|---|---|---|---|
| **API 入口** | `create() / modify()` | `create()` | `render() / rerender()` | `of()` | `new ButtonAnt()` | `new / wrap()` |
| **build() 仪式** | ✅ 需要 | ❌ | ✅ render() | ❌ | ❌ | ❌ |
| **拿到的对象** | `Button` | `ButtonAnt` (要 `.node()`) | `Button` | `Button` (要 `.unwrap()`) | `ButtonAnt`（直接是 Button） | `ButtonAnt` (要 `.node()`) |
| **能否包装外部 Button** | ❌ | ❌ | ✅ | ✅ | ❌ | ✅ |
| **响应式 (`bind`)** | ❌ | ✅ | ❌ | ❌ | ✅ | ✅ |
| **build/apply 同步问题** | ⚠️ 共享原语缓解 | 无（单 listener） | 无（单 render） | 无 | 无 | 无 |
| **JavaFX 生态契合度** | ❌ | ⚠️ | ❌ | ⚠️ | ✅ | ⚠️ |
| **FXML 原生支持** | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ |
| **多继承死锁** | N/A | N/A | N/A | N/A | ⚠️ 锁死 | ✅ 无 |
| **跟其他 *Ant 一致** | ✅ | 半 | ❌ | ❌ | ❌ | 半 |
| **代码量** | ~390 行 | ~150 | ~120 + Spec | ~80 | ~120 | ~150 |

### v2 决策候选排序

**抛开约束理论最优**：D（继承）≥ E（包装类）> A > 当前 ≥ B/C

**对当前 JFXium 实际收益**：当前 > E > D > 其他

---

## 六、v2 演进路线建议（如果未来真要重构）

### 第一步：保持现状识别真痛点

**触发 v2 重构的信号**（任何一条满足）：
- 业务侧出现 5+ 处需要响应式 `bind()` 的需求
- 项目接入 FXML / SceneBuilder（当前不用）
- 用户主动提出"想继承 ButtonAnt 扩展"的需求
- ButtonAnt 暴露面 5+ 次跟 ModifyBuilder 不同步导致 bug

→ 这些信号本项目当前都未触发，**保持模式 4**。

### 第二步：若需重构，推荐路径

**方案 1：增量演进**——保留 `create + modify` API 表面，内部加 Property 字段
```java
// 用户表面 API 不变
Button btn = ButtonAnt.create("X").type(PRIMARY).build();

// 但 Builder 内部用 Property 模型
private final ObjectProperty<Type> type = new SimpleObjectProperty<>(...);
// type listener 自动渲染 styleClass
```

**方案 2：彻底切到模式 3（继承 Button）**——最贴合 JavaFX 哲学
```java
public class ButtonAnt extends Button {
    public final ObjectProperty<Type> typeProperty() { ... }
    public final BooleanProperty ghostProperty() { ... }
    public ButtonAnt withType(Type t) { setType(t); return this; }   // 流式糖
    // ...
}

// 用户：ButtonAnt 就是 Button
ButtonAnt btn = new ButtonAnt("X").withType(PRIMARY);
```

**方案 3：混合模式（学 AtlantaFX）**——原生类组件用模式 1 / 模式 3，新控件用模式 2
```
原生类（Button/Tag/Alert/Switch/CheckBox/Radio）→ 模式 3（extends 原生）
新控件类（Card/Calendar/TimePicker/Carousel）→ 模式 2（extends Control + Skin）
```

### 第三步：迁移策略

不要一次性重写全部 *Ant 组件。先选 1-2 个使用频率最高的组件试点，跑 2-4 周观察：
- 用户反馈
- 跟其他模式 4 组件的协作问题
- demo 改造工作量

确认收益 > 成本后才推广。

---

## 七、决策原则（v2 重构时记得回看）

按 BaseCode SKILL「极简至上」「精准动刀」「不做推测性代码」：

1. **不为「假想的未来需求」重构**
   - "万一以后要 FXML" / "万一以后要响应式" 不是充分理由
   - 只为"已经多次出现的真实痛点"动手

2. **保持 *Ant 组件的内部一致性**
   - 要变就一起变（48 个组件统一）
   - 单独让 ButtonAnt 走异类范式 → 心智模型分裂的代价大于技术优雅

3. **借鉴业界但不照抄**
   - AtlantaFX 的 CSS-only 适合"主题库"定位，JFXium 是"组件库"，不能直接照搬
   - JFoenix 的 extends Button 死锁继承链是反面教材
   - 取其混合策略思路（不同组件用不同模式），而非任何单一极端

---

## 八、相关沉淀

- 本文产生于 2026-05-26 ButtonAnt 设计哲学讨论
- 相关 bug 修复：BUG.md #24/#25/#26/#27/#28（Button ghost inline / 死代码清理 / pom.xml lessc / 共享原语抽取 / ModifyBuilder 暴露面扩展）
- 项目当前组件设计的强约束：见 `组件组合规范/SKILL.md`
- AtlantaFX 源码本地路径：`/Users/openai/workspace/work_open/atlantafx`（用于查看模式 1/2 实证）

---

*版本：1.0*
*创建日期：2026-05-26*
*作者：JFXium 设计哲学讨论沉淀*
