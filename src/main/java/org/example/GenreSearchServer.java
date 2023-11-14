package org.example;// GenreSearchServer.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenreSearchServer {
    static String Protocol;
    MySqlTest mySqlTest = new MySqlTest();

    void GenreSearch() throws  IOException {
        new MySqlTest().dbConnection();
        Scanner sc = new Scanner(System.in);
        Protocol = "0x000001110";

        ResultSet resultSet = null;

        try {
            //장르목록
            resultSet = MySqlTest.dbconn.createStatement().executeQuery("SELECT DISTINCT genre1, genre2 FROM movie");
            List<String> GenreArray = new ArrayList<>();
            while(resultSet.next()) {
                GenreArray.add(String.valueOf(resultSet));
            }
            //영화제목
            resultSet = MySqlTest.dbconn.createStatement().executeQuery("SELECT name FROM movie WHERE genre1 = ? OR genre2 = ?");
            List<String> MovieNameArray = new ArrayList<>();
            while(resultSet.next()) {
                MovieNameArray.add(String.valueOf(resultSet));
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
