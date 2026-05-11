package org.openkawu.jfxium.playground;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.openkawu.jfxium.component.*;
import org.openkawu.jfxium.core.animation.AnimationAnt;
import org.openkawu.jfxium.core.layout.Layouts;
import org.openkawu.jfxium.core.theme.ThemeColor;
import org.openkawu.jfxium.core.theme.ThemeManager;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JFXium Playground - A demo application for the JFXium framework
 * Inspired by Ant Design official website layout
 * Left sidebar menu + Right content area
 */
public class JFXiumPlayground extends Application {
    private Stage stage;
    private Scene scene;
    private VBox contentArea;
    private Map<String, Node> componentDemos;
    private boolean isDarkMode = false;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Initialize component demos
        initComponentDemos();

        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: -color-bg-default;");

        // Left sidebar
        Node sidebar = createSidebar();
        root.setLeft(sidebar);

        // Right content area
        contentArea = new VBox(0);
        contentArea.setStyle("-fx-background-color: -color-bg-default;");
        contentArea.setPadding(new Insets(0));

        ScrollPane contentScroll = new ScrollPane(contentArea);
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: -color-bg-default;");
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setCenter(contentScroll);

        // Top header
        HBox header = createHeader();
        root.setTop(header);

        // Show default component (Button)
        showComponent("Button");

        scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());

        // Register scene for dynamic theme color updates
        ThemeManager.getInstance().registerScene(scene);

        stage.setTitle("JFXium Components - Inspired by Ant Design");
        stage.setScene(scene);
        stage.show();
    }

    private String currentTheme = "light";
    private boolean isCompact = false;
    private boolean isMui = false;

    private void switchTheme() {
        isDarkMode = !isDarkMode;
        applyCurrentTheme();
    }

    private void toggleCompact() {
        isCompact = !isCompact;
        applyCurrentTheme();
    }

    private void toggleMui() {
        isMui = !isMui;
        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        String themeName;
        if (isMui) {
            themeName = "mui";
            if (isDarkMode) {
                themeName += "-dark";
            }
            if (isCompact) {
                themeName += "-compact";
            }
        } else {
            themeName = isDarkMode ? "dark" : "light";
            if (isCompact) {
                themeName += "-compact";
            }
        }
        currentTheme = themeName;

        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(
            "/org/openkawu/jfxium/css/theme-" + themeName + ".css"
        ).toExternalForm());
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: -color-bg-default; -fx-padding: 16px 24px; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

        Label title = new Label("JFXium");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: -color-accent-emphasis;");

        Label subtitle = new Label("Enterprise-class JavaFX UI Framework");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted;");

        HBox.setHgrow(subtitle, Priority.ALWAYS);

        Button themeToggle = ButtonAnt.create("Switch Theme")
            .type(ButtonAnt.Type.PRIMARY)
            .size(ButtonAnt.Size.SMALL)
            .build();
        themeToggle.setOnAction(e -> switchTheme());

        Button compactToggle = ButtonAnt.create("Compact")
            .type(ButtonAnt.Type.DEFAULT)
            .size(ButtonAnt.Size.SMALL)
            .build();
        compactToggle.setOnAction(e -> toggleCompact());

        Button muiToggle = ButtonAnt.create("MUI")
            .type(ButtonAnt.Type.DEFAULT)
            .size(ButtonAnt.Size.SMALL)
            .build();
        muiToggle.setOnAction(e -> toggleMui());

        Button colorToggle = ButtonAnt.create("Color")
            .type(ButtonAnt.Type.DEFAULT)
            .size(ButtonAnt.Size.SMALL)
            .build();
        colorToggle.setOnAction(e -> cycleThemeColor());

        header.getChildren().addAll(title, subtitle, colorToggle, muiToggle, compactToggle, themeToggle);
        return header;
    }

    private int currentColorIndex = 0;
    private final ThemeColor.Preset[] colorPresets = ThemeColor.Preset.values();

    private void cycleThemeColor() {
        currentColorIndex = (currentColorIndex + 1) % colorPresets.length;
        ThemeColor.Preset preset = colorPresets[currentColorIndex];
        ThemeManager.getInstance().setPrimaryColor(preset);
    }

    private Node createSidebar() {
        VBox menu = MenuAnt.create()
            .group("通用 General")
            .item("Button", () -> showComponent("Button"))
            .item("Input", () -> showComponent("Input"))
            .item("TextArea", () -> showComponent("TextArea"))
            .item("CheckBox", () -> showComponent("CheckBox"))
            .item("RadioButton", () -> showComponent("RadioButton"))
            .item("Switch", () -> showComponent("Switch"))
            .item("Slider", () -> showComponent("Slider"))
            .item("Spinner", () -> showComponent("Spinner"))
            .divider()
            .group("布局 Layout")
            .item("Card", () -> showComponent("Card"))
            .item("Divider", () -> showComponent("Divider"))
            .item("Layout", () -> showComponent("Layout"))
            .item("Form", () -> showComponent("Form"))
            .item("Table", () -> showComponent("Table"))
            .divider()
            .group("导航 Navigation")
            .item("Breadcrumb", () -> showComponent("Breadcrumb"))
            .item("Menu", () -> showComponent("Menu"))
            .item("Pagination", () -> showComponent("Pagination"))
            .item("Steps", () -> showComponent("Steps"))
            .item("Tabs", () -> showComponent("Tabs"))
            .item("Anchor", () -> showComponent("Anchor"))
            .item("Dropdown", () -> showComponent("Dropdown"))
            .divider()
            .group("数据录入 Data Entry")
            .item("ComboBox", () -> showComponent("ComboBox"))
            .item("DatePicker", () -> showComponent("DatePicker"))
            .item("TimePicker", () -> showComponent("TimePicker"))
            .item("ColorPicker", () -> showComponent("ColorPicker"))
            .item("Upload", () -> showComponent("Upload"))
            .item("Transfer", () -> showComponent("Transfer"))
            .item("AutoComplete", () -> showComponent("AutoComplete"))
            .item("Cascader", () -> showComponent("Cascader"))
            .item("TreeSelect", () -> showComponent("TreeSelect"))
            .item("InputNumber", () -> showComponent("InputNumber"))
            .divider()
            .group("数据展示 Data Display")
            .item("Avatar", () -> showComponent("Avatar"))
            .item("Badge", () -> showComponent("Badge"))
            .item("Calendar", () -> showComponent("Calendar"))
            .item("Carousel", () -> showComponent("Carousel"))
            .item("Descriptions", () -> showComponent("Descriptions"))
            .item("Empty", () -> showComponent("Empty"))
            .item("Image", () -> showComponent("Image"))
            .item("List", () -> showComponent("List"))
            .item("ListView", () -> showComponent("ListView"))
            .item("Popover", () -> showComponent("Popover"))
            .item("QRCode", () -> showComponent("QRCode"))
            .item("Segmented", () -> showComponent("Segmented"))
            .item("Statistic", () -> showComponent("Statistic"))
            .item("Tag", () -> showComponent("Tag"))
            .item("Timeline", () -> showComponent("Timeline"))
            .item("Tooltip", () -> showComponent("Tooltip"))
            .item("Tree", () -> showComponent("Tree"))
            .item("Typography", () -> showComponent("Typography"))
            .divider()
            .group("反馈 Feedback")
            .item("Alert", () -> showComponent("Alert"))
            .item("Drawer", () -> showComponent("Drawer"))
            .item("Message", () -> showComponent("Message"))
            .item("Modal", () -> showComponent("Modal"))
            .item("Notification", () -> showComponent("Notification"))
            .item("Popconfirm", () -> showComponent("Popconfirm"))
            .item("Progress", () -> showComponent("Progress"))
            .item("Result", () -> showComponent("Result"))
            .item("Skeleton", () -> showComponent("Skeleton"))
            .item("Spin", () -> showComponent("Spin"))
            .divider()
            .group("其他 Other")
            .item("Animation", () -> showComponent("Animation"))
            .item("BackTop", () -> showComponent("BackTop"))
            .item("FloatButton", () -> showComponent("FloatButton"))
            .item("Mentions", () -> showComponent("Mentions"))
            .build();

        menu.setPrefWidth(260);
        menu.setMinWidth(260);

        ScrollPane scrollPane = new ScrollPane(menu);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: -color-bg-default;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    private void showComponent(String name) {
        contentArea.getChildren().clear();

        Node demo = componentDemos.get(name);
        if (demo != null) {
            // Component title
            Label title = new Label(name);
            title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default; -fx-padding: 32px 48px 16px 48px;");

            // Component description
            String description = getComponentDescription(name);
            Label desc = new Label(description);
            desc.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted; -fx-padding: 0 48px 32px 48px;");
            desc.setWrapText(true);

            // Demo section title
            Label demoTitle = new Label("Code Demo");
            demoTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default; -fx-padding: 24px 48px 16px 48px;");

            // Demo container
            VBox demoContainer = new VBox(demo);
            demoContainer.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: -color-border-muted; -fx-border-width: 1px;");
            demoContainer.setPadding(new Insets(24));
            VBox.setMargin(demoContainer, new Insets(0, 48, 48, 48));

            contentArea.getChildren().addAll(title, desc, demoTitle, demoContainer);
        }
    }

    private String getComponentDescription(String name) {
        return switch (name) {
            case "Button" -> "Button is used to start an immediate operation.";
            case "Input" -> "Input is used to get user input.";
            case "Card" -> "Card is a container for content.";
            case "Modal" -> "Modal dialog for displaying important information.";
            case "Table" -> "Table for displaying structured data.";
            case "Form" -> "Form component with data validation.";
            case "Breadcrumb" -> "Breadcrumb navigation aid.";
            case "Message" -> "Lightweight feedback message.";
            case "Notification" -> "Notification message at the corner.";
            case "Drawer" -> "Panel sliding from the edge.";
            case "Popconfirm" -> "Simple confirmation dialog.";
            case "Popover" -> "Floating card near target.";
            case "TimePicker" -> "Time selection component.";
            case "TreeSelect" -> "Tree-structured dropdown selection.";
            case "Typography" -> "Text formatting and styling.";
            case "FloatButton" -> "Floating action button.";
            case "List" -> "Advanced list with rich items.";
            default -> "Component demonstration.";
        };
    }

    private void initComponentDemos() {
        componentDemos = new LinkedHashMap<>();

        // 通用 General
        componentDemos.put("Button", createButtonDemo());
        componentDemos.put("Input", createInputDemo());
        componentDemos.put("TextArea", createTextAreaDemo());
        componentDemos.put("CheckBox", createCheckBoxDemo());
        componentDemos.put("RadioButton", createRadioButtonDemo());
        componentDemos.put("Switch", createSwitchDemo());
        componentDemos.put("Slider", createSliderDemo());
        componentDemos.put("Spinner", createSpinnerDemo());

        // 布局 Layout
        componentDemos.put("Card", createCardDemo());
        componentDemos.put("Divider", createDividerDemo());
        componentDemos.put("Layout", createLayoutDemo());
        componentDemos.put("Form", createFormDemo());
        componentDemos.put("Table", createTableDemo());

        // 导航 Navigation
        componentDemos.put("Anchor", createAnchorDemo());
        componentDemos.put("Breadcrumb", createBreadcrumbDemo());
        componentDemos.put("Dropdown", createDropdownDemo());
        componentDemos.put("Menu", createMenuDemo());
        componentDemos.put("Pagination", createPaginationDemo());
        componentDemos.put("Steps", createStepsDemo());
        componentDemos.put("Tabs", createTabsDemo());

        // 数据录入 Data Entry
        componentDemos.put("AutoComplete", createAutoCompleteDemo());
        componentDemos.put("Cascader", createCascaderDemo());
        componentDemos.put("ComboBox", createComboBoxDemo());
        componentDemos.put("DatePicker", createDatePickerDemo());
        componentDemos.put("TimePicker", createTimePickerDemo());
        componentDemos.put("ColorPicker", createColorPickerDemo());
        componentDemos.put("InputNumber", createInputNumberDemo());
        componentDemos.put("Upload", createUploadDemo());
        componentDemos.put("Transfer", createTransferDemo());
        componentDemos.put("TreeSelect", createTreeSelectDemo());

        // 数据展示 Data Display
        componentDemos.put("Avatar", createAvatarDemo());
        componentDemos.put("Badge", createBadgeDemo());
        componentDemos.put("Calendar", createCalendarDemo());
        componentDemos.put("Carousel", createCarouselDemo());
        componentDemos.put("Collapse", createCollapseDemo());
        componentDemos.put("Descriptions", createDescriptionsDemo());
        componentDemos.put("Empty", createEmptyDemo());
        componentDemos.put("Image", createImageDemo());
        componentDemos.put("List", createListDemo());
        componentDemos.put("ListView", createListViewDemo());
        componentDemos.put("Popover", createPopoverDemo());
        componentDemos.put("Segmented", createSegmentedDemo());
        componentDemos.put("Statistic", createStatisticDemo());
        componentDemos.put("Tag", createTagDemo());
        componentDemos.put("Timeline", createTimelineDemo());
        componentDemos.put("Tooltip", createTooltipDemo());
        componentDemos.put("Tree", createTreeDemo());
        componentDemos.put("Typography", createTypographyDemo());

        // 反馈 Feedback
        componentDemos.put("Alert", createAlertDemo());
        componentDemos.put("Drawer", createDrawerDemo());
        componentDemos.put("Message", createMessageDemo());
        componentDemos.put("Modal", createModalDemo());
        componentDemos.put("Notification", createNotificationDemo());
        componentDemos.put("Popconfirm", createPopconfirmDemo());
        componentDemos.put("Progress", createProgressDemo());
        componentDemos.put("Result", createResultDemo());
        componentDemos.put("Skeleton", createSkeletonDemo());
        componentDemos.put("Spin", createSpinDemo());

        // 其他 Other
        componentDemos.put("Animation", createAnimationDemo());
        componentDemos.put("BackTop", createBackTopDemo());
        componentDemos.put("FloatButton", createFloatButtonDemo());
        componentDemos.put("QRCode", createQRCodeDemo());
        componentDemos.put("Mentions", createMentionsDemo());
    }

    // ==================== Demo Methods ====================

    private VBox createButtonDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        HBox row1 = new HBox(12);
        row1.setAlignment(Pos.CENTER_LEFT);
        row1.getChildren().addAll(
            ButtonAnt.create("Primary").type(ButtonAnt.Type.PRIMARY).build(),
            ButtonAnt.create("Default").type(ButtonAnt.Type.DEFAULT).build(),
            ButtonAnt.create("Outlined").type(ButtonAnt.Type.OUTLINED).build(),
            ButtonAnt.create("Text").type(ButtonAnt.Type.TEXT).build(),
            ButtonAnt.create("Link").type(ButtonAnt.Type.LINK).build()
        );

        HBox row2 = new HBox(12);
        row2.setAlignment(Pos.CENTER_LEFT);
        row2.getChildren().addAll(
            ButtonAnt.create("Success").type(ButtonAnt.Type.SUCCESS).build(),
            ButtonAnt.create("Danger").type(ButtonAnt.Type.DANGER).build(),
            ButtonAnt.create("Warning").type(ButtonAnt.Type.WARNING).build()
        );

        HBox row3 = new HBox(12);
        row3.setAlignment(Pos.CENTER_LEFT);
        row3.getChildren().addAll(
            ButtonAnt.create("Small").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.SMALL).build(),
            ButtonAnt.create("Default").type(ButtonAnt.Type.PRIMARY).build(),
            ButtonAnt.create("Large").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.LARGE).build()
        );

        // Ghost buttons - 对标 Ant Design ghost 属性
        HBox row4 = new HBox(12);
        row4.setAlignment(Pos.CENTER_LEFT);
        row4.setStyle("-fx-background-color: -color-accent-emphasis; -fx-padding: 12px; -fx-background-radius: 6px;");
        row4.getChildren().addAll(
            ButtonAnt.create("Primary Ghost").type(ButtonAnt.Type.PRIMARY).ghost().build(),
            ButtonAnt.create("Default Ghost").type(ButtonAnt.Type.DEFAULT).ghost().build(),
            ButtonAnt.create("Danger Ghost").type(ButtonAnt.Type.DANGER).ghost().build()
        );

        // Block button - 对标 Ant Design block 属性
        VBox row5 = new VBox(8);
        row5.setAlignment(Pos.CENTER_LEFT);
        row5.getChildren().addAll(
            ButtonAnt.create("Block Primary").type(ButtonAnt.Type.PRIMARY).block().build(),
            ButtonAnt.create("Block Default").type(ButtonAnt.Type.DEFAULT).block().build()
        );

        box.getChildren().addAll(
            new Label("Button Types") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            row1,
            new Label("Color Variants") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            row2,
            new Label("Size Variants") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            row3,
            new Label("Ghost Buttons (on colored background)") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            row4,
            new Label("Block Buttons (full width)") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            row5
        );
        return box;
    }

    private VBox createInputDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            InputAnt.create().placeholder("Basic input").build(),
            InputAnt.create().placeholder("Small input").size(InputAnt.Size.SMALL).build(),
            InputAnt.create().placeholder("Large input").size(InputAnt.Size.LARGE).build(),
            InputAnt.create().placeholder("Disabled input").disabled(true).build(),
            InputAnt.create().placeholder("Input with prefix").build()
        );
        return box;
    }

    private VBox createCardDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        javafx.scene.layout.VBox card = CardAnt.create()
            .title("Card Title")
            .content(new Label("Card content goes here."))
            .extra(ButtonAnt.create("Action").type(ButtonAnt.Type.PRIMARY).build())
            .build();

        javafx.scene.layout.VBox hoverableCard = CardAnt.create()
            .title("Hoverable Card")
            .content(new Label("This card has hover effect."))
            .hoverable(true)
            .build();

        box.getChildren().addAll(card, hoverableCard);
        return box;
    }

    private VBox createModalDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        Button[] basicModalBtnRef = new Button[1];
        basicModalBtnRef[0] = ButtonAnt.create("Open Basic Modal")
            .type(ButtonAnt.Type.PRIMARY)
            .onClick(e -> {
                ModalAnt.create()
                    .title("Basic Modal")
                    .content("This is a basic modal dialog.")
                    .build()
                    .open(basicModalBtnRef[0]);
            })
            .build();

        // 键盘 ESC 关闭 + confirmLoading 演示
        Button[] loadingModalBtnRef = new Button[1];
        loadingModalBtnRef[0] = ButtonAnt.create("Open Loading Modal (ESC to close)")
            .type(ButtonAnt.Type.PRIMARY)
            .onClick(e -> {
                ModalAnt.create()
                    .title("Loading Modal")
                    .content("Press ESC to close this modal. The OK button shows loading state.")
                    .keyboard(true)
                    .confirmLoading(true)
                    .okText("Submit")
                    .cancelText("Cancel")
                    .onOk(() -> System.out.println("OK clicked!"))
                    .build()
                    .open(loadingModalBtnRef[0]);
            })
            .build();

        box.getChildren().addAll(
            basicModalBtnRef[0],
            loadingModalBtnRef[0]
        );
        return box;
    }

    private VBox createAnimationDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        Label fadeBox = new Label("Fade Animation");
        fadeBox.setStyle("-fx-padding: 20px; -fx-background-color: -color-accent-subtle; -fx-background-radius: 8px;");

        Button fadeBtn = ButtonAnt.create("Fade In/Out").type(ButtonAnt.Type.PRIMARY).build();
        fadeBtn.setOnAction(e -> {
            AnimationAnt.fadeIn(fadeBox, Duration.millis(500));
        });

        box.getChildren().addAll(fadeBox, fadeBtn);
        return box;
    }

    private VBox createTableDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        javafx.scene.control.TableView<Person> table = TableAnt.<Person>create()
            .column("Name", Person::getName)
            .column("Age", p -> String.valueOf(p.getAge()))
            .column("Email", Person::getEmail)
            .data(Arrays.asList(
                new Person("John Doe", 30, "john@example.com"),
                new Person("Jane Smith", 25, "jane@example.com"),
                new Person("Bob Johnson", 35, "bob@example.com")
            ))
            .striped(true)
            .bordered(true)
            .build();

        box.getChildren().add(table);
        return box;
    }

    private VBox createFormDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        // Vertical layout form
        VBox formVertical = FormAnt.create()
            .item("Username", InputAnt.create().placeholder("Enter username").build())
            .item("Email", InputAnt.create().placeholder("Enter email").build())
            .item("Password", InputAnt.create().placeholder("Enter password").build(), true)
            .layout(FormAnt.Layout.VERTICAL)
            .build();

        // Horizontal layout form - 对标 Ant Design labelCol/wrapperCol
        VBox formHorizontal = FormAnt.create()
            .item("Username", InputAnt.create().placeholder("Enter username").build())
            .item("Email", InputAnt.create().placeholder("Enter email").build())
            .item("Password", InputAnt.create().placeholder("Enter password").build(), true)
            .layout(FormAnt.Layout.HORIZONTAL)
            .build();

        box.getChildren().addAll(
            new Label("Vertical Layout") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            formVertical,
            new Label("Horizontal Layout (labelCol/wrapperCol)") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            formHorizontal
        );
        return box;
    }

    private VBox createLayoutDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        VBox vboxDemo = Layouts.vbox()
            .spacing(8)
            .padding(16)
            .align(Pos.CENTER)
            .children(
                new Label("VBox Layout"),
                ButtonAnt.create("Button 1").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("Button 2").type(ButtonAnt.Type.OUTLINED).build()
            )
            .build();
        vboxDemo.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");

        box.getChildren().add(vboxDemo);
        return box;
    }

    private VBox createMenuDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(new Label("Menu demo - see top navigation"));
        return box;
    }

    private VBox createTabsDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // 1. Line 样式（默认）- 对标 Ant Design type="line"
        VBox lineTabs = new VBox(8);
        lineTabs.getChildren().addAll(
            new Label("Line Style (default)") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .tab("tab1", "Tab 1", new Label("Content of Tab Pane 1"))
                .tab("tab2", "Tab 2", new Label("Content of Tab Pane 2"))
                .tab("tab3", "Tab 3", new Label("Content of Tab Pane 3"))
                .build()
        );

        // 2. Card 样式 - 对标 Ant Design type="card"
        VBox cardTabs = new VBox(8);
        cardTabs.getChildren().addAll(
            new Label("Card Style") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .type(TabsAnt.Type.CARD)
                .tab("tab1", "Card Tab 1", new Label("Content of Card Tab 1"))
                .tab("tab2", "Card Tab 2", new Label("Content of Card Tab 2"))
                .tab("tab3", "Card Tab 3", new Label("Content of Card Tab 3"))
                .build()
        );

        // 3. 大尺寸 + 居中 - 对标 Ant Design size="large" centered
        VBox largeCenteredTabs = new VBox(8);
        largeCenteredTabs.getChildren().addAll(
            new Label("Large + Centered") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .type(TabsAnt.Type.LINE)
                .size(TabsAnt.Size.LARGE)
                .centered(true)
                .tab("tab1", "Tab 1", new Label("Content of Tab 1"))
                .tab("tab2", "Tab 2", new Label("Content of Tab 2"))
                .tab("tab3", "Tab 3", new Label("Content of Tab 3"))
                .build()
        );

        // 4. 小号 + 底部位置 - 对标 Ant Design size="small" tabPlacement="bottom"
        VBox smallBottomTabs = new VBox(8);
        smallBottomTabs.getChildren().addAll(
            new Label("Small + Bottom Placement") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .type(TabsAnt.Type.LINE)
                .size(TabsAnt.Size.SMALL)
                .tabPlacement(TabsAnt.TabPlacement.BOTTOM)
                .tab("tab1", "Tab 1", new Label("Content of Tab 1"))
                .tab("tab2", "Tab 2", new Label("Content of Tab 2"))
                .build()
        );

        // 5. 带附加操作 - 对标 Ant Design tabBarExtraContent
        VBox extraTabs = new VBox(8);
        extraTabs.getChildren().addAll(
            new Label("With Extra Content") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .type(TabsAnt.Type.LINE)
                .extraRight(ButtonAnt.create("Extra Action").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.SMALL).build())
                .tab("tab1", "Tab 1", new Label("Content of Tab 1"))
                .tab("tab2", "Tab 2", new Label("Content of Tab 2"))
                .build()
        );

        // 6. 禁用标签 - 对标 Ant Design disabled
        VBox disabledTabs = new VBox(8);
        disabledTabs.getChildren().addAll(
            new Label("With Disabled Tab") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TabsAnt.create()
                .type(TabsAnt.Type.LINE)
                .tab("tab1", "Tab 1", new Label("Content of Tab 1"))
                .tab("tab2", "Tab 2", new Label("Content of Tab 2"), true)
                .tab("tab3", "Tab 3", new Label("Content of Tab 3"))
                .build()
        );

        box.getChildren().addAll(
            lineTabs,
            cardTabs,
            largeCenteredTabs,
            smallBottomTabs,
            extraTabs,
            disabledTabs
        );
        return box;
    }

    private VBox createTreeDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.TreeView<String> tree = new javafx.scene.control.TreeView<>();
        javafx.scene.control.TreeItem<String> root = new javafx.scene.control.TreeItem<>("Root");
        root.getChildren().addAll(
            new javafx.scene.control.TreeItem<>("Child 1"),
            new javafx.scene.control.TreeItem<>("Child 2")
        );
        tree.setRoot(root);
        box.getChildren().add(tree);
        return box;
    }

    private VBox createDatePickerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            DatePickerAnt.create().placeholder("Select date").build(),
            DatePickerAnt.create().placeholder("Range picker").build()
        );
        return box;
    }

    private VBox createSliderDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // 1. 基础滑动条 - 对标 Ant Design 基本示例
        VBox basicSlider = new VBox(8);
        basicSlider.getChildren().addAll(
            new Label("Basic Slider") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            SliderAnt.create()
                .min(0)
                .max(100)
                .value(50)
                .onChange(v -> System.out.println("Slider value: " + v))
                .build()
        );

        // 2. 禁用状态
        VBox disabledSlider = new VBox(8);
        disabledSlider.getChildren().addAll(
            new Label("Disabled Slider") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            SliderAnt.create()
                .min(0)
                .max(100)
                .value(30)
                .disabled(true)
                .build()
        );

        // 3. 带刻度标记 - 对标 Ant Design marks
        java.util.Map<Double, String> tempMarks = new java.util.LinkedHashMap<>();
        tempMarks.put(0.0, "0°C");
        tempMarks.put(26.0, "26°C");
        tempMarks.put(37.0, "37°C");
        tempMarks.put(100.0, "100°C");

        VBox marksSlider = new VBox(8);
        marksSlider.getChildren().addAll(
            new Label("Slider with Marks") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            SliderAnt.create()
                .min(0)
                .max(100)
                .value(37)
                .marks(tempMarks)
                .tipFormatter(v -> v + "°C")
                .build()
        );

        // 4. 范围选择 - 对标 Ant Design range
        VBox rangeSlider = new VBox(8);
        rangeSlider.getChildren().addAll(
            new Label("Range Slider") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            SliderAnt.create()
                .range(true)
                .min(0)
                .max(100)
                .defaultValue(new double[]{20, 80})
                .build()
        );

        // 5. 带提示框
        VBox tooltipSlider = new VBox(8);
        tooltipSlider.getChildren().addAll(
            new Label("Slider with Tooltip") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            SliderAnt.create()
                .min(0)
                .max(100)
                .value(50)
                .tipFormatter(v -> "Value: " + v.intValue())
                .tooltipVisible(true)
                .build()
        );

        box.getChildren().addAll(
            basicSlider,
            disabledSlider,
            marksSlider,
            rangeSlider,
            tooltipSlider
        );
        return box;
    }

    private VBox createProgressDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0.7);
        javafx.scene.control.ProgressIndicator indicator = new javafx.scene.control.ProgressIndicator(0.7);
        box.getChildren().addAll(
            new Label("Progress Bar (70%)"),
            progressBar,
            new Label("Progress Indicator"),
            indicator
        );
        return box;
    }

    private VBox createAlertDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        // 基础 Alert
        VBox basicAlerts = new VBox(8);
        basicAlerts.getChildren().addAll(
            AlertAnt.success("Success", "Operation completed successfully!").build(),
            AlertAnt.info("Info", "This is an informational message.").build(),
            AlertAnt.warning("Warning", "Please check your input.").build(),
            AlertAnt.error("Error", "Something went wrong.").build()
        );

        // Action Alert - 对标 Ant Design action 属性
        VBox actionAlert = AlertAnt.warning("Warning", "Please confirm before proceeding.")
            .action(ButtonAnt.create("Undo").type(ButtonAnt.Type.TEXT).build())
            .build();

        // Banner Alert - 对标 Ant Design banner 属性
        VBox bannerAlert = AlertAnt.warning("Banner Warning", "This is a banner-style alert at the top.")
            .banner()
            .closable(true)
            .build();

        box.getChildren().addAll(
            new Label("Basic Alerts") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            basicAlerts,
            new Label("Alert with Action") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            actionAlert,
            new Label("Banner Alert") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            bannerAlert
        );
        return box;
    }

    private VBox createCheckBoxDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new javafx.scene.control.CheckBox("Basic Checkbox"),
            new javafx.scene.control.CheckBox("Checked") {{ setSelected(true); }},
            new javafx.scene.control.CheckBox("Disabled") {{ setDisable(true); }}
        );
        return box;
    }

    private VBox createRadioButtonDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        box.getChildren().addAll(
            new javafx.scene.control.RadioButton("Option 1") {{ setToggleGroup(group); }},
            new javafx.scene.control.RadioButton("Option 2") {{ setToggleGroup(group); setSelected(true); }},
            new javafx.scene.control.RadioButton("Option 3") {{ setToggleGroup(group); }}
        );
        return box;
    }

    private VBox createComboBoxDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ComboBox<String> combo = new javafx.scene.control.ComboBox<>();
        combo.getItems().addAll("Option 1", "Option 2", "Option 3");
        combo.setPromptText("Select an option");
        box.getChildren().add(combo);
        return box;
    }

    private VBox createListViewDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ListView<String> list = new javafx.scene.control.ListView<>();
        list.getItems().addAll("Item 1", "Item 2", "Item 3", "Item 4", "Item 5");
        box.getChildren().add(list);
        return box;
    }

    private VBox createTextAreaDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
        textArea.setPromptText("Enter text here...");
        textArea.setPrefRowCount(4);
        box.getChildren().add(textArea);
        return box;
    }

    private VBox createSpinnerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.Spinner<Integer> spinner = new javafx.scene.control.Spinner<>(0, 100, 50);
        box.getChildren().addAll(
            new Label("Number Spinner (0-100)"),
            spinner
        );
        return box;
    }

    private VBox createBadgeDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Badge Demo - See Button examples with badges")
        );
        return box;
    }

    private VBox createDividerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Above divider"),
            new javafx.scene.control.Separator(),
            new Label("Below divider")
        );
        return box;
    }

    private VBox createTooltipDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        Button btn = ButtonAnt.create("Hover me").build();
        TooltipAnt.create("This is a tooltip!").install(btn);
        box.getChildren().add(btn);
        return box;
    }

    private VBox createSwitchDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ToggleButton toggle = new javafx.scene.control.ToggleButton("Switch");
        toggle.getStyleClass().add("switch");
        box.getChildren().addAll(
            new Label("Toggle Switch"),
            toggle
        );
        return box;
    }

    private VBox createPaginationDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.Pagination pagination = new javafx.scene.control.Pagination(10, 0);
        box.getChildren().add(pagination);
        return box;
    }

    private VBox createAccordionDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.Accordion accordion = new javafx.scene.control.Accordion();
        accordion.getPanes().addAll(
            new TitledPane("Panel 1", new Label("Content 1")),
            new TitledPane("Panel 2", new Label("Content 2")),
            new TitledPane("Panel 3", new Label("Content 3"))
        );
        box.getChildren().add(accordion);
        return box;
    }

    private VBox createColorPickerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        javafx.scene.control.ColorPicker picker = new javafx.scene.control.ColorPicker();
        box.getChildren().add(picker);
        return box;
    }

    private VBox createTagDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            TagAnt.create("Tag 1").build(),
            TagAnt.create("Tag 2").type(TagAnt.Type.ERROR).build(),
            TagAnt.create("Tag 3").type(TagAnt.Type.SUCCESS).build(),
            TagAnt.create("Closable").closable(true).build()
        );
        return box;
    }

    private VBox createAvatarDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            AvatarAnt.create("John Doe").size(AvatarAnt.Size.LARGE).build(),
            AvatarAnt.create("Jane Smith").size(AvatarAnt.Size.DEFAULT).build(),
            AvatarAnt.create("Bob").size(AvatarAnt.Size.SMALL).build()
        );
        return box;
    }

    private VBox createRateDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            RateAnt.create().value(3.5).build(),
            RateAnt.create().value(4).allowHalf(false).build()
        );
        return box;
    }

    private VBox createEmptyDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(EmptyAnt.create()
            .description("No data available")
            .extra(ButtonAnt.create("Create Now").type(ButtonAnt.Type.PRIMARY).build())
            .build());
        return box;
    }

    private VBox createStepsDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            StepsAnt.create()
                .step("Step 1", "Description 1")
                .step("Step 2", "Description 2")
                .step("Step 3", "Description 3")
                .current(1)
                .build()
        );
        return box;
    }

    private VBox createSkeletonDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            SkeletonAnt.create().width(200).height(16).build(),
            SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR).width(40).height(40).build(),
            SkeletonAnt.paragraph(3, 250, 14)
        );
        return box;
    }

    private VBox createResultDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            ResultAnt.success("Success", "Operation completed!").build(),
            ResultAnt.error("Error", "Something went wrong.").build()
        );
        return box;
    }

    private VBox createDescriptionsDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(DescriptionsAnt.create()
            .title("User Info")
            .item("Name", "John Doe")
            .item("Email", "john@example.com")
            .item("Phone", "+1 234 567 890")
            .build());
        return box;
    }

    private VBox createTimelineDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(TimelineAnt.create()
            .item("Create a services site", "2015-09-01", TimelineAnt.DotColor.BLUE)
            .item("Solve initial network problems", "2015-09-02", TimelineAnt.DotColor.BLUE)
            .item("Technical testing", "2015-09-03", TimelineAnt.DotColor.GREEN)
            .build());
        return box;
    }

    private VBox createCarouselDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(CarouselAnt.create()
            .items(
                createCarouselItem("Slide 1", "-color-accent-subtle"),
                createCarouselItem("Slide 2", "-color-success-subtle"),
                createCarouselItem("Slide 3", "-color-warning-subtle")
            )
            .build());
        return box;
    }

    private Node createCarouselItem(String text, String bgColor) {
        VBox item = new VBox(new Label(text));
        item.setAlignment(Pos.CENTER);
        item.setStyle("-fx-background-color: " + bgColor + "; -fx-min-height: 200px; -fx-background-radius: 8px;");
        item.setPrefWidth(600);
        return item;
    }

    private VBox createCalendarDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(CalendarAnt.create().build());
        return box;
    }

    private VBox createUploadDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            UploadAnt.create().buttonText("Upload File").multiple().build(),
            UploadAnt.create().type(UploadAnt.Type.DRAG).build()
        );
        return box;
    }

    private VBox createTransferDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(TransferAnt.<String>create()
            .dataSource(Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"))
            .targetKeys(Arrays.asList("Item 1", "Item 3"))
            .titles("Source", "Target")
            .build());
        return box;
    }

    private VBox createBreadcrumbDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            BreadcrumbAnt.create().items("Home", "Application", "Detail").build(),
            BreadcrumbAnt.create().separator(">").item("Home", item -> {}).item("Products", item -> {}).item("Detail").build()
        );
        return box;
    }

    private VBox createDrawerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        // 基础 Drawer
        Button btn1 = ButtonAnt.create("Open Basic Drawer").type(ButtonAnt.Type.PRIMARY).build();
        DrawerAnt.Drawer drawer1 = DrawerAnt.create()
            .title("Basic Drawer")
            .content(new Label("Drawer content"))
            .build();
        btn1.setOnAction(e -> drawer1.open(btn1));

        // Large Drawer + extra 操作区 - 对标 Ant Design size/extra
        Button btn2 = ButtonAnt.create("Open Large Drawer with Extra").type(ButtonAnt.Type.PRIMARY).build();
        DrawerAnt.Drawer drawer2 = DrawerAnt.create()
            .title("Large Drawer")
            .content(new Label("This is a large drawer with extra action area.\nClose button is now on the left (Ant Design style)."))
            .size(DrawerAnt.Size.LARGE)
            .extra(ButtonAnt.create("Extra Action").type(ButtonAnt.Type.PRIMARY).size(ButtonAnt.Size.SMALL).build())
            .build();
        btn2.setOnAction(e -> drawer2.open(btn2));

        box.getChildren().addAll(btn1, btn2);
        return box;
    }

    private VBox createMessageDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(new HBox(8,
            ButtonAnt.create("Success").type(ButtonAnt.Type.PRIMARY).onClick(e -> MessageAnt.success("Success message")).build(),
            ButtonAnt.create("Error").onClick(e -> MessageAnt.error("Error message")).build(),
            ButtonAnt.create("Warning").onClick(e -> MessageAnt.warning("Warning message")).build(),
            ButtonAnt.create("Info").onClick(e -> MessageAnt.info("Info message")).build()
        ));
        return box;
    }

    private VBox createNotificationDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(new HBox(8,
            ButtonAnt.create("Success").type(ButtonAnt.Type.PRIMARY).onClick(e -> NotificationAnt.success("Success", "Notification message")).build(),
            ButtonAnt.create("Error").onClick(e -> NotificationAnt.error("Error", "Notification message")).build(),
            ButtonAnt.create("Warning").onClick(e -> NotificationAnt.warning("Warning", "Notification message")).build(),
            ButtonAnt.create("Info").onClick(e -> NotificationAnt.info("Info", "Notification message")).build()
        ));
        return box;
    }

    private VBox createPopconfirmDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        Button btn = ButtonAnt.create("Delete").type(ButtonAnt.Type.PRIMARY).build();
        PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
            .title("Are you sure?")
            .description("This action cannot be undone.")
            .target(btn)
            .build();
        btn.setOnAction(e -> popconfirm.show());
        box.getChildren().add(btn);
        return box;
    }

    private VBox createPopoverDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        Button btn = ButtonAnt.create("Hover Me").type(ButtonAnt.Type.PRIMARY).build();
        PopoverAnt.Popover popover = PopoverAnt.create()
            .title("Popover Title")
            .content(new Label("Popover content"))
            .target(btn)
            .build();
        btn.setOnAction(e -> popover.show());
        box.getChildren().add(btn);
        return box;
    }

    private VBox createAnchorDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Anchor Navigation") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            AnchorAnt.create()
                .item("basic", "Basic", "#basic")
                .item("static", "Static", "#static")
                .item("api", "API", "#api")
                .activeKey("basic")
                .onChange(key -> System.out.println("Anchor: " + key))
                .build()
        );
        return box;
    }

    private VBox createSegmentedDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Segmented Control") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            SegmentedAnt.create()
                .option("daily", "Daily")
                .option("weekly", "Weekly")
                .option("monthly", "Monthly")
                .selected("daily")
                .onChange(val -> System.out.println("Segmented: " + val))
                .build(),
            SegmentedAnt.create()
                .option("list", "List")
                .option("grid", "Grid")
                .option("card", "Card")
                .selected("list")
                .block(true)
                .build()
        );
        return box;
    }

    private VBox createStatisticDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Statistic") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            new HBox(32) {{
                getChildren().addAll(
                    StatisticAnt.create().title("Active Users").value(112893).build(),
                    StatisticAnt.create().title("Total Sales").value(112893).prefix("$").build(),
                    StatisticAnt.create().title("Growth").value(12.5).suffix("%").valueColor(javafx.scene.paint.Color.GREEN).build()
                );
            }}
        );
        return box;
    }

    private VBox createSpinDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Spin Loading") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            new HBox(32) {{
                setAlignment(Pos.CENTER_LEFT);
                getChildren().addAll(
                    SpinAnt.create().indicator(SpinAnt.Indicator.SPINNER).tip("Loading...").build(),
                    SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).size(SpinAnt.Size.SMALL).build(),
                    SpinAnt.create().indicator(SpinAnt.Indicator.BARS).size(SpinAnt.Size.LARGE).build()
                );
            }}
        );
        return box;
    }

    private VBox createCollapseDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Collapse Panel") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            CollapseAnt.create()
                .panel("1", "This is panel header 1", new Label("Panel content 1"))
                .panel("2", "This is panel header 2", new Label("Panel content 2"))
                .panel("3", "This is panel header 3", new Label("Panel content 3"), true)
                .activeKey("1")
                .build()
        );
        return box;
    }

    private VBox createDropdownDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        Button triggerBtn = ButtonAnt.create("Click me").type(ButtonAnt.Type.PRIMARY).build();
        DropdownAnt.create()
            .trigger(triggerBtn)
            .item("1", "Menu Item 1")
            .item("2", "Menu Item 2")
            .divider()
            .item("3", "Danger Item", true)
            .onSelect(key -> System.out.println("Dropdown: " + key))
            .build();
        box.getChildren().addAll(
            new Label("Dropdown Menu") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            triggerBtn
        );
        return box;
    }

    private VBox createImageDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Image") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            new HBox(16) {{
                getChildren().addAll(
                    ImageAnt.create().width(120).height(120).alt("Placeholder").borderRadius(8).build(),
                    ImageAnt.create().width(120).height(120).alt("Fallback").fallback("Image Error").borderRadius(60).build()
                );
            }}
        );
        return box;
    }

    private VBox createCascaderDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        java.util.List<CascaderAnt.Option> options = java.util.Arrays.asList(
            new CascaderAnt.Option("zhejiang", "Zhejiang", java.util.Arrays.asList(
                new CascaderAnt.Option("hangzhou", "Hangzhou", java.util.Arrays.asList(
                    new CascaderAnt.Option("xihu", "West Lake"),
                    new CascaderAnt.Option("xiasha", "Xiasha")
                )),
                new CascaderAnt.Option("ningbo", "Ningbo", java.util.Arrays.asList(
                    new CascaderAnt.Option("jiangbei", "Jiangbei"),
                    new CascaderAnt.Option("yinzhou", "Yinzhou")
                ))
            )),
            new CascaderAnt.Option("jiangsu", "Jiangsu", java.util.Arrays.asList(
                new CascaderAnt.Option("nanjing", "Nanjing", java.util.Arrays.asList(
                    new CascaderAnt.Option("xuanwu", "Xuanwu"),
                    new CascaderAnt.Option("qinhuai", "Qinhuai")
                )),
                new CascaderAnt.Option("suzhou", "Suzhou", java.util.Arrays.asList(
                    new CascaderAnt.Option("gusu", "Gusu"),
                    new CascaderAnt.Option("wuzhong", "Wuzhong")
                ))
            ))
        );

        box.getChildren().addAll(
            new Label("Cascader") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            CascaderAnt.create()
                .placeholder("Please select")
                .options(options)
                .onChange(path -> System.out.println("Cascader: " + String.join(" / ", path)))
                .build()
        );
        return box;
    }

    private VBox createInputNumberDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Input Number") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            InputNumberAnt.create().value(10).step(1).onChange(v -> System.out.println("Number: " + v)).build(),
            InputNumberAnt.create().value(99.99).precision(2).prefix("$").build(),
            InputNumberAnt.create().value(50).min(0).max(100).suffix("%").size(InputNumberAnt.Size.SMALL).build()
        );
        return box;
    }

    private VBox createAutoCompleteDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        List<String> options = java.util.Arrays.asList("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew");
        box.getChildren().addAll(
            new Label("Auto Complete") {{ setStyle("-fx-font-size: 18px; -fx-font-weight: 600;"); }},
            AutoCompleteAnt.<String>create()
                .placeholder("Type a fruit name...")
                .options(options)
                .onSelect(item -> System.out.println("Selected: " + item))
                .build()
        );
        return box;
    }

    private VBox createBackTopDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().add(new Label("BackTop - Scroll down to see the button"));
        return box;
    }

    private VBox createTimePickerDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Full Time (HH:mm:ss)") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TimePickerAnt.create().format("HH:mm:ss").build(),
            new Label("Hour:Minute Only") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TimePickerAnt.create().format("HH:mm").build()
        );
        return box;
    }

    private VBox createTreeSelectDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        TreeSelectAnt.TreeNode root = new TreeSelectAnt.TreeNode("root", "All Categories",
            Arrays.asList(
                new TreeSelectAnt.TreeNode("electronics", "Electronics",
                    Arrays.asList(
                        new TreeSelectAnt.TreeNode("phones", "Phones"),
                        new TreeSelectAnt.TreeNode("laptops", "Laptops")
                    )),
                new TreeSelectAnt.TreeNode("clothing", "Clothing",
                    Arrays.asList(
                        new TreeSelectAnt.TreeNode("men", "Men"),
                        new TreeSelectAnt.TreeNode("women", "Women")
                    )),
                new TreeSelectAnt.TreeNode("books", "Books")
            ));

        box.getChildren().addAll(
            new Label("Tree Select") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TreeSelectAnt.create()
                .placeholder("Select a category")
                .tree(root)
                .onSelect(node -> System.out.println("Selected: " + node.getValue()))
                .build()
        );
        return box;
    }

    private VBox createTypographyDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Typography") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            TypographyAnt.title("H1 Title", 1).build(),
            TypographyAnt.title("H2 Title", 2).build(),
            TypographyAnt.title("H3 Title", 3).build(),
            TypographyAnt.paragraph("This is a paragraph with normal text content that demonstrates the paragraph style in the typography system.").build(),
            new HBox(12) {{
                setAlignment(Pos.CENTER_LEFT);
                getChildren().addAll(
                    TypographyAnt.text("Primary").type(TypographyAnt.Type.PRIMARY).build(),
                    TypographyAnt.text("Secondary").type(TypographyAnt.Type.SECONDARY).build(),
                    TypographyAnt.text("Success").type(TypographyAnt.Type.SUCCESS).build(),
                    TypographyAnt.text("Warning").type(TypographyAnt.Type.WARNING).build(),
                    TypographyAnt.text("Danger").type(TypographyAnt.Type.DANGER).build()
                );
            }},
            new HBox(12) {{
                setAlignment(Pos.CENTER_LEFT);
                getChildren().addAll(
                    TypographyAnt.text("Strong").strong().build(),
                    TypographyAnt.text("Italic").italic().build(),
                    TypographyAnt.text("Code").code().build(),
                    TypographyAnt.text("Mark").mark().build()
                );
            }}
        );
        return box;
    }

    private VBox createFloatButtonDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Float Button") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            new HBox(16) {{
                setAlignment(Pos.CENTER_LEFT);
                getChildren().addAll(
                    FloatButtonAnt.create()
                        .icon(new Label("+") {{ setStyle("-fx-font-size: 24px; -fx-text-fill: inherit;"); }})
                        .type(FloatButtonAnt.Type.PRIMARY)
                        .tooltip("Add new")
                        .build(),
                    FloatButtonAnt.create()
                        .icon(new Label("↑") {{ setStyle("-fx-font-size: 20px; -fx-text-fill: inherit;"); }})
                        .type(FloatButtonAnt.Type.DEFAULT)
                        .tooltip("Back to top")
                        .build()
                );
            }}
        );
        return box;
    }

    private VBox createListDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        Node avatar1 = AvatarAnt.create("John").size(AvatarAnt.Size.SMALL).build();
        Node avatar2 = AvatarAnt.create("Jane").size(AvatarAnt.Size.SMALL).build();
        Node avatar3 = AvatarAnt.create("Bob").size(AvatarAnt.Size.SMALL).build();

        box.getChildren().addAll(
            new Label("Basic List") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            ListAnt.create()
                .item("List item 1", "Description for item 1")
                .item("List item 2", "Description for item 2")
                .item("List item 3", "Description for item 3")
                .bordered(true)
                .build(),
            new Label("List with Avatars") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            ListAnt.create()
                .item(avatar1, "John Doe", "Software Engineer", ButtonAnt.create("Edit").type(ButtonAnt.Type.TEXT).build())
                .item(avatar2, "Jane Smith", "Product Designer", ButtonAnt.create("Edit").type(ButtonAnt.Type.TEXT).build())
                .item(avatar3, "Bob Johnson", "DevOps Engineer", ButtonAnt.create("Edit").type(ButtonAnt.Type.TEXT).build())
                .bordered(true)
                .build()
        );
        return box;
    }

    // Person class for Table demo
    public static class Person {
        private final String name;
        private final int age;
        private final String email;

        public Person(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }
    }

    private VBox createQRCodeDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Basic QRCode") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            QRCodeAnt.create()
                .value("https://github.com/openkawu/JFXium")
                .size(160)
                .build(),
            new Label("Custom Color") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            QRCodeAnt.create()
                .value("Hello JFXium")
                .size(120)
                .color(javafx.scene.paint.Color.web("#1677ff"))
                .bgColor(javafx.scene.paint.Color.web("#f0f5ff"))
                .build()
        );
        return box;
    }

    private VBox createMentionsDemo() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.getChildren().addAll(
            new Label("Mentions - Type @ to trigger") {{ setStyle("-fx-font-size: 14px; -fx-font-weight: 600;"); }},
            MentionsAnt.create()
                .placeholder("Type @ to mention someone...")
                .option("john", "John Doe")
                .option("jane", "Jane Smith")
                .option("bob", "Bob Wilson")
                .option("alice", "Alice Brown")
                .onSelect(user -> System.out.println("Selected: " + user))
                .build()
        );
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
