package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CompletedTaskDBConnection {

    private static CompletedTaskDBConnection instance;

    private Connection connection;

    private CompletedTaskDBConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todoList", "root", "1234");

    }

    public Connection getConnection(){

        return connection;
    }
    public static CompletedTaskDBConnection getInstance() throws SQLException {

        return  instance==null?instance=new CompletedTaskDBConnection():instance;
    }
}
