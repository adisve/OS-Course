package src.memory;

import java.util.ArrayDeque;

public class test {
    public static void main(String[] args) {
        ArrayDeque<Integer> deque = new ArrayDeque<Integer>(4);
        final int MAX = 4;
        for (int index = 0; index < 20; index++) {
            if(index > MAX) {
                deque.poll();
                System.out.println("New head: " + deque.getFirst());
                deque.add(index);
            } else {
                deque.add(index);
            }
            for (Integer integer : deque) {
                System.out.println(integer);
            }
        }
        /* for (Integer integer : deque) {
            System.out.println(integer);
        } */
    }
}
