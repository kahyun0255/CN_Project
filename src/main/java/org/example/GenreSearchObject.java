package org.example;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GenreSearchObject implements Serializable {
    public static class GenreList implements Serializable{
        public ArrayList<Pair<Integer, String>> genreList;
        public GenreList(ArrayList<Pair<Integer,String>> genreList){
            this.genreList=genreList;
        }
    }
    public static class GenreName implements Serializable{
        public String Name;
        public GenreName(String Name){
            this.Name=Name;
        }
    }

    public static class GenreMovieName implements Serializable{
        public ArrayList<String> movieName=new ArrayList<>();

        public GenreMovieName(ArrayList<String>movieName){
            this.movieName=movieName;
        }
    }
}
