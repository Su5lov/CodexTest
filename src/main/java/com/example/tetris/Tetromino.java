package com.example.tetris;

/**
 * Enumeration of all tetromino shapes used in the game.
 */
public enum Tetromino {
    NO_SHAPE(new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}}),
    Z_SHAPE(new int[][]{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}),
    S_SHAPE(new int[][]{{0, -1}, {0, 0}, {1, 0}, {1, 1}}),
    LINE_SHAPE(new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, 2}}),
    T_SHAPE(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, 1}}),
    SQUARE_SHAPE(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}),
    L_SHAPE(new int[][]{{-1, -1}, {0, -1}, {0, 0}, {0, 1}}),
    MIRRORED_L_SHAPE(new int[][]{{1, -1}, {0, -1}, {0, 0}, {0, 1}});

    private final int[][] coords;

    Tetromino(int[][] coords) {
        this.coords = coords;
    }

    public int[][] getCoords() {
        int[][] copy = new int[coords.length][coords[0].length];
        for (int i = 0; i < coords.length; i++) {
            System.arraycopy(coords[i], 0, copy[i], 0, coords[i].length);
        }
        return copy;
    }
}
