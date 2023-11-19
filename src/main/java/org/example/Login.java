package org.example;

import java.io.Serializable;

public class Login {
    public static Login LoginInfo;
    //FOR TEST

    public static class LoginInfo implements Serializable {
        public String id;
        public String pw;
        public LoginInfo(String id, String pw){
            this.id = id;
            this.pw = pw;
            //System.out.println("Login Info: ID: " + this.id + " PW: " + this.pw);
        }
    }


}
