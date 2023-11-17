//package org.example;
//
//import java.io.*;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Scanner;
//import org.example.MovieReservationObject.MovieInfo;
//
//public class MainSocketClient {
//    public static void main(String[] args) throws IOException {
//        MainSocketClient cm = new MainSocketClient();
//        cm.run();
//    }
//
//    void run() throws IOException {
//        //소켓 생성
//        Socket socket = new Socket();
////        MovieReservationClient socketClient = new MovieReservationClient();
////        MovieReservationServer socketClient = new MovieReservationServer();
//        MovieReservationServer socketClient = new MovieReservationServer();
//        socketClient.MovieReservation();
////        MovieInfo movieInfo = socketClient.MovieReservation();
//        Scanner sc = new Scanner(System.in);
//        SocketAddress address = new InetSocketAddress("127.0.0.1", 1717);
//        socket.connect(address);
//
//        try {
////            send(socket, movieInfo);
//            send(socket);
//            receive(socket);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    public static void send(Socket socket, MovieInfo movieInfo) throws IOException {
////        //생성한 person 객체를 byte array로 변환
////        byte[] data = toByteArray(movieInfo);
////        //서버로 내보내기 위한 출력 스트림 뚫음
////        OutputStream os = socket.getOutputStream();
////        //출력 스트림에 데이터 씀
////        os.write(data);
////        //보냄
////        os.flush();
////    }
//    public static void send(Socket socket) throws IOException {
//        //생성한 person 객체를 byte array로 변환
//        byte[] data = toByteArray("hi");
//        //서버로 내보내기 위한 출력 스트림 뚫음
//        OutputStream os = socket.getOutputStream();
//        //출력 스트림에 데이터 씀
//        os.write(data);
//        //보냄
//        os.flush();
//    }
//
//    public static void receive(Socket socket) throws IOException {
//        //수신 버퍼의 최대 사이즈 지정
//        int maxBufferSize = 1024;
//        //버퍼 생성
//        byte[] recvBuffer = new byte[maxBufferSize];
//        //서버로부터 받기 위한 입력 스트림 뚫음
//        InputStream is = socket.getInputStream();
//        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
//        int nReadSize = is.read(recvBuffer);
//
//        //받아온 값이 0보다 클때 = 받은 값이 존재할 때
//        if (nReadSize > 0) {
//            MovieInfo receiveMovieInfo = toObject(recvBuffer, MovieInfo.class);
//        }
//    }
//
//    public static byte[] toByteArray(Object obj) {
//        byte[] bytes = null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(bos);
//            oos.writeObject(obj);
//            oos.flush();
//            oos.close();
//            bos.close();
//            bytes = bos.toByteArray();
//        } catch (IOException ex) {
//            //TODO: Handle the exception
//        }
//        return bytes;
//    }
//
////    public static <T> T toObject(byte[] bytes, Class<T> type) {
////        Object obj = null;
////        try {
////            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
////            ObjectInputStream ois = new ObjectInputStream(bis);
////            obj = ois.readObject();
////        } catch (IOException ex) {
////            //TODO: Handle the exception
////        } catch (ClassNotFoundException ex) {
////            //TODO: Handle the exception
////        }
////        return type.cast(obj);
////    }
//    public static <T> T toObject(byte[] bytes, Class<T> type) {
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
//            ObjectInputStream ois = new ObjectInputStream(bis);
//            Object obj = ois.readObject();
//            return type.cast(obj);
//        } catch (IOException | ClassNotFoundException | ClassCastException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
