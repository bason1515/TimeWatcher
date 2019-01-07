import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Integer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

public class App {
    static TrackWindow enwin;
    protected static ScheduledExecutorService executor;
    static JFrame frame;

    static Map<Integer, ArrayList<String>> programs;
    static ArrayList<String> programsFirstName = new ArrayList<String>();
    static ArrayList<TimeSegment> timeArr;

    static JFreeChart chart;
    static TaskSeriesCollection collection = new TaskSeriesCollection();

    public static void main(String[] args) {
        enwin = new TrackWindow();
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(enwin, 0, 100, TimeUnit.MILLISECONDS);
        start();
    }

    public static void createGanttChart() {
        IntervalCategoryDataset dataset = collection;
        chart = ChartFactory.createGanttChart("Timeline", "Programs", "Time", dataset);
        chart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
    }

    public static void updateCategoryDataset() {
        // Updating all data
        programs = enwin.getPrograms();
        timeArr = enwin.getTimeArr();
        updateFirstName();
        // Creating series
        System.out.println("CLEAR");
        collection.removeAll();
        collection.getColumnKeys().clear();
        collection.getRowKeys().clear();
        // Creating each task series without duplication
        Map<Integer, TaskSeries> taskSeriesArr = new HashMap<Integer, TaskSeries>();
        for (TimeSegment seg : timeArr) {
            TaskSeries series = new TaskSeries(programs.get(seg.getPid()).get(0));
            taskSeriesArr.put(seg.getPid(), series);
        }
        // Adding each segments to specific series
        System.out.println(taskSeriesArr);
        for (TimeSegment seg : timeArr) {
            if (seg.getStopTime() < 1) { // Setting time for current open process
                seg.setStopTime(System.currentTimeMillis());
            }
            TaskSeries series = taskSeriesArr.get(seg.getPid());
            if (series.isEmpty()) {
                series.add(new Task("Applications", new Date(seg.getStartTime()), new Date(seg.getStopTime())));
                Task task = (Task) series.getTasks().get(0);
                task.addSubtask(new Task("Applications", new Date(seg.getStartTime()), new Date(seg.getStopTime())));
                System.out.println("First Task " + series.getTasks().get(0));
            } else {
                System.out.println("SubTask");
                Task task = (Task) series.getTasks().get(0);
                task.addSubtask(new Task("Applications", new Date(seg.getStartTime()), new Date(seg.getStopTime())));
                series.removeAll();
                series.add(task);
            }
            taskSeriesArr.put(seg.getPid(), series);
        }
        // Adding all task series to task collection
        for (TaskSeries series : taskSeriesArr.values()) {
            collection.add(series);
        }
    }

    public static void start() {
        frame = new JFrame("Time Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton updateData = new JButton("Update Data");
        updateData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCategoryDataset();
            }
        });
        createGanttChart();
        JPanel panel = new JPanel(new BorderLayout());
        ChartPanel charPanel = new ChartPanel(chart);
        charPanel.setSize(600, 500);
        panel.add(charPanel, BorderLayout.CENTER);
        panel.add(updateData, BorderLayout.SOUTH);
        frame.setContentPane(panel);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                executor.shutdown();
                System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public static void updateFirstName() {
        ArrayList<String> processName = new ArrayList<String>();
        for (ArrayList<String> arr : programs.values()) { // Collecting all first known names of process
            processName.add(arr.get(0));
            System.out.println("Category: " + arr.get(0));
        }
        programsFirstName = processName;
    }
}
