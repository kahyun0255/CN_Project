package Server;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import org.example.MyPageObject;

public class MypageSever_02 {
    MySqlTest mySqlTest = new MySqlTest();
    Scanner sc = new Scanner(System.in);
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;
    public Object Mypage(ClientHandler clientHandler, String UserID) throws IOException {
        new MySqlTest().dbConnection();

//        MyPageObject myPageObject = new MyPageObject();

        MyPageObject.MyPageInfo mypageinfo;
        try {
            pstmt = mySqlTest.dbconn.prepareStatement(
                    "SELECT " +
                            "u.id AS userid, " +
                            "u.name AS username, " +
                            "m.name AS movie_name, " +
                            "s.date, " +
                            "s.time, " +
                            "s.number AS seat_number " +
                            "FROM " +
                            "reservation r " +
                            "JOIN seat s ON r.seatid = s.seatid " +
                            "JOIN userinfo u ON r.userid = u.id " +
                            "JOIN movie m ON s.movieid = m.movieid " +
                            "WHERE " +
                            "u.id = ?"
            );
            pstmt.setString(1, UserID);
            resultSet = pstmt.executeQuery();

            ArrayList<MyPageObject.MyPageMovieInfo> UserInfoMovie = new ArrayList<>(); //이거 클라이언트한테 보내야함
            String userName = null;
            String userId=null;
            while (resultSet.next()) {
                userId = resultSet.getString("userid");
                userName = resultSet.getString("username");
                String movieName = resultSet.getString("movie_name");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String seatNumber = resultSet.getString("seat_number");

                // 이 정보들을 MyPageObject.MyPageMovieInfo 객체에 담아 리스트에 추가
                MyPageObject.MyPageMovieInfo info = new MyPageObject.MyPageMovieInfo(movieName, date, time, seatNumber);
                UserInfoMovie.add(info);
            }
            mypageinfo = new MyPageObject.MyPageInfo(userId, userName, UserInfoMovie);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return mypageinfo;
    }
}
