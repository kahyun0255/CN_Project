package org.example;// GenreSearchClient.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GenreSearchClient {
    public void run(Socket mainSocket) {
        try {
            Connection connection = GenreSearchServer.getConnection();
            Scanner scanner = new Scanner(System.in);

            // 데이터베이스에서 장르 목록 가져오기
            List<String> genres = GenreSearchServer.getDistinctGenres(connection);

            // 장르 목록에 번호 부여
            int genreNumber = 1;
            for (String genre : genres) {
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

                    if (selectedGenreNumber < 1 || selectedGenreNumber > genres.size()) {
                        throw new IllegalArgumentException("유효하지 않은 장르 번호입니다. 다시 입력해주세요.");
                    }

                    break; // 올바른 입력이면 반복문 탈출
                } catch (Exception e) {
                    System.out.println("존재하지 않는 장르 번호입니다. 번호를 다시 입력해주세요.");
                    scanner.nextLine(); // 입력 버퍼 비우기
                }
            }

            String selectedGenre = genres.get(selectedGenreNumber - 1);

            System.out.println("선택한 장르: " + selectedGenre);

            // 메인 소켓 클라이언트를 통해 장르 검색 서버에게 요청 전송
            ObjectOutputStream oos = new ObjectOutputStream(mainSocket.getOutputStream());
            oos.writeUTF(selectedGenre);
            oos.flush();

            // 데이터를 받을 때까지 대기
            ObjectInputStream ois = new ObjectInputStream(mainSocket.getInputStream());
            List<String> movieNames = (List<String>) ois.readObject();

            // 받은 영화 목록 출력
            System.out.println("추천 영화");
            for (String movieName : movieNames) {
                System.out.println(movieName);
            }

            connection.close();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleGenreSearchResponse(byte[] responseBytes) {
        // 받은 바이트 데이터를 원하는 형태로 처리
        System.out.println("Received bytes: " + new String(responseBytes));
        // 이 부분을 실제로 받은 데이터를 어떻게 처리할지에 대한 로직으로 변경
    }
}
