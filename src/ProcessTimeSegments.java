import java.util.ArrayList;

public class ProcessTimeSegments implements Cloneable {
    String processName;
    ArrayList<String> knownAs;
    ArrayList<TimeSegment> processTimeline;

    public ProcessTimeSegments(String processName) {
        processTimeline = new ArrayList<TimeSegment>();
        knownAs = new ArrayList<String>();
        this.processName = processName;
    }

    public void addTimeSegment(TimeSegment timeSegment) {
        if (getLastInTimeline() != null && getLastInTimeline().getStopTime() == 0) {
            setStopTime();
        }
        processTimeline.add(timeSegment);
    }

    public void addKnowAs(String windowName) {
        if (!knownAs.contains(windowName))
            knownAs.add(windowName);
    }

    public void removeTimeSegment(int index) {
        processTimeline.remove(index);
    }

    public TimeSegment getLastInTimeline() {
        if (processTimeline.isEmpty())
            return null;
        return processTimeline.get(processTimeline.size() - 1);
    }

    public void setStopTime() {
        TimeSegment newTimeSegment = getLastInTimeline();
        newTimeSegment.setStopTime(System.currentTimeMillis());
        processTimeline.set(processTimeline.size() - 1, newTimeSegment);
    }

    @Override
    public String toString() {
        return processName + " " + processTimeline;
    }

    @Override
    public int hashCode() {
        return processName.hashCode();
    }

    @Override
    public boolean equals(Object processTimeSegments) {
        if (!(processTimeSegments instanceof ProcessTimeSegments)) {
            return false;
        }
        ProcessTimeSegments p = (ProcessTimeSegments) processTimeSegments;
        if (p.getProcessName().equals(this.processName))
            return true;
        return false;
    }

    public ArrayList<String> getKnownAs() {
        return knownAs;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public ArrayList<TimeSegment> getProcessTimeline() {
        return processTimeline;
    }

    public void setProcessTimeline(ArrayList<TimeSegment> processTimeline) {
        this.processTimeline = processTimeline;
    }

    protected ProcessTimeSegments clone() {
        ProcessTimeSegments clonProcess = null;
        try {
            clonProcess = (ProcessTimeSegments) super.clone();
            ArrayList<TimeSegment> clonProcesTimeline = new ArrayList<>();
            for (TimeSegment t : processTimeline) {
                clonProcesTimeline.add(t.clone());
            }
            clonProcess.processTimeline = clonProcesTimeline;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (ProcessTimeSegments) clonProcess;
    }
}
