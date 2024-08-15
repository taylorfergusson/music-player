package com.musicplayer.controller;
import java.io.File;
import java.util.List;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Contoroller for the menu bar at the top of the program
 */
public class MenuBarController {
    private ViewModel model;
    private Library myLibrary = Library.getLibrary();

    public MenuBarController(){
    }

    public void initialize() {
    }

    /**
     * Creates a new playlist with a predetermined numerical name and opens it
     */
    @FXML
    private void newPlaylist() {
        String playlistTitle = "Playlist #" + (myLibrary.getPlaylists().size()+1);
        Playlist playlist = new Playlist(playlistTitle);
        SelectedPlaylist.getInstance().setPlaylistData(playlist);
        model.setSelectedView("Playlist");
    }

    /**
     * Lets the user import individual files, filtered by only the comptaible file types: wav, aiff, aif, mp3
     * Adds all these files to the library when confirmed
     */
    @FXML
    private void importFiles() {
        // Create a DirectoryChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select at least one file to import");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.aiff", "*.aif", "*.mp3"));

        // Show the file selection dialog
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        if (selectedFiles != null) {
            myLibrary.addAudioFiles(selectedFiles);
        }
    }

    /**
     * Lets the user import an entire folder and its subfolders
     */
    @FXML
    private void importFolder() {
        // Create a DirectoryChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder to import");

        // Show the folder selection dialog
        var selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            myLibrary.addFolderAudio(selectedDirectory.getAbsolutePath());
        }
    }
}