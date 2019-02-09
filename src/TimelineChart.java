import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class TimelineChart extends JFrame {

    private Timeline timeline;

    public TimelineChart(final String title, Timeline timeline) {
        super(title);
        this.timeline = timeline;
        final IntervalCategoryDataset dataset = createDataset();
        final JFreeChart chart = ChartFactory.createGanttChart("Timeline", // chart title
                "Process names", // domain axis label
                "Time", // range axis label
                dataset, // data
                true, // include legend
                true, // tooltips
                false // urls
        );
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // plot.getDomainAxis().setMaxCategoryLabelWidthRatio(10.0f);
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setDefaultToolTipGenerator(new CategoryToolTipGenerator() {
            int count = 0;
            Task lasttask = null;
            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int columne) {
                TaskSeriesCollection collection = (TaskSeriesCollection) dataset;
                TaskSeries s = collection.getSeries(0);
                Task task = s.get(columne);
                if(!task.equals(lasttask)) {
                    lasttask = task;
                    count = 0;
                }
                Task subTask = task.getSubtask(count);
                count++;
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date dateStart = subTask.getDuration().getStart();
                Date dateStop = subTask.getDuration().getEnd();
                return format.format(dateStart) + "-" + format.format(dateStop);
            }
        });

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        UIUtils.centerFrameOnScreen(this);
        setVisible(true);
    }

    private TaskSeriesCollection createDataset() {
        final TaskSeries s1 = new TaskSeries("Applications");
        ArrayList<ProcessTimeSegments> processesTimeline = timeline.getTimeline();
        // Preparing main task with oldest start time and latest stop time
        Task[] tasks = new Task[processesTimeline.size()];
        for (ProcessTimeSegments p : processesTimeline) {
            final Task task = new Task(p.getProcessName(), new Date(p.getProcessTimeline().get(0).getStartTime()),
                    new Date(p.getLastInTimeline().getStopTime()));

            addSubTasks(task, p); // Creating full Task with all times
            // task.setDescription(new Date(p.getProcessTimeline().get(0).getStartTime() + "
            // " + new Date(p.getLastInTimeline().getStopTime()));
            s1.add(task);
        }
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(s1);
        return collection;
    }

    private Task addSubTasks(Task task, ProcessTimeSegments p) {
        for (TimeSegment timeSeg : p.getProcessTimeline()) {
            final Task subTask = new Task("", new Date(timeSeg.getStartTime()), new Date(timeSeg.getStopTime()));
            task.addSubtask(subTask);
        }
        return task;
    }

}