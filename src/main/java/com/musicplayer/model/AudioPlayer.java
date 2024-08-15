package com.musicplayer.model;
import com.musicplayer.controller.AlertBoxController;
// Java program to play an Audio 
// file using Clip Object 
import java.io.File; 

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration; 
import javafx.scene.media.MediaPlayer;

/**
 * AudioPlayer class with a songPosition, songTimeline and mediaPlayer
 */
public class AudioPlayer {
    private DoubleProperty songPosition = new SimpleDoubleProperty();
    private Timeline songTimeline;
    MediaPlayer mediaPlayer;

    public AudioPlayer() {
    }

    /**
     * Loads the song into the audio player and starts the timeline. Will give an error message if load fails
     *
     * @param filePath
     *                An absolute file path to the song in question
     * @pre filePath != null
     * 
     * @return Boolean representing if the song has been loaded successfully
     */
    public Boolean load(String filePath) {
        assert filePath != null;
        try {
            Media media = new Media(new File(filePath).toURI().toString());
            this.mediaPlayer = new MediaPlayer(media);
            this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            songPosition.set(0);

            songTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    songPosition.set(mediaPlayer.getCurrentTime().toSeconds());
                }) 
            );
            songTimeline.setCycleCount(Timeline.INDEFINITE);

            // Only start this once it actually started playing
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.toSeconds() >= 0.05) {
                    mediaPlayer.setOnEndOfMedia(() -> {
                        AudioPlayerManager.getInstance().notifySongEnded();
                    });
                }
            });
            return true;
        } catch (MediaException e) {
            AlertBoxController.display("Error", "Unable to play song.");
            return false;
        }
    }

    /**
     * Method to play the audio file
     */
    public void play() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.play();
        songTimeline.play();
    }
    /**
     * Method to pause the audio file
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * Method to stop the audio file
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Method to restart the audio file
     */
    public void restart() {
        mediaPlayer.seek(Duration.seconds(0));
    }

    public DoubleProperty getSongPosition() {
        return songPosition;
    }

    /**
     * Sets a new song position, given by the slider position
     *
     * @param newPosition
     *                The position of the song to be skipped to
     * @pre newPosition >= 0
     */
    public void setSongPosition(double newPosition) {
        assert newPosition >= 0;
        mediaPlayer.seek(Duration.seconds(newPosition));
    }

    public double getVolume() {
        return mediaPlayer.getVolume();
    }

    /**
     * Sets a new volume, given by the volume slider or volume buttons
     *
     * @param newVolume
     *                The preferred volume of the audio
     * @pre newVolujme >= 0
     */
    public void setVolume(Double newVolume) {
        assert newVolume >= 0;
        mediaPlayer.setVolume(newVolume);
    }
}