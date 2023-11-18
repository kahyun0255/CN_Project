package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.GenreSearchObject;
import org.example.Pair;

public class GenreSearchServer {
    MySqlTest mySqlTest = new MySqlTest();
    Scanner sc = new Scanner(System.in);
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;
    public ArrayList<Pair<Integer,String>> getDistinctGenres(ClientHandler clientHandler) throws SQLException {
        ArrayList<Pair<Integer,String>> genres = new ArrayList<>();
        new MySqlTest().dbConnection();
        int idx=1;

        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT DISTINCT genre1 FROM movie UNION SELECT DISTINCT genre2 FROM movie"
        );

        resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            String columnValue = resultSet.getString(1); // 첫 번째 컬럼 값 가져오기
            Pair<Integer, String> p=Pair.of(idx,columnValue);
            genres.add(p);
            idx++;
        }
        return genres;
    }

    public ArrayList<String> searchMoviesByGenre(ClientHandler clientSocket, GenreSearchObject.GenreName genreName) throws SQLException, IOException {
        new MySqlTest().dbConnection();
        pstmt = mySqlTest.dbconn.prepareStatement(
                "SELECT name FROM movie WHERE genre1 = ? OR genre2 = ?"
        );
        pstmt.setString(1, genreName.Name);
        pstmt.setString(2, genreName.Name);
        resultSet = pstmt.executeQuery();

        ArrayList movieNames = new ArrayList<>();
        while (resultSet.next()) {
            String movieName = resultSet.getString("Name");
            movieNames.add(movieName);
        }
        return movieNames;
    }
}