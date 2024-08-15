package com.musicplayer.model;

import java.io.Serializable;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a single Artist, with an artist name and a list of albums
 */
public class Artist implements Serializable {

    private String artistName;
    private ArrayList<Album> albums = new ArrayList<>();

    /**
     * Comparator for two Artist objects. Sorts by artist name in lowercase, with a special large value character representing empty artist names so they are last
     */
    private static final Comparator<Artist> COMPARATOR = Comparator.comparing((Artist artist) -> {
        String artistName = artist.getArtistName().toLowerCase();
        return artistName.isEmpty() ? "\uffff" : artistName; // Use a special character as the largest value
    });

    /**
     * Constructor for an artist, with an artist name
     *
     * @param artistName
     *                The name of the artist
     * @pre artistName != null
     */
    public Artist(String artistName) {
        if (artistName == null) {
            throw new IllegalArgumentException("Artist name must be present");
        }
        this.artistName = artistName;
    }

    /**
     * Adds album to the artist's list of albums it's not there already
     *
     * @param album
     *                An album object to be added to the albums list
     * @pre album != null
     */
    public void addAlbum(Album album) {
        assert album != null;
        boolean albumAdded = false;
        // if list is empty, will skip this and add by default
        for (int i=0; i < albums.size(); i++) {
            if (albums.get(i).compareTo(album) > 0) {
                albums.add(i, album);
                albumAdded = true;
                break;
            }
        }
        if (!albumAdded){
            albums.add(album); // add to end
        }
    }

    /**
     * Removes the album from the artist's list of albums
     *
     * @param album
     *                An album object to be removed from the artist
     * @pre album != null
     */
    public void removeAlbum(Album album) {
        assert album != null;
        albums.remove(album);
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ObservableList<Album> getAlbums() {
        return FXCollections.observableArrayList(albums);
    }

    /**
     * Checks if artists are equal based on artist name
     *
     * @param o
     *          The object to compare an artist to
     * @return This method returns true if the artist is the same as the obj argument;
     *         false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Artist artist = (Artist) o;
        return artistName.equalsIgnoreCase(artist.artistName);
    }

    public int compareTo(Artist otherArtist) {
        return COMPARATOR.compare(this, otherArtist);
    }
}