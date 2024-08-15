package com.musicplayer.model;

import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;

/**
 * Class for the filtered search result songs
 */
public class FilteredSongs {
    private static FilteredSongs instance;
    private FilteredList<Song> filteredSongs;
    private ObservableList<Song> observableFilteredSongs = FXCollections.observableArrayList();;
    private Label emptyLabel = new Label("Searching your library...");

    private FilteredSongs(){}

    /**
     * Resets the filteredSongs list and adds a listener to the predicate property of the FilteredList
     * Used for when new songs are added or pre-existing songs are deleted
     */
    public void resetFilteredSongs() {
        this.filteredSongs = new FilteredList<>(Library.getLibrary().getSongs());

        // Add a listener to the predicate property of the FilteredList
        filteredSongs.predicateProperty().addListener((obs, oldPredicate, newPredicate) -> {
            // Update the ObservableList whenever the predicate changes
            observableFilteredSongs.setAll(filteredSongs);
        });
    }

    // Static method to return the single instance of FilteredSongs
    public static synchronized FilteredSongs getInstance() {
        if (instance == null) {
            instance = new FilteredSongs();
        }
        return instance;
    }

    /**
     * Sets the repeat status of playback
     * Off: stops playing at the end of the song list
     * On: continues playing the song list after it reaches the end
     * One: repeats the same song continuously
     *
     * @param searchQuery
     *                String representing the 3 repeat options: off, on, or one
     *                
     * @pre status.equals("off") || status.equals("on") || status.equals("one");
     */
    public void filterSongs(String searchQuery) {
        assert searchQuery != null;
        if (searchQuery == null || searchQuery.isEmpty()) {
            this.emptyLabel.setText("Searching your library...");
        } else {
            this.emptyLabel.setText("No results found for '" + searchQuery + "'");
        }
        if (filteredSongs != null) {
            Predicate<Song> predicate = item -> {
                String lowerText = searchQuery.toLowerCase();
                if (searchQuery == null || searchQuery.isEmpty()) {
                    return false; // empty if none
                }
                return item.getTitle().toLowerCase().contains(lowerText) ||
                        item.getAlbumTitle().toLowerCase().contains(lowerText) ||
                        item.getArtistName().toLowerCase().contains(lowerText) ||
                        item.getGenre().toLowerCase().contains(lowerText) ||
                        item.getYear().toLowerCase().contains(lowerText);
            };
            filteredSongs.setPredicate(predicate);
        }
    }

    public ObservableList<Song> getObservableFilteredSongs(){
        return observableFilteredSongs;
    }
}