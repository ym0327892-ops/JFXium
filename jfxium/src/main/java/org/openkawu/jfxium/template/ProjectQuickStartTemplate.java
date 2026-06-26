package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.CodeBlockAnt;
import org.openkawu.jfxium.component.composite.StepsAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectQuickStartTemplate - 快速上手步骤模板。
 *
 * <p>用于项目首页、README 展示页、文档站首页中「快速上手」区域，
 * 把一组上手步骤做成「步骤条 + 代码片段」的引导面板，
 * 让新用户能在最短时间内跑通项目。</p>
 *
 * <h2>适用场景</h2>
 * <ul>
 *   <li>项目首页「快速开始」区域</li>
 *   <li>README 展示页的 Getting Started</li>
 *   <li>文档站首页的引导步骤</li>
 *   <li>工程展示页的接入指南</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * ProjectQuickStartTemplate.create()
 *     .title("快速上手")
 *     .description("3 步跑通 JFXium 项目")
 *     .step("添加依赖", "在 pom.xml 中引入 JFXium",
 *           "<dependency>\n  <groupId>org.openkawu</groupId>\n  ...",
 *           CodeBlockAnt.Language.XML)
 *     .step("创建入口", "编写 Application 启动类",
 *           "public class MyApp extends Application { ... }",
 *           CodeBlockAnt.Language.JAVA)
 *     .step("运行项目", "执行 Maven 命令启动",
 *           "./mvnw javafx:run",
 *           CodeBlockAnt.Language.SHELL)
 *     .build();
 * }</pre>
 */
public final class ProjectQuickStartTemplate {

    // i18n keys: project.quickstart_title, project.quickstart_description

    private ProjectQuickStartTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record StepEntry(String title, String description, String code, CodeBlockAnt.Language language) {}

        private String title = Messages.get("project.quickstart_title");
        private String description = Messages.get("project.quickstart_description");
        private final List<StepEntry> steps = new ArrayList<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.quickstart_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.quickstart_description"));
            return this;
        }

        /**
         * 添加一个步骤（无代码）。
         */
        public Builder step(String title, String description) {
            return step(title, description, null, null);
        }

        /**
         * 添加一个带代码片段的步骤。
         *
         * @param title       步骤标题
         * @param description 步骤说明
         * @param code        代码片段（null 则不渲染代码块）
         * @param language    代码语言（null 默认 JAVA）
         */
        public Builder step(String title, String description, String code, CodeBlockAnt.Language language) {
            steps.add(new StepEntry(
                    TextUtils.safeText(title),
                    TextUtils.safeText(description),
                    code,
                    language != null ? language : CodeBlockAnt.Language.JAVA
            ));
            return this;
        }

        public VBox build() {
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .children(buildHeader(), buildSteps(), buildStepDetails())
                    .build();
            applyStyles(root);
            return root;
        }

        private VBox buildHeader() {
            return VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(title, 4).build(),
                            TypographyAnt.text(description)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();
        }

        /**
         * 步骤条导航：显示所有步骤的标题和完成状态。
         */
        private Node buildSteps() {
            if (steps.isEmpty()) {
                return VBoxAnt.create().build();
            }

            StepsAnt.Builder stepsBuilder = StepsAnt.create()
                    .direction(StepsAnt.Direction.HORIZONTAL)
                    .size(Size.DEFAULT)
                    .current(steps.size()); // 全部 finished

            for (StepEntry entry : steps) {
                stepsBuilder.step(entry.title(), entry.description());
            }

            return stepsBuilder.build();
        }

        /**
         * 步骤详情区：每个步骤展开说明 + 代码片段。
         */
        private Node buildStepDetails() {
            if (steps.isEmpty()) {
                return VBoxAnt.create().build();
            }

            VBox detailsContainer = VBoxAnt.create()
                    .spacing(16)
                    .build();

            for (int i = 0; i < steps.size(); i++) {
                StepEntry entry = steps.get(i);
                detailsContainer.getChildren().add(buildStepDetail(i + 1, entry));
            }

            return detailsContainer;
        }

        private Node buildStepDetail(int index, StepEntry entry) {
            // 步骤序号 + 标题
            Node stepTitle = TypographyAnt.title(index + ". " + entry.title(), 5).build();

            // 步骤说明
            Node stepDesc = TypographyAnt.text(entry.description())
                    .type(TypographyAnt.TextColor.SECONDARY)
                    .build();

            VBoxAnt detailBuilder = VBoxAnt.create()
                    .spacing(8)
                    .children(stepTitle, stepDesc);

            // 代码块（可选）
            if (entry.code() != null && !entry.code().isBlank()) {
                Node codeBlock = CodeBlockAnt.create()
                        .language(entry.language())
                        .code(entry.code())
                        .showLineNumbers(false)
                        .theme(CodeBlockAnt.Theme.AUTO)
                        .selectable(true)
                        .maxHeight(240)
                        .build();
                detailBuilder.children(codeBlock);
            }

            return detailBuilder.build();
        }
    }
}
