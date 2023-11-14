package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MypageClient_02 {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8081;

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            Scanner sc = new Scanner(System.in);  //클라이언트에서 메인이 실행하는 기능을 넣는다면
            Manager m = new Manager(); //클라이언트에서 메인이 실행하는 기능을 넣는다면
            int number; //클라이언트에서 메인이 실행하는 기능을 넣는다면
            List<userid> receivedInfoList = new ArrayList<>();
            List<info> InfoList = new ArrayList<>();
            sc = new Scanner(System.in); //클라이언트에서 메인이 실행하는 기능을 넣는다면
            //메인 선택창
            m.displayMainMenu(); //클라이언트에서 메인이 실행하는 기능을 넣는다면
            number = sc.nextInt(); //클라이언트에서 메인이 실행하는 기능을 넣는다면
            //==> 입력받음
            out.writeInt(4);
            byte[] dataToSend = ByteBuffer.allocate(4).putInt(number).array();
            out.write(dataToSend); //실제로 데이터를 보내는 부분.
            out.flush(); //버퍼에 남아있는 모든 데이터를 강제로 서버로 전송.
            // 이것은 네트워크 통신에서 데이터가 성공적으로 전송되었음을 보장하는 데 도움이 된다.

            int dataSize = in.readInt();
            byte[] receivedData = new byte[dataSize];
            in.readFully(receivedData);

// 바이트 배열을 객체 리스트로 역직렬화하기
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                receivedInfoList = (List<userid>) ois.readObject(); // 역직렬화하여 객체 리스트를 생성합니다.

                // 받은 객체 리스트의 정보를 출력
//                for (Mypage.info receivedInfo : receivedInfoList) {
//                    System.out.println("영화 제목: " + receivedInfo.title);
//                    System.out.println("예매 날짜: " + receivedInfo.date);
//                    System.out.println("예매 시간: " + receivedInfo.time);
//                    System.out.println("예매 좌석: " + receivedInfo.seat);
//                    System.out.println("회원 이름: " + receivedInfo.username);
//                    // 추가적인 출력 또는 처리 ...
//                }
                for (userid receivedInfo : receivedInfoList) {
                    System.out.println("회원 이름: " + receivedInfo.userid);
                    // 추가적인 출력 또는 처리 ...
                } //정보가 올바르게 왔는지 확인 용도...


            } catch (ClassNotFoundException e) {
                System.err.println("Class not found for deserialization: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IO Exception during deserialization: " + e.getMessage());
                e.printStackTrace();
            }

            while (true) {
                System.out.print("아이디를 입력하세요: ");
                String inputUserId = sc.next();

                boolean isMatched = false;
                for (userid receivedUserId : receivedInfoList) {
                    if (receivedUserId.userid.equals(inputUserId)) {
                        isMatched = true;
                        break;
                    }
                }

                if (isMatched) {
                    // 사용자 아이디가 일치하면 서버에 전송하고 루프를 종료
                    byte[] data = inputUserId.getBytes(StandardCharsets.UTF_8);
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();
                    break;  // while 루프 종료
                } else {
                    System.out.println("아이디가 일치하지 않습니다. 다시 입력해주세요.");
                }
            }

            int Size = in.readInt();
            byte[] Data = new byte[Size];
            in.readFully(Data);

            try {
                ByteArrayInputStream bai = new ByteArrayInputStream(Data);
                ObjectInputStream ois = new ObjectInputStream(bai);
                InfoList = (List<info>) ois.readObject(); // 역직렬화하여 객체 리스트를 생성합니다.
                if(!InfoList.isEmpty()) {
                    // 받은 객체 리스트의 정보를 출력
                    for (info receivedInfo : InfoList) {
                        System.out.println("영화 제목: " + receivedInfo.title);
                        System.out.println("예매 날짜: " + receivedInfo.date);
                        System.out.println("예매 시간: " + receivedInfo.time);
                        System.out.println("예매 좌석: " + receivedInfo.seat);
                        System.out.println("회원 이름: " + receivedInfo.username);
                        // 추가적인 출력 또는 처리 ...
                    }
                }
                else System.out.println("예매정보가 없습니다.");

            } catch (ClassNotFoundException e) {
                System.err.println("Class not found for deserialization: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IO Exception during deserialization: " + e.getMessage());
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

