package com.mss.adventofcode2022;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 extends DayBase {
    public Day19() {
        super(19);
    }

    // Resource numbering:
    int oreId = 0;
    int clayId = 1;
    int obsidianId = 2;
    int geodeId = 3;

    class Robot {
        String name;
        int[] resourcesNeeded = new int[4];
        int count;

        public Robot(String aName) {
            name = aName;
        }
    }

    class BluePrint {
        int id;
        Robot oreRobot = new Robot("oreRobot");
        Robot clayRobot = new Robot("clayRobot");
        Robot obsidianRobot = new Robot("obsidianRobot");
        Robot geodeRobot = new Robot("geodeRobot");
        int[] resources = new int[4];

        public BluePrint(int aId) {
            id = aId;
            oreRobot.count = 1;
        }

        // note that robot must be a robot of this blueprint
        public boolean canBuild(Robot robot) {
            for (int i = 0; i < 4; i++) {
                if (resources[i] < robot.resourcesNeeded[i]) return false;
            }
            return true;
        }

        // note that robot must be a robot of this blueprint
        public void build(Robot robot) {
            for (int i = 0; i < 4; i++) {
                resources[i] -= robot.resourcesNeeded[i];
            }
            robot.count++;
        }

        // note that robot must be a robot of this blueprint
        public void unBuild(Robot robot) {
            for (int i = 0; i < 4; i++) {
                resources[i] += robot.resourcesNeeded[i];
            }
            robot.count--;
        }

        // note that robot must be a robot of this blueprint
        public void collect() {
            resources[oreId] += oreRobot.count;
            resources[clayId] += clayRobot.count;
            resources[obsidianId] += obsidianRobot.count;
            resources[geodeId] += geodeRobot.count;
        }

        // note that robot must be a robot of this blueprint
        public void collectExcept(Robot robot) {
            robot.count--;
            collect();
            robot.count++;
        }

        // note that robot must be a robot of this blueprint
        public void unCollect() {
            resources[oreId] -= oreRobot.count;
            resources[clayId] -= clayRobot.count;
            resources[obsidianId] -= obsidianRobot.count;
            resources[geodeId] -= geodeRobot.count;
        }

        // note that robot must be a robot of this blueprint
        public void unCollectExcept(Robot robot) {
            robot.count--;
            unCollect();
            robot.count++;
        }
    }

    Pattern inputBlueprint = Pattern.compile("Blueprint (\\d*): Each ore robot costs (\\d*) ore. Each clay robot costs (\\d*) ore. Each obsidian robot costs (\\d*) ore and (\\d*) clay. Each geode robot costs (\\d*) ore and (\\d*) obsidian.");

    BluePrint currentBlueprint;

    private int maximizeForRobot(BluePrint bp, int remainingTime, Robot robot, int currentMax) {
        if (bp.canBuild(robot)) {
            bp.build(robot);
            bp.collectExcept(robot);
            int result = maximizeGeode(bp, remainingTime - 1, currentMax);
            bp.unCollectExcept(robot);
            bp.unBuild(robot);
            if (result > currentMax) {
                return result;
            }
        }
        return currentMax;
    }

    private void printBluePrint(BluePrint bp, int remainingTime) {
        for (int i = 25; i > remainingTime; i--) System.out.print(" ");
        System.out.println(remainingTime + " - (res,robot count) ore: (" + bp.resources[oreId] + "," + bp.oreRobot.count + ") " +
                "clay: (" + bp.resources[clayId] + "," + bp.clayRobot.count + ") " +
                "obsidian: (" + bp.resources[obsidianId] + "," + bp.obsidianRobot.count + ") " +
                "geode: (" + bp.resources[geodeId] + "," + bp.geodeRobot.count + ")");
    }

    // TODO I'm not happy with the performance, but it still finds the result in a reasonable time. This can be improved though.
    private int maximizeGeode(BluePrint bp, int remainingTime, int bestFound) {
//        printBluePrint(bp, remainingTime);
        if (remainingTime == 0) {
            return bp.resources[geodeId];
        }

        // Try to find an upper bound for the total geode I can build
        int maxICanDo = bp.resources[geodeId];
        // add the number of geodes that can be built by the existing robots
        maxICanDo += bp.geodeRobot.count * remainingTime;
        // Find how much time is needed to build the next geode robot
        int timeNeeded = 0;
        if (bp.resources[oreId] < bp.geodeRobot.resourcesNeeded[oreId]) {
            int neededOre = (bp.geodeRobot.resourcesNeeded[oreId] - bp.resources[oreId]);
            int oreRobotCount = bp.oreRobot.count;
            while (neededOre > 0) {
                timeNeeded++;
                neededOre -= oreRobotCount;
                oreRobotCount += 1;
            }
        }
        if (bp.resources[obsidianId] < bp.geodeRobot.resourcesNeeded[obsidianId]) {
            int neededObsidian = (bp.geodeRobot.resourcesNeeded[obsidianId] - bp.resources[obsidianId]);
            int obsidianRobotCount = bp.obsidianRobot.count;
            int tempTimeNeeded = 0;
            while (neededObsidian > 0) {
                tempTimeNeeded++;
                neededObsidian -= obsidianRobotCount;
                obsidianRobotCount += 1;
            }
            // take the max
            if (tempTimeNeeded > timeNeeded) timeNeeded = tempTimeNeeded;
        }
        // assuming I can build a new geodeRobot every remaining minute
        maxICanDo += ((remainingTime - timeNeeded - 1 ) * (remainingTime - timeNeeded - 2) / 2);

        if(maxICanDo < bestFound) return 0;


        int currentMax = 0;
        currentMax = maximizeForRobot(bp, remainingTime, bp.geodeRobot, bestFound);
        if (bp.oreRobot.count >= bp.geodeRobot.resourcesNeeded[oreId] && bp.obsidianRobot.count >= bp.geodeRobot.resourcesNeeded[obsidianId]) {
            // There is no need to look at other options.
            return currentMax;
        }
        if (bp.obsidianRobot.count < bp.geodeRobot.resourcesNeeded[obsidianId]) {
            currentMax = maximizeForRobot(bp, remainingTime, bp.obsidianRobot, currentMax);
        }
        if (bp.clayRobot.count < bp.obsidianRobot.resourcesNeeded[clayId]) {
            currentMax = maximizeForRobot(bp, remainingTime, bp.clayRobot, currentMax);
        }
        currentMax = maximizeForRobot(bp, remainingTime, bp.oreRobot, currentMax);
        bp.collect();
        int withoutRobot = maximizeGeode(bp, remainingTime - 1, currentMax);
        bp.unCollect();
        if (withoutRobot > currentMax) {
            currentMax = withoutRobot;
        }
        return currentMax;
    }

    public void printRobot(Robot robot) {
        System.out.println(robot.name + ":");
        System.out.println("  count: " + robot.count);
        System.out.println("  needed ore: " + robot.resourcesNeeded[oreId]);
        System.out.println("  needed clay: " + robot.resourcesNeeded[clayId]);
        System.out.println("  needed obsidian: " + robot.resourcesNeeded[obsidianId]);
        System.out.println("  needed geode: " + robot.resourcesNeeded[geodeId]);
    }

    private BluePrint readBluePrint(String line) {
        Matcher matcher = inputBlueprint.matcher(line);
        if (matcher.find()) {
            currentBlueprint = new BluePrint(Integer.valueOf(matcher.group(1)));
            currentBlueprint.oreRobot.resourcesNeeded[oreId] = Integer.valueOf(matcher.group(2));
            currentBlueprint.clayRobot.resourcesNeeded[oreId] = Integer.valueOf(matcher.group(3));
            currentBlueprint.obsidianRobot.resourcesNeeded[oreId] = Integer.valueOf(matcher.group(4));
            currentBlueprint.obsidianRobot.resourcesNeeded[clayId] = Integer.valueOf(matcher.group(5));
            currentBlueprint.geodeRobot.resourcesNeeded[oreId] = Integer.valueOf(matcher.group(6));
            currentBlueprint.geodeRobot.resourcesNeeded[obsidianId] = Integer.valueOf(matcher.group(7));
            printRobot(currentBlueprint.oreRobot);
            printRobot(currentBlueprint.clayRobot);
            printRobot(currentBlueprint.obsidianRobot);
            printRobot(currentBlueprint.geodeRobot);
            return currentBlueprint;
        } else throw new RuntimeException("unexpected input");
    }

    @Override
    protected long processLinePart1(String line) {
        currentBlueprint = readBluePrint(line);
        int max = maximizeGeode(currentBlueprint, 24, 0);
        return max * currentBlueprint.id;
    }

    @Override
    protected String endPart1() {
        return super.endPart1();
    }

    @Override
    protected String endPart2() {
        return super.endPart2();
    }

    int readBluePrintCount = 0;
    int multiplication = 1;
    @Override
    protected long processLinePart2(String line) {
        if (readBluePrintCount >= 3) return 0;

        readBluePrintCount++;
        currentBlueprint = readBluePrint(line);
        int max = maximizeGeode(currentBlueprint, 32, 0);
        multiplication *= max;
        if (readBluePrintCount == 3) return multiplication;
        return 0;
    }
}
