package com.musicplayer.controller;
import com.musicplayer.model.*;

// import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for audio playback section at the bottom of the program
 */
public class PlaybackController {

    @FXML
    private Label songTitleLabel;

    @FXML
    private Label songArtistLabel;

    @FXML
    private ToggleButton shuffleButton;

    @FXML
    private Button rewindButton;

    @FXML
    private ToggleButton playPauseButton;

    @FXML
    private Button fastForwardButton;

    @FXML
    private Button repeatButton;

    @FXML
    private ToggleButton queueButton;

    @FXML
    private Button speakerButton;

    @FXML
    private Button loudSpeakerButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView shuffleImage;

    @FXML
    private ImageView rewindImage;

    @FXML
    private ImageView playPauseImage;

    @FXML
    private ImageView fastForwardImage;

    @FXML
    private ImageView repeatImage;

    @FXML
    private ImageView queueImage;

    @FXML
    private ImageView speakerImage;

    @FXML
    private ImageView loudSpeakerImage;

    @FXML
    private Slider songPositionSlider;

    @FXML
    private Label songPositionLabel;

    @FXML
    private Label songEndLabel;

    private Image shuffleOffIcon;
    private Image shuffleIcon;
    private Image rewindIcon;
    private Image playIcon;
    private Image pauseIcon;
    private Image fastForwardIcon;
    private Image repeatOffIcon;
    private Image repeatIcon;
    private Image repeatOneIcon;
    private Image queueIcon;
    private Image speakerIcon;
    private Image loudSpeakerIcon;

    private int repeatButtonIndex = 0;

    private AudioPlayerManager audioPlayer = AudioPlayerManager.getInstance();
    // private Timeline songTimeline;
    private QueueModel queueModel = QueueModel.getInstance();


    public PlaybackController(){
    }

    public void initialize() {
        songTitleLabel.textProperty().bind(audioPlayer.getTitle());
        songArtistLabel.textProperty().bind(audioPlayer.getArtist());
        getIcons();
        setButtonImages();
        setupSongTimeline();
        volumeSlider.valueProperty().bindBidirectional(audioPlayer.getVolume());
    }

    /**
     * Gets all the appropriate icons for the buttons from the IconCache
     */
    private void getIcons() {
        shuffleOffIcon = IconCache.getIcon("shuffle-off");
        shuffleIcon = IconCache.getIcon("shuffle");
        rewindIcon = IconCache.getIcon("rewind");
        playIcon = IconCache.getIcon("play");
        pauseIcon = IconCache.getIcon("pause");
        fastForwardIcon = IconCache.getIcon("fast-forward");
        repeatOffIcon = IconCache.getIcon("repeat-off");
        repeatIcon = IconCache.getIcon("repeat");
        repeatOneIcon = IconCache.getIcon("repeat-one");
        queueIcon = IconCache.getIcon("queue");
        speakerIcon = IconCache.getIcon("speaker");
        loudSpeakerIcon = IconCache.getIcon("loud-speaker");
    }

    /**
     * Sets up the actual ImageView items from the loaded icons
     * Binds the shuffle images and play pause images with their respective selected properties, since they change
     */
    private void setButtonImages() {
        // regular buttons
        rewindImage.setImage(rewindIcon);
        fastForwardImage.setImage(fastForwardIcon);
        repeatImage.setImage(repeatOffIcon); // will be changed later
        queueImage.setImage(queueIcon);
        speakerImage.setImage(speakerIcon);
        loudSpeakerImage.setImage(loudSpeakerIcon);

        // toggle buttons
        shuffleButton.selectedProperty().bindBidirectional(audioPlayer.getShuffleOn());
        shuffleImage.imageProperty().bind(Bindings.when(shuffleButton.selectedProperty()).then(shuffleIcon).otherwise(shuffleOffIcon));

        playPauseButton.selectedProperty().bindBidirectional(audioPlayer.getIsPlaying());
        playPauseImage.imageProperty().bind(Bindings.when(playPauseButton.selectedProperty()).then(pauseIcon).otherwise(playIcon));
    }

    /**
     * Set up the song timeline slider, along with the numerical representation of the time elapsed and the time remaining
     */
    private void setupSongTimeline() {
        DoubleProperty songPosition = audioPlayer.getSongPosition();
        DoubleProperty songLength = audioPlayer.getTime();
        songPositionSlider.valueProperty().bindBidirectional(songPosition);
        songPositionSlider.maxProperty().bind(songLength); // set the max position of the slider to the song length

        // dependencies at the end
        ObservableValue<String> timeElapsed = Bindings.createStringBinding(() -> secondsToClockFormat(songPosition.get()), songPosition);
        songPositionLabel.textProperty().bind(timeElapsed);
 
        ObservableValue<String> timeRemaining = Bindings.createStringBinding(() -> secondsToClockFormat(songLength.get() - songPosition.get()), songLength, songPosition);
        songEndLabel.textProperty().bind(timeRemaining);
        
        songPositionSlider.setOnMouseReleased(event -> {
            audioPlayer.setSongPosition(songPosition.get());
        });
    }

    /**
     * Turns shuffle on or off depending if the button is selected
     */
    public void shuffleClicked() {
        if (shuffleButton.isSelected()) {
            audioPlayer.setShuffleOn(true);
        } else {
            audioPlayer.setShuffleOn(false);
        }
    }

    /**
     * When rewind button is clicked, tells the audioplayer to go back
     * Will either restart the song or go to the previous song depending on the time elapsed
     */
    public void rewindClicked() {
        audioPlayer.goBack();
    }

    /**
     * Will play next song when fast forward button is clicked
     */
    public void fastForwardClicked() {
        audioPlayer.playNextSong();
    }
        
    /**
     * When play pause button is clicked, will play if the button is selected, or pause otherwise
     */
    public void playPauseClicked() {
        if (playPauseButton.isSelected()) {
            audioPlayer.play();
        } else {
            audioPlayer.pause();
        }
    }

    /**
     * When repeat button is clicked, will show the appropriate button image and change repeat settings accordingly
     * Cycles through the 3 options, with the default (0) being off, 1 click being on, and 2 clicks making the audioplayer repeat the current song
     * Will return back to 0 on the 3rd click
     */
    public void repeatClicked() { // 0 is off, 1 is on, 2 is one
        repeatButtonIndex = (repeatButtonIndex + 1) % 3;

        switch(repeatButtonIndex) {
            case 0:
                repeatImage.setImage(repeatOffIcon);
                audioPlayer.setRepeatStatus("off");
                break;
            case 1:
                repeatImage.setImage(repeatIcon);
                audioPlayer.setRepeatStatus("on");
                break;
            case 2:
                repeatImage.setImage(repeatOneIcon);
                audioPlayer.setRepeatStatus("one");
                break;
        }
    }

    /**
     * Shows or hides the queue box depending on whether it's currently showing
     */
    @FXML
    private void showOrHideQueue() {
        queueModel.showOrHideQueue();
    }

    /**
     * Turns the volume down when volume down button clicked, also changes the slider position
     */
    @FXML
    private void volumeDown() {
        double newValue = volumeSlider.getValue() - 0.1;
        volumeSlider.setValue(newValue);
    }

    /**
     * Turns the volume up when volume up button clicked, also changes the slider position
     */
    @FXML
    private void volumeUp() {
        double newValue = volumeSlider.getValue() + 0.1;
        volumeSlider.setValue(newValue);
    }

    /**
     * Converts a double object representing time in seconds into clock format as a string object
     *
     * @param time
     *                The time in double format representing seconds
     * @pre time >= 0;
     */
    private String secondsToClockFormat(double time) {
        assert time >= 0;
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
