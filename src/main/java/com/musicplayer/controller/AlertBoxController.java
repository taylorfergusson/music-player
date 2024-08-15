package com.musicplayer.controller;
// import com.musicplayer.model.*;

// import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for an alert box that shows up when the user needs to know some information
 */
public class AlertBoxController {

    // @FXML
    // VBox alertVBox;

    // @FXML
    // Label alertLabel;

    // @FXML
    // Button alertButton;

    public AlertBoxController() {

    }

    public void initialize() {
    }

    /**
     * Opens a new window displaying an alert to the user
     *
     * @param title
     *                The title of the alert
     * @param message
     *                The description of the alert to the user
     * @pre title != null && message != null;
     */
    public static void display(String title, String message) {
        assert title != null && message != null;
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close the window");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("alertbox.css");
        window.setScene(scene);
        window.showAndWait();
    }
}