package org.example;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfoObject {
    public static class UserName implements Serializable {
        public String name;
        public UserName(String name) {
            this.name=name;
        }
    }
}
