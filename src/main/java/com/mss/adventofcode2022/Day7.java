package com.mss.adventofcode2022;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day7 extends DayBase {
    public Day7() {
        super(7);
    }

    class Dir {
        public String name = "/";
        public int size = 0;
        public LinkedList<Dir> children = new LinkedList<>();
    }

    Dir root = new Dir();
    LinkedList<Dir> currentPath = new LinkedList<>();


    Pattern filePattern = Pattern.compile("(\\d*) (.*)");

    private Dir findDirInCurrentDir(String name) {
        Dir dir = currentPath.peekLast();
        Iterator<Dir> it = dir.children.iterator();
        Dir child = null;
        while (it.hasNext() && child == null) {
            Dir next = it.next();
            if (next.name.equals(name)) child = next;
        }
        // if not found, add it to the current dir
        if (child == null) {
            Dir newDir = new Dir();
            newDir.name = name;
            dir.children.addLast(newDir);
            child = newDir;
        }
        return child;
    }

    @Override
    protected long processLinePart1(String line) {
        if (line.equals("$ cd /")) {
            currentPath.clear();
            currentPath.addLast(root);
        } else if (line.equals("$ cd ..")) {
            currentPath.removeLast();
        } else if (line.equals("$ ls")) {
            // do nothing
        } else if (line.startsWith("dir")) {
            // do nothing
        } else if (line.startsWith("$ cd ")) {
            String dirName = line.substring(5);
            // add to the path
            currentPath.addLast(findDirInCurrentDir(dirName));
        } else {
            Matcher matcher = filePattern.matcher(line);
            if (matcher.find()) {
                int size = Integer.valueOf(matcher.group(1));
                String fileName = matcher.group(2);
                Dir file = findDirInCurrentDir(fileName);
                file.size = size;
                return size;
            } else {
                throw new RuntimeException("cannot parse input line");
            }
        }
        return 0;
    }

    class SumInfo {
        public long mySize = 0;
        public long sizeSum = 0;
        public long minOverThreshold = Integer.MAX_VALUE;
    }

    private SumInfo findAll(Dir currentDir, long threshold) {
        if (currentDir.children.isEmpty()) {
            SumInfo sumInfo = new SumInfo();
            sumInfo.mySize = currentDir.size;
            System.out.println("file:" + currentDir.name + "-- mySize:" + sumInfo.mySize + "-- sizeSum:" + sumInfo.sizeSum);
            return sumInfo;
        }

        long sizeSum = 0;
        long mySize = currentDir.size;
        long minOverThreshold = Integer.MAX_VALUE;
        Iterator<Dir> it = currentDir.children.iterator();
        while (it.hasNext()) {
            Dir child = it.next();
            SumInfo sumInfo = findAll(child, threshold);
            sizeSum += sumInfo.sizeSum;
            mySize += sumInfo.mySize;
            if (sumInfo.minOverThreshold < minOverThreshold) minOverThreshold = sumInfo.minOverThreshold;
        }
        if (mySize <= 100000) {
            sizeSum += mySize;
        }
        if (mySize > threshold && mySize < minOverThreshold) minOverThreshold = mySize;

        System.out.println("Dir:" + currentDir.name + "-- mySize:" + mySize + "-- sizeSum:" + sizeSum);
        SumInfo sumInfo = new SumInfo();
        sumInfo.mySize = mySize;
        sumInfo.sizeSum = sizeSum;
        sumInfo.minOverThreshold = minOverThreshold;
        return sumInfo;
    }

    @Override
    protected String endPart1() {
        // Tree is parsed. Now find all directories of size 100000 or less
        SumInfo sumInfo = findAll(root, 0);
        return sumInfo.sizeSum + "";
    }

    @Override
    protected String endPart2() {
        // first find threshold
        SumInfo sumInfo = findAll(root, 0);
        long totalSize = 70000000;
        long neededSize = 30000000;
        long usedSize = sumInfo.mySize;
        long threshold = neededSize - (totalSize - usedSize);
        System.out.println("threshold is "+threshold);
        // now find min over threshold
        SumInfo sumInfo2 = findAll(root, threshold);
        return sumInfo2.minOverThreshold + "";
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
