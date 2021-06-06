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
package JavaCollectionsBenchmark;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Queue;
import java.util.*;

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
        defaultListCtx = new ArrayList<String>();
        for (int i = 0; i < populateSize; i++) {
            defaultListCtx.add(Integer.toString(i % 100));
        }
        colBenchResults = new HashMap<String, Map<Class<? extends Collection<?>>, Long>>();
        colMemoryResults = new HashMap<Class<? extends Collection<?>>, Long>();
    }

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            CollectionsBenchmark collectionsBenchmark = new CollectionsBenchmark(15000, 100000);

            // Standard Collections List, Set, Queue Time Benchmark
            collectionsBenchmark.run(ArrayList.class);
            collectionsBenchmark.run(LinkedList.class);
            collectionsBenchmark.run(HashSet.class);
            collectionsBenchmark.run(LinkedHashSet.class);
            collectionsBenchmark.run(TreeSet.class);
            collectionsBenchmark.run(PriorityQueue.class);
            collectionsBenchmark.run(ArrayDeque.class);
            collectionsBenchmark.displayCollectionsBenchmarkResults();

            // Standard Collections List, Set, Queue Memory Benchmark
			List<Class<? extends Collection>> classes = new ArrayList<Class<? extends Collection>>();
			classes.add(ArrayList.class);
            classes.add(LinkedList.class);
            classes.add(HashSet.class);
            classes.add(LinkedHashSet.class);
            classes.add(TreeSet.class);
            classes.add(PriorityQueue.class);
            classes.add(ArrayDeque.class);
    		collectionsBenchmark.runMemoryBench(classes);
			collectionsBenchmark.displayMemoryResults();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the Collections Benchmark on the given collection
     *
     * @param collectionTested the collection (if it's a List, some additional
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
            System.out.println("Performances of " + collection.getClass().getCanonicalName() + " populated with "
                    + populateSize + " elt(s)");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            // Test List Collection used in Benchmark cases
            final Collection<String> colTest = new ArrayList<String>();
            for (int i = 0; i < 1000; i++) {
                colTest.add(Integer.toString(i % 30));
            }

            // Standard Collection List, Set and Queue Benchmark
            if (collection instanceof List || collection instanceof Set || collection instanceof Queue) {
                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.add(Integer.toString(i % 29));
                    }
                }, populateSize, "add " + populateSize + " elements");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.remove(Integer.toString(i));
                    }
                }, Math.max(1, populateSize / 10), "remove " + Math.max(1, populateSize / 10) + " elements given Object");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.addAll(colTest);
                    }
                }, Math.min(populateSize, 1000), "addAll " + Math.min(populateSize, 1000) + " times " + colTest.size()
                        + " elements");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.contains(collection.size() - i - 1);
                    }
                }, Math.min(populateSize, 1000), "contains " + Math.min(populateSize, 1000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.removeAll(colTest);
                    }
                }, Math.min(populateSize, 10), "removeAll " + Math.min(populateSize, 10) + " times " + colTest.size()
                        + " elements");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.iterator();
                    }
                }, populateSize, "iterator " + populateSize + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.containsAll(colTest);
                    }
                }, Math.min(populateSize, 5000), "containsAll " + Math.min(populateSize, 5000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.toArray();
                    }
                }, Math.min(populateSize, 5000), "toArray " + Math.min(populateSize, 5000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.clear();
                    }
                }, 1, "clear");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        collection.retainAll(colTest);
                    }
                }, Math.min(populateSize, 10), "retainAll " + Math.min(populateSize, 10) + " times");
            }

            // List Benchmark
            if (collection instanceof List) {
                list = (List<String>) collection;
                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.add((i), Integer.toString(i));
                    }
                }, populateSize, "add at a given index " + populateSize + " elements");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.addAll((i), colTest);
                    }
                }, Math.min(populateSize, 1000), "addAll " + Math.min(populateSize, 1000) + " times " + colTest.size()
                        + " elements at a given index");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.get(i);
                    }
                }, Math.min(populateSize, 50000), "get " + Math.min(populateSize, 50000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.indexOf(i);
                    }
                }, Math.min(populateSize, 5000), "indexOf " + Math.min(populateSize, 5000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.lastIndexOf(i);
                    }
                }, Math.min(populateSize, 5000), "lastIndexOf " + Math.min(populateSize, 5000) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.set(i, Integer.toString(i % 29));
                    }
                }, Math.max(1, populateSize), "set " + Math.max(1, populateSize) + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.subList(collection.size() / 4, collection.size() / 2);
                    }
                }, populateSize, "subList on a " + (populateSize / 2 - populateSize / 4) + " elts sublist "
                        + populateSize + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.listIterator();
                    }
                }, populateSize, "listIterator " + populateSize + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.listIterator(i);
                    }
                }, populateSize, "listIterator at a given index " + populateSize + " times");

                execute(new BenchRunnable() {
                    @Override
                    public void run(int i) {
                        list.remove(list.size() / 2);
                    }
                }, 10000, "remove " + 10000 + " elements given index (index=list.size()/2)");
            }

            System.out.println("Benchmark done in " + ((double) (System.currentTimeMillis() - startTime)) / 1000 + "s");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
        System.out.print(taskName + " ... ");
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
        Map<Class<? extends Collection<?>>, Long> currentBench = colBenchResults.get(taskName);
        if (currentBench == null) {
            currentBench = new HashMap<Class<? extends Collection<?>>, Long>();
            colBenchResults.put(taskName, currentBench);
        }
        currentBench.put((Class<? extends Collection<String>>) collection.getClass(), time);
        // little gc to clean up all the stuff
        System.gc();
    }

    /**
     * Display Benchmark results
     */
    @SuppressWarnings("serial")
    public void displayCollectionsBenchmarkResults() {
        List<ChartPanel> chartPanels = new ArrayList<ChartPanel>();
        // sort task by names
        List<String> taskNames = new ArrayList<String>(colBenchResults.keySet());
        Collections.sort(taskNames);
        // browse task name, 1 chart per task
        for (String taskName : taskNames) {
            // time by class
            Map<Class<? extends Collection<?>>, Long> clazzResult = colBenchResults.get(taskName);

            ChartPanel chartPanel = createChart(taskName, "Time (ns)", clazzResult,
                    new StandardCategoryItemLabelGenerator() {
                        @Override
                        public String generateLabel(CategoryDataset dataset, int row, int column) {
                            String label = " " + dataset.getRowKey(row).toString();
                            if (dataset.getValue(row, column).equals(timeout * 1000000)) {
                                label += " (Timeout)";
                            }
                            return label;
                        }
                    });

            chartPanels.add(chartPanel);
        }
        // display in a JFrame
        JPanel mainPanel = new JPanel(new GridLayout(chartPanels.size() / 5, 5, 5, 5));
        for (ChartPanel chart : chartPanels) {
            mainPanel.add(chart);
        }
        JFrame frame = new JFrame("Collection Implementations Benchmark");
        frame.getContentPane().add(
                new JLabel("Collection Implementations Benchmark. Populate size : " + populateSize + ", timeout : "
                        + timeout + "ms"), BorderLayout.NORTH);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
     * Create a chartpanel
     *
     * @param title                 title
     * @param dataName              name of the data
     * @param clazzResult           data mapped by classes
     * @param catItemLabelGenerator label generator
     * @return the chartPanel
     */
    @SuppressWarnings("serial")
    private ChartPanel createChart(String title, String dataName,
                                   Map<Class<? extends Collection<?>>, Long> clazzResult,
                                   AbstractCategoryItemLabelGenerator catItemLabelGenerator) {
        // sort data by class name
        List<Class<? extends Collection<?>>> clazzes = new ArrayList<Class<? extends Collection<?>>>(
                clazzResult.keySet());
        Collections.sort(clazzes, new Comparator<Class<? extends Collection<?>>>() {
            @Override
            public int compare(Class<? extends Collection<?>> o1, Class<? extends Collection<?>> o2) {
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        // add the data to the dataset
        for (Class<? extends Collection<?>> clazz : clazzes) {
            dataSet.addValue(clazzResult.get(clazz), clazz.getName(), title.split(" ")[0]);
        }
        // create the chart
        JFreeChart chart = ChartFactory.createBarChart(null, null, dataName, dataSet, PlotOrientation.HORIZONTAL,
                false, true, false);
        chart.addSubtitle(new TextTitle(title));
        // some customization in the style
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(255, 200, 200));
        plot.setRangeGridlinePaint(Color.BLUE);
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setLabelFont(new Font("arial", Font.PLAIN, 10));
        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        // display the class name in the bar chart
        for (int i = 0; i < clazzResult.size(); i++) {
            renderer.setSeriesItemLabelGenerator(i, new StandardCategoryItemLabelGenerator() {
                @Override
                public String generateLabel(CategoryDataset dataset, int row, int column) {
                    String label = " " + dataset.getRowKey(row).toString();
                    if (dataset.getValue(row, column).equals(timeout * 1000000)) {
                        label += " (Timeout)";
                    }
                    return label;
                }
            });
            renderer.setSeriesItemLabelsVisible(i, true);
            ItemLabelPosition itemPosition = new ItemLabelPosition();
            renderer.setSeriesPositiveItemLabelPosition(i, itemPosition);
            renderer.setSeriesNegativeItemLabelPosition(i, itemPosition);
        }
        ItemLabelPosition itemPosition = new ItemLabelPosition();
        renderer.setPositiveItemLabelPositionFallback(itemPosition);
        renderer.setNegativeItemLabelPositionFallback(itemPosition);
        renderer.setShadowVisible(false);

        // create the chartpanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chart.setBorderVisible(true);
        return chartPanel;
    }

    /**
     * @param collectionClass
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

    /**
     * Display Memory results
     */
    public void displayMemoryResults() {
        ChartPanel chart = createChart("Memory usage of collections",
                "Memory usage (bytes) of collections populated by " + populateSize + " element(s)", colMemoryResults,
                new StandardCategoryItemLabelGenerator());
        JFrame frame = new JFrame("Collection Implementations Benchmark");
        frame.getContentPane().add(chart, BorderLayout.CENTER);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * BenchRunnable
     *
     */
    private interface BenchRunnable {
        /**
         * Runnable that can exploit the current loop index
         *
         * @param loopIndex loop index
         */
        void run(int loopIndex);
    }
}
