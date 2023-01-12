package com.mss.adventofcode2022;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 extends DayBase {
    public Day15() {
        super(15);
    }

    public class Interval {
        int start;
        int end;

        public Interval(int aStart, int aEnd) {
            start = aStart;
            end = aEnd;
        }

        public boolean containsStartOf(Interval other) {
            return (start <= other.start && other.start <= end);
        }

        public Interval copy() {
            return new Interval(this.start, this.end);
        }

        public boolean mergeWith(Interval other) {
            if (containsStartOf(other)) {
                if (end < other.end) end = other.end;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "(" + start + "," + end + ")";
        }
    }

    public class IntervalComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval o1, Interval o2) {
            if (o1.start - o2.start != 0) return o1.start - o2.start;
            return o1.end - o2.end;
        }
    }

    class Sensor {
        int x, y, bx, by;
        int beaconDistance;

        public Sensor(int aX, int aY, int aBx, int aBy) {
            x = aX;
            y = aY;
            bx = aBx;
            by = aBy;
            beaconDistance = distance(aX, aY, aBx, aBy);
        }

        public int distance(int aX, int aY, int aBx, int aBy) {
            return (aX < aBx ? aBx - aX : aX - aBx) + (aY < aBy ? aBy - aY : aY - aBy);
        }

        public void findAllInterval() {
            for (int i = 0; i < perYIntervals.length; i++) {
                Interval interval = findInterval(i);
                if (interval != null) perYIntervals[i].intervals.addLast(interval);
            }
        }

        public Interval findInterval(int aY) {
            int distanceToY = distance(x, y, x, aY);
            if (distanceToY > beaconDistance) return null;
            int diff = beaconDistance - distanceToY;
            return new Interval(x - diff, x + diff);
        }

        @Override
        public String toString() {
            return "Sensor(" + x + "," + y + " -> " + bx + "," + by + " with distance " + beaconDistance + ")";
        }
    }

    class PerYSolution {
        int y;
        LinkedList<Interval> intervals = new LinkedList<>();

        public PerYSolution(int aY) {
            y = aY;
        }

    }

    Pattern inputPattern = Pattern.compile("Sensor at x=(-?\\d*), y=(-?\\d*): closest beacon is at x=(-?\\d*), y=(-?\\d*)");

    LinkedList<Interval> intervals = new LinkedList<>();
    PerYSolution[] perYIntervals = null;

    LinkedList<Sensor> sensors = new LinkedList<>();
    HashSet<Integer> beaconsOnY = new HashSet<>();

    @Override
    protected long processLinePart1(String line) {
        Matcher matcher = inputPattern.matcher(line);
        if (matcher.find()) {
            Sensor sensor = new Sensor(
                    Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(2)),
                    Integer.valueOf(matcher.group(3)),
                    Integer.valueOf(matcher.group(4))
            );
            sensors.addLast(sensor);
            Interval interval = sensor.findInterval(2000000);
            if (sensor.by == 2000000) beaconsOnY.add(sensor.bx);
            System.out.println("For " + sensor + ", interval is: " + interval);
            if (interval != null) intervals.addLast(interval);
        } else {
            throw new RuntimeException("input is different than expected: " + line);
        }
        return 0;
    }

    public LinkedList<Interval> mergeIntervals(LinkedList<Interval> intervals) {
        // first sort
        Interval[] sortedIntervals = intervals.toArray(new Interval[0]);
        Arrays.sort(sortedIntervals, new IntervalComparator());
        // merge overlapping intervals
        LinkedList<Interval> merged = new LinkedList<>();
        if (sortedIntervals.length == 0) return merged;
        Interval nextInterval = sortedIntervals[0].copy();
        for (int i = 1; i < sortedIntervals.length; i++) {
            if (!nextInterval.mergeWith(sortedIntervals[i])) {
                // cannot merge, so I need to start a new interval
                merged.addLast(nextInterval);
                nextInterval = sortedIntervals[i].copy();
            }
        }
        merged.addLast(nextInterval);
        return merged;
    }

    @Override
    protected String endPart1() {
        LinkedList<Interval> mergedIntervals = mergeIntervals(intervals);
        int total = 0;
        Integer[] beaconLocs = beaconsOnY.toArray(new Integer[0]);
        Arrays.sort(beaconLocs);
        int beaconIndex = 0;
        int beaconCount = 0;
        for (Interval interval : mergedIntervals) {
            total += interval.end - interval.start + 1;
            while (beaconIndex < beaconLocs.length && beaconLocs[beaconIndex] <= interval.end) {
                if (interval.start <= beaconLocs[beaconIndex]) beaconCount++;
                beaconIndex++;
            }
        }
        System.out.println("Total " + total + " locations, but " + beaconCount + " beacons in these locations.");
        return "Beacon cannot be present in " + (total - beaconCount) + " locations";
    }

    @Override
    protected String endPart2() {
        for (int y = 0; y < perYIntervals.length; y++) {
            LinkedList<Interval> mergedIntervals = mergeIntervals(perYIntervals[y].intervals);
            // find the interval that has 0
            Iterator<Interval> it = mergedIntervals.iterator();
            // skipping boundary checks, assuming the input is always correct
            Interval next = it.next();
            while (next.end < 0) next = it.next();
            if (next.start == 1) return "missing (0," + y + ")";
            if (next.start > 0) throw new RuntimeException("something is wrong");
            if (next.end < perYIntervals.length)
                return "missing (" + (next.end + 1) + "," + y + ")  -> " + ((long)(next.end + 1) * (long)4000000 + (long)y);
        }
        return "!!!! Cannot find !!!!";
    }

    @Override
    protected long processLinePart2(String line) {
        if (perYIntervals == null) {
            perYIntervals = new PerYSolution[4000001];
            for (int y = 0; y < perYIntervals.length; y++) {
                perYIntervals[y] = new PerYSolution(y);
            }
        }

        Matcher matcher = inputPattern.matcher(line);
        if (matcher.find()) {
            Sensor sensor = new Sensor(
                    Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(2)),
                    Integer.valueOf(matcher.group(3)),
                    Integer.valueOf(matcher.group(4))
            );
            sensors.addLast(sensor);
            sensor.findAllInterval();
        } else {
            throw new RuntimeException("input is different than expected: " + line);
        }
        return 0;
    }
}
