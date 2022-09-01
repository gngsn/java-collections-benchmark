package benchmark;

import java.util.*;

public class BenchmarkClient {

    static long TIMEOUT = 15_000;

    static int POPULATE_SIZE = 100_000;

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            // Standard Collections List, Set, Queue Interfaces Time Benchmark
            CollectionsBenchmark collectionsBenchmark = new CollectionsBenchmark(TIMEOUT, POPULATE_SIZE);
            collectionsBenchmark.run(ArrayList.class);
            collectionsBenchmark.run(LinkedList.class);
            collectionsBenchmark.run(HashSet.class);
            collectionsBenchmark.run(LinkedHashSet.class);
            collectionsBenchmark.run(TreeSet.class);
            collectionsBenchmark.run(PriorityQueue.class);
            collectionsBenchmark.run(ArrayDeque.class);
            collectionsBenchmark.drawChart();


            // Standard Collections List, Set, Queue Interfaces Memory Benchmark
            List<Class<? extends Collection>> classes = new ArrayList<>(){{
                add(ArrayList.class);
                add(LinkedList.class);
                add(PriorityQueue.class);
                add(ArrayDeque.class);
            }};

            collectionsBenchmark.runMemoryBench(classes);
            collectionsBenchmark.displayMemoryResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
