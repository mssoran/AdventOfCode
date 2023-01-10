package com.mss.adventofcode2022;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class Day13 extends DayBase {
    public Day13() {
        super(13);
    }

    class MyList {
        public int val;
        public LinkedList<MyList> list = null;

        public MyList(int aVal) {
            val = aVal;
        }

        public MyList(MyList aElement) {
            list = new LinkedList<>();
            list.addLast(aElement);
        }

        public int compare(MyList other) {
            if(this.list == null && other.list == null) {
                return this.val - other.val;
            }

            if(this.list == null) {
                MyList newList = new MyList(this);
                return newList.compare(other);
            }

            if(other.list == null) {
                MyList newList = new MyList(other);
                return this.compare(newList);
            }

            // If I'm here, both has a list, compare each element
            Iterator<MyList> thisIt = this.list.iterator();
            Iterator<MyList> otherIt = other.list.iterator();
            while(thisIt.hasNext() && otherIt.hasNext()) {
                int subComp = thisIt.next().compare(otherIt.next());
                if (subComp != 0) return subComp;
            }
            if(!thisIt.hasNext() && !otherIt.hasNext()) return 0;
            if(!thisIt.hasNext()) return -1;
            return 1;
        }
    }

    class MyListComparator implements Comparator<MyList> {
        public int compare(MyList a, MyList b)
        {
            return a.compare(b);
        }
    }

    class ParseResult {
        public MyList list;
        public int nextIndex;
        public ParseResult(MyList aList, int aNextIndex) {
            list = aList;
            nextIndex = aNextIndex;
        }
    }

    public ParseResult parse(String line, int aIndex) {
        int index = aIndex;
        MyList result = new MyList(0);
        // skip whitespaces
        while(Character.isWhitespace(line.charAt(index))) index++;

        // if it's a list, must start with [
        if(line.charAt(index) == '[') {
            // parse a list
            index++;
            result.list = new LinkedList<>();
            // skip whitespaces
            while(Character.isWhitespace(line.charAt(index))) index++;

            while(line.charAt(index) != ']') {
                ParseResult subResult = parse(line, index);
                result.list.addLast(subResult.list);
                index = subResult.nextIndex;

                // skip any whitespace or ,
                while (Character.isWhitespace(line.charAt(index)) || line.charAt(index) == ',') index++;
            }
            index++;
            return new ParseResult(result, index);
        }
        // if not list, there must be just one value
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(line.charAt(index)))sb.append(line.charAt(index++));
        result.val = Integer.valueOf(sb.toString());

        return new ParseResult(result, index);
    }

    int index = 1;
    MyList firstList = null;
    @Override
    protected long processLinePart1(String line) {
        if (line.isBlank()) return 0;
        index++;
        ParseResult pr = parse(line, 0);

        if (firstList == null) {
            firstList = pr.list;
            return 0;
        } else {
            if(firstList.compare(pr.list) < 0) {
                firstList = null;
                return index / 2;
            }
            firstList = null;
        }
        return 0;
    }

    @Override
    protected String endPart1() {
        return super.endPart1();
    }

    @Override
    protected String endPart2() {
        MyList divider1 = new MyList(new MyList(new MyList(2)));
        MyList divider2 = new MyList(new MyList(new MyList(6)));
        lists.addLast(divider1);
        lists.addLast(divider2);
        MyList[] sorted = lists.toArray(new MyList[0]);
        Arrays.sort(sorted, new MyListComparator());
        int firstIndex = -1;
        for(int i = 0; i<sorted.length; i++) {
            if(firstIndex == -1) {
                if (sorted[i].compare(divider1) == 0) firstIndex = i + 1;
            } else {
                if (sorted[i].compare(divider2) == 0)
                    return "result is:" + (firstIndex * (i+1));
            }
        }
        throw new RuntimeException("Cannot find a result");
    }

    LinkedList<MyList> lists = new LinkedList<>();
    @Override
    protected long processLinePart2(String line) {
        if (line.isBlank()) return 0;
        ParseResult pr = parse(line, 0);
        lists.add(pr.list);
        return 0;
    }
}
