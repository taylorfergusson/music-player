package com.musicplayer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;;

/**
 * Represents a playlist in the library that is currently selected
 * Necessary for playlist view to keep a consistent format between each playlist
 */
public class SelectedPlaylist {
    private static volatile SelectedPlaylist INSTANCE = null;
    private Playlist playlist;
    private GridPane artworkGridPane = new GridPane();
    private StringProperty title = new SimpleStringProperty("New Playlist");
    private StringProperty songNumber = new SimpleStringProperty();
    private StringProperty length = new SimpleStringProperty();

    private ObservableList<Song> songList = FXCollections.observableArrayList();
    
    private SelectedPlaylist(){
    }

    /**
     * Ensures that there's only one selected playlist at a time
     * 
     * @return Singleton instance of SelectedPlaylist
     */
    public static SelectedPlaylist getInstance() {
        if (INSTANCE == null) {
            synchronized (SelectedAlbum.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SelectedPlaylist();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Sets the playlist page info to the info contained in the given playlist object
     *
     * @param playlist
     *                The playlist object containing the data that will be showed in playlist view
     * @pre playlist != null;
     */
    public void setPlaylistData(Playlist playlist){
        assert playlist != null;

        this.playlist = playlist;
        
        title.set(playlist.getTitle().get());
        songNumber.set(playlist.getSongNumber() + " · ");
        length.set(playlist.getLength());
        artworkGridPane.getChildren().clear();
        artworkGridPane.getChildren().addAll(playlist.getArtworkGridPane().getChildren());

        this.songList.setAll(playlist.getSongList());
    }

    public Playlist getPlaylist() {
        return this.playlist;
    }

    public GridPane getArtworkGridPane() {
        return this.artworkGridPane;
    }

    public StringProperty getTitle() {
        return this.title;
    }

    /**
     * Sets the title instance attribute to the value inside the title
     * Changes the title for selectedplaylist and the original playlist object
     *
     * @param title
     *                A string representing the desired playlist title
     * @pre title != null;
     */
    public void setTitle(String title) {
        assert title != null;
        this.title.set(title);
        playlist.setTitle(title);
    }

    public StringProperty getSongNumber() {
        return this.songNumber;
    }

    public StringProperty getLength() {
        return this.length;
    }

    public ObservableList<Song> getSongList() {
        return this.songList;
    }
} 