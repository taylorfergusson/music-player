package com.musicplayer.controller;


import java.io.IOException;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for each album in the list of albums that show for a specific artist in the artists view
 */
public class ArtistAlbumController extends VBox {

    @FXML
    private VBox artistAlbumVBox;

    @FXML
    private ImageView albumArtworkImageView;

    @FXML
    private Label albumTitleLabel;

    @FXML
    private Label albumGenreLabel;

    @FXML
    private Label albumYearLabel;

    @FXML
    private Label albumSongNumberLabel;

    @FXML
    private Label albumLengthLabel;

    //album tableview

    @FXML
    private TableView<Song> albumTableView;

    @FXML
    private TableColumn<Song, Integer> albumTrackNoColumn;

    @FXML
    private TableColumn<Song, String> albumSongTitleColumn;

    @FXML
    private TableColumn<Song, String> albumSongArtistNameColumn;

    @FXML
    private TableColumn<Song, Integer> albumSongPlaysColumn;

    @FXML
    private TableColumn<Song, String> albumSongTimeColumn;

    private SongTableViewController songTableViewController;

    public ArtistAlbumController() {
    }

    public void initialize() {
        setTableView();
    }

    /**
     * Sets the album data for a given album
     *
     * @param album
     *                The album to be displayed for a specific artist
     * @pre album != null;
     */
    public void setAlbumData(Album album) {
        assert album != null;

        albumArtworkImageView.setImage(album.getCoverArt());
        if (!album.getAlbumTitle().equals("")){
            albumTitleLabel.setText(album.getAlbumTitle());
        } else {
            albumTitleLabel.setText("Unknown Album");
        }
        if (!album.getGenre().equals("")){
            albumGenreLabel.setText(album.getGenre() + " · ");
        }
        if (!album.getYear().equals("")){
            albumYearLabel.setText(album.getYear() + " · ");
        }

        albumSongNumberLabel.setText(album.getNumberOfSongs() + " · ");
        albumLengthLabel.setText(album.getTotalLength());

        songTableViewController.setSongList(album.getSongList());
        songTableViewController.setAlbumFormat();
    }

    /**
     * Sets up the tableview for the album's list of songs and adds it into the artist album vbox
     */
    private void setTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            songTableViewController = loader.getController();
            artistAlbumVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
