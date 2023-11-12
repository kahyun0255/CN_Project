package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieReservationClient {
    void MovieReservation() throws IOException {
        Scanner sc = new Scanner(System.in);
        String Protocol = "0x000001101";

        List<Pair<Integer,String>> movieNumArray=new ArrayList<>(); //나중에 class에서 받은걸로 변경해야할듯??

        System.out.println("영화 번호를 입력해주세요.");
        for (Pair<Integer, String> pair : movieNumArray) {
            int movieId = pair.first;
            String movieName = pair.second;
            System.out.println(movieId + ", " + movieName);
        }
        while(true) {
            int MovieId = sc.nextInt();
            boolean movieIdExists = false;

            for (Pair<Integer, String> pair : movieNumArray) {
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

        System.out.println("날짜를 입력해주세요.");
        ArrayList dateArray=new ArrayList<>(); //나중에 바꾸기 -> 서버에서 받아온 ArrayList로
        for (Object date : dateArray) {
            System.out.println(date);
        }

        while (true){
            String Date=sc.next();
            if(!IsMatch.isMatch(Date,"????-??-??")){
                System.out.println("입력 형태가 잘못되었습니다. ????-??-?? 형태로 입력해주세요.");
            }
            else{
                if (!dateArray.contains(Date)) {
                    System.out.println("입력하신 날짜가 존재하지 않습니다. 날짜를 다시 입력해주세요.");
                }
                else{
                    break;
                }
            }
        }

        System.out.println("시간을 입력해주세요.");

        ArrayList timeArray=new ArrayList<>(); //아 ArrayList로 빼놧구나 하나면 ArrayList로 안해두 될듯?
        for (Object date : timeArray) {
            System.out.println(date);
        }

        while (true){
            String Time=sc.next();
            if(!IsMatch.isMatch(Time,"??:??")){
                System.out.println("입력 형태가 잘못되었습니다. ??:?? 형태로 입력해주세요.");
            }
            else{
                if (!timeArray.contains(Time)) {
                    System.out.println("입력하신 시간이 존재하지 않습니다. 시간을 다시 입력해주세요.");
                }
                else{
                    break;
                }
            }
        }

        System.out.println("인원을 입력해주세요.(숫자만 입력해주세요.)");
        int PeopleNum = sc.nextInt();

        List<Pair<String, Boolean>> seatArray=new ArrayList<>(); //이거 서버한테 보내야함~~

        System.out.println("<<좌석 배치도>>");
        System.out.println("  0 1 2 3 4 5 6 7 8");
        for (char row = 'A'; row <= 'E'; row++) {
            System.out.print(row + " ");
            for (int seat = 0; seat <= 8; seat++) {
                String seatNumber = row + "-" + seat;
                boolean isReserved = false;

                for (Pair<String, Boolean> pair : seatArray) {
                    String Number = pair.first;
                    boolean check = pair.second;
                    if(Number.equals(seatNumber)){
                        isReserved=check;
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

//        ArrayList MovieReservationInfo = new ArrayList<>(); //나중에 서버한테 클래스 형태로 받을거임 주석처리 다 빼기
//        System.out.println("영화 제목 : "+MovieReservationInfo.ReMovieName);
//        System.out.println("날짜 : "+MovieReservationInfo.ReDate);
//        System.out.println("시간 : "+MovieReservationInfo.ReTime);
//        System.out.println("인원 : "+MovieReservationInfo.RePeopleNum);
//        System.out.print("좌석 :");
//        for (Object s : MovieReservationInfo.ReSeatNum) { //나중에 지우기
//            System.out.print(s + ", ");
//        }

//        System.out.println(); //여기두 주석처리 ㄷ다~~~빼기 까먹고 지우면 큰일남~~ 큰일까지는 아니겟군아;;
//        System.out.println("정보가 맞는지 확인해주세요. 맞으면 예, 틀리면 아니오를 입력해주세요.");
//
//        int infoCheck = 0; //나중에 싹~~~지우기 확인용 다시 하기(OK / No 사인 받고 하기~~)
//        while (infoCheck == 0) {
//            String str = sc.next();
//            if (str.equals("예")) {
//                infoCheck = 1;
//                break;
//            } else if (str.equals("아니오")) {
//                infoCheck = 2;
//                break;
//                // 처음부터 다시 입력받기...
//            } else {
//                System.out.println("다시 입력해주세요.");
//            }
//        }
//
//        if(infoCheck==1){
//            //서버한테 OK 사인 보내기
//        }
//        else if(infoCheck==2){
//            //서버한테 NO 사인 혹은 영화 예약 페이지 종료하기 -> 종료도 서버에서 하나??
//        }
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