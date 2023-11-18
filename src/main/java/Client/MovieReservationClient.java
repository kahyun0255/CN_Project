package Client;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import org.example.MovieReservationObject;
import org.example.Pair;

public class MovieReservationClient {
    MovieReservationObject movieReservationObject = new MovieReservationObject();
    Scanner sc = new Scanner(System.in);
    String Protocol = "0x000001101";

    int MovieReservationMovieName(Client client, MovieReservationObject.MovieName movieNameArray) {
        System.out.println("영화 번호를 입력해주세요.");
        int MovieId;

        for (Pair<Integer, String> pair : movieNameArray.movieNumArray) {
            int movieId = pair.first;
            String movieName = pair.second;
            System.out.println(movieId + ", " + movieName);
        }
        while (true) {
            try{
                MovieId = sc.nextInt();
                boolean movieIdExists = false;

                for (Pair<Integer, String> pair : movieNameArray.movieNumArray) {
                    if (pair.first.equals(MovieId)) {
                        movieIdExists = true;
                        break; // 일치하는 영화 ID를 찾았으므로 더 이상 반복할 필요가 없음
                    }
                }

                if (!movieIdExists) {
                    System.out.println("입력하신 번호의 영화가 존재하지 않습니다. 영화 번호를 다시 입력해주세요.");
                } else {
                    break;
                }
            }catch (InputMismatchException e) {
                System.out.println("숫자를 입력해야 합니다. 다시 시도해주세요.");
                sc.next(); // 입력 버퍼 초기화
            }
        }
        return MovieId;
    }
    String MovieReservationDate(Client client, MovieReservationObject.MovieDate dateArray) {
        System.out.println("날짜를 입력해주세요.");
//        ArrayList dateArray = new ArrayList<>(); //나중에 바꾸기 -> 서버에서 받아온 ArrayList로
        for (Object date : dateArray.dateArray) {
            System.out.println(date);
        }
        String Date;
        while (true) {
            Date = sc.next();
            if (!IsMatch.isMatch(Date, "????-??-??")) {
                System.out.println("입력 형태가 잘못되었습니다. ????-??-?? 형태로 입력해주세요.");
            } else {
                if (!dateArray.dateArray.contains(Date)) {
                    System.out.println("입력하신 날짜가 존재하지 않습니다. 날짜를 다시 입력해주세요.");
                } else {
                    break;
                }
            }
        }
        return Date;
    }

    String MovieReservationTime(Client client, MovieReservationObject.MovieTime timeArray) {
        System.out.println("시간을 입력해주세요.");

//        ArrayList timeArray = new ArrayList<>(); //아 ArrayList로 빼놧구나 하나면 ArrayList로 안해두 될듯?
        for (Object date : timeArray.timeArray) {
            System.out.println(date);
        }
        String Time;
        while (true) {
            Time = sc.next();
            if (!IsMatch.isMatch(Time, "??:??")) {
                System.out.println("입력 형태가 잘못되었습니다. ??:?? 형태로 입력해주세요.");
            } else {
                if (!timeArray.timeArray.contains(Time)) {
                    System.out.println("입력하신 시간이 존재하지 않습니다. 시간을 다시 입력해주세요.");
                } else {
                    break;
                }
            }
        }
        return Time;
    }

    ArrayList MovieReservationSeat(Client client, MovieReservationObject.MovieSeat seatArray) {
        System.out.println("인원을 입력해주세요.(숫자만 입력해주세요.)");
        int PeopleNum = sc.nextInt();

//        List<Pair<String, Boolean>> seatArray = new ArrayList<>();

        System.out.println("<<좌석 배치도>>");
        System.out.println("  0 1 2 3 4 5 6 7 8");
        for (char row = 'A'; row <= 'E'; row++) {
            System.out.print(row + " ");
            for (int seat = 0; seat <= 8; seat++) {
                String seatNumber = row + "-" + seat;
                boolean isReserved = false;

                for (Pair<String, Boolean> pair : seatArray.seatArray) {
                    String Number = pair.first;
                    boolean check = pair.second;
                    if (Number.equals(seatNumber)) {
                        isReserved = check;
                        break;
                    }
                }
                System.out.print(isReserved ? "■ " : "□ ");
            }
            System.out.println();
        }

        ArrayList seatNum = new ArrayList<String>(); //이거 서버로 보내야함
        int cnt = 0;
        while (cnt < PeopleNum) {
            System.out.println("좌석을 하나씩 입력해주세요(예: A-3):");
            String SeatNum = sc.next();
            seatNum.add(SeatNum);
            cnt++;
        }
        return seatNum;
    }

    int MovieReservationInfo(Client client, MovieReservationObject.MovieInfo movieInfo) {
//        ArrayList MovieReservationInfo = new ArrayList<>(); //나중에 서버한테 클래스 형태로 받을거임 주석처리 다 빼기
        System.out.println("영화 제목 : "+movieInfo.InfoMovieName);
        System.out.println("날짜 : "+movieInfo.InfoMovieDate);
        System.out.println("시간 : "+movieInfo.InfoMovieTime);
        System.out.print("좌석 :");
        for (Object s : movieInfo.InfoSeatNum) { //향상된 for문이 아닌 그냥 for문으로 해서 마지막 콤마 제거하기
            System.out.print(s + ", ");
        }

        System.out.println(); //여기두 주석처리 ㄷ다~~~빼기 까먹고 지우면 큰일남~~ 큰일까지는 아니겟군아;;
        System.out.println("정보가 맞는지 확인해주세요. 맞으면 예, 틀리면 아니오를 입력해주세요.");

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

        if(infoCheck==1){
            //서버한테 OK 사인 보내기
            return 1;
        }
        else if(infoCheck==2){
            //서버한테 NO 사인 혹은 영화 예약 페이지 종료하기 -> 종료도 서버에서 하나??
            return 0;
        }
        return 3; //이상한게 갔다는 신호
    }
}

class IsMatch{
    public static boolean isMatch(String word, int n, String pattern, int m)
    {
        // 패턴의 끝에 도달
        if (m == pattern.length()) {
            return n == word.length();
        }

        if (n == word.length()) {
            for (int i = m; i < pattern.length(); i++) {
                if (pattern.charAt(i) != '*') {
                    return false;
                }
            }

            return true;
        }

        if (pattern.charAt(m) == '?' || pattern.charAt(m) == word.charAt(n)) {
            return isMatch(word, n + 1, pattern, m + 1);
        }

        if (pattern.charAt(m) == '*') {
            return isMatch(word, n + 1, pattern, m) ||
                    isMatch(word, n, pattern, m + 1);
        }

        return false;
    }
    public static boolean isMatch(String word, String pattern) {
        return isMatch(word, 0, pattern, 0);
    }
}
