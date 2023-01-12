package com.mss.adventofcode2022;

import java.util.HashMap;

public class Day17 extends DayBase {
    public Day17() {
        super(17);
    }

    class Rock {
        public int[] locations;
        public int height;
        public int width;

        public Rock(int[] aLocs) {
            locations = aLocs;
            height = 0;
            // assuming that the rock is continues (no all 0 row)
            while(height < locations.length && aLocs[height]!=0) height++;
            for(int i = 0; i < locations.length; i++) {
                int temp = 1;
                int count = 0;
                while(temp <= locations[i]) {
                    count ++;
                    temp <<= 1;
                }
                if (count - 1 > width) width = count - 1;
            }
        }
    }

    private final Rock rockHorizontal = new Rock(new int[] {0b1111});
    private final Rock rockPlus = new Rock(new int[] {0b10, 0b111, 0b10});
    private final Rock rockCorner = new Rock(new int[] {0b111, 0b100, 0b100});
    private final Rock rockVertical = new Rock(new int[] {0b1, 0b1, 0b1, 0b1});
    private final Rock rockSquare = new Rock(new int[] {0b11, 0b11});

    class Chamber {
        Rock rock;
        int rockRow;
        int rockCol;
        int topRow = -1;
        int width = 7;


        private int[] chamber = new int[100000];

        public void putRock(Rock rock) {
            rockCol = 2;
            rockRow = topRow + 4;
            this.rock = rock;
        }

        public boolean rockCanBeAt(int row, int col) {
            if (row < 0) {
//                System.out.println("row:"+row+" col:"+col+" cannot be here!");
                return false;
            }
            if (col < 0 || col + rock.width >= this.width) {
//                System.out.println("row:"+row+" col:"+col+" cannot be here! for col rock.width"+rock.width+"this.width:"+this.width);
                return false;
            }
            for (int i = 0; i < rock.height; i++) {
                if (((rock.locations[i] << col) & chamber[row+i]) != 0) return false;
            }
            return true;
        }

        public void pushLeft() {
            if (rockCanBeAt(rockRow, rockCol - 1)) {
                rockCol = rockCol - 1;
            }
        }

        public void pushRight() {
            if (rockCanBeAt(rockRow, rockCol + 1)) {
                rockCol = rockCol + 1;
            }
        }

        public void push(char ch) {
            switch (ch) {
                case '<': pushLeft();
                break;
                case '>': pushRight();
                break;
                default: throw new RuntimeException("unexpected push char '"+ch+"'");
            }
        }

        public void fixRock(){
            for (int i = 0; i < rock.height; i++) {
                chamber[rockRow + i] = (rock.locations[i] << rockCol) | chamber[rockRow + i];
            }
            if(rockRow + rock.height - 1> topRow) topRow = rockRow + rock.height - 1;
        }

        public boolean dropOne() {
            if (rockCanBeAt(rockRow -1, rockCol)) {
                rockRow = rockRow - 1;
                return true;
            }
            fixRock();
            return false;
        }

        public String lastLines(int lineCount) {
            if (topRow - lineCount <= 0) return null;
            StringBuilder sb = new StringBuilder();
            for (int i = topRow; i > topRow - lineCount; i--) {
                sb.append(chamber[i]);
                sb.append('-');
            }
            return sb.toString();
        }

        public void print() {
            int printOnlyThisManyLines = 2000;
            int h = topRow;
            if (rock != null && rock.height + rockRow > h) h = rock.height + rockRow;
            for (int i = h; i>= 0 && i > h - printOnlyThisManyLines; i--) {
                char[] line = {'|', '.', '.', '.', '.', '.', '.', '.', '|'};
                int pos;
                if (i >= rockRow && i < (rockRow+rock.height)) {
                    pos = 1;
                    for (int j = 1; j <8; j++){
                        if ((pos & (rock.locations[i - rockRow] << rockCol)) == pos) line[j] = '@';
                        pos <<= 1;
                    }
                }
                pos = 1;
                for (int j = 1; j <8; j++){
                    if ((pos & chamber[i]) == pos) line[j] = '#';
                    pos <<= 1;
                }
                System.out.println(line);
            }
            System.out.println("+-------+");
            System.out.println("rockRow:"+rockRow+" rockCol:"+rockCol+" rock.height:"+rock.height);
        }

    }

    char[] flows;
    @Override
    protected long processLinePart1(String line) {
        flows = line.toCharArray();
        return 0;
    }

    @Override
    protected String endPart1() {
        Chamber chamber = new Chamber();
        Rock[] rocks = {rockHorizontal, rockPlus, rockCorner, rockVertical, rockSquare};
        long rockId = 0;
        long flowId = 0;
        for (long i = 0; i < 2022; i++) {
            chamber.putRock(rocks[(int)(rockId++ % rocks.length)]);
            chamber.push(flows[(int)(flowId++ % flows.length)]);
            while(chamber.dropOne()) {
                chamber.push(flows[(int)(flowId++ % flows.length)]);
            }
        }
        return "total number of rows is " + (chamber.topRow + 1);
    }

    class LastLineData {
        public long topRow;
        public long rockId;
        public LastLineData(long aTopRow, long aRockId) {
            topRow = aTopRow;
            rockId = aRockId;
        }
    }
    @Override
    protected String endPart2() {
        Chamber chamber = new Chamber();
        Rock[] rocks = {rockHorizontal, rockPlus, rockCorner, rockVertical, rockSquare};
        long rockId = 0;
        long flowId = 0;
        HashMap<String, LastLineData> oldList = new HashMap<>();
        long i = 0;
        long stepCount = 1000000000000L;
        long jumpedRowCount = -1;
        while (i < stepCount) {
            if (i % 100000000L == 0) System.out.println("------  "+i);
            if(rockId % rocks.length == 0 && jumpedRowCount < 0) {
                // check for cycle
                String lastLines = chamber.lastLines(20) + (flowId % flows.length);
//                System.out.println("Checking last line with :'" + lastLines + "'");
                if (lastLines != null) {
                    if(oldList.containsKey(lastLines)) {
                        LastLineData data = oldList.get(lastLines);
                        System.out.println("Yes, I found a probable cycle! Previous topRow:" + data.topRow + "prev rockId:" + data.rockId + " current topRow:" + chamber.topRow + " current rockId:" + rockId);
                        long topRowDif = chamber.topRow - data.topRow;
                        long rockCountDiff = rockId - data.rockId;
                        System.out.println("topRow diff:" + topRowDif + " rockId diff:" + rockCountDiff);
                        // So, jump close to stepCount as much as possible
                        long jumpCount = (stepCount - i) / rockCountDiff;
                        jumpedRowCount = jumpCount * topRowDif;
                        i += jumpCount * rockCountDiff;
                    } else {
                        oldList.put(lastLines, new LastLineData(chamber.topRow, rockId));
                    }
                }
            }
            chamber.putRock(rocks[(int)(rockId++ % rocks.length)]);
            chamber.push(flows[(int)(flowId++ % flows.length)]);
            while(chamber.dropOne()) {
                chamber.push(flows[(int)(flowId++ % flows.length)]);
            }
            i++;
        }
//        chamber.print();
        return "total number of rows is " + (chamber.topRow + 1) + " and jumped row count is " + jumpedRowCount + " so the total is:" +(chamber.topRow + 1 + jumpedRowCount);
    }

    @Override
    protected long processLinePart2(String line) {
        flows = line.toCharArray();
        return 0;
    }
}
