import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class App {
    TrackWindow enwin;
    protected ScheduledExecutorService executor;
    JFrame frame;
    JComboBox<String> cBProcess;
    Database db = new Database();

    int minTimeToIdle = 15;

    private Timeline applyCustomNames(Timeline timeline) {
        HashMap<String, String> customProcessNames = db.getCustomProcessNames();
        Iterator<ProcessTimeSegments> iterator = timeline.getTimeline().values().iterator();
        HashMap<String, ProcessTimeSegments> customProcTimeline = new HashMap<>();
        while (iterator.hasNext()) {
            ProcessTimeSegments p = iterator.next();
            if (customProcessNames.containsKey(p.getProcessName())) {
                System.out.println("if: " + p.getProcessName() + " " + customProcessNames.get(p.getProcessName()));
                p.setProcessName(customProcessNames.get(p.getProcessName()));
            }
            customProcTimeline.put(p.getProcessName(), p);
        }
        timeline.setTimeline(customProcTimeline);
        return timeline;
    }

    public void updateCBProcess() {
        cBProcess.removeAllItems();
        Iterator<String> iteration = enwin.getTimeline().getTimeline().keySet().iterator();
        while (iteration.hasNext()) {
            cBProcess.addItem(iteration.next());
        }
    }

    public void start() {
        enwin = new TrackWindow(db.getIgnoreList(), minTimeToIdle);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(enwin, 0, 100, TimeUnit.MILLISECONDS);
        frame = new JFrame("Time Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cBProcess = new JComboBox<String>();
        JButton updateData = new JButton("Update Data");
        updateData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timeline copyTimeline = enwin.getTimeline().clone();
                new TimelineChart("Timeline", applyCustomNames(copyTimeline));
                updateCBProcess();
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(cBProcess, BorderLayout.NORTH);
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
