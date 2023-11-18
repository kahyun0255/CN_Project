package Client;

import org.example.Join;
import org.example.Login;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static Client.Client.*;

public class LoginClient{
    Client client = new Client(new Socket());
    Scanner sc = new Scanner(System.in);
    public int loginPage(Socket socket) throws IOException {

        System.out.println("\nClient :: loginPage()"); //FOR_DEBUG
        sc = new Scanner(System.in);

        int menuNum = 0;

        while (true) {
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.print("입력하세요: ");

            //입력값 체크
            while (true) {
                menuNum = sc.nextInt();
                if (menuNum > 2) {
                    System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
                } else {
                    break;
                }
            }

            if (menuNum == 1) {
                //서버에 로그인 정보가 갈꺼라고 send
                sendData(socket, LOGIN, 1);

                //receive
                int data = receiveData(socket);
                if (C_isOK == 1) {
                    //서버에서 알겠다고 ok 오면 send
                    System.out.print("아이디: ");
                    String id = sc.next();
                    System.out.print("비밀번호: ");
                    String pw = sc.next();

                    C_isOK = 0;
                    Login.LoginInfo logInfo = new Login.LoginInfo(id, pw);
                    sendObjectData(socket, LOGIN, logInfo);

                    receiveData(socket);
                    final int idNum = C_idNum;
                    System.out.printf("idNum: %d\n", idNum);
                    if (C_isOK == 1) {
                        System.out.println("로그인에 성공하셨습니다");
                        return 1;
                    } else {
                        System.out.println("로그인에 실패하셨습니다");
                    }
                    C_isOK = 0;

                }
            } else if (menuNum == 2) {
                System.out.println("회원가입 페이지 입니다.");

                sendData(socket, JOIN, 2);

                receiveData(socket);
                if (C_isOK == 1) {
                    System.out.print("이름: ");
                    String j_name = sc.next();
                    System.out.print("아이디: ");
                    String j_id = sc.next();
                    System.out.print("비밀번호: ");
                    String j_pw = sc.next();
                    C_isOK = 0;

                    Join.JoinInfo joinInfo = new Join.JoinInfo(j_name, j_id, j_pw);

                    sendObjectData(socket, JOIN, joinInfo);
                    System.out.println("2\n");

                    receiveData(socket);
                    if (C_isOK == 1) {
                        System.out.println("회원가입이 완료되었습니다.");
                        C_isOK = 0; //변수 초기화
                    }
                }
            }


        }
    }
}