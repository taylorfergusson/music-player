package com.musicplayer.model;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to have photos and icons easily accessible for later use
 */
public class IconCache {
    private static final Map<String, Image> iconMap = new HashMap<>();

    /**
     * Takes in an icon name and file path as input and adds them to the iconMap hashmap for later access
     *
     * @param iconName
     *                Name of the icon to use as an identifier later
     * @param iconResourcePath
     *                Relative path of the icon image, found in the resources folder
     *                
     * @pre iconName != null && iconResourcePath != null;
     */
    public static void loadIcon(String iconName, String iconResourcePath) {
        assert iconName != null && iconResourcePath != null;
        Image icon = new Image(IconCache.class.getResource(iconResourcePath).toExternalForm());
        iconMap.put(iconName, icon);
    }

    /**
     * Takes in an icon name as input and gets the appropriate icon from the saved icons
     *
     * @param iconName
     *                Name of the icon to get
     *                
     * @pre iconName != null
     * 
     * @return The desired icon from the iconMap
     */
    public static Image getIcon(String iconName) {
        assert iconName != null;
        return iconMap.get(iconName);
    }
}
