package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ProjectDashboardTemplate")
class ProjectDashboardTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("默认工程首页模板可直接构建")
    void build_default_dashboard() {
        VBox root = ProjectDashboardTemplate.create().build();

        assertNotNull(root);
        assertEquals(5, root.getChildren().size());
    }

    @Test
    @DisplayName("工程首页模板的 section 顺序稳定")
    void build_and_order() {
        VBox root = ProjectDashboardTemplate.create()
                .snapshot(ProjectDashboardTemplate.Snapshot.demo()
                        .pageTitle("Title")
                        .pageSubtitle("Subtitle")
                        .pageDescription("Description")
                        .currentUser("Tester")
                        .version("2.0.0")
                        .branch("dev")
                        .buildStatus("通过")
                        .mode("Preview")
                        .templateCount("14")
                        .componentCount("112+")
                        .updatedAt("2026-06-18")
                        .completion(0.5))
                .title("Title")
                .description("Description")
                .hero(section("hero"))
                .overview(section("overview"))
                .release(section("release"))
                .launchPad(section("launchpad"))
                .section(section("extra"))
                .build();

        assertNotNull(root);
        assertEquals(6, root.getChildren().size());
        assertEquals("hero", ((Label) root.getChildren().get(1)).getText());
        assertEquals("overview", ((Label) root.getChildren().get(2)).getText());
        assertEquals("release", ((Label) root.getChildren().get(3)).getText());
        assertEquals("launchpad", ((Label) root.getChildren().get(4)).getText());
        assertEquals("extra", ((Label) root.getChildren().get(5)).getText());
    }

    @Test
    @DisplayName("snapshot 可一次性注入工程展示数据")
    void snapshot_applies_data() {
        VBox root = ProjectDashboardTemplate.create()
                .snapshot(ProjectDashboardTemplate.Snapshot.demo()
                        .pageTitle("Dashboard")
                        .pageSubtitle("Sub")
                        .pageDescription("Desc")
                        .currentUser("Alice")
                        .version("2.1.0"))
                .build();

        assertNotNull(root);
        assertEquals(5, root.getChildren().size());
    }

    @Test
    @DisplayName("snapshot 读写属性保持一致")
    void snapshot_accessors_roundtrip() {
        ProjectDashboardTemplate.Snapshot snapshot = ProjectDashboardTemplate.Snapshot.create()
                .pageTitle("Dashboard")
                .pageSubtitle("Sub")
                .pageDescription("Desc")
                .currentUser("Alice")
                .version("2.1.0")
                .branch("dev")
                .buildStatus("通过")
                .mode("Preview")
                .templateCount("14")
                .componentCount("112+")
                .updatedAt("2026-06-18")
                .completion(0.5);

        assertEquals("Dashboard", snapshot.pageTitle());
        assertEquals("Sub", snapshot.pageSubtitle());
        assertEquals("Desc", snapshot.pageDescription());
        assertEquals("Alice", snapshot.currentUser());
        assertEquals("2.1.0", snapshot.version());
        assertEquals("dev", snapshot.branch());
        assertEquals("通过", snapshot.buildStatus());
        assertEquals("Preview", snapshot.mode());
        assertEquals("14", snapshot.templateCount());
        assertEquals("112+", snapshot.componentCount());
        assertEquals("2026-06-18", snapshot.updatedAt());
        assertEquals(0.5, snapshot.completion(), 0.0001);
    }

    private static Node section(String name) {
        return new Label(name);
    }
}
