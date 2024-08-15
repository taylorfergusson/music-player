package com.musicplayer.model;

import java.io.File;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

/**
 * Represents an Audio library, with playlists, artists, albums, and songs
 * Controls the importing and organization of new songs
 */
public class Library {

    private final ObservableList<Playlist> allPlaylists = FXCollections.observableArrayList();
    private final ObservableList<Artist> allArtists = FXCollections.observableArrayList();
    private final ObservableList<Album> allAlbums = FXCollections.observableArrayList();
    private final ObservableList<Song> allSongs = FXCollections.observableArrayList();

    private final String libraryName;

    private static Library INSTANCE = null;
    private static LibrarySerializer librarySerializer;

    /**z
     * Creates a library
     *
     * @param libraryName
     *                     Name of the library.
     * @pre pLibraryName!=null;
     */
    private Library(String libraryName) {
        assert libraryName != null;
        this.libraryName = libraryName;
        INSTANCE = this;
        librarySerializer = new LibrarySerializer();
        allSongs.setAll(librarySerializer.loadSongs());
        allAlbums.setAll(librarySerializer.loadAlbums());
        allArtists.setAll(librarySerializer.loadArtists());
        allPlaylists.setAll(librarySerializer.loadPlaylists());
        librarySerializer.loadDefaultSong();
    }

    /**
     * Calls the constructor to create a library if the library has not been
     * initialized.
     *
     * @param libraryName
     *                     Podcast name of the library.
     * @pre pLibraryName != null;
     */
    public static void createLibrary(String libraryName) {
        assert libraryName != null;
        if (INSTANCE == null) {
            INSTANCE = new Library(libraryName);
        }
    }

    /**
     * @return The single Library instance.
     */
    public static Library getLibrary() {
        if (INSTANCE == null) {
            INSTANCE = new Library("My Library");
        }
        return INSTANCE;
    }

    public String getLibraryName() {
        return this.libraryName;
    }

    public ObservableList<Playlist> getPlaylists() {
        return allPlaylists;
    }

    public ObservableList<Artist> getArtists() {
        return allArtists;
    }

    public ObservableList<Album> getAlbums() {
        return allAlbums;
    }

    public ObservableList<Song> getSongs() {
        return allSongs;
    }

    /**
     * Adds a Song to the library. Duplicate Songs aren't added twice.
     *
     * @param songFile
     *              The Song to add and extract metadata info from
     * @pre songFile!=null;
     */
    public void addSong(File songFile) {
        assert songFile != null;
        // Read the audio file and its metadata
        AudioFile audioFileObj;
        try {
            audioFileObj = AudioFileIO.read(songFile);
            audioFileObj.getAudioHeader();
            Tag tag = audioFileObj.getTag();
            // Print metadata information
            String title = getOrDefault(tag, FieldKey.TITLE, songFile.getName().replaceAll("\\.\\w+$", ""));
            String artistName = getOrDefault(tag, FieldKey.ARTIST, "");
            String albumArtistName = getOrDefault(tag, FieldKey.ALBUM_ARTIST, artistName);
            String albumTitle = getOrDefault(tag, FieldKey.ALBUM, "");
            String genre = getOrDefault(tag, FieldKey.GENRE, "");
            int time = audioFileObj.getAudioHeader().getTrackLength();
            String year = getOrDefault(tag, FieldKey.YEAR, "");
            if (year.length() > 4) {
                year = year.substring(0, 4);
            }
            int trackNo = parseTrackNo(tag);
            byte[] coverArtData = getCoverArtData(tag);
            Artist songArtist = getOrMakeArtist(artistName);
            Artist albumArtist = getOrMakeArtist(albumArtistName);
            Album songAlbum = getOrMakeAlbum(albumTitle, albumArtist);

            // If album artist is different from song artist, still put it under the song artist's name too if it's not there already
            if (!songArtist.equals(albumArtist) && !songArtist.getAlbums().contains(songAlbum)) {
                songArtist.addAlbum(songAlbum);
            }
            Song newSong = new Song(title, songArtist, songAlbum, genre, time, year, trackNo, coverArtData,
                    songFile.getAbsolutePath());
            if (allSongs.contains(newSong)) {
                return;
            }
            songAlbum.addSong(newSong);
            allSongs.add(newSong);

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    /**
     * Helper for addSong
     * Used to fill out the data to be displayed on the program
     * Will get the value inside the key if it's present, or a provided default value otherwise
     *
     * @param tag
     *                AudioFile object tag containing song metadata
     * @param fieldKey
     *                The key for the specific info field, like artist, title, genre
     * @param defaultValue
     *                Default string value to use in case of the field for the key being empty
     *                
     * @pre tag != null && fieldKey != null && defaultValue != null;
     * 
     * @return A string value of the value inside the tag for the specific key, or the default value provided
     */
    private String getOrDefault(Tag tag, FieldKey fieldKey, String defaultValue) {
        assert fieldKey != null && defaultValue != null;
        if (tag == null) {
            return defaultValue;
        }
        String value = tag.getFirst(fieldKey);
        return value.isEmpty() ? defaultValue : value;
    }

    /**
     * Helper for addSong
     * Gets the cover art data from a particular song using the tag
     * 
     * @param tag
     *                AudioFile object tag containing song metadata
     * 
     * @pre tag != null
     * 
     * @return An empty byte array if no image available, or a byte array containing image data
     */
    private byte[] getCoverArtData(Tag tag) {
        if (tag == null) {
            return new byte[0];
        }
        Artwork artwork = tag.getFirstArtwork();
        if (artwork != null) {
            return artwork.getBinaryData();
        } else {
            return new byte[0];
        }
    }

    /**
     * Creates or accesses an Artist object found using the given artistName
     * Will only create a new Artist if it's nonexistent
     *
     * @param artistName
     *                Name of the artist to be looked up
     *                
     * @pre artistName != null;
     * 
     * @return New Artist object or the preexisting Artist object with the same name
     */
    private Artist getOrMakeArtist(String artistName) {
        assert artistName != null;
        Artist newArtist = new Artist(artistName);
        for (Artist oldArtist : allArtists) {
            if (newArtist.equals(oldArtist)) { // equal
                return oldArtist;
            }
        }
        // add album to albumslist in order
        addArtist(newArtist);
        return newArtist;
    }

    /**
     * Creates or accesses an Album object found using the given album title and album artist
     * Will only create a new Album if it's nonexistent
     *
     * @param albumTitle
     *                Name of the album title to be looked up
     * @param albumArtist
     *                Name of the album artist to be looked up
     *                
     * @pre albumTitle != null && albumArtist != null;
     * 
     * @return New Album object or preexisting one with the same title and album artist
     */
    private Album getOrMakeAlbum(String albumTitle, Artist albumArtist) {
        assert albumTitle != null && albumArtist != null;
        Album newAlbum = new Album(albumTitle, albumArtist);
        for (Album oldAlbum : allAlbums) {
            if (newAlbum.equals(oldAlbum)) { // equal
                return oldAlbum;
            }
        }
        // add album to albumslist in order
        addAlbum(newAlbum);
        return newAlbum;
    }

    /**
     * Parser for the song's trackNo string
     *
     * @param tag
     *                AudioFile object tag containing song metadata of track number
     * 
     * @pre trackNo != null;
     * @return int of the song's track number if available
     *          Very large number if no track number available (necessary for sorting)
     */
    @SuppressWarnings("exports")
    public int parseTrackNo(Tag tag) {
        if (tag == null) {
            return Integer.MAX_VALUE;
        }
        String value = tag.getFirst(FieldKey.TRACK);
        return value.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(value);
    }

    /**
     * Adds an artist into the library in alphabetical order
     *
     * @param artist
     *                Artist object representing the artist to be added
     *                
     * @pre artist != null;
     */
    public void addArtist(Artist artist) {
        assert artist != null;
        boolean artistAdded = false;
        // if list is empty, will skip this and add by default
        for (int i=0; i < allArtists.size(); i++) {
            if (allArtists.get(i).compareTo(artist) > 0) {
                allArtists.add(i, artist);
                artistAdded = true;
                break;
            }
        }
        if (!artistAdded){
            allArtists.add(artist); // add to end
        }
    }

    /**
     * Adds an album to the library, ordered by artist alphabetically and then chronologically within each artist
     * Added to the allAlbums list, as well as the list of albums within the album artist
     *
     * @param album
     *                Album object representing the album to be added to the library
     *                
     * @pre album != null;
     */
    public void addAlbum(Album album) {
        assert album != null;
        boolean albumAdded = false;
        // if list is empty, will skip this and add by default
        for (int i=0; i < allAlbums.size(); i++) {
            if (allAlbums.get(i).compareTo(album) > 0) {
                allAlbums.add(i, album);
                albumAdded = true;
                break;
            }
        }
        if (!albumAdded){
            allAlbums.add(album); // add to end
        }
        album.getAlbumArtist().addAlbum(album);
    }

    /**
     * Adds a playlist object into the library's allPlaylists list
     *
     * @param playlist
     *                  Playlist object to be added to the library
     *                
     * @pre playlist != null
     */
    public void addPlaylist(Playlist playlist) {
        assert playlist != null;
        allPlaylists.add(playlist);
    }

    /**
     * Adds multiple audio files selected by the user
     *
     * @param selectedFiles
     *                  List of song files to be added to the library
     * 
     * @pre selectedFiles != null && selectedFiles.size() > 0;
     */
    public void addAudioFiles(List<File> selectedFiles) {
        assert selectedFiles != null && selectedFiles.size() > 0;
        for (File file : selectedFiles) {
            if (isAudioFile(file)){
                addSong(file);
            }
        }
    }

    /**
     * Adds all audio files inside a folder into the library
     * Includes all subfolders as well
     *
     * @param absolutePath
     *                  A file path to a folder containing audio files
     * 
     * @pre absolutePath != null;
     */
    public void addFolderAudio(String absolutePath) {
        assert absolutePath != null;
        // Create a File object for the specified folder
        File folder = new File(absolutePath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isAudioFile(file)) {
                        // Add song to the library
                        addSong(file);
                    } else if (file.isDirectory()) {
                        addFolderAudio(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Checks if the file's filename ends with compatible file extensions
     *
     * @param file
     *                  A file path to a folder containing audio files
     * 
     * @pre file != null;
     * 
     * @return Boolean representing whether the file is a compatible audio file
     */
    private boolean isAudioFile(File file) {
        assert file != null;
        String fileName = file.getName().toLowerCase();
        // Check if the file has a known audio file extension
        return fileName.endsWith(".wav") || fileName.endsWith(".aiff") || fileName.endsWith(".aif") || fileName.endsWith(".mp3");
    }

    /**
     * Completely removes a song from the library
     * Will remove it from the allSongs list and the album songlist it belongs to
     * If it's the only song in the album, the album will be deleted as well
     *
     * @param song
     *                  A song object to be deleted from the library
     * 
     * @pre song != null;
     */
    public void removeSong(Song song) {
        assert song != null;
        allSongs.remove(song);
        Album songAlbum = song.getAlbum();
        songAlbum.removeSong(song);

        if (songAlbum.getSongList().size() < 1) {
            removeAlbum(songAlbum);
        }
    }

    /**
     * Removes an album from the allAlbums list and the album artist's albums list
     * 
     * @param album
     *                  An album to be deleted from the library
     * 
     * @pre album != null;
     */
    private void removeAlbum(Album album) {
        assert album != null;
        allAlbums.remove(album);
        album.getAlbumArtist().removeAlbum(album);
    }

    /**
     * Saves the entire library
     */
    public void saveData() {
        librarySerializer.saveData();
    }
}