module ru.biluta.task1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires static lombok;
    requires commons.math3;

    opens ru.biluta.task1 to javafx.fxml;
    exports ru.biluta.task1;
    exports ru.biluta.task1.ui;
    opens ru.biluta.task1.ui to javafx.fxml;
}