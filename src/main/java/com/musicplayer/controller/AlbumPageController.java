package com.musicplayer.controller;

import java.io.IOException;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the page of a specific album, with more specific album data and the tracklist
 */
public class AlbumPageController implements ViewChangeListener {

    @FXML
    private ScrollPane albumPage;

    @FXML
    private VBox albumPageVBox;

    @FXML
    private ImageView albumArtworkImageView;

    @FXML
    private ImageView backImageView;

    @FXML
    private ImageView playAlbumImageView;

    @FXML
    private Label albumTitleLabel;

    @FXML
    private Label albumArtistNameLabel;

    @FXML
    private Label albumGenreLabel;

    @FXML
    private Label albumYearLabel;

    @FXML
    private Label albumSongNumberLabel;

    @FXML
    private Label albumLengthLabel;

    private ViewModel model;
    private SelectedAlbum selectedAlbum;

    public AlbumPageController(){
    }

    public void initialize(){
        setModel();
        setAlbumData();
        setTableView();
        setImages();
    }

    /**
     * Adds this controller as a view change listener to be shown and hidden accordingly.
     */
    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    /**
     * Sets up the specific album text that is shown alongside the album title and artist
     */
    private void setAlbumData() {
        selectedAlbum = SelectedAlbum.getInstance();
        albumArtworkImageView.imageProperty().bind(selectedAlbum.getAlbumArtwork());
        albumTitleLabel.textProperty().bind(selectedAlbum.getAlbumTitle());
        albumArtistNameLabel.textProperty().bind(selectedAlbum.getAlbumArtistName());
        albumGenreLabel.textProperty().bind(selectedAlbum.getAlbumGenre());
        albumYearLabel.textProperty().bind(selectedAlbum.getAlbumYear());
        albumSongNumberLabel.textProperty().bind(selectedAlbum.getAlbumSongNumber());
        albumLengthLabel.textProperty().bind(selectedAlbum.getAlbumLength());
    }

    /**
     * Sets up the song list tableview, with format specified from the FXML file.
     */
    private void setTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            SongTableViewController songTableViewController = loader.getController();
            songTableViewController.setSongList(selectedAlbum.getSongList());
            songTableViewController.setAlbumFormat();
            albumPageVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the icons for the back button and play album button
     */
    private void setImages() {
        backImageView.setImage(IconCache.getIcon("back"));
        playAlbumImageView.setImage(IconCache.getIcon("play"));
    }
    
    @FXML
    private void showAlbumScrollPane() {
        model.setSelectedView("Albums");
    }

    /**
     * Gets the album that is currently selected, loads it into the audio player manager, and plays it
     */
    @FXML
    private void playAlbum() {
        AudioPlayerManager.getInstance().loadAlbum(SelectedAlbum.getInstance().getAlbum());
        AudioPlayerManager.getInstance().play();
    }


    /**
     * Checks if album page is selected, becomes visible if yes, remains hidden otherwise
     */
    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("AlbumPage")) {
            albumPage.setVisible(true);
        } else {
            albumPage.setVisible(false);
        }
    }
}
