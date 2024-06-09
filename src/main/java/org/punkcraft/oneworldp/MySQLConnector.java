package org.punkcraft.oneworldp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnector {
    private Connection connection;

    public MySQLConnector(String host, String database, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdatePlayerData(String uuid) {
        try {
            String query = "INSERT INTO SwapData (uuid, change_loc_flag) VALUES (?, TRUE) ON DUPLICATE KEY UPDATE change_loc_flag = TRUE";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
