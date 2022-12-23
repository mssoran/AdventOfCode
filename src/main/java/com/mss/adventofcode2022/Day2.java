package com.mss.adventofcode2022;

public class Day2 extends DayBase {
    public Day2() {
        super(2);
    }

    enum State {
        Rock, Paper, Scissor;
    }

    private State readState(Character ch) {
        if (ch == 'X' || ch == 'A') return State.Rock;
        if (ch == 'Y' || ch == 'B') return State.Paper;
        if (ch == 'Z' || ch == 'C') return State.Scissor;
        throw new RuntimeException("Unknown State");
    }

    private static State nextState(State currentState) {
        switch (currentState) {
            case Rock:
                return State.Paper;
            case Paper:
                return State.Scissor;
            case Scissor:
                return State.Rock;
        }
        throw new RuntimeException("Unknown State, can't find next state");
    }

    private static boolean win(State opponentState, State myState) {
        return (opponentState == State.Rock && myState == State.Paper) ||
                (opponentState == State.Paper && myState == State.Scissor) ||
                (opponentState == State.Scissor && myState == State.Rock);
    }

    private static long findScore(State opponentState, State myState) {

        long score = 0;
        switch (myState) {
            case Rock:
                score += 1;
                break;
            case Paper:
                score += 2;
                break;
            case Scissor:
                score += 3;
                break;
        }
        if (opponentState == myState) score += 3;
        else if (win(opponentState, myState)) score += 6;

        return score;
    }

    private static State findStateFromResult(State opponentState, char ch) {
        switch (ch) {
            case 'X': // lose
                return nextState(nextState(opponentState));
            case 'Y': // draw
                return opponentState;
            case 'Z': // win
                return nextState(opponentState);
            default:
                throw new RuntimeException("Unknown State");
        }
    }

    @Override
    protected long processLinePart1(String line) {
        // line needs to be 3 chars
        State opponentState = readState(line.charAt(0));
        State myState = readState(line.charAt(2));
        return findScore(opponentState, myState);
    }

    @Override
    protected long processLinePart2(String line) {
        // line needs to be 3 chars
        State opponentState = readState(line.charAt(0));
        State myState = findStateFromResult(opponentState, line.charAt(2));
        return findScore(opponentState, myState);
    }


}
