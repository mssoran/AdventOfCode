package com.mss.adventofcode2022;

import java.util.LinkedList;

public class Day22 extends DayBase {
    public Day22() {
        super(22);
    }

    private final char wallTile = '#';
    private final char noTile = ' ';

    // Action is either walk stepCount steps in the current direction, or turn R/L
    class Action {
        boolean isTurnAction;
        long stepCount;
        char turnDirection;

        public Action() {
            isTurnAction = false;
            stepCount = 0;
        }

        public Action(char ch) {
            if(ch < '0' || ch > '9') {
                isTurnAction = true;
                turnDirection = ch;
            } else {
                isTurnAction = false;
                stepCount = ch - '0';
            }
        }

        // returns true if this action can be updated by the char. Otherwise, returns false
        boolean updateAction(char ch) {
            if(isTurnAction) return false;
            if(ch < '0' || ch > '9') return false;
            stepCount *= 10;
            stepCount += ch - '0';
            return true;
        }

        @Override
        public String toString() {
            if (isTurnAction) return "[" + turnDirection + "]" ;
            return "[" + stepCount + "]" ;
        }
    }

    class Face {
        String name;
        int rowStart;
        int colStart;
        int faceLen;
        FaceTransition[] transitions = new FaceTransition[4];

        public Face (String aName, int aRowStart, int aColStart, int aFaceLen) {
            name = aName;
            rowStart = aRowStart;
            colStart = aColStart;
            faceLen = aFaceLen;
        }

        public void doTransition(State state, int direction, int location) {
            transitions[direction].doTransition(state, location);

        }
    }

    // Keeps information about how to move from one face to the next. By setting correct
    // face transitions, we can model both simple wrap style and cube style movements.
    class FaceTransition{
        int inputDirection;
        boolean isReverse;
        Face targetFace;

        public FaceTransition(Face aTargetFace, int aInputDirection, boolean aIsReverse) {
            targetFace = aTargetFace;
            inputDirection = aInputDirection;
            isReverse = aIsReverse;
        }

        public void doTransition (State state, int location) {
            int newLocation = location;
            if (isReverse) {
                newLocation = targetFace.faceLen - 1 - location;
            }
            int row = 0;
            int col = 0;
            switch (inputDirection) {
                case 0: // >
                    row = newLocation;
                    col = 0;
                    break;
                case 1: // v
                    row = 0;
                    col = newLocation;
                    break;
                case 2: // <
                    row = newLocation;
                    col = targetFace.faceLen - 1;
                    break;
                case 3: // ^
                    row = targetFace.faceLen - 1;
                    col = newLocation;
                    break;
            }
            state.face = targetFace;
            state.row = row;
            state.col = col;
            state.direction = inputDirection;
        }
    }

    class State {
        int row;
        int col;
        int direction;
        Face face;

        private State copy() {
            State newState = new State();
            newState.row = row;
            newState.col = col;
            newState.direction = direction;
            newState.face = face;

            return newState;
        }


        private void moveOne() {
            switch (direction) {
                case 0: // >
                    if (col == face.faceLen - 1) {
                        // I'm on the edge. I need to do face transition
                        face.doTransition(this, 0, row);
                    } else {
                        col++;
                    }
                    break;
                case 1: // v
                    if (row == face.faceLen - 1) {
                        face.doTransition(this, 1, col);
                    } else {
                        row++;
                    }
                    break;
                case 2: // <
                    if (col == 0) {
                        face.doTransition(this, 2, row);
                    } else {
                        col--;
                    }
                    break;
                case 3:// ^
                    if (row == 0) {
                        face.doTransition(this, 3, col);
                    } else {
                        row--;
                    }
                    break;
                default:
                    throw new RuntimeException("cannot move, direction is unknown");
            }
        }

        public void move(long stepCount) {
            long count = stepCount;
            State newState = copy();
            System.out.println("newState:" + newState);
            while (count > 0) {
                newState.moveOne();
                //System.out.println("after moveOne newState:" + newState);

                if (field[newState.row + newState.face.rowStart][newState.col + newState.face.colStart] == wallTile) return;
                row = newState.row;
                col = newState.col;
                direction = newState.direction;
                face = newState.face;
                count--;
            }
        }
        public void applyAction(Action action) {
            if (action.isTurnAction) {
                switch (action.turnDirection) {
                    case 'R': direction = (direction + 1) % 4;
                        break;
                    case 'L': direction = (direction + 3) % 4;
                        break;
                    default:
                        throw new RuntimeException("Unknown direction");
                }
            } else {
                move(action.stepCount);
            }
        }

        public long password() {
            System.out.println("name:" + face.name + " row:" + row + " col:" + col + " direction:" + direction + "--"+face.rowStart+","+face.colStart);
            return (1000 * (row+ face.rowStart+1) + 4 * (col+face.colStart+1) + direction);
        }

        @Override
        public String toString() {
            return face.name+"(" + row + "," + col +")" + direction;
        }
    }

    char[][] field = new char[500][];
    int lineCount = 0;
    boolean readingField = true;
    LinkedList<Action> actions = new LinkedList<>();

    private void parsePath(String line) {
        Action currentAction = new Action();
        actions.addLast(currentAction);
        for (char ch : line.toCharArray()) {
            if (!currentAction.updateAction(ch)) {
                currentAction = new Action(ch);
                actions.addLast(currentAction);
            }
        }
    }

    @Override
    protected long processLinePart1(String line) {
        if (line.isBlank()) {
            readingField = false;
            return 0;
        }
        if (readingField) {
            field[lineCount++] = line.toCharArray();
        } else {
            parsePath(line);
        }
        return 0;
    }

    private String endPart(Face face) {
        State state = new State();
        state.face = face;
        state.row = 0;
        state.col = 0;
        state.direction = 0;

        System.out.println("Initial state is " + state);
        for (Action action : actions) {
            state.applyAction(action);
            System.out.println("After action " + action + " the state is " + state);
        }

        return "password is " + state.password();
    }

    @Override
    protected String endPart1() {
        // Not the most general way, but works to run both test and input without rebuilding
        if (lineCount == 200) {
            return endPart(facesPart1());
        } else {
            return endPart(facesTest1());
        }
    }

    /*
I'm going to hard code all the mappings. I don't like it, but currently I don't want to generalize.
Here is the input data. Each square is a face of the cube. Arrows shows how moving from one face to other
maps.

                                      ┌──◄───────┐
     ┌◄──────────────────────┐     1  │   1      │
     │                  4 5  │   9 0  │   4      │
     │           0      9 0  │   9 0  │   9      │
     │                   ┌───┼────┬───▼────┐     │
     │                   │   >    │   >    │     │
     │                   │        │        │     │
     │  ┌────────────────┼v  A    │   B   v◄───┐ │
     │  │     49         │        │        │   ▲ │
     │  │                ├────────┼────▲───┘   │ │
     │  │     50         │        │    │       │ │
     │  │                │        │    │       │ │
     │  ▼           ┌────┼── C  ──┼────┘       │ │
     │  │     99    │    │        │            │ │
     │  │       ┌───▼────┼────────┤            │ │
     │  │    100│        │        │            │ │
     │  │       │        │        │            │ │
     │  └───────►^  D    │   E  ^─┼────────────┘ │
     │       149│        │        │              │
     │          ├────────┼───▲────┘              │
     │       150│        │   │                   │
     │          │        │   │                   │
     └──────────►v  F   ─┼───┘                   │
             199│   >    │                       │
                └───┼────┘                       │
                    │                            │
                    └───────────────────────────►┘


 */

    // This is the input for simple wrap style movements
    private Face facesPart1() {
        Face A = new Face("A", 0, 50, 50);
        Face B = new Face("B", 0, 100, 50);
        Face C = new Face("C", 50, 50, 50);
        Face D = new Face("D", 100, 0, 50);
        Face E = new Face("E", 100, 50, 50);
        Face F = new Face("F", 150, 0, 50);
        A.transitions[0] = new FaceTransition(B, 0, false);
        A.transitions[1] = new FaceTransition(C, 1, false);
        A.transitions[2] = new FaceTransition(B, 2, false);
        A.transitions[3] = new FaceTransition(E, 3, false);

        B.transitions[0] = new FaceTransition(A, 0, false);
        B.transitions[1] = new FaceTransition(B, 1, false);
        B.transitions[2] = new FaceTransition(A, 2, false);
        B.transitions[3] = new FaceTransition(B, 3, false);

        C.transitions[0] = new FaceTransition(C, 0, false);
        C.transitions[1] = new FaceTransition(E, 1, false);
        C.transitions[2] = new FaceTransition(C, 2, false);
        C.transitions[3] = new FaceTransition(A, 3, false);

        D.transitions[0] = new FaceTransition(E, 0, false);
        D.transitions[1] = new FaceTransition(F, 1, false);
        D.transitions[2] = new FaceTransition(E, 2, false);
        D.transitions[3] = new FaceTransition(F, 3, false);

        E.transitions[0] = new FaceTransition(D, 0, false);
        E.transitions[1] = new FaceTransition(A, 1, false);
        E.transitions[2] = new FaceTransition(D, 2, false);
        E.transitions[3] = new FaceTransition(C, 3, false);

        F.transitions[0] = new FaceTransition(F, 0, false);
        F.transitions[1] = new FaceTransition(D, 1, false);
        F.transitions[2] = new FaceTransition(F, 2, false);
        F.transitions[3] = new FaceTransition(D, 3, false);

        return A;
    }

    // Data for cube style movements. The above figure shows which faces are connected with arrows
    private Face facesPart2() {
        Face A = new Face("A", 0, 50, 50);
        Face B = new Face("B", 0, 100, 50);
        Face C = new Face("C", 50, 50, 50);
        Face D = new Face("D", 100, 0, 50);
        Face E = new Face("E", 100, 50, 50);
        Face F = new Face("F", 150, 0, 50);
        A.transitions[0] = new FaceTransition(B, 0, false);
        A.transitions[1] = new FaceTransition(C, 1, false);
        A.transitions[2] = new FaceTransition(D, 0, true);
        A.transitions[3] = new FaceTransition(F, 0, false);

        B.transitions[0] = new FaceTransition(E, 2, true);
        B.transitions[1] = new FaceTransition(C, 2, false);
        B.transitions[2] = new FaceTransition(A, 2, false);
        B.transitions[3] = new FaceTransition(F, 3, false);

        C.transitions[0] = new FaceTransition(B, 3, false);
        C.transitions[1] = new FaceTransition(E, 1, false);
        C.transitions[2] = new FaceTransition(D, 1, false);
        C.transitions[3] = new FaceTransition(A, 3, false);

        D.transitions[0] = new FaceTransition(E, 0, false);
        D.transitions[1] = new FaceTransition(F, 1, false);
        D.transitions[2] = new FaceTransition(A, 0, true);
        D.transitions[3] = new FaceTransition(C, 0, false);

        E.transitions[0] = new FaceTransition(B, 2, true);
        E.transitions[1] = new FaceTransition(F, 2, false);
        E.transitions[2] = new FaceTransition(D, 2, false);
        E.transitions[3] = new FaceTransition(C, 3, false);

        F.transitions[0] = new FaceTransition(E, 3, false);
        F.transitions[1] = new FaceTransition(B, 1, false);
        F.transitions[2] = new FaceTransition(A, 1, false);
        F.transitions[3] = new FaceTransition(D, 3, false);

        return A;
    }


    /*

           ┌──────────────────┐
           │                  │
           │                  │  1 1      1
        0  │   3 4      7 8   │  1 2      5
      0    │             ┌────┴───┐
           │             │    ▼r  │
           │             │f       │
           │        ┌────┤►  A  ──┼───────────┐
      3    │        │    │        │           │
       ┌───┼────┬───┼────┼────────┤           │
      4│   │    │   │    │        │           │
       │        │        │        │           │
   ┌───┼─  B    │   C    │   D  ──┼───┐       │
   │  7│   │    │   │    │        │   │       │
   │   └───┼────┴───┼────┼────────┼───┴────┐  │
   │  8    │        │    │        │   ▼r   │  │
   │       │        │    │r       │       r│  │
   │       │        └────┤►  E    │   F   ◄├──┘
   │ 11    │             │   ▲r   │   ▲r   │
   │       │             └───┬────┴───┬────┘
   │       │                 │        │
   │       └─────────────────┘        │
   │                                  │
   └──────────────────────────────────┘

     */

    // This is the test input for simple wrap style movements
    private Face facesTest1() {
        Face A = new Face("A", 0, 8, 4);
        Face B = new Face("B", 4, 0, 4);
        Face C = new Face("C", 4, 4, 4);
        Face D = new Face("D", 4, 8, 4);
        Face E = new Face("E", 8, 8, 4);
        Face F = new Face("F", 8, 12, 4);

        A.transitions[0] = new FaceTransition(A, 0, false);
        A.transitions[1] = new FaceTransition(D, 1, false);
        A.transitions[2] = new FaceTransition(A, 2, false);
        A.transitions[3] = new FaceTransition(E, 3, false);

        B.transitions[0] = new FaceTransition(C, 0, false);
        B.transitions[1] = new FaceTransition(B, 1, false);
        B.transitions[2] = new FaceTransition(D, 2, false);
        B.transitions[3] = new FaceTransition(B, 3, false);

        C.transitions[0] = new FaceTransition(D, 0, false);
        C.transitions[1] = new FaceTransition(C, 1, false);
        C.transitions[2] = new FaceTransition(B, 2, false);
        C.transitions[3] = new FaceTransition(B, 3, false);

        D.transitions[0] = new FaceTransition(B, 0, false);
        D.transitions[1] = new FaceTransition(E, 1, false);
        D.transitions[2] = new FaceTransition(C, 2, false);
        D.transitions[3] = new FaceTransition(A, 3, false);

        E.transitions[0] = new FaceTransition(F, 0, false);
        E.transitions[1] = new FaceTransition(A, 1, false);
        E.transitions[2] = new FaceTransition(F, 2, false);
        E.transitions[3] = new FaceTransition(D, 3, false);

        F.transitions[0] = new FaceTransition(E, 0, false);
        F.transitions[1] = new FaceTransition(F, 1, false);
        F.transitions[2] = new FaceTransition(E, 2, false);
        F.transitions[3] = new FaceTransition(F, 3, false);

        return A;
    }

    // Test data for cube style movements. The above figure shows which faces are connected with arrows
    private Face facesTest2() {
        Face A = new Face("A", 0, 8, 4);
        Face B = new Face("B", 4, 0, 4);
        Face C = new Face("C", 4, 4, 4);
        Face D = new Face("D", 4, 8, 4);
        Face E = new Face("E", 8, 8, 4);
        Face F = new Face("F", 8, 12, 4);

        A.transitions[0] = new FaceTransition(F, 2, true);
        A.transitions[1] = new FaceTransition(D, 1, false);
        A.transitions[2] = new FaceTransition(C, 1, false);
        A.transitions[3] = new FaceTransition(B, 1, true);

        B.transitions[0] = new FaceTransition(C, 0, false);
        B.transitions[1] = new FaceTransition(E, 3, true);
        B.transitions[2] = new FaceTransition(F, 3, true);
        B.transitions[3] = new FaceTransition(A, 1, true);

        C.transitions[0] = new FaceTransition(D, 0, false);
        C.transitions[1] = new FaceTransition(E, 0, true);
        C.transitions[2] = new FaceTransition(B, 2, false);
        C.transitions[3] = new FaceTransition(A, 0, false);

        D.transitions[0] = new FaceTransition(F, 1, true);
        D.transitions[1] = new FaceTransition(E, 1, false);
        D.transitions[2] = new FaceTransition(C, 2, false);
        D.transitions[3] = new FaceTransition(A, 3, false);

        E.transitions[0] = new FaceTransition(F, 0, false);
        E.transitions[1] = new FaceTransition(B, 3, true);
        E.transitions[2] = new FaceTransition(C, 3, true);
        E.transitions[3] = new FaceTransition(D, 3, false);

        F.transitions[0] = new FaceTransition(A, 2, true);
        F.transitions[1] = new FaceTransition(B, 0, true);
        F.transitions[2] = new FaceTransition(E, 2, false);
        F.transitions[3] = new FaceTransition(D, 2, true);

        return A;
    }

    @Override
    protected String endPart2() {
        if (lineCount == 200) {
            return endPart(facesPart2());
        } else {
            return endPart(facesTest2());
        }
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
