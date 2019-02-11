
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

    public HashMap<String, String> getCustomProcessNames() {
        HashMap<String, String> customProcessNames = new HashMap<>();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM PROCESS");
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

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:firebirdsql:localhost/3050:C:\\Users\\bason\\eclipse-workspace\\TimeWatcher\\src\\TIMEWATCHERDB.FDB?encoding=UTF8";
        String username = "sysdba";
        String password = "masterkey";
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
