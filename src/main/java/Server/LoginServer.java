package Server;

import org.example.Pair;

import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static Server.ClientHandler.*;

public class LoginServer {
    MySqlTest mySqlTest = new MySqlTest();
    Scanner sc = new Scanner(System.in);
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;
    public void loginServer(Socket socket) throws IOException, SQLException {
        new MySqlTest().dbConnection();

        IsOK = 1;
        sendData(socket,LOGIN,0);

        String inputId="kkh1234";
        String inputPw="kk1234";
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT * FROM userinfo WHERE id = ? and password = ?"
        );
        pstmt.setString(1, inputId);
        pstmt.setString(2, inputPw);
        resultSet = pstmt.executeQuery();

        if (resultSet.next()) {
            // 결과 집합에 최소 한 개의 행이 있다면, 아이디가 존재함
            IdNum = 1;
            IsOK = 1;
        }
//         else {
//            // 결과 집합이 비어 있다면, 아이디가 존재하지 않음
//            IdNum=0;
//            IsOK=0;
//        }
//        IdNum = 1;
//        IsOK = 1;
        sendData(socket,LOGIN|0x4,0); //로그인 성공햤다고 가정... -> 헤더가 안가서 그런가 아래로 더 안가짐
        //////
        ///없으면///
        ///그런 경우는 없어


    }
}