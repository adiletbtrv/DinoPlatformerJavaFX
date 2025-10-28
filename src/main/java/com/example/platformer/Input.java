package com.example.platformer;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class Input {
    private final Set<KeyCode> keys = new HashSet<>();

    public Input(Scene scene) {
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));
    }

    public boolean isPressedLeft() { return keys.contains(KeyCode.A) || keys.contains(KeyCode.LEFT); }
    public boolean isPressedRight() { return keys.contains(KeyCode.D) || keys.contains(KeyCode.RIGHT); }
    public boolean isPressedJump() { return keys.contains(KeyCode.SPACE) || keys.contains(KeyCode.W) || keys.contains(KeyCode.UP); }
    public boolean isPressedUp() { return keys.contains(KeyCode.W) || keys.contains(KeyCode.UP); }
    public boolean isPressedDown() { return keys.contains(KeyCode.S) || keys.contains(KeyCode.DOWN); }
}