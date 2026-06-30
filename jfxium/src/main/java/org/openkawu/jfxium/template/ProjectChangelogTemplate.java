package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.ListAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectChangelogTemplate - 项目变更日志模板。
 *
 * <p>用于项目首页、发布页、README 展示页中「近期变更」区域，
 * 把版本号和对应的变更条目做成结构化的变更日志面板。</p>
 *
 * <h2>适用场景</h2>
 * <ul>
 *   <li>项目首页的「近期变更」面板</li>
 *   <li>发布页的版本变更日志</li>
 *   <li>README / 文档站的 CHANGELOG 展示</li>
 *   <li>工程展示页的迭代记录</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * ProjectChangelogTemplate.create()
 *     .title("变更日志")
 *     .description("项目近期版本与变更记录")
 *     .version("1.0.2", "2026-06-18",
 *         changelog("新增", "ProjectFeatureTemplate 特性网格模板"),
 *         changelog("新增", "ProjectChangelogTemplate 变更日志模板"),
 *         changelog("优化", "ProjectDashboardTemplate 整合新模板"))
 *     .version("1.0.1", "2026-06-15",
 *         changelog("新增", "ProjectOverviewTemplate 工程概览模板"))
 *     .build();
 * }</pre>
 */
public final class ProjectChangelogTemplate {

    // i18n keys: project.changelog_title, project.changelog_description

    private ProjectChangelogTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    /**
     * 便捷工厂：创建一条变更日志条目。
     *
     * @param type 变更类型（如 "新增"、"优化"、"修复"、"移除"）
     * @param text 变更说明
     * @return 变更条目 record
     */
    public static ChangeEntry changelog(String type, String text) {
        return new ChangeEntry(
                TextUtils.safeText(type),
                TextUtils.safeText(text)
        );
    }

    /**
     * 变更条目：类型 + 说明。
     *
     * @param type 变更类型（新增 / 优化 / 修复 / 移除）
     * @param text 变更说明
     */
    public record ChangeEntry(String type, String text) {}

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private record Version(String version, String date, List<ChangeEntry> changes) {}

        private String title = null;
        private String description = null;
        private final List<Version> versions = new ArrayList<>();
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = TextUtils.safeText(title, Messages.get("project.changelog_title"));
            return this;
        }

        public Builder description(String description) {
            this.description = TextUtils.safeText(description, Messages.get("project.changelog_description"));
            return this;
        }

        public Builder onAction(Consumer<String> handler) {
            this.onAction = handler;
            return this;
        }

        /**
         * 添加一个版本的变更日志。
         *
         * @param version 版本号（如 "1.0.2"）
         * @param date    发布日期（如 "2026-06-18"）
         * @param changes 该版本的变更条目
         */
        public Builder version(String version, String date, ChangeEntry... changes) {
            List<ChangeEntry> list = new ArrayList<>();
            if (changes != null) {
                for (ChangeEntry entry : changes) {
                    if (entry != null) {
                        list.add(entry);
                    }
                }
            }
            versions.add(new Version(
                    TextUtils.safeText(version),
                    TextUtils.safeText(date),
                    list
            ));
            return this;
        }

        public VBox build() {
            VBox root = VBoxAnt.create()
                    .spacing(16)
                    .children(buildHeader(), buildChangelogList())
                    .build();
            applyStyles(root);
            return root;
        }

        private VBox buildHeader() {
            String resolvedTitle = TextUtils.safeText(title, Messages.get("project.changelog_title"));
            String resolvedDescription = TextUtils.safeText(description, Messages.get("project.changelog_description"));
            return VBoxAnt.create()
                    .spacing(4)
                    .children(
                            TypographyAnt.title(resolvedTitle, 4).build(),
                            TypographyAnt.text(resolvedDescription)
                                    .type(TypographyAnt.TextColor.SECONDARY)
                                    .build()
                    )
                    .build();
        }

        private Node buildChangelogList() {
            if (versions.isEmpty()) {
                return VBoxAnt.create().build();
            }

            VBox container = VBoxAnt.create()
                    .spacing(16)
                    .build();

            for (int i = 0; i < versions.size(); i++) {
                Version ver = versions.get(i);
                container.getChildren().add(buildVersionBlock(ver, i == 0));
            }

            return container;
        }

        private Node buildVersionBlock(Version ver, boolean isLatest) {
            // 版本号 + 日期 + 可选 latest 标签
            Node versionTag = TagAnt.create(ver.version())
                    .type(isLatest ? TagAnt.Type.PRIMARY : TagAnt.Type.DEFAULT)
                    .build();

            Node dateLabel = TypographyAnt.text(ver.date())
                    .type(TypographyAnt.TextColor.SECONDARY)
                    .build();

            Node headerRow = HBoxAnt.create()
                    .spacing(8)
                    .align(javafx.geometry.Pos.CENTER_LEFT)
                    .children(versionTag, dateLabel)
                    .build();

            // 变更条目列表
            ListAnt.Builder listBuilder = ListAnt.create()
                    .bordered(false)
                    .split(true);

            for (ChangeEntry entry : ver.changes()) {
                Node typeTag = resolveTypeTag(entry.type());
                listBuilder.item(typeTag, entry.text(), entry.type());
            }

            Node list = listBuilder.build();

            return VBoxAnt.create()
                    .spacing(8)
                    .children(headerRow, list)
                    .build();
        }

        /**
         * 根据变更类型返回对应颜色的 Tag 节点。
         * 新增 → SUCCESS，优化 → PRIMARY，修复 → WARNING，移除 → ERROR。
         */
        private static Node resolveTypeTag(String type) {
            TagAnt.Type tagType = switch (type != null ? type : "") {
                case "新增", "feat", "add" -> TagAnt.Type.SUCCESS;
                case "优化", "refactor", "improve" -> TagAnt.Type.PRIMARY;
                case "修复", "fix", "bugfix" -> TagAnt.Type.WARNING;
                case "移除", "remove", "deprecate" -> TagAnt.Type.ERROR;
                default -> TagAnt.Type.DEFAULT;
            };
            return TagAnt.create(type).type(tagType).build();
        }
    }
}
