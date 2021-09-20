package me.petr.pacman;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;


public class Events {
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Game game;
    private ImageView pacmanEntity;
    private ArrayList<KeyCode> acceptedInput;
    private boolean onDelay = false;

    public Events(Pacman pacman, Stage stage, Game game) {
        this.game = game;
        this.acceptedInput = new ArrayList<>();
        acceptedInput.addAll(Arrays.asList(KeyCode.UP, KeyCode.W, KeyCode.DOWN, KeyCode.S, KeyCode.LEFT, KeyCode.A, KeyCode.RIGHT, KeyCode.D, KeyCode.R, KeyCode.F3, KeyCode.F4));

        this.pacmanEntity = game.getPacmanEntity();

        stage.getScene().setOnKeyPressed(event -> {
            KeyCode input = event.getCode();
            if (acceptedInput.contains(input)) {
                switch (input) {
                    case W:
                    case UP:
                        if (onDelay)
                            break;
                        if (game.gameEnded())
                            break;
                        if (!checkForWall(Direction.UP)) {
                            pacmanEntity.setRotate(270);
                            pacmanEntity.setLayoutY(pacmanEntity.getLayoutY() - Reference.CELL_SIZE);
                        }
                        check();
                        break;
                    case S:
                    case DOWN:
                        if (onDelay)
                            break;
                        if (game.gameEnded())
                            break;
                        if (!checkForWall(Direction.DOWN)) {
                            pacmanEntity.setRotate(90);
                            pacmanEntity.setLayoutY(pacmanEntity.getLayoutY() + Reference.CELL_SIZE);
                        }
                        check();
                        break;
                    case A:
                    case LEFT:
                        if (onDelay)
                            break;
                        if (game.gameEnded())
                            break;
                        if (!checkForWall(Direction.LEFT)) {
                            pacmanEntity.setRotate(180);
                            pacmanEntity.setLayoutX(pacmanEntity.getLayoutX() - Reference.CELL_SIZE);
                        }
                        check();
                        break;
                    case D:
                    case RIGHT:
                        if (onDelay)
                            break;
                        if (game.gameEnded())
                            break;
                        if (!checkForWall(Direction.RIGHT)) {
                            pacmanEntity.setRotate(0);
                            pacmanEntity.setLayoutX(pacmanEntity.getLayoutX() + Reference.CELL_SIZE);
                        }
                        check();
                        break;
                    case R:
                        pacman.restartGame();
                        break;
                    case F3:
                        game.endGame(Game.Outcome.VICTORY);
                        break;
                    case F4:
                        game.endGame(Game.Outcome.GAME_OVER);
                        break;
                }
            }
        });
    }

    private void putOnDelay() {
        onDelay = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onDelay = false;
            }
        }, Reference.MOVEMENT_DELAY);
    }

    private void check() {
        checkForGhost();
        checkForPoint();
        checkForPortal();
        printOutCords();
        putOnDelay();

    }

    private void printOutCords() {
        System.out.println(pacmanEntity.getLayoutX() + ", " + pacmanEntity.getLayoutY());
    }

    private void checkForGhost() {
        game.getGhosts().forEach(ghost -> {
            if (pacmanEntity.getLayoutX() == ghost.getGhost().getLayoutX() && pacmanEntity.getLayoutY() == ghost.getGhost().getLayoutY()) {
                game.die();
            }
        });
    }

    private void checkForPortal() {
        if (pacmanEntity.getLayoutX() < 0) {
            pacmanEntity.setLayoutX(pacmanEntity.getScene().getWidth() - Reference.CELL_SIZE);
        } else if (pacmanEntity.getLayoutX() >= pacmanEntity.getScene().getWidth()) {
            pacmanEntity.setLayoutX(0);
        }
    }

    private boolean checkForWall(Direction direction) {
        for (Rectangle wall : game.getWalls()) {
            if (direction == Direction.UP) {
                if (pacmanEntity.getLayoutX() == wall.getLayoutX() && pacmanEntity.getLayoutY() - Reference.CELL_SIZE == wall.getLayoutY())
                    return true;
            } else if (direction == Direction.DOWN) {
                if (pacmanEntity.getLayoutX() == wall.getLayoutX() && pacmanEntity.getLayoutY() + Reference.CELL_SIZE == wall.getLayoutY())
                    return true;
            } else if (direction == Direction.LEFT) {
                if (pacmanEntity.getLayoutX() - Reference.CELL_SIZE == wall.getLayoutX() && pacmanEntity.getLayoutY() == wall.getLayoutY())
                    return true;
            } else {
                if (pacmanEntity.getLayoutX() + Reference.CELL_SIZE == wall.getLayoutX() && pacmanEntity.getLayoutY() == wall.getLayoutY())
                    return true;
            }
        }
        return false;
    }

    private void checkForPoint() {
        ListIterator points = game.getPoints().listIterator();
        while (points.hasNext()) {
            ImageView point = (ImageView) points.next();
            if (pacmanEntity.getLayoutX() == point.getLayoutX() && pacmanEntity.getLayoutY() == point.getLayoutY()) {
                game.getGameContainer().getChildren().remove(point);
                points.remove();
                game.addPoint();
                if (game.getPoints().isEmpty()) {
                    game.endGame(Game.Outcome.VICTORY);
                }
            }
        }
    }
}
