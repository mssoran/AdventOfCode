package com.mss.adventofcode2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3 extends DayBase {

    public Day3() {
        super(3);
    }

    private int pri(char ch) {
        if (ch >= 'a' && ch <= 'z') return ch - 'a' + 1;
        if (ch >= 'A' && ch <= 'Z') return ch - 'A' + 1 + 26;
        throw new RuntimeException("char value is out of bounds");
    }

    protected long processLinePart1(String line) {
        int[] items = new int[53];
        int len = line.length();
        int mid = len / 2;
        // First mark items in the first compartment
        for (int i = 0; i < mid; i++) {
            items[pri(line.charAt(i))]++;
        }
        // Find the first (and only) item repeating
        for (int i = mid; i < len; i++) {
            int priority = pri(line.charAt(i));
            if (items[priority] > 0) return priority;
        }
        throw new RuntimeException("No errors are found");
    }

    private long processLine2(String line1, String line2, String line3) {
        int[] items = new int[53];
        int len = line1.length();
        // First mark items for the first elf
        for (int i = 0; i < len; i++) {
            int priority = pri(line1.charAt(i));
            if (items[priority] == 0) items[priority] = 1;
        }
        // Mark items for the second elf
        len = line2.length();
        for (int i = 0; i < len; i++) {
            int priority = pri(line2.charAt(i));
            if (items[priority] == 1) items[priority] = 2;
        }
        // Mark items for the third elf, and return the first found as badge
        len = line3.length();
        for (int i = 0; i < len; i++) {
            int priority = pri(line3.charAt(i));
            if (items[priority] == 2) return priority;
        }
        throw new RuntimeException("No errors are found");
    }

    private String[] group = new String[2];
    private int groupIndex = 0;

    protected long processLinePart2(String line) {
        if (groupIndex < 2) {
            group[groupIndex++] = line;
            return 0;
        }
        return processLine2(group[0], group[1], line);
    }

    public void part2() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/mss/adventofcode2022/input_day3.txt"))) {
            String line = reader.readLine();
            long totalScore = 0;
            while (line != null) {
                String line2 = reader.readLine();
                String line3 = reader.readLine();
                long score = processLine2(line, line2, line3);
                System.out.println("line:" + line + "line2:" + line2 + "line3:" + line3 + " score: " + score);
                totalScore += score;
                line = reader.readLine();
            }
            System.out.println("Total score is: " + totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
