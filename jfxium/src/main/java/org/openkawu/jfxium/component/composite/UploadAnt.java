package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.css.CssClasses;
import org.openkawu.jfxium.core.i18n.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Upload - 对标 Ant Design Upload。
 *
 * 重构：drag 区域 / icon / 文字 / 文件项 / 移除按钮 全部走 LESS（{@code upload-*}），
 * 拖拽进入态通过 {@link CssClasses#UPLOAD_DRAG_ACTIVE} 修饰类切换，不再 inline 重写整段 setStyle。
 */
public class UploadAnt {

    public enum Type {
        SELECT, DRAG
    }

    public enum ListType {
        TEXT, PICTURE, PICTURE_CARD
    }

    public static class UploadFile {
        String name;
        double size;
        String status; // "uploading", "done", "error", "removed"
        double percent;

        public UploadFile(String name, double size) {
            this.name = name;
            this.size = size;
            this.status = "done";
            this.percent = 100;
        }
    }

    public static class Builder {
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

        public Builder type(Type type) { this.type = type; return this; }
        public Builder listType(ListType listType) { this.listType = listType; return this; }
        public Builder multiple(boolean multiple) { this.multiple = multiple; return this; }
        public Builder multiple() { return multiple(true); }
        public Builder directory(boolean directory) { this.directory = directory; return this; }
        public Builder showUploadList(boolean show) { this.showUploadList = show; return this; }
        public Builder noUploadList() { return showUploadList(false); }
        public Builder accept(String accept) { this.accept = accept; return this; }
        public Builder buttonText(String text) { this.buttonText = text; return this; }
        public Builder dragText(String text) { this.dragText = text; return this; }
        public Builder hintText(String text) { this.hintText = text; return this; }
        public Builder onChange(Consumer<List<File>> onChange) { this.onChange = onChange; return this; }
        public Builder onRemove(Consumer<File> onRemove) { this.onRemove = onRemove; return this; }

        public VBox build() {
            VBox upload = new VBox(8);
            upload.getStyleClass().add(CssClasses.UPLOAD);

            if (type == Type.SELECT) {
                upload.getChildren().add(buildSelectUpload());
            } else {
                upload.getChildren().add(buildDragUpload());
            }

            if (showUploadList) {
                upload.getChildren().add(buildFileList());
            }
            return upload;
        }

        private HBox buildSelectUpload() {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);

            Button uploadBtn = ButtonAnt.create(buttonText != null ? buttonText : Messages.get("upload.button"))
                    .type(ButtonAnt.Type.PRIMARY)
                    .build();

            FileChooser fileChooser = new FileChooser();
            if (!accept.equals("*")) {
                String[] extensions = accept.split(",");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", extensions));
            }

            uploadBtn.setOnAction(e -> {
                List<File> files;
                if (multiple) {
                    files = fileChooser.showOpenMultipleDialog(uploadBtn.getScene().getWindow());
                } else {
                    File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
                    files = file != null ? List.of(file) : null;
                }
                if (files != null) handleFiles(files);
            });

            container.getChildren().add(uploadBtn);
            return container;
        }

        private StackPane buildDragUpload() {
            StackPane dragArea = new StackPane();
            dragArea.getStyleClass().add(CssClasses.UPLOAD_DRAG);

            VBox content = new VBox(12);
            content.setAlignment(Pos.CENTER);

            SVGPath icon = new SVGPath();
            icon.setContent("M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z");
            icon.setScaleX(2);
            icon.setScaleY(2);
            icon.getStyleClass().add(CssClasses.UPLOAD_DRAG_ICON);

            Label dragLabel = new Label(dragText != null ? dragText : Messages.get("upload.drag"));
            dragLabel.getStyleClass().add(CssClasses.UPLOAD_DRAG_TEXT);
            Label hintLabel = new Label(hintText != null ? hintText : Messages.get("upload.hint"));
            hintLabel.getStyleClass().add(CssClasses.UPLOAD_HINT_TEXT);

            content.getChildren().addAll(icon, dragLabel, hintLabel);
            dragArea.getChildren().add(content);

            // 拖拽进入：加修饰类切换主题色边框；离开：移除修饰类。不再重写整段 setStyle。
            dragArea.setOnDragOver(e -> {
                if (e.getGestureSource() != dragArea && e.getDragboard().hasFiles()) {
                    e.acceptTransferModes(TransferMode.COPY);
                    if (!dragArea.getStyleClass().contains(CssClasses.UPLOAD_DRAG_ACTIVE)) {
                        dragArea.getStyleClass().add(CssClasses.UPLOAD_DRAG_ACTIVE);
                    }
                }
                e.consume();
            });
            dragArea.setOnDragExited(e -> dragArea.getStyleClass().remove(CssClasses.UPLOAD_DRAG_ACTIVE));
            dragArea.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasFiles()) handleFiles(db.getFiles());
                e.setDropCompleted(db.hasFiles());
                dragArea.getStyleClass().remove(CssClasses.UPLOAD_DRAG_ACTIVE);
                e.consume();
            });

            // 点击 drag 区域同样触发文件选择
            dragArea.setOnMouseClicked(e -> {
                FileChooser fileChooser = new FileChooser();
                if (!accept.equals("*")) {
                    String[] extensions = accept.split(",");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", extensions));
                }
                List<File> files;
                if (multiple) {
                    files = fileChooser.showOpenMultipleDialog(dragArea.getScene().getWindow());
                } else {
                    File file = fileChooser.showOpenDialog(dragArea.getScene().getWindow());
                    files = file != null ? List.of(file) : null;
                }
                if (files != null) handleFiles(files);
            });

            return dragArea;
        }

        private VBox buildFileList() {
            VBox list = new VBox(4);
            list.getStyleClass().add(CssClasses.UPLOAD_LIST);

            for (UploadFile file : fileList) {
                HBox fileItem = new HBox(8);
                fileItem.setAlignment(Pos.CENTER_LEFT);
                fileItem.getStyleClass().add(CssClasses.UPLOAD_FILE_ITEM);

                Label nameLabel = new Label(file.name);
                nameLabel.getStyleClass().add(CssClasses.UPLOAD_FILE_NAME);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                fileItem.getChildren().add(nameLabel);

                if (file.status.equals("uploading")) {
                    ProgressBar progressBar = new ProgressBar(file.percent / 100.0);
                    progressBar.setPrefWidth(100);
                    fileItem.getChildren().add(progressBar);
                } else if (file.status.equals("error")) {
                    Label errorLabel = new Label(Messages.get("upload.error"));
                    errorLabel.getStyleClass().add(CssClasses.UPLOAD_FILE_ERROR);
                    fileItem.getChildren().add(errorLabel);
                }

                Button removeBtn = new Button("×");
                removeBtn.getStyleClass().add(CssClasses.UPLOAD_REMOVE_BTN);
                removeBtn.setOnAction(e -> {
                    fileList.remove(file);
                    if (onRemove != null) onRemove.accept(new File(file.name));
                });
                fileItem.getChildren().add(removeBtn);

                list.getChildren().add(fileItem);
            }
            return list;
        }

        private void handleFiles(List<File> files) {
            for (File file : files) {
                fileList.add(new UploadFile(file.getName(), file.length()));
            }
            if (onChange != null) onChange.accept(files);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
