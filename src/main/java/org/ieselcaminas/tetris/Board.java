/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package org.ieselcaminas.tetris;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author alu10191634
 */
public class Board extends javax.swing.JPanel {

    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;
    private int deltaTime;
    private Timer timer;
    private Shape currentShape;
    private int currentRow;
    private int currentCol;
    private MyKeyAdapter keyAdapter;
    public Tetrominoes[][] matrix;
    private Incrementer incrementer;
    private GetScorer getScorer;

    public void setIncrementer(Incrementer incrementer) {
        this.incrementer = incrementer;
    }

    private void processGameOver() {
        timer.stop();
        removeKeyListener(keyAdapter);
        JFrame parentJFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ScoreDialog dialog = new ScoreDialog(parentJFrame, true);
        dialog.setGetScorer(getScorer);
        dialog.setVisible(true);
    }

    public void setGetScorer(GetScorer getScorer){
        this.getScorer = getScorer;
    }
    
    class MyKeyAdapter extends KeyAdapter {
        private boolean paused = false;
        public boolean isPaused() {
            return paused;
        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (!paused && canMove(currentRow, currentCol - 1, currentShape)) {
                        currentCol--;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!paused && canMove(currentRow, currentCol + 1, currentShape)) {
                        currentCol++;
                    }
                    break;
                case KeyEvent.VK_UP:
                    Shape rotated = currentShape.rotateLeft();
                    if (!paused && canMove(currentRow, currentCol, rotated)) {
                        currentShape = rotated;
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (!paused && canMove(currentRow + 1, currentCol, currentShape)) {
                        currentRow++;
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    paused = !paused;
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    /**
     * Creates new form Board
     */
    public Board() {
        initComponents();
        myInit();
    }

    public void initGame() {
        addKeyListener(keyAdapter);
        resetMatrix();
        resetPosition();
        currentShape = new Shape();
        setDeltaTime();
        incrementer.resetScore();
        timer.start();
        repaint();
    }

    public void setDeltaTime() {
        switch (ConfigData.instance.getlevel()) {
            case 0:
                deltaTime = 500;
                break;
            case 1:
                deltaTime = 300;
                break;
            case 2:
                deltaTime = 150;
                break;
            default:
                throw new AssertionError();
        }
        timer.setDelay(deltaTime);
    }

    public void myInit() {
        resetMatrix();
        //deltaTime = 500;
        setFocusable(true);
        keyAdapter = new MyKeyAdapter();
        
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!keyAdapter.isPaused()) {
                    tick();
                }
            }
        });
        repaint();
    }

    public void resetMatrix() {
        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = Tetrominoes.NoShape;
            }
        }
    }

    public void resetPosition() {
        currentRow = 0;
        currentCol = NUM_COLS / 2;
    }

    private void tick() {
        if (canMove(currentRow + 1, currentCol, currentShape)) {
            currentRow++;
        } else {
            if (currentRow == 0) {
                processGameOver();
            } else {
                movePieceToMatrix();
                checkCompletedRows();
                resetPosition();
                currentShape.setRandomShape();
            }
        }
        repaint();
    }

    private void checkCompletedRows() {
        int row = NUM_ROWS - 1;
        while (row >= 0) {
            if (completedRow(row)) {
                removeRow(row);
                incrementer.incrementScore(10);
            } else {
                row--;
            }
        }

    }

    public boolean completedRow(int row) {
        for (int col = 0; col < NUM_COLS; col++) {
            if (matrix[row][col] == Tetrominoes.NoShape) {
                return false;
            }
        }
        return true;
    }

    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[i][col] = matrix[i - 1][col];

            }
        }
        addZeroRow();
    }

    public void addZeroRow() {
        for (int col = 0; col < NUM_COLS; col++) {
            matrix[0][col] = Tetrominoes.NoShape;
        }
    }

    private boolean canMove(int row, int col, Shape piece) {
        if (row + piece.maxY() >= NUM_ROWS) {
            return false;
        }
        if (col + piece.maxX() >= NUM_COLS || col + piece.minX() < 0) {
            return false;
        }
        if (colidesWithMatrix(row, col, piece) == true) {
            return false;
        }
        return true;
    }

    private boolean colidesWithMatrix(int nextRow, int nextCol, Shape shape) {
        for (int i = 0; i < 4; i++) {
            int row = nextRow + shape.getY(i);
            int col = nextCol + shape.getX(i);
            if (row < 0) {
                continue;
            }
            if (matrix[row][col] != Tetrominoes.NoShape) {
                return true;
            }
        }
        return false;
    }

    private void movePieceToMatrix() {
        for (int i = 0; i < 4; i++) {
            int row = currentRow + currentShape.getY(i);
            int col = currentCol + currentShape.getX(i);
            matrix[row][col] = currentShape.getShape();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintFrame(g);
        paintMatrix(g);
        if (currentShape != null) {
            paintShape(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void paintMatrix(Graphics g) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                drawSquare(g, row, col, matrix[row][col]);
            }
        }
    }

    private void paintShape(Graphics g) {
        for (int i = 0; i < 4; i++) {
            int row = currentRow + currentShape.getY(i);
            int col = currentCol + currentShape.getX(i);
            drawSquare(g, row, col, currentShape.getShape());
        }
    }

    private void paintFrame(Graphics g) {
        g.setColor(Color.black);
        int width = squareWidth() * NUM_COLS;
        int height = squareHeight() * NUM_ROWS;
        g.drawRect(0, 0, width, height);
    }

    public int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    public int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    private void drawSquare(Graphics g, int row, int col,
            Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(102, 255, 102));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
