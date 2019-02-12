import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class App {
    TrackWindow enwin;
    protected ScheduledExecutorService executor;
    JFrame frame;
    JComboBox<String> cBProcess;
    Database db = new Database();

    int minTimeToIdle = 15;
    int minTimeInWindow = 0;

    private Timeline applyCustomNames(Timeline timeline) {
        HashMap<String, String> customProcessNames = db.getCustomProcessNames();
        Iterator<ProcessTimeSegments> iterator = timeline.getTimeline().values().iterator();
        HashMap<String, ProcessTimeSegments> customProcTimeline = new HashMap<>();
        while (iterator.hasNext()) {
            ProcessTimeSegments p = iterator.next();
            if (customProcessNames.containsKey(p.getProcessName())) {
                p.setProcessName(customProcessNames.get(p.getProcessName()));
            }
            customProcTimeline.put(p.getProcessName(), p);
        }
        timeline.setTimeline(customProcTimeline);
        return timeline;
    }

    protected void updateDB() {
        HashMap<String, String> dbProcess = db.getProcess();
        Iterator<String> iterator = enwin.getTimeline().getTimeline().keySet().iterator();
        while (iterator.hasNext()) {
            String p = iterator.next();
            if (!dbProcess.containsKey(p))
                db.addProcess(p);
            updateDBKnownAs(p);
        }
    }

    private void updateDBKnownAs(String p) {
        ArrayList<String> dbKnownAs = db.getKnownAs(p);
        Iterator<String> iterator = enwin.timeline.getTimeline().get(p).getKnownAs().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            if (!dbKnownAs.contains(name))
                db.addKnownAs(p, name);
        }
    }

    public void updateCBProcess() {
        cBProcess.removeAllItems();
        Iterator<String> iteration = enwin.getTimeline().getTimeline().keySet().iterator();
        HashMap<String, String> customNames = db.getCustomProcessNames();
        while (iteration.hasNext()) {
            String porcessExe = iteration.next();
            if (customNames.containsKey(porcessExe) && !customNames.get(porcessExe).equals("null"))
                cBProcess.addItem(porcessExe + " - " + customNames.get(porcessExe));
            else
                cBProcess.addItem(porcessExe);
        }
        for (String ignore : db.getIgnoreList()) {
            if (customNames.containsKey(ignore))
                cBProcess.addItem(ignore + " - " + customNames.get(ignore));
            else
                cBProcess.addItem(ignore);
        }
    }

    public void start() {
        enwin = new TrackWindow(db.getIgnoreList(), minTimeToIdle);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(enwin, 0, 100, TimeUnit.MILLISECONDS);
        frame = new JFrame("Time Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField customNameField = new JTextField("Please update date to get processes");
        JTextField idleTimeField = new JTextField();
        JTextField windowTimeField = new JTextField();
        JList<String> namesPropositions = new JList<>();
        JLabel idleTimeLable = new JLabel("Min time to idle: " + minTimeToIdle + "s");
        JLabel windowTimeLable = new JLabel("Min time in window: " + minTimeInWindow + "s");
        JButton updateData = new JButton("Update Data/Generate timeline");
        JButton ignoreButton = new JButton("Ignore");
        namesPropositions.setVisibleRowCount(1);
        namesPropositions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane propositionScrollPane = new JScrollPane(namesPropositions);
        cBProcess = new JComboBox<String>();
        cBProcess.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String item = (String) e.getItem();
                item = item.split(" - ")[0];
                ArrayList<String> propositions = db.getKnownAs(item);
                namesPropositions.setListData(propositions.toArray(new String[propositions.size()])); // Displaying new
                                                                                                      // names
                                                                                                      // proposition
                HashMap<String, String> customNames = db.getCustomProcessNames();
                if (db.getIgnoreList().contains(item))
                    ignoreButton.setText("Unignore");
                else
                    ignoreButton.setText("Ignore");
                if (customNames.containsKey(item))
                    item = customNames.get(item);
                customNameField.setText(item);
            }
        });
        namesPropositions.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                customNameField.setText(namesPropositions.getSelectedValue());
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
                    String processExe = (String) cBProcess.getSelectedItem();
                    processExe = processExe.split(" - ")[0];
                    db.updateCustomProcessName(processExe, customNameField.getText());
                    customNameField.setText("-SAVED-");
                }
            }
        });
        idleTimeField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        minTimeToIdle = Integer.parseInt(idleTimeField.getText());
                        idleTimeLable.setText("Min time to idle: " + minTimeToIdle + "s");
                    } catch (NumberFormatException ex) {
                        idleTimeField.setText("Please enter a number");
                        return;
                    }
                    enwin.setMinTimeToIdle(minTimeToIdle);
                }
            }
        });
        windowTimeField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        minTimeInWindow = Integer.parseInt(windowTimeField.getText());
                        windowTimeLable.setText("Min time in window: " + minTimeInWindow + "s");
                    } catch (NumberFormatException ex) {
                        windowTimeField.setText("Please enter a number");
                        return;
                    }
                    enwin.getTimeline().setMinTimeInWindow(minTimeInWindow);
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
                updateDB();
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
        gridPanel.add(propositionScrollPane);
        gridPanel.add(idleTimeLable);
        gridPanel.add(idleTimeField);
        gridPanel.add(windowTimeLable);
        gridPanel.add(windowTimeField);
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
