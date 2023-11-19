package Server;

import antlr.PrintWriterWithSMAP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Server.ClientHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread{
    static List<PrintWriter> users= Collections.synchronizedList(new ArrayList<PrintWriter>());
    Socket socket;
    static BufferedReader in=null;
    PrintWriter out=null;
    static int name;
    public Server(Socket socket) throws IOException {
        this.socket=socket;
        try{
            out=new PrintWriter(socket.getOutputStream());
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            users.add(out);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("Server Class: main()"); //FOR_DEBUG
        int port = 7778;
        try {
            ServerSocket ss=new ServerSocket(port);
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = ss.accept();
                System.out.println("New client connected");
                // Handle the client in a separate thread
                Thread serverThread = new Server(socket);
                name=in.read();
                System.out.println("------user 들어옴 "+name+"-----");
                System.out.println(users.size());
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

