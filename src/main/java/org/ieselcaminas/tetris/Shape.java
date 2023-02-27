/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ieselcaminas.tetris;

/**
 *
 * @author alu10191634
 */
public class Shape {

    private static final int[][][] COORDS_TABLE = new int[][][]{
        {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
        {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
        {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
        {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
    };
    
    private Tetrominoes pieceShape;
    private int coords[][];
    
    public Shape(){
        coords = new int[4][2];
        setRandomShape();
    }
    
    public void setShape(Tetrominoes shapeType) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                coords[i][j] = COORDS_TABLE[shapeType.ordinal()][i][j];
            }
        }
        pieceShape = shapeType;
    }
    
    private void setX(int index, int x){
        coords[index][0] = x; 
    }
    
    private void setY(int index, int y) {
        coords[index][1] = y;
    }
    
    public int getX(int index){
        return coords[index][0];
    }
    
    public int getY(int index){
        return coords[index][1];
    }
    
    public Tetrominoes getShape(){
        return pieceShape;
    }
    
    public void setRandomShape(){
        int random = (int)(Math.random() * 7) + 1;
        Tetrominoes tetro = Tetrominoes.values()[random];
        setShape(tetro);
    }
    
    public int minX() {
        int candidate = coords[0][0];
        for (int i = 0; i < 4; i++) {
            if (coords[i][0] < candidate) {
                candidate = coords[i][0];
            }
        }
        return candidate;
    }
    
    public int minY() {
        int candidate = coords[0][1];
        for (int i = 0; i < 4; i++) {
            if (coords[i][1] < candidate) {
                candidate = coords[i][1];
            }
        }
        return candidate;
    }
    
    public int maxX() {
        int candidate = coords[0][0];
        for (int i = 0; i < 4; i++) {
            if (coords[i][0] > candidate) {
                candidate = coords[i][0];
            }
        }
        return candidate;
    }
    
    public int maxY() {
        int candidate = coords[0][1];
        for (int i = 0; i < 4; i++) {
            if (coords[i][1] > candidate) {
                candidate = coords[i][1];
            }
        }
        return candidate;
    }

    public Shape rotateLeft() {
        Shape newShape = new Shape();
        newShape.setShape(pieceShape);
        if (pieceShape != Tetrominoes.SquareShape) {
            for (int i = 0; i < 4; i++) {
                newShape.setX(i, getY(i));
                newShape.setY(i, -getX(i));
            }
        }
            return newShape;
        }
    }
