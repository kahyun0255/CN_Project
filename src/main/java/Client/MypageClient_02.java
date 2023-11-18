package Client;

//import Client.Manager;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.MyPageObject;
import org.example.MyPageObject.MyPageInfo;

public class MypageClient_02 {
    static void MyPageClinet(Client client, MyPageObject.MyPageInfo myPageInfo)  {
        System.out.println("아이디 : " + myPageInfo.id);
        System.out.println("이름 : " + myPageInfo.name);

        for(MyPageObject.MyPageMovieInfo myPage : myPageInfo.movieInfo){
            System.out.println("-----------------");
            System.out.println("영화 제목 : "+myPage.MovieName);
            System.out.println("예약 날짜 : "+myPage.Date);
            System.out.println("예약 시간 : "+myPage.Time);
            System.out.println("예약 좌석 : "+myPage.Seat);
        }
    }
}

