package com.musicplayer.controller;

import com.musicplayer.model.*;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller for each album item that is shown on the list of albums gridpane, represented by the album art, artist, and title
 */
public class AlbumItemController extends VBox {
    @FXML
    private ToggleButton playPauseButton;

    @FXML
    private ImageView playPauseImage;

    @FXML
    private Region overlay;

    @FXML
    private ImageView albumCover;

    @FXML
    private Label albumTitle;

    @FXML
    private Label albumArtistName;

    private Album album;
    private Image playIcon;
    private Image pauseIcon;

    private AudioPlayerManager audioPlayerManager = AudioPlayerManager.getInstance();

    public AlbumItemController() {
    }

    /**
     * Initializes the play and pause button for when you hover over the album art
     */
    public void initialize() {
        playIcon = IconCache.getIcon("play");
        pauseIcon = IconCache.getIcon("pause");
    }

    /**
     * Shows the album art, artist, and title of the given album
     *
     * @param album
     *                The album object to be shown
     * @pre album!=null;
     */
    public void setAlbumData(Album album) {
        assert album != null;
        this.album = album;

        if (!album.getAlbumTitle().equals("")){
            albumTitle.setText(album.getAlbumTitle());
        }
        if (!album.getAlbumArtistName().equals("")){
            albumArtistName.setText(album.getAlbumArtistName());
        }
        
        albumCover.setImage(album.getCoverArt());

        playPauseButton.selectedProperty().bindBidirectional(album.getIsPlaying());
        playPauseImage.imageProperty().bind(Bindings.when(playPauseButton.selectedProperty()).then(pauseIcon).otherwise(playIcon));
    }

    
    @FXML
    private void showPlayButton() {
        overlay.setVisible(true);
        playPauseButton.setVisible(true);
    }

    @FXML
    private void hidePlayButton() {
        overlay.setVisible(false);
        playPauseButton.setVisible(false);
    }

    @FXML
    private void goToAlbum() {
        SelectedAlbum.getInstance().setAlbumData(album);
        ViewModel.getInstance().setSelectedView("AlbumPage");
    }

    /**
     * Play or pause the album, checks if the currently loaded album is the same as the one you want to load'
     * If it is, then play and pause naturally, if it's not, load the new album
     */
    @FXML
    private void playOrPauseAlbum() {
        ObservableList<Song> loadedSongList = audioPlayerManager.getSongList();
        ObservableList<Song> albumSongList = album.getSongList();
        if (!loadedSongList.equals(albumSongList)) {
            audioPlayerManager.loadAlbum(album);
            audioPlayerManager.play();
        } else if (!album.getIsPlaying().get()) {
            audioPlayerManager.pause();
        } else {
            audioPlayerManager.play();
        }
    }
}
