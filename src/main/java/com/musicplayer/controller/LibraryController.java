package com.musicplayer.controller;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Controller for the library sidebar
 * Includes the library info like artists, albums, and songs, as well as the list of playlists
 */
public class LibraryController implements ViewChangeListener {

    @FXML
    private TextField searchBox;

    @FXML
    private ListView<String> libraryListView;

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    private ImageView artworkImageView;

    private ViewModel model;
    private FilteredSongs filteredSongs;

    public LibraryController(){
        setModel();
    }

    public void initialize() {
        libraryListView.getItems().addAll("Artists", "Albums", "Songs");
        artworkImageView.imageProperty().bind(AudioPlayerManager.getInstance().getCoverArt());
        libraryListView.getSelectionModel().select(2);
        filteredSongs = FilteredSongs.getInstance();
        setPlaylistList();
    }

    /**
     * Sets up the list of playlists, showing the title of each one
     * Will say "No playlists" if the library does not have any playlists
     */
    private void setPlaylistList() {
        Library myLibrary = Library.getLibrary();
        Label emptyLabel = new Label("No playlists");
        playlistListView.setPlaceholder(emptyLabel);
        playlistListView.setItems(myLibrary.getPlaylists());
        // Set the cell factory to display the title of each playlist
        playlistListView.setCellFactory(new Callback<ListView<Playlist>, ListCell<Playlist>>() {
            @Override
            public ListCell<Playlist> call(ListView<Playlist> listView) {
                return new ListCell<Playlist>() {
                    @Override
                    protected void updateItem(Playlist playlist, boolean empty) {
                        super.updateItem(playlist, empty);
                        if (empty || playlist == null) {
                            setText(null);
                        } else {
                            setText(playlist.getTitle().get()); // Set the text of the cell to the playlist title
                        }
                    }
                };
            }
        });
    }

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    /**
     * Changes to search view if search box is clicked
     */
    @FXML
    private void searchBoxClicked() {
        model.setSelectedView("Search");
    }

    /**
     * Changes the list of songs to show those that fit with the search query, updates on every change
     */
    @FXML
    private void searchTextChanged() {
        if (filteredSongs != null) {
            filteredSongs.filterSongs(searchBox.getText());
        }
    }

    /**
     * Changes the selected view when a library item is clicked
     */
    @FXML
    private void libraryItemClicked() {
        String selectedLibraryItem = libraryListView.getSelectionModel().getSelectedItem();
        model.setSelectedView(selectedLibraryItem);
    }

    /**
     * Changes the selected view when a playlist item is clicked
     */
    @FXML
    private void playlistItemClicked() {
        Playlist selectedPlaylistItem = playlistListView.getSelectionModel().getSelectedItem();
        SelectedPlaylist.getInstance().setPlaylistData(selectedPlaylistItem);
        model.setSelectedView("Playlist");
    }

    /**
     * Selects the appropriate library or playlist item when it is being shown
     * Needed for consistency between only being able to select a playlist OR library item
     */
    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Search")) {
            playlistListView.getSelectionModel().clearSelection();
            libraryListView.getSelectionModel().clearSelection();
        } else if (selectedView.equals("Artists")) {
            playlistListView.getSelectionModel().clearSelection();
            libraryListView.getSelectionModel().select(0);
        } else if (selectedView.equals("Albums") || selectedView.equals("AlbumsPage")) {
            playlistListView.getSelectionModel().clearSelection();
            libraryListView.getSelectionModel().select(1);
        } else if (selectedView.equals("Songs")) {
            playlistListView.getSelectionModel().clearSelection();
            libraryListView.getSelectionModel().select(2);
        } else {
            libraryListView.getSelectionModel().clearSelection(); // would have been playlist FIX LATE
        }
    }
}