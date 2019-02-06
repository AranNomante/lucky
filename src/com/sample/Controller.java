package com.sample;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;

public class Controller {
    private static final Duration TRANSITION_LEN = new Duration(200);
    //Looks like many instances of Controller are created
    //In particular whenever a new game starts..
    //
    private static int instances = 0;
    private static Circle[] circles = new Circle[4];
    private final int id;
    @FXML
    public RadioButton mute;
    @FXML
    public Button newGame;
    @FXML
    public Button howTo;
    @FXML
    public Button credits;
    @FXML
    public Button exit;
    @FXML
    public Button menu;
    @FXML
    public GridPane pane;
    @FXML
    public Label score;
    @FXML
    public Label time;
    @FXML
    public Label tries;
    @FXML
    public Circle black;
    @FXML
    public Circle red;
    @FXML
    public Circle blue;
    @FXML
    public Circle yellow;
    private FadeTransition transitionButton;
    private FadeTransition transitionIndicatorDisappear;
    private FadeTransition transitionIndicatorAppear;
    private boolean animating;

    public Controller() {
        id = ++instances;
        System.out.printf("%s object #%d created\n", this.getClass().getSimpleName(), id);
    }

    // warning: mute can be null when this method called during load victory scene
    // some fxml voodoo at work, intellij doesn't think anyone calls this method
    /// (maybe in fxml controller has to be defined or initialize has to be public)
    /// tried muting and loading victory scene and unmuting and trying nothing happened didn't understand why would it be a problem
    public void initialize() {
        if (score != null && time != null && tries != null && blue != null && red != null
                && black != null && yellow != null) {
            System.out.println("Controller: Binding properties.. game");
            score.textProperty().bind(Game.scoreProperty);
            time.textProperty().bind(Game.timeProperty);
            tries.textProperty().bind(Game.triesProperty);
            animating = false;
            transitionButton = new FadeTransition();
            transitionIndicatorDisappear = new FadeTransition();
            transitionIndicatorAppear = new FadeTransition();
            hideIndicators();
            System.out.println("Game screen loaded");
        } else if (score != null && time != null && tries != null) {
            System.out.println("Controller: Binding properties.. victory");
            score.textProperty().bind(Game.scoreProperty);
            time.textProperty().bind(Game.timeProperty);
            tries.textProperty().bind(Game.triesProperty);
        } else {
            System.out.println("Controller: NOT binding properties, because..");
            System.out.printf(
                    "score != null : %b,  time != null : %b, tries != null : %b\n",
                    score != null, time != null, tries != null
            );
        }
        if (mute != null) {
            mute.setSelected(Main.isMuted);
        }
    }

    public void newGameButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Game.resetGame();
            Main.window.hide();
            Main.window.setScene(getScene("game"));
            Main.window.setTitle("The Main Pick");
            Main.window.setMaximized(false);
            Main.playBGM("bgm_game");
            Main.window.show();
        } catch (IOException e) {
            System.err.println("could not change the scene to: game");
        }
    }

    public void menuButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_menu");
            if (Main.window.getTitle().equals("The Main Pick")) {
                Main.window.hide();
                Game.setGameIsOver();
                Main.window.setScene(getScene("menu"));
                Main.window.setMaximized(false);
                Main.window.show();
            } else {
                Main.window.setScene(getScene("menu"));
            }
            Main.window.setTitle("Main Menu");
        } catch (IOException e) {
            System.err.println("could not change the scene to: game");
        }
    }

    private void indicatorAnim(String color) {
        for (Circle circle :
                circles) {
            if (color.equals(circle.getId()) && circle.isVisible()) {
                break;
            } else if (color.equals(circle.getId())) appear(circle);
            else if (circle.isVisible()) disappear(circle);
        }
    }

    private void appear(Circle circle) {
        transitionIndicatorAppear.setNode(circle);
        transitionIndicatorAppear.setDuration(TRANSITION_LEN);
        transitionIndicatorAppear.setFromValue(0.0);
        transitionIndicatorAppear.setToValue(1.0);
        transitionIndicatorAppear.setCycleCount(1);
        transitionIndicatorAppear.play();
        circle.setVisible(true);
    }

    private void disappear(Circle circle) {
        transitionIndicatorDisappear.setNode(circle);
        transitionIndicatorDisappear.setDuration(TRANSITION_LEN);
        transitionIndicatorDisappear.setFromValue(1.0);
        transitionIndicatorDisappear.setToValue(0.0);
        transitionIndicatorDisappear.setCycleCount(1);
        transitionIndicatorDisappear.setOnFinished(actionEvent -> circle.setVisible(false));
        transitionIndicatorDisappear.play();
    }

    private void hideIndicators() {
        blue.setVisible(false);
        red.setVisible(false);
        yellow.setVisible(false);
        yellow.setId("yellow");
        black.setId("black");
        red.setId("red");
        blue.setId("blue");
        circles[0] = black;
        circles[1] = blue;
        circles[2] = yellow;
        circles[3] = red;
    }

    public void gameButtonClicked(ActionEvent event) {
        ObservableList<Node> buttons = pane.getChildren();
        Button button = (Button) event.getSource();
        int index = buttons.indexOf(button);
        int column = index % 10;
        int row = (index - index % 10) / 10;
        if (!((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure") &&
                !((Button) event.getSource()).getStyleClass().toString().equals("button button-uncovered")) {
            indicatorAnim(Game.clickValue(row, column));
            transitionButton.setNode((Button) event.getSource());
            transitionButton.setDuration(TRANSITION_LEN);
            transitionButton.setFromValue(1.0);
            transitionButton.setToValue(0.0);
            transitionButton.setCycleCount(1);
            transitionButton.setOnFinished(actionEvent -> {

                if (!((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure") &&
                        !((Button) event.getSource()).getStyleClass().toString().equals("button button-uncovered")) {

                    transitionButton.setFromValue(0.0);
                    transitionButton.setToValue(1.0);
                    System.out.println(((Button) event.getSource()).getStyleClass().toString());
                    ((Button) event.getSource()).getStyleClass().remove("button-covered");
                    ((Button) event.getSource()).getStyleClass().add(Game.click(row, column));
                    transitionButton.play();
                    transitionButton.setOnFinished(ActionEvent -> {
                        animating = false
                        ;
                        if (((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure")) {
                            loadVictoryScene();

                        }
                    });
                }
            });
            System.out.printf("Animating: %b\n", animating);
            if (!animating) {
                animating = true;
                transitionButton.play();
                Main.playSFX("sfx_card_unfold");
            }
        }

        System.out.printf("button:%d (row:%d,col:%d)\n", index, row, column);
    }

    public void howToPlayButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_how_to");
            Main.window.setScene(getScene("howTo"));
            Main.window.setTitle("How to Play");
        } catch (IOException e) {
            System.err.println("could not change the scene to: how to play");
        }
    }

    public void creditsButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_credits");
            Main.window.setScene(getScene("credits"));
            Main.window.setTitle("Credits");
        } catch (IOException e) {
            System.err.println("could not change the scene to: credits");
        }
    }

    public void exitButtonClicked() {
        Main.playSFX("sfx_button_clicked");
        Main.window.close();
    }

    public void muteRadioButtonChecked() {
        Main.playSFX("sfx_toggle");
        if (mute.isSelected()) {
            Main.isMuted = true;
            Main.mediaPlayerBGM.setVolume(0.0);
            Main.mediaPlayerSFX.setVolume(0.0);
            System.out.println("muted");
        } else {
            Main.isMuted = false;
            Main.mediaPlayerBGM.setVolume(1.0);
            Main.mediaPlayerSFX.setVolume(1.0);

            System.out.println("unmuted");
        }
    }

    private void loadVictoryScene() {
        System.out.println("Loading victory scene\n");
        try {
            Main.window.hide();
            Main.playBGM("bgm_victory");
            Main.window.setScene(getScene("victory"));
            Main.window.setTitle("Victory");
            Main.window.setMaximized(false);
            Main.window.show();
        } catch (IOException e) {
            System.err.println("could not change the scene to: victory");
        }
    }

    private Scene getScene(String name) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(name + ".fxml"));
        if (name.equals("game")) {
            return new Scene(root, 700, 700);
        }
        return new Scene(root, 600, 600);
    }

    //except the current controller and the first (#1) rest are garbage collected...
    @Override
    protected void finalize() throws Throwable {
        try {
            System.err.println("Object destroyed of type"
                    + this.getClass().toString());
            System.err.println("#id:" + this.id);
        } finally {
            super.finalize();
        }
    }
}