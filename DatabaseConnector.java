package com.example.assignment1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/imdb_movie_data";
        String username = "root";
        String password = "Harsh@2003";

        try {
            // Establishing the connection
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");

            // You can perform database operations here

            // Don't forget to close the connection when done
            connection.close();
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console for errors.");
            e.printStackTrace();
        }
    }
}
