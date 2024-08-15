package com.musicplayer.model;

import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a single Song object with song data
 */
public class Song implements Serializable {

    private String title;
    private Artist artist;
    private Album album;
    private String genre;
    private final int time;
    private String year;
    private final String dateAdded = LocalDate.now().toString();;
    private transient IntegerProperty plays = new SimpleIntegerProperty(0);
    private int savedPlays = 0;
    private int trackNo;
    private byte[] coverArtData;
    private String filePath;
    private transient BooleanProperty isPlaying = new SimpleBooleanProperty(false);


    private static final Comparator<Song> COMPARATOR = Comparator.comparing(Song::getTrackNo).thenComparing(Song::getTitle);

    /**
     * Creates a new Song object
     *
     * @param title
     *                The title of the song
     * @param artist
     *                The song's artist
     * @param album
     *                The album the song is from
     * @param genre
     *                The genre of the song
     * @param time
     *                The song length in seconds
     * @param year
     *                The release date year of the song
     * @param trackNo
     *                The song's track number within the album
     * @param coverArtData
     *                The byte information of the song's cover art, allowed to be null
     * @param filePath
     *                The filepath for the song's location on the computer's hard drive
     * 
     * @pre title != null && artist != null && album != null && genre != null && time > 0 &&
     *      year != null && trackNo != null && coverArtData != null && filePath != null
     */
    public Song(String title, Artist artist, Album album, String genre, int time, String year, int trackNo,
            byte[] coverArtData, String filePath) {

        if (title == null || artist == null || album == null || genre == null || time <= 0 || year == null || trackNo <= 0 || coverArtData == null || filePath == null) {
            throw new IllegalArgumentException("Invalid argument for the Song object constructor");
        }
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.time = time;
        this.year = year;
        this.trackNo = trackNo;
        this.coverArtData = coverArtData;
        this.filePath = filePath;
    }

    /**
     * Getter for the songTitleProperty of the song object's title
     *
     * @return SimpleStringProperty object copy of the song's title
     */
    public StringProperty songTitleProperty() {
        return new SimpleStringProperty(title);
    }

    /**
     * Getter for the songArtistProperty of the song object's artist
     *
     * @return SimpleStringProperty object copy of the song's artist
     */
    public StringProperty songArtistProperty() {
        return new SimpleStringProperty(artist.getArtistName());
    }

    public String getTitle() {
        return title;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getArtistName() {
        return artist.getArtistName();
    }

    public Album getAlbum() {
        return album;
    }

    public int getTime() {
        return time;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public IntegerProperty getPlays() {
        return plays;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getAlbumTitle() {
        return album.getAlbumTitle();
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public int getTrackNo() {
        return trackNo;
    }
    
    /**
     * Getter for the song's cover art
     *
     * @return Image of the art contained in the song's coverArtData
     *         Default art if no cover art is available
     */
    public Image getCoverArt() {
        if (coverArtData.length > 0) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(coverArtData);
            return new Image(inputStream);
        }
        return IconCache.getIcon("default-art");
    }

    // public void play() {
    //     System.out.println("Now playing " + title);
    // }

    /**
     * Sets the isPlaying instance attribute based on a given boolean
     *
     * @param isPlaying
     *                Boolean representing whether the song is playing or not
     * 
     * @pre isPlaying != null
     */
    public void setIsPlaying(Boolean isPlaying) {
        assert isPlaying != null;
        this.isPlaying.set(isPlaying);
    }

    public BooleanProperty getIsPlaying() {
        return isPlaying;
    }

    /**
     * Adds 1 play to the song's 'plays' instance attribute
     */
    public void addPlay() {
        plays.set(plays.get() + 1);
    }

    
    /**
     * Serilaizes the song class to be put in a file
     * Necessary for saving the number of plays from a SimpleIntegerProperty object
     *
     * @param oos
     *                An object output stream for file
     * @pre oos != null;
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        assert oos != null;
        savedPlays = plays.get();
        oos.defaultWriteObject(); // Serialize non-transient fields
    }

    /**
     * Reads object from file and deserializes it to make album objects
     * Necessary for loading the title as a stringproperty object from a string
     *
     * @param ois
     *                An object input stream from file
     * @pre ois != null;
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        assert ois != null;
        ois.defaultReadObject(); // Deserialize non-transient fields
        plays = new SimpleIntegerProperty(0); // Initialize the plays property
        plays.set(savedPlays);
        isPlaying = new SimpleBooleanProperty(false);
    }

    /**
     * Checks if two songs are equal based on song title and artist
     *
     * @param o
     *          The object to compare a song to
     * @return This method returns true if the song is the same as the obj argument;
     *         false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Song song = (Song) o;
        return this.title.equalsIgnoreCase(song.title) && this.artist.getArtistName().equalsIgnoreCase(song.artist.getArtistName());
    }

    /**
     * Equal songs have the same hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }

    @Override
    public Song clone() {
        return new Song(title, artist, album, genre, time, year, trackNo, coverArtData, filePath);
    }

    public int compareTo(Song otherSong) {
        return COMPARATOR.compare(this, otherSong);
    }
}