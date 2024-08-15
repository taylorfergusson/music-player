package com.musicplayer.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;;

/**
 * Represents an album in the library that is currently selected
 * Necessary for album view to keep a consistent format for each album
 */
public class SelectedAlbum {
    private static volatile SelectedAlbum INSTANCE = null;
    private Album album;
    private ObjectProperty<Image> albumArtwork = new SimpleObjectProperty<>();
    private StringProperty albumTitle = new SimpleStringProperty("Unknown Album");
    private StringProperty albumArtistName = new SimpleStringProperty("Unknown Artist");
    private StringProperty albumGenre = new SimpleStringProperty();
    private StringProperty albumYear = new SimpleStringProperty();
    private StringProperty albumSongNumber = new SimpleStringProperty();
    private StringProperty albumLength = new SimpleStringProperty();
    private ObservableList<Song> songList = FXCollections.observableArrayList();

    private SelectedAlbum(){
    }

    /**
     * Ensures that there's only one selected album at a time
     * 
     * @return Singleton instance of SelectedAlbum
     */
    public static SelectedAlbum getInstance() {
        if (INSTANCE == null) {
            synchronized (SelectedAlbum.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SelectedAlbum();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Sets the album page info to the info contained in the given album object
     *
     * @param album
     *                An album object containing the album data to be set up on the album page
     * @pre album != null;
     */
    public void setAlbumData(Album album){
        assert album != null;

        this.album = album;
        
        albumArtwork.set(album.getCoverArt());
        if (!album.getAlbumTitle().equals("")){
            albumTitle.set(album.getAlbumTitle());
        } else {
            albumTitle.set("Unknown Album");
        }
        if (!album.getAlbumArtistName().equals("")){
            albumArtistName.set(album.getAlbumArtistName());
        } else {
            albumTitle.set("Unknown Artist");
        }
        if (!album.getGenre().equals("")){
            albumGenre.set(album.getGenre() + " · ");
        }
        if (!album.getYear().equals("")){
            albumYear.set(album.getYear() + " · ");
        }
        albumSongNumber.set(album.getNumberOfSongs() + " · ");
        albumLength.set(album.getTotalLength());

        this.songList.setAll(album.getSongList());
    }

    public Album getAlbum() {
        return this.album;
    }

    public ObjectProperty<Image> getAlbumArtwork() {
        return this.albumArtwork;
    }

    public StringProperty getAlbumTitle() {
        return this.albumTitle;
    }

    public StringProperty getAlbumArtistName() {
        return this.albumArtistName;
    }

    public StringProperty getAlbumGenre() {
        return this.albumGenre;
    }

    public StringProperty getAlbumYear() {
        return this.albumYear;
    }

    public StringProperty getAlbumSongNumber() {
        return this.albumSongNumber;
    }

    public StringProperty getAlbumLength() {
        return this.albumLength;
    }

    public ObservableList<Song> getSongList() {
        return this.songList;
    }
} 