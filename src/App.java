import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JTextField;

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
        for (String ignore : db.getIgnoreList()) {
            cBProcess.addItem(ignore);
        }
    }

    public void start() {
        enwin = new TrackWindow(db.getIgnoreList(), minTimeToIdle);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(enwin, 0, 100, TimeUnit.MILLISECONDS);
        frame = new JFrame("Time Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField customNameField = new JTextField();
        JButton updateData = new JButton("Update Data");
        JButton ignoreButton = new JButton("Ignore");
        cBProcess = new JComboBox<String>();
        cBProcess.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String item = (String) e.getItem();
                HashMap<String, String> customNames= db.getCustomProcessNames();
                if (db.getIgnoreList().contains(item))
                    ignoreButton.setText("Unignore");
                else
                    ignoreButton.setText("Ignore");
                if (customNames.containsKey(item))
                    item = customNames.get(item);
                customNameField.setText(item);
            }
        });
        customNameField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (cBProcess.getItemCount() == 0 || customNameField.getText().trim().equals("")) {
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String procesExe = (String) cBProcess.getSelectedItem();
                    db.deleteCustomProcessNames(procesExe);
                    db.addCustomProcessNames(procesExe, customNameField.getText());
                    customNameField.setText("-SAVED-");
                }
            }
        });
        ignoreButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cBProcess.getItemCount() == 0)
                    return;
                String item = (String) cBProcess.getSelectedItem();
                if (ignoreButton.getText().equals("Ignore")) {
                    db.addIgnore(item);
                    ignoreButton.setText("Unignore");
                } else {
                    db.deleteIgnore(item);
                    ignoreButton.setText("Ignore");
                }
                enwin.setIgnoreList(db.getIgnoreList());
            }
        });
        updateData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timeline copyTimeline = enwin.getTimeline().clone();
                new TimelineChart("Timeline", applyCustomNames(copyTimeline));
                updateCBProcess();
            }
        });
        GridLayout grid = new GridLayout(0, 2);
        grid.setVgap(5);
        grid.setHgap(3);
        JPanel gridPanel = new JPanel(grid);
        gridPanel.add(cBProcess);
        gridPanel.add(customNameField);
        gridPanel.add(ignoreButton);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gridPanel, BorderLayout.NORTH);
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
