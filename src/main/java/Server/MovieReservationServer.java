package Server;

import static Server.ClientHandler.sendObjectData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.MovieReservationObject;
import org.example.MovieReservationObject.InputMovieDate;
import org.example.MovieReservationObject.MovieInfo;
import org.example.MovieReservationObject.MovieName;
import org.example.Pair;

public class MovieReservationServer {
    static int MovieId;
    static String Date;
    static String Time;
    static int PeopleNum;
    static String SeatNum;
    static String Protocol;

    static String userId = "kkh1234";


    MySqlTest mySqlTest = new MySqlTest();
    Scanner sc = new Scanner(System.in);
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;
//    Protocol = "0x000001101";

    public ArrayList<Pair<Integer, String>> MovieReservationMovieName(ClientHandler clientHandler) throws IOException {
        new MySqlTest().dbConnection();

        MovieReservationObject movieReservationObject = new MovieReservationObject();
        ArrayList<Pair<Integer, String>> movieNumArray = new ArrayList<>(); //이거 클라이언트한테 보내야함

        try {
            resultSet = mySqlTest.dbconn.createStatement().executeQuery("select * from movie"); //데이터베이스에서 긁어오기
            while (resultSet.next()) {
                int column1Value = resultSet.getInt("movieid");
                String column2Value = resultSet.getString("name");
                Pair<Integer, String> p = Pair.of(column1Value, column2Value);
                movieNumArray.add(p); //페어로 묶어 obj 형태로 보내기
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movieNumArray;
    }

    //            MovieId = sc.nextInt(); //나중에 지우기 -> 클라이언트단에서 오류 판단하고 올바른거만 줄듯??
//
    public ArrayList MovieReservationDate(ClientHandler clientHandler, int MovieId)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        ArrayList dateArray = new ArrayList<>(); //이거 클라이언트한테 보내야함
        resultSet = mySqlTest.dbconn.createStatement()
                .executeQuery("select distinct date from seat where movieid=" + MovieId);
        while (resultSet.next()) {
            String columnValue = resultSet.getString("date");
            dateArray.add(columnValue);

            System.out.println(columnValue); //나중에 지우기
        }
        return dateArray;
    }

    public ArrayList MovieReservationTime(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT DISTINCT time FROM seat WHERE movieid = ? AND date = ?"); //데이터 베이스에서 긁어오기
        pstmt.setInt(1, MovieId);
        pstmt.setString(2, Date.inputDate);
        resultSet = pstmt.executeQuery();
        ArrayList timeArray = new ArrayList<>(); //이거 클라이언트한테 보내야함
        while (resultSet.next()) {
            String columnValue = resultSet.getString("time");
            timeArray.add(columnValue);
        }
        return timeArray;
    }

    public ArrayList<Pair<String, Boolean>> MovieReservationSeatCheck(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        // 좌석의 예약 상태를 데이터베이스에서 가져옵니다.
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT number, `check` FROM seat WHERE movieid = ? AND date = ? AND time = ? ORDER BY number"
        );
        pstmt.setInt(1, MovieId);
        pstmt.setString(2, Date.inputDate);
        pstmt.setString(3, Time.inputTime);
        resultSet = pstmt.executeQuery();

        ArrayList<Pair<String, Boolean>> seatArray = new ArrayList<>(); //클라이언트한테 보내야함

        while (resultSet.next()) {
            String column1Value = resultSet.getString("number");
            boolean column2Value = resultSet.getBoolean("check");
            Pair<String, Boolean> p = Pair.of(column1Value, column2Value);
            seatArray.add(p); //페어로 묶어 obj 형태로 보내기
        }
        return seatArray;
    }

    public Object MovieReservationInfo(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time, MovieReservationObject.MovieSeatNum seatNum) throws IOException, SQLException {
        new MySqlTest().dbConnection();
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT name FROM movie WHERE movieid = ?"
        );
        pstmt.setInt(1, MovieId);
        resultSet = pstmt.executeQuery();

        String DBmovieName = null;
        if (resultSet.next()) {
            DBmovieName = resultSet.getString(1); // 또는 resultSet.getString("name");
            System.out.print(
                    "영화 제목 : " + DBmovieName + "\n날짜 : " + Date.inputDate + "\n시간 : " + Time.inputTime + "\n좌석 : ");
//              MovieReservationInfo(movieName, Date, Time, PeopleNum,seatNum); // 나중에 넣기 -> 클라이언트로 보낼 데이터임 근데 클래스 형태로 보내야해서 어떻게 보낼지는 생각해봐야할듯
            for (Object s : seatNum.seatNumArray) { //나중에 지우기 -> 향샹된 for문이 아닌 그냥 for문으로해서 마지막 콤마 제거하기
                System.out.print(s + ", ");
            }
            System.out.println(" ");
        }
        MovieReservationObject.MovieInfo movieInfo = new MovieReservationObject.MovieInfo(DBmovieName, Date.inputDate, Time.inputTime, seatNum.seatNumArray);
        return movieInfo;
    }

    public void MovieReservation(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time, MovieReservationObject.MovieSeatNum seatNum, int infoCheck) throws IOException, SQLException {
        new MySqlTest().dbConnection();
        if (infoCheck == 1) {
            for (int i = 0; i < seatNum.seatNumArray.size(); i++) { //seatNum ArrayList는 나중에 클라이언트쪽에서 보내줌
                String nowSeatNum = (String) seatNum.seatNumArray.get(i);

                pstmt = mySqlTest.dbconn.prepareStatement(
                        "UPDATE seat SET `check` = true WHERE movieid = ? AND date = ? AND time = ? AND number = ?"
                );
//                System.out.println("MovieId : " + MovieId + " Date : " + Date + " Time : " + Time + " nowSeatNum : "
//                        + nowSeatNum); //나중에 지우기 -> 확인용임...
                pstmt.setInt(1, MovieId);
                pstmt.setString(2, Date.inputDate);
                pstmt.setString(3, Time.inputTime);
                pstmt.setString(4, nowSeatNum);
                pstmt.executeUpdate(); //쿼리 수행

                //reservation에 정보 넣기
                String seatQuery = "SELECT Seatid FROM seat WHERE number = ?";
                pstmt = mySqlTest.dbconn.prepareStatement(seatQuery);
                pstmt.setString(1, nowSeatNum); // nowSeatNum은 사용자가 입력한 좌석 번호
                resultSet = pstmt.executeQuery();

                int seatId = -1;
                if (resultSet.next()) {
                    seatId = resultSet.getInt("Seatid");
                }
                if (seatId != -1) {
                    String insertQuery = "INSERT INTO Reservation (Seatid, userid) VALUES (?, ?)";
                    pstmt = mySqlTest.dbconn.prepareStatement(insertQuery);
                    pstmt.setInt(1, seatId);  // 찾은 Seatid 사용
                    pstmt.setString(2, userId);  // userId는 로그인중인 계정을 넣어야함... -> 로그인 유지 방법 생각해서 그에 맞게 변경하기...
                    pstmt.executeUpdate();
                }
            }
        } else {
            System.out.println("영화 예매를 종료하겠습니다. 다시 시작해주세요.");
        }
    }
}
