package com.musicplayer.controller;

import java.io.IOException;

import com.musicplayer.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

/**
 * Controller for the queue that shows the upcoming songs
 */
public class QueueController {

    @FXML
    private VBox queueVBox;

    @FXML
    private TableColumn<Song, String> titleColumn;

    @FXML
    private TableColumn<Song, String> artistNameColumn;

    @FXML
    private TableColumn<Song, String> timeColumn;

    private AudioPlayerManager audioPlayerManager;

    private QueueModel queueModel = QueueModel.getInstance();

    public QueueController () {
        this.audioPlayerManager = AudioPlayerManager.getInstance();
    }

    public void initialize() {
        queueModel.addQueueListener(this);
        setTableView();
    }

    /**
     * Sets up the tableview representing the list of songs that are coming up next
     */
    private void setTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/SongTableView.fxml"));
            Parent songTableViewRoot = loader.load();
            SongTableViewController songTableViewController = loader.getController();
            songTableViewController.setSongList(audioPlayerManager.getQueueList());
            songTableViewController.setQueueFormat();
            queueVBox.getChildren().add(songTableViewRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows or hides the queue depending on whether the queue button is selected
     */
    public void onQueueSelectedChange() {
        if (queueVBox.isVisible()) {
            queueVBox.setVisible(false);
        } else {
            queueVBox.setVisible(true);
        }
    }
}
