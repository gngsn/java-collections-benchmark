package benchmark;

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

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Chart {

    private long timeout;

    private int populateSize;

    public Chart(long timeout, int populateSize) {
        this.timeout = timeout;
        this.populateSize = populateSize;
    }

    /**
     * Display Benchmark results
     */
    @SuppressWarnings("serial")
    public void displayCollectionsBenchmarkResults(Map<String, Map<Class<? extends Collection<?>>, Long>> colBenchResults) {
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
     * Display Memory results
     */
    public void displayMemoryResults(Map<Class<? extends Collection<?>>, Long> colMemoryResults) {
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
     * Create a chartpanel
     *
     * @param title                 title
     * @param dataName              name of the data
     * @param clazzResult           data mapped by classes
     * @param catItemLabelGenerator label generator
     * @return the chartPanel
     */
//    @SuppressWarnings("serial")
    private ChartPanel createChart(String title, String dataName,
                                   Map<Class<? extends Collection<?>>, Long> clazzResult,
                                   AbstractCategoryItemLabelGenerator catItemLabelGenerator) {
        // sort data by class name
        List<Class<? extends Collection<?>>> clazzes = new ArrayList<>(clazzResult.keySet());
        Collections.sort(clazzes, new Comparator<>() {
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

}
