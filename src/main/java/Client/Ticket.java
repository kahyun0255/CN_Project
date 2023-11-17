package Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Ticket {
    ArrayList<String> r_array = new ArrayList<String>(Arrays.asList("0"));

    char row;
    int col;
    int k;
    int p = 0;
    String r_number;
    int p_number = 0;
    String movietitle;
    int movienum;
    Scanner sc = new Scanner(System.in);
    String[] strings;

    public Ticket() {}

    public void ticketing(User u, int [][]seat, String movietitle) {

        System.out.print("예약할 인원 수를 입력하세요: ");
        p_number = sc.nextInt();
        sc.nextLine();
        while(true) {
            PrintSeat(seat);
            System.out.printf("좌석 이름 입력: ");
            strings = checkList(seat);   //중복 입력 확인.
            if(strings.length == p_number) {
                for(int i = 0;i<strings.length;i++) {
                    row = strings[i].charAt(0);
                    if(row>=65 && row<=75)
                        k = (int)row - 65;
                    else if(row>=97 && row<=107)
                        k = (int)row - 97;
                    col = Integer.parseInt(strings[i].substring(1));
                    if(seat[k][col-1] == 0) {
                        seat[k][col-1] = 1;
                    }
                }
                break;
            }
            else {
                System.out.println("입력한 좌석 수와 인원 수가 맞지 않습니다. 다시 입력해주세요.");
                continue;
            }
        }

        rnumber();

        PrintSeat(seat);
        System.out.println("예약이 완료 되었습니다.");

        u.setUser(r_array.get(p), movietitle, strings,p_number);
    }

    public boolean checkSeat(String[] strings,int [][]seat) {
        int checkfalse = 0;
        for(int i=0;i<strings.length;) {
            row = strings[i].charAt(0);
            if(row>=65 && row<=75)
                k = (int)row - 65;
            else if(row>=97 && row<=107)
                k = (int)row - 97;
            col = Integer.parseInt(strings[i].substring(1));
            if(seat[k][col-1] == 0) {
                System.out.println(strings[i]+"는 예약 가능한 좌석입니다.");
            }
            else if(seat[k][col-1] == 1) {
                System.out.println(strings[i]+"는 이미 예약이 된 좌석입니다. 다시 선택해 주시기 바랍니다.");
                checkfalse++;
            }
            i++;
        }

        if(checkfalse>0) {
            System.out.printf("좌석 입력: ");
            return false;
        }
        return true;
    }

    public String[] checkList(int [][]seat) {    //중복 입력 체크
        while(true) {
            String inputLine = sc.nextLine();
            String[] strings = inputLine.split(",");
            HashSet<String> uniqueStrings = new HashSet<>();
            boolean check = false;
            for (String str : strings) {
                if (!uniqueStrings.add(str)) {
                    System.out.println("중복으로 입력하셨습니다. 다시 선택해주세요.");
                    check = true;
                    break;
                }
            }
            if (!check) {
                if(checkSeat(strings,seat)) {
                    return strings;
                }
                else
                    continue;
            }
        }
    }


    public void rnumber() {// 예약번호 입력받고 겹치는지 확인

        System.out.println("예약 확인시 사용할 비밀번호 4자리를 입력해주세요.");

        boolean a = true;
        boolean b = false;

        while (a == true) {
            System.out.printf("비밀번호 입력: ");
            r_number = sc.next();

            while (true) {

                if (r_number.length() == 4) {

                    for (int index = 0; index < r_array.size(); index++) {

                        if (r_number.equals(r_array.get(index))) {
                            System.out.println("이미 사용중인 비밀번호입니다. 다른 비밀번호를 입력해주세요.");
                            break;
                        }

                        else {

                            if (index != (r_array.size() - 1)) {
                                continue;
                            }

                            else {
                                System.out.println("사용 가능한 비밀번호입니다.");
                                a = false;
                                b = true;
                                break;
                            }

                        }

                    }
                }
                else {
                    System.out.println("비밀번호는 4자리입니다. 다시 입력해주세요.");
                }
                break;

            }
            if (b == true) {p++; r_array.add(r_number);}

        }
    }

    public void PrintSeat(int [][]seat) {
        System.out.println("\n\t<<좌석 배치도>>\n");
        System.out.println(" 1 2 3 4 5 6 7 8 9 101112131415");
        for(int i=0;i<11;i++) {
            System.out.print((char)(i+65)+" ");
            for(int j=0;j<15;j++) {
                if(seat[i][j] == 0)
                    System.out.print("□ ");
                else if(seat[i][j] == 1)
                    System.out.print("■ ");
            }
            System.out.println("\n");
        }
    }
}
