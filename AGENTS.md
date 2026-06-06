# AGENTS.md

This file provides guidance to Qoder (qoder.com) when working with code in this repository.

---

## Project Overview

JFXium is a JavaFX UI framework inspired by Ant Design 6.x. It wraps and enhances JavaFX native controls with a Builder-pattern API, LESS-based theming (11 built-in themes), and over 94 components covering controls, composites, overlays, layouts, and business templates. Zero FXML — all UI is constructed in pure Java code.

- **Java 21+, JavaFX 21.0.6, Maven 3.8+**
- **GroupId**: `org.openkawu`, **ArtifactId**: `jfxium`, **Version**: `1.0-SNAPSHOT`

---

## Build & Run Commands

```bash
# Build core library only (includes LESS → CSS compilation)
./mvnw install -pl jfxium -DskipTests -q

# Run the Showcase Demo (automatically builds both modules)
./mvnw javafx:run -pl jfxium-demo

# Run tests
./mvnw test -pl jfxium

# Full clean build
./mvnw clean install -DskipTests
```

The demo `mainClass` is `org.openkawu.jfxium.jfxiumUiExample.JfxiumUiExampleApp` (configured in `jfxium-demo/pom.xml`).

---

## Project Structure (Multi-Module Maven)

```
JFXium/
├── pom.xml                          # Parent POM (Java 21, JavaFX 21.0.6, JUnit 5.12.1)
├── jfxium/                          # Core library module
│   ├── pom.xml                      # Dependencies: javafx-controls, javafx-fxml, ikonli
│   └── src/main/
│       ├── java/org/openkawu/jfxium/
│       │   ├── module-info.java     # Module declaration (see exports below)
│       │   ├── core/                # Foundation layer
│       │   │   ├── token/           # Design tokens (ColorToken, SpacingToken, etc.)
│       │   │   ├── theme/           # ThemeManager, Theme interface, ThemeColor
│       │   │   ├── css/             # CssClasses, CssStyles, Background
│       │   │   ├── animation/       # AnimationAnt (fade, slide, scale)
│       │   │   ├── builder/         # AbstractStyleBuilder<SELF> — shared Builder base
│       │   │   ├── i18n/            # Messages (ResourceBundle, default zh_CN)
│       │   │   ├── form/            # FormContext, FormModel, Rule (validation)
│       │   │   ├── layout/          # OverlayManager, SceneLayout
│       │   │   ├── command/         # Command pattern
│       │   │   └── util/            # EventBus, TextFormatters, WindowManager, etc.
│       │   ├── component/
│       │   │   ├── base/            # Base component classes
│       │   │   ├── control/         # ~29 native JavaFX control wrappers
│       │   │   ├── composite/       # ~44 custom-built composite components
│       │   │   ├── overlay/         # ~9 popup/dialog overlay components
│       │   │   └── layout/          # ~15 layout container wrappers
│       │   ├── layout/              # Page-level layouts (AppShellAnt, LayoutAnt)
│       │   └── template/            # Business templates (CrudTemplate, LoginTemplate, etc.)
│       └── resources/org/openkawu/jfxium/
│           ├── css/                 # Compiled CSS outputs (11 theme files)
│           │   └── less/            # LESS source files
│           │       ├── variables-base.less   # Spacing, radius, fonts, control heights, mixins
│           │       ├── variables.less         # Light theme color tokens (0-9 scale)
│           │       ├── variables-dark.less    # Dark theme color tokens
│           │       ├── theme-base.less        # → @import "components/_index"
│           │       ├── theme-light.less       # → @import variables.less + theme-base.less
│           │       ├── theme-dark.less
│           │       ├── theme-*-compact.less   # Override size tokens, then @import theme-base
│           │       ├── theme-mui*.less        # Material Design variants
│           │       ├── theme-shadcn.less / theme-cyberpunk.less / theme-custom.less
│           │       └── components/            # 64 component-level .less files
│           └── i18n/                # messages.properties, messages_zh_CN, messages_en
└── jfxium-demo/                     # Showcase application (depends on jfxium)
    └── src/main/java/org/openkawu/jfxium/
        ├── jfxiumUiExample/         # Main demo entry (JfxiumUiExampleApp)
        ├── demo/showcase/           # ShowcaseDemo (component gallery)
        └── demo/admin/              # AdminDemo (CRUD reference)
```

---

## Module Exports (module-info.java)

```
module org.openkawu.jfxium {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    exports org.openkawu.jfxium.core.token;
    exports org.openkawu.jfxium.core.theme;
    exports org.openkawu.jfxium.core.css;
    exports org.openkawu.jfxium.core.animation;
    exports org.openkawu.jfxium.core.layout;
    exports org.openkawu.jfxium.core.i18n;
    exports org.openkawu.jfxium.core.command;
    exports org.openkawu.jfxium.core.form;
    exports org.openkawu.jfxium.core.util;
    exports org.openkawu.jfxium.component.control;
    exports org.openkawu.jfxium.component.composite;
    exports org.openkawu.jfxium.component.overlay;
    exports org.openkawu.jfxium.component.base;
    exports org.openkawu.jfxium.component.layout;
    exports org.openkawu.jfxium.layout;
    exports org.openkawu.jfxium.template;
}
```

---

## Component Architecture

### Three component tiers

| Tier | Package | Description |
|------|---------|-------------|
| **control** | `component.control` | Thin wrappers over JavaFX native controls (`extends Button`, etc.) with Builder API and styleClass-based theming. Examples: ButtonAnt, InputAnt, ComboBoxAnt, TableAnt. |
| **composite** | `component.composite` | Custom components built from multiple JavaFX nodes. No inheritance from a single native control. Examples: CardAnt, AlertAnt, MenuAnt, FormAnt, WatermarkAnt. |
| **overlay** | `component.overlay` | Popup/dialog components using `Stage`, `Popup`, or `ContextMenu`. Examples: ModalAnt, DrawerAnt, DropdownAnt, MessageAnt. |

### Universal Builder pattern

Every component follows this pattern:
```java
MyComponentAnt comp = MyComponentAnt.create()   // static factory
    .property1(value)
    .property2(value)
    .onSomeEvent(handler)
    .build();                                     // returns the built Node/Control
```

### AbstractStyleBuilder<SELF>

The shared base class `org.openkawu.jfxium.core.builder.AbstractStyleBuilder<SELF>` provides:
- `style(String)` — inline CSS (use sparingly)
- `styleClass(String)` — add style classes (preferred)
- `padding(Insets)` / `padding(double)` — set padding
- `maxWidth/minWidth/prefWidth/maxHeight/minHeight/prefHeight(double)`
- `applyStyles(Node)` — apply accumulated styles in `build()`

All new Builder classes **must** extend `AbstractStyleBuilder` instead of re-implementing these fields.

### Controller pattern for runtime state changes

For components that need post-construction state modification (e.g., changing selected menu item, advancing steps), use the Controller pattern:
```java
// Builder exposes a controller() method
MenuAnt menu = MenuAnt.create().menu("File", ...).build();
MenuAnt.Controller ctrl = MenuAnt.controllerOf(menu);
ctrl.setSelectedKey("file");
```

This avoids rebuilding the entire component tree. **Always add a Controller** when a component's state needs to change after `build()`. Existing Controllers: `MenuAnt.Controller`, `StepsAnt.Controller`, `AnchorAnt.Controller`.

### Component design rules

1. **final JavaFX controls** (Button, CheckBox, etc.) must be composed, not extended. Use the composite pattern: wrap them as a field inside a container.
2. **Form components** (InputAnt, CheckBoxAnt, etc.) use an inheritance-based design (`extends` the native control).
3. **All colors/styles go through styleClass → LESS**, never inline `setStyle()` with hardcoded color values.
4. **New control** must: (a) add CssClasses constants in `CssClasses.java`, (b) add LESS styles in `components/_xxx.less`, (c) register in `components/_index.less`.

---

## LESS Theming System

### Build pipeline

LESS source files are compiled to CSS by **jlessc** (pure Java, no Node.js required) via `groovy-maven-plugin` during the `generate-resources` phase. This runs automatically with `mvn compile` or `mvn install`.

**Themes compiled**: theme-light, theme-dark, theme-light-compact, theme-dark-compact, theme-mui, theme-mui-compact, theme-mui-dark, theme-mui-dark-compact, theme-shadcn, theme-cyberpunk, theme-custom (11 total).

### Theme architecture

```
variables-{name}.less   →   Defines color tokens (0-9 scale)
variables-base.less      →   Defines size/spacing/radius tokens, mixins
theme-base.less          →   Imports components/_index.less (all component CSS)
theme-{name}.less        →   @import variables-{name}.less → @import theme-base.less
```

Compact themes additionally override size tokens (e.g., `@control-height: 28px`, `@spacing-sm: 6px`) before importing `theme-base.less`.

### Color token system (mandatory)

Every theme color must define a 0-9 scale:
```less
@color-accent-0: #e6f4ff;   // Lightest (hover backgrounds)
@color-accent-5: #1677ff;   // Base (primary emphasis)
@color-accent-6: #0958d9;   // Active/pressed
@color-accent-9: #001d66;   // Darkest
```

Semantic variables map to scale indices: `@color-accent-emphasis` → index 5, `@color-accent-hover` → index 0, `@color-accent-active` → index 6, `@color-accent-muted` → index 2.

### Critical CSS rules (from SKILL.md)

- **No `-fx-transition`** — JavaFX CSS does not support it. Define state colors directly (`.button:hover { ... }`).
- **No `box-shadow`** — Use `-fx-effect: dropshadow(gaussian, color, blur, spread, offsetX, offsetY)`.
- **No `padding` / `border`** — Always use `-fx-padding`, `-fx-border-color`, `-fx-background-radius`, etc.
- **Pseudo-class mapping**: Web `:active` → JavaFX `:pressed` or `:armed`; Web `:focus` → JavaFX `:focused`; selection → `:selected`.
- **SVG icons**: Use `-fx-shape: "M10 20..."` with `-fx-background-color`, never `background-image`.
- **Borders via background stacking**: Use `-fx-background-color: borderColor, fillColor; -fx-background-insets: -2, 0;` for outline-like effects.
- **All heights/paddings must use tokens** (e.g., `@control-height`, `@ctrl-padding-y`), not hardcoded px. This is how compact mode works — it overrides tokens.
- **`@border-radius-full` (9999px) only on size-clamped nodes** (fixed-size thumb, badge). Never on track/progress bars whose width is determined by parent layout.

---

## ThemeManager & Runtime Theming

```java
// Apply a theme
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().registerScene(scene);  // Watch for future theme changes

// Change primary color at runtime (injects CSS variables via setStyle)
ThemeManager.getInstance().setPrimaryColor(Color.web("#ff5722"));
```

ThemeManager maintains a three-axis state machine: **Family** (Ant/MUI/Shadcn/Cyberpunk) × **dark** (boolean) × **compact** (boolean). Theme switching re-applies the accent color automatically (BUG #62 fix).

---

## i18n System

- **Default locale**: `zh_CN` (Simplified Chinese)
- **Resources**: `jfxium/src/main/resources/org/openkawu/jfxium/i18n/messages*.properties`
- **API**: `Messages.get("key")`, `Messages.get("key", args...)`, `Messages.setLocale(Locale)`
- **Key naming**: `<component>.<element>` (e.g., `codeblock.copy`, `treeselect.placeholder`)
- Uses JDK `ResourceBundle` / `MessageFormat` — zero third-party deps
- Components with built-in i18n: CodeBlockAnt, TreeSelectAnt, EmptyAnt, ModalAnt, PopconfirmAnt, UploadAnt, TransferAnt

---

## When Fixing Bugs

Follow the **dual traceability principle** (SKILL §22): when a demo bug is found, always investigate whether the root cause is in the framework (jfxium), not just the demo. If a demo has boilerplate like manual key→label maps or node-rebuilding for state changes, that signals a framework API gap.

The bug tracker is `BUG.md` (currently at #65). New issues are numbered sequentially. The acceptance checklist is `ACCEPTANCE.md`.

---

## Key Dependencies

| Dependency | Version | Notes |
|------------|---------|-------|
| JavaFX (controls, fxml) | 21.0.6 | Managed in parent POM |
| Ikonli (javafx + antdesignicons) | 12.3.1 | Icon library |
| jlessc | 1.16 | Pure Java LESS compiler (build-time only) |
| JUnit Jupiter | 5.12.1 | Testing |

**Removed dependencies** (no longer used): javafx-web, javafx-media.
