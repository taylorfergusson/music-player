module com.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires jaudiotagger;

    opens com.musicplayer to javafx.fxml;
    exports com.musicplayer;

    exports com.musicplayer.controller;
    opens com.musicplayer.controller;

    exports com.musicplayer.model to javafx.base, javafx.fxml;

    opens com.musicplayer.model to javafx.base;
}