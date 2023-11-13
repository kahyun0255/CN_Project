package org.example;// SignUpServer.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinServer {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dbplus";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    public static void processSignUpData(Socket clientSocket) {
        try {
            // 회원가입 정보를 받음
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String signUpData = ois.readUTF();

            // 회원가입 정보를 적절히 처리
            String[] signUpArray = signUpData.split(",");
            String id = signUpArray[0];
            String name = signUpArray[1];
            String password = signUpArray[2];

            // 데이터베이스에 연결
            Connection connection = getConnection();

            // 데이터베이스에 회원가입 정보 입력
            String sql = "INSERT INTO userinfo (id, name, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString( 3, password);
                preparedStatement.executeUpdate();
            }

            System.out.println("회원가입 정보를 데이터베이스에 저장했습니다.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
