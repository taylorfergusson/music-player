package com.musicplayer.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Represents an audio playlist, with a name, description, and list of songs
 */
public class Playlist implements Serializable {
    private transient StringProperty title = new SimpleStringProperty("");
    private String savedTitle = "";
    private String description = "Add description";
    private ArrayList<Song> songList = new ArrayList<>();

    public Playlist(String playlistTitle) {
        this.title.set(playlistTitle);
        Library.getLibrary().addPlaylist(this);
    }


    /**
     * Getter for playlist title
     * 
     * @return The title of the playlist
     */
    public StringProperty getTitle() {
        return title;
    }

    /**
     * Sets the title instance attribute to the value inside the title
     *
     * @param title
     *                A string representing the desired playlist title
     * @pre title != null;
     */
    public void setTitle(String title) {
        assert title != null;
        this.title.set(title);
    }

    /**
     * Getter for playlist description
     * 
     * @return The description of the playlist
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description instance attribute to the value inside the description
     *
     * @param description
     *                A string representing the desired description
     * @pre description != null;
     */
    public void setDescription(String description) {
        assert description != null;
        this.description = description;
    }

    /**
     * Getter for the artwork gridpane
     * Generates a gridpane of art based on the songs inside the playlist
     * 
     * @return A gridpane made from of the first 4 songs' art,
     *         or just the first song's art if there are between 1 and 3 songs
     *         or the default cover art if there are no songs in the playlist
     */
    public GridPane getArtworkGridPane() {
        GridPane gridPane = new GridPane();
        ImageView artwork;
        if (songList.size() >= 4) {
            for (int i=0; i < 4; i++) {
                artwork = new ImageView(songList.get(i).getCoverArt());
                artwork.setFitWidth(125);
                artwork.setFitHeight(125);

                gridPane.add(artwork, i%2, i/2);
            }
        } else {
            if (1 <= songList.size() && songList.size() <= 3) {
                artwork = new ImageView(songList.get(0).getCoverArt());
            } else {
                artwork = new ImageView(IconCache.getIcon("default-art"));
            }
            artwork.setFitWidth(250);
            artwork.setFitHeight(250);
            gridPane.add(artwork, 0, 0);
        }
        return gridPane;
    }

    /**
     * Adds a song into the playlist's list of songs
     * 
     * @param song
     *                A song object to be added to the playlist
     * @pre song != null;
     */
    public void addSong(Song song) {
        assert song != null;
        songList.add(song);
    }

    /**
     * Getter for the playlist's list of songs
     * 
     * @return The song list
     */
    public ObservableList<Song> getSongList() {
        return FXCollections.observableArrayList(songList);
    }

    /**
     * Getter for the number of songs in the playlist
     * 
     * @return A string representing the number of songs in the playlist
     */
    public String getSongNumber() {
        int songNo = songList.size();
        if (songNo == 1) {
            return "1 song";
        } else {
            return String.valueOf(songList.size()) + " songs";
        }
    }

    /**
     * Getter for playlist length in hours, minutes, seconds format
     * 
     * @return A string representing the length of the playlist
     */
    public String getLength() {
        int length = 0;
        for (Song song : songList) {
            length += song.getTime();
        }
        int hours = length / 3600;
        int minutes = length % 3600 / 60;
        int seconds = length % 3600 % 60;
        if (length > 3600) {
            return hours + " hr " + minutes + " min " + seconds + " sec";
        } else {
            return minutes + " min " + seconds + " sec";
        }
    }

    /**
     * Serilaizes playlist class to be put in a file
     * Necessary for saving the SimpleStringProperty title object as a string
     *
     * @param oos
     *                An object output stream for file
     * @pre oos != null;
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        assert oos != null;
        savedTitle = title.get();
        oos.defaultWriteObject(); // Serialize non-transient fields
    }

    /**
     * Reads object from file and deserializes it to make playlist objects
     * Necessary for loading the title as a SimpleStringProperty object from a string
     *
     * @param ois
     *                An object input stream from file
     * @pre ois != null;
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        assert ois != null;
        ois.defaultReadObject(); // Deserialize non-transient fields
        title = new SimpleStringProperty(savedTitle);
    }
}