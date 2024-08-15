package com.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a singleton ViewModel to manage the different views of the library
 */
public class ViewModel {
    private static volatile ViewModel INSTANCE = null;
    private String selectedView;
    private List<ViewChangeListener> listeners = new ArrayList<>();

    private ViewModel(){
    }

    /**
     * Ensures that there's only one ViewModel at a time, so there's not more than 1 manager for the different views
     * @return Singleton instance of ViewModel
     */
    public static ViewModel getInstance() {
        if (INSTANCE == null) {
            synchronized (ViewModel.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModel();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Getter for the current library view selected
     *
     * @return The current view that is selected
     */
    public String getSelectedView() {
        return selectedView;
    }

    /**
     * Sets the selected view to the given view, to change the way the library is shown to the user
     *
     * @param selectedView
     *                A string representing which view should be shown
     * @pre selectedView != null;
     */
    public void setSelectedView(String selectedView) {
        assert selectedView != null;
        this.selectedView = selectedView;
        notifyListeners();
    }

    /**
     * Adds a view change listener to the listeners list to keep track of changes in the library view
     *
     * @param listener
     *                A ViewChangeListener object to pay attention to when the view is changed
     * @pre listener != null;
     */
    public void addViewChangeListener(ViewChangeListener listener) {
        assert listener != null;
        listeners.add(listener);
    }

    /**
     * Notifies all listeners of any changes to the view
     */
    private void notifyListeners() {
        for (ViewChangeListener listener : listeners) {
            listener.onViewChange(selectedView);
        }
    }
}
