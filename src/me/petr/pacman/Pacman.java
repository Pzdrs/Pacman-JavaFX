package me.petr.pacman;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Pacman extends Application {
    private static Pacman instance;
    private Parent root;
    private Game game;
    private Stage primaryStage;
    public int currentLevel = 1, lives = 3, time = 0, points = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        startGame();
    }

    public static Pacman getInstance() {
        return instance;
    }

    public void startGame() {
        root = new VBox();
        game = new Game(this);

        primaryStage.setTitle(Reference.NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("assets/pacman.png"))));
        primaryStage.show();

        new Events(this, primaryStage, game);
    }

    public void restartGame() {
        startGame();
    }

    @Override
    public void stop() {
        game.getGhosts().forEach(ghost -> ghost.stop());
        game.getTimer().cancel();
    }

    public Parent getRoot() {
        return root;
    }
}
