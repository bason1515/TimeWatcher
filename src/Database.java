
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
