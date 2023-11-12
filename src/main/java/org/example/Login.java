package org.example;

import java.io.Serializable;

public class Login {
    public static Login LoginInfo;
    //FOR TEST

    static class LoginInfo implements Serializable {
        String id;
        String pw;
        LoginInfo(String id, String pw){
            this.id = id;
            this.pw = pw;
            System.out.println("Login Info: ID: " + this.id + " PW: " + this.pw);
        }
    }


}
