package org.example;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.example.MovieReservationObject.MovieName;

public class MyPageObject implements Serializable {
    public static class MyPageInfo implements Serializable{
        public String id;
        public String name;
        public ArrayList<MyPageMovieInfo>movieInfo;
        public MyPageInfo(String id, String name, ArrayList<MyPageMovieInfo>movieInfo){
            this.id=id;
            this.name=name;
            this.movieInfo=movieInfo;
        }
    }

    public static class MyPageMovieInfo implements Serializable{
        public String MovieName;
        public String Date;
        public String Time;
        public String Seat;

        public MyPageMovieInfo(String MovieName, String Date,String Time, String Seat){
            this.MovieName=MovieName;
            this.Date=Date;
            this.Time=Time;
            this.Seat=Seat;
        }
    }
}
