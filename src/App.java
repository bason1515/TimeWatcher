import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class App {
    static TrackWindow enwin;
    protected static ScheduledExecutorService executor;
    static JFrame frame;
    static Database db = new Database();

    public static void main(String[] args) {
        enwin = new TrackWindow(db.getIgnoreList());
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(enwin, 0, 100, TimeUnit.MILLISECONDS);
        start();
    }

    private static Timeline applyCustomNames(Timeline timeline) {
        HashMap<String, ProcessTimeSegments> processTimeline = new HashMap<>();
        HashMap<String, String> customProcessNames = db.getCustomProcessNames();
        for (ProcessTimeSegments p : timeline.getTimeline()) {
            if (customProcessNames.containsKey(p.getProcessName())) {
                p.setProcessName(customProcessNames.get(p.getProcessName()));
                processTimeline.put(p.getProcessName(), p);
            } else
                processTimeline.put(p.getProcessName(), p);
        }
        timeline.setTimeline(processTimeline);
        return timeline;
    }

    public static void start() {
        frame = new JFrame("Time Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton updateData = new JButton("Update Data");
        updateData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TimelineChart("Timeline", applyCustomNames(enwin.timeline));
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
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
}
