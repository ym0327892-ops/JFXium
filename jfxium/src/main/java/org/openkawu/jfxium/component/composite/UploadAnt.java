package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * JFXium 上传组件 - 对标 Ant Design Upload（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：文件上传控件，支持点击选择或拖拽上传，
 * 展示已上传文件列表。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>上传方式</b>：SELECT（点击选择）/ DRAG（拖拽上传）</li>
 *   <li><b>列表样式</b>：TEXT / PICTURE / PICTURE_CARD</li>
 *   <li>多文件上传 + 文件列表展示</li>
 *   <li>文件状态（uploading / done / error / removed）</li>
 *   <li>拖拽进入态视觉反馈（走 LESS {@code .upload-drag-active}）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node upload = UploadAnt.create()
 *     .type(UploadAnt.Type.DRAG)
 *     .multiple(true)
 *     .onChange(files -> System.out.println("文件数：" + files.size()))
 *     .build();
 * }</pre>
 */
public class UploadAnt {

    public enum Type {
        SELECT, DRAG
    }

    public enum ListType {
        TEXT, PICTURE, PICTURE_CARD
    }

    public static class UploadFile {
        File file;
        String name;
        double size;
        String status; // "uploading", "done", "error", "removed"
        double percent;

        public UploadFile(File file) {
            this.file = file;
            this.name = file != null ? file.getName() : "";
            this.size = file != null ? file.length() : 0;
            this.status = "done";
            this.percent = 100;
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Type type = Type.SELECT;
        private ListType listType = ListType.TEXT;
        private boolean multiple = false;
        private boolean directory = false;
        private boolean showUploadList = true;
        private String accept = "*";
        // null = 用 i18n 默认值；非 null = 调用方显式指定
        private String buttonText = null;
        private String dragText = null;
        private String hintText = null;
        private Consumer<List<File>> onChange = null;
        private Consumer<File> onRemove = null;
        private List<UploadFile> fileList = new ArrayList<>();
        private VBox uploadRoot;
        private VBox fileListView;

        public Builder type(Type type) { this.type = type != null ? type : Type.SELECT; return this; }
        public Builder listType(ListType listType) { this.listType = listType != null ? listType : ListType.TEXT; return this; }
        public Builder multiple(boolean multiple) { this.multiple = multiple; return this; }
        public Builder multiple() { return multiple(true); }
        public Builder directory(boolean directory) { this.directory = directory; return this; }
        public Builder showUploadList(boolean show) { this.showUploadList = show; return this; }
        public Builder noUploadList() { return showUploadList(false); }
        public Builder accept(String accept) { this.accept = accept != null ? accept : "*"; return this; }
        public Builder buttonText(String text) { this.buttonText = text; return this; }
        public Builder dragText(String text) { this.dragText = text; return this; }
        public Builder hintText(String text) { this.hintText = text; return this; }
        public Builder onChange(Consumer<List<File>> onChange) { this.onChange = onChange; return this; }
        public Builder onRemove(Consumer<File> onRemove) { this.onRemove = onRemove; return this; }

        public VBox build() {
            uploadRoot = new VBox();
            uploadRoot.getStyleClass().add(JfxStyles.UPLOAD);

            if (type == Type.SELECT) {
                uploadRoot.getChildren().add(buildSelectUpload());
            } else {
                uploadRoot.getChildren().add(buildDragUpload());
            }

            if (showUploadList) {
                fileListView = new VBox();
                fileListView.getStyleClass().add(JfxStyles.UPLOAD_LIST);
                if (listType == ListType.PICTURE) {
                    fileListView.getStyleClass().add(JfxStyles.UPLOAD_LIST_PICTURE);
                } else if (listType == ListType.PICTURE_CARD) {
                    fileListView.getStyleClass().add(JfxStyles.UPLOAD_LIST_PICTURE_CARD);
                }
                refreshFileList();
                uploadRoot.getChildren().add(fileListView);
            }
            applyStyles(uploadRoot);
            return uploadRoot;
        }

        private HBox buildSelectUpload() {
            HBox container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);

            Button uploadBtn = ButtonAnt.create(buttonText != null ? buttonText : Messages.get("upload.button"))
                    .type(ButtonAnt.Type.PRIMARY)
                    .build();

            uploadBtn.setOnAction(e -> {
                List<File> files = chooseFiles(uploadBtn);
                if (files != null) handleFiles(files);
            });

            container.getChildren().add(uploadBtn);
            return container;
        }

        private StackPane buildDragUpload() {
            StackPane dragArea = new StackPane();
            dragArea.getStyleClass().add(JfxStyles.UPLOAD_DRAG);

            VBox content = new VBox();
            content.setAlignment(Pos.CENTER);

            SVGPath icon = new SVGPath();
            icon.setContent("M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z");
            icon.setScaleX(2);
            icon.setScaleY(2);
            icon.getStyleClass().add(JfxStyles.UPLOAD_DRAG_ICON);

            Label dragLabel = new Label(dragText != null ? dragText : Messages.get("upload.drag"));
            dragLabel.getStyleClass().add(JfxStyles.UPLOAD_DRAG_TEXT);
            Label hintLabel = new Label(hintText != null ? hintText : Messages.get("upload.hint"));
            hintLabel.getStyleClass().add(JfxStyles.UPLOAD_HINT_TEXT);

            content.getChildren().addAll(icon, dragLabel, hintLabel);
            dragArea.getChildren().add(content);

            // 拖拽进入：加修饰类切换主题色边框；离开：移除修饰类。不再重写整段 setStyle。
            dragArea.setOnDragOver(e -> {
                if (e.getGestureSource() != dragArea && e.getDragboard().hasFiles()) {
                    e.acceptTransferModes(TransferMode.COPY);
                    if (!dragArea.getStyleClass().contains(JfxStyles.UPLOAD_DRAG_ACTIVE)) {
                        dragArea.getStyleClass().add(JfxStyles.UPLOAD_DRAG_ACTIVE);
                    }
                }
                e.consume();
            });
            dragArea.setOnDragExited(e -> dragArea.getStyleClass().remove(JfxStyles.UPLOAD_DRAG_ACTIVE));
            dragArea.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasFiles()) handleFiles(db.getFiles());
                e.setDropCompleted(db.hasFiles());
                dragArea.getStyleClass().remove(JfxStyles.UPLOAD_DRAG_ACTIVE);
                e.consume();
            });

            // 点击 drag 区域同样触发文件选择
            dragArea.setOnMouseClicked(e -> {
                List<File> files = chooseFiles(dragArea);
                if (files != null) handleFiles(files);
            });

            return dragArea;
        }

        private void refreshFileList() {
            if (fileListView == null) {
                return;
            }
            fileListView.getChildren().clear();
            for (UploadFile file : fileList) {
                if (file == null) {
                    continue;
                }
                HBox fileItem = new HBox();
                fileItem.setAlignment(Pos.CENTER_LEFT);
                fileItem.getStyleClass().add(JfxStyles.UPLOAD_FILE_ITEM);
                if (listType == ListType.PICTURE) {
                    fileItem.getStyleClass().add(JfxStyles.UPLOAD_FILE_ITEM_PICTURE);
                } else if (listType == ListType.PICTURE_CARD) {
                    fileItem.getStyleClass().add(JfxStyles.UPLOAD_FILE_ITEM_PICTURE_CARD);
                }

                Node preview = createFilePreview(file);
                if (preview != null) {
                    fileItem.getChildren().add(preview);
                }

                Label nameLabel = new Label(file.name);
                nameLabel.getStyleClass().add(JfxStyles.UPLOAD_FILE_NAME);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                fileItem.getChildren().add(nameLabel);

                if ("uploading".equals(file.status)) {
                    ProgressBar progressBar = new ProgressBar(file.percent / 100.0);
                    progressBar.setPrefWidth(100);
                    fileItem.getChildren().add(progressBar);
                } else if ("error".equals(file.status)) {
                    Label errorLabel = new Label(Messages.get("upload.error"));
                    errorLabel.getStyleClass().add(JfxStyles.UPLOAD_FILE_ERROR);
                    fileItem.getChildren().add(errorLabel);
                }

                Button removeBtn = new Button("×");
                removeBtn.getStyleClass().add(JfxStyles.UPLOAD_REMOVE_BTN);
                removeBtn.setOnAction(e -> {
                    fileList.remove(file);
                    refreshFileList();
                    if (onRemove != null) onRemove.accept(file.file);
                    notifyChange();
                });
                fileItem.getChildren().add(removeBtn);

                fileListView.getChildren().add(fileItem);
            }
        }

        private void handleFiles(List<File> files) {
            List<File> acceptedFiles = normalizeSelectedFiles(files);
            for (File file : acceptedFiles) {
                fileList.add(new UploadFile(file));
            }
            refreshFileList();
            notifyChange();
        }

        private List<File> chooseFiles(Node ownerNode) {
            if (ownerNode.getScene() == null || ownerNode.getScene().getWindow() == null) {
                return null;
            }
            if (directory) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File dir = directoryChooser.showDialog(ownerNode.getScene().getWindow());
                if (dir == null) {
                    return null;
                }
                return normalizeSelectedFiles(List.of(dir));
            }

            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            if (multiple) {
                List<File> files = fileChooser.showOpenMultipleDialog(ownerNode.getScene().getWindow());
                return files != null ? normalizeSelectedFiles(files) : null;
            }
            File file = fileChooser.showOpenDialog(ownerNode.getScene().getWindow());
            return file != null ? normalizeSelectedFiles(List.of(file)) : null;
        }

        private void configureFileChooser(FileChooser fileChooser) {
            if (!accept.equals("*")) {
                String[] extensions = accept.split(",");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", extensions));
            }
        }

        private List<File> normalizeSelectedFiles(List<File> files) {
            if (files == null || files.isEmpty()) {
                return Collections.emptyList();
            }
            List<File> expanded = new ArrayList<>();
            for (File file : files) {
                collectAcceptedFiles(file, expanded);
            }
            return expanded;
        }

        private void collectAcceptedFiles(File file, List<File> result) {
            if (file == null || !file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                if (!directory) {
                    return;
                }
                File[] children = file.listFiles();
                if (children == null) {
                    return;
                }
                for (File child : children) {
                    collectAcceptedFiles(child, result);
                }
                return;
            }
            if (matchesAccept(file)) {
                result.add(file);
            }
        }

        private boolean matchesAccept(File file) {
            if ("*".equals(accept) || accept.isBlank()) {
                return true;
            }
            String name = file.getName().toLowerCase(Locale.ROOT);
            for (String rawPattern : accept.split(",")) {
                String pattern = rawPattern.trim().toLowerCase(Locale.ROOT);
                if (pattern.isEmpty()) {
                    continue;
                }
                if (pattern.startsWith("*.")) {
                    if (name.endsWith(pattern.substring(1))) {
                        return true;
                    }
                    continue;
                }
                if (pattern.startsWith(".")) {
                    if (name.endsWith(pattern)) {
                        return true;
                    }
                    continue;
                }
                if (name.endsWith("." + pattern)) {
                    return true;
                }
            }
            return false;
        }

        private Node createFilePreview(UploadFile file) {
            if (file == null || file.file == null) {
                return null;
            }
            if (listType == ListType.TEXT) {
                return null;
            }
            if (isImageFile(file.file)) {
                Image image = new Image(file.file.toURI().toString(), 48, 48, true, true, true);
                if (!image.isError()) {
                    ImageView imageView = new ImageView(image);
                    double size = listType == ListType.PICTURE_CARD ? 64 : 48;
                    imageView.setFitWidth(size);
                    imageView.setFitHeight(size);
                    imageView.setPreserveRatio(true);
                    StackPane thumb = new StackPane(imageView);
                    thumb.getStyleClass().add(JfxStyles.UPLOAD_FILE_THUMB);
                    thumb.setPrefSize(size, size);
                    thumb.setMinSize(size, size);
                    thumb.setMaxSize(size, size);
                    return thumb;
                }
            }

            double size = listType == ListType.PICTURE_CARD ? 64 : 48;
            StackPane placeholder = new StackPane(IconAnt.path(IconAnt.Path.FILE, listType == ListType.PICTURE_CARD ? 24 : 18));
            placeholder.getStyleClass().add(JfxStyles.UPLOAD_FILE_THUMB);
            placeholder.setPrefSize(size, size);
            placeholder.setMinSize(placeholder.getPrefWidth(), placeholder.getPrefHeight());
            placeholder.setMaxSize(placeholder.getPrefWidth(), placeholder.getPrefHeight());
            return placeholder;
        }

        private boolean isImageFile(File file) {
            String name = file.getName().toLowerCase(Locale.ROOT);
            return name.endsWith(".png")
                    || name.endsWith(".jpg")
                    || name.endsWith(".jpeg")
                    || name.endsWith(".gif")
                    || name.endsWith(".bmp")
                    || name.endsWith(".webp");
        }

        private void notifyChange() {
            if (onChange == null) {
                return;
            }
            List<File> currentFiles = new ArrayList<>(fileList.size());
            for (UploadFile uploadFile : fileList) {
                if (uploadFile != null && uploadFile.file != null) {
                    currentFiles.add(uploadFile.file);
                }
            }
            onChange.accept(currentFiles);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
