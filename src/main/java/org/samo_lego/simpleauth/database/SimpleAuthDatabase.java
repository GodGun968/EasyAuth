package org.samo_lego.simpleauth.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Thanks to
 * @author sqlitetutorial.net
 */

public class SimpleAuthDatabase {
    private static final Logger LOGGER = LogManager.getLogger();

    // Connects to the DB
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:mods/SimpleAuth/players.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return conn;
    }

    // If the mod runs for the first time, we need to create the DB table
    public void makeTable() {
        try (Connection conn = this.connect()) {
            // Creating database table if it doesn't exist yet
            String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                    "  `UserID`    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `UUID`      BINARY(16)  NOT NULL,\n" +
                    "  `Username`  VARCHAR(16) NOT NULL,\n" +
                    "  `Password`  VARCHAR(64) NOT NULL,\n" +
                    "  UNIQUE (`UUID`)\n" +
                    "  UNIQUE (`Username`)\n" +
                    ");";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    // When player registers, we insert the data into DB
    public void insert(String uuid, String username, String password) {
        String sql = "INSERT INTO users(uuid, username, password) VALUES(?,?,?)";
        try (
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    // Deletes row containing the username provided
    public void delete(String uuid) {
        String sql = "DELETE FROM users WHERE uuid = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, uuid);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    // Updates the password of the user
    public void update(String uuid, String pass) {
        String sql = "UPDATE users SET password = ? "
                + "WHERE uuid = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, pass);
            pstmt.setString(2, uuid);

            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    // Gets the hashed password from DB
    public String getPassword(String uuid){
        String sql = "SELECT UUID, Password "
                + "FROM users WHERE UUID = ?";
        String pass = null;

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            // Setting statement
            pstmt.setString(1,uuid);
            ResultSet rs  = pstmt.executeQuery();

            // Getting the password
            pass = rs.getString("Password");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return pass;
    }
}