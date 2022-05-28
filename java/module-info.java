module application.whatsup {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires spring.security.crypto;
    requires java.sql;
    requires java.desktop;

    opens application.whatsup to javafx.fxml;
    exports application.whatsup;
    exports application.whatsup.controllers;
    opens application.whatsup.controllers to javafx.fxml;
}