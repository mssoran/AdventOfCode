package com.mss.adventofcode2022;

public class Day8 extends DayBase {
    public Day8() {
        super(8);
    }

    private int gridSize = 0;
    private int currentRow = 0;

    private int[][] grid;
    private boolean[][] visibleGrid;

    private long processLine(String line) {
        if (gridSize == 0) {
            gridSize = line.length();
            grid = new int[gridSize][gridSize];
            visibleGrid = new boolean[gridSize][gridSize];
        }
        for (int i = 0; i < gridSize; i++) {
            grid[currentRow][i] = line.charAt(i) - '0';
        }
        currentRow++;
        return 0;
    }

    private void markVisibleRow(int rowStart, int rowEnd, int columnStart, int columnEnd, int rowStep, int columnStep) {
        int max = grid[rowStart][columnStart];
        visibleGrid[rowStart][columnStart] = true;
        int rowIndex = rowStart + rowStep;
        int columnIndex = columnStart + columnStep;
        while (rowIndex != rowEnd || columnIndex != columnEnd) {
            if (grid[rowIndex][columnIndex] > max) {
                visibleGrid[rowIndex][columnIndex] = true;
                max = grid[rowIndex][columnIndex];
            }
            rowIndex += rowStep;
            columnIndex += columnStep;
        }
    }

    private int countViewingDistance(int row, int column, int rowStep, int columnStep) {
        int count = 0;
        int myHeight = grid[row][column];
        int rowIndex = row + rowStep;
        int columnIndex = column + columnStep;
        while (rowIndex >= 0 && rowIndex < gridSize && columnIndex >= 0 && columnIndex < gridSize) {
            count++;
            if (grid[rowIndex][columnIndex] >= myHeight) return count;
            rowIndex += rowStep;
            columnIndex += columnStep;
        }
        return count;
    }

    @Override
    protected long processLinePart1(String line) {
        return processLine(line);
    }

    @Override
    protected String endPart1() {
        // mark all visible
        for (int i = 0; i < gridSize; i++) {
            markVisibleRow(0, gridSize - 1, i, i, 1, 0);
            markVisibleRow(gridSize - 1, 0, i, i, -1, 0);
            markVisibleRow(i, i, 0, gridSize - 1, 0, 1);
            markVisibleRow(i, i, gridSize - 1, 0, 0, -1);
        }
        // count visible
        int count = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (visibleGrid[i][j]) count++;
            }
        }

        return count + "";
    }

    @Override
    protected String endPart2() {
        int maxScore = 0;
        for (int i = 1; i < gridSize - 1; i++) {
            for (int j = 1; j < gridSize - 1; j++) {
                int score = countViewingDistance(i, j, 0, 1) *
                        countViewingDistance(i, j, 0, -1) *
                        countViewingDistance(i, j, 1, 0) *
                        countViewingDistance(i, j, -1, 0);
                if (score > maxScore) maxScore = score;
            }
        }
        return maxScore + "";
    }

    @Override
    protected long processLinePart2(String line) {
        return processLine(line);
    }
}
