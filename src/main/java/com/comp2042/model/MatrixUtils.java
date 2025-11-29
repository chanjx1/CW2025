package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for performing 2D matrix operations.
 * <p>
 * This class handles low-level grid logic such as collision detection (intersection),
 * merging shapes into the board, and detecting/clearing full rows.
 * </p>
 */
public class MatrixUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixUtils(){
        // Utility class
    }

    /**
     * Checks if the brick at a given position collides with the board boundaries or existing blocks.
     * <p>
     * Fixed logic: Loops now correctly map row to Y and col to X to prevent coordinate transposition errors.
     * </p>
     *
     * @param matrix The current state of the board grid.
     * @param brick  The shape matrix of the brick being checked.
     * @param x      The target column index (x-coordinate).
     * @param y      The target row index (y-coordinate).
     * @return true if a collision is detected or if the brick is out of bounds, false otherwise.
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

    /**
     * Helper method to check if a specific coordinate is outside the board grid.
     *
     * @param matrix   The board grid.
     * @param targetX  The column index to check.
     * @param targetY  The row index to check.
     * @return true if the coordinate is outside the array bounds.
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        // targetY (row) must be within [0, height)
        // targetX (col) must be within [0, width)
        return targetY < 0 || targetY >= matrix.length || targetX < 0 || targetX >= matrix[targetY].length;
    }

    /**
     * Creates a deep copy of a 2D integer array.
     *
     * @param original The array to copy.
     * @return A new, independent 2D array containing the same values.
     */
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
     * Merges a brick into the board matrix at the specified position.
     * <p>
     * Returns a new matrix representing the board state after the brick is locked in place.
     * </p>
     *
     * @param filledFields The current board matrix.
     * @param brick        The shape matrix of the brick to merge.
     * @param x            The column index where the brick landed.
     * @param y            The row index where the brick landed.
     * @return A new 2D array with the brick's blocks added to the grid.
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

    /**
     * Checks the board for complete rows, removes them, and shifts blocks down.
     *
     * @param matrix The current board matrix.
     * @return A {@link ClearRow} object containing the number of cleared lines and the updated matrix.
     */
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

    /**
     * Creates a deep copy of a list of 2D arrays.
     * <p>
     * Used mainly for cloning brick rotation states.
     * </p>
     *
     * @param list The list of matrices to copy.
     * @return A new list containing deep copies of the matrices.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixUtils::copy).collect(Collectors.toList());
    }
}