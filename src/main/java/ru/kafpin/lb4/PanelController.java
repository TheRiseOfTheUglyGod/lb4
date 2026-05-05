package ru.kafpin.lb4;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class PanelController {

    @FXML
    private ComboBox<String> diskOps;
    @FXML
    private TableColumn<FileInfo, String> filedateColumn;
    @FXML
    private TableColumn<FileInfo, String> filenameColumn;
    @FXML
    private TableView<FileInfo> filesTable;
    @FXML
    private TableColumn<FileInfo, Long> filesizeColumn;
    @FXML
    private TextField pathField;
    @FXML
    private TableColumn<FileInfo, String> typeColumn;

    @FXML
    void initialize() {
        typeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getType().getName()));
        filenameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFileName()));
        filesizeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getSize()));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        filedateColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));

        filesTable.getSortOrder().add(typeColumn);

        filesizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long aLong, boolean empty) {
                super.updateItem(aLong, empty);
                if (aLong == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text;
                    if (aLong == -1) {
                        text = "[DIR]";
                    } else {
                        text = String.format("%,d байт", aLong);
                    }
                    setText(text);
                }
            }
        });

        diskOps.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            diskOps.getItems().add(p.toString());
        }
        diskOps.getSelectionModel().select(0);

        updateList(Paths.get("."));
    }

    public void updateList(Path path) {
        filesTable.getItems().clear();
        try {
            pathField.setText(path.normalize().toString());
            filesTable.getItems().addAll(
                    Files.list(path)
                            .map(FileInfo::new)
                            .collect(Collectors.toList())
            );
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Не удалось обновить список файлов",
                    ButtonType.OK
            );
            alert.showAndWait();
        }
    }

    @FXML
    void onPassUp(ActionEvent event) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    @FXML
    void onSelectDisk(ActionEvent event) {
        updateList(Paths.get(diskOps.getSelectionModel().getSelectedItem()));
    }

    @FXML
    public void onTable(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Path path = Paths.get(pathField.getText())
                    .resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
            if (Files.isDirectory(path)) {
                updateList(path);
            }
        }
    }

    public String selectedFileName() {
        if (!filesTable.isFocused()) {
            return null;
        }
        FileInfo selected = filesTable.getSelectionModel().getSelectedItem();
        return selected != null ? selected.getFileName() : null;
    }

    public String getPath() {
        return pathField.getText();
    }
}