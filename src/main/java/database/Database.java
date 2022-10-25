package database;

import java.sql.*;
import java.util.Arrays;

import static database.Filer.getPath;
import static database.Hasher.getSalt;
import static database.Hasher.hashPassword;

public class Database {

    private static Connection connection = null;

    public Database() {
        createUserTable();
        createConfigTable();
        insertUserTable();
        selectAllFromUserTable();
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

    public ConfigDTO getConfig(int id) {
        try {
            connect();
            Statement stmt = connection.createStatement();

            String sql = "SELECT * FROM CONFIG WHERE ID = " + id + ";";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String description = rs.getString("DESCRIPTION");
                stmt.close();
                disconnect();
                return new ConfigDTO(description);
            }

            stmt.close();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void setConfig(int id, ConfigDTO config) {
        try {
            connect();

            Statement stmt = connection.createStatement();

            String sql = "UPDATE CONFIG SET DESCRIPTION = " + config.getDescription() + " WHERE ID = " + id + ";";
            stmt.executeUpdate(sql);
            stmt.close();

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

    private void createUserTable() {
        try {
            connect();

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE USERS " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "PASSWORD CHAR(512), " +
                    "SALT BLOB)";

            stmt.executeUpdate(sql);
            stmt.close();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createConfigTable() {
        try {
            connect();

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE CONFIG " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "DESCRIPTION TEXT NOT NULL)";

            stmt.executeUpdate(sql);
            stmt.close();

            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertUserTable() {
        try {
            connect();
            connection.setAutoCommit(false);

            PreparedStatement stmt = null;

            for (int i = 0; i < 10; i++) {
                String username = "User" + i;
                byte[] salt = getSalt();
                String password = hashPassword("test" + i, salt);

                String sql = "INSERT INTO USERS (ID, NAME, PASSWORD, SALT) " +
                        "VALUES (" + i + ", '" + username + "', '" + password + "', ?);";

                stmt = connection.prepareStatement(sql);
                stmt.setBytes(1, salt);
                stmt.executeUpdate();
            }

            stmt.close();

            connection.commit();
            disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void selectAllFromUserTable() {
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
}
