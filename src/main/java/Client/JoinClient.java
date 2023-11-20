package Client;

import org.example.Join;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import static Client.Client.*;
import static Client.Client.C_isOK;

public class JoinClient {
    Client client = new Client(new Socket());

    public static void joinPage(Socket socket) throws IOException {
        Scanner sc = new Scanner(System.in);

        sendData(socket, JOIN, 2);

        receiveData(socket);
        if (C_isOK == 1) {
            System.out.print("아이디: ");
            String id = sc.next();
            System.out.print("이름: ");
            String name = sc.next();
            System.out.print("비밀번호: ");
            String pw = sc.next();
            C_isOK = 0;

            Join.JoinInfo joinInfo = new Join.JoinInfo(id, name, pw);

            sendObjectData(socket, JOIN, joinInfo);

            receiveData(socket);
            if (C_isOK == 1) {
                System.out.println("회원가입이 완료되었습니다.");
                C_isOK = 0; //변수 초기화

            }
        }
    }
}