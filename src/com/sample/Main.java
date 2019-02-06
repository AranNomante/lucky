package com.sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main extends Application {
    private static final double ONE_MEGA_BYTE = 1024 * 1024;
    private final static String[] SOUND_LIST = {
            "bgm_credits.mp3", "bgm_game.mp3", "bgm_game_1.mp3", "bgm_game_2.mp3", "bgm_game_3.mp3", "bgm_how_to.mp3",
            "bgm_menu.mp3", "bgm_victory.mp3", "sfx_button_clicked.wav", "sfx_card_unfold.wav", "sfx_toggle.wav"
    };
    private static final String[] SUFFICES = {"", "_1", "_2", "_3"};
    static Random random = new Random(); //used elsewhere
    static boolean isMuted;
    static Stage window;
    static MediaPlayer mediaPlayerBGM;
    static MediaPlayer mediaPlayerSFX;
    private static int instances = 0;
    private static HashMap<String, Media> sounds = new HashMap<>();
    //private static ArrayList<Double> usage = new ArrayList<>();
    private final int id;

    // testing shows only one instance is created
    //
    public Main() {
        id = ++instances;
        System.out.printf("%s object #%d created\n", this.getClass().getSimpleName(), id);
    }

    public static void main(String[] args) {
        launch(args);
       // new MemoryTrack(usage);
        //uncomment line 32 44 103 to generate memory usage txt...

    }

    static void playBGM(String key) {
        if (mediaPlayerBGM != null) {
            mediaPlayerBGM.stop();
            mediaPlayerBGM.dispose();
        }
        if (key.equals("bgm_game")) {
            key += SUFFICES[random.nextInt(SUFFICES.length)];
        }
        mediaPlayerBGM = new MediaPlayer(sounds.get(key));
        mediaPlayerBGM.setStartTime(Duration.ZERO);
        mediaPlayerBGM.setCycleCount(MediaPlayer.INDEFINITE);
        if (isMuted) {
            mediaPlayerBGM.setVolume(0.0);
        }
        mediaPlayerBGM.play();  //play even if muted else unmute is noop
    }

    static void playSFX(String key) {
        if (mediaPlayerSFX != null) {
            mediaPlayerSFX.stop();
            mediaPlayerSFX.dispose();
        }
        mediaPlayerSFX = new MediaPlayer(sounds.get(key));
        if (isMuted) {
            mediaPlayerSFX.setVolume(0.0);
        } else {
            mediaPlayerSFX.setVolume(0.25);
        }
        mediaPlayerSFX.play();  //play even if muted else unmute is noop
    }

    // Mandatory override - called by framework (after calling optional override: init)
    //
    @Override
    public void start(Stage primaryStage) throws Exception {
        isMuted = false;
        loadSounds();
        playBGM("bgm_menu");
        window = primaryStage;
        Parent rootMenu = FXMLLoader.load(getClass().getResource("menu.fxml"));
        // long running operation runs on different thread
        Thread scoreThread = new Thread(() -> {
            Runnable updater = () -> {
                if (!Game.getGameIsOver() && Game.getScore() != 0 && window.getTitle().equals("The Main Pick") &&
                        Game.firstClickHappened()) {
                    Game.scoreCalculator();
                }
            };

            while (true) {
                try {
                    Runtime r = Runtime.getRuntime();
                    double mbUsed = (r.totalMemory() - r.freeMemory()) / ONE_MEGA_BYTE;
                    System.err.printf("MB used = %f.\n", mbUsed);
                 //   usage.add(mbUsed);
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        });
        // don't let thread prevent JVM shutdown
        scoreThread.setDaemon(true);
        scoreThread.start();
        window.setTitle("Main Menu");
        window.setScene(new Scene(rootMenu, 600, 600));
        //  window.setResizable(false);
        window.show();
    }

    private void loadSounds() {
        for (String soundName : SOUND_LIST) {
            URL resource = getClass().getResource("/" + soundName);
            String key = soundName.substring(0, soundName.lastIndexOf('.'));
            if (!sounds.containsKey(key)) {
                System.out.printf("Adding sound %s -> %s\n", soundName, resource);
                sounds.put(key, new Media(resource.toString()));
            } else {
                System.out.printf("Not adding sound %s (already added)\n", soundName);
            }
        }
    }
}