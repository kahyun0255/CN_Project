package org.example;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class JoinClient {
    public static void run(Socket mainSocket) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("아이디: ");
            String id = scanner.nextLine();

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("비밀번호: ");
            String password = scanner.nextLine();

            // 회원가입 정보를 객체 또는 문자열로 만들어서 메인 소켓 클라이언트를 통해 메인 소켓 서버로 전송
            String signUpData = id + "," + name + "," + password;
            ObjectOutputStream oos = new ObjectOutputStream(mainSocket.getOutputStream());
            oos.writeUTF(signUpData);
            oos.flush();

            System.out.println("회원가입이 완료되었습니다.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}