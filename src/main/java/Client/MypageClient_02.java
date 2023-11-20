package Client;

import org.example.MyPageObject;

public class MypageClient_02 {
    static void MyPageClinet(Client client, MyPageObject.MyPageInfo myPageInfo)  {
        if(myPageInfo.id==null){
            System.out.println("예약 정보가 없습니다.");
        }
        else{
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
}

