package com.mss.adventofcode2022;

public class Day6 extends DayBase {
    public Day6() {
        super(6);
    }

    private boolean allDifferent(char[] recentChars) {
        boolean[] chars = new boolean[26];
        int index;
        for (int i = 0; i < recentChars.length; i++) {
            index = recentChars[i] - 'a';
            if(chars[index]) return false;
            chars[index] = true;
        }
        return true;
    }

    private long processLine(String line, int recentCharsSize) {
        char[] recentChars = new char[recentCharsSize];
        for (int i = 0; i < recentCharsSize - 1; i++) {
            recentChars[i] = line.charAt(i);
        }
        int index = recentCharsSize - 1;
        while (index < line.length()) {
            recentChars[index % recentCharsSize] = line.charAt(index);
            if (allDifferent(recentChars)) {
                return index + 1;
            }
            index++;
        }
        throw new RuntimeException("Cannot find a solution");
    }

    @Override
    protected long processLinePart1(String line) {
        return processLine(line, 4);
    }

    @Override
    protected String endPart1() {
        return super.endPart1();
    }

    @Override
    protected String endPart2() {
        return super.endPart2();
    }

    @Override
    protected long processLinePart2(String line) {
        return processLine(line, 14);
    }
}
