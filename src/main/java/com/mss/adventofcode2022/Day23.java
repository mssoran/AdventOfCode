package com.mss.adventofcode2022;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Day23 extends DayBase {

    // neighbors are kept as a bitmask. For example:
    // 123
    // 4#5
    // 678
    // is binary 12345678

    public Day23() {
        super(23);
        // add all the movements
        // North:
        movements.add(new Movement(0b011100000, new Location(-1, 0)));
        // South:
        movements.add(new Movement(0b000000111, new Location(1, 0)));
        // West:
        movements.add(new Movement(0b010010100, new Location(0, -1)));
        // East:
        movements.add(new Movement(0b000101001, new Location(0, 1)));
    }

    class Movement {
        int mask;
        Location move;
        Movement(int aMask, Location aMove) {
            mask = aMask;
            move = aMove;
        }
    }

    class Location {
        final int x;
        final int y;

        Location(int aX, int aY) {
            x = aX;
            y = aY;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Location)) return false;
            Location other = (Location) o;
            return other.x == x && other.y == y;
        }

        public int hashCode() {
            return x << 16 + y;
        }

        public String toString() {
            return String.format("(%d, %d)", x, y);
        }

        Location add(Location other) {
            return new Location(x + other.x, y + other.y);
        }
    }


    HashSet<Location> currentLocations = new HashSet<>();
    int readingRow = 0;

    LinkedList<Movement> movements = new LinkedList<>();

    @Override
    protected long processLinePart1(String line) {
        int elfCount = 0;
        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == '#') {
                currentLocations.add(new Location(readingRow, i));
            }
        }
        readingRow++;
        return elfCount;
    }

    Location[] neighborLocations = {
            new Location(-1, -1),
            new Location(-1, 0),
            new Location(-1, 1),
            new Location(0, -1),
            new Location(0, 1),
            new Location(1, -1),
            new Location(1, 0),
            new Location(1, 1),
    };

    private int neighbors(Location loc) {
        int result = 0;
        for (int i = 0; i < neighborLocations.length; i++) {
            result <<= 1;
            if(currentLocations.contains(loc.add(neighborLocations[i]))) {
                result |= 1;
            }
        }
        return result;
    }

    private boolean makeOneMove() {
        HashMap<Location, Location> proposals = new HashMap<>();
        HashMap<Location, Integer> locationCount = new HashMap<>();
        int movementCount = 0;

        for (Location loc : currentLocations) {
            int n = neighbors(loc);
            // if there are no neighbors, elf doesn't move, so no proposals.
            if (n != 0) {
                // find a proposal
                Iterator<Movement> it = movements.iterator();
                boolean notFound = true;
                while (notFound && it.hasNext()) {
                    Movement movement = it.next();
                    if ((movement.mask & n) == 0) {
                        Location proposedLoc = loc.add(movement.move);
                        proposals.put(loc, proposedLoc);
                        locationCount.put(proposedLoc, locationCount.getOrDefault(proposedLoc, 0) + 1);
                        notFound = false;
                        movementCount++;
                    }
                }
                if (notFound) {
                    proposals.put(loc, loc);
                }
            } else {
                proposals.put(loc, loc);
            }
        }

        // all proposals are found, now we can make movements
        HashSet<Location> newLocations = new HashSet<>();
        proposals.forEach((loc, proposedLoc) -> {
                    if (locationCount.getOrDefault(proposedLoc, 0) <= 1) {
                        newLocations.add(proposedLoc);
                    } else {
                        newLocations.add(loc);
                    }
                }
        );

        // update the movement order
        Movement firstMovement = movements.removeFirst();
        movements.addLast(firstMovement);

        currentLocations = newLocations;

        return movementCount != 0;
    }

    @Override
    protected String endPart1() {

        for(int round = 0; round < 10; round ++) {
            makeOneMove();

//            System.out.println("round:" + round);
//            currentLocations.forEach( (loc) ->
//                    System.out.println("-- " + loc));
        }

        // find the value
        int minx = 100000;
        int maxx = -100000;
        int miny = 100000;
        int maxy = -100000;

        for(Location loc : currentLocations.toArray(new Location[0]) ) {
                    if (loc.x < minx) minx = loc.x;
                    if (loc.x > maxx) maxx = loc.x;
                    if (loc.y < miny) miny = loc.y;
                    if (loc.y > maxy) maxy = loc.y;
                }

        return "Total empty:" + ((maxx - minx + 1) * (maxy - miny + 1) - currentLocations.size());
    }

    @Override
    protected String endPart2() {
        int round = 0;
        while (true) {
            round++;
            if(!makeOneMove())
                return "No movement at round " + round;
        }
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
