package org.example;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieReservationObject {
    static class MovieInfo implements Serializable {
        int movieId;
        String date;
        String time;
        int peopleNum;
        String seatNum;

        MovieInfo(int movieId, String date, String time, int peopleNum, String seatNum) {
            this.movieId = movieId;
            this.date = date;
            this.time = time;
            this.peopleNum = peopleNum;
            this.seatNum = seatNum;
        }
    }

//    static class MovieName implements Serializable {
//        ArrayList<Pair<Integer,String>> movieNumArray;
//        MovieName(ArrayList<Pair<Integer, String>>movieNumArray) {
//            this.movieNumArray=movieNumArray;
//        }
//    }
//

}
