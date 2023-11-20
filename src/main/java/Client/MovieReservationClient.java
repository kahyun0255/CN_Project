package Client;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.example.MovieReservationObject;
import org.example.Pair;

public class MovieReservationClient {
    MovieReservationObject movieReservationObject = new MovieReservationObject();
    Scanner sc = new Scanner(System.in);

    int MovieReservationMovieName(Client client, MovieReservationObject.MovieName movieNameArray) {
        int MovieId=0;

        for (Pair<Integer, String> pair : movieNameArray.movieNumArray) {
            int movieId = pair.first;
            String movieName = pair.second;
            System.out.println(movieId + ", " + movieName);
        }
        while (true) {
            System.out.printf("영화 번호를 입력해주세요: ");
            try {
                MovieId = sc.nextInt();
            }catch (InputMismatchException e) {
                System.out.println("숫자를 입력해야 합니다. 다시 시도해주세요.");
                sc.next(); // 입력 버퍼 초기화
            }
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
        }
        return MovieId;
    }
    String MovieReservationDate(Client client, MovieReservationObject.MovieDate dateArray) {
        for (Object date : dateArray.dateArray) {
            System.out.println(date);
        }
        String Date;
        while (true) {
            System.out.printf("날짜를 입력해주세요: ");
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
        for (Object date : timeArray.timeArray) {
            System.out.println(date);
        }
        String Time;
        while (true) {
            System.out.printf("시간을 입력해주세요: ");
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
        int PeopleNum=0;
        while(true){
            System.out.printf("인원을 입력해주세요.(숫자만 입력해주세요.): ");
            try {
                PeopleNum = sc.nextInt();
            }catch (InputMismatchException e) {
                System.out.println("숫자를 입력해야 합니다. 다시 시도해주세요.");
                sc.next(); // 입력 버퍼 초기화
            }
            if(PeopleNum==0){
                System.out.println("최소 1명의 인원을 입력해주세요");
            }else{
                break;
            }

        }

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

        ArrayList seatNum = new ArrayList<String>();
        int cnt = 0;
        while (cnt < PeopleNum) {
            String SeatNum = "";
            while(true) {
                System.out.printf("좌석을 하나씩 입력해주세요(예: A-3):");
                SeatNum = sc.next();

                if(!IsMatch.isMatch(SeatNum, "?-?")){
                    System.out.println("입력 형태가 잘못되었습니다. A-3과 같은 형태로 입력해주세요");
                }else if((SeatNum.charAt(0)<'A' || SeatNum.charAt(0)>'E') || (SeatNum.charAt(2)<'0'||SeatNum.charAt(2)>'8')){
                    System.out.println("선택한 좌석이 존재하지 않는 좌석입니다. 다시 입력해주세요");
                }else{
                    seatNum.add(SeatNum);
                    cnt++;
                    break;
                }
            }
        }
        return seatNum;
    }

    int MovieReservationInfo(Client client, MovieReservationObject.MovieInfo movieInfo) {
        System.out.println("영화 제목 : "+movieInfo.InfoMovieName);
        System.out.println("날짜 : "+movieInfo.InfoMovieDate);
        System.out.println("시간 : "+movieInfo.InfoMovieTime);
        System.out.print("좌석 :");
        for (Object s : movieInfo.InfoSeatNum) {
            System.out.print(s + ", ");
        }

        System.out.println();
        System.out.printf("정보가 맞는지 확인해주세요(Y/N): ");

        int infoCheck = 0;
        while (infoCheck == 0) {
            String str = sc.next();
            if (str.equals("Y")) {
                infoCheck = 1;
                break;
            } else if (str.equals("N")) {
                infoCheck = 2;
                break;
            } else {
                System.out.printf("다시 입력해주세요: ");
            }
        }

        if(infoCheck==1){
            return 1;
        }
        else if(infoCheck==2){
            return 0;
        }
        return 3;
    }
}

class IsMatch{
    public static boolean isMatch(String word, int n, String pattern, int m)
    {
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
