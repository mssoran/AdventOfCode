package com.mss.adventofcode2022;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class Day18 extends DayBase {
    public Day18() {
        super(18);
    }

    class Position {
        int x;
        int y;
        int z;
        public Position (int aX, int aY, int aZ) {
            x = aX;
            y = aY;
            z = aZ;
        }

        public void add(Position other) {
            x += other.x;
            y += other.y;
            z += other.z;
        }

        public void subtract(Position other) {
            x -= other.x;
            y -= other.y;
            z -= other.z;
        }

        @Override
        public int hashCode() {
            return x << 14 + y << 7 + z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            if (obj instanceof Position) {
                Position pos = (Position) obj;
                return x == pos.x && y == pos.y && z == pos.z;
            }
            return false;
        }

        @Override
        public String toString() {
            return "("+x+","+y+","+z+")";
        }

        public Position clone() {
            return new Position(x, y ,z);
        }
    }

    Position[] directions = {
            new Position(1, 0, 0),
            new Position(-1, 0, 0),
            new Position(0, 1, 0),
            new Position(0, -1, 0),
            new Position(0, 0, 1),
            new Position(0, 0, -1),
    };

    Position[] outerNeighborDirections;

    HashSet<Position> cubes = new HashSet<>();
    Position anOuterCube = null;

    @Override
    protected long processLinePart1(String line) {
        String[] coords = line.split(",");
        if (coords == null || coords.length != 3) throw new RuntimeException("unexpected input. Cannot read coords");
        Position currentCube = new Position(Integer.valueOf(coords[0]),Integer.valueOf(coords[1]),Integer.valueOf(coords[2]));
        cubes.add(currentCube);
        // find a cube that is on the outer side for sure
        if(anOuterCube == null || currentCube.x < anOuterCube.x) anOuterCube = currentCube.clone();
        return 0;
    }

    @Override
    protected String endPart1() {
        // simply count not existing neighbors
        int count = 0;
        Iterator<Position> it = cubes.iterator();
        while (it.hasNext()) {
            Position cube = it.next().clone();
            int cubeCount = 0;
            for (int i = 0; i < directions.length; i++) {
                cube.add(directions[i]);
                if (!cubes.contains(cube)) {
                    cubeCount++;
                }
                cube.subtract(directions[i]);
            }
            count += cubeCount;
        }
        return "There are " + count + " not connected faces";
    }

    HashSet<Position> outerCubes = new HashSet<>();

    private boolean hasNeighborCube(Position candidate) {
        if (cubes.contains(candidate)) return false;
        Position pos = candidate.clone();
        for (int i = 0; i < outerNeighborDirections.length; i++) {
            pos.add(outerNeighborDirections[i]);
            if(cubes.contains(pos)) return true;
            pos.subtract(outerNeighborDirections[i]);
        }
        return false;
    }

    @Override
    protected String endPart2() {
        // init outerNeighborDirections
        LinkedList<Position> temp = new LinkedList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    if (i != 0 || j != 0 || k != 0) {
                       temp.addLast(new Position(i, j, k));
                    }
                }
            }
        }
        outerNeighborDirections = temp.toArray(new Position[0]);
        // start from anOuterCube and bfs and find all outer cubes
        LinkedList<Position> queue = new LinkedList<>();
        anOuterCube.add(directions[1]); // direction[1] is (-1, 0, 0)
        queue.add(anOuterCube.clone());
        outerCubes.add(anOuterCube);
        while (!queue.isEmpty()) {
            Position current = queue.removeFirst();
            for(int i = 0; i < directions.length; i++) {
                Position candidate = current.clone();
                candidate.add(directions[i]);
                if (hasNeighborCube(candidate) && outerCubes.add(candidate.clone())) {
                    queue.addLast(candidate.clone());
                }
            }
        }
        // count cubes, that has a outerCube neighbor
        int count = 0;
        Iterator<Position> it = cubes.iterator();
        while (it.hasNext()) {
            Position cube = it.next().clone();
            int cubeCount = 0;
            for (int i = 0; i < directions.length; i++) {
                cube.add(directions[i]);
                if (outerCubes.contains(cube)) {
                    cubeCount++;
                }
                cube.subtract(directions[i]);
            }
            count += cubeCount;
        }
        return "There are " + count + " outer faces";
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
