package com.example.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Board panel that holds game state and painting logic.
 */
public final class Board extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22;
    private static final int PERIOD_MS = 400;

    private final Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Shape nextPiece;
    private Tetromino[] board;
    private final JLabel statusBar;
    private final NextPiecePanel nextPiecePanel;
    private final ProceduralAudio audio;

    public Board(JLabel statusBar, NextPiecePanel nextPiecePanel, ProceduralAudio audio) {
        this.statusBar = statusBar;
        this.nextPiecePanel = nextPiecePanel;
        this.audio = audio;
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(PERIOD_MS, this);
        timer.start();

        board = new Tetromino[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        addKeyListener(new TAdapter());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private int squareWidth() {
        return getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
        return getHeight() / BOARD_HEIGHT;
    }

    private Tetromino shapeAt(int x, int y) {
        return board[y * BOARD_WIDTH + x];
    }

    public void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        isPaused = false;
        clearBoard();
        curPiece.setShape(Tetromino.NO_SHAPE);

        nextPiece = new Shape();
        nextPiece.setRandomShape();
        updatePreview();

        newPiece();
        timer.start();
        audio.startMusic();
        statusBar.setText(String.format("Счёт: %d", numLinesRemoved));
    }

    private void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusBar.setText("Пауза");
            audio.pauseMusic();
        } else {
            timer.start();
            statusBar.setText(String.format("Счёт: %d", numLinesRemoved));
            audio.resumeMusic();
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; ++i) {
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                Tetromino shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetromino.NO_SHAPE) {
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetromino.NO_SHAPE) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; ++i) {
            board[i] = Tetromino.NO_SHAPE;
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[y * BOARD_WIDTH + x] = curPiece.getShape();
        }

        audio.playLockSound();
        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        if (nextPiece == null) {
            nextPiece = new Shape();
            nextPiece.setRandomShape();
        }

        curPiece = nextPiece;
        nextPiece = new Shape();
        nextPiece.setRandomShape();
        updatePreview();
        curX = BOARD_WIDTH / 2 + curPiece.minX();
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetromino.NO_SHAPE);
            timer.stop();
            isStarted = false;
            statusBar.setText(String.format("Игра окончена. Счёт: %d", numLinesRemoved));
            audio.stopMusic();
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Tetromino.NO_SHAPE) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {
                if (shapeAt(j, i) == Tetromino.NO_SHAPE) {
                    lineFull = false;
                    break;
                }
            }

            if (lineFull) {
                ++numFullLines;

                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {
                        board[k * BOARD_WIDTH + j] = shapeAt(j, k + 1);
                    }
                }

                for (int j = 0; j < BOARD_WIDTH; ++j) {
                    board[(BOARD_HEIGHT - 1) * BOARD_WIDTH + j] = Tetromino.NO_SHAPE;
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusBar.setText(String.format("Счёт: %d", numLinesRemoved));
            audio.playLineClearSound(numFullLines);
            isFallingFinished = true;
            curPiece.setShape(Tetromino.NO_SHAPE);
            repaint();
        }
    }

    private void updatePreview() {
        if (nextPiecePanel != null) {
            nextPiecePanel.setPreview(nextPiece);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetromino shape) {
        Color[] colors = {
            new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102),
            new Color(102, 102, 204),
            new Color(204, 204, 102),
            new Color(204, 102, 204),
            new Color(102, 204, 204),
            new Color(218, 170, 0)
        };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || curPiece.getShape() == Tetromino.NO_SHAPE) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_P) {
                pause();
                return;
            }

            if (isPaused) {
                return;
            }

            switch (keycode) {
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> oneLineDown();
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_A -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_S -> oneLineDown();
                case KeyEvent.VK_R -> start();
                default -> {
                }
            }
        }
    }
}
