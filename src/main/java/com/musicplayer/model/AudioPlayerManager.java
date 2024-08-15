package com.musicplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Audio player manager class
 * Manages which song is being played, which songs are next, and the presentation of song info
 */
public class AudioPlayerManager {
    private static volatile AudioPlayerManager INSTANCE = null;
    private AudioPlayer audioPlayer = new AudioPlayer();
    private ObservableList<Song> songList = FXCollections.observableArrayList();
    private ObservableList<Song> originalSongList = FXCollections.observableArrayList(); // for unshuffled
    private ObservableList<Song> playNextList = FXCollections.observableArrayList();
    private ObservableList<Song> playLastList = FXCollections.observableArrayList();
    private ObservableList<Song> queueList = FXCollections.observableArrayList();
    private int songIndex;
    private Song song;
    private StringProperty title = new SimpleStringProperty("Untitled");
    private StringProperty artist = new SimpleStringProperty("Unknown Album");
    private DoubleProperty time = new SimpleDoubleProperty();
    private DoubleProperty songPosition = new SimpleDoubleProperty();
    private DoubleProperty volume = new SimpleDoubleProperty(1.0);
    private ObjectProperty<Image> coverArt = new SimpleObjectProperty<>((Image) new WritableImage(250, 250));
    private BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private BooleanProperty shuffleOn = new SimpleBooleanProperty(false);
    private String repeatStatus = "off";

    private AudioPlayerManager(){
        coverArt.set(new WritableImage(250, 250));
        volume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                audioPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    /**
     * Ensures that there's only one audio player at a time, so there's not more than 1 audio file playing
     * @return Singleton instance of AudioPlayerManager
     */
    public static AudioPlayerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AudioPlayerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioPlayerManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Sets up a song list for the songs to be played from
     * Necessary for knowing what song to play next, and shuffling songs
     *
     * @param songList
     *                The list of songs that the current song is in
     * @pre songList != null && songList.size() > 0;
     */
    public void setUpSongList(ObservableList<Song> songList) {
        assert songList != null && songList.size() > 0;
        this.originalSongList.setAll(songList); // unshuffled
        this.songList.setAll(songList);
    }

    /**
     * Shuffles the song list that is currently loaded into the program
     */
    private void shuffleSongList() {
        Song prioritySong = songList.remove(songIndex); // remove the song being played and shuffle the rest
        List<Song> songListToShuffle = new ArrayList<>(songList);
        Collections.shuffle(songListToShuffle);
        songList.setAll(songListToShuffle);
        songList.add(0, prioritySong);
        songIndex = 0;
    }

    /**
     * Loads the song at the particular index of the current song list
     *
     * @param songIndex
     *                The index of the song in the song list
     * @pre songIndex >= 0;
     * 
     * @return Boolean representing whether the song was successfully loaded
     */
    public Boolean loadSongByIndex(int songIndex) {
        assert songIndex >= 0;
        Song newSong = songList.get(songIndex);
        int oldSongIndex = songIndex;
        this.songIndex = songIndex;
        if (shuffleOn.get()) { // make the songlist a shuffled version
            shuffleSongList();
        }
        if (!loadSong(newSong)) {
            this.songIndex = oldSongIndex; // didnt work, revert to old one
            return false;
        }
        return true;
    }

    /**
     * Loads the new song into the audio player and updates the display info if it loads successfully
     *
     * @param newSong
     *                A song object representing the new song to be loaded
     * @pre newSong != null;
     * 
     * @return Boolean representing whether the song was successfully loaded
     */
    private Boolean loadSong(Song newSong) {
        assert newSong != null;
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        if (song != null) {
            song.getAlbum().setIsPlaying(false);
        }
        String filePath = newSong.getFilePath();

        if (!audioPlayer.load(filePath)) {
            return false;
        }
        song = newSong;
        title.set(song.getTitle());
        artist.set(song.getArtistName());
        time.set(song.getTime());
        coverArt.set(song.getCoverArt());
        songPosition.bindBidirectional(audioPlayer.getSongPosition());
        audioPlayer.setVolume(volume.get());
        updateQueueList();
        return true;
    }

    /**
     * Loads the given album and starts playing the first song on the album
     *
     * @param album
     *                The album to be loaded and played
     *                
     * @pre album != null;
     */
    public void loadAlbum(Album album) {
        assert album != null;
        ObservableList<Song> oldSongList = songList;
        setUpSongList(album.getSongList());
        if (!loadSongByIndex(0)) {
            setUpSongList(oldSongList);
            return;
        }
        album.setIsPlaying(true);
    }

    public void play(){
        audioPlayer.play();
        isPlaying.set(true);
    }

    /**
     * Activated when the song ends, this function adds a play to the song and plays the next song in the list
     */
    public void notifySongEnded(){
        if (isPlaying.get()) {
            song.addPlay();
            playNextSong();
        }
    }

    /**
     * Adds the given song into the queue at the front so it plays next
     *
     * @param song
     *                The song to be played next in the queue
     *                
     * @pre song != null;
     */
    public void addToPlayNext(Song song) {
        assert song != null;
        playNextList.add(0, song);
        updateQueueList();
    }

    /**
     * Adds the given song into the queue at the back so it plays last
     *
     * @param song
     *                The song to be played last in the queue
     *                
     * @pre song != null;
     */
    public void addToPlayLast(Song song) {
        assert song != null;
        playLastList.add(song);
        updateQueueList();
    }

    /**
     * Updates the queue list for when there's any song added to the queue or skipped
     */
    private void updateQueueList() {
        ObservableList<Song> combinedList = FXCollections.observableArrayList();
        List<Song> remainingSongs = songList.subList(songIndex+1, songList.size());
        combinedList.addAll(playNextList);
        combinedList.addAll(remainingSongs);
        combinedList.addAll(playLastList);
        queueList.setAll(combinedList);
    }

    public ObservableList<Song> getQueueList() {
        return queueList;
    }

    /**
     * Plays the next song in the queue
     * If Repeat one is on, will repeat the current song
     * Empties play next list first, then regular song list, then play last list
     * When all lists empty, will go back to beginning of song list, will play again if repeat on or pause otherwise
     */
    public void playNextSong() {
        if (repeatStatus.equals("one")) {
            audioPlayer.restart();
            // play?
        } else if (playNextList.size() > 0) { // if songs in playnext, play these
            Song queueSong = playNextList.get(0); // so we can remove the data
            playNextList.remove(0);
            loadSong(queueSong);
            play();
        } else if (songIndex < songList.size() - 1) {
            loadSongByIndex(++songIndex);
            play();
        } else if (playLastList.size() > 0) { // if songs in playlast, play these
            Song queueSong = playLastList.get(0); // so we can remove the data
            playLastList.remove(0);
            loadSong(queueSong);
            play();
        } else {
            loadSongByIndex(0);
            if (repeatStatus.equals("on")) {
                play(); // play from the start instead of just loading
            } else {
                isPlaying.set(false); // if repeat off, pause all media
            }
        }
    }

    /**
     * Plays previous song OR restarts if at the front of list or past 5 seconds of the song
     */
    public void goBack() {
        int MIN_SECONDS_PAST = 5;
        if (songIndex == 0 || songPosition.get() > MIN_SECONDS_PAST) {
            audioPlayer.restart();
        } else {
            loadSongByIndex(--songIndex);
            play();
        }
    }

    public void pause() {
        audioPlayer.pause();
        isPlaying.set(false);
    }

    public void stop() {
        audioPlayer.stop();
        isPlaying.set(false);
    }

    public ObservableList<Song> getSongList() {
        return songList;
    }

    public StringProperty getTitle(){
        return title;
    }

    public StringProperty getArtist(){
        return artist;
    }

    public DoubleProperty getTime(){
        return time;
    }

    public ObjectProperty<Image> getCoverArt(){
        return coverArt;
    }

    public BooleanProperty getIsPlaying() {
        return isPlaying;
    }

    public BooleanProperty getShuffleOn() {
        return shuffleOn;
    }

    /**
     * Sets the shuffle on or off depending on the given boolean value
     * Updates the queue list to reflect changes
     *
     * @param shuffleOn
     *                Boolean representing whether shuffle should be on or off
     *                
     * @pre shuffleOn != null;
     */
    public void setShuffleOn(Boolean shuffleOn) {
        assert shuffleOn != null;
        this.shuffleOn.set(shuffleOn);
        if (songList.isEmpty()) {
            return;
        }
        if (shuffleOn) {
            shuffleSongList();
        } else {
            songList.setAll(originalSongList);
        }
        updateQueueList();
    }

    /**
     * Sets the repeat status of playback
     * Off: stops playing at the end of the song list
     * On: continues playing the song list after it reaches the end
     * One: repeats the same song continuously
     *
     * @param status
     *                String representing the 3 repeat options: off, on, or one
     *                
     * @pre status.equals("off") || status.equals("on") || status.equals("one");
     */
    public void setRepeatStatus(String status) {
        assert status.equals("off") || status.equals("on") || status.equals("one");
        this.repeatStatus = status;
    }

    public DoubleProperty getSongPosition() {
        return songPosition;
    }
    
    /**
     * Sets a new position of the song from a given double value
     * 
     * @param newPosition
     *                Double value representing the position of the track to be skipped to
     *                
     * @pre newPosition >= 0;
     */
    public void setSongPosition(double newPosition) {
        assert newPosition >= 0;
        audioPlayer.setSongPosition(newPosition);
    }

    public DoubleProperty getVolume() {
        return volume;
    }

    /**
     * Decreases the audio player volume by 0.1
     */
    public void decreaseVolume() {
        double newVolume = volume.get() - 0.1;
        volume.setValue(newVolume);
    }

    /**
     * Increases the audio player volume by 0.1
     */
    public void increaseVolume() {
        double newVolume = volume.get() + 0.1;
        volume.setValue(newVolume);
    }
} 