package com.musicplayer.controller;
import java.io.IOException;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the list of artists in the library, along with the list of their albums
 */
public class ArtistsController implements ViewChangeListener {

    @FXML
    private HBox artistsHBox;

    @FXML
    private ListView<Artist> artistsListView;

    @FXML
    private VBox artistAlbumsVBox;

    @FXML
    private Label artistNameLabel = new Label("Select an artist");

    private ViewModel model;

    public ArtistsController(){
    }

    /**
     * Initializes the list of artists
     * Makes them show up as "Unknown Artist" if the artist name is an empty string
     * If artist is selected, it loads the scrollpane of their albums
     */
    public void initialize(){
        Library myLibrary = Library.getLibrary();
        setModel();
        Label emptyLabel = new Label("No artists");
        artistsListView.setPlaceholder(emptyLabel);
        artistsListView.setItems(myLibrary.getArtists());

        artistsListView.setCellFactory(param -> new ListCell<Artist>() {
            @Override
            protected void updateItem(Artist artist, boolean empty) {
                super.updateItem(artist, empty);

                if (empty || artist == null) {
                    setText("");
                } else if (artist.getArtistName() == "") {
                    setText("Unknown Artist");
                } else {
                    setText(artist.getArtistName());
                }
            }
        });

        artistsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadArtistAlbumScrollPane(newValue);
            }
        });
    }
    

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Artists")) {
            artistsHBox.setVisible(true);
        } else {
            artistsHBox.setVisible(false);
        }
    }

    /**
     * Loads the scrollpane for every album of a given artist
     *
     * @param artist
     *                The title of the alert
     * @pre artist != null;
     */
    private void loadArtistAlbumScrollPane(Artist artist) {
        assert artist != null;
        artistAlbumsVBox.getChildren().clear();
        String artistName = artist.getArtistName();
        if (artistName.isEmpty()) {
            artistNameLabel.setText("Unknown Artist");
        } else {
            artistNameLabel.setText(artist.getArtistName());           
        }
        try {
            for (Album album : artist.getAlbums()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/ArtistAlbum.fxml"));
                VBox albumVBox = loader.load();
                ArtistAlbumController controller = loader.getController();
                controller.setAlbumData(album);
                artistAlbumsVBox.getChildren().add(albumVBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}