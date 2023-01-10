package com.mss.adventofcode2022;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day11 extends DayBase {
    public Day11() {
        super(11);
    }

    class Operation {

        private final Pattern operationPattern = Pattern.compile("([*+]) (.*)");

        public Operation(String operation) {
            Matcher m = operationPattern.matcher(operation);
            if (m.find()) {
                operator = m.group(1).charAt(0);
                if (m.group(2).equals("old")) {
                    operand = null;
                } else {
                    operand = new BigInteger(m.group(2));
                }
            } else {
                throw new RuntimeException("Operation cannot be parsed:" + operation);
            }
        }

        char operator;
        BigInteger operand;

        public BigInteger result(BigInteger old) {
            BigInteger operandValue;
            if (operand == null) operandValue = old;
            else operandValue = operand;
            switch (operator) {
                case '*':
                    return old.multiply(operandValue).mod(mod);
                case '+':
                    return old.add(operandValue).mod(mod);
                default:
                    throw new RuntimeException("unknown operator");
            }
        }
    }

    class Monkey {
        LinkedList<BigInteger> items = new LinkedList<>();
        Operation operation;
        BigInteger testDivisible;
        int trueMonkey;
        int falseMonkey;
        long inspectionCount = 0;

        private String getRest(String starting, String line) {
            if (line.startsWith(starting)) return line.substring(starting.length());
            return null;
        }

        public void parseLine(String line) {
            String rest;
            if ((rest = getRest("  Starting items: ", line)) != null) {
                String[] items = rest.split(", ");
                for (String item : items) {
                    this.items.addLast(new BigInteger(item));
                }
            } else if ((rest = getRest("  Operation: new = old ", line)) != null) {
                this.operation = new Operation(rest);
            } else if ((rest = getRest("  Test: divisible by ", line)) != null) {
                this.testDivisible = new BigInteger(rest);
            } else if ((rest = getRest("    If true: throw to monkey ", line)) != null) {
                this.trueMonkey = Integer.valueOf(rest);
            } else if ((rest = getRest("    If false: throw to monkey ", line)) != null) {
                this.falseMonkey = Integer.valueOf(rest);
            } else if (line.isBlank()) {
                // do nothing
            } else {
                throw new RuntimeException("Unknown line:" + line);
            }
        }

    }

    LinkedList<Monkey> monkeysList = new LinkedList<Monkey>();
    int currentMonkeyId = 0;
    Monkey currentMonkey = null;

    @Override
    protected long processLinePart1(String line) {
        if (line.equals("Monkey " + currentMonkeyId + ":")) {
            currentMonkey = new Monkey();
            monkeysList.addLast(currentMonkey);
            currentMonkeyId++;
        } else {
            currentMonkey.parseLine(line);
        }
        return 0;
    }


    private static BigInteger mod = BigInteger.ONE;

    private void runTurn(Monkey[] monkeys, Monkey monkey, long divisor) {
        while (!monkey.items.isEmpty()) {
            monkey.inspectionCount++;
            BigInteger item = monkey.items.removeFirst();
            BigInteger newLevel = monkey.operation.result(item).divide(BigInteger.valueOf(divisor));
            int nextMonkey;
            if (newLevel.mod(monkey.testDivisible).compareTo(BigInteger.ZERO) == 0) {
                nextMonkey = monkey.trueMonkey;
            } else {
                nextMonkey = monkey.falseMonkey;
            }
            monkeys[nextMonkey].items.addLast(newLevel);
        }
    }

    private void printMonkeys() {
        int id = 0;
        for (Monkey monkey : monkeysList) {
            System.out.print("Monkey " + id++ + ":");
            for (BigInteger item : monkey.items) {
                System.out.print(item + ",");
            }
            System.out.println(" --- count:" + monkey.inspectionCount);
        }
    }

    private void runRound(Monkey[] monkeys, long divisor) {
        for (int i = 0; i < monkeys.length; i++) {
            runTurn(monkeys, monkeys[i], divisor);
        }
        //printMonkeys();
    }


    @Override
    protected String endPart1() {
        return endPart(20, 3);
    }

    private String endPart(int roundCount, long divisor) {
        printMonkeys();
        Monkey[] monkeys = monkeysList.toArray(new Monkey[0]);
        for (Monkey monkey : monkeys) mod = mod.multiply(monkey.testDivisible);
        for (int i = 0; i < roundCount; i++) {
            System.out.println("running round " + i);
            runRound(monkeys, divisor);
        }
        // find the max
        int maxMonkeyId = 0;
        for (int i = 1; i < monkeys.length; i++) {
            if (monkeys[i].inspectionCount > monkeys[maxMonkeyId].inspectionCount) {
                maxMonkeyId = i;
            }
        }
        //find the second max;
        int secondMaxId = 0;
        if (maxMonkeyId == 0) secondMaxId = 1;
        for (int i = 0; i < monkeys.length; i++) {
            if (i != maxMonkeyId && monkeys[i].inspectionCount > monkeys[secondMaxId].inspectionCount) {
                secondMaxId = i;
            }
        }
        return "max:" + monkeys[maxMonkeyId].inspectionCount + " second max:" + monkeys[secondMaxId].inspectionCount + " monkey business:" + (monkeys[maxMonkeyId].inspectionCount * monkeys[secondMaxId].inspectionCount);
    }

    @Override
    protected String endPart2() {
        return endPart(10000, 1);
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
