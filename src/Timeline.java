import java.util.HashMap;
import java.util.Iterator;

public class Timeline implements Cloneable {
    private String lastProcessName;
    private String currentProcessName;
    private HashMap<String, ProcessTimeSegments> timeline;
    int minTimeInWindow;

    public Timeline(String processName, int minTimeInWindow) {
        ProcessTimeSegments process = new ProcessTimeSegments(processName);
        process.addTimeSegment(new TimeSegment(System.currentTimeMillis()));
        timeline = new HashMap<String, ProcessTimeSegments>();
        timeline.put(processName, process);
        currentProcessName = processName;
        this.minTimeInWindow = minTimeInWindow;
    }

    public ProcessTimeSegments getCurrentProcess() {
        return timeline.get(currentProcessName);
    }

    public ProcessTimeSegments getLastProcess() {
        return timeline.get(lastProcessName);
    }

    public boolean containsProcess(String processName) {
        return timeline.containsKey(processName);
    }

    public void add(String processName) {
        // prevent remembering short window switches
        if (getCurrentProcess().getLastInTimeline().getTime() < minTimeInWindow * 1000) {
            getCurrentProcess().removeTimeSegment(getCurrentProcess().getProcessTimeline().size() - 1);
            if (getCurrentProcess().getProcessTimeline().isEmpty())
                timeline.remove(currentProcessName);
            currentProcessName = lastProcessName;
            timeline.get(currentProcessName).getLastInTimeline().setStopTime(0);
        }
        if (processName.equals(currentProcessName))
            return;
        // Finising time segment of current process
        ProcessTimeSegments currentProcess = timeline.get(currentProcessName);
        currentProcess.setStopTime();
        timeline.put(currentProcessName, currentProcess);
        // Opening new time segment of new foreground process
        ProcessTimeSegments newProcess;
        if (!containsProcess(processName)) {
            newProcess = new ProcessTimeSegments(processName);
        } else {
            newProcess = timeline.get(processName);
        }
        newProcess.addTimeSegment(new TimeSegment(System.currentTimeMillis()));
        timeline.put(processName, newProcess);
        lastProcessName = currentProcessName;
        currentProcessName = processName;
    }
    
    public void addKnownAs(String windowName) {
        ProcessTimeSegments currentProcess = timeline.get(currentProcessName);
        currentProcess.addKnowAs(windowName);
    }

    public void displayTimeline() {
        System.out.println(timeline);
    }

    public void setNewName(String to, String newName) {
        if (!containsProcess(to))
            return;
        ProcessTimeSegments process = timeline.get(to);
        process.setProcessName(newName);
        timeline.remove(to);
        timeline.put(newName, process);
    }

    public void setTimeline(HashMap<String, ProcessTimeSegments> timeline) {
        this.timeline = timeline;
    }

    public HashMap<String, ProcessTimeSegments> getTimeline() {
        return timeline;
    }

    protected Timeline clone() {
        Timeline clonTimeline = null;
        try {
            clonTimeline = (Timeline) super.clone();
            HashMap<String, ProcessTimeSegments> clonProcesTimeline = new HashMap<>();
            Iterator<ProcessTimeSegments> iterator = clonTimeline.getTimeline().values().iterator();
            while (iterator.hasNext()) {
                ProcessTimeSegments p = iterator.next();
                clonProcesTimeline.put(p.getProcessName(), p.clone());
            }
            clonTimeline.timeline = clonProcesTimeline;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clonTimeline;
    }

    public int getMinTimeInWindow() {
        return minTimeInWindow;
    }

    public void setMinTimeInWindow(int minTimeInWindow) {
        this.minTimeInWindow = minTimeInWindow;
    }
}
