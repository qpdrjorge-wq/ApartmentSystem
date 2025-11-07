package com.example.oop_javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
    public static Connection connectDB() throws SQLException, SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/rentManagement";
        String user = "root";
        String pass = "NLCaelumplx13";

        return DriverManager.getConnection(url, user, pass);
    }
}