# JFXium

> A modern JavaFX UI framework inspired by Ant Design 6.x

[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-blue.svg)](https://openjfx.io/)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

[中文文档](README_CN.md)

---

## Features

- **67 Components** - Comprehensive UI component library covering General, Layout, Navigation, Data Entry, Data Display, and Feedback
- **Ant Design 6.x Style** - Pixel-perfect implementation of Ant Design's design language
- **Light & Dark Themes** - Built-in theme switching with CSS variables
- **Customizable Themes** - Modify a single LESS file to generate your own theme
- **Builder Pattern** - Fluent API for all components
- **Accessibility** - Keyboard navigation, ARIA labels, focus management
- **Zero FXML** - Pure code-based UI construction

---

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Load Theme

```java
@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    // Load light theme
    scene.getStylesheets().add(
        getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm()
    );
    stage.setScene(scene);
    stage.show();
}
```

### 3. Use Components

```java
// Create a primary button
Button btn = ButtonAnt.create("Click me")
    .type(ButtonAnt.Type.PRIMARY)
    .onClick(e -> System.out.println("Hello JFXium!"))
    .build();

// Create an input field
TextField input = InputAnt.create()
    .placeholder("Enter something...")
    .build();

// Show a success message
MessageAnt.success("Operation successful!");

// Show a notification
NotificationAnt.info("Notification", "This is a notification message");
```

---

## Component Overview

### General (8)

| Component | Description | Example |
|-----------|-------------|---------|
| **ButtonAnt** | Button | `ButtonAnt.create("Submit").type(Type.PRIMARY).build()` |
| **InputAnt** | Input field | `InputAnt.create().placeholder("Enter...").build()` |
| **TextAreaAnt** | Text area | `TextAreaAnt.create().rows(4).build()` |
| **CheckBoxAnt** | Checkbox | `CheckBoxAnt.create().text("Remember me").build()` |
| **RadioButtonAnt** | Radio button | `RadioButtonAnt.create().text("Option A").build()` |
| **SwitchAnt** | Switch | `SwitchAnt.create().text("Enable").build()` |
| **SliderAnt** | Slider | `SliderAnt.create().min(0).max(100).build()` |
| **SpinnerAnt** | Spinner | `SpinnerAnt.create().min(0).max(100).build()` |

### Layout (8)

| Component | Description | Example |
|-----------|-------------|---------|
| **CardAnt** | Card container | `CardAnt.create().title("Title").content(node).build()` |
| **DividerAnt** | Divider | `DividerAnt.create().text("OR").build()` |
| **FormAnt** | Form layout | `FormAnt.create().label("Name", input).build()` |
| **TableAnt** | Data table | `TableAnt.create(Person.class).column("Name", "name").build()` |
| **LayoutAnt** | Page layout | `LayoutAnt.create().header(h).sider(s, 200).content(c).build()` |
| **FlexAnt** | Flex layout | `FlexAnt.create().direction(ROW).gap(16).children(nodes).build()` |
| **GridAnt** | Grid system | `GridAnt.create().col(6, node).col(6, node).build()` |
| **SpaceAnt** | Spacing | `SpaceAnt.create().size(16).children(nodes).build()` |

### Navigation (7)

| Component | Description | Example |
|-----------|-------------|---------|
| **AnchorAnt** | Anchor | `AnchorAnt.create().item("basic", "Basic", "#basic").build()` |
| **BreadcrumbAnt** | Breadcrumb | `BreadcrumbAnt.create().item("Home", () -> {}).build()` |
| **DropdownAnt** | Dropdown menu | `DropdownAnt.create().trigger(btn).item("1", "Edit").build()` |
| **MenuAnt** | Menu bar | `MenuAnt.create().menu("File", m -> m.item("New", () -> {})).build()` |
| **PaginationAnt** | Pagination | `PaginationAnt.create().total(100).pageSize(10).build()` |
| **StepsAnt** | Steps | `StepsAnt.create().step("Order", "1").step("Pay", "2").build()` |
| **TabsAnt** | Tabs | `TabsAnt.create().tab("Tab1", content).build()` |

### Data Entry (10)

| Component | Description | Example |
|-----------|-------------|---------|
| **AutoCompleteAnt** | Auto-complete | `AutoCompleteAnt.<String>create().options(list).build()` |
| **CascaderAnt** | Cascader | `CascaderAnt.create().options(options).build()` |
| **ComboBoxAnt** | ComboBox | `ComboBoxAnt.create().items(list).build()` |
| **DatePickerAnt** | Date picker | `DatePickerAnt.create().placeholder("Select date").build()` |
| **TimePickerAnt** | Time picker | `TimePickerAnt.create().format("HH:mm:ss").build()` |
| **ColorPickerAnt** | Color picker | `ColorPickerAnt.create().value(Color.BLUE).build()` |
| **InputNumberAnt** | Number input | `InputNumberAnt.create().min(0).max(100).build()` |
| **MentionsAnt** | Mentions | `MentionsAnt.create().option("user1", "John").build()` |
| **UploadAnt** | File upload | `UploadAnt.create().multiple(true).build()` |
| **TransferAnt** | Transfer | `TransferAnt.create().source(list1).target(list2).build()` |

### Data Display (20)

| Component | Description | Example |
|-----------|-------------|---------|
| **AvatarAnt** | Avatar | `AvatarAnt.create().text("JD").size(Size.LARGE).build()` |
| **BadgeAnt** | Badge | `BadgeAnt.create().count(5).build()` |
| **CalendarAnt** | Calendar | `CalendarAnt.create().value(LocalDate.now()).build()` |
| **CarouselAnt** | Carousel | `CarouselAnt.create().image("/1.png").autoplay(true).build()` |
| **CollapseAnt** | Collapse | `CollapseAnt.create().panel("1", "Title", content).build()` |
| **DescriptionsAnt** | Descriptions | `DescriptionsAnt.create().item("Name", "John").build()` |
| **EmptyAnt** | Empty state | `EmptyAnt.create().description("No data").build()` |
| **ImageAnt** | Image | `ImageAnt.create().src("/photo.png").width(200).build()` |
| **ListAnt** | Advanced list | `ListAnt.create().item(avatar, "Title", "Desc").build()` |
| **ListViewAnt** | List view | `ListViewAnt.create().items(list).build()` |
| **PopoverAnt** | Popover | `PopoverAnt.create().title("Tip").content(node).build()` |
| **QRCodeAnt** | QR Code | `QRCodeAnt.create().value("https://...").size(160).build()` |
| **SegmentedAnt** | Segmented | `SegmentedAnt.create().option("day", "Day").build()` |
| **StatisticAnt** | Statistic | `StatisticAnt.create().title("Users").value(112893).build()` |
| **TagAnt** | Tag | `TagAnt.create().text("Tag").color(Color.BLUE).build()` |
| **TimelineAnt** | Timeline | `TimelineAnt.create().item("Created", "2024-01-01", Color.BLUE).build()` |
| **TooltipAnt** | Tooltip | `TooltipAnt.create().text("Tooltip").build()` |
| **TreeAnt** | Tree | `TreeAnt.create().root("Root", r -> r.child("Child")).build()` |
| **TreeSelectAnt** | Tree select | `TreeSelectAnt.create().treeNode("1", "Root", children).build()` |
| **TypographyAnt** | Typography | `TypographyAnt.title("Title").level(1).build()` |

### Feedback (10)

| Component | Description | Example |
|-----------|-------------|---------|
| **AlertAnt** | Alert | `AlertAnt.create().type(Type.SUCCESS).message("Success").build()` |
| **DrawerAnt** | Drawer | `DrawerAnt.create().title("Settings").build().open(owner)` |
| **MessageAnt** | Message | `MessageAnt.success("Success!"))` |
| **ModalAnt** | Modal dialog | `ModalAnt.create().title("Confirm").build().open(owner)` |
| **NotificationAnt** | Notification | `NotificationAnt.info("Title", "Content")` |
| **PopconfirmAnt** | Popconfirm | `PopconfirmAnt.create().title("Delete?").onConfirm(() -> {}).build()` |
| **ProgressAnt** | Progress | `ProgressAnt.create().value(0.5).build()` |
| **ResultAnt** | Result | `ResultAnt.create().status(Status.SUCCESS).title("Success").build()` |
| **SkeletonAnt** | Skeleton | `SkeletonAnt.create().rows(3).animated(true).build()` |
| **SpinAnt** | Spin | `SpinAnt.create().indicator(Indicator.SPINNER).tip("Loading...").build()` |

### Other (6)

| Component | Description | Example |
|-----------|-------------|---------|
| **AccordionAnt** | Accordion | `AccordionAnt.create().pane("Title", content).build()` |
| **AnimationAnt** | Animation | `AnimationAnt.fadeIn(node, 300)` |
| **BackTopAnt** | Back to top | `BackTopAnt.create().target(scrollPane).build()` |
| **FloatButtonAnt** | Float button | `FloatButtonAnt.create().icon(icon).onClick(() -> {}).build()` |
| **IconAnt** | Icon | `IconAnt.symbol(IconAnt.Symbol.CLOSE)` |
| **RateAnt** | Rate | `RateAnt.create().value(3.5).allowHalf(true).build()` |

---

## Theming

### Built-in Themes

JFXium comes with 5 built-in themes:

| Theme | Style | Primary Color | Preview |
|-------|-------|---------------|---------|
| **Light** | Default | Blue `#1677ff` | Clean, professional |
| **Dark** | Dark mode | Blue `#1677ff` | Eye-friendly dark |
| **MUI** | Material Design | Indigo `#1976d2` | Google's Material |
| **shadcn** | Modern minimal | Zinc `#18181b` | Ultra minimal |
| **Cyberpunk** | Neon | Cyan `#00ffff` | Neon glow dark |

### Switch Themes

```java
// Light theme
scene.getStylesheets().clear();
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());

// Dark theme
scene.getStylesheets().clear();
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-dark.css").toExternalForm());

// MUI theme
scene.getStylesheets().clear();
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-mui.css").toExternalForm());

// shadcn theme
scene.getStylesheets().clear();
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-shadcn.css").toExternalForm());

// Cyberpunk theme
scene.getStylesheets().clear();
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-cyberpunk.css").toExternalForm());
```

### Custom Theme

Edit `src/main/resources/org/openkawu/jfxium/css/less/theme-custom.less` and run `mvn compile`.

See [THEME.md](docs/THEME.md) for detailed theming guide.

---

## Documentation

- [API Reference](API.md) - Complete component API documentation
- [Chinese Guide](README_CN.md) - Detailed Chinese documentation
- [Theme Guide](docs/THEME.md) - Theming and customization
- [Layout Guide](docs/LAYOUT.md) - Layout system
- [Animation Guide](docs/ANIMATION.md) - Animation utilities
- [Component Guide](docs/COMPONENTS.md) - Component details

---

## Demo

Run the Playground to see all components in action:

```bash
mvn javafx:run
```

---

## Requirements

- Java 17+
- JavaFX 21.0.6
- Maven 3.8+

---

## License

MIT License
