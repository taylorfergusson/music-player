package com.musicplayer.controller;

import java.io.IOException;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * Controller for the list of all songs in the library
 */
public class SongsController implements ViewChangeListener {

    @FXML
    private VBox allSongsVBox;

    private Library myLibrary = Library.getLibrary();

    private ViewModel model;
    

    public SongsController() {
        setModel();
    }

    public void initialize() {
        setUpTableView();
    }

    /**
     * Sets up the tableview to show every song in the library in all songs format
     */
    private void setUpTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            SongTableViewController songTableViewController = loader.getController();
            songTableViewController.setSongList(myLibrary.getSongs());
            songTableViewController.setAllSongsFormat();
            allSongsVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Songs")) {
            allSongsVBox.setVisible(true);
        } else {
            allSongsVBox.setVisible(false);
        }
    }
}
