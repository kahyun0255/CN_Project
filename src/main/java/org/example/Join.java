package org.example;
import java.io.Serializable;

public class Join {
    public static Join JoinInfo;

    public static class JoinInfo implements Serializable {
        public String name;
        public String id;
        public String pw;

        public JoinInfo(String id,String name,  String pw){
            this.id = id;
            this.name = name;
            this.pw = pw;
            //System.out.println("Join Info(name: " + this.name +
            //       "id: " + this.id  + "pw: " + this.pw);
        }
    }
}