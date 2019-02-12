
public class TimeSegment implements Cloneable {
    private long startTime;
    private long stopTime;

    public TimeSegment(long startTime) {
        this.startTime = startTime;
        stopTime = 0;
    }

    public long getStopTime() {
        if (stopTime == 0)
            return System.currentTimeMillis();
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTime() {
        if (stopTime != 0)
            return stopTime - startTime;
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return "" + getTime();
    }

    protected TimeSegment clone() {
        TimeSegment clonTime = null;
        try {
            clonTime = (TimeSegment) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (TimeSegment) clonTime;
    }
}
