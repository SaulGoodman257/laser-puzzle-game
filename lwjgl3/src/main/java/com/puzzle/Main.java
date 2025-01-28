package com.puzzle;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class Main {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Laser Puzzle Game");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setResizable(false);
        config.setWindowIcon(Files.FileType.Internal, "icon_256.png" , "icon_128.png", "icon_64.png","icon_48.png",  "icon_32.png");
        new Lwjgl3Application(new MainGame(), config);
    }
}
