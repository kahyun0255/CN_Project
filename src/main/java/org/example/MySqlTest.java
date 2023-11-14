package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlTest {
    String dbDriver="com.mysql.cj.jdbc.Driver";
    String dbUrl="jdbc:mysql://127.0.0.1:3306/comnet?serverTimezone=Asia/Seoul&useSSL=false";
    String dbUser = "comnet";
    String dbPassword = "cat1234";

    public static Connection dbconn=null;

    public void dbConnection(){
        Connection connection = null;

        try
        {
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            dbconn = connection;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
