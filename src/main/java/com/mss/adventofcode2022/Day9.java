package com.mss.adventofcode2022;

import java.util.HashMap;
import java.util.HashSet;

public class Day9 extends DayBase {
    public Day9() {
        super(9);
        visited.add(tail.toString());
        for(int i = 0; i < rope.length; i++) {
            rope[i] = new Position();
        }
    }

    HashSet<String> visited = new HashSet<String>();

    class Position {
        public int x = 0;
        public int y = 0;

        public void moveUp() { y += 1; }
        public void moveDown() { y -= 1;}
        public void moveRight() { x += 1;}
        public void moveLeft() { x -= 1;}

        public void move(String line, Position tail) {
            String[] parts = line.split(" ");
            int count = Integer.valueOf(parts[1]);
            while (count > 0) {
                switch (parts[0]) {
                    case "U":
                        moveUp();
                        break;
                    case "D":
                        moveDown();
                        break;
                    case "R":
                        moveRight();
                        break;
                    case "L":
                        moveLeft();
                        break;
                    default:
                        throw new RuntimeException("unknown move");
                }
                tail.moveTowards(this);
                visited.add(tail.toString());
                count --;
            }
        }

        public void move2(String line, Position[] rope) {
            String[] parts = line.split(" ");
            int count = Integer.valueOf(parts[1]);
            while (count > 0) {
                switch (parts[0]) {
                    case "U":
                        moveUp();
                        break;
                    case "D":
                        moveDown();
                        break;
                    case "R":
                        moveRight();
                        break;
                    case "L":
                        moveLeft();
                        break;
                    default:
                        throw new RuntimeException("unknown move");
                }
                rope[0].moveTowards(this);
                for(int i = 1; i < rope.length; i++) {
                    rope[i].moveTowards(rope[i-1]);
                }
                visited.add(rope[rope.length - 1].toString());
                count --;
            }
        }

        public void moveTowards(Position other) {
            if (other.x == this.x && other.y == this.y) return;
            if (other.x == this.x) {
                if (this.y > other.y) {
                    if (this.y - other.y > 1) this.y--;
                } else {
                    if (other.y - this.y > 1) this.y++;
                }
            } else if (other.y == this.y) {
                if (this.x > other.x) {
                    if (this.x - other.x > 1) this.x--;
                } else {
                    if (other.x - this.x > 1) this.x++;
                }
            } else {
                if (this.x - other.x > 1 || other.x - this.x > 1 || this.y - other.y > 1 || other.y - this.y > 1) {
                    if(this.x > other.x) this.x--; else this.x++;
                    if(this.y > other.y) this.y--; else this.y++;
                }
            }
        }

        @Override
        public String toString() {
            return x + "," + y;
        }
    }

    Position head = new Position();
    Position tail = new Position();

    Position[] rope = new Position[9];

    @Override
    protected long processLinePart1(String line) {
        head.move(line, tail);
        return 0;
    }

    @Override
    protected String endPart1() {
        return visited.size() + "";
    }

    @Override
    protected String endPart2() {
        return visited.size() + "";
    }

    @Override
    protected long processLinePart2(String line) {
        head.move2(line, rope);
        return 0;
    }
}
