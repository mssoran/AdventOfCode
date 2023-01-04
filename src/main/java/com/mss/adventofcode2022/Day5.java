package com.mss.adventofcode2022;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day5 extends DayBase {
    public Day5() {
        super(5);
    }

    enum ReadingState {
        Stacks, Movements;
    }

    // The input file format is not very well defined, and limits are not clear.
    // I don't want to spend a lot of time to parse the input etc, so, I'll make
    // some assumptions.
    LinkedList<Character>[] stacks = new LinkedList[100];
    ReadingState currentReadingState = ReadingState.Stacks;

    private void ensureStack(int stackNo) {
        if (stacks[stackNo] == null) stacks[stackNo] = new LinkedList<>();
    }

    private void updateStacks(String line) {
        int len = line.length();
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            if (ch >= 'A' && ch <= 'Z') { // this is another assumption, which is not clear in the question
                int stackNo = i / 4;
                ensureStack(stackNo);
                stacks[stackNo].addFirst(ch);
            }
        }
    }

    private Pattern r = Pattern.compile("move (\\d*) from (\\d*) to (\\d*)");

    private void moveCrates(String line, boolean moveAsGroup) {
        Matcher m = r.matcher(line);
        if (m.find()) {
            int count = Integer.valueOf(m.group(1));
            int from = Integer.valueOf(m.group(2)) - 1;
            int to = Integer.valueOf(m.group(3)) - 1;
            LinkedList<Character> fromStack;
            if (moveAsGroup) {
                fromStack = new LinkedList<>();
                for (int i = 0; i < count; i++) {
                    fromStack.addLast(stacks[from].removeLast());
                }
            } else {
                fromStack = stacks[from];
            }
            ensureStack(to);
            for (int i = 0; i < count; i++) {
                stacks[to].addLast(fromStack.removeLast());
            }
        } else {
            throw new RuntimeException("Input format is wrong: " + line);
        }
    }

    private long processLine(String line, boolean moveAsGroup) {
        if (currentReadingState == ReadingState.Stacks) {
            if (line.contains("[")) {
                updateStacks(line);
            } else if (line.isBlank()) {
                currentReadingState = ReadingState.Movements;
            } else {
                // Do nothing...
            }
        } else {
            moveCrates(line, moveAsGroup);
        }
        return 0;
    }

    @Override
    protected long processLinePart1(String line) {
        return processLine(line, false);
    }

    @Override
    protected String endPart1() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            if (stacks[i] != null && !stacks[i].isEmpty()) sb.append(stacks[i].peekLast());
        }
        return sb.toString();
    }

    @Override
    protected String endPart2() {
        return endPart1();
    }

    @Override
    protected long processLinePart2(String line) {
        return processLine(line, true);
    }
}
