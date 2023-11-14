package org.example;
import java.io.Serializable;
import java.util.ArrayList;

public class SUMIN {
    public class genrelist implements Serializable {
            public ArrayList<String> GenreArray;
            public genrelist(ArrayList<String>GenreArray) {
                this.GenreArray=GenreArray;
            }
        }
    public class UserInfo implements Serializable {
        public String UserId;
        public String UserName;
        public String UserPassword;
        public ArrayList<String>JoinData;
        public UserInfo(String UserId, String UserName, String UserPassword, ArrayList<String>JoinData ){
            this.UserId=UserId;
            this.UserName=UserName;
            this.UserPassword=UserPassword;
        }
    }
}
