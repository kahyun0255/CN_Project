package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenreSearch
{
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dbplus";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    public static Connection getConnection() throws SQLException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
    public static void main(String[] args)
    {
        try
        {
            Connection connection = getConnection();
            Scanner scanner = new Scanner((System.in));

            //장르 목록 생성
            List<String> genres = new ArrayList<>();
            genres.add("Action");
            genres.add("Adventure");
            genres.add("Crime");
            genres.add("Comedy");
            genres.add("Drama");
            genres.add("Horror");
            genres.add("Sci-Fi");
            genres.add("Roamnce");

            //장르 목록에 번호 부여
            int genreNumber = 1;
            for(String genre : genres)
            {
                System.out.println(genreNumber + ". " + genre);
                genreNumber++;
            }

            //사용자에게 원하는 장르 번호 입력받기
            System.out.print("원하는 장르 번호를 입력해주세요: ");
            int selectedGenreNumber = scanner.nextInt();

            String selectedGenre = genres.get(selectedGenreNumber - 1);

            System.out.println("선택한 장르의 영화: " + selectedGenre);

            searchMoviesByGenre(connection, selectedGenre);

            connection.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void searchMoviesByGenre(Connection connection, String genre) throws SQLException
    {
        String sql = "SELECT name FROM movie WHERE genre1 = ? OR genre2 = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, genre);
        preparedStatement.setString(2, genre);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("선택한 장르의 영화");
        while (resultSet.next())
        {
            String movieName = resultSet.getString("Name");
            System.out.println(movieName);
        }

        resultSet.close();
        preparedStatement.close();
    }

}
