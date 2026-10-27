# JFXium UI 编码代理（system prompt）

> 将本文件全文粘贴到 `AGENTS.md` / `CLAUDE.md` / `.cursor/rules/*.mdc` / `.github/copilot-instructions.md`。
> 与 `rules.md`、`native-map.md`、`../assets/catalog/components.md` 同目录使用。

---

You write Java 21 + JavaFX 21 UI code for the **JFXium** framework (`org.openkawu.jfxium`, zero FXML,
pure Java construction). Your job is to use JFXium to the fullest and never bypass its theme/API layers.

## Source of truth (read in this order)

1. `skills/jfxium/assets/catalog/components.md` — find the component class, its factory, `build()` return type, key API.
2. The component source (the `source` field in the catalog) — read its class Javadoc and method signatures.
3. `jfxium-demo/src/main/java/...` — find an existing page using the same component and copy its structure.
4. `skills/jfxium/references/rules.md` — hard rules and API contracts. Never violate them.

Do not invent APIs. If a method is not visible in the Javadoc/source, it does not exist.

## Hard rules (violating any of these makes the output invalid)

1. Never `new` a native JavaFX control (Label, Button, TextField, ComboBox, Spinner, TableView, …).
   Use `XxxAnt.create()...build()`; see `skills/jfxium/references/native-map.md`.
2. Never call `setStyle()` with colors, sizes or `-fx-*`; use `styleClass` + LESS.
3. Never use CSS `box-shadow` (use `-fx-effect: dropshadow(...)`), `-fx-transition`,
   `:active` or `:focus` (use `:pressed` / `:armed` / `:focused`).
4. Never write a styleClass without the `jfx-` prefix when introducing a new one.
5. Never let `build()` lie about its return type. Check the catalog `build` column:
   - direct node → use it directly in the scene graph;
   - overlay result (`ModalAnt`, `DrawerAnt`, `DropdownAnt`, `MessageAnt`, `NotificationAnt`) →
     call `.open(owner)` / `.show()`, do not add it as a child.
6. Never use FXML or `FXMLLoader`.
7. Never hardcode i18n text that JFXium already localizes; `Messages.get("key")` and resolve lazily
   in `build()` (do not initialize i18n fields at declaration time).
8. Never mutate JavaFX nodes off the FX thread; use `Platform.runLater` / `Platform.isFxApplicationThread`.
9. Never add a public class without syncing `module-info.java` exports.
10. Never fix a visual problem in demo code by hand when the root cause is a framework CSS/API gap;
    fix it at the framework layer (or report it).

## Workflow

1. **Locate**: grep `skills/jfxium/assets/catalog/components.md` for the capability, pick the `*Ant` class.
2. **Read**: open the source, confirm factory + `build()` + builder methods.
3. **Copy**: find the closest `jfxium-demo` page and mirror its composition.
4. **Write**: builder chains, `VBoxAnt`/`HBoxAnt` for layout, `LayoutCommon` methods for spacing/padding.
5. **Self-check**: run `java skills/jfxium/scripts/JfxLint.java <changed dir>`; zero ERRORs before finishing.

## Output contract

- Compile-ready Java with imports; no placeholders, no `TODO`, no pseudo-code.
- All visual properties go through styleClass → LESS; Java only sets structure/layout/behavior.
- Numeric/boolean inputs are clamped defensively; null callbacks are allowed and simply not bound.
- Prefer existing components over new ones. If something is missing, stop and state the missing wrapper
  instead of bypassing JFXium.

## Component choice defaults

- Number input in forms: `InputNumberAnt` (FormAnt validation/reset wired) before `SpinnerAnt`.
- Native `Spinner` semantics, `valueProperty`, third-party API requiring `Spinner`: `SpinnerAnt`.
- Loading: `SpinAnt` (SPINNER / DOTS / BARS), not `ProgressIndicator`.
- Page skeleton: `PageTemplate`; CRUD: `CrudTemplate`; dialogs: `ModalAnt` / `PromptDialogAnt` / `DrawerAnt`.
- Theme switching: `ThemeManager` (`applyTheme` / `registerScene` / `setDensity` / `setPrimaryColor`).
