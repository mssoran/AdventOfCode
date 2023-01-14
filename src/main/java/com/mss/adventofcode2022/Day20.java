package com.mss.adventofcode2022;

import java.util.HashMap;

public class Day20 extends DayBase {
    public Day20() {
        super(20);
    }

    public class Node {
        int moveCount;
        Node next;
        Node prev;

        public Node(int aMoveCount) {
            moveCount = aMoveCount;
        }
    }
    HashMap<Integer, Node> table = new HashMap<Integer, Node>();
    int currentKey = 0;
    // modeForRing* members are used for perf and clarity. Otherwise, they're not needed.
    int modForRing;
    int modForRingHalf;
    int minusModForRingHalf;
    Node prevNode = null;
    Node zeroNode = null;
    @Override
    protected long processLinePart1(String line) {
        Node currentNode = new Node(Integer.valueOf(line));
        if (currentNode.moveCount == 0) zeroNode = currentNode;
        currentNode.prev = prevNode;
        if (prevNode != null) {
            prevNode.next = currentNode;
        }
        prevNode = currentNode;
        table.put(currentKey, currentNode);
        currentKey++;
        return 0;
    }

    private void makeForwardMove(Node node, int moveCount) {
        // remove node from the ring
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // find where to put node
        Node putAfter = node.prev;
        for (int i = 0; i < moveCount; i++) {
            putAfter = putAfter.next;
        }
        // insert the node back to the ring
        node.next = putAfter.next;
        node.prev = putAfter;
        node.prev.next = node;
        node.next.prev = node;
    }

    private void makeBackwardMove(Node node, int moveCount) {
        // remove node from the ring
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // find where to put node
        Node putAfter = node.prev;
        for (int i = 0; i < moveCount; i++) {
            putAfter = putAfter.prev;
        }
        // insert the node back to the ring
        node.next = putAfter.next;
        node.prev = putAfter;
        node.prev.next = node;
        node.next.prev = node;
    }

    private void makeMoveForKey(int key, long decryptionKey) {
        Node node = table.get(key);
        // decide move count. I know modForRing is small, so it's ok to cast the result to int
        int moveCount = (int) ((decryptionKey * node.moveCount) % modForRing);
        if (moveCount > modForRingHalf) {
            moveCount -= modForRing;
        } else if (moveCount < minusModForRingHalf) {
            moveCount += modForRing;
        }

        if(moveCount > 0) {
            makeForwardMove(node, moveCount);
        } else if (moveCount < 0) {
            makeBackwardMove(node, -moveCount);
        }
    }

    private void printRing() {
        Node node = zeroNode;
        System.out.print(node.moveCount + "  ");
        node = node.next;
        while(node != zeroNode) {
            System.out.print(node.moveCount + "  ");
            node = node.next;
        }
        System.out.println();
    }

    private long decrypt(long decryptionKey, int numberOfApplication) {
        // finish building circular double linked list
        Node firstNode = table.get(0);
        firstNode.prev = prevNode;
        prevNode.next = firstNode;

        modForRing = currentKey - 1;
        modForRingHalf = modForRing / 2;
        minusModForRingHalf = -modForRingHalf;

        for(int applicationCount = 0; applicationCount < numberOfApplication; applicationCount++) {
            // for each Node, make the move
            for (int i = 0; i < currentKey; i++) {
                makeMoveForKey(i, decryptionKey);
            }
        }

        // Find 3 numbers
        Node node = zeroNode;
        int i = 0;
        long sum = 0;
        while (i < 1000) {
            node = node.next;
            i++;
        }
        sum += node.moveCount;
        while (i < 2000) {
            node = node.next;
            i++;
        }
        sum += node.moveCount;
        while (i < 3000) {
            node = node.next;
            i++;
        }
        sum += node.moveCount;

        return sum * decryptionKey;
    }

    @Override
    protected String endPart1() {
        long sum = decrypt(1, 1);
        return "Sum of 3 numbers is " + sum;
    }

    @Override
    protected String endPart2() {
        long sum = decrypt(811589153, 10);
        return "Sum of 3 numbers is " + sum;
    }

    @Override
    protected long processLinePart2(String line) {
        return processLinePart1(line);
    }
}
