package Client;// GenreSearchClient.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import Server.GenreSearchServer;
import org.example.GenreSearchObject;
import org.example.Pair;

public class GenreSearchClient {
    Scanner sc = new Scanner(System.in);
    String GenreSearchList(Client client, GenreSearchObject.GenreList GenreList) {
        for (Pair<Integer, String> genre : GenreList.genreList) {
            System.out.println(genre.first + ". " + genre.second);
        }

        int selectedGenreNumber = 0;
        while (true) {
            System.out.print("원하는 장르 번호를 입력해주세요: ");
            selectedGenreNumber = sc.nextInt();

            // 선택한 번호가 유효한지 확인
            boolean isValid = false;
            for (Pair<Integer, String> genre : GenreList.genreList) {
                if (genre.first == selectedGenreNumber) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                // 유효한 번호인 경우
                for (Pair<Integer, String> genre : GenreList.genreList) {
                    if (genre.first == selectedGenreNumber) {
                        System.out.println("선택한 장르: " + genre.second);
                        return GenreList.genreList.get(selectedGenreNumber).second;
                    }
                }
            } else {
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
            }
        }
    }
    void MovieNamePrint(Client client, GenreSearchObject.GenreMovieName movieName){
        System.out.println("선택한 장르의 영화는");
        for(String movie:movieName.movieName){
            System.out.println(movie);
        }
        System.out.println("입니다.");
    }
}

