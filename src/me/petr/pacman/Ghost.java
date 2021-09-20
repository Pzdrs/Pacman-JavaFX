package me.petr.pacman;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.Timer;
import java.util.TimerTask;

public class Ghost {
    private Game game;
    private ImageView ghost;
    private Timer timer;
    private GhostAIDirectionEnum direction;

    public Ghost(Game game, ImageView ghost) {
        this.game = game;
        this.ghost = ghost;
        this.direction = GhostAIDirectionEnum.getRandom();
    }

    /**
     * Start ghost movement
     */
    public void start() {
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (checkForIntersection()) {
                        direction = GhostAIDirectionEnum.getRandom();
                    }
                    switch (direction) {
                        case UP:
                            if (checkForCollision(GhostAIDirectionEnum.UP)) {
                                direction = GhostAIDirectionEnum.getRandom();
                                break;
                            }
                            ghost.setLayoutY(ghost.getLayoutY() - Reference.CELL_SIZE);
                            break;
                        case DOWN:
                            if (checkForCollision(GhostAIDirectionEnum.DOWN)) {
                                direction = GhostAIDirectionEnum.getRandom();
                                break;
                            }
                            ghost.setLayoutY(ghost.getLayoutY() + Reference.CELL_SIZE);
                            break;
                        case LEFT:
                            if (checkForCollision(GhostAIDirectionEnum.LEFT)) {
                                direction = GhostAIDirectionEnum.getRandom();
                                break;
                            }
                            ghost.setLayoutX(ghost.getLayoutX() - Reference.CELL_SIZE);
                            break;
                        case RIGHT:
                            if (checkForCollision(GhostAIDirectionEnum.RIGHT)) {
                                direction = GhostAIDirectionEnum.getRandom();
                                break;
                            }
                            ghost.setLayoutX(ghost.getLayoutX() + Reference.CELL_SIZE);
                            break;
                    }
                    checkForPacman();
                });
            }
        }, 0, Reference.MOVEMENT_DELAY);
    }

    public void stop() {
        this.timer.cancel();
    }

    private boolean checkForIntersection() {
       /* int waysToGo = 0;
        for (Rectangle wall : game.getWalls()) {
            if (ghost.getLayoutX() != wall.getLayoutX() && ghost.getLayoutY() - Reference.CELL_SIZE != wall.getLayoutY()) {
                waysToGo++;
            }
            if (ghost.getLayoutX() != wall.getLayoutX() && ghost.getLayoutY() + Reference.CELL_SIZE != wall.getLayoutY()) {
                waysToGo++;
            }
            if (ghost.getLayoutX() - Reference.CELL_SIZE != wall.getLayoutX() && ghost.getLayoutY() != wall.getLayoutY()) {
                waysToGo++;
            }
            if (ghost.getLayoutX() + Reference.CELL_SIZE != wall.getLayoutX() && ghost.getLayoutY() != wall.getLayoutY()) {
                waysToGo++;
            }
        }
        if (waysToGo >= 3) {
            return true;
        }*/
       //DOESNT FUCKING WORK IDC
        return false;
    }

    /**
     * Kontroluje jestli duch narazil do zdi
     * @param direction
     * @return
     */
    private boolean checkForCollision(GhostAIDirectionEnum direction) {
        switch (direction) {
            case UP:
                for (Rectangle wall : game.getWalls()) {
                    if (ghost.getLayoutX() == wall.getLayoutX() && ghost.getLayoutY() - Reference.CELL_SIZE == wall.getLayoutY()) {
                        return true;
                    }
                }
                break;
            case DOWN:
                for (Rectangle wall : game.getWalls()) {
                    if (ghost.getLayoutX() == wall.getLayoutX() && ghost.getLayoutY() + Reference.CELL_SIZE == wall.getLayoutY()) {
                        return true;
                    }
                }
                break;
            case LEFT:
                for (Rectangle wall : game.getWalls()) {
                    if (ghost.getLayoutX() - Reference.CELL_SIZE == wall.getLayoutX() && ghost.getLayoutY() == wall.getLayoutY()) {
                        return true;
                    }
                }
                break;
            case RIGHT:
                for (Rectangle wall : game.getWalls()) {
                    if (ghost.getLayoutX() + Reference.CELL_SIZE == wall.getLayoutX() && ghost.getLayoutY() == wall.getLayoutY()) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public void checkForPacman() {
        if (game.getPacmanEntity().getLayoutX() == ghost.getLayoutX() && game.getPacmanEntity().getLayoutY() ==  ghost.getLayoutY()) {
            game.die();
        }
    }

    public ImageView getGhost() {
        return ghost;
    }
}
