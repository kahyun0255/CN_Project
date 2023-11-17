package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Server.ClientHandler;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server Class: main()"); //FOR_DEBUG
        int port = 7778;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                // Handle the client in a separate thread
                new ClientHandler(socket).start();

                //시작되면 서버가 로그인 되어있는지 확인 메세지 보내 -> 네 알겟더요.

            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

