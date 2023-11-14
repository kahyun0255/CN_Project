package org.example;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class JoinClient {
    public static void run(Socket mainSocket) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("아이디: ");
        String id = scanner.nextLine();

        System.out.print("이름: ");
        String name = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();


        System.out.println("회원가입이 완료되었습니다.");

    }
}