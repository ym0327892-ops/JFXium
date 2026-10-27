# JFXium UI instructions

This project builds Java 21 + JavaFX 21 UI with the **JFXium** framework (`org.openkawu.jfxium`),
pure Java, no FXML. When generating UI code:

1. Read `skills/jfxium/SKILL.md` for the workflow, component map and hard rules.
2. Use `*Ant` components only — never `new Label()`, `new Button()`, `new TextField()`,
   `new ComboBox()`, `new Spinner()`, `new TableView()`, etc.
3. Never call `setStyle()` with colors or px; style via `styleClass` + LESS.
4. Never use `box-shadow`, `-fx-transition`, CSS `:active` or `:focus`.
5. Dialogs/drawers return a Result: call `.open(owner)`; messages use `MessageAnt.success(...)`.
6. Number input in forms: `InputNumberAnt`; native Spinner semantics: `SpinnerAnt`.
7. Loading: `SpinAnt`; theming: `ThemeManager`.
8. New public classes must be exported in `module-info.java`.

Self-check after editing: `java skills/jfxium/scripts/JfxLint.java <dir>` (zero ERRORs).
