package com.mss.adventofcode2022;

public class Main {


    private static void printUsage() {
        System.out.println("There are two ways:");
        System.out.println("   d -> day d,part1 -> runs com.mss.adventofcode2022.day1_1");
        System.out.println("   d p -> day d, part p -> runs com.mss.adventofcode2022.dayd_p");
    }

    private static void runSolution(int day, int part, String[] args) {
        DayBase dayObj;
        switch (day) {
            case 1: dayObj = new Day1(); break;
            case 2: dayObj = new Day2(); break;
            case 3: dayObj = new Day3(); break;
            case 4: dayObj = new Day4(); break;
            case 5: dayObj = new Day5(); break;
            case 6: dayObj = new Day6(); break;
            case 7: dayObj = new Day7(); break;
            case 8: dayObj = new Day8(); break;
            case 9: dayObj = new Day9(); break;
            case 10: dayObj = new Day10(); break;
//            case 11: dayObj = new Day11(); break;
//            case 12: dayObj = new Day12(); break;
//            case 13: dayObj = new Day13(); break;
//            case 14: dayObj = new Day14(); break;
//            case 15: dayObj = new Day15(); break;
//            case 16: dayObj = new Day16(); break;
//            case 17: dayObj = new Day17(); break;
//            case 18: dayObj = new Day18(); break;
//            case 19: dayObj = new Day19(); break;
//            case 20: dayObj = new Day20(); break;
//            case 21: dayObj = new Day21(); break;
//            case 22: dayObj = new Day22(); break;
//            case 23: dayObj = new Day23(); break;
//            case 24: dayObj = new Day24(); break;
//            case 25: dayObj = new Day25(); break;
            default:
                printUsage();
                throw new RuntimeException("Day "+day+" is not supported");
        }
        if(part == 1) {
            dayObj.part1();
        } else if(part == 2) {
            dayObj.part2();
        } else {
            printUsage();
            throw new RuntimeException("Unknown day, part: " + day + "," + part);
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            runSolution(Integer.valueOf(args[0]), 1, args);
        } else if (args.length == 2) {
            runSolution(Integer.valueOf(args[0]), Integer.valueOf(args[1]), args);
        } else {
            printUsage();
        }
    }
}
