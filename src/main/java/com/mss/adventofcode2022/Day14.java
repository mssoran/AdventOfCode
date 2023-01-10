package com.mss.adventofcode2022;

public class Day14 extends DayBase {
    public Day14() {
        super(14);
    }

    class Position {
        public int x;
        public int y;
        public Position(int aX, int aY) {
            x = aX;
            y = aY;
        }

        public Position(String str) {
            String[] xandystr = str.trim().split(",");
            x = Integer.valueOf(xandystr[0]);
            y = Integer.valueOf(xandystr[1]);
        }

        public void fillWallTo(Position to) {
            int xStart = x;
            int xEnd = to.x;
            int yStart = y;
            int yEnd = to.y;
            if (x == to.x) {
                if (y > to.y) {
                    yStart = to.y;
                    yEnd = y;
                }
            } else if (y == to.y) {
                if( x > to.x) {
                    xStart = to.x;
                    xEnd = x;
                }

            } else {
                throw new RuntimeException("unexpected, not horizontal or vertical!");
            }
            for(int i = xStart; i <= xEnd; i++)
                for(int j = yStart; j <= yEnd; j++) {
                    grid[i][j] = 'w';
                    if(j > maxyList[i]) maxyList[i] = j;
                }
        }
    }
    char[][] grid = new char[1000][500];
    int[] maxyList = new int[1000];
    int minx = 1000;
    int maxx = 0;
    int miny = 1000;
    int maxy = 0;

    private void updateMinMax(Position pos) {
        if(pos.x < minx) minx = pos.x;
        if(pos.x > maxx) maxx = pos.x;
        if(pos.y < miny) miny = pos.y;
        if(pos.y > maxy) maxy = pos.y;
    }

    public void printGrid() {
        System.out.println("------------" + miny +"      (" + minx + "," + maxx +")");
        for (int y = miny - 1; y <= maxy + 1; y++) {
            for(int x = minx - 1; x <= maxx + 1; x++ ) {
                if (grid[x][y] == 0) System.out.print(".");
                else System.out.print(grid[x][y]);
            }
            System.out.println("");
        }
    }
    @Override
    protected long processLinePart1(String line) {
        String[] positions = line.split("->");
        Position current = new Position(positions[0]);
        updateMinMax(current);
        for (int i = 1; i < positions.length; i++) {
            Position nextPos = new Position(positions[i]);
            updateMinMax(nextPos);
            current.fillWallTo(nextPos);
            current = nextPos;
        }
        return 0;
    }

    public boolean goIfYouCan(Position sand, Position relDir) {
        if(grid[sand.x + relDir.x][sand.y + relDir.y] == 0) {
            sand.x = sand.x + relDir.x;
            sand.y = sand.y + relDir.y;
            return true;
        }
        return false;
    }

    Position relDown = new Position(0, 1);
    Position relLeftDown = new Position(-1, 1);
    Position relRightDown = new Position(1, 1);
    public boolean drop(Position sand) {
        int initialX = sand.x;
        int initialY = sand.y;
        while (true) {
            if(maxyList[sand.x] < sand.y) return false;
            if(!goIfYouCan(sand, relDown)) if(!goIfYouCan(sand, relLeftDown)) if (!goIfYouCan(sand, relRightDown)) {
                // this is for second part
                if(sand.x == initialX && sand.y == initialY) return false;
                // so I cannot move, fix the position
                grid[sand.x][sand.y] = 'o';
                return true;
            }
        }
    }

    @Override
    protected String endPart1() {
        printGrid();
        int count = 0;
        Position sand = new Position(500, 0);
        while(drop(sand)) {
            count++;
            sand = new Position(500, 0);
        }
        printGrid();
        return count + " sand stays!";
    }

    @Override
    protected String endPart2() {
        // add floor
        Position pos = new Position(0, maxy + 2);
        Position endPos = new Position(999, maxy + 2);
        pos.fillWallTo(endPos);
        return endPart1() + " and add 1 for second part...";
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
