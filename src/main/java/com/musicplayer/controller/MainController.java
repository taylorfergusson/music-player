package com.musicplayer.controller;
import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * Controller for creating the library singleton instance and loading all the icons into the program
 */
public class MainController {

    @FXML
    private ImageView backgroundImageView;

    Library myLibrary;

    /**
     * Creates library
     */
    public MainController() {
        Library.createLibrary("Taylor's Library");
        myLibrary = Library.getLibrary();
        loadIcons();
    }

    /**
     * Sets the background image of the app to the currently loaded song's
     * album art upon initialization
     */
    public void initialize() {
        backgroundImageView.imageProperty().bind(AudioPlayerManager.getInstance().getCoverArt());
    }

    /**
     * Loads all the icons from the img folder into the IconCache for easy access later
     */
    private void loadIcons() {
        IconCache.loadIcon("play", "/img/icons/play.png");
        IconCache.loadIcon("pause", "/img/icons/pause.png");
        IconCache.loadIcon("rewind", "/img/icons/rewind.png");
        IconCache.loadIcon("fast-forward", "/img/icons/fast-forward.png");
        IconCache.loadIcon("shuffle-off", "/img/icons/shuffle-off.png");
        IconCache.loadIcon("shuffle", "/img/icons/shuffle.png");
        IconCache.loadIcon("repeat-off", "/img/icons/repeat-off.png");
        IconCache.loadIcon("repeat", "/img/icons/repeat.png");
        IconCache.loadIcon("repeat-one", "/img/icons/repeat-one.png");
        IconCache.loadIcon("queue", "/img/icons/queue.png");
        IconCache.loadIcon("speaker", "/img/icons/speaker.png");
        IconCache.loadIcon("loud-speaker", "/img/icons/loud-speaker.png");
        IconCache.loadIcon("back", "/img/icons/back.png");
        IconCache.loadIcon("forward", "/img/icons/forward.png");
        IconCache.loadIcon("heart", "/img/icons/heart.png");
        IconCache.loadIcon("heart-filled", "/img/icons/heart-filled.png");
        IconCache.loadIcon("default-art", "/img/default-art.jpg");
    }
}
