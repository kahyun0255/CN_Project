package org.example;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieReservationObject implements Serializable {
    public static class MovieName implements Serializable {
        public ArrayList<Pair<Integer,String>> movieNumArray;
        public MovieName(ArrayList<Pair<Integer, String>>movieNumArray) {
            this.movieNumArray=movieNumArray;
        }
    }

    public static class MovieDate implements Serializable {
        public ArrayList<String> dateArray;
        public MovieDate(ArrayList<String>dateArray){
            this.dateArray=dateArray;
        }
    }

    public static class InputMovieDate implements Serializable {
        public String inputDate;
        public InputMovieDate(String inputDate){
            this.inputDate=inputDate;
        }
    }

    public static class MovieTime implements Serializable {
        public ArrayList<String> timeArray;
        public MovieTime(ArrayList<String>timeArray){
            this.timeArray=timeArray;
        }
    }

    public static class InputMovieTime implements Serializable {
        public String inputTime;
        public InputMovieTime(String inputTime){
            this.inputTime=inputTime;
        }
    }

    public static class MovieSeat implements Serializable {
        public ArrayList<Pair<String, Boolean>>seatArray;
        public MovieSeat(ArrayList<Pair<String, Boolean>> seatArray){
            this.seatArray=seatArray;
        }
    }
    public static class MovieSeatNum implements Serializable {
        public ArrayList<String> seatNumArray;
        public MovieSeatNum(ArrayList<String>seatNumArray){

            this.seatNumArray=seatNumArray;
        }
    }

    public static class MovieInfo implements Serializable {
        public String InfoMovieName;
        public String InfoMovieDate;
        public String InfoMovieTime;
        public ArrayList<String>InfoSeatNum;
        public MovieInfo(String InfoMovieName, String InfoMovieDate, String InfoMovieTime, ArrayList<String>InfoSeatNum ){
            this.InfoMovieName=InfoMovieName;
            this.InfoMovieDate=InfoMovieDate;
            this.InfoMovieTime=InfoMovieTime;
            this.InfoSeatNum=InfoSeatNum;
        }
    }
}



