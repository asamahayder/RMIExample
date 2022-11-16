package main;

import java.io.Serial;
import java.io.Serializable;

public class AuthObject implements Serializable {

    String user;
    String password;

    @Serial
    private static final long serialVersionUID = 1L;

    public AuthObject(String user, String password){
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
