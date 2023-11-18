package org.example;
import java.io.Serializable;

public class Join {
    public static Join JoinInfo;

    public static class JoinInfo implements Serializable {
        String name;
        String id;
        String pw;

        public JoinInfo(String name, String id, String pw){
            this.name = name;
            this.id = id;
            this.pw = pw;
            //System.out.println("Join Info(name: " + this.name +
            //       "id: " + this.id  + "pw: " + this.pw);
        }
    }
}