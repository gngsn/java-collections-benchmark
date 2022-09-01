/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package benchmark;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Collections Benchmark
 *
 * @author Leo Lewis & Kaan Keskin
 */
public class CollectionsBenchmark {

    /**
     * Time in ms after which the CollectionsBenchmark task is considered timeout and is
     * stopped
     */
    private long timeout;

    /**
     * Is the given CollectionsBenchmark task timeout
     */
    private volatile boolean isTimeout;

    /**
     * Number of elements to populate the collection on which the CollectionsBenchmark will
     * be launched
     */
    private int populateSize;

    /**
     * Collection implementation to be tested
     */
    private Collection<String> collection;

    /**
     * List implementation to be tested
     */
    private List<String> list;

    /**
     * Default context used for each Collections Benchmark test (will populate the tested
     * collection before launching the bench)
     */
    private List<String> defaultListCtx;

    /**
     * Collections Benchmark results
     */
    private Map<String, Map<Class<? extends Collection<?>>, Long>> colBenchResults;

    /**
     * Collections Memory results
     */
    private Map<Class<? extends Collection<?>>, Long> colMemoryResults;


    /**
     * Constructor
     *
     * @param timeout
     * @param populateSize
     */
    public CollectionsBenchmark(long timeout, int populateSize) {
        this.timeout = timeout;
        this.populateSize = populateSize;

        defaultListCtx = new ArrayList<>();

        for (int i = 0; i < populateSize; i++) {
            defaultListCtx.add(Integer.toString(i % 100));
        }

        colBenchResults = new HashMap<>();
        colMemoryResults = new HashMap<>();
    }

    /**
     * Run the Collections Interface Benchmark on the given collection
     *
     * @param collectionClass the collection (if it's a List, some additional
     *                         bench will be done)
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void run(Class<? extends Collection> collectionClass) {
        try {
            long startTime = System.currentTimeMillis();
            Constructor<? extends Collection> constructor = collectionClass.getDeclaredConstructor((Class<?>[]) null);
            constructor.setAccessible(true);
            collection = (Collection<String>) constructor.newInstance();
            System.out.printf("Performances of %s populated with %s element(s)\n", collection.getClass().getCanonicalName(), populateSize);
            System.out.println("~".repeat(55));

            // Test List Collection used in Benchmark cases
            final Collection<String> colTest = IntStream.range(0, 1_000).boxed().map(i -> Integer.toString(i % 30)).collect(Collectors.toList());

            // Standard Collection List, Set and Queue Interface Benchmark
            if (collection instanceof List || collection instanceof Set || collection instanceof Queue) {

                execute(i -> collection.add(Integer.toString(i % 29)), populateSize, String.format("add %d elements", populateSize));
                execute(i -> collection.remove(Integer.toString(i)), Math.max(1, populateSize / 10), String.format("remove %d elements", populateSize));
                execute(i -> collection.addAll(colTest), Math.min(populateSize, 1000), String.format("addAll %d times %d elements", Math.min(populateSize, 1000), colTest.size()));
                execute(i -> collection.contains(Integer.toString(collection.size() - i - 1)), Math.min(populateSize, 1000), String.format("contains %d times", Math.min(populateSize, 1000)));
                execute(i -> collection.removeAll(colTest), Math.min(populateSize, 10), String.format("removeAll %d times %d elements", Math.min(populateSize, 10), colTest.size()));
                execute(i -> collection.iterator(), populateSize, "iterator " + populateSize + " times");
                execute(i -> collection.containsAll(colTest), Math.min(populateSize, 5000), "containsAll " + Math.min(populateSize, 5000) + " times");
                execute(i -> collection.toArray(), Math.min(populateSize, 5000), "toArray " + Math.min(populateSize, 5000) + " times");
                execute(i -> collection.clear(), 1, "clear");
                execute(i -> collection.retainAll(colTest), Math.min(populateSize, 10), "retainAll " + Math.min(populateSize, 10) + " times");
            }

            System.out.printf("Benchmark done in %f s\n", ((double) (System.currentTimeMillis() - startTime)) / 1000);
            System.out.println("~".repeat(55));
            // free memory
            collection.clear();

        } catch (Exception e) {
            System.err.println("Failed running Benchmark on class " + collectionClass.getCanonicalName());
            e.printStackTrace();
        }

        collection = null;
        list = null;
        heavyGc();
    }

    /**
     * Execute the current run code loop times.
     *
     * @param run      code to run
     * @param loop     number of time to run the code
     * @param taskName name displayed at the end of the task
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void execute(BenchRunnable run, int loop, String taskName) {
        System.out.printf(taskName + " ...\t");
        // set default context
        collection.clear();
        collection.addAll(defaultListCtx);
        // warmup
        warmUp();
        isTimeout = false;
        // timeout timer
        Timer timer = new Timer((int) timeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTimeout = true;
                // to raise a ConcurrentModificationException or a
                // NoSuchElementException to interrupt internal work in the List
                collection.clear();
            }
        });
        timer.setRepeats(false);
        timer.start();
        long startTime = System.nanoTime();
        int i;
        for (i = 0; i < loop && !isTimeout; i++) {
            try {
                run.run(i);
            } catch (Exception e) {
                // on purpose so ignore it
            }
        }
        timer.stop();
        long time = isTimeout ? timeout * 1000000 : System.nanoTime() - startTime;
        System.out.println((isTimeout ? "Timeout (>" + time + "ns) after " + i + " loop(s)" : time + "ns"));
        // restore default context,
        // the collection instance might have been
        // corrupted by the timeout so create a new instance
        try {
            Constructor<? extends Collection> constructor = collection.getClass().getDeclaredConstructor((Class<?>[]) null);
            constructor.setAccessible(true);
            collection = constructor.newInstance();
            // update the reference
            if (collection instanceof List) {
                list = (List<String>) collection;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // store the results for display
        Map<Class<? extends Collection<?>>, Long> currentBench = colBenchResults.computeIfAbsent(taskName, k -> new HashMap<>());
        currentBench.put((Class<? extends Collection<String>>) collection.getClass(), time);
        // little gc to clean up all the stuff
        System.gc();
    }

    /**
     * Do some operation to be sure that the internal structure is allocated
     */
    @SuppressWarnings("rawtypes")
    private void warmUp() {
        collection.remove(collection.iterator().next());
        if (collection instanceof List) {
            collection.remove(0);
            ((List) collection).indexOf(((List) collection).get(0));
        }
        collection.iterator();
        collection.toArray();
    }

    /**
     * @param collectionClasses
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void runMemoryBench(List<Class<? extends Collection>> collectionClasses) {
        for (Class<? extends Collection> clazz : collectionClasses) {
            try {
                // run some gc
                heavyGc();
                long usedMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
                Constructor<? extends Collection> constructor = clazz.getDeclaredConstructor((Class<?>[]) null);
                constructor.setAccessible(true);

                // do the test on 100 objects, to be more accurate
                for (int i = 0; i < 100; i++) {
                    this.collection = (Collection<String>) constructor.newInstance();
                    // polulate
                    collection.addAll(defaultListCtx);
                    warmUp();
                }

                // measure size
                long objectSize = (long) ((ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() - usedMemory) / 100f);
                System.out.println(clazz.getCanonicalName() + " Object size : " + objectSize + " bytes");
                colMemoryResults.put((Class<? extends Collection<?>>) clazz, objectSize);
                collection.clear();
                collection = null;
            } catch (Exception e) {
                System.err.println("Failed running Benchmark on class " + clazz.getCanonicalName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Force (very) heavy GC
     */
    private void heavyGc() {
        try {
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(1000);
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
            System.gc();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void drawChart() {
        new Chart(timeout, populateSize).displayCollectionsBenchmarkResults(colBenchResults);
    }

    public void displayMemoryResults() {
        new Chart(timeout, populateSize).displayMemoryResults(colMemoryResults);
    }
}
