package com.musicplayer.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Class that saves the library data into .ser files so previous library information can be loaded on startup
 * Saves data from songs, albums, artists, playlists, and the default song list to be loaded on startup
 */
public class LibrarySerializer {
    private final String SONG_DATA_PATH = "src/main/resources/savedata/song.ser";
    private final String ALBUM_DATA_PATH = "src/main/resources/savedata/album.ser";
    private final String ARTIST_DATA_PATH = "src/main/resources/savedata/artist.ser";
    private final String PLAYLIST_DATA_PATH = "src/main/resources/savedata/playlist.ser";
    private final String DEFAULT_SONG_DATA_PATH = "src/main/resources/savedata/defaultSong.ser";

    private final Library myLibrary = Library.getLibrary();
    private final AudioPlayerManager audioPlayerManager = AudioPlayerManager.getInstance();

    public LibrarySerializer() {
    }

    /**
     * Main function that saves each library list into a file
     */
    public void saveData() {
        saveListIntoFile(myLibrary.getSongs(), SONG_DATA_PATH);
        saveListIntoFile(myLibrary.getAlbums(), ALBUM_DATA_PATH);
        saveListIntoFile(myLibrary.getArtists(), ARTIST_DATA_PATH);
        saveListIntoFile(myLibrary.getPlaylists(), PLAYLIST_DATA_PATH);
        saveListIntoFile(audioPlayerManager.getSongList(), DEFAULT_SONG_DATA_PATH);
    }

    /**
     * Saves an ObservableList into a .ser file
     *
     * @param list
     *                  The list to be saved into the .ser file
     * @param filePath
     *                  The file path to the .ser destination file
     * 
     * @pre list != null && filePath != null;
     */
    public void saveListIntoFile(ObservableList<?> list, String filePath) {
        assert list != null && filePath != null;
        try {
            // Create necessary directories if they don't exist
            Files.createDirectories(Paths.get(filePath).getParent());

            // Create the file if it doesn't exist
            if (!Files.exists(Paths.get(filePath))) {
                Files.createFile(Paths.get(filePath));
            }

            // Write list to file
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new ArrayList<>(list));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an ObservableList of songs from a .ser file using the loadListFromFile function
     *
     * @return ObservableList of all the songs in the library
     */
    @SuppressWarnings("unchecked")
    public ObservableList<Song> loadSongs() {
        return (ObservableList<Song>) loadListFromFile(SONG_DATA_PATH);
    }

    /**
     * Loads an ObservableList of albums from a .ser file using the loadListFromFile function
     *
     * @return ObservableList of all the albums in the library
     */
    @SuppressWarnings("unchecked")
    public ObservableList<Album> loadAlbums() {
        return (ObservableList<Album>) loadListFromFile(ALBUM_DATA_PATH);
    }

    /**
     * Loads an ObservableList of artists from a .ser file using the loadListFromFile function
     *
     * @return ObservableList of all the artists in the library
     */
    @SuppressWarnings("unchecked")
    public ObservableList<Artist> loadArtists() {
        return (ObservableList<Artist>) loadListFromFile(ARTIST_DATA_PATH);
    }

    /**
     * Loads an ObservableList of playlists from a .ser file using the loadListFromFile function
     *
     * @return ObservableList of all the playlists in the library
     */
    @SuppressWarnings("unchecked")
    public ObservableList<Playlist> loadPlaylists() {
        return (ObservableList<Playlist>) loadListFromFile(PLAYLIST_DATA_PATH);
    }

    /**
     * Loads an ObservableList of songs from a .ser file using the loadListFromFile function
     * Makes this song list the default list to be played from when the program opens
     * Sets the first song as the default song
     */
    @SuppressWarnings("unchecked")
    public void loadDefaultSong() {
        ObservableList<Song> defaultSongList =  (ObservableList<Song>) loadListFromFile(DEFAULT_SONG_DATA_PATH);
        if (defaultSongList.size() > 0) {
            audioPlayerManager.setUpSongList(defaultSongList);
            audioPlayerManager.loadSongByIndex(0);
        }
    }

    /**
     * Loads an ObservableList from a .ser file
     *
     * @param filePath
     *                  The file path to the .ser file to be loaded
     * 
     * @pre filePath != null;
     */
    public ObservableList<?> loadListFromFile(String filePath) {
        assert filePath != null;
        try {
            // Load song list from file
            FileInputStream fis = new FileInputStream(filePath);
            @SuppressWarnings("resource")
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<?> tempList = (List<?>) ois.readObject();
            return FXCollections.observableList(tempList);
        } catch (IOException | ClassNotFoundException e) {
            return FXCollections.observableArrayList();
        }
    }
}
