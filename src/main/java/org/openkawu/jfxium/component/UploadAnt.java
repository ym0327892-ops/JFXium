package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Upload Component
 * Inspired by Ant Design Upload
 * Upload files by clicking or dragging.
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
        private String buttonText = "Click to Upload";
        private String dragText = "Click or drag file to this area to upload";
        private String hintText = "Support for single or bulk upload";
        private Consumer<List<File>> onChange = null;
        private Consumer<File> onRemove = null;
        private List<UploadFile> fileList = new ArrayList<>();

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder listType(ListType listType) {
            this.listType = listType;
            return this;
        }

        public Builder multiple(boolean multiple) {
            this.multiple = multiple;
            return this;
        }

        public Builder multiple() {
            return multiple(true);
        }

        public Builder directory(boolean directory) {
            this.directory = directory;
            return this;
        }

        public Builder showUploadList(boolean show) {
            this.showUploadList = show;
            return this;
        }

        public Builder noUploadList() {
            return showUploadList(false);
        }

        public Builder accept(String accept) {
            this.accept = accept;
            return this;
        }

        public Builder buttonText(String text) {
            this.buttonText = text;
            return this;
        }

        public Builder dragText(String text) {
            this.dragText = text;
            return this;
        }

        public Builder hintText(String text) {
            this.hintText = text;
            return this;
        }

        public Builder onChange(Consumer<List<File>> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder onRemove(Consumer<File> onRemove) {
            this.onRemove = onRemove;
            return this;
        }

        public VBox build() {
            VBox upload = new VBox(8);
            upload.getStyleClass().add("upload");

            if (type == Type.SELECT) {
                upload.getChildren().add(buildSelectUpload());
            } else {
                upload.getChildren().add(buildDragUpload());
            }

            // File list
            if (showUploadList) {
                VBox fileListBox = buildFileList();
                upload.getChildren().add(fileListBox);
            }

            return upload;
        }

        private HBox buildSelectUpload() {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);

            javafx.scene.control.Button uploadBtn = ButtonAnt.create(buttonText)
                .type(ButtonAnt.Type.PRIMARY)
                .build();

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            if (!accept.equals("*")) {
                String[] extensions = accept.split(",");
                javafx.stage.FileChooser.ExtensionFilter filter = new javafx.stage.FileChooser.ExtensionFilter("Files", extensions);
                fileChooser.getExtensionFilters().add(filter);
            }

            uploadBtn.setOnAction(e -> {
                List<File> files;
                if (multiple) {
                    files = fileChooser.showOpenMultipleDialog(uploadBtn.getScene().getWindow());
                } else {
                    File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
                    files = file != null ? List.of(file) : null;
                }

                if (files != null) {
                    handleFiles(files);
                }
            });

            container.getChildren().add(uploadBtn);
            return container;
        }

        private StackPane buildDragUpload() {
            StackPane dragArea = new StackPane();
            dragArea.getStyleClass().add("upload-drag");
            dragArea.setStyle(
                "-fx-background-color: -color-bg-subtle;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-border-style: dashed;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 32px;" +
                "-fx-min-height: 180px;"
            );

            VBox content = new VBox(12);
            content.setAlignment(Pos.CENTER);

            // Upload icon
            SVGPath icon = new SVGPath();
            icon.setContent("M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z");
            icon.setScaleX(2);
            icon.setScaleY(2);
            icon.setStyle("-fx-fill: -color-accent-emphasis;");

            Label dragLabel = new Label(dragText);
            dragLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 16px;");

            Label hintLabel = new Label(hintText);
            hintLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

            content.getChildren().addAll(icon, dragLabel, hintLabel);
            dragArea.getChildren().add(content);

            // Drag and drop events
            dragArea.setOnDragOver(e -> {
                if (e.getGestureSource() != dragArea && e.getDragboard().hasFiles()) {
                    e.acceptTransferModes(TransferMode.COPY);
                    dragArea.setStyle(
                        "-fx-background-color: -color-accent-subtle;" +
                        "-fx-border-color: -color-accent-emphasis;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-style: dashed;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 32px;" +
                        "-fx-min-height: 180px;"
                    );
                }
                e.consume();
            });

            dragArea.setOnDragExited(e -> {
                dragArea.setStyle(
                    "-fx-background-color: -color-bg-subtle;" +
                    "-fx-border-color: -color-border-default;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-style: dashed;" +
                    "-fx-border-radius: 8px;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-padding: 32px;" +
                    "-fx-min-height: 180px;"
                );
            });

            dragArea.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasFiles()) {
                    handleFiles(db.getFiles());
                }
                e.setDropCompleted(db.hasFiles());
                e.consume();
            });

            // Click to upload
            dragArea.setOnMouseClicked(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                if (!accept.equals("*")) {
                    String[] extensions = accept.split(",");
                    javafx.stage.FileChooser.ExtensionFilter filter = new javafx.stage.FileChooser.ExtensionFilter("Files", extensions);
                    fileChooser.getExtensionFilters().add(filter);
                }

                List<File> files;
                if (multiple) {
                    files = fileChooser.showOpenMultipleDialog(dragArea.getScene().getWindow());
                } else {
                    File file = fileChooser.showOpenDialog(dragArea.getScene().getWindow());
                    files = file != null ? List.of(file) : null;
                }

                if (files != null) {
                    handleFiles(files);
                }
            });

            return dragArea;
        }

        private VBox buildFileList() {
            VBox list = new VBox(4);
            list.getStyleClass().add("upload-list");
            list.setStyle("-fx-padding: 8px 0;");

            for (UploadFile file : fileList) {
                HBox fileItem = new HBox(8);
                fileItem.setAlignment(Pos.CENTER_LEFT);
                fileItem.setStyle("-fx-padding: 8px; -fx-background-color: -color-bg-subtle; -fx-background-radius: 4px;");

                Label nameLabel = new Label(file.name);
                nameLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px;");
                HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

                fileItem.getChildren().add(nameLabel);

                if (file.status.equals("uploading")) {
                    ProgressBar progressBar = new ProgressBar(file.percent / 100.0);
                    progressBar.setPrefWidth(100);
                    fileItem.getChildren().add(progressBar);
                } else if (file.status.equals("error")) {
                    Label errorLabel = new Label("Error");
                    errorLabel.setStyle("-fx-text-fill: -color-danger-emphasis; -fx-font-size: 12px;");
                    fileItem.getChildren().add(errorLabel);
                }

                // Remove button
                javafx.scene.control.Button removeBtn = new javafx.scene.control.Button("×");
                removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-fg-muted; -fx-font-size: 16px; -fx-cursor: hand;");
                removeBtn.setOnAction(e -> {
                    fileList.remove(file);
                    if (onRemove != null) {
                        onRemove.accept(new File(file.name));
                    }
                });
                fileItem.getChildren().add(removeBtn);

                list.getChildren().add(fileItem);
            }

            return list;
        }

        private void handleFiles(List<File> files) {
            for (File file : files) {
                UploadFile uploadFile = new UploadFile(file.getName(), file.length());
                fileList.add(uploadFile);
            }

            if (onChange != null) {
                onChange.accept(files);
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
