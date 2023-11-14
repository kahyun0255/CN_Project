package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSocketServer {

//    MySqlTest mysqlTest=new MySqlTest();

    private ExecutorService executors = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws IOException {
        MainSocketServer socketServer = new MainSocketServer();
        socketServer.run();
    }

    public void run() throws IOException {
        try {
            int port = 1717;
            ServerSocket server = new ServerSocket(port);

            while (true) {

                System.out.println("-------접속 대기중------");
                Socket socket = server.accept(); // 계속 기다리고 있다가 클라이언트가 접속하면 통신할 수 있는 소켓 반환
                System.out.println(socket.getInetAddress() + "로 부터 연결요청이 들어옴");
                InputStream is = socket.getInputStream();
                byte[] bytes = new byte[1024];

                int readByteCount = is.read(bytes);
                Thread newThread = new Thread(readByteCount, bytes, socket);
                executors.submit(newThread);
//                if (readByteCount > 0) {
//                    System.out.println("클라이언트로 부터 데이터 수신");
//                    MovieReservationServer.sendData(bytes, socket);
//                }
//                System.out.println("****** 재전송 완료 ****");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//class MySqlTest{
//    String dbDriver="com.mysql.cj.jdbc.Driver";
//    String dbUrl="jdbc:mysql://127.0.0.1:3306/comnet?serverTimezone=Asia/Seoul&useSSL=false";
//    String dbUser = "comnet";
//    String dbPassword = "cat1234";
//
//    public static Connection dbconn=null;
//
//    public void dbConnection(){
//        Connection connection = null;
//
//        try
//        {
//            Class.forName(dbDriver);
//            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
//            dbconn = connection;
//
//            //나중에 커넥션 멘트 삭제하기
//            System.out.println("DB Connection [성공]");
//        }
//        catch (SQLException e)
//        {
//            System.out.println("DB Connection [실패]");
//            e.printStackTrace();
//        }
//        catch (ClassNotFoundException e) {
//            System.out.println("DB Connection [실패]");
//            e.printStackTrace();
//        }
//    }
//}

class Thread implements Runnable {

    private int readByteCount;
    private byte[] bytes;
    private Socket socket;

    public Thread(int readByteCount, byte[] bytes, Socket socket) {
        this.readByteCount = readByteCount;
        this.bytes = bytes;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (readByteCount > 0) {
            System.out.println("클라이언트로 부터 데이터 수신");
            MovieReservationServer.sendData(bytes, socket);
        }
        System.out.println("****** 재전송 완료 ****");
    }
}
