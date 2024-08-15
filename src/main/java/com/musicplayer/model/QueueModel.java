package com.musicplayer.model;


import com.musicplayer.controller.QueueController;

/**
 * Represents a singleton audio queue of songs that are coming up next
 */
public class QueueModel {
    private static volatile QueueModel INSTANCE = null;
    private QueueController queueListener;

    private QueueModel(){
    }

    /**
     * Ensures that there's only one QueueModel at a time, so there's not more than 1 list of songs to pick from
     * @return Singleton instance of QueueModel
     */
    public static QueueModel getInstance() {
        if (INSTANCE == null) {
            synchronized (QueueModel.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QueueModel();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Shows or hides the list of songs in the queue depending on the current state
     */
    public void showOrHideQueue() {
        queueListener.onQueueSelectedChange();
    }

    /**
     * Manages QueueController objects that will act as queueListeners and pay attention to the queue's state
     *
     * @param queueListener
     *                  Adds a QueueController object that will listen to the queue
     * 
     * @pre queueListener != null
     */
    public void addQueueListener(QueueController queueListener) {
        assert queueListener != null;
        this.queueListener = queueListener;
    }
}
