
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    public void createDatabase() {
        // TODO
    }

    public void clearDatabase() {
        // TODO
    }

    public void setInitData(int minIdleTime, int minWindowTime) {
        String minIdle = wrap(minIdleTime);
        String minWindow = wrap(minWindowTime);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM INIT_DATA;");
            stm.executeUpdate(
                    "INSERT INTO INIT_DATA (MIN_IDLE, MIN_WINDOW) VALUES (" + minIdle + ", " + minWindow + ");");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public int[] getInitData() {
        int[] init = new int[2];
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM INIT_DATA;");
            while (res.next()) {
                init[0] = res.getInt("MIN_IDLE");
                init[1] = res.getInt("MIN_WINDOW");
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return init;
    }

    public HashMap<String, String> getCustomProcessNames() {
        HashMap<String, String> customProcessNames = new HashMap<>();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM PROCESS WHERE PROCESS.PROCESS_NAME IS NOT NULL ");
            while (res.next()) {
                customProcessNames.put(res.getString("PROCESS_EXE"), res.getString("PROCESS_NAME"));
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return customProcessNames;
    }

    public void addCustomProcessNames(String processExe, String processName) {
        processExe = wrap(processExe);
        processName = wrap(processName);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("INSERT INTO PROCESS (PROCESS_EXE, PROCESS_NAME) VALUES (" + processExe + ", "
                    + processName + ");");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public void updateCustomProcessName(String processExe, String newName) {
        processExe = wrap(processExe);
        newName = wrap(newName);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate(
                    "UPDATE PROCESS SET PROCESS_NAME = " + newName + " WHERE PROCESS_EXE = " + processExe + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public void addKnownAs(String processExe, String name) {
        name = wrap(name);
        String id = wrap(getIdOfProcess(processExe));
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("INSERT INTO KNOWNAS (NAME, PROCESS_ID) VALUES (" + name + ", " + id + ");");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getKnownAs(String processExe) {
        String id = wrap(getIdOfProcess(processExe));
        ArrayList<String> names = new ArrayList<>();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT NAME FROM KNOWNAS WHERE KNOWNAS.PROCESS_ID = " + id + ";");
            while (res.next()) {
                names.add(res.getString("NAME"));
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return names;
    }

    private String getIdOfProcess(String processExe) {
        processExe = wrap(processExe);
        String id = null;
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT ID FROM PROCESS WHERE PROCESS.PROCESS_EXE = " + processExe + ";");
            while (res.next()) {
                id = res.getString("ID");
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return id;
    }

    public void addProcess(String processExe) {
        processExe = wrap(processExe);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("INSERT INTO PROCESS (PROCESS_EXE) VALUES (" + processExe + ");");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getProcess() {
        HashMap<String, String> ProcessNames = new HashMap<>();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM PROCESS");
            while (res.next()) {
                ProcessNames.put(res.getString("PROCESS_EXE"), res.getString("PROCESS_NAME"));
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return ProcessNames;
    }

    public void deleteCustomProcessNames(String processExe) {
        processExe = wrap(processExe);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM PROCESS WHERE PROCESS.PROCESS_EXE = " + processExe + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getIgnoreList() {
        ArrayList<String> ignoreList = new ArrayList<>();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM IGNORE");
            while (res.next()) {
                ignoreList.add(res.getString("PROCESS_EXE"));
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
        return ignoreList;
    }

    public void addIgnore(String processExe) {
        processExe = wrap(processExe);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("INSERT INTO IGNORE (PROCESS_EXE) VALUES (" + processExe + ");");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    public void deleteIgnore(String processExe) {
        processExe = wrap(processExe);
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM IGNORE WHERE IGNORE.PROCESS_EXE = " + processExe + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection Fail");
            e.printStackTrace();
        }
    }

    private String wrap(String str) {
        return "'" + str + "'";
    }

    private String wrap(int arg) {
        return "'" + arg + "'";
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:firebirdsql:localhost/3050:C:\\Users\\bason\\eclipse-workspace\\TimeWatcher\\src\\TIMEWATCHERDB.FDB?encoding=UTF8";
        String username = "sysdba";
        String password = "masterkey";
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
