package Server;

import org.example.Login;
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
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    public void loginServer(ClientHandler clientHandler, Socket socket) throws IOException, SQLException {
        new MySqlTest().dbConnection();

        IsOK = 1;
        sendData(socket, LOGIN, 0);

        byte[] loginObjectData = receiveObjectData(socket);
        Login.LoginInfo loginInfo = toObject(loginObjectData,Login.LoginInfo.class);

        String inputId = loginInfo.id;
        String inputPw = loginInfo.pw;

        // 쿼리 실행 전에 PreparedStatement를 준비합니다.
        pstmt = mySqlTest.dbconn.prepareStatement("SELECT * FROM userinfo WHERE id = ? and password=?");
        pstmt.setString(1, inputId);
        pstmt.setString(2, inputPw);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            //ID Num 수정 필요

            IdNum = clientHandler.getSessionID();//1;
            IsOK = 1;
            sendData(socket, LOGIN | 0x4, 0);
            IsOK = 0;
            ClientHandler.addUserId(clientHandler.getSessionID(), inputId);
        } else {
            // 결과 집합이 비어 있다면, 아이디가 존재하지 않음
            IdNum = 0;
            IsOK = 0;
            sendData(socket, LOGIN, 0);
        }

    }
}