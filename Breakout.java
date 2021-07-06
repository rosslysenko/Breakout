/*This class is used to create Breakout, an arcade game.
It is the most famous representative of the games of the "Arkanoid" genre.
The upper third of the game screen is occupied by a series of bars.
On the screen, bouncing off the top and side edges of the screen, moves the ball.
Hitting a bar, the ball bounces and the bar disappears. You have 3 rounds.
The player loses one life when the ball hits the bottom of the screen; to prevent this,
the player has a movable paddle that can be used to bounce the ball back to the top of the screen.
*/

package com.shpp.p2p.cs.rlysenko.assignment4;

import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 3;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 2;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Breaks counter
     */
    private static final int BRICKS_NUMBER = NBRICKS_PER_ROW * NBRICK_ROWS;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    /**
     * Number of bricks on level left
     */
    private static int counter = BRICKS_NUMBER;

    /**
     * Velocity of the ball
     */
    private static double vx, vy = -5;

    /**
     * Object the paddle
     */
    private GRect paddle;


    public void run() {
        createGameStage();
        for (int turns = 0; turns < NTURNS; turns++) {
            waitForClick();
            setMoveBall(drawBall());
        }
        showText("Game over!", Color.RED, 800);
        exit();
    }


    /*
    The method is used to display text in the center of the screen and takes the output string,
    text color and display delay as parameters.
    */
    private void showText(String s, Color c, int time) {
        GLabel text = new GLabel(s);
        text.setColor(c);
        text.setFont("Calibri-35");
        text.setLocation(getWidth() / 2 - text.getWidth() / 2, getHeight() / 2 - text.getHeight());
        add(text);
        pause(time);
        remove(text);
    }


    /*
    The method is used to create a playing field: draws bricks, racket, ball
    and includes a method for tracking mouse events.
    */
    private void createGameStage() {
        drawBricks();
        addMouseListeners();
        createPaddle();
        drawBall();
    }


    /*
    The method is used to draw bricks, each two row of which has an individual color,
    parameters are set by constants.
    */
    private void drawBricks() {
        Color[] arr = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};

        for (int i = 0, k = 0; i < NBRICK_ROWS; i++, k++) {
            if (k == arr.length * 2) {
                k = 0;
            }
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                drawBrick(BRICK_SEP / 2 + ((BRICK_WIDTH + BRICK_SEP) * j),
                        BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * i), arr[k / 2]);
            }
        }
    }


    //    The method is used to draw brick, parameters are set by constants.
    private void drawBrick(int x, int y, Color c) {
        GRect rectangle = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        rectangle.setFilled(true);
        rectangle.setFillColor(c);
        rectangle.setColor(c);
        add(rectangle);
    }


    /*
     This method is used to control the paddle with the mouse.
     As long as the cursor is within the window, the movement of the paddle (center)
     in the horizontal plane agrees with the movement of the cursor.
     */
    public void mouseMoved(MouseEvent e) {
        if ((e.getX() + PADDLE_WIDTH / 2) < getWidth() && (e.getX() >= PADDLE_WIDTH / 2)) {
            paddle.move((e.getX() - PADDLE_WIDTH / 2) - paddle.getX(), 0);
        }
    }


    //    The method is used to draw paddle, parameters are set by constants.
    private void createPaddle() {
        paddle = new GRect(getWidth() / 2 - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setFillColor(Color.BLACK);
        add(paddle);
    }


    //    The method is used to draw ball, parameters are set by constants.
    private GOval drawBall() {
        GOval ball = new GOval((getWidth() / 2 - BALL_RADIUS), (getHeight() / 2 - BALL_RADIUS),
                BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        ball.setFillColor(Color.BLACK);
        add(ball);
        return ball;
    }


    /*
     This method is used to set the ball animation.
     At the beginning of the round the ball flies down randomly to the left or right.
     When it collides with the top and side walls, the ball reverses its direction.
     When the ball collides with the bottom wall, it continues its downward motion.
     The player loses one attempt (three attempts in total) and receives a message in the center of the screen.
     Also includes a method for handling collisions with game objects.
     */
    private void setMoveBall(GOval ball) {
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        while (ball.getY() < getHeight() - BALL_RADIUS) {
            ball.move(vx, vy);
            handleCollisions(ball);
            if ((ball.getX() <= 0) || ball.getX() >= getWidth() - BALL_RADIUS * 2) {
                vx = -vx;
            }
            if (ball.getY() <= 0) {
                vy = -vy;
            }
            pause(10);
        }
        remove(ball);
        showText("You lose!", Color.BLACK, 400);
    }


    /*
     This method is used to handle interaction with game objects and create game mechanics.
     If a ball collides with a paddle, it reverses the direction of motion.
     If the ball collides with a brick, it changes the direction of motion to the opposite,
     with this brick removed from the playing field and the counter of the number of bricks is decremented
     by the number of removed objects. When the number of bricks on the field becomes zero,
     the player wins and receives a message about it in the center of the screen
    */
    private void handleCollisions(GOval o) {
        GObject collider = getCollidingObject(o);
        if (collider == paddle) {
            vy = -vy;
        } else if (collider != null) {
            remove(collider);
            if (counter == 0) {
                showText("You win!", Color.GREEN, 800);
                exit();
            }
            counter--;

            vy = -vy;
        }
    }


    /*
    This method is used to handle a four-point ball collision.
    If the coordinates of one of the points coincide with the coordinates of some object,
    the method returns that object and if not - it returns null.
    */
    private GObject getCollidingObject(GOval ball) {
        if (getElementAt(ball.getX(), ball.getY()) != null) {
            return (getElementAt(ball.getX(), ball.getY()));
        } else if (getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY()) != null) {
            return (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()));
        } else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
            return (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2));
        } else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
            return (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2));
        } else {
            return null;
        }
    }
}
