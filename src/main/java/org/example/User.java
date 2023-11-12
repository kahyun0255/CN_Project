package org.example;


import java.util.ArrayList;
import java.util.Scanner;

public class User {

    ArrayList<User> users = new ArrayList<User>();

    Scanner sc = new Scanner(System.in);
    String movietitle, number, user_number; // number: ticketing에서 받아올 정보, user_number: user가 입력할 정보
    String[] strings;
    int p_number, i;

    public User() {
        // TODO Auto-generated constructor stub
    }

    public User(String number, String movietitle, String[] strings, int p_number) {
        this.number = number;
        this.movietitle = movietitle;
        this.strings = strings;
        this.p_number = p_number;
    }

    public void setUser(String number, String movietitle, String[] strings, int p_number) {
        // TODO Auto-generated method stub
        users.add( new User(number, movietitle, strings, p_number) );
    }

    public void information() {
        boolean a = true;

        if (users.size() == 0) {
            System.out.println("아직 예약자가 없습니다.");
        }

        else {
            while (a) {
                System.out.print("예약 확인용 비밀번호 4자리를 입력하세요: ");
                user_number = sc.next();

                for (int i = 0; i < users.size(); i++) {
                    if (user_number.equals(users.get(i).number)) {
                        System.out.println();
                        System.out.println("<< 예약 정보 >>");
                        System.out.println("영화: " + users.get(i).movietitle);
                        System.out.println("인원: " + users.get(i).p_number);

                        System.out.print("자리: ");
                        for (int j = 0; j < users.get(i).strings.length; j++)
                            System.out.print(users.get(i).strings[j] + " ");

                        System.out.println();

                        System.out.println("즐거운 관람 되세요.");
                        a=false;
                        break;
                    }

                    else {
                        if (i != (users.size()-1) ) {
                            continue;
                        }
                        else {
                            System.out.println("비밀번호가 다릅니다. 다시 입력해주세요.");
                            System.out.println();
                            break;
                        }
                    }
                }
            }
        }
    }

}
