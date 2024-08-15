package com.musicplayer.controller;

import java.io.IOException;

import com.musicplayer.model.*;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;


/**
 * Controller for the search view for songs in the library
 */
public class SearchController implements ViewChangeListener {

    @FXML
    private VBox searchSongsVBox;

    private ViewModel model;

    private FilteredSongs filteredSongs;
    private ObservableList<Song> searchQuerySongs;
    

    public SearchController() {
        this.filteredSongs = FilteredSongs.getInstance();
        this.searchQuerySongs = filteredSongs.getObservableFilteredSongs();
        setModel();
    }

    public void initialize() {
        setTableView();
    }

    /**
     * Sets up the tableview for the list of songs in search format
     */
    private void setTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            SongTableViewController songTableViewController = loader.getController();
            songTableViewController.setSongList(searchQuerySongs);
            songTableViewController.setSearchFormat();
            searchSongsVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When called, the tableview of filtered songs will be updated to match the search query
     */
    private void updateSearchQuerySongs() {
        filteredSongs.resetFilteredSongs();
    }

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Search")) {
            updateSearchQuerySongs();
            searchSongsVBox.setVisible(true);
        } else {
            searchSongsVBox.setVisible(false);
        }
    }
}
