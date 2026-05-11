# JFXium Component API Reference

> Auto-generated on 2026-05-09
> Total Components: 67
> Ant Design 6.x Coverage: ~98.5%

---

## Table of Contents

- [General](#general)
- [Layout](#layout)
- [Navigation](#navigation)
- [Data Entry](#data-entry)
- [Data Display](#data-display)
- [Feedback](#feedback)
- [Other](#other)
- [Accessibility](#accessibility)

---

## General

### ButtonAnt

Primary UI element for user actions.

```java
Button btn = ButtonAnt.create("Click me")
    .type(ButtonAnt.Type.PRIMARY)    // DEFAULT, PRIMARY, SUCCESS, WARNING, DANGER, OUTLINED, DASHED, TEXT, LINK
    .size(ButtonAnt.Size.LARGE)       // DEFAULT, SMALL, LARGE
    .rounded()                         // Circular corners
    .icon(iconNode)
    .loading(true)
    .disabled(true)
    .onClick(e -> System.out.println("Clicked!"))
    .build();
```

**Types:**
- `DEFAULT` - Standard button
- `PRIMARY` / `ACCENT` - Highlighted action
- `SUCCESS` - Green positive action
- `WARNING` - Orange caution action
- `DANGER` - Red destructive action
- `OUTLINED` - Bordered button
- `DASHED` - Dashed border button
- `TEXT` - Text-only button
- `LINK` - Link-style button

**Accessibility:** Supports keyboard navigation (Tab, Enter, Space).

---

### InputAnt

Text input field.

```java
TextField input = InputAnt.create()
    .placeholder("Enter your name")
    .text("Default value")
    .size(InputAnt.Size.LARGE)        // DEFAULT, SMALL, LARGE
    .disabled(true)
    .readOnly(true)
    .build();
```

**Accessibility:** Auto-sets accessible text from placeholder.

---

### TextAreaAnt

Multi-line text input.

```java
TextArea area = TextAreaAnt.create()
    .placeholder("Enter description...")
    .rows(4)
    .disabled(true)
    .build();
```

---

### CheckBoxAnt

Checkbox for boolean selection.

```java
CheckBox cb = CheckBoxAnt.create()
    .text("Remember me")
    .selected(true)
    .disabled(true)
    .build();
```

---

### RadioButtonAnt

Radio button for single selection.

```java
RadioButton rb = RadioButtonAnt.create()
    .text("Option 1")
    .selected(true)
    .disabled(true)
    .build();
```

---

### SwitchAnt

Toggle switch.

```java
ToggleButton toggle = SwitchAnt.create()
    .text("Enable notifications")
    .selected(true)
    .disabled(true)
    .build();
```

---

### SliderAnt

Slider for numeric range selection.

```java
Slider slider = SliderAnt.create()
    .min(0)
    .max(100)
    .value(50)
    .showTicks(true)
    .build();
```

---

### SpinnerAnt

Numeric spinner with increment/decrement.

```java
Spinner<Integer> spinner = SpinnerAnt.create()
    .min(0)
    .max(100)
    .value(50)
    .step(1)
    .build();
```

---

## Layout

### CardAnt

Container for content.

```java
VBox card = CardAnt.create()
    .title("Card Title")
    .extra(button)
    .content(new Label("Content"))
    .bordered(true)
    .shadow(CardAnt.Shadow.MEDIUM)    // NONE, SMALL, MEDIUM, LARGE
    .hoverable(true)
    .build();
```

---

### DividerAnt

Horizontal or vertical divider.

```java
Separator divider = DividerAnt.create()
    .text("OR")
    .vertical(false)
    .build();
```

---

### FormAnt

Form layout with labels and fields.

```java
VBox form = FormAnt.create()
    .label("Username", usernameField)
    .label("Password", passwordField, true)  // required
    .help("Password must be 8+ characters")
    .build();
```

---

### TableAnt

Data table with columns.

```java
TableView<Person> table = TableAnt.create(Person.class)
    .column("Name", "name", 150)
    .column("Age", "age", 80)
    .data(personList)
    .striped(true)
    .bordered(true)
    .build();
```

---

## Navigation

### AnchorAnt

Anchor navigation for page sections.

```java
VBox anchor = AnchorAnt.create()
    .item("basic", "Basic", "#basic")
    .item("api", "API", "#api")
    .activeKey("basic")
    .direction(AnchorAnt.Direction.VERTICAL)
    .onChange(key -> System.out.println("Nav: " + key))
    .build();
```

---

### BreadcrumbAnt

Breadcrumb navigation.

```java
HBox breadcrumb = BreadcrumbAnt.create()
    .item("Home", () -> navigate("/"))
    .item("Products", () -> navigate("/products"))
    .item("Detail", null)  // current page
    .separator("/")
    .build();
```

---

### DropdownAnt

Dropdown menu.

```java
Node dropdown = DropdownAnt.create()
    .trigger(button)
    .item("1", "Edit")
    .item("2", "Delete", true)  // disabled
    .divider()
    .onSelect(key -> System.out.println("Selected: " + key))
    .build();
```

---

### MenuAnt

Menu bar.

```java
MenuBar menuBar = MenuAnt.create()
    .menu("File", menu -> menu
        .item("New", () -> newFile())
        .item("Open", () -> openFile())
        .separator()
        .item("Exit", () -> exit()))
    .build();
```

---

### PaginationAnt

Pagination control.

```java
Pagination pagination = PaginationAnt.create()
    .total(100)
    .pageSize(10)
    .current(1)
    .showSizeChanger(true)
    .onChange((page, size) -> loadPage(page, size))
    .build();
```

---

### StepsAnt

Step indicator.

```java
VBox steps = StepsAnt.create()
    .step("Cart", "1")
    .step("Payment", "2")
    .step("Confirm", "3")
    .current(1)
    .direction(StepsAnt.Direction.HORIZONTAL)
    .build();
```

---

### TabsAnt

Tab container.

```java
TabPane tabs = TabsAnt.create()
    .tab("Tab 1", content1)
    .tab("Tab 2", content2, true)  // closable
    .type(TabsAnt.Type.LINE)       // LINE, CARD
    .build();
```

---

## Data Entry

### AutoCompleteAnt

Autocomplete input with suggestions.

```java
HBox autoComplete = AutoCompleteAnt.<String>create()
    .placeholder("Type to search...")
    .options(Arrays.asList("Apple", "Banana", "Cherry"))
    .onSelect(item -> System.out.println(item))
    .build();
```

---

### CascaderAnt

Cascading selection for hierarchical data.

```java
HBox cascader = CascaderAnt.create()
    .placeholder("Please select")
    .options(options)
    .onChange(path -> System.out.println(path))
    .build();
```

---

### ComboBoxAnt

Dropdown selection.

```java
ComboBox<String> combo = ComboBoxAnt.create()
    .items(Arrays.asList("Option 1", "Option 2"))
    .placeholder("Select...")
    .build();
```

---

### DatePickerAnt

Date selection.

```java
DatePicker datePicker = DatePickerAnt.create()
    .placeholder("Select date")
    .format("yyyy-MM-dd")
    .build();
```

---

### ColorPickerAnt

Color selection.

```java
ColorPicker colorPicker = ColorPickerAnt.create()
    .value(Color.BLUE)
    .build();
```

---

### InputNumberAnt

Numeric input with controls.

```java
HBox inputNumber = InputNumberAnt.create()
    .value(10)
    .min(0)
    .max(100)
    .step(1)
    .precision(0)
    .prefix("$")
    .suffix("%")
    .onChange(v -> System.out.println(v))
    .build();
```

---

### UploadAnt

File upload.

```java
VBox upload = UploadAnt.create()
    .multiple(true)
    .accept(".jpg,.png")
    .onChange(files -> System.out.println(files))
    .build();
```

---

### TransferAnt

Transfer items between two lists.

```java
HBox transfer = TransferAnt.create()
    .source(Arrays.asList("Item 1", "Item 2"))
    .target(Arrays.asList("Item 3"))
    .titles("Available", "Selected")
    .onChange((source, target) -> System.out.println(target))
    .build();
```

---

## Data Display

### AvatarAnt

User avatar.

```java
StackPane avatar = AvatarAnt.create()
    .text("JD")
    .image("/avatar.png")
    .size(AvatarAnt.Size.LARGE)      // SMALL, DEFAULT, LARGE
    .shape(AvatarAnt.Shape.CIRCLE)   // CIRCLE, SQUARE
    .build();
```

---

### BadgeAnt

Badge for counts or status.

```java
StackPane badge = BadgeAnt.create()
    .count(5)
    .dot(true)
    .status(BadgeAnt.Status.SUCCESS)  // SUCCESS, WARNING, ERROR, PROCESSING, DEFAULT
    .offset(5, -5)
    .build();
```

---

### CalendarAnt

Calendar display.

```java
VBox calendar = CalendarAnt.create()
    .value(LocalDate.now())
    .onSelect(date -> System.out.println(date))
    .build();
```

---

### CarouselAnt

Image carousel.

```java
HBox carousel = CarouselAnt.create()
    .image("/img1.png")
    .image("/img2.png")
    .autoplay(true)
    .interval(3000)
    .build();
```

---

### CollapseAnt

Collapsible panels.

```java
VBox collapse = CollapseAnt.create()
    .panel("1", "Panel 1", new Label("Content 1"))
    .panel("2", "Panel 2", new Label("Content 2"), true)  // disabled
    .accordion(true)
    .activeKey("1")
    .build();
```

---

### DescriptionsAnt

Key-value descriptions.

```java
VBox descriptions = DescriptionsAnt.create()
    .title("User Info")
    .item("Name", "John Doe")
    .item("Age", "30")
    .column(2)
    .bordered(true)
    .build();
```

---

### EmptyAnt

Empty state placeholder.

```java
VBox empty = EmptyAnt.create()
    .description("No data")
    .image("/empty.png")
    .build();
```

---

### ImageAnt

Image display.

```java
StackPane image = ImageAnt.create()
    .src("/photo.png")
    .width(200)
    .height(200)
    .alt("Photo")
    .preview(true)
    .borderRadius(8)
    .build();
```

---

### ListViewAnt

List display.

```java
ListView<String> list = ListViewAnt.create()
    .items(Arrays.asList("Item 1", "Item 2"))
    .bordered(true)
    .build();
```

---

### PopoverAnt

Hover popover.

```java
Node popover = PopoverAnt.create()
    .title("Info")
    .content(new Label("Details here"))
    .trigger(button)
    .placement(PopoverAnt.Placement.TOP)
    .build();
```

---

### SegmentedAnt

Segmented control.

```java
HBox segmented = SegmentedAnt.create()
    .option("daily", "Daily")
    .option("weekly", "Weekly")
    .selected("daily")
    .block(true)
    .onChange(val -> System.out.println(val))
    .build();
```

---

### StatisticAnt

Statistic display.

```java
VBox statistic = StatisticAnt.create()
    .title("Active Users")
    .value(112893)
    .prefix("$")
    .suffix("%")
    .valueColor(Color.GREEN)
    .build();
```

---

### TagAnt

Tag for categorization.

```java
HBox tag = TagAnt.create()
    .text("Tag")
    .color(TagAnt.Color.BLUE)        // BLUE, GREEN, RED, ORANGE, GRAY
    .closable(true)
    .onClose(() -> System.out.println("Closed"))
    .build();
```

---

### TimelineAnt

Timeline display.

```java
VBox timeline = TimelineAnt.create()
    .item("Create order", "2024-01-01", TimelineAnt.Color.BLUE)
    .item("Payment", "2024-01-02", TimelineAnt.Color.GREEN)
    .pending("Delivery")
    .build();
```

---

### TooltipAnt

Tooltip on hover.

```java
Tooltip tooltip = TooltipAnt.create()
    .text("This is a tooltip")
    .delay(500)
    .build();
Tooltip.install(node, tooltip);
```

---

### TreeAnt

Tree structure.

```java
TreeView<String> tree = TreeAnt.create()
    .root("Root", root -> root
        .child("Child 1")
        .child("Child 2", child -> child
            .child("Grandchild")))
    .build();
```

---

## Feedback

### AlertAnt

Alert message.

```java
VBox alert = AlertAnt.create()
    .type(AlertAnt.Type.SUCCESS)      // SUCCESS, INFO, WARNING, ERROR
    .message("Operation successful")
    .description("Details here")
    .closable(true)
    .onClose(() -> System.out.println("Closed"))
    .build();
```

---

### DrawerAnt

Slide-out panel.

```java
DrawerAnt.Drawer drawer = DrawerAnt.create()
    .title("Settings")
    .content(content)
    .placement(DrawerAnt.Placement.RIGHT)  // LEFT, RIGHT, TOP, BOTTOM
    .width(400)
    .maskClosable(true)
    .onClose(closed -> System.out.println("Closed"))
    .build();

drawer.open(ownerNode);
drawer.close();
```

**Animation:** Fade + slide from edge.

---

### MessageAnt

Global message toast.

```java
MessageAnt.success("Success!");
MessageAnt.error("Error!");
MessageAnt.warning("Warning!");
MessageAnt.info("Info!");
MessageAnt.loading("Loading...");
```

---

### ModalAnt

Modal dialog.

```java
ModalAnt.Modal modal = ModalAnt.create()
    .title("Confirm")
    .content("Are you sure?")
    .width(400)
    .centered(true)
    .maskClosable(true)
    .onClose(closed -> System.out.println("Closed"))
    .build();

modal.open(ownerNode);
modal.close();
```

**Animation:** Fade + scale.

---

### NotificationAnt

Notification toast.

```java
NotificationAnt.notify(
    "Title",
    "Description",
    NotificationAnt.Type.SUCCESS,
    ownerNode
);
```

---

### PopconfirmAnt

Confirm popup.

```java
Node popconfirm = PopconfirmAnt.create()
    .title("Delete?")
    .content("This cannot be undone")
    .onConfirm(() -> delete())
    .onCancel(() -> cancel())
    .build();
```

---

### ProgressAnt

Progress indicator.

```java
ProgressBar progress = ProgressAnt.create()
    .value(0.5)
    .type(ProgressAnt.Type.LINE)      // LINE, CIRCLE
    .status(ProgressAnt.Status.SUCCESS)  // NORMAL, SUCCESS, ERROR
    .build();
```

---

### ResultAnt

Result page.

```java
VBox result = ResultAnt.create()
    .status(ResultAnt.Status.SUCCESS)  // SUCCESS, ERROR, INFO, WARNING
    .title("Operation Successful")
    .subTitle("Details here")
    .extra(button)
    .build();
```

---

### SkeletonAnt

Loading skeleton.

```java
VBox skeleton = SkeletonAnt.create()
    .rows(3)
    .columns(1)
    .animated(true)
    .build();
```

---

### SpinAnt

Loading spinner.

```java
VBox spin = SpinAnt.create()
    .indicator(SpinAnt.Indicator.SPINNER)  // SPINNER, DOTS, BARS
    .size(SpinAnt.Size.LARGE)              // SMALL, DEFAULT, LARGE
    .tip("Loading...")
    .fullscreen(true)
    .build();
```

---

## Other

### AccordionAnt

Accordion panels.

```java
Accordion accordion = AccordionAnt.create()
    .pane("Section 1", content1)
    .pane("Section 2", content2)
    .build();
```

---

### AnimationAnt

Animation utilities.

```java
AnimationAnt.fadeIn(node, 300);
AnimationAnt.fadeOut(node, 300);
AnimationAnt.slideUp(node, 300);
AnimationAnt.scaleIn(node, 300);
```

---

### BackTopAnt

Back to top button.

```java
Node backTop = BackTopAnt.create()
    .visibilityHeight(400)
    .target(scrollPane)
    .build();
```

---

### RateAnt

Star rating.

```java
HBox rate = RateAnt.create()
    .value(3.5)
    .allowHalf(true)
    .disabled(true)
    .onChange(v -> System.out.println(v))
    .build();
```

---

## Accessibility

### AccessibilityUtils

Helper methods for accessibility.

```java
// Set ARIA label
AccessibilityUtils.setAriaLabel(node, "Description");

// Set ARIA description
AccessibilityUtils.setAriaDescription(node, "Detailed help text");

// Configure button
AccessibilityUtils.configureButton(button, "Submit", "Saves the form");

// Configure input
AccessibilityUtils.configureTextInput(input, "Username", "Enter your username");

// Add focus visible styling
AccessibilityUtils.addFocusVisible(node);
```

**Focus Management:**
- All interactive components support Tab navigation
- Focus visible styling with accent color ring
- Enter/Space activation for buttons

---

## Size Variants

Many components support size variants:

```java
// Small
ButtonAnt.create("Small").size(ButtonAnt.Size.SMALL).build()
InputAnt.create().size(InputAnt.Size.SMALL).build()

// Large
ButtonAnt.create("Large").size(ButtonAnt.Size.LARGE).build()
InputAnt.create().size(InputAnt.Size.LARGE).build()
```

**Supported components:** Button, Input, TextArea, PasswordField, ComboBox, ChoiceBox, Tag, Badge

---

## Theme Colors

Button supports semantic colors:

```java
ButtonAnt.create("Save").type(ButtonAnt.Type.SUCCESS).build()    // Green
ButtonAnt.create("Warning").type(ButtonAnt.Type.WARNING).build() // Orange
ButtonAnt.create("Delete").type(ButtonAnt.Type.DANGER).build()   // Red
```

---

*End of API Reference*
