import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

public class TrackWindow implements Runnable {
    private static final int MAX_TITLE_LENGTH = 1024;

    Timeline timeline;
    ArrayList<String> ignoreList;

    private int minTimeToIdle;

    // Setting up first process in timeline
    public TrackWindow(ArrayList<String> ignoreList, int minTimeToIdle) {
        String windowName = getWindowName();
        int pid = getProcessPid();
        String processName = getProcessExe(pid);
        timeline = new Timeline(processName);
        this.ignoreList = ignoreList;
        this.minTimeToIdle = minTimeToIdle;
    }

    public String getWindowName() {
        // Getting full window name
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        String windowName = Native.toString(buffer);
        // Window name is usually written at the end
        if (windowName.lastIndexOf("-") != -1) {
            windowName = windowName.substring(windowName.lastIndexOf("-") + 1).trim();
        }
        return windowName;
    }

    public int getProcessPid() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        IntByReference pid = new IntByReference(0);
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
        return pid.getValue();
    }

    public void start() throws Exception {
        String windowName = getWindowName();
        int pid = getProcessPid();
        String processName = getProcessExe(pid);
        // -----------------
        System.out.println(windowName + " " + processName + " " + pid);
        System.out.println(isChangeProcess(processName) + " " + timeline.getCurrentProcess());
        // -----------------
        if(getIdleTimeMillis()/1000 > minTimeToIdle) {
            processName = "Idle";
        }
        if (!isChangeProcess(processName))
            return;
        if (ignoreList.contains(processName))
            return;
        timeline.add(processName);
        timeline.displayTimeline();
    }

    private boolean isChangeProcess(String processName) {
        return !timeline.getCurrentProcess().getProcessName().equals(processName);
    }

    public static int getIdleTimeMillis() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

    // Acquiring proces.exe name
    public String getProcessExe(int pid) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("tasklist.exe /SVC");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Scanner scan = new Scanner(new InputStreamReader(proc.getInputStream()));
        while (scan.hasNext()) {
            String process = scan.nextLine();
            if (process.contains(Integer.toString(pid))) {
                scan.close();
                return process.split(" ")[0];
            }
        }
        scan.close();
        return "";
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(ArrayList<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    public int getMinTimeToIdle() {
        return minTimeToIdle;
    }

    public void setMinTimeToIdle(int minTimeToIdle) {
        this.minTimeToIdle = minTimeToIdle;
    }
}