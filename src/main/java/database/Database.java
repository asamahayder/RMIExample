package database;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;

import static database.Filer.deleteDatabaseFile;
import static database.Filer.getPath;
import static database.Hasher.getSalt;
import static database.Hasher.hashPassword;

public class Database {

    private static Connection connection = null;

    public Database() {

    }

    public static void createDatabase() {
        String[] methods = new String[] {"print", "queue", "topQueue", "start", "stop", "restart", "status", "readConfig", "setConfig"};
        String[] roles = new String[] {"Admin", "Power User", "Technician", "User"};

        deleteDatabaseFile();
        createUserTable();
        createConfigTable();
        insertUserTable();
        insertConfig();
        insertAccessControl(methods, roles);
    }

    public UserDTO selectUser(String username) {
        try {
            connect();
            Statement stmt = connection.createStatement();

            String sql = "SELECT * FROM USERS WHERE NAME = '" + username + "';";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String name = rs.getString("NAME");
                String password = rs.getString("PASSWORD");
                byte[] salt = rs.getBytes("SALT");

                disconnect();
                return new UserDTO(name, password, salt);
            }
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public ConfigDTO getConfig(String parameter) {
        try {
            connect();
            Statement stmt = connection.createStatement();

            String sql = "SELECT * FROM CONFIG WHERE DESCRIPTION = '" + parameter + "';";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String value = rs.getString("VALUE");
                stmt.close();
                disconnect();
                return new ConfigDTO(value);
            }

            stmt.close();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void setConfig(String parameter, String value) {
        try {
            connect();

            String pQuery = "UPDATE CONFIG SET VALUE = ? WHERE DESCRIPTION = ?";
            PreparedStatement pstmt = connection.prepareStatement(pQuery);
            pstmt.setString(1, value);
            pstmt.setString(2, parameter);
            pstmt.execute();
            pstmt.close();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void connect() {
        try {
            String path = getPath();
            String url = "jdbc:sqlite:" + path + "database.db";

            connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createUserTable() {
        try {
            connect();

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE USERS " +
                    "(USER_ID INT PRIMARY KEY NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "PASSWORD CHAR(512), " +
                    "SALT BLOB, " +
                    "ROLE_ID INT NOT NULL)";

            stmt.executeUpdate(sql);
            stmt.close();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createConfigTable() {
        try {
            connect();

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE CONFIG " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "DESCRIPTION TEXT NOT NULL, " +
                    "VALUE TEXT NOT NULL)";

            stmt.executeUpdate(sql);
            stmt.close();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertUserTable() {
        try {
            connect();
            connection.setAutoCommit(false);

            helpInsertUser(0, "Alice", 0);        // Alice is admin
            helpInsertUser(1, "Bob", 2);          // Bob is technician
            helpInsertUser(2, "Cecilia", 1);      // Cecilia is power user
            helpInsertUser(3, "David", 3);        // Ordinary user
            helpInsertUser(4, "Eric", 3);         // Ordinary user
            helpInsertUser(5, "Fred", 3);         // Ordinary user
            helpInsertUser(6, "George", 3);       // Ordinary user

            connection.commit();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertConfig() {
        try {
            connect();
            connection.setAutoCommit(false);

            PreparedStatement stmt = null;

            String sql = "INSERT INTO CONFIG (ID, DESCRIPTION, VALUE) " +
                    "VALUES ("+1+", '" + "testParam" + "', '" + "testString" + "');";

            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();

            connection.commit();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createAccessControlTables() {
        try {
            connect();

            // OPERATION_ID, METHOD
            String operationsTable = "CREATE TABLE OPERATIONS " +
                    "(OPERATION_ID INT PRIMARY KEY NOT NULL, " +
                    "METHOD TEXT NOT NULL)";

            // USER_ID, OPERATION_ID
            String userOperationsTable = "CREATE TABLE USER_OPERATIONS " +
                    "(USER_ID INT NOT NULL, " +
                    "OPERATION_ID INT NOT NULL)";

            // ROLE_ID, ROLE
            String rolesTable = "CREATE TABLE ROLES " +
                    "(ROLE_ID INT PRIMARY KEY NOT NULL, " +
                    "ROLE TEXT NOT NULL)";

            // ROLE_ID, OPERATION_ID
            String roleOperationsTable = "CREATE TABLE ROLE_OPERATIONS " +
                    "(ROLE_ID INT NOT NULL, " +
                    "OPERATION_ID INT NOT NULL)";

            // ROLE_ID, ROLE_CHILD_ID
            String roleTreeTable = "CREATE TABLE ROLE_TREE " +
                    "(ROLE_ID INT NOT NULL, " +
                    "ROLE_CHILD_ID, INT)";

            connection.prepareStatement(operationsTable).execute();
            connection.prepareStatement(userOperationsTable).execute();
            connection.prepareStatement(rolesTable).execute();
            connection.prepareStatement(roleOperationsTable).execute();
            connection.prepareStatement(roleTreeTable).execute();

            connection.commit();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertAccessControl(String[] methods, String[] roles) {
        try {
            connect();

            // INSERT ACCESS CONTROL STUFF
            for (int i = 0; i < methods.length; i++) {
                String sql = "INSERT INTO OPERATIONS (OPERATION_ID, OPERATION) VALUES (" + i + ", ." + methods[i] + "')";
                connection.prepareStatement(sql).execute();
            }

            // INSERT ROLE BASED ACCESS CONTROL STUFF
            for (int i = 0; i < roles.length; i++) {
                String sql = "INSERT INTO ROLES (ROLE_ID, ROLE) VALUES (" + i + ", '" + roles[i] + "')";
                connection.prepareStatement(sql).execute();
            }

            // ROLE IDS = (0 = admin, 1 = power user, 2 = technician, 3 = user)
            // OPERATION IDS = (0 = print, 1 = queue, 2 = topQueue, 3 = start, 4 = stop, 5 = restart, 6 = status, 7 = readConfig, 8 = setConfig)

            // USER OPERATION
            // USER IDS = (0 = Alice, 1 = Bob, 2 = Cecilia, 3-6 users)
            
            // Regular users can print and queue
            for (int i = 3 ; i <= 6 ; i++) {
                connection.prepareStatement(helpInsertUserOperations(i, 0)).execute();  // user can print
                connection.prepareStatement(helpInsertUserOperations(i, 1)).execute();  // user can queue
            }

            // Bob can start, stop, restart, status, readConfig, setConfig
            connection.prepareStatement(helpInsertUserOperations(1, 3)).execute();
            connection.prepareStatement(helpInsertUserOperations(1, 4)).execute();
            connection.prepareStatement(helpInsertUserOperations(1, 5)).execute();
            connection.prepareStatement(helpInsertUserOperations(1, 6)).execute();
            connection.prepareStatement(helpInsertUserOperations(1, 7)).execute();
            connection.prepareStatement(helpInsertUserOperations(1, 8)).execute();

            // Cecilia can print, queue, topQueue and restart
            connection.prepareStatement(helpInsertUserOperations(2, 0)).execute();
            connection.prepareStatement(helpInsertUserOperations(2, 1)).execute();
            connection.prepareStatement(helpInsertUserOperations(2, 2)).execute();
            connection.prepareStatement(helpInsertUserOperations(2, 5)).execute();

            // Alice can everything
            for (int i = 0 ; i < methods.length ; i++) {
                connection.prepareStatement(helpInsertUserOperations(0, i)).execute();
            }

            // ROLE OPERATION
            connection.prepareStatement(helpInsertRoleOperations(3, 0)).execute();  // user can print
            connection.prepareStatement(helpInsertRoleOperations(3, 1)).execute();  // user can queue
            connection.prepareStatement(helpInsertRoleOperations(1, 2)).execute();  // power user can topQueue
            connection.prepareStatement(helpInsertRoleOperations(1, 5)).execute();  // power user can restart
            connection.prepareStatement(helpInsertRoleOperations(2, 3)).execute();  // technician can start
            connection.prepareStatement(helpInsertRoleOperations(2, 4)).execute();  // technician can stop
            connection.prepareStatement(helpInsertRoleOperations(2, 5)).execute();  // technician can restart
            connection.prepareStatement(helpInsertRoleOperations(2, 6)).execute();  // technician can status
            connection.prepareStatement(helpInsertRoleOperations(2, 7)).execute();  // technician can readConfig
            connection.prepareStatement(helpInsertRoleOperations(2, 8)).execute();  // technician can setConfig

            // ROLE TREE
            connection.prepareStatement(helpInsertRoleTree(0, 1)).execute();
            connection.prepareStatement(helpInsertRoleTree(0, 2)).execute();
            connection.prepareStatement(helpInsertRoleTree(1, 3)).execute();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String helpInsertRoleTree(int whatRoleParent, int whatRoleChild) {
        return "INSERT INTO ROLE_TREE (ROLE_ID, ROLE_CHILD_ID) " +
                "VALUES (" + whatRoleParent + ", " + whatRoleChild + ")";
    }

    private static String helpInsertRoleOperations(int whatRole, int whatOperation) {
        return "INSERT INTO ROLE_OPERATIONS (ROLE_ID, OPERATION_ID) " +
                "VALUES (" + whatRole + ", " + whatOperation + ")";
    }

    private static String helpInsertUserOperations(int whatRole, int whatOperation) {
        return "INSERT INTO USER_OPERATIONS (USER_ID, OPERATION_ID) " +
                "VALUES (" + whatRole + ", " + whatOperation + ")";
    }

    private static void helpInsertUser(int id, String userName, int role_id) throws NoSuchAlgorithmException, SQLException {
        byte[] salt = getSalt();
        String passwordHash = hashPassword("test", salt);
        String sql = "INSERT INTO USERS (ROLE_ID, OPERATION_ID) " +
                "VALUES (" + id + ", " + userName + ", " + passwordHash + ", ? , " + role_id + ")";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setBytes(1, salt);
        stmt.execute();
    }

    private static void selectAllFromUserTable() {
        try {
            connect();

            connection.setAutoCommit(false);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS");

            while (rs.next()) {
                int id = rs.getInt("ID");
                String username = rs.getString("Name");
                String password = rs.getString("Password");
                byte[] salt = rs.getBytes("SALT");

                System.out.println("ID = " + id);
                System.out.println("NAME = " + username);
                System.out.println("PASSWORD = " + password);
                System.out.println("SALT = " + Arrays.toString(salt));
            }

            rs.close();
            stmt.close();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void selectAllFromConfigTable() {
        try {
            connect();

            connection.setAutoCommit(false);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CONFIG");

            while (rs.next()) {
                System.out.println(rs.getString("DESCRIPTION"));
                System.out.println(rs.getString("VALUE"));
            }

            rs.close();
            stmt.close();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
