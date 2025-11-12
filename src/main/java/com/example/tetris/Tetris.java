package com.example.tetris;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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

        ProceduralAudio audio = new ProceduralAudio();
        NextPiecePanel nextPiecePanel = new NextPiecePanel();
        Board board = new Board(statusbar, nextPiecePanel, audio);
        add(board, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel(new BorderLayout());
        JLabel nextLabel = new JLabel("Следующая фигура", SwingConstants.CENTER);
        sidePanel.add(nextLabel, BorderLayout.NORTH);
        sidePanel.add(nextPiecePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        setTitle("Тетрис");
        setSize(320, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board.start();
        board.requestFocusInWindow();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
