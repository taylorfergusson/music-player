package com.musicplayer.model;

/**
 * Represents a listener that pays attention to which view is selected, whether album, playlist, artist, songs, etc
 */
public interface ViewChangeListener {
    void setModel();
    void onViewChange(String selectedView);
}
