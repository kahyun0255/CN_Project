package org.example;

import java.sql.*;

public class DatabaseEunYeong {

    // 데이터베이스 연결을 위한 자격증명
    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/comnet?serverTimezone=Asia/Seoul&useSSL=false";
    private static final String DATABASE_USER = "comnet";
    private static final String DATABASE_PASSWORD = "cat1234";

    // JDBC 드라이버 로드
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 데이터베이스에 연결
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            System.out.println("Successfully connected to the database.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    // 샘플 쿼리 실행
    public static void executeSampleQuery() {
        String sql = "SELECT movie.name AS movie_name, seat.date AS seat_date, seat.time AS seat_time, seat.number AS seat_number, userinfo.name AS user_name " +
                "FROM movie " +
                "JOIN seat ON movie.movieid = seat.movieid " +
                "JOIN reservation ON reservation.seatid = seat.seatid " +
                "JOIN userinfo ON reservation.userid = userinfo.id " +
                "WHERE userinfo.id = 'yangyang';"; // 'your_table'은 실제 테이블 이름으로 바꿔야 함
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 컬럼 이름이나 인덱스를 사용하여 데이터 추출
                String userid = rs.getString("userinfo.id");
                String title = rs.getString("movie.name");
                String date = rs.getString("seat.date");
                String time = rs.getString("seat.time");
                String username = rs.getString("userinfo.name");
                String seat = rs.getString("seat.number");
                if(userid.equals("yangyang")) {
                    System.out.println("회원 이름: " + username);
                    System.out.println("영화 제목: " + title);
                    System.out.println("예매 날짜: " + date);
                    System.out.println("예매 시간: " + time);
                    System.out.println("예매 좌석: " + seat);
                }
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        executeSampleQuery();
    }
} //데이터베이스 클래스 작동하는지 실행하는 문.. 서버랑은 상관없음...