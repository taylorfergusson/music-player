package com.musicplayer.controller;

import com.musicplayer.model.*;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * Controller for the tableview that shows all the songs in a particular list and in a specified format
 */
public class SongTableViewController {

    @FXML
    private TableView<Song> songTableView;

    @FXML
    private TableColumn<Song, ImageView> coverArtColumn;

    @FXML
    private TableColumn<Song, String> trackNoColumn;

    @FXML
    private TableColumn<Song, String> titleColumn;

    @FXML
    private TableColumn<Song, String> artistColumn;

    @FXML
    private TableColumn<Song, String> albumColumn;

    @FXML
    private TableColumn<Song, String> genreColumn;

    @FXML
    private TableColumn<Song, String> timeColumn;

    @FXML
    private TableColumn<Song, String> yearColumn;

    @FXML
    private TableColumn<Song, String> dateColumn;

    @FXML
    private TableColumn<Song, Integer> playsColumn;

    @FXML
    private ContextMenu songContextMenu;

    @FXML
    private Menu playlistMenu;

    private Library myLibrary = Library.getLibrary();
    private AudioPlayerManager audioPlayerManager = AudioPlayerManager.getInstance();
    private ObservableList<Song> songList;
    private Song selectedSong;

    public void initialize() {
        initializeSongTableView();
        initializeSongSelector();
        initializeSongMouseClicker();
        initializePlaylistMenu();
        initializeHoverSettings();
    }

    /**
     * Initializes every column that can be shown in the tableview
     * Cover art, track number, title, artist, album, genre, plays, time, year, date added
     */
    private void initializeSongTableView() {
        coverArtColumn.setCellValueFactory(cellData -> {
            Image coverArt = cellData.getValue().getCoverArt(); // Assuming getCoverArt() returns an Image
            ImageView imageView = new ImageView(coverArt);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            return new SimpleObjectProperty<>(imageView);
        });

        trackNoColumn.setCellValueFactory(cellData -> {
            int trackNo = cellData.getValue().getTrackNo();
            if (trackNo == Integer.MAX_VALUE) {
                // Return an empty string for Integer.MAX_VALUE track numbers
                return new SimpleStringProperty("");
            } else {
                // Otherwise, return the regular track number
                return new SimpleStringProperty(Integer.toString(trackNo));
            }
        });

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("albumTitle"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        playsColumn.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Song, Integer> param) {
                return param.getValue().getPlays().asObject();
            }
        });
        timeColumn.setCellValueFactory(cellData -> {
            double time = cellData.getValue().getTime();
            return new SimpleStringProperty(secondsToClockFormat(time));
        });
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
    }

    /**
     * Initializes the song selector that keeps track of what song is selected
     */
    private void initializeSongSelector() {
        songTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSong = newValue;
            }
        });
    }

    /**
     * Initializes the clicker for when a songtableview item is clicked
     */
    private void initializeSongMouseClicker() {
        songTableView.setOnMouseClicked(event -> {
            songMouseClicked(event);
        });
    }

    /**
     * When the mouse is clicked on a song, checks to see if the primary button was clicked twice and plays the song if so
     * If the secondary button is clicked, will show the context menu for the song
     */
    private void songMouseClicked(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            int songIndex = songTableView.getSelectionModel().getSelectedIndex();
            if (songIndex >= 0) {
                audioPlayerManager.setUpSongList(songList);
                audioPlayerManager.loadSongByIndex(songIndex);
                audioPlayerManager.play();
            }
        }

        if (event.getButton().equals(MouseButton.SECONDARY)) {
            songContextMenu.show(songTableView, event.getScreenX(), event.getScreenY());
        }
    }

    /**
     * Initializes the playlist menu within the context menu
     * Listens to see if the playlist list has changed and updates accordingly
     */
    private void initializePlaylistMenu() {
        Library myLibrary = Library.getLibrary();

        myLibrary.getPlaylists().addListener((ListChangeListener.Change<? extends Playlist> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(playlist -> {
                        setUpAddToPlaylist(playlist);
                    });
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(playlist -> {
                        playlistMenu.getItems().removeIf(item -> item.textProperty().getValue().equals(playlist.getTitle()));
                    });
                }
            }
        });

        // Initial population of playlistMenu
        for (Playlist playlist : myLibrary.getPlaylists()) {
            setUpAddToPlaylist(playlist);
        }
    }

    /**
     * Sets up the "Add to playlist" section of the context menu for a given playlist
     *
     * @param playlist
     *                The playlist to be added to the "Add to Playlist" section
     * @pre playlist != null;
     */
    private void setUpAddToPlaylist(Playlist playlist) {
        assert playlist != null;
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(playlist.getTitle());

        menuItem.setOnAction(event -> {
            playlist.addSong(selectedSong);
        });

        // Add action event handler for the menu item if needed
        playlistMenu.getItems().add(menuItem);
    }

    /**
     * Initializes the hover settings for each row in the song tableview, only showing hover styles over nonempty items
     */
    private void initializeHoverSettings() {
        songTableView.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (!row.isEmpty() && row.getItem() != null) {
                    // Apply hover settings for non-null items
                    // For example, change background color
                    if (newValue) {
                        row.setStyle("-fx-background-color: rgba(0,0,0,0.05);");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }

    /**
     * Adds the song next in the queue
     */
    @FXML
    private void addToPlayNext() {
        audioPlayerManager.addToPlayNext(selectedSong);
    }

    /**
     * Adds the song to the end of the queue
     */
    @FXML
    private void addToPlayLast() {
        audioPlayerManager.addToPlayLast(selectedSong);
    }

    /**
     * Removes the song from the entire library
     */
    @FXML
    private void deleteSongFromLibrary() {
        myLibrary.removeSong(selectedSong);
    }

    /**
     * Returns the tableview object
     */
    public TableView<Song> getSongTableView() {
        return songTableView;
    }

    public void setSongList(ObservableList<Song> songList) {
        assert songList != null && songList.size() > 0;
        this.songList = songList;
        songTableView.setItems(songList);
    }

    /**
     * Sets the tableview to all songs format, where only the track number is hidden
     * If there are no songs in library, will say this
     */
    public void setAllSongsFormat() {
        Label emptyLabel = new Label("No songs in library");
        songTableView.setPlaceholder(emptyLabel);
        trackNoColumn.setVisible(false);
    }

    /**
     * Sets the tableview to search format, which only shows song art, title, album, artist, genre, and year
     * Will say "Searching your library" until characters are typed in the search box
     */
    public void setSearchFormat() {
        Label emptyLabel = new Label("Searching your library");
        songTableView.setPlaceholder(emptyLabel);
        trackNoColumn.setVisible(false);
        timeColumn.setVisible(false);
        dateColumn.setVisible(false);
        playsColumn.setVisible(false);
    }

    /**
     * Sets the tableview to album format, which only shows song track number, title, artist, time, and plays
     */
    public void setAlbumFormat() {
        coverArtColumn.setVisible(false);
        albumColumn.setVisible(false);
        genreColumn.setVisible(false);
        yearColumn.setVisible(false);
        dateColumn.setVisible(false);
    }

    /**
     * Sets the tableview to playlist format, which only shows song art, title, artist, album, time, and plays
     */
    public void setPlaylistFormat() {
        trackNoColumn.setVisible(false);
        genreColumn.setVisible(false);
        yearColumn.setVisible(false);
        dateColumn.setVisible(false);
    }

    /**
     * Sets the tableview to queue format, which only shows song art, title, artist, and time
     */
    public void setQueueFormat() {
        Label emptyLabel = new Label("No songs in queue");
        songTableView.setPlaceholder(emptyLabel);
        // loveColumn.setVisible(false);
        trackNoColumn.setVisible(false);
        albumColumn.setVisible(false);
        genreColumn.setVisible(false);
        playsColumn.setVisible(false);
        yearColumn.setVisible(false);
        dateColumn.setVisible(false);
    }

    // should probabky just have one of these VVVVVV

    /**
     * Converts a double object representing time in seconds into clock format as a string object
     *
     * @param time
     *                The time in double format representing seconds
     * @pre time >= 0;
     */
    private String secondsToClockFormat(double time) {
        long hours = (long) time / 3600;
        long minutes = (long) time % 3600 / 60;
        long seconds = (long) time % 3600 % 60;
        if (time > 3600) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
