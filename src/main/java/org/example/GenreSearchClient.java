package org.example;// GenreSearchClient.java
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenreSearchClient {
    void GenreSearch() throws IOException {
        try {
            Scanner scanner = new Scanner(System.in);
            String Protocol = "0x000001110";

            // 데이터베이스에서 장르 목록 가져오기
            List<String> GenreArray = new ArrayList<>();

            // 장르 목록에 번호 부여
            int genreNumber = 1;
            for (String genre : GenreArray) {
                System.out.println(genreNumber + ". " + genre);
                genreNumber++;
            }

            int selectedGenreNumber;

            // 사용자에게 올바른 장르 번호를 입력받을 때까지 반복
            while (true) {
                // 사용자에게 원하는 장르 번호 입력받기
                System.out.print("원하는 장르 번호를 입력해주세요: ");

                // 예외 처리를 통해 숫자가 아닌 값이나 범위를 벗어나는 값에 대한 처리
                try {
                    selectedGenreNumber = scanner.nextInt();

                    if (selectedGenreNumber < 1 || selectedGenreNumber > GenreArray.size()) {
                        throw new IllegalArgumentException("유효하지 않은 장르 번호입니다. 다시 입력해주세요.");
                    }

                    break; // 올바른 입력이면 반복문 탈출
                } catch (Exception e) {
                    System.out.println("존재하지 않는 장르 번호입니다. 번호를 다시 입력해주세요.");
                    scanner.nextLine(); // 입력 버퍼 비우기
                }
            }

            String selectedGenre = GenreArray.get(selectedGenreNumber - 1);

            System.out.println("선택한 장르: " + selectedGenre);

            List<String> MovieNameArray = new ArrayList<>();
            // 받은 영화 목록 출력
            System.out.println("추천 영화");
            for (String movieName : MovieNameArray) {
                System.out.println(movieName);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
