package Server;// GenreSearchServer.java
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

public class GenreSearchServer {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dbplus";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static List<String> getDistinctGenres(Connection connection) throws SQLException {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre1 FROM movie UNION SELECT DISTINCT genre2 FROM movie";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String genre = resultSet.getString("genre1");
                genres.add(genre);
            }
        }
        return genres;
    }

    public static void searchMoviesByGenre(Connection connection, Socket clientSocket) throws SQLException, IOException {
        // 바이트로 받은 데이터를 읽어옴
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        String selectedGenre = ois.readUTF();

        String sql = "SELECT name FROM movie WHERE genre1 = ? OR genre2 = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, selectedGenre);
            preparedStatement.setString(2, selectedGenre);
            ResultSet resultSet = preparedStatement.executeQuery();

            // 결과를 리스트로 저장
            List<String> movieNames = new ArrayList<>();
            while (resultSet.next()) {
                String movieName = resultSet.getString("Name");
                movieNames.add(movieName);
            }

            // 리스트를 바이트 배열로 변환하여 클라이언트에게 전송
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(movieNames);
            oos.flush();
        }
    }
}