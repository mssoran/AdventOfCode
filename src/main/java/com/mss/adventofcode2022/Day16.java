package com.mss.adventofcode2022;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 extends DayBase {
    public Day16() {
        super(16);
    }

    class Edge {
        public String toValve;
        public int len;

        public Edge(String aValve, int aLen) {
            toValve = aValve;
            len = aLen;
        }

        @Override
        public String toString() {
            return "("+toValve+"-"+len+")";
        }
    }

    class Valve {
        String name;
        long flowRate;
        LinkedList<Edge> neighbors = new LinkedList<>();

        public Valve(String aName, long aFlowRate, String aNeighbors) {
            name = aName;
            flowRate = aFlowRate;
            if (aNeighbors != null) {
                String[] nameList = aNeighbors.split(",");
                for (String valveName : nameList) {
                    String trimmedName = valveName.trim();
                    if (!trimmedName.isBlank()) {
                        neighbors.addLast(new Edge(trimmedName, 1));
                    }
                }
            }
        }
    }

    HashMap<String, Valve> gr = new HashMap<>();
    // shortest paths graph
    HashMap<String, Valve> sgr = new HashMap<>();
    HashSet<String> valves = new HashSet<>();

    Pattern inputPattern = Pattern.compile("Valve (\\w*) has flow rate=(\\d*); tunnels? leads? to valves? (.*)");

    @Override
    protected long processLinePart1(String line) {
        Matcher matcher = inputPattern.matcher(line);
        if (!matcher.find()) throw new RuntimeException("Input cannot be parsed");

        String name = matcher.group(1);
        long flowRate = Long.valueOf(matcher.group(2));
        Valve valve = new Valve(name, flowRate, matcher.group(3));
        gr.put(name, valve);
        if (name.equals("AA") || flowRate > 0) valves.add(name);

        return 0;
    }

    private Valve ensureSgr(String name) {
        if (!sgr.containsKey(name)) {
            // add a new valve without neighbors
            sgr.put(name, new Valve(name, gr.get(name).flowRate, null));
        }
        return sgr.get(name);
    }

    private void bfsFrom(Valve startingValve) {
        LinkedList<Valve> queue = new LinkedList<>();
        // startingValve is a valve from sgr, use the valve from gr
        queue.add(gr.get(startingValve.name));
        HashMap<String, Integer> visited = new HashMap<>();
        visited.put(startingValve.name, 0);
        while (!queue.isEmpty()) {
            Valve valve = queue.removeFirst();
            for (Edge neighbors : valve.neighbors) {
                if (!visited.containsKey(neighbors.toValve)) {
                    visited.put(neighbors.toValve, visited.get(valve.name) + 1);
                    queue.addLast(gr.get(neighbors.toValve));
                    if (valves.contains(neighbors.toValve)) {
                        // then we need to add to sgr, so use sgr valves
                        ensureSgr(neighbors.toValve);
                        startingValve.neighbors.addLast(new Edge(neighbors.toValve, visited.get(neighbors.toValve)));
                    }
                }
            }
        }
    }

    private void printGr(HashMap<String, Valve> g, String message) {
        System.out.println("Printing graph " + message + "--------------------------");
        g.forEach((name, valve) -> {
            System.out.print("   " + name + " (" + valve.flowRate + ")  : ");
            valve.neighbors.forEach((edge) -> System.out.print("(" + edge.toValve + " - " + edge.len + ")  "));
            System.out.println("");
        });
    }

    private void constructShortestPathsGraph() {
        for (String valveName : valves) {
            Valve valve =ensureSgr(valveName);
            remainingTotal += valve.flowRate;
            bfsFrom(valve);
        }
    }

    private long findMostPressure(Valve currentValve, int remainingTime, HashSet<String> openedValves, long totalPressure) {
        if (remainingTime <= 0) return totalPressure;
        // if all valves are open, there is nothing we can do, return total Pressure
        if (openedValves.size() == sgr.size()) return totalPressure;
        if (openedValves.contains(currentValve.name)) return totalPressure;

        openedValves.add(currentValve.name);
        long myPressure = currentValve.flowRate * (remainingTime - 1);
        long maxPressure = totalPressure + myPressure;

        for (Edge neighbor : currentValve.neighbors) {
            if (!openedValves.contains(neighbor.toValve) && remainingTime > neighbor.len + 1) {
                long neighborPressure = findMostPressure(sgr.get(neighbor.toValve), remainingTime - neighbor.len - 1, openedValves, totalPressure + myPressure);
                if (neighborPressure > maxPressure) maxPressure = neighborPressure;
            }
        }

        openedValves.remove(currentValve.name);
        return maxPressure;
    }

    @Override
    protected String endPart1() {
        constructShortestPathsGraph();
        printGr(gr, " original gr");
        printGr(sgr, "Shortest paths sgr");
        // start from AA and find the most pressure
        HashSet<String> openedValves = new HashSet<>();
        long mostPressure = findMostPressure(sgr.get("AA"), 31, openedValves, 0);
        return "most pressure is " + mostPressure;
    }

    class MovingState {
        public Valve fromValve;
        public Valve toValve;
        public int timeToValve;


        public MovingState(Valve aFromValve) {
            fromValve = aFromValve;
        }

        public MovingState(Valve aFromValve, Edge aNeighbor) {
            fromValve = aFromValve;
            toValve = sgr.get(aNeighbor.toValve);
            timeToValve = aNeighbor.len;
        }

        public MovingState newByMoving() {
            return new MovingState(toValve);
        }

        public boolean atDestination() {
            if (timeToValve < 0) throw new RuntimeException("timeToValve cannot be less than 0");
            return hasDestination() && timeToValve <= 0;
        }

        public boolean hasDestination() {
            return toValve != null;
        }

        public void advanceTime(int aTime) {
            if(toValve!=null)timeToValve -= aTime;
        }
    }

    class Step {
        public int remaining;
        public String action;
        public Step(int aRemaining, String aAction) {
            remaining = aRemaining;
            action = aAction;
        }
    }

    private void printRecursion(MovingState meState, MovingState elephantState, int remainingTime, HashSet<String> openedValves, HashSet<String> scheduledValves, long totalPressure) {
//        if(remainingTime < 0) return;
//        for(int i = 0; i < 32 - remainingTime; i++) System.out.print(" ");
//        System.out.print(remainingTime + " ticking... ");
//        System.out.print("me->"+meState.fromValve.name+"->"+(meState.toValve == null?"null":meState.toValve.name)+"("+meState.timeToValve+")  ");
//        System.out.print("elephant->"+elephantState.fromValve.name+"->"+(elephantState.toValve == null?"null":elephantState.toValve.name)+"("+elephantState.timeToValve+")  ");
//        System.out.print("opened list:");
//        openedValves.forEach( (valve) -> System.out.print(valve+"  "));
//        System.out.print("scheduled list:");
//        scheduledValves.forEach( (valve) -> System.out.print(valve+"  "));
//        System.out.println("Total:"+totalPressure);
    }
    private void printReturn(LinkedList<Step> sol, MovingState meState, MovingState elephantState, int remainingTime, String message, long totalPressure) {
//        for(int i = 0; i < 32 - remainingTime; i++) System.out.print(" ");
//        System.out.print(remainingTime + " ticking... --------------returning:"+message+"-------------- ");
//        System.out.print("me->"+meState.fromValve.name+"->"+(meState.toValve == null?"null":meState.toValve.name)+"("+meState.timeToValve+")  ");
//        System.out.print("elephant->"+elephantState.fromValve.name+"->"+(elephantState.toValve == null?"null":elephantState.toValve.name)+"("+elephantState.timeToValve+")  Solution: ");
//        sol.forEach(step -> System.out.print("at "+step.remaining+":"+step.action+" "));
//        System.out.println(" Total:"+totalPressure);
    }

    long maxKnown = -1;
    long remainingTotal = 0;

    private long findMostPressureWithElephant(LinkedList<Step> sol, MovingState meState, MovingState elephantState, int remainingTime, HashSet<String> openedValves, HashSet<String> scheduledValves, long totalPressure) {
        //printRecursion(meState, elephantState, remainingTime, openedValves, scheduledValves, totalPressure);
        if (maxKnown >= totalPressure + remainingTotal * (remainingTime-1)) {
//            System.out.println("prunning...");
            return 0;
        }
        if (remainingTime <= 0)
        {
            printReturn(sol, meState, elephantState, remainingTime, "no remaining time", totalPressure);
            if (maxKnown < totalPressure) maxKnown = totalPressure;
            return totalPressure;
        }
        // if all valves are open, there is nothing we can do, return total Pressure
        if (openedValves.size() == sgr.size()) {
            printReturn(sol, meState, elephantState, remainingTime, "all valves are open", totalPressure);
            if (maxKnown < totalPressure) maxKnown = totalPressure;
            return totalPressure;
        }

        ///////////////////////////////////////////////////////////////
        // if any doesn't have toValve, try all neighbors
        if(!meState.hasDestination()) {
//            System.out.println("me doesn't have destination");
            // hack if all are scheduled
            if(scheduledValves.size() == sgr.size()) {
                MovingState newMeState = new MovingState(meState.fromValve, new Edge("AA", 100));
                sol.addLast(new Step(remainingTime, "hack meState"));
                long result = findMostPressureWithElephant(sol, newMeState, elephantState, remainingTime, openedValves, scheduledValves, totalPressure);
                printReturn(sol, meState, elephantState, remainingTime, "elephant doesn't have destination", result);
                sol.removeLast();
                return result;
            }
            long maxTotal = totalPressure;
            for (Edge neighbor : meState.fromValve.neighbors) {
                if(!scheduledValves.contains(neighbor.toValve)) {
                    MovingState newMeState = new MovingState(meState.fromValve, neighbor);
                    scheduledValves.add(neighbor.toValve);
                    sol.addLast(new Step(remainingTime, "pick m-neighbor "+neighbor));
                    long sub = findMostPressureWithElephant(sol, newMeState, elephantState, remainingTime, openedValves, scheduledValves, totalPressure);
                    sol.removeLast();
                    if(sub > maxTotal) maxTotal = sub;
                    scheduledValves.remove(neighbor.toValve);
                }
            }
            printReturn(sol, meState, elephantState, remainingTime, "me doesn't have destination", maxTotal);
            return maxTotal;
        } else if(!elephantState.hasDestination()) {
//            System.out.println("elephant doesn't have destination");
            // hack if all are scheduled
            if(scheduledValves.size() == sgr.size()) {
                MovingState newElephantState = new MovingState(elephantState.fromValve, new Edge("AA", 100));
                sol.addLast(new Step(remainingTime, "hack elephantState"));
                long result = findMostPressureWithElephant(sol, meState, newElephantState, remainingTime, openedValves, scheduledValves, totalPressure);
                printReturn(sol, meState, elephantState, remainingTime, "elephant doesn't have destination", result);
                sol.removeLast();
                return result;
            }
            long maxTotal = totalPressure;
            for (Edge neighbor : elephantState.fromValve.neighbors) {
                if(!scheduledValves.contains(neighbor.toValve)) {
                    MovingState newElephantState = new MovingState(elephantState.fromValve, neighbor);
                    scheduledValves.add(neighbor.toValve);
                    sol.addLast(new Step(remainingTime, "pick e-neighbor "+neighbor));
                    long sub = findMostPressureWithElephant(sol, meState, newElephantState, remainingTime, openedValves, scheduledValves, totalPressure);
                    sol.removeLast();
                    if(sub > maxTotal) maxTotal = sub;
                    scheduledValves.remove(neighbor.toValve);
                }
            }
            printReturn(sol, meState, elephantState, remainingTime, "elephant doesn't have destination", maxTotal);
            return maxTotal;
            // if at destination, just open valves and call again with remaining-1
        } else if(meState.atDestination() || elephantState.atDestination()) {
//            System.out.println("one is at destination");
            long addedPressure = 0;
            MovingState newMeState = meState;
            MovingState newElephantState = elephantState;
            StringBuilder action = new StringBuilder();
            boolean meWasAtDest = false;
            boolean elWasAtDest = false;
            if (meState.atDestination() && elephantState.atDestination())
            {
                addedPressure += meState.toValve.flowRate * (remainingTime - 1);
                addedPressure += elephantState.toValve.flowRate * (remainingTime - 1);
                remainingTotal -= meState.toValve.flowRate + elephantState.toValve.flowRate;
                openedValves.add(meState.toValve.name);
                openedValves.add(elephantState.toValve.name);
                newMeState = meState.newByMoving();
                newElephantState = elephantState.newByMoving();
                meWasAtDest = true;
                elWasAtDest = true;
                action.append("me open "+meState.toValve.name+" el open "+elephantState.toValve.name);
            } else if (meState.atDestination()) {
                addedPressure += meState.toValve.flowRate * (remainingTime - 1);
                remainingTotal -= meState.toValve.flowRate;
                openedValves.add(meState.toValve.name);
                newMeState = meState.newByMoving();
                elephantState.advanceTime(1);
                meWasAtDest = true;
                action.append("me open "+meState.toValve.name);
            } else if (elephantState.atDestination()) {
                addedPressure += elephantState.toValve.flowRate * (remainingTime - 1);
                remainingTotal -= elephantState.toValve.flowRate;
                openedValves.add(elephantState.toValve.name);
                newElephantState = elephantState.newByMoving();
                meState.advanceTime(1);
                elWasAtDest = true;
                action.append(" el open "+elephantState.toValve.name);
            } else throw new RuntimeException("how so");
            sol.addLast(new Step(remainingTime, "dest "+action));
            long result = findMostPressureWithElephant(sol, newMeState, newElephantState, remainingTime - 1, openedValves, scheduledValves, totalPressure + addedPressure);

            if (meWasAtDest && elWasAtDest)
            {
                if(!meState.toValve.name.equals("AA")) openedValves.remove(meState.toValve.name);
                if(!elephantState.toValve.name.equals("AA")) openedValves.remove(elephantState.toValve.name);
                remainingTotal += meState.toValve.flowRate + elephantState.toValve.flowRate;
            } else if (meWasAtDest) {
                if(!meState.toValve.name.equals("AA")) openedValves.remove(meState.toValve.name);
                elephantState.advanceTime(-1);
                remainingTotal += meState.toValve.flowRate;
            } else if (elWasAtDest) {
                if(!elephantState.toValve.name.equals("AA")) openedValves.remove(elephantState.toValve.name);
                meState.advanceTime(-1);
                remainingTotal += elephantState.toValve.flowRate;
            }
            printReturn(sol, meState, elephantState, remainingTime, "one is at destination", result);
            sol.removeLast();
            return result;
        // if both has toValve, find min len, advance, and call again with remaining-len
        } else if(meState.hasDestination() && elephantState.hasDestination()) {
//            System.out.println("both have destination");
            int moveTime = meState.timeToValve;
            if(moveTime > elephantState.timeToValve) moveTime = elephantState.timeToValve;
            meState.advanceTime(moveTime);
            elephantState.advanceTime(moveTime);
            sol.addLast(new Step(remainingTime, "move "+moveTime));
            long result = findMostPressureWithElephant(sol, meState, elephantState, remainingTime - moveTime, openedValves, scheduledValves, totalPressure);
            meState.advanceTime(-moveTime);
            elephantState.advanceTime(-moveTime);
            printReturn(sol, meState, elephantState, remainingTime, "both have destination", result);
            sol.removeLast();
            return result;
        } else {
            throw new RuntimeException("Unexpected to be here!");
        }
        ///////////////////////////////////////////////////////////////
    }

    @Override
    protected String endPart2() {
        constructShortestPathsGraph();
        printGr(gr, " original gr");
        printGr(sgr, "Shortest paths sgr");
        // start from AA and find the most pressure
        HashSet<String> openedValves = new HashSet<>();
        HashSet<String> scheduledValves = new HashSet<>();
        scheduledValves.add("AA");
        MovingState meState = new MovingState(sgr.get("AA"), new Edge("AA", 0));
        MovingState elephantState = new MovingState(sgr.get("AA"), new Edge("AA", 0));
        LinkedList<Step> sol = new LinkedList<>();
        long mostPressure = findMostPressureWithElephant(sol, meState, elephantState, 27, openedValves, scheduledValves, 0);
        return "most pressure is " + mostPressure;
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
