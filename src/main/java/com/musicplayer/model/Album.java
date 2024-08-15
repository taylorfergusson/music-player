package com.musicplayer.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * Represents a single Album, with a title, artist, songlist, and a property isPlaying
 */
public class Album implements Serializable {

    private String albumTitle;
    private Artist albumArtist;
    private ArrayList<Song> songList = new ArrayList<>();
    private transient BooleanProperty isPlaying = new SimpleBooleanProperty(false);

    /**
     * Comparator for two albums, sorting alphabetically by artist name (no artist means its put at the end), and then chronoligically by year if the artist name is the same
     */
    private static final Comparator<Album> COMPARATOR = Comparator.comparing((Album album) -> {
        String artist = album.getAlbumArtistName().toLowerCase();
        return artist.isEmpty() ? "\uffff" : artist; // Use a special character as the largest value
    }).thenComparing((Album album) -> {
        String year = album.getYear();
        return year.isEmpty() ? "\uffff" : year; // Use a special character as the largest value
    });

    /**
     * Constructor for an Album with a title and album artist
     *
     * @param albumTitle
     *                The title of the album as a string
     * @param albumArtist
     *                The artist name as a string
     * @pre albumTitle != null && albumArtist != null;
     */
    public Album(String albumTitle, Artist albumArtist) {
        if (albumTitle == null || albumArtist == null) {
            throw new IllegalArgumentException("Album title and artist must be present");
        }
        this.albumTitle = albumTitle;
        this.albumArtist = albumArtist;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public Artist getAlbumArtist() {
        return albumArtist;
    }

    public String getAlbumArtistName() {
        return albumArtist.getArtistName();
    }

    public String getGenre() {
        return songList.isEmpty() ? "" : songList.get(0).getGenre();
    }

    public String getYear() {
        return songList.isEmpty() ? "\uffff" : songList.get(0).getYear();
    }

    /**
     * Getter for number of songs in album
     * 
     * @return Number of songs in the album as a string; "1 song" if 1 song, "_ songs" otherwise
     */
    public String getNumberOfSongs() {
        int songNo = songList.size();
        return songNo == 1 ? "1 song" : String.valueOf(songList.size()) + " songs";
    }

    /**
     * Getter for total length of album
     * 
     * @return Total length of the album as a string in  "# hr # min # sec" format
     */
    public String getTotalLength() {
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
     * Getter for album cover art
     * 
     * @return Default art if song list empty, otherwise cover art for the album, taken from the first song on the album
     */
    public Image getCoverArt() {
        return songList.isEmpty() ? IconCache.getIcon("default-art") : songList.get(0).getCoverArt();
    }

    /**
     * Getter for the album song list
     * 
     * @return An observableArrayList copy of the songList
     */
    public ObservableList<Song> getSongList() {
        return FXCollections.observableArrayList(songList);
    }

    /**
     * Adds a song to the album, placing it in the appropriate location according to track number
     *
     * @param song
     *                A single song object
     * @pre song != null;
     */
    public void addSong(Song song) {
        assert song != null;
        boolean songAdded = false;
        // if list is empty, will skip this and add by default
        for (int i=0; i < songList.size(); i++) {
            if (songList.get(i).compareTo(song) > 0) {
                songList.add(i, song);
                songAdded = true;
                break;
            }
        }
        if (!songAdded){
            songList.add(song);
        }
    }

    /**
     * Removes a song from the album
     *
     * @param song
     *                A single song object
     * @pre song != null;
     */
    public void removeSong(Song song) {
        assert song != null;
        songList.remove(song);
    }

    /**
     * Sets the isPlaying instance attribute to the value inside the isPlaying argument
     *
     * @param isPlaying
     *                A boolean object representing whether the album should be playing
     * @pre isPlaying != null;
     */
    public void setIsPlaying(Boolean isPlaying) {
        assert isPlaying != null;
        this.isPlaying.set(isPlaying);
    }

    /**
     * Getter for isPlaying to see if the album is playing
     * 
     * @return The isPlaying instance attribute
     */
    public BooleanProperty getIsPlaying() {
        return isPlaying;
    }

    /**
     * Reads object from file and deserializes it to make album objects
     * Necessary for loading saved library information while reopening program, specifically setting isPlaying to false
     *
     * @param ois
     *                An object input stream from file
     * @pre ois != null;
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        assert ois != null;
        ois.defaultReadObject(); // Deserialize non-transient fields
        isPlaying = new SimpleBooleanProperty(false);
    }

    /**
     * Checks if two songs are equal based on album title and album artist
     *
     * @param o
     *          The object to compare an album to
     * @return This method returns true if the album is the same as the obj argument;
     *         false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Album album = (Album) o;
        return albumTitle.equalsIgnoreCase(album.albumTitle) && albumArtist.getArtistName().equalsIgnoreCase(album.albumArtist.getArtistName());
    }

    /**
     * Equal songs have the same hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(albumTitle, albumArtist);
    }

    /**
     * Compares two albums
     */
    public int compareTo(Album otherAlbum) {
        return COMPARATOR.compare(this, otherAlbum);
    }
}