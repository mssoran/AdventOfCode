package com.mss.adventofcode2022;

import java.util.HashMap;

public class Day21 extends DayBase {
    public Day21() {
        super(21);
    }

    public class YellResult {
        long simpleVal;
        boolean thereIsHuman;
        public YellResult (long aSimpleVal, boolean aThereIsHuman) {
            simpleVal = aSimpleVal;
            thereIsHuman = aThereIsHuman;
        }
    }

    public class Monkey {
        String name;
        boolean simpleMonkey;
        boolean iHaveHuman = false;
        long simpleVal;
        String operation;
        String leftMonkey;
        String rightMonkey;

        public Monkey(String aName, long aVal) {
            if (aName.equals("humn")) {
                iHaveHuman = true;
            }
            simpleMonkey = true;
            name = aName;
            simpleVal = aVal;
        }

        public Monkey(String aName, String aLeftMonkey, String aOperation, String aRightMonkey) {
            simpleMonkey = false;
            name = aName;
            leftMonkey = aLeftMonkey;
            operation = aOperation;
            rightMonkey = aRightMonkey;
        }

        public YellResult yell() {
            if (simpleMonkey) {
                return new YellResult(simpleVal, iHaveHuman);
            }

            YellResult leftValue = monkeys.get(leftMonkey).yell();
            YellResult rightValue = monkeys.get(rightMonkey).yell();
            switch (operation) {
                case "+": simpleVal = leftValue.simpleVal + rightValue.simpleVal;
                break;
                case "-": simpleVal = leftValue.simpleVal - rightValue.simpleVal;
                break;
                case "*": simpleVal = leftValue.simpleVal * rightValue.simpleVal;
                break;
                case "/": simpleVal = leftValue.simpleVal / rightValue.simpleVal;
                break;
            }
            simpleMonkey = true;
            iHaveHuman = leftValue.thereIsHuman || rightValue.thereIsHuman;
            return new YellResult(simpleVal, iHaveHuman);
        }

        public long makeYellResultEqualTo(long targetVal) {
            if (name.equals("humn")) return targetVal;
            Monkey left= monkeys.get(leftMonkey);
            Monkey right= monkeys.get(rightMonkey);
            YellResult leftResult = left.yell();
            YellResult rightResult = right.yell();
            if (leftResult.thereIsHuman) {
                long newTarget;
                switch (operation) {
                    case "+": newTarget = targetVal - rightResult.simpleVal;
                        break;
                    case "-": newTarget = targetVal + rightResult.simpleVal;
                        break;
                    case "*": newTarget = targetVal / rightResult.simpleVal;
                        break;
                    case "/": newTarget = targetVal * rightResult.simpleVal;
                        break;
                    default: throw new RuntimeException("Unknown operation !!!");
                }
                return left.makeYellResultEqualTo(newTarget);
            } else {
                long newTarget;
                switch (operation) {
                    case "+": newTarget = targetVal - leftResult.simpleVal;
                        break;
                    case "-": newTarget = leftResult.simpleVal - targetVal;
                        break;
                    case "*": newTarget = targetVal / leftResult.simpleVal;
                        break;
                    case "/": newTarget = leftResult.simpleVal / targetVal;
                        break;
                    default: throw new RuntimeException("Unknown operation !!!");
                }
                return right.makeYellResultEqualTo(newTarget);
            }
        }
    }

    HashMap<String, Monkey> monkeys = new HashMap<>();

    @Override
    protected long processLinePart1(String line) {
        String[] split = line.split(" ");
        String monkeyName = split[0].substring(0, split[0].length()-1);
        //System.out.println("monkey name is " + monkeyName + " size of split is " + split.length);
        if (split.length == 2) {
            monkeys.put(monkeyName, new Monkey(monkeyName, Integer.valueOf(split[1])));
        } else {
            monkeys.put(monkeyName, new Monkey(monkeyName, split[1], split[2], split[3]));
        }
        return 0;
    }

    @Override
    protected String endPart1() {
        YellResult result = monkeys.get("root").yell();
        return "root yells " + result.simpleVal + " do I have human is  " + result.thereIsHuman;
    }

    @Override
    protected String endPart2() {
        Monkey rootMonkey = monkeys.get("root");
        rootMonkey.yell();
        Monkey leftMonkey = monkeys.get(rootMonkey.leftMonkey);
        Monkey rightMonkey = monkeys.get(rootMonkey.rightMonkey);
        YellResult leftResult = leftMonkey.yell();
        YellResult rightResult = rightMonkey.yell();
        long humanVal;
        if (leftResult.thereIsHuman) {
            humanVal = leftMonkey.makeYellResultEqualTo(rightResult.simpleVal);
        } else {
            humanVal = rightMonkey.makeYellResultEqualTo(leftResult.simpleVal);
        }
        return "to make them equal, humn must yell " + humanVal;
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
