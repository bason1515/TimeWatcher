import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Timeline {
    String lastProcessName;
    String currentProcessName;
    HashMap<String, ProcessTimeSegments> timeline;

    public Timeline(String processName) {
        ProcessTimeSegments process = new ProcessTimeSegments(processName);
        process.addTimeSegment(new TimeSegment(System.currentTimeMillis()));

        timeline = new HashMap<String, ProcessTimeSegments>();
        timeline.put(processName, process);
        currentProcessName = processName;
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
        if (processName == currentProcessName)
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

    public void displayTimeline() {
        System.out.println(timeline);
    }

    public ArrayList<ProcessTimeSegments> getTimeline() {
        Iterator<ProcessTimeSegments> iterator = timeline.values().iterator();
        ArrayList<ProcessTimeSegments> arrayTimeline = new ArrayList<ProcessTimeSegments>();
        while (iterator.hasNext()) {
            arrayTimeline.add(iterator.next());
        }
        System.out.println(arrayTimeline);
        return arrayTimeline;
    }

    public void setTimeline(HashMap<String, ProcessTimeSegments> timeline) {
        this.timeline = timeline;
    }
}
