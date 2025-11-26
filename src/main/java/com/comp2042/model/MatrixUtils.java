package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixUtils {

    private MatrixUtils(){
        // Utility class
    }

    /**
     * Checks if the brick at position (x, y) collides with the board boundaries or existing blocks.
     * Fixed: Loops now correctly map row->y and col->x.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                // If the brick cell is not empty
                if (brick[row][col] != 0) {
                    int targetX = x + col;
                    int targetY = y + row;

                    // Check bounds or collision with board content
                    if (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        // targetY (row) must be within [0, height)
        // targetX (col) must be within [0, width)
        return targetY < 0 || targetY >= matrix.length || targetX < 0 || targetX >= matrix[targetY].length;
    }

    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges the brick into the board matrix.
     * Fixed: Loops now correctly map row->y and col->x.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                if (brick[row][col] != 0) {
                    int targetX = x + col;
                    int targetY = y + row;
                    // Safety check to prevent crashing if a glitched piece is partly out of bounds
                    if (targetY >= 0 && targetY < copy.length && targetX >= 0 && targetX < copy[0].length) {
                        copy[targetY][targetX] = brick[row][col];
                    }
                }
            }
        }
        return copy;
    }

    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }

        return new ClearRow(clearedRows.size(), tmp);
    }

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixUtils::copy).collect(Collectors.toList());
    }
}