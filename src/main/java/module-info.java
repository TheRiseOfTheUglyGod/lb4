module ru.kafpin.lb4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.kafpin.lb4 to javafx.fxml;
    exports ru.kafpin.lb4;
}