package com.example.tetris;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Entry point for the Tetris game.
 */
public final class Tetris extends JFrame {
    private Tetris() {
        initUI();
    }

    private void initUI() {
        JLabel statusbar = new JLabel("Нажмите Enter для старта, P - пауза");
        add(statusbar, BorderLayout.SOUTH);

        Board board = new Board(statusbar);
        add(board);

        setTitle("Тетрис");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
