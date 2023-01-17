package com.mss.adventofcode2022;

public class Day25 extends DayBase {
    public Day25() {
        super(25);
    }

    class Snafu {
        String strVal;
        long intVal;

        Snafu(long aIntVal) {
            intVal = aIntVal;

            StringBuilder sb = new StringBuilder();
            long val = aIntVal;
            int digit;
            while(val > 0) {
                digit = (int)(val % 5);
                System.out.print("val:" + val + " digit:" + digit);
                switch (digit) {
                    case 0:
                    case 1:
                    case 2:
                        sb.append(digit);
                        break;
                    case 3:
                        sb.append("=");
                        val += 2;
                        break;
                    case 4:
                        sb.append("-");
                        val += 1;
                        break;
                }

                val = val / 5;
                System.out.println(" string up to here:" + sb + " new Val:" + val);
            }
            strVal = sb.reverse().toString();
        }

        Snafu (String aStrVal) {
            strVal = aStrVal;

            long val = 0;
            long mul = 1;
            for (int i = strVal.length() - 1; i >= 0; i--) {
                char digit = strVal.charAt(i);
                switch (digit) {
                    case '0':
                    case '1':
                    case '2':
                        val += mul * (digit - '0');
                        break;
                    case '=':
                        val -= mul * 2;
                        break;
                    case '-':
                        val -= mul;
                        break;
                    default:
                        throw new RuntimeException("Unknown char '" + digit +"' in string '" + strVal + "'");
                }
                mul *= 5;
            }
            intVal = val;
        }
    }

    long total = 0;
    @Override
    protected long processLinePart1(String line) {
        Snafu snafu = new Snafu(line);
        total += snafu.intVal;

        return snafu.intVal;
    }

    @Override
    protected String endPart1() {
        return "sum is " + (new Snafu(total)).strVal;
    }

    @Override
    protected String endPart2() {
        return super.endPart2();
    }

    @Override
    protected long processLinePart2(String line) {
        return 0;
    }
}
