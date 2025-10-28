package com.example.platformer;

import javafx.scene.image.Image;

public class ImageInfo {
    public static void printImageSize(String resourcePath) {
        try (var is = ImageInfo.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("resource not found: " + resourcePath);
                return;
            }
            Image img = new Image(is);
            System.out.println("image: " + resourcePath + " -> " + img.getWidth() + "x" + img.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}