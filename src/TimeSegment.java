
public class TimeSegment {
    private int pid;
    private long startTime;
    private long stopTime;

    public TimeSegment(int pid, long startTime) {
        this.pid = pid;
        this.startTime = startTime;
        stopTime = 0;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public int getPid() {
        return pid;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTime() {
        if (stopTime != 0)
            return stopTime - startTime;
        return 0;
    }
}
