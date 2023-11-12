package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.MovieReservationObject.MovieInfo;

public class MovieReservationServer {
    static int MovieId;
    static String Date;
    static String Time;
    static int PeopleNum;
    static String SeatNum;
    static String Protocol;

    static String userId="kkh1234";

    MySqlTest mysqlTest=new MySqlTest();

    void MovieReservation() throws IOException {
        new MySqlTest().dbConnection();
        Scanner sc = new Scanner(System.in);
        Protocol = "0x000001101";

        PreparedStatement pstmt = null;
        ResultSet resultSet=null;
        try {
            System.out.println("영화 번호를 입력해주세요."); //나중에 지우기 -> 나중에 변경

            resultSet = MySqlTest.dbconn.createStatement().executeQuery("select * from movie"); //데이터베이스에서 긁어오기
            List<Pair<Integer,String>> movieNumArray=new ArrayList<>(); //이거 클라이언트한테 보내야함
            while (resultSet.next()) {
                // 각 열의 데이터를 읽어옴
                int column1Value = resultSet.getInt("movieid");
                String column2Value = resultSet.getString("name");
                Pair<Integer,String> p=Pair.of(column1Value, column2Value);
                movieNumArray.add(p); //페어로 묶어 obj 형태로 보내기

                System.out.println(column1Value + ", " + column2Value);
            }

           MovieId = sc.nextInt(); //나중에 지우기 -> 클라이언트단에서 오류 판단하고 올바른거만 줄듯??


            System.out.println("날짜를 입력해주세요."); //나중에 지우기

            ArrayList dateArray=new ArrayList<>(); //이거 클라이언트한테 보내야함
            resultSet = MySqlTest.dbconn.createStatement().executeQuery("select distinct date from seat where movieid="+MovieId);
            while (resultSet.next()) {
                String columnValue = resultSet.getString("date");
                dateArray.add(columnValue);

                System.out.println(columnValue); //나중에 지우기
            }
            Date=sc.next(); //나중에 지우기 -> 클라이언트단에서 오류 판단하고 올바른거 줄듯?


            System.out.println("시간을 입력해주세요."); //나중에 지우기

            pstmt = MySqlTest.dbconn.prepareStatement("SELECT DISTINCT time FROM seat WHERE movieid = ? AND date = ?"); //데이터 베이스에서 긁어오기
            pstmt.setInt(1, MovieId);
            pstmt.setString(2, Date);
            resultSet = pstmt.executeQuery();
            ArrayList timeArray=new ArrayList<>(); //이거 클라이언트한테 보내야함
            while (resultSet.next()) {
                String columnValue = resultSet.getString("time");
                timeArray.add(columnValue);
                System.out.println(columnValue); //나중에 지우기
            }

           Time=sc.next(); //프론트에서 판별

            System.out.println("인원을 입력해주세요.(숫자만 입력해주세요.)"); //나중에 지우기
            PeopleNum = sc.nextInt(); //나중에 지우기


            // 좌석의 예약 상태를 데이터베이스에서 가져옵니다.
            pstmt = MySqlTest.dbconn.prepareStatement(
                    "SELECT number, `check` FROM seat WHERE movieid = ? AND date = ? AND time = ? ORDER BY number"
            );
            pstmt.setInt(1, MovieId);
            pstmt.setString(2, Date);
            pstmt.setString(3, Time);
            resultSet = pstmt.executeQuery();

            System.out.println("<<좌석 배치도>>"); //나중에 지우기
            System.out.println("  0 1 2 3 4 5 6 7 8"); //나중에 지우기

            List<Pair<String, Boolean>> seatArray=new ArrayList<>(); //클라이언트한테 보내야함

            for (char row = 'A'; row <= 'E'; row++) { //나중에 지우기
                System.out.print(row + " "); //나중에 지우기
                for (int seat = 0; seat <= 8; seat++) { //나중에 지우기
                    String seatNumber = row + "-" + seat; //나중에 지우기
                    boolean isReserved = false; //나중에 지우기

                    while (resultSet.next()) {
                        String column1Value = resultSet.getString("number");
                        boolean column2Value = resultSet.getBoolean("check");
                        Pair<String,Boolean> p=Pair.of(column1Value, column2Value);
                        seatArray.add(p); //페어로 묶어 obj 형태로 보내기
                        if (resultSet.getString("number").equals(seatNumber)) { //나중에 지우기
                            isReserved = resultSet.getBoolean("check"); //나중에 지우기
                            break; //나중에 지우기
                        }
                    }
                    System.out.print(isReserved ? "■ " : "□ "); //나중에 지우기
//                    resultSet.beforeFirst(); //나중에 지우기
                }
                System.out.println(); //나중에 지우기
            }

            ArrayList seatNum = new ArrayList<String>(); //나중에 지우기 -> 이건 클라이언트에서 좌석 정보를 담아서 보내줌 아닌가? 아 일단 담아두는거임 아래를 위해서 이거 클라이언트에서 보내준다...
            int cnt = 0;
            while (cnt < PeopleNum) { //나중에 지우기
                System.out.println("좌석을 하나씩 입력해주세요(예: A-3):");
                SeatNum = sc.next();

                pstmt = MySqlTest.dbconn.prepareStatement(
                        "SELECT number, `check` FROM seat WHERE movieid = ? AND date = ? AND time = ? ORDER BY number"
                );

                pstmt.setInt(1, MovieId);
                pstmt.setString(2, Date);
                pstmt.setString(3, Time);
                resultSet = pstmt.executeQuery();

                seatNum.add(SeatNum);

                cnt++;
            }

            pstmt = MySqlTest.dbconn.prepareStatement(
                    "SELECT name FROM movie WHERE movieid = ?"
            );
            pstmt.setInt(1, MovieId);
            resultSet = pstmt.executeQuery();


            if (resultSet.next()) {
                String movieName = resultSet.getString(1); // 또는 resultSet.getString("name");
                System.out.print("영화 제목 : " + movieName + "\n날짜 : " + Date + "\n시간 : " + Time + "\n인원 : " + PeopleNum + "\n좌석 : ");
//                MovieReservationInfo(movieName, Date, Time, PeopleNum,seatNum); // 나중에 넣기 -> 클라이언트로 보낼 데이터임 근데 클래스 형태로 보내야해서 어떻게 보낼지는 생각해봐야할듯
                for (Object s : seatNum) { //나중에 지우기
                    System.out.print(s + ", ");
                }
            }

            System.out.println(); //나중에 지우기
            System.out.println("정보가 맞는지 확인해주세요. 맞으면 예, 틀리면 아니오를 입력해주세요."); //나중에 지우기

            int infoCheck = 0; //나중에 싹~~~지우기 확인용 다시 하기(OK / No 사인 받고 하기~~)
            while (infoCheck == 0) {
                String str = sc.next();
                if (str.equals("예")) {
                    infoCheck = 1;
                    break;
                } else if (str.equals("아니오")) {
                    infoCheck = 2;
                    break;
                    // 처음부터 다시 입력받기...
                } else {
                    System.out.println("다시 입력해주세요.");
                }
            }


            //입력받은 정보 다 보내고 ok 사인 받으면 아래 코드 진행
            if(infoCheck==1) {
                for (int i = 0; i < seatNum.size(); i++) { //seatNum ArrayList는 나중에 클라이언트쪽에서 보내줌
                    String nowSeatNum = (String) seatNum.get(i);

                    pstmt = MySqlTest.dbconn.prepareStatement(
                            "UPDATE seat SET `check` = true WHERE movieid = ? AND date = ? AND time = ? AND number = ?"
                    );
                    System.out.println("MovieId : " + MovieId + " Date : " + Date + " Time : " + Time + " nowSeatNum : "
                            + nowSeatNum); //나중에 지우기 -> 확인용임...
                    pstmt.setInt(1, MovieId);
                    pstmt.setString(2, Date);
                    pstmt.setString(3, Time);
                    pstmt.setString(4, nowSeatNum);
                    pstmt.executeUpdate(); //쿼리 수행

                    //reservation에 정보 넣기
                    String seatQuery = "SELECT Seatid FROM seat WHERE number = ?";
                    pstmt = MySqlTest.dbconn.prepareStatement(seatQuery);
                    pstmt.setString(1, nowSeatNum); // nowSeatNum은 사용자가 입력한 좌석 번호
                    resultSet = pstmt.executeQuery();

                    int seatId = -1;
                    if (resultSet.next()) {
                        seatId = resultSet.getInt("Seatid");
                    }
                    // 찾은 Seatid를 사용하여 Reservation 테이블에 새로운 레코드를 삽입
                    if (seatId != -1) {
                        String insertQuery = "INSERT INTO Reservation (Seatid, userid) VALUES (?, ?)";
                        pstmt = MySqlTest.dbconn.prepareStatement(insertQuery);
                        pstmt.setInt(1, seatId);  // 찾은 Seatid 사용
                        pstmt.setString(2, userId);  // userId는 로그인중인 계정을 넣어야함....

                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally { //오류처리 해야하는데 머르겟더요;;
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* ignored */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* ignored */ }
            // 여기서는 dbconn을 닫지 않습니다. 왜냐하면 아직 사용중일 수 있기 때문입니다.
            // dbconn.close();
        }
    }
    public static void sendData(byte[] bytes, Socket socket){
        try {
            OutputStream os = socket.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch(Exception e1){
            e1.printStackTrace();
        }
    }
}

class MovieReservationInfo{
    public String ReMovieName;
    public String ReDate;
    public String ReTime;
    public int RePeopleNum;
    ArrayList<String> ReSeatNum;
    MovieReservationInfo(String ReMovieName, String ReDate, String ReTime, int RePeopleNum, ArrayList<String>ReSeatNum) {
        this.ReMovieName=ReMovieName;
        this.ReDate=ReDate;
        this.ReTime=ReTime;
        this.RePeopleNum=RePeopleNum;
        this.ReSeatNum=ReSeatNum;
    }
}

// Pair class
class Pair<U, V>
{
    public final U first;       // 쌍의 첫 번째 필드
    public final V second;      // 쌍의 두 번째 필드

    // 지정된 값으로 새 쌍을 구성합니다.
    private Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    // 지정된 객체가 현재 객체와 "같음" 확인
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        // 기본 객체의 `equals()` 메서드 호출
        if (!first.equals(pair.first)) {
            return false;
        }
        return second.equals(pair.second);
    }

    @Override
    // 해시 테이블을 지원하기 위해 객체의 해시 코드를 계산합니다.
    public int hashCode()
    {
        // 기본 객체의 해시 코드 사용
        return 31 * first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    // 타입이 지정된 Pair 불변 인스턴스를 생성하기 위한 팩토리 메소드
    public static <U, V> Pair <U, V> of(U a, V b)
    {
        // 전용 생성자를 호출
        return new Pair<>(a, b);
    }
}

class MySqlTest{
    String dbDriver="com.mysql.cj.jdbc.Driver";
    String dbUrl="jdbc:mysql://127.0.0.1:3306/comnet?serverTimezone=Asia/Seoul&useSSL=false";
    String dbUser = "comnet";
    String dbPassword = "cat1234";

    public static Connection dbconn=null;

    public void dbConnection(){
        Connection connection = null;

        try
        {
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            dbconn = connection;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
