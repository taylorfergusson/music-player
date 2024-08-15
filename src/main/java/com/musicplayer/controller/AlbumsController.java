package com.musicplayer.controller;
import java.io.IOException;

import com.musicplayer.model.*;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Controller for the list of albums that show in grid form
 */
public class AlbumsController implements ViewChangeListener {

    @FXML
    private ScrollPane albumScrollPane;

    @FXML
    private TilePane albumTilePane;

    @FXML
    private ImageView backImageView;

    @FXML
    private ImageView forwardImageView;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Label pageNumberLabel;

    ObservableList<Album> allAlbums;

    private ViewModel model;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 60;

    public AlbumsController(){
    }

    public void initialize(){
        setModel();
        allAlbums = Library.getLibrary().getAlbums();
        backImageView.setImage(IconCache.getIcon("back"));
        forwardImageView.setImage(IconCache.getIcon("forward"));
        
    }

    /**
     * Goes to the next page of albums
     */
    @FXML
    private void nextPage() {
        ++currentPage;
        loadAlbumScrollPane(currentPage);
    }

    /**
     * Goes to the previous page of albums
     */
    @FXML
    private void previousPage() {
        --currentPage;
        loadAlbumScrollPane(currentPage);
        forwardButton.setVisible(true);
    }
    

    @Override
    public void setModel() {
        this.model = ViewModel.getInstance();
        model.addViewChangeListener(this);
    }

    @Override
    public void onViewChange(String selectedView) {
        if (selectedView.equals("Albums")) {
            loadAlbumScrollPane(0); // first page, or page 0
            albumScrollPane.setVisible(true);
        } else {
            albumScrollPane.setVisible(false);
        }
    }

    /**
     * Loads the proper albums depending on what page is to be loaded, based on the ITEMS_PER_PAGE constant
     * Hides forward button if on last page, hides back button if on first page
     *
     * @param page
     *                The page of the list of albums to be loaded
     * @pre page>=0;
     */
    private void loadAlbumScrollPane(int page) {
        assert page >= 0;
        albumTilePane.getChildren().clear();

        int albumsSize = allAlbums.size();
        boolean onLastPage = (currentPage * ITEMS_PER_PAGE < albumsSize) && ((currentPage + 1) * ITEMS_PER_PAGE > albumsSize);
        
        pageNumberLabel.setText(String.valueOf(currentPage+1));
        if (onLastPage) {
            forwardButton.setVisible(false);
        } else {
            forwardButton.setVisible(true);
        }

        if (currentPage < 1) {
            backButton.setVisible(false);
        } else {
            backButton.setVisible(true);
        }

        try {
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                if (page * ITEMS_PER_PAGE + i >= allAlbums.size()) { // if we ran out of albums
                    break;
                }
                Album album = allAlbums.get(page * ITEMS_PER_PAGE + i);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicplayer/AlbumItem.fxml"));
                VBox albumItem = loader.load();
                AlbumItemController controller = loader.getController();
                controller.setAlbumData(album);
                albumTilePane.getChildren().add(albumItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}