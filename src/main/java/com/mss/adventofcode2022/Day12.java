package com.mss.adventofcode2022;

import java.util.LinkedList;

public class Day12 extends DayBase {
    public Day12() {
        super(12);
    }

    class Location {
        public int row;
        public int column;

        public Location(int aRow, int aColumn) {
            row = aRow;
            column = aColumn;
        }
    }

    char[][] area = new char[50][200];
    int[][] pathLen = new int[50][200];
    int rowCount = 0;
    int columnCount = 0;
    Location startLoc;
    Location endLoc;

    private void readArea(String line) {
        columnCount = line.length();
        for (int i = 0; i < columnCount; i++) {
            char ch = line.charAt(i);
            switch (ch) {
                case 'S':
                    area[rowCount][i] = 'a';
                    startLoc = new Location(rowCount, i);
                    break;
                case 'E':
                    area[rowCount][i] = 'z';
                    endLoc = new Location(rowCount, i);
                    break;
                default:
                    area[rowCount][i] = ch;
            }
        }
        rowCount++;
    }

    @Override
    protected long processLinePart1(String line) {
        readArea(line);
        return 0;
    }

    private void addValidNeighbor(Location loc, Location relativeDir, LinkedList<Location> queue) {
        int newRow = loc.row + relativeDir.row;
        int newColumn = loc.column + relativeDir.column;
        if (newRow >= 0 && newRow < rowCount &&
                newColumn >= 0 && newColumn < columnCount &&
                pathLen[newRow][newColumn] == 0 &&
                area[loc.row][loc.column] - area[newRow][newColumn] <= 1
        ) {
            pathLen[newRow][newColumn] = pathLen[loc.row][loc.column] + 1;
            queue.addLast(new Location(newRow, newColumn));
        }
    }

    private final Location[] relativeDirs = new Location[]{
            new Location(1, 0),
            new Location(-1, 0),
            new Location(0, 1),
            new Location(0, -1),
    };

    private void addValidNeighbors(Location loc, LinkedList<Location> queue) {
        for (Location relativeDir : relativeDirs) {
            addValidNeighbor(loc, relativeDir, queue);
        }
    }


    private int findPath(boolean findStart) {
        LinkedList<Location> queue = new LinkedList<>();
        queue.addLast(endLoc);
        pathLen[endLoc.row][endLoc.column] = 1;
        while (!queue.isEmpty()) {
            Location loc = queue.removeFirst();
            if(findStart && loc.row == startLoc.row && loc.column == startLoc.column) {
                return pathLen[loc.row][loc.column] - 1;
            }
            if(!findStart && area[loc.row][loc.column] == 'a') {
                return pathLen[loc.row][loc.column] - 1;
            }
            addValidNeighbors(loc, queue);
        }
        throw new RuntimeException("Cannot reach to the end");
    }

    @Override
    protected String endPart1() {
        return "Shortest path: " + findPath(true);
    }

    @Override
    protected String endPart2() {
        return "Shortest path: " + findPath(false);
    }

    @Override
    protected long processLinePart2(String line) {
        readArea(line);
        return 0;
    }
}
