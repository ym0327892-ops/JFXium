# JFXium 组件速查（v1.34.2）

> 生成物，禁止手改。完整信息（extends / 定位 / source）见 `components.md` / `components.json`。

| 组件 | 包 | build() | 关键 API |
|---|---|---|---|
| **AnchorAnt** | component.composite | `VBox` | item, items, direction, offsetTop, activeKey, onChange, controller |
| **AutoCompleteAnt** | component.composite | `HBox` | value, options, optionToString, filter, onChange, onSelect, maxSuggestions, bindValue |
| **AvatarAnt** | component.composite | `StackPane` | size, shape, text, image, src, icon, backgroundColor, textColor |
| **BackTopAnt** | component.composite | `StackPane` | visibilityHeight, duration, target, bottom, right, install |
| **BadgeAnt** | component.composite | `StackPane` | content, count, dot, status |
| **BreadcrumbAnt** | component.composite | `HBox` | separator, item, items |
| **CalendarAnt** | component.composite | `VBox` | value, selectedDate, mode, onSelect, onPanelChange, fullscreen, controllerOf |
| **CarouselAnt** | component.composite | `StackPane` | item, items, autoplay, autoplayInterval, dots, noDots, arrows, noArrows, effect, initialIndex, dotPosition |
| **CascaderAnt** | component.composite | `HBox` | hasChildren, options, allowClear, showSearch, onChange, value, bindValue |
| **ChartAnt** | component.composite | `Chart` | line, bar, area, stackedBar, stackedArea, scatter, bubble, data, slice, point, pointSeries, typeOf, pie, controllerOf, type, title, xAxisLabel, yAxisLabel |
| **CodeBlockAnt** | component.composite | `BorderPane` | main, language, code, showLineNumbers, theme, maxHeight, showCopyButton, title, selectable |
| **CollapseAnt** | component.composite | `VBox` | disabled, controllerOf, panel, panels, accordion, activeKey, activeKeys, expandIcon, controller, expand, collapse, toggle, expandAll, collapseAll |
| **DateTimePickerAnt** | component.composite | `StackPane` | controllerOf, value, editable, format, allowClear, size, onChange, bindValue, showPanel, hidePanel |
| **DescriptionsAnt** | component.composite | `VBox` | title, size, column, bordered, item |
| **EmptyAnt** | component.composite | `VBox` | description, image, extra, extraButton |
| **FloatButtonAnt** | component.composite | `StackPane` | icon, tooltip, type, onClick, size |
| **FormAnt** | component.composite | `VBox` | required, rule, rules, helpText, validateStatus, labelAlign, labelWidth, hidden, hide, tooltip, end, size, colon, labelCol, wrapperCol, rowGap, columnGap, validateTrigger |
| **GroupBoxAnt** | component.composite | `VBox` | title, extra, content, bordered, size, type, actions, tab, activeTabKey, defaultActiveTabKey, onTabChange, tabBarExtraContent, headerBackground, headerBorder, hoverable |
| **HBarAnt** | component.composite | `HBarAnt` | left, center, right, gap, alignment, borderTop, borderBottom, borderLeft, borderRight, background, padding, borderRadius, styleClass, style, visible, managed, opacity, id |
| **ImageAnt** | component.composite | `StackPane` | src, width, height, alt, preview, fallback, placeholder, objectFit, borderRadius, controllerOf |
| **InputNumberAnt** | component.composite | `HBox` | value, min, max, step, readOnly, size, onChange, precision, prefix, suffix, bindValue |
| **ListAnt** | component.composite | `VBox` | item, items, bordered, split, header, footer, loading |
| **MenuAnt** | component.composite | `Pane` | controllerOf, childLevel, addItem, subMenu, mode, theme, collapsed, selectedKey, onSelect, expandMode, expandedKeys, onExpandChange, endSubMenu, controller, toggleCollapsed, expandKey, collapseKey, defaultExpanded |
| **ProgressAnt** | component.composite | `HBox` | bar, circle, progress, size, status, showInfo, controllerOf |
| **QRCodeAnt** | component.composite | `StackPane` | value, size, color, bgColor, icon, bordered, controllerOf |
| **RateAnt** | component.composite | `HBox` | count, value, defaultValue, allowHalf, size, onChange, onHoverChange, bindValue |
| **ResizablePanelAnt** | component.composite | `StackPane` | content, mode, minWidth, minHeight, prefWidth, prefHeight, maxWidth, maxHeight, grow, growX, growY |
| **ResultAnt** | component.composite | `VBox` | status, title, subTitle, extra, extraButton, success, error, info, warning |
| **SegmentedAnt** | component.composite | `HBox` | option, options, selected, size, block, onChange, bindValue, controllerOf |
| **SelectableTextAnt** | component.composite | `Node` | multiline, wrap, maxWidth, prefWidth, type, bordered, focusHalo |
| **SkeletonAnt** | component.composite | `StackPane` | variant, width, height, animated, noAnimation, paragraph, avatarText |
| **SliderAnt** | component.composite | `Node` | min, max, value, defaultValue, step, range, vertical, reverse, dots, marks, tipFormatter, tooltipVisible, onChange, onChangeComplete, bindValue |
| **SpinAnt** | component.composite | `VBox` | tip, size, indicator, fullscreen, overlayOpacity, overlay, show, hide |
| **StatisticAnt** | component.composite | `VBox` | title, value, prefix, suffix, precision, size, controllerOf |
| **StepsAnt** | component.composite | `Node` | step, current, direction, size, controller, next, prev |
| **SurfaceAnt** | component.composite | `VBox` | title, extra, content, children, bordered, shadow, gap |
| **SwitchAnt** | component.composite | `HBox` | selected, checkedText, uncheckedText, shape, onChange, bindValue |
| **TabsAnt** | component.composite | `Node` | controllerOf, tab, type, size, tabPlacement, centered, extraLeft, extraRight, onChange, activeIndex, activeKey, controller, selectByKey, next, prev |
| **TagAnt** | component.composite | `HBox` | modify, text, type, size, shape, closable, bordered, noBorder, color, onClose, apply |
| **TimePickerAnt** | component.composite | `StackPane` | controllerOf, value, format, allowClear, size, onChange, bindValue, editable, showPanel, hidePanel |
| **TimelineAnt** | component.composite | `VBox` | mode, pending, item |
| **TitleBarAnt** | component.composite | `TitleBarAnt` | title, icon, stage, left, center, right, windowControls, controls, draggable |
| **TransferAnt** | component.composite | `HBox` | dataSource, targetKeys, titles, render, onChange, onSelectChange, showSearch, bindValue |
| **TreeSelectAnt** | component.composite | `HBox` | disabled, expanded, hasChildren, tree, multiple, onSelect, onMultipleSelect, bindValue |
| **UploadAnt** | component.composite | `VBox` | type, listType, multiple, directory, showUploadList, noUploadList, accept, buttonText, dragText, hintText, onChange, onRemove |
| **VBarAnt** | component.composite | `VBarAnt` | top, center, bottom, gap, alignment, compact |
| **WatermarkAnt** | component.composite | `StackPane` | content, text, image, customNode, rotate, opacity, fontSize, gapX, gapY, imageWidth, imageHeight, color, fontFamily, fontWeight, fontGap, preventRemoval, wrap, controllerOf |
| **WindowControlAnt** | component.composite | `WindowControlAnt` | target, minimizeVisible, maximizeVisible, closeVisible, maximized, requestClose |
| **AccordionAnt** | component.control | `VBox` | pane, activeKey, activeKeys |
| **ButtonAnt** | component.control | `ButtonAnt` | link, compactLink, iconOnly, type, size, shape, rounded, square, loading, ghost, block, text, icon, contentDisplay, onClick, command |
| **CanvasAnt** | component.control | `CanvasAnt` | size, onDraw, gc, clear, fill, stroke, redraw |
| **CheckBoxAnt** | component.control | `CheckBoxAnt` | text, selected, indeterminate, allowIndeterminate, size, shape, onChange, bindValue |
| **ChoiceBoxAnt** | component.control | `ChoiceBoxAnt<T>` | items, value, onSelect, size, bindValue |
| **ColorPickerAnt** | component.control | `ColorPickerAnt` | value, onChange, size, bindValue, promptText |
| **ComboBoxAnt** | component.control | `ComboBoxAnt<T>` | items, value, placeholder, editable, size, onChange, bindValue |
| **DatePickerAnt** | component.control | `DatePickerAnt` | placeholder, value, editable, showWeekNumbers, size, onChange, bindValue |
| **HyperlinkAnt** | component.control | `HyperlinkAnt` | text, onClick, underline, visited |
| **IconAnt** | component.control | — | symbol, path |
| **InputAnt** | component.control | `HBox` | placeholder, text, size, status, readOnly, bindValue, createPassword, onChange, disabled |
| **LabelAnt** | component.control | `LabelAnt` | text, type, secondary, success, warning, danger, disabledColor, wrap, graphic, contentDisplay, align, strong, italic, underline, delete, code, mark, copyable |
| **ListViewAnt** | component.control | `ListViewAnt<T>` | items, placeholder, selectionMode, onSelect, onDoubleClick, fixedCellSize, editable |
| **MentionsAnt** | component.control | `MentionsAnt` | option, options, prefix, placeholder, rows, onSelect, onChange, bindValue |
| **MenuBarAnt** | component.control | — | menu, clearMenus, item, disabled, accelerator, divider, subMenu, endMenu, endSubMenu |
| **MenuButtonAnt** | component.control | `MenuButtonAnt` | text, size, rounded, square, icon, contentDisplay, arrowStyle, noArrow, item, itemDisabled, separator, add, focusTraversable |
| **PaginationAnt** | component.control | `PaginationAnt` | controllerOf, next, prev, first, last, pageCount, currentPage, maxPageIndicatorCount, onChange |
| **RadioButtonAnt** | component.control | `RadioButtonAnt` | text, selected, size, shape, toggleGroup, onChange, bindValue |
| **SearchInputAnt** | component.control | — | placeholder, text, size, allowClear, onChange, onSearch, bindValue, clear, input, clearButton |
| **SeparatorAnt** | component.control | `SeparatorAnt` | orientation, halignment, valignment |
| **SpinnerAnt** | component.control | `SpinnerAnt` | value, min, max, step, precision, editable, placeholder, onChange, bindValue, size, fromString |
| **SplitButtonAnt** | component.control | `SplitButtonAnt` | text, size, arrowStyle, rounded, square, icon, contentDisplay, onClick, item, itemDisabled, separator, add, focusTraversable |
| **StatusBarAnt** | component.control | `StatusBarAnt` | info, status, progress, action, left, center, right, updateInfo, updateProgress, updateStatus |
| **TableAnt** | component.control | `TableAnt<T>` | column, numberColumn, booleanColumn, nodeColumn, actionColumn, columnWidth, columnMinWidth, columnMaxWidth, columnResizable, columnSortable, columnSorter, columnAlign, headerAlign, contentAlign, columnVisible, action, type, danger |
| **TextAreaAnt** | component.control | `TextAreaAnt` | placeholder, text, rows, wrapText, editable, onChange, bindValue, readOnly |
| **TitledPaneAnt** | component.control | `TitledPaneAnt` | title, content, expanded, animated, collapsible |
| **ToggleButtonAnt** | component.control | `ToggleButtonAnt` | mandatoryGroup, text, icon, contentDisplay, size, shape, rounded, square, selected, toggleGroup, onAction, onChange, bindValue |
| **ToolBarAnt** | component.control | `ToolBarAnt` | orientation, button, item, divider, spacer, addButton, addItem, addDivider, addSpacer, clear |
| **TooltipAnt** | component.control | `TooltipAnt` | text, styleClass, style, delay, duration, hideDelay, install |
| **TreeAnt** | component.control | `TreeAnt<T>` | leaf, node, root, showRoot, valueExtractor, onSelect |
| **TreeTableAnt** | component.control | `TreeTableAnt<T>` | column, root, onSelect, multiSelect, expandAll, collapseAll, expandToLevel, child, endChild, endRoot |
| **TypographyAnt** | component.control | `Label` | ellipsis, rows, type, copyable, strong, italic, underline, delete, code, mark, title, paragraph, text |
| **AnchorPaneAnt** | component.layout | — | — |
| **BorderPaneAnt** | component.layout | — | — |
| **DividerAnt** | component.layout | `Node` | text, orientation, vertical, position |
| **FlexAnt** | component.layout | `Pane` | createVertical, direction, justify, align, wrap, gap, rowGap, columnGap, children, child |
| **FlowPaneAnt** | component.layout | — | — |
| **GridAnt** | component.layout | `VBox` | of, row, col, span, offset, xs, sm, md, lg, xl, xxl, xsOffset, smOffset, mdOffset, lgOffset, xlOffset, xxlOffset, responsive |
| **HBoxAnt** | component.layout | — | — |
| **ScrollPaneAnt** | component.layout | — | padding |
| **SpaceAnt** | component.layout | `Pane` | createVertical, direction, size, align, split, children |
| **SplitPaneAnt** | component.layout | — | — |
| **StackPaneAnt** | component.layout | — | — |
| **TextFlowAnt** | component.layout | — | — |
| **TilePaneAnt** | component.layout | — | — |
| **VBoxAnt** | component.layout | — | — |
| **AlertAnt** | component.overlay | — | info, warning, error, confirm, title, content, okText, cancelText, onOk, onCancel, onResult, graphic, showAndWaitForOk |
| **ContextMenuAnt** | component.overlay | `ContextMenuResult` | divider, target, item, disabled, accelerator, onSelect, onSelectItem, show, hide |
| **DesktopNotificationAnt** | component.overlay | `DesktopResult` | title, description, content, type, placement, duration, closable, closeOnClick, onClose, onClick, show, success, error, warning, info, changed |
| **DrawerAnt** | component.overlay | `DrawerResult` | title, content, placement, width, height, maskClosable, onClose, footer, size, extra, keyboard, closePlacement, open, close |
| **DropdownAnt** | component.overlay | `DropdownResult` | divider, trigger, showArrow, item, items, onSelect, onSelectItem, placement, show, hide |
| **MessageAnt** | component.overlay | `MessageResult` | content, type, duration, position, show, close, success, error, warning, info, loading, changed |
| **ModalAnt** | component.overlay | `ModalResult` | title, content, maskClosable, width, height, maxHeight, minHeight, onClose, footer, noFooter, centered, keyboard, closePlacement, confirmLoading, okText, cancelText, onOk, bindToLocale |
| **NotificationAnt** | component.overlay | `NotificationResult` | title, description, content, type, placement, duration, closable, onClose, onClick, show, success, error, warning, info |
| **PopconfirmAnt** | component.overlay | `Popconfirm` | title, description, okText, cancelText, onConfirm, onCancel, target, placement, show, hide |
| **PopoverAnt** | component.overlay | `Popover` | title, content, target, placement, trigger, onOpenChange, show, hide, close |
| **PromptDialogAnt** | component.overlay | `PromptDialogResult` | title, message, defaultValue, placeholder, okText, cancelText, onConfirm, onCancel, width, open, close |
| **AppShellAnt** | layout | `BorderPane` | header, sider, content, footer, collapsible, collapsed, collapsedWidth, breakpoint, trigger, headerBackground, siderBackground, contentBackground, footerBackground, onCollapseChange, buildResult, toggle, collapsedProperty |
| **LayoutAnt** | layout | `BorderPane` | header, sider, content, footer |
| **CrudTemplate** | template | `BorderPane` | title, topLeft, topRight, body, bottomLeft, bottomRight, bordered, background, topbarSpacing, bottombarSpacing, sectionGap |
| **DashboardTemplate** | template | `VBox` | welcome, stat, statColumns, bottomLeft, bottomRight, leftRatio, sectionGap, padding |
| **FilterBarAnt** | template | `HBox` | spacing, padding, search, filter, action, actionPrimary, actionNode |
| **LaunchPadTemplate** | template | `VBox` | title, description, columns, gap, onAction, item |
| **LoginTemplate** | template | `BorderPane` | brandName, tagline, feature, features, copyright, bannerWidth, formTitle, formSubtitle, usernamePlaceholder, passwordPlaceholder, submitText, showRememberMe, onSubmit, onForgot, onRegister |
| **PageTemplate** | template | `VBox` | title, description, body, section, sections, headerGap, sectionGap, headerToBodyGap, padding, background |
| **ProjectChangelogTemplate** | template | `VBox` | changelog, ChangeEntry, title, description, onAction, version |
| **ProjectConsoleTemplate** | template | `BorderPane` | userMenu, headerActions, statusBar, standardMenuBar, SidebarLink, SidebarSection, SidebarResult, sidebarLink, sidebarSection, sidebarPane, brand, brandSubtitle, brandIcon, headerTop, headerCenter, headerRight, menuBar, headerLeft |
| **ProjectDashboardTemplate** | template | `VBox` | demo, pageTitle, pageSubtitle, pageDescription, currentUser, version, branch, buildStatus, mode, templateCount, componentCount, updatedAt, completion, icon, mergeFrom, title, description, onAction |
| **ProjectFeatureTemplate** | template | `VBox` | title, description, columns, gap, onAction, feature |
| **ProjectHeroTemplate** | template | `VBox` | userMenu, title, subtitle, description, icon, status, meta, action, actions |
| **ProjectOverviewTemplate** | template | `VBox` | title, description, status, tech, metric, meta, milestone, activity, progress, onAction |
| **ProjectQuickStartTemplate** | template | `VBox` | title, description, step |
| **ProjectReleaseTemplate** | template | `VBox` | title, description, status, meta, release, change, readiness, onAction |
| **ProjectShowcaseTemplate** | template | `VBox` | title, description, hero, section, sections |
| **ShowcaseSectionTemplate** | template | `VBox` | title, description, body |
| **WorkspaceSettingsTemplate** | template | `VBox` | drawer, themeManager, watermarkVisibleSupplier, onWatermarkVisibleChanged, title, description, fromString |
| **WorkspaceTemplate** | template | `BorderPane` | userMenu, brand, brandSubtitle, brandIcon, header, headerLeft, headerTop, headerCenter, headerRight, headerGap, sider, siderWidth, content, footer, collapsible, collapsed, collapsedWidth, trigger |
