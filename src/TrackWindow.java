import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

public class TrackWindow implements Runnable {
    private static final int MAX_TITLE_LENGTH = 1024;
    Map<Integer, ArrayList<String>> programs = new HashMap<Integer, ArrayList<String>>();
    ArrayList<TimeSegment> timeArr = new ArrayList<TimeSegment>();
    int lastPid;
    int minTime = 6000, minIdle = 15;
    boolean isIdle;

    public TrackWindow() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        IntByReference pid = new IntByReference(0);
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
        registerProcess("Idle", 999999); // IdleProcess

        lastPid = pid.getValue();
        isIdle = false;
        timeArr.add(new TimeSegment(lastPid, System.currentTimeMillis()));
    }

    public void start() throws Exception {
        int idleTime = getIdleTimeMillisWin32() / 1000; // User idle time
        if (isIdle && idleTime > minIdle) {
            System.out.println("IDLE");
            return;
        } else if (idleTime > minIdle) {
            timeLine(999999);
            lastPid = 999999;
            System.out.println("Sleeping");
            isIdle = true;
            return;
        }
        isIdle = false;
        // System.out.println("Idle for: " + idleTime + "s");

        // Geting PID and foreground window name
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        IntByReference pid = new IntByReference(0);
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
        // System.out.println("Process: " + getProcess(pid.getValue()));
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        String windowName = Native.toString(buffer);

        // Getting program name, with is usually written in window name at the end
        if (windowName.lastIndexOf("-") != -1) {
            windowName = windowName.substring(windowName.lastIndexOf("-") + 1).trim();
        }
        registerProcess(windowName, pid.getValue());
        // System.out.println("Active window title: " + windowName);

        // Registering new position in Timeline and avoiding Sleep process with PID = 0
        if (lastPid != pid.getValue() && pid.getValue() >= 1) {
            timeLine(pid.getValue());
            displayRaport();
        }
        lastPid = pid.getValue();
    }

    // Linking windows names to process PID
    private void registerProcess(String windowName, int pid) {
        if (programs.containsKey(pid)) {
            ArrayList<String> arr = programs.get(pid);
            if (!arr.contains(windowName)) {
                arr.add(windowName);
                programs.replace(pid, arr);
            }
        } else {
            ArrayList<String> arr = new ArrayList<String>();
            arr.add(windowName);
            programs.put(pid, arr);
        }
    }

    // Registering window change time and adding new element in our time line
    private void timeLine(int pid) {
        long time = System.currentTimeMillis();
        TimeSegment lastSeg = timeArr.get(timeArr.size() - 1);
        lastSeg.setStopTime(time);
        if (lastSeg.getTime() >= minTime) {
            timeArr.remove(timeArr.size() - 1);
            timeArr.add(lastSeg);
            timeArr.add(new TimeSegment(pid, time + 1));
        }
    }

    // log about PID to Names relations
    public void displayRaport() {
        System.out.println(programs);
        for (TimeSegment seg : timeArr) {
            if (seg.getTime() > 1000)
                System.out.print(programs.get(seg.getPid()) + "(" + seg.getPid() + ")" + ": " + seg.getTime() + " | ");
        }
        System.out.println("");
    }

    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;

    }

    // Acquiring proces .exe name
    public String getProcess(int pid) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("tasklist.exe /SVC");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        Scanner scan = new Scanner(new InputStreamReader(proc.getInputStream()));
        while (scan.hasNext()) {
            String process = scan.nextLine();
            if (process.contains(Integer.toString(pid))) {
                scan.close();
                return process;
            }
        }
        scan.close();
        return "";
    }

    public Map<Integer, ArrayList<String>> getPrograms() {
        return programs;
    }

    public void setPrograms(Map<Integer, ArrayList<String>> programs) {
        this.programs = programs;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public ArrayList<TimeSegment> getTimeArr() {
        return timeArr;
    }

    public boolean isIdle() {
        return isIdle;
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}