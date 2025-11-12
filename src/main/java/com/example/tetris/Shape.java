package com.example.tetris;

import java.util.Random;

/**
 * Represents a tetromino piece that can be rotated and placed on the board.
 */
public final class Shape {
    private Tetromino pieceShape;
    private final int[][] coords;
    private static final Random RANDOM = new Random();

    public Shape() {
        coords = new int[4][2];
        setShape(Tetromino.NO_SHAPE);
    }

    public void setShape(Tetromino shape) {
        int[][] shapeCoords = shape.getCoords();
        for (int i = 0; i < 4; ++i) {
            System.arraycopy(shapeCoords[i], 0, coords[i], 0, 2);
        }
        pieceShape = shape;
    }

    public void setRandomShape() {
        Tetromino[] shapes = Tetromino.values();
        setShape(shapes[RANDOM.nextInt(shapes.length - 1) + 1]);
    }

    public Tetromino getShape() {
        return pieceShape;
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public Shape rotateLeft() {
        if (pieceShape == Tetromino.SQUARE_SHAPE) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.coords[i][0] = y(i);
            result.coords[i][1] = -x(i);
        }
        return result;
    }

    public Shape rotateRight() {
        if (pieceShape == Tetromino.SQUARE_SHAPE) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.coords[i][0] = -y(i);
            result.coords[i][1] = x(i);
        }
        return result;
    }

    public int minX() {
        int min = coords[0][0];
        for (int i = 1; i < 4; i++) {
            min = Math.min(min, coords[i][0]);
        }
        return min;
    }

    public int minY() {
        int min = coords[0][1];
        for (int i = 1; i < 4; i++) {
            min = Math.min(min, coords[i][1]);
        }
        return min;
    }
}
