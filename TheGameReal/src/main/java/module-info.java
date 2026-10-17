module _2dhif.thegamereal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    exports app;
    exports _2dhif.thegamereal.control;
    exports _2dhif.thegamereal.model;
    exports _2dhif.thegamereal.view;
    opens app to javafx.graphics;
}