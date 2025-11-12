package com.example.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Panel that renders a preview of the upcoming tetromino.
 */
public final class NextPiecePanel extends JPanel {
    private static final int CELL_SIZE = 20;
    private static final Color[] COLORS = {
        new Color(0, 0, 0),
        new Color(204, 102, 102),
        new Color(102, 204, 102),
        new Color(102, 102, 204),
        new Color(204, 204, 102),
        new Color(204, 102, 204),
        new Color(102, 204, 204),
        new Color(218, 170, 0)
    };

    private Shape preview;

    public NextPiecePanel() {
        setPreferredSize(new Dimension(CELL_SIZE * 4 + 16, CELL_SIZE * 4 + 16));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    public void setPreview(Shape shape) {
        if (shape == null) {
            preview = null;
        } else {
            preview = shape.copy();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (preview == null || preview.getShape() == Tetromino.NO_SHAPE) {
            return;
        }

        int minX = preview.x(0);
        int maxX = preview.x(0);
        int minY = preview.y(0);
        int maxY = preview.y(0);

        for (int i = 1; i < 4; ++i) {
            minX = Math.min(minX, preview.x(i));
            maxX = Math.max(maxX, preview.x(i));
            minY = Math.min(minY, preview.y(i));
            maxY = Math.max(maxY, preview.y(i));
        }

        int pieceWidth = (maxX - minX + 1) * CELL_SIZE;
        int pieceHeight = (maxY - minY + 1) * CELL_SIZE;
        int offsetX = (getWidth() - pieceWidth) / 2 - minX * CELL_SIZE;
        int offsetY = (getHeight() - pieceHeight) / 2 - minY * CELL_SIZE;

        Color color = COLORS[preview.getShape().ordinal()];

        for (int i = 0; i < 4; ++i) {
            int x = preview.x(i) * CELL_SIZE + offsetX;
            int y = preview.y(i) * CELL_SIZE + offsetY;
            g.setColor(color);
            g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
            g.setColor(color.brighter());
            g.drawLine(x, y + CELL_SIZE - 1, x, y);
            g.drawLine(x, y, x + CELL_SIZE - 1, y);
            g.setColor(color.darker());
            g.drawLine(x + 1, y + CELL_SIZE - 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1);
            g.drawLine(x + CELL_SIZE - 1, y + CELL_SIZE - 1, x + CELL_SIZE - 1, y + 1);
        }
    }
}
