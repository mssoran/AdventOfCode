package com.mss.adventofcode2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

abstract public class DayBase {

    protected String inputFileName = "src/main/java/com/mss/adventofcode2022/input_day3.txt";

    protected DayBase(int day) {
        inputFileName = "src/main/java/com/mss/adventofcode2022/input_day" + day + ".txt";
    }

    abstract protected long processLinePart1(String line);

    protected String endPart1() {
        return "--";
    }

    protected String endPart2() {
        return "--";
    }

    abstract protected long processLinePart2(String line);

    public void part1() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line = reader.readLine();
            long totalScore = 0;
            while (line != null) {
                long score = processLinePart1(line);
                System.out.println("line:" + line + " score: " + score);
                totalScore += score;
                line = reader.readLine();
            }
            System.out.println("Part1 End: " + endPart1());
            System.out.println("Total part1 score is: " + totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void part2() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line = reader.readLine();
            long totalScore = 0;
            while (line != null) {
                long score = processLinePart2(line);
                System.out.println("line:" + line + " score: " + score);
                totalScore += score;
                line = reader.readLine();
            }
            System.out.println("Part2 End: " + endPart2());
            System.out.println("Total part2 score is: " + totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
