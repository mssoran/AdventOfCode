package com.mss.adventofcode2022;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;

public class Day24 extends DayBase {
    public Day24() {
        super(24);
    }

    class Cell {
        char initialChar;
        Cell (char aInitialChar) {
            initialChar = aInitialChar;
            horizontalSize = 1;
            verticalSize = 1;
            memorySize = 100; // this is a hack... This is needed for start and end cells only
            horizontals = new boolean[1];
            horizontals[0] = (aInitialChar == '.');
            verticals = new boolean[1];
            verticals[0] = (aInitialChar == '.');
            memory = new boolean[memorySize];
        }
        int horizontalSize;
        int verticalSize;
        int memorySize;
        boolean[] horizontals;
        boolean[] verticals;
        boolean[] memory;

        void updateHorizontal(int row, int col, Cell[][] v) {
            if (initialChar == '#') return;
            horizontalSize = v[row].length - 2;
            horizontals = new boolean[horizontalSize];
            horizontals[0] = initialChar == '.' || initialChar == '^' || initialChar == 'v';
            for(int i = 1; i < horizontalSize; i++) {
                horizontals[i] = (v[row][(col-1+i)%horizontalSize + 1].initialChar != '<')
                        && (v[row][(col-1+horizontalSize-i)%horizontalSize + 1].initialChar != '>');
            }
        }


        void printHorizontals() {
            System.out.print("initial:"+initialChar+"- Horizontals:");
            for(boolean h : horizontals) System.out.print(h?"t":"f");
            System.out.println("  -- total:" + horizontalSize);
        }
        void printVerticals() {
            System.out.print("initial:"+initialChar+"- Verticals:");
            for(boolean v : verticals) System.out.print(v?"t":"f");
            System.out.println("  -- total:" + verticalSize);
        }

        void updateVertical(int row, int col, Cell[][] v) {
            if (initialChar == '#') return;
            verticalSize = v.length - 2;
            verticals = new boolean[verticalSize];
            verticals[0] = initialChar == '.' || initialChar == '>' || initialChar == '<';
            for(int i = 1; i < verticalSize; i++) {
                verticals[i] = (v[(row-1+i)%verticalSize + 1][col].initialChar != '^')
                        && (v[(row-1+verticalSize-i)%verticalSize + 1][col].initialChar != 'v');
            }
        }

        void updateMemory() {
            BigInteger h = new BigInteger("" + horizontalSize);
            BigInteger v = new BigInteger("" + verticalSize);
            memorySize = h.multiply(v).divide(h.gcd(v)).intValue();
            memory = new boolean[memorySize];
        }

        void cleanMemory() {
            Arrays.fill(memory, false);
        }
    }

    Cell[][] valley;
    LinkedList<Cell[]> inputCells = new LinkedList<>();
    @Override
    protected long processLinePart1(String line) {
        Cell[] inputLine = new Cell[line.length()];
        for(int i = 0; i < line.length(); i++) {
            inputLine[i] = new Cell(line.charAt(i));
        }
        inputCells.addLast(inputLine);
        return 0;
    }

    class State {
        int row, col;
        int round;
        State(int aRow, int aCol, int aRound) {
            row = aRow;
            col = aCol;
            round = aRound;
        }

        @Override
        public String toString() {
            return "State(row:"+row+", col:" + col + " - round:" + round + ")";
        }
    }

    private boolean iCanBeAt(int row, int col, int round) {
        if(row < 0 || col < 0) return false;
        if(row >= valley.length || col >= valley[row].length) return false;

        Cell cell = valley[row][col];
        if (cell.initialChar == '#') return false;
        if(cell.memory[round % cell.memorySize]) return false;
        boolean result = cell.horizontals[round % cell.horizontalSize] && cell.verticals[round % cell.verticalSize];

        // it's a hack to update memory here. In fact, iCanBeAt should return using memory but memory should be
        // updated later when adding to queue
        cell.memory[round % cell.memorySize] = true;

        return result;
    }

    private void initialSetUp() {
        valley = inputCells.toArray(new Cell[0][]);
        for (int row = 1; row < valley.length - 1; row++) {
            for (int col = 1; col < valley[row].length - 1; col++) {
                valley[row][col].updateHorizontal(row, col, valley);
                valley[row][col].updateVertical(row, col, valley);
                valley[row][col].updateMemory();
            }
        }
    }

    private int bfs(LinkedList<State> q, int targetRow, int targetCol) {
        while (!q.isEmpty()) {
            State state = q.removeFirst();
//            System.out.println("Starting to process state " + state);
//            valley[state.row][state.col].printHorizontals();
//            valley[state.row][state.col].printVerticals();

            if(state.row == targetRow && state.col == targetCol) {
                // arrived the exit
                return state.round;
            }
            // add all possible next moves
            if(iCanBeAt(state.row, state.col, state.round + 1)) q.addLast(new State(state.row, state.col, state.round + 1));
            if(iCanBeAt(state.row - 1, state.col, state.round + 1)) q.addLast(new State(state.row - 1, state.col, state.round + 1));
            if(iCanBeAt(state.row, state.col - 1, state.round + 1)) q.addLast(new State(state.row, state.col - 1, state.round + 1));
            if(iCanBeAt(state.row, state.col + 1, state.round + 1)) q.addLast(new State(state.row, state.col + 1, state.round + 1));
            if(iCanBeAt(state.row + 1, state.col, state.round + 1)) q.addLast(new State(state.row + 1, state.col, state.round + 1));
        }
        throw new RuntimeException("I can't find a solution");
    }

    @Override
    protected String endPart1() {
        // do initial set up
        initialSetUp();

        // Do BFS
        LinkedList<State> q = new LinkedList<>();
        q.addLast(new State(0, 1, 0));
        int result = bfs(q, valley.length - 1, valley[valley.length - 1].length - 2);

        return "Arrived the exit in " + result;
    }

    private void cleanMemory() {
        for(Cell[] cellRow : valley) {
            for (Cell cell : cellRow) {
                cell.cleanMemory();
            }
        }
    }

    @Override
    protected String endPart2() {
        // do initial set up
        initialSetUp();

        // Do BFS
        LinkedList<State> q = new LinkedList<>();
        q.addLast(new State(0, 1, 0));
        int result1 = bfs(q, valley.length - 1, valley[valley.length - 1].length - 2);

        System.out.println("I reached the end in " + result1);

        // Now go back to start
        q.clear();
        cleanMemory();
        q.addLast(new State(valley.length - 1, valley[valley.length - 1].length - 2, result1));
        int result2 = bfs(q, 0, 1);

        System.out.println("Back to end in " + result2);

        // To end again...
        q.clear();
        cleanMemory();
        q.addLast(new State(0, 1, result2));
        int result3 = bfs(q, valley.length - 1, valley[valley.length - 1].length - 2);

        return "Arrived the exit in " + result3;
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
