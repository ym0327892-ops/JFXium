# 验收清单 — form-ant-enhance

> 实现状态：**已落地（M19.39）**。本清单分「机器可验证（已实测）」与「人工 UI 验收（待跑 demo）」两段。
> 机器项均已在 2026-06-01 实测通过，命令可复跑。

## 一、机器可验证项（✅ 已实测通过）

| # | 验收标准（对应 requirements） | 验证命令 / 方式 | 结果 |
|---|---|---|---|
| 1 | FormAnt 4 个新 API 存在 | `grep 'public Builder (header\|footer\|footerAlign\|section)' FormAnt.java` | ✅ 4 个全在（含 `footer(Node)` + `footer(Node...)` 两重载） |
| 2 | Req 6.1 FormAnt 无 inline setStyle | `grep -nE 'setStyle\("-fx-' FormAnt.java` | ✅ 0 命中 |
| 3 | Req 7.5 FormExamplePage 无 inline setStyle | `grep -nE 'setStyle\("-fx-' FormExamplePage.java` | ✅ 0 命中 |
| 4 | Req 6.3 CssClasses 3 常量 | `grep FORM_HEADER/FORM_SECTION_TITLE/FORM_FOOTER` | ✅ 全在 |
| 5 | Req 6.4 LESS 3 选择器 | `grep .form-header/.form-section-title/.form-footer theme-base.less` | ✅ 全在（行 4437/4442/4449） |
| 6 | Req 7.1 FormExamplePage 已创建 | 文件存在性 | ✅ |
| 7 | Req 7.3 注册到 DATA_ENTRY | `grep dataentry.form MainView.java` | ✅ 已注册「Form 表单」 |
| 8 | Req 8.1 编译通过 | `./mvnw install -pl jfxium -DskipTests -q` + `./mvnw compile -pl jfxium-demo -q` | ✅ 双零报错 |
| 9 | Req 5 向下兼容 | 老 `footer(Node)` / `item(...)` 多重载 / `build()` 返回 VBox / `buildResult()` 返回 Result 均保留 | ✅ 代码核对一致 |

## 二、人工 UI 验收项（⏳ 需本机跑 demo）

> 启动：`./mvnw javafx:run -pl jfxium-demo`，进入「数据录入 → Form 表单」页。
> 验收基准：**default 尺寸**（SKILL 密度约束，large 不验收）。每条切 8 套主题确认无异常。

- [ ] Section 1 基础表单：3 字段、单按钮 footer，无 header/section，布局正常
- [ ] Section 2 校验：初始加载**不显示**任何错误；点「校验」按钮或失焦后才出现错误文案
- [ ] Section 3 字段联动：password 变化自动触发 confirm 重新校验（无需点按钮）
- [ ] Section 4 header + footer 组合：顶部提示 banner 显示，footer 三按钮右对齐
- [ ] Section 5 多按钮 footer：取消/重置/提交三按钮，右对齐，间距一致（8px）
- [ ] Section 6 section 分段：「基本信息/联系方式/权限设置」三标题视觉分隔清晰
- [ ] Section 7 三 layout 对比：HORIZONTAL/VERTICAL/INLINE 渲染正确，INLINE 下 section 被忽略
- [ ] 切换 8 套主题（light/dark/light-compact/dark-compact/mui×4 等）：header/section/footer 颜色跟随，无硬编码残留
- [ ] 暗色主题下 section 标题、错误文案对比度安全

## 三、验收结论

- **代码层 / 编译层**：✅ 通过，可验收
- **UI 层**：⏳ 待人工跑 demo 勾选第二段清单
