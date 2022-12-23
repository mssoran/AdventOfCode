package com.mss.adventofcode2022;
public class Day1 extends DayBase {
    public Day1() {
        super(1);
    }
    long maxTotal = 0;
    long maxOfElf = 0;
    long currentElfTotal = 0;
    long currentElfMax = 0;
    long currentElf = 0;

    @Override
    protected long processLinePart1(String line) {
        if (line.isBlank()) {
            endPart1();
        } else {
            currentElf = Long.parseLong(line);
            currentElfTotal += currentElf;
            if (currentElf > currentElfMax) currentElfMax = currentElf;
        }
        return 0;
    }

    @Override
    protected String endPart1() {
        if (currentElfTotal > maxTotal) {
            maxTotal = currentElfTotal;
            maxOfElf = currentElfMax;
        }
        currentElfTotal = 0;
        currentElfMax = 0;
        return maxOfElf + "";
    }

    @Override
    protected long processLinePart2(String line) {
        throw new RuntimeException("not supported");
    }
}
