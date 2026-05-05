package ru.kafpin.lb4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainController {

    @FXML
    private PanelController leftPanelController;
    @FXML
    private PanelController rightPanelController;

    @FXML
    void onCopy(ActionEvent event) {
        performCopyMove(false);
    }

    @FXML
    void onMove(ActionEvent event) {
        performCopyMove(true);
    }

    @FXML
    void onDelete(ActionEvent event) {
        if (leftPanelController.selectedFileName() == null && rightPanelController.selectedFileName() == null) {
            showError("Не выбран файл для удаления");
            return;
        }

        PanelController src = (leftPanelController.selectedFileName() != null)
                ? leftPanelController : rightPanelController;

        Path srcPath = Paths.get(src.getPath(), src.selectedFileName());

        try {
            Files.delete(srcPath);
            src.updateList(Paths.get(src.getPath()));
        } catch (IOException e) {
            showError("Ошибка удаления файла: " + e.getMessage());
        }
    }

    @FXML
    void onExit(ActionEvent event) {
        System.exit(0);
    }

    private void performCopyMove(boolean move) {
        if (leftPanelController.selectedFileName() == null && rightPanelController.selectedFileName() == null) {
            showError("Не выбран файл для " + (move ? "перемещения" : "копирования"));
            return;
        }

        PanelController src, dst;
        if (leftPanelController.selectedFileName() != null) {
            src = leftPanelController;
            dst = rightPanelController;
        } else {
            src = rightPanelController;
            dst = leftPanelController;
        }

        Path srcPath = Paths.get(src.getPath(), src.selectedFileName());
        Path dstPath = Paths.get(dst.getPath()).resolve(srcPath.getFileName());

        try {
            if (move) {
                Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            }
            dst.updateList(Paths.get(dst.getPath()));
            src.updateList(Paths.get(src.getPath()));
        } catch (IOException e) {
            showError("Ошибка " + (move ? "перемещения" : "копирования") + " файла: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}