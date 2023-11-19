package Server;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import org.example.MovieReservationObject;
import org.example.Pair;

public class MovieReservationServer {
    MySqlTest mySqlTest = new MySqlTest();
    Scanner sc = new Scanner(System.in);
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

//    Protocol = "0x000001101";

    public ArrayList<Pair<Integer, String>> MovieReservationMovieName(ClientHandler clientHandler, String userId) throws IOException {
        new MySqlTest().dbConnection();

        MovieReservationObject movieReservationObject = new MovieReservationObject();
        ArrayList<Pair<Integer, String>> movieNumArray = new ArrayList<>(); //이거 클라이언트한테 보내야함

        try {
            System.out.println("----MovieResUserID "+userId);
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
    public ArrayList MovieReservationDate(ClientHandler clientHandler, int MovieId, String userId)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        ArrayList dateArray = new ArrayList<>(); //이거 클라이언트한테 보내야함
        resultSet = mySqlTest.dbconn.createStatement()
                .executeQuery("select distinct date from seat where movieid=" + MovieId);

        System.out.println("------MovieRes movid id Date "+MovieId);

        while (resultSet.next()) {
            String columnValue = resultSet.getString("date");
            dateArray.add(columnValue);

            System.out.println(columnValue); //나중에 지우기
        }
        return dateArray;
    }

    public ArrayList MovieReservationTime(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, String userId)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT DISTINCT time FROM seat WHERE movieid = ? AND date = ?"); //데이터 베이스에서 긁어오기
        pstmt.setInt(1, MovieId);
        pstmt.setString(2, Date.inputDate);
        resultSet = pstmt.executeQuery();
        System.out.println("------MovieRes movid id Time "+MovieId);
        ArrayList timeArray = new ArrayList<>(); //이거 클라이언트한테 보내야함
        while (resultSet.next()) {
            String columnValue = resultSet.getString("time");
            timeArray.add(columnValue);
        }
        return timeArray;
    }

    public ArrayList<Pair<String, Boolean>> MovieReservationSeatCheck(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time, String userId)
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

        System.out.println("------MovieRes movid id SeatCheck "+MovieId);
        System.out.println("------MovieRes movid id Date "+Date.inputDate);
        System.out.println("------MovieRes movid id Time "+Time.inputTime);

        ArrayList<Pair<String, Boolean>> seatArray = new ArrayList<>(); //클라이언트한테 보내야함

        while (resultSet.next()) {
            String column1Value = resultSet.getString("number");
            boolean column2Value = resultSet.getBoolean("check");
            Pair<String, Boolean> p = Pair.of(column1Value, column2Value);
            seatArray.add(p); //페어로 묶어 obj 형태로 보내기
        }
        return seatArray;
    }

    public Object MovieReservationInfo(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time, MovieReservationObject.MovieSeatNum seatNum, String userId)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT name FROM movie WHERE movieid = ?"
        );
        pstmt.setInt(1, MovieId);
        resultSet = pstmt.executeQuery();

        System.out.println("------MovieRes movid id Info "+MovieId);
        System.out.println("------MovieRes movid id Date "+Date.inputDate);
        System.out.println("------MovieRes movid id Time "+Time.inputTime);

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

    public int MovieReservation(ClientHandler clientHandler, int MovieId, MovieReservationObject.InputMovieDate Date, MovieReservationObject.InputMovieTime Time, MovieReservationObject.MovieSeatNum seatNum, int infoCheck, String userId)
            throws IOException, SQLException {
        new MySqlTest().dbConnection();
        if (infoCheck == 1) {
            for (int i = 0; i < seatNum.seatNumArray.size(); i++) { //seatNum ArrayList는 나중에 클라이언트쪽에서 보내줌
                String nowSeatNum = (String) seatNum.seatNumArray.get(i);

                String checkQuery = "SELECT `check` FROM seat WHERE number = ? AND movieid = ? AND date = ? AND time = ?";
                pstmt = mySqlTest.dbconn.prepareStatement(checkQuery);
                pstmt.setString(1, nowSeatNum);
                pstmt.setInt(2, MovieId);
                pstmt.setString(3, Date.inputDate);
                pstmt.setString(4, Time.inputTime);
                ResultSet checkResult = pstmt.executeQuery();

                if (checkResult.next() && checkResult.getBoolean("check")) {
                    System.out.println("선택하신 좌석 " + nowSeatNum + "은(는) 이미 예약되었습니다. 다른 좌석을 선택해주세요.");
                    return 3;
                }

                pstmt = mySqlTest.dbconn.prepareStatement(
                        "UPDATE seat SET `check` = true WHERE movieid = ? AND date = ? AND time = ? AND number = ?"
                );
                System.out.println("Mvoie Reservation 에서... MovieId : " + MovieId + " Date : " + Date + " Time : " + Time + " nowSeatNum : "
                        + nowSeatNum); //나중에 지우기 -> 확인용임...
                pstmt.setInt(1, MovieId);
                pstmt.setString(2, Date.inputDate);
                pstmt.setString(3, Time.inputTime);
                pstmt.setString(4, nowSeatNum);
                pstmt.executeUpdate(); //쿼리 수행

                // 예약 정보 삽입 전에 정확한 seatid 찾기
                String seatQuery = "SELECT Seatid FROM seat WHERE number = ? AND movieid = ? AND date = ? AND time = ?";
                pstmt = mySqlTest.dbconn.prepareStatement(seatQuery);
                pstmt.setString(1, nowSeatNum); // 현재 선택된 좌석 번호
                pstmt.setInt(2, MovieId);       // 영화 ID
                pstmt.setString(3, Date.inputDate); // 선택된 날짜
                pstmt.setString(4, Time.inputTime); // 선택된 시간
                resultSet = pstmt.executeQuery();

                int seatId = -1;
                if (resultSet.next()) {
                    seatId = resultSet.getInt("Seatid");
                }

                if (seatId != -1) {
                    // 여기서 예약 정보를 reservation 테이블에 삽입
                    String insertQuery = "INSERT INTO Reservation (Seatid, userid) VALUES (?, ?)";
                    pstmt = mySqlTest.dbconn.prepareStatement(insertQuery);
                    pstmt.setInt(1, seatId); // 조회된 Seatid 사용
                    pstmt.setString(2, userId); // 현재 로그인된 사용자 ID
                    pstmt.executeUpdate();
                }
            }
        } else {
            System.out.println("영화 예매를 종료하겠습니다. 다시 시작해주세요.");
            return 0;
        }
        return 1;
    }
}
