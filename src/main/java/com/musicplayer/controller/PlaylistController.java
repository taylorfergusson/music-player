package com.musicplayer.controller;

import java.io.IOException;

import com.musicplayer.model.*;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the page representing a playlist
 */
public class PlaylistController implements ViewChangeListener {

    @FXML
    private ScrollPane playlistScrollPane;

    @FXML
    private VBox playlistVBox;

    @FXML
    private GridPane artworkGridPane;

    @FXML
    private ImageView backImageView;

    @FXML
    private ImageView playPlaylistImageView;

    @FXML
    private Label playlistTitleLabel;

    @FXML
    private Label songNumberLabel;

    @FXML
    private Label lengthLabel;

    private ViewModel model;
    private AudioPlayerManager audioPlayerManager = AudioPlayerManager.getInstance();
    private SelectedPlaylist selectedPlaylist;

    public PlaylistController(){
    }

    public void initialize(){
        setModel();
        setPlaylistData();
        setTableView();
        setImages();
    }

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    /**
     * Sets up the playlist information to be shown depending on which playlist is selected
     */
    private void setPlaylistData() {
        selectedPlaylist = SelectedPlaylist.getInstance();
        Bindings.bindBidirectional(playlistTitleLabel.textProperty(), selectedPlaylist.getTitle());
        songNumberLabel.textProperty().bind(selectedPlaylist.getSongNumber());
        lengthLabel.textProperty().bind(selectedPlaylist.getLength());
    }

    /**
     * Sets up the tableview representing the songs in the selected playlist
     */
    private void setTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            SongTableViewController songTableViewController = loader.getController();
            songTableViewController.setSongList(selectedPlaylist.getSongList());
            songTableViewController.setPlaylistFormat();
            playlistVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the play playlist image
     */
    private void setImages() {
        playPlaylistImageView.setImage(IconCache.getIcon("play"));
    }

    /**
     * Activated when the play playlist button is clicked, will play the playlist from the first song
     */
    @FXML
    private void playPlaylist() {
        ObservableList<Song> songList = selectedPlaylist.getSongList();
        if (songList.size() > 0) {
            audioPlayerManager.setUpSongList(songList);
            audioPlayerManager.loadSongByIndex(0);
            audioPlayerManager.play();
        }
    }

    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Playlist")) {
            artworkGridPane.getChildren().clear();
            artworkGridPane.getChildren().addAll(selectedPlaylist.getPlaylist().getArtworkGridPane().getChildren());
            playlistScrollPane.setVisible(true);
        } else {
            playlistScrollPane.setVisible(false);
        }
    }
}