import com.alibaba.fastjson.JSON;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class test {

    public static void main(String[] args) {
//        TestPojo testPojo = new TestPojo();
//        testPojo.setCode(10);
//        testPojo.setData(10);
//        byte[] bytes = JSON.toJSONBytes(testPojo);
//        System.out.println(new String(bytes));
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((o1, o2) -> {
                System.out.println(o1 + "," + o2);
                return o2 - o1;
        });
        priorityQueue.add(10);
        priorityQueue.add(1);
        priorityQueue.add(20);
        System.out.println(priorityQueue.remove());
    }

}
