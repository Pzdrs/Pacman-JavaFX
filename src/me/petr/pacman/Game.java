package me.petr.pacman;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Game {
    private enum GhostAIDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private Pacman pacman;
    private Cell[][] level;
    private VBox container, gameEndContainer;
    private AnchorPane gameContainer;
    private HBox statsContainer;
    private Label livesLabel, pointsLabel, timeLabel;
    private ImageView pacmanEntity;
    private ArrayList<Rectangle> walls;
    private ArrayList<ImageView> points;
    private boolean gameEnded = false;
    private Timer timer;
    private List<Ghost> ghosts;
    private GhostAIDirectionEnum currentGhostAIDirection;

    public Game(Pacman pacman) {
        this.pacman = pacman;
        this.level = new Cell[21][19];
        // nahraje mapu
        loadLevel();
        this.container = (VBox) pacman.getRoot();

        this.statsContainer = new HBox();
        statsContainer.setAlignment(Pos.CENTER);
        this.pointsLabel = new Label(Reference.POINTS_LABEL.replace("$points", String.valueOf(pacman.points)));
        this.livesLabel = new Label(Reference.LIVES_LABEL.replace("$lives", String.valueOf(pacman.lives)));
        this.timeLabel = new Label(Reference.TIME_DESCRIPTION.replace("$time", String.valueOf(pacman.time)));
        statsContainer.getChildren().addAll(
                new Label(Reference.CONTROLS_DESCRIPTION),
                new Label("     "),
                timeLabel,
                new Label("     "),
                pointsLabel,
                new Label("     "),
                livesLabel,
                new Label("     "),
                new Label("Level: " + pacman.currentLevel));

        this.gameContainer = new AnchorPane();
        container.getChildren().addAll(gameContainer, statsContainer);
        this.walls = new ArrayList<>();
        this.points = new ArrayList<>();
        this.ghosts = new ArrayList<>();
        this.currentGhostAIDirection = GhostAIDirectionEnum.UP;

        // vykreslí mapu
        drawLevel();
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pacman.time++;
                Platform.runLater(() -> {
                    timeLabel.setText(Reference.TIME_DESCRIPTION.replace("$time", String.valueOf(pacman.time)));
                });
            }
        }, 0, 1000);
        //init ghost AI
        ghosts.forEach(ghost -> {
            ghost.start();
        });
    }

    private boolean checkForIntersection() {
        int emptySpaces = 0;
        for (Rectangle wall : getWalls()) {
            if (pacmanEntity.getLayoutX() == wall.getLayoutX() && pacmanEntity.getLayoutY() - Reference.CELL_SIZE == wall.getLayoutY())
                emptySpaces++;
            if (pacmanEntity.getLayoutX() == wall.getLayoutX() && pacmanEntity.getLayoutY() + Reference.CELL_SIZE == wall.getLayoutY())
                emptySpaces++;
            if (pacmanEntity.getLayoutX() - Reference.CELL_SIZE == wall.getLayoutX() && pacmanEntity.getLayoutY() == wall.getLayoutY())
                emptySpaces++;
            if (pacmanEntity.getLayoutX() + Reference.CELL_SIZE == wall.getLayoutX() && pacmanEntity.getLayoutY() == wall.getLayoutY())
                emptySpaces++;
        }
        if (emptySpaces >= 3)
            return true;
        return false;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean gameEnded() {
        return gameEnded;
    }

    public ArrayList<Rectangle> getWalls() {
        return walls;
    }

    /**
     * Vykreslí mapu podle zadání v level1.txt
     */
    public void drawLevel() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length; j++) {
                if (level[i][j].getCellType().equals(CellType.WALL)) {
                    Rectangle rectangle = new Rectangle(25, 25);
                    rectangle.setLayoutX(j * 25);
                    rectangle.setLayoutY(i * 25);
                    gameContainer.getChildren().add(rectangle);
                    walls.add(rectangle);
                }
                if (level[i][j].getCellType().equals(CellType.SMALL_DOT)) {
                    ImageView point = new ImageView(new Image(String.valueOf(getClass().getResource("assets/point.png"))));
                    point.setFitWidth(25);
                    point.setFitHeight(25);
                    point.setLayoutX(j * 25);
                    point.setLayoutY(i * 25);

                    points.add(point);
                    gameContainer.getChildren().add(point);
                }
            }
        }

        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length; j++) {
                if (level[i][j].getCellType().equals(CellType.PACMAN)) {
                    ImageView pacman = new ImageView(new Image(String.valueOf(getClass().getResource("assets/pacman.png"))));
                    pacman.setFitWidth(25);
                    pacman.setFitHeight(25);
                    pacman.setLayoutX(j * 25);
                    pacman.setLayoutY(i * 25);

                    gameContainer.getChildren().add(pacman);

                    this.pacmanEntity = pacman;
                }
                if (level[i][j].getCellType().equals(CellType.GHOST)) {
                    ImageView ghost = new ImageView(new Image(String.valueOf(getClass().getResource("assets/ghost.png"))));
                    ghost.setFitWidth(25);
                    ghost.setFitHeight(25);
                    ghost.setLayoutX(j * 25);
                    ghost.setLayoutY(i * 25);

                    gameContainer.getChildren().add(ghost);
                    ghosts.add(new Ghost(this, ghost));
                }
            }
        }
    }

    public void loadLevel() {
        try {
            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "/src/me/petr/pacman/levels/level" + pacman.currentLevel + ".txt"));
            while (scanner.hasNext()) {
                for (int i = 0; i < level.length; i++) {
                    for (int j = 0; j < level[0].length; j++) {
                        if (level[i][j] == null) {
                            switch (scanner.next()) {
                                case "W":
                                    level[i][j] = new Cell(CellType.WALL);
                                    break;
                                case "P":
                                    level[i][j] = new Cell(CellType.PACMAN);
                                    break;
                                case "S":
                                    level[i][j] = new Cell(CellType.SMALL_DOT);
                                    break;
                                case "G":
                                    level[i][j] = new Cell(CellType.GHOST);
                                    break;
                                default:
                                    level[i][j] = new Cell(CellType.EMPTY);
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImageView getPacmanEntity() {
        return pacmanEntity;
    }

    public ArrayList<ImageView> getPoints() {
        return points;
    }

    public AnchorPane getGameContainer() {
        return gameContainer;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void addPoint() {
        pacman.points++;
        pointsLabel.setText(Reference.POINTS_LABEL.replace("$points", String.valueOf(pacman.points)));
    }

    public void die() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length; j++) {
                if (level[i][j].getCellType().equals(CellType.PACMAN)) {
                    pacmanEntity.setLayoutX(j * 25);
                    pacmanEntity.setLayoutY(i * 25);
                }
            }
        }
        pacmanEntity.setRotate(0);
        pacman.lives--;
        livesLabel.setText(Reference.LIVES_LABEL.replace("$lives", String.valueOf(pacman.lives)));
        if (pacman.lives == 0)
            endGame(Outcome.GAME_OVER);
    }

    public enum Outcome {
        VICTORY, GAME_OVER
    }

    public void endGame(Outcome outcome) {
        if (gameEnded)
            return;
        gameEnded = true;
        timer.cancel();
        ghosts.forEach(ghost -> ghost.stop());
        this.gameEndContainer = new VBox();
        Label title = new Label(), subtitle = new Label();
        subtitle.setTextFill(Color.RED);

        if (outcome == Outcome.VICTORY) {
            pacman.currentLevel++;
            if (pacman.currentLevel <= 3) {
                pacman.restartGame();
            } else {
                title.setText(Reference.VICTORY_TITLE);
                title.setTextFill(Color.ORANGE);
                title.setFont(new Font(100));
                title.setPadding(new Insets(100, 0, 0, 25));

                subtitle.setText(Reference.VICTORY_SUBTITLE
                        .replace("$points", String.valueOf(pacman.points))
                        .replace("$time", String.valueOf(pacman.time))
                        .replace("$lives", String.valueOf(pacman.lives)));
                subtitle.setFont(new Font(25));
                subtitle.setPadding(new Insets(0, 0, 0, 75));
                pacman.currentLevel = 1;
            }
        } else {
            title.setText(Reference.GAME_OVER_TITLE);
            title.setTextFill(Color.RED);
            title.setFont(new Font(75));
            title.setPadding(new Insets(100, 0, 0, 25));

            subtitle.setText(Reference.GAME_OVER_SUBTITLE);
            subtitle.setFont(new Font(25));
            subtitle.setPadding(new Insets(0, 0, 0, 75));
        }
        gameEndContainer.getChildren().addAll(title, subtitle);
        gameContainer.getChildren().add(gameEndContainer);
    }
}
