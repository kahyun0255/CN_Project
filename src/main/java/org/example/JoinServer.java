package org.example;// SignUpServer.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class JoinServer {
    static String id;
    static String name;
    static String password;
    static String Protocol;
    MySqlTest mySqlTest = new MySqlTest();
    public static void processJoinData(Socket clientSocket) {
        new MySqlTest().dbConnection();
        Scanner sc = new Scanner(System.in);
        Protocol = "0x000001010";

        PreparedStatement pstmt = null;
        try {


            ArrayList JoinData = new ArrayList<>();

            // 데이터베이스에 회원가입 정보 입력
            pstmt = MySqlTest.dbconn.prepareStatement("INSERT INTO userinfo (id, name, password) VALUES (?, ?, ?)");
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
//            String sql = "INSERT INTO userinfo (id, name, password) VALUES (?, ?, ?)";
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//                preparedStatement.setString(1, id);
//                preparedStatement.setString(2, name);
//                preparedStatement.setString( 3, password);
//                preparedStatement.executeUpdate();
//            }

            System.out.println("회원가입 정보를 데이터베이스에 저장했습니다.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
