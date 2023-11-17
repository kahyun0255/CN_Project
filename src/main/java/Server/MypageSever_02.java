//package Server;
//
//import Server.DatabaseEunYeong;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MypageSever_02 {
//
//    public static void Mypage() throws IOException {
//        Connection conn = DatabaseEunYeong.connect();
//
//        try {
//            String query = "SELECT id AS user_name FROM userinfo;";
//
//            try (PreparedStatement stmt = conn.prepareStatement(query)) {
//                try (ResultSet rs = stmt.executeQuery()) {
//                    List<userid> useridList = new ArrayList<>();
//                    while (rs.next()) {
//                        String userid = rs.getString("user_name");
//                        userid infoObject = new userid(userid);
//                        useridList.add(infoObject);
//                    }
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    ObjectOutputStream oos = new ObjectOutputStream(baos);
//                    oos.writeObject(useridList);
//                    oos.flush();
//                    byte[] infoBytes = baos.toByteArray();
//
//                    out.writeInt(infoBytes.length);
//                    out.write(infoBytes);
//                }
//            }
//        }
//    }
//                    int file = in.readInt();  //서버는 클라이언트로부터 데이터 길이를 받아옴.
//                    byte[] id = new byte[file]; //데이터 길이만큼 버퍼 생성.
//                    in.readFully(id);
//                    String receivedString = new String(id, StandardCharsets.UTF_8);
//                    try (Connection conn = DatabaseEunYeong.connect()) {
//                        String query = "SELECT movie.name AS movie_name, seat.date AS seat_date, seat.time AS seat_time, seat.number AS seat_number, userinfo.name AS user_name " +
//                                "FROM movie " +
//                                "JOIN seat ON movie.movieid = seat.movieid " +
//                                "JOIN reservation ON reservation.seatid = seat.seatid " +
//                                "JOIN userinfo ON reservation.userid = userinfo.id " +
//                                "WHERE userinfo.id = ?;";
//
//                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//                            stmt.setString(1, receivedString);  // 쿼리의 첫 번째 파라미터를 설정합니다.  ==> ?로 특정 값을 찾을 때 쓰는 함수.
//
//                            try (ResultSet rs = stmt.executeQuery()) {
//                                // 결과를 저장할 리스트 생성
//                                List<info> useridList = new ArrayList<>();
//
//                                // 조회된 모든 행에 대해 반복
//                                while (rs.next()) {
//                                    String title = rs.getString("movie_name");
//                                    String date = rs.getString("seat_date");
//                                    String time = rs.getString("seat_time");
//                                    String userid = rs.getString("user_name");
//                                    String seatNumber = rs.getString("seat_number");
//
//                                    // 각 행에 대한 정보를 Mypage.info 객체에 저장하고 리스트에 추가
//                                    info infoObject = new info(title, date, time, userid, seatNumber);
//                                    useridList.add(infoObject);
//                                }
//
//                                // Mypage.info 객체의 리스트를 바이트 배열로 직렬화
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                ObjectOutputStream oos = new ObjectOutputStream(baos);
//                                oos.writeObject(useridList);
//                                oos.flush();
//                                byte[] infoBytes = baos.toByteArray();
//
//                                // 클라이언트에게 객체 리스트의 바이트 길이와 객체 데이터를 전송
//                                out.writeInt(infoBytes.length);
//                                out.write(infoBytes);
//                            }
//
//                        }
//                    } catch (SQLException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//
////package org.example;
////
////import java.io.*;
////import java.net.ServerSocket;
////import java.net.Socket;
////import java.nio.ByteBuffer;
////import java.nio.charset.StandardCharsets;
////import java.sql.PreparedStatement;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////
////public class MypageSever_02 {
////    public static void main(String[] args) {
////        // 데이터베이스 연결 초기화
////        MySqlTest mySqlTest = new MySqlTest();
////        mySqlTest.dbConnection();
////
////        try (ServerSocket serverSocket = new ServerSocket(8080)) {
////            System.out.println("Server started...");
////
////            // 데이터베이스 연결 초기화 (MySqlTest 클래스에서 가져온 방식)
////            MySqlTest mySqlTest = new MySqlTest();
////            mySqlTest.dbConnection();
////
////            while (true) {
////                try (Socket clientSocket = serverSocket.accept();
////                     DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
////                     DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
////
////                    // 클라이언트로부터 데이터 받기
////                    int fileSize = in.readInt();
////                    byte[] data = new byte[fileSize];
////                    in.readFully(data);
////                    int number = ByteBuffer.wrap(data).getInt();
////
////                    if (number == 3) {
////                        // 데이터베이스 연결 및 쿼리 실행
////                        String query = "SELECT id AS user_name FROM userinfo;";
////                        try (PreparedStatement stmt = MySqlTest.dbconn.prepareStatement(query);
////                             ResultSet rs = stmt.executeQuery()) {
////
////                            List<userid> useridList = new ArrayList<>();
////
////                            while (rs.next()) {
////                                String userid = rs.getString("user_name");
////                                userid infoObject = new userid(userid);
////                                useridList.add(infoObject);
////                            }
////
////                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                            ObjectOutputStream oos = new ObjectOutputStream(baos);
////                            oos.writeObject(useridList);
////                            oos.flush();
////                            byte[] infoBytes = baos.toByteArray();
////
////                            out.writeInt(infoBytes.length);
////                            out.write(infoBytes);
////                        }
////                    }
////
////                    // ... 추가적인 데이터 처리 및 응답 ...
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//class userid implements Serializable {
//    String userid;
//    userid(String userid) {
//        this.userid = userid;
//    }
//}
//class info implements Serializable {
//    String title;
//    String date;
//    String time;
//    String username;
//    String seat;
//
//    info(String title, String date, String time, String username, String seat) {
//        this.title = title;
//        this.date = date;
//        this.time = time;
//        this.username = username;
//        this.seat = seat;
//    }
//}