package com.mss.adventofcode2022;

public class Day4 extends DayBase {

    public Day4() {
        super(4);
    }

    class Range {
        int start;
        int end;

        public Range(String pair) {
            String[] list = pair.split("-");
            start = Integer.valueOf(list[0]);
            end = Integer.valueOf(list[1]);
        }
    }

    private boolean contains(Range elf1Range, Range elf2Range) {
        return (elf1Range.start <= elf2Range.start && elf1Range.end >= elf2Range.end);
    }

    private boolean contains(Range elf1Range, int id) {
        return (elf1Range.start <= id && elf1Range.end >= id);
    }

    protected long processLinePart1(String line) {
        String[] pairs = line.split(",");
        Range elf1Range = new Range(pairs[0]);
        Range elf2Range = new Range(pairs[1]);

        if (contains(elf1Range, elf2Range) || contains(elf2Range, elf1Range)) return 1;
        return 0;
    }
    protected long processLinePart2(String line) {
        String[] pairs = line.split(",");
        Range elf1Range = new Range(pairs[0]);
        Range elf2Range = new Range(pairs[1]);

        if (contains(elf1Range, elf2Range.start) ||
                contains(elf1Range, elf2Range.end) ||
                contains(elf2Range, elf1Range.start)) return 1;
        return 0;
    }
}
