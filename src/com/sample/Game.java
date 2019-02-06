package com.sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;

public class Game {
    static StringProperty scoreProperty;
    static StringProperty timeProperty;
    static StringProperty triesProperty;
    private static int time;
    private static int score;
    private static int tries;
    private static int[][] tiles = new int[10][10];
    private static boolean gameIsOver;
    private static boolean firstClick;
    private static int instances = 0;
    private static int treasureColumn;
    private static int treasureRow;
    private int id;

    // Testing shows no instances created..
    //
    public Game() {
        id = ++instances;
        System.out.printf("%s object #%d created\n", this.getClass().getSimpleName(), id);
    }

    static void resetGame() {
        tries = 0;
        score = 100_000;
        time = 0;
        gameIsOver = false;
        firstClick = false;
        scoreProperty = new SimpleStringProperty("" + score);
        timeProperty = new SimpleStringProperty("0");
        triesProperty = new SimpleStringProperty("0");
        for (int[] row : tiles) {
            Arrays.fill(row, 0);
        }
        treasureColumn = Main.random.nextInt(10);
        treasureRow = Main.random.nextInt(10);
        System.out.printf("Treasure location:(row:%d, col:%d)\n", treasureRow, treasureColumn);
        tiles[treasureRow][treasureColumn] = 1;
    }

    static boolean firstClickHappened() {
        return firstClick;
    }

    static void setGameIsOver() {
        gameIsOver = true;
    }

    static boolean getGameIsOver() {
        return gameIsOver;
    }

    static int getScore() {
        return score;
    }

    static void scoreCalculator() {
        time++;
        int tenth = time / 10;
        if (tenth < 5) {
            score -= 100 * (1 + tenth);
        } else {
            score -= 1000;
        }
        if (score < 0) {
            score = 0;
        }
        scoreProperty.setValue("" + score);
        timeProperty.setValue("" + time);
        triesProperty.setValue("" + tries);
        System.out.printf("Score:%s,Time:%s,Tries%s\n", scoreProperty.getValue(), timeProperty.getValue(), triesProperty.getValue());
    }


    // More cohesive if it just returned true/false ?
    //
    static String click(int row, int column) {
        tries++;
        firstClick = true;
        int clickValue = tiles[row][column];
        System.out.printf("Clicked: %d,%d value: %d\n", row, column, clickValue);
        if (clickValue == 0) {
            score -= 1000;
        } else {
            setGameIsOver();
        }
        return (clickValue == 1) ? "button-treasure" : "button-uncovered";
    }

    static String clickValue(int row, int column) {
        int differenceRow = row - treasureRow;
        int differenceCol = column - treasureColumn;
        double distance = Math.sqrt(Math.pow(differenceRow, 2) + Math.pow(differenceCol, 2));
        System.out.println("Distance:" + distance);
        if (distance > 8) return "blue";
        else if (distance > 5) return "yellow";
        else return "red";
    }
}

