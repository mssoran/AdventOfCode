package com.mss.adventofcode2022;

import java.util.regex.Pattern;

public class Day10 extends DayBase {
    public Day10() {
        super(10);
    }

    class CRT {
        char[][] screen = {
                "........................................".toCharArray(),
                "........................................".toCharArray(),
                "........................................".toCharArray(),
                "........................................".toCharArray(),
                "........................................".toCharArray(),
                "........................................".toCharArray(),
        };

        public void set(int x, int y) {
            screen[x][y] = '#';
        }

        public void print() {
            System.out.println("-------------------------------------------");
            for (char[] line:  screen) {
                System.out.println(line);
            }
        }
    }
    int time = 1;
    int register = 1;
    CRT crt = new CRT();

    @Override
    protected long processLinePart1(String line) {
        int signalStrength = 0;
        if ((time - 20) % 40 == 0) {
            signalStrength = register * time;
        }
        if (line.equals("noop")){
            time++;
        } else {
            // line must be line addx NNNN
            int diff = Integer.valueOf(line.substring(5));
            time++;
            if ((time - 20) % 40 == 0) {
                signalStrength = register * time;
            }
            time++;
            register += diff;
        }

        return signalStrength;
    }

    @Override
    protected String endPart1() {
        return super.endPart1();
    }

    @Override
    protected String endPart2() {
        return super.endPart2();
    }

    private void setPixel() {
        int col = (time - 1) % 40;
        if(Math.abs(col - register) <= 1) {
            crt.set((time-1) / 40, col);
        }
    }
    @Override
    protected long processLinePart2(String line) {
        // now drawing pixel time-1
        setPixel();
        crt.print();
        if (line.equals("noop")){
            time++;
        } else {
            // line must be line addx NNNN
            int diff = Integer.valueOf(line.substring(5));
            time++;

            setPixel();
            crt.print();
            time++;
            register += diff;
        }

        return 0;
    }
}
