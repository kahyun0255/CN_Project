package Server;// SignUpServer.java
import org.example.Join;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static Server.ClientHandler.*;
import static Server.ClientHandler.JOIN;

public class JoinServer {
    MySqlTest mySqlTest = new MySqlTest();
    PreparedStatement pstmt = null;
    public void joinServer(Socket socket) throws IOException, SQLException {

        new MySqlTest().dbConnection();

        //DB에서 데이터 받아옴
        IsOK = 1;
        sendData(socket,JOIN,0);
        byte[] joinObjectData = receiveObjectData(socket);
        Join.JoinInfo joinInfo = toObject(joinObjectData, Join.JoinInfo.class);

        String id = joinInfo.id;
        String name = joinInfo.name;
        String password = joinInfo.pw;
        //받은 정보 DB에 저장
        // 데이터베이스에 연결
        String sql = "INSERT INTO userinfo (id, name, password) VALUES (?, ?, ?)";
        pstmt = mySqlTest.dbconn.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.setString(2, name);
        pstmt.setString(3, password);
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            System.out.println("회원가입 정보를 데이터베이스에 저장했습니다.");
            // 성공 메시지 전송 로직 (생략)
            //저장완료되면 ok 사인 보내기
            IsOK = 1;
            sendData(socket,JOIN,0);
        } else {
            System.out.println("데이터 저장 실패.");
            // 실패 메시지 전송 로직 (생략)
        }

        // PreparedStatement와 연결 닫기
        if (pstmt != null)
            pstmt.close();
        mySqlTest.closeConnection(); // 데이터베이스 연결 닫기


    }
}
