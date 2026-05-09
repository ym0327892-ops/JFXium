package org.openkawu.jfxium.playground;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.openkawu.jfxium.component.*;
import org.openkawu.jfxium.core.animation.JFXAnimation;
import org.openkawu.jfxium.core.layout.Layouts;
import org.openkawu.jfxium.core.theme.ThemeManager;

import java.util.Arrays;
import java.util.List;

/**
 * JFXium Playground - A demo application for the JFXium framework
 * Inspired by AtlantaFX
 */
public class JFXiumPlayground extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        // Initialize theme - AtlantaFX style
        ThemeManager.getInstance().applyTheme(new org.openkawu.jfxium.core.theme.LightTheme());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
            new Tab("Button", createButtonDemo()),
            new Tab("Input", createInputDemo()),
            new Tab("Card", createCardDemo()),
            new Tab("Modal", createModalDemo()),
            new Tab("Animation", createAnimationDemo()),
            new Tab("Table", createTableDemo()),
            new Tab("Form", createFormDemo()),
            new Tab("Layout", createLayoutDemo()),
            new Tab("Menu", createMenuDemo()),
            new Tab("Tabs", createTabsDemo()),
            new Tab("Tree", createTreeDemo()),
            new Tab("DatePicker", createDatePickerDemo()),
            new Tab("Slider", createSliderDemo()),
            new Tab("Progress", createProgressDemo()),
            new Tab("Alert", createAlertDemo()),
            new Tab("CheckBox", createCheckBoxDemo()),
            new Tab("RadioButton", createRadioButtonDemo()),
            new Tab("ComboBox", createComboBoxDemo()),
            new Tab("ListView", createListViewDemo()),
            new Tab("TextArea", createTextAreaDemo()),
            new Tab("Spinner", createSpinnerDemo()),
            new Tab("Badge", createBadgeDemo()),
            new Tab("Divider", createDividerDemo()),
            new Tab("Tooltip", createTooltipDemo()),
            new Tab("Switch", createSwitchDemo()),
            new Tab("Pagination", createPaginationDemo()),
            new Tab("Accordion", createAccordionDemo()),
            new Tab("ColorPicker", createColorPickerDemo()),
            new Tab("Tag", createTagDemo()),
            new Tab("Avatar", createAvatarDemo()),
            new Tab("Rate", createRateDemo()),
            new Tab("Empty", createEmptyDemo()),
            new Tab("Steps", createStepsDemo()),
            new Tab("Skeleton", createSkeletonDemo()),
            new Tab("Examples", createRealWorldExamples())
        );

        Button themeToggle = JFXButton.create("Toggle Dark Mode")
            .type(JFXButton.Type.PRIMARY)
            .build();
        themeToggle.setOnAction(e -> ThemeManager.getInstance().toggleTheme());

        VBox root = new VBox(10, themeToggle, tabPane);
        root.setPadding(new Insets(16));
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("JFXium Playground - AtlantaFX Style");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createButtonDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Button types
        HBox types = new HBox(12);
        types.setAlignment(Pos.CENTER_LEFT);
        types.getChildren().addAll(
            JFXButton.create("Default").build(),
            JFXButton.create("Primary").type(JFXButton.Type.PRIMARY).build(),
            JFXButton.create("Outlined").type(JFXButton.Type.OUTLINED).build(),
            JFXButton.create("Dashed").type(JFXButton.Type.DASHED).build(),
            JFXButton.create("Text").type(JFXButton.Type.TEXT).build(),
            JFXButton.create("Link").type(JFXButton.Type.LINK).build()
        );

        // Button sizes
        HBox sizes = new HBox(12);
        sizes.setAlignment(Pos.CENTER_LEFT);
        sizes.getChildren().addAll(
            JFXButton.create("Small").size(JFXButton.Size.SMALL).build(),
            JFXButton.create("Default").build(),
            JFXButton.create("Large").size(JFXButton.Size.LARGE).build()
        );

        // Button shapes
        HBox shapes = new HBox(12);
        shapes.setAlignment(Pos.CENTER_LEFT);
        shapes.getChildren().addAll(
            JFXButton.create("Square").square().build(),
            JFXButton.create("Default").build(),
            JFXButton.create("Rounded").rounded().build()
        );

        box.getChildren().addAll(
            new Label("Button Types") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            types,
            new Label("Button Sizes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            sizes,
            new Label("Button Shapes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            shapes
        );

        return box;
    }

    // ==================== CheckBox Demo ====================
    private VBox createCheckBoxDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.CheckBox basicCheckBox = JFXCheckBox.create("Basic CheckBox")
            .onChange(checked -> System.out.println("Checked: " + checked))
            .build();

        javafx.scene.control.CheckBox selectedCheckBox = JFXCheckBox.create("Selected CheckBox")
            .selected(true)
            .build();

        javafx.scene.control.CheckBox disabledCheckBox = JFXCheckBox.create("Disabled CheckBox")
            .disabled(true)
            .build();

        javafx.scene.control.CheckBox indeterminateCheckBox = JFXCheckBox.create("Indeterminate CheckBox")
            .indeterminate(true)
            .build();

        box.getChildren().addAll(
            new Label("CheckBoxes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicCheckBox,
            selectedCheckBox,
            disabledCheckBox,
            indeterminateCheckBox
        );

        return box;
    }

    // ==================== RadioButton Demo ====================
    private VBox createRadioButtonDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();

        javafx.scene.control.RadioButton option1 = JFXRadioButton.create("Option 1")
            .toggleGroup(group)
            .selected(true)
            .build();

        javafx.scene.control.RadioButton option2 = JFXRadioButton.create("Option 2")
            .toggleGroup(group)
            .build();

        javafx.scene.control.RadioButton option3 = JFXRadioButton.create("Option 3")
            .toggleGroup(group)
            .disabled(true)
            .build();

        box.getChildren().addAll(
            new Label("RadioButtons") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            option1,
            option2,
            option3
        );

        return box;
    }

    // ==================== ComboBox Demo ====================
    private VBox createComboBoxDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ComboBox<String> basicComboBox = JFXComboBox.<String>create()
            .items("Option 1", "Option 2", "Option 3")
            .placeholder("Select an option")
            .onChange(value -> System.out.println("Selected: " + value))
            .build();

        javafx.scene.control.ComboBox<String> disabledComboBox = JFXComboBox.<String>create()
            .items("Option A", "Option B", "Option C")
            .value("Option A")
            .disabled(true)
            .build();

        box.getChildren().addAll(
            new Label("ComboBoxes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicComboBox,
            disabledComboBox
        );

        return box;
    }

    // ==================== ListView Demo ====================
    private VBox createListViewDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ListView<String> basicListView = JFXListView.<String>create()
            .items("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
            .onSelect(item -> System.out.println("Selected: " + item))
            .build();

        box.getChildren().addAll(
            new Label("ListViews") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicListView
        );

        return box;
    }

    // ==================== TextArea Demo ====================
    private VBox createTextAreaDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.TextArea basicTextArea = JFXTextArea.create()
            .placeholder("Enter description...")
            .rows(4)
            .onChange(text -> System.out.println("Text: " + text))
            .build();

        javafx.scene.control.TextArea disabledTextArea = JFXTextArea.create()
            .text("This is a disabled text area.")
            .disabled(true)
            .build();

        box.getChildren().addAll(
            new Label("TextAreas") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicTextArea,
            disabledTextArea
        );

        return box;
    }

    // ==================== Spinner Demo ====================
    private VBox createSpinnerDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ProgressIndicator smallSpinner = JFXSpinner.create()
            .size(24)
            .build();

        javafx.scene.control.ProgressIndicator defaultSpinner = JFXSpinner.create()
            .build();

        javafx.scene.control.ProgressIndicator largeSpinner = JFXSpinner.create()
            .size(48)
            .build();

        HBox spinnersBox = new HBox(24, smallSpinner, defaultSpinner, largeSpinner);
        spinnersBox.setAlignment(Pos.CENTER);

        box.getChildren().addAll(
            new Label("Spinners") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            spinnersBox
        );

        return box;
    }

    // ==================== Badge Demo ====================
    private VBox createBadgeDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.StackPane countBadge = JFXBadge.create()
            .content(JFXButton.create("Messages").type(JFXButton.Type.DEFAULT).build())
            .count(5)
            .build();

        javafx.scene.layout.StackPane dotBadge = JFXBadge.create()
            .content(JFXButton.create("Notifications").type(JFXButton.Type.DEFAULT).build())
            .dot(true)
            .build();

        javafx.scene.layout.StackPane statusBadge = JFXBadge.create()
            .content(new Label("Online"))
            .status(JFXBadge.Status.SUCCESS)
            .build();

        HBox badgesBox = new HBox(24, countBadge, dotBadge, statusBadge);
        badgesBox.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(
            new Label("Badges") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            badgesBox
        );

        return box;
    }

    // ==================== Divider Demo ====================
    private VBox createDividerDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.Separator horizontalDivider = JFXDivider.create().build();

        javafx.scene.control.Separator verticalDivider = JFXDivider.create()
            .vertical()
            .build();

        HBox verticalBox = new HBox(16, new Label("Left"), verticalDivider, new Label("Right"));
        verticalBox.setAlignment(Pos.CENTER_LEFT);
        verticalBox.setPrefHeight(40);

        box.getChildren().addAll(
            new Label("Dividers") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            new Label("Above horizontal divider"),
            horizontalDivider,
            new Label("Below horizontal divider"),
            verticalBox
        );

        return box;
    }

    private VBox createInputDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Input sizes
        HBox sizes = new HBox(12);
        sizes.setAlignment(Pos.CENTER_LEFT);
        sizes.getChildren().addAll(
            JFXInput.create().placeholder("Small input").size(JFXInput.Size.SMALL).build(),
            JFXInput.create().placeholder("Default input").build(),
            JFXInput.create().placeholder("Large input").size(JFXInput.Size.LARGE).build()
        );

        // Disabled
        TextField disabled = JFXInput.create().placeholder("Disabled input").disabled(true).build();

        box.getChildren().addAll(
            new Label("Input Sizes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            sizes,
            new Label("Disabled Input") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            new HBox(12, disabled)
        );

        return box;
    }

    private VBox createCardDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        HBox cards = new HBox(16);
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.getChildren().addAll(
            JFXCard.create()
                .title("Default Card")
                .content(new Label("This is a simple card with just a title and content."))
                .build(),
            JFXCard.create()
                .title("Bordered Card")
                .content(new Label("This card has a visible border."))
                .bordered(true)
                .build(),
            JFXCard.create()
                .title("Shadow Card")
                .content(new Label("This card has a drop shadow for elevation."))
                .shadow(JFXCard.Shadow.MEDIUM)
                .build(),
            JFXCard.create()
                .title("Hoverable Card")
                .content(new Label("Hover over this card to see animation."))
                .shadow(JFXCard.Shadow.SMALL)
                .hoverable(true)
                .build()
        );

        box.getChildren().addAll(
            new Label("Card Variants") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            cards
        );

        return box;
    }

    private VBox createModalDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Basic modal
        Button basicModalBtn = JFXButton.create("Open Basic Modal")
            .type(JFXButton.Type.PRIMARY)
            .onClick(e -> {
                JFXModal.create()
                    .title("Basic Modal")
                    .content("This is a basic modal dialog with title and content.")
                    .owner(stage)
                    .show();
            })
            .build();

        // Confirm modal
        Button confirmBtn = JFXButton.create("Open Confirm Modal")
            .type(JFXButton.Type.OUTLINED)
            .onClick(e -> {
                JFXModal.create()
                    .title("确认删除")
                    .content("确定要删除这条记录吗？删除后无法恢复。")
                    .width(400)
                    .okText("确认")
                    .cancelText("取消")
                    .onOk(() -> System.out.println("Confirmed!"))
                    .onCancel(() -> System.out.println("Cancelled!"))
                    .owner(stage)
                    .show();
            })
            .build();

        // Info modal (no cancel)
        Button infoBtn = JFXButton.create("Open Info Modal")
            .type(JFXButton.Type.TEXT)
            .onClick(e -> {
                JFXModal.create()
                    .title("Information")
                    .content("This is an information modal without cancel button.")
                    .showCancel(false)
                    .okText("知道了")
                    .owner(stage)
                    .show();
            })
            .build();

        box.getChildren().addAll(
            new Label("Modal Dialogs") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            new HBox(12, basicModalBtn, confirmBtn, infoBtn)
        );

        return box;
    }

    private VBox createAnimationDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Animation demo boxes
        Label fadeBox = new Label("Fade In/Out");
        fadeBox.setStyle("-fx-background-color: -color-accent-emphasis; -fx-text-fill: white; -fx-padding: 20px; -fx-background-radius: 8px;");
        fadeBox.setOpacity(0);

        Button fadeInBtn = JFXButton.create("Fade In")
            .type(JFXButton.Type.PRIMARY)
            .onClick(e -> JFXAnimation.fadeIn(fadeBox, Duration.millis(500)).play())
            .build();

        Button fadeOutBtn = JFXButton.create("Fade Out")
            .type(JFXButton.Type.OUTLINED)
            .onClick(e -> JFXAnimation.fadeOut(fadeBox, Duration.millis(500)).play())
            .build();

        Label slideBox = new Label("Slide In");
        slideBox.setStyle("-fx-background-color: -color-success-emphasis; -fx-text-fill: white; -fx-padding: 20px; -fx-background-radius: 8px;");

        Button slideUpBtn = JFXButton.create("Slide Up")
            .type(JFXButton.Type.PRIMARY)
            .onClick(e -> {
                slideBox.setTranslateY(50);
                JFXAnimation.slideInFromBottom(slideBox, Duration.millis(400)).play();
            })
            .build();

        Button slideLeftBtn = JFXButton.create("Slide Left")
            .type(JFXButton.Type.OUTLINED)
            .onClick(e -> {
                slideBox.setTranslateX(50);
                JFXAnimation.slideInFromRight(slideBox, Duration.millis(400)).play();
            })
            .build();

        Label popBox = new Label("Pop Effect");
        popBox.setStyle("-fx-background-color: -color-warning-emphasis; -fx-text-fill: white; -fx-padding: 20px; -fx-background-radius: 8px;");

        Button popInBtn = JFXButton.create("Pop In")
            .type(JFXButton.Type.PRIMARY)
            .onClick(e -> {
                popBox.setScaleX(0.5);
                popBox.setScaleY(0.5);
                popBox.setOpacity(0);
                JFXAnimation.popIn(popBox, Duration.millis(300)).play();
            })
            .build();

        Button popOutBtn = JFXButton.create("Pop Out")
            .type(JFXButton.Type.OUTLINED)
            .onClick(e -> JFXAnimation.popOut(popBox, Duration.millis(300)).play())
            .build();

        box.getChildren().addAll(
            new Label("Animation Effects") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            new HBox(12, fadeInBtn, fadeOutBtn, fadeBox),
            new HBox(12, slideUpBtn, slideLeftBtn, slideBox),
            new HBox(12, popInBtn, popOutBtn, popBox)
        );

        return box;
    }

    // ==================== Table Demo ====================
    private VBox createTableDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Sample data
        class Person {
            String name;
            int age;
            String email;
            boolean active;

            Person(String name, int age, String email, boolean active) {
                this.name = name; this.age = age; this.email = email; this.active = active;
            }
            String getName() { return name; }
            int getAge() { return age; }
            String getEmail() { return email; }
            boolean isActive() { return active; }
        }

        List<Person> people = Arrays.asList(
            new Person("张三", 28, "zhangsan@example.com", true),
            new Person("李四", 32, "lisi@example.com", false),
            new Person("王五", 24, "wangwu@example.com", true),
            new Person("赵六", 35, "zhaoliu@example.com", true)
        );

        // Basic table
        javafx.scene.control.TableView<Person> basicTable = JFXTable.<Person>create()
            .column("Name", Person::getName)
            .column("Email", Person::getEmail)
            .numberColumn("Age", Person::getAge)
            .data(people)
            .build();

        // Striped and bordered table with selection
        javafx.scene.control.TableView<Person> styledTable = JFXTable.<Person>create()
            .column("Name", Person::getName)
            .column("Email", Person::getEmail)
            .numberColumn("Age", Person::getAge)
            .booleanColumn("Active", Person::isActive)
            .nodeColumn("Action", person ->
                JFXButton.create("Edit").type(JFXButton.Type.TEXT).build()
            )
            .data(people)
            .striped(true)
            .bordered(true)
            .selectable(true)
            .build();

        box.getChildren().addAll(
            new Label("Basic Table") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicTable,
            new Label("Styled Table (Striped + Bordered + Selectable)") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            styledTable
        );

        return box;
    }

    // ==================== Form Demo ====================
    private VBox createFormDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Vertical layout form
        VBox verticalForm = JFXForm.create()
            .item("Username", JFXInput.create().placeholder("Enter username").build())
            .item("Email", JFXInput.create().placeholder("Enter email").build())
            .itemRequired("Password", JFXInput.create().placeholder("Enter password").build())
            .item("Bio", JFXInput.create().placeholder("Tell us about yourself").build(), "This will be displayed on your profile")
            .layout(JFXForm.Layout.VERTICAL)
            .submitText("Submit")
            .onSubmit(data -> System.out.println("Form submitted: " + data))
            .build();

        // Horizontal layout form
        VBox horizontalForm = JFXForm.create()
            .item("Username", JFXInput.create().placeholder("Enter username").build())
            .item("Email", JFXInput.create().placeholder("Enter email").build())
            .itemRequired("Password", JFXInput.create().placeholder("Enter password").build())
            .layout(JFXForm.Layout.HORIZONTAL)
            .labelWidth(100)
            .submitText("Register")
            .onSubmit(data -> System.out.println("Form submitted: " + data))
            .build();

        box.getChildren().addAll(
            new Label("Vertical Form") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            verticalForm,
            new Label("Horizontal Form") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            horizontalForm
        );

        return box;
    }

    // ==================== Layout DSL Demo ====================
    private VBox createLayoutDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // VBox layout
        VBox vboxDemo = Layouts.vbox()
            .spacing(8)
            .padding(16)
            .align(javafx.geometry.Pos.CENTER)
            .children(
                new Label("VBox Layout"),
                JFXButton.create("Button 1").type(JFXButton.Type.PRIMARY).build(),
                JFXButton.create("Button 2").type(JFXButton.Type.OUTLINED).build()
            )
            .build();
        vboxDemo.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");

        // HBox layout with grow
        HBox hboxDemo = Layouts.hbox()
            .spacing(12)
            .padding(16)
            .align(javafx.geometry.Pos.CENTER_LEFT)
            .children(
                new Label("HBox Layout"),
                Layouts.grow(),
                JFXButton.create("Right").type(JFXButton.Type.PRIMARY).build()
            )
            .build();
        hboxDemo.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");

        // Grid layout
        javafx.scene.layout.GridPane gridDemo = Layouts.grid()
            .cols(3)
            .gap(8)
            .padding(16)
            .children(
                JFXButton.create("1").build(),
                JFXButton.create("2").build(),
                JFXButton.create("3").build(),
                JFXButton.create("4").build(),
                JFXButton.create("5").build(),
                JFXButton.create("6").build()
            )
            .build();
        gridDemo.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");

        box.getChildren().addAll(
            new Label("VBox Layout") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            vboxDemo,
            new Label("HBox Layout (with grow spacer)") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            hboxDemo,
            new Label("Grid Layout (3 columns)") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            gridDemo
        );

        return box;
    }

    // ==================== Menu Demo ====================
    private VBox createMenuDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.MenuBar menuBar = JFXMenu.create()
            .menu("File",
                JFXMenu.item("New", e -> System.out.println("New file")),
                JFXMenu.item("Open", e -> System.out.println("Open file")),
                JFXMenu.separator(),
                JFXMenu.item("Exit", e -> System.out.println("Exit"))
            )
            .menu("Edit",
                JFXMenu.item("Cut", e -> System.out.println("Cut")),
                JFXMenu.item("Copy", e -> System.out.println("Copy")),
                JFXMenu.item("Paste", e -> System.out.println("Paste"))
            )
            .menu("Help",
                JFXMenu.item("About", e -> System.out.println("About"))
            )
            .build();

        box.getChildren().addAll(
            new Label("Menu Bar") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            menuBar
        );

        return box;
    }

    // ==================== Tabs Demo ====================
    private VBox createTabsDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.TabPane basicTabs = JFXTabs.create()
            .tab("Tab 1", new Label("Content of Tab 1"))
            .tab("Tab 2", new Label("Content of Tab 2"))
            .tab("Tab 3", new Label("Content of Tab 3"))
            .build();

        javafx.scene.control.TabPane closableTabs = JFXTabs.create()
            .tab("Closable 1", new Label("Content 1"))
            .tab("Closable 2", new Label("Content 2"))
            .closable(true)
            .build();

        box.getChildren().addAll(
            new Label("Basic Tabs") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicTabs,
            new Label("Closable Tabs") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            closableTabs
        );

        return box;
    }

    // ==================== Tree Demo ====================
    private VBox createTreeDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.TreeView<String> tree = JFXTree.<String>create()
            .root("Root",
                JFXTree.node("Folder 1",
                    JFXTree.leaf("File 1-1"),
                    JFXTree.leaf("File 1-2"),
                    JFXTree.node("Subfolder 1-3",
                        JFXTree.leaf("File 1-3-1"),
                        JFXTree.leaf("File 1-3-2")
                    )
                ),
                JFXTree.node("Folder 2",
                    JFXTree.leaf("File 2-1"),
                    JFXTree.leaf("File 2-2")
                ),
                JFXTree.leaf("File 3")
            )
            .onSelect(item -> System.out.println("Selected: " + item))
            .build();

        box.getChildren().addAll(
            new Label("Tree View") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            tree
        );

        return box;
    }

    // ==================== DatePicker Demo ====================
    private VBox createDatePickerDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.DatePicker basicDatePicker = JFXDatePicker.create()
            .placeholder("Select date")
            .onChange(date -> System.out.println("Selected: " + date))
            .build();

        javafx.scene.control.DatePicker disabledDatePicker = JFXDatePicker.create()
            .placeholder("Disabled")
            .editable(false)
            .build();

        box.getChildren().addAll(
            new Label("Basic DatePicker") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicDatePicker,
            new Label("Disabled DatePicker") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            disabledDatePicker
        );

        return box;
    }

    // ==================== Slider Demo ====================
    private VBox createSliderDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.Slider basicSlider = JFXSlider.create()
            .min(0)
            .max(100)
            .value(50)
            .onChange(value -> System.out.println("Slider value: " + value))
            .build();

        javafx.scene.control.Slider steppedSlider = JFXSlider.create()
            .min(0)
            .max(100)
            .value(30)
            .step(10)
            .showTickLabels(true)
            .showTickMarks(true)
            .build();

        box.getChildren().addAll(
            new Label("Basic Slider") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicSlider,
            new Label("Stepped Slider") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            steppedSlider
        );

        return box;
    }

    // ==================== Progress Demo ====================
    private VBox createProgressDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.HBox progressBar1 = JFXProgress.bar()
            .progress(0.25)
            .size(JFXProgress.Size.SMALL)
            .build();

        javafx.scene.layout.HBox progressBar2 = JFXProgress.bar()
            .progress(0.5)
            .size(JFXProgress.Size.DEFAULT)
            .status(JFXProgress.Status.SUCCESS)
            .build();

        javafx.scene.layout.HBox progressBar3 = JFXProgress.bar()
            .progress(0.75)
            .size(JFXProgress.Size.LARGE)
            .status(JFXProgress.Status.WARNING)
            .build();

        javafx.scene.layout.VBox progressCircle1 = JFXProgress.circle()
            .progress(0.6)
            .size(60)
            .build();

        javafx.scene.layout.VBox progressCircle2 = JFXProgress.circle()
            .progress(0.8)
            .size(80)
            .status(JFXProgress.Status.ERROR)
            .build();

        javafx.scene.layout.HBox circlesBox = new javafx.scene.layout.HBox(24, progressCircle1, progressCircle2);
        circlesBox.setAlignment(Pos.CENTER);

        box.getChildren().addAll(
            new Label("Progress Bars") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            progressBar1,
            progressBar2,
            progressBar3,
            new Label("Progress Circles") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            circlesBox
        );

        return box;
    }

    // ==================== Alert Demo ====================
    private VBox createAlertDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.VBox successAlert = JFXAlert.success("Success", "Operation completed successfully!")
            .build();

        javafx.scene.layout.VBox infoAlert = JFXAlert.info("Info", "This is an informational message.")
            .build();

        javafx.scene.layout.VBox warningAlert = JFXAlert.warning("Warning", "Please be careful with this action.")
            .build();

        javafx.scene.layout.VBox errorAlert = JFXAlert.error("Error", "Something went wrong. Please try again.")
            .closable(true)
            .onClose(() -> System.out.println("Alert closed"))
            .build();

        box.getChildren().addAll(
            new Label("Alerts") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            successAlert,
            infoAlert,
            warningAlert,
            errorAlert
        );

        return box;
    }

    // ==================== Real World Examples ====================
    private VBox createRealWorldExamples() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        // Login Form Example
        VBox loginExample = createLoginExample();

        // Dashboard Example
        VBox dashboardExample = createDashboardExample();

        // Settings Panel Example
        VBox settingsExample = createSettingsExample();

        box.getChildren().addAll(
            new Label("Real World Examples") {{ setStyle("-fx-font-size: 20px; -fx-font-weight: 600;"); }},
            loginExample,
            new javafx.scene.control.Separator(),
            dashboardExample,
            new javafx.scene.control.Separator(),
            settingsExample
        );

        return box;
    }

    private VBox createLoginExample() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");
        container.setMaxWidth(400);

        Label title = new Label("Login Example");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600;");

        VBox loginForm = Layouts.vbox()
            .spacing(12)
            .align(Pos.CENTER)
            .children(
                new Label("Welcome Back") {{ setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); }},
                new Label("Please sign in to continue") {{ setStyle("-fx-text-fill: -color-fg-muted;"); }},
                JFXInput.create().placeholder("Username").build(),
                JFXInput.create().placeholder("Password").build(),
                JFXButton.create("Sign In")
                    .type(JFXButton.Type.PRIMARY)
                    .size(JFXButton.Size.LARGE)
                    .rounded()
                    .onClick(e -> System.out.println("Login clicked"))
                    .build(),
                new Label("Forgot password?") {{ setStyle("-fx-text-fill: -color-accent-emphasis; -fx-cursor: hand;"); }}
            )
            .build();

        container.getChildren().addAll(title, loginForm);
        return container;
    }

    private VBox createDashboardExample() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");

        Label title = new Label("Dashboard Example");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600;");

        // Stats cards
        HBox statsRow = Layouts.hbox()
            .spacing(16)
            .children(
                createStatCard("Total Users", "1,234", "+12%", "-color-accent-emphasis"),
                createStatCard("Revenue", "$56,789", "+8%", "-color-success-emphasis"),
                createStatCard("Orders", "892", "+15%", "-color-warning-emphasis"),
                createStatCard("Pending", "23", "-5%", "-color-danger-emphasis")
            )
            .build();

        // Recent activity
        VBox activityCard = JFXCard.create()
            .title("Recent Activity")
            .content(new Label("Recent user activities will be displayed here."))
            .shadow(JFXCard.Shadow.MEDIUM)
            .build();

        container.getChildren().addAll(title, statsRow, activityCard);
        return container;
    }

    private VBox createStatCard(String title, String value, String change, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: -color-bg-default; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: -color-border-muted;");
        card.setPrefWidth(150);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-muted;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");

        Label changeLabel = new Label(change);
        changeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel, changeLabel);
        return card;
    }

    private VBox createSettingsExample() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px;");
        container.setMaxWidth(600);

        Label title = new Label("Settings Example");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600;");

        VBox settingsForm = JFXForm.create()
            .item("Username", JFXInput.create().placeholder("Enter username").build())
            .item("Email", JFXInput.create().placeholder("Enter email").build())
            .item("Notifications", JFXSlider.create().min(0).max(100).value(75).build())
            .item("Theme", JFXDatePicker.create().placeholder("Select theme date").build())
            .layout(JFXForm.Layout.VERTICAL)
            .submitText("Save Settings")
            .onSubmit(data -> {
                System.out.println("Settings saved: " + data);
                JFXAlert.success("Success", "Settings saved successfully!").build();
            })
            .build();

        container.getChildren().addAll(title, settingsForm);
        return container;
    }

    // ==================== Tooltip Demo ====================
    private VBox createTooltipDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        Button tooltipBtn = JFXButton.create("Hover me for tooltip").build();
        JFXTooltip.create("This is a tooltip message!")
            .delay(Duration.millis(100))
            .install(tooltipBtn);

        Button delayedTooltipBtn = JFXButton.create("Hover me (delayed)").build();
        JFXTooltip.create("This tooltip appears after 500ms")
            .delay(Duration.millis(500))
            .install(delayedTooltipBtn);

        box.getChildren().addAll(
            new Label("Tooltips") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            tooltipBtn,
            delayedTooltipBtn
        );

        return box;
    }

    // ==================== Switch Demo ====================
    private VBox createSwitchDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ToggleButton basicSwitch = JFXSwitch.create()
            .text("Basic Switch")
            .onChange(checked -> System.out.println("Switch: " + checked))
            .build();

        javafx.scene.control.ToggleButton selectedSwitch = JFXSwitch.create()
            .text("Selected Switch")
            .selected(true)
            .build();

        javafx.scene.control.ToggleButton disabledSwitch = JFXSwitch.create()
            .text("Disabled Switch")
            .disabled(true)
            .build();

        box.getChildren().addAll(
            new Label("Switches") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicSwitch,
            selectedSwitch,
            disabledSwitch
        );

        return box;
    }

    // ==================== Pagination Demo ====================
    private VBox createPaginationDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.Pagination basicPagination = JFXPagination.create()
            .pageCount(10)
            .currentPage(0)
            .onChange(page -> System.out.println("Page: " + page))
            .build();

        javafx.scene.control.Pagination limitedPagination = JFXPagination.create()
            .pageCount(20)
            .currentPage(5)
            .maxPageIndicatorCount(5)
            .build();

        box.getChildren().addAll(
            new Label("Pagination") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicPagination,
            limitedPagination
        );

        return box;
    }

    // ==================== Accordion Demo ====================
    private VBox createAccordionDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.Accordion accordion = JFXAccordion.create()
            .pane("Panel 1", new Label("Content of panel 1"))
            .pane("Panel 2", new Label("Content of panel 2"))
            .pane("Panel 3", new Label("Content of panel 3"))
            .build();

        box.getChildren().addAll(
            new Label("Accordion") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            accordion
        );

        return box;
    }

    // ==================== ColorPicker Demo ====================
    private VBox createColorPickerDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.control.ColorPicker basicColorPicker = JFXColorPicker.create()
            .value(javafx.scene.paint.Color.BLUE)
            .onChange(color -> System.out.println("Color: " + color))
            .build();

        javafx.scene.control.ColorPicker disabledColorPicker = JFXColorPicker.create()
            .value(javafx.scene.paint.Color.RED)
            .disabled(true)
            .build();

        box.getChildren().addAll(
            new Label("Color Pickers") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicColorPicker,
            disabledColorPicker
        );

        return box;
    }

    // ==================== Tag Demo ====================
    private VBox createTagDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        HBox types = new HBox(12);
        types.setAlignment(Pos.CENTER_LEFT);
        types.getChildren().addAll(
            JFXTag.create("Default").build(),
            JFXTag.create("Primary").type(JFXTag.Type.PRIMARY).build(),
            JFXTag.create("Success").type(JFXTag.Type.SUCCESS).build(),
            JFXTag.create("Warning").type(JFXTag.Type.WARNING).build(),
            JFXTag.create("Error").type(JFXTag.Type.ERROR).build()
        );

        HBox sizes = new HBox(12);
        sizes.setAlignment(Pos.CENTER_LEFT);
        sizes.getChildren().addAll(
            JFXTag.create("Small").size(JFXTag.Size.SMALL).build(),
            JFXTag.create("Default").build(),
            JFXTag.create("Large").size(JFXTag.Size.LARGE).build()
        );

        HBox shapes = new HBox(12);
        shapes.setAlignment(Pos.CENTER_LEFT);
        shapes.getChildren().addAll(
            JFXTag.create("Default").type(JFXTag.Type.PRIMARY).build(),
            JFXTag.create("Round").type(JFXTag.Type.PRIMARY).shape(JFXTag.Shape.ROUND).build(),
            JFXTag.create("Square").type(JFXTag.Type.PRIMARY).shape(JFXTag.Shape.SQUARE).build()
        );

        HBox closable = new HBox(12);
        closable.setAlignment(Pos.CENTER_LEFT);
        closable.getChildren().addAll(
            JFXTag.create("Closable").type(JFXTag.Type.PRIMARY).closable().build(),
            JFXTag.create("No Border").type(JFXTag.Type.SUCCESS).noBorder().build()
        );

        box.getChildren().addAll(
            new Label("Tag Types") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            types,
            new Label("Tag Sizes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            sizes,
            new Label("Tag Shapes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            shapes,
            new Label("Tag Variants") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            closable
        );

        return box;
    }

    // ==================== Avatar Demo ====================
    private VBox createAvatarDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        HBox sizes = new HBox(16);
        sizes.setAlignment(Pos.CENTER_LEFT);
        sizes.getChildren().addAll(
            JFXAvatar.create("S").size(JFXAvatar.Size.SMALL).build(),
            JFXAvatar.create("D").size(JFXAvatar.Size.DEFAULT).build(),
            JFXAvatar.create("L").size(JFXAvatar.Size.LARGE).build(),
            JFXAvatar.create("XL").size(JFXAvatar.Size.XL).build()
        );

        HBox shapes = new HBox(16);
        shapes.setAlignment(Pos.CENTER_LEFT);
        shapes.getChildren().addAll(
            JFXAvatar.create("C").shape(JFXAvatar.Shape.CIRCLE).build(),
            JFXAvatar.create("S").shape(JFXAvatar.Shape.SQUARE).build()
        );

        HBox colors = new HBox(16);
        colors.setAlignment(Pos.CENTER_LEFT);
        colors.getChildren().addAll(
            JFXAvatar.create("A").backgroundColor("-color-accent-emphasis").build(),
            JFXAvatar.create("S").backgroundColor("-color-success-emphasis").build(),
            JFXAvatar.create("W").backgroundColor("-color-warning-emphasis").build(),
            JFXAvatar.create("E").backgroundColor("-color-danger-emphasis").build()
        );

        box.getChildren().addAll(
            new Label("Avatar Sizes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            sizes,
            new Label("Avatar Shapes") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            shapes,
            new Label("Avatar Colors") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            colors
        );

        return box;
    }

    // ==================== Rate Demo ====================
    private VBox createRateDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.HBox basicRate = JFXRate.create()
            .value(3)
            .onChange(v -> System.out.println("Rate: " + v))
            .build();

        javafx.scene.layout.HBox halfRate = JFXRate.create()
            .value(2.5)
            .allowHalf()
            .onChange(v -> System.out.println("Half Rate: " + v))
            .build();

        javafx.scene.layout.HBox disabledRate = JFXRate.create()
            .value(4)
            .disabled()
            .build();

        javafx.scene.layout.HBox smallRate = JFXRate.create()
            .size(JFXRate.Size.SMALL)
            .value(3)
            .build();

        javafx.scene.layout.HBox largeRate = JFXRate.create()
            .size(JFXRate.Size.LARGE)
            .value(3)
            .build();

        box.getChildren().addAll(
            new Label("Basic Rate") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicRate,
            new Label("Half Rate") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            halfRate,
            new Label("Disabled Rate") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            disabledRate,
            new Label("Small Rate") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            smallRate,
            new Label("Large Rate") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            largeRate
        );

        return box;
    }

    // ==================== Empty Demo ====================
    private VBox createEmptyDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.VBox basicEmpty = JFXEmpty.create("No data available").build();

        javafx.scene.layout.VBox actionEmpty = JFXEmpty.create("No results found")
            .extraButton("Create New", () -> System.out.println("Create clicked"))
            .build();

        box.getChildren().addAll(
            new Label("Basic Empty") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            basicEmpty,
            new Label("Empty with Action") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            actionEmpty
        );

        return box;
    }

    // ==================== Steps Demo ====================
    private VBox createStepsDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.Node horizontalSteps = JFXSteps.create()
            .step("Cart", "Items in cart")
            .step("Shipping", "Enter address")
            .step("Payment", "Select method")
            .step("Confirm", "Review order")
            .current(1)
            .build();

        javafx.scene.Node verticalSteps = JFXSteps.create()
            .step("Step 1", "Description 1")
            .step("Step 2", "Description 2")
            .step("Step 3", "Description 3")
            .current(2)
            .direction(JFXSteps.Direction.VERTICAL)
            .build();

        box.getChildren().addAll(
            new Label("Horizontal Steps") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            horizontalSteps,
            new Label("Vertical Steps") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            verticalSteps
        );

        return box;
    }

    // ==================== Skeleton Demo ====================
    private VBox createSkeletonDemo() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(16));

        javafx.scene.layout.StackPane textSkeleton = JFXSkeleton.create()
            .width(200)
            .height(16)
            .build();

        javafx.scene.layout.StackPane circularSkeleton = JFXSkeleton.create()
            .variant(JFXSkeleton.Variant.CIRCULAR)
            .width(40)
            .height(40)
            .build();

        javafx.scene.layout.StackPane rectSkeleton = JFXSkeleton.create()
            .variant(JFXSkeleton.Variant.RECTANGULAR)
            .width(120)
            .height(80)
            .build();

        javafx.scene.layout.VBox paragraphSkeleton = JFXSkeleton.paragraph(3, 250, 14);

        javafx.scene.layout.HBox avatarSkeleton = JFXSkeleton.avatarText();

        javafx.scene.layout.StackPane staticSkeleton = JFXSkeleton.create()
            .width(150)
            .height(16)
            .noAnimation()
            .build();

        box.getChildren().addAll(
            new Label("Text Skeleton") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            textSkeleton,
            new Label("Circular Skeleton") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            circularSkeleton,
            new Label("Rectangular Skeleton") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            rectSkeleton,
            new Label("Paragraph Skeleton") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            paragraphSkeleton,
            new Label("Avatar + Text Skeleton") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            avatarSkeleton,
            new Label("Static Skeleton (no animation)") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: 600;"); }},
            staticSkeleton
        );

        return box;
    }
}
